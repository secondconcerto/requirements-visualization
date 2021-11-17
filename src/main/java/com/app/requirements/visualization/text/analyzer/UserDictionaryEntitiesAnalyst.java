package com.app.requirements.visualization.text.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

public class UserDictionaryEntitiesAnalyst {

    private final Map<String, String> mapOfRolesWithOptionalDescriptions = new HashMap<>();
    private final Map<String, String> mapOfActionsWithOptionalDescriptions = new HashMap<>();
    private final Map<String, String> mapOfActionBenefitWithOptionalDescriptions = new HashMap<>();
    private Map<String, String> userDictionary = new HashMap<>();

    public void setUserDictionary(Map<String, String> userDictionary) {
        this.userDictionary = userDictionary;
        mapOfRolesWithOptionalDescriptions.clear();
        mapOfActionsWithOptionalDescriptions.clear();
        mapOfActionBenefitWithOptionalDescriptions.clear();
    }

    public Map<String, String> lookForRolesInUserDictionary(List<String> foundRoles) {
        for (String role : foundRoles) {
            role = role.toLowerCase();
            mapOfRolesWithOptionalDescriptions.put(role, userDictionary.getOrDefault(role, ""));
        }
        return mapOfRolesWithOptionalDescriptions;
    }

    public Map<String, String> lookForActionsInUserDictionary(NavigableMap<String, String> firstPersonActionAction) {
        return getOptionalDescriptionMap(firstPersonActionAction, mapOfActionsWithOptionalDescriptions);
    }

    public Map<String, String> lookForComplementsInUserDictionary(NavigableMap<String, String> firstPersonBenefitAction) {
        return getOptionalDescriptionMap(firstPersonBenefitAction, mapOfActionBenefitWithOptionalDescriptions);
    }

    private Map<String, String> getOptionalDescriptionMap(NavigableMap<String, String> actionMap, Map<String, String> mapWithOptionalDescription) {

        String action = actionMap.get("action");
        lookUpInUserMap(mapWithOptionalDescription, action);

        String complement = actionMap.get("complement");
        lookUpInUserMap(mapWithOptionalDescription, complement);
        return mapWithOptionalDescription;
    }

    private void lookUpInUserMap(Map<String, String> mapWithOptionalDescription, String token) {
        String[] tokenizedArray = token
                .split("[[ ]*|[,]*|[\\.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+");

        for (String word : tokenizedArray) {
            word = word.toLowerCase();
            String finalComplement = word;
            mapWithOptionalDescription.put(userDictionary.entrySet().stream()
                    .filter(entry -> check(entry.getKey(), finalComplement))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(""), word);
        }
    }

    private boolean check(String userDictionaryKey, String finalComplement) {
        String[] tokenizedArray = userDictionaryKey
                .split("[[ ]*|[,]*|[\\.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+");
        for (String word : tokenizedArray) {
            word = word.toLowerCase();
            if (word.equals(finalComplement)) {
                return true;
            }
        }
        return false;
    }
}
