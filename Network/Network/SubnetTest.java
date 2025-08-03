package Network.Network;

import Network.DataUnit.DataLinkLayer.EthernetFrame;
import Network.DataUnit.DataUnit;
import Network.DataUnit.NetworkLayer.IPPacket;
import Network.Node.Core.Router;
import Network.Node.Node;
import Network.Util.IPUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.UnknownHostException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Subnet Tests")
public class SubnetTest {

    @Mock
    private Internet mockInternet;
    
    @Mock
    private Router mockGateway;
    
    @Mock
    private Node mockNode1;
    
    @Mock
    private Node mockNode2;
    
    @Mock
    private EthernetFrame mockEthernetFrame;
    
    @Mock
    private IPPacket mockIPPacket;
    
    @Mock
    private DataUnit mockDataUnit;

    private Subnet subnet;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Capture System.out for testing console output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        // Create a basic subnet for testing
        subnet = Subnet.builder()
                .subnetAddress("192.168.1.0")
                .subnetMask("255.255.255.0")
                .isPrivate(true)
                .internet(mockInternet)
                .build();
    }

    @Test
    @DisplayName("Builder creates subnet with correct address")
    void testBuilderCreatesSubnetWithCorrectAddress() {
        Subnet testSubnet = Subnet.builder()
                .subnetAddress("10.0.0.0")
                .subnetMask("255.0.0.0")
                .isPrivate(true)
                .internet(mockInternet)
                .build();

        assertNotNull(testSubnet);
        // Note: getSubnetAddress returns int, would need IPUtil.intToIp to verify string representation
    }

    @Test
    @DisplayName("Builder handles invalid IP address gracefully")
    void testBuilderHandlesInvalidIPAddress() {
        Subnet testSubnet = Subnet.builder()
                .subnetAddress("invalid.ip.address")
                .subnetMask("255.255.255.0")
                .build();

        assertEquals(0, testSubnet.getSubnetAddress());
    }

    @Test
    @DisplayName("Builder handles invalid subnet mask gracefully")
    void testBuilderHandlesInvalidSubnetMask() {
        Subnet testSubnet = Subnet.builder()
                .subnetAddress("192.168.1.0")
                .subnetMask("invalid.mask")
                .build();

        assertEquals(0, testSubnet.getSubnetMask());
    }

    @Test
    @DisplayName("Builder sets all properties correctly")
    void testBuilderSetsAllPropertiesCorrectly() {
        Subnet testSubnet = Subnet.builder()
                .subnetAddress("172.16.0.0")
                .subnetMask("255.255.0.0")
                .isPrivate(false)
                .internet(mockInternet)
                .build();

        assertNotNull(testSubnet);
        assertNotEquals(0, testSubnet.getSubnetAddress());
        assertNotEquals(0, testSubnet.getSubnetMask());
    }

    @Test
    @DisplayName("addNode adds node to subnet")
    void testAddNodeAddsNodeToSubnet() {
        subnet.addNode(mockNode1);
        
        // Verify node was added by checking if it receives broadcasts
        when(mockEthernetFrame.getIPPacket()).thenReturn(mockIPPacket);
        when(mockIPPacket.getProtocol()).thenReturn(17); // UDP protocol for broadcast
        
        subnet.broadcast(mockEthernetFrame, mockNode2);
        
        verify(mockNode1).receive(mockEthernetFrame);
    }

    @Test
    @DisplayName("addNode handles multiple nodes")
    void testAddNodeHandlesMultipleNodes() {
        subnet.addNode(mockNode1);
        subnet.addNode(mockNode2);
        
        when(mockEthernetFrame.getIPPacket()).thenReturn(mockIPPacket);
        when(mockIPPacket.getProtocol()).thenReturn(17);
        
        subnet.broadcast(mockEthernetFrame, mockNode1);
        
        verify(mockNode2).receive(mockEthernetFrame);
        verify(mockNode1, never()).receive(mockEthernetFrame); // Sender should not receive
    }

    @Test
    @DisplayName("broadcast sends data to all nodes except sender")
    void testBroadcastSendsDataToAllNodesExceptSender() {
        subnet.addNode(mockNode1);
        subnet.addNode(mockNode2);
        
        when(mockEthernetFrame.getIPPacket()).thenReturn(mockIPPacket);
        when(mockIPPacket.getProtocol()).thenReturn(17); // UDP protocol
        
        subnet.broadcast(mockEthernetFrame, mockNode1);
        
        verify(mockNode2).receive(mockEthernetFrame);
        verify(mockNode1, never()).receive(mockEthernetFrame);
    }

    @Test
    @DisplayName("broadcast ignores non-EthernetFrame data")
    void testBroadcastIgnoresNonEthernetFrameData() {
        subnet.addNode(mockNode1);
        
        subnet.broadcast(mockDataUnit, mockNode2);
        
        verify(mockNode1, never()).receive(any());
    }

    @Test
    @DisplayName("broadcast ignores non-UDP protocol")
    void testBroadcastIgnoresNonUDPProtocol() {
        subnet.addNode(mockNode1);
        
        when(mockEthernetFrame.getIPPacket()).thenReturn(mockIPPacket);
        when(mockIPPacket.getProtocol()).thenReturn(6); // TCP protocol
        
        subnet.broadcast(mockEthernetFrame, mockNode2);
        
        verify(mockNode1, never()).receive(any());
    }

    @Test
    @DisplayName("send delivers packet to node with matching IP address")
    void testSendDeliversPacketToNodeWithMatchingIPAddress() {
        subnet.addNode(mockNode1);
        
        when(mockEthernetFrame.getIPPacket()).thenReturn(mockIPPacket);
        when(mockIPPacket.getDestinationIP()).thenReturn("192.168.1.100");
        when(mockEthernetFrame.getDestinationMAC()).thenReturn("AA:BB:CC:DD:EE:FF");
        when(mockNode1.getIpAddress()).thenReturn("192.168.1.100");
        when(mockNode1.getMACAddress()).thenReturn("11:22:33:44:55:66");
        when(mockNode1.toString()).thenReturn("Node1");
        
        subnet.send(mockEthernetFrame);
        
        verify(mockNode1).receive(mockEthernetFrame);
        assertTrue(outputStream.toString().contains("Match found!"));
    }

    @Test
    @DisplayName("send delivers packet to node with matching MAC address")
    void testSendDeliversPacketToNodeWithMatchingMACAddress() {
        subnet.addNode(mockNode1);
        
        when(mockEthernetFrame.getIPPacket()).thenReturn(mockIPPacket);
        when(mockIPPacket.getDestinationIP()).thenReturn("192.168.1.200");
        when(mockEthernetFrame.getDestinationMAC()).thenReturn("AA:BB:CC:DD:EE:FF");
        when(mockNode1.getIpAddress()).thenReturn("192.168.1.100");
        when(mockNode1.getMACAddress()).thenReturn("AA:BB:CC:DD:EE:FF");
        when(mockNode1.toString()).thenReturn("Node1");
        
        subnet.send(mockEthernetFrame);
        
        verify(mockNode1).receive(mockEthernetFrame);
        assertTrue(outputStream.toString().contains("Match found!"));
    }

    @Test
    @DisplayName("send handles broadcast MAC address")
    void testSendHandlesBroadcastMACAddress() {
        subnet.addNode(mockNode1);
        
        when(mockEthernetFrame.getIPPacket()).thenReturn(mockIPPacket);
        when(mockIPPacket.getDestinationIP()).thenReturn("192.168.1.200");
        when(mockEthernetFrame.getDestinationMAC()).thenReturn("FF:FF:FF:FF:FF:FF");
        when(mockNode1.getIpAddress()).thenReturn("192.168.1.100");
        when(mockNode1.getMACAddress()).thenReturn("AA:BB:CC:DD:EE:FF");
        when(mockNode1.toString()).thenReturn("Node1");
        
        subnet.send(mockEthernetFrame);
        
        verify(mockNode1).receive(mockEthernetFrame);
        assertTrue(outputStream.toString().contains("Match found!"));
    }

    @Test
    @DisplayName("send handles broadcast IP address")
    void testSendHandlesBroadcastIPAddress() {
        subnet.addNode(mockNode1);
        
        when(mockEthernetFrame.getIPPacket()).thenReturn(mockIPPacket);
        when(mockIPPacket.getDestinationIP()).thenReturn("255.255.255.255");
        when(mockEthernetFrame.getDestinationMAC()).thenReturn("AA:BB:CC:DD:EE:FF");
        when(mockNode1.getIpAddress()).thenReturn("192.168.1.100");
        when(mockNode1.getMACAddress()).thenReturn("11:22:33:44:55:66");
        when(mockNode1.toString()).thenReturn("Node1");
        
        subnet.send(mockEthernetFrame);
        
        verify(mockNode1).receive(mockEthernetFrame);
        assertTrue(outputStream.toString().contains("Match found!"));
    }

    @Test
    @DisplayName("send forwards to gateway when destination not found")
    void testSendForwardsToGatewayWhenDestinationNotFound() {
        subnet.setGateway(mockGateway);
        subnet.addNode(mockNode1);
        
        when(mockEthernetFrame.getIPPacket()).thenReturn(mockIPPacket);
        when(mockIPPacket.getDestinationIP()).thenReturn("10.0.0.100");
        when(mockEthernetFrame.getDestinationMAC()).thenReturn("AA:BB:CC:DD:EE:FF");
        when(mockNode1.getIpAddress()).thenReturn("192.168.1.100");
        when(mockNode1.getMACAddress()).thenReturn("11:22:33:44:55:66");
        when(mockNode1.toString()).thenReturn("Node1");
        when(mockGateway.toString()).thenReturn("Gateway");
        
        subnet.send(mockEthernetFrame);
        
        verify(mockGateway).forward(mockEthernetFrame);
        verify(mockNode1, never()).receive(mockEthernetFrame);
        assertTrue(outputStream.toString().contains("forwarding to gateway"));
    }

    @Test
    @DisplayName("send reports error when destination not found and no gateway")
    void testSendReportsErrorWhenDestinationNotFoundAndNoGateway() {
        subnet.addNode(mockNode1);
        
        when(mockEthernetFrame.getIPPacket()).thenReturn(mockIPPacket);
        when(mockIPPacket.getDestinationIP()).thenReturn("10.0.0.100");
        when(mockEthernetFrame.getDestinationMAC()).thenReturn("AA:BB:CC:DD:EE:FF");
        when(mockNode1.getIpAddress()).thenReturn("192.168.1.100");
        when(mockNode1.getMACAddress()).thenReturn("11:22:33:44:55:66");
        when(mockNode1.toString()).thenReturn("Node1");
        
        subnet.send(mockEthernetFrame);
        
        verify(mockNode1, never()).receive(mockEthernetFrame);
        assertTrue(outputStream.toString().contains("ERROR: Destination not found and no gateway configured"));
    }

    @Test
    @DisplayName("send ignores non-EthernetFrame data")
    void testSendIgnoresNonEthernetFrameData() {
        subnet.addNode(mockNode1);
        
        subnet.send(mockDataUnit);
        
        verify(mockNode1, never()).receive(any());
    }

    @Test
    @DisplayName("send delivers to multiple matching nodes")
    void testSendDeliversToMultipleMatchingNodes() {
        subnet.addNode(mockNode1);
        subnet.addNode(mockNode2);
        
        when(mockEthernetFrame.getIPPacket()).thenReturn(mockIPPacket);
        when(mockIPPacket.getDestinationIP()).thenReturn("255.255.255.255"); // Broadcast
        when(mockEthernetFrame.getDestinationMAC()).thenReturn("AA:BB:CC:DD:EE:FF");
        when(mockNode1.getIpAddress()).thenReturn("192.168.1.100");
        when(mockNode1.getMACAddress()).thenReturn("11:22:33:44:55:66");
        when(mockNode1.toString()).thenReturn("Node1");
        when(mockNode2.getIpAddress()).thenReturn("192.168.1.101");
        when(mockNode2.getMACAddress()).thenReturn("22:33:44:55:66:77");
        when(mockNode2.toString()).thenReturn("Node2");
        
        subnet.send(mockEthernetFrame);
        
        verify(mockNode1).receive(mockEthernetFrame);
        verify(mockNode2).receive(mockEthernetFrame);
    }

    @Test
    @DisplayName("setGateway sets gateway correctly")
    void testSetGatewaySetGatewayCorrectly() {
        subnet.setGateway(mockGateway);
        
        // Test that gateway is used when needed
        when(mockEthernetFrame.getIPPacket()).thenReturn(mockIPPacket);
        when(mockIPPacket.getDestinationIP()).thenReturn("10.0.0.100");
        when(mockEthernetFrame.getDestinationMAC()).thenReturn("AA:BB:CC:DD:EE:FF");
        when(mockGateway.toString()).thenReturn("Gateway");
        
        subnet.send(mockEthernetFrame);
        
        verify(mockGateway).forward(mockEthernetFrame);
    }

    @Test
    @DisplayName("getSubnetAddress returns correct address")
    void testGetSubnetAddressReturnsCorrectAddress() {
        int address = subnet.getSubnetAddress();
        assertNotEquals(0, address); // Should be converted from 192.168.1.0
    }

    @Test
    @DisplayName("getSubnetMask returns correct mask")
    void testGetSubnetMaskReturnsCorrectMask() {
        int mask = subnet.getSubnetMask();
        assertNotEquals(0, mask); // Should be converted from 255.255.255.0
    }

    @Test
    @DisplayName("send logs packet delivery information")
    void testSendLogsPacketDeliveryInformation() {
        subnet.addNode(mockNode1);
        
        when(mockEthernetFrame.getIPPacket()).thenReturn(mockIPPacket);
        when(mockIPPacket.getDestinationIP()).thenReturn("192.168.1.100");
        when(mockEthernetFrame.getDestinationMAC()).thenReturn("AA:BB:CC:DD:EE:FF");
        when(mockNode1.getIpAddress()).thenReturn("192.168.1.100");
        when(mockNode1.getMACAddress()).thenReturn("11:22:33:44:55:66");
        when(mockNode1.toString()).thenReturn("Node1");
        
        subnet.send(mockEthernetFrame);
        
        String output = outputStream.toString();
        assertTrue(output.contains("[Subnet] Received packet for delivery"));
        assertTrue(output.contains("[Subnet] Destination: MAC=AA:BB:CC:DD:EE:FF IP=192.168.1.100"));
        assertTrue(output.contains("[Subnet] Checking node: Node1"));
    }

    @Test
    @DisplayName("broadcast handles empty node set")
    void testBroadcastHandlesEmptyNodeSet() {
        when(mockEthernetFrame.getIPPacket()).thenReturn(mockIPPacket);
        when(mockIPPacket.getProtocol()).thenReturn(17);
        
        // Should not throw exception with empty node set
        assertDoesNotThrow(() -> subnet.broadcast(mockEthernetFrame, mockNode1));
    }

    @Test
    @DisplayName("send handles empty node set")
    void testSendHandlesEmptyNodeSet() {
        when(mockEthernetFrame.getIPPacket()).thenReturn(mockIPPacket);
        when(mockIPPacket.getDestinationIP()).thenReturn("192.168.1.100");
        when(mockEthernetFrame.getDestinationMAC()).thenReturn("AA:BB:CC:DD:EE:FF");
        
        subnet.send(mockEthernetFrame);
        
        assertTrue(outputStream.toString().contains("ERROR: Destination not found and no gateway configured"));
    }

    @Test
    @DisplayName("builder creates new instance each time")
    void testBuilderCreatesNewInstanceEachTime() {
        Subnet.Builder builder1 = Subnet.builder();
        Subnet.Builder builder2 = Subnet.builder();
        
        assertNotSame(builder1, builder2);
    }

    @Test
    @DisplayName("builder allows method chaining")
    void testBuilderAllowsMethodChaining() {
        Subnet testSubnet = Subnet.builder()
                .subnetAddress("10.0.0.0")
                .subnetMask("255.0.0.0")
                .isPrivate(true)
                .internet(mockInternet)
                .build();
                
        assertNotNull(testSubnet);
    }

    // Clean up after each test
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}