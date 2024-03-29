package com.app.requirements.visualization.text.analyzer;

import com.app.requirements.visualization.text.analyzer.api.DictionaryAPIResources;
import com.app.requirements.visualization.text.analyzer.api.NLPResources;
import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.models.CategorizedEntity;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AnalyticalUtility {

    private final UserDictionaryEntitiesAnalyst userDictionaryEntitiesAnalyst = new UserDictionaryEntitiesAnalyst();
    private final NLPResources NLPResources = new NLPResources();
    private final DictionaryAPIResources dictionaryAPIResources = new DictionaryAPIResources();

    private final Map<String, String> tokenizedUserStoryMap = new HashMap<>();
    private Set<String> foundRoles = new HashSet<>();
    private Set<String> foundKeyPhrases = new HashSet<>();
    private NavigableMap<String, String> firstPersonBenefitAction = new TreeMap<>();
    private NavigableMap<String, String> firstPersonActionAction = new TreeMap<>();
    private Map<String, String> rolesInUserDictionary = new HashMap<>();
    private Map<String, String> actionInUserDictionary = new HashMap<>();
    private Map<String, String> benefitInUserDictionary = new HashMap<>();
    private Map<String, List<String>> synonymMap = new HashMap<>();

    public Map<String, Set<String>> startAnalysis(Map<String, String> userStoryMap, Map<String, String> userDictionary, String userStoryAsOneSentence) throws IOException {
        initializeUtility();
        TextAnalyticsClient client = initializeNER();
        tokenizedUserStoryMap.putAll(userStoryMap);
        userDictionaryEntitiesAnalyst.setUserDictionary(userDictionary);
        findRoles(userStoryAsOneSentence, client);
        findKeyExpression(userStoryAsOneSentence, client);
        findMainRoleActions(userStoryAsOneSentence, userStoryMap);
        findKeywordsInUserDictionary();
        findSynonyms();
        FormulateRequirements formulateRequirements = new FormulateRequirements(rolesInUserDictionary, foundRoles,
                actionInUserDictionary, benefitInUserDictionary, foundKeyPhrases);
        return formulateRequirements.findTermsInAppDictionary(synonymMap);
    }

    private void initializeUtility() {
        tokenizedUserStoryMap.clear();
        foundRoles.clear();
        foundKeyPhrases.clear();
        firstPersonBenefitAction.clear();
        firstPersonActionAction.clear();
        rolesInUserDictionary.clear();
        actionInUserDictionary.clear();
        benefitInUserDictionary.clear();
        synonymMap.clear();
    }

    private TextAnalyticsClient initializeNER() {
        return NLPResources.authenticateClient();
    }

    private void findRoles(String userStoryMap, TextAnalyticsClient client) {
        foundRoles = NLPResources.recognizeEntities(client, userStoryMap).stream()
                .map(CategorizedEntity::getText)
                .collect(Collectors.toSet());
    }

    private void findKeyExpression(String userStoryAsOneSentence, TextAnalyticsClient client) {
        foundKeyPhrases = NLPResources.extractKeyPhrases(client, userStoryAsOneSentence);
    }

    private void findMainRoleActions(String userStory, Map<String, String> userStoryMap) throws IOException {
        NLPResources.extractPartsOfSpeech(userStory);
        List<String> foundAction = NLPResources.findAllPersonaActions();
        NLPResources.findPersonaActionsInTokens(foundAction, tokenizedUserStoryMap);
        firstPersonActionAction = NLPResources.findActionsActionComplement(tokenizedUserStoryMap);
        firstPersonBenefitAction = NLPResources.findBenefitsActionComplement(tokenizedUserStoryMap);
    }

    private void findKeywordsInUserDictionary() {
        rolesInUserDictionary = userDictionaryEntitiesAnalyst.lookForRolesInUserDictionary(foundRoles);
        actionInUserDictionary = userDictionaryEntitiesAnalyst.lookForActionsInUserDictionary(firstPersonActionAction);
        benefitInUserDictionary = userDictionaryEntitiesAnalyst.lookForComplementsInUserDictionary(firstPersonBenefitAction);
    }

    private void findSynonyms() throws IOException {
        Collection<String> listOfActionWords = firstPersonActionAction.values();
        Collection<String> listOfBenefitWords = firstPersonBenefitAction.values();
        Collection<String> listOfUserDescriptionActionWords = actionInUserDictionary.keySet();
        Collection<String> listOfUserDescriptionBenefitWords = benefitInUserDictionary.keySet();
        Collection<String> listOfRoles = foundRoles;

        extractWordsAndSearch(listOfActionWords);
        extractWordsAndSearch(listOfBenefitWords);
        extractWordsAndSearch(listOfUserDescriptionActionWords);
        extractWordsAndSearch(listOfUserDescriptionBenefitWords);
        extractWordsAndSearch(listOfRoles);
    }

    private void extractWordsAndSearch(Collection<String> listOfActionWords) throws IOException {
        for (String word : listOfActionWords) {
            String[] tokenizedArray = word.split("[[ ]*|[,]*|[\\.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+");
            for (String wordToSearch : tokenizedArray) {
                List<String> synonyms = dictionaryAPIResources.performRequest(wordToSearch);
                synonymMap.put(wordToSearch, synonyms);
            }
        }
    }
}
