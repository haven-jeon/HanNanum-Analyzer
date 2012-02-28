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
 * This exception occurs when it is failed to map a morpheme tag with another one.
 * 
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class TagMappingException extends Exception {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The name of the morpheme tag which the exception occurs with.
	 */
	private String tag = null;
	
	/**
	 * Constructor.
	 * @param tag - the morpheme tag which the exception occurs with.
	 */
	public TagMappingException(String tag) {
		this.tag = tag;
	}
	
	/**
	 * Returns the exception message.
	 */
	@Override
	public String getMessage() {
		return "Failed to get a tag which is mapped with " + this.tag;
	}
}
