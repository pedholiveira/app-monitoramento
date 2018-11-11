package com.infnet.gec.app_monitoramento.listener;

import java.util.List;

public interface ObterMedidoresListener {

    void onObterMedidoresSuccess(List<String> medidores);

    void onObterMedidoresFailure();
}
