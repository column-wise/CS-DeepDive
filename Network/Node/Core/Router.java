package Network.Node.Core;

import Network.Network.Subnet;
import Network.Node.Node;

import java.util.ArrayList;
import java.util.List;

public class Router extends Node {
	List<Interface> interfaces;
	NATTable natTable;

	public void addInterface(Interface iface) {
		interfaces.add(iface);
	}

	public Router() {
		interfaces = new ArrayList<Interface>();
		natTable = new NATTable();
	}

	public Router(String MACAddress) {
		this();
		this.MACAddress = MACAddress;
	}

	public static class Interface {
		String MACAddress;
		String ipAddress;
		Subnet connectedSubnet;

		private Interface(String MACAddress, String ipAddress, Subnet subnet) {
			this.MACAddress = MACAddress;
			this.ipAddress = ipAddress;
			connectedSubnet = subnet;
		}

		public static Builder builder() {
			return new Builder();
		}

		public static class Builder {
			String MACAddress;
			String ipAddress;
			Subnet connectedSubnet;

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

			public Interface build() {
				return new Interface(MACAddress, ipAddress, connectedSubnet);
			}
		}
	}

	private class NATTable {

	}
}
