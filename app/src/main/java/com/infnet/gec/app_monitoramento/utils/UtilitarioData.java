package com.infnet.gec.app_monitoramento.utils;

import java.util.Calendar;

public class UtilitarioData {

    public static int obterAnoCorrente() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
}
