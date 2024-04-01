package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SessionManager {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    // Método para cerrar sesión
    public void cerrarSesion() {
        mAuth.signOut();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
        }
    }
}
