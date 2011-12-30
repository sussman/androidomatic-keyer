/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/* Note that this class was stolen^H^H^H copied from the Android developer doc examples
   and hacked on quite a bit.  Hooray for Apache-licensed examples! */
package com.templaro.opsiz.aka;

import java.util.HashMap;

/** Class that implements the text to morse code conversion */
class MorseConverter {
	
	private static final HashMap<Character, MorseBit[]> morse_map = new HashMap<Character, MorseBit[]>(){
        {
        	put('A', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('B', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('C', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('D', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT });
        	put('E', new MorseBit[] { MorseBit.DOT });
        	put('F', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('G', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT });
        	put('H', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('I', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('J', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('K', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH });
        	put('L', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('M', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('N', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('O', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH });
        	put('P', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT});
        	put('Q', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('R', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT});
        	put('S', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT });
        	put('T', new MorseBit[] { MorseBit.DASH });
        	put('U', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH });
        	put('V', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('W', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH });
        	put('X', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('Y', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('Z', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('0', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('1',new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('2', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('3', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('4', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('5', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('6', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('7', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('8',new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('9',new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP,
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('/', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT});
        	put('.', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DASH});
        	put(',', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT,MorseBit.GAP, MorseBit.DASH, 
        			MorseBit.GAP, MorseBit.DASH});
        	put('?', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DOT});
        	//BT
        	put('=', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH});
        	put('-', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH});
        	//AR
        	put('+', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT});
        	//KN
        	put('~', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT});
        	//SK
        	put('*', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DASH });	
        	
        	//Latin Non-English Extensions

        	put('Ä', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('Æ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('Ą', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('À', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH});
        	put('Å', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH});
        	put('Ç', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('Ĉ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('Ć', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('Š', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('Ð', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('Ś', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT});
        	put('È', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('Ł', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('É', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('Đ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('Ę', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('Ĝ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('Ĥ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('Ĵ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('Ź', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, 
        			MorseBit.GAP, MorseBit.DOT });
        	put('Ń', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('Ñ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('Ö', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });  
        	put('Ø', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });  
        	put('Ó', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });  
        	put('Ŝ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('Þ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('Ü', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('Ŭ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('Ż', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });


        	//Русский Код Морзе -- Russian Morse Encoding

        	put('А', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('Б', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('В', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH });
        	put('Г', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT });
        	put('Д', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT });
        	put('Е', new MorseBit[] { MorseBit.DOT });
        	put('Ж', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('З', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('И', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('Й', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('К', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH });
        	put('Л', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('М', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('Н', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('О', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH });
        	put('П', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT});
        	put('Р', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT});
        	put('С', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT });
        	put('Т', new MorseBit[] { MorseBit.DASH });
        	put('У', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH });
        	put('Ф', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('Х', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('Ц', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('Ч', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('Ш', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('Щ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('Ъ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('Ы', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('Ь', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('Э', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('Ю', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('Я', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });

        	// 和文モールス符号  Japanese Wabun Code

        	put('ア', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('イ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('ウ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH });
        	put('エ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('オ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('カ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('キ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('ク', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('ケ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('コ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('サ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('シ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('ス', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('セ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT});
        	put('ソ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT});
        	put('タ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('チ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('ツ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT});
        	put('テ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('ト', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('ナ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT});
        	put('ニ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('ヌ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('ネ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('ノ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('ハ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('ヒ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('フ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('ヘ', new MorseBit[] { MorseBit.DOT });
        	put('ホ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT });
        	put('マ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('ミ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('ム', new MorseBit[] { MorseBit.DASH });
        	put('メ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH});
        	put('モ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('ヤ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH });
        	put('ユ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('ヨ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('ラ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT });
        	put('リ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT });
        	put('ル', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP,MorseBit.DASH, MorseBit.GAP, MorseBit.DOT});
        	put('レ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH });
        	put('ロ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('ワ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH });
        	put('ヰ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('ヱ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('ヲ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH });
        	put('ン', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT});


        	// Gojuon combinations with dakuten and handakuten are treated as single code points
        	// in unicode, so they are represented here as single characters, but could also be
        	// composed sequentially, as there is a letter gap between the gojuon and the diacritical

        	put('ガ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.LETTER_GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DOT });
        	put('ギ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, 
        			MorseBit.LETTER_GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('グ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.LETTER_GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DOT });
        	put('ゲ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.LETTER_GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DOT });
        	put('ゴ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH,MorseBit.LETTER_GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DOT });
        	put('ザ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.LETTER_GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DOT });
        	put('ジ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, 
        			MorseBit.LETTER_GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('ズ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, 
        			MorseBit.LETTER_GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('ゼ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, 
        			MorseBit.LETTER_GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('ゾ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.LETTER_GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DOT });
        	put('ダ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.LETTER_GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('ヂ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.LETTER_GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DOT });
        	put('ヅ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.LETTER_GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DOT });
        	put('デ', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, 
        			MorseBit.LETTER_GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('ド',  new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, 
        			MorseBit.LETTER_GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('バ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.LETTER_GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DOT });
        	put('ビ',  new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, 
        			MorseBit.LETTER_GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('ブ',  new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.LETTER_GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DOT });
        	put('ベ', new MorseBit[] { MorseBit.DOT, MorseBit.LETTER_GAP, MorseBit.DOT, MorseBit.GAP,
        			MorseBit.DOT });
        	put('ボ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.LETTER_GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	put('パ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.LETTER_GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('ピ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.LETTER_GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });
        	put('プ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.LETTER_GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	put('ペ',  new MorseBit[] { MorseBit.DOT, MorseBit.LETTER_GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, 
        			MorseBit.GAP, MorseBit.DOT });
        	put('ポ', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.LETTER_GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP,
        			MorseBit.DOT });

        	// comma 
        	put('、', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DASH});
        	// full stop
        	put('。', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP,
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, 
        			MorseBit.GAP, MorseBit.DOT });

        	// dakuten
        	put('゙', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT });
        	// handakuten  
        	put('゚', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP,
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });

        	// Choonpu
        	put('ー', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, 
        			MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH });

        	//DO
        	put(')', new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, 
        			MorseBit.GAP, MorseBit.DASH });
        	//SN
        	put('(', new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, 
        			MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT });
        	}
        };

    private static final MorseBit[] ERROR_GAP = new MorseBit[] { MorseBit.GAP };

    /** Return the pattern data for a given character */
    static MorseBit[] pattern(char c) {
    	if (Character.isLetter(c))
            c = Character.toUpperCase(c);
    	if (morse_map.containsKey(c))
        	return morse_map.get(c);
    	else
        	return ERROR_GAP;
    }

    static MorseBit[] pattern(String str) {
        boolean lastWasWhitespace;
        int strlen = str.length();

        // Calculate how MorseBit our array needs to be.
        int len = 1;
        lastWasWhitespace = true;
        for (int i=0; i<strlen; i++) {
            char c = str.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!lastWasWhitespace) {
                    len++;
                    lastWasWhitespace = true;
                }
            } else {
                if (!lastWasWhitespace) {
                    len++;
                }
                lastWasWhitespace = false;
                len += pattern(c).length;
            }
        }

        // Generate the pattern array.  Note that we put an extra element of 0
        // in at the beginning, because the pattern always starts with the pause,
        // not with the vibration.
        MorseBit[] result = new MorseBit[len];
        result[0] = MorseBit.GAP;
        int pos = 1;
        lastWasWhitespace = true;
        for (int i=0; i<strlen; i++) {
            char c = str.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!lastWasWhitespace) {
                    result[pos] = MorseBit.WORD_GAP;
                    pos++;
                    lastWasWhitespace = true;
                }
            } else {
                if (!lastWasWhitespace) {
                    result[pos] = MorseBit.LETTER_GAP;
                    pos++;
                }
                lastWasWhitespace = false;
                MorseBit[] letter = pattern(c);
                System.arraycopy(letter, 0, result, pos, letter.length);
                pos += letter.length;
            }
        }
        return result;
    }
}