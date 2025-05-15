package CCS;

import javax.swing.*;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // ----------------------------
        // MATRIX OPERATIONS SECTION
        // ----------------------------
        // 1) Select dimensions for Matrix A using the GUI dialog.
        SizeSelector selectorA = new SizeSelector(null);
        selectorA.setVisible(true); // Blocks until the dialog is closed.

        if (!selectorA.isConfirmed()) {
            System.out.println("Matrix A size selection canceled. Exiting.");
            System.exit(0);
        }
        int rowsA = selectorA.getSelectedRows();
        int colsA = selectorA.getSelectedCols();

        // 2) Read Matrix A elements from console.
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter elements for matrix A (" + rowsA + "x" + colsA + "):");
        Matrix matA = new Matrix(rowsA, colsA);
        int totalA = rowsA * colsA;
        for (int i = 0; i < totalA; i++) {
            while (!scanner.hasNextDouble()) {
                System.out.println("Invalid input. Please enter a number:");
                scanner.next(); // Discard invalid token.
            }
            double value = scanner.nextDouble();
            matA.set(i / colsA, i % colsA, value);
        }
        // Flush any extra tokens on the line.
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
        System.out.println("Matrix A filled. Proceeding to Matrix B size selection...");

        // 3) Select dimensions for Matrix B.
        SizeSelector selectorB = new SizeSelector(null);
        selectorB.setVisible(true); // Blocks until closed.
        if (!selectorB.isConfirmed()) {
            System.out.println("Matrix B size selection canceled. Exiting.");
            System.exit(0);
        }
        int rowsB = selectorB.getSelectedRows();
        int colsB = selectorB.getSelectedCols();

        // 4) Read Matrix B elements from console.
        System.out.println("Enter elements for matrix B (" + rowsB + "x" + colsB + "):");
        Matrix matB = new Matrix(rowsB, colsB);
        int totalB = rowsB * colsB;
        for (int i = 0; i < totalB; i++) {
            while (!scanner.hasNextDouble()) {
                System.out.println("Invalid input. Please enter a number:");
                scanner.next();
            }
            double value = scanner.nextDouble();
            matB.set(i / colsB, i % colsB, value);
        }
        // Flush any extra tokens.
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        // ----------------------------
        // Display Matrix Operations Results
        // ----------------------------
        System.out.println("\nMatrix A:");
        matA.display();
        System.out.println("Matrix B:");
        matB.display();

        System.out.println("Transpose of Matrix A:");
        matA.transpose().display();
        System.out.println("Transpose of Matrix B:");
        matB.transpose().display();

        System.out.println("Matrix A after Gaussian Elimination:");
        matA.gaussianElimination().display();
        System.out.println("Matrix B after Gaussian Elimination:");
        matB.gaussianElimination().display();

        try {
            System.out.println("Matrix A + B:");
            matA.add(matB).display();
            System.out.println("Matrix A - B:");
            matA.subtract(matB).display();
            System.out.println("Matrix A * B:");
            matA.multiply(matB).display();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }


        // Demonstrate vector operations using VectorOperations.
        double[] vecSum = VectorOperations.add(matA, matB);
        double[] vecScaled = VectorOperations.multiply(matA, 2.0);
        double norm1 = VectorOperations.euclideanNorm(matA);
        double distance = VectorOperations.euclideanDistance(matA, matB);
        double dot = VectorOperations.dotProduct(matA, matB);

        System.out.println("Vector 1: " + Arrays.toString(matA.toVector()));
        System.out.println("Vector 2: " + Arrays.toString(matB.toVector()));
        System.out.println("Vector 1 + Vector 2: " + Arrays.toString(vecSum));
        System.out.println("2.0 * Vector 1: " + Arrays.toString(vecScaled));
        System.out.println("||Vector 1||: " + norm1);
        System.out.println("Distance between Vector 1 and Vector 2: " + distance);
        System.out.println("Dot product of Vector 1 and Vector 2: " + dot);

        // ----------------------------
        // KNN CLASSIFICATION SECTION (First Task)
        // ----------------------------
        System.out.println("\n=== KNN Classification Demo ===");
        System.out.print("Enter the value of k (number of neighbours): ");
        int k = scanner.nextInt();

        // Ask user for the number of features for observations.
        System.out.print("Enter the number of features for each observation: ");
        int numFeatures = scanner.nextInt();

        // Ask for number of training observations.
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

        // Create KNN classifier.
        KNearestNeighbours knn = new KNearestNeighbours(k, trainingData);

        // Ask for new observation features.
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

        // Finally, close the scanner.
        scanner.close();
    }
}