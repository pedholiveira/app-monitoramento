package com.infnet.gec.app_monitoramento.visao;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.infnet.gec.app_monitoramento.R;
import com.infnet.gec.app_monitoramento.listener.ObterConsumoListener;
import com.infnet.gec.app_monitoramento.model.Consumo;
import com.infnet.gec.app_monitoramento.presenter.ConsumoPresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM;

public class ConsumoActivity extends AppCompatActivity implements ObterConsumoListener {

    public BarChart barChart;

    private ConsumoPresenter presenter;
    private String medidor;

    @Override
    public void onObterConsumoSuccess(List<Consumo> consumos) {
        configurarGrafico();
        plotarValores(consumos);
    }

    @Override
    public void onObterConsumoFailure() {
        Toast.makeText(this, "Falha ao obter consumos.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumo);

        barChart = findViewById(R.id.chart);

        medidor = getIntent().getStringExtra("medidor");
        setTitle(medidor);

        presenter = new ConsumoPresenter(this);
        presenter.obterConsumo(medidor, this);
    }

    private void configurarGrafico() {
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(1500);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
    }

    private void plotarValores(List<Consumo> consumos) {
        AtomicInteger i = new AtomicInteger(0);
        List<BarEntry> values = consumos.stream()
                .map(c -> new BarEntry(i.getAndIncrement(), c.getValor()))
                .collect(Collectors.toList());

        BarDataSet dataSet = new BarDataSet(values, "Valores em m³");
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        List<String> labels = consumos.stream()
                .map(c -> c.getData())
                .collect(Collectors.toList());
        barChart.getXAxis().setValueFormatter((value, axis) -> labels.get((int) value));
        barChart.setData(new BarData(Arrays.asList(dataSet)));
        barChart.invalidate();
    }
}
