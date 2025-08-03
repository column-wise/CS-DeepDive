package Network.Network;

import Network.DataUnit.DataLinkLayer.EthernetFrame;
import Network.DataUnit.DataUnit;
import Network.DataUnit.NetworkLayer.IPPacket;
import Network.Node.Core.Router;
import Network.Node.Node;
import Network.Util.IPUtil;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

public class Subnet {
    private int subnetAddress;
    private int subnetMask;
    private boolean isPrivate;
    private Internet internet;
    private Router gateway;
    Set<Node> nodes = new HashSet<Node>();

    private Subnet(int subnetAddress, int subnetMask, boolean isPrivate, Internet internet) {
        this.subnetAddress = subnetAddress;
        this.subnetMask = subnetMask;
        this.isPrivate = isPrivate;
        this.internet = internet;
    }

    public int getSubnetAddress() {
        return subnetAddress;
    }

    public int getSubnetMask() {
        return subnetMask;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void broadcast(DataUnit data, Node sender) {
        if(!(data instanceof EthernetFrame) || ((EthernetFrame) data).getIPPacket().getProtocol() != 17) {
            return;
        }

        for (Node node : nodes) {
            if(node.equals(sender)) continue;
            node.receive(data);
        }
    }

    public void send(DataUnit data) {
        if(!(data instanceof EthernetFrame)) return;
        EthernetFrame frame = (EthernetFrame) data;
        IPPacket packet = frame.getIPPacket();

        String destMAC = frame.getDestinationMAC();
        String destIP = packet.getDestinationIP();

        boolean delivered = false;

        System.out.println("[Subnet] Received packet for delivery");
        System.out.println("[Subnet] Destination: MAC=" + destMAC + " IP=" + destIP);

        for (Node node : nodes) {
            System.out.println("[Subnet] Checking node: " + node.toString());
            boolean match =
                    node.getIpAddress().equals(destIP) ||
                            node.getMACAddress().equals(destMAC) ||
                            "FF:FF:FF:FF:FF:FF".equals(destMAC) ||
                            "255.255.255.255".equals(destIP);

            if (match) {
                System.out.println("[Subnet] Match found! Delivering to node: " + node.toString());
                node.receive(data);
                delivered = true;
            }
        }

        if(!delivered && gateway != null) {
            System.out.println("[Subnet] Destination not found in local subnet, forwarding to gateway");
            System.out.println("[Subnet] Gateway: " + gateway.toString());
            gateway.forward(data);
        } else if(!delivered) {
            System.out.println("[Subnet] ERROR: Destination not found and no gateway configured");
        }
    }

    public void setGateway(Router gateway) {
        this.gateway = gateway;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int subnetAddress;
        private int subnetMask;
        private boolean isPrivate = false;
        private Internet internet = null;

        public Builder subnetAddress(String subnetAddress) {
            try {
                this.subnetAddress = IPUtil.ipToInt(subnetAddress);
            } catch (UnknownHostException e) {
                this.subnetAddress = 0;
            }

            return this;
        }

        public Builder subnetMask(String subnetMask) {
            try {
                this.subnetMask = IPUtil.ipToInt(subnetMask);
            } catch (UnknownHostException e) {
                this.subnetMask = 0;
            }
            return this;
        }

        public Builder isPrivate(boolean isPrivate) {
            this.isPrivate = isPrivate;
            return this;
        }

        public Builder internet(Internet internet) {
            this.internet = internet;
            return this;
        }

        public Subnet build() {
            return new Subnet(subnetAddress, subnetMask, isPrivate, internet);
        }
    }
}
