package com.infnet.gec.app_monitoramento.network;

import com.infnet.gec.app_monitoramento.model.Consumo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Endpoints {
    @GET("consumo")
    Call<List<Consumo>> obterConsumos();
}
