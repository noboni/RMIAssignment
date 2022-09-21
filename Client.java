import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.MarshalledObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class Client {
    public static void main(String args[]) {
        try {
            Registry registry = LocateRegistry.getRegistry();
            ProxyServerInterface proxyServerInterface = (ProxyServerInterface) registry.lookup("proxy");
            List<Request> requests = new ArrayList<>(getAllMethodInformation());
            for (Request request : requests) {
                Integer serverId = proxyServerInterface.getServerId(request.getZone());
                String serverName = "server" + serverId;
                request.setServerId(serverId);

                ServiceInterface serverStub = (ServiceInterface) registry.lookup(serverName);
                MarshalledObject<Request> marshalledRequest = new MarshalledObject<>(request);
                serverStub.storeRequestInQueue(marshalledRequest);

//                MarshalledObject<Response> response = serverStub.executeFunction(marshalledRequest);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static List<Request> getAllMethodInformation() {
        List<Request> result = new ArrayList<Request>();
//        String fileName = "2022-09-07-input.txt";
        String fileName = "test-input.txt";
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
}
