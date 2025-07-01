package Network.Node;

import Network.DataUnit.DataUnit;
import Network.DataUnit.NetworkLayer.IPPacket;
import Network.DataUnit.TransportLayer.TCPSegment;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TCPManager {
	private final Map<String, TCPConnection> sessions = new HashMap<>();
	private final Supplier<Integer> localPortAllocator;
	private final Consumer<DataUnit> lowerSender;

	public TCPManager(Supplier<Integer> localPortAllocator,
					  Consumer<DataUnit> lowerSender) {
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
			// 새 커넥션 생성 및 SYN 발송
			connection = new TCPConnection(
					sourcePort, destIP, destPort,
					tcpSeg -> wrapAndSend(sourceIP, destIP, tcpSeg)
			);
			sessions.put(sessionKey, connection);
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
					tcpSeg -> wrapAndSend(localIP, remoteIP, tcpSeg)
			);
			sessions.put(sessionKey, connection);
		}
		connection.onReceive(segment);
	}

	private String key(int localPort, String destIP, int destPort) {
		return localPort + "->" + destIP + ":" + destPort;
	}

	private void wrapAndSend(String localIP, String remoteIP, TCPSegment seg) {
		// 1) IP 계층
		IPPacket ip = new IPPacket(localIP, remoteIP, 6, seg);
		lowerSender.accept(ip);
	}
}
