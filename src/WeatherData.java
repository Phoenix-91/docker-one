public class WeatherData {

    private String city;
    private double temperature;
    private double feelsLike;
    private int humidity;
    private double windSpeed;
    private String description;

    public WeatherData(String city, double temperature, double feelsLike,
                       int humidity, double windSpeed, String description) {
        this.city        = city;
        this.temperature = temperature;
        this.feelsLike   = feelsLike;
        this.humidity    = humidity;
        this.windSpeed   = windSpeed;
        this.description = description;
    }

    // Getters
    public String getCity()        { return city; }
    public double getTemperature() { return temperature; }
    public double getFeelsLike()   { return feelsLike; }
    public int    getHumidity()    { return humidity; }
    public double getWindSpeed()   { return windSpeed; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return String.format(
            "\n City       : %s"     +
            "\n Temp       : %.1f C" +
            "\n Feels Like : %.1f C" +
            "\n Humidity   : %d%%"   +
            "\n Wind Speed : %.1f m/s" +
            "\n Condition  : %s",
            city, temperature, feelsLike, humidity, windSpeed, description
        );
    }
}
