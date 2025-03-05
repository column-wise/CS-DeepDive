package NetworkSimulation.DataUnit.NetworkLayer;

import NetworkSimulation.DataUnit.DataUnit;
import NetworkSimulation.DataUnit.TransportLayer.TransportDataUnit;

public class IPPacket implements DataUnit {
    private final String sourceIP;
    private final String destinationIP;
    private final int protocol; // 17=UDP, 6=TCP
    private final TransportDataUnit transportDataUnit; // UDPDatagram 또는 TCPSegment 저장 가능

    public IPPacket(String sourceIP, String destinationIP, int protocol, TransportDataUnit transportSegment) {
        this.sourceIP = sourceIP;
        this.destinationIP = destinationIP;
        this.protocol = protocol;
        this.transportDataUnit = transportSegment;
    }

    @Override
    public String toString() {
        return "IPPacket [SIP=" + sourceIP + ", DIP=" + destinationIP + ", Protocol=" + protocol + ", TransportSegment=" + transportDataUnit + "]";
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public String getDestinationIP() {
        return destinationIP;
    }

    public int getProtocol() {
        return protocol;
    }

    public TransportDataUnit getTransportDataUnit() {
        return transportDataUnit;
    }
}
