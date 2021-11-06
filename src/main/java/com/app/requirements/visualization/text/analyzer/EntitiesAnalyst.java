package com.app.requirements.visualization.text.analyzer;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class EntitiesAnalyst {

    public Map<String, String> lookFoRoles(Map<String, String> userDictionary, Map<String, String> userStoryMap) {
        Collection<String> wordsFromUserStory = userStoryMap.values();
        return userDictionary.entrySet().stream()
                .filter(entry -> wordsFromUserStory.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
