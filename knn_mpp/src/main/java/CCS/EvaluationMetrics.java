package CCS;

import java.util.List;

public class EvaluationMetrics {

    /**
     * Returns the proportion of correctly classified observations.
     */
    public static double measureAccuracy(List<String> realClasses, List<String> predictedClasses) {
        if (realClasses.size() != predictedClasses.size()) {
            throw new IllegalArgumentException("Lists must be of same size");
        }
        int correct = 0;
        for (int i = 0; i < realClasses.size(); i++) {
            if (realClasses.get(i).equals(predictedClasses.get(i))) {
                correct++;
            }
        }
        return (double) correct / realClasses.size();
    }
}