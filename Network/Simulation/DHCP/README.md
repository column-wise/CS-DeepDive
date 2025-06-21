# DHCP란

DHCP(Dynamic Host Configuration Protocol)의 약자로

네트워크에 연결된 장치들에게 TCP/IP의 기본 설정을 자동으로 제공해주는 프로토콜

서버-클라이언트의 형태로 동작함

서버: DHCP 서버

클라이언트: 네트워크에 새로 접속하는 장치

# DHCP의 목적

1. 수동으로 IP 주소를 설정할 필요 없이 클라이언트가 자동으로 네트워크 설정을 받도록 함
2. IP 주소의 효율적인 관리
    - 유휴 IP를 재사용하여 주소 낭비를 줄이고, 주소 공간을 효과적으로 할당
3. 대규모 네트워크 환경에서의 중앙 집중식 관리
    - IP 주소, 서브넷 마스크, 게이트웨이, DNS 서버 등 다양한 설정 값을 서버에서 일괄적으로 관리 가능
4. 네트워크 구성 오류 감소
    - 수동 설정 시 발생할 수 있는 오타, 중복 IP 설정 등의 오류를 줄임.

# DHCP의 장점

1. 신뢰성 높은 IP 주소 구성
    - IP 자동 할당으로 기기들의 IP가 충돌하는 것을 방지
2. 중앙 집중 관리
    - 서버에서 전체 네트워크 주소 할당 정책을 쉽게 제어 가능
3. 모바일/무선 환경에 적합
    - 자주 연결이 끊기고 위치가 바뀌는 장치에 유리

# DHCP의 단점

1. 서버 의존성
    - DHCP 서버가 다운되면 클라이언트는 IP 할당을 받지 못해 네트워크 연결 불가
2. 보안 취약점
    - DHCP에는 인증 메커니즘이 없어 악의적인 DHCP 서버가 IP를 임의로 할당하는 스푸핑 공격 위험이 있음

      (DHCP 스푸핑: DHCP Discover가 브로드 캐스팅으로 전달되므로 악의적인 서버가 먼저 DHCP offer를 보내면 클라이언트가 그것을 받아들일ㄴ 수도 있음)

3. 예측 불가능한 주소 할당
    - 고정 IP가 필요한 장치(서버, 프린터 등)에 DHCP를 사용하면 IP가 바뀔 수 있음(DHCP Reservation: 특정 MAC 주소에 고정 IP를 주는 방식으로 해결 가능)
4. 브로드캐스트 의존
    - DHCP Discover/Offer 등 초기 통신이 브로드캐스트로 이뤄지기 때문에 라우터나 중계기에 따라 제한될 수 있음

      (라우터는 기본적으로 브로드캐스트 트래픽을 다른 서브넷으로 전달하지 않기 때문에 DHCP 서버가 다른 서브넷에 있다면 클라이언트가 서버를 찾지 못할 수 있음)ㄴ


# DHCP 동작과정

DORA 라고도 불림

