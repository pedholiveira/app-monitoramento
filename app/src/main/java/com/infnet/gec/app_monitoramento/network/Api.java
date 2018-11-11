package com.infnet.gec.app_monitoramento.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    private static Api instance;
    private Endpoints endpoints;

    private Api() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-monitoramento.herokuapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        endpoints = retrofit.create(Endpoints.class);
    }

    public static Api getInstance() {
        if(instance == null) {
            instance = new Api();
        }
        return instance;
    }

    public Endpoints getEndpoints() {
        return endpoints;
    }
}
