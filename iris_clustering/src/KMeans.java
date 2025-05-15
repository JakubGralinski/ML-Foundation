import java.util.Arrays;
import java.util.Random;

public class KMeans {
    private final int k;
    private final int maxIterations;
    private double[][] centroids;

    public KMeans(int k, int maxIterations) {
        if (k <= 0) throw new IllegalArgumentException("k must be > 0");
        this.k = k;
        this.maxIterations = maxIterations;
    }

    public void fit(double[][] data) {
        int n = data.length;
        int dim = data[0].length;
        int[] labels = new int[n];
        Random rnd = new Random();

        // 1) Random initial assignment ensuring at least one per cluster
        for (int i = 0; i < n; i++) {
            labels[i] = (i < k) ? i : rnd.nextInt(k);
        }

        // 2) Compute initial centroids
        centroids = new double[k][dim];
        updateCentroids(data, labels);

        // 3) Iterate until convergence or maxIterations
        for (int iter = 0; iter < maxIterations; iter++) {
            boolean changed = false;

            // assign each point to nearest centroid
            for (int i = 0; i < n; i++) {
                int best = findClosestCluster(data[i], centroids);
                if (best != labels[i]) {
                    labels[i] = best;
                    changed = true;
                }
            }

            // recompute centroids
            double[][] newCentroids = computeCentroids(data, labels, dim);
            if (Arrays.deepEquals(newCentroids, centroids)) {
                break;  // converged
            }
            centroids = newCentroids;
            if (!changed) break;
        }
    }

    public int[] predict(double[][] data) {
        int[] labels = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            labels[i] = findClosestCluster(data[i], centroids);
        }
        return labels;
    }

    public double[][] getCentroids() {
        return centroids;
    }

    // helper to recompute centroids in-place
    private void updateCentroids(double[][] data, int[] labels) {
        centroids = computeCentroids(data, labels, data[0].length);
    }

    private static double[][] computeCentroids(double[][] data, int[] labels, int dim) {
        int k = Arrays.stream(labels).max().orElseThrow() + 1;
        double[][] sums = new double[k][dim];
        int[] counts = new int[k];

        for (int i = 0; i < data.length; i++) {
            int lbl = labels[i];
            for (int j = 0; j < dim; j++) {
                sums[lbl][j] += data[i][j];
            }
            counts[lbl]++;
        }

        double[][] cent = new double[k][dim];
        for (int c = 0; c < k; c++) {
            if (counts[c] == 0) continue; // shouldn't happen after init
            for (int j = 0; j < dim; j++) {
                cent[c][j] = sums[c][j] / counts[c];
            }
        }
        return cent;
    }

    // from first task
    private static int findClosestCluster(double[] vector, double[][] centroids) {
        double minDistance = Double.MAX_VALUE;
        java.util.List<Integer> closest = new java.util.ArrayList<>();
        for (int i = 0; i < centroids.length; i++) {
            double dist = euclideanDistance(vector, centroids[i]);
            if (dist < minDistance) {
                minDistance = dist;
                closest.clear();
                closest.add(i);
            } else if (dist == minDistance) {
                closest.add(i);
            }
        }
        return (closest.size() == 1)
                ? closest.get(0)
                : closest.get(new Random().nextInt(closest.size()));
    }

    private static double euclideanDistance(double[] v1, double[] v2) {
        if (v1.length != v2.length)
            throw new IllegalArgumentException("Vectors must have same dim");
        double sum = 0;
        for (int i = 0; i < v1.length; i++) {
            double d = v1[i] - v2[i];
            sum += d * d;
        }
        return Math.sqrt(sum);
    }
}