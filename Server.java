import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;

public class Server {
    private String name;
    private Queue<Request> requests = new LinkedList<>();

    public Server(Integer serverId) throws RemoteException, AlreadyBoundException {
        System.out.println("Server " + serverId + "is running.");
        this.name = "server" + serverId;
        Registry registry = LocateRegistry.getRegistry();
        Servant servant = new Servant();
        ServiceInterface serverStub = (ServiceInterface) UnicastRemoteObject.exportObject(servant, serverId);
        registry.bind(name, serverStub);
    }

    public int  getQueueSize() {
        return requests.size();
    }
}
