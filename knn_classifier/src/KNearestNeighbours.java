import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KNearestNeighbours {
    private int k; //number of nearest observations used to determine the predicted class
    private List<Observation> trainDataset; //list of all training vectors with their classes

    public KNearestNeighbours(int k, List<Observation> trainDataset) {
        this.k = k;
        this.trainDataset = trainDataset;
    }

    // Calculate Euclidean distance between two feature vectors.
    public double calculateEuclideanDistance(double[] a, double[] b) {
        if (a.length != b.length)
            throw new IllegalArgumentException("Dimension mismatch");
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
        int[] indices = new int[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }
        // selection sort for dists
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (distances[indices[i]] > distances[indices[j]]) {
                    int temp = indices[i];
                    indices[i] = indices[j];
                    indices[j] = temp;
                }
            }
        }
        return indices;
    }

    // Find the most frequently occurring class among the k nearest neighbours.
    // random if a tie
    public String findPredictedClass(int[] sortedIndices) {
        java.util.Map<String, Integer> freq = new java.util.HashMap<>();
        // Count frequencies for the first k neighbours.
        for (int i = 0; i < k && i < sortedIndices.length; i++) {
            String label = trainDataset.get(sortedIndices[i]).getLabel();
            freq.put(label, freq.getOrDefault(label, 0) + 1);
        }
        int maxFreq = 0;
        for (int count : freq.values()) {
            if (count > maxFreq)
                maxFreq = count;
        }

        List<String> candidates = new ArrayList<>();
        for (String label : freq.keySet()) {
            if (freq.get(label) == maxFreq)
                candidates.add(label);
        }
        // If there is more than one candidate, pick one at random.
        Random rand = new Random();
        return candidates.get(rand.nextInt(candidates.size()));
    }

    public String predict(double[] observation) {
        int[] sortedIndices = sortDistances(observation);
        return findPredictedClass(sortedIndices);
    }
}