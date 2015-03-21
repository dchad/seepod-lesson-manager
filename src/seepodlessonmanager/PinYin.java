
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

/**
 * PinYin interface holding some constants.
 *
 * @author Jee Vang
 *
 */
public interface PinYin {
	//these are the tones
	//the fifth tone is a neutral tone and has no tone mark, http://cc-cedict.org/wiki/format:syntax
	public static final int TONE1 = 1;
	public static final int TONE2 = 2;
	public static final int TONE3 = 3;
	public static final int TONE4 = 4;
	public static final int TONE5 = 5;

	//these are the vowels (upper case) in Mandarin PinYin
	public static final char VOWEL_A = 'A';
	public static final char VOWEL_E = 'E';
	public static final char VOWEL_I = 'I';
	public static final char VOWEL_O = 'O';
	public static final char VOWEL_U = 'U';
	public static final char VOWEL_V = 'U';

	//these are the vowels (lower case) in Mandarin PinYin
	public static final char VOWEL_a = 'a';
	public static final char VOWEL_e = 'e';
	public static final char VOWEL_i = 'i';
	public static final char VOWEL_o = 'o';
	public static final char VOWEL_u = 'u';
	public static final char VOWEL_v = 'u';

	//these are the vowels (upper and lower case) in Mandarin PinYin
	//notice how v and V are used to represent u: and U:
	public static final char A1 = 'Ā';
	public static final char A2 = 'Á';
	public static final char A3 = 'Ǎ';
	public static final char A4 = 'À';
	public static final char E1 = 'Ē';
	public static final char E2 = 'É';
	public static final char E3 = 'Ě';
	public static final char E4 = 'È';
	public static final char I1 = 'Ī';
	public static final char I2 = 'Í';
	public static final char I3 = 'Ǐ';
	public static final char I4 = 'Ì';
	public static final char O1 = 'Ō';
	public static final char O2 = 'Ó';
	public static final char O3 = 'Ǒ';
	public static final char O4 = 'Ò';
	public static final char U1 = 'Ū';
	public static final char U2 = 'Ú';
	public static final char U3 = 'Ǔ';
	public static final char U4 = 'Ù';
	public static final char V1 = 'Ǖ';
	public static final char V2 = 'Ǘ';
	public static final char V3 = 'Ǚ';
	public static final char V4 = 'Ǜ';
	public static final char a1 = 'ā';
	public static final char a2 = 'á';
	public static final char a3 = 'ǎ';
	public static final char a4 = 'à';
	public static final char e1 = 'ē';
	public static final char e2 = 'é';
	public static final char e3 = 'ě';
	public static final char e4 = 'è';
	public static final char i1 = 'ī';
	public static final char i2 = 'í';
	public static final char i3 = 'ǐ';
	public static final char i4 = 'ì';
	public static final char o1 = 'ō';
	public static final char o2 = 'ó';
	public static final char o3 = 'ǒ';
	public static final char o4 = 'ò';
	public static final char u1 = 'ū';
	public static final char u2 = 'ú';
	public static final char u3 = 'ǔ';
	public static final char u4 = 'ù';
	public static final char v = 'ü';
	public static final char v1 = 'ǖ';
	public static final char v2 = 'ǘ';
	public static final char v3 = 'ǚ';
	public static final char v4 = 'ǜ';
}