package Network.Node;

import Network.DataUnit.DataLinkLayer.EthernetFrame;
import Network.DataUnit.DataUnit;
import Network.DataUnit.NetworkLayer.IPPacket;
import Network.DataUnit.TransportLayer.TransportDataUnit;
import Network.Network.Network;
import Network.Util.IPUtil;

public abstract class Node {
    protected String ipAddress;
    protected String MACAddress;
    protected Network network;

    public void receive(DataUnit data) {
        System.out.println(getClass().getName() + " " + ipAddress + " received\n" + data + "\n");

        IPPacket ipPacket = null;

        // L2 계층: EthernetFrame 처리
        if (data instanceof EthernetFrame frame) {
            if (!isDestinedToMeMAC(frame.getDestinationMAC())) return;
            ipPacket = frame.getIPPacket();
        } else if (data instanceof IPPacket packet) {
            ipPacket = packet;
        } else {
            return;
        }

        // L3 계층: IP 확인
        if (!isDestinedToMeIP(ipPacket.getDestinationIP())) return;

        // L4 계층: 프로토콜 분기
        switch (ipPacket.getProtocol()) {
            case 6 -> handleTCP(data);
            case 17 -> handleUDP(data);
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

    private boolean isSubnetBroadcast(String destIP) {
        try {
            int dest = IPUtil.ipToInt(destIP);
            int subnetAddr = network.getSubnetAddress();
            int subnetMask = network.getSubnetMask();
            int broadcast = subnetAddr | (~subnetMask);
            return dest == broadcast;
        } catch (Exception e) {
            return false;
        }
    }

    protected void handleUDP(DataUnit dataUnit) {

    }

    protected void handleTCP(DataUnit dataUnit) {

    }

    @Override
    public String toString() {
        return getClass().getName() + " MAC: " + MACAddress + " ,IP Address: " + ipAddress;
    }
}
