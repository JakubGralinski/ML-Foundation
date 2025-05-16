package pl.edu.pja.tpo03;

public class TextVectorizer {
    /**
     * Converts the input text into a 26-dimensional vector representing letter frequencies.
     * The text is converted to lowercase, non-letter characters are removed,
     * and each frequency is divided by the total number of letters.
     * @param text The input text.
     * @return A double array of length 26 with letter probabilities.
     */
    public static double[] convert(String text) {
        // Convert to lowercase and remove all non-letter characters.
        text = text.toLowerCase().replaceAll("[^a-z]", "");
        double[] freq = new double[26];
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int idx = c - 'a';
            if (idx >= 0 && idx < 26) {
                freq[idx]++;
            }
        }
        // Normalize frequencies to get probabilities.
        double total = text.length();
        if (total > 0) {
            for (int i = 0; i < 26; i++) {
                freq[i] /= total;
            }
        }
        return freq;
    }
}