package aws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdNameHolder {

    private static Map<String, String> nameMap = new HashMap<>();
    private static Map<String, String> idMap = new HashMap<>();

    public static void addInstance(String name, String id) {
        nameMap.put(id, name);
        idMap.put(name, id);
    }

    public static void removeInstance(String id) {
        nameMap.remove(idMap.get(id));
        idMap.remove(id);
    }

    public static void clear() {
        nameMap.clear();
        idMap.clear();
    }

    public static int size() {
        return nameMap.size();
    }

    public static Map<String, String> listInstances() {
        return idMap;
    }

    public static String convertNameToIdIfExists(String name) {
        return idMap.getOrDefault(name, name);
    }
}
