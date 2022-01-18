package com.app.requirements.visualization.text.analyzer;

import com.app.requirements.visualization.text.analyzer.entity.AppDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class FormulateRequirements {

    private final AppDictionary appDictionary = new AppDictionary();

    private final Set<String> finalRequirements = new HashSet<>();
    private final Set<String> UIRequirements = new HashSet<>();
    private Map<String, String> rolesInUserDictionary;
    private Set<String> foundRoles = new HashSet<>();
    private Set<String> foundKeyPhrases =  new HashSet<>();
    private Map<String, String> actionInUserDictionary;
    private Map<String, String> benefitInUserDictionary;
    private Map<String, Set<String>> allRequirements = new HashMap<>();
    private Map<String, List<String>> foundSynonymsMap = new HashMap<>();

    public FormulateRequirements(Map<String, String> rolesInUserDictionary, Set<String> foundRoles,
                                 Map<String, String> actionInUserDictionary, Map<String, String> benefitInUserDictionary, Set<String> foundKeyPhrases) {
        this.rolesInUserDictionary = rolesInUserDictionary;
        this.foundRoles = foundRoles;
        this.actionInUserDictionary = actionInUserDictionary;
        this.benefitInUserDictionary = benefitInUserDictionary;
        this.foundKeyPhrases = foundKeyPhrases;
    }

    public Map<String, Set<String>> findTermsInAppDictionary(Map<String, List<String>> synonymsMap) {
        addRolesToRequirements(foundRoles);
        ArrayList<Map.Entry<String, List<String>>> foundTermsFromProjectDictionary = new ArrayList<>();
        for (Map.Entry<String, List<String>> synonym : synonymsMap.entrySet()) {
            Collection<Map.Entry<String, List<String>>> foundTerms = lookForTermsInProjectDictionary(synonym.getKey(), synonym.getValue());
            if(!foundTerms.isEmpty()) {
                foundTermsFromProjectDictionary.addAll(foundTerms);
                foundSynonymsMap.put(synonym.getKey(), synonym.getValue());
            }
        }
        List<List<String>> mapToFoundTerms = foundTermsFromProjectDictionary.stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        checkWhatRequirementsIsToPerform(mapToFoundTerms);
        return allRequirements;
    }

    private void addRolesToRequirements(Set<String> foundRoles) {
        finalRequirements.add("Your app will need such roles:" + foundRoles.toString());
    }

    private List lookForTermsInProjectDictionary(String userWord, List<String> synonyms) {
        return synonyms.stream()
                .filter(entry -> appDictionary.getAppTerms().containsKey(entry))
                .map(entry -> new AbstractMap.SimpleEntry(userWord, appDictionary.getAppTerms().get(entry)))
                .collect(Collectors.toList());
    }

    private void checkWhatRequirementsIsToPerform(List<List<String>> foundTermsMap) {
        List<String> foundTermsInAppDictionary = convertListOfListsIntoOne(foundTermsMap);
        for (String entry : foundTermsInAppDictionary) {
            findRequirement(entry);
        }
        allRequirements.put("text", finalRequirements);
        allRequirements.put("ui", UIRequirements);
        allRequirements.put("keyPhrases", foundKeyPhrases);
    }

    @NotNull
    private List<String> convertListOfListsIntoOne(List<List<String>> foundTermsMap) {
        List<String> foundTermsInAppDictionaryRaw = foundTermsMap.stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
        List<String> foundTermsInAppDictionary = removeDuplicatesFromList(foundTermsInAppDictionaryRaw);
        return foundTermsInAppDictionary;
    }

    @NotNull
    private List<String> removeDuplicatesFromList(List<String> foundTermsFromProjectDictionary) {
        if(!foundTermsFromProjectDictionary.isEmpty()) {
            return foundTermsFromProjectDictionary.stream().distinct().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void findRequirement(String term) {
        switch (term) {
            case "contact":
                finalRequirements.add("If users are to stay in touch, it's a good idea to add a contact form.\n");
                UIRequirements.add("contact");
                break;
            case "form":
                finalRequirements.add("It is worth thinking about a form through which it will be possible to get in touch. " +
                        "You need a field to take user input in the view. It may be simple area of more complex form.\n");
                UIRequirements.add("form");
                break;
            case "filter":
                finalRequirements.add("You need an option to filter data. \n");
                UIRequirements.add("filter");
                break;
            case "friend":
                finalRequirements.add("You need to be able to add colleagues as friends in the virtual world. \n");
                UIRequirements.add("friend");
                break;
            case "enter":
                finalRequirements.add("You need some text area so user can enter input. Perhaps some form would be useful.\n");
                UIRequirements.add("input");
                break;
            case "data":
                finalRequirements.add("Perhaps you will need a data structure to store information. \n");
                prepareColumnInDataTable();
                UIRequirements.add("table");
                break;
            case "date":
                finalRequirements.add("You must have structure to keep important dates - e.g. calender. \n");
                UIRequirements.add("date");
                break;
            case "problem":
                finalRequirements.add("You must a panel that will allow to communicate any error or problem, so it can be solved by someone. \n");
                UIRequirements.add("problem");
                break;
            case "profile":
                finalRequirements.add("Participants (members) will need their profiles. \n");
                UIRequirements.add("profile");
                break;
            case "share":
                finalRequirements.add("You should think about a platform that allows you to share your content with other users. \n");
                UIRequirements.add("socialPlatform");
                break;
            case "update":
                finalRequirements.add("Remember about function to refresh or update your data. \n");
                UIRequirements.add("update");
                break;
        }
    }

    private void prepareColumnInDataTable() {
        if (actionInUserDictionary.containsValue("data")) {
            String valueByKey = getValueByKey("data", actionInUserDictionary);
            finalRequirements.add("You will need such columns: " + valueByKey);
            String[] tokenizedWord = valueByKey
                    .split(",");
            allRequirements.put("columns", new HashSet<>(Arrays.asList(tokenizedWord)));
        } else if (benefitInUserDictionary.containsValue("data")) {
            String valueByKey = getValueByKey("data", benefitInUserDictionary);
            finalRequirements.add("You will need such columns: " + valueByKey);
            String[] tokenizedWord = valueByKey
                    .split(",");
            allRequirements.put("columns", new HashSet<>(Arrays.asList(tokenizedWord)));
        }  else if (actionInUserDictionary.containsValue("columns")) {
            String valueByKey = getValueByKey("columns", actionInUserDictionary);
            finalRequirements.add("You will need such columns: " + valueByKey);
            String[] tokenizedWord = valueByKey
                    .split(",");
            allRequirements.put("columns", new HashSet<>(Arrays.asList(tokenizedWord)));
        } else if (benefitInUserDictionary.containsValue("columns")) {
            String valueByKey = getValueByKey("columns", benefitInUserDictionary);
            finalRequirements.add("You will need such columns: " + valueByKey);
            String[] tokenizedWord = valueByKey
                    .split(",");
            allRequirements.put("columns", new HashSet<>(Arrays.asList(tokenizedWord)));
        } else if (!checkIfSynonymExists("data").isEmpty()) {
            getTermFromSynonym(actionInUserDictionary);
            getTermFromSynonym(benefitInUserDictionary);
        } if (!allRequirements.containsKey("columns")) {
            String[] tokenizedWord = {"column 1", "column 2", "column 3"};
            allRequirements.put("columns", new HashSet<>(Arrays.asList(tokenizedWord)));
        }
    }

    private void getTermFromSynonym(Map<String, String> userDictionaryMap) {
        String termToLook = checkIfSynonymExists("data").get(0);
        String valueByKey = getValueByKey(termToLook, userDictionaryMap);
        if(!valueByKey.isEmpty()) {
            finalRequirements.add("You will need such columns: " + valueByKey);
            String[] tokenizedWord = valueByKey
                    .split(",");
            allRequirements.put("columns", new HashSet<>(Arrays.asList(tokenizedWord)));
        }
    }

    private List<String> checkIfSynonymExists(String data) {
        return foundSynonymsMap.entrySet().stream()
                .filter(entry -> entry.getValue().contains(data))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private String getValueByKey(String value, Map<String, String> map) {
        return map.entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst().orElse("");
    }
}
