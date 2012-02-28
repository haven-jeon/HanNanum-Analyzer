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

/**
 * Number dictionary for recognizing number expressions using automata.
 * 
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */ 
public class NumberDic {
	/** number automata */
	private byte[][] num_automata = {
			/* ACC, +, -, ., ,, n, etc.     */
			{ 0, 0, 0, 0, 0, 0, 0}, /* 0 */
			{ 0, 9, 9, 0, 0, 2, 0}, /* 1 */
			{ 1, 0, 0,11, 5, 3, 0}, /* 2 */
			{ 1, 0, 0,11, 5, 4, 0}, /* 3 */
			{ 1, 0, 0,11, 5,10, 0}, /* 4 */
			{ 0, 0, 0, 0, 0, 6, 0}, /* 5 */
			{ 0, 0, 0, 0, 0, 7, 0}, /* 6 */
			{ 0, 0, 0, 0, 0, 8, 0}, /* 7 */
			{ 1, 0, 0, 0, 5, 0, 0}, /* 8 */
			{ 0, 0, 0, 0, 0,10, 0}, /* 9 */
			{ 1, 0, 0,11, 0,10, 0}, /* 10 */
			{ 1, 0, 0, 0, 0,12, 0}, /* 11 */
			{ 1, 0, 0, 0, 0,12, 0}  /* 12 */
	};

	/**
	 * Returns whether the input was recognized as a number.
	 * @param idx - current state to check
	 * @return true: the input sequence was recognized as a number, false: not a number 
	 */
	public boolean isNum(int idx) {
		if (num_automata[idx][0] == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * It searches the number dictionary with the specified character and the current state of the automata.
	 * @param c - the next character for searching
	 * @param nidx - the current state of the automata
	 * @return next state
	 */
	public int node_look(int c, int nidx)
	{
		int inp;
		switch(c)
		{
		case '+' : inp=1; break;
		case '-' : inp=2; break;
		case '.' : inp=3; break;
		case ',' : inp=4; break;
		default  : 
			if(Character.isDigit(c))
				inp=5; 
			else
				inp=6;
		}
		return num_automata[nidx][inp];
	}
}
