package com.example.controleestoque.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.controleestoque.Entidades.Product;
import com.example.controleestoque.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class EditProductFragment extends Fragment {

    private BootstrapEditText edtProductEdit;
    private BootstrapEditText edtProducerEdit;
    private BootstrapEditText edtPriceEdit;
    private BootstrapButton btnEditProduct;
    private BootstrapButton btnRemoveProduct;
    private ImageView imgsearchProduct;

    private BootstrapEditText edtProductSearch;
    private ListView listSearchProduct;

    private Dialog dialog;
    private Context context;

    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private List<Product> listProduct = new ArrayList<Product>();
    private ArrayAdapter<Product> arrayAdapterProduct;
    private Product product;

    //Layout para mostrar apenas parte das funções
    public LinearLayout iconSearch;
    public LinearLayout dataSearched;


    @Override
    public void onCreate(Bundle savedInstanceState){
        context = getContext();
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_product, container, false);

        //Inicializa Firebase
        FirebaseApp.initializeApp(context);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        //Inicializa componentes da página principal
        edtProductEdit = root.findViewById(R.id.edtProductEdit);
        edtProducerEdit = root.findViewById(R.id.edtProducerEdit);
        edtPriceEdit = root.findViewById(R.id.edtPriceEdit);
        btnEditProduct = root.findViewById(R.id.btnEditProduct);
        btnRemoveProduct = root.findViewById(R.id.btnRemoveProduct);
        imgsearchProduct = root.findViewById(R.id.imgsearchProduct);

        btnEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth = FirebaseAuth.getInstance();
                reference = FirebaseDatabase.getInstance().getReference();

                String productSelected = product.getKey();

                if(TextUtils.isEmpty(edtProductEdit.getText().toString())) {
                    edtProductEdit.setError("*");
                    edtProductEdit.requestFocus();
                }else if(TextUtils.isEmpty(edtProducerEdit.getText().toString())){
                    edtProducerEdit.setError("*");
                    edtProducerEdit.requestFocus();
                }else if(TextUtils.isEmpty(edtPriceEdit.getText().toString())){
                    edtPriceEdit.setError("*");
                    edtPriceEdit.requestFocus();
                }else {
                    reference.child("products").orderByChild("key").equalTo(productSelected).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                UpdateProduct(edtProductEdit.getText().toString(), edtProducerEdit.getText().toString(), edtPriceEdit.getText().toString(), product.getKey(), product.getQrcode());
                            }
                            Toast.makeText(context, "Cadastro atualizado com sucesso", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(root).navigate(R.id.action_nav_editproduct_to_nav_home);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });

        btnRemoveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct(product.getKey());
                Navigation.findNavController(root).navigate(R.id.action_nav_editproduct_to_nav_home);
            }

        });

        imgsearchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    abrirDialog();
            }
        });

        iconSearch = root.findViewById(R.id.iconSearch);
        dataSearched = root.findViewById(R.id.dataSearched);

        dataSearched.setVisibility(View.GONE);

        edtProductEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!product.getKey().isEmpty()){
                    dataSearched.setVisibility(View.VISIBLE);
                }
            }
        });

        return root;
    }

    private void deleteProduct(String key) {
        DatabaseReference delete = FirebaseDatabase.getInstance().getReference("products").child(key);

        delete.removeValue();
        edtProductEdit.setText("");
        edtProducerEdit.setText("");
        edtPriceEdit.setText("");

        Toast.makeText(context,"Registro excluído com sucesso.", Toast.LENGTH_SHORT).show();

    }

    private void abrirDialog(){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.alert_search_product);

        edtProductSearch = dialog.findViewById(R.id.edtProductSearch);
        listSearchProduct = dialog.findViewById(R.id.listSearchProduct);

        pesquisarProd("");

        edtProductSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               String palavra = edtProductSearch.getText().toString().trim();
               pesquisarProd(palavra);
            }


        });

        dialog.show();
    }

    private void pesquisarProd(String palavra) {
        Query query;
        if(palavra.equals("")){
            query = databaseReference.child("products").orderByChild("product");
        }else{
            query = databaseReference.child("products").orderByChild("product").startAt(palavra).endAt(palavra+"\uf8ff");
        }

        listProduct.clear();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objSnapshot:snapshot.getChildren()){
                    Product p = objSnapshot.getValue(Product.class);
                    listProduct.add(p);
                }

                arrayAdapterProduct = new ArrayAdapter<Product>(context, android.R.layout.simple_expandable_list_item_1, listProduct);

                listSearchProduct.setAdapter(arrayAdapterProduct);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listSearchProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                product = arrayAdapterProduct.getItem(position);
                edtProductEdit.setText(product.getProduct());
                edtProducerEdit.setText(product.getProducer());
                edtPriceEdit.setText(product.getPrice());
                dialog.dismiss();
            }
        });

    }

    private void UpdateProduct(String product, String producer,String price, String keyUser, String qrcode){

        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("products");

        Product editProduct = new Product(product, producer, price, keyUser, qrcode);

        Map<String, Object> userValues = editProduct.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/products/" + keyUser, userValues);

        reference.updateChildren(childUpdates);
    }

}