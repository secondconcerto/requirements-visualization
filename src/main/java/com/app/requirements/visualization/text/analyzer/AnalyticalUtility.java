package com.app.requirements.visualization.text.analyzer;

import com.app.requirements.visualization.text.analyzer.api.DictionaryDivResources;
import com.app.requirements.visualization.text.analyzer.api.NLPResources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class AnalyticalUtility {

    private final UserDictionaryEntitiesAnalyst userDictionaryEntitiesAnalyst = new UserDictionaryEntitiesAnalyst();
    private final NLPResources NLPResources = new NLPResources();
    private final DictionaryDivResources dictionaryDivResources = new DictionaryDivResources();


    private final Map<String, String> tokenizedUserStoryMap = new HashMap<>();
    private List<String> foundRoles = new ArrayList<>();
    private NavigableMap<String, String> firstPersonBenefitAction = new TreeMap<>();
    private NavigableMap<String, String> firstPersonActionAction = new TreeMap<>();
    private Map<String, String> rolesInUserDictionary = new HashMap<>();
    private Map<String, String> actionInUserDictionary = new HashMap<>();
    private Map<String, String> benefitInUserDictionary = new HashMap<>();
    private Map<String, List<String>> synonymMap = new HashMap<String, List<String>>();

    public Map<String, List<String>> startAnalysis(Map<String, String> userStoryMap, Map<String, String> userDictionary, String userStoryAll) throws IOException {
        initializeUtility();
        tokenizedUserStoryMap.putAll(userStoryMap);
        userDictionaryEntitiesAnalyst.setUserDictionary(userDictionary);
        findRoles(userStoryAll);
        findMainRoleActions(userStoryAll);
        findKeywordsInUserDictionary();
        findSynonyms();
        FormulateRequirements formulateRequirements = new FormulateRequirements(rolesInUserDictionary, foundRoles,
                actionInUserDictionary, benefitInUserDictionary);
        return formulateRequirements.findTermsInAppDictionary(synonymMap);
    }

    private void initializeUtility() {
        tokenizedUserStoryMap.clear();
        foundRoles.clear();
        firstPersonBenefitAction.clear();
        firstPersonActionAction.clear();
        rolesInUserDictionary.clear();
        actionInUserDictionary.clear();
        benefitInUserDictionary.clear();
        synonymMap.clear();
    }

    private void findRoles(String userStoryMap) throws IOException {
        foundRoles = NLPResources.recognizeEntities(userStoryMap);
    }

    private void findMainRoleActions(String userStory) throws IOException {
        NLPResources.extractPartsOfSpeech(userStory);
        List<String> foundAction = NLPResources.findAllPersonaActions();
        NLPResources.findPersonaActionsInTokens(foundAction, tokenizedUserStoryMap);
        NLPResources.findActionsComplement(tokenizedUserStoryMap);
        firstPersonActionAction = NLPResources.getFirstPersonActionAction();
        firstPersonBenefitAction = NLPResources.getFirstPersonBenefitAction();
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

        extractWordsAndSearch(listOfActionWords);
        extractWordsAndSearch(listOfBenefitWords);
        extractWordsAndSearch(listOfUserDescriptionActionWords);
        extractWordsAndSearch(listOfUserDescriptionBenefitWords);
    }

    private void extractWordsAndSearch(Collection<String> listOfActionWords) throws IOException {
        for (String word : listOfActionWords) {
            String[] tokenizedArray = word.split("[[ ]*|[,]*|[\\.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+");
            for (String wordToSearch : tokenizedArray) {
                List<String> synonyms = dictionaryDivResources.performRequest(wordToSearch);
                synonymMap.put(wordToSearch, synonyms);
            }
        }
    }


}
