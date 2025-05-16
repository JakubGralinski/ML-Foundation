package pl.edu.pja.tpo03;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Perceptron {
    private int dimension;
    private double[] weights;
    //private double bias; // threshold = -bias
    private int epochs;
    private double threshold;
    private double alpha;
    private double beta;


    public Perceptron(int dimension, double alpha, double beta, double initThreshold) {
        this.dimension = dimension;
        this.weights = new double[dimension];
        this.alpha = alpha;
        this.beta = beta;
        this.threshold = initThreshold;
        Random rand = new Random();
        for (int i = 0; i < dimension; i++) {
            weights[i] = rand.nextDouble() - 0.5; // initialize between -0.5 and 0.5
        }
    }
    public int predict(double[] inputs) {
        double sum = 0.0;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * inputs[i];
        }
        return (sum >= threshold) ? 1 : 0;
    }

    // Delta Rule
    public void train(double[][] inputs, int[] labels, int maxEpochs, List<Double> epochAccuracies) {
        epochs = 0;
        boolean converged;
        int n = inputs.length;

        for (int epoch = 0; epoch < maxEpochs; epoch++) {
            converged = true;
            for (int i = 0; i < n; i++) {
                int prediction = predict(inputs[i]);
                int error = labels[i] - prediction;
                if (error != 0) {
                    for (int j = 0; j < dimension; j++) {
                        weights[j] += alpha * error * inputs[i][j];
                    }
                    // Update threshold
                    threshold = threshold - alpha * error;
                    converged = false;
                }
            }
            epochs++;

            // Calculate training accuracy after this epoch.
            int correct = 0;
            for (int i = 0; i < n; i++) {
                if (predict(inputs[i]) == labels[i])
                    correct++;
            }
            double accuracy = (double) correct / n;
            epochAccuracies.add(accuracy);

            if (converged) break;
        }

    }

    public double[] getWeights() {
        return weights;
    }

    public double getThreshold() {
        return threshold;
    }

    public void printModel() {
        System.out.println("Final Weights: " + Arrays.toString(weights));
        System.out.println("Final Threshold: " + threshold);
        System.out.println("Total Epochs: " + epochs);
    }

}