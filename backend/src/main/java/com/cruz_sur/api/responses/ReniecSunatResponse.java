package com.cruz_sur.api.responses;

import org.springframework.beans.factory.annotation.Value; // Cambia esta importación
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReniecSunatResponse {

    @Value("${apis.token}")
    private String apisToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://api.apis.net.pe";

    public Map<String, Object> getPerson(String dni) {
        String url = BASE_URL + "/v2/reniec/dni?numero=" + dni;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apisToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, requestEntity, Map.class);
            System.out.printf(response.getBody().toString());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode().value()) {
                case 422:
                    return Map.of("error", "DNI no válido");
                case 404:
                    return Map.of("error", "DNI no encontrado");
                default:
                    return Map.of("error", "Ocurrió un error al consultar el DNI");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public Map<String, Object> getRuc(String ruc) {
        String url = BASE_URL + "/v2/sunat/ruc?numero=" + ruc;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apisToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
            System.out.println("Response from API: " + response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode().value()) {
                case 422:
                    return Map.of("error", "RUC no válido");
                case 404:
                    return Map.of("error", "RUC no encontrado");
                default:
                    return Map.of("error", "Ocurrió un error al consultar el RUC");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
