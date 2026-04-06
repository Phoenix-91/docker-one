import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONObject;

public class WeatherService {

private static final String API_KEY  = "API_KEY_OF_OPENWEATHERMAP";  // <-- Replace this
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public WeatherData getWeather(String city) throws Exception {
        // Encode city name to handle spaces and special characters
        String encodedCity = URLEncoder.encode(city, "UTF-8");

        String urlString = BASE_URL
                + "?q="     + encodedCity
                + "&appid=" + API_KEY
                + "&units=metric";  // Change to "imperial" for Fahrenheit

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int responseCode = conn.getResponseCode();

        if (responseCode == 401) throw new RuntimeException("Invalid API Key. Check your key at openweathermap.org.");
        if (responseCode == 404) throw new RuntimeException("City \"" + city + "\" not found. Try another name.");
        if (responseCode != 200) throw new RuntimeException("API Error: HTTP " + responseCode);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        conn.disconnect();

        return parseResponse(new JSONObject(response.toString()));
    }

    private WeatherData parseResponse(JSONObject json) {
        String name        = json.getString("name")
                           + ", " + json.getJSONObject("sys").getString("country");
        JSONObject main    = json.getJSONObject("main");
        JSONObject wind    = json.getJSONObject("wind");
        String description = json.getJSONArray("weather")
                                 .getJSONObject(0)
                                 .getString("description");

        return new WeatherData(
            name,
            main.getDouble("temp"),
            main.getDouble("feels_like"),
            main.getInt("humidity"),
            wind.getDouble("speed"),
            description
        );
    }
}
