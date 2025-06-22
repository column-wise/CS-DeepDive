package Network.Node;

import Network.DataUnit.DataUnit;

public interface UDPHandler {
	void sendUDP(String destIP, int destPort, DataUnit data);
	void handleUDP(DataUnit data);
}
