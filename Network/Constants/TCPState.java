package Network.Constants;
// Comprehensive unit tests available in TCPStateTest.java
// Tests cover: enum properties, valueOf validation, state transitions, serialization, and more

import Network.DataUnit.DataUnit;

public enum TCPState {
	CLOSED, SYN_SENT, SYN_RECEIVED, ESTABLISHED, FIN_WAIT_1, FIN_WAIT_2, TIME_WAIT;
}
