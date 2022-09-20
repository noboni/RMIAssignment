import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServiceInterface extends Remote {
    List<GeoNameInformation> getAllGeoNameInformation() throws RemoteException;
    Long getPopulationOfCountry(String countryCode) throws RemoteException;
    Integer getNumberOfCities(String countryCode, Long minAmountOfPopulation) throws RemoteException;
    Integer getNumberOfCountries(Long cityCount, Long minPopulation) throws RemoteException;
    Integer getNumberOfCountries(Long cityCount, Long minPopulation, Long maxPopulation) throws RemoteException;
}
