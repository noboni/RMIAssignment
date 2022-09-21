import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Queue;

public interface ServiceInterface extends Remote {
    List<GeoNameInformation> getAllGeoNameInformation() throws RemoteException;
    Integer getPopulationOfCountry(String countryCode) throws RemoteException;
    Integer getNumberOfCities(String countryCode, Long minAmountOfPopulation) throws RemoteException;
    Integer getNumberOfCountries(Long cityCount, Long minPopulation) throws RemoteException;
    Integer getNumberOfCountries(Long cityCount, Long minPopulation, Long maxPopulation) throws RemoteException;
    void storeRequestInQueue(MarshalledObject<Request> request) throws RemoteException;
    void executeFunction() throws RemoteException;
    Response executeFunction(Request request)  throws RemoteException;
    int getQueueSize() throws RemoteException;
}
