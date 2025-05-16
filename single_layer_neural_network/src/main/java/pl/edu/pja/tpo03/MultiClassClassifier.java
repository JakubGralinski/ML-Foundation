package pl.edu.pja.tpo03;


import java.util.ArrayList;
import java.util.List;

/**
 * MultiClassClassifier uses multiple Perceptron instances to classify
 * inputs among multiple classes using a "local output representation."
 */
public class MultiClassClassifier {
    private List<Perceptron> perceptrons; // One Perceptron per class
    private List<String> classLabels;

/**
 * Constructor
 * @param classLabels  A list of unique class labels
 * @param dimension    The dimensionality of the feature vectors
 * @param alpha        Learning rate for the Perceptrons
 * @param beta         Beta parameter for the Perceptrons
 */
    public MultiClassClassifier(List<String > classLabels, int dimension, double alpha, double beta) {
        this.classLabels = classLabels;
        this.perceptrons = new ArrayList<>();

        for (int i = 0; i < classLabels.size(); i++) {
            double initThreshold = 0.0;
            Perceptron perceptron = new Perceptron(dimension, alpha, beta, initThreshold);
            perceptrons.add(perceptron);
        }
    }

    /**
     * Trains all Perceptrons using local output representation.
     * Each Perceptron sees label=1 for its own class, and label=0 otherwise.
     *
     * @param trainFeatures  A list of feature vectors (double[])
     * @param trainLabels    A list of labels corresponding to each feature vector
     * @param maxEpochs      Maximum epochs for training
     */
    public void train(List<double[]> trainFeatures, List<String> trainLabels, int maxEpochs) {
        if (trainFeatures.size() != trainLabels.size()) {
            throw new IllegalArgumentException("trainFeatures and trainLabels size mismatch");
        }

        for(int i = 0; i < perceptrons.size(); i++) {
            Perceptron perceptron = perceptrons.get(i);
            String classLabel = classLabels.get(i);

            double[][] inputArr = new double[trainFeatures.size()][];
            int[] targetArr = new int[trainFeatures.size()];

            for (int j = 0; j < trainFeatures.size(); j++) {
                inputArr[j] = trainFeatures.get(j);

                if(trainLabels.get(j).equals(classLabel)) {
                    targetArr[j] = 1;
                }else {
                    targetArr[j] = 0;
                }
            }

            // Acc in dummy list
            List<Double> epochAccuracies = new ArrayList<>();

            perceptron.train(inputArr, targetArr, 1, epochAccuracies);
            //TODO: Checking convergance, not only max epochs
        }
    }
    /**
     * Predicts the class for a single feature vector using local output representation.
     * If multiple perceptrons are above threshold (predict=1), chooses the one with
     * the greatest net= w^T x - threshold.
     *
     * @param features A single feature vector
     * @return The predicted class label
     */
    public String predict(double[] features) {
        List<Integer> predictions = new ArrayList<>();
        List<Double> netValues = new ArrayList<>();

        // Net for each perceptron
        for (int i = 0; i < perceptrons.size(); i++) {
            double sum = 0.0;
            double[] weights = perceptrons.get(i).getWeights();

            for (int j = 0; j < weights.length; j++) {
                sum += weights[j] * features[j];
            }
            double net = sum - perceptrons.get(i).getThreshold();

            netValues.add(net);
        }

        double bestNet = Double.NEGATIVE_INFINITY;
        int bestIndex = -1;

        for (int i = 0; i < netValues.size(); i++) {
            double net = netValues.get(i);
            if (net > bestNet) {
                bestNet = net;
                bestIndex = i;
            }
        }

        return classLabels.get(bestIndex);
    }

    /**
     * Batch-predict multiple feature vectors.
     *
     * @param testFeatures A list of feature vectors
     * @return The list of predicted class labels
     */
    public List<String> predictBatch(List<double[]> testFeatures) {
        List<String> preds = new ArrayList<>();
        for (double[] f : testFeatures) {
            preds.add(predict(f));
        }
        return preds;
    }

    public void printNetwork() {
        for (int i = 0; i < perceptrons.size(); i++) {
            System.out.println("Perceptron for class: " + classLabels.get(i));
            perceptrons.get(i).printModel();
            System.out.println();
        }
    }
    }