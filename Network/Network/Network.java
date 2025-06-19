package Network.Network;

import Network.DataUnit.DataLinkLayer.EthernetFrame;
import Network.DataUnit.DataUnit;
import Network.Node.Node;
import Network.Util.IPUtil;

import java.util.HashSet;
import java.util.Set;

public class Network {
    private int subnetAddress;
    private int subnetMask;
    Set<Node> nodes = new HashSet<Node>();

    public Network(String subnetAddress, String subnetMask) {
        try {
            this.subnetAddress = IPUtil.ipToInt(subnetAddress);
            this.subnetMask = IPUtil.ipToInt(subnetMask);
        } catch (Exception e) {
            this.subnetAddress = 0;
            this.subnetMask = 0;
        }
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
}
