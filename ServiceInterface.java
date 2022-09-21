import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Queue;

public interface ServiceInterface extends Remote {
    void storeRequestInQueue(MarshalledObject<Request> request) throws RemoteException;
    void executeFunction() throws RemoteException;
    int getQueueSize() throws RemoteException;
}
