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

public class MedidorPresenter extends BasePresenter {

    public MedidorPresenter(Context context) {
        super(context);
    }

    public void obterMedidores(final ObterMedidoresListener listener) {
        Api.getInstance().getEndpoints().obterMedidores().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                listener.onObterMedidoresSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                listener.onObterMedidoresFailure();
            }
        });
    }
}
