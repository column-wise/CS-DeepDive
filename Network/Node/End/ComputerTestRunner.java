package Network.Node.End;

/**
 * Test runner for Computer class - compatible with existing test structure
 * This provides a main method similar to existing DHCPTest and NATTest files
 * while the comprehensive unit tests are in src/test/java/Network/Node/End/ComputerTest.java
 */
public class ComputerTestRunner {
    public static void main(String[] args) {
        System.out.println("=== Computer Class Test Runner ===");
        System.out.println("This is a basic compatibility test runner.");
        System.out.println("For comprehensive unit tests, run: mvn test");
        System.out.println("Or execute tests in src/test/java/Network/Node/End/ComputerTest.java");
        
        try {
            // Basic instantiation test
            Network.Network.Subnet testSubnet = Network.Network.Subnet.builder()
                    .subnetAddress("192.168.1.0")
                    .subnetMask("255.255.255.0")
                    .build();
            
            Computer computer = Computer.builder()
                    .MACAddress("AA:BB:CC:DD:EE:FF")
                    .ipAddress("192.168.1.100")
                    .subnet(testSubnet)
                    .build();
            
            System.out.println("✓ Computer creation test passed");
            
            // Basic functionality test
            computer.setIpAddress("192.168.1.200");
            System.out.println("✓ IP address setting test passed");
            
            computer.discoverDHCPServer();
            System.out.println("✓ DHCP discovery test passed");
            
            computer.sendHttpRequest(Network.Constants.HTTPMethodType.GET, "http://example.com");
            System.out.println("✓ HTTP request test passed");
            
            System.out.println("\n=== All basic tests passed! ===");
            System.out.println("Run comprehensive unit tests with: mvn test");
            
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}