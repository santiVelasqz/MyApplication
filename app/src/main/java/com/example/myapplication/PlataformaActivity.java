package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlataformaActivity extends AppCompatActivity implements PlataformaAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private PlataformaAdapter adapter;
    private List<Pelicula> peliculas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        recyclerView = findViewById(R.id.listaNetflix);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String plataforma = getIntent().getStringExtra("plataforma");
        String tipoEstreno = getIntent().getStringExtra("tipoEstreno");

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
        System.out.println("Valor de estreno: " + pelicula.getEstreno());
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
        intent.putExtra("estreno", pelicula.getEstreno().toDate().toString());
        startActivity(intent);
    }

    public void atras (View view){
        finish();
    }
}
