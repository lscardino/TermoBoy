package com.example.termoboy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class LogoActivity extends AppCompatActivity {

    private Button btnSkip;
    private final int CONTADOR = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

/*
        imgTiempoNuevo.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_forward_black_24dp));
        imgTiempo.setImageDrawable(imgTiempoNuevo.getDrawable()); }
*/
        btnSkip = findViewById(R.id.btn_Skip);
        btnSkip.setText(Integer.toString(CONTADOR));
        btnSkip.setBackground(getDrawable(R.drawable.ic_arrow_forward_black_24dp));


        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSkip.setEnabled(false);
                siguentePantalla();
            }

            @Override
            protected void finalize() throws Throwable {
                btnSkip.setEnabled(true);
                super.finalize();
            }
        });

        new ThreadContador().start();
    }

    private FirebaseAuth mAuth;
    private void siguentePantalla(){

        mAuth = FirebaseAuth.getInstance();
        Intent sigPantalla;
        if (mAuth.getCurrentUser() != null){
            //Iniciar la actividá
            Log.d("DATOS","Usuario existe already");
            sigPantalla = new Intent(getApplicationContext(), MainActivity.class);
        }else{
            sigPantalla = new Intent(this, Bienvenido_Registro.class);
        }
        startActivity(sigPantalla);
        finish();
    }

    class ThreadContador extends Thread {
        @Override
        public void run() {
            for (int res = CONTADOR;  res > 0; res--){
                final int resPost = res;
                btnSkip.post(new Runnable() {
                    @Override
                    public void run() {
                        btnSkip.setText(String.valueOf(resPost));
                    }
                });
                try {
                    Thread.sleep(900);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            siguentePantalla();
        }
    }
}
