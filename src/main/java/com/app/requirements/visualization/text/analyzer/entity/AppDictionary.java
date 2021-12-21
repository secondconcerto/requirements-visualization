package com.app.requirements.visualization.text.analyzer.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AppDictionary {

    public static HashMap<String, List<String>> appTerms;

    static {
        appTerms = new HashMap<>();
        appTerms.put("enter", Arrays.asList("form"));
        appTerms.put("input", Arrays.asList("form"));
        appTerms.put("form", Arrays.asList("form"));
        appTerms.put("forms", Arrays.asList("form"));
        appTerms.put("contact", Arrays.asList("contact"));
        appTerms.put("contacts", Arrays.asList("contact"));
        appTerms.put("email", Arrays.asList("contact"));
        appTerms.put("emails", Arrays.asList("contact"));
        appTerms.put("filter", Arrays.asList("filter"));
        appTerms.put("data", Arrays.asList("data"));
        appTerms.put("date", Arrays.asList("date"));
        appTerms.put("dates", Arrays.asList("date"));
        appTerms.put("days", Arrays.asList("date"));
        appTerms.put("day", Arrays.asList("date"));
        appTerms.put("calendar", Arrays.asList("date"));
        appTerms.put("calendars", Arrays.asList("date"));
        appTerms.put("problem", Arrays.asList("problem"));
        appTerms.put("problems", Arrays.asList("problem"));
        appTerms.put("profile", Arrays.asList("profile"));
        appTerms.put("profiles", Arrays.asList("profile"));
        appTerms.put("support", Arrays.asList("problem", "contact"));
        appTerms.put("helpdesk", Arrays.asList("problem", "contact"));
        appTerms.put("refresh", Arrays.asList("update"));
        appTerms.put("update", Arrays.asList("update"));
        appTerms.put("reload", Arrays.asList("update"));
    }

    public HashMap<String, List<String>> getAppTerms() {
        return appTerms;
    }

}
