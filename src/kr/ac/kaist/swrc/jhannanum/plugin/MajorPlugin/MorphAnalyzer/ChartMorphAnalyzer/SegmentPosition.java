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

package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer;

import kr.ac.kaist.swrc.jhannanum.share.Code;

/**
 * This class is for segmentation of morphemes in a given eojeol.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class SegmentPosition {
	/**
	 * This class marks the position of segmentation.
	 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
	 */
	public class Position {
		/** the consonant or vowel of this position */
		char key;
		
		/** the processing state */
		int state;

		/** the index of next segment position */
		int nextPosition;

		/** the temporary index for system dictionary */
		int sIndex;
		
		/** the temporary index for user dictionary */
		int uIndex;
		
		/** the temporary index for number dictionary */
		int nIndex;
		
		/** the number of morphemes possible at this position */
		int morphCount;
		
		/** the list of morphemes possible at this position */
		int[] morpheme = new int[MAX_MORPHEME_COUNT];
	}
	
	/** the maximum number of segmentation */
	final public static int MAX_SEGMENT = 1024;

	/** the maximum number of morphemes possible */
	final public static int MAX_MORPHEME_COUNT = 512;

	/** the processing state - dictionary search */
	final public static int SP_STATE_N = 0;

	/** the processing state - expansion regarding phoneme change phenomenon */
	final public static int SP_STATE_D = 1;

	/** the processing state - recursive processing */
	final public static int SP_STATE_R = 2;

	/** the processing state - connection rule */
	final public static int SP_STATE_M = 3;

	/** the processing state - failure */
	final public static int SP_STATE_F = 4;

	/** the key of the start node for data structure */
	final public static char POSITION_START_KEY = 0;
	
	/** the list of segment positions */
	private Position[] position = null;

	/** the last index of the segment position */
	private int positionEnd = 0;

	/**
	 * Constructor.
	 */
	public SegmentPosition() {
		position = new Position[MAX_SEGMENT];
		for (int i = 0; i < MAX_SEGMENT; i++) {
			position[i] = new Position();
		}
	}

	/**
	 * Adds new segment position.
	 * @param key - vowel or consonant of the segment position
	 * @return the index of the segment position in the list
	 */
	public int addPosition(char key) {
		position[positionEnd].key = key;
		position[positionEnd].state = SP_STATE_N;
		position[positionEnd].morphCount = 0;
		position[positionEnd].nextPosition = 0;
		position[positionEnd].sIndex = 0;
		position[positionEnd].uIndex = 0;
		position[positionEnd].nIndex = 0;

		return positionEnd++;
	}

	/**
	 * Gets the segment position on the specified index.
	 * @param index - the index of the segment position
	 * @return the segment position on the specified index
	 */
	public Position getPosition(int index) {
		return position[index];
	}

	/**
	 * Initializes the data structure for segment positions with given string.
	 * @param str - the plain string to analyze
	 * @param simti - SIMple Trie Index
	 */
	public void init(String str, Simti simti) {
		int prevIndex = 0;
		int nextIndex = 0;

		positionEnd = 0;
		prevIndex = addPosition(POSITION_START_KEY);
		position[prevIndex].state = SP_STATE_M;

		String rev = "";
		for (int i = str.length() - 1; i >= 0; i--) {
			rev += str.charAt(i);
		}

		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			nextIndex = addPosition(c);
			setPositionLink(prevIndex, nextIndex);
			prevIndex = nextIndex;

			simti.insert(rev.substring(0, str.length() - i).toCharArray(),
					nextIndex);
		}

		/* for marking the end of the eojeol */
		setPositionLink(prevIndex, 0);
	}

	/**
	 * Returns the index of the next segment position of the position on the specified index.
	 * @param index - the index of the segment position
	 * @return the index of the next segment position
	 */
	public int nextPosition(int index) {
		return position[index].nextPosition;
	}

	/**
	 * It prints the segment position information to the console.
	 */
	public void printPosition() {
		System.err.println("positionEnd: " + positionEnd);
		for (int i = 0; i < positionEnd; i++) {
			System.err.format("position[%d].key=%c nextPosition=%d\n", i, Code
					.toCompatibilityJamo(position[i].key),
					position[i].nextPosition);
		}
	}

	/**
	 * It connects two segment positions.
	 * @param prevIndex - the index of the previous position
	 * @param nextIndex - the index of the next position
	 * @return the index of the previous position
	 */
	public int setPositionLink(int prevIndex, int nextIndex) {
		position[prevIndex].nextPosition = nextIndex;
		return prevIndex;
	}
}
