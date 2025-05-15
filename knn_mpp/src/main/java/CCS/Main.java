package CCS;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {

    // 1d Sobol Generator (Van Der Korput Generator)
    // quasi-random k values in the range [1, max] using a van der Corput sequence in base 2.
    /*public static int[] generateSobolKValues(int count, int max) {
        int[] kValues = new int[count];
        for (int i = 1; i <= count; i++) {
            double vdc = vanDerCorput(i, 2);
            int k = (int)(vdc * max) + 1; // scale to [1, max]
            kValues[i - 1] = k;
        }
        return kValues;
    }*/

    // van der Corput sequence generator in a given base.
    /*public static double vanDerCorput(int n, int base) {
        double vdc = 0.0;
        double denom = 1.0;
        while (n > 0) {
            denom *= base;
            int remainder = n % base;
            vdc += remainder / denom;
            n /= base;
        }
        return vdc;
    }*/

    public static void main(String[] args) {
        String irisPath = "src/main/resources/IRIS.csv";
        List<Observation> dataset = PrepareDataset.readCSV(irisPath);

        PrepareDataset.DatasetSplit split = PrepareDataset.trainTestSplit(dataset);
        List<Observation> train = split.train;
        List<Observation> test = split.test;

        int[] simpleKValues = new int[50];
        for (int i = 0; i < 50; i++) {
            simpleKValues[i] = i + 1;
        }

        class KAccPair {
            int k;
            double acc;
            KAccPair(int k, double acc) {
                this.k = k;
                this.acc = acc;
            }
        }
        List<KAccPair> pairs = new ArrayList<>();

        for (int k : simpleKValues) {
            KNearestNeighbours knn = new KNearestNeighbours(k, train);

            List<String> predicted = new ArrayList<>();
            List<String> real = new ArrayList<>();
            for (Observation obs : test) {
                predicted.add(knn.predict(obs.getFeatures()));
                real.add(obs.getLabel());
            }
            double accuracy = EvaluationMetrics.measureAccuracy(real, predicted);
            pairs.add(new KAccPair(k, accuracy));
            System.out.printf("k=%d => accuracy=%.3f%n", k, accuracy);
        }

        // Sort pairs by asc k.
        pairs.sort(Comparator.comparingInt(p -> p.k));

        int[] kValues = new int[pairs.size()];
        double[] accuracies = new double[pairs.size()];
        for (int i = 0; i < pairs.size(); i++) {
            kValues[i] = pairs.get(i).k;
            accuracies[i] = pairs.get(i).acc;
        }

        int bestK = kValues[0];
        double bestAccuracy = accuracies[0];
        for (int i = 0; i < pairs.size(); i++) {
            if (accuracies[i] > bestAccuracy) {
                bestAccuracy = accuracies[i];
                bestK = kValues[i];
            }
        }
        System.out.printf("%nBest k found is %d with accuracy %.3f%n", bestK, bestAccuracy);

        // Write results to CSV.
        try (FileWriter writer = new FileWriter("knn_results.csv")) {
            writer.write("k,accuracy\n");
            for (KAccPair pair : pairs) {
                writer.write(pair.k + "," + pair.acc + "\n");
            }
            System.out.println("Results written to knn_results.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            AccuracyChartPanel chartPanel = new AccuracyChartPanel(kValues, accuracies);
            JFrame chartFrame = new JFrame("KNN Accuracy Plot");
            chartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            chartFrame.add(chartPanel);
            chartFrame.pack();
            chartFrame.setLocationRelativeTo(null);
            chartFrame.setVisible(true);
        });

        KNearestNeighbours finalKnn = new KNearestNeighbours(bestK, train);
        SwingUtilities.invokeLater(() -> {
            PredictionUI predictionUI = new PredictionUI(finalKnn);
            predictionUI.setVisible(true);
        });
    }
}