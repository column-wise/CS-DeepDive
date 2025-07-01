package Network.Node;

import Network.DataUnit.TransportLayer.TCPSegment;

import java.util.function.Consumer;

public class TCPConnection {
	enum State {CLOSED, SYN_SENT, SYN_RECEIVED, ESTABLISHED, FIN_WAIT_1, FIN_WAIT_2, TIME_WAIT};
	private State state = State.CLOSED;
	private final int localPort;
	private final String remoteIP;
	private final int remotePort;
	private int seqNum = 0;
	private int ackNum = 0;
	private Consumer<TCPSegment> sender;
	private TCPSegment pendingSegment = null;

	TCPConnection(int localPort, String remoteIP, int remotePort, Consumer<TCPSegment> sender) {
		this.localPort = localPort;
		this.remoteIP = remoteIP;
		this.remotePort = remotePort;
		this.sender = sender;
		seqNum = (int)(System.currentTimeMillis() % 1000000000);
	}

	void sendSyn() {
		TCPSegment syn = TCPSegment.syn(localPort, remotePort, seqNum);
		sender.accept(syn);
	}

	void sendAck() {
		TCPSegment ack = TCPSegment.ack(localPort, remotePort, seqNum, ackNum);
		sender.accept(ack);
	}

	void sendSynAck() {
		TCPSegment synAck = TCPSegment.synAck(localPort, remotePort, seqNum, ackNum);
		sender.accept(synAck);
	}

	void sendFin() {
		TCPSegment fin = TCPSegment.fin(localPort, remotePort, seqNum, ackNum);
		sender.accept(fin);
	}

	public void sendData(String payload) {
		TCPSegment.TCPHeader tcpHeader = TCPSegment.TCPHeader.builder()
				.sourcePort(localPort)
				.destinationPort(remotePort)
				.sequenceNumber(seqNum)
				.acknowledgementNumber(ackNum)
				.dataOffset(5)
				.flags(TCPSegment.TCPHeader.FLAG_ACK)
				.windowSize(65535)
				.checksum("0000")
				.urgentPointer("0")
				.options("")
				.build();
		TCPSegment segment = new TCPSegment(tcpHeader, payload);
		if (state == State.ESTABLISHED) {
			sender.accept(segment);
		} else {
			pendingSegment = segment; // ESTABLISHED 전이면 대기
		}
	}

	public void onReceive(TCPSegment seg) {
		TCPSegment.TCPHeader header = seg.getHeader();
		switch (state) {
			case SYN_SENT:
				if (header.isSynAck()) {
					ackNum = header.getSequenceNumber() + 1;
					sendAck();
					state = State.ESTABLISHED;
					if (pendingSegment != null) {
						sender.accept(pendingSegment);
						pendingSegment = null;
					}
				}
				break;
			case CLOSED:
				if (header.isSyn()) {
					ackNum = header.getSequenceNumber() + 1;
					sendSynAck();
					state = State.SYN_RECEIVED;
				}
				break;
			case SYN_RECEIVED:
				if (header.isAck()) {
					state = State.ESTABLISHED;
					if (pendingSegment != null) {
						sender.accept(pendingSegment);  // 연결되자마자 전송
						pendingSegment = null;
					}
				}
				break;
			case ESTABLISHED:
				System.out.println(seg.toString());
				break;
		}
	}

	boolean isClosed() {
		return state == State.CLOSED;
	}

	public State getState() {
		return state;
	}
}
