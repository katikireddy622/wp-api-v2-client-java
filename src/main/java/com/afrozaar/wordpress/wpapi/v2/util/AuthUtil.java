package com.afrozaar.wordpress.wpapi.v2.util;

import com.afrozaar.wordpress.wpapi.v2.util.Tuples.Tuple2;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

public class AuthUtil {

    public static HttpHeaders createHeaders(String username, String password) {
        HttpHeaders httpHeaders = new HttpHeaders();
        final Tuple2<String, String> authHeader = authTuple(username, password);
        httpHeaders.set(authHeader.v1, authHeader.v2);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return httpHeaders;
    }

    public static Tuple2<String, String> authTuple(String username, String password) {
        final byte[] encodedAuth = Base64.getEncoder().encode((username + ":" + password).getBytes());
        return Tuples.tuple("Authorization", "Basic " + new String(encodedAuth));
    }

    public static Tuple2<String, String> jwtAuthTuple(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();

        final String baseUrl = "http://127.0.0.1/wp-json/jwt-auth/v1";
        URI uri = null;
        try {
            uri = new URI(baseUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, json.toString(), String.class);
        JSONObject obj = new JSONObject(result.getBody());
        String token=obj.getString("token");
        return Tuples.tuple("Authorization", "Bearer " + token);
    }

    public static HttpEntity<String> basicAuth(String username, String password) {
        return new HttpEntity<>(createHeaders(username, password));
    }
}
