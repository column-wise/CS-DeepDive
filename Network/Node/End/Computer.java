package Network.Node.End;

import Network.Constants.DHCPMessageType;
import Network.Constants.HTTPMethodType;
import Network.DataUnit.DataLinkLayer.EthernetFrame;
import Network.DataUnit.DataUnit;
import Network.DataUnit.NetworkLayer.IPPacket;
import Network.Network.Subnet;
import Network.Node.Node;
import Network.DataUnit.TransportLayer.UDPDatagram;
import Network.Util.PayloadParser;

import java.util.Map;

public class Computer extends Node {

    protected Computer(String MACAddress, String ipAddress, Subnet subnet) {
        this.MACAddress = MACAddress;
        this.ipAddress = ipAddress;
        this.subnet = subnet;
    }

    @Override
    protected void handleUDP(DataUnit dataUnit) {
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

        if("255.255.255.255".equals(destinationIP)
                && dhcpServerIdentifier != null
                && myIP != null) {
            DHCPMessageType.from(dhcpMessageTypeStr).ifPresent(type -> {
                switch (type) {
                    case DHCPOFFER -> requestDHCP(myIP);
                    case DHCPACK  -> setIpAddress(myIP);
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

        subnet.broadcast(frame, this);
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

        subnet.broadcast(frame, this);
    }

    @Override
    protected void sendTCP(String destIP, int destPort, DataUnit dataUnit) {

    }

    @Override
    protected void establishTCP(String destIP, int destPort) {

    }

    @Override
    protected void sendUDP(String destIP, int destPort, DataUnit dataUnit) {

    }

    public void sendHttpRequest(HTTPMethodType method, String url) {

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends Node.Builder<Builder>{
        private String MACAddress;
        private String ipAddress = null;
        private Subnet subnet;

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
