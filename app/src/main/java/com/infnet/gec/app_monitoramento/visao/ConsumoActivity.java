package com.infnet.gec.app_monitoramento.visao;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.infnet.gec.app_monitoramento.R;
import com.infnet.gec.app_monitoramento.listener.ObterAnosDisponiveisListener;
import com.infnet.gec.app_monitoramento.listener.ObterConsumoListener;
import com.infnet.gec.app_monitoramento.model.Consumo;
import com.infnet.gec.app_monitoramento.presenter.ConsumoPresenter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM;

public class ConsumoActivity extends AppCompatActivity implements ObterConsumoListener, OnChartValueSelectedListener, ObterAnosDisponiveisListener, AdapterView.OnItemSelectedListener {

    public ProgressBar pbLoading;
    public ScrollView svContainer;
    public Spinner spAnos;
    public BarChart bcGraficoAnual;
    public LinearLayout llGraficoMes;
    public TextView tvGraficoMes;
    public BarChart bcGraficoMes;

    private ConsumoPresenter presenter;

    private List<Integer> anos;
    private List<Consumo> consumosAnual;
    private List<Consumo> consumosMes;

    private String medidorSelecionado;
    private Integer anoSelecionado;

    @Override
    public void onObterConsumoSuccess(List<Consumo> consumos) {
        this.consumosAnual = consumos;
        configurarGrafico(bcGraficoAnual, true);
        plotarValores(bcGraficoAnual, consumosAnual);
        pbLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onObterConsumoFailure() {
        Toast.makeText(this, "Falha ao obter consumos.", Toast.LENGTH_SHORT).show();
        pbLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onObterAnosDisponiveisSuccess(List<Integer> anos) {
        this.anos = anos;
        spAnos.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, anos));
        spAnos.setOnItemSelectedListener(this);
    }

    @Override
    public void onObterAnosDisponiveisFailure() {
        Toast.makeText(this, "Falha ao obter consumos.", Toast.LENGTH_SHORT).show();
        pbLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        anoSelecionado = anos.get(i);
        llGraficoMes.setVisibility(View.INVISIBLE);
        presenter.obterConsumo(medidorSelecionado, anoSelecionado, this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //nada a fazer
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Consumo consumo = consumosAnual.get((int) e.getX());
        carregarConsumoMes(consumo.getDataReferencia(), consumo.getMes());
    }

    @Override
    public void onNothingSelected() {
        svContainer.fullScroll(View.FOCUS_UP);
        llGraficoMes.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumo);

        pbLoading = findViewById(R.id.pbLoading);
        svContainer = findViewById(R.id.svContainer);
        spAnos = findViewById(R.id.spAnos);
        bcGraficoAnual = findViewById(R.id.bcGraficoAnual);
        llGraficoMes = findViewById(R.id.llGraficoMes);
        tvGraficoMes = findViewById(R.id.tvGraficoMes);
        bcGraficoMes = findViewById(R.id.bcGraficoMes);

        medidorSelecionado = getIntent().getStringExtra("medidor");
        setTitle(medidorSelecionado);

        presenter = new ConsumoPresenter(this);
        pbLoading.setVisibility(View.VISIBLE);
        presenter.obterAnosDisponiveis(medidorSelecionado, this);
    }

    private void configurarGrafico(BarChart barChart, boolean podeSelecionar) {
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.setFitBars(true);
        barChart.setOnChartValueSelectedListener(this);
        barChart.setDragEnabled(false);
        barChart.setTouchEnabled(podeSelecionar);
    }

    private void plotarValores(BarChart barChart, List<Consumo> consumos) {
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
        barChart.getXAxis().setValueFormatter((value, axis) -> labels.get((int) value));
        barChart.setData(new BarData(Arrays.asList(dataSet)));
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

    private void carregarConsumoMes(String referencia, Integer mes) {
        pbLoading.setVisibility(View.VISIBLE);
        presenter.obterConsumoMes(medidorSelecionado, mes, anoSelecionado, new ObterConsumoListener() {
            @Override
            public void onObterConsumoSuccess(List<Consumo> consumos) {
                ConsumoActivity.this.consumosMes = consumos;
                configurarGrafico(bcGraficoMes, false);
                plotarValores(bcGraficoMes, consumosMes);
                pbLoading.setVisibility(View.INVISIBLE);

                llGraficoMes.setVisibility(View.VISIBLE);
                tvGraficoMes.setText("Consumo de " + referencia);
                svContainer.fullScroll(View.FOCUS_DOWN);
            }

            @Override
            public void onObterConsumoFailure() {
                ConsumoActivity.this.onObterConsumoFailure();
            }
        });
    }
}
