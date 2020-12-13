package com.example.controleestoque.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.controleestoque.Entidades.Product;
import com.example.controleestoque.Entidades.User;
import com.example.controleestoque.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewProductFragment extends Fragment {

    //Criando os objetos
    private BootstrapButton btnCadastrarProd;
    private BootstrapButton btnNovaMedida;
    private BootstrapButton btnLerMedida;

    private BootstrapEditText edtProduct;
    private BootstrapEditText edtProducer;
    private BootstrapEditText edtPrice;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference myDevice;
    private FirebaseUser userAuth;
    private Context context;

    private Product product;

    //Layout para mostrar apenas parte das funções
    public LinearLayout lerMedida;
    public LinearLayout novaMedida;
    public LinearLayout cadastrar;

    private int massa1;
    private int massa2;
    private int massa;

    @Override
    public void onCreate(Bundle savedInstanceState){
        context = getContext();
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_newproduct, container, false);


// Initialize Firebase Auth and database
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myDevice = database.getReference("devices").child("device1");

        product = new Product();

        //Vincula o objeto ao id
        btnCadastrarProd = root.findViewById(R.id.btnCadastrarProd);
        btnNovaMedida = root.findViewById(R.id.btnNovaMedida);
        btnLerMedida = root.findViewById(R.id.btnLerMedida);

        edtProduct = root.findViewById(R.id.edtProduct);
        edtProducer = root.findViewById(R.id.edtProducer);
        edtPrice = root.findViewById(R.id.edtPrice);

        lerMedida = root.findViewById(R.id.lerMedida);
        novaMedida = root.findViewById(R.id.novaMedida);
        cadastrar = root.findViewById(R.id.cadastrar);

        btnCadastrarProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(edtProduct.getText().toString())) {
                    edtProduct.setError("*");
                    edtProduct.requestFocus();
                }else if(TextUtils.isEmpty(edtProducer.getText().toString())){
                    edtProducer.setError("*");
                    edtProducer.requestFocus();
                }else if(TextUtils.isEmpty(edtPrice.getText().toString())){
                    edtPrice.setError("*");
                    edtPrice.requestFocus();
                }else {
                    product.setProduct(edtProduct.getText().toString());
                    product.setProducer(edtProducer.getText().toString());
                    product.setPrice(edtPrice.getText().toString());

                    insertProductDB(product);

                }
            }
        });

        btnNovaMedida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                novaMedida.setVisibility(View.GONE);
                lerMedida.setVisibility(View.VISIBLE);

                myDevice.child("massadev").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            massa1 = snapshot.getValue(Integer.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        btnLerMedida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lerMedida.setVisibility(View.GONE);
                cadastrar.setVisibility(View.VISIBLE);

                myDevice.child("massadev").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            massa2 = snapshot.getValue(Integer.class);
                            massa = massa2 - massa1;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        lerMedida.setVisibility(View.GONE);
        cadastrar.setVisibility(View.GONE);


        return root;
    }

    //Função para criar produto no Firebase
    private void insertProductDB(Product product) {
        //criando o usuario
        myRef = database.getReference("products");

        String key = myRef.child("products").push().getKey();
        product.setKey(key);

        product.setMassa(massa);

        myRef.child(key).setValue(product);
        Toast.makeText(context, "Produto cadastrado com sucesso", Toast.LENGTH_SHORT).show();

    }
}