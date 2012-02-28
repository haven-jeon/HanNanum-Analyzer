/*  Copyright 2010, 2011 Semantic Web Research Center, KAIST

This file is part of JHanNanum.

JHanNanum is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JHanNanum is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with JHanNanum.  If not, see <http://www.gnu.org/licenses/>   */

package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * This class is the data structure for the pre-analyzed dictionary.
 * 
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class AnalyzedDic {
	private Hashtable<String, String> dictionary;
	
	/**
	 * Constructor.
	 */
	public AnalyzedDic() {
		dictionary = new Hashtable<String, String>();
	}

	/**
	 * Constructor. It loads the pre-analyzed dictionary from data file to the hash table.
	 * The file format of dictionary should be like this: "ITEM\tCONTENT\n"
	 * 
	 * @param dictionaryFileName - the path for the pre-analyzed dictionary file
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public AnalyzedDic(String dictionaryFileName) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		dictionary = new Hashtable<String, String>();
		
		this.readDic(dictionaryFileName);
	}

	/**
	 * Cleans the data in the hash table.
	 */
	public void clear() {
		dictionary.clear();
	}
	
	/**
	 * Returns the contents according to the specified item.
	 * @param item - item to search
	 * @return the dictionary contents for the search item
	 */
	public String get(String item) {
		return dictionary.get(item);
	}
	
	/**
	 * It loads the pre-analyzed dictionary from data file to the hash table.
	 * The file format of dictionary should be like this: "ITEM\tCONTENT\n"
	 * @param dictionaryFileName - the path for the pre-analyzed dictionary file
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void readDic(String dictionaryFileName) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		dictionary.clear();
		String str = "";
		
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dictionaryFileName), "UTF-8"));

		while ((str = in.readLine()) != null) {
			str.trim();
			if (str.equals("")) {
				continue;
			}
			
			StringTokenizer tok	= new StringTokenizer(str, "\t");
			String key = tok.nextToken();
			String value = "";
			while (tok.hasMoreTokens()) {
				value += tok.nextToken() + "\n";
			}
			dictionary.put(key, value.trim());
		}
	}
}
