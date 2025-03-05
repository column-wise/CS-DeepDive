package NetworkSimulation.Node.End;

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
    public void receive(UDPDatagram packet) {

    }

    public void discoverDHCPServer() {

        // 일반적으로 DHCP 클라이언트는 포트 68, 서버는 포트 67을 사용
        int sourcePort = 68;              // 클라이언트 포트
        int destinationPort = 67;         // DHCP 서버 포트
        int length = 8 + MACAddress.length();
        // 간단한 시뮬레이션에서는 checksum 계산을 생략하고 0으로 설정
        int checksum = 0;

        UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(sourcePort, destinationPort, length, checksum);
        UDPDatagram packet = new UDPDatagram(header, MACAddress);
        network.broadcast(packet);
    }
}
