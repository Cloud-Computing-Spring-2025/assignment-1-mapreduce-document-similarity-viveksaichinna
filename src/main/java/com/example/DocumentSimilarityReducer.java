package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {
    
    private Map<String, Set<String>> documentWords = new HashMap<>();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // There should be only one value per document
        for (Text value : values) {
            Set<String> words = new HashSet<>(Arrays.asList(value.toString().split(",")));
            documentWords.put(key.toString(), words);
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        List<String> documents = new ArrayList<>(documentWords.keySet());
        Collections.sort(documents); // Ensure consistent ordering

        for (int i = 0; i < documents.size(); i++) {
            for (int j = i + 1; j < documents.size(); j++) {
                String doc1 = documents.get(i);
                String doc2 = documents.get(j);
                
                Set<String> set1 = documentWords.get(doc1);
                Set<String> set2 = documentWords.get(doc2);

                double similarity = calculateJaccardSimilarity(set1, set2);
                
                String docPair = doc1 + ", " + doc2;
                context.write(new Text(docPair), new Text(String.format("Similarity: %.2f", similarity)));
            }
        }
    }

    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }
}