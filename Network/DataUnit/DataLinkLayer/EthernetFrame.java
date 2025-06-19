package Network.DataUnit.DataLinkLayer;

import Network.DataUnit.DataUnit;
import Network.DataUnit.NetworkLayer.IPPacket;

public class EthernetFrame implements DataUnit {
    private final String destinationMAC;
    private final String sourceMAC;
    private final int etherType; // IP 패킷인 경우 0x0800
    private final IPPacket ipPacket;

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

    public String getDestinationMAC() {
        return destinationMAC;
    }

    public String getSourceMAC() {
        return sourceMAC;
    }

    public int getEtherType() {
        return etherType;
    }

    public IPPacket getIPPacket() {
        return ipPacket;
    }
}
