package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PeliculaDetalleActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    Button btnCerrarsesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelicula);
        btnCerrarsesion = findViewById(R.id.btn_cerrarsesion);

        // Inicializar sessionManager
        sessionManager = new SessionManager(this);

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        String estreno = intent.getStringExtra("estreno");
        String descripcion = intent.getStringExtra("descripcion");
        String genero = intent.getStringExtra("genero");
        String director = intent.getStringExtra("director");
        String fotoUrl = intent.getStringExtra("fotoUrl");
        String tipo = intent.getStringExtra("tipo");
        String plataforma = intent.getStringExtra("plataforma");
        String push = intent.getStringExtra("push");
        final String trailerUrl = intent.getStringExtra("trailerUrl");

        // Referencias de vistas
        ImageView imageViewFoto = findViewById(R.id.imageViewFoto);
        TextView textViewNombre = findViewById(R.id.textViewNombre);
        TextView textViewEstreno = findViewById(R.id.textViewEstreno);
        TextView textViewDescripcion = findViewById(R.id.textViewDescripcion);
        TextView textViewGeneroDirector = findViewById(R.id.textViewGeneroDirector);
        Button buttonTrailer = findViewById(R.id.buttonTrailer);

        // Mostrar datos
        textViewNombre.setText(nombre);
        textViewEstreno.setText("Estreno: " + estreno);
        textViewDescripcion.setText("Descripción: " + descripcion);
        textViewGeneroDirector.setText("Género: " + genero + "   || Director: " + director);

        // Cargar imagen de la película
        Picasso.get().load(fotoUrl).into(imageViewFoto);

        // Abrir el trailer de la película en YouTube
        buttonTrailer.setOnClickListener(v -> {
            Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
            startActivity(trailerIntent);
        });
        btnCerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarsesion();
            }
        });
    }


    public void cerrarsesion (){
        sessionManager.cerrarSesion();

        // Redirigir al LoginActivity
        Intent intent = new Intent(PeliculaDetalleActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finalizar la actividad actual para que el usuario no pueda volver atrás
    }

    public void atras (View view){
        finish();
    }

    public void principal(View view){

        Intent intent = new Intent(PeliculaDetalleActivity.this, SelecEstreno.class);
        startActivity(intent);
        finish();
    }
}
