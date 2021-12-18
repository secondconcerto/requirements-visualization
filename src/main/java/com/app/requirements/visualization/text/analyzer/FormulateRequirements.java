package com.app.requirements.visualization.text.analyzer;

import com.app.requirements.visualization.text.analyzer.entity.AppDictionary;

import java.util.*;
import java.util.stream.Collectors;

public class FormulateRequirements {

    private final AppDictionary appDictionary = new AppDictionary();

    private final Set<String> finalRequirements = new HashSet<>();
    private final Set<String> UIRequirements = new HashSet<>();
    private Map<String, String> rolesInUserDictionary;
    private Set<String> foundRoles = new HashSet<>();
    private Set<String> foundKeyPhrases =  new HashSet<>();
    private Map<String, String> actionInUserDictionary;
    private Map<String, String> benefitInUserDictionary;
    private Map<String, Set<String>> allRequirements = new HashMap<>();

    public FormulateRequirements(Map<String, String> rolesInUserDictionary, Set<String> foundRoles,
                                 Map<String, String> actionInUserDictionary, Map<String, String> benefitInUserDictionary, Set<String> foundKeyPhrases) {
        this.rolesInUserDictionary = rolesInUserDictionary;
        this.foundRoles = foundRoles;
        this.actionInUserDictionary = actionInUserDictionary;
        this.benefitInUserDictionary = benefitInUserDictionary;
        this.foundKeyPhrases = foundKeyPhrases;
    }

    public Map<String, Set<String>> findTermsInAppDictionary(Map<String, List<String>> synonymsMap) {
        addRolesToRequirements(foundRoles);
        ArrayList<Map.Entry<String, String>> foundTermsFromProjectDictionary = new ArrayList<>();
        for (Map.Entry<String, List<String>> synonym : synonymsMap.entrySet()) {
            Collection<Map.Entry<String, String>> foundTerms = lookForTermsInProjectDictionary(synonym.getKey(), synonym.getValue());
            foundTermsFromProjectDictionary.addAll(foundTerms);
        }
        Map<String, String> mapToFoundTerms = foundTermsFromProjectDictionary.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        checkWhatRequirementsIsToPerform(mapToFoundTerms);

        return allRequirements;
    }

    private void addRolesToRequirements(Set<String> foundRoles) {
        finalRequirements.add("Your app will need such roles:" + foundRoles.toString());
    }

    private List lookForTermsInProjectDictionary(String userWord, List<String> synonyms) {
        return synonyms.stream()
                .filter(entry -> appDictionary.getAppTerms().containsKey(entry))
                .map(entry -> new AbstractMap.SimpleEntry(userWord, appDictionary.getAppTerms().get(entry)))
                .collect(Collectors.toList());
    }

    private void checkWhatRequirementsIsToPerform(Map<String, String> foundTermsMap) {
        for (Map.Entry<String, String> entry : foundTermsMap.entrySet()) {
            findRequirement(entry.getValue());
        }
        allRequirements.put("text", finalRequirements);
        allRequirements.put("ui", UIRequirements);
        allRequirements.put("keyPhrases", foundKeyPhrases);
    }

    private void findRequirement(String term) {
        switch (term) {
            case "form":
            case "input":
                finalRequirements.add("You need a field to take user input in the view. It may be simple area of more complex form \n");
                UIRequirements.add("form");
                break;
            case "filter":
                finalRequirements.add("You need an option to filter data. \n");
                UIRequirements.add("filter");
                break;
            case "enter":
                finalRequirements.add("You need some text area so user can enter input. Perhaps some form would be useful.\n");
                UIRequirements.add("input");
                break;
            case "data":
                finalRequirements.add("Perhaps you will need a data structure to store information.");
                prepareColumnInDataTable(term);
                UIRequirements.add("table");
                break;
            case "date":
                finalRequirements.add("You must have structure to keep important dates.");
                UIRequirements.add("date");
                break;
            default:
                System.out.println("No requirements were found :( ");
        }
    }

    private void prepareColumnInDataTable(String term) {
        if (actionInUserDictionary.containsValue("data")) {
            String valueByKey = getValueByKey("data", actionInUserDictionary);
            finalRequirements.add("You will need such columns: " + valueByKey);
            String[] tokenizedWord = valueByKey
                    .split(",");
            allRequirements.put("columns", new HashSet<>(Arrays.asList(tokenizedWord)));
        } else if (benefitInUserDictionary.containsValue("data")) {
            String valueByKey = getValueByKey("data", benefitInUserDictionary);
            finalRequirements.add("You will need such columns: " + valueByKey);
            String[] tokenizedWord = valueByKey
                    .split(",");
            allRequirements.put("columns", new HashSet<>(Arrays.asList(tokenizedWord)));

        } else if (actionInUserDictionary.containsValue(term)) {
            String valueByKey = getValueByKey(term, actionInUserDictionary);
            finalRequirements.add("You will need such columns: " + valueByKey);
            String[] tokenizedWord = valueByKey
                    .split(",");
            allRequirements.put("columns", new HashSet<>(Arrays.asList(tokenizedWord)));
        } else if (benefitInUserDictionary.containsValue(term)) {
            String valueByKey = getValueByKey(term, benefitInUserDictionary);
            finalRequirements.add("You will need such columns: " + valueByKey);
            String[] tokenizedWord = valueByKey
                    .split(",");
            allRequirements.put("columns", new HashSet<>(Arrays.asList(tokenizedWord)));

        } else if (actionInUserDictionary.containsValue("columns")) {
            String valueByKey = getValueByKey("columns", actionInUserDictionary);
            finalRequirements.add("You will need such columns: " + valueByKey);
            String[] tokenizedWord = valueByKey
                    .split(",");
            allRequirements.put("columns", new HashSet<>(Arrays.asList(tokenizedWord)));
        } else if (benefitInUserDictionary.containsValue("columns")) {
            String valueByKey = getValueByKey("columns", benefitInUserDictionary);
            finalRequirements.add("You will need such columns: " + valueByKey);
            String[] tokenizedWord = valueByKey
                    .split(",");
            allRequirements.put("columns", new HashSet<>(Arrays.asList(tokenizedWord)));
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
