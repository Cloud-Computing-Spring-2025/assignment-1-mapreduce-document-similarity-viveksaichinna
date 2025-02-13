import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Set<String> docSet = new HashSet<>();
        for (Text val : values) {
            docSet.add(val.toString());
        }

        String[] docs = docSet.toArray(new String[0]);
        for (int i = 0; i < docs.length; i++) {
            for (int j = i + 1; j < docs.length; j++) {
                String pair = docs[i] + "," + docs[j];
                context.write(new Text(pair), new Text(key.toString()));
            }
        }
    }
}
