package Network.Node;

import Network.DataUnit.NetworkLayer.IPPacket;
import Network.DataUnit.TransportLayer.TCPSegment;
import Network.DataUnit.TransportLayer.TransportDataUnit;
import Network.DataUnit.TransportLayer.UDPDatagram;

import java.util.function.Consumer;

public class IPManager {
	private String ipAddress;

	// 1) 링크 계층(Computer.sendIPPacket)으로 패킷을 넘겨줄 콜백
	private final Consumer<IPPacket> sender;
	// 2) 기본 TTL (Time To Live)
	private final int defaultTTL;

	/**
	 * 기본 생성자: TTL은 64로 설정
	 *
	 * @param sender Computer.sendIPPacket() 메서드를 레퍼런스로 넘겨준다
	 */
	public IPManager(Consumer<IPPacket> sender) {
		this(sender, 64);
	}

	/**
	 * TTL을 직접 지정할 수 있는 생성자
	 *
	 * @param sender   Computer.sendIPPacket() 메서드
	 * @param defaultTTL 초기 TTL 값
	 */
	public IPManager(Consumer<IPPacket> sender, int defaultTTL) {
		this.sender = sender;
		this.defaultTTL = defaultTTL;
	}

	public void sendIPPacket(String destinationIP, TransportDataUnit transportDataUnit) {
		IPPacket ipPacket = null;

		if(transportDataUnit instanceof TCPSegment) {
			TCPSegment segment = (TCPSegment) transportDataUnit;
			ipPacket = new IPPacket(getIPAddress(), destinationIP, 6, segment);
		} else if(transportDataUnit instanceof UDPDatagram) {
			UDPDatagram datagram = (UDPDatagram) transportDataUnit;
			ipPacket = new IPPacket(getIPAddress(), destinationIP, 17, datagram);
		} else {
			System.out.println("not supported transport layer packet type");
			return;
		}
		sender.accept(ipPacket);
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getIPAddress() {
		return ipAddress;
	}
}