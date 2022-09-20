import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientStub {
    public static void main(String args[]){
        try{
            Registry registry = LocateRegistry.getRegistry();
            ServiceInterface serverStub = (ServiceInterface) registry.lookup("server");
            ServiceInterface serverStub2 = (ServiceInterface) registry.lookup("server1");
            serverStub.getAllGeoNameInformation();

        }catch(Exception e){System.out.println(e);}
    }
}