![Image](https://github.com/user-attachments/assets/2a136d57-b1d7-4428-9e47-238cad9d915e)

## 1.  DHCP Discover

클라이언트가 네트워크에 접속한 직후에는 할당 받은 IP가 없기 때문에 DHCP 서버로부터 관련 정보를 받아야 함.

destMAC: FF:FF:FF:FF:FF:FF, destIP: 255.255.255.255, sourceIP: 0.0.0.0 으로 브로드 캐스팅하여 DHCP 응답을 기다림

```java
// client
public void discoverDHCPServer() {

	// 일반적으로 DHCP 클라이언트는 포트 68, 서버는 포트 67을 사용
	int sourcePort = 68;              // 클라이언트 포트
	int destinationPort = 67;         // DHCP 서버 포트
	// 간단한 시뮬레이션에서는 checksum 계산을 생략하고 0으로 설정
	int checksum = 0;

	String payload = "DHCP Message Type=" + DHCPMessageType.DHCPDISCOVER +
			",Client MAC=" + MACAddress;

	int length = 8 + payload.length();

	UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(sourcePort, destinationPort, length, checksum);
	UDPDatagram udpDatagram = new UDPDatagram(header, payload);

	// protocol 17: UDP, 6: TCP
	IPPacket ipPacket = new IPPacket("0.0.0.0", "255.255.255.255", 17, udpDatagram);
	EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", MACAddress, 0x0800, ipPacket);

	network.broadcast(frame, this);
}
```

## 2. DHCP Offer

클라이언트로부터 전송된 브로드캐스트를 감지하여(패킷에 담긴 sourceIP가 0.0.0.0, DHCP Message Type이 DHCPDISCOVER) 사용 가능한 IP 중 하나를 선택,

브로드캐스트로 전송함.(클라이언트는 아직 IP가 없으므로)

응답하는 패킷에는 DHCP Server Identifier가 포함되어 있는데, 클라이언드의 DHCP Discover에 대한 응답이 여러 DHCP 서버로부터 올 수 있기 때문에

클라이언트가 DHCP request를 보낼 서버를 Identifier로 선택하도록 하기 위함이다.

```java
// DHCP Server
private void offerDHCP(Map<String, String> receivedPayload) {
	String clientMAC = receivedPayload.get("Client MAC");
	String payload = "Client MAC=" + clientMAC +
			",Your IP=" + IPUtil.intToIp(findAllocatableIP()) +
			",Subnet Mask=" + IPUtil.intToIp(network.getSubnetMask()) +
			",Router=" + IPUtil.intToIp(network.getSubnetAddress()+1) +
			",DNS=" + "DNS 서버 IP 주소" +
			",IP Lease Time=" + LEASE_TIME +
			",DHCP Message Type=" + DHCPMessageType.DHCPOFFER +
			",DHCP Server Identifier=" + ipAddress;

	// DHCP의 UDP 포트: 서버(67), 클라이언트(68)
	int sourcePort = 67;
	int destinationPort = 68;
	int length = payload.length() + 8;  // UDP 헤더 기본 길이 8바이트를 포함
	int checksum = 0; // 시뮬레이션에서는 체크섬 계산 생략

	UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(sourcePort, destinationPort, length, checksum);
	UDPDatagram datagram = new UDPDatagram(header, payload);
	IPPacket ipPacket = new IPPacket(ipAddress, "255.255.255.255", 17, datagram);
	EthernetFrame frame = new EthernetFrame(clientMAC, MACAddress, 0x0800, ipPacket);
	network.broadcast(frame, this);
}
```

## 3. DHCP Request

클라이언트는 offer 받은 IP 주소를 사용하겠다는 요청을 다시 브로드캐스트로 보냄

```java
// client
private void requestDHCP(String myIP) {
	int sourcePort = 68;
	int destinationPort = 67;
	int checksum = 0;

	// DHCP 서버로부터 offer 받은 IP가 유효한지 확인하기 위해 ARP Probe 과정을 거치기도 함
	// 해당 IP로 ARP 요청을 보내보고, 응답이 없으면 사용 가능하다고 판단

	String payload = "DHCP Message Type=DHCPREQUEST" +
			",Client MAC=" + MACAddress +
			",Requested IP Address=" + myIP +
			",Flags=1";
	// Flags=0 -> DHCP ACK 응답을 유니캐스트로 보내주세요
	// Flags=1 -> 브로드캐스트로 보내주세요

	int length = 8 + payload.length();

	UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(sourcePort, destinationPort, length, checksum);
	UDPDatagram udpDatagram = new UDPDatagram(header, payload);

	IPPacket ipPacket = new IPPacket("0.0.0.0", "255.255.255.255", 17, udpDatagram);
	EthernetFrame frame = new EthernetFrame("FF:FF:FF:FF:FF:FF", MACAddress, 0x0800, ipPacket);

	network.broadcast(frame, this);
}
```

## 4. DHCP Acknowledgement

클라이언트가 request한 IP 주소에 대한 임대 시간을 포함하여 클라이언트에 전달(유니캐스트/브로드캐스트 가능)
```java
private void ackDHCP(Map<String, String> receivedPayload) {
        String clientMAC = receivedPayload.get("Client MAC");
        String flags = receivedPayload.get("Flags");
        String yourIP = receivedPayload.get("Requested IP Address");
        String payload = "Client MAC=" + clientMAC +
                ",Your IP=" + yourIP +
                ",Subnet Mask=" + IPUtil.intToIp(network.getSubnetMask()) +
                ",Router=" + IPUtil.intToIp(network.getSubnetAddress()+1) +
                ",DNS=" + "DNS 서버 IP 주소" +
                ",IP Lease Time=" + LEASE_TIME +
                ",DHCP Message Type=" + DHCPMessageType.DHCPACK +
                ",DHCP Server Identifier=" + ipAddress;

        int sourcePort = 67;
        int destinationPort = 68;
        int length = payload.length() + 8;
        int checksum = 0;

        UDPDatagram.UDPHeader header = new UDPDatagram.UDPHeader(sourcePort, destinationPort, length, checksum);
        UDPDatagram datagram = new UDPDatagram(header, payload);
        IPPacket ipPacket = new IPPacket(ipAddress, "255.255.255.255", 17, datagram);
        EthernetFrame frame = new EthernetFrame(clientMAC, MACAddress, 0x0800, ipPacket);

        if(flags.equals("1")) {
            network.broadcast(frame, this);
        } else if(flags.equals("0")) {
            // unicast
        }
    }
```

이후 클라이언트는 해당 IP로 통신할 수 있게 됨