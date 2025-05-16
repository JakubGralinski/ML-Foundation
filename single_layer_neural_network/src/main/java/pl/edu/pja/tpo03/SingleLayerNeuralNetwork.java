package pl.edu.pja.tpo03;

import java.util.ArrayList;
import java.util.List;

/**
 * SingleLayerNeuralNetwork manages a set of Perceptrons (one per class)
 * using local output representation.
 *
 * Requirements:
 * - Use a dictionary (here, List) to store Perceptrons for each class.
 * - Use functions to train (using the delta rule) and predict the class for an input vector.
 */
public class SingleLayerNeuralNetwork {
    private List<Perceptron> neurons; // One perceptron per class.
    private double alpha;             // Learning rate.
    private double beta;              // Bias/threshold value.

    /**
     * Constructor for the SingleLayerNeuralNetwork.
     *
     * @param classesLen      Number of classes (and thus the number of perceptrons).
     * @param neuronDimension Dimensionality of each input vector.
     * @param alpha           Learning rate.
     * @param beta            Bias (threshold) value.
     * @throws Exception      If perceptron initialization fails.
     */
    public SingleLayerNeuralNetwork(int classesLen, int neuronDimension, double alpha, double beta) throws Exception {
        this.alpha = alpha;
        this.beta = beta;
        this.neurons = new ArrayList<>();
        // Create a perceptron for each class.
        for (int i = 0; i < classesLen; i++) {
            double[] initialWeights = new double[neuronDimension];
            // Initialize weights to 0 (or you can initialize randomly if preferred).
            for (int j = 0; j < neuronDimension; j++) {
                initialWeights[j] = 0.0;
            }
            // Create the perceptron. Adjust the constructor call to match your Perceptron class.
            Perceptron neuron = new Perceptron(neuronDimension, alpha, beta, 0.0);
            neurons.add(neuron);
        }
    }

    /**
     * Trains the neural network layer.
     * For each perceptron, convert multi-class labels to binary labels:
     *   - 1 if the sample belongs to that class.
     *   - 0 otherwise.
     *
     * @param inputs A 2D array of input feature vectors.
     * @param labels An array of integer labels.
     * @param maxEpochs Maximum epochs for each perceptron during training.
     * @throws Exception if training fails.
     */
    public void trainLayer(double[][] inputs, int[] labels, int maxEpochs) throws Exception {
        int numSamples = inputs.length;
        for (int classIndex = 0; classIndex < neurons.size(); classIndex++) {
            Perceptron neuron = neurons.get(classIndex);
            int[] binaryLabels = new int[numSamples];
            for (int i = 0; i < numSamples; i++) {
                binaryLabels[i] = (labels[i] == classIndex) ? 1 : 0;
            }
            // Train the perceptron with its binary labels.
            // Here, we call neuron.train with maxEpochs and a dummy list for epochAccuracies.
            neuron.train(inputs, binaryLabels, maxEpochs, new ArrayList<Double>());
        }
    }

    /**
     * Predicts the class for a given input vector.
     *
     * @param inputs A feature vector.
     * @return The predicted class as an integer.
     * @throws Exception if prediction fails.
     */
    public int predict(double[] inputs) throws Exception {
        double maxNetValue = Double.NEGATIVE_INFINITY;
        int predictionResult = -1;
        for (int classIndex = 0; classIndex < neurons.size(); classIndex++) {
            Perceptron neuron = neurons.get(classIndex);
            double netValue = 0.0;
            double[] weights = neuron.getWeights();
            for (int i = 0; i < inputs.length; i++) {
                netValue += weights[i] * inputs[i];
            }
            netValue -= neuron.getThreshold();
            if (netValue > maxNetValue) {
                maxNetValue = netValue;
                predictionResult = classIndex;
            }
        }
        return predictionResult;
    }

    /**
     * Batch-predicts the class for multiple input vectors.
     *
     * @param testFeatures A list of feature vectors.
     * @return A list of predicted class labels.
     * @throws Exception if prediction fails.
     */
    public List<Integer> predictBatch(List<double[]> testFeatures) throws Exception {
        List<Integer> predictions = new ArrayList<>();
        for (double[] feature : testFeatures) {
            predictions.add(predict(feature));
        }
        return predictions;
    }

    /**
     * Prints the final model details for each perceptron.
     */
    public void printNetwork() {
        for (int i = 0; i < neurons.size(); i++) {
            System.out.println("Perceptron for class: " + i);
            neurons.get(i).printModel();
            System.out.println();
        }
    }
}