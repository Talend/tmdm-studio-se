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

    private static final String TAG_PASSWORD_GEGIN = "<password>";

    private static final String TAG_PASSWORD_END = "</password>";

    private static final String MASKCODE_CHAR = "*";

    private static final String MASKCODE = "*******";

    protected String content = "";//$NON-NLS-1$

    protected String maskedContent = "";//$NON-NLS-1$

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
            if (isPasswordHiden(newContent)) {
                if (contentChanged(newContent)) {
                    String[] splitNewContent = splitbyPasswordTag(newContent);
                    String[] splitContent = splitbyPasswordTag(content);
                    this.content = splitNewContent[0] + splitContent[1] + splitNewContent[2];
                    this.maskedContent = hidePassword(content);
                }
            } else {
                this.content = newContent;
                this.maskedContent = hidePassword(content);
            }
        }
    }

    private String hidePassword(final String content) {
        String[] splitbyPasswordTag = splitbyPasswordTag(content);
        if (splitbyPasswordTag != null) {
            return splitbyPasswordTag[0] + (TAG_PASSWORD_GEGIN + MASKCODE + TAG_PASSWORD_END) + splitbyPasswordTag[2];
        }

        return content;
    }

    private boolean isPasswordHiden(final String content) {
        String[] splitbyPasswordTag = splitbyPasswordTag(content);
        if (splitbyPasswordTag != null) {
            return splitbyPasswordTag[1].contains(MASKCODE_CHAR);
        }

        return false;
    }

    private boolean contentChanged(final String newcontent) {
        String[] splitbyPassword_newcontent = splitbyPasswordTag(newcontent);
        String[] splitbyPassword_maskcontent = splitbyPasswordTag(maskedContent);

        if(splitbyPassword_newcontent != null && splitbyPassword_maskcontent != null) {
            return !(splitbyPassword_newcontent[0].equals(splitbyPassword_maskcontent[0])
                    && splitbyPassword_newcontent[2].equals(splitbyPassword_maskcontent[2]));
        }
        
        return true;
    }

    private String[] splitbyPasswordTag(String _content) {
        int start = _content.indexOf(TAG_PASSWORD_GEGIN);
        int end = _content.indexOf(TAG_PASSWORD_END) + TAG_PASSWORD_END.length();
        if (start != -1) {
            return new String[] { _content.substring(0, start), _content.substring(start, end), _content.substring(end) };
        }

        return null;
    }
}
