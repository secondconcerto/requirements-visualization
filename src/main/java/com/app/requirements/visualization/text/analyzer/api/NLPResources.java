package com.app.requirements.visualization.text.analyzer.api;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class NLPResources {

    private final Map<String, String> posTagMap = new HashMap<>();
    private final NavigableMap<String, String> firstPersonBenefitAction = new TreeMap<>();
    private final NavigableMap<String, String> firstPersonActionAction = new TreeMap<>();

    public NavigableMap<String, String> getFirstPersonBenefitAction() {
        return firstPersonBenefitAction;
    }

    public NavigableMap<String, String> getFirstPersonActionAction() {
        return firstPersonActionAction;
    }

    public List<String> recognizeEntities(String text) throws IOException {
        try (InputStream modelIn = new FileInputStream("src/main/resources/en-ner-person.bin")) {
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            NameFinderME nameFinder = new NameFinderME(model);
            String tempStory = text.replaceAll("[^a-zA-Z0-9]", " ");
            String storyArray[] = tempStory.split(" ");
            Span nameSpans[] = nameFinder.find(storyArray);
            return Arrays.stream(nameSpans).map(span -> span.toString()).collect(Collectors.toList());
        }
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
