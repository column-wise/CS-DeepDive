package NetworkSimulation.DataUnit.DataLinkLayer;

import NetworkSimulation.DataUnit.DataUnit;
import NetworkSimulation.DataUnit.NetworkLayer.IPPacket;

public class EthernetFrame implements DataUnit {
    String destinationMAC;
    String sourceMAC;
    int etherType; // IP 패킷인 경우 0x0800
    IPPacket ipPacket;

    public EthernetFrame(String destinationMAC, String sourceMAC, int etherType, IPPacket ipPacket) {
        this.destinationMAC = destinationMAC;
        this.sourceMAC = sourceMAC;
        this.etherType = etherType;
        this.ipPacket = ipPacket;
    }

    @Override
    public String toString() {
        return "EthernetFrame [DA=" + destinationMAC + ", SA=" + sourceMAC + ", EtherType=" + etherType + ", IPPacket=" + ipPacket + "]";
    }
}
