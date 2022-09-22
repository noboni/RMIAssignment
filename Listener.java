import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Listener extends Remote {
    void workCompleted(Request request, Response result, boolean isFromClientCache, boolean isFromServerCache) throws RemoteException;
}
