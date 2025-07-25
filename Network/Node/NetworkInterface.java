package Network.Node;

import Network.DataUnit.DataLinkLayer.EthernetFrame;
import Network.DataUnit.DataUnit;
import Network.Network.Subnet;

public class NetworkInterface {
	private Subnet subnet;

	public NetworkInterface(Subnet subnet) {
		this.subnet = subnet;
	}

	public void transmit(DataUnit dataUnit, Node sender) {
		EthernetFrame ethernetFrame = (EthernetFrame) dataUnit;
		if (ethernetFrame.isBroadcast()) {
			subnet.broadcast(ethernetFrame, sender);
		} else {
			subnet.send(dataUnit);
		}
	}

	public Subnet getSubnet() {
		return subnet;
	}
}
