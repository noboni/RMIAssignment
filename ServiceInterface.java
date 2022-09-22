import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceInterface extends Remote {
    void storeRequestInQueue(MarshalledObject<Request> request) throws RemoteException;
    void executeFunction(boolean isCacheEnabled) throws RemoteException;
    int getQueueSize() throws RemoteException;
}
