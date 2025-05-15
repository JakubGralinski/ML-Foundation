package CCS;

import java.util.*;

public class KNearestNeighbours {
    private int k;  // Number of nearest neighbors to consider
    private List<Observation> trainDataset;

    public KNearestNeighbours(int k, List<Observation> trainDataset) {
        this.k = k;
        this.trainDataset = trainDataset;
    }

    // Calculate Euclidean distance between two vectors.
    public double calculateEuclideanDistance(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Dimensions must match");
        }
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    public int[] sortDistances(double[] observation) {
        int n = trainDataset.size();
        double[] distances = new double[n];
        for (int i = 0; i < n; i++) {
            distances[i] = calculateEuclideanDistance(observation, trainDataset.get(i).getFeatures());
        }
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }

        Arrays.sort(indices, Comparator.comparingDouble(i -> distances[i]));

        // Convert Integer[] back to int[]
        int[] sortedIndices = new int[n];
        for (int i = 0; i < n; i++) {
            sortedIndices[i] = indices[i];
        }

        return sortedIndices;
    }

    // Find the mode among the k nearest neighbors.
    // If there is a tie, randomly select one of the top classes.
    public String findPredictedClass(int[] sortedIndices) {
        Map<String, Integer> freq = new HashMap<>();
        // Count the frequency for the top k neighbors.
        for (int i = 0; i < Math.min(k, sortedIndices.length); i++) {
            String label = trainDataset.get(sortedIndices[i]).getLabel();
            freq.put(label, freq.getOrDefault(label, 0) + 1);
        }
        // Determine maximum frequency.
        int maxFreq = 0;
        for (int count : freq.values()) {
            if (count > maxFreq) {
                maxFreq = count;
            }
        }
        // Collect all classes that have the maximum frequency.
        List<String> candidates = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : freq.entrySet()) {
            if (entry.getValue() == maxFreq) {
                candidates.add(entry.getKey());
            }
        }
        // If there's more select one at random.
        Random rand = new Random();
        return candidates.get(rand.nextInt(candidates.size()));
    }

    public String predict(double[] observation) {
        int[] sortedIndices = sortDistances(observation);
        return findPredictedClass(sortedIndices);
    }
}