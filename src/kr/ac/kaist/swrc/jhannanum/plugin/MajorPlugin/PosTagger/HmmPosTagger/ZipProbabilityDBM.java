package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.HmmPosTagger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipProbabilityDBM extends ProbabilityDBM {

	
	public ZipProbabilityDBM(String zipFilePath, String filePath) throws IOException {
		table = new Hashtable<String,double[]>();
		
		ZipFile zip = new ZipFile(zipFilePath);
		ZipEntry entry = zip.getEntry(filePath);
		InputStream in  = zip.getInputStream(entry);

		BufferedReader bufreader = new  BufferedReader(new InputStreamReader(in));
		
		init(bufreader);
		zip.close();
	}
	
	

}
