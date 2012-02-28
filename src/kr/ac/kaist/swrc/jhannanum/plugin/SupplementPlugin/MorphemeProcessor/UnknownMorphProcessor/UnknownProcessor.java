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

package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.UnknownMorphProcessor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.MorphemeProcessor;

/**
 * This plug-in is for morphemes tagged with 'unk'. These morphemes can not be found in the morpheme dictionaries
 * so their POS tag was temporarily mapped with 'unknown'. The most of morphemes not registered in the dictionaries
 * can be expected to be noun with highly probability. So this plug-in maps the 'unk' tag to 'ncn' and 'nqq'.
 * 
 * It is a morpheme processor plug-in which is a supplement plug-in of phase 2 in HanNanum work flow.
 * 
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class UnknownProcessor implements MorphemeProcessor {
	@Override
	public SetOfSentences doProcess(SetOfSentences sos) {
		ArrayList<Eojeol[]> eojeolSetArray = sos.getEojeolSetArray();
		
		LinkedList<Eojeol> eojeolArray = new LinkedList<Eojeol>();
		
		for (int i = 0; i < eojeolSetArray.size(); i++) {
			Eojeol[] eojeolSet = eojeolSetArray.get(i);
			
			eojeolArray.clear();
			for (int j = 0; j < eojeolSet.length; j++) {
				eojeolArray.add(eojeolSet[j]);
			}
			
			int unkCount = 0;
			for (int j = 0; j < eojeolArray.size(); j++) {
				Eojeol eojeol = eojeolArray.get(j);
				String[] tags = eojeol.getTags();
				String[] morphemes = eojeol.getMorphemes();
				
				for (int k = 0; k < tags.length; k++) {
					if (tags[k].equals("unk")) {
						tags[k] = "nqq";
						
						Eojeol newEojeol = new Eojeol(morphemes.clone(), tags.clone());
						eojeolArray.add(newEojeol);
						
						tags[k] = "ncn";
						unkCount++;
					}
				}
			}
			
			if (unkCount > 0) {
				eojeolSetArray.set(i, eojeolArray.toArray(eojeolSet));
			}
		}
		
		return sos;
	}

	@Override
	public void initialize(String baseDir, String configFile) throws FileNotFoundException, IOException {
		
	}

	@Override
	public void shutdown() {
	}
}