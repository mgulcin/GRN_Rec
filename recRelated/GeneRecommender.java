package recRelated;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import recRelated.GeneRecommenderParameters.MOType;
import recRelated.GeneRecommenderParameters.OutlistType;
import recRelated.Similarity.SimType;

public class GeneRecommender {

	//parameters used in calculations
	protected String inputPath;
	protected String folderName;
	
	
	protected GeneRecommenderParameters parameters;

	// binding probabilities of genes (e.g. from Lee's data): (recommender-->recommended+vals)
	protected HashMap<String, ArrayList<Rating>> targetSimilarityBasedMap; 

	// methods
	public GeneRecommender(String inputPath, String outputPath,
			GeneRecommenderParameters parameters) {
		super();
		this.inputPath = inputPath;
		this.folderName ="";
		this.parameters = parameters;
		targetSimilarityBasedMap= null;
	}

	public GeneRecommender(String inputPath, String outputPath) {
		super();
		this.inputPath = inputPath;
		this.folderName ="";
		this.parameters = null;
		targetSimilarityBasedMap= null;
	}
	
	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public GeneRecommenderParameters getParameters() {
		return parameters;
	}


	public void setParameters(GeneRecommenderParameters parameters) {
		this.parameters = parameters;
	}


	// 
	public ArrayList<Recommendation> recommend(Connection con, String targetGene, ArrayList<SimType> simFieldList )
			throws IOException {
		// find item-recScore by collobarative filtering

		// 1) Find k-many similar genes 
		ArrayList<String> similarGenes = getMostSimilarGenes(con, targetGene, 
				parameters.getNumberOfSimilarGenes(), simFieldList);

		/*// 2) Print most similar genes to each target gene
		String simGenesOutPath = outputPath + "simGenes_"
				+ prefferedMoType.toString() + ".csv";
		printer.printMostSimilarGenes(simGenesOutPath, targetGene,similarGenes);*/

		// 3) Find recommendations - sorted by score!!
		PriorityQueue<Recommendation> rec = findRecommendations(con, targetGene, 
				similarGenes);

		// 4) Return best k recommendation as a result
		ArrayList<Recommendation> resultRecs = getBestKRecommendations(targetGene, 
				rec, parameters.getOutputListSize());


		// return 
		return resultRecs;

	}


	protected ArrayList<Recommendation> getBestKRecommendations(
			String targetGene, PriorityQueue<Recommendation> rec,
			Integer outputListSize) {
		ArrayList<Recommendation> resultMap = null;
		switch(parameters.getOutlistType()){
		case FIXEDLENGTH:
		{
			// Return best k recommendation as a result
			resultMap = new ArrayList<Recommendation>();
			while(resultMap.size() < outputListSize){
				Recommendation r = rec.poll();

				if(r!=null){
					resultMap.add(r);	
				} else {
					// no element left in the queue
					break;
				}

			}
		}
		break;
		case THRESHOLDBASED:
		{
			resultMap = new ArrayList<Recommendation>();
			while(rec.size() > 0){
				Recommendation r = rec.poll();

				if(r!=null){
					if(r.getScore() > 0){
						resultMap.add(r);	
					}
				} else {
					// no element left in the queue
					break;
				}
			}
		}
		break;
		default: break;
		}

		return resultMap;
	}

	protected PriorityQueue<Recommendation> findRecommendations(Connection con,
			String targetGene, ArrayList<String> similarGenes) throws IOException {
		// 0) Read connection prob of genes to each other (recommender-->recommended+vals)
		if(targetSimilarityBasedMap == null){
			targetSimilarityBasedMap = getRatings(parameters.getChosenTargetSim());
		}

		// 1) Get ratings from similar genes (rating, genesWhoRecommendedThis )
		// for each recommended gene, create a list containing info of prob & recommender
		// recommended --> recommender + vals
		HashMap<String, ArrayList<Rating>> allRecommendedGenes= 
				combineRecommendations(targetGene, similarGenes, targetSimilarityBasedMap);

		// 2) Calculate recommendation score for each gene
		// // recommended --> rating
		HashMap<String, Double> geneRecMap = 
				calculateGeneScores(targetGene, allRecommendedGenes);

		// 3) find genes  & sort acc score
		PriorityQueue<Recommendation> rec = createRecsFromGenes(geneRecMap);

		return rec;
	}

