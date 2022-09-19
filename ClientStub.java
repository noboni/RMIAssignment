import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ClientStub {
    public static void main(String args[]){
        try{
            Registry registry = LocateRegistry.getRegistry();
            ServerInterface serverStub = (ServerInterface) registry.lookup("server");
            ServerInterface serverStub2 = (ServerInterface) registry.lookup("server1");
            serverStub.getAllGeoNameInformation();
            System.out.println(serverStub2.add(1,2));

        }catch(Exception e){System.out.println(e);}
    }
}
