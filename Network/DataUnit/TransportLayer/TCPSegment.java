package Network.DataUnit.TransportLayer;

public class TCPSegment implements TransportDataUnit {
	private final TCPHeader tcpHeader;
	private final String payload;

	public TCPSegment(TCPHeader tcpHeader, String payload) {
		this.tcpHeader = tcpHeader;
		this.payload = payload;
	}

	public static class TCPHeader {
		int sourcePort;
		int destinationPort;
		int sequenceNumber;
		int acknowledgementNumber;
		int dataOffset;
		int reserved = 000;
		String flags;
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
				String flags,
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

		public static Builder builder() {
			return new Builder();
		}

		public static class Builder {
			private int sourcePort;
			private int destinationPort;
			private int sequenceNumber;
			private int acknowledgementNumber;
			private int dataOffset;
			private String flags;
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

			public Builder flags(String flags) {
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
}
