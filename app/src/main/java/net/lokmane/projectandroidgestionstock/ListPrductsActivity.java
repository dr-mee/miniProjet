package net.lokmane.projectandroidgestionstock;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListPrductsActivity extends AppCompatActivity {
    ListView listViewProduits;
    Button btn_add;

    ProgressDialog dialog;
    JSONParser parser = new JSONParser();
    ArrayList<HashMap<String, String>> values = new ArrayList<HashMap<String, String>>();
    int success;
    ArrayList<Product> data = new ArrayList<Product>();

    private static int RESULT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_prducts);

        btn_add = (Button) findViewById(R.id.addProduct);
        listViewProduits = findViewById(R.id.listViewProduits);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddProductActivity.class);
                startActivityForResult(intent, RESULT_CODE);
            }
        });

        getData();

        listViewProduits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "Product id: "+data.get(i).getId(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
                intent.putExtra("id", ""+data.get(i).getId());
                intent.putExtra("name", ""+data.get(i).getName());
                intent.putExtra("quantity", ""+data.get(i).getQuantity());
                intent.putExtra("image", ""+data.get(i).getImage());
                startActivityForResult(intent, RESULT_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("RESULTCODE", resultCode+"");
        getData();

    }

    private void getData(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getString(R.string.host)+"all.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "ALL DATA", Toast.LENGTH_LONG).show();
                        // Display the first 500 characters of the response string.

                        try {
                            JSONObject res = new JSONObject(response);
                            success = res.getInt("success");
                            if(success==1){
                                data.clear();
                                JSONArray products = res.getJSONArray("produits");


                                for(int i=0; i<products.length(); i++){

                                    JSONObject object1 = (JSONObject) products.get(i);
                                    HashMap<String, String> map1 = new HashMap<>();
                                    Product p = new Product(
                                            object1.getInt("id"),
                                            object1.getString("nomProduit"),
                                            object1.getInt("quantiteProduit"),
                                            object1.getString("image")
                                    );


                                    data.add(p);
                                    Log.i("info1", String.valueOf(values));

                                }
                                CustomAdapter customAdapter = new CustomAdapter(getApplicationContext());
                                listViewProduits.setAdapter(customAdapter);

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
        });

        RequestQueue queue =  Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);


    }

    private class CustomAdapter extends BaseAdapter {

        Context mContext;

        public CustomAdapter(Context context){
            mContext = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {

            view = LayoutInflater.from(mContext).inflate(R.layout.item, null);

            ImageView imageProduct = (ImageView) view.findViewById(R.id.imageViewProduit);
            /*TextView id = (TextView) view.findViewById(R.id.textViewId);*/
            TextView name = (TextView) view.findViewById(R.id.textViewNomProduit);
            TextView quantity = (TextView) view.findViewById(R.id.textViewQuantiteProduit);

            Product p = data.get(i);

           /* id.setText(""+p.getId());*/
            name.setText(p.getName());
            quantity.setText(""+p.getQuantity());

            String imageUri = getString(R.string.host)+p.getImage();
            Picasso.with(mContext).load(imageUri).into(imageProduct);


            return view;
        }

    }


    class All extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ListPrductsActivity.this);
            dialog.setMessage("Patientez SVP");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONObject object = new JSONObject();
            HashMap<String, String> map = new HashMap<>();
            object = parser.makeHttpRequest("http://192.168.1.118/User/all.php", "GET", map);
            try {
                success = object.getInt("success");
                if(success==1){
                    JSONArray users = object.getJSONArray("produits");


                    for(int i=0; i<users.length(); i++){
                        HashMap<String, String> map1 = new HashMap<>();
                        JSONObject object1 = (JSONObject) users.get(i);
                        map1.put("id", object1.getString("id"));
                        map1.put("nomProduit", object1.getString("nomProduit"));
                        map1.put("quantiteProduit", object1.getString("quantiteProduit"));
                        map1.put("image", object1.getString("image"));

                        values.add(map1);
                        Log.i("info1", String.valueOf(values));
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.cancel();
            //SimpleAdapter adapter = new SimpleAdapter(ListPrductsActivity.this, values, R.layout.item, new String[]{"id", "nomProduit", "quantiteProduit", "image"}, new int[]{R.id.textViewId, R.id.textViewNomProduit, R.id.textViewQuantiteProduit, R.id.imageViewProduit});
            //listViewProduits.setAdapter(adapter);
        }
    }


}