package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    Button btn_registrar;
    EditText email;
    EditText password;
    FirebaseAuth mAuth;
    Button btngoogle;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        btn_login = findViewById(R.id.btn_acceder);
        btn_registrar = findViewById(R.id.btn_registrar);
        email = findViewById(R.id.et_mail);
        password = findViewById(R.id.et_contra);
        btngoogle = findViewById(R.id.btn_google);

        verificarArchivoSuscripciones();
        
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btngoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();

                // Verificar si los EditText están vacíos
                if (emailText.isEmpty() || passwordText.isEmpty()) {
                    // Mostrar un Toast indicando que se deben rellenar los campos
                    Toast.makeText(getApplicationContext(), "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Los campos no están vacíos, llamar a registrarUsuario()
                    registrarUsuario();
                }
            }
        });


        Button resetPasswordButton = findViewById(R.id.btn_reset_password);
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void verificarArchivoSuscripciones() {
        Context context = getApplicationContext();
        File file = new File(context.getFilesDir(), "notificaciones.txt");

        if (!file.exists()) {
            try {
                // Crear el archivo si no existe
                FileOutputStream fos = context.openFileOutput("notificaciones.txt", Context.MODE_PRIVATE);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al crear el archivo de suscripciones", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void resetPassword() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_reset_password, null);
        EditText emailEditText = dialogView.findViewById(R.id.et_email);

        // Crear el cuadro de diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Restablecer Contraseña")
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = emailEditText.getText().toString().trim();

                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(LoginActivity.this, "Por favor, introduce tu dirección de correo electrónico", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Enviar el correo electrónico para restablecer la contraseña
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "Se ha enviado un correo electrónico para restablecer tu contraseña", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "No se pudo enviar el correo electrónico para restablecer la contraseña. Verifica tu dirección de correo electrónico e inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancelar", null);

        // Mostrar el cuadro de diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
// este metodo autentica si el usuario se encuentra en la base de datos firebase para su login
    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(LoginActivity.this, SelecEstreno.class);
                startActivity(intent);
                finish();
            } else {
            }
        }
    };
// aqui permite iniciar sesion con google
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
// este metodo permite acceder con la cuenta de google
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
// este metodo ayuda a la autenticacion con google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Iniciando sesión...", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
// este metodo permite iniciar sesion
    private void iniciarSesion() {
        String correo = email.getText().toString();
        String contra = password.getText().toString();
        mAuth.signInWithEmailAndPassword(correo, contra)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                        } else {
                            Toast.makeText(LoginActivity.this, "Error al iniciar sesión. Verifica tus credenciales.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
//con este metodo permite registrar usuario
    private void registrarUsuario() {
        String correo = email.getText().toString();
        String contra = password.getText().toString();
        mAuth.createUserWithEmailAndPassword(correo, contra)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Usuario registrado, iniciando sesión...", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error al registrar usuario. Inténtalo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
