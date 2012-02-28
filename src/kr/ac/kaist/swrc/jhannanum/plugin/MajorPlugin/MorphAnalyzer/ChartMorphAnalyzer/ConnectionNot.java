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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import kr.ac.kaist.swrc.jhannanum.share.TagSet;

/**
 * This class is for the impossible connection rules of morphemes. It is used to check
 * whether morphemes may not appear consecutively.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class ConnectionNot {
	/** The name of the connection rules. */
	public String title = null; 
	
	/** The version of the connection rules. */
	public String version = null;
	
	/** The copyright of the connection rules. */
	public String copyright = null;
	
	/** The author of the connection rules. */
	public String author = null;
	
	/** The date when the connection rules are updated. */
	public String date = null;
	
	/** The people who edited the connection rules. */
	public String editor = null;

	/** Start tag. */
	public String startTag = null;
	
	/** Table for the tags that cannot appear consecutively */
	private int[][] notTagTable = null;
	
	/** Table for the morphemes that cannot appear consecutively */
	private String[][] notMorphTable = null;
	
	/** The number of impossible connection rules */
	private int ruleCount = 0;
	
	
	/**
	 * Constructor.
	 */
	public ConnectionNot() {
		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		startTag = "";
	}
	
	/**
	 * Checks whether the two morphemes may not appear consecutively.
	 * @return true: they may appear consecutively, false: they may not appear consecutively
	 */
	public boolean checkConnection() {
		/* It should be updated.s */
		return true;
	}
	
	/**
	 * Cleans the rules loaded and metadata.
	 */
	public void clear() {
		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		startTag = "";
		ruleCount = 0;
		notTagTable = null;
		notMorphTable = null;
	}
	
	/**
	 * Initializes the object with the specified file for impossible connection rules.
	 * @param filePath - the file for the impossible connection rules
	 * @param tagSet - the morpheme tag set used in the rules
	 * @throws IOException
	 */
	public void init(String filePath, TagSet tagSet) throws IOException {
		readFile(filePath, tagSet);
	}
	
	/**
	 * Reads the impossible connection rules from the specified file.
	 * @param filePath - the file for the impossible connection rules
	 * @param tagSet - the morpheme tag set used in the rules
	 * @throws IOException
	 */
	private void readFile(String filePath, TagSet tagSet) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
		String line = null;
		ArrayList<String> ruleList = new ArrayList<String>();

		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		startTag = "";
		ruleCount = 0;
		
		while ((line = br.readLine()) != null) {
			StringTokenizer lineTokenizer = new StringTokenizer(line, "\t");
			if (lineTokenizer.hasMoreElements() == false) {
				continue;
			}
			
			String lineToken = lineTokenizer.nextToken();
			
			if (lineToken.startsWith("@")) {
				if ("@title".equals(lineToken)) {
					title = lineTokenizer.nextToken();
				} else if ("@version".equals(lineToken)) {
					version = lineTokenizer.nextToken();
				} else if ("@copyright".equals(lineToken)) {
					copyright = lineTokenizer.nextToken();
				} else if ("@author".equals(lineToken)) {
					author = lineTokenizer.nextToken();
				} else if ("@date".equals(lineToken)) {
					date = lineTokenizer.nextToken();
				} else if ("@editor".equals(lineToken)) {
					editor = lineTokenizer.nextToken();
				}
			} else if ("CONNECTION_NOT".equals(lineToken)) {
				ruleList.add(lineTokenizer.nextToken());
			}
		}
		
		ruleCount = ruleList.size();
		
		notTagTable = new int[ruleCount][2];
		notMorphTable = new String[ruleCount][2];

		Iterator<String> iter = ruleList.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			String rule = iter.next();
			StringTokenizer st = new StringTokenizer(rule, " ");
			notMorphTable[i][0] = st.nextToken();
			notTagTable[i][0] = tagSet.getTagID(st.nextToken());
			notMorphTable[i][1] = st.nextToken();
			notTagTable[i][1] = tagSet.getTagID(st.nextToken());
		}
		
		ruleList.clear();
		br.close();
	}
}