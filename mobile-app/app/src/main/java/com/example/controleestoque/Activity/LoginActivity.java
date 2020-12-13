package com.example.controleestoque.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.controleestoque.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    //Criando os objetos de login
    private BootstrapButton btnLogin;
    private BootstrapEditText edtEmail;
    private BootstrapEditText edtPassword;
    private TextView txtRecoveryPassword;

    //objetos do alert de recuperação de senha
    private BootstrapEditText edtEmailAlert;
    private BootstrapButton btnCancel;
    private BootstrapButton btnRecovery;
    private Dialog dialog;

    //Instanciando Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Recuperando a instancia da auth firebase
        mAuth = FirebaseAuth.getInstance();


        //Vinculando objetos aos id's
        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.editPassword);
        txtRecoveryPassword = findViewById(R.id.txtRecoveryPassword);

        //Criando os eventos de click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EfetuarLogin(edtEmail.getText().toString(), edtPassword.getText().toString());
            }
        });

        txtRecoveryPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirDialog();
            }
        });

    }

    //Criando métodos
    private void EfetuarLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            Toast.makeText(com.example.controleestoque.Activity.LoginActivity.this, "Login efetuado com sucesso!", Toast.LENGTH_LONG).show();
                            abrirMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(com.example.controleestoque.Activity.LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            abrirMainActivity();
        }
    }

    private void abrirMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void abrirDialog(){
        dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.alert_recovery_password);

        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnRecovery = dialog.findViewById(R.id.btnRecovery);
        edtEmailAlert = dialog.findViewById(R.id.edtEmailRecovery);

        btnRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(edtEmailAlert.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TAG", "E-mail enviado");
                                    Toast.makeText(LoginActivity.this,"Verifique sua caixa de e-mail",Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }else {
                                    Toast.makeText(LoginActivity.this,"E-mail inválido",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}

