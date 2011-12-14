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

/** Class that implements the text to morse code conversion */
class MorseConverter {

    /** The characters from 'A' to 'Z' */
    private static final MorseBit[][] LETTERS = new MorseBit[][] {
        /* A */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH },
        /* B */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT },
        /* C */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT },
        /* D */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT },
        /* E */ new MorseBit[] { MorseBit.DOT },
        /* F */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT },
        /* G */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT },
        /* H */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT },
        /* I */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT },
        /* J */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH },
        /* K */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH },
        /* L */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT },
        /* M */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH },
        /* N */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT },
        /* O */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH },
        /* P */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT },
        /* Q */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH },
        /* R */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT },
        /* S */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT },
        /* T */ new MorseBit[] { MorseBit.DASH },
        /* U */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH },
        /* V */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH },
        /* W */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH },
        /* X */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH },
        /* Y */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH },
        /* Z */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT },
    };

    /** The characters from '0' to '9' */
    private static final MorseBit[][] NUMBERS = new MorseBit[][] {
        /* 0 */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH },
        /* 1 */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH },
        /* 2 */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH },
        /* 3 */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH },
        /* 4 */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DASH },
        /* 5 */ new MorseBit[] { MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT },
        /* 6 */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT },
        /* 7 */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT },
        /* 8 */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT, MorseBit.GAP, MorseBit.DOT },
        /* 9 */ new MorseBit[] { MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DASH, MorseBit.GAP, MorseBit.DOT },
    };

    private static final MorseBit[] ERROR_GAP = new MorseBit[] { MorseBit.GAP };

    /** Return the pattern data for a given character */
    static MorseBit[] pattern(char c) {
        if (c >= 'A' && c <= 'Z') {
            return LETTERS[c - 'A'];
        }
        if (c >= 'a' && c <= 'z') {
            return LETTERS[c - 'a'];
        }
        else if (c >= '0' && c <= '9') {
            return NUMBERS[c - '0'];
        }
        else {
            return ERROR_GAP;
        }
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