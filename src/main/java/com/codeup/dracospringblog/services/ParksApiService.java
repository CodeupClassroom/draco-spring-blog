package com.codeup.dracospringblog.services;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ParksApiService {

    @Value("${parks-api-key}")
    private String API_KEY;

    public String fetchData() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://developer.nps.gov/api/v1/alerts?parkCode=acad,dena")
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Api-Key", API_KEY)
                .build();

        Response response;

        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
