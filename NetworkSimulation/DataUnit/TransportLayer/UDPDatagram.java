package NetworkSimulation.DataUnit.TransportLayer;

public class UDPDatagram implements TransportDataUnit {
    private final UDPHeader udpHeader;
    private final String payload;

    public UDPDatagram(UDPHeader udpHeader, String payload) {
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

    public UDPHeader getUDPHeader() {
        return udpHeader;
    }

    public String getPayload() {
        return payload;
    }
}
