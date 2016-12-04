package recRelated;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;


public class FilePrinter extends Printer{

	public FilePrinter(boolean debugVal) {
		super(debugVal,PrinterType.FILEPRINTER);
	}

	public void printOverallEvalResult(String path,
			GeneRecommenderParameters parameters,
			HashMap<String, EvaluationResult> overallResultByTypeMap) {
		try{
			//print  list to file
			FileOutputStream fos;
			fos = new FileOutputStream(path,true);
			PrintStream ps = new PrintStream(fos);

			for(Entry<String, EvaluationResult> e: overallResultByTypeMap.entrySet()){
				String interactionName = e.getKey();
				EvaluationResult evalResult = e.getValue();


				StringBuilder builder = new StringBuilder();
				builder.append(parameters.toString());
				builder.append(",");
				builder.append(interactionName);
				builder.append(",");
				builder.append(evalResult.toString());
				builder.append(",");


				ps.print(builder.toString());
				ps.println();

			}


			ps.flush();
			ps.close();

		} catch(Exception e){
			e.printStackTrace();
		}

	}

	public void printEvalResult(String path,
			HashMap<String, HashMap<String, EvaluationResult>> evalResultMap)  {
		try{
			//print  list to file
			FileOutputStream fos;
			fos = new FileOutputStream(path,true);
			PrintStream ps = new PrintStream(fos);

			for(Entry<String, HashMap<String, EvaluationResult>> e: evalResultMap.entrySet()){
				String targetGene = e.getKey();
				HashMap<String, EvaluationResult> resMap = e.getValue();

				for(Entry<String, EvaluationResult> e2: resMap.entrySet()){
					String interactionName = e2.getKey();
					EvaluationResult evalResult = e2.getValue();

					StringBuilder builder = new StringBuilder();
					builder.append(targetGene);
					builder.append(",");
					builder.append(interactionName);
					builder.append(",");
					builder.append(evalResult.toString());
					builder.append(",");


					ps.print(builder.toString());
					ps.println();
				}
			}


			ps.flush();
			ps.close();

		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void printRecommendedGenes(String path,
			String targetGene, ArrayList<Recommendation> resultRecs) {
		try{
			//print  list to file
			FileOutputStream fos;
			fos = new FileOutputStream(path,true);
			PrintStream ps = new PrintStream(fos);

			ps.print(targetGene + ",");
			for(Recommendation rec:resultRecs){
				ps.print(rec.getRecommendedGene() + ",");
			}
			ps.println();

			ps.flush();
			ps.close();

		} catch(Exception e){
			e.printStackTrace();
		}

	}
	
	public void printRecommendedGenesMultiLine(String path,
			String targetGene, ArrayList<Recommendation> resultRecs) {
		try{
			//print  list to file
			FileOutputStream fos;
			fos = new FileOutputStream(path,true);
			PrintStream ps = new PrintStream(fos);

			for(Recommendation rec:resultRecs){
				ps.print(targetGene.replace("G", "") + "\t");
				ps.println(rec.getRecommendedGene().replace("G", "") + "\t" + 1);
			}
			
			ps.flush();
			ps.close();

		} catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public void printMostSimilarGenes(String path, String targetGene,
			ArrayList<String> similarGenes) {
		try{
			//print  list to file
			FileOutputStream fos;
			fos = new FileOutputStream(path,true);
			PrintStream ps = new PrintStream(fos);

			ps.print(targetGene + ",");
			for(String simGene:similarGenes){
				ps.print(simGene + ",");
			}
			ps.println();

			ps.flush();
			ps.close();

		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void printString(String path, String str) {
		try{
			//print  list to file
			FileOutputStream fos;
			fos = new FileOutputStream(path,true);
			PrintStream ps = new PrintStream(fos);

			ps.println(str );
			
			ps.flush();
			ps.close();

		} catch(Exception e){
			e.printStackTrace();
		}
		
	}


}
