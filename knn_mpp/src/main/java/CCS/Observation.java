package CCS;

public class Observation {
    private double[] features;
    private String label;

    public Observation(double[] features, String label) {
        this.features = features;
        this.label = label;
    }

    public double[] getFeatures() {
        return features;
    }

    public String getLabel() {
        return label;
    }
}