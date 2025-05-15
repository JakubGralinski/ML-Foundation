public class EvaluationMetrics {
    /**
     * Computes Within-Cluster Sum of Squares:
     * sum over all points of (distance to its centroid)^2
     */
    public static double computeWCSS(double[][] data, int[] labels, double[][] centroids) {
        double total = 0.0;
        for (int i = 0; i < data.length; i++) {
            int lbl = labels[i];
            double dist = 0;
            for (int j = 0; j < data[i].length; j++) {
                double d = data[i][j] - centroids[lbl][j];
                dist += d * d;
            }
            total += dist;
        }
        return total;
    }
}