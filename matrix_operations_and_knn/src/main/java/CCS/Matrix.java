package CCS;

import java.util.Arrays;

public class Matrix {
    private double[][] data;
    private int rows, cols;

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
    }

    public Matrix(double[][] matrix) {
        this.rows = matrix.length;
        this.cols = (rows > 0) ? matrix[0].length : 0;
        this.data = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(matrix[i], 0, this.data[i], 0, matrix[i].length);
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public double get(int i, int j) {
        return data[i][j];
    }

    public void set(int i, int j, double value) {
        data[i][j] = value;
    }

    public Matrix add(Matrix other) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new IllegalArgumentException("Invalid size for addition");
        }
        Matrix result = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.data[i][j] = this.data[i][j] + other.data[i][j];
            }
        }
        return result;
    }

    public Matrix subtract(Matrix other) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new IllegalArgumentException("Invalid size for subtraction");
        }
        Matrix result = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.data[i][j] = this.data[i][j] - other.data[i][j];
            }
        }
        return result;
    }

    public Matrix multiply(Matrix other) {
        if (this.cols != other.rows) {
            throw new IllegalArgumentException("Invalid size for multiplication");
        }
        Matrix result = new Matrix(this.rows, other.cols);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                double sum = 0;
                for (int k = 0; k < this.cols; k++) {
                    sum += this.data[i][k] * other.data[k][j];
                }
                result.data[i][j] = sum;
            }
        }
        return result;
    }

    public Matrix transpose() {
        Matrix result = new Matrix(cols, rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.data[j][i] = data[i][j];
            }
        }
        return result;
    }

    public Matrix gaussianElimination() {
        Matrix result = new Matrix(this.data);
        int lead = 0;
        for (int i = 0; i < result.rows; i++) {
            if (lead >= result.cols) break;
            int rowCurr = i;
            while (rowCurr < result.rows && Math.abs(result.data[rowCurr][lead]) < 1e-10) {
                rowCurr++;
            }
            if (rowCurr == result.rows) {
                lead++;
                continue;
            }
            double[] temp = result.data[rowCurr];
            result.data[rowCurr] = result.data[i];
            result.data[i] = temp;

            double val = result.data[i][lead];
            for (int j = 0; j < result.cols; j++) {
                result.data[i][j] /= val;
            }

            for (int k = 0; k < result.rows; k++) {
                if (k != i) {
                    double factor = result.data[k][lead];
                    for (int j = 0; j < result.cols; j++) {
                        result.data[k][j] -= result.data[i][j] * factor;
                    }
                }
            }
            lead++;
        }
        return result;
    }

    public void display() {
        for (double[] row : data) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println();
    }

    // Helper method to flatten the matrix data in row-major order.
    public double[] toVector() {
        double[] vector = new double[rows * cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, vector, i * cols, cols);
        }
        return vector;
    }

    // Override toString() to display the flattened vector.
    @Override
    public String toString() {
        return Arrays.toString(this.toVector());
    }
}