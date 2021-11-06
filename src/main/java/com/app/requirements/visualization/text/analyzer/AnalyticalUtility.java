package com.app.requirements.visualization.text.analyzer;

import com.app.requirements.visualization.text.analyzer.api.NLPResources;
import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.models.CategorizedEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalyticalUtility {

    private EntitiesAnalyst entitiesAnalyst = new EntitiesAnalyst();
    private final NLPResources NLPResources = new NLPResources();
    private Map<String, String> originalUserStoryMap = new HashMap<>();
    private List<String> foundRoles = new ArrayList<>();


    public void startAnalysis(Map<String, String> userStoryMap, Map<String, String> userDictionary, String userStoryAll) throws IOException {
        TextAnalyticsClient client = initializeNER();
        originalUserStoryMap.putAll(entitiesAnalyst.lookFoRoles(userDictionary, userStoryMap));
        findRoles(userStoryAll, client);

    }

    private TextAnalyticsClient initializeNER() {
        return NLPResources.authenticateClient();
    }


    private void findRoles(String userStoryMap, TextAnalyticsClient client) throws IOException {
        foundRoles = NLPResources.recognizeEntities(client, userStoryMap).stream()
                .map(CategorizedEntity::getText)
                .collect(Collectors.toList());
    }

    private void findMainRoleAction(String userStoryMap, TextAnalyticsClient client) throws IOException {
        foundRoles = NLPResources.recognizeEntities(client, userStoryMap).stream()
                .map(CategorizedEntity::getText)
                .collect(Collectors.toList());
    }
}
