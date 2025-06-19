package Network.Node.End;

import Network.DataUnit.DataLinkLayer.EthernetFrame;
import Network.DataUnit.DataUnit;
import Network.DataUnit.NetworkLayer.IPPacket;
import Network.Network.Network;
import Network.Node.Node;
import Network.DataUnit.TransportLayer.UDPDatagram;

public class Computer extends Node {

    private Computer(String MACAddress, String ipAddress, Network network) {
        this.MACAddress = MACAddress;
        this.ipAddress = ipAddress;
        this.network = network;
    }

    @Override
    public void receive(DataUnit data) {
        if (!(data instanceof EthernetFrame)) return;

        EthernetFrame frame = (EthernetFrame) data;
        String destinationMAC = frame.getDestinationMAC();

        if (!destinationMAC.equals(this.MACAddress)) {
            // 내 MAC 주소가 아니면 무시
            return;
        }
        super.receive(data);
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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

        // protocol 17: UDP, 6: TCP
        IPPacket ipPacket = new IPPacket("0.0.0.0", "255.255.255.255", 17, udpDatagram);
        EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", MACAddress, 0x0800, ipPacket);

        network.broadcast(frame, this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String MACAddress;
        private String ipAddress = null;
        private Network network;

        public Builder mac(String mac) {
            MACAddress = mac;
            return this;
        }

        public Builder ip(String ip) {
            ipAddress = ip;
            return this;
        }

        public Builder network(Network network) {
            this.network = network;
            return this;
        }

        public Computer build() {
            return new Computer(MACAddress, ipAddress, network);
        }
    }
}
