package com.infnet.gec.app_monitoramento.visao;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.infnet.gec.app_monitoramento.R;
import com.infnet.gec.app_monitoramento.listener.ObterAnosDisponiveisListener;
import com.infnet.gec.app_monitoramento.listener.ObterConsumoListener;
import com.infnet.gec.app_monitoramento.model.Consumo;
import com.infnet.gec.app_monitoramento.presenter.ConsumoPresenter;
import com.infnet.gec.app_monitoramento.utils.UtilitarioData;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM;

public class ConsumoActivity extends AppCompatActivity implements ObterConsumoListener, OnChartValueSelectedListener, ObterAnosDisponiveisListener, AdapterView.OnItemSelectedListener {

    public ProgressBar pbLoading;
    public Spinner spAnos;
    public BarChart barChart;

    private ConsumoPresenter presenter;
    private String medidor;

    private List<Integer> anos;
    private List<Consumo> consumos;

    @Override
    public void onObterConsumoSuccess(List<Consumo> consumos) {
        this.consumos = consumos;
        plotarValores();
        pbLoading.setVisibility(View.GONE);
    }

    @Override
    public void onObterConsumoFailure() {
        Toast.makeText(this, "Falha ao obter consumos.", Toast.LENGTH_SHORT).show();
        pbLoading.setVisibility(View.GONE);
    }

    @Override
    public void onObterAnosDisponiveisSuccess(List<Integer> anos) {
        this.anos = anos;
        spAnos.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, anos));
        spAnos.setOnItemSelectedListener(this);
        //presenter.obterConsumo(medidor, UtilitarioData.obterAnoCorrente(), this);
    }

    @Override
    public void onObterAnosDisponiveisFailure() {
        Toast.makeText(this, "Falha ao obter consumos.", Toast.LENGTH_SHORT).show();
        pbLoading.setVisibility(View.GONE);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        presenter.obterConsumo(medidor, anos.get(i), this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //nada a fazer
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Consumo consumo = consumos.get((int) e.getX());

        //TODO - Ampliar consumo diário para o mês selecionado.
        Toast.makeText(this, "Consumo do mês " + (consumo.getMes() + 1), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {
        //nada a fazer
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumo);

        pbLoading = findViewById(R.id.pbLoading);
        spAnos = findViewById(R.id.spAnos);
        barChart = findViewById(R.id.chart);

        medidor = getIntent().getStringExtra("medidor");
        setTitle(medidor);

        presenter = new ConsumoPresenter(this);
        pbLoading.setVisibility(View.VISIBLE);
        presenter.obterAnosDisponiveis(medidor, this);

        configurarGrafico();
    }

    private void configurarGrafico() {
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.setOnChartValueSelectedListener(this);
    }

    private void plotarValores() {
        AtomicInteger i = new AtomicInteger(0);
        List<BarEntry> values = consumos.stream()
                .map(c -> new BarEntry(i.getAndIncrement(), c.getValor()))
                .collect(Collectors.toList());

        BarDataSet dataSet = new BarDataSet(values, "Valor em litros");
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        List<String> labels = consumos.stream()
                .map(c -> c.getDataReferencia())
                .collect(Collectors.toList());

        barChart.getXAxis().mEntries = new float[]{};
        barChart.animateY(1500);
        barChart.getXAxis().setValueFormatter((value, axis) -> labels.get((int) value));
        barChart.setData(new BarData(Arrays.asList(dataSet)));
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }
}
