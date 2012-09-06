/*  Copyright 2011 Heewon Jeon(madjakarta@gmail.com)

This file is part of KoNLP.

KoNLP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

KoNLP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with KoNLP.  If not, see <http://www.gnu.org/licenses/>   
*/

package kr.ac.kaist.swrc.jhannanum.share;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONException;

public class JSONZipReader extends JSONReader {

	private String zipFilePath = null;
	private String jsonFile = null;
	
	
	/**
	 * Constructor.
	 * @throws IOException 
	 * @throws JSONException 
	 * 
	 */
	public JSONZipReader(String zipFilePath, String jsonFile) throws JSONException, IOException  {
		this.zipFilePath = zipFilePath;
		this.jsonFile = jsonFile;
		read();
	}
	
	/**
	 * read JSON file from JAR file.
	 * @return Length of JSON Keys
	 * @throws JSONException, IOException
	 */
	
	protected int read() throws JSONException, IOException{
		ZipFile zip = new ZipFile(zipFilePath);
		ZipEntry entry = zip.getEntry(jsonFile);
		InputStream in  = zip.getInputStream(entry);
		this.json = read(in);
		zip.close();
		return json.length();
	}
	/*
	public static void main(String[] args) throws JSONException, IOException{
		JSONZipReader reader = new JSONZipReader("D:/opensource/Sejong/inst/dics/handics.zip", "conf/plugin/MajorPlugin/MorphAnalyzer/ChartMorphAnalyzer.json");
		System.out.println(reader.getAuthor());
	}
*/
}
