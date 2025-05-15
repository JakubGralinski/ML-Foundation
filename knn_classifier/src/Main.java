import javax.swing.*;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        // ----------------------------
        // KNN CLASSIFICATION SECTION (First Task)
        // ----------------------------
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the value of k (number of neighbours): ");
        int k = scanner.nextInt();

        System.out.print("Enter the number of features for each observation: ");
        int numFeatures = scanner.nextInt();

        System.out.print("Enter the number of training observations: ");
        int numTrain = scanner.nextInt();

        List<Observation> trainingData = new ArrayList<>();
        for (int i = 0; i < numTrain; i++) {
            double[] features = new double[numFeatures];
            System.out.println("Enter " + numFeatures + " features for observation " + (i + 1) + ":");
            for (int j = 0; j < numFeatures; j++) {
                while (!scanner.hasNextDouble()) {
                    System.out.println("Invalid input. Please enter a number:");
                    scanner.next();
                }
                features[j] = scanner.nextDouble();
            }
            System.out.print("Enter class label for observation " + (i + 1) + ": ");
            String label = scanner.next();
            trainingData.add(new Observation(features, label));
        }

        KNearestNeighbours knn = new KNearestNeighbours(k, trainingData);

        double[] newObservation = new double[numFeatures];
        System.out.println("Enter " + numFeatures + " features for a new observation to classify:");
        for (int j = 0; j < numFeatures; j++) {
            while (!scanner.hasNextDouble()) {
                System.out.println("Invalid input. Please enter a number:");
                scanner.next();
            }
            newObservation[j] = scanner.nextDouble();
        }

        String predictedClass = knn.predict(newObservation);
        System.out.println("The predicted class for the new observation is: " + predictedClass);

        scanner.close();
    }
}