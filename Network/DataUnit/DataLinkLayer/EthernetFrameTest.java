package Network.DataUnit.DataLinkLayer;

import Network.DataUnit.NetworkLayer.IPPacket;
import Network.DataUnit.TransportLayer.TransportDataUnit;

public class EthernetFrameTest {
    
    // Test constants
    private static final String VALID_MAC_1 = "00:11:22:33:44:55";
    private static final String VALID_MAC_2 = "AA:BB:CC:DD:EE:FF";
    private static final String BROADCAST_MAC = "FF:FF:FF:FF:FF:FF";
    private static final int IP_ETHER_TYPE = 0x0800;
    private static final String BROADCAST_IP = "255.255.255.255";
    private static final String UNICAST_IP = "192.168.1.1";
    
    // Mock transport data unit for creating IP packets
    private static class MockTransportDataUnit implements TransportDataUnit {
        @Override
        public String toString() {
            return "MockTransportDataUnit {}";
        }
    }
    
    public static void main(String[] args) throws Exception {
        EthernetFrameTest test = new EthernetFrameTest();
        
        System.out.println("Running EthernetFrame tests...\n");
        
        try {
            // Constructor tests
            test.testConstructorWithValidParameters();
            test.testConstructorWithNullParameters();
            test.testConstructorWithDifferentEtherTypes();
            
            // Getter method tests
            test.testGetterMethods();
            
            // Broadcast detection tests
            test.testBroadcastDetection();
            test.testNonBroadcastCases();
            test.testBroadcastWithCaseVariations();
            test.testBroadcastWithNullIPPacket();
            
            // toString method tests
            test.testToStringMethod();
            test.testToStringWithNullValues();
            test.testToStringIndentation();
            
            // Edge case tests
            test.testEdgeCases();
            test.testBoundaryValues();
            test.testMalformedMACAddresses();
            
            // Integration tests
            test.testIntegrationScenarios();
            
            // Additional comprehensive tests
            test.testDataUnitInterface();
            test.testEtherTypeValidation();
            test.testMACAddressFormats();
            test.testBroadcastEdgeCases();
            test.testPerformanceScenarios();
            
            System.out.println("\nAll EthernetFrame tests completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // Constructor Tests
    private void testConstructorWithValidParameters() {
        System.out.println("Testing constructor with valid parameters...");
        IPPacket ipPacket = new IPPacket(UNICAST_IP, "192.168.1.2", 6, new MockTransportDataUnit());
        EthernetFrame frame = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, IP_ETHER_TYPE, ipPacket);
        
        assert frame != null : "Frame should not be null";
        assert VALID_MAC_1.equals(frame.getDestinationMAC()) : "Destination MAC should match";
        assert VALID_MAC_2.equals(frame.getSourceMAC()) : "Source MAC should match";
        assert frame.getEtherType() == IP_ETHER_TYPE : "EtherType should match";
        assert frame.getIPPacket() == ipPacket : "IP packet should match";
        
        System.out.println("✓ Constructor with valid parameters test passed");
    }
    
    private void testConstructorWithNullParameters() {
        System.out.println("Testing constructor with null parameters...");
        
        // Test with null MAC addresses
        EthernetFrame frame1 = new EthernetFrame(null, null, IP_ETHER_TYPE, null);
        assert frame1 != null : "Frame should not be null";
        assert frame1.getDestinationMAC() == null : "Destination MAC should be null";
        assert frame1.getSourceMAC() == null : "Source MAC should be null";
        assert frame1.getIPPacket() == null : "IP packet should be null";
        
        System.out.println("✓ Constructor with null parameters test passed");
    }
    
    private void testConstructorWithDifferentEtherTypes() {
        System.out.println("Testing constructor with different EtherType values...");
        IPPacket ipPacket = new IPPacket(UNICAST_IP, "192.168.1.2", 6, new MockTransportDataUnit());
        
        int[] etherTypes = {0x0800, 0x0806, 0x86DD, 0x8100, 0, -1, 0xFFFF, Integer.MAX_VALUE, Integer.MIN_VALUE};
        
        for (int etherType : etherTypes) {
            EthernetFrame frame = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, etherType, ipPacket);
            assert frame.getEtherType() == etherType : "EtherType should match: " + etherType;
        }
        
        System.out.println("✓ Constructor with different EtherType values test passed");
    }
    
    // Getter Method Tests
    private void testGetterMethods() {
        System.out.println("Testing getter methods...");
        IPPacket ipPacket = new IPPacket(UNICAST_IP, "192.168.1.2", 6, new MockTransportDataUnit());
        EthernetFrame frame = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, IP_ETHER_TYPE, ipPacket);
        
        assert VALID_MAC_1.equals(frame.getDestinationMAC()) : "getDestinationMAC should return correct value";
        assert VALID_MAC_2.equals(frame.getSourceMAC()) : "getSourceMAC should return correct value";
        assert frame.getEtherType() == IP_ETHER_TYPE : "getEtherType should return correct value";
        assert frame.getIPPacket() == ipPacket : "getIPPacket should return correct value";
        
        System.out.println("✓ Getter methods test passed");
    }
    
