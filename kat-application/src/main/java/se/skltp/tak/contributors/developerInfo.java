
package se.skltp.tak.contributors;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class developerInfo implements InfoContributor {

    private static List<String> splitToList(String separatedString) {
        List<String> list = Arrays.asList(separatedString.split("[.]"));
        return list;
    }

    private static LinkedHashMap<String, Object> propToTree(Properties props) {
        LinkedHashMap<String, Object> sys = new LinkedHashMap<String, Object>();
        for (Object key : props.keySet()) {
            LinkedHashMap<String, Object> node = sys;
            String lastSub = null;
            LinkedHashMap<String, Object> pn = null;
            for (String sub : splitToList((String) key)) {
                Object subNode = node.get(sub);
                pn = node;
                lastSub = sub;
                if (subNode instanceof Map) {
                    node = (LinkedHashMap<String, Object>) subNode;
                } else {
                    node.put(sub, new LinkedHashMap<String, Object>());
                    node = (LinkedHashMap<String, Object>) node.get(sub);
                }
            }
            pn.put(lastSub, props.get(key));
        }
        return sys;
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("system", propToTree(System.getProperties()));
        builder.withDetail("environment", System.getenv());
    }
}
