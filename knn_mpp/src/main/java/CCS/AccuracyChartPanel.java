package CCS;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class AccuracyChartPanel extends JPanel {
    private int[] kValues;
    private double[] accuracies;
    private double minAccuracy;
    private double maxAccuracy;
    private static final int MARGIN = 50;
    private static final Color GRID_COLOR = new Color(200, 200, 200);

    private static final int TICK_COUNT = 6;

    public AccuracyChartPanel(int[] kValues, double[] accuracies) {
        this.kValues = kValues.clone();
        this.accuracies = accuracies.clone();

        int minK = Integer.MAX_VALUE;
        int maxK = Integer.MIN_VALUE;
        for (int k : kValues) {
            if (k < minK) minK = k;
            if (k > maxK) maxK = k;
        }

        // by just subtracting 1 from minK if it’s > 1, etc.
        if (minK > 1) minK--;
        maxK++;

        minAccuracy = Double.MAX_VALUE;
        maxAccuracy = Double.MIN_VALUE;
        for (double acc : accuracies) {
            if (acc < minAccuracy) minAccuracy = acc;
            if (acc > maxAccuracy) maxAccuracy = acc;
        }
        if (Math.abs(maxAccuracy - minAccuracy) < 1e-12) {
            maxAccuracy = minAccuracy + 0.01;
        }
        // Add a small buffer around the min/max accuracy
        double range = maxAccuracy - minAccuracy;
        minAccuracy = Math.max(0.0, minAccuracy - 0.05 * range); // keep above 0 if possible
        maxAccuracy += 0.05 * range;

        setPreferredSize(new Dimension(600, 400));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Turn on anti-aliasing for smoother lines
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int width  = getWidth();
        int height = getHeight();

        int originX = MARGIN;
        int originY = height - MARGIN;
        int usableWidth  = width  - 2 * MARGIN;
        int usableHeight = height - 2 * MARGIN;

        // Draw a thin rectangle around the usable area
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRect(originX, originY - usableHeight, usableWidth, usableHeight);

        int actualMinK = Integer.MAX_VALUE;
        int actualMaxK = Integer.MIN_VALUE;
        for (int k : kValues) {
            if (k < actualMinK) actualMinK = k;
            if (k > actualMaxK) actualMaxK = k;
        }
        if (actualMinK == actualMaxK) {
            actualMaxK = actualMinK + 1;
        }

        int plotMinK = Math.max(0, actualMinK - 1);
        int plotMaxK = actualMaxK + 1;

        double xScale = (double) usableWidth / (plotMaxK - plotMinK);

        double yScale = (double) usableHeight / (maxAccuracy - minAccuracy);

        g2.setColor(GRID_COLOR);

        int rangeK = plotMaxK - plotMinK;
        int stepK = Math.max(1, rangeK / (TICK_COUNT - 1));
        // If stepK=0, force it to 1 to avoid infinite loop
        for (int kVal = plotMinK; kVal <= plotMaxK; kVal += stepK) {
            int x = (int) (originX + (kVal - plotMinK) * xScale);
            // vertical line
            g2.drawLine(x, originY, x, originY - usableHeight);

            // draw X label
            String label = String.valueOf(kVal);
            int labelWidth = g2.getFontMetrics().stringWidth(label);
            g2.setColor(Color.BLACK);
            g2.drawString(label, x - labelWidth / 2, originY + 15);
            g2.setColor(GRID_COLOR);
        }

        double rangeAcc = maxAccuracy - minAccuracy;
        double stepAcc = rangeAcc / (TICK_COUNT - 1);
        for (int i = 0; i < TICK_COUNT; i++) {
            double accTick = minAccuracy + i * stepAcc;
            int y = (int) (originY - (accTick - minAccuracy) * yScale);

            g2.drawLine(originX, y, originX + usableWidth, y);

            // Y label
            DecimalFormat df = new DecimalFormat("0.###");
            String label = df.format(accTick);
            int labelWidth = g2.getFontMetrics().stringWidth(label);
            g2.setColor(Color.BLACK);
            g2.drawString(label, originX - labelWidth - 5, y + 5);
            g2.setColor(GRID_COLOR);
        }
        g2.setColor(Color.RED);
        for (int i = 0; i < kValues.length; i++) {
            int kVal = kValues[i];
            double acc = accuracies[i];

            int x = (int) (originX + (kVal - plotMinK) * xScale);
            int y = (int) (originY - (acc - minAccuracy) * yScale);

            // Circle for the data point
            int pointSize = 5;
            g2.fillOval(x - pointSize/2, y - pointSize/2, pointSize, pointSize);

            // Connect lines
            if (i > 0) {
                int prevKVal = kValues[i - 1];
                double prevAcc = accuracies[i - 1];
                int xPrev = (int) (originX + (prevKVal - plotMinK) * xScale);
                int yPrev = (int) (originY - (prevAcc - minAccuracy) * yScale);
                g2.drawLine(xPrev, yPrev, x, y);
            }
        }

        // Axis labels
        g2.setColor(Color.BLACK);
        // X-axis label
        g2.drawString("k", originX + usableWidth + 15, originY + 5);
        // Y-axis label
        g2.drawString("Accuracy", originX - 45, originY - usableHeight - 10);
    }
}