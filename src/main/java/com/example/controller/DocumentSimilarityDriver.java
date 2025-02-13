package com.example.controller;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import com.example.DocumentSimilarityMapper;
import com.example.DocumentSimilarityReducer;

public class DocumentSimilarityDriver {
    
    public static void main(String[] args) {
        try {
            // Hadoop Configuration
            Configuration conf = new Configuration();
            Job job = Job.getInstance(conf, "Document Similarity Calculation");

            // Set the JAR file
            job.setJarByClass(DocumentSimilarityDriver.class);

            // Set Mapper and Reducer classes
            job.setMapperClass(DocumentSimilarityMapper.class);
            job.setReducerClass(DocumentSimilarityReducer.class);

            // Set Output Key and Value Types
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);

            // Set Input and Output Format
            job.setInputFormatClass(TextInputFormat.class);
            job.setOutputFormatClass(TextOutputFormat.class);

            // Set Input and Output Paths from command-line arguments
            TextInputFormat.setInputPaths(job, new Path(args[0]));
            TextOutputFormat.setOutputPath(job, new Path(args[1]));

            // Submit the job and wait for completion
            System.exit(job.waitForCompletion(true) ? 0 : 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}