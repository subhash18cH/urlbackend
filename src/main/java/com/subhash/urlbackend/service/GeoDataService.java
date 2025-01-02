package com.subhash.urlbackend.service;


import com.subhash.urlbackend.utility.GeoData;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

public class GeoDataService {

    private  final String API_KEY = "351b2f66128ca0";
    private  final String API_URL = "https://ipinfo.io/{IP_ADDRESS}?token=" + API_KEY;

    public GeoData getGeoData(String ipAddress) {
        RestTemplate restTemplate = new RestTemplate();
        String url = API_URL.replace("{IP_ADDRESS}", "8.8.8.8");
        try {
            String response = restTemplate.getForObject(url, String.class);

            if (response != null) {
                JSONObject json = new JSONObject(response);
                GeoData geoData = new GeoData();

                geoData.setCity(json.optString("city"));
                geoData.setRegion(json.optString("region"));
                geoData.setCountry(json.optString("country"));
                return geoData;
            }
        } catch (Exception e) {
            System.err.println("Error fetching GeoData: " + e.getMessage());
        }
        return null;
    }
}