    // Broadcast Detection Tests
    private void testBroadcastDetection() {
        System.out.println("Testing broadcast detection...");
        IPPacket broadcastPacket = new IPPacket("192.168.1.1", BROADCAST_IP, 6, new MockTransportDataUnit());
        EthernetFrame frame = new EthernetFrame(BROADCAST_MAC, VALID_MAC_2, IP_ETHER_TYPE, broadcastPacket);
        
        assert frame.isBroadcast() : "Should detect broadcast frame";
        
        System.out.println("✓ Broadcast detection test passed");
    }
    
    private void testNonBroadcastCases() {
        System.out.println("Testing non-broadcast cases...");
        
        // Test with unicast MAC and broadcast IP
        IPPacket broadcastPacket = new IPPacket("192.168.1.1", BROADCAST_IP, 6, new MockTransportDataUnit());
        EthernetFrame frame1 = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, IP_ETHER_TYPE, broadcastPacket);
        assert !frame1.isBroadcast() : "Should not detect broadcast with unicast MAC";
        
        // Test with broadcast MAC and unicast IP
        IPPacket unicastPacket = new IPPacket("192.168.1.1", UNICAST_IP, 6, new MockTransportDataUnit());
        EthernetFrame frame2 = new EthernetFrame(BROADCAST_MAC, VALID_MAC_2, IP_ETHER_TYPE, unicastPacket);
        assert !frame2.isBroadcast() : "Should not detect broadcast with unicast IP";
        
