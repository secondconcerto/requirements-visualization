package com.app.requirements.visualization.text.analyzer.entity;

import java.util.HashMap;

public class AppDictionary {

    public static HashMap<String, String> appTerms;

    static {
        appTerms = new HashMap<>();
        appTerms.put("enter", "form");
        appTerms.put("input", "form");
        appTerms.put("form", "form");
        appTerms.put("forms", "form");
        appTerms.put("contact", "contact");
        appTerms.put("contacts", "contact");
        appTerms.put("email", "contact");
        appTerms.put("emails", "contact");
        appTerms.put("filter", "filter");
        appTerms.put("data", "data");
        appTerms.put("datas", "data");
        appTerms.put("date", "date");
        appTerms.put("dates", "date");
        appTerms.put("days", "date");
        appTerms.put("day", "date");
        appTerms.put("calendar", "date");
        appTerms.put("calendars", "date");
    }

    public HashMap<String, String> getAppTerms() {
        return appTerms;
    }

}
