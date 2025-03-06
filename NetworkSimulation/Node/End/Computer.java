package NetworkSimulation.Node.End;

import NetworkSimulation.DataUnit.DataLinkLayer.EthernetFrame;
import NetworkSimulation.DataUnit.DataUnit;
import NetworkSimulation.DataUnit.NetworkLayer.IPPacket;
import NetworkSimulation.Network.Network;
import NetworkSimulation.Node.Node;
import NetworkSimulation.DataUnit.TransportLayer.UDPDatagram;

public class Computer extends Node {

    public Computer(String MACAddress, Network network) {
        super(MACAddress, network);
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public void receive(DataUnit data) {
        System.out.println(data.toString());
    }

    public void discoverDHCPServer() {

        // 일반적으로 DHCP 클라이언트는 포트 68, 서버는 포트 67을 사용
        int sourcePort = 68;              // 클라이언트 포트
        int destinationPort = 67;         // DHCP 서버 포트
        // 간단한 시뮬레이션에서는 checksum 계산을 생략하고 0으로 설정
        int checksum = 0;

        String payload = "DHCP Message Type=DHCPDISCOVER" +
                ",Client MAC=" + MACAddress;

        int length = 8 + payload.length();

        UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(sourcePort, destinationPort, length, checksum);
        UDPDatagram udpDatagram = new UDPDatagram(header, payload);

        IPPacket ipPacket = new IPPacket("0.0.0.0", "255.255.255.255", 17, udpDatagram);
        EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", MACAddress, 0x0800, ipPacket);

        network.broadcast(frame);
    }
}
