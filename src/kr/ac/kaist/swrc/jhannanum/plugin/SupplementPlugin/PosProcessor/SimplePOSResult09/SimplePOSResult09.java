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

package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.SimplePOSResult09;

import java.util.ArrayList;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.PosProcessor;
import kr.ac.kaist.swrc.jhannanum.share.TagMapper;

/**
 * This plug-in changes the detailed POS tagging results to be simple. The KAIST tag set has
 * 69 morpheme tags but this plug-in uses only 9 tags: <br>
 * <br>
 * N(체언), P(용언), M(수식언), I(독립언), J(관계언), E(어미), X(접사), S(기호), F(외국어)<br>
 * <br>
 * It is a POS Processor plug-in which is a supplement plug-in of phase 3 in HanNanum work flow.<br>
 * <br>
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class SimplePOSResult09 implements PosProcessor {
	/** the level of analysis */
	final private int TAG_LEVEL = 1;
	
	/** temporary list for new tags */
	private ArrayList<String> tagList = null;
	
	/** temporary list for morpheme tags */
	private ArrayList<String> morphemeList = null;
	
	/**
	 * Constructor.
	 */
	public SimplePOSResult09() {
		tagList = new ArrayList<String>();
		morphemeList = new ArrayList<String>();
	}
	
	/**
	 * It changes the POS tagging result with 69 KAIST tags to the simplified result with 9 tags.
	 * @param st - the result of morphological analysis where each eojeol has more than analysis result
	 * @return the simplified POS tagging result
	 */
	@Override
	public Sentence doProcess(Sentence st) {
		String prevTag = null;
		boolean changed = false;
		
		Eojeol[] eojeolSet = st.getEojeols();
		
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
		}
		st.setEojeols(eojeolSet);
		
		return st;
	}

	@Override
	public void initialize(String baseDir, String configFile) throws Exception {

	}

	@Override
	public void shutdown() {

	}
}
