package com.infnet.gec.app_monitoramento.visao;

import android.graphics.Color;
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
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.infnet.gec.app_monitoramento.R;
import com.infnet.gec.app_monitoramento.listener.ObterAnosDisponiveisListener;
import com.infnet.gec.app_monitoramento.listener.ObterConsumoListener;
import com.infnet.gec.app_monitoramento.model.Consumo;
import com.infnet.gec.app_monitoramento.presenter.ConsumoPresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM;

public class ConsumoActivity extends AppCompatActivity implements ObterConsumoListener, OnChartValueSelectedListener, ObterAnosDisponiveisListener, AdapterView.OnItemSelectedListener {

    public ProgressBar pbLoading;
    public ScrollView svContainer;
    public Spinner spAnos;
    public CombinedChart ccGraficoAnual;
    public LinearLayout llGraficoMes;
    public TextView tvGraficoMes;
    public CombinedChart ccGraficoMes;

    private ConsumoPresenter presenter;

    private List<Integer> anos;
    private List<Consumo> consumosAnual;
    private List<Consumo> consumosMes;

    private String medidorSelecionado;
    private Integer anoSelecionado;

    @Override
    public void onObterConsumoSuccess(List<Consumo> consumos) {
        this.consumosAnual = consumos;
        configurarGrafico(ccGraficoAnual, consumos, true);
        plotarConsumos(ccGraficoAnual, consumosAnual);
        plotarMediaConsumos(ccGraficoAnual, consumosAnual);
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
        if(e.getData() instanceof Consumo){ //Clique na barra.
            Consumo consumo = (Consumo) e.getData();
            carregarConsumoMes(consumo.getDataReferencia(), consumo.getMes());
        } else { //Clique na média.

        }
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
        ccGraficoAnual = findViewById(R.id.ccGraficoAnual);
        llGraficoMes = findViewById(R.id.llGraficoMes);
        tvGraficoMes = findViewById(R.id.tvGraficoMes);
        ccGraficoMes = findViewById(R.id.ccGraficoMes);

        medidorSelecionado = getIntent().getStringExtra("medidor");
        setTitle(medidorSelecionado);

        presenter = new ConsumoPresenter(this);
        pbLoading.setVisibility(View.VISIBLE);
        presenter.obterAnosDisponiveis(medidorSelecionado, this);
    }

    private void configurarGrafico(CombinedChart combinedChart, List<Consumo> consumos, boolean podeSelecionar) {
        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setSpaceMin(0.5f);
        xAxis.setSpaceMax(0.5f);

        combinedChart.getAxisRight().setEnabled(false);
        combinedChart.getDescription().setEnabled(false);
        combinedChart.animateY(1000);
        combinedChart.setOnChartValueSelectedListener(this);
        combinedChart.setDragEnabled(false);
        combinedChart.setTouchEnabled(podeSelecionar);

        List<String> labels = consumos.stream()
                .map(c -> c.getDataReferencia())
                .collect(Collectors.toList());
        combinedChart.getXAxis().setValueFormatter((value, axis) -> {
            if(value < 0 || value >= labels.size()) {
                return "";
            }
            return labels.get((int) value);
        });
    }

    private void plotarConsumos(CombinedChart combinedChart, List<Consumo> consumos) {
        AtomicInteger i = new AtomicInteger(0);
        List<BarEntry> values = consumos.stream()
                .map(c -> new BarEntry(i.getAndIncrement(), c.getValor(), c))
                .collect(Collectors.toList());

        BarDataSet dataSet = new BarDataSet(values, "Valor em litros");
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(Arrays.asList(dataSet));
        barData.setBarWidth(0.25f);

        CombinedData combinedData = new CombinedData();
        if(combinedChart.getData() != null) {
            combinedData = combinedChart.getData();
        }
        combinedData.setData(barData);

        combinedChart.setData(combinedData);
        combinedChart.notifyDataSetChanged();
        combinedChart.invalidate();
    }

    private void plotarMediaConsumos(CombinedChart combinedChart, List<Consumo> consumos) {
        List<Float> medias = new ArrayList<>();
        if(consumos.size() > 1) {
            medias = calcularMedias(consumos);
        }
        AtomicInteger i = new AtomicInteger(0);
        List<Entry> entries = medias.stream()
                                        .map(m -> new Entry(i.getAndIncrement(), m))
                                        .collect(Collectors.toList());

        LineDataSet dataSet = new LineDataSet(entries, "Média de consumo");
        dataSet.setColor(Color.rgb(66, 134, 244));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleColor(Color.rgb(8, 44, 102));
        dataSet.setCircleRadius(5f);
        dataSet.setFillColor(Color.rgb(8, 44, 102));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(Arrays.asList(dataSet));
        CombinedData combinedData = new CombinedData();
        if(combinedChart.getData() != null) {
            combinedData = combinedChart.getData();
        }
        combinedData.setData(lineData);

        combinedChart.setData(combinedData);
        combinedChart.notifyDataSetChanged();
        combinedChart.invalidate();
    }

    private List<Float> calcularMedias(List<Consumo> consumos) {
        List<Float> medias = new ArrayList<>();
        AtomicInteger i = new AtomicInteger(0);
        medias.add(consumos.get(0).getValor());
        consumos.forEach(c -> {
            if(i.get() > 0) {
                Float mediaAnterior = medias.get(i.get() - 1);
                Consumo consumo = consumos.get(i.get());
                medias.add((mediaAnterior + consumo.getValor()) / (i.get() + 1));
            }
            i.incrementAndGet();
        });
        return medias;
    }

    private void carregarConsumoMes(String referencia, Integer mes) {
        pbLoading.setVisibility(View.VISIBLE);
        presenter.obterConsumoMes(medidorSelecionado, mes, anoSelecionado, new ObterConsumoListener() {
            @Override
            public void onObterConsumoSuccess(List<Consumo> consumos) {
                ConsumoActivity.this.consumosMes = consumos;
                configurarGrafico(ccGraficoMes, consumos, false);
                plotarConsumos(ccGraficoMes, consumosMes);
                plotarMediaConsumos(ccGraficoMes, consumosMes);
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
