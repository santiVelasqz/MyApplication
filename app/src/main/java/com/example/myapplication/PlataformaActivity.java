package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlataformaActivity extends AppCompatActivity implements PlataformaAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private PlataformaAdapter adapter;
    private List<Pelicula> peliculas;
    private SessionManager sessionManager;
    Button btnCerrarsesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        // Inicialización del botón dentro de onCreate()
        btnCerrarsesion = findViewById(R.id.btn_cerrarsesion);


        // Inicializar sessionManager
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.listaNetflix);
        TextView tvestreno = findViewById(R.id.tv_testreno);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String plataforma = getIntent().getStringExtra("plataforma");
        String tipoEstreno = getIntent().getStringExtra("tipoEstreno");

        if (tipoEstreno.equals("estrenados")) {
            tvestreno.setText("Estrenadas");
        } else if (tipoEstreno.equals("proximos")) {
            tvestreno.setText("Proximos estrenos");
        } else {
            tvestreno.setText("");
        }

        peliculas = new ArrayList<>(); // Corregido a Pelicula

        // Obtener referencia a la colección de películas en Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Peliculas")
                .whereEqualTo("Plataforma", plataforma) // Filtrar por plataforma seleccionada
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Obtener datos de la película
                                String nombre = document.getString("Nombre");
                                String descripcion = document.getString("Descripcion");
                                String fotoUrl = document.getString("Foto");
                                String trailerUrl = document.getString("Trailer");
                                Timestamp estrenoTimestamp = document.getTimestamp("Estreno");
                                String genero = document.getString("Genero");
                                String tipo = document.getString("Tipo");
                                String push = document.getString("push");
                                String director = document.getString("Director");

                                System.out.println("Valor de estreno: " + estrenoTimestamp); //


                                // Agregar la película a la lista solo si cumple con el tipo de estreno
                                if (cumpleTipoEstreno(estrenoTimestamp, tipoEstreno)) {
                                    peliculas.add(new Pelicula(nombre, descripcion, fotoUrl, trailerUrl, plataforma, estrenoTimestamp, genero, tipo, push, director));
                                }
                            }

                            // Configurar el adaptador con las películas obtenidas
                            adapter = new PlataformaAdapter(PlataformaActivity.this, peliculas);
                            adapter.setOnItemClickListener(PlataformaActivity.this);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.d("PlataformaActivity", "Error getting documents: ", task.getException());
                            Toast.makeText(PlataformaActivity.this, "Error al obtener películas", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        btnCerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarsesion();
            }
        });
    }



    // Método para verificar si la película cumple con el tipo de estreno (estrenos pasados o próximos)
    private boolean cumpleTipoEstreno(Timestamp estrenoTimestamp, String tipoEstreno) {
        Date fechaActual = Calendar.getInstance().getTime();
        Date fechaEstreno = estrenoTimestamp.toDate();

        // Comparar las fechas
        if (tipoEstreno.equals("estrenados")) {
            // Si la fecha de estreno es anterior a la fecha actual, la película ya se estrenó
            return fechaEstreno.before(fechaActual);
        } else if (tipoEstreno.equals("proximos")) {
            // Si la fecha de estreno es posterior o igual a la fecha actual, la película es próxima
            return fechaEstreno.after(fechaActual) || fechaEstreno.equals(fechaActual);
        }

        return true;
    }

    @Override
    public void onItemClick(int position) {
        Pelicula pelicula = peliculas.get(position);
        Intent intent = new Intent(this, PeliculaDetalleActivity.class);
        intent.putExtra("nombre", pelicula.getNombre());
        intent.putExtra("descripcion", pelicula.getDescripcion());
        intent.putExtra("fotoUrl", pelicula.getFotoUrl());
        intent.putExtra("trailerUrl", pelicula.getTrailerUrl());
        intent.putExtra("genero", pelicula.getGenero());
        intent.putExtra("director", pelicula.getDirector());
        intent.putExtra("push", pelicula.getPush());
        intent.putExtra("plataforma", pelicula.getPlataforma());
        intent.putExtra("tipo", pelicula.getTipo());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaFormateada = dateFormat.format(pelicula.getEstreno().toDate());
        intent.putExtra("estreno", fechaFormateada);
        startActivity(intent);
    }


    public void cerrarsesion (){
        sessionManager.cerrarSesion();

        // Redirigir al LoginActivity
        Intent intent = new Intent(PlataformaActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finalizar la actividad actual para que el usuario no pueda volver atrás
    }

    public void atras (View view){
        finish();
    }

    public void principal(View view){

        Intent intent = new Intent(PlataformaActivity.this, SelecEstreno.class);
        startActivity(intent);
        finish();
    }
}
