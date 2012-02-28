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
 * This class represents a sentence which is the sequence of eojeols that
 * are morphologically analyzed. Each eojeol has a plain eojeol, a morpheme list,
 * and a tag list for a sequence of morphemes.

 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 *
 */
public class Sentence extends CommObject {
	/**
	 * The number of eojeols.
	 */
	public int length = 0;
	
	/**
	 * The array of the plain eojeols.
	 */
	private String[] plainEojeols = null;
	
	/**
	 * The array of the eojeols that are morphologically analyzed.
	 */
	private Eojeol[] eojeols = null;
	
	/**
	 * Constructor.
	 * @param documentID - ID of the document which this sentence belong to
	 * @param sentenceID - ID of the sentence
	 * @param endOfDocument - If this flag is true, this sentence is the last one of the document.
	 */
	public Sentence(int documentID, int sentenceID, boolean endOfDocument) {
		super.setDocumentID(documentID);
		super.setSentenceID(sentenceID);
		super.setEndOfDocument(endOfDocument);
	}
	
	/**
	 * Constructor.
	 * @param documentID - ID of the document which this sentence belong to
	 * @param sentenceID - ID of the sentence
	 * @param endOfDocument - If this flag is true, this sentence is the last one of the document.
	 */
	public Sentence(int documentID, int sentenceID, boolean endOfDocument, String[] plainEojeols, Eojeol[] eojeols) {
		super.setDocumentID(documentID);
		super.setSentenceID(sentenceID);
		super.setEndOfDocument(endOfDocument);
		
		this.eojeols = eojeols;
		this.plainEojeols = plainEojeols;
		
		if (eojeols != null && plainEojeols != null) {
			if (plainEojeols.length <= eojeols.length) {
				length = eojeols.length;
			} else {
				length = plainEojeols.length;
			}
		} else {
			length = 0;
		}
	}

	/**
	 * Getter of the array of the eojeols that consist of morphemes and their tags.
	 * @return the array of the eojeols
	 */
	public Eojeol[] getEojeols() {
		return eojeols;
	}
	
	/**
	 * Returns the eojeol object at the specified index.
	 * @param index - the index of the eojeol
	 * @return eojeol at the specified index
	 */
	public Eojeol getEojeol(int index) {
		return eojeols[index];
	}

	/**
	 * Set the eojeols list with the specified array.
	 * @param eojeols - new eojeol array for this sentence
	 */
	public void setEojeols(Eojeol[] eojeols) {
		this.eojeols = eojeols;
		this.length = eojeols.length;
	}
	
	/**
	 * Set an eojeol at the specified index.
	 * @param index - the index of the eojeol to set up
	 * @param eojeol - the new eojeol
	 */
	public void setEojeol(int index, Eojeol eojeol) {
		eojeols[index] = eojeol;
	}
	
	/**
	 * Set an eojeol at the specified index with morphemes and their tags.
	 * @param index - the index of the eojeol to set up
	 * @param morphemes - the new morpheme list
	 * @param tags - the new tag list
	 */
	public void setEojeol(int index, String[] morphemes, String[] tags) {
		Eojeol eojeol = new Eojeol(morphemes, tags);
		eojeols[index] = eojeol;
	}
	
	/**
	 * Returns the string representation of this sentence.
	 * For example,
	 *		나는
	 *			나/npp+는/jxc
     *
	 *		학교에서
	 *			학교/ncn+에서/jca
     *
	 *		공부를
	 *			공부/ncpa+를/jco
	 *
	 *		하고
	 *			하/pvg+고/ecc
	 *
	 *		있다.
	 *			있/paa+다/ef+./sf
	 *
	 */
	public String toString() {
		String str = "";
		for (int i = 0; i < length; i++) {
			str += plainEojeols[i] + "\n";
			str += "\t" + eojeols[i].toString() + "\n\n";
		}
		return str;
	}
	
	/**
	 * Returns the array of the plain eojeols.
	 * @return the plain eojeol array
	 */
	public String[] getPlainEojeols() {
		return plainEojeols;
	}

	/**
	 * It sets the plain eojeol list with the specified array.
	 * @param plainEojeols - the array of the plain eojeols
	 */
	public void setPlainEojeols(String[] plainEojeols) {
		this.plainEojeols = plainEojeols;
	}
}