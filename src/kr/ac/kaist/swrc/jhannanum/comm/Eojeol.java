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
 * This class represents an Eojeol for internal use. An eojeol consists
 * of more than one umjeol, and each eojeol is separated with spaces.
 * Korean is a agglutinative language so lexemes in an eojeol may be inflected. 
 * 
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class Eojeol {
	/**
	 * The number of morphemes in this eojeol.
	 */
	public int length = 0;
	
	/**
	 * Morphemes in the eojeol.
	 */
	private String[] morphemes = null;
	
	/**
	 * Morpheme tags of each morpheme.
	 */
	private String[] tags = null;
	
	/**
	 * Constructor.
	 */
	public Eojeol() {
	}
	
	/**
	 * Constructor.
	 * @param morphemes - array of morphemes
	 * @param tags - tag array for each morpheme
	 */
	public Eojeol(String[] morphemes, String[] tags) {
		this.morphemes = morphemes;
		this.tags = tags;
		if (morphemes.length < tags.length) {
			length = morphemes.length;
		} else {
			length = tags.length;
		}
	}
	
	/**
	 * It returns the morpheme list in the eojeol.
	 * @return morpheme list for this eojeol
	 */
	public String[] getMorphemes() {
		return morphemes;
	}
	
	/**
	 * It returns the morpheme on the specific index.
	 * @param index - index of morpheme
	 * @return the morpheme on the index
	 */
	public String getMorpheme(int index) {
		return morphemes[index];
	}
	
	/**
	 * Set the morpheme list with a morpheme array.
	 * @param morphemes - array to set the morphemes
	 */
	public void setMorphemes(String[] morphemes) {
		this.morphemes = morphemes;
		if (tags != null && tags.length < morphemes.length) {
			length = tags.length;
		} else {
			length = morphemes.length;
		}
	}
	
	/**
	 * Set a morpheme on the specific position
	 * @param index - position of the morpheme to change
	 * @param morpheme - new morpheme for the index
	 * @return index: when the morpheme was set up correctly, otherwise -1
	 */
	public int setMorpheme(int index, String morpheme) {
		if (index >= 0 && index < morphemes.length) {
			morphemes[index] = morpheme;
			return index;
		} else {
			return -1;
		}
	}
	
	/**
	 * It returns the tag list for the morphemes in the eojeol.
	 * @return tags list for morphemes
	 */
	public String[] getTags() {
		return tags;
	}
	
	/**
	 * It returns the tag of the morpheme on the given position.
	 * @param index - the position of the morpheme to get its tag
	 * @return morpheme tag on the given position
	 */
	public String getTag(int index) {
		return tags[index];
	}
	
	/**
	 * It sets the tag list for the morphemes of the eojeol.
	 * @param tags - new tags list for the morpheme list
	 */
	public void setTags(String[] tags) {
		this.tags = tags;
		if (morphemes != null && morphemes.length < tags.length) {
			length = morphemes.length;
		} else {
			length = tags.length;
		}
	}
	
	/**
	 * It changes the tag of the morpheme on the index
	 * @param index - position of the morpheme to change its tag
	 * @param tag - new morpheme tag
	 * @return index: the new tag was set up correctly, otherwise -1
	 */
	public int setTag(int index, String tag) {
		if (index >= 0 && index < tags.length) {
			tags[index] = tag;
			return index;
		} else {
			return -1;
		}
	}

	/**
	 * It returns a string that represents the eojeol with morphemes and tags.
	 * For example, 나/npp+는/jxc.
	 */
	@Override
	public String toString() {
		String str = "";
		for (int i = 0; i < length; i++) {
			if (i != 0) {
				str += "+";
			}
			str += morphemes[i] + "/" + tags[i];
		}
		return str;
	}
}
