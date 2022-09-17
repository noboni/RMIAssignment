import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    int add(int number1, int number2) throws RemoteException;
}
