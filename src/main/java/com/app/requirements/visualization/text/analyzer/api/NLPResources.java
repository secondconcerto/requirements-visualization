package com.app.requirements.visualization.text.analyzer.api;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.ai.textanalytics.models.CategorizedEntity;
import com.azure.core.credential.AzureKeyCredential;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class NLPResources {

    private static final String KEY = "c20c7535495840e98928e30166916228";
    private static final String ENDPOINT = "https://warszawa.cognitiveservices.azure.com/";
    private final Map<String, String> posTagMap = new HashMap<>();
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

    public List<CategorizedEntity> recognizeEntities(TextAnalyticsClient client, String text) {
        //azure
        // List<CategorizedEntity> categorizedEntityList = client.recognizeEntities(text).stream().collect(Collectors.toList());
        return categorizedEntityList;
        //TODO to bedzie pierwszy reqiurement - ilość ról w aplikacji
    }

    public Map<String, String> extractPartsOfSpeech(String text) throws IOException {
        InputStream tokenModelIn;
        InputStream posModelIn;

        tokenModelIn = new FileInputStream("src/main/resources/en-token.bin");
        TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
        Tokenizer tokenizer = new TokenizerME(tokenModel);
        String[] tokens = tokenizer.tokenize(text);

        posModelIn = new FileInputStream("src/main/resources/en-pos-maxent.bin");
        POSModel posModel = new POSModel(posModelIn);
        POSTaggerME posTagger = new POSTaggerME(posModel);
        String[] tags = posTagger.tag(tokens);
        if (tags.length == tokens.length) {
            for (int i = 0; i < tags.length; i++) {
                posTagMap.put(tokens[i], tags[i]);
            }
        }
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
        foundActions.remove("want");
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
            if ((!firstPersonMapToFill.isEmpty() && posTagMap.get(foundAction).equals("MD"))
                    || (!firstPersonMapToFill.isEmpty() && posTagMap.get(foundAction).equals("VB"))) {
                firstPersonMapToFill.put(firstPersonMapToFill.lastEntry().getKey(), firstPersonMapToFill.lastEntry().getValue() + " " + foundAction);
            } else if (!firstPersonMapToFill.isEmpty()) {
                firstPersonMapToFill.put("action" + firstPersonMapToFill.size(), foundAction);
            } else {
                firstPersonMapToFill.put("action", foundAction);
            }
            return true;
        }
        return false;
    }


    public void findActionsComplement(Map<String, String> tokenizedUserStoryMap) {
        findComplementForGivenPart(tokenizedUserStoryMap, "Action", firstPersonActionAction);
        findComplementForGivenPart(tokenizedUserStoryMap, "Benefit", firstPersonBenefitAction);
    }

    private void findComplementForGivenPart(Map<String, String> tokenizedUserStoryMap, String token, NavigableMap<String, String> firstPersonActionAction) {
        String actionToken = tokenizedUserStoryMap.get(token);
        Collection<String> allActionActions = firstPersonActionAction.values();
        int firstIndex;
        int endIndex;
        if (!allActionActions.isEmpty()) {
            firstIndex = actionToken.indexOf(String.valueOf(allActionActions.toArray()[0]));
            endIndex = firstIndex + allActionActions.toArray()[0].toString().length();
            String complement = actionToken.substring(endIndex + 1);
            firstPersonActionAction.put("complement", complement);
        }

        //TODO pamietac ze nie moze byc wiecej niz 1 czasownik w action
    }

}
