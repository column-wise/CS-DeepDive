package Network.DataUnit.TransportLayer;

public class TCPSegment implements TransportDataUnit {
	private final TCPHeader tcpHeader;
	private final String payload;

	public TCPSegment(TCPHeader tcpHeader, String payload) {
		this.tcpHeader = tcpHeader;
		this.payload = payload;
	}

	// SYN 플래그만 설정된 TCP 세그먼트를 만든다
	public static TCPSegment syn(int sourcePort, int destPort, int sequenceNumber) {
		TCPHeader hdr = TCPHeader.builder()
				.sourcePort(sourcePort)
				.destinationPort(destPort)
				.sequenceNumber(sequenceNumber)
				.acknowledgementNumber(0)
				.dataOffset(5)       // 헤더 길이 (5 워드 = 20 bytes)
				.flags(TCPHeader.FLAG_SYN)        // 제어 비트
				.windowSize(65535)   // 예시 윈도우 크기
				.checksum("0000")
				.urgentPointer("0")
				.options("")
				.build();
		return new TCPSegment(hdr, "");
	}

	// FIN 플래그만 설정된 TCP 세그먼트를 만든다
	public static TCPSegment fin(int sourcePort, int destPort, int sequenceNumber, int acknowledgementNumber) {
		TCPHeader hdr = TCPHeader.builder()
				.sourcePort(sourcePort)
				.destinationPort(destPort)
				.sequenceNumber(sequenceNumber)
				.acknowledgementNumber(acknowledgementNumber)
				.dataOffset(5)
				.flags(TCPHeader.FLAG_FIN)
				.windowSize(65535)
				.checksum("0000")
				.urgentPointer("0")
				.options("")
				.build();
		return new TCPSegment(hdr, "");
	}

	// ACK 플래그만 설정된 TCP 세그먼트를 만든다
	public static TCPSegment ack(int sourcePort, int destPort, int sequenceNumber, int acknowledgementNumber) {
		TCPHeader hdr = TCPHeader.builder()
				.sourcePort(sourcePort)
				.destinationPort(destPort)
				.sequenceNumber(sequenceNumber)
				.acknowledgementNumber(acknowledgementNumber)
				.dataOffset(5)
				.flags(TCPHeader.FLAG_ACK)
				.windowSize(65535)
				.checksum("0000")
				.urgentPointer("0")
				.options("")
				.build();
		return new TCPSegment(hdr, "");
	}

	// ACK 플래그만 설정된 TCP 세그먼트를 만든다
	public static TCPSegment synAck(int sourcePort, int destPort, int sequenceNumber, int acknowledgementNumber) {
		TCPHeader hdr = TCPHeader.builder()
				.sourcePort(sourcePort)
				.destinationPort(destPort)
				.sequenceNumber(sequenceNumber)
				.acknowledgementNumber(acknowledgementNumber)
				.dataOffset(5)
				.flags(TCPHeader.FLAG_SYN + TCPHeader.FLAG_ACK)
				.windowSize(65535)
				.checksum("0000")
				.urgentPointer("0")
				.options("")
				.build();
		return new TCPSegment(hdr, "");
	}

	public String getPayload() {
		return payload;
	}

	public static class TCPHeader {
		public static final int FLAG_FIN = 1 << 0;  // 000001
		public static final int FLAG_SYN = 1 << 1;  // 000010
		public static final int FLAG_RST = 1 << 2;  // 000100
		public static final int FLAG_PSH = 1 << 3;  // 001000
		public static final int FLAG_ACK = 1 << 4;  // 010000
		public static final int FLAG_URG = 1 << 5;  // 100000

		int sourcePort;
		int destinationPort;
		int sequenceNumber;
		int acknowledgementNumber;
		int dataOffset;
		int reserved = 000;
		int flags;
		int windowSize;
		String checksum;
		String urgentPointer;
		String options;

		private TCPHeader(
				int sourcePort,
				int destinationPort,
				int sequenceNumber,
				int acknowledgementNumber,
				int dataOffset,
				int flags,
				int windowSize,
				String checksum,
				String urgentPointer,
				String options
				) {
			this.sourcePort = sourcePort;
			this.destinationPort = destinationPort;
			this.sequenceNumber = sequenceNumber;
			this.acknowledgementNumber = acknowledgementNumber;
			this.dataOffset = dataOffset;
			this.flags = flags;
			this.windowSize = windowSize;
			this.checksum = checksum;
			this.urgentPointer = urgentPointer;
			this.options = options;
		}

		public boolean isSynAck() {
			return isSyn() && isAck();
		}

		public boolean isSyn() {
			return (flags & FLAG_SYN) != 0;
		}

		public boolean isAck() {
			return (flags & FLAG_ACK) != 0;
		}

		public int getSourcePort() {
			return sourcePort;
		}

		public int getDestinationPort() {
			return destinationPort;
		}

		public int getSequenceNumber() {
			return sequenceNumber;
		}

		public static Builder builder() {
			return new Builder();
		}

		public static class Builder {
			private int sourcePort;
			private int destinationPort;
			private int sequenceNumber;
			private int acknowledgementNumber;
			private int dataOffset;
			private int flags;
			private int windowSize;
			private String checksum;
			private String urgentPointer;
			private String options;

			public Builder sourcePort(int sourcePort) {
				this.sourcePort = sourcePort;
				return this;
			}

			public Builder destinationPort(int destinationPort) {
				this.destinationPort = destinationPort;
				return this;
			}

			public Builder sequenceNumber(int sequenceNumber) {
				this.sequenceNumber = sequenceNumber;
				return this;
			}

			public Builder acknowledgementNumber(int acknowledgementNumber) {
				this.acknowledgementNumber = acknowledgementNumber;
				return this;
			}

			public Builder dataOffset(int dataOffset) {
				this.dataOffset = dataOffset;
				return this;
			}

			public Builder flags(int flags) {
				this.flags = flags;
				return this;
			}

			public Builder windowSize(int windowSize) {
				this.windowSize = windowSize;
				return this;
			}

			public Builder checksum(String checksum) {
				this.checksum = checksum;
				return this;
			}

			public Builder urgentPointer(String urgentPointer) {
				this.urgentPointer = urgentPointer;
				return this;
			}

			public Builder options(String options) {
				this.options = options;
				return this;
			}

			public TCPHeader build() {
				return new TCPHeader(
						sourcePort,
						destinationPort,
						sequenceNumber,
						acknowledgementNumber,
						dataOffset,
						flags,
						windowSize,
						checksum,
						urgentPointer,
						options
				);
			}
		}
	}

	public TCPHeader getHeader() {
		return tcpHeader;
	}
}
