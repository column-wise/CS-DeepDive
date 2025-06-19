package Network.Node;

import Network.DataUnit.DataUnit;
import Network.Network.Network;

public abstract class Node {
    protected String ipAddress;
    protected String MACAddress;
    protected Network network;

    // todo. EthernetFrame, IPPacket, TransportDataUnit 모두 처리할 수 있도록
    // todo. protocol 확인해서 UDP / TCP로 분기하는 로직 추가
    public void receive(DataUnit data) {
        System.out.println(getClass().getName() + " " + ipAddress + " received\n" + data.toString()+"\n");
    }
}
