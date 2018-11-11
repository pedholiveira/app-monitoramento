package com.infnet.gec.app_monitoramento.presenter;

import android.content.Context;

import com.infnet.gec.app_monitoramento.listener.ObterMedidoresListener;
import com.infnet.gec.app_monitoramento.model.Consumo;
import com.infnet.gec.app_monitoramento.network.Api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedidorPresenter {

    private Context context;

    public MedidorPresenter(Context context) {
        this.context = context;
    }

    public void obterMedidores(final ObterMedidoresListener listener) {
        Api.getInstance().getEndpoints().obterConsumos().enqueue(new Callback<List<Consumo>>() {
            @Override
            public void onResponse(Call<List<Consumo>> call, Response<List<Consumo>> response) {
                List<Consumo> consumos = response.body();
                //TODO - Salvar consumos.
                List<String> medidores = consumos.parallelStream()
                                                    .map(c -> c.getNome())
                                                    .distinct()
                                                    .collect(Collectors.toList());
                listener.onObterMedidoresSuccess(medidores);
            }

            @Override
            public void onFailure(Call<List<Consumo>> call, Throwable t) {
                listener.onObterMedidoresFailure();
            }
        });
    }
}
