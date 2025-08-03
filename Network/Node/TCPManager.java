package Network.Node;

import Network.DataUnit.TransportLayer.TCPSegment;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class TCPManager {
	private final Map<String, TCPConnection> sessions = new HashMap<>();
	private final Supplier<Integer> localPortAllocator;
	private final BiConsumer<String, TCPSegment> lowerSender;

	public TCPManager(Supplier<Integer> localPortAllocator,
					  BiConsumer<String, TCPSegment> lowerSender) {
		this.localPortAllocator = localPortAllocator;
		this.lowerSender    = lowerSender;
	}

	public void sendSegment(String sourceIP,
							String destIP,
							int destPort,
							String payload) {
		int sourcePort = localPortAllocator.get();
		String sessionKey = key(sourcePort, destIP, destPort);
		TCPConnection connection = sessions.get(sessionKey);

		if (connection == null || connection.isClosed()) {
			System.out.println("connection is not established...");
			// 새 커넥션 생성 및 SYN 발송
			connection = new TCPConnection(
					sourcePort, destIP, destPort,
					tcpSeg -> lowerSender.accept(destIP, tcpSeg)
			);
			sessions.put(sessionKey, connection);
			System.out.println("sending syn request...");
			connection.sendSyn();
		}
		else if (connection.getState() == TCPConnection.State.ESTABLISHED) {
			connection.sendData(payload);
		}
	}

	public void onSegmentReceived(String localIP, String remoteIP, TCPSegment segment) {
		int localPort  = segment.getHeader().getDestinationPort();
		int remotePort = segment.getHeader().getSourcePort();
		String sessionKey = key(localPort, remoteIP, remotePort);

		TCPConnection connection = sessions.get(sessionKey);
		if (connection == null) {
			connection = new TCPConnection(
					localPort, remoteIP, remotePort,
					tcpSeg -> lowerSender.accept(remoteIP, tcpSeg)
			);
			sessions.put(sessionKey, connection);
		}
		connection.onReceive(segment);
	}

	private String key(int localPort, String destIP, int destPort) {
		return localPort + "->" + destIP + ":" + destPort;
	}
}
