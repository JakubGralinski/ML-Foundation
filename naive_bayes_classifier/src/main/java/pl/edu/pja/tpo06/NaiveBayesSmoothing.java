package pl.edu.pja.tpo06;

public class NaiveBayesSmoothing {

    /**
     * Applies Laplace smoothing.
     */
    public static double simpleSmoothing(int numerator, int denominator, int classes) {
        return (double)(numerator + 1) / (denominator + classes);
    }

    public static void main(String[] args) {

        // Ex 1
        int numerator1 = 0;
        int denominator1 = 5;
        int classes1 = 3;
        double smoothedProb1 = simpleSmoothing(numerator1, denominator1, classes1);
        System.out.println("\nEx 1:");
        System.out.println("Numerator = " + numerator1);
        System.out.println("Denominator = " + denominator1);
        System.out.println("Classes = " + classes1);
        System.out.println("Smoothed Probability = " + smoothedProb1);

        // Ex 2
        int numerator2 = 2;
        int denominator2 = 10;
        int classes2 = 4;
        double smoothedProb2 = simpleSmoothing(numerator2, denominator2, classes2);
        System.out.println("\nEx 2:");
        System.out.println("Numerator = " + numerator2);
        System.out.println("Denominator = " + denominator2);
        System.out.println("Classes = " + classes2);
        System.out.println("Smoothed Probability = " + smoothedProb2);
    }
}