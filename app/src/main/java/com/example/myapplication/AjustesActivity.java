package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AjustesActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        sessionManager = new SessionManager(this);

        Button cambiarcontraseña = findViewById(R.id.btn_cambiarcontraseña);
        Button cerrarsesion = findViewById(R.id.btn_cerrarsesion);
        Button eliminarcuenta = findViewById(R.id.btn_cuenta);
        Button gestionnotis = findViewById(R.id.btn_gestion);

        //utilizamos un listener para cerrar sesion
        cerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarsesion();
            }
        });
        //utilizamos un listener para eliminar cuenta
        eliminarcuenta.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                eliminarCuenta();
            }
        });

        gestionnotis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoGestionNotificaciones();
            }
        });

        //utilizamos un listener para cambiar contraseña
        cambiarcontraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                boolean isGoogleUser = false;
                List<? extends UserInfo> userInfoList = user.getProviderData();
                for (UserInfo userInfo : userInfoList) {
                    if (GoogleAuthProvider.PROVIDER_ID.equals(userInfo.getProviderId())) {
                        isGoogleUser = true;
                        break;
                    }
                }

                if (isGoogleUser) {
                    // El usuario está registrado con Google, mostrar Toast y salir de la función
                    Toast.makeText(AjustesActivity.this, "Usuario registrado con Google, imposible cambiar la contraseña", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    mostrarDialogoCambiarContrasena();
                }
            }
        });
    }
// este metodo se usa como adaptador que carga un layout para proceder a cambiar la contraseña
    private void mostrarDialogoCambiarContrasena() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialogo_cambiar_contrasena, null);
        builder.setView(view);

        EditText etContrasenaActual = view.findViewById(R.id.et_contrasena_actual);
        EditText etNuevaContrasena = view.findViewById(R.id.et_nueva_contrasena);
        EditText etConfirmarNuevaContrasena = view.findViewById(R.id.et_confirmar_nueva_contrasena);

        builder.setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String contrasenaActual = etContrasenaActual.getText().toString();
                String nuevaContrasena = etNuevaContrasena.getText().toString();
                String confirmarNuevaContrasena = etConfirmarNuevaContrasena.getText().toString();

                cambiarContrasenaFirebase(contrasenaActual, nuevaContrasena);
            }
        });

        builder.setNegativeButton("Cancelar", null);

        builder.create().show();
    }
// este metodo nos permite cambiar la contraseña comparando que exista el email para su cambio de contraseña
    private void cambiarContrasenaFirebase(String contrasenaActual, String nuevaContrasena) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), contrasenaActual);

        user.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user.updatePassword(nuevaContrasena)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(AjustesActivity.this, "Contraseña cambiada exitosamente", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AjustesActivity.this, "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AjustesActivity.this, "Error de autenticación. Verifica tu contraseña actual", Toast.LENGTH_SHORT).show();
                    }
                });
    }
// metodo para eliminar la cuenta
    private void eliminarCuenta() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AjustesActivity.this, "Cuenta eliminada exitosamente", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AjustesActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(AjustesActivity.this, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void mostrarDialogoGestionNotificaciones() {
        // Obtener las suscripciones guardadas
        ArrayList<String> suscripciones = obtenerSuscripciones();

        // Crear un diálogo personalizado
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Gestión de Notificaciones");

        // Configurar la lista de temas suscritos
        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, suscripciones);
        listView.setAdapter(adapter);
        builder.setView(listView);

        // Configurar el botón de desuscribirse
        builder.setPositiveButton("Desuscribirse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtener los temas seleccionados para desuscribirse
                int count = listView.getCount();
                for (int i = 0; i < count; i++) {
                    if (listView.isItemChecked(i)) {
                        String tema = suscripciones.get(i);
                        // Desuscribirse del tema en Firebase Messaging
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(tema);
                        // Eliminar la suscripción del archivo de notificaciones
                        eliminarSuscripcion(tema);
                        Toast.makeText(AjustesActivity.this, "Desuscripción exitosa de " + tema, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Mostrar el diálogo
        builder.show();
    }

    // Método para obtener las suscripciones guardadas en el archivo notificaciones.txt
    private ArrayList<String> obtenerSuscripciones() {
        ArrayList<String> suscripciones = new ArrayList<>();
        try {
            // Obtener la ruta del archivo notificaciones.txt
            String filePath = getApplicationContext().getFilesDir() + "/" + "notificaciones.txt";
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

    // Método para eliminar una suscripción del archivo notificaciones.txt
    private void eliminarSuscripcion(String tema) {
        try {
            // Obtener la ruta del archivo notificaciones.txt
            String filePath = getApplicationContext().getFilesDir() + "/" + "notificaciones.txt";
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

// metodo para cerrar sesion
    public void cerrarsesion (){
        sessionManager.cerrarSesion();

        // Redirigir al LoginActivity
        Intent intent = new Intent(AjustesActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finalizar la actividad actual para que el usuario no pueda volver atrás
    }
    //boton para ir a la ventana anterior
    public void atras (View view){
        finish();
    }
}
