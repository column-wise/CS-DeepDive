package Network.DataUnit.TransportLayer;

public class UDPDatagram implements TransportDataUnit {
    private final UDPHeader udpHeader;
    private final String payload;

    public UDPDatagram(UDPHeader udpHeader, String payload) {
        this.udpHeader = udpHeader;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "UDPDatagram {\n" +
                "\tSource Port: " + udpHeader.sourcePort + "\n" +
                "\tDestination Port: " + udpHeader.destinationPort + "\n" +
                "\tLength: " + udpHeader.length + "\n" +
                "\tChecksum: " + udpHeader.checksum + "\n" +
                "\tPayload: " + payload.replace(",", ",\n\t\t") +
                "\n}";
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
