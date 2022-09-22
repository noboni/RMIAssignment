import java.io.*;
import java.rmi.MarshalledObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Client extends java.rmi.server.UnicastRemoteObject implements Listener {
    volatile static LinkedHashMap<String, Response> clientCache = new LinkedHashMap<>();

    public static void main(String args[]) throws RemoteException {
        boolean isCacheEnabled;
        //check if the client cache is enabled or not
        isCacheEnabled = args.length != 0 && !args[0].equalsIgnoreCase("false");
        new Client(isCacheEnabled);
    }

    public Client(boolean isCacheEnabled) throws RemoteException {
        try {
            System.out.println("Starting client server");
            Registry registry = LocateRegistry.getRegistry();
            //Get the proxy server
            ProxyServerInterface proxyServerInterface = (ProxyServerInterface) registry.lookup("proxy");
            List<Request> requests = new ArrayList<>(getAllMethodInformation());
            for (Request request : requests) {
                request.setCacheEnabledInClient(isCacheEnabled);
                //if  the cache is enable and data is in the cache send to the writer to write in the file
                //else if the cache is enabled and the data is not in the cache call the server and execute and then put it in the client cache
                //else the server and execute
                //And finally write in the desired file
                if (isCacheEnabled) {
                    String requestString = getRequestString(request);
                    Response response = clientCache.get(requestString);
                    System.out.println("Size of client Cache:" + clientCache.size());
                    if (response != null) {
                        System.out.println("Found in client cache");
                        workCompleted(request, response, true, false);
                        continue;
                    }
                }
                Integer serverId = proxyServerInterface.getServerId(request.getZone());
                String serverName = "server" + serverId;
                request.setServerId(serverId);
                request.setListener(this);
                System.out.println("Requesting to server:" + serverId);
                ServiceInterface serverStub = (ServiceInterface) registry.lookup(serverName);
                MarshalledObject<Request> marshalledRequest = new MarshalledObject<>(request);
                serverStub.storeRequestInQueue(marshalledRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parsing data from input file and storing in the request objects
     *
     * @return
     */
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
                System.out.println("Total number of client requests---------------" + result.size());
                return result;
            } finally {
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This is a client side listener which is called from the server side after a request execution is completed
     * We check whether it comes from a cache or not and write it in the corresponding file accordingly
     *
     * @param request
     * @param response
     * @param isFromClientCache
     * @param isFromServerCache
     * @throws RemoteException
     */
    @Override
    public synchronized void workCompleted(Request request,
                                           Response response,
                                           boolean isFromClientCache,
                                           boolean isFromServerCache) throws RemoteException {
        System.out.println("Received response from server:" + response.getServerId());
        if (isFromClientCache) {
            writeInFile(response, "client_cache.txt");
        } else if (isFromServerCache) {
            response.setTurnAroundTime(System.currentTimeMillis() - request.getTurnAroundStartTime());
            writeInFile(response, "server_cache.txt");
        } else {
            response.setTurnAroundTime(System.currentTimeMillis() - request.getTurnAroundStartTime());
            if (request.isCacheEnabledInClient()) {
                if (clientCache.size() > 50) {
                    Map.Entry<String, Response> entry = clientCache.entrySet().iterator().next();
                    clientCache.remove(entry.getKey());
                }

                String requestString = getRequestString(request);
                clientCache.put(requestString, response);
            }
            writeInFile(response, "naive_server.txt");
        }

    }

    private void writeInFile(Response response, String fileName) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        try {
            // In order to append text to a file, you need to open // file into append mode, you do it by using // FileReader and passing append = true
            fw = new FileWriter(fileName, true);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            String result = response.getResult() + " " + response.getMethodName() + " " +
                    response.getParameters().stream().collect(Collectors.joining(" "))
                    + " (turnaround time: " + response.getTurnAroundTime() + " ms, " +
                    "execution time: " + response.getExecutionTime() + " ms, " +
                    "waiting time: " + response.getWaitingTime() + " ms, processed by Server " + response.getServerId() + ")";
            pw.println(result);
            System.out.println("Data Successfully appended into file:" + fileName);
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

    private String getRequestString(BaseRequest baseRequest) {
        String parameters = baseRequest.getParameters().stream().collect(Collectors.joining(" "));
        return baseRequest.getMethodName() + " " + parameters + " Zone:" + baseRequest.getZone();
    }

}
