import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientStub {
    public static void main(String args[]){
        try{
            Registry registry = LocateRegistry.getRegistry();
            ServerInterface serverStub = (ServerInterface) registry.lookup("server");
            System.out.println(serverStub.add(1,2));
        }catch(Exception e){System.out.println(e);}
    }
}
