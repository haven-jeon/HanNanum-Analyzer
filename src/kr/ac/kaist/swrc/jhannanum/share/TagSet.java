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

package kr.ac.kaist.swrc.jhannanum.share;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Morpheme tag set.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class TagSet {
	/** KAIST tag set */
	final public static int TAG_SET_KAIST = 0;
	
	/** tag type - all */
	final public static int TAG_TYPE_ALL = 0;
	
	/** tag type - verb */
	final public static int TAG_TYPE_VERBS = 1;
	
	/** tag type - noun */
	final public static int TAG_TYPE_NOUNS = 2;
	
	/** tag type - pronoun */
	final public static int TAG_TYPE_NPS = 3;
	
	/** tag type - adjective */
	final public static int TAG_TYPE_ADJS = 4;
	
	/** tag type - bound noun */
	final public static int TAG_TYPE_NBNP = 5;
	
	/** tag type - josa(particle) */
	final public static int TAG_TYPE_JOSA = 6;
	
	/** tag type - yongeon(verb, adjective) */
	final public static int TAG_TYPE_YONGS = 7;
	
	/** tag type - eomi(ending) */
	final public static int TAG_TYPE_EOMIES = 8;
	
	/** tag type - predicative particle */
	final public static int TAG_TYPE_JP = 9;
	
	/** the number of tag types */
	final public static int TAG_TYPE_COUNT = 10;
	
	/** phoneme type - all */
	final public static int PHONEME_TYPE_ALL = 0;

	/** the name of tag set */
	public String title = null;
	
	/** the version of tag set */
	public String version = null;
	
	/** the copyright of tag set */
	public String copyright = null;
	
	/** the author of tag set */
	public String author = null;
	
	/** the last update date of tag set */
	public String date = null;
	
	/** the editor of the tag set */
	public String editor = null;

	/** the morpheme tag list */
	private ArrayList<String> tagList = null;
	
	/** the irregular rule list */
	private ArrayList<String> irregularList = null;
	
	/** the hash map for the group of tags */
	private HashMap<String, int[]> tagSetMap = null;
	
	/** the table for tag types */
	private int[][] tagTypeTable = null;
	
	/** the list of index tags */
	public int[] indexTags = null;
	
	/** the list of unknown tags */
	public int[] unkTags = null;
	
	/** the start tag */
	public int iwgTag = 0;
	
	/** the unknown tag */
	public int unkTag = 0;
	
	/** the number tag */
	public int numTag = 0;
	
	/** 'ㅂ' irregular */
	public int IRR_TYPE_B;
	
	/** 'ㅅ' irregular */
	public int IRR_TYPE_S;
	
	/** 'ㄷ' irregular */
	public int IRR_TYPE_D;
	
	/** 'ㅎ' irregular */
	public int IRR_TYPE_H;
	
	/** '르' irregular */
	public int IRR_TYPE_REU;
	
	/** '러' irregular */
	public int IRR_TYPE_REO;

	/**
	 * Constructor.
	 */
	public TagSet() {
		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		tagList = new ArrayList<String>();
		irregularList = new ArrayList<String>();
		tagSetMap = new HashMap<String, int[]>();
		tagTypeTable = new int[TAG_TYPE_COUNT][];
	}
	
	/**
	 * Checks the phoneme type
	 * @param phonemeType - phoneme type
	 * @param phoneme - phoneme
	 * @return true: the phoneme belongs to the specified type, otherwise false
	 */
	public boolean checkPhonemeType(int phonemeType, int phoneme) {
		if (phonemeType == PHONEME_TYPE_ALL) {
			return true;
		}
		return phonemeType == phoneme;
	}
	
	/**
	 * Checks morpheme tag type
	 * @param tagType - tag type
	 * @param tag - morpheme tag
	 * @return true: the morpheme tag belongs to the specified type
	 */
	public boolean checkTagType(int tagType, int tag) {
		if (tagType == TAG_TYPE_ALL) {
			return true;
		}
		for (int i = 0; i < tagTypeTable[tagType].length; i++) {
			if (tagTypeTable[tagType][i] == tag) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Cleans the data loaded.
	 */
	public void clear() {
		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		tagList.clear();
		irregularList.clear();
		tagSetMap.clear();
	}
	
	/**
	 * Returns the ID of the specified irregular rule.
	 * @param irregular	- irregular rule
	 * @return the ID of the irregular rule
	 */
	public int getIrregularID(String irregular) {
		return irregularList.indexOf(irregular);
	}
	
	/**
	 * Returns the name of the irregular rule for the specified ID.
	 * @param irregularID - the ID of the irregular rule
	 * @return the name of the irregular rule
	 */
	public String getIrregularName(int irregularID) {
		return irregularList.get(irregularID);
	}
	
	/**
	 * Returns the number of morpheme tags loaded.
	 * @return the number of morpheme tags loaded
	 */
	public int getTagCount() {
		return tagList.size();
	}
	
	/**
	 * Returns the ID of the morpheme tag.
	 * @param tag - morpheme tag
	 * @return the tag ID, -1 if it doesn't contain the tag
	 */
	public int getTagID(String tag) {
		return tagList.indexOf(tag);
	}

	/**
	 * Returns the tag name for the specified ID.
	 * @param tagID	- the morpheme tag ID
	 * @return the tag name
	 */
	public String getTagName(int tagID) {
		return tagList.get(tagID);
	}
	
	/**
	 * Returns the morpheme tags in the specified tag group.
	 * @param tagSetName - the name of the tag group
	 * @return the list of tag IDs
	 */
	public int[] getTags(String tagSetName) {
		return tagSetMap.get(tagSetName);
	}
	
	/**
	 * Reads the tag set file, and initializes the object.
	 * @param filePath - the file for morpheme tag set
	 * @throws IOException
	 */
	public void init(String filePath, int tagSetFlag) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
		String line = null;
		
		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		tagList.clear();
		irregularList.clear();
		tagSetMap.clear();
		
		ArrayList<Integer> tempTagNumbers = new ArrayList<Integer>();
		
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
			} else if ("TAG".equals(lineToken)) {
				tagList.add(lineTokenizer.nextToken());
			} else if ("TSET".equals(lineToken)) {
				String tagSetName = lineTokenizer.nextToken();
				StringTokenizer tagTokenizer = new StringTokenizer(lineTokenizer.nextToken(), " ");
				
				while (tagTokenizer.hasMoreTokens()) {
					String tagToken = tagTokenizer.nextToken();
					int tagNumber = tagList.indexOf(tagToken);
					
					if (tagNumber != -1) {
						tempTagNumbers.add(tagNumber);
					} else {
						int[] values = tagSetMap.get(tagToken);
						if (values != null) {
							for (int i = 0; i < values.length; i++) {
								tempTagNumbers.add(values[i]);
							}
						}
					}
				}
				int[] tagNumbers = new int[tempTagNumbers.size()];
				Iterator<Integer> iter = tempTagNumbers.iterator();
				for (int i = 0; iter.hasNext(); i++) {
					tagNumbers[i] = iter.next();
				}
				tagSetMap.put(tagSetName, tagNumbers);
				tempTagNumbers.clear();
				
			} else if ("IRR".equals(lineToken)) {
				irregularList.add(lineTokenizer.nextToken());
			}
		}
		br.close();
		
		setTagTypes(tagSetFlag);
		indexTags = tagSetMap.get("index");
		unkTags = tagSetMap.get("unkset");
		iwgTag = tagList.indexOf("iwg");
		unkTag = tagList.indexOf("unk");
		numTag = tagList.indexOf("nnc");
		
		IRR_TYPE_B = getIrregularID("irrb");
		IRR_TYPE_S = getIrregularID("irrs");
		IRR_TYPE_D = getIrregularID("irrd");
		IRR_TYPE_H = getIrregularID("irrh");
		IRR_TYPE_REU = getIrregularID("irrlu");
		IRR_TYPE_REO = getIrregularID("irrle");
	}

	/**
	 * Sets the tag types.
	 * @param tagSetFlag - the flag for tag set (TAG_SET_KAIST, ..)
	 */
	public void setTagTypes(int tagSetFlag) {
		if (tagSetFlag == TAG_SET_KAIST) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			int[] values = null;
			Iterator<Integer> iter = null;

			// verb
			values = tagSetMap.get("pv");
			for (int i = 0; i < values.length; i++) {
				list.add(values[i]);
			}
			values = tagSetMap.get("xsm");
			for (int i = 0; i < values.length; i++) {
				list.add(values[i]);
			}
			list.add(tagList.indexOf("px"));
			tagTypeTable[TAG_TYPE_VERBS] = new int[list.size()];
			iter = list.iterator();
			for (int i = 0; iter.hasNext(); i++) {
				tagTypeTable[TAG_TYPE_VERBS][i] = iter.next(); 
			}
			list.clear();

			// noun
			tagTypeTable[TAG_TYPE_NOUNS] = tagSetMap.get("n");
			
			// nps
			tagTypeTable[TAG_TYPE_NPS] = tagSetMap.get("np");
			
			// adjs
			tagTypeTable[TAG_TYPE_ADJS] = tagSetMap.get("pa");
			
			// eomies
			tagTypeTable[TAG_TYPE_EOMIES] = tagSetMap.get("e");
			
			// yongs
			values = tagSetMap.get("p");
			for (int i = 0; i < values.length; i++) {
				list.add(values[i]);
			}
			values = tagSetMap.get("xsv");
			for (int i = 0; i < values.length; i++) {
				list.add(values[i]);
			}
			values = tagSetMap.get("xsm");
			for (int i = 0; i < values.length; i++) {
				list.add(values[i]);
			}
			list.add(tagList.indexOf("ep"));
			list.add(tagList.indexOf("jp"));
			
			tagTypeTable[TAG_TYPE_YONGS] = new int[list.size()];
			iter = list.iterator();
			for (int i = 0; iter.hasNext(); i++) {
				tagTypeTable[TAG_TYPE_YONGS][i] = iter.next(); 
			}
			list.clear();
			
			// jp
			tagTypeTable[TAG_TYPE_JP] = new int[1];
			tagTypeTable[TAG_TYPE_JP][0] = tagList.indexOf("jp");
			
			// nbnp
			tagTypeTable[TAG_TYPE_NBNP] = new int[3];
			tagTypeTable[TAG_TYPE_NBNP][0] = tagList.indexOf("nbn");
			tagTypeTable[TAG_TYPE_NBNP][1] = tagList.indexOf("npd");
			tagTypeTable[TAG_TYPE_NBNP][2] = tagList.indexOf("npp");
			
			// josa
			tagTypeTable[TAG_TYPE_JOSA] = new int[6];
			tagTypeTable[TAG_TYPE_JOSA][0] = tagList.indexOf("jxc");
			tagTypeTable[TAG_TYPE_JOSA][0] = tagList.indexOf("jco");
			tagTypeTable[TAG_TYPE_JOSA][0] = tagList.indexOf("jca");
			tagTypeTable[TAG_TYPE_JOSA][0] = tagList.indexOf("jcm");
			tagTypeTable[TAG_TYPE_JOSA][0] = tagList.indexOf("jcs");
			tagTypeTable[TAG_TYPE_JOSA][0] = tagList.indexOf("jcc");
		}
	}
}
