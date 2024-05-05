package com.example.myapplication;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class SuscripcionUtil {

    // Método para eliminar una suscripción del archivo notificaciones.txt
    public static void eliminarSuscripcion(Context context, String tema) {
        try {
            // Obtener la ruta del archivo notificaciones.txt
            String filePath = context.getFilesDir() + "/" + "notificaciones.txt";
            File file = new File(filePath);
            // Verificar si el archivo existe
            if (file.exists()) {
                // Leer el contenido del archivo y eliminar la línea correspondiente al tema
                BufferedReader reader = new BufferedReader(new FileReader(file));
                ArrayList<String> lines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    // Si la línea contiene el tema, no la añadimos a la lista de líneas
                    if (!line.startsWith(tema + ":")) {
                        lines.add(line);
                    }
                }
                reader.close();

                // Escribir el contenido actualizado al archivo
                FileWriter writer = new FileWriter(file);
                for (String l : lines) {
                    writer.write(l + "\n");
                }
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para obtener las suscripciones guardadas en el archivo notificaciones.txt
    public static ArrayList<String> obtenerSuscripciones(Context context) {
        ArrayList<String> suscripciones = new ArrayList<>();
        try {
            // Obtener la ruta del archivo notificaciones.txt
            String filePath = context.getFilesDir() + "/" + "notificaciones.txt";
            File file = new File(filePath);
            // Verificar si el archivo existe
            if (file.exists()) {
                // Leer el contenido del archivo
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Separar el nombre y el push
                    String[] parts = line.split(":");
                    if (parts.length >= 2) {
                        suscripciones.add(parts[0]);
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return suscripciones;
    }

    // Método para mostrar un mensaje Toast
    public static void mostrarMensaje(Context context, String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
    }

    // Método para desuscribirse de los temas seleccionados
    public static void desuscribirseDeTemasSeleccionados(Context context, ListView listView, ArrayList<String> suscripciones) {
        SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
        int itemCount = listView.getCount();
        for (int i = 0; i < itemCount; i++) {
            if (checkedItems.get(i)) {
                String tema = suscripciones.get(i);
                // Desuscribirse del tema en Firebase Messaging
                FirebaseMessaging.getInstance().unsubscribeFromTopic(tema);
                // Eliminar la suscripción del archivo de notificaciones
                eliminarSuscripcion(context, tema);
                // Mostrar mensaje de desuscripción exitosa
                mostrarMensaje(context, "Desuscripción exitosa de " + tema);
            }
        }
    }

    public static void guardarSuscripciones(String nombre, String push, Context context) {
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

    public static void desuscribirseDeTema(String tema, Context context) {
        try {
            // Obtener la ruta del archivo notificaciones.txt
            String filePath = context.getFilesDir() + "/" + "notificaciones.txt";
            File file = new File(filePath);
            // Verificar si el archivo existe
            if (file.exists()) {
                // Leer el contenido del archivo y eliminar la línea correspondiente al tema
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                boolean encontrado = false;
                while ((line = reader.readLine()) != null) {
                    // Si la línea no contiene el tema, la añadimos al nuevo contenido
                    if (!line.startsWith(tema + ":")) {
                        stringBuilder.append(line).append("\n");
                    } else {
                        encontrado = true;
                    }
                }
                reader.close();

                // Escribir el nuevo contenido al archivo
                FileWriter writer = new FileWriter(file);
                writer.write(stringBuilder.toString());
                writer.close();

                if (encontrado) {
                    // Si se encontró el tema y se eliminó correctamente, mostrar un mensaje Toast
                    mostrarMensaje(context, "Desuscripción exitosa de " + tema);
                } else {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
