import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ServerSimulator {
    public static Map<Integer, ServiceInterface> servers = new HashMap<>();

    public static void main(String args[]) {
        try {
            System.out.println("Started server simulator\n\n\n");
            boolean isCacheEnabled;
            if(args.length == 0 || args[0].equalsIgnoreCase("false")){
                isCacheEnabled = false;
            }
            else {
                isCacheEnabled = true;
            }
            //Create proxy server and register in the remote registry
            Registry registry = LocateRegistry.getRegistry();
            ProxyServer proxyServer = new ProxyServer();
            ProxyServerInterface proxyServerInterface = (ProxyServerInterface) UnicastRemoteObject.exportObject(proxyServer, 9);
            registry.bind("proxy", proxyServerInterface);

            //Create 5 servers and register in the remote registry
            for (int cnt = 1; cnt <= 5; cnt++) {
                Servant servant = new Servant(cnt);
                ServiceInterface serverStub = (ServiceInterface) UnicastRemoteObject.exportObject(servant, cnt);
                registry.bind(servant.getName(), serverStub);
                servers.put(cnt, serverStub);

                //Create a processing thread for every server
                Thread thread = new Thread() {
                    public void run() {
                        while (true) {
                            try {
                                serverStub.executeFunction(isCacheEnabled);
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

    /**
     * Get server interface object by server id
     * @param serverId
     * @return
     */
    public static ServiceInterface getServerById(Integer serverId) {
        return servers.get(serverId);

    }
}
