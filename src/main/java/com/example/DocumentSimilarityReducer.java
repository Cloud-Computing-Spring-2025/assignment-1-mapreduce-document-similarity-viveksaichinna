package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashSet;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {
    
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // Use a HashSet to collect unique document IDs
        HashSet<String> docSet = new HashSet<>();

        for (Text doc : values) {
            docSet.add(doc.toString());
        }

        // Emit (word, [doc1, doc2, doc3...]) => Helps in Jaccard Similarity Calculation
        if (docSet.size() > 1) { // Only process words appearing in multiple documents
            context.write(key, new Text(docSet.toString()));
        }
    }
}