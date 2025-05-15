import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    /**
     * Finds the closest cluster centroid to the given vector using Euclidean distance.
     * @param vector -The input vector to classify
     * @param centroids -Array of centroid vectors
     * returns the index of closest centroid
     */
    public static int findClosestCluster(double[] vector, double[][] centroids) {
        if (vector == null || centroids == null || centroids.length == 0) {
            throw new IllegalArgumentException("Vector and centroids cannot be null or empty");
        }
        
        double minDistance = Double.MAX_VALUE;
        java.util.List<Integer> closestIndices = new java.util.ArrayList<>();
        
        // Calculate distances to all centroids
        for (int i = 0; i < centroids.length; i++) {
            double distance = euclideanDistance(vector, centroids[i]);
            
            if (distance < minDistance) {
                minDistance = distance;
                closestIndices.clear();
                closestIndices.add(i);
            } else if (distance == minDistance) {
                closestIndices.add(i);
            }
        }
        
        // If there are multiple closest clusters, choose randomly
        if (closestIndices.size() > 1) {
            int randomIndex = new java.util.Random().nextInt(closestIndices.size());
            return closestIndices.get(randomIndex);
        }
        
        return closestIndices.get(0);
    }

    private static double euclideanDistance(double[] v1, double[] v2) {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException("Vectors must have the same dim");
        }
        
        double sumSquared = 0.0;
        for (int i = 0; i < v1.length; i++) {
            double diff = v1[i] - v2[i];
            sumSquared += diff * diff;
        }
        return Math.sqrt(sumSquared);
    }

    public static void main(String[] args) throws FileNotFoundException {
        // Example usage
        double[] vector = {1.0, 2.0};
        double[][] centroids = {
            {0.0, 0.0},  // centroid 0
            {2.0, 2.0},  // centroid 1
            {-1.0, 3.0}  // centroid 2
        };
        
        System.out.println("\nTesting findClosestCluster function:");
        System.out.println("Vector: [1.0, 2.0]");
        System.out.println("Centroids:");
        for (int i = 0; i < centroids.length; i++) {
            System.out.printf("Centroid %d: [%.1f, %.1f]%n", i, centroids[i][0], centroids[i][1]);
        }
        
        int closestCluster = findClosestCluster(vector, centroids);
        System.out.println("Closest cluster: " + closestCluster);
        
        // Test case with equidistant centroids
        double[] testVector = {1.0, 1.0};
        double[][] equidistantCentroids = {
            {0.0, 0.0},  // distance = sqrt(2)
            {2.0, 2.0}   // distance = sqrt(2)
        };
        System.out.println("\nTesting with equidistant centroids:");
        System.out.println("Vector: [1.0, 1.0]");
        closestCluster = findClosestCluster(testVector, equidistantCentroids);
        System.out.println("Random choice between equidistant clusters: " + closestCluster);

        //MPP
        // 1) Load Iris features only
        double[][] data = loadIris("src/iris.csv");

        // 2) Fit KMeans
        int k = 3, maxIter = 100;
        KMeans kmeans = new KMeans(k, maxIter);
        kmeans.fit(data);

        // 3) Predict
        int[] labels = kmeans.predict(data);
        double wcss = EvaluationMetrics.computeWCSS(data, labels, kmeans.getCentroids());
        System.out.printf("WCSS (k=%d): %.3f%n", k, wcss);

        // 4) Write assignments to CSV for plotting
        try (PrintWriter out = new PrintWriter("iris_clusters.csv")) {
            out.println("f1,f2,f3,f4,cluster");
            for (int i = 0; i < data.length; i++) {
                out.printf(
                        "%.3f,%.3f,%.3f,%.3f,%d%n",
                        data[i][0], data[i][1], data[i][2], data[i][3], labels[i]
                );
            }
        }
        System.out.println("Wrote iris_clusters.csv for plotting.");
    }

    private static double[][] loadIris(String path) {
        List<double[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank() || line.endsWith("Iris-setosa") ||
                        line.endsWith("Iris-versicolor") || line.endsWith("Iris-virginica")) {
                    // drop the class label
                    String[] parts = line.split(",");
                    double[] vals = new double[4];
                    for (int i = 0; i < 4; i++) {
                        vals[i] = Double.parseDouble(parts[i]);
                    }
                    rows.add(vals);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rows.toArray(new double[0][]);
    }
}