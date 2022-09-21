import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.MarshalledObject;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class Servant implements ServiceInterface {
    private String name;
    public Queue<Request> requests = new LinkedList<>();

    public Servant(Integer serverId) throws RemoteException, AlreadyBoundException {
        System.out.println("Server " + serverId + "is running.");
        this.name = "server" + serverId;
//        ExecuteRequests executeRequests = new ExecuteRequests();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public class ExecuteRequests implements Runnable {
//        public void run() {
//            while (true) {
//                if (requests.size() > 0) {
//                    Request request = requests.poll();
//                    Response response = executeFunction(request);
//                    System.out.println("Result------:" + response.getResult());
//                }
//            }
//        }
//    }

    @Override
    public int getQueueSize() {
        return requests.size();
    }

    @Override
    public List<GeoNameInformation> getAllGeoNameInformation() {
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
                System.out.println("Total size---------------" + result.size());
                return result;
            } finally {
                br.close();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("File not found");
        }
    }

    @Override
    public Integer getPopulationOfCountry(String countryCode) {
        List<GeoNameInformation> geoNameInformation = getAllGeoNameInformation();
        return (int) geoNameInformation.stream().filter(it -> it.getCountryCode().equals(countryCode))
                .map(GeoNameInformation::getPopulation)
                .mapToLong(Long::longValue).sum();
    }

    @Override
    public Integer getNumberOfCities(String countryCode, Long minAmountOfPopulation) {
        List<GeoNameInformation> geoNameInformation = getAllGeoNameInformation();
        return (int) geoNameInformation.stream().filter(it -> it.getCountryCode().equals(countryCode))
                .filter(it -> it.getPopulation() >= minAmountOfPopulation)
                .count();
    }

    @Override
    public Integer getNumberOfCountries(Long cityCount, Long minPopulation) {
        List<GeoNameInformation> geoNameInformations = getAllGeoNameInformation();
        return (int) geoNameInformations.stream().filter(it -> it.getPopulation() >= minPopulation)
                .collect(Collectors.groupingBy(GeoNameInformation::getCountryName, Collectors.counting()))
                .values().stream().filter(it -> it >= cityCount).count();
    }

    @Override
    public Integer getNumberOfCountries(Long cityCount, Long minPopulation, Long maxPopulation) {
        List<GeoNameInformation> geoNameInformations = getAllGeoNameInformation();
        return (int) geoNameInformations.stream()
                .filter(it -> it.getPopulation() >= minPopulation && it.getPopulation() <= maxPopulation)
                .collect(Collectors.groupingBy(GeoNameInformation::getCountryName, Collectors.counting()))
                .values().stream().filter(it -> it >= cityCount).count();
    }

    @Override
    public void storeRequestInQueue(MarshalledObject<Request> request) throws RemoteException {
        try {
            this.requests.add(request.get());
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Some exception occured");
        }

    }

    @Override
    public void executeFunction() {
        while (requests.size() > 0) {
            Request request = requests.poll();
            Response response = executeFunction(request);
            System.out.println("Resonse-----------" + response.getResult());
        }
    }

    @Override
    public Response executeFunction(Request request) {
        try {
            if (request.getZone() == request.getServerId()) {
                Thread.sleep(80);
            } else {
                Thread.sleep(170);
            }
            Integer result = null;
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

            Response response = new Response();
            response.setResult(result);
            response.setMethodName(request.getMethodName());
            response.setServerId("" + request.getServerId());
            response.setParameters(request.getParameters());
            return response;
        } catch (Exception e) {
            throw new IllegalArgumentException("Some exception occured");
        }
    }

}
