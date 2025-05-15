package CCS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SizeSelector extends JDialog {
    private static final int MAX_ROWS = 10;
    private static final int MAX_COLS = 10;

    private int selectedRows = 0;
    private int selectedCols = 0;
    private boolean confirmed = false;  // true if user clicked a cell

    private JLabel dimensionLabel;
    private JPanel gridPanel;

    public SizeSelector(Frame parent) {
        // Make this dialog modal so that the main code waits until it is closed.
        super(parent, "Size Selector", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        dimensionLabel = new JLabel("Select matrix size: 0x0");

        // Panel that draws the grid.
        gridPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int cellSize = 30; // Adjust cell size as needed.
                for (int r = 0; r < MAX_ROWS; r++) {
                    for (int c = 0; c < MAX_COLS; c++) {
                        int x = c * cellSize;
                        int y = r * cellSize;
                        // Fill cell if within selected region.
                        if (r < selectedRows && c < selectedCols) {
                            g.setColor(Color.GRAY);
                            g.fillRect(x, y, cellSize, cellSize);
                        }
                        // Draw border.
                        g.setColor(Color.BLACK);
                        g.drawRect(x, y, cellSize, cellSize);
                    }
                }
            }

            @Override
            public Dimension getPreferredSize() {
                int cellSize = 30;
                return new Dimension(MAX_COLS * cellSize, MAX_ROWS * cellSize);
            }
        };

        // Update the selection on mouse movement.
        gridPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int cellSize = 30;
                int col = e.getX() / cellSize;
                int row = e.getY() / cellSize;
                if (row >= 0 && row < MAX_ROWS && col >= 0 && col < MAX_COLS) {
                    selectedRows = row + 1;
                    selectedCols = col + 1;
                    dimensionLabel.setText("Select matrix size: " + selectedRows + "x" + selectedCols);
                    repaint();
                }
            }
        });

        // Mouse click confirms the selection.
        gridPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int cellSize = 30;
                int col = e.getX() / cellSize;
                int row = e.getY() / cellSize;
                if (row >= 0 && row < MAX_ROWS && col >= 0 && col < MAX_COLS) {
                    selectedRows = row + 1;
                    selectedCols = col + 1;
                    dimensionLabel.setText("Selected matrix size: " + selectedRows + "x" + selectedCols);
                    confirmed = true;
                    dispose(); // Close dialog immediately.
                }
            }
        });

        // Close button (to cancel the selection).
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(dimensionLabel);
        topPanel.add(closeButton);

        add(topPanel, BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(parent); // Center on parent.
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getSelectedRows() {
        return selectedRows;
    }

    public int getSelectedCols() {
        return selectedCols;
    }
}