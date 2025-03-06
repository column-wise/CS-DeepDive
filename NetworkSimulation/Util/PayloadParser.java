package NetworkSimulation.Util;

import java.util.HashMap;
import java.util.Map;

public class PayloadParser {
    public static Map<String, String> parsePayload(String payload) {
        Map<String, String> payloadMap = new HashMap<String, String>();
        String[] tokens = payload.split(",");
        for (String token : tokens) {
            String[] keyValue = token.split("=");
            payloadMap.put(keyValue[0], keyValue[1]);
        }

        return payloadMap;
    }
}
