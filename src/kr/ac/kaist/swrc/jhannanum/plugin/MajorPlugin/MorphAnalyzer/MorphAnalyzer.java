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

package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer;

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.Plugin;

/**
 * The plug-in interface is for morphological analysis
 * 
 * - Phase: The Second Phase
 * - Type: Major Plug-in
 * 
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public interface MorphAnalyzer extends Plugin {
	/**
	 * It performs morphological analysis on the specified plain sentence, and returns the all analysis result where
	 * each plain eojeol has more than one morphologically analyzed eojeol.
	 * @param ps - the plain sentence to be morphologically analyzed
	 * @return - the set of eojeols where each eojeol has at least one morphological analysis result
	 */
	abstract public SetOfSentences morphAnalyze(PlainSentence ps);
}
