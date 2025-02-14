package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DocumentSimilarityMapper extends Mapper<LongWritable, Text, Text, Text> {
    
    private Text documentPair = new Text();
    private Text wordSet = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String documentName = ((FileSplit) context.getInputSplit()).getPath().getName();
        String content = value.toString().toLowerCase();
        
        // Tokenize and create a set of unique words
        Set<String> uniqueWords = new HashSet<>();
        for (String word : content.split("\\s+")) {
            if (!word.isEmpty()) {
                uniqueWords.add(word);
            }
        }
        
        // Emit document name as key and word set as value
        context.write(new Text(documentName), new Text(String.join(",", uniqueWords)));
    }
}