package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelecEstreno extends AppCompatActivity {
    private SessionManager sessionManager;
    Button btnCerrarsesion; // Declaración del botón

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selec_estreno);

        // Inicialización del botón dentro de onCreate()
        btnCerrarsesion = findViewById(R.id.btn_cerrarsesion);

        // Inicializar sessionManager
        sessionManager = new SessionManager(this);

        Button btnestrenados = findViewById(R.id.btn_estrenados);
        Button btnproximos = findViewById(R.id.btn_proximos);

        btnestrenados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a SelecPlataforma para ver estrenos pasados
                Intent intent = new Intent(SelecEstreno.this, SelecPlataforma.class);
                intent.putExtra("tipoEstreno", "estrenados");
                startActivity(intent);
            }
        });

        btnproximos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a SelecPlataforma para ver próximos estrenos
                Intent intent = new Intent(SelecEstreno.this, SelecPlataforma.class);
                intent.putExtra("tipoEstreno", "proximos");
                startActivity(intent);
            }
        });

        btnCerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarsesion();
            }
        });
    }

    // Método para cerrar sesión y redirigir al LoginActivity
    public void cerrarsesion (){
        sessionManager.cerrarSesion();

        // Redirigir al LoginActivity
        Intent intent = new Intent(SelecEstreno.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finalizar la actividad actual para que el usuario no pueda volver atrás
    }

    public void atras (View view){
        finish();
    }
}

