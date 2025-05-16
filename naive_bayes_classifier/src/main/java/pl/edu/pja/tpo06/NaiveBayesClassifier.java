package pl.edu.pja.tpo06;

import java.util.*;

public class NaiveBayesClassifier {
    private final boolean applySmoothingAll;
    private final List<Map<String, String>> trainDataset;
    private final Map<String, Double> priorProbabilities;
    private final Map<String, Map<String, Map<String, Double>>> conditionalProbabilities;
    private final Set<String> classLabels;
    private final Map<String, Set<String>> attributeValues;

    public NaiveBayesClassifier(boolean applySmoothingAll, List<Map<String, String>> trainDataset) {
        this.applySmoothingAll = applySmoothingAll;
        this.trainDataset = trainDataset;
        this.priorProbabilities = new HashMap<>();
        this.conditionalProbabilities = new HashMap<>();
        this.classLabels = new HashSet<>();
        this.attributeValues = new HashMap<>();
        
        calculateProbabilities();
    }

    private void calculateProbabilities() {
        // Extract class labels and attribute values
        String labelAttribute = "class";
        for (Map<String, String> instance : trainDataset) {
            classLabels.add(instance.get(labelAttribute));
            for (Map.Entry<String, String> entry : instance.entrySet()) {
                if (!entry.getKey().equals(labelAttribute)) {
                    attributeValues.computeIfAbsent(entry.getKey(), k -> new HashSet<>())
                            .add(entry.getValue());
                }
            }
        }

        // Calculate prior probabilities P(C)
        int totalInstances = trainDataset.size();
        for (String classLabel : classLabels) {
            long classCount = trainDataset.stream()
                    .filter(instance -> instance.get(labelAttribute).equals(classLabel))
                    .count();
            priorProbabilities.put(classLabel, (double) classCount / totalInstances);
        }

        // Calculate conditional probabilities P(X|C)
        for (String attribute : attributeValues.keySet()) {
            Map<String, Map<String, Double>> attributeProbabilities = new HashMap<>();
            
            for (String classLabel : classLabels) {
                Map<String, Double> valueProbabilities = new HashMap<>();
                List<Map<String, String>> classInstances = trainDataset.stream()
                        .filter(instance -> instance.get(labelAttribute).equals(classLabel))
                        .toList();
                
                for (String value : attributeValues.get(attribute)) {
                    long valueCount = classInstances.stream()
                            .filter(instance -> instance.get(attribute).equals(value))
                            .count();
                    
                    double probability;
                    if (valueCount == 0 || applySmoothingAll) {
                        // Apply Laplace smoothing
                        probability = NaiveBayesSmoothing.simpleSmoothing((int) (valueCount + 1), classInstances.size(), attributeValues.get(attribute).size());
                        //probability = (valueCount + 1.0) / (classInstances.size() + attributeValues.get(attribute).size());
                    } else {
                        probability = (double) valueCount / classInstances.size();
                    }
                    valueProbabilities.put(value, probability);
                }
                attributeProbabilities.put(classLabel, valueProbabilities);
            }
            conditionalProbabilities.put(attribute, attributeProbabilities);
        }
    }

    public String predict(Map<String, String> input) {
        String bestClass = null;
        double maxProbability = Double.NEGATIVE_INFINITY;

        for (String classLabel : classLabels) {
            double probability = Math.log(priorProbabilities.get(classLabel));

            for (Map.Entry<String, String> entry : input.entrySet()) {
                if (!entry.getKey().equals("class")) {
                    Map<String, Map<String, Double>> attributeProbabilities = conditionalProbabilities.get(entry.getKey());
                    Map<String, Double> classProbabilities = attributeProbabilities.get(classLabel);
                    double condProb = classProbabilities.get(entry.getValue());
                    probability += Math.log(condProb);
                }
            }

            if (probability > maxProbability) {
                maxProbability = probability;
                bestClass = classLabel;
            }
        }

        return bestClass;
    }

    public Map<String, Double> getPriorProbabilities() {
        return priorProbabilities;
    }

    public Map<String, Map<String, Map<String, Double>>> getConditionalProbabilities() {
        return conditionalProbabilities;
    }

    public Set<String> getClassLabels() {
        return classLabels;
    }
}
