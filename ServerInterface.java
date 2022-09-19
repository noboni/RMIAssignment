import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface extends Remote {
    int add(int number1, int number2) throws RemoteException;
    List<GeoNameInformation> getAllGeoNameInformation() throws IOException;
    Long getPopulationOfCountry(String countryCode);
    Integer getNumberOfCities(String countryCode, Long minAmountOfPopulation);
}
