package dbRelated;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;


public class Main {

	static String spellmanNoMissingPath = "./data/spellman_subset25_imputed.csv";

	public static void main(String[] args) {
		//fillMissingValues(spellmanPath, spellmanNoMissingPath);
	}

	private static void fillMissingValues(String path,
			String outPath) {
		try{
			// read genes
			Set<Gene> geneSet = readGenes(path);

			// for each gene find tSerie containing missing value
			HashMap<Gene, ArrayList<String>> genesWithMissingVal = findGenesWithMissingValue(geneSet);
			
			// if gene contains missing value; calculate missing value and insert newValue
			HashMap<Gene, HashMap<String, Double>> genesWithoutMissingVal = fillMissings(genesWithMissingVal, geneSet);
			
		} catch(Exception e){
			e.printStackTrace();
		}

	}

	private static HashMap<Gene, HashMap<String, Double>> fillMissings(
			HashMap<Gene, ArrayList<String>> genesWithMissingVal,
			Set<Gene> geneSet) {
		HashMap<Gene, HashMap<String, Double>> genesWithoutMissingVal = new HashMap<Gene, HashMap<String,Double>>();
		for(Entry<Gene, ArrayList<String>> e: genesWithMissingVal.entrySet()){
			Gene g = e.getKey();
			ArrayList<String> tNames = e.getValue();
			
			// find knn for this gene
			//HashSet<Gene> knn = findKnn(g,geneSet);
			
			// fill the map of missing tName-value
			HashMap<String, Double> noMissingMap = new HashMap<String, Double>();
			//TODO
			
			// add the noMissingMap to the output map
			genesWithoutMissingVal.put(g, noMissingMap);
		}
		
		
		return genesWithoutMissingVal;
	}

	/**
	 * Returns map of gene --> list of time series with missing value
	 * @param geneSet
	 * @return
	 */
	private static HashMap<Gene, ArrayList<String>> findGenesWithMissingValue(Set<Gene> geneSet) {
		HashMap<Gene, ArrayList<String>> genesWithMissingValue = new HashMap<Gene, ArrayList<String>>();
		
		for(Gene g: geneSet){
			HashMap<String, Double> tMap = g.getTimeSeriesMap();
			
			for(Entry<String, Double> e: tMap.entrySet()){
				String tName = e.getKey();
				Double val = e.getValue();
				
				// an empty timeSerie
				if(val.equals(Double.MIN_VALUE)){
					// insert this gene and tName to the return map
					ArrayList<String> tNameList = genesWithMissingValue.get(g);
					if(tNameList == null){
						// a new gene
						tNameList = new ArrayList<String>();
						tNameList.add(tName);
						genesWithMissingValue.put(g,tNameList);
					} else {
						// update arraylist and re-insert to map
						tNameList.add(tName);
						genesWithMissingValue.put(g,tNameList);
					}
				}
			}
			
		}
		
		return genesWithMissingValue;
	}

	private static Set<Gene> readGenes(String path) throws IOException {
		Set<Gene> geneList = new HashSet<Gene>(25);

		// Open the file
		FileInputStream fstream = new FileInputStream(path);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		// Read 1 line for header 
		String header = br.readLine();
		String[] splittedHeader = header.split(",");
		//Read File Line By Line
		String strLine = null;
		while ((strLine = br.readLine()) != null)   // read info
		{
			String[] splitted = strLine.split(",");
			String stdName = splitted[0];
			String sysName = splitted[1];

			Gene newGene = new Gene(stdName, sysName);

			int size = splitted.length;
			for(int i=2; i<size; i++){
				String timeSerieName = splittedHeader[i];
				String valueStr = splitted[i];
				Double value = Double.MIN_VALUE;
				if (valueStr.equals("") == false){
					value = Double.valueOf(valueStr);
				}

				newGene.insertIntoTimeSeriesMap(timeSerieName, value);
			}

			geneList.add(newGene);
		}

		//Close the input stream
		in.close();
		return geneList;
	}
}
