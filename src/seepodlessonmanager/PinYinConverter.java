/*
 * 
 * Modified from the original by Derek Chadwick for inclusion in Seepod Lesson Manager
 */

package seepodlessonmanager;

/**
 * Copyright 2009 Jee Vang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Converts PinYin with numbers to tones.
 *
 * @author Jee Vang
 *
 */
public class PinYinConverter
{

	/**
	 * Tokens to ignore.
	 */
	private static final String[] ignoreTokens = {
		",", " "
	};

	public String convert(String str) throws Exception {
		StringBuffer sb = new StringBuffer();
		String[] words = getWords(str);
		for(int i=0; i < words.length; i++) {
			String syllable = words[i];

			if(ignoreToken(syllable)) {
				sb.append(syllable);
			} else {
				String formatted = convertNumberToTone(syllable);
				sb.append(formatted);
			}

			if(i < words.length-1) {
				sb.append(' ');
			}
		}
		return sb.toString();
	}

	/**
	 * Check to see if a token should be ignored for conversion.
	 * @param token Token.
	 * @return A boolean indicating if token should be ignored for conversion.
	 */
	public boolean ignoreToken(String token) {
		for(int i=0; i < ignoreTokens.length; i++) {
			if(ignoreTokens[i].equals(token)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@link http://www.pinyin.info/rules/where.html}
	 * {@link http://en.wikipedia.org/wiki/Pinyin}
	 * @param syllable Syllable.
	 * @return
	 */
	public String convertNumberToTone(String syllable) {
		int tone = getTone(syllable);

		if(PinYin.TONE5 == tone) {
			String s = formatv(syllable);
			s = removeNumbers(s);
			return s;
		}

		int indexOfVowel = getIndexOfVowel(syllable);
		char vowel = syllable.charAt(indexOfVowel);
		char replacementVowel = getReplacementVowel(syllable, vowel, tone);

		StringBuffer sb = new StringBuffer();
		int length = syllable.length();
		for(int i=0; i < length; i++) {
			char ch = syllable.charAt(i);
			if(i == indexOfVowel) {
				sb.append(replacementVowel);
			} else {
				sb.append(ch);
			}
		}

		String s = sb.toString();
		s = formatv(s);
		s = removeColonCharacters(s);
		s = removeNumbers(s);

		return s;
	}

	/**
	 * Format u: to ü.
	 * @param syllable Syllable.
	 * @return Formatted syllable.
	 */
	public String formatv(String syllable) {
		int index = syllable.indexOf(':');
		if(-1 == index) {
			return syllable;
		}

		char ch = syllable.charAt(index-1);
		if(PinYin.v1 == ch || PinYin.v2 == ch || PinYin.v3 == ch || PinYin.v4 == ch ||
				PinYin.V1 == ch || PinYin.V2 == ch || PinYin.V3 == ch || PinYin.V4 == ch
				) {
			return syllable;
		}

		StringBuffer sb = new StringBuffer();
		int length = syllable.length();
		for(int i=0; i < length; i++) {
			char c = syllable.charAt(i);
			if(i == index-1) {
				if('u' == c) {
					sb.append(PinYin.v);
					continue;
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * Remove tone numbers from a syllable.
	 * @param syllable Syllable.
	 * @return Syllable with tone numbers removed.
	 */
	public String removeNumbers(String syllable) {
		StringBuffer sb = new StringBuffer();
		int length = syllable.length();
		for(int i=0; i < length; i++) {
			char ch = syllable.charAt(i);
			try {
				String s = ""+ch;
				Integer.parseInt(s);
			} catch(NumberFormatException nfe) {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	/**
	 * Remove colon characters.
	 * @param syllable Syllable.
	 * @return The syllable without the colon.
	 */
	public String removeColonCharacters(String syllable) {
		StringBuffer sb = new StringBuffer();
		int length = syllable.length();
		for(int i=0; i < length; i++) {
			char ch = syllable.charAt(i);
			if(':' == ch) {
				continue;
			}
			sb.append(ch);
		}
		return sb.toString();
	}

	/**
	 * Get the replacement vowel.
	 * @param syllable Syllable.
	 * @param vowel Vowel to replace.
	 * @param tone Tone of vowel.
	 * @return Replacement vowel.
	 */
	public char getReplacementVowel(String syllable, char vowel, int tone) {
		//check the upper case vowels
		if(PinYin.VOWEL_A == vowel) {
			if(PinYin.TONE1 == tone) {
				return PinYin.A1;
			}
			if(PinYin.TONE2 == tone) {
				return PinYin.A2;
			}
			if(PinYin.TONE3 == tone) {
				return PinYin.A3;
			}
			if(PinYin.TONE4 == tone) {
				return PinYin.A4;
			}
		}

		if(PinYin.VOWEL_E == vowel) {
			if(PinYin.TONE1 == tone) {
				return PinYin.E1;
			}
			if(PinYin.TONE2 == tone) {
				return PinYin.E2;
			}
			if(PinYin.TONE3 == tone) {
				return PinYin.E3;
			}
			if(PinYin.TONE4 == tone) {
				return PinYin.E4;
			}
		}

		if(PinYin.VOWEL_I == vowel) {
			if(PinYin.TONE1 == tone) {
				return PinYin.I1;
			}
			if(PinYin.TONE2 == tone) {
				return PinYin.I2;
			}
			if(PinYin.TONE3 == tone) {
				return PinYin.I3;
			}
			if(PinYin.TONE4 == tone) {
				return PinYin.I4;
			}
		}

		if(PinYin.VOWEL_O == vowel) {
			if(PinYin.TONE1 == tone) {
				return PinYin.O1;
			}
			if(PinYin.TONE2 == tone) {
				return PinYin.O2;
			}
			if(PinYin.TONE3 == tone) {
				return PinYin.O3;
			}
			if(PinYin.TONE4 == tone) {
				return PinYin.O4;
			}
		}

		if(-1 == syllable.indexOf(':')) {
			if(PinYin.VOWEL_U == vowel) {
				if(PinYin.TONE1 == tone) {
					return PinYin.U1;
				}
				if(PinYin.TONE2 == tone) {
					return PinYin.U2;
				}
				if(PinYin.TONE3 == tone) {
					return PinYin.U3;
				}
				if(PinYin.TONE4 == tone) {
					return PinYin.U4;
				}
			}
		}

		if(PinYin.VOWEL_V == vowel) {
			if(PinYin.TONE1 == tone) {
				return PinYin.V1;
			}
			if(PinYin.TONE2 == tone) {
				return PinYin.V2;
			}
			if(PinYin.TONE3 == tone) {
				return PinYin.V3;
			}
			if(PinYin.TONE4 == tone) {
				return PinYin.V4;
			}
		}

		//now check the lower case vowels
		if(PinYin.VOWEL_a == vowel) {
			if(PinYin.TONE1 == tone) {
				return PinYin.a1;
			}
			if(PinYin.TONE2 == tone) {
				return PinYin.a2;
			}
			if(PinYin.TONE3 == tone) {
				return PinYin.a3;
			}
			if(PinYin.TONE4 == tone) {
				return PinYin.a4;
			}
		}

		if(PinYin.VOWEL_e == vowel) {
			if(PinYin.TONE1 == tone) {
				return PinYin.e1;
			}
			if(PinYin.TONE2 == tone) {
				return PinYin.e2;
			}
			if(PinYin.TONE3 == tone) {
				return PinYin.e3;
			}
			if(PinYin.TONE4 == tone) {
				return PinYin.e4;
			}
		}

		if(PinYin.VOWEL_i == vowel) {
			if(PinYin.TONE1 == tone) {
				return PinYin.i1;
			}
			if(PinYin.TONE2 == tone) {
				return PinYin.i2;
			}
			if(PinYin.TONE3 == tone) {
				return PinYin.i3;
			}
			if(PinYin.TONE4 == tone) {
				return PinYin.i4;
			}
		}

		if(PinYin.VOWEL_o == vowel) {
			if(PinYin.TONE1 == tone) {
				return PinYin.o1;
			}
			if(PinYin.TONE2 == tone) {
				return PinYin.o2;
			}
			if(PinYin.TONE3 == tone) {
				return PinYin.o3;
			}
			if(PinYin.TONE4 == tone) {
				return PinYin.o4;
			}
		}

		if(-1 == syllable.indexOf(':')) {
			if(PinYin.VOWEL_u == vowel) {
				if(PinYin.TONE1 == tone) {
					return PinYin.u1;
				}
				if(PinYin.TONE2 == tone) {
					return PinYin.u2;
				}
				if(PinYin.TONE3 == tone) {
					return PinYin.u3;
				}
				if(PinYin.TONE4 == tone) {
					return PinYin.u4;
				}
			}
		}

		if(PinYin.VOWEL_v == vowel) {
			if(PinYin.TONE1 == tone) {
				return PinYin.v1;
			}
			if(PinYin.TONE2 == tone) {
				return PinYin.v2;
			}
			if(PinYin.TONE3 == tone) {
				return PinYin.v3;
			}
			if(PinYin.TONE4 == tone) {
				return PinYin.v4;
			}
		}

		return ' ';
	}

	/**
	 * Get the index of the vowel in the syllable.
	 * @param syllable Syllable.
	 * @return Index of vowel.
	 */
	public int getIndexOfVowel(String syllable) {
		int index = -1;

		//A and e trump all other vowels and always take the tone mark.
		//There are no Mandarin syllables in Hanyu Pinyin that contain
		//both a and e.
		boolean hasAa = hasVowelAa(syllable);
		boolean hasEe = hasVowelEe(syllable);
		if(hasAa || hasEe) {
			if(hasAa) {
				index = getIndexOfA(syllable);
				if(-1 == index) {
					index = getIndexOfa(syllable);
				}
				return index;
			} else {
				index = getIndexOfE(syllable);
				if(-1 == index) {
					index = getIndexOfe(syllable);
				}
				return index;
			}
		}

		//In the combination ou, o takes the mark.
		boolean hasOo = hasVowelOo(syllable);
		boolean hasUu = hasVowelUu(syllable);
		if(hasOo && hasUu) {
			index = getIndexOfO(syllable);
			if(-1 == index) {
				index = getIndexOfo(syllable);
			}
			return index;
		}

		//In all other cases, the final vowel takes the mark.
		index = getIndexOfLastVowel(syllable);
		return index;
	}

	/**
	 * Get the index of the last vowel.
	 * @param syllable Syllable.
	 * @return Index of last vowel.
	 */
	public int getIndexOfLastVowel(String syllable) {
		int index = -1;
		int length = syllable.length();
		for(int i=length-1; i >= 0; i--) {
			char c = syllable.charAt(i);
			if(
					PinYin.VOWEL_a == c ||
					PinYin.VOWEL_A == c ||
					PinYin.VOWEL_e == c ||
					PinYin.VOWEL_E == c ||
					PinYin.VOWEL_i == c ||
					PinYin.VOWEL_I == c ||
					PinYin.VOWEL_o == c ||
					PinYin.VOWEL_O == c ||
					PinYin.VOWEL_u == c ||
					PinYin.VOWEL_U == c ||
					':' == c
					) {
				if(':' == c) {
					return i-1;
				}
				return i;
			}
		}
		return index;
	}

	/**
	 * Get the index of A.
	 * @param syllable Syllable.
	 * @return Index of A.
	 */
	public int getIndexOfA(String syllable) {
		return getIndexOfVowel(syllable, 'A');
	}

	/**
	 * Get the index of E.
	 * @param syllable Syllable.
	 * @return Index of E.
	 */
	public int getIndexOfE(String syllable) {
		return getIndexOfVowel(syllable, 'E');
	}

	/**
	 * Get the index of I.
	 * @param syllable Syllable.
	 * @return Index of I.
	 */
	public int getIndexOfI(String syllable) {
		return getIndexOfVowel(syllable, 'I');
	}

	/**
	 * Get the index of O.
	 * @param syllable Syllable.
	 * @return Index of O.
	 */
	public int getIndexOfO(String syllable) {
		return getIndexOfVowel(syllable, 'O');
	}

	/**
	 * Get the index of U.
	 * @param syllable Syllable.
	 * @return Index of U.
	 */
	public int getIndexOfU(String syllable) {
		return getIndexOfVowel(syllable, 'U');
	}

	/**
	 * Get the index of U:.
	 * @param syllable Syllable.
	 * @return Index of U:.
	 */
	public int getIndexOfV(String syllable) {
		return getIndexOfVowel(syllable, 'U');
	}

	/**
	 * Get the index of a.
	 * @param syllable Syllable.
	 * @return Index of a.
	 */
	public int getIndexOfa(String syllable) {
		return getIndexOfVowel(syllable, 'a');
	}

	/**
	 * Get the index of e.
	 * @param syllable Syllable.
	 * @return Index of e.
	 */
	public int getIndexOfe(String syllable) {
		return getIndexOfVowel(syllable, 'e');
	}

	/**
	 * Get the index of i.
	 * @param syllable Syllable.
	 * @return Index of i.
	 */
	public int getIndexOfi(String syllable) {
		return getIndexOfVowel(syllable, 'i');
	}

	/**
	 * Get the index of o.
	 * @param syllable Syllable.
	 * @return Index of o.
	 */
	public int getIndexOfo(String syllable) {
		return getIndexOfVowel(syllable, 'o');
	}

	/**
	 * Get the index of u.
	 * @param syllable Syllable.
	 * @return Index of u.
	 */
	public int getIndexOfu(String syllable) {
		return getIndexOfVowel(syllable, 'u');
	}

	/**
	 * Get the index of u:.
	 * @param syllable Syllable.
	 * @return Index of u:.
	 */
	public int getIndexOfv(String syllable) {
		return getIndexOfVowel(syllable, 'u');
	}

	/**
	 * Get the index of a vowel from the syllable.
	 * @param syllable Syllable.
	 * @param vowel Vowel.
	 * @return Index of vowel.
	 */
	public int getIndexOfVowel(String syllable, char vowel) {
		int index = -1;
		if(-1 != (index = syllable.indexOf(vowel))) {
			return index;
		}
		return index;
	}

	/**
	 * Check if the syllable has the vowel, A or a.
	 * @param syllable Syllable.
	 * @return A boolean indicating if syllable has the vowel, a.
	 */
	public boolean hasVowelAa(String syllable) {
		if(hasVowel(syllable, PinYin.VOWEL_A) || hasVowel(syllable, PinYin.VOWEL_a)) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the syllable has the vowel, E or e.
	 * @param syllable Syllable.
	 * @return A boolean indicating if syllable has the vowel, e.
	 */
	public boolean hasVowelEe(String syllable) {
		if(hasVowel(syllable, PinYin.VOWEL_E) || hasVowel(syllable, PinYin.VOWEL_e)) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the syllable has the vowel, I or i.
	 * @param syllable Syllable.
	 * @return A boolean indicating if syllable has the vowel, i.
	 */
	public boolean hasVowelIi(String syllable) {
		if(hasVowel(syllable, PinYin.VOWEL_I) || hasVowel(syllable, PinYin.VOWEL_i)) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the syllable has the vowel, O or o.
	 * @param syllable Syllable.
	 * @return A boolean indicating if syllable has the vowel, o.
	 */
	public boolean hasVowelOo(String syllable) {
		if(hasVowel(syllable, PinYin.VOWEL_O) || hasVowel(syllable, PinYin.VOWEL_o)) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the syllable has the vowel, U or u.
	 * @param syllable Syllable.
	 * @return A boolean indicating if syllable has the vowel, u.
	 */
	public boolean hasVowelUu(String syllable) {
		if(hasVowel(syllable, PinYin.VOWEL_U) || hasVowel(syllable, PinYin.VOWEL_u)) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the syllable has the vowel, U: or u:.
	 * @param syllable Syllable.
	 * @return A boolean indicating if syllable has the vowel, u:.
	 */
	public boolean hasVowelVv(String syllable) {
		if(hasVowel(syllable, PinYin.VOWEL_V) && hasVowel(syllable, ':')) {
			return true;
		}

		if(hasVowel(syllable, PinYin.VOWEL_v) && hasVowel(syllable, ':')) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if a syllable has a vowel.
	 * @param syllable Syllable.
	 * @param vowel Vowel: a, e, i, o, u, u:
	 * @return A boolean indicating if a syllable has the specified
	 * vowel.
	 */
	public boolean hasVowel(String syllable, char vowel) {
		int index = syllable.indexOf(vowel);
		if(-1 == index) {
			return false;
		}
		return true;
	}

	/**
	 * Get the tone from a syllable.
	 * @param syllable A syllable with a number
	 * to indicate the tone. i.e. wo3, ni3, shi4.
	 * @return The tone number. Returns -1 if no tone number
	 * is detected.
	 */
	public int getTone(String syllable) {
		char c = syllable.charAt(syllable.length()-1);
		try {
			String s = new String(""+c);
			int tone = Integer.parseInt(s);
			return tone;
		} catch(NumberFormatException nfe) {

		}
		return -1;
	}

	/**
	 * Get the words from a string delimited by spaces.
	 * @param str A string.
	 * @return Tokens in the string.
	 */
	public String[] getWords(String str) {
		List list = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(str, " ");
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();
			list.add(token);
		}
		String[] tokens =
			(String[])list.toArray(new String[list.size()]);
		return tokens;
	}
}
