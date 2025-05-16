package pl.edu.pja.tpo02;

import java.util.List;

public class DataSet {
    public double[][] features;
    public int[] labels;

    public DataSet(double[][] features, int[] labels) {
        this.features = features;
        this.labels = labels;
    }
}
