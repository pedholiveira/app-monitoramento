package com.infnet.gec.app_monitoramento.presenter;

import android.content.Context;

import com.infnet.gec.app_monitoramento.listener.ObterAnosDisponiveisListener;
import com.infnet.gec.app_monitoramento.listener.ObterConsumoListener;
import com.infnet.gec.app_monitoramento.model.Consumo;
import com.infnet.gec.app_monitoramento.network.Api;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsumoPresenter extends BasePresenter {

    public ConsumoPresenter(Context context) {
        super(context);
    }

    public void obterAnosDisponiveis(String medidor, ObterAnosDisponiveisListener listener) {
        Api.getInstance().getEndpoints().obterAnosDisponiveis(medidor).enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                listener.onObterAnosDisponiveisSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                listener.onObterAnosDisponiveisFailure();
            }
        });
    }

    public void obterConsumo(String medidor, Integer ano, ObterConsumoListener listener) {
        Api.getInstance().getEndpoints().obterConsumos(medidor, ano).enqueue(new Callback<List<Consumo>>() {
            @Override
            public void onResponse(Call<List<Consumo>> call, Response<List<Consumo>> response) {
                listener.onObterConsumoSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<Consumo>> call, Throwable t) {
                listener.onObterConsumoFailure();
            }
        });
    }

    public void obterConsumoMes(String medidor, int mes, int ano, ObterConsumoListener listener) {
        Api.getInstance().getEndpoints().obterConsumosPorMes(medidor, mes, ano).enqueue(new Callback<List<Consumo>>() {
            @Override
            public void onResponse(Call<List<Consumo>> call, Response<List<Consumo>> response) {
                listener.onObterConsumoSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<Consumo>> call, Throwable t) {
                listener.onObterConsumoFailure();
            }
        });
    }
}
