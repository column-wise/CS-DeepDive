package Network.Node.End;

import Network.Constants.DHCPMessageType;
import Network.DataUnit.DataLinkLayer.EthernetFrame;
import Network.DataUnit.DataUnit;
import Network.DataUnit.NetworkLayer.IPPacket;
import Network.Network.Subnet;
import Network.Node.Node;
import Network.DataUnit.TransportLayer.UDPDatagram;
import Network.Util.IPUtil;
import Network.Util.PayloadParser;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class DHCPServer extends Node {

    private final int intIpAddress;
    private final int startIP;
    private final int endIP;
    private final long LEASE_TIME = 60000;   // 60초 후 만료
    private final Map<Integer, Long> allocatedIP;

    private DHCPServer(String MACAddress, String ipAddress, Subnet subnet) throws UnknownHostException {
        this.MACAddress = MACAddress;
        this.ipAddress = ipAddress;
        this.subnet = subnet;
        intIpAddress = IPUtil.ipToInt(ipAddress);
        startIP = subnet.getSubnetAddress() + 2;   // + 1은 router 용도로
        endIP = (subnet.getSubnetAddress() | (~subnet.getSubnetMask())) - 1;
        allocatedIP = new HashMap<>();
    }

    // client 가 discover를 보낸 후 DHCP request를 하지 않을 수도 있지만 바로 request를 한다고 가정.
    private int findAllocatableIP() {
        cleanExpiredIPs();
        return allocateIP();
    }

    private int allocateIP() {
        for(int ip = startIP; ip <= endIP; ip++) {
            if(ip == intIpAddress) continue;
            if(!allocatedIP.containsKey(ip)) {
                allocatedIP.put(ip, System.currentTimeMillis() + LEASE_TIME);
                return ip;
            }
        }
        return -1;
    }

    // TODO. 회수한 ip 관리 로직 추가
    private void cleanExpiredIPs() {
        for(Map.Entry<Integer, Long> entry : allocatedIP.entrySet()) {
            if(entry.getValue() < System.currentTimeMillis()) {
                allocatedIP.remove(entry.getKey());
            }
        }
    }

    @Override
    public void receive(DataUnit data) {
        super.receive(data);
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

        if ("FF:FF:FF:FF:FF:FF".equals(destinationMAC)
                && "255.255.255.255".equals(destinationIP)
                && "0.0.0.0".equals(sourceIP)) {

            DHCPMessageType.from(dhcpMessageTypeStr).ifPresent(type -> {
                switch (type) {
                    case DHCPDISCOVER -> offerDHCP(payload);
                    case DHCPREQUEST  -> ackDHCP(payload);
                }
            });
        }
    }

    // todo. DNS 구현 시 DNS 주소 추가
    private void offerDHCP(Map<String, String> receivedPayload) {
        String clientMAC = receivedPayload.get("Client MAC");
        String payload = "Client MAC=" + clientMAC +
                ",Your IP=" + IPUtil.intToIp(findAllocatableIP()) +
                ",Subnet Mask=" + IPUtil.intToIp(subnet.getSubnetMask()) +
                ",Router=" + IPUtil.intToIp(subnet.getSubnetAddress()+1) +
                ",DNS=" + "DNS 서버 IP 주소" +
                ",IP Lease Time=" + LEASE_TIME +
                ",DHCP Message Type=" + DHCPMessageType.DHCPOFFER +
                ",DHCP Server Identifier=" + ipAddress;

        // DHCP의 UDP 포트: 서버(67), 클라이언트(68)
        int sourcePort = 67;
        int destinationPort = 68;
        int length = payload.length() + 8;  // UDP 헤더 기본 길이 8바이트를 포함
        int checksum = 0; // 시뮬레이션에서는 체크섬 계산 생략

        UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(sourcePort, destinationPort, length, checksum);
        UDPDatagram datagram = new UDPDatagram(header, payload);
        IPPacket ipPacket = new IPPacket(ipAddress, "255.255.255.255", 17, datagram);
        EthernetFrame frame = new EthernetFrame(clientMAC, MACAddress, 0x0800, ipPacket);
        subnet.broadcast(frame, this);
    }

    private void ackDHCP(Map<String, String> receivedPayload) {
        String clientMAC = receivedPayload.get("Client MAC");
        String flags = receivedPayload.get("Flags");
        String yourIP = receivedPayload.get("Requested IP Address");
        String payload = "Client MAC=" + clientMAC +
                ",Your IP=" + yourIP +
                ",Subnet Mask=" + IPUtil.intToIp(subnet.getSubnetMask()) +
                ",Router=" + IPUtil.intToIp(subnet.getSubnetAddress()+1) +
                ",DNS=" + "DNS 서버 IP 주소" +
                ",IP Lease Time=" + LEASE_TIME +
                ",DHCP Message Type=" + DHCPMessageType.DHCPACK +
                ",DHCP Server Identifier=" + ipAddress;

        int sourcePort = 67;
        int destinationPort = 68;
        int length = payload.length() + 8;
        int checksum = 0;

        UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(sourcePort, destinationPort, length, checksum);
        UDPDatagram datagram = new UDPDatagram(header, payload);
        IPPacket ipPacket = new IPPacket(ipAddress, "255.255.255.255", 17, datagram);
        EthernetFrame frame = new EthernetFrame(clientMAC, MACAddress, 0x0800, ipPacket);

        if(flags.equals("1")) {
            subnet.broadcast(frame, this);
        } else if(flags.equals("0")) {
            // unicast
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String MACAddress;
        private String ipAddress;
        private Subnet subnet;

        public Builder ip(String ip) {
            this.ipAddress = ip;
            return this;
        }

        public Builder mac(String mac) {
            this.MACAddress = mac;
            return this;
        }

        public Builder network(Subnet subnet) {
            this.subnet = subnet;
            return this;
        }

        public DHCPServer build() {
            try {
                return new DHCPServer(MACAddress, ipAddress, subnet);
            } catch (UnknownHostException e) {
                throw new RuntimeException("Invalid IP Address: " + ipAddress, e);
            }
        }
    }
}
