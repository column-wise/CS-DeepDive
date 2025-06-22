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

	TCPConnection(int localPort, String remoteIP, int remotePort, Consumer<TCPSegment> sender) {
		this.localPort = localPort;
		this.remoteIP = remoteIP;
		this.remotePort = remotePort;
		this.sender = sender;
	}

	void sendSyn() {
		state = State.SYN_SENT;
		TCPSegment syn = TCPSegment.syn(localPort, remotePort, seqNum++);
		sender.accept(syn);
	}

	void sendFin() {
		state = State.FIN_WAIT_1;
		TCPSegment fin = TCPSegment.fin(localPort, remotePort, seqNum, ackNum);
		sender.accept(fin);
	}

	void onReceive(TCPSegment seg) {
		switch (state) {
			case SYN_SENT:
				if(seg.getHeader().isSynAck()) {
					TCPSegment ack = TCPSegment.ack(localPort, remotePort, seqNum, ackNum);
					state = State.SYN_RECEIVED;
				}
		}
	}
}
