package net.lokmane.projectandroidgestionstock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import net.lokmane.projectandroidgestionstock.model.Product;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {


    Button btnChooseImage;
    //Button btnUploadImage;
    EditText editTextNomProduit;
    EditText editTextQuantite;
    Button btnUpdate;
    Button btnSupprimer;
    ImageView imageUpload;

    Product p;

    String urlUpload = "";
    String urlDelete = "";
    final int CODE_GALLERY_REQUEST = 999;
    Bitmap bitmap;

    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        btnChooseImage = findViewById(R.id.btnChooseImage);
        //btnUploadImage = findViewById(R.id.btnUploadImage);
        editTextNomProduit = findViewById(R.id.nameProduct);
        editTextQuantite = findViewById(R.id.quantityProduct);
        btnUpdate = findViewById(R.id.btnUpdate);
        imageUpload = findViewById(R.id.imageProduct);
        btnSupprimer = findViewById(R.id.btnSupprimer);

        urlUpload = getString(R.string.host)+"update.php";

        urlDelete = getString(R.string.host)+"delete.php";

        Intent intent = getIntent();
        if(intent != null){
            editTextNomProduit.setText(intent.getStringExtra("name"));
            editTextQuantite.setText(intent.getStringExtra("quantity"));
            String imageUri = getString(R.string.host)+intent.getStringExtra("image");
            Picasso.with(getApplicationContext()).load(imageUri).into(imageUpload);
            id = Integer.parseInt(intent.getStringExtra("id"));
            int q = Integer.parseInt(intent.getStringExtra("quantity"));
            p = new Product(id, intent.getStringExtra("name"), q, intent.getStringExtra("image"));
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });

        btnSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData();

            }
        });







        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Démarrer la caméra et attendre le résultat
                // startActivityForResult(intent, 100);


                ActivityCompat.requestPermissions(
                        ProductActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        CODE_GALLERY_REQUEST
                );
            }
        });
    }





    private void updateData(){
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        ProductActivity.super.onBackPressed();
                        // Display the first 500 characters of the response string.

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();

            }
        }){

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if(bitmap != null){
                    String imageData = imageToString(bitmap);
                    params.put("image", imageData);
                }
                params.put("id", id+"");
                params.put("nomProduit", editTextNomProduit.getText().toString());
                params.put("quantiteProduit", editTextQuantite.getText().toString());

                return params;
            }
        };

        RequestQueue queue =  Volley.newRequestQueue(ProductActivity.this);
        queue.add(stringRequest);

    }

    private void deleteData(){
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlDelete,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        ProductActivity.super.onBackPressed();
                        // Display the first 500 characters of the response string.

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();

            }
        }){

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //String imageData = imageToString(bitmap);
                params.put("id", id+"");

                return params;
            }
        };

        RequestQueue queue =  Volley.newRequestQueue(ProductActivity.this);
        queue.add(stringRequest);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CODE_GALLERY_REQUEST){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), CODE_GALLERY_REQUEST);

            }

            else{
                Toast.makeText(getApplicationContext(), "You don't have permission", Toast.LENGTH_LONG).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // if (requestCode == 100) {
        //   captureImage = (Bitmap) data.getExtras().get("data");
        // imageUpload.setImageBitmap(captureImage);
        // convert image
        //ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //captureImage.compress(Bitmap.CompressFormat.PNG, 100, bos);
        //bArray = bos.toByteArray();
        // Toast.makeText(getApplicationContext(), Arrays.toString(bArray), Toast.LENGTH_LONG).show();


        // }


        if(requestCode == CODE_GALLERY_REQUEST && resultCode == RESULT_OK && data!=null){
            Uri filePath = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);

                imageUpload.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }



        super.onActivityResult(requestCode, resultCode, data);



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageByte = outputStream.toByteArray();

        String encodedImage = Base64.getEncoder().encodeToString(imageByte);
        return encodedImage;
    }

}