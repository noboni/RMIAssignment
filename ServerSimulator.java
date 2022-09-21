import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class ServerSimulator {
    public static Map<Integer, ServiceInterface> servers = new HashMap<>();

    public static void main(String args[]) {
        try {
            Registry registry = LocateRegistry.getRegistry();
            ProxyServer proxyServer = new ProxyServer();
            ProxyServerInterface proxyServerInterface = (ProxyServerInterface) UnicastRemoteObject.exportObject(proxyServer, 9);
            registry.bind("proxy", proxyServerInterface);

            for (int cnt = 1; cnt <= 5; cnt++) {
                Servant servant = new Servant(cnt);
                ServiceInterface serverStub = (ServiceInterface) UnicastRemoteObject.exportObject(servant, cnt);
                registry.bind(servant.getName(), serverStub);
                servers.put(cnt, serverStub);

                Thread thread = new Thread() {
                    public void run() {
                        while (true) {
                            try {
                                serverStub.executeFunction();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                thread.start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static ServiceInterface getServerById(Integer serverId) {
        return servers.get(serverId);

    }
}
