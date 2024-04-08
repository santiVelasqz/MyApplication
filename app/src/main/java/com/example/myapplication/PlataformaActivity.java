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
    String estrenoFormateado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        btnCerrarsesion = findViewById(R.id.btn_cerrarsesion);
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
            tvestreno.setText("Próximos estrenos");
        } else if (tipoEstreno.equals("hoy")) {
            tvestreno.setText("¡Se estrena hoy!");
        }

        peliculas = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Peliculas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nombre = document.getString("Nombre");
                                String descripcion = document.getString("Descripcion");
                                String fotoUrl = document.getString("Foto");
                                String trailerUrl = document.getString("Trailer");
                                Timestamp estrenoTimestamp = document.getTimestamp("Estreno");
                                SimpleDateFormat sdf = new SimpleDateFormat("d 'de' MMMM yyyy", new Locale("es", "ES"));
                                estrenoFormateado = sdf.format(estrenoTimestamp.toDate());
                                String plataforma = document.getString("Plataforma");
                                String genero = document.getString("Genero");
                                String tipo = document.getString("Tipo");
                                String push = document.getString("push");
                                String director = document.getString("Director");

                                if (cumpleTipoEstreno(estrenoTimestamp, tipoEstreno)) {
                                    peliculas.add(new Pelicula(nombre, descripcion, fotoUrl, trailerUrl, plataforma, estrenoTimestamp, genero, tipo, push, director, estrenoFormateado));
                                }
                            }

                            if (plataforma != null && !plataforma.equals("todas")) {
                                List<Pelicula> peliculasPorPlataforma = new ArrayList<>();
                                for (Pelicula pelicula : peliculas) {
                                    if (pelicula.getPlataforma().equals(plataforma)) {
                                        peliculasPorPlataforma.add(pelicula);
                                    }
                                }
                                peliculas = peliculasPorPlataforma;
                            }

                            adapter = new PlataformaAdapter(PlataformaActivity.this, peliculas, tipoEstreno);
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

    private boolean cumpleTipoEstreno(Timestamp estrenoTimestamp, String tipoEstreno) {
        Date fechaActual = Calendar.getInstance().getTime();
        Date fechaEstreno = estrenoTimestamp.toDate();

        if (tipoEstreno.equals("estrenados")) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -60); //fijar el limite de dias anteriores en que se muestran las peliculas ya estrenadas
            Date fechaLimite = calendar.getTime();

            // Verificar si la fecha de estreno está después de la fecha hace 30 días
            return fechaEstreno.after(fechaLimite) && fechaEstreno.compareTo(fechaActual) <= 0;
        } else if (tipoEstreno.equals("proximos")) {
            return fechaEstreno.compareTo(fechaActual) > 0;
        } else if (tipoEstreno.equals("hoy")) {
            Calendar calEstreno = Calendar.getInstance();
            calEstreno.setTime(estrenoTimestamp.toDate());
            Calendar calHoy = Calendar.getInstance();
            return calEstreno.get(Calendar.YEAR) == calHoy.get(Calendar.YEAR) &&
                    calEstreno.get(Calendar.MONTH) == calHoy.get(Calendar.MONTH) &&
                    calEstreno.get(Calendar.DAY_OF_MONTH) == calHoy.get(Calendar.DAY_OF_MONTH);
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
        intent.putExtra("estreno", pelicula.getEstrenoFormateado());
        startActivity(intent);
    }

    public void cerrarsesion() {
        sessionManager.cerrarSesion();
        Intent intent = new Intent(PlataformaActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void atras(View view) {
        finish();
    }

    public void principal(View view) {
        Intent intent = new Intent(PlataformaActivity.this, SelecEstreno.class);
        startActivity(intent);
        finish();
    }
}
