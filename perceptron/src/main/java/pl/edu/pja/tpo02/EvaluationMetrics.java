package pl.edu.pja.tpo02;


public class EvaluationMetrics {

    /**
     * Returns the proportion of correctly classified observations.
     */
    public static double measureAccuracy(int[] realClasses, int[] predictedClasses) {
        if (realClasses.length != predictedClasses.length) {
            throw new IllegalArgumentException("Lists must be of same size");
        }
        int correct = 0;
        for (int i = 0; i < realClasses.length; i++) {
            if (realClasses[i] == predictedClasses[i]) {
                correct++;
            }
        }
        return (double) correct / realClasses.length;
    }
}