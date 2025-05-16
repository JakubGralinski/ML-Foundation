package pl.edu.pja.tpo06;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Map<String, String>> fullDataset = new ArrayList<>();
        
        // Adding all instances from the outGame dataset
        addInstance(fullDataset, "sunny", "hot", "high", "false", "no");
        addInstance(fullDataset, "sunny", "hot", "high", "true", "no");
        addInstance(fullDataset, "overcast", "hot", "high", "false", "yes");
        addInstance(fullDataset, "rainy", "mild", "high", "false", "yes");
        addInstance(fullDataset, "rainy", "cool", "normal", "false", "yes");
        addInstance(fullDataset, "rainy", "cool", "normal", "true", "no");
        addInstance(fullDataset, "overcast", "cool", "normal", "true", "yes");
        addInstance(fullDataset, "sunny", "mild", "high", "false", "no");
        addInstance(fullDataset, "sunny", "cool", "normal", "false", "yes");
        addInstance(fullDataset, "rainy", "mild", "normal", "false", "yes");
        addInstance(fullDataset, "sunny", "mild", "normal", "true", "yes");
        addInstance(fullDataset, "overcast", "mild", "high", "true", "yes");
        addInstance(fullDataset, "overcast", "hot", "normal", "false", "yes");
        addInstance(fullDataset, "rainy", "mild", "high", "true", "no");

        // Create a specific test case that has rare/unseen combinations
        List<Map<String, String>> testDataset = new ArrayList<>();
        // This combination (overcast + cool + high + true) doesn't appear in the training data
        addInstance(testDataset, "overcast", "cool", "high", "true", "yes");
        
        // Use all instances for training
        List<Map<String, String>> trainDataset = new ArrayList<>(fullDataset);

        // Create classifier instances with and without smoothing
        System.out.println("Training Naive Bayes Classifier with smoothing...");
        NaiveBayesClassifier classifierWithSmoothing = new NaiveBayesClassifier(true, trainDataset);

        System.out.println("\nTraining Naive Bayes Classifier without smoothing...");
        NaiveBayesClassifier classifierWithoutSmoothing = new NaiveBayesClassifier(false, trainDataset);

        System.out.println("\nTraining Data Statistics:");
        System.out.println("Number of instances: " + trainDataset.size());
        
        System.out.println("\nConditional Probabilities (With Smoothing):");
        printProbabilities(classifierWithSmoothing);
        
        System.out.println("\nConditional Probabilities (Without Smoothing):");
        printProbabilities(classifierWithoutSmoothing);

        // Evaluate both classifiers
        evaluateClassifier("With Smoothing", classifierWithSmoothing, testDataset);
        evaluateClassifier("Without Smoothing", classifierWithoutSmoothing, testDataset);
    }

    private static void printProbabilities(NaiveBayesClassifier classifier) {
        Map<String, Map<String, Map<String, Double>>> probs = classifier.getConditionalProbabilities();
        Map<String, Double> priorProbs = classifier.getPriorProbabilities();
        
        System.out.println("Prior Probabilities:");
        priorProbs.forEach((label, prob) -> 
            System.out.printf("P(class=%s) = %.4f%n", label, prob));
        
        System.out.println("\nExample Conditional Probabilities for 'outlook':");
        Map<String, Map<String, Double>> outlookProbs = probs.get("outlook");
        if (outlookProbs != null) {
            outlookProbs.forEach((classLabel, valueProbs) -> {
                System.out.printf("For class=%s:%n", classLabel);
                valueProbs.forEach((value, prob) -> 
                    System.out.printf("P(outlook=%s|class=%s) = %.4f%n", 
                                    value, classLabel, prob));
            });
        }
    }

    private static void addInstance(List<Map<String, String>> dataset,
                                    String outlook, String temperature,
                                    String humidity, String windy, String playClass) {
        Map<String, String> instance = new HashMap<>();
        instance.put("outlook", outlook);
        instance.put("temperature", temperature);
        instance.put("humidity", humidity);
        instance.put("windy", windy);
        instance.put("class", playClass);
        dataset.add(instance);
    }

    private static void evaluateClassifier(String name, NaiveBayesClassifier classifier, List<Map<String, String>> testDataset) {
        System.out.println("\nEvaluating " + name + ":");
        EvaluationMetrics metrics = new EvaluationMetrics(classifier.getClassLabels());

        for (Map<String, String> instance : testDataset) {
            String actualClass = instance.get("class");
            String predictedClass = classifier.predict(instance);
            metrics.addPrediction(actualClass, predictedClass);
            
            System.out.println("\nInstance: " + instance);
            System.out.println("Actual class: " + actualClass);
            System.out.println("Predicted class: " + predictedClass);
        }

        metrics.printMetrics();
    }
}