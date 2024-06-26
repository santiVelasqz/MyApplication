package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PlataformaAdapter extends RecyclerView.Adapter<PlataformaAdapter.PeliculaViewHolder> {
    private Context context;
    private List<Pelicula> peliculas;
    private OnItemClickListener listener;
    private String tipoEstreno;

    public interface OnItemClickListener {
        void onItemClick(Pelicula pelicula);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PlataformaAdapter(Context context, List<Pelicula> peliculas, String tipoEstreno) {
        this.context = context;
        this.peliculas = peliculas;
        this.tipoEstreno = tipoEstreno;

        // ORDENAR LA LISTA POR FECHA
        Collections.sort(peliculas, new Comparator<Pelicula>() {
            @Override
            public int compare(Pelicula p1, Pelicula p2) {
                if (tipoEstreno.equals("estrenados")) {
                    return p2.getEstreno().compareTo(p1.getEstreno());  // Orden descendente para estrenos pasados
                } else {
                    return p1.getEstreno().compareTo(p2.getEstreno()); // Orden ascendente para próximos estrenos
                }
            }
        });
        notifyDataSetChanged();
    }

    public void clear() {
        peliculas.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Pelicula> listaNueva) {
        peliculas.addAll(listaNueva);
        notifyDataSetChanged();
    }
    // ESTE MÉTODO DEL RECYCLER PERMITE SELECCIONAR CON QUE LAYOUT SE VA A TRABAJAR
    //HACIA DONDE SE RELACIONAN LOS DATOS
    @NonNull
    @Override
    public PeliculaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_xml, parent, false);
        return new PeliculaViewHolder(view);
    }
    // ESTE METODO CARGA LOS DATOS QUE SE ENVIAN AL ONCREATEVIEWHOLDER
    @Override
    public void onBindViewHolder(@NonNull PeliculaViewHolder holder, int position) {
        Pelicula pelicula = peliculas.get(position);
        holder.bind(pelicula);
        Picasso.get().load(pelicula.getFotoUrl()).into(holder.imagen_pelicula);

        // Formatear la fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("es", "ES"));
        String fechaFormateada = dateFormat.format(pelicula.getEstreno().toDate());


        holder.fecha_peli.setText(fechaFormateada); // Establecer la fecha formateada
    }

    @Override
    public int getItemCount() {
        return peliculas.size();
    }
    //AQUI SE REFERENCIAN LOS ELEMENTOS DEL LAYOUT QUE SE VAN A UTILIZAR
    public class PeliculaViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNombre;
        private ImageView imagen_pelicula;
        private TextView fecha_peli;
        private TextView txtTipo;
        private TextView txtPlataforma;

        //AQUI SE RELACIONAN LOS ELEMENTOS DEL LAYOUT
        public PeliculaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.txt_movie_name);
            imagen_pelicula = itemView.findViewById(R.id.imag_pelic);
            fecha_peli = itemView.findViewById(R.id.tv_fecha);
            txtTipo = itemView.findViewById(R.id.txt_tipo);
            txtPlataforma = itemView.findViewById(R.id.txt_plataforma);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(peliculas.get(position)); // Usar la película en la posición actual
                        }
                    }
                }
            });
        }
        //AQUI SE OBTIENE EL DATO
        public void bind(Pelicula pelicula) {
            // Asignar los valores de la película a las vistas
            textViewNombre.setText(pelicula.getNombre());
            txtTipo.setText(pelicula.getTipo());
            txtPlataforma.setText(pelicula.getPlataforma());
        }
    }

}
