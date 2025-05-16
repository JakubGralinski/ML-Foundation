package pl.edu.pja.tpo02;

import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        DataSet dataset = PrepareDataset.loadIrisDataset("IRIS.csv");
        DataSet[] splits = PrepareDataset.trainTestSplit(dataset, 0.7);
        DataSet trainSet = splits[0];
        DataSet testSet = splits[1];

        // Use all 4 features
        double[][] trainFeatures = trainSet.features;
        double[][] testFeatures = testSet.features;

        // Create the perceptron with dimension=4
        double alpha = 0.1;
        double beta = 0.0; // Not used
        double initialThreshold = 0.0;
        Perceptron perceptron = new Perceptron(4, alpha, beta, initialThreshold);

        // Train the perceptron and record accuracy per epoch
        List<Double> epochAccuracies = new ArrayList<>();
        int maxEpochs = 1000;
        perceptron.train(trainFeatures, trainSet.labels, maxEpochs, epochAccuracies);
        perceptron.printModel();

        // Predict on the test set and measure accuracy
        int[] predictedTest = new int[testFeatures.length];
        for (int i = 0; i < testFeatures.length; i++) {
            predictedTest[i] = perceptron.predict(testFeatures[i]);
        }
        double testAccuracy = EvaluationMetrics.measureAccuracy(testSet.labels, predictedTest);
        System.out.println("Test Accuracy: " + testAccuracy);

        System.out.println("Training Accuracy per Epoch:");
        for (int i = 0; i < epochAccuracies.size(); i++) {
            System.out.println("Epoch " + (i + 1) + ": " + epochAccuracies.get(i));
        }

        // Save training accuracy CSV
        String home = System.getProperty("user.home");
        String outputPath = home + "/Desktop/studies/PJATK_25_Summer_Semester/NAI/NAI_perceptron_implementation/NAI_perceptron_mapping/training_accuracy.csv";
        try (PrintWriter writer = new PrintWriter(new File(outputPath))) {
            writer.println("Epoch,Accuracy");
            for (int i = 0; i < epochAccuracies.size(); i++) {
                writer.println((i + 1) + "," + epochAccuracies.get(i));
            }
            System.out.println("Training accuracy CSV written to: " + outputPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Save perceptron weights and threshold to CSV
        double[] weights = perceptron.getWeights();
        double threshold = perceptron.getThreshold();

        String weightsPath = home + "/Desktop/studies/PJATK_25_Summer_Semester/NAI/NAI_perceptron_implementation/NAI_perceptron_mapping/perceptron_weights.csv";
        try (PrintWriter writer = new PrintWriter(new File(weightsPath))) {
            writer.println("w0,w1,w2,w3,threshold");
            writer.println(weights[0] + "," + weights[1] + "," + weights[2] + "," + weights[3] + "," + threshold);
            System.out.println("Perceptron weights CSV written to: " + weightsPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Save test observations CSV
        String testObsPath = home + "/Desktop/studies/PJATK_25_Summer_Semester/NAI/NAI_perceptron_implementation/NAI_perceptron_mapping/test_observations.csv";
        try (PrintWriter writer = new PrintWriter(new File(testObsPath))) {
            writer.println("sepal_length,sepal_width,petal_length,petal_width,label");
            for (int i = 0; i < testFeatures.length; i++) {
                writer.println(
                        testFeatures[i][0] + "," +
                                testFeatures[i][1] + "," +
                                testFeatures[i][2] + "," +
                                testFeatures[i][3] + "," +
                                testSet.labels[i]
                );
            }
            System.out.println("Test observations CSV written to: " + testObsPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Prediction on new input via console
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter new observation for prediction:");
        System.out.print("Enter sepal length: ");
        double sepalLength = scanner.nextDouble();
        System.out.print("Enter sepal width: ");
        double sepalWidth = scanner.nextDouble();
        System.out.print("Enter petal length: ");
        double petalLength = scanner.nextDouble();
        System.out.print("Enter petal width: ");
        double petalWidth = scanner.nextDouble();

        int prediction = perceptron.predict(new double[]{sepalLength, sepalWidth, petalLength, petalWidth});
        System.out.println("Predicted class: " + (prediction == 1 ? "setosa" : "versicolor"));
        scanner.close();
    }
}