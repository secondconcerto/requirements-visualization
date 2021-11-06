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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NLPResources {
    private static final String KEY = "c20c7535495840e98928e30166916228";
    private static final String ENDPOINT = "https://warszawa.cognitiveservices.azure.com/";

    public TextAnalyticsClient authenticateClient() {
        return new TextAnalyticsClientBuilder()
                .credential(new AzureKeyCredential(KEY))
                .endpoint(ENDPOINT)
                .buildClient();
    }

    public List<CategorizedEntity> recognizeEntities(TextAnalyticsClient client, String text) {
        //azure
        List<CategorizedEntity> categorizedEntityList = client.recognizeEntities(text).stream().collect(Collectors.toList());
        return categorizedEntityList;
    }

    private Map<String, String> extractPartsOfSpeech(String text) throws IOException {
        InputStream tokenModelIn;
        InputStream posModelIn;
        Map<String, String> posTagMap = new HashMap<>();

        tokenModelIn = new FileInputStream("src/main/resources/en-token.bin");
        TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
        Tokenizer tokenizer = new TokenizerME(tokenModel);
        String[] tokens = tokenizer.tokenize(text);

        // Parts-Of-Speech Tagging
        // reading parts-of-speech model to a stream
        posModelIn = new FileInputStream("src/main/resources/en-pos-maxent.bin");
        // loading the parts-of-speech model from stream
        POSModel posModel = new POSModel(posModelIn);
        // initializing the parts-of-speech tagger with model
        POSTaggerME posTagger = new POSTaggerME(posModel);
        // Tagger tagging the tokens
        String[] tags = posTagger.tag(tokens);
        // Getting the probabilities of the tags given to the tokens
        //double[] probs = posTagger.probs();
        if (tags.length == tokens.length) {
            for (int i = 0; i < tags.length + 1; i++) {
                posTagMap.put(tokens[i], tags[i]);
            }
        }
        return posTagMap;
    }

}
