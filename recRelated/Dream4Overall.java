package recRelated;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Dream4Overall {
	static String evalResultFolderStr = ".//evalResult-MOnly_Size100(Time)//";
	static String resultPath = evalResultFolderStr + "//OverallResult" + ".csv";
	
	public static void main(String[] args) {
		Printer printer = new FilePrinter(false);
		getOverallResults(printer, evalResultFolderStr);
		
	}
	
	private static void getOverallResults(Printer printer, String evalResultFolderStr) {
		// get files in result folder
		try{
			// prepare the overall output file and header
			String outputStr = "AUROC_PVAL, AUPR_PVAL, AUROC_SCORE, AUPR_SCORE, SCORE";
			printer.printString(resultPath, outputStr);
			
			// read the files
			File evalResultFolder = new File(evalResultFolderStr);
			File[] listOfFiles = evalResultFolder.listFiles(); 

			for(File evalFile:listOfFiles){
				String name = evalFile.getName();
				if(name.startsWith("recGenes")){
					// get overall results
					getOverallResults(printer, evalResultFolderStr, evalFile);

				}
			}

		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static void getOverallResults(Printer printer, String resDir, File evalFile) {
				
		// read file , get total number of tp,fp,tn,fn, prec,recall,f1
		HashMap<String,EvaluationResult> overallResultByTypeMap = new HashMap<String,EvaluationResult>();
		
		try{
			// Open the file
			FileInputStream fstream = new FileInputStream(evalFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			//Read File Line By Line until the index of target
			String strLine = null;
			while ((strLine = br.readLine()) != null)   // read info
			{
				if(strLine.equals("Overall")){
					//Read 2 more lines to get the results
					//<AUROC_PVAL, AUPR_PVAL, AUROC_SCORE, AUPR_SCORE, SCORE>
					strLine = br.readLine();
					strLine = br.readLine();
					
					// print the line to the output
					// printResults
					String outputStr = evalFile.getName() + "," + strLine;
					printer.printString(resultPath, outputStr);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
