package Network.Simulation.NAT;

import Network.Network.Internet;
import Network.Network.Subnet;
import Network.Node.Core.Router;
import Network.Node.End.Computer;
import Network.Node.End.DHCPServer;

public class NATTest {
	public static void main(String[] args) {
		Internet internet = new Internet();

		Subnet publicNetwork = Subnet.builder()
				.subnetAddress("128.119.40.0")
				.subnetMask("255.255.255.0")
				.isPrivate(false)
				.build();

		// 웹 서버는 주로 고정 IP 사용
		Computer webServer = Computer.builder()
				.mac("AA:12:34:56:78:90")
				.ip("128.119.40.186")
				.subnet(publicNetwork)
				.build();

		publicNetwork.addNode(webServer);

		Subnet privateNetwork = Subnet.builder()
				.subnetAddress("10.0.0.0")
				.subnetMask("255.255.255.0")
				.isPrivate(true)
				.build();

		Computer client = Computer.builder()
				.mac("BB:AA:CC:DD:EE:FF")
				.subnet(privateNetwork)
				.build();

		DHCPServer dhcpServer = DHCPServer.builder()
				.mac("BB:CC:DD:EE:FF:AA")
				.ip("10.0.0.10")
				.subnet(privateNetwork)
				.build();

		privateNetwork.addNode(dhcpServer);
		privateNetwork.addNode(client);

		internet.addSubnet(publicNetwork);
		internet.addSubnet(privateNetwork);

		Router router = new Router("AA:BB:CC:DD:EE:01");

		// ① 공인망 인터페이스 (ISP 쪽)
		router.addInterface(
				Router.Interface.builder()
						.mac("AA:22:33:44:55:66")
						.ip("138.76.29.7")
						.connectedSubnet(publicNetwork)
						.build()
		);

		// ② 사설망 인터페이스
		router.addInterface(
				Router.Interface.builder()
						.mac("BB:DD:AA:CC:EE:FF")
						.ip("10.0.0.254")
						.connectedSubnet(privateNetwork)
						.build()
		);

		publicNetwork.addNode(router);
		privateNetwork.addNode(router);

		privateNetwork.setGateway(router);

		client.discoverDHCPServer();
	}
}
