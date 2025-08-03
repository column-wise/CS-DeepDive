package Network.Node.Core;

import Network.DataUnit.DataLinkLayer.EthernetFrame;
import Network.DataUnit.DataUnit;
import Network.DataUnit.NetworkLayer.IPPacket;
import Network.Network.Subnet;
import Network.Node.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class Router extends Node {
	private String externalIP = null;
	List<Interface> interfaces;
	NATTable natTable;
	private final AtomicInteger portAllocator = new AtomicInteger(0);

	public void addInterface(Interface iface) {
		interfaces.add(iface);
		if(iface.type == Interface.Type.PUBLIC && externalIP == null) externalIP = iface.ipAddress;
		if(iface.type == Interface.Type.PRIVATE) ipAddress = iface.ipAddress;
	}

	public Router() {
		interfaces = new ArrayList<Interface>();
		natTable = new NATTable(() -> portAllocator.getAndIncrement());
	}

	public Router(String MACAddress) {
		this();
		this.MACAddress = MACAddress;
	}

	@Override
	protected void handleTCP(DataUnit data) {

	}

	@Override
	protected void handleUDP(DataUnit data) {

	}

	public void forward(DataUnit data) {
		EthernetFrame frame = (EthernetFrame) data;
		IPPacket packet = frame.getIPPacket();
		String destIP = packet.getDestinationIP();
		
		System.out.println("[Router] Forwarding packet to destination: " + destIP);

		// TODO: Implement proper routing table with longest prefix matching
		// For now, simple forwarding logic:
		// 1. Check if destination is in any connected subnet
		// 2. If not found, forward to PUBLIC interface (default route)
		
		for(Interface iface : interfaces) {
			// Check if destination is in this interface's subnet
			if(isDestinationInSubnet(destIP, iface)) {
				System.out.println("[Router] Found destination in connected subnet via " + iface.type + " interface (" + iface.ipAddress + ")");
				iface.connectedSubnet.send(data);
				return;
			}
		}
		
		// Not found in any connected subnet, use default route (PUBLIC interface)
		System.out.println("[Router] Destination not in connected subnets, using default route");
		for(Interface iface : interfaces) {
			if(iface.type == Interface.Type.PUBLIC) {
				System.out.println("[Router] Forwarding via PUBLIC interface (" + iface.ipAddress + ") to external network");
				iface.connectedSubnet.send(data);
				return;
			}
		}
		
		System.out.println("[Router] ERROR: No PUBLIC interface available for default route");
	}
	
	private boolean isDestinationInSubnet(String destIP, Interface iface) {
		// TODO: Implement proper subnet matching with CIDR
		// For now, simplified check
		return false; // Placeholder
	}

	public static class Interface {
		public enum Type {
			PUBLIC,
			PRIVATE
		}
		String MACAddress;
		String ipAddress;
		Subnet connectedSubnet;
		Type type;

		private Interface(String MACAddress, String ipAddress, Subnet subnet, Type type) {
			this.MACAddress = MACAddress;
			this.ipAddress = ipAddress;
			connectedSubnet = subnet;
			this.type = type;
		}

		public static Builder builder() {
			return new Builder();
		}

		public static class Builder {
			String MACAddress;
			String ipAddress;
			Subnet connectedSubnet;
			Type type = Type.PRIVATE;

			public Builder mac(String MACAddress) {
				this.MACAddress = MACAddress;
				return this;
			}

			public Builder ip(String ipAddress) {
				this.ipAddress = ipAddress;
				return this;
			}

			public Builder connectedSubnet(Subnet subnet) {
				this.connectedSubnet = subnet;
				return this;
			}

			public Builder type(Type type) {
				this.type = type;
				return this;
			}

			public Interface build() {
				return new Interface(MACAddress, ipAddress, connectedSubnet, type);
			}
		}
	}

	private class NATTable {
		private Map<ConnectionKey, NATEntry> internalToExternal = new HashMap<>();
		private Map<Integer, ConnectionKey> externalToInternal = new HashMap<>();
		private final Supplier<Integer> portAllocator;

		NATTable(Supplier<Integer> portAllocator) {
			this.portAllocator = portAllocator;
		}

		private ConnectionKey translateInbound(int externalPort) {
			return externalToInternal.get(externalPort);
		}

		private NATEntry translateOutbound(String internalIP, int internalPort) {
			ConnectionKey key = new ConnectionKey(internalIP, internalPort);
			if(!internalToExternal.containsKey(key)) {
				int externalPort = portAllocator.get();
				NATEntry entry = new NATEntry(internalIP, internalPort, externalIP, externalPort);
				internalToExternal.put(key, entry);
				externalToInternal.put(externalPort, key);
			}
			return internalToExternal.get(key);
		}

		private class ConnectionKey {
			String ip;
			int port;

			ConnectionKey(String ip, int port) {
				this.ip = ip;
				this.port = port;
			}

			@Override
			public boolean equals(Object o) {
				if (this == o) return true;
				if (o == null || getClass() != o.getClass()) return false;
				ConnectionKey that = (ConnectionKey) o;
				return port == that.port && Objects.equals(ip, that.ip);
			}

			@Override
			public int hashCode() {
				return Objects.hash(ip, port);
			}
		}

		private class NATEntry {
			String internalIP;
			int internalPort;
			String externalIP;
			int externalPort;

			NATEntry(String internalIP, int internalPort, String externalIP, int externalPort) {
				this.internalIP = internalIP;
				this.internalPort = internalPort;
				this.externalIP = externalIP;
				this.externalPort = externalPort;
			}
		}
	}
}
