package com.app.requirements.visualization.text.analyzer;

import com.app.requirements.visualization.text.analyzer.api.NLPResources;
import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.models.CategorizedEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class AnalyticalUtility {

    private final UserDictionaryEntitiesAnalyst userDictionaryEntitiesAnalyst = new UserDictionaryEntitiesAnalyst();
    private final NLPResources NLPResources = new NLPResources();
    private final Map<String, String> tokenizedUserStoryMap = new HashMap<>();
    private List<String> foundRoles = new ArrayList<>();
    private Map<String, String> posTagMap = new HashMap<>();
    private NavigableMap<String, String> firstPersonBenefitAction = new TreeMap<>();
    private NavigableMap<String, String> firstPersonActionAction = new TreeMap<>();

    public void startAnalysis(Map<String, String> userStoryMap, Map<String, String> userDictionary, String userStoryAll) throws IOException {
        TextAnalyticsClient client = initializeNER();
        tokenizedUserStoryMap.putAll(userStoryMap);
        userDictionaryEntitiesAnalyst.setUserDictionary(userDictionary);
        findRoles(userStoryAll, client);
        findMainRoleActions(userStoryAll);
        findKeywordsInUserDictionary();
    }

    private TextAnalyticsClient initializeNER() {
        return NLPResources.authenticateClient();
    }

    private void findRoles(String userStoryMap, TextAnalyticsClient client) {
        foundRoles = NLPResources.recognizeEntities(client, userStoryMap).stream()
                .map(CategorizedEntity::getText)
                .collect(Collectors.toList());
    }

    private void findMainRoleActions(String userStory) throws IOException {
        posTagMap = NLPResources.extractPartsOfSpeech(userStory);
        List<String> foundAction = NLPResources.findAllPersonaActions();
        NLPResources.findPersonaActionsInTokens(foundAction, tokenizedUserStoryMap);
        NLPResources.findActionsComplement(tokenizedUserStoryMap);
        firstPersonActionAction = NLPResources.getFirstPersonActionAction();
        firstPersonBenefitAction = NLPResources.getFirstPersonBenefitAction();
    }

    private void findKeywordsInUserDictionary() {
        userDictionaryEntitiesAnalyst.lookForRolesInUserDictionary(foundRoles);
        userDictionaryEntitiesAnalyst.lookForActionsInUserDictionary(firstPersonActionAction);
        userDictionaryEntitiesAnalyst.lookForComplementsInUserDictionary(firstPersonBenefitAction);
    }
}