        // Test with both unicast
        EthernetFrame frame3 = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, IP_ETHER_TYPE, unicastPacket);
        assert !frame3.isBroadcast() : "Should not detect broadcast with both unicast";
        
        System.out.println("✓ Non-broadcast cases test passed");
    }
    
    private void testBroadcastWithCaseVariations() {
        System.out.println("Testing broadcast with case variations...");
        IPPacket broadcastPacket = new IPPacket("192.168.1.1", BROADCAST_IP, 6, new MockTransportDataUnit());
        
        // Test with lowercase broadcast MAC
        String lowercaseBroadcastMAC = "ff:ff:ff:ff:ff:ff";
        EthernetFrame frame = new EthernetFrame(lowercaseBroadcastMAC, VALID_MAC_2, IP_ETHER_TYPE, broadcastPacket);
        assert !frame.isBroadcast() : "Should be case sensitive for MAC comparison";
        
        System.out.println("✓ Broadcast case variations test passed");
    }
    
    private void testBroadcastWithNullIPPacket() {
        System.out.println("Testing broadcast with null IP packet...");
        EthernetFrame frame = new EthernetFrame(BROADCAST_MAC, VALID_MAC_2, IP_ETHER_TYPE, null);
        
        try {
            frame.isBroadcast();
            assert false : "Should throw NullPointerException";
        } catch (NullPointerException e) {
            // Expected behavior
        }
        
        System.out.println("✓ Broadcast with null IP packet test passed");
    }
    
    // toString Method Tests
    private void testToStringMethod() {
        System.out.println("Testing toString method...");
        IPPacket ipPacket = new IPPacket(UNICAST_IP, "192.168.1.2", 6, new MockTransportDataUnit());
        EthernetFrame frame = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, IP_ETHER_TYPE, ipPacket);
        
        String result = frame.toString();
        assert result != null : "toString should not return null";
        assert result.contains("EthernetFrame") : "Should contain class name";
        assert result.contains("Destination MAC: " + VALID_MAC_1) : "Should contain destination MAC";
        assert result.contains("Source MAC: " + VALID_MAC_2) : "Should contain source MAC";
        assert result.contains("EtherType: " + IP_ETHER_TYPE) : "Should contain EtherType";
        
        System.out.println("✓ toString method test passed");
    }
    
    private void testToStringWithNullValues() {
        System.out.println("Testing toString with null values...");
        EthernetFrame frame = new EthernetFrame(null, null, IP_ETHER_TYPE, null);
        
        try {
            frame.toString();
            assert false : "Should throw NullPointerException";
        } catch (NullPointerException e) {
            // Expected behavior
        }
        
        System.out.println("✓ toString with null values test passed");
    }
    
    private void testToStringIndentation() {
        System.out.println("Testing toString indentation...");
        IPPacket ipPacket = new IPPacket(UNICAST_IP, "192.168.1.2", 6, new MockTransportDataUnit());
        EthernetFrame frame = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, IP_ETHER_TYPE, ipPacket);
        
        String result = frame.toString();
        // Check that IP packet content is properly indented
        String[] lines = result.split("\n");
        boolean foundIndentedContent = false;
        for (String line : lines) {
            if (line.contains("Source IP:") && line.startsWith("\t\t")) {
                foundIndentedContent = true;
                break;
            }
        }
        assert foundIndentedContent : "IP packet content should be properly indented";
        
        System.out.println("✓ toString indentation test passed");
    }
    
    // Edge Case Tests
    private void testEdgeCases() {
        System.out.println("Testing edge cases...");
        
        // Empty MAC addresses
        IPPacket ipPacket = new IPPacket(UNICAST_IP, "192.168.1.2", 6, new MockTransportDataUnit());
        EthernetFrame frame1 = new EthernetFrame("", "", IP_ETHER_TYPE, ipPacket);
        assert "".equals(frame1.getDestinationMAC()) : "Should handle empty destination MAC";
        assert "".equals(frame1.getSourceMAC()) : "Should handle empty source MAC";
        assert !frame1.isBroadcast() : "Empty MAC should not be broadcast";
        
        // Negative EtherType
        EthernetFrame frame2 = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, -1, ipPacket);
        assert frame2.getEtherType() == -1 : "Should handle negative EtherType";
        
        // Large EtherType
        EthernetFrame frame3 = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, Integer.MAX_VALUE, ipPacket);
        assert frame3.getEtherType() == Integer.MAX_VALUE : "Should handle large EtherType";
        
        System.out.println("✓ Edge cases test passed");
    }
    
    private void testBoundaryValues() {
        System.out.println("Testing boundary values...");
        IPPacket ipPacket = new IPPacket(UNICAST_IP, "192.168.1.2", 6, new MockTransportDataUnit());
        
        // Minimum EtherType value
        EthernetFrame frame1 = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, 0, ipPacket);
        assert frame1.getEtherType() == 0 : "Should handle minimum EtherType";
        
        // Maximum 16-bit EtherType value
        EthernetFrame frame2 = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, 0xFFFF, ipPacket);
        assert frame2.getEtherType() == 0xFFFF : "Should handle maximum 16-bit EtherType";
        
        // Standard EtherType values
        int[] standardTypes = {0x0800, 0x0806, 0x86DD, 0x8100};
        for (int etherType : standardTypes) {
            EthernetFrame frame = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, etherType, ipPacket);
            assert frame.getEtherType() == etherType : "Should handle standard EtherType: " + etherType;
        }
        
        System.out.println("✓ Boundary values test passed");
    }
    
    private void testMalformedMACAddresses() {
        System.out.println("Testing malformed MAC addresses...");
        IPPacket broadcastPacket = new IPPacket("192.168.1.1", BROADCAST_IP, 6, new MockTransportDataUnit());
        
        // Different MAC format (dashes instead of colons)
        String malformedMAC = "FF-FF-FF-FF-FF-FF";
        EthernetFrame frame = new EthernetFrame(malformedMAC, VALID_MAC_2, IP_ETHER_TYPE, broadcastPacket);
        assert !frame.isBroadcast() : "Should not detect broadcast with different MAC format";
        
        // MAC without separators
        String noSeparatorMAC = "FFFFFFFFFFFF";
        EthernetFrame frame2 = new EthernetFrame(noSeparatorMAC, VALID_MAC_2, IP_ETHER_TYPE, broadcastPacket);
        assert !frame2.isBroadcast() : "Should not detect broadcast with no separator MAC format";
        
        System.out.println("✓ Malformed MAC addresses test passed");
    }
    
    // Integration Tests
    private void testIntegrationScenarios() {
        System.out.println("Testing integration scenarios...");
        
        // Test with real IPPacket object
        IPPacket realIPPacket = new IPPacket("10.0.0.1", "10.0.0.2", 17, new MockTransportDataUnit());
        EthernetFrame frame = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, IP_ETHER_TYPE, realIPPacket);
        
        // Verify all methods work together
        assert frame.getDestinationMAC() != null : "Should have destination MAC";
        assert frame.getSourceMAC() != null : "Should have source MAC";
        assert frame.getEtherType() > 0 : "Should have positive EtherType";
        assert frame.getIPPacket() != null : "Should have IP packet";
        assert !frame.isBroadcast() : "Should not be broadcast";
        assert frame.toString() != null : "Should have string representation";
        
        // Test immutability
        EthernetFrame frame1 = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, IP_ETHER_TYPE, realIPPacket);
        EthernetFrame frame2 = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, IP_ETHER_TYPE, realIPPacket);
        
        assert frame1.getDestinationMAC().equals(frame2.getDestinationMAC()) : "Should maintain consistent destination MAC";
        assert frame1.getSourceMAC().equals(frame2.getSourceMAC()) : "Should maintain consistent source MAC";
        assert frame1.getEtherType() == frame2.getEtherType() : "Should maintain consistent EtherType";
        
        System.out.println("✓ Integration scenarios test passed");
    }
    
    // Additional Comprehensive Tests
    private void testDataUnitInterface() {
        System.out.println("Testing DataUnit interface compliance...");
        IPPacket ipPacket = new IPPacket(UNICAST_IP, "192.168.1.2", 6, new MockTransportDataUnit());
        EthernetFrame frame = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, IP_ETHER_TYPE, ipPacket);
        
        // Test that EthernetFrame implements DataUnit
        assert frame instanceof Network.DataUnit.DataUnit : "EthernetFrame should implement DataUnit interface";
        
        System.out.println("✓ DataUnit interface compliance test passed");
    }
    
    private void testEtherTypeValidation() {
        System.out.println("Testing EtherType validation scenarios...");
        IPPacket ipPacket = new IPPacket(UNICAST_IP, "192.168.1.2", 6, new MockTransportDataUnit());
        
        // Test with common network protocol EtherTypes
        int[] protocolTypes = {
            0x0800, // IPv4
            0x0806, // ARP
            0x86DD, // IPv6
            0x8100, // VLAN-tagged frame
            0x88CC, // LLDP
            0x8847, // MPLS unicast
            0x8848  // MPLS multicast
        };
        
        for (int etherType : protocolTypes) {
            EthernetFrame frame = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, etherType, ipPacket);
            assert frame.getEtherType() == etherType : "Should correctly store EtherType: 0x" + Integer.toHexString(etherType);
        }
        
        System.out.println("✓ EtherType validation test passed");
    }
    
    private void testMACAddressFormats() {
        System.out.println("Testing various MAC address formats...");
        IPPacket ipPacket = new IPPacket(UNICAST_IP, "192.168.1.2", 6, new MockTransportDataUnit());
        
        // Test different valid MAC address formats
        String[] macFormats = {
            "00:11:22:33:44:55",
            "AA:BB:CC:DD:EE:FF",
            "01:23:45:67:89:AB",
            "FE:DC:BA:98:76:54"
        };
        
        for (String mac : macFormats) {
            EthernetFrame frame = new EthernetFrame(mac, VALID_MAC_2, IP_ETHER_TYPE, ipPacket);
            assert mac.equals(frame.getDestinationMAC()) : "Should correctly store MAC address: " + mac;
        }
        
        System.out.println("✓ MAC address formats test passed");
    }
    
    private void testBroadcastEdgeCases() {
        System.out.println("Testing broadcast detection edge cases...");
        
        // Test with different broadcast IP variations
        String[] broadcastIPs = {
            "255.255.255.255",
            "192.168.1.255", // Subnet broadcast
            "10.0.255.255"   // Network broadcast
        };
        
        for (String broadcastIP : broadcastIPs) {
            IPPacket packet = new IPPacket("10.0.0.1", broadcastIP, 17, new MockTransportDataUnit());
            EthernetFrame frame = new EthernetFrame(BROADCAST_MAC, VALID_MAC_2, IP_ETHER_TYPE, packet);
            
            if (broadcastIP.equals("255.255.255.255")) {
                assert frame.isBroadcast() : "Should detect global broadcast: " + broadcastIP;
            } else {
                assert !frame.isBroadcast() : "Should not detect non-global broadcast as broadcast: " + broadcastIP;
            }
        }
        
        System.out.println("✓ Broadcast edge cases test passed");
    }
    
    private void testPerformanceScenarios() {
        System.out.println("Testing performance scenarios...");
        
        // Test with large number of frames
        IPPacket ipPacket = new IPPacket(UNICAST_IP, "192.168.1.2", 6, new MockTransportDataUnit());
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 1000; i++) {
            EthernetFrame frame = new EthernetFrame(VALID_MAC_1, VALID_MAC_2, IP_ETHER_TYPE, ipPacket);
            frame.getDestinationMAC();
            frame.getSourceMAC();
            frame.getEtherType();
            frame.getIPPacket();
            frame.isBroadcast();
            frame.toString();
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assert duration < 5000 : "Performance test should complete within 5 seconds, took: " + duration + "ms";
        
        System.out.println("✓ Performance scenarios test passed (Duration: " + duration + "ms)");
    }
}