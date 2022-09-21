import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Listener extends Remote {
    void workCompleted(Request request, Response result) throws RemoteException;
}
