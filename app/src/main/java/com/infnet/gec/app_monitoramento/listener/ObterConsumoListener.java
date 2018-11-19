package com.infnet.gec.app_monitoramento.listener;

import com.infnet.gec.app_monitoramento.model.Consumo;

import java.util.List;

public interface ObterConsumoListener {
    void onObterConsumoSuccess(List<Consumo> consumos);

    void onObterConsumoFailure();
}
