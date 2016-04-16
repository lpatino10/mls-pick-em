package com.example.loganpatino.mlspickem;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Converter.Factory;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by loganpatino on 4/16/16.
 */
public class RestClient {

    private static GameInterface gameInterface;
    private static String baseUrl = "http://mls-schedule-2016.co.nf/";

    public static GameInterface getClient() {
        if (gameInterface == null) {
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            gameInterface = client.create(GameInterface.class);
        }

        return gameInterface;
    }

    public interface GameInterface {
        @GET("/MLS_Schedule_2016.json")
        Call<CallResult> getSchedule();
    }

}
