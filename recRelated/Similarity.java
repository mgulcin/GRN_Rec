package recRelated;

import java.util.HashMap;

public class Similarity {
	private String geneName;
	private HashMap<SimType, Double> similarityMap; // <simType,simVal> 

	// sim types
	public enum SimType { ALL, PHASE1, PHASE2, PHASE3, CONNPROB, // used by the dataset of Spellman and Lee
		KNOCKDOWNS, KNOCKOUTS, MULTIFACTORIAL, AVGTIMESERIES // used by dream4 challenge size10&100
		;}

	public Similarity(String geneName, HashMap<SimType, Double> similarityMap) {
		super();
		this.geneName = geneName;
		this.similarityMap = similarityMap;
	}

	public String getGeneName() {
		return geneName;
	}

	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}

	public HashMap<SimType, Double> getSimilarityMap() {
		return similarityMap;
	}

	public void setSimilarityMap(HashMap<SimType, Double> similarityMap) {
		this.similarityMap = similarityMap;
	}
    
	public static  String decideFileName(SimType field, String folderName) {
		String retVal ="";
		switch(field){
		case PHASE1: 
			retVal = "geneSim_byImputedPhase1.csv"; 
			break;
		case PHASE2: 
			retVal = "geneSim_byImputedPhase2.csv"; 
			break;
		case PHASE3: 
			retVal = "geneSim_byImputedPhase3.csv"; 
			break;
		case ALL: 
			retVal = "geneSim_byImputed.csv"; 
			break;
		case CONNPROB: 
			retVal = "pVal_output.csv";
			break;
		case KNOCKDOWNS:
			retVal = "//" + folderName + "//" + folderName+"_knockdowns.tsv_CosSim.csv"; 
			break;
		case KNOCKOUTS:
			retVal = "//" + folderName + "//" +folderName+"_knockouts.tsv_CosSim.csv"; 
			break;
		case MULTIFACTORIAL:
			retVal = "//" + folderName + "//" +folderName+"_multifactorial.tsv_CosSim.csv"; 
			break;
		case AVGTIMESERIES:
			retVal = "//" + folderName + "//" +folderName+"_timeseries.tsv_CosSimAvg.csv"; 
			break;
		default:
			System.out.println("Wrong similarity type. The file name could not be decided.");
			break;
		}
		return retVal;
	}
}
