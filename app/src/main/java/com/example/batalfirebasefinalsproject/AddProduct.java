package com.example.batalfirebasefinalsproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.batalfirebasefinalsproject.Models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddProduct extends AppCompatActivity {

    //Variables
    private TextView back_btn, quantity;
    private ImageView product_image;
    private TextInputEditText product_name, product_price, product_description;
    private Button add_qty_btn, deduct_qty_btn, add_product_btn, upload_product_image, remove_product_image;
    private ActivityResultLauncher<Intent> imageResultLauncher;
    private int qtyResult;
    private Uri selectedImage;
    private String productID;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product_page);
        refs();
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(AddProduct.this, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });

        add_qty_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qtyResult = qtyResult + 1;
                quantity.setText(String.valueOf(qtyResult));
            }
        });

        deduct_qty_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qtyResult = qtyResult - 1;
                if(qtyResult < 0) {
                    qtyResult = 0;
                    quantity.setText(String.valueOf(qtyResult));
                }
                else {
                    quantity.setText(String.valueOf(qtyResult));
                }
            }
        });

        //Image Result Launcher
        imageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent image = result.getData();
                    if (image.getExtras() != null) {
                        Bundle bundle = image.getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        product_image.setImageBitmap(bitmap);
                        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, "Title", null);
                        selectedImage = Uri.parse(path);
                    } else {
                        Uri imageData = image.getData();
                        product_image.setImageURI(imageData);
                        selectedImage = imageData;
                    }
                }
            }
        });

        //Add Products to the Firebase
        add_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!String.valueOf(product_name.getText()).equals("") && !String.valueOf(product_price.getText()).equals("") && !String.valueOf(product_description.getText()).equals("") && !String.valueOf(quantity.getText()).equals("0")) {
                    final CharSequence[] options = { "Yes" ,"No" };
                    AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AddProduct.this);
                    builder.setTitle("Add Product?");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Yes")) {
                                progressDialog = new ProgressDialog(AddProduct.this);
                                progressDialog.setMessage("Processing...");
                                progressDialog.show();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products");
                                productID = databaseReference.push().getKey();
                                databaseReference.child(productID).setValue(new Product(productID,String.valueOf(product_name.getText()),String.valueOf(quantity.getText()),String.valueOf(product_price.getText()),"",String.valueOf(product_description.getText()))).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            uploadImage(productID);
                                        }
                                    }
                                });
                            } else if (options[item].equals("No")) {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();
                }
                else {
                    Toast.makeText(AddProduct.this, "Please complete all fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        upload_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] options = { "Take Photo", "Choose from Gallery", "Cancel" };
                AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AddProduct.this);
                builder.setTitle("Choose your action to add a picture");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (options[item].equals("Take Photo")) {
                            requestCameraPermission();
                            Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            if(takePicture.resolveActivity(getPackageManager()) != null) {
                                if (ContextCompat.checkSelfPermission(AddProduct.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    imageResultLauncher.launch(takePicture);
                                } else {
                                    Toast.makeText(AddProduct.this, "Please enable camera permissions", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(AddProduct.this,"There's no app that supports this action", Toast.LENGTH_SHORT).show();
                            }
                        } else if (options[item].equals("Choose from Gallery")) {
                            requestStoragePermission();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            imageResultLauncher.launch(pickPhoto);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        remove_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.valorant_icon);
                product_image.setImageURI(Uri.parse(""));
                product_image.setImageBitmap(icon);
                selectedImage = Uri.parse("");
            }
        });
    }

    private void refs() {
    //Variable Assignments
        //TextView
        back_btn = findViewById(R.id.backButtonTextView);
        quantity = findViewById(R.id.quantityTextView);

        //Edit Text
        product_name = findViewById(R.id.textInputEditText_PName);
        product_price = findViewById(R.id.textInputEditText_PPrice);
        product_description = findViewById(R.id.textInputEditText_PDescription);

        //Buttons
        add_qty_btn = findViewById(R.id.addQtyButton);
        deduct_qty_btn = findViewById(R.id.deductQtyButton);
        add_product_btn = findViewById(R.id.addProductButton);
        upload_product_image = findViewById(R.id.uploadProductImageButton);
        remove_product_image = findViewById(R.id.removeProductImageButton);

        //Int Values
        qtyResult = Integer.parseInt(String.valueOf(quantity.getText()));

        //ImageView
        product_image = findViewById(R.id.productImageIcon);
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.valorant_icon);
        product_image.setImageURI(Uri.parse(""));
        product_image.setImageBitmap(icon);
    }

    //Request Permissions Method
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(AddProduct.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddProduct.this, new String[]{Manifest.permission.CAMERA}, 5);
        } else {

        }
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(AddProduct.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddProduct.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 5);
        } else {

        }
    }

    //Upload Image Method
    private void uploadImage(String productID)
    {
        if(selectedImage != null)
        {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("ProductImage/"+ productID + "profile" +".jpg");
            ref.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()) {
                                Map<String, Object> userHashMap = new HashMap<>();
                                userHashMap.put("product_image", task.getResult().toString());
                                FirebaseDatabase.getInstance().getReference("Products").child(productID).updateChildren(userHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Successfully added the product!", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(AddProduct.this, Home.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            finish();
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        final CharSequence[] options = { "Yes" ,"No" };
        AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AddProduct.this);
        builder.setTitle("Are you sure you want to cancel?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Yes")) {
                    Intent intent = new Intent(AddProduct.this, Home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    startActivity(intent);
                } else if (options[item].equals("No")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
}