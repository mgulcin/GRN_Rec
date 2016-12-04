package recRelated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import recRelated.GeneRecommenderParameters.ISType;
import recRelated.GeneRecommenderParameters.MOType;
import recRelated.GeneRecommenderParameters.OutlistType;
import recRelated.Similarity.SimType;

public class MainDream4 {
	// parameters
	static String[] targetGeneList = null;//{"G1","G2","G3","G4","G5","G6","G7","G8","G9","G10"};


	static Integer TOTALGENECOUNT = 16;//targetGeneList.length;
	static SimType chosenTargetSim = SimType.KNOCKDOWNS;

	protected static Printer printer = new FilePrinter(false);
	protected static String inputPath = ".//data//Dream4_InSlico_100//";
	protected static String outputPath = ".//output_Dream4_Size100_Nw5//";
	protected static String folderName = "insilico_size100_5";
	protected static String outputPathTemp = ".//output_Dream4_Size100_Nw";
	protected static String folderNameTemp = "insilico_size100_";

	public static void main(String[] args) {
		int size = 100;
		targetGeneList = new String[size];
		for(Integer i=1; i<=size; i++){
			String name =  "G" + i.toString();
			targetGeneList[i-1]=name;
		}
		for(Integer i=1; i<= 5; i++){
			outputPath = outputPathTemp + i +"//";
			folderName = folderNameTemp + i;

			try {
				// create recommender
				GeneRecommender gr = new GeneRecommender(inputPath, outputPath);
				gr.setFolderName(folderName);

				// create  parameters
				List<GeneRecommenderParameters> parameterList = createParameters();

				// Set sim-fields to be used
				//TODO Make field selection dynamic
				ArrayList<SimType> simFieldList = new ArrayList<SimType>();
				simFieldList.add(SimType.KNOCKDOWNS);
				simFieldList.add(SimType.KNOCKOUTS);
				//simFieldList.add(SimType.MULTIFACTORIAL);
				simFieldList.add(SimType.AVGTIMESERIES);

				// set parameters
				for(GeneRecommenderParameters gParameters:parameterList){
					gr.setParameters(gParameters);
					// run experiments
					for(String targetGene: targetGeneList){
						ArrayList<Recommendation> recommendedGenes = gr.recommend(null, 
								targetGene, simFieldList);

						// Print recommended genes to the target gene
						String recGenesOutPath = outputPath + "recGenes," + gParameters.toString() + ".csv";
						//String recGenesOutPath = outputPath + "recGenesDream4_Size100_" + i + ".csv";

						printer.printRecommendedGenesMultiLine(recGenesOutPath, targetGene,recommendedGenes);
					}
				}

			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public static List<GeneRecommenderParameters> createParameters(){
		// defult parameters
		MOType moType = MOType.KDOMINATES;
		ISType isType = ISType.WEIGHTEDAVG;
		OutlistType outlistType = OutlistType.THRESHOLDBASED;

		Integer numberOfSimilarGenes = 11;
		Integer outputListSize = -1; 
		Double connThresholdDeafult = 0.99;

		// retList
		List<GeneRecommenderParameters> parameterList = new ArrayList<GeneRecommenderParameters>();
		// no need to change outlist size
		int decimalPlaces = 2; 
		for(Double connThreshold = 0.51; connThreshold <= 1.0;  connThreshold+=0.03){
			BigDecimal bd = new BigDecimal(connThreshold);
			bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
			connThreshold = bd.doubleValue();           

			GeneRecommenderParameters gParameters = 
					new GeneRecommenderParameters(numberOfSimilarGenes,
							outputListSize, connThreshold, 
							moType, outlistType, isType, chosenTargetSim);
			parameterList.add(gParameters);
		}	



		return parameterList;
	}

}
