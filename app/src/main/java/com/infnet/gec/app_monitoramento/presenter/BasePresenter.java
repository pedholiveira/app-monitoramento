package com.infnet.gec.app_monitoramento.presenter;

import android.content.Context;

public abstract class BasePresenter {
    protected Context context;

    public BasePresenter(Context context) {
        this.context = context;
    }
}
