package NetworkSimulation.Node.End;

import NetworkSimulation.Network.Network;
import NetworkSimulation.Node.Node;
import NetworkSimulation.DataUnit.TransportLayer.UDPDatagram;

import java.util.HashMap;
import java.util.Map;

public class DHCPServer extends Node {

    private Network network;
    private final int startIP;
    private final int endIP;
    private final long LEASE_TIME = 60000;   // 60초 후 만료
    private final Map<Integer, Long> allocatedIP;

    public DHCPServer(String MACAddress, Network network) {
        super(MACAddress, network);
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
        for(Map.Entry<Integer, Long> entry : allocatedIP.entrySet()) {
            if(entry.getValue() < System.currentTimeMillis()) {
                allocatedIP.remove(entry.getKey());
            }
        }
    }

    @Override
    public void receive(UDPDatagram packet) {

    }
}
