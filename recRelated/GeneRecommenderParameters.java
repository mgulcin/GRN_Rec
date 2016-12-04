package recRelated;

import recRelated.Similarity.SimType;


public class GeneRecommenderParameters {
	//  types
	public enum MOType{ONLYDOMINATES, KDOMINATES, ATLEASTKDOMINATES;}
	public enum OutlistType{FIXEDLENGTH, THRESHOLDBASED;}
	public enum ISType{SUM, AVG, MAX, WEIGHTEDAVG;}

	// defult parameters
	public static MOType moTypeDefault = MOType.ONLYDOMINATES;
	public static ISType isTypeDefault = ISType.SUM;
	public static OutlistType outlistTypeDefault = OutlistType.THRESHOLDBASED;
	public static Integer numberOfSimilarGenesDefault = -1;
	public static Integer outputListSizeDefault = -1; 
	public static Double connThresholdDefault = -1.0;
	public static SimType chosenTargetSimDefault = Similarity.SimType.CONNPROB;

	// params	
	protected Integer numberOfSimilarGenes;
	protected Integer outputListSize;
	protected Double connThreshold;
	protected MOType prefferedMoType;
	protected OutlistType outlistType;
	protected ISType itemSelectionType;
	protected SimType chosenTargetSim;

	public GeneRecommenderParameters(Integer numberOfSimilarGenes,
			Integer outputListSize, Double connThreshold,
			MOType prefferedMoType, OutlistType outlistType,
			ISType itemSelectionType, SimType chosenTargetSim) {
		super();
		this.numberOfSimilarGenes = numberOfSimilarGenes;
		this.outputListSize = outputListSize;
		this.connThreshold = connThreshold;
		this.prefferedMoType = prefferedMoType;
		this.outlistType = outlistType;
		this.itemSelectionType = itemSelectionType;
		this.chosenTargetSim = chosenTargetSim;
	}

	public Integer getNumberOfSimilarGenes() {
		return numberOfSimilarGenes;
	}

	public void setNumberOfSimilarGenes(Integer numberOfSimilarGenes) {
		this.numberOfSimilarGenes = numberOfSimilarGenes;
	}

	public Integer getOutputListSize() {
		return outputListSize;
	}

	public void setOutputListSize(Integer outputListSize) {
		this.outputListSize = outputListSize;
	}

	public Double getConnThreshold() {
		return connThreshold;
	}

	public void setConnThreshold(Double connThreshold) {
		this.connThreshold = connThreshold;
	}

	public MOType getPrefferedMoType() {
		return prefferedMoType;
	}

	public void setPrefferedMoType(MOType prefferedMoType) {
		this.prefferedMoType = prefferedMoType;
	}

	public OutlistType getOutlistType() {
		return outlistType;
	}

	public void setOutlistType(OutlistType outlistType) {
		this.outlistType = outlistType;
	}

	public ISType getItemSelectionType() {
		return itemSelectionType;
	}

	public void setItemSelectionType(ISType itemSelectionType) {
		this.itemSelectionType = itemSelectionType;
	}
	
	public SimType getChosenTargetSim() {
		return chosenTargetSim;
	}

	public void setChosenTargetSim(SimType chosenTargetSim) {
		this.chosenTargetSim = chosenTargetSim;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(numberOfSimilarGenes);
		builder.append(",");
		builder.append(outputListSize);
		builder.append(",");
		builder.append(connThreshold);
		builder.append(",");
		builder.append(prefferedMoType);
		builder.append(",");
		builder.append(outlistType);
		builder.append(",");
		builder.append(itemSelectionType);
		return builder.toString();
	} 



}
