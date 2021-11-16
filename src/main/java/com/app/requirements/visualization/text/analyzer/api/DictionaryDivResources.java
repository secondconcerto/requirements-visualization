package com.app.requirements.visualization.text.analyzer.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DictionaryDivResources {

    private final String dictionaryDivURL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

    public List<String> performRequest(String wordToSearch) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(dictionaryDivURL + wordToSearch)
                .get()
                .build();

        Response response = client.newCall(request).execute();

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
