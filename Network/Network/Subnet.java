package Network.Network;

import Network.DataUnit.DataLinkLayer.EthernetFrame;
import Network.DataUnit.DataUnit;
import Network.Node.Node;
import Network.Util.IPUtil;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

public class Subnet {
    private int subnetAddress;
    private int subnetMask;
    private boolean isPrivate;
    Set<Node> nodes = new HashSet<Node>();

    private Subnet(int subnetAddress, int subnetMask, boolean isPrivate) {
        this.subnetAddress = subnetAddress;
        this.subnetMask = subnetMask;
        this.isPrivate = isPrivate;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int subnetAddress;
        private int subnetMask;
        private boolean isPrivate = false;

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

        public Subnet build() {
            return new Subnet(subnetAddress, subnetMask, isPrivate);
        }
    }
}
