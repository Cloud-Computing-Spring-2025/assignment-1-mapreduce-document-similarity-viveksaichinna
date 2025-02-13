package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class DocumentSimilarityMapper extends Mapper<LongWritable, Text, Text, Text> {
    
    private Text word = new Text();
    private Text docId = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // Input format: "doc1.txt hadoop is a distributed system"
        String line = value.toString().trim();
        String[] parts = line.split("\\s+", 2);

        if (parts.length < 2) return; // Ignore invalid lines

        String documentName = parts[0]; // Extract document ID (e.g., doc1.txt)
        String documentContent = parts[1]; // Extract document words

        StringTokenizer tokenizer = new StringTokenizer(documentContent);
        while (tokenizer.hasMoreTokens()) {
            word.set(tokenizer.nextToken());
            docId.set(documentName);
            context.write(docId, word);  // Emit (docID, word)
        }
    }
}
