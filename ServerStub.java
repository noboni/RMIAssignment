import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerStub {
    public static void main(String args[]){
        try{
            Registry registry = LocateRegistry.getRegistry();
            Server server =new Server();
            ServerInterface serverStub = (ServerInterface) UnicastRemoteObject.exportObject(server,0) ;
            registry.bind("server",serverStub);
        }catch(Exception e){System.out.println(e);}
    }
}
