package com.infnet.gec.app_monitoramento.network;

import com.infnet.gec.app_monitoramento.model.Consumo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Endpoints {
    @GET("medidores")
    Call<List<String>> obterMedidores();

    @POST("consumos/{medidor}/{ano}")
    Call<List<Consumo>> obterConsumos(@Path("medidor") String medidor, @Path("ano") Integer ano);

    @POST("consumosMensal/{medidor}/{mes}/{ano}")
    Call<List<Consumo>> obterConsumosPorMes(@Path("medidor") String medidor, @Path("mes") int mes, @Path("ano") int ano);

    @GET("anosDisponiveis/{medidor}")
    Call<List<Integer>> obterAnosDisponiveis(@Path("medidor") String medidor);
}
