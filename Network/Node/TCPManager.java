package Network.Node;

import Network.DataUnit.TransportLayer.TCPSegment;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TCPManager {
	private final Map<String, TCPConnection> sessions = new HashMap<>();
	private final Supplier<Integer> localPortAllocator;
	private final Consumer<TCPSegment> sender;

	public TCPManager(Supplier<Integer> localPortAllocator, Consumer<TCPSegment> sender) {
		this.localPortAllocator = localPortAllocator;
		this.sender = sender;
	}

	public void establish(String destIP, int destPort) {

	}

	public void close(String destIP, int destPort) {

	}

	public void onSegmentReceived(TCPSegment segment) {

	}

	private String key(int localPort, String destIP, int destPort) {
		return localPort + "->" + destIP + ":" + destPort;
	}
}
