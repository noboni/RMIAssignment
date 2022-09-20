import java.rmi.MarshalledObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String args[]){
        try{
            Registry registry = LocateRegistry.getRegistry();
            ProxyServerInterface proxyServerInterface = (ProxyServerInterface) registry.lookup("proxy");
            Integer serverId = proxyServerInterface.getServerId(1);
            MarshalledObject<Response> response = null;
//            ServiceInterface serverStub = (ServiceInterface) registry.lookup("server");
//            ServiceInterface serverStub2 = (ServiceInterface) registry.lookup("server1");
//            serverStub.getAllGeoNameInformation();
            for (String s : registry.list()){
                System.out.println(s);
            }

        }catch(Exception e){System.out.println(e);}
    }
}
