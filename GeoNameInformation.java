public class GeoNameInformation {
    private Long geonameId;
    private String name;
    private String countryCode;
    private String countryName;
    private Long population;
    private String timeZone;
    private String coordinates;

    public GeoNameInformation(){

    }
    public GeoNameInformation(Long geonameId, String name, String countryCode, String countryName, Long population, String coordinates) {
        this.geonameId = geonameId;
        this.name = name;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.population = population;
        this.coordinates = coordinates;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Long getGeonameId() {
        return geonameId;
    }

    public void setGeonameId(Long geonameId) {
        this.geonameId = geonameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Long getPopulation() {
        return population;
    }

    public void setPopulation(Long population) {
        this.population = population;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }
}
