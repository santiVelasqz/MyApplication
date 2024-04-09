package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class list_plataformas_adaptador extends ArrayAdapter<String> {

    private final Context context;
    private final String[] itemNames;
    private final Integer[] itemImages;
    private final String tipoEstreno;

    //CONSTRUCTOR DE LA LISTA DEL ADAPTADOR
    public list_plataformas_adaptador(@NonNull Context context, String[] itemNames, Integer[] itemImages, String tipoEstreno) {
        super(context, R.layout.item_plataforma, itemNames);
        this.context = context;
        this.itemNames = itemNames;
        this.itemImages = itemImages;
        this.tipoEstreno = tipoEstreno;
    }
    // AQUI SE REFERENCIA A QUE LAYOUT SE VAN A ENVIAR LOS DATOS
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.item_plataforma, null, true);

        ImageView itemImage = rowView.findViewById(R.id.ig_plataforma);
        itemImage.setImageResource(itemImages[position]);
        // AQUI SE ENVIAN LOS DATOS DE LA PLATAFORMA Y EL TIPO DE ESTRENO
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlataformaActivity.class);
                intent.putExtra("plataforma", itemNames[position]);
                intent.putExtra("tipoEstreno", tipoEstreno);
                context.startActivity(intent);
            }
        });

        return rowView;
    }
}
