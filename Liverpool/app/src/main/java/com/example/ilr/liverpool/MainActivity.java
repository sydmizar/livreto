package com.example.ilr.liverpool;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText productText;
    TextView responseView;
    ProgressBar progressBar;
    static final String API_URL = "https://shoppapp.liverpool.com.mx/appclienteservices/services/plp?";
    private String mSearchString;
    private static final String SEARCH_KEY = "search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseView = (TextView) findViewById(R.id.responseView);
        productText = (EditText) findViewById(R.id.productText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //responseView = (ListView) findViewById(R.id.responseView);

        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveFeedTask().execute();
            }
        });

        if (savedInstanceState != null) {
            mSearchString = savedInstanceState.getString(SEARCH_KEY);
        }
    }

    @Override

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(SEARCH_KEY, "onSaveInstanceState");

        final EditText textBox =
                (EditText) findViewById(R.id.productText);
        CharSequence userText = textBox.getText();
        outState.putCharSequence("savedText", userText);

    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            String product = productText.getText().toString();
            // Do some validation here

            try {
                URL url = new URL(API_URL + "search-string=" + product);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            //responseView.setText(response);
            // TODO: check this.exception
            // TODO: do something with the feed

            try {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONObject results = object.getJSONObject("plpResults");
                JSONArray resultObject = (JSONArray) results.getJSONArray("refinementGroups");
                JSONObject resultMarcas = (JSONObject) resultObject.get(1);

                try{
                    ProductJSONParser productJsonParser = new ProductJSONParser();
                    productJsonParser.parse(resultMarcas);
                }catch(Exception e){
                    Log.d("JSON Exception1",e.toString());
                }

                ProductJSONParser productJsonParser = new ProductJSONParser();

                List<HashMap<String, String>> countries = null;

                try{
                    countries = productJsonParser.parse(resultMarcas);
                }catch(Exception e){
                    Log.d("Exception",e.toString());
                }

                String[] from = { "product","details"};

                int[] to = { R.id.tv_product,R.id.tv_product_details};
                SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), countries, R.layout.lv_layout, from, to);



                ListView productView = (ListView) findViewById(R.id.responseLView);
                productView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

