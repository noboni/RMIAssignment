import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ServerSimulator {
    public static Map<Integer, Server> servers = new HashMap<>();
    public static void main(String args[]) {
        try {
            Registry registry = LocateRegistry.getRegistry();
            Servant servant = new Servant();
            ProxyServerInterface proxyServerInterface = (ProxyServerInterface) UnicastRemoteObject.exportObject(servant, 9);
            registry.bind("proxy", proxyServerInterface);

            for (int cnt = 1; cnt <= 5; cnt++) {
                Server server = new Server(cnt);
                servers.put(cnt, server);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static Server getServerById(Integer serverId) {
        return servers.get(serverId);

    }
}
