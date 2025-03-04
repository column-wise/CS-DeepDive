package NetworkSimulation.Util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class IPUtil {

    public static int ipToInt(String ip) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(ip);
        byte[] bytes = inetAddress.getAddress();
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static String intToIp(int ipInt) {
        return String.format("%d.%d.%d.%d",
                (ipInt >> 24) & 0xFF,
                (ipInt >> 16) & 0xFF,
                (ipInt >> 8) & 0xFF,
                ipInt & 0xFF);
    }
}