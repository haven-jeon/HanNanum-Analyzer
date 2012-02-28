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

package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.HmmPosTagger;

/**
 * This class is to generate the eojeol tag which represents the features of morphemes in an eojeol.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class PhraseTag {
	/**
	 * Generates the eojeol tag regarding the sequence of morpheme tags in an eojeol
	 * @param tags - the sequence of morpheme tags in an eojeol
	 * @return the eojeol tag
	 */
	public static String getPhraseTag(String[] tags) {
		char[] res = {'.', '.'};
		int end = tags.length - 1;
		
		if (tags.length < 4) {
			String[] tmp = {"", "", "", ""};
	
			/* 초기화 */
			for (int i = 0 ; i < tags.length; i++) {
				tmp[i] = tags[i];
			}
			tags = tmp;
		}

		if (tags.length <= 0 || tags[0].length() == 0) {
			return String.copyValueOf(res);
		}

		// checks the tags in order
		switch (tags[0].charAt(0)) {
		case 'm':
			if (tags[0].startsWith("ma")) {
				if (tags[1].startsWith("p")) {
					res[0] = 'P';
				} else if (tags[1].startsWith("x")) {
					res[0] = 'P';
				} else if (tags[1].startsWith("jcp")) {
					res[0] = 'P';
				} else {
					res[0] = 'A';
				}
			} else if (tags[0].matches("m^a.*")) {
				if (tags[end].startsWith("j")) {
					res[0] = 'N';
				} else if (tags[1].startsWith("n")) {
					res[0] = 'N';
				} else if (tags[1].startsWith("p")) {
					res[0] = 'P';
				} else {
					res[0] = 'M';
				}
			}
			break;

		case 'e':
			if (tags[0].startsWith("ecc") || tags[0].startsWith("ecs")) {
				res[0] = 'C';
			}
			break;

		case 'f':
			res[0] = 'N';
			break;

		case 'i':
			if (tags[1].startsWith("j")) {
				res[0] = 'N';
			} else {
				res[0] = 'I';
			}
			break;

		case 'n':
			if (tags[1].matches("x.(v|m).*")) {
				if (tags[2].matches("..n.*") || tags[3].matches("..n.*")) {
					res[0] = 'N';
				} else {
					res[0] = 'P';
				}
			} else if (tags[1].matches("x.n.*")) {
				res[0] = 'N';
			} else if (tags[1].startsWith("p")) {
				if (tags[2].matches("..n.*") || tags[3].matches("..n.*")) {
					res[0] = 'N';
				} else {
					res[0] = 'P';
				}
			} else {
				res[0] = 'N';
			}
			break;

		case 'p':
			if (tags[1].startsWith("xsa")) {
				res[0] = 'A';
			} else if (tags[1].startsWith("etn") || tags[2].startsWith("n")) {
				res[0] = 'N';
			} else {
				res[0] = 'P';
			}
			break;

		case 's':
			if (tags[1].startsWith("su") || tags[2].startsWith("j")) {
				res[0] = 'N';
			} else if (tags[2].startsWith("n") || tags[end].startsWith("j")) {
				res[0] = 'N';
			} else {
				res[0] = 'S';
			}

			if (tags[0].startsWith("sf") || tags[1].startsWith("s")) {
				res[1] = 'F';
			}
			break;

		case 'x':
			if (tags[0].startsWith("xsn") || tags[0].startsWith("xp")) {
				res[0] = 'N';
			}
			break;
		}

		// checks the last tag
		String lastTag = tags[end];
		switch (lastTag.charAt(0)) {
		case 'e':
			if (lastTag.startsWith("ecc") || lastTag.startsWith("ecs") || lastTag.startsWith("ecx")) {
				res[1] = 'C';
			} else if (lastTag.startsWith("ef")) {
				res[1] = 'F';
			} else if (lastTag.startsWith("etm")) {
				res[1] = 'M';
			} else if (lastTag.startsWith("etn")) {
				res[1] = 'N';
			}
			break;

		case 'j':
			if (lastTag.startsWith("jcv")) {
				res[0] = 'I';
			} else if (lastTag.startsWith("jx")) {
				if (res[0] == 'A') {
					res[1] = 'J';
				} else {
					res[1] = 'X';
				}
			} else if (lastTag.startsWith("jcj")) {
				if (res[0] == 'A'){
					res[1] = 'J';
				} else {
					res[1] = 'Y';
				}
			} else if (lastTag.startsWith("jca")) {
				res[1] = 'A';
			} else if (lastTag.startsWith("jcm")) {
				if (res[0] == 'A') {
					res[1] = 'J';
				} else {
					res[1] = 'M';
				}
			} else if (lastTag.startsWith("jc")) {
				res[1] = 'J';
			}
			break;
			
		case 'm':
			if (lastTag.matches("m^a.*")) {
				res[1] = 'M';
			} else if (lastTag.startsWith("mag")) {
				res[1] = 'A';
			}
			break;

		case 'n':
			if (lastTag.startsWith("n")) {
				res[0] = 'N';
			}
			break;
		
		case 'x':
			if (lastTag.startsWith("xsa")) {
				res[1] = 'A';
			}
			break;
			
			
		}

		// post processing
		if (res[0] == res[1]) {
			res[1] = '.';
		} else if (res[0] == '.') {
			res[0] = res[1];
			res[1] = '.';
		}

		if (res[0] == 'A') {
			if (res[1] == 'M') {
				res[0] = 'N';
			}
		} else if (res[0] == 'M') {
			if (res[1] == 'A') {
				res[0] = 'A';
			} else if (res[1] == 'F') {
				res[0] = 'N';
			} else if (res[1] == 'C') {
				res[0] = 'N';
			}
		} else if (res[0] == 'I') {
			if (res[1] == 'M' || res[1] == 'J') {
				res[0] = 'N';
			} else if (res[1] == 'C') {
				res[0] = 'P';
			} else if( res[1] == 'F') {
				res[0] = 'N';
			}
		}

		if (res[0] == res[1]) {
			res[1] = '.';
		}

		return String.copyValueOf(res);
	}
}