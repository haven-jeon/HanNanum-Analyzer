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
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import kr.ac.kaist.swrc.jhannanum.share.TagSet;

/**
 * This class is for the connection rules of morphemes. It is used to check whether the morphemes
 * can appear consecutively.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class Connection {
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
	
	/** The connectoin table which has the connection information ofo morphemes */
	private boolean[][] connectionTable = null;
	
	/**
	 * Constructor.
	 */
	public Connection() {
		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		startTag = "";
		connectionTable = null;
	}
	
	/**
	 * Checks whether two morpheme tags can appear consecutively.
	 * @param tagSet - morpheme tag set
	 * @param tag1 - the first morpheme tag to check
	 * @param tag2 - the second morpheme tag to check
	 * @param len1 - the length of the first morpheme
	 * @param len2 - the length of the second morpheme
	 * @param typeOfTag2 - the tag type of the second morpheme tag
	 * @return true: the two consecutive morpheme tags can appear, false: they cannot appear
	 */
	public boolean checkConnection(TagSet tagSet, int tag1, int tag2, int len1, int len2, int typeOfTag2) {
		String tag1Name = tagSet.getTagName(tag1);
		String tag2Name = tagSet.getTagName(tag2);
		
		if ((tag1Name.startsWith("nc") || tag1Name.charAt(0) == 'f') &&
				tag2Name.charAt(0) == 'n') {
			if (tag2Name.startsWith("nq")) {
				return false;
			} else if (len1 < 4 || len2 < 2) {
				return false;
			}
		}
		
//		System.err.println(tag1Name + "\t" + tag2Name + ": " + connectionTable[tag1][tag2] + " " + tagSet.checkTagType(nextTagType, tag2));
		return connectionTable[tag1][tag2] && tagSet.checkTagType(typeOfTag2, tag2);
	}
	
	/**
	 * Cleans the connection rules and metadata.
	 */
	public void clear() {
		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		startTag = "";
		connectionTable = null;
	}
	
	/**
	 * Initialize the connection rules from the rule data file.
	 * @param filePath - the path for the connection rule data file
	 * @param tagCount - the number of the total tags
	 * @param tagSet - the tag set which is used in the connection rules
	 * @throws IOException
	 */
	public void init(String filePath, int tagCount, TagSet tagSet) throws IOException {
		readFile(filePath, tagCount, tagSet);
	}
	
	/**
	 * Reads the connection rule data file, and initialize the object.
	 * @param filePath - the path for the connection rule file
	 * @param tagCount - the number of total tags in the tag set
	 * @param tagSet - the tag set which is used in the connection rules
	 * @throws IOException
	 */
	private void readFile(String filePath, int tagCount, TagSet tagSet) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
		String line = null;
		HashSet<Integer> tagSetA = new HashSet<Integer>();
		HashSet<Integer> tagSetB = new HashSet<Integer>();

		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		startTag = "";
		connectionTable = new boolean[tagCount][tagCount];
		
		for (int i = 0; i < tagCount; i++) {
			for (int j = 0; j < tagCount; j++) {
				connectionTable[i][j] = false;
			}
		}
		
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
			} else if ("CONNECTION".equals(lineToken)) {
				lineToken = lineTokenizer.nextToken();
				String[] tagLists = lineToken.split("\\*", 2);
				
				StringTokenizer tagTokenizer = new StringTokenizer(tagLists[0], ",()");
				while (tagTokenizer.hasMoreTokens()) {
					String tagToken = tagTokenizer.nextToken();
					
					StringTokenizer tok = new StringTokenizer(tagToken, "-");
					while (tok.hasMoreTokens()) {
						String t = tok.nextToken();
						int[] fullTagIDSet = tagSet.getTags(t);
						
						if (fullTagIDSet != null) {
							for (int i = 0; i < fullTagIDSet.length; i++) {
								tagSetA.add(fullTagIDSet[i]);
							}
						} else {
							tagSetA.add(tagSet.getTagID(t));
						}
						while (tok.hasMoreTokens()) {
							tagSetA.remove(tagSet.getTagID(tok.nextToken()));
						}
					}
				}
				
				tagTokenizer = new StringTokenizer(tagLists[1], ",()");
				while (tagTokenizer.hasMoreTokens()) {
					String tagToken = tagTokenizer.nextToken();
					
					StringTokenizer tok = new StringTokenizer(tagToken, "-");
					while (tok.hasMoreTokens()) {
						String t = tok.nextToken();
						int[] fullTagIDSet = tagSet.getTags(t);
						
						if (fullTagIDSet != null) {
							for (int i = 0; i < fullTagIDSet.length; i++) {
								tagSetB.add(fullTagIDSet[i]);
							}
						} else {
							tagSetB.add(tagSet.getTagID(t));
						}
						while (tok.hasMoreTokens()) {
							tagSetB.remove(tagSet.getTagID(tok.nextToken()));
						}
					}
				}
				
				Iterator<Integer> iterA = tagSetA.iterator();
				
				while (iterA.hasNext()) {
					int leftSide = iterA.next();
					Iterator<Integer> iterB = tagSetB.iterator();
					
					while (iterB.hasNext()) {
						connectionTable[leftSide][iterB.next()] = true;
					}
				}
				
				tagSetA.clear();
				tagSetB.clear();
			} else if ("START_TAG".equals(lineToken)) {
				startTag = lineTokenizer.nextToken();
			}
		}
		br.close();
	}
}
