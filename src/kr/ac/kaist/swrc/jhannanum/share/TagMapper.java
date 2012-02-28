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

package kr.ac.kaist.swrc.jhannanum.share;

/**
 * It changes the analysis level of the KAIST morpheme tag.
 * 
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class TagMapper {
	/**
	 * Changes the analysis level of the KAIST morpheme tag.
	 * @param tag - morpheme tag
	 * @param level - the analysis level
	 * @return the morpheme tag on the analysis level
	 */
	public static String getKaistTagOnLevel(String tag, int level){
		if (tag == null || level > 4 || level < 1) {
			return null;
		}
		
		int tagLen = tag.length();
		if (tagLen > level) {
			return tag.substring(0, level).toUpperCase();
		} else {
			return tag.toUpperCase();
		}
	}
}
