import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class Servant implements ServiceInterface {
    private String name;
    public Queue<Request> requests = new LinkedList<>();
    static volatile LinkedHashMap<String, Response> serverCache = new LinkedHashMap<>();

    public Servant(Integer serverId) throws RemoteException {
        System.out.println("Server " + serverId + "is running.");
        this.name = "server" + serverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public synchronized int getQueueSize() {
        return requests.size();
    }

    /**
     * Processing the whole dataset and putting them in a list of GeoNameInformation
     * @return
     */
    private List<GeoNameInformation> getAllGeoNameInformation() {
        List<GeoNameInformation> result = new ArrayList<GeoNameInformation>();
        String fileName = "2022-09-07-dataset.csv";
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
            try {
                // Read first line
                String line = br.readLine();
                // Make sure file has correct headers
                if (line == null) throw new IllegalArgumentException("File is empty");
                if (!line.equals("Geoname ID;Name;Country Code;Country name EN;Population;Timezone;Coordinates"))
                    throw new IllegalArgumentException("File has wrong columns: " + line);
                // Run through following lines
                while ((line = br.readLine()) != null) {
                    // Break line into entries using comma
                    String[] items = line.split(";");
                    try {
                        // If there are too many entries, throw a dummy exception, if
                        // there are too few, the same exception will be thrown later
                        // Convert data to geoNameInfo record
                        GeoNameInformation geoNameInformation = new GeoNameInformation();
                        geoNameInformation.setGeonameId(Long.parseLong(items[0]));
                        geoNameInformation.setName(items[1]);
                        geoNameInformation.setCountryCode(items[2]);
                        geoNameInformation.setCountryName(items[3]);
                        geoNameInformation.setPopulation(Long.parseLong(items[4]));
                        geoNameInformation.setTimeZone(items[5]);
                        geoNameInformation.setCoordinates(items[6]);
                        result.add(geoNameInformation);
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e) {
                        // Caught errors indicate a problem with data format -> Print warning and continue
                        e.printStackTrace();
                        System.out.println("Invalid line: " + line);
                    }
                }
                System.out.println("Total size of the dataset---------------" + result.size());
                return result;
            } finally {
                br.close();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("File not found");
        }
    }

    /**
     * given a country code as input, return the population of the country by
     * summing up the population of cities in that country
     * @param countryCode
     * @return
     */
    private Integer getPopulationOfCountry(String countryCode) {
        List<GeoNameInformation> geoNameInformation = getAllGeoNameInformation();
        return (int) geoNameInformation.stream().filter(it -> it.getCountryCode().equals(countryCode))
                .map(GeoNameInformation::getPopulation)
                .mapToLong(Long::longValue).sum();
    }

    /**
     * given a country code and min as input, return the total number of cities
     * in the given country that contains atleast the min amount of
     * population
     * @param countryCode
     * @param minAmountOfPopulation
     * @return
     */
    private Integer getNumberOfCities(String countryCode, Long minAmountOfPopulation) {
        List<GeoNameInformation> geoNameInformation = getAllGeoNameInformation();
        return (int) geoNameInformation.stream().filter(it -> it.getCountryCode().equals(countryCode))
                .filter(it -> it.getPopulation() >= minAmountOfPopulation)
                .count();
    }

    /**
     * Return the number of countries that contain atleast ‘citycount’ number
     * of cities where each city has the population of atleast ‘minpopulation’
     * number of people.
     * @param cityCount
     * @param minPopulation
     * @return
     */
    private Integer getNumberOfCountries(Long cityCount, Long minPopulation) {
        List<GeoNameInformation> geoNameInformations = getAllGeoNameInformation();
        return (int) geoNameInformations.stream().filter(it -> it.getPopulation() >= minPopulation)
                .collect(Collectors.groupingBy(GeoNameInformation::getCountryName, Collectors.counting()))
                .values().stream().filter(it -> it >= cityCount).count();
    }


    /**
     *  Return the number of countries that contain atleast ‘citycount’ number
     *  of cities where each city has population between ‘minpopulation’ and
     *  ‘maxpopulation’
     * @param cityCount
     * @param minPopulation
     * @param maxPopulation
     * @return
     */
    private Integer getNumberOfCountries(Long cityCount, Long minPopulation, Long maxPopulation) {
        List<GeoNameInformation> geoNameInformations = getAllGeoNameInformation();
        return (int) geoNameInformations.stream()
                .filter(it -> it.getPopulation() >= minPopulation && it.getPopulation() <= maxPopulation)
                .collect(Collectors.groupingBy(GeoNameInformation::getCountryName, Collectors.counting()))
                .values().stream().filter(it -> it >= cityCount).count();
    }

    /**
     * Store the requests in the queue for proccessing
     * @param marshalledRequest
     * @throws RemoteException
     */
    @Override
    public synchronized void storeRequestInQueue(MarshalledObject<Request> marshalledRequest) throws RemoteException {
        try {
            Request request = marshalledRequest.get();
            request.setTurnAroundStartTime(System.currentTimeMillis());
            //Check if server in the same zone
            if (request.getZone() == request.getServerId()) {
                //Simulate the connection time to the server in the same zone
                Thread.sleep(80);
            } else {
                //Simulate the connection time to the server in the neighbour zone
                Thread.sleep(170);
            }
            request.setWaitingStartTime(System.currentTimeMillis());
            this.requests.add(request);
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            throw new IllegalArgumentException("Some exception occured");
        }
    }


    /**
     * Call from the processing thread to check the queue size and execute.
     * Return from the cache if cache is already enabled and data is in the cache
     * @param isCacheEnabled
     * @throws RemoteException
     */
    @Override
    public void executeFunction(boolean isCacheEnabled) throws RemoteException {
        while (requests.size() > 0) {
            Request request = requests.poll();
            boolean isFromServerCache = false;
            Response response = null;
            //Check if the cache is enabled or not
            //If the cache is enabled and the data is in the cache send it from the cache to the client listener
            //Else process the data and send it to the client listener
            if(isCacheEnabled) {
                String requestString = getRequestString(request);
                response = serverCache.get(requestString);
                isFromServerCache = true;
                if (response == null) {
                    isFromServerCache = false;
                    if (serverCache.size() > 200) {
                        Map.Entry<String, Response> entry = serverCache.entrySet().iterator().next();
                        serverCache.remove(entry.getKey());
                    }
                    response = executeFunction(request);
                    serverCache.put(requestString, response);
                }
            }else {
                response = executeFunction(request);
            }
            request.getListener().workCompleted(request, response, false, isFromServerCache );
        }
    }

    /**
     * This method calls the necesary functions according to the method names and parameters
     * @param request
     * @return
     */
    private Response executeFunction(Request request) {
        try {
            Response response = new Response();
            response.setWaitingTime(System.currentTimeMillis() - request.getWaitingStartTime());
            request.setExecutionStartTime(System.currentTimeMillis());
            Integer result = null;
            //Intended method call
            if (request.getMethodName().equals("getPopulationofCountry")) {
                if (request.getParameters().size() < 1) {
                    throw new IllegalArgumentException("Invalid number of parameters");
                }
                String countryCode = request.getParameters().get(0);
                result = getPopulationOfCountry(countryCode);
            } else if (request.getMethodName().equals("getNumberofCities")) {
                if (request.getParameters().size() < 2) {
                    throw new IllegalArgumentException("Invalid number of parameters");
                }
                String countryCode = request.getParameters().get(0);
                Long minPopulation = Long.parseLong(request.getParameters().get(1));
                result = getNumberOfCities(countryCode, minPopulation);
            } else if (request.getMethodName().equals("getNumberofCountries")) {
                if (request.getParameters().size() == 2) {
                    Long cityCount = Long.parseLong(request.getParameters().get(0));
                    Long minPopulation = Long.parseLong(request.getParameters().get(1));
                    result = getNumberOfCountries(cityCount, minPopulation);
                } else if (request.getParameters().size() == 3) {
                    Long cityCount = Long.parseLong(request.getParameters().get(0));
                    Long minPopulation = Long.parseLong(request.getParameters().get(1));
                    Long maxPopulation = Long.parseLong(request.getParameters().get(2));
                    result = getNumberOfCountries(cityCount, minPopulation, maxPopulation);
                } else {
                    throw new IllegalArgumentException("Invalid number of parameters");
                }
            } else {
                throw new IllegalArgumentException("Invalid number of parameters");
            }
            response.setResult(result);
            response.setMethodName(request.getMethodName());
            response.setServerId("" + request.getServerId());
            response.setParameters(request.getParameters());
            response.setExecutionTime(System.currentTimeMillis() - request.getExecutionStartTime());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Some exception occured");
        }
    }

    private String getRequestString(BaseRequest baseRequest) {
        String parameters = baseRequest.getParameters().stream().collect(Collectors.joining(" "));
        return baseRequest.getMethodName() + " " + parameters + " Zone:" + baseRequest.getZone();
    }

}
