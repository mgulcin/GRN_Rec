package other;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class Dream4OutputCreator {
	protected static String outputPathBase = ".//outputMOnlyDream4-Size100(Time)//";
	protected static String inputPathBase = ".//outputMOnly_Dream4_size100_Nw";
	protected static String fileNameListPath = ".//data//filesDream4_size100.txt";
	protected static String fileNameBase = "DREAM4_Example_InSilico_Size100_";

	// collect nw1-nw5 in a folder for each different setting (~4000 folders)
	public static void main(String[] args) {
		try{
			// read all file names
			ArrayList<String> fileNames  = readFileNames(fileNameListPath);
			// create folder for all fileNames
			createFolders(outputPathBase, fileNames);

			// copy output files to the destination folders
			for(String file:fileNames){
				String path = outputPathBase +  file.replace(".csv", "" + "//");
				for(int i=1; i<=5; i++){
					String destStr =  path + fileNameBase + i;
					String sourceStr = inputPathBase + i + "//" + file;

					File  dest = new File(destStr);
					File  source = new File(sourceStr);
					copyFileUsingStream(source, dest);
				}
			}
		} catch (Exception e){
			e.printStackTrace();;
		}
	}

	private static void copyFileUsingStream(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	// create the folders to hold the results
	private static void createFolders(String outputPathBase,
			ArrayList<String> fileNames) {
		for(String file:fileNames){
			String path = outputPathBase +  file.replace(".csv", "" + "//");
			File  f = new File(path);
			f.mkdir();
		}

	}

	private static ArrayList<String> readFileNames(String path) throws IOException {
		ArrayList<String> fileNames = new ArrayList<String>();
		// Open the file
		FileInputStream fstream = new FileInputStream(path);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		//Read File Line By Line until the index of target
		String strLine = null;
		while ((strLine = br.readLine()) != null)   // read info
		{
			fileNames.add(strLine);
		}
	
		br.close();
		in.close();
		fstream.close();
		
		return fileNames;
	}
}
