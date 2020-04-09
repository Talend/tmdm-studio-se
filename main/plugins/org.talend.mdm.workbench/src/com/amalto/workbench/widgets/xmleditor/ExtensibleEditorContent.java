// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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

    private static final String TAG_PASSWORD_BEGIN = "<password>";

    private static final String TAG_PASSWORD_END = "</password>";

    private static final String TAG_PASSWORD_EMPTY = "<password/>";

    private static final String MASKCODE_CHAR = "*";

    private static final String MASKCODE = "*******";

    private String content;

    private String maskedContent;

    public ExtensibleEditorContent(String content) {

        if (content != null)
            this.content = content;
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
            if (isPasswordHidden(newContent)) {
                if (contentChanged(newContent)) {
                    String[] splitNewContent = splitByPasswordTag(newContent);
                    String[] splitContent = splitByPasswordTag(content);
                    if (splitContent != null) {
                        this.content = splitNewContent[0] + splitContent[1] + splitNewContent[2];
                    } else {
                        // origin content does not contains password part, this handle copy/input contents which
                        // contains
                        // password part, result: leave password empty
                        this.content = splitNewContent[0] + (TAG_PASSWORD_BEGIN + TAG_PASSWORD_END) + splitNewContent[2];
                    }

                    this.maskedContent = hidePassword(content);
                }
            } else {
                this.content = newContent;
                this.maskedContent = hidePassword(content);
            }
        }
    }

    private String hidePassword(final String content) {
        String[] splitByPasswordTag = splitByPasswordTag(content);
        if (splitByPasswordTag != null) {
            if (splitByPasswordTag[1].length() > TAG_PASSWORD_BEGIN.length() + TAG_PASSWORD_END.length()) {
                return splitByPasswordTag[0] + (TAG_PASSWORD_BEGIN + MASKCODE + TAG_PASSWORD_END) + splitByPasswordTag[2];
            }

            return splitByPasswordTag[0] + (TAG_PASSWORD_BEGIN + TAG_PASSWORD_END) + splitByPasswordTag[2];
        }

        return content;
    }

    private boolean isPasswordHidden(final String content) {
        String[] splitByPasswordTag = splitByPasswordTag(content);
        if (splitByPasswordTag != null) {
            return splitByPasswordTag[1].contains(MASKCODE_CHAR);
        }

        return false;
    }

    private boolean contentChanged(final String newcontent) {
        String[] splitByPassword_newcontent = splitByPasswordTag(newcontent);
        String[] splitByPassword_maskcontent = splitByPasswordTag(maskedContent);

        if(splitByPassword_newcontent != null && splitByPassword_maskcontent != null) {
            return !(splitByPassword_newcontent[0].equals(splitByPassword_maskcontent[0])
                    && splitByPassword_newcontent[2].equals(splitByPassword_maskcontent[2]));
        }
        
        return true;
    }

    // empty tag has low priority
    private String[] splitByPasswordTag(String _content) {
        int start = _content.indexOf(TAG_PASSWORD_BEGIN);
        int end = _content.lastIndexOf(TAG_PASSWORD_END) + TAG_PASSWORD_END.length();
        int firstEmptyTagIndex = _content.indexOf(TAG_PASSWORD_EMPTY);
        if (start != -1) {
            return new String[] { _content.substring(0, start), _content.substring(start, end), _content.substring(end) };
        } else if (firstEmptyTagIndex != -1) {
            int lastEmptyTagIndex = _content.lastIndexOf(TAG_PASSWORD_EMPTY);
            return new String[] { _content.substring(0, firstEmptyTagIndex),
                    _content.substring(firstEmptyTagIndex, lastEmptyTagIndex + TAG_PASSWORD_EMPTY.length()),
                    _content.substring(lastEmptyTagIndex + TAG_PASSWORD_EMPTY.length()) };
        }

        return null;
    }
}
