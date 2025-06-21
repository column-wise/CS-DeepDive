package Network.Constants;

import java.util.Optional;

public enum DHCPMessageType {
	DHCPDISCOVER, DHCPOFFER, DHCPREQUEST, DHCPACK, DHCPNACK;

	public static Optional<DHCPMessageType> from(String value) {
		try {
			return Optional.of(valueOf(value.trim()));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}
