package rec_engine;

import javax.swing.*;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        // Test instance methods
        RecommendationApp app = new RecommendationApp();
        testPearsonSimilarity(app);
        testGetRecommendations(app);
        testUIComponents(app);
    }

    public static void testPearsonSimilarity(RecommendationApp app) {
        System.out.println("=== Testing Pearson Similarity ===");

        double sim = app.pearsonSimilarity("Alice", "Bob");
        System.out.printf("Alice-Bob similarity: %.3f%n", sim);

        sim = app.pearsonSimilarity("Alice", "Charlie");
        System.out.printf("Alice-Charlie similarity: %.3f (should be negative)%n", sim);

        sim = app.pearsonSimilarity("Alice", "David");
        System.out.printf("Alice-David similarity: %.3f%n", sim);
    }

    public static void testGetRecommendations(RecommendationApp app) {
        System.out.println("\n=== Testing Recommendations ===");

        System.out.println("Recommendations for Alice:");
        List<RecommendationApp.Recommendation> recs = app.getRecommendations("Alice");
        printRecommendations(recs);

        System.out.println("\nRecommendations for Charlie:");
        recs = app.getRecommendations("Charlie");
        printRecommendations(recs);
    }

    private static void printRecommendations(List<RecommendationApp.Recommendation> recs) {
        if (recs.isEmpty()) {
            System.out.println("No recommendations available");
            return;
        }
        for (RecommendationApp.Recommendation r : recs) {
            System.out.printf("- %s (score: %.2f)%n", r.getItem(), r.getScore());
        }
    }

    public static void testUIComponents(RecommendationApp app) {
        System.out.println("\n=== Testing UI Components ===");
        System.out.println("Users in dropdown: " + app.userComboBox.getItemCount());

        SwingUtilities.invokeLater(() -> {
            app.setVisible(true);
            System.out.println("UI should now be visible for visual inspection");
        });
    }
}