package com.infnet.gec.app_monitoramento.listener;

import java.util.List;

public interface ObterAnosDisponiveisListener {
    void onObterAnosDisponiveisSuccess(List<Integer> anos);

    void onObterAnosDisponiveisFailure();
}
