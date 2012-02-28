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

package kr.ac.kaist.swrc.jhannanum.exception;

/**
 * This exception occurs when the return type of HanNanum work flow is not matched correctly.
 * A work flow has several phases, so it returns the result with the relevant type according to the last plug-in
 * on the work flow. It means that the work flow may return different types of result. 
 * 
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class ResultTypeException extends Exception {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The array of the phase names.
	 */
	private static final String[] objName = {null, "PlainSentence", "SetOfSentences", "Sentence"};
	
	/**
	 * The analysis phase of the work flow.
	 */
	private int phase = 0;
	
	/**
	 * Constructor.
	 * @param phase - analysis phase of the work flow
	 */
	public ResultTypeException(int phase) {
		this.phase = phase;
	}
	
	/**
	 * Returns the exception message.
	 */
	@Override
	public String getMessage() {
		return "The workflow ends in phase-" + phase + " so '" + objName[phase] + "' is required to store the result properly.";
	}
}
