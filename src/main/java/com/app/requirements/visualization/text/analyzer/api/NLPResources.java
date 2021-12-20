package com.app.requirements.visualization.text.analyzer.api;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.ai.textanalytics.models.CategorizedEntity;
import com.azure.core.credential.AzureKeyCredential;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class NLPResources {

    private static final String KEY = "063bf9a88276445491e2592ac29efbc3";
    private static final String ENDPOINT = "https://nlp-oliwia.cognitiveservices.azure.com/";
    private final NavigableMap<String, String> posTagMap = new TreeMap<>();
    private final List<CategorizedEntity> categorizedEntityList = new ArrayList<>();
    private final NavigableMap<String, String> firstPersonBenefitAction = new TreeMap<>();
    private final NavigableMap<String, String> firstPersonActionAction = new TreeMap<>();

    public NavigableMap<String, String> getFirstPersonBenefitAction() {
        return firstPersonBenefitAction;
    }

    public NavigableMap<String, String> getFirstPersonActionAction() {
        return firstPersonActionAction;
    }

    public TextAnalyticsClient authenticateClient() {
        return new TextAnalyticsClientBuilder()
                .credential(new AzureKeyCredential(KEY))
                .endpoint(ENDPOINT)
                .buildClient();
    }

    public List<CategorizedEntity> recognizeEntities(TextAnalyticsClient client, String userStoryAsOneString) {
        //azure
        List<CategorizedEntity> categorizedEntityList = client.recognizeEntities(userStoryAsOneString).stream().collect(Collectors.toList());
        List<CategorizedEntity> personTypeList = new ArrayList<>();
        for (CategorizedEntity entity: categorizedEntityList ) {
            if(entity.getCategory().toString().equals("PersonType")) {
                personTypeList.add(entity);
            }
        }
        return personTypeList;
        //TODO to bedzie pierwszy reqiurement - ilość ról w aplikacji
    }

    public Set<String> extractKeyPhrases(TextAnalyticsClient client, String userStoryAsOneString)
    {
        Map<Integer, String> foundExpressions = new TreeMap<>();
       // System.out.printf("Recognized phrases: %n");
        for (String keyPhrase : client.extractKeyPhrases(userStoryAsOneString)) {
            int index = userStoryAsOneString.indexOf(keyPhrase);
            foundExpressions.put(index, keyPhrase);
        }
        return foundExpressions.values().stream().collect(Collectors.toSet());
    }

    public Map<String, String> extractPartsOfSpeech(String userStory) throws IOException {
        InputStream tokenModelIn;
        InputStream posModelIn;
        InputStream chunkerModelIn;

        tokenModelIn = new FileInputStream("src/main/resources/en-token.bin");
        TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
        Tokenizer tokenizer = new TokenizerME(tokenModel);

        posModelIn = new FileInputStream("src/main/resources/en-pos-maxent.bin");
        POSModel posModel = new POSModel(posModelIn);
        POSTaggerME posTagger = new POSTaggerME(posModel);

        chunkerModelIn = new FileInputStream("src/main/resources/en-chunker.bin");
        ChunkerModel chunkerModelodel = new ChunkerModel(chunkerModelIn);
        ChunkerME chunker = new ChunkerME(chunkerModelodel);

       /* for (String partOfUserStory : userStoryMap.values()) {*/
            String[] tokens = tokenizer.tokenize(userStory);
            String[] tags = posTagger.tag(tokens);
            String chunks[] = chunker.chunk(tokens, tags);
            if (tags.length == tokens.length) {
                for (int i = 0; i < tags.length; i++) {
                    posTagMap.put(tokens[i], tags[i]);
                }
            }
        /*}*/


        removeFormConstants();
        return posTagMap;
    }

    private void removeFormConstants() {
        posTagMap.remove("as");
        posTagMap.remove("a");
        posTagMap.remove("i");
        posTagMap.remove("want");
        posTagMap.remove("to");
        posTagMap.remove("so");
        posTagMap.remove("that");
    }

    public List<String> findAllPersonaActions() {
        //TODO exception gdy nie znaleziono czasownika
        return posTagMap.entrySet().stream()
                .filter(this::checkIfVerb)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private Boolean checkIfVerb(Map.Entry<String, String> entry) {
        return entry.getValue().equals("MD") || entry.getValue().equals("VBP") || entry.getValue().equals("VB");
    }

    public void findPersonaActionsInTokens(List<String> foundActions, Map<String, String> tokenizedUserStoryMap) {
        /*foundActions.remove("want");*/
        List<String> foundActionCopy = new ArrayList<>(foundActions);
        foundActions.stream()
                .filter(action -> lookUpForActionInActionPart(action, tokenizedUserStoryMap))
                .map(foundActionCopy::remove)
                .collect(Collectors.toList());
        foundActionCopy.stream()
                .filter(action -> lookUpForActionInBenefitPart(action, tokenizedUserStoryMap))
                .collect(Collectors.toList());
    }


    private boolean lookUpForActionInActionPart(String foundAction, Map<String, String> tokenizedUserStoryMap) {
        String actionToken = tokenizedUserStoryMap.get("Action");
        return extractCommonVerbs(foundAction, actionToken, firstPersonActionAction);
    }


    private boolean lookUpForActionInBenefitPart(String foundAction, Map<String, String> tokenizedUserStoryMap) {
        String benefitActionToken = tokenizedUserStoryMap.get("Benefit");
        return extractCommonVerbs(foundAction, benefitActionToken, firstPersonBenefitAction);
    }

    private boolean extractCommonVerbs(String foundAction, String token, NavigableMap<String, String> firstPersonMapToFill) {
        if (token.contains(foundAction)) {
            if ((!firstPersonMapToFill.isEmpty() && posTagMap.get(foundAction).equals("VB")
                    && posTagMap.get(firstPersonMapToFill.lastEntry().getValue()).equals("MD"))) {
                firstPersonMapToFill.put(firstPersonMapToFill.lastEntry().getKey(), createProperVerb(foundAction, firstPersonMapToFill));
            } else if (!firstPersonMapToFill.isEmpty()) {
                firstPersonMapToFill.put("action" + firstPersonMapToFill.size(), foundAction);
            } else {
                firstPersonMapToFill.put("action", foundAction);
            }
            return true;
        }
        return false;
    }

    @NotNull
    private String createProperVerb(String foundAction, NavigableMap<String, String> firstPersonMapToFill) {
        if(posTagMap.get(foundAction).equals("VB")) {
            return firstPersonMapToFill.lastEntry().getValue() + " " + foundAction ;
        } else {
            return foundAction + " " + firstPersonMapToFill.lastEntry().getValue();
        }
    }

    public NavigableMap<String, String> findActionsActionComplement(Map<String, String> tokenizedUserStoryMap) {
        return findComplementForGivenPart(tokenizedUserStoryMap, "Action", firstPersonActionAction);
    }

    public NavigableMap<String, String> findBenefitsActionComplement(Map<String, String> tokenizedUserStoryMap) {
        return findComplementForGivenPart(tokenizedUserStoryMap, "Benefit", firstPersonBenefitAction);
    }

    private NavigableMap<String, String> findComplementForGivenPart(Map<String, String> tokenizedUserStoryMap, String token, NavigableMap<String, String> actions) {
        String actionToken = tokenizedUserStoryMap.get(token);
        Collection<String> allActionActions = actions.values();
        int firstIndex;
        int endIndex;
        if (!allActionActions.isEmpty()) {
            firstIndex = actionToken.indexOf(String.valueOf(allActionActions.toArray()[0]));
            if(firstIndex > -1) {
                endIndex = firstIndex + allActionActions.toArray()[0].toString().length();
                String complement = "";
                if(endIndex == actionToken.length() || endIndex == actionToken.length()-1) {
                    if(firstIndex != 0 || firstIndex != 1)
                    complement = actionToken.substring(0,firstIndex);

                } else {
                    complement = actionToken.substring(endIndex + 1);
                }
                actions.put("complement", complement);
            }
        }
        return actions;

        //TODO pamietac ze nie moze byc wiecej niz 1 czasownik w action
    }

}
