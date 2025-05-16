package pl.edu.pja.tpo02;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class PrepareDataset {
    public static DataSet loadIrisDataset(String filename) {
        List<double[]> featureList = new ArrayList<>();
        List<Integer> labelsList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) { // Process remaining lines.
                String[] tokens = line.split(",");
                if (tokens.length < 5) continue; // Skip malformed lines.
                String species = tokens[4].trim();
                if (species.equalsIgnoreCase("Iris-setosa") || species.equalsIgnoreCase("Iris-versicolor")) {
                    double[] feature = new double[4];
                    for (int i = 0; i < 4; i++) {
                        feature[i] = Double.parseDouble(tokens[i]);
                    }
                    featureList.add(feature);
                    int label = species.equalsIgnoreCase("Iris-setosa") ? 1 : 0;
                    labelsList.add(label);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DataSet(featureList.toArray(new double[0][]),
                labelsList.stream().mapToInt(Integer::intValue).toArray());
    }

    public static DataSet[] trainTestSplit(DataSet dataSet, double trainRatio) {
        List<double[]> trainFeatures = new ArrayList<>();
        List<Integer> trainLabels = new ArrayList<>();
        List<double[]> testFeatures = new ArrayList<>();
        List<Integer> testLabels = new ArrayList<>();

        Map<Integer, List<Integer>> classIndicies = new HashMap<>();
        for(int i = 0; i < dataSet.labels.length; i++) {
            int label = dataSet.labels[i];
            classIndicies.putIfAbsent(label, new ArrayList<>());
            classIndicies.get(label).add(i);
        }

        Random rand = new Random();
        for(Integer label : classIndicies.keySet()) {
            List<Integer> indicies = classIndicies.get(label);
            Collections.shuffle(indicies, rand);
            int trainCount = (int) (indicies.size() * trainRatio);
            for(int i = 0; i < indicies.size(); i++) {
                int idx = indicies.get(i);
                if(i < trainCount) {
                    trainFeatures.add(dataSet.features[idx]);
                    trainLabels.add(dataSet.labels[idx]);
                } else {
                    testFeatures.add(dataSet.features[idx]);
                    testLabels.add(dataSet.labels[idx]);
                }
            }
        }
        DataSet trainSet = new DataSet(trainFeatures.toArray(new double[0][]),
                            trainLabels.stream().mapToInt(i -> i).toArray());

        DataSet testSet = new DataSet(testFeatures.toArray(new double[0][]),
                            testLabels.stream().mapToInt(i -> i).toArray());
        return new DataSet[]{trainSet, testSet};
    }
}

