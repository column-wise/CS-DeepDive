package NetworkSimulation.DataUnit.NetworkLayer;

import NetworkSimulation.DataUnit.DataUnit;
import NetworkSimulation.DataUnit.TransportLayer.TransportDataUnit;

public class IPPacket implements DataUnit {
    String sourceIP;
    String destinationIP;
    int protocol; // 17=UDP, 6=TCP
    TransportDataUnit transportDataUnit; // UDPDatagram 또는 TCPSegment 저장 가능

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
}
