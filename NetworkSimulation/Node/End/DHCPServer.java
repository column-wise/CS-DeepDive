package NetworkSimulation.Node.End;

import NetworkSimulation.Network.Network;
import NetworkSimulation.Node.Node;
import NetworkSimulation.Packet.UDPPacket;

import java.util.HashMap;
import java.util.Map;

public class DHCPServer extends Node {

    private final int startIP;
    private final int endIP;
    private final long LEASE_TIME = 60000;   // 60초 후 만료
    private final Map<Integer, Long> allocatedIP;

    public DHCPServer(String MACAddress, Network network) {
        super(MACAddress);
        startIP = network.getSubnetAddress() + 1;
        endIP = (network.getSubnetAddress() | (~network.getSubnetMask())) - 1;
        allocatedIP = new HashMap<>();
    }

    private int allocateIP() {
        for(int ip = startIP; ip <= endIP; ip++) {
            if(!allocatedIP.containsKey(ip)) {
                allocatedIP.put(ip, System.currentTimeMillis() + LEASE_TIME);
                return ip;
            }
        }
        return -1;
    }

    // TODO.
    private void cleanExpiredIPs() {

    }

    @Override
    public void receive(UDPPacket packet) {

    }
}
