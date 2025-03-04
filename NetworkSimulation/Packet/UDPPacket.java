package NetworkSimulation.Packet;

public class UDPPacket {
    UDPHeader udpHeader;
    String payload;

    public UDPPacket(UDPHeader udpHeader, String payload) {
        this.udpHeader = udpHeader;
        this.payload = payload;
    }

    public String toString() {
        return payload;
    }

    public static class UDPHeader {
        int sourcePort;
        int destinationPort;
        int length;
        int checksum;

        public UDPHeader(int sourcePort, int destinationPort, int length, int checksum) {
            this.sourcePort = sourcePort;
            this.destinationPort = destinationPort;
            this.length = length;
            this.checksum = checksum;
        }
    }
}
