package com.app.requirements.visualization.text.analyzer.entity;

import java.util.HashMap;

public class AppDictionary {

    public static HashMap<String, String> appTerms;

    static {
        appTerms = new HashMap<>();
        appTerms.put("enter", "userInput");
        appTerms.put("filter", "filter");
        appTerms.put("data", "data");
    }

    public HashMap<String, String> getAppTerms() {
        return appTerms;
    }

}
