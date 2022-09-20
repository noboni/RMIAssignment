import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProxyServerInterface extends Remote {
        Integer getServerId(Integer zone) throws RemoteException;
}
