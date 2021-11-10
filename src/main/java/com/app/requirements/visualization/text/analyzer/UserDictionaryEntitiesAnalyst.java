package com.app.requirements.visualization.text.analyzer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.stream.Collectors;

public class UserDictionaryEntitiesAnalyst {

    private Map<String, String> userDictionary = new HashMap<>();
    private final Map<String, String> mapOfRolesWithOptionalDescriptions = new HashMap<>();

    public void setUserDictionary(Map<String, String> userDictionary) {
        this.userDictionary = userDictionary;
    }

    public Map<String, String> lookForRolesInUserDictionary(Map<String, String> userDictionary, Map<String, String> userStoryMap) {
        Collection<String> wordsFromUserStory = userStoryMap.values();
        return userDictionary.entrySet().stream()
                .filter(entry -> wordsFromUserStory.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void lookForRolesInUserDictionary(List<String> foundRoles) {
        for (String role : foundRoles) {
            mapOfRolesWithOptionalDescriptions.put(role, userDictionary.getOrDefault(role, ""));
        }
    }

    public void lookForActionsInUserDictionary(NavigableMap<String, String> firstPersonBenefitAction) {
    }

    public void lookForComplementsInUserDictionary(NavigableMap<String, String> firstPersonBenefitAction) {
    }
}
