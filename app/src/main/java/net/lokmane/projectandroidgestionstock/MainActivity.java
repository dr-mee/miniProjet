package net.lokmane.projectandroidgestionstock;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button btnLogin;

    ProgressDialog dialog;
    JSONParser parser = new JSONParser();
    int success;
    HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, email.getText().toString(), Toast.LENGTH_LONG).show();
                //Toast.makeText(MainActivity.this, password.getText().toString(), Toast.LENGTH_LONG).show();
                Auth();



            }
        });
    }



    private void Auth(){
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.host)+"select_one.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        //MainActivity.super.onBackPressed();
                        // Display the first 500 characters of the response string.

                        try {
                            JSONObject res = new JSONObject(response);
                            success = res.getInt("success");
                            if(success==1){
                                Toast.makeText(MainActivity.this, "User Exist", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, ListPrductsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
                            else {
                                Toast.makeText(MainActivity.this, "User Not Exist", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


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
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());

                return params;
            }
        };

        RequestQueue queue =  Volley.newRequestQueue(MainActivity.this);
        queue.add(stringRequest);
    }






    class Auth extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Patientez SVP");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                map = new HashMap<String, String>();
                map.put("email", email.getText().toString());
                map.put("password", password.getText().toString());

            }catch (Exception e){
                e.printStackTrace();
            }


            JSONObject object = parser.makeHttpRequest(getString(R.string.host)+"select_one.php", "GET", map);
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
                Toast.makeText(MainActivity.this, "User Exist", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, ListPrductsActivity.class);
                startActivity(intent);


            }
            else {
                Toast.makeText(MainActivity.this, "User Not Exist", Toast.LENGTH_LONG).show();
            }
        }
    }





}