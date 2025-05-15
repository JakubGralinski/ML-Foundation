package CCS;

import java.io.*;
import java.util.*;

public class PrepareDataset {

    public static class DatasetSplit {
        public List<Observation> train;
        public List<Observation> test;

        public DatasetSplit(List<Observation> train, List<Observation> test) {
            this.train = train;
            this.test = test;
        }
    }

    public static List<Observation> readCSV(String filePath) {
        List<Observation> dataset = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine(); // assume header exists
            String line;
            while ((line = br.readLine()) != null) {
                // CSV format: sepal_length, sepal_width, petal_length, petal_width, class
                String[] tokens = line.split(",");
                double[] features = new double[tokens.length - 1];
                for (int i = 0; i < tokens.length - 1; i++) {
                    features[i] = Double.parseDouble(tokens[i]);
                }
                String label = tokens[tokens.length - 1].trim();
                dataset.add(new Observation(features, label));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataset;
    }

    public static DatasetSplit trainTestSplit(List<Observation> dataset) {
        // Group observations by class.
        Map<String, List<Observation>> grouped = new HashMap<>();
        for (Observation obs : dataset) {
            grouped.computeIfAbsent(obs.getLabel(), k -> new ArrayList<>()).add(obs);
        }
        List<Observation> train = new ArrayList<>();
        List<Observation> test = new ArrayList<>();
        // For each class, split 66% train, 33% test.
        for (List<Observation> group : grouped.values()) {
            Collections.shuffle(group);
            int trainSize = (int) Math.round(group.size() * 0.66);

            train.addAll(group.subList(0, trainSize));
            test.addAll(group.subList(trainSize, group.size()));
        }
        return new DatasetSplit(train, test);
    }
}