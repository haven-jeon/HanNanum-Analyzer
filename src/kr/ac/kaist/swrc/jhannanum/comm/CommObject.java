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
 * CommObject is used by a work flow and plug-ins that are expected to communicate
 * with each other. This object contains the meta data of input documents and
 * sentences. The input and output data types of HanNanum plug-ins such as PlainSentence,
 * Sentence, and SetOfSentences inherit this class.
 * 
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class CommObject {
	/**
	 * ID of the document which this object belong to.
	 */
	private int documentID = 0;
	
	/**
	 * ID of the sentence which this object belong to.
	 */
	private int sentenceID = 0;
	
	/**
	 * Flag for notifying the end of document.
	 */
	private boolean endOfDocument = false;
	
	/**
	 * It returns true when it is the last element of the document.
	 * 
	 * @return true - when it is the end of document, otherwise false
	 */
	public boolean isEndOfDocument() {
		return endOfDocument;
	}
	
	/**
	 * Set the end of document flag of this object.
	 * @param endOfDocument - true: when it is the end of document, otherwise false
	 */
	public void setEndOfDocument(boolean endOfDocument) {
		this.endOfDocument = endOfDocument;
	}

	/**
	 * It returns the ID of the document which this object is belong to.
	 * @return documentID
	 */
	public int getDocumentID() {
		return documentID;
	}

	/**
	 * Set the document ID for this object.
	 * @param documentID
	 */
	public void setDocumentID(int documentID) {
		this.documentID = documentID;
	}

	/**
	 * Get the sentence ID for this object.
	 * @return sentenceID
	 */
	public int getSentenceID() {
		return sentenceID;
	}

	/**
	 * Set the sentence ID for this object.
	 * @param sentenceID
	 */
	public void setSentenceID(int sentenceID) {
		this.sentenceID = sentenceID;
	}
}
