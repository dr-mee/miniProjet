package net.lokmane.projectandroidgestionstock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {
    Button btnChooseImage;
    Button btnUploadImage;
    EditText editTextNomProduit;
    EditText editTextQuantite;
    Button btnAjouter;

    ProgressDialog dialog;
    JSONParser parser = new JSONParser();
    int success;

    Bitmap captureImage;

    final int CODE_GALLERY_REQUEST = 999;
    //String urlUpload = "http://192.168.83.120/stock/upload.php";
    String urlUpload = "";

    Bitmap bitmap;

    ImageView imageUpload;

    byte[] bArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        btnChooseImage = findViewById(R.id.btnChooseImage);
        //btnUploadImage = findViewById(R.id.btnUploadImage);
        editTextNomProduit = findViewById(R.id.editTextNomProduit);
        editTextQuantite = findViewById(R.id.editTextQuantite);
        btnAjouter = findViewById(R.id.btnAjouter);
        imageUpload = findViewById(R.id.imageUpload);

        urlUpload = getString(R.string.host)+"save.php";



       btnAjouter.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               // new Add().execute();
               saveData();
           }
       });


        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Démarrer la caméra et attendre le résultat
               // startActivityForResult(intent, 100);


                ActivityCompat.requestPermissions(
                       AddProductActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                       CODE_GALLERY_REQUEST
                );
            }
        });


        /*btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
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
                        String imageData = imageToString(bitmap);
                        params.put("image", imageData);

                        return params;
                    }
                };

                RequestQueue queue =  Volley.newRequestQueue(AddProductActivity.this);
                queue.add(stringRequest);
            }
        });*/





    }

    private void saveData(){
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        AddProductActivity.super.onBackPressed();
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
                String imageData = imageToString(bitmap);
                params.put("image", imageData);
                params.put("nomProduit", editTextNomProduit.getText().toString());
                params.put("quantiteProduit", editTextQuantite.getText().toString());

                return params;
            }
        };

        RequestQueue queue =  Volley.newRequestQueue(AddProductActivity.this);
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













    class Add extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddProductActivity.this);
            dialog.setMessage("Patientez SVP");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> map = new HashMap<String, String>();

            map.put("nomProduit", editTextNomProduit.getText().toString());
            map.put("quantiteProduit", editTextQuantite.getText().toString());
            Toast.makeText(getApplicationContext(), Arrays.toString(bArray), Toast.LENGTH_LONG).show();
            map.put("image", Arrays.toString(bArray));



            JSONObject object = parser.makeHttpRequest("192.168.137.237/Stock/add.php", "GET", map);
            try {
                success = object.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.cancel();
            if(success==1){
                Toast.makeText(AddProductActivity.this, "Ajout effectué", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(AddProductActivity.this, ListPrductsActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(AddProductActivity.this, "Echec!!!", Toast.LENGTH_LONG).show();
            }
        }
    }
}