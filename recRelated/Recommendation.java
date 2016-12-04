package recRelated;

import java.util.Comparator;

public class Recommendation {

	String recommendedGene;
	Double score;
	
	
	
	public Recommendation(String recommendedGene, Double score) {
		super();
		this.recommendedGene = recommendedGene;
		this.score = score;
	}

	public String getRecommendedGene() {
		return recommendedGene;
	}



	public void setRecommendedGene(String recommendedGene) {
		this.recommendedGene = recommendedGene;
	}



	public Double getScore() {
		return score;
	}



	public void setScore(Double score) {
		this.score = score;
	}



	public static Comparator<Recommendation> ScoreComparator = new Comparator<Recommendation>() {

		public int compare(Recommendation o1, Recommendation o2) {
			return o2.getScore().compareTo(o1.getScore());
		}

	};
}
