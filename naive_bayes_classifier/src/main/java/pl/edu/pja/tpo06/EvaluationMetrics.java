package pl.edu.pja.tpo06;

import java.util.*;

public class EvaluationMetrics {
    private final Map<String, Integer> truePositives;
    private final Map<String, Integer> falsePositives;
    private final Map<String, Integer> falseNegatives;
    private final Set<String> classLabels;
    private int totalPredictions;

    public EvaluationMetrics(Set<String> classLabels) {
        this.classLabels = classLabels;
        this.truePositives = new HashMap<>();
        this.falsePositives = new HashMap<>();
        this.falseNegatives = new HashMap<>();
        this.totalPredictions = 0;
        
        for (String label : classLabels) {
            truePositives.put(label, 0);
            falsePositives.put(label, 0);
            falseNegatives.put(label, 0);
        }
    }

    public void addPrediction(String actual, String predicted) {
        totalPredictions++;
        
        if (actual.equals(predicted)) {
            truePositives.merge(actual, 1, Integer::sum);
        } else {
            falsePositives.merge(predicted, 1, Integer::sum);
            falseNegatives.merge(actual, 1, Integer::sum);
        }
    }

    public double getAccuracy() {
        int totalTruePositives = truePositives.values().stream().mapToInt(Integer::intValue).sum();
        return totalPredictions > 0 ? (double) totalTruePositives / totalPredictions : 0.0;
    }

    public double getPrecision(String classLabel) {
        int tp = truePositives.get(classLabel);
        int fp = falsePositives.get(classLabel);
        return (tp + fp) > 0 ? (double) tp / (tp + fp) : 0.0;
    }

    public double getRecall(String classLabel) {
        int tp = truePositives.get(classLabel);
        int fn = falseNegatives.get(classLabel);
        return (tp + fn) > 0 ? (double) tp / (tp + fn) : 0.0;
    }

    public double getFMeasure(String classLabel) {
        double precision = getPrecision(classLabel);
        double recall = getRecall(classLabel);
        return (precision + recall) > 0 ? 
            2 * (precision * recall) / (precision + recall) : 0.0;
    }

    public void printMetrics() {
        System.out.println("\nOverall Accuracy: " + String.format("%.4f", getAccuracy()));
        System.out.println("\nPer-class metrics:");
        
        for (String label : classLabels) {
            System.out.println("\nClass: " + label);
            System.out.println("Precision: " + String.format("%.4f", getPrecision(label)));
            System.out.println("Recall: " + String.format("%.4f", getRecall(label)));
            System.out.println("F-measure: " + String.format("%.4f", getFMeasure(label)));
        }
    }
}
