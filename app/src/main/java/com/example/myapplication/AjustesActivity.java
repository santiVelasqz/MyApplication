package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AjustesActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        sessionManager = new SessionManager(this);

        Button cambiarcontraseña = findViewById(R.id.btn_cambiarcontraseña);
        Button cerrarsesion = findViewById(R.id.btn_cerrarsesion);
        Button gestionnotis = findViewById(R.id.btn_gestionnoti);
        Button eliminarcuenta = findViewById(R.id.btn_cuenta);

        cerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarsesion();
            }
        });
    }


    public void cerrarsesion (){
        sessionManager.cerrarSesion();

        // Redirigir al LoginActivity
        Intent intent = new Intent(AjustesActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finalizar la actividad actual para que el usuario no pueda volver atrás
    }
    public void atras (View view){
        finish();
    }
}