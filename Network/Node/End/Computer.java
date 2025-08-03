package Network.Node.End;

import Network.Constants.DHCPMessageType;
import Network.Constants.HTTPMethodType;
import Network.DataUnit.DataLinkLayer.EthernetFrame;
import Network.DataUnit.DataUnit;
import Network.DataUnit.NetworkLayer.IPPacket;
import Network.DataUnit.TransportLayer.TCPSegment;
import Network.Network.Subnet;
import Network.Node.IPManager;
import Network.Node.NetworkInterface;
import Network.Node.Node;
import Network.DataUnit.TransportLayer.UDPDatagram;
import Network.Node.TCPManager;
import Network.Node.UDPManager;
import Network.Util.PayloadParser;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Computer extends Node {
	private final IPManager ipManager = new IPManager(this::sendEthernetFrame);

	// Ephemeral port (49152~65535)
	private final AtomicInteger portAllocator = new AtomicInteger(49152);
	private final TCPManager tcpManager = new TCPManager(
			() -> portAllocator.getAndUpdate(cur -> cur == 65535 ? 49152 : cur + 1),
			(destinationIP, segment) -> ipManager.sendIPPacket(destinationIP, segment));
	private final UDPManager udpManager = new UDPManager(
			() -> portAllocator.getAndUpdate(cur -> cur == 65535 ? 49152 : cur + 1),
			(destinationIP, datagram) -> ipManager.sendIPPacket(destinationIP, datagram));

	protected Computer(String MACAddress, String ipAddress, Subnet subnet) {
		this.MACAddress = MACAddress;
		this.ipAddress = ipAddress;
		networkInterface = new NetworkInterface(subnet);
	}

	protected void sendEthernetFrame(IPPacket packet) {
		String dstMAC = getDestinationMAC(packet.getDestinationIP());
		EthernetFrame frame = new EthernetFrame(
				dstMAC,
				this.MACAddress,
				0x0800,     // EtherType: IPv4
				packet
		);
		super.send(frame);
	}

	// todo. ARP로 목적지 MAC 주소 얻어오는 로직 추가
	private String getDestinationMAC(String destinationIP) {
		return "";
	}

	@Override
	protected void handleTCP(DataUnit data) {
		EthernetFrame ethernetFrame = (EthernetFrame) data;
		IPPacket packet = ethernetFrame.getIPPacket();
		String remoteIP = packet.getSourceIP();
		tcpManager.onSegmentReceived(ipAddress, remoteIP, (TCPSegment) packet.getTransportDataUnit());
	}

	@Override
	public void handleUDP(DataUnit dataUnit) {
		EthernetFrame frame = (EthernetFrame) dataUnit;
		String destinationMAC = frame.getDestinationMAC();
		String sourceMAC = frame.getSourceMAC();

		IPPacket ipPacket = frame.getIPPacket();
		String destinationIP = ipPacket.getDestinationIP();
		String sourceIP = ipPacket.getSourceIP();

		UDPDatagram udpDatagram = (UDPDatagram) ipPacket.getTransportDataUnit();
		Map<String, String> payload = PayloadParser.parsePayload(udpDatagram.getPayload());
		String dhcpMessageTypeStr = payload.get("DHCP Message Type");
		String dhcpServerIdentifier = payload.get("DHCP Server Identifier");
		String myIP = payload.get("Your IP");

		if ("255.255.255.255".equals(destinationIP)
				&& dhcpServerIdentifier != null
				&& myIP != null) {
			DHCPMessageType.from(dhcpMessageTypeStr).ifPresent(type -> {
				switch (type) {
					case DHCPOFFER -> requestDHCP(myIP);
					case DHCPACK -> setIpAddress(myIP);
					case DHCPNACK -> discoverDHCPServer();
				}
			});
		}
	}

	public void setIpAddress(String ipAddress) {
		System.out.println(this);
		System.out.println("IP Setting...");
		this.ipAddress = ipAddress;
		System.out.println(this);
	}

	public void discoverDHCPServer() {

		// 일반적으로 DHCP 클라이언트는 포트 68, 서버는 포트 67을 사용
		int sourcePort = 68;              // 클라이언트 포트
		int destinationPort = 67;         // DHCP 서버 포트
		// 간단한 시뮬레이션에서는 checksum 계산을 생략하고 0으로 설정
		int checksum = 0;

		String payload = "DHCP Message Type=" + DHCPMessageType.DHCPDISCOVER +
				",Client MAC=" + MACAddress;

		int length = 8 + payload.length();

		UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(sourcePort, destinationPort, length, checksum);
		UDPDatagram udpDatagram = new UDPDatagram(header, payload);

		// protocol 17: UDP, 6: TCP
		IPPacket ipPacket = new IPPacket("0.0.0.0", "255.255.255.255", 17, udpDatagram);
		EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", MACAddress, 0x0800, ipPacket);

		networkInterface.getSubnet().broadcast(frame, this);
	}

	private void requestDHCP(String myIP) {
		int sourcePort = 68;
		int destinationPort = 67;
		int checksum = 0;

		// DHCP 서버로부터 offer 받은 IP가 유효한지 확인하기 위해 ARP Probe 과정을 거치기도 함
		// 해당 IP로 ARP 요청을 보내보고, 응답이 없으면 사용 가능하다고 판단

		String payload = "DHCP Message Type=DHCPREQUEST" +
				",Client MAC=" + MACAddress +
				",Requested IP Address=" + myIP +
				",Flags=1";
		// Flags=0 -> DHCP ACK 응답을 유니캐스트로 보내주세요
		// Flags=1 -> 브로드캐스트로 보내주세요

		int length = 8 + payload.length();

		UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(sourcePort, destinationPort, length, checksum);
		UDPDatagram udpDatagram = new UDPDatagram(header, payload);

		IPPacket ipPacket = new IPPacket("0.0.0.0", "255.255.255.255", 17, udpDatagram);
		EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", MACAddress, 0x0800, ipPacket);

		networkInterface.getSubnet().broadcast(frame, this);
	}

	public void sendHttpRequest(HTTPMethodType method, String url) {
		// DNS 구현시 url -> ip 매핑 로직 추가되어야 함
		String destinationIP = "128.119.40.186";
		int destinationPort = 80;
		System.out.println(this.toString());
		System.out.println("sending HTTP Request...");
		tcpManager.sendSegment(ipAddress, destinationIP, destinationPort, url + method.toString());
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends Node.Builder<Builder> {

		@Override
		protected Builder self() {
			return this;
		}

		@Override
		public Computer build() {
			Computer computer = new Computer(MACAddress, ipAddress, subnet);
			System.out.println(computer);
			System.out.println("Added to Network\n");
			return computer;
		}
	}
}
