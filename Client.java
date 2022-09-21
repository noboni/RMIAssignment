import java.io.*;
import java.rmi.MarshalledObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Client extends java.rmi.server.UnicastRemoteObject  implements Listener{
    public static void main(String args[]) throws RemoteException {
        new Client();
    }
    public Client() throws RemoteException{
        try {
            Registry registry = LocateRegistry.getRegistry();
            ProxyServerInterface proxyServerInterface = (ProxyServerInterface) registry.lookup("proxy");
            List<Request> requests = new ArrayList<>(getAllMethodInformation());
            for (Request request : requests) {
                Integer serverId = proxyServerInterface.getServerId(request.getZone());
                String serverName = "server" + serverId;
                request.setServerId(serverId);
                request.setListener(this);

                ServiceInterface serverStub = (ServiceInterface) registry.lookup(serverName);
                MarshalledObject<Request> marshalledRequest = new MarshalledObject<>(request);
                serverStub.storeRequestInQueue(marshalledRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Request> getAllMethodInformation() {
        List<Request> result = new ArrayList<Request>();
        String fileName = "2022-09-07-input.txt";
//        String fileName = "test-input.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
            try {
                // Read first line
                String line = br.readLine();
                // Make sure file has correct headers
                if (line == null) throw new IllegalArgumentException("File is empty");

                while (line != null) {
                    System.out.println("Line-------------:" + line);
                    // Break line into entries using space
                    String[] items = line.split(" ");
                    try {
                        // Convert data to request record
                        Request request = new Request();
                        request.setMethodName(items[0]);
                        List<String> params = new ArrayList<String>();
                        for (int i = 1; i < items.length - 1; i++) {
                            params.add(items[i]);
                        }
                        request.setParameters(params);
                        request.setZone(Integer.parseInt(items[items.length - 1].split(":")[1]));
                        result.add(request);
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e) {
                        // Caught errors indicate a problem with data format -> Print warning and continue
                        e.printStackTrace();
                    }
                    line = br.readLine();
                }
                System.out.println("Total requests---------------" + result.size());
                return result;
            } finally {
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public synchronized void workCompleted(Request request, Response response) throws RemoteException {
        response.setTurnAroundTime(System.currentTimeMillis() - request.getTurnAroundStartTime());
        System.out.println("Here..................");
        // In order to append text to a file, you need to open // file into append mode, you do it by using // FileReader and passing append = true
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter("naive_server.txt", true);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            String result = response.getResult()+" "+response.getMethodName()+ " "+
                    response.getParameters().stream().collect(Collectors.joining(" "))
                    +" (turnaround time: "+response.getTurnAroundTime()+" ms, " +
                    "execution time: "+response.getExecutionTime()+" ms, " +
                    "waiting time: "+response.getWaitingTime()+" ms, processed by Server "+response.getServerId()+")" ;
            pw.println(result);
            System.out.println("Data Successfully appended into file");
            pw.flush();
        } catch (IOException i) {
            i.printStackTrace();
        } finally {
            try {
                pw.close();
                bw.close();
                fw.close();
            } catch (IOException io) {// can't do anything
                io.printStackTrace();
            }
        }
    }

}
