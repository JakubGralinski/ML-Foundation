package CCS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PredictionUI extends JFrame {
    private JTextField featuresField;
    private JLabel resultLabel;
    private KNearestNeighbours knnClassifier;

    public PredictionUI(KNearestNeighbours knn) {
        this.knnClassifier = knn;
        setTitle("New Observation Prediction");
        setSize(500, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Create a panel for the input label and text field in the center.
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Enter features (comma separated):"));
        featuresField = new JTextField(25);
        inputPanel.add(featuresField);
        add(inputPanel, BorderLayout.CENTER);

        JButton predictButton = new JButton("Predict");
        predictButton.addActionListener(e -> {
            String input = featuresField.getText();
            String[] tokens = input.split(",");
            double[] features = new double[tokens.length];
            try {
                for (int i = 0; i < tokens.length; i++) {
                    features[i] = Double.parseDouble(tokens[i].trim());
                }
                String prediction = knnClassifier.predict(features);
                resultLabel.setText("Predicted Class: " + prediction);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Invalid input!");
            }
        });
        add(predictButton, BorderLayout.EAST);

        resultLabel = new JLabel(" ");
        add(resultLabel, BorderLayout.SOUTH);
    }
}