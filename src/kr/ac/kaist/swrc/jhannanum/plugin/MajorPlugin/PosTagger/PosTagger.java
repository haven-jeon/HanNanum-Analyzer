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

package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger;

import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.Plugin;

/**
 * The plug-in interface is for Part Of Speech Tagger.
 * 
 * - Phase: The Third Phase
 * - Type: Major Plug-in
 * 
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public interface PosTagger extends Plugin {
	/**
	 * It performs POS tagging, which selects the most promising morphological analysis result of each eojeol,
	 * so that the final result is the morphologically analyzed sentence with the highest probability.
	 * @param sos - the result morphological analysis where each eojeol has more than one candidate of analysis
	 * @return the morphologically analyzed sentence with the highest probability
	 */
	abstract Sentence tagPOS(SetOfSentences sos);
}
