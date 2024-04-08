package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
    private List<Pelicula> peliculasOriginales; // Lista que contiene todas las películas originales
    private List<Pelicula> peliculasFiltradas; // Lista que contiene las películas filtradas
    private SessionManager sessionManager;
    private String estrenoFormateado;
    private String plataforma;
    private ToggleButton toggleSeries;
    private ToggleButton togglePeliculas;
    private String tipoEstreno;
    private SearchView txtbuscar;
    Button btn_ajustes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        toggleSeries = findViewById(R.id.toggleSeries);
        togglePeliculas = findViewById(R.id.togglePeliculas);
        sessionManager = new SessionManager(this);
        txtbuscar = findViewById(R.id.txtbuscar);
        txtbuscar.requestFocus();
        recyclerView = findViewById(R.id.listaNetflix);
        TextView tvestreno = findViewById(R.id.tv_testreno);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btn_ajustes = findViewById(R.id.btn_ajustes);


        txtbuscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarPorNombre(newText);
                return true;
            }
        });

        toggleSeries.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && togglePeliculas.isChecked()) {
                    // Si se activa el botón de series y el de películas está activado, desactivar el de películas
                    togglePeliculas.setChecked(false);
                }
                actualizarLista();
            }
        });

        togglePeliculas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && toggleSeries.isChecked()) {
                    // Si se activa el botón de películas y el de series está activado, desactivar el de series
                    toggleSeries.setChecked(false);
                }
                actualizarLista();
            }
        });

        plataforma = getIntent().getStringExtra("plataforma");
        tipoEstreno = getIntent().getStringExtra("tipoEstreno");

        if (tipoEstreno.equals("estrenados")) {
            tvestreno.setText("Estrenadas");
        } else if (tipoEstreno.equals("proximos")) {
            tvestreno.setText("Próximos estrenos");
        } else if (tipoEstreno.equals("hoy")) {
            tvestreno.setText("¡Se estrena hoy!");
        }

        peliculasOriginales = new ArrayList<>();
        peliculasFiltradas = new ArrayList<>();

        obtenerDatosFirebase();



        btn_ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlataformaActivity.this, AjustesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void obtenerDatosFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Peliculas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            peliculasOriginales.clear(); // Limpiar la lista antes de agregar los nuevos datos
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Obtener los datos y agregarlos a la lista de películas
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
                                    peliculasOriginales.add(new Pelicula(nombre, descripcion, fotoUrl, trailerUrl, plataforma, estrenoTimestamp, genero, tipo, push, director, estrenoFormateado));
                                }
                            }

                            if (plataforma != null && !plataforma.equals("todas")) {
                                List<Pelicula> peliculasPorPlataforma = new ArrayList<>();
                                for (Pelicula pelicula : peliculasOriginales) {
                                    if (pelicula.getPlataforma().equals(plataforma)) {
                                        peliculasPorPlataforma.add(pelicula);
                                    }
                                }
                                peliculasFiltradas = peliculasPorPlataforma;
                            } else {
                                peliculasFiltradas = new ArrayList<>(peliculasOriginales);
                            }

                            actualizarLista();
                        } else {
                            Log.d("PlataformaActivity", "Error getting documents: ", task.getException());
                            Toast.makeText(PlataformaActivity.this, "Error al obtener películas", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void actualizarLista() {
        List<Pelicula> listaFiltrada = new ArrayList<>();

        for (Pelicula pelicula : peliculasFiltradas) {
            if ((!toggleSeries.isChecked() && !togglePeliculas.isChecked())) {
                listaFiltrada.add(pelicula);
            } else if (toggleSeries.isChecked() && pelicula.getTipo().equals("Serie")) {
                // Si se pulsa solo el botón de series, se muestran las películas de tipo serie
                listaFiltrada.add(pelicula);
            } else if (togglePeliculas.isChecked() && pelicula.getTipo().equals("Pelicula")) {
                // Si se pulsa solo el botón de películas, se muestran las películas de tipo película
                listaFiltrada.add(pelicula);
            }
        }

        if (adapter != null) {
            adapter.clear(); // Limpiar la lista actual en el adaptador
            adapter.addAll(listaFiltrada); // Agregar la lista filtrada al adaptador
            adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
        } else {
            adapter = new PlataformaAdapter(PlataformaActivity.this, listaFiltrada, tipoEstreno);
            adapter.setOnItemClickListener(PlataformaActivity.this);
            recyclerView.setAdapter(adapter);
        }
    }

    private boolean cumpleTipoEstreno(Timestamp estrenoTimestamp, String tipoEstreno) {
        Date fechaActual = Calendar.getInstance().getTime();
        Date fechaEstreno = estrenoTimestamp.toDate();

        if (tipoEstreno.equals("estrenados")) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -60); //fijar el limite de dias anteriores en que se muestran las peliculas ya estrenadas
            Date fechaLimite = calendar.getTime();

            // Verificar si la fecha de estreno está después de la fechalimite
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
    public void onItemClick(Pelicula pelicula) {
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

    private void filtrarPorNombre(String query) {
        List<Pelicula> listaFiltrada = new ArrayList<>();

        for (Pelicula pelicula : peliculasFiltradas) {
            if (pelicula.getNombre().toLowerCase().contains(query.toLowerCase())) {
                listaFiltrada.add(pelicula);
            }
        }

        if (adapter != null) {
            adapter.clear(); // Limpiar la lista actual en el adaptador
            adapter.addAll(listaFiltrada); // Agregar la lista filtrada al adaptador
            adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
        } else {
            adapter = new PlataformaAdapter(PlataformaActivity.this, listaFiltrada, tipoEstreno);
            adapter.setOnItemClickListener(PlataformaActivity.this);
            recyclerView.setAdapter(adapter);
        }
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
