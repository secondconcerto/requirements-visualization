package com.app.requirements.visualization.text.analyzer.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DictionaryAPIResources {

    private final String dictionaryURL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

    public List<String> performRequest(String wordToSearch) throws IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(dictionaryURL + wordToSearch)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        /*OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://english-synonyms.p.rapidapi.com/" + wordToSearch)
                .get()
                .addHeader("x-rapidapi-host", "english-synonyms.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "0547043824mshc6f0e2f133b5910p116908jsned9738fc8068")
                .build();*/

        /*Response response = client.newCall(request).execute();*/

        if (response.isSuccessful()) {
            return extractSynonymsFromResponse(response, wordToSearch);
        }
        return new ArrayList<>();
    }

    private List<String> extractSynonymsFromResponse(Response response, String wordToSearch) throws IOException {
        String myResponse = response.peekBody(1000000).string();
        StringBuilder stringJSONBuilder = new StringBuilder(myResponse);
        stringJSONBuilder.deleteCharAt(0);
        stringJSONBuilder.deleteCharAt(myResponse.length() - 2);
        JSONObject obj = new JSONObject(stringJSONBuilder.toString());
        JSONArray arr = obj.getJSONArray("meanings");
        JSONObject obj2 = arr.getJSONObject(0);
        JSONArray arr2 = obj2.getJSONArray("definitions");
        JSONObject obj3 = arr2.getJSONObject(0);
        JSONArray arr3 = obj3.getJSONArray("synonyms");
        List<String> synonyms = new ArrayList<>();
        if (arr3.length() > 10) {
            for (int i = 0; i < 10; i++) {
                String synonym = arr3.getString(i);
                synonyms.add(synonym);
            }
        } else {
            for (int i = 0; i < arr3.length(); i++) {
                String synonym = arr3.getString(i);
                synonyms.add(synonym);
            }
        }
        synonyms.add(wordToSearch);
        return synonyms;
    }
}
