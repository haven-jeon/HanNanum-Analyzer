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

package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.SentenceSegmentor;

import java.io.FileNotFoundException;
import java.io.IOException;

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.PlainTextProcessor;

/**
 * This plug-in reads a document which consists of more than one sentence, and recognize the end of each sentence
 * based on punctuation marks. So if punctuation marks were not used correctly in the sentences, this plug-in
 * will not work well. </br>
 * </br>
 * It considers '.', '!', '?' as the marks for the end of sentence, but these symbols can be used in other purpose,
 * so it deals with those problems. </br>
 * </br>
 * For example, </br>
 *  - 12.42 : number </br>
 *  - A. Introduction : section title </br>
 *  - I'm fine... : ellipsis </br>
 *  - U.S. : abbreviation </br>
 *  </br>
 * It is a Plain Text Processor plug-in which is a supplement plug-in of phase 1 in HanNanum work flow.
 * 
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class SentenceSegmentor implements PlainTextProcessor {
	/** the ID of the document */
	private int documentID = 0;
	
	/** the ID of the sentence */
	private int sentenceID = 0;
	
	/** the flag to check if there is remaining data in the input buffer */
	private boolean hasRemainingData = false;
	
	/** the buffer for storing intermediate results */
	private String bufRes = null;
	
	/** the buffer for storing the remaining part after one sentence returned */
	private String[] bufEojeols = null;
	
	/** the index of the buffer for storing the remaining part */
	private int bufEojeolsIdx = 0;
	
	/** the flag to check whether current sentence is the end of document */
	private boolean endOfDocument = false;

	/**
	 * Checks if the specified symbol can appear with previous symbols.
	 * @param c - the character to check
	 * @return true: if the character can come together with the previous symbols, false: not possible
	 */
	private boolean isSym(char c) {
		switch (c) {
		case ')': return true;
		case ']': return true;
		case '}': return true;
		case '?': return true;
		case '!': return true;
		case '.': return true;
		case '\'': return true;
		case '\"': return true;
		}
		return false;
	}

	
	/**
	 * It recognizes the end of each sentence and return the first sentence.
	 * @param ps - the plain sentence which can consist of several sentences
	 * @return the first sentence recognized 
	 */
	@Override
	public PlainSentence doProcess(PlainSentence ps) {
		String[] eojeols = null;
		String res = null;
		boolean isFirstEojeol = true;
		boolean isEOS = false;
		int i = 0;
		int j = 0;

		if (bufEojeols != null) {
			eojeols = bufEojeols;
			i = bufEojeolsIdx;

			bufEojeols = null;
			bufEojeolsIdx = 0;
		} else {
			if (ps == null) {
				return null;
			}

			if (documentID != ps.getDocumentID()) {
				documentID = ps.getDocumentID();
				sentenceID = 0;
			}

			String str = null;
			if ((str = ps.getSentence()) == null) {
				return null;
			}
			eojeols = str.split("\\s");

			endOfDocument = ps.isEndOfDocument();
		}

		for ( ; isEOS == false && i < eojeols.length; i++) {
			if (!eojeols[i].matches(".*(\\.|\\!|\\?).*")) {
				// the eojeol doesn't have '.', '!', '?'
				if (isFirstEojeol) {
					res = eojeols[i];
					isFirstEojeol = false;
				} else {
					res += " " + eojeols[i];
				}
			} else {
				// the eojeol has '.', '!', '?'
				char[] ca = eojeols[i].toCharArray();

				for (j = 0 ; isEOS == false && j < ca.length; j++) {
					switch (ca[j]) {
					case '.':
						if (j == 1) {
							// ellipsis
							continue;
						}
						if (j > 0) {
							// abbreviation
							if (Character.isLowerCase(ca[j-1]) || Character.isUpperCase(ca[j-1]))  {
								continue;
							}
						}
						if (j < ca.length - 1) {
							// number
							if (Character.isDigit(ca[j+1])) {
								continue;
							}
						}
						isEOS = true;
						break;
					case '!':
						isEOS = true;
						break;
					case '?':
						isEOS = true;
						break;
					}

					if (isEOS) {
						if (isFirstEojeol) {
							res = eojeols[i].substring(0, j) + " " + ca[j]; 
							isFirstEojeol = false;
						} else {
							res += " " + eojeols[i].substring(0, j) + " " + ca[j];
						}

						// a sequence of symbols such as '...', '?!!'
						while (j < ca.length - 1) {
							if (isSym(ca[j+1])) {
								j++;
								res += ca[j];
							} else {
								break;
							}
						}
					}
				}
				if (isEOS == false) {
					if (isFirstEojeol) {
						res = eojeols[i]; 
						isFirstEojeol = false;
					} else {
						res += " " + eojeols[i];
					}
				}
			}
		}

		i--;
		j--;

		if (isEOS) {
			// the remaining part of an eojeol after the end of sentence is stored in the buffer
			if (j + 1 < eojeols[i].length()) {
				eojeols[i] = eojeols[i].substring(j + 1);
				bufEojeols = eojeols;
				bufEojeolsIdx = i;
				hasRemainingData = true;
			} else {
				if (i == eojeols.length - 1) {
					// all eojeols were processed
					hasRemainingData = false;
				} else {
					// if there were some eojeols not processed, they were stored in the buffer
					bufEojeols = eojeols;
					bufEojeolsIdx = i + 1;
					hasRemainingData = true;
				}
			}

			if (bufRes == null) {
				return new PlainSentence(documentID, sentenceID++, !hasRemainingData && endOfDocument, res);
			} else {
				res = bufRes + " " + res;
				bufRes = null;
				return new PlainSentence(documentID, sentenceID++, !hasRemainingData && endOfDocument, res);
			}
		} else {
			if (res != null && res.length() > 0) {
				bufRes = res;
			}
			hasRemainingData = false;
			return null;
		}
	}

	@Override
	public void initialize(String baseDir, String configFile) throws FileNotFoundException, IOException {

	}

	@Override
	public void shutdown() {
	}

	@Override
	public PlainSentence flush() {
		if (bufRes != null) {
			String res = bufRes;
			bufRes = null;
			hasRemainingData = false;
			return new PlainSentence(documentID, sentenceID++, !hasRemainingData && endOfDocument, res);
		} else {
			return null;
		}
	}

	@Override
	public boolean hasRemainingData() {
		return hasRemainingData;
	}
}