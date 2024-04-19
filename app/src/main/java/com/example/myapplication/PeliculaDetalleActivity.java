package com.example.myapplication;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import com.squareup.picasso.Picasso;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PeliculaDetalleActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private YouTubePlayerView youTubePlayerView;
    Button btn_ajustes;
    Button notificacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelicula);
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);
        btn_ajustes = findViewById(R.id.btn_ajustes);
        notificacion = findViewById(R.id.btn_notificacion);


        // Inicializar sessionManager
        sessionManager = new SessionManager(this);
        //SE RECOGEN LOS DATOS QUE SE HAN ENVIADO DE OTRA ACTIVITY
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
        String tipoEstreno = getIntent().getStringExtra("tipoEstreno");
        final String trailerUrl = intent.getStringExtra("trailerUrl");

        // Referencias de vistas
        ImageView imageViewFoto = findViewById(R.id.imageViewFoto);
        TextView textViewNombre = findViewById(R.id.textViewNombre);
        TextView textViewEstreno = findViewById(R.id.textViewEstreno);
        TextView textViewDescripcion = findViewById(R.id.textViewDescripcion);
        TextView textViewGeneroDirector = findViewById(R.id.textViewGeneroDirector);

        // Mostrar datos
        textViewNombre.setText(nombre);
        textViewEstreno.setText("Estreno: " + estreno);
        textViewDescripcion.setText("Descripción: " + descripcion);
        textViewGeneroDirector.setText("Género: " + genero + "   || Director: " + director);

        // Cargar imagen de la película
        Picasso.get().load(fotoUrl).into(imageViewFoto);

        // Cargar video de YouTube
        String trailerID = obtenerIdDeUrl(trailerUrl);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                if (trailerID != null) {
                    youTubePlayer.loadVideo(trailerID, 0);
                }
            }
        });
        //ESTE BOTON ENVIA AL USUARIO A UNA ACTIVITY DONDE HAY OPCIONES DE AJUSTES DE LA CUENTA
        btn_ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PeliculaDetalleActivity.this, AjustesActivity.class);
                startActivity(intent);
            }
        });

        if ("proximos".equals(tipoEstreno)) {
            notificacion.setVisibility(View.VISIBLE);
        } else {
            notificacion.setVisibility(View.GONE);
        }

        notificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarSuscripciones(nombre, push);
                // Suscribir al usuario al tema correspondiente
                FirebaseMessaging.getInstance().subscribeToTopic(push)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "Genial! Te avisáremos.";
                                if (!task.isSuccessful()) {
                                    msg = "Algo ha ido mal, inténtalo mas tarde.";
                                }
                                Log.d(TAG, msg);
                                Toast.makeText(PeliculaDetalleActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
    //AQUI SE OBTIENE LA URL DEL VIDEO PARA QUE SE VISUALICE DIRECTAMENTE EN LA APP
    public String obtenerIdDeUrl(String url) {
        String id = null;
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url); // URL de YouTube

        if (matcher.find()) {
            id = matcher.group();
        }
        return id;
    }
    private void guardarSuscripciones(String nombre, String push) {
        Context context = getApplicationContext();
        String contenido = nombre + ":" + push + "\n";

        try {
            // Abrir el archivo notificaciones.txt en modo append (para añadir al final)
            FileWriter fileWriter = new FileWriter(context.getFilesDir() + "/" + "notificaciones.txt", true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(contenido);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void atras(View view) {
        finish();
    }

    public void principal(View view) {
        Intent intent = new Intent(PeliculaDetalleActivity.this, SelecEstreno.class);
        startActivity(intent);
        finish();
    }
}

