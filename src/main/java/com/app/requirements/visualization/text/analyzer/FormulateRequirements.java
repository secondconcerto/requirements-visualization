package com.app.requirements.visualization.text.analyzer;

import com.app.requirements.visualization.text.analyzer.entity.AppDictionary;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FormulateRequirements {

    private final AppDictionary appDictionary = new AppDictionary();

    private final List<String> finalRequirements = new ArrayList<>();
    Map<String, String> rolesInUserDictionary;
    List<String> foundRoles;
    Map<String, String> actionInUserDictionary;
    Map<String, String> benefitInUserDictionary;

    public FormulateRequirements(Map<String, String> rolesInUserDictionary, List<String> foundRoles,
                                 Map<String, String> actionInUserDictionary, Map<String, String> benefitInUserDictionary) {
        this.rolesInUserDictionary = rolesInUserDictionary;
        this.foundRoles = foundRoles;
        this.actionInUserDictionary = actionInUserDictionary;
        this.benefitInUserDictionary = benefitInUserDictionary;
    }

    public List<String> findTermsInAppDictionary(Map<String, List<String>> termsMap) {
        addRolesToRequirements(foundRoles);
        ArrayList<Map.Entry<String, String>> foundTermsMap = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : termsMap.entrySet()) {
            Collection<Map.Entry<String, String>> list = lookForTerms(entry.getKey(), entry.getValue());
            foundTermsMap.addAll(list);
        }
        Map<String, String> mapFoFoundTerms = foundTermsMap.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)); //todo change fo to to
        checkWhatRequirementsIsToPerform(mapFoFoundTerms);
        return finalRequirements;
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
                    finalRequirements.add("You will need such columns: " + actionInUserDictionary.get("data"));
                } else if (benefitInUserDictionary.containsValue("data")) {
                    finalRequirements.add("You will need such columns: " + benefitInUserDictionary.get("data"));
                }
                //TODO zrobic wyszukiwanie klucza w mapie na podstawie wartosci linie 72 i 74
                // pododawac do slownika slowa
                // html return do zrobienia dalej

                break;
            default:
                System.out.println("No requirements were found :( ");
        }
    }
}
