package recRelated;


public class Rating {
	String recommenderGeneName;
	String recommendedGeneName; 
	Double ratingScore;
	
	public Rating(String recommenderGeneName, String recommendedGeneName,
			Double ratingScore) {
		super();
		this.recommenderGeneName = recommenderGeneName;
		this.recommendedGeneName = recommendedGeneName;
		this.ratingScore = ratingScore;
	}

	public String getRecommenderGeneName() {
		return recommenderGeneName;
	}

	public void setRecommenderGeneName(String recommenderGeneName) {
		this.recommenderGeneName = recommenderGeneName;
	}

	public String getRecommendedGeneName() {
		return recommendedGeneName;
	}

	public void setRecommendedGeneName(String recommendedGeneName) {
		this.recommendedGeneName = recommendedGeneName;
	}

	public Double getRatingScore() {
		return ratingScore;
	}

	public void setRatingScore(Double ratingScore) {
		this.ratingScore = ratingScore;
	}
	
	
	
}
