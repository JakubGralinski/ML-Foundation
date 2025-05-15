package CCS;

public class VectorOperations {

    // -- Methods for double[] vectors --

    /**
     * Adds two vectors element-wise.
     */
    public static double[] add(double[] v1, double[] v2) {
        if (v1.length != v2.length)
            throw new IllegalArgumentException("Vectors must have the same length for addition.");
        double[] result = new double[v1.length];
        for (int i = 0; i < v1.length; i++) {
            result[i] = v1[i] + v2[i];
        }
        return result;
    }

    /**
     * Multiplies a vector by a scalar.
     */
    public static double[] multiply(double[] v, double scalar) {
        double[] result = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            result[i] = v[i] * scalar;
        }
        return result;
    }

    /**
     * Computes the Euclidean norm of a vector.
     */
    public static double euclideanNorm(double[] v) {
        double sum = 0;
        for (double value : v) {
            sum += value * value;
        }
        return Math.sqrt(sum);
    }

    /**
     * Computes the Euclidean distance between two vectors.
     */
    public static double euclideanDistance(double[] v1, double[] v2) {
        if (v1.length != v2.length)
            throw new IllegalArgumentException("Vectors must have the same length for distance calculation.");
        double sum = 0;
        for (int i = 0; i < v1.length; i++) {
            double diff = v1[i] - v2[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    /**
     * Computes the dot product of two vectors.
     */
    public static double dotProduct(double[] v1, double[] v2) {
        if (v1.length != v2.length)
            throw new IllegalArgumentException("Vectors must have the same length for dot product.");
        double sum = 0;
        for (int i = 0; i < v1.length; i++) {
            sum += v1[i] * v2[i];
        }
        return sum;
    }

    // -- Overloaded methods for Matrix inputs --
    // These methods convert a Matrix into a flattened double[] vector (row-major order)
    // and then perform the vector operations.

    private static double[] flatten(Matrix m) {
        return m.toVector();
    }

    public static double[] add(Matrix m1, Matrix m2) {
        if (m1.getRows() != m2.getRows() || m1.getCols() != m2.getCols())
            throw new IllegalArgumentException("Matrices must have the same dimensions for addition.");
        return add(flatten(m1), flatten(m2));
    }

    public static double[] multiply(Matrix m, double scalar) {
        return multiply(flatten(m), scalar);
    }

    public static double euclideanNorm(Matrix m) {
        return euclideanNorm(flatten(m));
    }

    public static double euclideanDistance(Matrix m1, Matrix m2) {
        if (m1.getRows() != m2.getRows() || m1.getCols() != m2.getCols())
            throw new IllegalArgumentException("Matrices must have the same dimensions for distance calculation.");
        return euclideanDistance(flatten(m1), flatten(m2));
    }

    public static double dotProduct(Matrix m1, Matrix m2) {
        if (m1.getRows() != m2.getRows() || m1.getCols() != m2.getCols())
            throw new IllegalArgumentException("Matrices must have the same dimensions for dot product.");
        return dotProduct(flatten(m1), flatten(m2));
    }
}