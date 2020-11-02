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
import com.example.controleestoque.Entidades.Product;
import com.example.controleestoque.Entidades.User;
import com.example.controleestoque.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewProductFragment extends Fragment {

    //Criando os objetos
    private BootstrapButton btnCadastrarProd;

    private BootstrapEditText edtProduct;
    private BootstrapEditText edtProducer;
    private BootstrapEditText edtPrice;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser userAuth;
    private Context context;

    private Product product;

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

        product = new Product();

        //Vincula o objeto ao id
        btnCadastrarProd = root.findViewById(R.id.btnCadastrarProd);

        edtProduct = root.findViewById(R.id.edtProduct);
        edtProducer = root.findViewById(R.id.edtProducer);
        edtPrice = root.findViewById(R.id.edtPrice);

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

                    Navigation.findNavController(root).navigate(R.id.action_nav_products_to_nav_home);
                }
            }
        });
        return root;
    }

    //Função para criar produto no Firebase
    private void insertProductDB(Product product) {
        //criando o usuario
        myRef = database.getReference("products");

        String key = myRef.child("products").push().getKey();
        product.setKey(key);

        String qrcode = myRef.child("products").push().getKey();
        product.setQrcode(qrcode);

        myRef.child(key).setValue(product);
        Toast.makeText(context, "Produto cadastrado com sucesso", Toast.LENGTH_SHORT).show();

    }
}