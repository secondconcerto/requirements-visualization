package com.app.requirements.visualization.text.analyzer;

import com.app.requirements.visualization.text.analyzer.entity.AppDictionary;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FormulateRequirements {

    private final AppDictionary appDictionary = new AppDictionary();

    private final List<String> finalRequirements = new ArrayList<>();
    private Map<String, String> rolesInUserDictionary;
    private List<String> foundRoles;
    private Map<String, String> actionInUserDictionary;
    private Map<String, String> benefitInUserDictionary;
    private Map<String, List<String>> allRequirements = new HashMap<>();

    public FormulateRequirements(Map<String, String> rolesInUserDictionary, List<String> foundRoles,
                                 Map<String, String> actionInUserDictionary, Map<String, String> benefitInUserDictionary) {
        this.rolesInUserDictionary = rolesInUserDictionary;
        this.foundRoles = foundRoles;
        this.actionInUserDictionary = actionInUserDictionary;
        this.benefitInUserDictionary = benefitInUserDictionary;
    }

    public Map<String, List<String>> findTermsInAppDictionary(Map<String, List<String>> termsMap) {
        addRolesToRequirements(foundRoles);
        ArrayList<Map.Entry<String, String>> foundTermsMap = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : termsMap.entrySet()) {
            Collection<Map.Entry<String, String>> list = lookForTerms(entry.getKey(), entry.getValue());
            foundTermsMap.addAll(list);
        }
        Map<String, String> mapFoFoundTerms = foundTermsMap.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)); //todo change fo to to
        checkWhatRequirementsIsToPerform(mapFoFoundTerms);
        return allRequirements;
    }

    private void addRolesToRequirements(List<String> foundRoles) {
        finalRequirements.add("Your app will need such roles:" + foundRoles.toString());
    }

    private List lookForTerms(String userWord, List<String> synonyms) {
        return synonyms.stream()
                .filter(entry -> appDictionary.getAppTerms().containsKey(entry))
                .map(entry -> new AbstractMap.SimpleEntry(userWord, appDictionary.getAppTerms().get(entry)))
                .collect(Collectors.toList());
    }

    private void checkWhatRequirementsIsToPerform(Map<String, String> foundTermsMap) {
        for (Map.Entry<String, String> entry : foundTermsMap.entrySet()) {
            findRequirement(entry.getKey(), entry.getValue());
        }
        allRequirements.put("text", finalRequirements);
    }

    private void findRequirement(String userWord, String term) {
        switch (term) {
            case "userInput":
                finalRequirements.add("You need a field to take user input in the view. \n");
                break;
            case "filter":
                finalRequirements.add("You need an option to filter data. \n");
                break;
            case "data":
                finalRequirements.add("Perhaps you will need a data structure to store information.");
                if (actionInUserDictionary.containsValue("data")) {
                    String valueByKey = getValueByKey("data", actionInUserDictionary);
                    finalRequirements.add("You will need such columns: " + valueByKey);
                    String[] tokenizedWord = valueByKey
                            .split(",");
                    allRequirements.put("columns", Arrays.asList(tokenizedWord));
                } else if (benefitInUserDictionary.containsValue("data")) {
                    String valueByKey = getValueByKey("data", benefitInUserDictionary);
                    finalRequirements.add("You will need such columns: " + valueByKey);
                    String[] tokenizedWord = valueByKey
                            .split(",");
                    allRequirements.put("columns", Arrays.asList(tokenizedWord));
                } else if (actionInUserDictionary.containsValue("columns")) {
                    String valueByKey = getValueByKey("columns", actionInUserDictionary);
                    finalRequirements.add("You will need such columns: " + valueByKey);
                    String[] tokenizedWord = valueByKey
                            .split(",");
                    allRequirements.put("columns", Arrays.asList(tokenizedWord));
                } else if (benefitInUserDictionary.containsValue("columns")) {
                    String valueByKey = getValueByKey("columns", benefitInUserDictionary);
                    finalRequirements.add("You will need such columns: " + valueByKey);
                    String[] tokenizedWord = valueByKey
                            .split(",");
                    allRequirements.put("columns", Arrays.asList(tokenizedWord));
                }
                break;
            default:
                System.out.println("No requirements were found :( ");
        }
    }

    private String getValueByKey(String value, Map<String, String> map) {
        return map.entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst().orElse("");
    }
}
