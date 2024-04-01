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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelicula);

        // Obtener datos de la película pasados desde el intent
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

        // Obtener referencias de vistas
        ImageView imageViewFoto = findViewById(R.id.imageViewFoto);
        TextView textViewNombre = findViewById(R.id.textViewNombre);
        TextView textViewEstreno = findViewById(R.id.textViewEstreno);
        TextView textViewDescripcion = findViewById(R.id.textViewDescripcion);
        TextView textViewGeneroDirector = findViewById(R.id.textViewGeneroDirector);
        Button buttonTrailer = findViewById(R.id.buttonTrailer);

        // Mostrar datos en las vistas
        textViewNombre.setText(nombre);

        if (estreno != null && !estreno.isEmpty()) {
            // Cortar el String para obtener solo el día de la semana, el mes y el día del mes
            String fechaCortada = estreno.substring(0, 10);

            // Mostrar la fecha cortada en el TextView
            textViewEstreno.setText("Estreno: " + fechaCortada);
        } else {
            textViewEstreno.setText("Estreno: Desconocido");
        }


        System.out.println("Valor de estreno: " + estreno); //

        textViewDescripcion.setText("Descripción: " + descripcion);
        textViewGeneroDirector.setText("Género: " + genero + ", Director: " + director);

        // Cargar imagen de la película usando Picasso
        Picasso.get().load(fotoUrl).into(imageViewFoto);

        // Abrir el trailer de la película en YouTube al hacer clic en el botón
        buttonTrailer.setOnClickListener(v -> {
            Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
            startActivity(trailerIntent);
        });
    }

    public void atras (View view){
        finish();
    }
}
