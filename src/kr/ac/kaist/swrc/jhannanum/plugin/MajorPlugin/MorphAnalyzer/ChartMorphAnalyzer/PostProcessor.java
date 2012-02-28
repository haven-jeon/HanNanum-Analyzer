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
import java.util.Iterator;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.share.Code;

/**
 * This class is for post processing of morphological analysis.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class PostProcessor {
	/** the triple character representation of '하' */
	private String HA = null;
	
	/** the triple character representation of '아' */
	private String AR = null;
	
	/** the triple character representation of '어' */
	private String A_ = null;
	
	/** the triple character representation of 'ㅏㅑㅗ' */
	private String PV = null;
	
	/** the triple character representation of '끄뜨쓰크트' */
	private String XEU = null;
	
	/** the triple character representation of '돕' */
	private String DOB = null;
	
	/** the triple character representation of '곱' */
	private String GOB = null;
	
	/** the triple character representation of '으' */
	private String EU = null;
	
	/** the triple character representation of '습니' */
	private String SU = null;
	
	/** the triple character representation of '는다' */
	private String NU = null;

	/**
	 * Constructor.
	 */
	public PostProcessor() {
		HA = Code.toTripleString("하");
		AR = Code.toTripleString("아");
		A_ = Code.toTripleString("어");
		PV = Code.toTripleString("ㅏㅑㅗ");
		XEU = Code.toTripleString("끄뜨쓰크트");
		DOB = Code.toTripleString("돕");
		GOB = Code.toTripleString("곱");
		EU = Code.toTripleString("으");
		SU = Code.toTripleString("습니");
		NU = Code.toTripleString("는다");
	}
	
	/**
	 * It does post processing of morphological analysis to deal with some exceptions.
	 * @param sos - the result of morphological analysis
	 * @return the result of morphological analysis with post processing
	 */
	public SetOfSentences doPostProcessing(SetOfSentences sos) {
		ArrayList<Eojeol[]> eojeolSetArray = sos.getEojeolSetArray();
		Iterator<Eojeol[]> iter = eojeolSetArray.iterator();
		
		while (iter.hasNext()) {
			Eojeol[] eojeolSet = iter.next();
			String prevMorph = "";
			
			for (int i = 0; i < eojeolSet.length; i++) {
				Eojeol eojeol = eojeolSet[i];
				String[] morphemes = eojeol.getMorphemes();
				String[] tags = eojeol.getTags();
				
				for (int j = 0; j < eojeol.length; j++) {
					String tri = Code.toTripleString(morphemes[j]);
					if (tags[j].startsWith("e")) {
						int prevLen = prevMorph.length();
						
						if (tri.startsWith(A_)) {		/* 어 -> 아 */
							if (prevLen >= 4 && prevMorph.charAt(prevLen-1) == EU.charAt(1) && !isXEU(prevMorph.charAt(prevLen-2)) && ((Code.isJungseong(prevMorph.charAt(prevLen-3)) && isPV(prevMorph.charAt(prevLen-3))) || (Code.isJongseong(prevMorph.charAt(prevLen-3)) && isPV(prevMorph.charAt(prevLen-4))))) {
								morphemes[j] = Code.toString(AR.toCharArray());
							} else if (prevLen >= 3 && prevMorph.charAt(prevLen-1) == DOB.charAt(2) && (prevMorph.substring(prevLen-3).equals(DOB) == false || prevMorph.substring(prevLen-3).equals(GOB) == false)) {
								/* for 'ㅂ' irregular */
							} else if (prevLen>=2 && prevMorph.substring(prevLen-2).equals(HA)) {
							} else if (prevLen>=2 && ( (Code.isJungseong(prevMorph.charAt(prevLen-1)) && isPV(prevMorph.charAt(prevLen-1))) || (Code.isJongseong(prevMorph.charAt(prevLen-1)) && isPV(prevMorph.charAt(prevLen-2))) )) {	// final consonant or not
								morphemes[j] = Code.toString(AR.toCharArray());
							}
						} else if (tri.startsWith(EU.substring(0, 2)) || tri.startsWith(SU.substring(0, 4)) || tri.startsWith(NU.substring(0, 4))) {
							/* elision of '으', '스', '느' */
							if (prevLen >= 2 && (Code.isJungseong(prevMorph.charAt(prevLen-1)) || prevMorph.charAt(prevLen-1) == 0x11AF)) {
								morphemes[j] = Code.toString(tri.substring(2).toCharArray());
							}
						}
					}
					
					prevMorph = Code.toTripleString(morphemes[j]);
				}
			}
		}

		return sos;
	}

	/**
	 * Checks whether the specified character is one of 'ㅏ', 'ㅑ', 'ㅗ'.
	 * @param c - the character to check
	 * @return true: the character is one of 'ㅏ', 'ㅑ', 'ㅗ', false: not one of the characters
	 */
	private boolean isPV(char c) {
		if (PV.indexOf(c) == -1) {
			return false;
		}
		return true;
	}

	/**
	 * Checks whether the specified character is one of '끄', '뜨', '쓰', '크', '트'.
	 * @param c - the character to check
	 * @return true: the character is one of '끄', '뜨', '쓰', '크', '트', false: not one of the characters
	 */
	private boolean isXEU(char c) {
		if (XEU.indexOf(c) == -1) {
			return false;
		}
		return true;
	}
}
