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

import java.util.ArrayList;
import java.util.LinkedList;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer.SegmentPosition.Position;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer.Trie.INFO;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer.Trie.TNODE;
import kr.ac.kaist.swrc.jhannanum.share.Code;
import kr.ac.kaist.swrc.jhannanum.share.TagSet;

/**
 * This class is for the lattice style morpheme chart which is a internal data structure for morphological analysis without backtracking.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class MorphemeChart {
	/**
	 * A morpheme node in the lattice style chart.
	 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
	 */
	public class Morpheme {
		/** morpheme tag */
		public int tag;
		
		/** phoneme */
		public int phoneme;
		
		/** the index of the next node */
		public int nextPosition;
		
		/** the type of the next morpheme */
		public int nextTagType;
		
		/** the state of current processing */
		public int state;
		
		/** the number of morphemes connected */
		public int connectionCount;
		
		/** the list of the morphemes connected */
		public int[] connection = new int[MAX_MORPHEME_CONNECTION];
		
		/** plain string */
		public String str = "";
	}
	
	/** the reserved word for replacement of Chinese characters */
	final static private String CHI_REPLACE = "HAN_CHI";
	
	/** the reserved word for replacement of English alphabets */
	final static private String ENG_REPLACE = "HAN_ENG";
	
	/** the list for the replacement of Chinese character */
	private LinkedList<String> chiReplacementList = null;
	
	/** the list for the replacement of English alphabets */
	private LinkedList<String> engReplacementList = null;
	
	/** the index for replacement of English alphabets */
	private int engReplaceIndex = 0;
	
	/** the index for replacement of Chinese characters */
	private int chiReplaceIndex = 0;
	
	/** the maximum number of connections between one morpheme and others */
	final private static int MAX_MORPHEME_CONNECTION = 30;
	
	/** the maximum number of morpheme nodes in the chart */
	final private static int MAX_MORPHEME_CHART = 2046;
	
	/** the processing state - incomplete */
	final private static int MORPHEME_STATE_INCOMPLETE = 2;
	
	/** the processing state - success */
	final private static int MORPHEME_STATE_SUCCESS = 1;

	/** the maximum number of analysis results */
	final private static int MAX_CANDIDATE_NUM = 100000;
	
	/** the processing state - fail */
	final private static int MORPHEME_STATE_FAIL = 0;
	
	/** the morpheme chart */
	public Morpheme[] chart = null;
	
	/** the last index of the chart */
	public int chartEnd = 0;
	
	/** the morpheme tag set */
	private TagSet tagSet = null;
	
	/** the connection rules */
	private Connection connection = null;
	
	/** segment position */
	private SegmentPosition sp = null;
	
	/** string buffer */
	private String bufString = "";
	
	/** path of segmentation */
	private int[] segmentPath = new int[SegmentPosition.MAX_SEGMENT];
	
	/** chart expansion */
	private Exp exp = null;
	
	/** system morpheme dictionary */
	private Trie systemDic = null;
	
	/** user morpheme dictionary */
	private Trie userDic = null;
	
	/** number dictionary - automata */
	private NumberDic numDic = null;
	
	/** SIMple Trie Index */
	private Simti simti = null;
	
	/** the number of analysis results printed */
	private int printResultCnt = 0;

	/** the list of eojeols analyzed */
	private LinkedList<Eojeol> resEojeols = null;
	
	/** the list of morphemes analyzed */
	private ArrayList<String> resMorphemes = null;
	
	/** the list of morpheme tags analyzed */
	private ArrayList<String> resTags = null;
	
	/**
	 * Constructor.
	 * @param tagSet - the morpheme tag set
	 * @param connection - the morpheme connection rules
	 * @param systemDic - the system morpheme dictionary
	 * @param userDic - the user morpheme dictionary
	 * @param numDic - the number dictionary
	 * @param simti - the SIMple Trie Index
	 * @param resEojeolList - the list of eojeols to store the analysis result
	 */
	public MorphemeChart(TagSet tagSet, Connection connection, Trie systemDic, Trie userDic, NumberDic numDic, Simti simti, LinkedList<Eojeol> resEojeolList) {
		chart = new Morpheme[MAX_MORPHEME_CHART];
		for (int i = 0; i < MAX_MORPHEME_CHART; i++) {
			chart[i] = new Morpheme();
		}
		
		this.sp = new SegmentPosition();
		this.tagSet = tagSet;
		this.connection = connection;
		this.exp = new Exp(this, tagSet);
		this.systemDic = systemDic;
		this.userDic = userDic;
		this.numDic = numDic;
		this.simti = simti;
		this.resEojeols = resEojeolList;
		
		resMorphemes = new ArrayList<String>();
		resTags = new ArrayList<String>();
		
		chiReplacementList = new LinkedList<String>();
		engReplacementList = new LinkedList<String>();
	}
	
	/**
	 * Adds a new morpheme to the chart.
	 * @param tag - the morpheme tag ID
	 * @param phoneme - phoneme
	 * @param nextPosition - the index of next morpheme
	 * @param nextTagType - the tag type of next morpheme
	 * @return the last index of the chart
	 */
	public int addMorpheme(int tag, int phoneme, int nextPosition, int nextTagType) {
		chart[chartEnd].tag = tag;
		chart[chartEnd].phoneme = phoneme;
		chart[chartEnd].nextPosition = nextPosition;
		chart[chartEnd].nextTagType = nextTagType;
		chart[chartEnd].state = MORPHEME_STATE_INCOMPLETE;
		chart[chartEnd].connectionCount = 0;
		return chartEnd++;
	}
	
	/**
	 * It inserts the reverse of the given string to the SIMTI data structure.
	 * @param str - string to insert to the SIMTI structure
	 * @return the index of the next morpheme
	 */
	public int altSegment(String str) {
		int prev = 0;
		int next = 0;
		int match;
		int len;
		int to;
		
		len = str.length();
		
		String rev = "";
		for (int i = len - 1; i >= 0; i--) {
			rev += str.charAt(i);
		}
		
		char[] revStrArray = rev.toCharArray();
		
		match = simti.search(revStrArray);
		to = simti.fetch(rev.substring(0, match).toCharArray());
		
		for (int i = 0; i < str.length(); i++) {
			if (len <= match) {
				break;
			}
			next = sp.addPosition(str.charAt(i));
			if (prev != 0) {
				sp.setPositionLink(prev, next);
			}
			
			simti.insert(rev.substring(0, len).toCharArray(), next);
			prev = next;
			len--;
		}
		
		if (prev != 0) {
			sp.setPositionLink(prev, to);
		}
		
		return simti.fetch(revStrArray);
	}
	
	/**
	 * It performs morphological analysis on the morpheme chart constructed.
	 * @return the number of analysis results
	 */
	public int analyze() {
		int res = 0;
		
		res = analyze(0, TagSet.TAG_TYPE_ALL);

		if (res > 0) {
			return res;
		} else {
			return analyzeUnknown();
		}
	}
	
	/**
	 * It performs morphological anlysis on the morpheme chart from the specified index in the chart.
	 * @param chartIndex - the index of the chart to analyze
	 * @param tagType - the type of next morpheme
	 * @return the number of analysis results
	 */
	private int analyze(int chartIndex, int tagType) {
		int from, to;
		int i,j,x, y;
		int mp;
		char c;
		int nc_idx;
		TNODE node;
		LinkedList<INFO> infoList = null;
		INFO info = null;
		
		int sidx = 1;
		int uidx = 1;
		int nidx = 1;
		Position fromPos = null;
		Position toPos = null;
		Morpheme morph = chart[chartIndex]; 
		from = morph.nextPosition;
		fromPos = sp.getPosition(from);
		
		switch(sp.getPosition(from).state) {
		default:
			return 0;
		
		/* dictionary search */
		case SegmentPosition.SP_STATE_N:
			i = 0;
			bufString = "";
			
			// searches all combinations of words segmented through the dictionaries
			for (to = from; to != SegmentPosition.POSITION_START_KEY; to = sp.nextPosition(to)) {
				toPos = sp.getPosition(to);
				c = toPos.key;
				
				if (sidx != 0) {
					sidx = systemDic.node_look(c, sidx);
				}
				if (uidx != 0) {
					uidx = userDic.node_look(c, uidx);
				}
				if (nidx != 0) {
					nidx = numDic.node_look(c, nidx);
				}
				
				toPos.sIndex = sidx;
				toPos.uIndex = uidx;
				toPos.nIndex = nidx;
				
				bufString += c;
				segmentPath[i++] = to;
			}
			
			nidx = 0;
			
			for ( ; i > 0; i--) {
				to = segmentPath[i-1];
				toPos = sp.getPosition(to);

				// system dictionary
				if (toPos.sIndex != 0) {
					node = systemDic.get_node(toPos.sIndex);
					if ((infoList = node.info_list) != null) {
						for (j = 0; j < infoList.size(); j++) {
							info = infoList.get(j);

							nc_idx = addMorpheme(info.tag, info.phoneme, sp.nextPosition(to), 0);
							chart[nc_idx].str = bufString.substring(0, i);
							fromPos.morpheme[fromPos.morphCount++] = nc_idx;
						}
					}
				}
				
				// user dictionary
				if (toPos.uIndex != 0) {
					node = userDic.get_node(toPos.uIndex);
					if ((infoList = node.info_list) != null) {
						for (j = 0; j < infoList.size(); j++) {
							info = infoList.get(j);
							nc_idx = addMorpheme(info.tag, info.phoneme, sp.nextPosition(to), 0);
							chart[nc_idx].str = bufString.substring(0, i);
							fromPos.morpheme[fromPos.morphCount++] = nc_idx;
						}
					}
				}
				
				// number dictionary
				if (nidx == 0 && toPos.nIndex != 0) {
					if (numDic.isNum(toPos.nIndex)) {
						nc_idx = addMorpheme(tagSet.numTag, TagSet.PHONEME_TYPE_ALL, sp.nextPosition(to), 0);
						chart[nc_idx].str = bufString.substring(0, i);
						fromPos.morpheme[fromPos.morphCount++] = nc_idx;
						nidx = toPos.nIndex;
					} else {
						nidx = 0;
					}
				}
			}
			
			fromPos.state = SegmentPosition.SP_STATE_D; 

		/* chart expansion regarding various rules */
		case SegmentPosition.SP_STATE_D:
			exp.prule(from, morph.str, bufString, sp);
			sp.getPosition(from).state = SegmentPosition.SP_STATE_R;
			
		/* recursive processing */
		case SegmentPosition.SP_STATE_R:
			x = 0;
			for (i = 0; i < fromPos.morphCount; i++) {
				mp = fromPos.morpheme[i];
				
				// It prevents a recursive call for '습니다', which needs to be improved.
				if (tagSet.checkTagType(tagType, chart[mp].tag) == false) {
					continue;
				}
				
				// It prevents some redundant processing
				if (chart[mp].state == MORPHEME_STATE_INCOMPLETE) {
					y = analyze(mp, chart[mp].nextTagType);
					x += y;
					
					if (y != 0) {
						chart[mp].state = MORPHEME_STATE_SUCCESS;
					} else {
						chart[mp].state = MORPHEME_STATE_FAIL;
					}
				}
				else {
					x += chart[mp].connectionCount;
				}
			}
			
			if (x == 0) {
				if (tagType == TagSet.TAG_TYPE_ALL) {
					fromPos.state = SegmentPosition.SP_STATE_F;
				}
				return 0;
			}
			
			if (tagType == TagSet.TAG_TYPE_ALL) {
				fromPos.state = SegmentPosition.SP_STATE_M;
			}

		/* connecton rule */
		case SegmentPosition.SP_STATE_M:
			for (i = 0; i < fromPos.morphCount; i++) {
				mp = fromPos.morpheme[i];
				
				if (chart[mp].state == MORPHEME_STATE_SUCCESS &&
					connection.checkConnection(
							tagSet,
							morph.tag,
							chart[mp].tag,
							morph.str.length(),
							chart[mp].str.length(),
							morph.nextTagType)) {
					morph.connection[morph.connectionCount++] = mp;
				}
			}
		}
		return morph.connectionCount;
	}

	/**
	 * It segments all phonemes, and tags 'unknown' to each segment, and then performs chart analysis,
	 * so that the eojeols that consist of morphems not in the dictionaries can be processed.
	 * @return the number of analysis results
	 */
	private int analyzeUnknown() {
		int i;
		int nc_idx;
		
		bufString = "";
		
		Position pos_1 = sp.getPosition(1);
		
		for (i = 1; i != 0; i = sp.nextPosition(i)) {
			Position pos = sp.getPosition(i);

			bufString += pos.key;
			
//			if (Code.isChoseong(pos.key)) {
//				continue;
//			}
			
			nc_idx = addMorpheme(tagSet.unkTag, TagSet.PHONEME_TYPE_ALL, sp.nextPosition(i), TagSet.TAG_TYPE_ALL);
			chart[nc_idx].str = bufString;
			
			pos_1.morpheme[pos_1.morphCount++] = nc_idx;
			pos_1.state = SegmentPosition.SP_STATE_R;
		}
		
		chart[0].connectionCount = 0;
		
		return analyze(0, 0);
	}
	
	/**
	 * Checks the specified morpheme is exist in the morpheme chart.
	 * @param morpheme - the list of indices of the morphemes to check
	 * @param morphemeLen - the length of the list
	 * @param tag - morpheme tag ID
	 * @param phoneme - phoneme	
	 * @param nextPosition - the index of the next morpheme
	 * @param nextTagType - the type of the next morpheme tag
	 * @param str - plain string
	 * @return true: the morpheme is in the chart, false: not exist
	 */
	public boolean checkChart(int[] morpheme, int morphemeLen, int tag, int phoneme, int nextPosition, int nextTagType, String str) {
		for (int i = 0; i < morphemeLen; i++) {
			Morpheme morph = chart[morpheme[i]];
			if (morph.tag == tag &&
					morph.phoneme == phoneme &&
					morph.nextPosition == nextPosition &&
					morph.nextTagType == nextTagType &&
					morph.str.equals(str)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Generates the morphological analysis result based on the morpheme chart where the analysis is performed.
	 */
	public void getResult() {
		
		printResultCnt = 0;
		printChart(0);
	}

	/**
	 * Initializes the morpheme chart with the specified word.
	 * @param word - the plain string of an eojeol to analyze
	 */
	public void init(String word) {
		simti.init();
		word = preReplace(word);
		sp.init(Code.toTripleString(word), simti);
		
		chartEnd = 0;
		Position p = sp.getPosition(0);
		p.morpheme[p.morphCount++] = chartEnd;
		chart[chartEnd].tag = tagSet.iwgTag;
		chart[chartEnd].phoneme = 0;
		chart[chartEnd].nextPosition = 1;
		chart[chartEnd].nextTagType = 0;
		chart[chartEnd].state = MORPHEME_STATE_SUCCESS;
		chart[chartEnd].connectionCount = 0;
		chart[chartEnd].str = "";
		chartEnd++;
	}
	
	/**
	 * It expands the morpheme chart to deal with the phoneme change phenomenon.
	 * @param from - the index of the start segment position
	 * @param front - the front part of the string
	 * @param back - the next part of the string
	 * @param ftag - the morpheme tag of the front part
	 * @param btag - the morpheme tag of the next part
	 * @param phoneme - phoneme
	 */
	public void phonemeChange(int from, String front, String back, int ftag, int btag, int phoneme) {
		TNODE node = null;
		int size = 0;
		boolean x, y;
		int next;
		int nc_idx;
		
		// searches the system dictionary for the front part
		node = systemDic.fetch(front.toCharArray());
		if (node != null && node.info_list != null) {
			size = node.info_list.size();
		}
		
		Position pos = sp.getPosition(from);
		
		for (int i = 0; i < size; i++) {
			INFO info = node.info_list.get(i);

			// comparison of the morpheme tag of the front part
			x = tagSet.checkTagType(ftag, info.tag);
			
			// comparison of the phoneme of the front part
			y = tagSet.checkPhonemeType(phoneme, info.phoneme);
			
			if (x && y) {
				next = altSegment(back);
				
				if (checkChart(pos.morpheme, pos.morphCount, info.tag, info.phoneme, next, btag, front) == false) {
					nc_idx = addMorpheme(info.tag, info.phoneme, next, btag);
					chart[nc_idx].str = front;
					pos.morpheme[pos.morphCount++] = nc_idx;
				} else {
					System.err.println("phonemeChange: exit");
					System.exit(0);
				}
			}
		}
	}
	
	/**
	 * It generates the final mophological analysis result from the morpheme chart.
	 * @param chartIndex - the start index of the chart to generate final result
	 */
	private void printChart(int chartIndex) {
		int i;
		Morpheme morph = chart[chartIndex];
		int engCnt = 0;
		int chiCnt = 0;

		if (chartIndex == 0) {
			for (i = 0; i < morph.connectionCount; i++) {
				resMorphemes.clear();
				resTags.clear();
				printChart(morph.connection[i]);
			}
		} else {
			String morphStr = Code.toString(morph.str.toCharArray());
			int idx = 0;
			engCnt = 0;
			chiCnt = 0;
			while (idx != -1) {
				if ((idx = morphStr.indexOf(ENG_REPLACE)) != -1) {
					engCnt++;
					morphStr = morphStr.replaceFirst(ENG_REPLACE, engReplacementList.get(engReplaceIndex++));
				} else if ((idx = morphStr.indexOf(CHI_REPLACE)) != -1) {
					chiCnt++;
					morphStr = morphStr.replaceFirst(CHI_REPLACE, chiReplacementList.get(chiReplaceIndex++));
				}
			}
			
			resMorphemes.add(morphStr);
			resTags.add(tagSet.getTagName(morph.tag));

			for (i = 0; i < morph.connectionCount && printResultCnt < MAX_CANDIDATE_NUM; i++) {
				if (morph.connection[i] == 0) {
					String[] mArray = resMorphemes.toArray(new String[0]);
					String[] tArray = resTags.toArray(new String[0]);
					resEojeols.add(new Eojeol(mArray, tArray));
					
					printResultCnt++;
				} else {
					printChart(morph.connection[i]);
				}
			}
			
			resMorphemes.remove(resMorphemes.size() - 1);
			resTags.remove(resTags.size() - 1);
			if (engCnt > 0) {
				engReplaceIndex -= engCnt;
			}
			if (chiCnt > 0) {
				chiReplaceIndex -= chiCnt;
			}
		}
	}
	
	/**
	 * It prints the all data in the chart to the console.
	 */
	public void printMorphemeAll() {
		 System.err.println("chartEnd: " + chartEnd);
		for (int i = 0; i < chartEnd; i++) {
			System.err.println("chartID: " + i);
			System.err.format("%s/%s.%s nextPosition=%c nextTagType=%s state=%d ",
					Code.toString(chart[i].str.toCharArray()),
					tagSet.getTagName(chart[i].tag),
					tagSet.getIrregularName(chart[i].phoneme),
					Code.toCompatibilityJamo(sp.getPosition(chart[i].nextPosition).key),
					tagSet.getTagName(chart[i].nextTagType),
					chart[i].state);
			System.err.print("connection=");
			for (int j = 0; j < chart[i].connectionCount; j++) {
				 System.err.print(chart[i].connection[j] + ", ");
			}
			System.err.println();
		}
	}
	
	/**
	 * Replaces the English alphabets and Chinese characters in the specified string with the reserved words.
	 * @param str - the string to replace English and Chinese characters
	 * @return the string in which English and Chinese characters were replace with the reserved words
	 */
	private String preReplace(String str) {
		String result = "";
		boolean engFlag = false;
		boolean chiFlag = false;
		String buf = "";
		
		engReplacementList.clear();
		chiReplacementList.clear();
		engReplaceIndex = 0;
		chiReplaceIndex = 0;

		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);

			if (((c >= 'a' && c <= 'z') || c >= 'A' && c <= 'Z')) {
				/* English Alphabets */
				if (engFlag) {
					buf += c;
				} else {
					if (engFlag) {
						engFlag = false;
						engReplacementList.add(buf);
						buf = "";
					}
					result += ENG_REPLACE;
					buf += c;
					engFlag = true;
				}

			} else if (((c >= 0x2E80 && c <= 0x2EFF) || (c >= 0x3400 && c <= 0x4DBF)) || (c >= 0x4E00 && c < 0x9FBF) ||
					(c >= 0xF900 && c <= 0xFAFF) && chiFlag) {
				/* Chinese Characters */
				if (chiFlag) {
					buf += c;
				} else {
					if (chiFlag) {
						chiFlag = false;
						chiReplacementList.add(buf);
						buf = "";
					}
					result += CHI_REPLACE;
					buf += c;
					chiFlag = true;
				}
			} else {
				result += c;
				if (engFlag) {
					engFlag = false;
					engReplacementList.add(buf);
					buf = "";
				}
				if (chiFlag) {
					chiFlag = false;
					chiReplacementList.add(buf);
					buf = "";
				}
			}
		}
		if (engFlag) {
			engReplacementList.add(buf);
		}
		if (chiFlag) {
			chiReplacementList.add(buf);
		}
		return result;
	}
}
