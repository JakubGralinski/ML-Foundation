package pl.edu.pja.tpo03;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    // -- EVALUATION METRICS --

    public static double measureAccuracy(List<String> yTrue, List<String> yPred) {
        if (yTrue == null || yPred == null || yTrue.size() == 0 || yTrue.size() != yPred.size()) {
            throw new IllegalArgumentException("Size mismatch in evaluation lists");
        }
        int correct = 0;
        for (int i = 0; i < yTrue.size(); i++) {
            if (yTrue.get(i).equals(yPred.get(i))) {
                correct++;
            }
        }
        return (double) correct / yTrue.size();
    }

    public static double measurePrecision(List<String> yTrue, List<String> yPred, String positiveLabel) {
        if (yTrue == null || yPred == null || yTrue.size() == 0 || yTrue.size() != yPred.size()) {
            throw new IllegalArgumentException("Size mismatch in evaluation lists");
        }
        int tp = 0;
        int fp = 0;
        for (int i = 0; i < yTrue.size(); i++) {
            if (yPred.get(i).equals(positiveLabel)) {
                if (yTrue.get(i).equals(positiveLabel)) {
                    tp++;
                } else {
                    fp++;
                }
            }
        }
        if (tp + fp == 0) return 0.0;
        return (double) tp / (tp + fp);
    }

    public static double measureRecall(List<String> yTrue, List<String> yPred, String positiveLabel) {
        if (yTrue == null || yPred == null || yTrue.size() == 0 || yTrue.size() != yPred.size()) {
            throw new IllegalArgumentException("Size mismatch in evaluation lists");
        }
        int tp = 0;
        int fn = 0;
        for (int i = 0; i < yTrue.size(); i++) {
            if (yTrue.get(i).equals(positiveLabel)) {
                if (yPred.get(i).equals(positiveLabel)) {
                    tp++;
                } else {
                    fn++;
                }
            }
        }
        if (tp + fn == 0) return 0.0;
        return (double) tp / (tp + fn);
    }

    public static double measureFMeasure(List<String> yTrue, List<String> yPred, String positiveLabel) {
        double precision = measurePrecision(yTrue, yPred, positiveLabel);
        double recall = measureRecall(yTrue, yPred, positiveLabel);
        if (precision + recall == 0) return 0.0;
        return 2 * precision * recall / (precision + recall);
    }

    public static void main(String[] args) {
        List<String> allLines = readLines("src/main/resources/sentences.csv");
        if (allLines.isEmpty()) {
            System.err.println("No lines found in sentences.csv");
            return;
        }

        List<String> allTexts = new ArrayList<>();
        List<String> allLabels = new ArrayList<>();
        boolean isFirstLine = true;
        for (String line : allLines) {
            if (isFirstLine) {
                isFirstLine = false;
                continue;  // skip header
            }
            String[] parts = line.split(",", 2);
            if (parts.length < 2) continue;
            String text = parts[0].trim();
            String label = parts[1].trim();
            allTexts.add(text);
            allLabels.add(label);
        }

        // Shuffle data
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < allTexts.size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, new Random());

        // Split into 70% training and 30% test data
        int trainSize = (int) (allTexts.size() * 0.7);
        List<String> trainTexts = new ArrayList<>();
        List<String> trainLabels = new ArrayList<>();
        List<String> testTexts = new ArrayList<>();
        List<String> testLabels = new ArrayList<>();
        for (int i = 0; i < indices.size(); i++) {
            int idx = indices.get(i);
            if (i < trainSize) {
                trainTexts.add(allTexts.get(idx));
                trainLabels.add(allLabels.get(idx));
            } else {
                testTexts.add(allTexts.get(idx));
                testLabels.add(allLabels.get(idx));
            }
        }

        // Convert training texts to 26-dimensional vectors using TextVectorizer
        List<double[]> trainFeatures = new ArrayList<>();
        for (String text : trainTexts) {
            trainFeatures.add(TextVectorizer.convert(text));
        }

        // Identify unique language labels to set up the classifier
        Set<String> uniqueClasses = new HashSet<>(allLabels);
        List<String> classList = new ArrayList<>(uniqueClasses);

        // Create the multi-class classifier (dimension = 26, alpha = 0.1, beta = 0.0)
        MultiClassClassifier classifier;
        try {
            classifier = new MultiClassClassifier(classList, 26, 0.1, 0.0);// alpha- learning rate
        } catch (Exception e) {
            System.err.println("Error creating classifier: " + e.getMessage());
            return;
        }

        // Train the classifier ( 50 passes with 1 epoch each)
        for (int i = 0; i < 50; i++) {
            classifier.train(trainFeatures, trainLabels, 1);
        }

        // Convert test texts to feature vectors
        List<double[]> testFeatures = new ArrayList<>();
        for (String text : testTexts) {
            testFeatures.add(TextVectorizer.convert(text));
        }

        // Predict test texts (each predicted label is a String)
        List<String> predictions;
        try {
            predictions = classifier.predictBatch(testFeatures);
        } catch (Exception e) {
            System.err.println("Error during prediction: " + e.getMessage());
            return;
        }

        // Evaluate predictions
        System.out.println("Test predictions: " + predictions);
        double accuracy = measureAccuracy(testLabels, predictions);
        System.out.println("Accuracy: " + accuracy);
        Map<String, Double> precisionMap = new HashMap<>();
        Map<String, Double> recallMap = new HashMap<>();
        Map<String, Double> fMeasureMap = new HashMap<>();
        for (String cls : classList) {
            double precision = measurePrecision(testLabels, predictions, cls);
            double recall = measureRecall(testLabels, predictions, cls);
            double fmeasure = measureFMeasure(testLabels, predictions, cls);
            precisionMap.put(cls, precision);
            recallMap.put(cls, recall);
            fMeasureMap.put(cls, fmeasure);
            System.out.println("For class " + cls + ":");
            System.out.println("   Precision: " + precision);
            System.out.println("   Recall: " + recall);
            System.out.println("   F-Measure: " + fmeasure);
        }

        // Save detailed results into CSV
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("results.csv"))) {
            writer.write("text,true_label,predicted_label\n");
            for (int i = 0; i < testTexts.size(); i++) {
                writer.write("\"" + testTexts.get(i).replace("\"", "\"\"") + "\"," +
                        testLabels.get(i) + "," +
                        predictions.get(i) + "\n");
            }
            // Also write overall metrics as additional rows
            writer.write("Overall Accuracy," + accuracy + ",\n");
            for (String cls : classList) {
                writer.write("Precision_" + cls + "," + precisionMap.get(cls) + ",\n");
                writer.write("Recall_" + cls + "," + recallMap.get(cls) + ",\n");
                writer.write("FMeasure_" + cls + "," + fMeasureMap.get(cls) + ",\n");
            }
            System.out.println("Results saved to results.csv");
        } catch (IOException e) {
            System.err.println("Error writing results to CSV: " + e.getMessage());
        }

        // Print final perceptron details
        classifier.printNetwork();
    }

    private static List<String> readLines(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            List<String> validLines = new ArrayList<>();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    validLines.add(line);
                }
            }
            return validLines;
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath + " -> " + e.getMessage());
            return Collections.emptyList();
        }
    }
}