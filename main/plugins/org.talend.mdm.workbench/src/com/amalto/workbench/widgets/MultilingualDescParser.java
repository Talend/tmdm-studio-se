// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.widgets;

import java.util.Map;

import com.amalto.workbench.utils.Util;

/**
 * @author sbliu
 *
 */
public class MultilingualDescParser {

    /**
     * Parse a multiple language string.
     * 
     * s is expected to be in the following format:
     * 
     * [en:...][fr:...][zh:...]
     * 
     * Characters ] and \ can be escaped in these using backslash escapes, for example
     * 
     * [en: a message with a \] character in the middle]
     * 
     * A message for a language can also be embedded anywhere in the string, for example
     * 
     * abcd[en:...]abcd[fr:...]abcd
     * 
     * The map between language code and message is stored in m.
     * 
     * @param msg Multiple language message string to be parsed
     * @param msgMap Map between language codes and messages in which to store results
     */
    public static void parseMultiLanguageString(String msg, Map<String, String> msgMap) {

        if (msg != null && msgMap != null) {

            // Parse states
            final byte PARSE_ERROR = 0;
            final byte LOOKING_FOR_OPENING_BRACKET = 1;
            final byte LOOKING_FOR_COUNTRY_CODE_FIRST_CHAR = 2;
            final byte LOOKING_FOR_COUNTRY_CODE_SECOND_CHAR = 3;
            final byte LOOKING_FOR_COLON = 4;
            final byte LOOKING_FOR_CLOSING_BRACKET = 5;
            final byte ENCOUNTERED_FIRST_BACKSLASH = 6;

            byte parseState = LOOKING_FOR_OPENING_BRACKET;
            StringBuffer countryCodeBuffer = new StringBuffer(); // string buffer for constructing current country code
            StringBuffer messageBuffer = new StringBuffer(); // string buffer for constructing current error message

            for (int i = 0, l = msg.length(); i < l && parseState != PARSE_ERROR; ++i) {
                char c = msg.charAt(i);

                switch (parseState) {
                case LOOKING_FOR_OPENING_BRACKET:
                    if (c == '[') {
                        parseState = LOOKING_FOR_COUNTRY_CODE_FIRST_CHAR;
                    }
                    break;
                case LOOKING_FOR_COUNTRY_CODE_FIRST_CHAR:
                    if (isLetter(c)) {
                        countryCodeBuffer.append(c);
                        parseState = LOOKING_FOR_COUNTRY_CODE_SECOND_CHAR;
                    } else {
                        parseState = LOOKING_FOR_OPENING_BRACKET;
                    }
                    break;
                case LOOKING_FOR_COUNTRY_CODE_SECOND_CHAR:
                    if (isLetter(c)) {
                        countryCodeBuffer.append(c);
                        parseState = LOOKING_FOR_COLON;
                    } else {
                        countryCodeBuffer = new StringBuffer();
                        parseState = LOOKING_FOR_OPENING_BRACKET;
                    }
                    break;
                case LOOKING_FOR_COLON:
                    if (c == ':') {
                        parseState = LOOKING_FOR_CLOSING_BRACKET;
                    } else {
                        countryCodeBuffer = new StringBuffer();
                        parseState = LOOKING_FOR_OPENING_BRACKET;
                    }
                    break;
                case LOOKING_FOR_CLOSING_BRACKET:
                    if (c == ']') {
                        String countryCode = countryCodeBuffer.toString().toLowerCase();
                        if (Util.iso2lang.get(countryCode) != null) {
                            msgMap.put(countryCode, messageBuffer.toString());
                        }
                        countryCodeBuffer = new StringBuffer();
                        messageBuffer = new StringBuffer();
                        parseState = LOOKING_FOR_OPENING_BRACKET;
                    } else if (c == '\\') {
                        parseState = ENCOUNTERED_FIRST_BACKSLASH;
                    } else {
                        messageBuffer.append(c);
                    }
                    break;
                case ENCOUNTERED_FIRST_BACKSLASH:
                    if (c == '\\' || c == ']') {
                        messageBuffer.append(c);
                    }
                    parseState = LOOKING_FOR_CLOSING_BRACKET;
                    break;
                default:
                    parseState = PARSE_ERROR;
                }
            }
        }
    }

    private static boolean isLetter(char c) {
        boolean result = ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');

        return result;
    }

    /**
     * Encode multi-language message as a string using format [cc:mmm][cc:mmm] where cc is the language code and mmm is
     * the user specified message. ']' and '\' are automatically backslash escaped.
     * 
     * @param msgMap Map between two letter language code and message in that language
     * @return Multi-language message encoded as a string, with ']' and '\' escaped
     */
    public static String escapeMultiLanguageString(Map<String, String> msgMap) {

        StringBuffer resultBuffer = new StringBuffer();

        if (msgMap != null) {

            for (Map.Entry<String, String> entry : msgMap.entrySet()) {
                String countryCode = entry.getKey();
                String msg = entry.getValue();

                if (countryCode != null) {
                    countryCode = countryCode.toLowerCase();

                    if (Util.iso2lang.get(countryCode) != null) {

                        resultBuffer.append('[');
                        resultBuffer.append(countryCode.toUpperCase());
                        resultBuffer.append(':');

                        if (msg != null) {

                            for (int i = 0, l = msg.length(); i < l; ++i) {

                                char c = msg.charAt(i);
                                if (c == '\\' || c == ']') {
                                    resultBuffer.append('\\');
                                }
                                resultBuffer.append(c);
                            }
                        }

                        resultBuffer.append(']');
                    }
                }
            }
        }

        return resultBuffer.toString();
    }
}
