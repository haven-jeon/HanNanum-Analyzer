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

package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.NounExtractor;

import java.util.LinkedList;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.PosProcessor;

/**
 * This plug-in extracts the morphemes recognized as a noun after Part Of Speech tagging was done.
 * 
 * It is a POS Processor plug-in which is a supplement plug-in of phase 3 in HanNanum work flow.
 * 
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class NounExtractor implements PosProcessor {
	/** the buffer for noun morphemes */
	private LinkedList<String> nounMorphemes = null;
	
	/** the buffer for tags of the morphemes */
	private LinkedList<String> nounTags = null;
	

	@Override
	public void initialize(String baseDir, String configFile) throws Exception {
		nounMorphemes = new LinkedList<String>();
		nounTags = new LinkedList<String>();
	}

	@Override
	public void shutdown() {
		
	}

	/**
	 * It extracts the morphemes which were recognized as noun after POS tagging.
	 * @param st - the POS tagged sentence
	 * @return the sentence in which only nouns were remained
	 */
	@Override
	public Sentence doProcess(Sentence st) {
		Eojeol[] eojeols = st.getEojeols();
		
		for (int i = 0; i < eojeols.length; i++) {
			String[] morphemes = eojeols[i].getMorphemes();
			String[] tags = eojeols[i].getTags();
			nounMorphemes.clear();
			nounTags.clear();
			
			for (int j = 0; j < tags.length; j++) {
				char c = tags[j].charAt(0);
				if (c == 'n') {
					nounMorphemes.add(morphemes[j]);
					nounTags.add(tags[j]);
				} else if (c == 'f') {
					nounMorphemes.add(morphemes[j]);
					nounTags.add("ncn");
				}
			}
			
			eojeols[i].setMorphemes(nounMorphemes.toArray(new String[0]));
			eojeols[i].setTags(nounTags.toArray(new String[0]));
		}
		
		st.setEojeols(eojeols);
		
		return st;
	}
}
