// package com.example;

// import org.apache.hadoop.io.Text;
// import org.apache.hadoop.mapreduce.Reducer;

// import java.io.IOException;
// import java.util.HashSet;

// public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {
    
//     @Override
//     protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
//         // Use a HashSet to collect unique document IDs
//         HashSet<String> docSet = new HashSet<>();

//         for (Text doc : values) {
//             docSet.add(doc.toString());
//         }

//         // Emit (word, [doc1, doc2, doc3...]) => Helps in Jaccard Similarity Calculation
//         if (docSet.size() > 1) { // Only process words appearing in multiple documents
//             context.write(key, new Text(docSet.toString()));
//         }
//     }
// }
package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {
    
    // Store document words
    private Map<String, Set<String>> documentWords = new HashMap<>();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // Collect words for each document
        Set<String> words = new HashSet<>();
        for (Text word : values) {
            words.add(word.toString());
        }

        // Store words in a global map for later document comparison
        documentWords.put(key.toString(), words);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        // Compare all document pairs
        String[] docs = documentWords.keySet().toArray(new String[0]);

        for (int i = 0; i < docs.length; i++) {
            for (int j = i + 1; j < docs.length; j++) {
                String docPair = docs[i] + ", " + docs[j];

                Set<String> docSet1 = documentWords.get(docs[i]);
                Set<String> docSet2 = documentWords.get(docs[j]);

                // Compute Jaccard similarity
                double similarity = calculateJaccardSimilarity(docSet1, docSet2);
                context.write(new Text(docPair), new Text(String.format("Similarity: %.2f", similarity)));
            }
        }
    }

    // Compute Jaccard Similarity
    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() || set2.isEmpty()) return 0.0; // Avoid NaN issues

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }
}
