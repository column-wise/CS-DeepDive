package NetworkSimulation.Simulation;

import NetworkSimulation.Network.Network;
import NetworkSimulation.Node.End.Computer;
import NetworkSimulation.Node.End.DHCPServer;
import NetworkSimulation.Util.IPUtil;

public class DHCPTest {
    public static void main(String[] args) throws Exception {
        Network network = new Network("192.168.1.0", "255.255.255.0");
        DHCPServer dhcpServer = new DHCPServer("00-1A-2B-3C-4D-5E", "192.168.1.10", network);
        network.addNode(dhcpServer);

        Computer client = new Computer("01-2A-3B-4C-5D-6E", network);
        network.addNode(client);
        client.discoverDHCPServer();
    }
}
