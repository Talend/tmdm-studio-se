// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.widgets.xmleditor;

public class ExtensibleEditorContent {

    private String content;

    private String maskedContent;

    public ExtensibleEditorContent(String content) {
        setContent(content);
    }

    public String getContent() {
        return content;
    }

    public String getMaskContent() {
        return maskedContent;
    }

    public void setContent(String newContent) {
        if (newContent == null) {
            this.content = "";//$NON-NLS-1$
            this.maskedContent = "";
        } else {
            this.content = newContent;
            if (PasswordTagUtil.isPasswordHidden(newContent)) {
                if (PasswordTagUtil.isContentChanged(newContent, maskedContent)) {
                    String[] splitNewContent = PasswordTagUtil.splitByPasswordTag(newContent);
                    String[] splitContent = PasswordTagUtil.splitByPasswordTag(content);
                    if (splitContent != null) {
                        this.content = splitNewContent[0] + splitContent[1] + splitNewContent[2];
                    } else {
                        // origin content does not contains password part, this handle copy/input contents which
                        // contains password part, result: leave password empty
                        this.content = splitNewContent[0]
                                + (PasswordTagUtil.TAG_PASSWORD_BEGIN + PasswordTagUtil.TAG_PASSWORD_END) + splitNewContent[2];
                    }

                    this.maskedContent = PasswordTagUtil.hidePassword(content);
                }
            } else {
                this.maskedContent = PasswordTagUtil.hidePassword(content);
            }
        }
    }

    public static class PasswordTagUtil {

        public static final String TAG_PASSWORD_BEGIN = "<password>";

        public static final String TAG_PASSWORD_END = "</password>";

        public static final String MASKCODE_CHAR = "*";

        public static final String MASKCODE = "******";

        public static String hidePassword(final String content) {
            String[] splitByPasswordTag = splitByPasswordTag(content);
            if (splitByPasswordTag != null) {
                if (splitByPasswordTag[1].length() > TAG_PASSWORD_BEGIN.length() + TAG_PASSWORD_END.length()) {
                    return splitByPasswordTag[0] + (TAG_PASSWORD_BEGIN + MASKCODE + TAG_PASSWORD_END) + splitByPasswordTag[2];
                }

                return splitByPasswordTag[0] + (TAG_PASSWORD_BEGIN + TAG_PASSWORD_END) + splitByPasswordTag[2];
            }

            return content;
        }

        public static boolean isPasswordHidden(final String content) {
            String[] splitByPasswordTag = splitByPasswordTag(content);
            if (splitByPasswordTag != null) {
                return splitByPasswordTag[1].contains(MASKCODE_CHAR);
            }

            return false;
        }

        /*
         * @param newcontent password is mask coded
         * 
         * @param content mask coded or not
         * 
         * @return content except password part changed
         */
        public static boolean isContentChanged(final String newcontent, String content) {
            String[] splitByPassword_newcontent = splitByPasswordTag(newcontent);
            String[] splitByPassword_content = splitByPasswordTag(content);

            if (splitByPassword_newcontent != null && splitByPassword_content != null) {
                return !(splitByPassword_newcontent[0].equals(splitByPassword_content[0])
                        && splitByPassword_newcontent[2].equals(splitByPassword_content[2]));
            }

            return true;
        }

        // No need consider empty tag
        public static String[] splitByPasswordTag(String _content) {
            int start = _content.indexOf(TAG_PASSWORD_BEGIN);
            int end = _content.lastIndexOf(TAG_PASSWORD_END);
            if (start != -1 && end != -1) {
                end = end + TAG_PASSWORD_END.length();
                return new String[] { _content.substring(0, start), _content.substring(start, end), _content.substring(end) };
            }

            return null;
        }
    }
}
