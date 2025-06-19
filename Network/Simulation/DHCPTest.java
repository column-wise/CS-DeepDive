package Network.Simulation;

import Network.Network.Network;
import Network.Node.End.Computer;
import Network.Node.End.DHCPServer;

public class DHCPTest {
    public static void main(String[] args) throws Exception {
        Network network = new Network("192.168.1.0", "255.255.255.0");
        DHCPServer dhcpServer = DHCPServer.builder()
                .ip("192.168.1.10")
                .mac("00-1A-2B-3C-4D-5E")
                .network(network).build();
        network.addNode(dhcpServer);

        Computer client = Computer.builder()
                .mac("01-2A-3B-4C-5D-6E")
                .network(network).build();

        network.addNode(client);
        client.discoverDHCPServer();
    }
}
