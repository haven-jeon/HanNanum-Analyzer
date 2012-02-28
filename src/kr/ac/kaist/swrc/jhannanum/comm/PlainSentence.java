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

package kr.ac.kaist.swrc.jhannanum.comm;

/**
 * This class contains a plain sentence which is not analyzed yet.
 * It is used by the HanNanum work flow and its plug-ins to communicate
 * each other.
 * 
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class PlainSentence extends CommObject {
	/**
	 * The plain sentence.
	 */
	private String sentence = null;
	
	/**
	 * Constructor.
	 * @param documentID - ID of the document which this sentence belong to
	 * @param sentenceID - ID of this sentence
	 * @param endOfDocument - If this flag is true, the sentence is the last one of the document.
	 */
	public PlainSentence(int documentID, int sentenceID, boolean endOfDocument) {
		super.setDocumentID(documentID);
		super.setSentenceID(sentenceID);
		super.setEndOfDocument(endOfDocument);
	}
	
	/**
	 * Constructor.
	 * @param documentID - ID of the document which this sentence belong to
	 * @param sentenceID - ID of this sentence
	 * @param endOfDocument - If this flag is true, the sentence is the last one of the document.
	 * @param sentence - the plain sentence
	 */
	public PlainSentence(int documentID, int sentenceID, boolean endOfDocument, String sentence) {
		super.setDocumentID(documentID);
		super.setSentenceID(sentenceID);
		super.setEndOfDocument(endOfDocument);
		this.sentence = sentence;
	}

	/**
	 * Getter of the plain sentence.
	 * @return the plain sentence
	 */
	public String getSentence() {
		return sentence;
	}

	/**
	 * Setter of the plain sentence.
	 * @param sentence - the plain sentence
	 */
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	/**
	 * It returns the plain string.
	 * @return the plain string or null when it is not set set up
	 */
	@Override 
	public String toString() {
		if (sentence == null) {
			return "";
		}
		return sentence;
	}
}
