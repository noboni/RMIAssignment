import java.rmi.MarshalledObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String args[]){
        try{
            Registry registry = LocateRegistry.getRegistry();
            ProxyServerInterface proxyServerInterface = (ProxyServerInterface) registry.lookup("proxy");
            Integer serverId = proxyServerInterface.getServerId(1);
            String serverName = "server"+ serverId;
            MarshalledObject<Response> response = null;
            ServiceInterface serverStub = (ServiceInterface) registry.lookup(serverName);
//            ServiceInterface serverStub2 = (ServiceInterface) registry.lookup("server1");
//            serverStub.getAllGeoNameInformation();
            System.out.println("Here----------------"+serverStub.getNumberOfCities("NO",100000L));

        }catch(Exception e){System.out.println(e);}
    }

    public static List<Request> getAllMethodInformation() {
        List<Request> result = new ArrayList<Request>();
        String fileName = "2022-09-07-input.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
            try {
                // Read first line
                String line = br.readLine();
                // Make sure file has correct headers
                if (line == null) throw new IllegalArgumentException("File is empty");
                
                while ((line = br.readLine()) != null) {
                    // Break line into entries using space
                    String[] items = line.split(" ");
                    try {
                        // Convert data to request record
                        Request request = new Request();
                        request.setMethodName(items[0]);
                        List<String> params = new ArrayList<String>();
                        for(int i = 1; i < items.length - 1; i++){
                            params.add(items[i]);
                        }
                        request.setParameters(params);
                        request.setZone(Long.parseLong(items[items.length - 1].split(":")[1]));
                        result.add(request);
                        System.out.println(request.getParameters());
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e) {
                        // Caught errors indicate a problem with data format -> Print warning and continue
                        e.printStackTrace();
                    }
                }
                System.out.println("Total size---------------" + result.size());
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
