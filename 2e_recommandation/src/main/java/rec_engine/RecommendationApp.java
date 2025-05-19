package rec_engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.util.Map;

public class RecommendationApp extends JFrame {
    private final Map<String, Map<String, Double>> userRatings = new HashMap<>();
    JComboBox<String> userComboBox;
    private JTextArea recommendationsArea;
    private JButton recommendButton;
    private JButton refreshButton;
    private JLabel statusLabel;
    private JPanel chartPanel;

    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SECONDARY_COLOR = new Color(255, 87, 34);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);

    public static class Recommendation {
        private final String item;
        private final double score;

        public Recommendation(String item, double score) {
            this.item = item;
            this.score = score;
        }

        public String getItem() {
            return item;
        }

        public double getScore() {
            return score;
        }
    }

    public RecommendationApp() {
        super("Movie Recommendation Engine");
        initializeUI();
        initUserRatings();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        recommendationsArea = new JTextArea();
        recommendationsArea.setEditable(false);
        recommendationsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        recommendationsArea.setLineWrap(true);
        recommendationsArea.setWrapStyleWord(true);
        recommendationsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(recommendationsArea);
        scrollPane.setPreferredSize(new Dimension(400, 400));

        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSimilarityChart(g);
            }
        };
        chartPanel.setPreferredSize(new Dimension(300, 400));
        chartPanel.setBorder(BorderFactory.createTitledBorder("User Similarity Visualization"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, chartPanel);
        splitPane.setResizeWeight(0.6);
        centerPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Recommendation Controls"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Select User:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        userComboBox = new JComboBox<>();
        panel.add(userComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        recommendButton = createStyledButton("Get Recommendations", PRIMARY_COLOR);
        recommendButton.addActionListener(this::handleRecommendation);
        refreshButton = createStyledButton("Refresh Data", SECONDARY_COLOR);
        refreshButton.addActionListener(e -> refreshUserList());
        buttonPanel.add(recommendButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void handleRecommendation(ActionEvent e) {
        String user = (String) userComboBox.getSelectedItem();
        if (user == null || user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a user first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        statusLabel.setText("Calculating recommendations for " + user + "...");
        new SwingWorker<List<Recommendation>, Void>() {
            @Override
            protected List<Recommendation> doInBackground() {
                return getRecommendations(user);
            }

            @Override
            protected void done() {
                try {
                    List<Recommendation> recs = get();
                    displayRecommendations(user, recs);
                    chartPanel.repaint();
                    statusLabel.setText("Recommendations ready for " + user);
                } catch (Exception ex) {
                    statusLabel.setText("Error generating recommendations");
                    JOptionPane.showMessageDialog(RecommendationApp.this,
                            "Error generating recommendations: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void initUserRatings() {
        Map<String, Double> aliceRatings = new HashMap<>();
        aliceRatings.put("Star Wars", 5.0);
        aliceRatings.put("Harry Potter", 3.0);
        aliceRatings.put("Lord of the Rings", 4.0);
        aliceRatings.put("Frozen", 1.0);
        aliceRatings.put("Inception", 5.0);
        userRatings.put("Alice", aliceRatings);

        Map<String, Double> bobRatings = new HashMap<>();
        bobRatings.put("Star Wars", 4.0);
        bobRatings.put("Harry Potter", 2.0);
        bobRatings.put("Lord of the Rings", 5.0);
        bobRatings.put("Frozen", 1.0);
        bobRatings.put("Inception", 4.0);
        userRatings.put("Bob", bobRatings);

        Map<String, Double> charlieRatings = new HashMap<>();
        charlieRatings.put("Star Wars", 1.0);
        charlieRatings.put("Harry Potter", 5.0);
        charlieRatings.put("Lord of the Rings", 1.0);
        charlieRatings.put("Frozen", 5.0);
        charlieRatings.put("Inception", 2.0);
        userRatings.put("Charlie", charlieRatings);

        Map<String, Double> davidRatings = new HashMap<>();
        davidRatings.put("Star Wars", 2.0);
        davidRatings.put("Harry Potter", 5.0);
        davidRatings.put("Lord of the Rings", 2.0);
        davidRatings.put("Frozen", 4.0);
        davidRatings.put("Inception", 3.0);
        userRatings.put("David", davidRatings);

        Map<String, Double> eveRatings = new HashMap<>();
        eveRatings.put("Star Wars", 5.0);
        eveRatings.put("Harry Potter", 1.0);
        eveRatings.put("Lord of the Rings", 4.0);
        eveRatings.put("Inception", 4.0);
        eveRatings.put("Avengers", 5.0);
        userRatings.put("Eve", eveRatings);

        refreshUserList();
    }

    public double pearsonSimilarity(String user1, String user2) {
        Map<String, Double> ratings1 = userRatings.get(user1);
        Map<String, Double> ratings2 = userRatings.get(user2);

        Set<String> sharedItems = new HashSet<>(ratings1.keySet());
        sharedItems.retainAll(ratings2.keySet());

        int n = sharedItems.size();
        if (n < 2) return 0;

        double sum1 = 0, sum2 = 0;
        double sum1Sq = 0, sum2Sq = 0;
        double pSum = 0;

        for (String item : sharedItems) {
            double r1 = ratings1.get(item);
            double r2 = ratings2.get(item);

            sum1 += r1;
            sum2 += r2;
            sum1Sq += r1 * r1;
            sum2Sq += r2 * r2;
            pSum += r1 * r2;
        }

        double numerator = pSum - (sum1 * sum2 / n);
        double denominator = Math.sqrt((sum1Sq - (sum1 * sum1) / n) * (sum2Sq - (sum2 * sum2) / n));

        if (denominator == 0) return 0;
        return numerator / denominator;
    }

    public List<Recommendation> getRecommendations(String user) {
        Map<String, Double> totals = new HashMap<>();
        Map<String, Double> simSums = new HashMap<>();

        for (String other : userRatings.keySet()) {
            if (other.equals(user)) continue;

            double sim = pearsonSimilarity(user, other);
            if (sim <= 0) continue;

            Map<String, Double> userRatingMap = userRatings.get(user);
            Map<String, Double> otherRatingMap = userRatings.get(other);

            for (Map.Entry<String, Double> entry : otherRatingMap.entrySet()) {
                String item = entry.getKey();
                if (!userRatingMap.containsKey(item)) {
                    totals.put(item, totals.getOrDefault(item, 0.0) + entry.getValue() * sim);
                    simSums.put(item, simSums.getOrDefault(item, 0.0) + sim);
                }
            }
        }

        List<Recommendation> recommendations = new ArrayList<>();
        for (String item : totals.keySet()) {
            double simSum = simSums.get(item);
            if (simSum > 0) {
                recommendations.add(new Recommendation(item, totals.get(item) / simSum));
            }
        }

        recommendations.sort(Comparator.comparingDouble(Recommendation::getScore).reversed());
        return recommendations;
    }

    private void refreshUserList() {
        userComboBox.removeAllItems();
        for (String user : userRatings.keySet()) {
            userComboBox.addItem(user);
        }
        statusLabel.setText("User list refreshed");
    }

    private void displayRecommendations(String user, List<Recommendation> recs) {
        if (recs.isEmpty()) {
            recommendationsArea.setText("No recommendations available for " + user + ".");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Top Recommendations for ").append(user).append(":\n\n");
        for (Recommendation r : recs) {
            int stars = (int) Math.round(r.getScore());
            sb.append(String.format("★ %s (score: %.2f) %s%n",
                    r.getItem(),
                    r.getScore(),
                    "★".repeat(Math.max(0, stars))));
        }

        sb.append("\nSimilarity Analysis:\n-------------------\n");
        List<Map.Entry<String, Double>> similarUsers = new ArrayList<>();
        for (String otherUser : userRatings.keySet()) {
            if (!otherUser.equals(user)) {
                similarUsers.add(new AbstractMap.SimpleEntry<>(otherUser, pearsonSimilarity(user, otherUser)));
            }
        }

        similarUsers.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        for (Map.Entry<String, Double> entry : similarUsers) {
            String relation = entry.getValue() > 0 ? "similar" : "dissimilar";
            sb.append(String.format("- %s is %s to you (score: %.2f)%n",
                    entry.getKey(), relation, entry.getValue()));
        }

        recommendationsArea.setText(sb.toString());
    }

    private void drawSimilarityChart(Graphics g) {
        String selectedUser = (String) userComboBox.getSelectedItem();
        if (selectedUser == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = chartPanel.getWidth();
        int height = chartPanel.getHeight();
        int barWidth = 40;
        int spacing = 20;
        int x = 50;
        int maxBarHeight = height - 100;

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2d.drawString("User Similarity with " + selectedUser, width/2 - 100, 20);

        g2d.drawLine(40, height - 50, width - 20, height - 50);
        g2d.drawLine(40, height - 50, 40, 40);

        int i = 0;
        for (String user : userRatings.keySet()) {
            if (!user.equals(selectedUser)) {
                double similarity = pearsonSimilarity(selectedUser, user);
                int barHeight = (int) (Math.abs(similarity) * maxBarHeight);
                int y = height - 50 - barHeight;

                Color barColor = similarity > 0 ? PRIMARY_COLOR : SECONDARY_COLOR;
                g2d.setColor(barColor);
                g2d.fillRect(x, y, barWidth, barHeight);

                g2d.setColor(Color.BLACK);
                g2d.drawString(String.format("%.2f", similarity), x, y - 5);
                g2d.drawString(user, x, height - 30);

                x += barWidth + spacing;
                i++;
            }
        }

        g2d.setColor(PRIMARY_COLOR);
        g2d.fillRect(width - 150, 50, 20, 20);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Positive correlation", width - 120, 65);

        g2d.setColor(SECONDARY_COLOR);
        g2d.fillRect(width - 150, 80, 20, 20);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Negative correlation", width - 120, 95);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RecommendationApp app = new RecommendationApp();
            app.setVisible(true);
        });
    }
}