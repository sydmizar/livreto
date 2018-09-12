package com.example.ilr.liverpool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductJSONParser {

    /** Receives a JSONObject and returns a list */
    public List<HashMap<String,String>> parse(JSONObject jObject){

        JSONArray jProducts = null;
        try {
            /** Retrieves all the elements in the 'countries' array */
            //jProducts = jObject.getJSONArray("countries");

            jProducts = jObject.getJSONArray("refinement");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /** Invoking getCountries with the array of json object
         * where each json object represent a country
         */
        return getProducts(jProducts);
    }

    private List<HashMap<String, String>> getProducts(JSONArray jProducts){
        int productCount = jProducts.length();
        List<HashMap<String, String>> productList = new ArrayList<HashMap<String,String>>();
        HashMap<String, String> product = null;

        /** Taking each country, parses and adds to list object */
        for(int i=0; i<productCount;i++){
            try {
                /** Call getCountry with country JSON object to parse the country */
                product = getProduct((JSONObject)jProducts.get(i));
                productList.add(product);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return productList;
    }

    /** Parsing the Country JSON object */
    private HashMap<String, String> getProduct(JSONObject jProduct){

        HashMap<String, String> product = new HashMap<String, String>();
        String productName = "";
        String stocks="";


        try {
            productName = jProduct.getString("label");
            stocks = jProduct.getString("count");


            String details =        "Producto : " + productName + "\n" +
                    "Existencias : " + stocks + "\n";

            product.put("product", productName);
          //  product.put("stocks", stocks);
            product.put("details", details);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return product;
    }
}