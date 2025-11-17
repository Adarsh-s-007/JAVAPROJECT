import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ResultAnalyzer extends JFrame {

    private JTextField[] subjectNameFields;
    private JTextField[] markFields;
    private JTextField[] creditFields;
    private JLabel resultLabel;
    private BarGraphPanel graphPanel;
    private int numSubjects;

    public ResultAnalyzer() {
        
        String input = JOptionPane.showInputDialog(null, "Enter Number of Subjects:", "Setup", JOptionPane.QUESTION_MESSAGE);
        try {
            if (input == null) System.exit(0); 
            numSubjects = Integer.parseInt(input);
            if (numSubjects <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid number. Defaulting to 5.");
            numSubjects = 5;
        }

        setTitle("Student Result Analyzer - JNTUH");
        setSize(850, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Color.WHITE);

        
        File imageFile = new File("images.png");
        if (!imageFile.exists()) {
            JOptionPane.showMessageDialog(this,
                    "Logo 'images.png' was not found!\n\nPlease paste the image in this folder:\n" + imageFile.getAbsolutePath(),
                    "Logo Missing",
                    JOptionPane.WARNING_MESSAGE);
        }

        try {
            BufferedImage logoImage = ImageIO.read(imageFile);
            Image scaledImage = logoImage.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
            logoPanel.add(logoLabel);
        } catch (IOException e) {
            JLabel errorLabel = new JLabel("<html><center>[Logo Missing]<br>Restart app after adding images.png</center></html>");
            errorLabel.setForeground(Color.RED);
            logoPanel.add(errorLabel);
        }
        add(logoPanel, BorderLayout.NORTH);

        
        JPanel inputsContainer = new JPanel(new BorderLayout());

      
        JPanel headerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        headerPanel.add(new JLabel("Subject Name", SwingConstants.CENTER));
        headerPanel.add(new JLabel("Marks (0-100)", SwingConstants.CENTER));
        headerPanel.add(new JLabel("Credits", SwingConstants.CENTER));
        inputsContainer.add(headerPanel, BorderLayout.NORTH);

        
        JPanel gridPanel = new JPanel(new GridLayout(numSubjects, 3, 10, 10));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        subjectNameFields = new JTextField[numSubjects];
        markFields = new JTextField[numSubjects];
        creditFields = new JTextField[numSubjects];

        for (int i = 0; i < numSubjects; i++) {
            
            subjectNameFields[i] = new JTextField("Subject " + (i + 1));
            subjectNameFields[i].setHorizontalAlignment(JTextField.CENTER);
            gridPanel.add(subjectNameFields[i]);

            
            markFields[i] = new JTextField();
            markFields[i].setHorizontalAlignment(JTextField.CENTER);
            gridPanel.add(markFields[i]);

            
            creditFields[i] = new JTextField("3");
            creditFields[i].setHorizontalAlignment(JTextField.CENTER);
            gridPanel.add(creditFields[i]);
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        inputsContainer.add(scrollPane, BorderLayout.CENTER);

        
        JButton calculateButton = new JButton("Calculate CGPA");
        calculateButton.setBackground(new Color(70, 130, 180));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFont(new Font("Arial", Font.BOLD, 14));
        calculateButton.addActionListener(new CalculateAction());
        JPanel btnPanel = new JPanel();
        btnPanel.add(calculateButton);
        inputsContainer.add(btnPanel, BorderLayout.SOUTH);

        add(inputsContainer, BorderLayout.CENTER);

        
        JPanel bottomPanel = new JPanel(new BorderLayout());

        graphPanel = new BarGraphPanel();
        graphPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Performance Graph (Grade Points)",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), Color.DARK_GRAY));
        graphPanel.setBackground(Color.WHITE);

        resultLabel = new JLabel("Enter details and click Calculate", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        bottomPanel.add(resultLabel, BorderLayout.NORTH);
        bottomPanel.add(graphPanel, BorderLayout.CENTER);
        bottomPanel.setPreferredSize(new Dimension(700, 300));

        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private int getGradePoint(int marks) {
        if (marks >= 90) return 10;
        if (marks >= 80) return 9;
        if (marks >= 70) return 8;
        if (marks >= 60) return 7;
        if (marks >= 50) return 6;
        if (marks >= 40) return 5;
        if (marks >= 35) return 4;
        return 0;
    }

    private String getOverallGradeLetter(double cgpa) {
        if (cgpa >= 9.0) return "O";
        if (cgpa >= 8.0) return "A+";
        if (cgpa >= 7.0) return "A";
        if (cgpa >= 6.0) return "B+";
        if (cgpa >= 5.0) return "B";
        if (cgpa >= 4.0) return "C";
        return "P";
    }

    private class CalculateAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] marks = new int[numSubjects];
            String[] names = new String[numSubjects];
            double totalWeightedPoints = 0;
            double totalCredits = 0;
            boolean isFail = false;

            try {
                for (int i = 0; i < numSubjects; i++) {
                    names[i] = subjectNameFields[i].getText().trim();
                    if(names[i].isEmpty()) names[i] = "S" + (i+1);

                    String mText = markFields[i].getText().trim();
                    if(mText.isEmpty()) mText = "0";
                    int m = Integer.parseInt(mText);

                    String cText = creditFields[i].getText().trim();
                    if(cText.isEmpty()) cText = "0";
                    double c = Double.parseDouble(cText);

                    if (m < 0 || m > 100) {
                        JOptionPane.showMessageDialog(null, "Marks must be 0-100 for " + names[i]);
                        return;
                    }
                    if (c < 0) {
                        JOptionPane.showMessageDialog(null, "Credits cannot be negative.");
                        return;
                    }

                    marks[i] = m;

                    if (m < 35) isFail = true;

                    int gp = getGradePoint(m);
                    totalWeightedPoints += (gp * c);
                    totalCredits += c;
                }

                graphPanel.setData(marks, names);

                if (isFail) {
                    resultLabel.setText("Result: FAIL | CGPA: 0.0 (Backlog)");
                    resultLabel.setForeground(Color.RED);
                } else {
                    double cgpa = 0.0;
                    if (totalCredits > 0) cgpa = totalWeightedPoints / totalCredits;

                    String overallGrade = getOverallGradeLetter(cgpa);

                    resultLabel.setText(String.format("Result: PASS | Credits: %.1f | CGPA: %.2f | Grade: %s",
                            totalCredits, cgpa, overallGrade));
                    resultLabel.setForeground(new Color(0, 100, 0));
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid numbers for Marks and Credits.");
            }
        }
    }

    class BarGraphPanel extends JPanel {
        private int[] marks;
        private String[] names;

        public void setData(int[] marks, String[] names) {
            this.marks = marks;
            this.names = names;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (marks == null || marks.length == 0) return;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int barGap = 20;
            int maxBarWidth = 80;

            int calculatedBarWidth = (width - (marks.length + 1) * barGap) / marks.length;
            int barWidth = Math.min(calculatedBarWidth, maxBarWidth);

            int totalContentWidth = (marks.length * barWidth) + ((marks.length - 1) * barGap);
            int startX = (width - totalContentWidth) / 2;
            if (startX < 10) startX = 10;

            int xAxisY = height - 40;

            g2d.setColor(Color.DARK_GRAY);
            g2d.drawLine(10, xAxisY, width - 10, xAxisY);

            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= 10; i += 2) {
                int yPos = xAxisY - (int)((i / 10.0) * (height - 60));
                g2d.drawString(String.valueOf(i), 5, yPos + 4);
            }

            for (int i = 0; i < marks.length; i++) {
                int gp = getGradePoint(marks[i]);
                int barHeight = (int) ((gp / 10.0) * (height - 60));
                int x = startX + (i * (barWidth + barGap));
                int y = xAxisY - barHeight;

                if (marks[i] < 35) g2d.setColor(new Color(255, 100, 100));
                else if (gp >= 9) g2d.setColor(new Color(0, 150, 0));
                else g2d.setColor(new Color(100, 149, 237));

                g2d.fillRect(x, y, barWidth, barHeight);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(x, y, barWidth, barHeight);

                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                String gpStr = String.valueOf(gp);
                int sW = g2d.getFontMetrics().stringWidth(gpStr);
                g2d.drawString(gpStr, x + (barWidth/2) - (sW/2), y - 5);

                g2d.setFont(new Font("Arial", Font.PLAIN, 11));
                String subName = names[i];
                if (subName.length() > 10) subName = subName.substring(0, 8) + "..";

                int subNameW = g2d.getFontMetrics().stringWidth(subName);
                g2d.drawString(subName, x + (barWidth / 2) - (subNameW / 2), xAxisY + 15);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ResultAnalyzer::new);
    }

}
