package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class list_plataformas_adaptador extends RecyclerView.Adapter<list_plataformas_adaptador.ViewHolder> {

    private final Context context;
    private final String[] itemNames;
    private final Integer[] itemImages;
    private final String tipoEstreno;

    //CONSTRUCTOR DE LA LISTA DEL ADAPTADOR
    public list_plataformas_adaptador(@NonNull Context context, String[] itemNames, Integer[] itemImages, String tipoEstreno) {
        this.context = context;
        this.itemNames = itemNames;
        this.itemImages = itemImages;
        this.tipoEstreno = tipoEstreno;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plataforma, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.itemImage.setImageResource(itemImages[position]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlataformaActivity.class);
                intent.putExtra("plataforma", itemNames[position]);
                intent.putExtra("tipoEstreno", tipoEstreno);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemNames.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.ig_plataforma);
        }
    }
}