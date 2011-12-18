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
        MorseBit[] result = new MorseBit[len+1];
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