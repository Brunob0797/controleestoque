package com.example.controleestoque.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.controleestoque.Entidades.User;
import com.example.controleestoque.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class EditUserFragment extends Fragment {

    private BootstrapEditText edtNameEdit;
    private RadioButton normalUserEdit;
    private RadioButton adminUserEdit;
    private BootstrapButton btnEditUser;
    private ImageView imgsearchUser;

    private BootstrapEditText edtNameSearch;
    private ListView listSearchUser;

    private Dialog dialog;
    private Context context;

    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private List<User> listUser = new ArrayList<User>();
    private ArrayAdapter<User> arrayAdapterUser;
    private User user;

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
        View root = inflater.inflate(R.layout.fragment_edit_user, container, false);

        //Inicializa Firebase
        FirebaseApp.initializeApp(context);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        //Inicializa componentes da página principal
        edtNameEdit = root.findViewById(R.id.edtNameEdit);
        normalUserEdit = root.findViewById(R.id.normalUserEdit);
        adminUserEdit = root.findViewById(R.id.adminUserEdit);
        btnEditUser = root.findViewById(R.id.btnEditUser);
        imgsearchUser = root.findViewById(R.id.imgsearchUser);

        btnEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth = FirebaseAuth.getInstance();
                reference = FirebaseDatabase.getInstance().getReference();

                String emailSelectedUser = user.getEmail();
                if(TextUtils.isEmpty(edtNameEdit.getText().toString())){
                    edtNameEdit.setError("*");
                    edtNameEdit.requestFocus();
                }else {
                    reference.child("users").orderByChild("email").equalTo(emailSelectedUser).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                User userUpdated = userSnapshot.getValue(User.class);
                                if (normalUserEdit.isChecked()) {
                                    UpdateUser(edtNameEdit.getText().toString(), user.getEmail(), user.getKey(), "Usuario");
                                } else {
                                    UpdateUser(edtNameEdit.getText().toString(), user.getEmail(), user.getKey(), "Admin");
                                }
                            }
                            Toast.makeText(context, "Cadastro atualizado com sucesso", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        imgsearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    abrirDialog();
            }
        });

        iconSearch = root.findViewById(R.id.iconSearch);
        dataSearched = root.findViewById(R.id.dataSearched);

        dataSearched.setVisibility(View.GONE);

        edtNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!user.getEmail().isEmpty()){
                    dataSearched.setVisibility(View.VISIBLE);
                }
            }
        });

        return root;
    }

    private void abrirDialog(){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.alert_search_name);

        edtNameSearch = dialog.findViewById(R.id.edtNameSearch);
        listSearchUser = dialog.findViewById(R.id.listSearchUser);

        pesquisar("");

        edtNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String palavra = edtNameSearch.getText().toString().trim();
                pesquisar(palavra);
            }


        });

        dialog.show();
    }

    private void pesquisar(String palavra) {
        Query query;
        if(palavra.equals("")){
            query = databaseReference.child("users").orderByChild("name");
        }else{
            query = databaseReference.child("users").orderByChild("name").startAt(palavra).endAt(palavra+"\uf8ff");
        }

        listUser.clear();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objSnapshot:snapshot.getChildren()){
                    User u = objSnapshot.getValue(User.class);
                    listUser.add(u);
                }

                arrayAdapterUser = new ArrayAdapter<User>(context, android.R.layout.simple_expandable_list_item_1, listUser);

                listSearchUser.setAdapter(arrayAdapterUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listSearchUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                user = arrayAdapterUser.getItem(position);
                edtNameEdit.setText(user.getName());
                if (user.getUsertype().equals("Admin")){
                    adminUserEdit.setChecked(true);
                }else{
                    normalUserEdit.setChecked(true);
                }
                dialog.dismiss();
            }
        });

    }

    private void UpdateUser(String name, String email, String keyUser, String usertype){

        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users");

        User userEdit = new User(name, email, usertype, keyUser);

        Map<String, Object> userValues = userEdit.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + keyUser, userValues);

        reference.updateChildren(childUpdates);
    }

}