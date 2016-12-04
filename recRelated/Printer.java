/**
 * 
 */
package recRelated;

import java.util.ArrayList;
import java.util.HashMap;


/**
 *
 */
public abstract class Printer {
	public enum PrinterType
	{
		FILEPRINTER,
		CONSOLEPRINTER
	};
	
	// true --> print, false--> do not print
	protected boolean debugPrint = true;
	protected PrinterType type = null;
	
	
	/**
	 * @param debugPrint
	 * @param type
	 */
	public Printer(boolean debugPrint, PrinterType type ) {
		super();
		this.debugPrint = debugPrint;
		this.type = type;
	}

	abstract public void printString(String path, String str) ;
	
	abstract public void printMostSimilarGenes(String path, String targetGene,
			ArrayList<String> similarGenes) ;


	abstract public void printRecommendedGenes(String recGenesOutPath,
			String targetGene, ArrayList<Recommendation> resultRecs);


	abstract public void printEvalResult(String evalResultPath,
			HashMap<String, HashMap<String, EvaluationResult>> evalResultMap);


	abstract public void printOverallEvalResult(String resultPath,
			GeneRecommenderParameters parameters,
			HashMap<String, EvaluationResult> overallResultByTypeMap);


	abstract public void printRecommendedGenesMultiLine(String recGenesOutPath,
			String targetGene, ArrayList<Recommendation> recommendedGenes);
}
