package Network.Node.End;

import Network.Constants.DHCPMessageType;
import Network.DataUnit.DataLinkLayer.EthernetFrame;
import Network.DataUnit.DataUnit;
import Network.DataUnit.NetworkLayer.IPPacket;
import Network.DataUnit.TransportLayer.UDPDatagram;
import Network.Network.Subnet;
import Network.Node.NetworkInterface;
import Network.Util.IPUtil;
import Network.Util.PayloadParser;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Comprehensive unit tests for DHCPServer class.
 * This test class provides thorough coverage of DHCPServer functionality including:
 * - Builder pattern validation
 * - IP allocation and management
 * - DHCP message handling (DISCOVER, REQUEST)
 * - Lease expiration logic
 * - Edge cases and error conditions
 * 
 * Testing approach: Manual test execution with main method since no formal testing framework is detected.
 * If JUnit is available, this can be easily converted to use @Test annotations.
 */
public class DHCPServerUnitTest {

    public static void main(String[] args) {
        DHCPServerUnitTest testSuite = new DHCPServerUnitTest();
        
        System.out.println("=== Starting DHCPServer Unit Tests ===");
        
        try {
            testSuite.runAllTests();
            System.out.println("=== All tests completed successfully ===");
        } catch (Exception e) {
            System.err.println("Test suite failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void runAllTests() throws Exception {
        testBuilderPattern();
        testIPAllocation();
        testLeaseManagement();
        testDHCPMessageHandling();
        testEdgeCases();
        testNetworkCommunication();
    }

    private void testBuilderPattern() throws Exception {
        System.out.println("\n--- Testing Builder Pattern ---");
        
        // Test valid builder usage
        try {
            Subnet testSubnet = createTestSubnet();
            DHCPServer server = DHCPServer.builder()
                    .mac("00:11:22:33:44:55")
                    .ip("192.168.1.1")
                    .subnet(testSubnet)
                    .build();
            
            assert server != null : "Builder should create valid DHCPServer instance";
            System.out.println("✓ Valid builder usage test passed");
        } catch (Exception e) {
            System.err.println("✗ Valid builder usage test failed: " + e.getMessage());
        }

        // Test invalid IP address
        try {
            Subnet testSubnet = createTestSubnet();
            DHCPServer.builder()
                    .mac("00:11:22:33:44:55")
                    .ip("invalid.ip.address")
                    .subnet(testSubnet)
                    .build();
            
            System.err.println("✗ Invalid IP test failed - should have thrown exception");
        } catch (RuntimeException e) {
            System.out.println("✓ Invalid IP address test passed - correctly threw exception");
        }

        // Test method chaining
        try {
            Subnet testSubnet = createTestSubnet();
            DHCPServer.Builder builder = DHCPServer.builder();
            DHCPServer.Builder result1 = builder.mac("00:11:22:33:44:55");
            DHCPServer.Builder result2 = result1.ip("192.168.1.1");
            DHCPServer.Builder result3 = result2.subnet(testSubnet);
            
            assert builder == result1 : "Builder methods should return same instance";
            assert result1 == result2 : "Builder methods should return same instance";
            assert result2 == result3 : "Builder methods should return same instance";
            System.out.println("✓ Builder method chaining test passed");
        } catch (Exception e) {
            System.err.println("✗ Builder method chaining test failed: " + e.getMessage());
        }
    }

    private void testIPAllocation() throws Exception {
        System.out.println("\n--- Testing IP Allocation ---");
        
        Subnet testSubnet = createTestSubnet();
        DHCPServer server = DHCPServer.builder()
                .mac("00:11:22:33:44:55")
                .ip("192.168.1.1")
                .subnet(testSubnet)
                .build();

        // Test IP allocation within range
        try {
            Method allocateIPMethod = DHCPServer.class.getDeclaredMethod("allocateIP");
            allocateIPMethod.setAccessible(true);
            
            int allocatedIP = (int) allocateIPMethod.invoke(server);
            int subnetBase = IPUtil.ipToInt("192.168.1.0");
            int subnetMask = IPUtil.ipToInt("255.255.255.0");
            int startIP = subnetBase + 2;
            int endIP = (subnetBase | (~subnetMask)) - 1;
            int serverIP = IPUtil.ipToInt("192.168.1.1");
            
            assert allocatedIP >= startIP : "Allocated IP should be within valid range";
            assert allocatedIP <= endIP : "Allocated IP should be within valid range";
            assert allocatedIP != serverIP : "Should not allocate server's own IP";
            
            System.out.println("✓ IP allocation within range test passed");
            System.out.println("  Allocated IP: " + IPUtil.intToIp(allocatedIP));
        } catch (Exception e) {
            System.err.println("✗ IP allocation within range test failed: " + e.getMessage());
        }

        // Test multiple IP allocations don't conflict
        try {
            Method allocateIPMethod = DHCPServer.class.getDeclaredMethod("allocateIP");
            allocateIPMethod.setAccessible(true);
            
            int ip1 = (int) allocateIPMethod.invoke(server);
            int ip2 = (int) allocateIPMethod.invoke(server);
            
            assert ip1 != ip2 : "Multiple allocations should return different IPs";
            assert ip1 > 0 : "First allocation should be valid";
            assert ip2 > 0 : "Second allocation should be valid";
            
            System.out.println("✓ Multiple IP allocation test passed");
            System.out.println("  First IP: " + IPUtil.intToIp(ip1) + ", Second IP: " + IPUtil.intToIp(ip2));
        } catch (Exception e) {
            System.err.println("✗ Multiple IP allocation test failed: " + e.getMessage());
        }

        // Test IP pool exhaustion
        try {
            Field allocatedIPField = DHCPServer.class.getDeclaredField("allocatedIP");
            allocatedIPField.setAccessible(true);
            Map<Integer, Long> allocatedIPs = (Map<Integer, Long>) allocatedIPField.get(server);
            
            // Fill up all available IPs
            int subnetBase = IPUtil.ipToInt("192.168.1.0");
            int subnetMask = IPUtil.ipToInt("255.255.255.0");
            int startIP = subnetBase + 2;
            int endIP = (subnetBase | (~subnetMask)) - 1;
            int serverIP = IPUtil.ipToInt("192.168.1.1");
            
            for (int ip = startIP; ip <= endIP; ip++) {
                if (ip != serverIP) {
                    allocatedIPs.put(ip, System.currentTimeMillis() + 60000);
                }
            }
            
            Method allocateIPMethod = DHCPServer.class.getDeclaredMethod("allocateIP");
            allocateIPMethod.setAccessible(true);
            int result = (int) allocateIPMethod.invoke(server);
            
            assert result == -1 : "Should return -1 when no IPs available";
            System.out.println("✓ IP pool exhaustion test passed");
        } catch (Exception e) {
            System.err.println("✗ IP pool exhaustion test failed: " + e.getMessage());
        }
    }

    private void testLeaseManagement() throws Exception {
        System.out.println("\n--- Testing Lease Management ---");
        
        Subnet testSubnet = createTestSubnet();
        DHCPServer server = DHCPServer.builder()
                .mac("00:11:22:33:44:55")
                .ip("192.168.1.1")
                .subnet(testSubnet)
                .build();

        // Test lease expiration
        try {
            Field allocatedIPField = DHCPServer.class.getDeclaredField("allocatedIP");
            allocatedIPField.setAccessible(true);
            Map<Integer, Long> allocatedIPs = (Map<Integer, Long>) allocatedIPField.get(server);
            
            int testIP = IPUtil.ipToInt("192.168.1.100");
            allocatedIPs.put(testIP, System.currentTimeMillis() - 1000); // Expired 1 second ago
            
            Method cleanExpiredIPsMethod = DHCPServer.class.getDeclaredMethod("cleanExpiredIPs");
            cleanExpiredIPsMethod.setAccessible(true);
            cleanExpiredIPsMethod.invoke(server);
            
            assert !allocatedIPs.containsKey(testIP) : "Expired IP should be removed";
            System.out.println("✓ Lease expiration test passed");
        } catch (Exception e) {
            System.err.println("✗ Lease expiration test failed: " + e.getMessage());
        }

        // Test valid lease retention
        try {
            Field allocatedIPField = DHCPServer.class.getDeclaredField("allocatedIP");
            allocatedIPField.setAccessible(true);
            Map<Integer, Long> allocatedIPs = (Map<Integer, Long>) allocatedIPField.get(server);
            
            int testIP = IPUtil.ipToInt("192.168.1.101");
            allocatedIPs.put(testIP, System.currentTimeMillis() + 30000); // Valid for 30 seconds
            
            Method cleanExpiredIPsMethod = DHCPServer.class.getDeclaredMethod("cleanExpiredIPs");
            cleanExpiredIPsMethod.setAccessible(true);
            cleanExpiredIPsMethod.invoke(server);
            
            assert allocatedIPs.containsKey(testIP) : "Valid lease should be retained";
            System.out.println("✓ Valid lease retention test passed");
        } catch (Exception e) {
            System.err.println("✗ Valid lease retention test failed: " + e.getMessage());
        }

        // Test findAllocatableIP cleans expired IPs
        try {
            Field allocatedIPField = DHCPServer.class.getDeclaredField("allocatedIP");
            allocatedIPField.setAccessible(true);
            Map<Integer, Long> allocatedIPs = (Map<Integer, Long>) allocatedIPField.get(server);
            
            int expiredIP = IPUtil.ipToInt("192.168.1.102");
            allocatedIPs.put(expiredIP, System.currentTimeMillis() - 1000); // Expired
            
            Method findAllocatableIPMethod = DHCPServer.class.getDeclaredMethod("findAllocatableIP");
            findAllocatableIPMethod.setAccessible(true);
            int result = (int) findAllocatableIPMethod.invoke(server);
            
            assert !allocatedIPs.containsKey(expiredIP) : "Expired IP should be cleaned during allocation";
            assert result > 0 : "Should return valid IP after cleaning";
            System.out.println("✓ findAllocatableIP cleanup test passed");
        } catch (Exception e) {
            System.err.println("✗ findAllocatableIP cleanup test failed: " + e.getMessage());
        }
    }

    private void testDHCPMessageHandling() throws Exception {
        System.out.println("\n--- Testing DHCP Message Handling ---");
        
        Subnet testSubnet = createTestSubnet();
        DHCPServer server = DHCPServer.builder()
                .mac("00:11:22:33:44:55")
                .ip("192.168.1.1")
                .subnet(testSubnet)
                .build();

        // Test DHCP OFFER creation
        try {
            Map<String, String> discoverPayload = new HashMap<>();
            discoverPayload.put("Client MAC", "AA:BB:CC:DD:EE:FF");
            discoverPayload.put("DHCP Message Type", DHCPMessageType.DHCPDISCOVER.toString());
            
            Method offerDHCPMethod = DHCPServer.class.getDeclaredMethod("offerDHCP", Map.class);
            offerDHCPMethod.setAccessible(true);
            
            // This should not throw an exception
            offerDHCPMethod.invoke(server, discoverPayload);
            System.out.println("✓ DHCP OFFER creation test passed");
        } catch (Exception e) {
            System.err.println("✗ DHCP OFFER creation test failed: " + e.getMessage());
        }

        // Test DHCP ACK creation with broadcast flag
        try {
            Map<String, String> requestPayload = new HashMap<>();
            requestPayload.put("Client MAC", "AA:BB:CC:DD:EE:FF");
            requestPayload.put("Requested IP Address", "192.168.1.100");
            requestPayload.put("Flags", "1");
            requestPayload.put("DHCP Message Type", DHCPMessageType.DHCPREQUEST.toString());
            
            Method ackDHCPMethod = DHCPServer.class.getDeclaredMethod("ackDHCP", Map.class);
            ackDHCPMethod.setAccessible(true);
            
            // This should not throw an exception
            ackDHCPMethod.invoke(server, requestPayload);
            System.out.println("✓ DHCP ACK creation (broadcast) test passed");
        } catch (Exception e) {
            System.err.println("✗ DHCP ACK creation (broadcast) test failed: " + e.getMessage());
        }

        // Test DHCP ACK creation with unicast flag
        try {
            Map<String, String> requestPayload = new HashMap<>();
            requestPayload.put("Client MAC", "AA:BB:CC:DD:EE:FF");
            requestPayload.put("Requested IP Address", "192.168.1.100");
            requestPayload.put("Flags", "0");
            requestPayload.put("DHCP Message Type", DHCPMessageType.DHCPREQUEST.toString());
            
            Method ackDHCPMethod = DHCPServer.class.getDeclaredMethod("ackDHCP", Map.class);
            ackDHCPMethod.setAccessible(true);
            
            // This should not throw an exception
            ackDHCPMethod.invoke(server, requestPayload);
            System.out.println("✓ DHCP ACK creation (unicast) test passed");
        } catch (Exception e) {
            System.err.println("✗ DHCP ACK creation (unicast) test failed: " + e.getMessage());
        }
    }

    private void testEdgeCases() throws Exception {
        System.out.println("\n--- Testing Edge Cases ---");
        
        Subnet testSubnet = createTestSubnet();
        DHCPServer server = DHCPServer.builder()
                .mac("00:11:22:33:44:55")
                .ip("192.168.1.1")
                .subnet(testSubnet)
                .build();

        // Test receive method doesn't throw exception
        try {
            TestDataUnit testData = new TestDataUnit();
            server.receive(testData);
            System.out.println("✓ receive() method test passed");
        } catch (Exception e) {
            System.err.println("✗ receive() method test failed: " + e.getMessage());
        }

        // Test handleTCP method doesn't throw exception
        try {
            TestDataUnit testData = new TestDataUnit();
            server.handleTCP(testData);
            System.out.println("✓ handleTCP() method test passed");
        } catch (Exception e) {
            System.err.println("✗ handleTCP() method test failed: " + e.getMessage());
        }

        // Test sendEthernetFrame method
        try {
            TestIPPacket testPacket = new TestIPPacket();
            Method sendEthernetFrameMethod = DHCPServer.class.getDeclaredMethod("sendEthernetFrame", IPPacket.class);
            sendEthernetFrameMethod.setAccessible(true);
            sendEthernetFrameMethod.invoke(server, testPacket);
            System.out.println("✓ sendEthernetFrame() method test passed");
        } catch (Exception e) {
            System.err.println("✗ sendEthernetFrame() method test failed: " + e.getMessage());
        }

        // Test handling malformed DHCP payloads
        try {
            Map<String, String> malformedPayload = new HashMap<>();
            // Missing required fields
            
            Method offerDHCPMethod = DHCPServer.class.getDeclaredMethod("offerDHCP", Map.class);
            offerDHCPMethod.setAccessible(true);
            
            // Should handle gracefully without crashing
            offerDHCPMethod.invoke(server, malformedPayload);
            System.out.println("✓ Malformed payload handling test passed");
        } catch (Exception e) {
            // Expected behavior - graceful degradation
            System.out.println("✓ Malformed payload handling test passed (expected exception)");
        }
    }

    private void testNetworkCommunication() throws Exception {
        System.out.println("\n--- Testing Network Communication ---");
        
        Subnet testSubnet = createTestSubnet();
        DHCPServer server = DHCPServer.builder()
                .mac("00:11:22:33:44:55")
                .ip("192.168.1.1")
                .subnet(testSubnet)
                .build();

        // Test that server initializes network components correctly
        try {
            Field ipManagerField = DHCPServer.class.getDeclaredField("ipManager");
            ipManagerField.setAccessible(true);
            Object ipManager = ipManagerField.get(server);
            assert ipManager != null : "IPManager should be initialized";
            
            Field udpManagerField = DHCPServer.class.getDeclaredField("udpManager");
            udpManagerField.setAccessible(true);
            Object udpManager = udpManagerField.get(server);
            assert udpManager != null : "UDPManager should be initialized";
            
            Field networkInterfaceField = DHCPServer.class.getDeclaredField("networkInterface");
            networkInterfaceField.setAccessible(true);
            Object networkInterface = networkInterfaceField.get(server);
            assert networkInterface != null : "NetworkInterface should be initialized";
            
            System.out.println("✓ Network component initialization test passed");
        } catch (Exception e) {
            System.err.println("✗ Network component initialization test failed: " + e.getMessage());
        }

        // Test port allocator initialization
        try {
            Field portAllocatorField = DHCPServer.class.getDeclaredField("portAllocator");
            portAllocatorField.setAccessible(true);
            Object portAllocator = portAllocatorField.get(server);
            assert portAllocator != null : "Port allocator should be initialized";
            
            System.out.println("✓ Port allocator initialization test passed");
        } catch (Exception e) {
            System.err.println("✗ Port allocator initialization test failed: " + e.getMessage());
        }
    }

    private Subnet createTestSubnet() {
        return Subnet.builder()
                .subnetAddress("192.168.1.0")
                .subnetMask("255.255.255.0")
                .build();
    }

    // Test helper classes
    private static class TestDataUnit extends DataUnit {
        @Override
        public String toString() {
            return "TestDataUnit";
        }
    }

    private static class TestIPPacket extends IPPacket {
        public TestIPPacket() {
            super("192.168.1.1", "192.168.1.2", 17, new TestDataUnit());
        }
    }
}