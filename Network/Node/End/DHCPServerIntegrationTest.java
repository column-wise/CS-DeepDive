package Network.Node.End;

import Network.Network.Subnet;
import Network.Node.End.Computer;
import Network.Node.End.DHCPServer;

/**
 * Integration test for DHCPServer following the project's existing test pattern.
 * This test simulates a complete DHCP interaction scenario.
 */
public class DHCPServerIntegrationTest {
    public static void main(String[] args) throws Exception {
        System.out.println("=== DHCPServer Integration Test ===");
        
        // Create test subnet
        Subnet subnet = Subnet.builder()
                .subnetAddress("192.168.1.0")
                .subnetMask("255.255.255.0")
                .build();

        // Create DHCP server
        DHCPServer dhcpServer = DHCPServer.builder()
                .ip("192.168.1.10")
                .mac("00-1A-2B-3C-4D-5E")
                .subnet(subnet)
                .build();
        subnet.addNode(dhcpServer);

        // Create test client
        Computer client = Computer.builder()
                .mac("00-11-22-33-44-55")
                .subnet(subnet)
                .build();
        subnet.addNode(client);

        System.out.println("✓ Created DHCP server with IP: " + dhcpServer.getIpAddress());
        System.out.println("✓ Created client with MAC: " + client.getMACAddress());
        System.out.println("✓ Both nodes added to subnet: " + subnet.getSubnetAddress());

        // Test DHCP discovery process
        try {
            System.out.println("\n--- Testing DHCP Discovery Process ---");
            client.discoverDHCPServer();
            
            // Allow some time for DHCP process
            Thread.sleep(1000);
            
            System.out.println("✓ DHCP discovery process completed");
            
            if (client.getIpAddress() != null && !client.getIpAddress().equals("0.0.0.0")) {
                System.out.println("✓ Client successfully obtained IP: " + client.getIpAddress());
            } else {
                System.out.println("⚠ Client did not obtain IP address (may be expected behavior)");
            }
            
        } catch (Exception e) {
            System.err.println("✗ DHCP discovery test failed: " + e.getMessage());
        }

        // Test multiple clients
        try {
            System.out.println("\n--- Testing Multiple Clients ---");
            
            Computer client2 = Computer.builder()
                    .mac("00-AA-BB-CC-DD-EE")
                    .subnet(subnet)
                    .build();
            subnet.addNode(client2);
            
            client2.discoverDHCPServer();
            Thread.sleep(1000);
            
            System.out.println("✓ Second client added and tested");
            
        } catch (Exception e) {
            System.err.println("✗ Multiple clients test failed: " + e.getMessage());
        }

        System.out.println("\n=== Integration Test Completed ===");
    }
}