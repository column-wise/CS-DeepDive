package Network.Simulation.DHCP;

import Network.Network.Subnet;
import Network.Node.End.Computer;
import Network.Node.End.DHCPServer;

public class DHCPTest {
    public static void main(String[] args) throws Exception {
        Subnet subnet = new Subnet("192.168.1.0", "255.255.255.0");
        DHCPServer dhcpServer = DHCPServer.builder()
                .ip("192.168.1.10")
                .mac("00-1A-2B-3C-4D-5E")
                .network(subnet)
                .build();
        subnet.addNode(dhcpServer);

        Computer client = Computer.builder()
                .mac("01-2A-3B-4C-5D-6E")
                .network(subnet)
                .build();
        subnet.addNode(client);

        client.discoverDHCPServer();
        Thread.sleep(100);
    }
}
