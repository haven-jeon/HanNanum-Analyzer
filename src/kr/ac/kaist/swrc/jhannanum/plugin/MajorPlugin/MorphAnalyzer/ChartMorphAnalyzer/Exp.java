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
import kr.ac.kaist.swrc.jhannanum.share.TagSet;

/**
 * This class for expansion of morphological analysis regarding rules such as
 * elision, contractions, and irregular rules.
 * 
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class Exp { 
	/*
	 * TAG_TYPE_YONGS : TAG_TYPE_YONGS
	 * TAG_TYPE_EOMIES : TAG_TYPE_EOMIES
	 * IRR_TYPE_S : IRR_TYPE_S
	 * ᆮ Irregular rule : IRR_TYPE_D
	 * ᆸ Irregular rule : IRR_TYPE_B
	 * ᇂ Irregular rule : IRR_TYPE_H
	 * 르 Irregular rule : IRR_TYPE_REU
	 * 러 Irregular rule : IRR_TYPE_REO
	 */

	/**
	 * The last index of pset
	 */
	private int pset_end = 0;

	/**
	 * The lattice style morpheme chart
	 */
	private MorphemeChart mc = null;

	/**
	 * Morpheme tag set
	 */
	private TagSet tagSet = null;

	/**
	 * The list for expansion rules.
	 */
	private String[][] pset = 
	{
		{"초성","ᄀᄁᄂᄃᄄᄅᄆᄇᄈᄉᄊᄋᄌᄍᄎᄏᄐᄑᄒ"},
		{"종성","ᆨᆩᆪᆫᆬᆭᆮᆯᆰᆱᆲᆳᆴᆵᆶᆷᆸᆹᆺᆻᆼᆽᆾᆿᇀᇁᇂ"},
		{"중성","ᅡᅣᅥᅧᅩᅭᅮᅲᅳᅵᅢᅤᅦᅨᅬᅱᅴᅪᅯᅫᅰ"},

		{"음성모음","ᅥᅮᅧᅲᅦᅯᅱᅨ"},
		{"양성모음","ᅡᅩᅣᅢᅪᅬᅤ"},
		{"중성모음","ᅳᅵ"},

		/* rules on '것' : 걸로, 걸, 겁니다, 건 거면 */
		{"rule_것l",""},
		{"rule_것","ᄂᄆᄅᆫᆯᆸ"},
		{"rule_것r",""},

		/* 'ᆯ' elision-1 */
		{"l11","ᅡᅣᅥᅧᅩᅭᅮᅲᅳᅵᅢᅤᅦᅨᅬᅱᅴᅪᅯᅫᅰ"},
		{"11"," ᆫᆯᆷᆸᄂᄉ"},
		{"r11",""},

		/* 'ᆯ' elision-2 */
		{"l11-1","ᅡᅣᅥᅧᅩᅭᅮᅲᅳᅵᅢᅤᅦᅨᅬᅱᅴᅪᅯᅫᅰ"},
		{"11-1","ᄂᄉ"},
		{"r11-1",""},

		/* 'ᅳ'  elision*/
		{"l12",""},
		{"12","ᅡᅥ"},
		{"r12",""},

		/* 'ᅡ' elision */
		{"l13",""},
		{"13","ᅡ"},
		{"r13",""},

		/* 'ᅥ' elision */
		{"l14",""},
		{"14","ᅥᅦᅧᅢ"},
		{"r14",""},

		/* 'ᆮ' irregular */
		{"l21","ᆯ"},
		{"21","ᄋ"},
		{"r21","ᅥᅡᅳ"},

		/* 'ᆺ' irregular */
		{"l22","ᅡᅥᅮᅳᅵ"},
		{"22","ᄋ"},
		{"r22","ᅥᅡᅳ"},

		/* 'ᆸ' irregular-1 */
		{"l23","ᄋ"},
		{"23","ᅮ"},
		{"r23",""},

		/* 'ᆸ' irregular-2 */
		{"l24","ᄋ"},
		{"24","ᅪ"},
		{"r24",""},

		/* 'ᆸ' irregular-3 */
		{"l25","ᄋ"},
		{"25","ᅯ"},
		{"r25",""},

		/* 'ᇂ' irregular-1 */
		{"l26","ᄀᄃᄅᄆᄋ"},
		{"26","ᅡᅣ"},
		{"r26",""},

		/* 'ᇂ' irregular-2 */
		{"l27","ᄀᄃᄅᄆᄄᄋ"},
		{"27","ᅢᅤ"},
		{"r27",""},

		/* 'ᇂ' irregular-3 */
		{"l28","ᄀᄃᄅᄆᄄᄋ"},
		{"28","ᅥ"},
		{"r28",""},

		/* '르' irregular */
		{"l29","ᆯ"},
		{"29","ᄅ"},
		{"r29","ᅥᅡ"},

		/* '러' irregular */
		{"l30","ᅳ"},
		{"30","ᄅ"},
		{"r30","ᅥ"},

		/* '우' irregular */
		{"l31","ᄑ"},
		{"31","ᅥ"},
		{"r31",""},

		/* '여' irregular-1 */
		{"l32","ᄒ"},
		{"32","ᅡ"},
		{"r32","ᄋ"},

		/* '여' irregular-2 */
		{"l33","ᄒ"},
		{"33","ᅢ"},
		{"r33",""},

		/* 'ᅩ', 'ᅮ' contraction */
		{"l51",""},
		{"51","ᅪᅯ"},
		{"r51",""},

		/* 'ᅬ' contraction */
		{"l52",""},
		{"52","ᅫ"},
		{"r52",""},

		/* 'ᅵ' contraction */
		{"l53",""},
		{"53","ᅧ"},
		{"r53",""},

		/* 
		 * '으(eomi)' elision
		 * the rule l54 is shared for '으', '스', '느'
		 */ 
		{"l54","ᆯᅡᅣᅥᅧᅩᅭᅮᅲᅳᅵᅢᅤᅦᅨᅬᅱᅴᅪᅯᅫᅰ"},
		{"54"," ᆫᆯᆷᆸᄂᄅᄆᄉᄋ"},
		{"r54",""}
	};

	/**
	 * Constructor.
	 * @param mc - the lattice style morpheme chart
	 * @param tagSet - morpheme tag set
	 */
	public Exp(MorphemeChart mc, TagSet tagSet) {
		this.mc = mc;
		this.tagSet = tagSet;
		pset_end = pset.length;
	}

	/**
	 * Inserts the string str2 to the specified position of the string str1.
	 * @param str1 - the string where the other string is going to be inserted
	 * @param cur - the index of the str1 for insertion
	 * @param str2 - the string to insert
	 * @return the new string that str2 is inserted to str1 at the specified position
	 */
	private String insert(String str1, int cur, String str2) {
		return str1.substring(0, cur) + str2 + str1.substring(cur);
	}

	/**
	 * It checks pset whether the rule is applied to the specified index of given string.
	 * @param base - the string to check
	 * @param idx - the index of the string
	 * @param rule - the name of the rule to check
	 * @return 0 - the rule is not applied, otherwise applied
	 */
	private int	pcheck(String base, int idx, String rule) {
		char c;

		if (idx < base.length()) {
			c = base.charAt(idx);
		} else {
			c = '\0';
		}

		for (int i = 0; i < pset_end; i++) {
			if (pset[i][0].equals(rule)) {
				if (pset[i][1].length() == 0) {
					return 1;
				} else {
					int index = pset[i][1].indexOf(c);
					if (index == -1) {
						return 0;
					} else {
						return index + 1;
					}
				}
			}
		}
		return 0;
	}

	/**
	 * Check the rules on the given string, and expand the morpheme chart.
	 * @param from - the index for the start of segment position
	 * @param str1 - the front part of the string
	 * @param str2 - the next part of the string
	 * @param sp - the segment position
	 */
	public void prule(int from, String str1, String str2, SegmentPosition sp) {
		int i;
		rule_NP(from, str1, str2);
		// sp.printPosition();

		for(i=0;i<str2.length();i++) {
			rule_rem(from,str1,str2,i);
			// sp.printPosition();
			rule_irr_word(from,str1,str2,i);
			// sp.printPosition();
			rule_irr_word2(from,str1,str2,i);
			// sp.printPosition();
			rule_shorten(from,str1,str2,i);
			// sp.printPosition();
			rule_eomi_u(from,str1,str2,i);
			// sp.printPosition();
			rule_johwa(from,str1,str2,i);
			// sp.printPosition();
			rule_i(from,str1,str2,i);
			// sp.printPosition();
			rule_gut(from,str1,str2,i);
			// sp.printPosition();
		}
	}

	/**
	 * Replaces the character at the specified position of the string str1
	 * with the first character of the string str2.
	 * @param str1 - base string
	 * @param cur - index of the character to 
	 * @param str2 - the first character of the string is used to replace
	 * @return the string with the new character replaced
	 */
	private String replace(String str1, int cur, String str2) {
		char[] array = str1.toCharArray();
		
		if (str2.length() == 0) {
			System.err.println("Exp.java: replace(): s is to short");
			System.exit(0);
		}
		array[cur] = str2.charAt(0);
		
		return String.valueOf(array);
	}

	/**
	 * It expands the morpheme chart regarding the elision rule '으', '스', '느'.
	 * @param from - the start index for the segment position
	 * @param prev - the passed part of the string
	 * @param str - the next part of the string to check
	 * @param cur - the current index of the string for checking the rules
	 */
	private void rule_eomi_u(int from, String prev, String str, int cur) {
		String buf;
		String buf2;
		String new_str;

		if (cur > str.length()) {
			return;
		}

		if ((cur>0 && pcheck(str,cur-1,"l54")!=0)
				&&pcheck(str,cur,"54")!=0
				&&pcheck(str,cur+1,"r54")!=0) {
			new_str=insert(str,cur,"으");
			buf = new_str.substring(0,cur);
			buf2 = new_str.substring(cur);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}
		if ((cur>0 && pcheck(str,cur-1,"l54")!=0)
				&&strncmp(str,cur,"ᆸ니",0,3)==0) {
			new_str=insert(str,cur,"스");
			buf = new_str.substring(0,cur);
			buf2 = new_str.substring(cur);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}
		if ((cur>0 && pcheck(str,cur-1,"l54")!=0)
				&&strncmp(str,cur,"ᆫ다",0,3)==0) {
			new_str=insert(str,cur,"느");
			buf = new_str.substring(0,cur);
			buf2 = new_str.substring(cur);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}
	}

	/**
	 * It expands the morpheme chart regarding the rules about '것'.
	 * @param from - the start index for the segment position
	 * @param prev - the passed part of the string
	 * @param str - the next part of the string to check
	 * @param cur - the current index of the string for checking the rules
	 */
	private void rule_gut(int from,String prev,String str,int cur) {
		String buf;
		String buf2;
		String new_str;

		if (cur >= str.length()) {
			return;
		}

		if (cur>1&& strncmp(str,cur-2,"거",0,2)==0 &&
				pcheck(str,cur,"rule_것")!=0) {

			if (str.charAt(cur)=='ᆸ') {
				if (strncmp(str,cur,"ᆸ니",0,3)==0) {
					new_str=insert(str,cur,"ᆺ이");
					buf = new_str.substring(0,cur+1);
					buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
					mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JP,0);
				}
			} else {
				if (strncmp(str,cur,"ᆯ로",0,3)==0) {
					new_str=replace(str,cur,"ᆺ");
					new_str=insert(new_str,cur+1,"으");
					buf = new_str.substring(0,cur+1);
					buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
					mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);

				} else if (str.charAt(cur)=='ᆯ'||str.charAt(cur)=='ᆫ') {
					if (str.length() != cur + 1) {
						new_str=insert(str,cur,"ᆺ이");
						buf = new_str.substring(0,cur+1);
						buf2 = new_str.substring(cur+1);
						// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
						mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JP,0);
					}

					new_str=insert(str,cur,"ᆺ으");
					buf = new_str.substring(0,cur+1);
					buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
					mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
				} else {
					new_str=insert(str,cur,"ᆺ이");
					buf = new_str.substring(0,cur+1);
					buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
					mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JP,0);
				}
			}
		} 
	}

	/**
	 * It expands the morpheme chart regarding the rule '이'.
	 * @param from - the start index for the segment position
	 * @param prev - the passed part of the string
	 * @param str - the next part of the string to check
	 * @param cur - the current index of the string for checking the rules
	 */
	private void rule_i(int from, String prev, String str,int cur) {
		String buf;
		String buf2;
		String new_str;

		if (cur+2 > str.length()) {
			return;
		}

		if ((prev!=null&&prev.length() != 0&&cur==0)
				&&pcheck(prev,prev.length()-1,"중성")!=0) {

			if (strncmp(str,0,"여",0,2)==0) {
				new_str=replace(str,cur+1,"ᅥ");
				new_str=insert(new_str,cur+1,"ᅵᄋ");
				buf = new_str.substring(0,cur+2);
				buf2 = new_str.substring(cur+2);
				// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
				mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_JP,TagSet.TAG_TYPE_EOMIES,0);
			} else {
				if (pcheck(str,0,"종성")!=0||
						strncmp(str,0,"는",0,3)==0||strncmp(str,0,"은",0,3)==0||
						strncmp(str,0,"음",0,3)==0||strncmp(str,2,"는",0,3)==0) 
					return;
				// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
				mc.phonemeChange(from,"이",str,TagSet.TAG_TYPE_JP,TagSet.TAG_TYPE_EOMIES,0);
				buf = "이" + str;
				rule_eomi_u(from,prev,buf,cur+2);
			}
		}
	}

	/**
	 * It expands the morpheme chart regarding the irregular rules about 'ㄷ', 'ㅅ', 'ㅂ', 'ㅎ', '르', '러'.
	 * @param from - the start index for the segment position
	 * @param prev - the passed part of the string
	 * @param str - the next part of the string to check
	 * @param cur - the current index of the string for checking the rules
	 */
	private void rule_irr_word(int from,String prev,String str,int cur) {
		String buf;
		String buf2;
		String new_str;
		int len = str.length();

		/* 'ᆮ' irregular rule */
		if ((cur>0&&cur<=len&&pcheck(str,cur-1,"l21")!=0)
				&&pcheck(str,cur,"21")!=0
				&&pcheck(str,cur+1,"r21")!=0) {
			new_str = replace(str,cur-1,"ᆮ");
			buf = new_str.substring(0,cur);
			buf2 = new_str.substring(cur);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_D);
		}

		/* 'ᆺ' irregular rule */
		if ((cur>0&&cur<len&&pcheck(str,cur-1,"l22")!=0)
				&&pcheck(str,cur,"22")!=0
				&&pcheck(str,cur+1,"r22")!=0) {
			new_str=insert(str,cur,"ᆺ");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_S);
		}

		/* 'ㅂ' irregular rule */
		if ((cur>0&&cur<=len&&pcheck(str,cur-1,"l23")!=0)
				&&pcheck(str,cur,"23")!=0
				&&pcheck(str,cur+1,"r23")!=0) {
			new_str=replace(str,cur,"ᅳ");
			new_str=insert(new_str,cur-1,"ᆸ");
			buf = new_str.substring(0,cur);
			buf2 = new_str.substring(cur);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_B);
		}

		/* 'ᆸ' irregular rule */
		if ((cur>0&&cur<=len&&pcheck(str,cur-1,"l24")!=0)
				&&pcheck(str,cur,"24")!=0
				&&pcheck(str,cur+1,"r24")!=0) {
			new_str=replace(str,cur,"ᅥ");
			new_str=insert(new_str,cur-1,"ᆸ");
			buf = new_str.substring(0,cur);
			buf2 = new_str.substring(cur);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_B);
		}

		/* 'ㅂ' irregular rule */
		if ((cur>0&&cur<=len&&pcheck(str,cur-1,"l25")!=0)
				&&pcheck(str,cur,"25")!=0
				&&pcheck(str,cur+1,"r25")!=0) {
			new_str=replace(str,cur,"ᅥ");
			new_str=insert(new_str,cur-1,"ᆸ");
			buf = new_str.substring(0,cur);
			buf2 = new_str.substring(cur);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_B);
		}

		/* 'ᇂ' irregular rule */
		if ((cur>0&&cur+1<len&&pcheck(str,cur-1,"l26")!=0)
				&&pcheck(str,cur,"26")!=0
				&&pcheck(str,cur+1,"r26")!=0) {
			new_str=insert(str,cur+1,"ᇂ으");
			buf = new_str.substring(0,cur+2);
			buf2 = new_str.substring(cur+2);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_H);
		}

		/* 'ㅎ' irregular rule */
		if ((cur>0&&cur+1<len&&pcheck(str,cur-1,"l27")!=0)
				&&pcheck(str,cur,"27")!=0
				&&pcheck(str,cur+1,"r27")!=0) {
			if (str.charAt(cur)=='ᅢ') {
				new_str=replace(str,cur,"ᅡ");
			} else {
				new_str=replace(str,cur,"ᅣ");
			}
			new_str=insert(new_str,cur+1,"ᇂ어");
			buf = new_str.substring(0,cur+2);
			buf2 = new_str.substring(cur+2);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_H);
			//			이운재 추가
			if (str.charAt(cur)=='ᅢ') {
				new_str = replace(str,cur,"ᅥ");
			} else {
				new_str = replace(str,cur,"ᅧ");
			}
			new_str=insert(new_str,cur+1,"ᇂ어");
			buf = new_str.substring(0,cur+2);
			buf2 = new_str.substring(cur+2);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_H);
		}
		
		/* 'ㅎ' irregular rule */
		if ((cur>0&&cur+1<len&&pcheck(str,cur-1,"l28")!=0)
				&&pcheck(str,cur,"28")!=0
				&&pcheck(str,cur+1,"r28")!=0) {
			new_str=replace(str,cur,"ᅥ");
			new_str=insert(new_str,cur+1,"ᇂᄋ");
			buf = new_str.substring(0,cur+2);
			buf2 = new_str.substring(cur+2);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_H);
		}


		/* '르' irregular rule */
		if ((cur>0&&cur<len&&pcheck(str,cur-1,"l29")!=0)
				&&pcheck(str,cur,"29")!=0
				&&pcheck(str,cur+1,"r29")!=0) {
			new_str = replace(str,cur,"ᅳ");
			if (new_str.charAt(cur+1)=='ᅡ') 
				new_str = new_str.substring(0, cur+1) + 'ᅥ' + new_str.substring(cur+2);
			new_str = insert(new_str,cur+1,"ᄋ");
			new_str = new_str.substring(0, cur-1) + Code.toChoseong(new_str.charAt(cur-1)) + new_str.substring(cur);

			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_REU);
		}
		
		/* '러' irregular rule */
		if ((cur>0&&cur<=len&&pcheck(str,cur-1,"l30")!=0)
				&&pcheck(str,cur,"30")!=0
				&&pcheck(str,cur+1,"r30")!=0&&(cur-2>=0&&str.charAt(cur-2)=='ᄅ')) {
			new_str=replace(str,cur,"ᄋ");
			buf = new_str.substring(0,cur);
			buf2 = new_str.substring(cur);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_REO);
		}
	}

	/**
	 * It expands the morpheme chart regarding the irregular rules about '우', '여'.
	 * @param from - the start index for the segment position
	 * @param prev - the passed part of the string
	 * @param str - the next part of the string to check
	 * @param cur - the current index of the string for checking the rules
	 */
	private void rule_irr_word2(int from,String prev,String str,int cur)
	{
		String buf;
		String buf2;
		String new_str;

		if (cur >= str.length()) {
			return;
		}

		/* '우' irregular rule */
		if ((cur>0&&pcheck(str,cur-1,"l31")!=0)
				&&pcheck(str,cur,"31")!=0
				&&pcheck(str,cur+1,"r31")!=0) {
			new_str=replace(str,cur,"ᅮ");
			new_str=insert(new_str,cur+1,"어");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}

		/* '여' irregular rule */
		if ((cur>0&&pcheck(str,cur-1,"l32")!=0)
				&&pcheck(str,cur,"32")!=0
				&&pcheck(str,cur+1,"r32")!=0&&str.charAt(cur+2)=='ᅧ') {
			new_str=replace(str,cur+2,"ᅥ");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}

		/* '여' irregular rule */
		if ((cur>0&&pcheck(str,cur-1,"l33")!=0)
				&&pcheck(str,cur,"33")!=0
				&&pcheck(str,cur+1,"r33")!=0) {
			new_str=replace(str,cur,"ᅡ");
			new_str=insert(new_str,cur+1,"어");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}
	}

	/**
	 * It expands the morpheme chart regarding the vowel harmony rules.
	 * @param from - the start index for the segment position
	 * @param prev - the passed part of the string
	 * @param str - the next part of the string to check
	 * @param cur - the current index of the string for checking the rules
	 */
	private void rule_johwa(int from,String prev,String str,int cur)	{
		String buf;
		String buf2;
		String new_str;
		if (cur>0&&pcheck(str,cur-1,"양성모음")!=0) {
			if (cur+2<str.length()&&str.charAt(cur+1)=='ᄋ'&&str.charAt(cur+2)=='ᅡ') {
				new_str=replace(str,cur+2,"ᅥ");
				buf = new_str.substring(0,cur+1);
				buf2 = new_str.substring(cur+1);
				// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
				mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
			}
			else if (cur+1<str.length()&&str.charAt(cur)=='ᄋ'&&str.charAt(cur+1)=='ᅡ') {
				new_str=replace(str,cur+1,"ᅥ");
				buf = new_str.substring(0,cur);
				buf2 = new_str.substring(cur);
				// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
				mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
			}
		}
	}

	/**
	 * It expands the morpheme chart regarding the rules about personal pronoun.
	 * @param from - the start index for the segment position
	 * @param prev - the passed part of the string
	 * @param str - the next part of the string to check
	 */
	private void rule_NP(int from,String prev,String str) {
		String buf;

		if (strncmp(str,0,"내가",0,4)==0) {
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"나",str+2,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strncmp(str,0,"네가",0,4)==0) {
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"너",str+2,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strncmp(str,0,"제가",0,4)==0) {
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"저",str+2,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strcmp(str,0,"내",0)==0) {
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"나","의",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strcmp(str,0,"네",0)==0) {
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"너","의",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strcmp(str,0,"제",0)==0) {
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"저","의",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strncmp(str,0,"내게",0,4)==0) {
			buf = "에" + str.substring(2);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"나",buf,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strncmp(str,0,"네게",0,4)==0) {
			buf = "에" + str.substring(2);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"너",buf,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strncmp(str,0,"제게",0,4)==0) {
			buf = "에" + str.substring(2);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"저",buf,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strncmp(str,0,"나",0,2)==0) {
			if (str.length()==3&&str.charAt(2)=='ᆫ') {
				// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
				mc.phonemeChange(from,"나","는",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
			} else if (str.length() == 3&&str.charAt(2)=='ᆯ') {
				// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
				mc.phonemeChange(from,"나","를",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
			}
		} else if (strncmp(str,0,"너",0,2)==0) {
			if (str.length() == 3&&str.charAt(2)=='ᆫ') {
				// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
				mc.phonemeChange(from,"너","는",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
			} else if (str.length() == 3&&str.charAt(2)=='ᆯ') {
				// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
				mc.phonemeChange(from,"너","를",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
			}
		} else if (strncmp(str,0,"누구",0,4)==0) {
			if (str.length() == 5&&str.charAt(4)=='ᆫ') {
				// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
				mc.phonemeChange(from,"누구","는",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
			} else if (str.length() == 5&&str.charAt(4)=='ᆯ') {
				// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
				mc.phonemeChange(from,"누구","를",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
			}
		} else if (strcmp(str,0,"무언가",0)==0) {
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"무엇","인가",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		}
	}

	/**
	 * It expands the morpheme chart regarding the elision rules about 'ㄹ', 'ㅡ', 'ㅏ', 'ㅓ'.
	 * @param from - the start index for the segment position
	 * @param prev - the passed part of the string
	 * @param str - the next part of the string to check
	 * @param cur - the current index of the string for checking the rules
	 */
	private void rule_rem(int from,String prev,String str,int cur) {
		String buf;
		String buf2;
		String new_str;

		if (cur >= str.length()) {
			return;
		}

		/* 'ㄹ' elision rule */
		if ((cur>0&&pcheck(str,cur-1,"l11")!=0)
				&&(pcheck(str,cur,"11")!=0 || strncmp(str,cur,"오",0,2)==0)
				&&pcheck(str,cur+1,"r11")!=0) {

			String buf3;
			new_str=insert(str,cur,"ᆯ");
			buf3 = new_str;

			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
			rule_eomi_u(from,prev,buf3,cur+1);
		}

		/* 'ㅡ' elision rule */
		if ((cur>0&&pcheck(str,cur-1,"l12")!=0)
				&&pcheck(str,cur,"12")!=0
				&&pcheck(str,cur+1,"r12")!=0
				||(cur==1&&str.charAt(cur)!='ᅡ')
		) {
			new_str = replace(str,cur,"ᅥ");
			new_str = insert(new_str,cur,"ᅳᄋ");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}

		/* 'ㅏ' elision rule */
		if ((cur>0&&pcheck(str,cur-1,"l13")!=0)
				&&pcheck(str,cur,"13")!=0 
				&&pcheck(str,cur+1,"r13")!=0) {
			new_str = insert(str,cur+1,"어");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}

		/* 'ㅓ' elision rule */
		if ((cur>0&&pcheck(str,cur-1,"l14")!=0)
				&&pcheck(str,cur,"14")!=0
				&&pcheck(str,cur+1,"r14")!=0) {
			new_str=insert(str,cur+1,"어");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}
	}

	/**
	 * It expands the morpheme chart regarding the contration rules about 'ㅗ', 'ㅜ', 'ㅚ', 'ㅣ'.
	 * @param from - the start index for the segment position
	 * @param prev - the passed part of the string
	 * @param str - the next part of the string to check
	 * @param cur - the current index of the string for checking the rules
	 */
	private void rule_shorten(int from,String prev,String str,int cur)
	{
		String buf;
		String buf2;
		String new_str;
		if (cur >= str.length()) {
			return;
		}

		/* 'ㅗ', 'ㅜ' contraction rule */
		if ((cur>0&&pcheck(str,cur-1,"l51")!=0)
				&&pcheck(str,cur,"51")!=0
				&&pcheck(str,cur+1,"r51")!=0) {
			if (str.charAt(cur)=='ᅪ') {
				new_str=replace(str,cur,"ᅩ");
			} else {
				new_str=replace(str,cur,"ᅮ");
			}
			new_str=insert(new_str,cur+1,"어");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}

		/* 'ㅚ' contraction rule */
		if ((cur>0&&pcheck(str,cur-1,"l52")!=0)
				&&pcheck(str,cur,"52")!=0
				&&pcheck(str,cur+1,"r52")!=0) {
			new_str=replace(str,cur,"ᅬ");
			new_str=insert(new_str,cur+1,"어");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
			// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}

		/* 'ㅣ' contraction rule */
		if (cur>0)
		{
			if (((cur>1 || (str.charAt(cur-1)!='ᄋ'))&&pcheck(str,cur-1,"l53")!=0)
					&&pcheck(str,cur,"53")!=0
					&&pcheck(str,cur+1,"r53")!=0) {

				new_str=replace(str,cur,"ᅵ");
				new_str=insert(new_str,cur+1,"어");
				buf = new_str.substring(0,cur+1);
				buf2 = new_str.substring(cur+1);
				// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
				mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
			}
		}
	}

	/**
	 * C style string compare method.
	 * @param s1 - string 1
	 * @param i1 - start index of string 1 for comparing
	 * @param s2 - string 2
	 * @param i2 - strart index of string 2 for comparing
	 * @return 0 : equal, > 0 : string 1 is higher in alphabetical order, < 0 : string 1 is lower in alphabetical order
	 */
	private int strcmp(String s1, int i1, String s2, int i2) {
		int l1 = s1.length() - i1;
		int l2 = s2.length() - i2;

		int len = l1;
		boolean diff = false;

		if (len > l2) {
			len = l2;
		}

		while (len-- > 0) {
			if (s1.charAt(i1++) != s2.charAt(i2++)) {
				diff = true;
				break;
			}
		}

		if (diff == false && l1 != l2) {
			if (l1 > l2) {
				return s1.charAt(i1);
			} else {
				return -s2.charAt(i2);
			}
		}
		return s1.charAt(i1-1) - s2.charAt(i2-1);
	}

	/**
	 * C style string compare method for the specified length.
	 * @param s1 - string 1
	 * @param i1 - start index of string 1 for comparing
	 * @param s2 - string 2
	 * @param i2 - strart index of string 2 for comparing
	 * @param len - the number of characters to compare
	 * @return 0 : equal, > 0 : string 1 is higher in alphabetical order, < 0 : string 1 is lower in alphabetical order
	 */
	private int strncmp(String s1, int i1, String s2, int i2, int len) {
		if (s1.length() - i1 < len) {
			return 1;
		} else if (s2.length() - i2 < len) {
			return -1;
		}
		while (len-- > 0) {
			if (s1.charAt(i1++) != s2.charAt(i2++)) {
				break;
			}
		}
		return s1.charAt(i1-1) - s2.charAt(i2-1);
	}
}


