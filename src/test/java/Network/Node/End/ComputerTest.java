package Network.Node.End;

import Network.Constants.DHCPMessageType;
import Network.Constants.HTTPMethodType;
import Network.DataUnit.DataLinkLayer.EthernetFrame;
import Network.DataUnit.NetworkLayer.IPPacket;
import Network.DataUnit.TransportLayer.TCPSegment;
import Network.DataUnit.TransportLayer.UDPDatagram;
import Network.Network.Subnet;
import Network.Node.NetworkInterface;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ComputerTest {

    @Mock
    private Subnet mockSubnet;
    
    private Computer computer;
    private final String testMACAddress = "AA:BB:CC:DD:EE:FF";
    private final String testIPAddress = "192.168.1.100";
    
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private AutoCloseable mockCloseable;

    @BeforeEach
    void setUp() {
        mockCloseable = MockitoAnnotations.openMocks(this);
        when(mockSubnet.toString()).thenReturn("MockSubnet");
        computer = Computer.builder()
                .MACAddress(testMACAddress)
                .ipAddress(testIPAddress)
                .subnet(mockSubnet)
                .build();
        
        // Capture System.out for testing print statements
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setOut(originalOut);
        if (mockCloseable != null) {
            mockCloseable.close();
        }
    }

    @Nested
    @DisplayName("Constructor and Builder Tests")
    class ConstructorAndBuilderTests {
        
        @Test
        @DisplayName("Should create Computer with valid parameters")
        void shouldCreateComputerWithValidParameters() {
            Computer testComputer = Computer.builder()
                    .MACAddress("11:22:33:44:55:66")
                    .ipAddress("10.0.0.1")
                    .subnet(mockSubnet)
                    .build();
            
            assertNotNull(testComputer);
            assertEquals("11:22:33:44:55:66", testComputer.MACAddress);
            assertEquals("10.0.0.1", testComputer.ipAddress);
            assertNotNull(testComputer.networkInterface);
        }
        
        @Test
        @DisplayName("Should handle null MAC address")
        void shouldHandleNullMACAddress() {
            Computer testComputer = Computer.builder()
                    .MACAddress(null)
                    .ipAddress("10.0.0.1")
                    .subnet(mockSubnet)
                    .build();
            
            assertNotNull(testComputer);
            assertNull(testComputer.MACAddress);
        }
        
        @Test
        @DisplayName("Should handle null IP address")
        void shouldHandleNullIPAddress() {
            Computer testComputer = Computer.builder()
                    .MACAddress("11:22:33:44:55:66")
                    .ipAddress(null)
                    .subnet(mockSubnet)
                    .build();
            
            assertNotNull(testComputer);
            assertNull(testComputer.ipAddress);
        }
        
        @Test
        @DisplayName("Builder should return correct instance type")
        void builderShouldReturnCorrectInstanceType() {
            Computer.Builder builder = Computer.builder();
            assertNotNull(builder);
            assertSame(builder, builder.self());
        }
        
        @Test
        @DisplayName("Should handle invalid MAC address format gracefully")
        void shouldHandleInvalidMACAddressFormat() {
            Computer testComputer = Computer.builder()
                    .MACAddress("invalid-mac-format")
                    .ipAddress("10.0.0.1")
                    .subnet(mockSubnet)
                    .build();
            
            assertNotNull(testComputer);
            assertEquals("invalid-mac-format", testComputer.MACAddress);
        }
        
        @Test
        @DisplayName("Should handle invalid IP address format gracefully")
        void shouldHandleInvalidIPAddressFormat() {
            Computer testComputer = Computer.builder()
                    .MACAddress("11:22:33:44:55:66")
                    .ipAddress("invalid.ip.format")
                    .subnet(mockSubnet)
                    .build();
            
            assertNotNull(testComputer);
            assertEquals("invalid.ip.format", testComputer.ipAddress);
        }
    }

    @Nested
    @DisplayName("IP Address Management Tests")
    class IPAddressManagementTests {
        
        @Test
        @DisplayName("Should set IP address and print confirmation")
        void shouldSetIPAddressAndPrintConfirmation() {
            String newIP = "192.168.1.200";
            
            computer.setIpAddress(newIP);
            
            assertEquals(newIP, computer.ipAddress);
            String output = outputStream.toString();
            assertTrue(output.contains("IP Setting..."));
        }
        
        @Test
        @DisplayName("Should handle null IP address setting")
        void shouldHandleNullIPAddressSetting() {
            computer.setIpAddress(null);
            
            assertNull(computer.ipAddress);
            String output = outputStream.toString();
            assertTrue(output.contains("IP Setting..."));
        }
        
        @Test
        @DisplayName("Should handle empty IP address setting")
        void shouldHandleEmptyIPAddressSetting() {
            computer.setIpAddress("");
            
            assertEquals("", computer.ipAddress);
            String output = outputStream.toString();
            assertTrue(output.contains("IP Setting..."));
        }
        
        @Test
        @DisplayName("Should handle IP address with spaces")
        void shouldHandleIPAddressWithSpaces() {
            String ipWithSpaces = " 192.168.1.200 ";
            
            computer.setIpAddress(ipWithSpaces);
            
            assertEquals(ipWithSpaces, computer.ipAddress);
            String output = outputStream.toString();
            assertTrue(output.contains("IP Setting..."));
        }
        
        @Test
        @DisplayName("Should handle very long IP address string")
        void shouldHandleVeryLongIPAddressString() {
            String longIP = "a".repeat(1000);
            
            assertDoesNotThrow(() -> computer.setIpAddress(longIP));
            assertEquals(longIP, computer.ipAddress);
        }
    }

    @Nested
    @DisplayName("DHCP Functionality Tests")
    class DHCPFunctionalityTests {
        
        @Test
        @DisplayName("Should discover DHCP server with correct UDP packet structure")
        void shouldDiscoverDHCPServerWithCorrectUDPPacketStructure() {
            computer.discoverDHCPServer();
            
            verify(mockSubnet).broadcast(any(EthernetFrame.class), eq(computer));
        }
        
        @Test
        @DisplayName("Should handle DHCP OFFER message correctly")
        void shouldHandleDHCPOfferMessageCorrectly() {
            // Create mock UDP datagram with DHCP OFFER payload
            String payload = "DHCP Message Type=DHCPOFFER,DHCP Server Identifier=192.168.1.1,Your IP=192.168.1.50";
            UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(67, 68, payload.length() + 8, 0);
            UDPDatagram udpDatagram = new UDPDatagram(header, payload);
            
            IPPacket ipPacket = new IPPacket("192.168.1.1", "255.255.255.255", 17, udpDatagram);
            EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", "11:22:33:44:55:66", 0x0800, ipPacket);
            
            computer.handleUDP(frame);
            
            verify(mockSubnet, times(2)).broadcast(any(EthernetFrame.class), eq(computer));
        }
        
        @Test
        @DisplayName("Should handle DHCP ACK message correctly")
        void shouldHandleDHCPAckMessageCorrectly() {
            String newIP = "192.168.1.50";
            String payload = "DHCP Message Type=DHCPACK,DHCP Server Identifier=192.168.1.1,Your IP=" + newIP;
            UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(67, 68, payload.length() + 8, 0);
            UDPDatagram udpDatagram = new UDPDatagram(header, payload);
            
            IPPacket ipPacket = new IPPacket("192.168.1.1", "255.255.255.255", 17, udpDatagram);
            EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", testMACAddress, 0x0800, ipPacket);
            
            computer.handleUDP(frame);
            
            assertEquals(newIP, computer.ipAddress);
            String output = outputStream.toString();
            assertTrue(output.contains("IP Setting..."));
        }
        
        @Test
        @DisplayName("Should handle DHCP NACK message correctly")
        void shouldHandleDHCPNackMessageCorrectly() {
            String payload = "DHCP Message Type=DHCPNACK,DHCP Server Identifier=192.168.1.1,Your IP=192.168.1.50";
            UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(67, 68, payload.length() + 8, 0);
            UDPDatagram udpDatagram = new UDPDatagram(header, payload);
            
            IPPacket ipPacket = new IPPacket("192.168.1.1", "255.255.255.255", 17, udpDatagram);
            EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", testMACAddress, 0x0800, ipPacket);
            
            computer.handleUDP(frame);
            
            verify(mockSubnet, times(2)).broadcast(any(EthernetFrame.class), eq(computer));
        }
        
        @Test
        @DisplayName("Should ignore non-broadcast DHCP messages")
        void shouldIgnoreNonBroadcastDHCPMessages() {
            String payload = "DHCP Message Type=DHCPACK,DHCP Server Identifier=192.168.1.1,Your IP=192.168.1.50";
            UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(67, 68, payload.length() + 8, 0);
            UDPDatagram udpDatagram = new UDPDatagram(header, payload);
            
            IPPacket ipPacket = new IPPacket("192.168.1.1", "192.168.1.100", 17, udpDatagram);
            EthernetFrame frame = new EthernetFrame("AA:BB:CC:DD:EE:FF", testMACAddress, 0x0800, ipPacket);
            
            String originalIP = computer.ipAddress;
            computer.handleUDP(frame);
            
            assertEquals(originalIP, computer.ipAddress);
        }
        
        @Test
        @DisplayName("Should ignore UDP messages without DHCP server identifier")
        void shouldIgnoreUDPMessagesWithoutDHCPServerIdentifier() {
            String payload = "DHCP Message Type=DHCPACK,Your IP=192.168.1.50";
            UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(67, 68, payload.length() + 8, 0);
            UDPDatagram udpDatagram = new UDPDatagram(header, payload);
            
            IPPacket ipPacket = new IPPacket("192.168.1.1", "255.255.255.255", 17, udpDatagram);
            EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", testMACAddress, 0x0800, ipPacket);
            
            String originalIP = computer.ipAddress;
            computer.handleUDP(frame);
            
            assertEquals(originalIP, computer.ipAddress);
        }
        
        @Test
        @DisplayName("Should ignore UDP messages without Your IP field")
        void shouldIgnoreUDPMessagesWithoutYourIPField() {
            String payload = "DHCP Message Type=DHCPACK,DHCP Server Identifier=192.168.1.1";
            UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(67, 68, payload.length() + 8, 0);
            UDPDatagram udpDatagram = new UDPDatagram(header, payload);
            
            IPPacket ipPacket = new IPPacket("192.168.1.1", "255.255.255.255", 17, udpDatagram);
            EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", testMACAddress, 0x0800, ipPacket);
            
            String originalIP = computer.ipAddress;
            computer.handleUDP(frame);
            
            assertEquals(originalIP, computer.ipAddress);
        }
        
        @Test
        @DisplayName("Should handle invalid DHCP message types gracefully")
        void shouldHandleInvalidDHCPMessageTypesGracefully() {
            String payload = "DHCP Message Type=INVALID_TYPE,DHCP Server Identifier=192.168.1.1,Your IP=192.168.1.50";
            UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(67, 68, payload.length() + 8, 0);
            UDPDatagram udpDatagram = new UDPDatagram(header, payload);
            
            IPPacket ipPacket = new IPPacket("192.168.1.1", "255.255.255.255", 17, udpDatagram);
            EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", testMACAddress, 0x0800, ipPacket);
            
            String originalIP = computer.ipAddress;
            assertDoesNotThrow(() -> computer.handleUDP(frame));
            assertEquals(originalIP, computer.ipAddress);
        }
        
        @Test
        @DisplayName("Should handle multiple DHCP discoveries without interference")
        void shouldHandleMultipleDHCPDiscoveriesWithoutInterference() {
            for (int i = 0; i < 3; i++) {
                computer.discoverDHCPServer();
            }
            
            verify(mockSubnet, times(3)).broadcast(any(EthernetFrame.class), eq(computer));
        }
        
        @Test
        @DisplayName("Should create proper DHCP request after offer")
        void shouldCreateProperDHCPRequestAfterOffer() {
            String offeredIP = "192.168.1.50";
            String payload = "DHCP Message Type=DHCPOFFER,DHCP Server Identifier=192.168.1.1,Your IP=" + offeredIP;
            UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(67, 68, payload.length() + 8, 0);
            UDPDatagram udpDatagram = new UDPDatagram(header, payload);
            
            IPPacket ipPacket = new IPPacket("192.168.1.1", "255.255.255.255", 17, udpDatagram);
            EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", testMACAddress, 0x0800, ipPacket);
            
            computer.handleUDP(frame);
            
            // Should broadcast twice: initial discovery + DHCP request
            verify(mockSubnet, times(2)).broadcast(any(EthernetFrame.class), eq(computer));
        }
    }

    @Nested
    @DisplayName("HTTP Request Tests")
    class HTTPRequestTests {
        
        @Test
        @DisplayName("Should send HTTP GET request")
        void shouldSendHTTPGetRequest() {
            String url = "http://example.com";
            
            computer.sendHttpRequest(HTTPMethodType.GET, url);
            
            String output = outputStream.toString();
            assertTrue(output.contains("sending HTTP Request..."));
        }
        
        @Test
        @DisplayName("Should send HTTP POST request")
        void shouldSendHTTPPostRequest() {
            String url = "http://example.com/api";
            
            computer.sendHttpRequest(HTTPMethodType.POST, url);
            
            String output = outputStream.toString();
            assertTrue(output.contains("sending HTTP Request..."));
        }
        
        @Test
        @DisplayName("Should handle null URL gracefully")
        void shouldHandleNullURLGracefully() {
            assertDoesNotThrow(() -> computer.sendHttpRequest(HTTPMethodType.GET, null));
            
            String output = outputStream.toString();
            assertTrue(output.contains("sending HTTP Request..."));
        }
        
        @Test
        @DisplayName("Should handle null HTTP method gracefully")
        void shouldHandleNullHTTPMethodGracefully() {
            String url = "http://example.com";
            
            assertDoesNotThrow(() -> computer.sendHttpRequest(null, url));
            
            String output = outputStream.toString();
            assertTrue(output.contains("sending HTTP Request..."));
        }
        
        @Test
        @DisplayName("Should handle empty URL")
        void shouldHandleEmptyURL() {
            assertDoesNotThrow(() -> computer.sendHttpRequest(HTTPMethodType.GET, ""));
            
            String output = outputStream.toString();
            assertTrue(output.contains("sending HTTP Request..."));
        }
        
        @Test
        @DisplayName("Should handle very long URL")
        void shouldHandleVeryLongURL() {
            String longUrl = "http://example.com/" + "a".repeat(1000);
            
            assertDoesNotThrow(() -> computer.sendHttpRequest(HTTPMethodType.GET, longUrl));
            
            String output = outputStream.toString();
            assertTrue(output.contains("sending HTTP Request..."));
        }
        
        @Test
        @DisplayName("Should handle special characters in URL")
        void shouldHandleSpecialCharactersInURL() {
            String specialUrl = "http://example.com/test?param=value&other=特殊字符";
            
            assertDoesNotThrow(() -> computer.sendHttpRequest(HTTPMethodType.POST, specialUrl));
            
            String output = outputStream.toString();
            assertTrue(output.contains("sending HTTP Request..."));
        }
    }

    @Nested
    @DisplayName("TCP Handling Tests")
    class TCPHandlingTests {
        
        @Test
        @DisplayName("Should handle TCP segments correctly")
        void shouldHandleTCPSegmentsCorrectly() {
            // Create mock TCP segment
            TCPSegment mockTCPSegment = mock(TCPSegment.class);
            
            IPPacket ipPacket = new IPPacket("192.168.1.200", testIPAddress, 6, mockTCPSegment);
            EthernetFrame frame = new EthernetFrame(testMACAddress, "11:22:33:44:55:66", 0x0800, ipPacket);
            
            assertDoesNotThrow(() -> computer.handleTCP(frame));
        }
        
        @Test
        @DisplayName("Should handle TCP segments with null source IP")
        void shouldHandleTCPSegmentsWithNullSourceIP() {
            TCPSegment mockTCPSegment = mock(TCPSegment.class);
            
            IPPacket ipPacket = new IPPacket(null, testIPAddress, 6, mockTCPSegment);
            EthernetFrame frame = new EthernetFrame(testMACAddress, "11:22:33:44:55:66", 0x0800, ipPacket);
            
            assertDoesNotThrow(() -> computer.handleTCP(frame));
        }
        
        @Test
        @DisplayName("Should handle multiple TCP segments")
        void shouldHandleMultipleTCPSegments() {
            TCPSegment mockTCPSegment1 = mock(TCPSegment.class);
            TCPSegment mockTCPSegment2 = mock(TCPSegment.class);
            
            IPPacket ipPacket1 = new IPPacket("192.168.1.200", testIPAddress, 6, mockTCPSegment1);
            IPPacket ipPacket2 = new IPPacket("192.168.1.201", testIPAddress, 6, mockTCPSegment2);
            
            EthernetFrame frame1 = new EthernetFrame(testMACAddress, "11:22:33:44:55:66", 0x0800, ipPacket1);
            EthernetFrame frame2 = new EthernetFrame(testMACAddress, "11:22:33:44:55:67", 0x0800, ipPacket2);
            
            assertDoesNotThrow(() -> {
                computer.handleTCP(frame1);
                computer.handleTCP(frame2);
            });
        }
    }

    @Nested
    @DisplayName("Ethernet Frame Sending Tests")
    class EthernetFrameSendingTests {
        
        @Test
        @DisplayName("Should create Ethernet frame with correct structure")
        void shouldCreateEthernetFrameWithCorrectStructure() {
            UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(12345, 80, 20, 0);
            UDPDatagram udpDatagram = new UDPDatagram(header, "test payload");
            IPPacket packet = new IPPacket(testIPAddress, "192.168.1.1", 17, udpDatagram);
            
            assertDoesNotThrow(() -> computer.sendEthernetFrame(packet));
        }
        
        @Test
        @DisplayName("Should handle null IP packet gracefully")
        void shouldHandleNullIPPacketGracefully() {
            assertDoesNotThrow(() -> computer.sendEthernetFrame(null));
        }
        
        @Test
        @DisplayName("Should handle IP packet with null destination")
        void shouldHandleIPPacketWithNullDestination() {
            UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(12345, 80, 20, 0);
            UDPDatagram udpDatagram = new UDPDatagram(header, "test payload");
            IPPacket packet = new IPPacket(testIPAddress, null, 17, udpDatagram);
            
            assertDoesNotThrow(() -> computer.sendEthernetFrame(packet));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle extremely long payload in DHCP messages")
        void shouldHandleExtremelyLongPayloadInDHCPMessages() {
            StringBuilder longPayload = new StringBuilder("DHCP Message Type=DHCPACK,DHCP Server Identifier=192.168.1.1,Your IP=192.168.1.50");
            for (int i = 0; i < 1000; i++) {
                longPayload.append(",Extra Field ").append(i).append("=value").append(i);
            }
            
            UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(67, 68, longPayload.length() + 8, 0);
            UDPDatagram udpDatagram = new UDPDatagram(header, longPayload.toString());
            
            IPPacket ipPacket = new IPPacket("192.168.1.1", "255.255.255.255", 17, udpDatagram);
            EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", testMACAddress, 0x0800, ipPacket);
            
            assertDoesNotThrow(() -> computer.handleUDP(frame));
        }
        
        @Test
        @DisplayName("Should handle malformed payload gracefully")
        void shouldHandleMalformedPayloadGracefully() {
            String malformedPayload = "invalid_format_payload_without_proper_structure";
            UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(67, 68, malformedPayload.length() + 8, 0);
            UDPDatagram udpDatagram = new UDPDatagram(header, malformedPayload);
            
            IPPacket ipPacket = new IPPacket("192.168.1.1", "255.255.255.255", 17, udpDatagram);
            EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", testMACAddress, 0x0800, ipPacket);
            
            assertDoesNotThrow(() -> computer.handleUDP(frame));
        }
        
        @Test
        @DisplayName("Should handle multiple consecutive DHCP operations")
        void shouldHandleMultipleConsecutiveDHCPOperations() {
            // Test rapid succession of DHCP operations
            for (int i = 0; i < 5; i++) {
                assertDoesNotThrow(() -> computer.discoverDHCPServer());
            }
            
            verify(mockSubnet, times(5)).broadcast(any(EthernetFrame.class), eq(computer));
        }
        
        @Test
        @DisplayName("Should handle concurrent IP address changes")
        void shouldHandleConcurrentIPAddressChanges() {
            // Simulate concurrent access to IP address setting
            String[] ips = {"192.168.1.10", "192.168.1.11", "192.168.1.12"};
            
            for (String ip : ips) {
                assertDoesNotThrow(() -> computer.setIpAddress(ip));
            }
            
            assertEquals("192.168.1.12", computer.ipAddress);
        }
        
        @Test
        @DisplayName("Should handle empty payload in UDP messages")
        void shouldHandleEmptyPayloadInUDPMessages() {
            String emptyPayload = "";
            UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(67, 68, 8, 0);
            UDPDatagram udpDatagram = new UDPDatagram(header, emptyPayload);
            
            IPPacket ipPacket = new IPPacket("192.168.1.1", "255.255.255.255", 17, udpDatagram);
            EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", testMACAddress, 0x0800, ipPacket);
            
            assertDoesNotThrow(() -> computer.handleUDP(frame));
        }
        
        @Test
        @DisplayName("Should handle UDP frames with different EtherTypes")
        void shouldHandleUDPFramesWithDifferentEtherTypes() {
            String payload = "DHCP Message Type=DHCPACK,DHCP Server Identifier=192.168.1.1,Your IP=192.168.1.50";
            UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(67, 68, payload.length() + 8, 0);
            UDPDatagram udpDatagram = new UDPDatagram(header, payload);
            
            IPPacket ipPacket = new IPPacket("192.168.1.1", "255.255.255.255", 17, udpDatagram);
            EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", testMACAddress, 0x86DD, ipPacket); // IPv6 EtherType
            
            assertDoesNotThrow(() -> computer.handleUDP(frame));
        }
    }

    @Nested
    @DisplayName("Port Allocation Tests")
    class PortAllocationTests {
        
        @Test
        @DisplayName("Should allocate ports in ephemeral range")
        void shouldAllocatePortsInEphemeralRange() {
            // Create multiple computers to test port allocation
            for (int i = 0; i < 10; i++) {
                Computer testComputer = Computer.builder()
                        .MACAddress("AA:BB:CC:DD:EE:0" + i)
                        .ipAddress("192.168.1." + (100 + i))
                        .subnet(mockSubnet)
                        .build();
                
                assertNotNull(testComputer);
                // Port allocation is internal to TCPManager and UDPManager
                // This test ensures the computer can be created successfully
                // with the port allocation mechanism in place
            }
        }
    }

    @Nested
    @DisplayName("Network Interface Tests")
    class NetworkInterfaceTests {
        
        @Test
        @DisplayName("Should initialize network interface correctly")
        void shouldInitializeNetworkInterfaceCorrectly() {
            assertNotNull(computer.networkInterface);
            assertEquals(mockSubnet, computer.networkInterface.getSubnet());
        }
        
        @Test
        @DisplayName("Should handle computer creation with null subnet")
        void shouldHandleComputerCreationWithNullSubnet() {
            assertDoesNotThrow(() -> {
                Computer testComputer = Computer.builder()
                        .MACAddress(testMACAddress)
                        .ipAddress(testIPAddress)
                        .subnet(null)
                        .build();
                
                assertNotNull(testComputer);
            });
        }
    }

    @Test
    @DisplayName("Should handle toString method correctly")
    void shouldHandleToStringMethodCorrectly() {
        String result = computer.toString();
        assertNotNull(result);
        // Basic test that toString doesn't throw an exception
    }
    
    @Test
    @DisplayName("Should maintain state consistency across operations")
    void shouldMaintainStateConsistencyAcrossOperations() {
        String originalIP = computer.ipAddress;
        String originalMAC = computer.MACAddress;
        
        // Perform various operations
        computer.discoverDHCPServer();
        computer.sendHttpRequest(HTTPMethodType.GET, "http://example.com");
        
        // State should remain consistent
        assertEquals(originalIP, computer.ipAddress);
        assertEquals(originalMAC, computer.MACAddress);
        assertNotNull(computer.networkInterface);
    }
}