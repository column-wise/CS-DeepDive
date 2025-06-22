package Network.Node;

import Network.DataUnit.DataLinkLayer.EthernetFrame;
import Network.DataUnit.DataUnit;
import Network.DataUnit.NetworkLayer.IPPacket;
import Network.Network.Subnet;
import Network.Util.IPUtil;

public abstract class Node {
    protected String ipAddress;
    protected String MACAddress;
    protected Subnet subnet;

    public void receive(DataUnit data) {
        // L2 계층: EthernetFrame 여부 파악 및 MAC 체크
        IPPacket ipPacket = null;
        boolean destinedToMe = false;

        if (data instanceof EthernetFrame frame) {
            destinedToMe = isDestinedToMeMAC(frame.getDestinationMAC());
            if (destinedToMe) {
                ipPacket = frame.getIPPacket();
                // L3 IP 체크
                destinedToMe = isDestinedToMeIP(ipPacket.getDestinationIP());
            }
        } else if (data instanceof IPPacket packet) {
            destinedToMe = isDestinedToMeIP(packet.getDestinationIP());
            if (destinedToMe) {
                ipPacket = packet;
            }
        }

        // 간단 로그: 일단 받은 사실만 찍고, 대상 아니면 리턴
        System.out.println(getClass().getName() + " " + ipAddress + " received a packet"
                + (destinedToMe ? "" : " (not for me)") + ".\n");
        if (!destinedToMe) {
            return;
        }

        // 상세 로그: 실제 나에게 온 패킷만 data 내용까지 출력
        System.out.println(data + "\n");

        // L4 계층: 프로토콜 분기
        switch (ipPacket.getProtocol()) {
            case 6 -> {
                handleTCP(data);
            }
            case 17 -> {
                handleUDP(data);
            }
            default -> System.out.println("Unknown protocol: " + ipPacket.getProtocol());
        }
    }

    protected boolean isDestinedToMeMAC(String destMAC) {
        return destMAC.equals(this.MACAddress) || destMAC.equals("FF:FF:FF:FF:FF:FF");
    }

    protected boolean isDestinedToMeIP(String destIP) {
        return destIP.equals(this.ipAddress)
                || destIP.equals("255.255.255.255")
                || isSubnetBroadcast(destIP);
    }

    protected abstract void handleTCP(DataUnit data);

    protected abstract void handleUDP(DataUnit data);

    private boolean isSubnetBroadcast(String destIP) {
        try {
            int dest = IPUtil.ipToInt(destIP);
            int subnetAddr = subnet.getSubnetAddress();
            int subnetMask = subnet.getSubnetMask();
            int broadcast = subnetAddr | (~subnetMask);
            return dest == broadcast;
        } catch (Exception e) {
            return false;
        }
    }

    protected void send(DataUnit data) {
        subnet.send(data);
    }

    @Override
    public String toString() {
        return getClass().getName() + " MAC: " + MACAddress + " ,IP Address: " + ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getMACAddress() {
        return MACAddress;
    }

    public Subnet getSubnet() {
        return subnet;
    }

    public abstract static class Builder<T extends Builder<T>> {
        protected String ipAddress;
        protected String MACAddress;
        protected Subnet subnet;

        public T ip(String ip) {
            this.ipAddress = ip;
            return self();
        }

        public T mac(String mac) {
            this.MACAddress = mac;
            return self();
        }

        public T subnet(Subnet subnet) {
            this.subnet = subnet;
            return self();
        }

        protected abstract T self();

        public abstract Node build();
    }
}
