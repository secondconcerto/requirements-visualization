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
        action = action.toLowerCase();
        mapOfActionsWithOptionalDescriptions.put(action, userDictionary.getOrDefault(action, ""));

        String complement = actionMap.get("complement");
        String[] tokenizedComplement = complement
                .split("[[ ]*|[,]*|[\\.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+");

        for (String word : tokenizedComplement) {
            complement = complement.toLowerCase();
            String finalComplement = complement;
            mapWithOptionalDescription.put(complement, userDictionary.keySet().stream()
                    .filter(entry -> entry.contains(finalComplement))
                    .findFirst()
                    .orElse(""));
        }

        return mapOfActionsWithOptionalDescriptions;
    }
}
