package Network.Node;

import Network.DataUnit.DataUnit;
import Network.Network.Network;

public abstract class Node {
    protected String ipAddress;
    protected String MACAddress;
    protected Network network;

    public void receive(DataUnit data) {
        System.out.println(getClass().getName() + " " + ipAddress + " received " + data.toString());
    }
}
