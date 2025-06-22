package Network.Node;

import Network.DataUnit.TransportLayer.UDPDatagram;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class UDPManager {
	private final Supplier<Integer> portAllocator;
	private final Consumer<UDPDatagram> sender;

	public UDPManager(Supplier<Integer> portAllocator, Consumer<UDPDatagram> sender) {
		this.portAllocator = portAllocator;
		this.sender = sender;
	}

	public void onDatagramReceived() {

	}
}
