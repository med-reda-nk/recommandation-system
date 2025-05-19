package recommender;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.recommender.*;

import java.io.File;
import java.util.List;

public class RecommenderExample {
    public static void main(String[] args) throws Exception {
        DataModel model = new FileDataModel(new File("data/ratings.csv"));

        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(3, similarity, model);

        Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        List<RecommendedItem> recommendations = recommender.recommend(101, 5);

        for (RecommendedItem recommendation : recommendations) {
            System.out.println("Item: " + recommendation.getItemID() + " , Score: " + recommendation.getValue());
        }
    }
}