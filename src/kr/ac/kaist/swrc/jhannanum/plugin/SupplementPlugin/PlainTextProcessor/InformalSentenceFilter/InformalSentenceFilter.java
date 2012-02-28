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

package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.InformalSentenceFilter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.PlainTextProcessor;

/**
 * This plug-in filters informal sentences in which an eojeol is quite long and some characters were
 * repeated many times. These informal patterns occur poor performance of morphological analysis
 * so this plug-in should be used in HanNanum work flow which will analyze documents with informal sentences.
 * 
 * It is a Plain Text Processor plug-in which is a supplement plug-in of phase 1 in HanNanum work flow.
 * 
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class InformalSentenceFilter implements PlainTextProcessor {
	/** the maximum number of repetition of a character allowed */
	final static private int REPEAT_CHAR_ALLOWED = 5;

	/**
	 * It recognizes informal sentences in which an eojeol is quite long and some characters were
	 * repeated many times. To prevent decrease of analysis performance because of those unimportant
	 * irregular pattern, it inserts some blanks in those eojeols to seperate them.
	 */
	@Override
	public PlainSentence doProcess(PlainSentence ps) {
		String word = null;
		StringBuffer buf = new StringBuffer();
		StringTokenizer st = new StringTokenizer(ps.getSentence(), " \t");

		while (st.hasMoreTokens()) {
			word = st.nextToken();

			/* repeated character */
			if (word.length() > REPEAT_CHAR_ALLOWED) {
				char[] wordArray = word.toCharArray();
				int repeatCnt = 0;
				char checkChar = wordArray[0];

				buf.append(checkChar);

				for (int i = 1; i < wordArray.length; i++) {
					if (checkChar == wordArray[i]) {
						if (repeatCnt == REPEAT_CHAR_ALLOWED - 1) {
							buf.append(' ');
							buf.append(wordArray[i]);
							repeatCnt = 0;
						} else {
							buf.append(wordArray[i]);
							repeatCnt++;
						}
					} else {
						if (checkChar == '.') {
							buf.append(' ');
						}
						buf.append(wordArray[i]);
						checkChar = wordArray[i];
						repeatCnt = 0;
					}
				}
			} else {
				buf.append(word);
			}
			buf.append(' ');
		}
		ps.setSentence(buf.toString());
		return ps;
	}


	@Override
	public void initialize(String baseDir, String configFile) throws FileNotFoundException, IOException {

	}


	@Override
	public PlainSentence flush() {
		return null;
	}


	@Override
	public void shutdown() {

	}

	@Override
	public boolean hasRemainingData() {
		return false;
	}
}
