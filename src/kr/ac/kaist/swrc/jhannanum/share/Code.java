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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class is for code conversion. HanNanum internally uses triple encoding, which represents
 * an Korean eumjeol with three characters - CHOSEONG(beginning consonant), JUNGSEONG(vowel), JONGSEONG(final consonant).
 * This class converts the Korean encoding from unicode to triple encoding, and vice versa.
 * 
 * @see <a href="http://www.utf8-chartable.de/unicode-utf8-table.pl">http://www.utf8-chartable.de/unicode-utf8-table.pl</a>
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class Code {
	/** triple encoding */
	public final static int ENCODING_TRIPLE = 0;
	
	/** unicode */
	public final static int ENCODING_UNICODE = 1;
	
	/** CHOSEONG(beginning consonant) */
	public final static int JAMO_CHOSEONG = 0;
	
	/** JUNGSEONG(vowel) */
	public final static int JAMO_JUNGSEONG = 1;
	
	/** JONGSEONG(final consonant) */
	public final static int JAMO_JONGSEONG = 2;
	
	/** hangul filler in unicode */
	public final static char HANGUL_FILLER = 0x3164;
	
	/** the list of CHOSEONG - beginning consonant */
	private final static char[] CHOSEONG_LIST = 
		{'ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ','ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'};
	
	/** the list of JONGSEONG - final consonant */
	private final static char[] JONGSEONG_LIST = 
		{HANGUL_FILLER, 'ㄱ','ㄲ','ㄳ','ㄴ','ㄵ','ㄶ','ㄷ','ㄹ','ㄺ','ㄻ','ㄼ','ㄽ','ㄾ','ㄿ','ㅀ','ㅁ','ㅂ','ㅄ','ㅅ','ㅆ','ㅇ','ㅈ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'};
	
	/** the list of JONGSEONG for reverse */
	private final static byte[] CHOSEONG_LIST_REV =
		{0,1,-1,2,-1,-1,3,4,5,-1,-1,-1,-1,-1,-1,-1,6,7,8,-1,9,10,11,12,13,14,15,16,17,18};
	
	/** the list of JONGSEONG for reverse */
	private final static byte[] JONGSEONG_LIST_REV =
		{1,2,3,4,5,6,7,-1,8,9,10,11,12,13,14,15,16,17,-1,18,19,20,21,22,-1,23,24,25,26,27};
	
	/**
	 * It changes the encoding of text file between UTF-8 and the triple encoding.
	 * @param srcFileName - the input file
	 * @param desFileName - the output file
	 * @param srcEncoding - the encoding of input file: ENCODING_UNICODE or ENCODING_TRIPLE
	 * @param desEncoding - the encoding of input file: ENCODING_UNICODE or ENCODING_TRIPLE
	 * @throws IOException
	 */
	public static void convertFile(String srcFileName, String desFileName, int srcEncoding, int desEncoding) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFileName), "UTF-8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(desFileName), "UTF-8"));
		String line = null;
		
		if (srcEncoding == ENCODING_UNICODE && desEncoding == ENCODING_TRIPLE) {
			while ((line = br.readLine()) != null) {
				char[] buf = toTripleArray(line);
				bw.write(buf);
				bw.write('\n');
			}
		} else if (srcEncoding == ENCODING_TRIPLE && desEncoding == ENCODING_UNICODE) {
			while ((line = br.readLine()) != null) {
				String buf = toString(line.toCharArray());
				bw.write(buf);
				bw.write('\n');
			}
		}
		br.close();
		bw.close();
	}
	
	/**
	 * It checks whether the specified character is choseong.
	 * @param c - the character to check
	 * @return true: the specified character is choseong, false: not choseong
	 */
	public static boolean isChoseong(char c) {
		if (c >= 0x1100 && c <= 0x1112) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * It checks whether the specified character is jongseong.
	 * @param c - the character to check
	 * @return true: the specified character is jongseong, false: not jongseong
	 */
	public static boolean isJongseong(char c) {
		if (c >= 0x11A8 && c <= 0x11C2) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * It checks whether the specified character is jungseong.
	 * @param c - the character to check
	 * @return true: the specified character is jungseong, false: not jungseong
	 */
	public static boolean isJungseong(char c) {
		if (c >= 0x1161 && c <= 0x1175) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * It changes the specified jongseong to choseong.
	 * @param jongseong - the final consonant
	 * @return the consonant which is changed from jongseong to choseong
	 */
	public static char toChoseong(char jongseong) {
		if (jongseong >= 0x11A8 && jongseong <= 0x11C2) {
			jongseong -= 0x11A7;
			// 종성
			char tmp = JONGSEONG_LIST[jongseong];
			tmp -= 0x3131;
			if (CHOSEONG_LIST_REV[tmp] != -1) {
				return (char)(CHOSEONG_LIST_REV[tmp] + 0x1100);
			}
		}
		return jongseong;
	}
	
	/**
	 * Changes the unicode Hangul jamo to unicode compatibility Hangul jamo.
	 * @param jamo - unicode Hangul jamo
	 * @return the compatibility Hangul jamo
	 */
	public static char toCompatibilityJamo(char jamo) {
		if (jamo >= 0x1100 && jamo < 0x1100 + CHOSEONG_LIST.length) {
			return CHOSEONG_LIST[jamo - 0x1100];
		}
		if (jamo >= 0x1161 && jamo <= 0x1175) {
			return (char)(jamo - 0x1161 + 0x314F);
		}
		if (jamo == 0) {
			return HANGUL_FILLER;
		} else {
			if (jamo >= 0x11A8 && jamo < 0x11A7 + JONGSEONG_LIST.length) {
				return JONGSEONG_LIST[jamo - 0x11A7];
			}
		}
		return jamo;
	}
	
	/**
	 * It changes the unicode Hangul compatibility jamo to Hangul jamo - choseong, jungseong, or jongseong.
	 * @param jamo - the unicode Hangul compatibility jamo
	 * @param flag - JAMO_CHOSEONG or JAMO_JUNGSEONG or JAMO_JONGSEONG
	 * @return the unicode Hangul jamo
	 */
	public static char toJamo(char jamo, int flag) {
		char result = 0;
		switch (flag) {
		case JAMO_CHOSEONG:
			if (jamo >= 0 && jamo <= 0x12) {
				result = (char)(jamo + 0x1100);
			}
			break;
		case JAMO_JUNGSEONG:
			if (jamo >= 0 && jamo <= 0x14) {
				result = (char)(jamo + 0x1161);
			}
			break;
		case JAMO_JONGSEONG:
			if (jamo >= 1 && jamo <= 0x1B) {
				result = (char)(jamo + 0x11A7);
			}
			break;
		}
		return result;
	}
	
	/**
	 * Converts the encoding of the text from Hangul triple encoding to unicode.
	 * @param tripleArray - the text with the Hangul triple encoding
	 * @return the unicode text
	 */
	public static String toString(char[] tripleArray) {
		String result = "";
		int i = 0;
		int len = tripleArray.length;
		
		int cho;
		int jung;
		int jong;
		
		if (len == 0) {
			return "";
		}
		
		char c = tripleArray[i];
		
		while (i < len) {
			if (c >= 0x1100 && c <= 0x1112) {
				cho = c - 0x1100;

				if (++i < len) {
					c = tripleArray[i];
				}
				if (c >= 0x1161 && c <= 0x1175 && i < len) {
					jung = c - 0x1161;
					
					if (++i < len) {
						c = tripleArray[i];
					}
					if (c >= 0x11A8 && c <= 0x11C2 && i < len) {
						jong = c - 0x11A7;
						
						// choseong + jungseong + jongseong
						result += (char)(0xAC00 + (cho * 21 * 28) + (jung * 28) + jong);
						if (++i < len) {
							c = tripleArray[i];
						}
					} else {
						// choseong + jungseong
						result += (char)(0xAC00 + (cho * 21 * 28) + (jung * 28));
					}
				} else {
					// choseong: a single choseong is represented as ^consonant
					char tmp = CHOSEONG_LIST[cho];
					if (tmp == 'ㅃ' || tmp == 'ㅉ' || tmp == 'ㄸ') {
						result += CHOSEONG_LIST[cho];
					} else {
						result += "^" + CHOSEONG_LIST[cho];
					}
				}
			} else if (c >= 0x1161 && c <= 0x1175 && i < len) {
				jung = c - 0x1161;
				
				// jungseong
				result += (char)(jung + 0x314F);
				
				if (++i < len) {
					c = tripleArray[i];
				}
			} else if (c >= 0x11A8 && c <= 0x11C2 && i < len) {
				jong = c - 0x11A7;
				
				// jongseong
				result += JONGSEONG_LIST[jong];
				
				if (++i < len) {
					c = tripleArray[i];
				}
			} else {
				result += c;
				
				if (++i < len) {
					c = tripleArray[i];
				}
			}
		}
		return result;
	}
	
	/**
	 * Converts the encoding of the text from Hangul triple encoding to unicode.
	 * @param tripleArray - the text with the Hangul triple encoding
	 * @param len - the length of text to convert
	 * @return the unicode text
	 */
	public static String toString(char[] tripleArray, int len) {
		String result = "";
		int i = 0;
		
		int cho;
		int jung;
		int jong;
		
		char c = tripleArray[i++];
		
		while (i < len) {
			if (c >= 0x1100 && c <= 0x1112 && i < len) {
				cho = c - 0x1100;
				c = tripleArray[i++];
				if (c >= 0x1161 && c <= 0x1175 && i < len) {
					jung = c - 0x1161;
					c = tripleArray[i++];
					if (c >= 0x11A8 && c <= 0x11C2 && i < len) {
						jong = c - 0x11A7;
						// choseong + jungseong + jongseong
						result += (char)(0xAC00 + (cho * 21 * 28) + (jung * 28) + jong);
						c = tripleArray[i++];
					} else {
						// choseong + jongseong
						result += (char)(0xAC00 + (cho * 21 * 28) + (jung * 28));
					}
				} else {
					// choseong: a single choseong is represented as ^consonant
					char tmp = CHOSEONG_LIST[cho];
					if (tmp == 'ㅃ' || tmp == 'ㅉ' || tmp == 'ㄸ') {
						result += CHOSEONG_LIST[cho];
					} else {
						result += "^" + CHOSEONG_LIST[cho];
					}
				}
			} else if (c >= 0x1161 && c <= 0x1175 && i < len) {
				jung = c - 0x1161;
				// jungseong
				result += (char)(jung + 0x314F);
				c = tripleArray[i++];
			} else if (c >= 0x11A8 && c <= 0x11C2 && i < len) {
				jong = c - 0x11A7;
				// jongseong
				result += JONGSEONG_LIST[jong];
				c = tripleArray[i++];
			} else {
				result += c;
				c = tripleArray[i++];
			}
		}
		return result;
	}
	
	/**
	 * It combines the specified choseong, jungseong, and jongseong to one unicode Hangul syllable. 
	 * @param cho - beginning consonant
	 * @param jung - vowel
	 * @param jong - final consonant
	 * @return the combined Hangul syllable
	 */
	public static char toSyllable(char cho, char jung, char jong) {
		if (cho >= 0x1100 && cho <= 0x1112) {
			cho -= 0x1100;
			if (jung >= 0x1161 && jung <= 0x1175) {
				jung -= 0x1161;
				if (jong >= 0x11A8 && jong <= 0x11C2) {
					jong -= 0x11A8;
					// choseong + jungseong + jongseong
					return (char)(0xAC00 + (cho * 21 * 28) + (jung * 28) + jong);
				} else {
					// choseong + jungseong
					return (char)(0xAC00 + (cho * 21 * 28) + (jung * 28));
				}
			} else {
				// choseong
				return CHOSEONG_LIST[cho];
			}
		} else if (jung >= 0x1161 && jung <= 0x1175) {
			jung -= 0x1161;
			// jungseong
			return (char)(jung + 0x314F);
		} else if (jong >= 0x11A8 && jong <= 0x11C2) {
			jong -= 0x11A;
			// jongseong
			return JONGSEONG_LIST[jong];
		}
		return HANGUL_FILLER;
	}
	
	/**
	 * It converts the encoding of the specified text from unicode to triple encoding.
	 * @param str - the unicode text
	 * @return the text represented in the Hangul triple encoding
	 */
	public static char[] toTripleArray(String str) {
		char[] result = null;
		ArrayList<Character> charList = new ArrayList<Character>(); 
		char c = 0;
		char cho;
		char jung;
		char jong;
		
		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			
			if(c >= 0xAC00 && c <= 0xD7AF) {
				int combined = c - 0xAC00;
				if ((cho = toJamo((char)(combined / (21 * 28)), JAMO_CHOSEONG)) != 0) {
					charList.add(cho);	
				}
				combined %= (21 * 28);
				if ((jung = toJamo((char)(combined / 28), JAMO_JUNGSEONG)) != 0) {
					charList.add(jung);
				}
				if ((jong = toJamo((char)(combined % 28), JAMO_JONGSEONG)) != 0) {
					charList.add(jong);
				}
			} else if (c >= 0x3131 && c <= 0x314E) {
				c -= 0x3131;
				if (JONGSEONG_LIST_REV[c] != -1) {
					// a single consonant is regarded as a final consonant
					charList.add((char)(JONGSEONG_LIST_REV[c] + 0x11A7));
				} else if (CHOSEONG_LIST_REV[c] != -1) {
					// a single consonant which can not be a final consonant becomes a beginning consonant
					charList.add((char)(CHOSEONG_LIST_REV[c] + 0x1100));
				} else {
					// exception (if it occur, the conversion array has some problem)
					charList.add((char)(c + 0x3131));
				}
			} else if (c >= 0x314F && c <= 0x3163) {
				// a single vowel changes jungseong
				charList.add((char)(c - 0x314F + 0x1161));
			} else if (c == '^' && str.length() > i + 1 && str.charAt(i+1) >= 0x3131 && str.charAt(i+1) <= 0x314E) {
				// ^consonant changes to choseong
				c = (char)(str.charAt(i+1) - 0x3131);
				if (CHOSEONG_LIST_REV[c] != -1) {
					charList.add((char)(CHOSEONG_LIST_REV[c] + 0x1100));
					i++;
				} else {
					charList.add('^');
				}
			}
			else {
				// other characters
				charList.add(c);
			}
		}
		
		result = new char[charList.size()];
		Iterator<Character> iter = charList.iterator();
		for (int i = 0; i < result.length; i++) {
			result[i] = iter.next();
		}
		
		return result;
	}
	
	/**
	 * It returns the unicode representation of triple encoding text.
	 * @param str - the unicode text
	 * @return the unicode representation of triple encoding text
	 */
	public static String toTripleString(String str) {
		return String.valueOf(toTripleArray(str));
	}
}