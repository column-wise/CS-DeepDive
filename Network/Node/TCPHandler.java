package Network.Node;

import Network.DataUnit.DataUnit;

public interface TCPHandler {
	void establishTCP(String destIP, int destPort);
	void sendTCP(String destIP, int destPort, DataUnit data);
	void closeTCP(String destIP, int destPort);
	void handleTCP(DataUnit data);
}
