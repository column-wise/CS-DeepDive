package Network.Node;

import Network.DataUnit.TransportLayer.UDPDatagram;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class UDPManager {
	private final Supplier<Integer> portAllocator;
	private final BiConsumer<String, UDPDatagram> sender;

	public UDPManager(Supplier<Integer> portAllocator, BiConsumer<String, UDPDatagram> sender) {
		this.portAllocator = portAllocator;
		this.sender = sender;
	}

	public void onDatagramReceived() {

	}
}
