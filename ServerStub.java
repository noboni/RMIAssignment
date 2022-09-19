import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerStub {
    public static void main(String args[]){
        try{
            Registry registry = LocateRegistry.getRegistry();
            Registry registry2 = LocateRegistry.getRegistry();
            Server server =new Server();
            ServerInterface serverStub = (ServerInterface) UnicastRemoteObject.exportObject(server,0) ;
            Server server2 =new Server();
            ServerInterface serverStub2 = (ServerInterface) UnicastRemoteObject.exportObject(server2,1) ;
//            ServerInterface serverStub3 = (ServerInterface) UnicastRemoteObject.exportObject(server,2) ;
//            ServerInterface serverStub4 = (ServerInterface) UnicastRemoteObject.exportObject(server,3) ;
//            ServerInterface serverStub5 = (ServerInterface) UnicastRemoteObject.exportObject(server,5) ;
            registry.bind("server",serverStub);
            registry2.bind("server1",serverStub2);
//            registry.bind("server2",serverStub3);
//            registry.bind("server3",serverStub4);
//            registry.bind("server4",serverStub5);
        }catch(Exception e){System.out.println(e);}
    }
}
