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

package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.SimpleMAResult22;

import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.MorphemeProcessor;
import kr.ac.kaist.swrc.jhannanum.share.TagMapper;

/**
 * This plug-in changes the detailed morphological analysis results to be simple. The KAIST tag set has
 * 69 morpheme tags but this plug-in uses 22 tags:<br>
 * <br>
 * NC(보통명사), NQ(고유명사), NB(의존명사), NP(대명사), NN(수사)<br>
 * PV(동사), PA(형용사), PX(보조용언)<br>
 * MM(관형사), MA(부사)<br>
 * II(감탄사)<br>
 * JC(격조사), JX(보조사), JP(서술격조사)<br>
 * EP(선얼말어미), EC(연결어미), ET(전성어미), EF(종결어미)<br>
 * XP(접두사), XS(접미사)<br>
 * S(기호)<br>
 * F(외국어)<br>
 * <br>
 * Note: This plug-in is not compatible with HmmPosTagger.<br>
 * <br>
 * It is a morpheme processor plug-in which is a supplement plug-in of phase 2 in HanNanum work flow.<br>
 * <br>
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class SimpleMAResult22 implements MorphemeProcessor {
	/** the level of analysis */
	final private int TAG_LEVEL = 2;
	
	/** hash map to remove duplicates */
	private HashMap<String,Eojeol> dupFilterMap = null;
	
	/** temporary list for new tags */
	private ArrayList<String> tagList = null;
	
	/** temporary list for morpheme tags */
	private ArrayList<String> morphemeList = null;
	
	/**
	 * Constructor.
	 */
	public SimpleMAResult22() {
		dupFilterMap = new HashMap<String,Eojeol>();
		tagList = new ArrayList<String>();
		morphemeList = new ArrayList<String>();
	}
	
	/**
	 * It changes the morphological analysis result with 69 KAIST tags to the simplified result with 22 tags.
	 * @param sos - the result of morphological analysis where each eojeol has more than analysis result
	 * @return the simplified morphological analysis result
	 */
	@Override
	public SetOfSentences doProcess(SetOfSentences sos) {
		ArrayList<Eojeol[]> eojeolSetArray = sos.getEojeolSetArray();
		ArrayList<Eojeol[]> resultSetArray = new ArrayList<Eojeol[]>();
		
		int len = eojeolSetArray.size();
		String prevTag = null;
		boolean changed = false;
		
		for (int pos = 0; pos < len; pos++) {
			Eojeol[] eojeolSet = eojeolSetArray.get(pos);
			dupFilterMap.clear();
			
			for (int i = 0; i < eojeolSet.length; i++) {
				String[] tags = eojeolSet[i].getTags();
				prevTag = "";
				changed = false;
				
				for (int j = 0; j < tags.length; j++) {
					tags[j] = TagMapper.getKaistTagOnLevel(tags[j], TAG_LEVEL);
					
					if (tags[j].equals(prevTag)) {
						changed = true;
					}
					prevTag = tags[j];
				}
				
				if (changed) {
					tagList.clear();
					morphemeList.clear();
					String[] morphemes = eojeolSet[i].getMorphemes();
					
					for (int j = 0; j < tags.length - 1; j++) {
						if (tags[j].equals(tags[j+1])) {
							morphemes[j+1] = morphemes[j] + morphemes[j+1];
						} else {
							tagList.add(tags[j]);
							morphemeList.add(morphemes[j]);
						}
					}
					tagList.add(tags[tags.length - 1]);
					morphemeList.add(morphemes[morphemes.length - 1]);
				
					eojeolSet[i] = new Eojeol(morphemeList.toArray(new String[0]), tagList.toArray(new String[0]));
				}
				
				String key = eojeolSet[i].toString();
				if (!dupFilterMap.containsKey(key)) {
					dupFilterMap.put(key, eojeolSet[i]);
				}
			}
			if (eojeolSet.length != dupFilterMap.size()) {
				resultSetArray.add(dupFilterMap.values().toArray(new Eojeol[0]));
			} else {
				resultSetArray.add(eojeolSet);
			}
		}
		
		sos.setEojeolSetArray(resultSetArray);
		return sos;
	}

	@Override
	public void initialize(String baseDir, String configFile) throws Exception {

	}

	@Override
	public void shutdown() {

	}
}
