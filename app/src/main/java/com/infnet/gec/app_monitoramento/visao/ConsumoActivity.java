package com.infnet.gec.app_monitoramento.visao;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.infnet.gec.app_monitoramento.R;

import java.util.ArrayList;
import java.util.List;

public class ConsumoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumo);

        int[] consumo = getIntent().getIntArrayExtra("consumo");

        LineChart chart = findViewById(R.id.chart);
        List<Entry> chartEntries = new ArrayList<>();

        for (int valor : consumo) {
            chartEntries.add(new Entry(valor, valor));
        }

        LineDataSet dataSet = new LineDataSet(chartEntries, "Label");

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }
}
