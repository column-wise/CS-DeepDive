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
        return "EthernetFrame {\n" +
                "\tDestination MAC: " + destinationMAC + "\n" +
                "\tSource MAC: " + sourceMAC + "\n" +
                "\tEtherType: " + etherType + "\n" +
                "\t" + ipPacket.toString().replace("\n", "\n\t") + // IPPacket 내부도 들여쓰기
                "\n}";
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

    public boolean isBroadcast() {
        String destinationMAC = getDestinationMAC();
        IPPacket ipPacket = getIPPacket();
        String destinationIP = ipPacket.getDestinationIP();

        return destinationMAC.equals("FF:FF:FF:FF:FF:FF") && destinationIP.equals("255.255.255.255");
    }
}
