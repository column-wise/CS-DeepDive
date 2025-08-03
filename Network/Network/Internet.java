package Network.Network;

import Network.Node.Core.Router;

import java.util.ArrayList;
import java.util.List;

public class Internet {
	private List<Subnet> subnets;
	private List<Router> routers;

	public Internet() {
		subnets = new ArrayList<>();
		routers = new ArrayList<>();
	}

	public void addSubnet(Subnet subnet) {
		subnets.add(subnet);
	}

	public void addRouter(Router router) {
		routers.add(router);
	}
}