	protected HashMap<String, ArrayList<recRelated.Rating>> getRatings(SimType field) 
			throws IOException {
		HashMap<String, ArrayList<Rating>> ratingsMap = new HashMap<String, ArrayList<Rating>>();

		// decide on fileName to use
		String fileName = Similarity.decideFileName(field, folderName);

		// set path
		String path = inputPath + fileName;

		// read file
		// Open the file
		FileInputStream fstream = new FileInputStream(path);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		// Read 1 line for header 
		String header = br.readLine();
		String[] splittedHeader = header.split(",");

		//Read File Line By Line until the index of target
		String strLine = null;
		while ((strLine = br.readLine()) != null)   // read info
		{	
			// read line, parse it and add to map
			String[] splitted = strLine.split(",");
			String stdNameTarget = splitted[0].replace("\"", "");

			String recommenderGeneName = splitted[0].replace("\"", "");
			for(int i = 1; i < splitted.length; i++){
				String recommendedGeneName = splittedHeader[i].replace("\"", "");
				Double value = Double.valueOf(splitted[i]);

				insertTo(ratingsMap, recommendedGeneName, recommenderGeneName, value);
			}

		}

		return ratingsMap;
	}

	
	protected PriorityQueue<recRelated.Recommendation> createRecsFromGenes(
			HashMap<String, Double> geneRecMap) {
		// for each item add score and create recommendation	
		PriorityQueue<Recommendation> recList = null;
		try{
			Comparator<Recommendation> recComp = new RecommendationComparator();
			int size = 1;
			if(parameters.getOutputListSize() != parameters.outputListSizeDefault){
				size = parameters.getOutputListSize();
			}
			recList = new PriorityQueue<Recommendation>(size, recComp);

			for(Entry<String, Double> e:geneRecMap.entrySet())
			{
				String recommendedGene = e.getKey();
				Double score = e.getValue();

				Recommendation rec = new Recommendation(recommendedGene, score);
				recList.add(rec);
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return recList;
	}
	protected HashMap<String, Double> calculateGeneScores(String targetGene,
			HashMap<String, ArrayList<recRelated.Rating>> allRecommendedGenes) {
		// calculate average, for genes
		HashMap<String, Double> recScores = new HashMap<String, Double>();

		// recommended --> rating
		for(Map.Entry<String, ArrayList<Rating>> r:allRecommendedGenes.entrySet())
		{
			String recommendedGene = r.getKey();
			ArrayList<Rating> ratingList = r.getValue();

			Double recScore = findRecScore(recommendedGene, targetGene, ratingList);
			recScores.put(recommendedGene, recScore);
		}

		return recScores;
	}

	protected Double findRecScore(String recommendedGene, String targetGene, 
			ArrayList<Rating> ratingList) {
		// use probVals as weights
		Double recScore = 0.0;

		switch(parameters.getItemSelectionType()){
		case SUM:
			recScore =findRecScoreSum(recommendedGene, targetGene, ratingList);
			break;
		case AVG:
			recScore =findRecScoreAvg(recommendedGene, targetGene, ratingList);
			break;
		case MAX:
			recScore =findRecScoreMax(recommendedGene, targetGene, ratingList);
			break;
		case WEIGHTEDAVG: 
			recScore =findRecScoreWeightedAvg(recommendedGene, targetGene, ratingList);
			break;
		default:
			System.out.println("Wrong isType");
			System.exit(-1);
			break;
		}

		return recScore;
	}

	private Double findRecScoreMax(String recommendedGene, String targetGene,
			ArrayList<Rating> ratingList) {
		Double recScore = 0.0;

		for(Rating rating: ratingList){
			// get connection probVal
			Double probVal = rating.getRatingScore();

			// sum up avgBindingProbs of users
			if(parameters.getOutlistType() == OutlistType.THRESHOLDBASED){
				if(probVal >= parameters.getConnThreshold()){
					if(probVal > recScore){
						recScore = probVal;
					}
				}
			} else if(parameters.getOutlistType() == OutlistType.FIXEDLENGTH){
				if(probVal > recScore){
					recScore = probVal;
				}
			} else {
				System.out.println("Wrong outputListType");
				System.exit(-1);
			}

		}

		return recScore;
	}

	private Double findRecScoreAvg(String recommendedGene, String targetGene,
			ArrayList<Rating> ratingList) {
		Double recScore = 0.0;

		for(Rating rating: ratingList){
			// get connection probVal
			Double probVal = rating.getRatingScore();

			// sum up avgBindingProbs of users
			if(parameters.getOutlistType() == OutlistType.THRESHOLDBASED){
				if(probVal >= parameters.getConnThreshold()){
					recScore += probVal;
				}
			} else if(parameters.getOutlistType() == OutlistType.FIXEDLENGTH){
				recScore += probVal;	
			} else {
				System.out.println("Wrong outputListType");
				System.exit(-1);
			}

		}
		// recscore is avg of avgBindingProbs of users
		recScore = recScore/ratingList.size();

		return recScore;
	}

	/**
	 * 
	 * @param recommendedGene
	 * @param targetGene
	 * @param ratingList : Contains recommenderGene, recommendedGene and rating info. 
	 * 					   recommendedGene in here and the first parameter are the same.
	 * @return
	 */
	private Double findRecScoreWeightedAvg(String recommendedGene, String targetGene,
			ArrayList<Rating> ratingList) {
		Double recScore = 0.0;

		Double totalWeight = 0.0;
		for(Rating rating: ratingList){
			// get connection probVal
			Double probVal = rating.getRatingScore();
			String recommenderGene = rating.getRecommenderGeneName();

			// sum up avgBindingProbs of users
			if(parameters.getOutlistType() == OutlistType.THRESHOLDBASED){
				if(probVal >= parameters.getConnThreshold()){
					Double weight = getWeight(targetGene, recommenderGene);
					totalWeight += weight;
					recScore += probVal * weight;
				}
			} else if(parameters.getOutlistType() == OutlistType.FIXEDLENGTH){
				Double weight =  getWeight(targetGene, recommenderGene);
				totalWeight += weight;
				recScore += probVal * weight;
			} else {
				System.out.println("Wrong outputListType");
				System.exit(-1);
			}

		}
		// recscore is avg of avgBindingProbs of users
		recScore = recScore/totalWeight;

		return recScore;
	}


	/**
	 * Now, we are getting the weights from connection probabilities among genes,
	 * TODO Normally this should be dynamic, 
	 * and user should be able to decide on which measure to be used as weight
	 * 
	 * @param targetGene: Who is given the recommendation at the end
	 * @param secondGene: One of the selected neighbor 
	 * @return
	 */
	private Double getWeight(String targetGene, String secondGene) {
		Double weight = -1.0;

		// Search over the connection probabilities of the target gene
		// find the second input gene, and get the rating score as the weight
		ArrayList<Rating> weightList = targetSimilarityBasedMap.get(targetGene);
		for(Rating r: weightList){
			// TODO search by using equals() comparison??
			if(r.getRecommenderGeneName().equals(targetGene) && 
					r.getRecommendedGeneName().equals(secondGene)){
				weight = r.getRatingScore();
				break;
			}
		}

		if(weight.equals(-1.0)){
			System.out.println("An error found: No sim found between "
					+ "genes "+ targetGene+ " and " + secondGene);
			System.exit(-1);
		}
		return weight;
	}

	private Double findRecScoreSum(String recommendedGene, String targetGene,
			ArrayList<Rating> ratingList) {
		Double recScore = 0.0;
		for(Rating rating: ratingList){
			// get connection probVal
			Double probVal = rating.getRatingScore();

			// recscore is total of avgBindingProbs of users
			if(parameters.getOutlistType() == OutlistType.THRESHOLDBASED){
				if(probVal >= parameters.getConnThreshold()){
					recScore += probVal;
				}
			} else if(parameters.getOutlistType() == OutlistType.FIXEDLENGTH){
				recScore += probVal;	
			} else {
				System.out.println("Wrong outputListType");
				System.exit(-1);
			}

		}
		return recScore;
	}

	private void insertTo(
			HashMap<String, ArrayList<recRelated.Rating>> ratingsMap,
			String recommendedGeneName, String recommenderGeneName, Double value) {
		//map: (recommender-->recommended+vals)
		// control if any entry exists for this gene
		ArrayList<recRelated.Rating> ratingList = ratingsMap.get(recommenderGeneName);
		if(ratingList == null){
			// no such entry exists before
			ratingList = new ArrayList<recRelated.Rating>();
			Rating rating = new Rating(recommenderGeneName, recommendedGeneName, value);
			ratingList.add(rating);
			ratingsMap.put(recommenderGeneName, ratingList);
		} else {
			// update entry
			Rating rating = new Rating(recommenderGeneName, recommendedGeneName, value);
			ratingList.add(rating);
			ratingsMap.put(recommenderGeneName, ratingList);
		}

	}

	protected HashMap<String, ArrayList<recRelated.Rating>> combineRecommendations(
			String targetGene, ArrayList<String> similarGenes, 
			HashMap<String, ArrayList<recRelated.Rating>> connScores) {
		// combine recommender and recommended genes 
		// recommended --> recommender + vals
		HashMap<String, ArrayList<Rating>> allRecommendedGenes = 
				new HashMap<String, ArrayList<Rating>>();

		for(String simGene: similarGenes){
			ArrayList<recRelated.Rating> ratingList = connScores.get(simGene);
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
		}


		return allRecommendedGenes;
	}

	/**
	 *  Get k-many similar genes 
	 * @param con
	 * @param targetGene
	 * @param numberOfSimilarGenes
	 * @return
	 */
	protected ArrayList<String> getMostSimilarGenes(Connection con,
			String targetGene, Integer numberOfSimilarGenes, ArrayList<SimType> fieldList) {

		// 2) Get other genes similarities to the target
		List<Similarity> geneSimValsMap = getGeneSimVals(con, targetGene, fieldList);

		// 3) Get most similar users using multi-obj-opt
		ArrayList<String> similarGenes = getMostSimilarGenes(parameters.prefferedMoType, 
				geneSimValsMap,  fieldList, numberOfSimilarGenes);


		/*// create retList by reading the similar genes info from db
		ArrayList<Gene> retList = createSimGenesList(con, similarGenes);*/

		return similarGenes;
	}


	private ArrayList<String> getMostSimilarGenes(MOType prefferedMoType,
			List<Similarity> geneSimValsMap, ArrayList<SimType> fieldList,
			Integer numberOfSimilarGenes) {
		ArrayList<String> similarGenes = null;

		switch(parameters.getPrefferedMoType()){
		case ONLYDOMINATES:
		{
			similarGenes = getMostSimilarGenes(geneSimValsMap, 
					fieldList, numberOfSimilarGenes);
		}
		break;
		case KDOMINATES:
		{
			similarGenes = new ArrayList<String>();
			while(similarGenes.size() < numberOfSimilarGenes){
				ArrayList<String> similarGenesTemp	= getMostSimilarGenes(similarGenes, geneSimValsMap, 
						fieldList, numberOfSimilarGenes);

				if(similarGenesTemp.size() > 0 ) {
					int size = similarGenesTemp.size();
					if((similarGenes.size() + size) <= numberOfSimilarGenes){
						similarGenes.addAll(similarGenesTemp);
					}else{
						for(int i = 0; i < size; i++){
							if(similarGenes.size() < numberOfSimilarGenes){
								String similarGene = similarGenesTemp.get(i);
								similarGenes.add(similarGene);
							} else{
								break;
							}
						}
					}
				} else{
					break;
				}
			}	
		}
		break;
		case ATLEASTKDOMINATES:
		{
			similarGenes = new ArrayList<String>();
			while(similarGenes.size() < numberOfSimilarGenes){
				ArrayList<String> similarGenesTemp	= getMostSimilarGenes(similarGenes, geneSimValsMap, 
						fieldList, numberOfSimilarGenes);

				if(similarGenesTemp.size() > 0 ) {
					similarGenes.addAll(similarGenesTemp);
				} else{
					break;
				}
			}		
		}
		break;
		default: 
			System.out.println("Error in type of preffered MO type");
			break;
		}




		return similarGenes;
	}
	public ArrayList<String> getMostSimilarGenes(ArrayList<String> alreadySelected, 
			List<recRelated.Similarity> geneSimValsMap, ArrayList<Similarity.SimType> fieldsToUse, 
			Integer numberOfSimilarGenes) {
		// remove elements(non-dominated users) which are already selected
		ArrayList<Similarity> geneSimValsMapPruned = new ArrayList<Similarity>();

		for(Similarity sim:geneSimValsMap){
			String geneName = sim.getGeneName();
			if(alreadySelected.contains(geneName)){
				// do nothing
			} else{
				geneSimValsMapPruned.add(sim);
			}
		}

		// run normal getSimilar users & return
		return getMostSimilarGenes(geneSimValsMapPruned, fieldsToUse, numberOfSimilarGenes);
	}

	public ArrayList<String> getMostSimilarGenes(List<recRelated.Similarity> geneSimValsMap, 
			ArrayList<Similarity.SimType> fieldsToUse, 
			Integer numberOfSimilarGenes) {
		// 1) create dominance matrix
		MOBasedSimilarityCalculator moSimCalc = new MOBasedSimilarityCalculator();
		Double[][] dominanceMatrix = moSimCalc.createDominanceMatrix(geneSimValsMap, fieldsToUse);

		// 2) select non-dominated neighbours
		ArrayList<Similarity> nonDominatedSims = moSimCalc.findNonDominatedSims(geneSimValsMap, 
				dominanceMatrix);

		/*// 3) sort non-dominated neighbours -- TODO by what 
		ArrayList<Similarity.SimType> sortOrder = new ArrayList<Similarity.SimType>();
		ArrayList<Integer> similarUsers = moSimCalc.sortBy(nonDominatedSims, sortOrder, 
				numberOfSimilarUsers, userSimilarityThreshold);		*/

		ArrayList<String> neighbours = new ArrayList<String>();
		for(Similarity sim:nonDominatedSims){
			neighbours.add(sim.getGeneName());
		}

		return neighbours;


	}
	
	/**
	 * Get gene simValues by reading  the related files
	 * @param con
	 * @param targetGene
	 * @param fieldList
	 * @return
	 */
	private List<Similarity> getGeneSimVals(Connection con, String targetGene,
			ArrayList<SimType> fieldList) {
		ArrayList<Similarity> similarities = new ArrayList<>();

		try{
			// read similarities by field
			HashMap<SimType,HashMap<String, Double>> simValByField = new HashMap<SimType, HashMap<String,Double>>();

			for(SimType field:fieldList){
				HashMap<String, Double> simValsOfOtherGenes = getSimilarity(targetGene, field);	
				simValByField.put(field, simValsOfOtherGenes);
			}

			// create similarities by gene name (for other calculations made later on)
			// create geneName to field-simVal map
			HashMap<String,HashMap<SimType, Double>> simValByGene = createSimValbyGeneMap(simValByField);

			for(Entry<String, HashMap<SimType, Double>> e: simValByGene.entrySet()){
				Similarity sim = new Similarity(e.getKey(),e.getValue());
				similarities.add(sim);
			}


		} catch(Exception e){
			e.printStackTrace();
		}

		return similarities;
	}

	private HashMap<String, HashMap<SimType, Double>> createSimValbyGeneMap(
			HashMap<SimType, HashMap<String, Double>> simValByField) {
		HashMap<String,HashMap<SimType, Double>> simValByGene = new HashMap<String, HashMap<SimType,Double>>();

		for(Entry<SimType, HashMap<String, Double>> e : simValByField.entrySet()){
			SimType simType = e.getKey();
			HashMap<String, Double> simVals = e.getValue();

			for(Entry<String, Double> e2:simVals.entrySet()){
				String geneName = e2.getKey();
				Double simVal = e2.getValue();

				// insert to returning hashmap
				insertTo(simValByGene, simType,geneName,simVal);

			}
		}

		return simValByGene;
	}


	private void insertTo(
			HashMap<String, HashMap<SimType, Double>> simValByGene,
			SimType simType, String geneName, Double simVal) {
		// control if any entry exists for this gene
		HashMap<SimType, Double> geneNameEntry = simValByGene.get(geneName);
		if(geneNameEntry == null){
			geneNameEntry = new HashMap<Similarity.SimType, Double>();
			geneNameEntry.put(simType, simVal);
			simValByGene.put(geneName, geneNameEntry);
		} else {
			// update entry
			geneNameEntry.put(simType, simVal);
			simValByGene.put(geneName, geneNameEntry);
		}
	}

	private HashMap<String, Double> getSimilarity(String targetGene, SimType field) 
			throws NumberFormatException, IOException {
		// decide on fileName to use
		String fileName = Similarity.decideFileName(field, folderName);

		// set path
		String path = inputPath + fileName;

		// read file
		// Open the file
		FileInputStream fstream = new FileInputStream(path);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		// Read 1 line for header 
		String header = br.readLine();
		String[] splittedHeader = header.split(",");
		// find index of target gene
		Integer indexOfTargetGene = findGeneIndex(targetGene, splittedHeader);

		//Read File Line By Line until the index of target
		String strLine = null;
		int readLineCount=0;
		HashMap<String, Double> simMap = new HashMap<String, Double>();
		while ((strLine = br.readLine()) != null)   // read info
		{
			readLineCount++;
			if(readLineCount == indexOfTargetGene){
				// read related line, parse it and return output
				String[] splitted = strLine.split(",");
				String stdNameTarget = splitted[0].replace("\"", "");

				for(int i = 1; i < splitted.length; i++){
					String geneName = splittedHeader[i].replace("\"", "");
					Double value = Double.valueOf(splitted[i]);

					// dont collect the gene itself 
					if(geneName.equals(targetGene) == false){
						simMap.put(geneName, value);
					} 

				}

			}

		}

		return simMap;
	}

	private Integer findGeneIndex(String geneName, String[] splittedHeader) {
		Integer retVal = null;
		for(int i=0; i<splittedHeader.length; i++){
			String s = splittedHeader[i].replace("\"", "");
			if(geneName.equals(s)){
				retVal = i;
				break;
			}
		}

		return retVal;
	}

}
