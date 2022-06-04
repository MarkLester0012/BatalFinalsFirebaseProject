package com.example.batalfirebasefinalsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.example.batalfirebasefinalsproject.Adapters.ProductAdapter;
import com.example.batalfirebasefinalsproject.Models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    private Button add_product_btn;
    private TextView textViewUsername,logout;
    private RecyclerView products;
    private ProductAdapter productAdapter;
    private AutoCompleteTextView search;
    private List productList = new ArrayList<Product>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        refs();
        products.setLayoutManager(new GridLayoutManager(this, 1));
        //textViewUsername.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        add_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(Home.this, AddProduct.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
        loadItems();

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                productList.clear();
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(v.getText().toString().equals("")) {
                        loadItems();
                    }
                    else {
                        searchProducts(v.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] options = { "Yes" ,"No" };
                AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Home.this);
                builder.setTitle("Are you sure you want to logout?");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Yes")) {
                            FirebaseAuth.getInstance().signOut();
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(new Intent(Home.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        } else if (options[item].equals("No")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void refs() {
        add_product_btn = findViewById(R.id.addProductButton);
        products = findViewById(R.id.layoutSearchResultsRecycleView);
        search = findViewById(R.id.homesearch);
        logout= findViewById(R.id.logoutBTN);
    }

    @SuppressWarnings("unchecked")
    private void loadItems() {
        FirebaseDatabase.getInstance().getReference("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    productList.clear();
                    for(DataSnapshot dsp : snapshot.getChildren()) {
                        Product product = dsp.getValue(Product.class);
                        productList.add(product);
                    }
                    productAdapter = new ProductAdapter(Home.this, productList);
                    productAdapter.notifyDataSetChanged();
                    products.setAdapter(productAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressWarnings("unchecked")
    private void searchProducts(String keyword) {
        FirebaseDatabase.getInstance().getReference("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    productList.clear();
                    for(DataSnapshot dsp : snapshot.getChildren()) {
                        Product product = dsp.getValue(Product.class);
                        if(product.getProduct_name().toLowerCase().contains(keyword.toLowerCase()) || product.getProduct_description().toLowerCase().contains(keyword.toLowerCase())) {
                            productList.add(product);
                        }
                    }
                    productAdapter = new ProductAdapter(Home.this, productList);
                    productAdapter.notifyDataSetChanged();
                    products.setAdapter(productAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        final CharSequence[] options = { "Yes" ,"No" };
        AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Home.this);
        builder.setTitle("Are you sure you want to logout?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Yes")) {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(new Intent(Home.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION));
                } else if (options[item].equals("No")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
}