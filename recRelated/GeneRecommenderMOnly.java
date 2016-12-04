package recRelated;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import recRelated.Similarity.SimType;

/**
 * Similar to GeneRecommender, but recommends neighbors 
 * (Not historical information collected from neighbors)
 *
 */
public class GeneRecommenderMOnly extends GeneRecommender {

	public GeneRecommenderMOnly(String inputPath, String outputPath) {
		super(inputPath, outputPath);
		// TODO Auto-generated constructor stub
	}

	/** 
	 * Instead of using past preferences of neighbors (historical information)
	 * directly suggest neighbors!!
	 * 
	 * NOTE: Only difference from GeneRecommender is how we combine the recommendations!!
	 */	
	protected HashMap<String, ArrayList<recRelated.Rating>> combineRecommendations(
			String targetGene, ArrayList<String> similarGenes, 
			HashMap<String, ArrayList<recRelated.Rating>> connScores) {
		// get recommender and recommended genes 
		// recommended --> recommender + vals
		HashMap<String, ArrayList<Rating>> allRecommendedGenes = 
				new HashMap<String, ArrayList<Rating>>();
		
		// NOTE: only difference is that the outer loop in 
		//			GeneRecommender.combineRecommendations(..) has been removed, 
		// 		and rating List of target is used
		ArrayList<recRelated.Rating> ratingList = connScores.get(targetGene);
		for(Rating rating:ratingList){
			String recommendedName = rating.getRecommendedGeneName();
			
			// insert into allRecommendedGenes
			ArrayList<recRelated.Rating> retRating = allRecommendedGenes.get(recommendedName);
			if(retRating==null){
				// no such entry 
				retRating = new ArrayList<Rating>();
				retRating.add(rating);
				allRecommendedGenes.put(recommendedName, retRating);
			} else {
				// update the entry
				retRating.add(rating);
				allRecommendedGenes.put(recommendedName, retRating);
			}
		}
		
		return allRecommendedGenes;
	}


}
