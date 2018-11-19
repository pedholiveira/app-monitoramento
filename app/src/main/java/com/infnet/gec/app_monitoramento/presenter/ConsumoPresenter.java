package com.infnet.gec.app_monitoramento.presenter;

import android.content.Context;

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

    public void obterConsumo(String medidor, ObterConsumoListener listener) {
        Api.getInstance().getEndpoints().obterConsumos(medidor).enqueue(new Callback<List<Consumo>>() {
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
