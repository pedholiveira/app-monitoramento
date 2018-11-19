package com.infnet.gec.app_monitoramento.visao;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.infnet.gec.app_monitoramento.R;
import com.infnet.gec.app_monitoramento.listener.ObterMedidoresListener;
import com.infnet.gec.app_monitoramento.model.Consumo;
import com.infnet.gec.app_monitoramento.network.Api;
import com.infnet.gec.app_monitoramento.presenter.MedidorPresenter;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedidoresActivity extends AppCompatActivity implements ObterMedidoresListener {

    public ProgressBar pbLoading;

    private MedidorPresenter presenter;

    private List<String> medidores;
    private ListView lvMedidores;

    @Override
    public void onObterMedidoresSuccess(List<String> medidores) {
        this.medidores = medidores;
        lvMedidores.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, medidores));
        pbLoading.setVisibility(View.GONE);
    }

    @Override
    public void onObterMedidoresFailure() {
        Toast.makeText(this, "Falha ao obter medidores.", Toast.LENGTH_SHORT).show();
        pbLoading.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medidores);

        pbLoading = findViewById(R.id.pbLoading);
        carregarListViewMedidores();

        presenter = new MedidorPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarMedidores();
    }

    private void carregarListViewMedidores() {
        lvMedidores = findViewById(R.id.lvMedidores);
        lvMedidores.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(this, ConsumoActivity.class);
            intent.putExtra("medidor", medidores.get(i));
            startActivity(intent);
        });
    }

    private void carregarMedidores() {
        pbLoading.setVisibility(View.VISIBLE);
        presenter.obterMedidores(this);
    }
}
