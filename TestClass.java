import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestClass {
    public static void main(String args[]){
        System.out.println(getPopulationOfCountry("SE"));
        System.out.println(getNumberOfCities("NO",100000L));
        System.out.println(getNumberOfCountries(2L,5000000L));
        System.out.println(getNumberOfCountries(30L,100000L,800000L));

    }

    public static List<GeoNameInformation> getAllGeoNameInformation() {
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
//                        if (items.length > 7) throw new ArrayIndexOutOfBoundsException();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Long getPopulationOfCountry(String countryCode) {
        List<GeoNameInformation> geoNameInformation = getAllGeoNameInformation();
        return geoNameInformation.stream().filter(it -> it.getCountryCode().equals(countryCode))
                .map(GeoNameInformation::getPopulation)
                .mapToLong(Long::longValue).sum();
    }


    public static Integer getNumberOfCities(String countryCode, Long minAmountOfPopulation) {
        List<GeoNameInformation> geoNameInformation = getAllGeoNameInformation();
        return (int) geoNameInformation.stream().filter(it -> it.getCountryCode().equals(countryCode))
                .filter(it -> it.getPopulation() >= minAmountOfPopulation)
                .count();
    }

    public static Long getNumberOfCountries(Long cityCount, Long minPopulation){
        List<GeoNameInformation> geoNameInformations = getAllGeoNameInformation();
        return geoNameInformations.stream().filter(it -> it.getPopulation() >= minPopulation)
                .collect(Collectors.groupingBy(GeoNameInformation::getCountryName,Collectors.counting()))
                .values().stream().filter(it->it>=cityCount).count();
    }

    public static Long getNumberOfCountries(Long cityCount, Long minPopulation, Long maxPopulation){
        List<GeoNameInformation> geoNameInformations = getAllGeoNameInformation();
        return geoNameInformations.stream()
                .filter(it -> it.getPopulation() >= minPopulation && it.getPopulation()<=maxPopulation)
                .collect(Collectors.groupingBy(GeoNameInformation::getCountryName,Collectors.counting()))
                .values().stream().filter(it->it>=cityCount).count();
    }
}
