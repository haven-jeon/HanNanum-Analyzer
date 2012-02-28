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

import java.util.ArrayList;

/**
 * This class represents the set of sentences that were results of the morphological analysis
 * about a input sentence. Each eojeol has more than one morphological analysis result which consists of
 * a morpheme list and their tags. So a morphologically analyzed sentence is a sequence of
 * analysis result of each eojeol. For example, <br>
 * 
 * <table>
 * <tr><td>나는</td>						<td>학교에</td>					<td>간다.</td></tr>
 * <tr><td>-------------------------</td><td>-------------------------</td><td>-------------------------</td></tr>
 * <tr><td>나/ncn+는/jxc</td>			<td>학교/ncn+에/jca</td>			<td>갈/pvg+ㄴ다/ef+./sf</td></tr>
 * <tr><td>나/npp+는/jxc</td>			<td></td>						<td>가/pvg+ㄴ다/ef+./sf</td></tr>
 * <tr><td>나/pvg+는/etm</td>			<td></td>						<td>가/px+ㄴ다/ef+./sf</td></tr>
 * <tr><td>나/px+는/etm</td>				<td></td>						<td></td></tr>
 * <tr><td>나/pvg+아/ecs+는/jxc</td>		<td></td>						<td></td></tr>
 * <tr><td>나/pvg+아/ef+는/etm</td>		<td></td>						<td></td></tr>
 * <tr><td>나/px+아/ecs+는/jxc</td>		<td></td>						<td></td></tr>
 * <tr><td>나/px+아/ef+는/etm</td>		<td></td>						<td></td></tr>
 * <tr><td>날/pvg+는/etm</td>			<td></td>						<td></td></tr>
 * </table>
 * <br>
 * In this example, there are 9 x 1 x 3 = 27 morphologically analyzed sentences.<br>
 * 	
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class SetOfSentences extends CommObject {
	/**
	 * The number of eojeols.
	 */
	public int length = 0;
	
	/**
	 * The array of the morphologically analyzed eojeol lists.
	 */
	private ArrayList<Eojeol[]> eojeolSetArray = null;
	
	/**
	 * The array of the plain eojeols.
	 */
	private ArrayList<String> plainEojeolArray = null;
	
	/**
	 * Constructor.
	 * @param documentID - ID of the document which this sentence is belong to
	 * @param sentenceID - ID of the sentence
	 * @param endOfDocument - If this flag is true, the sentence is the last one of the document.
	 */
	public SetOfSentences(int documentID, int sentenceID, boolean endOfDocument) {
		super.setDocumentID(documentID);
		super.setSentenceID(sentenceID);
		super.setEndOfDocument(endOfDocument);
		
		eojeolSetArray = new ArrayList<Eojeol[]>();
		plainEojeolArray = new ArrayList<String>();
	}
	
	/**
	 * Constructor.
	 * @param documentID - ID of the document which this sentence is belong to
	 * @param sentenceID - ID of the sentence
	 * @param endOfDocument - If this flag is true, the sentence is the last one of the document.
	 * @param plainEojeolArray - the array of the plain eojeols
	 * @param eojeolSetArray - the array of the eojeol lists
	 */
	public SetOfSentences(int documentID, int sentenceID, boolean endOfDocument, ArrayList<String> plainEojeolArray, ArrayList<Eojeol[]> eojeolSetArray) {
		super.setDocumentID(documentID);
		super.setSentenceID(sentenceID);
		super.setEndOfDocument(endOfDocument);
		
		if (eojeolSetArray != null) {
			length = eojeolSetArray.size();
		}
		this.plainEojeolArray = plainEojeolArray;
		this.eojeolSetArray = eojeolSetArray;
	}
	
	/**
	 * Returns the array of the plain eojeol.
	 * @return the array of the plain eojeol
	 */
	public ArrayList<String> getPlainEojeolArray() {
		return plainEojeolArray;
	}

	/**
	 * Sets the array of the plain eojeols.
	 * @param plainEojeolArray
	 */
	public void setPlainEojeolArray(ArrayList<String> plainEojeolArray) {
		this.plainEojeolArray = plainEojeolArray;
	}

	/**
	 * Adds an plain eojeol to the end of the sentence.
	 * @param eojeol - plain eojeol
	 * @return true: when it is added correctly, otherwise false
	 */
	public boolean addPlainEojeol(String eojeol) {
		return plainEojeolArray.add(eojeol);
	}

	/**
	 * Adds eojeols that are morphologically analyzed about one eojeol
	 * to the end of the sentence.
	 * @param eojeols - morphologically analyzed eojeol list about one eojeol
	 * @return true: when it is added correctly, otherwise false
	 */
	public boolean addEojeolSet(Eojeol[] eojeols) {
		return eojeolSetArray.add(eojeols);
	}

	/**
	 * Returns the array of the morphologically analyzed eojeol list.
	 * @return the array of the morphologically analyzed eojeol list
	 */
	public ArrayList<Eojeol[]> getEojeolSetArray() {
		return eojeolSetArray;
	}

	/**
	 * Sets the array of the morphologically analyzed eojeol list. 
	 * @param eojeolSetArray - the array of the morphologically analyzed eojeol list
	 */
	public void setEojeolSetArray(ArrayList<Eojeol[]> eojeolSetArray) {
		this.eojeolSetArray = eojeolSetArray;
	}
	
	/**
	 * Returns the string representation of the morphologically analyzed sentences.
	 * For example,
	 * 
	 * 	나는
	 *		나/ncn+는/jxc
	 *		나/npp+는/jxc
	 *		나/pvg+는/etm
	 *		나/px+는/etm
	 *		나/pvg+아/ecs+는/jxc
	 *		나/pvg+아/ef+는/etm
	 *		나/px+아/ecs+는/jxc
	 *		나/px+아/ef+는/etm
	 *		날/pvg+는/etm
	 *
	 *	학교에
	 *		학교/ncn+에/jca
	 *
	 *	간다.
	 *		갈/pvg+ㄴ다/ef+./sf
	 *		가/pvg+ㄴ다/ef+./sf
	 *		가/px+ㄴ다/ef+./sf
	 *
	 */
	@Override
	public String toString() {
		String str = "";
		for (int i = 0; i < length; i++) {
			str += plainEojeolArray.get(i) + "\n";
			Eojeol[] eojeolArray = eojeolSetArray.get(i);
			for (int j = 0; j < eojeolArray.length; j++) {
				str += "\t" + eojeolArray[j] + "\n";
			}
			str += "\n";
		}
		return str;
	}
}