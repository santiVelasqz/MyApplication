package com.example.myapplication;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
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
    private boolean isNotificacionEnabled = true;
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

        verificarSuscripcion(nombre, push);


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
                if (isNotificacionEnabled) {
                    // Si el usuario está suscrito, desuscribirlo
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(push)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg;
                                    if (task.isSuccessful()) {
                                        msg = "Te has desuscrito.";
                                        // Cambiar color y texto del botón
                                        notificacion.setBackgroundColor(Color.TRANSPARENT); // Cambiar de nuevo a transparente
                                        ((Button) v).setText("¡Avísame!"); // Cambiar texto a "¡Avísame!"
                                        eliminarSuscripcion(push);
                                        isNotificacionEnabled = false; // El usuario ya no está suscrito
                                        Toast.makeText(PeliculaDetalleActivity.this, msg, Toast.LENGTH_SHORT).show();

                                    } else {
                                        msg = "Algo ha ido mal, inténtalo más tarde.";
                                    }
                                }
                            });
                } else {
                    // Si el usuario no está suscrito, suscribirlo
                    FirebaseMessaging.getInstance().subscribeToTopic(push)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @SuppressLint("RestrictedApi")
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg;
                                    if (task.isSuccessful()) {
                                        msg = "Genial! Te avisaremos.";
                                        // Cambiar color y texto del botón
                                        notificacion.setBackgroundColor(Color.parseColor("#FFA500")); // Cambiar color a naranja
                                        ((Button) v).setText("Quitar aviso"); // Cambiar texto a "Quitar aviso"
                                        isNotificacionEnabled = true; // El usuario está suscrito ahora
                                        guardarSuscripciones(nombre, push); // Guardar la suscripción en el archivo
                                    } else {
                                        msg = "Algo ha ido mal, inténtalo mas tarde.";
                                    }
                                    Log.d(TAG, msg);
                                    Toast.makeText(PeliculaDetalleActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
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

    private void verificarSuscripcion(String nombre, String push) {
        Context context = getApplicationContext();
        String contenidoBusqueda = nombre + ":" + push;

        try {
            // Abrir el archivo notificaciones.txt para buscar la suscripción
            File file = new File(context.getFilesDir() + "/" + "notificaciones.txt");
            Scanner scanner = new Scanner(file);

            // Buscar la suscripción en el archivo
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(contenidoBusqueda)) {
                    notificacion.setBackgroundColor(Color.parseColor("#FFA500"));
                    notificacion.setText("Quitar aviso");
                    isNotificacionEnabled = true;
                    scanner.close();
                    return;
                }
            }
            notificacion.setBackgroundColor(Color.TRANSPARENT);
            notificacion.setText("¡Avísame!");
            isNotificacionEnabled = false;

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void eliminarSuscripcion(String tema) {
        try {
            // Obtener la ruta del archivo notificaciones.txt
            String filePath = getApplicationContext().getFilesDir() + "/" + "notificaciones.txt";
            File file = new File(filePath);
            // Verificar si el archivo existe
            if (file.exists()) {
                // Leer el contenido del archivo y eliminar la línea correspondiente al tema
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    // Si la línea no contiene el tema, la añadimos al nuevo contenido
                    if (!line.startsWith(tema + ":")) {
                        stringBuilder.append(line).append("\n");
                    }
                }
                reader.close();

                // Escribir el nuevo contenido al archivo
                FileWriter writer = new FileWriter(file);
                writer.write(stringBuilder.toString());
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

