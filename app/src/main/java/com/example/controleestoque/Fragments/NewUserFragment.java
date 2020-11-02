package com.example.controleestoque.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.controleestoque.Entidades.User;
import com.example.controleestoque.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewUserFragment extends Fragment {

    //Criando os objetos
    private BootstrapButton btnCadastrar;

    private BootstrapEditText edtName;
    private BootstrapEditText edtEmail;
    private BootstrapEditText edtPassword;

    private RadioButton normaluser;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser userAuth;
    private Context context;

    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState){
        context = getContext();
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_newuser, container, false);


// Initialize Firebase Auth and database
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        user = new User();

        //Vincula o objeto ao id
        btnCadastrar = root.findViewById(R.id.btnCadastrar);

        edtName = root.findViewById(R.id.edtName);
        edtEmail = root.findViewById(R.id.edtEmail);
        edtPassword = root.findViewById(R.id.editPassword);

        normaluser = root.findViewById(R.id.normaluser);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setEmail(edtEmail.getText().toString());
                user.setName(edtName.getText().toString());
                user.setPassword(edtPassword.getText().toString());
                if(normaluser.isChecked()){
                    user.setUsertype("Usuario");
                }else{
                    user.setUsertype("Admin");
                }

                if(TextUtils.isEmpty(edtName.getText().toString())){
                    edtName.setError("*");
                    edtName.requestFocus();
                }else if(TextUtils.isEmpty(edtEmail.getText().toString())){
                    edtEmail.setError("*");
                    edtEmail.requestFocus();
                }else if(TextUtils.isEmpty(edtPassword.getText().toString())){
                    edtPassword.setError("*");
                    edtPassword.requestFocus();
                }else {
                    criarConta(user);
                    Navigation.findNavController(root).navigate(R.id.action_nav_newuser_to_nav_home);
                }
            }
        });
        return root;
    }

    //Função para criar conta no firebase
    private void criarConta(User user) {
            mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "createUserWithEmail:success");
                                Toast.makeText(context, "Cadastro Efetuado",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseUser user_auth = FirebaseAuth.getInstance().getCurrentUser();
                                inserirUsuarioDB(user, user_auth);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(context, "Erro no cadastro",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

    }

    private void inserirUsuarioDB(User user, FirebaseUser user_auth) {
        //criando o usuario
        myRef = database.getReference("users");

        String uid = user_auth.getUid();

        //Replica a chave uid
        user.setKey(uid);

        myRef.child(uid).setValue(user);
    }
}