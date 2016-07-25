// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.dialogs.AnnotationLanguageLabelsDialog;
import com.amalto.workbench.editors.AMainPageV2;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

/**
 * This class is meant to work out a composite populated with label, button, text which stand next to each other the
 * composite will in the parent composite holder
 * 
 * @author fliu
 * 
 */

public class DescAnnotationComposite implements SelectionListener {

    private Button annotationButton;

    private Text descriptionText;

    private Composite descAntionHolder;

    private AMainPageV2 accommodation;

    private String descriptionValue;

    private String dlgTitle;

    private LinkedHashMap<String, String> dataStore = new LinkedHashMap<String, String>();

    private TableColumn column;

    private GridData textGD;

    private static final String REGEXP_METACHARACTERS = "\\[([\\w]+)[\\s]*:[\\s]*([^]]*)\\]";//$NON-NLS-1$

    private static final Pattern DESC_PATTERN = Pattern.compile(REGEXP_METACHARACTERS);

    public DescAnnotationComposite(String labelName, String buttonName, FormToolkit toolkit, Composite parent,
            AMainPageV2 dialog, boolean isBtnRight) {
        descAntionHolder = toolkit.createComposite(parent);
        descAntionHolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 0;
        layout.marginLeft = 0;
        layout.marginTop = 0;
        layout.marginHeight = 0;
        layout.marginBottom = 0;
        descAntionHolder.setLayout(layout);
        if (labelName != null && labelName.length() > 0) {
            Label descriptionLabel = toolkit.createLabel(descAntionHolder, labelName, SWT.NULL);
            descriptionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
        }
        if (isBtnRight) {
            descriptionText = toolkit.createText(descAntionHolder, "", SWT.BORDER);//$NON-NLS-1$
            textGD = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
            descriptionText.setLayoutData(textGD);

            descriptionText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    if (accommodation != null && descriptionValue != null && !descriptionValue.equals(descriptionText.getText())) {
                        accommodation.markDirtyWithoutCommit();
                    }
                    descriptionValue = descriptionText.getText();
                    fillDataStore(descriptionText.getText());
                }
            });
            annotationButton = toolkit.createButton(descAntionHolder, "", SWT.PUSH);//$NON-NLS-1$
            annotationButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
            annotationButton.addSelectionListener(this);
            annotationButton.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
            annotationButton.setToolTipText(Messages.DescAnnotationComposite_AnnoBtnDesc);

        } else {
            annotationButton = toolkit.createButton(descAntionHolder, "", SWT.PUSH);//$NON-NLS-1$
            annotationButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
            annotationButton.addSelectionListener(this);
            annotationButton.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
            annotationButton.setToolTipText(Messages.DescAnnotationComposite_AnnoBtnDesc);

            descriptionText = toolkit.createText(descAntionHolder, "", SWT.BORDER);//$NON-NLS-1$
            textGD = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
            descriptionText.setLayoutData(textGD);
            descriptionText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    if (accommodation != null && descriptionValue != null && !descriptionValue.equals(descriptionText.getText())) {
                        accommodation.markDirtyWithoutCommit();
                    }
                    descriptionValue = descriptionText.getText();
                    fillDataStore(descriptionText.getText());
                }
            });
        }
        // Util.createCompDropTarget(descriptionText);
        accommodation = dialog;
        dlgTitle = Messages.DescAnnotationComposite_AnnoBtnDesc;
    }

    public void setAnnotationDialogTitle(String title) {
        dlgTitle = title;
    }

    public void setText(String text) {
        descriptionText.setText(text);
        descriptionValue = text;

        fillDataStore(text);
    }

    public static boolean isLetter(char c) {
        boolean result = ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');

        return result;
    }

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
     * @param s Multiple language message string to be parsed
     * @param m Map between language codes and messages in which to store results
     */
    public static void parseMultiLanguageString(String s, Map<String, String> m) {

        if (s != null && m != null) {

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

            for (int i = 0, l = s.length(); i < l && parseState != PARSE_ERROR; ++i) {
                char c = s.charAt(i);

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
                            m.put(countryCode, messageBuffer.toString());
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

    /**
     * Encode multi-language message as a string using format [cc:mmm][cc:mmm] where cc is the language code and mmm is
     * the user specified message. ']' and '\' are automatically backslash escaped.
     * 
     * @param m Map between two letter language code and message in that language
     * @return Multi-language message encoded as a string, with ']' and '\' escaped
     */
    public static String escapeMultiLanguageString(Map<String, String> m) {

        StringBuffer resultBuffer = new StringBuffer();

        if (m != null) {

            for (Map.Entry<String, String> entry : m.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();

                if (k != null) {
                    k = k.toLowerCase();

                    if (Util.iso2lang.get(k) != null) {

                        resultBuffer.append('[');
                        resultBuffer.append(k.toUpperCase());
                        resultBuffer.append(':');

                        if (v != null) {

                            for (int i = 0, l = v.length(); i < l; ++i) {

                                char c = v.charAt(i);
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

    private void fillDataStore(String text) {
        dataStore.clear();
        parseMultiLanguageString(text, dataStore);
    }

    public String getText() {
        return descriptionText.getText();
    }

    public Text getTextWidget() {
        return descriptionText;
    }

    public void widgetSelected(SelectionEvent e) {
        AnnotationLanguageLabelsDialog dlg = new AnnotationLanguageLabelsDialog(dataStore, new DescAnnotationListener(),
                descriptionText.getShell(), dlgTitle);

        dlg.setBlockOnOpen(true);
        dlg.open();

        if (dlg.getReturnCode() == Window.OK) {

            dlg.close();
        }
    }

    public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
    };

    public String commitChange() {
        return descriptionText.getText();
    }

    public Composite getComposite() {
        return descAntionHolder;
    }

    private class DescAnnotationListener implements SelectionListener {

        public DescAnnotationListener() {
            super();
        }

        public void widgetSelected(SelectionEvent e) {
            AnnotationLanguageLabelsDialog dlg = (AnnotationLanguageLabelsDialog) ((Widget) e.getSource()).getData("dialog");//$NON-NLS-1$
            if (dlg.getReturnCode() == Window.OK) {
                String outPut = "";//$NON-NLS-1$
                outPut = DescAnnotationComposite.escapeMultiLanguageString(dataStore);

                if (!outPut.equals(descriptionText.getText())) {
                    descriptionText.setText(outPut);
                    if (accommodation != null) {
                        accommodation.markDirtyWithoutCommit();
                    }
                }

            }
            dlg.close();

        }

        public void widgetDefaultSelected(SelectionEvent e) {
        };
    }

    public TableColumn getColumn() {
        return column;
    }

    public void setColumn(TableColumn column) {
        this.column = column;
        resetWidth();
    }

    public void resetWidth() {
        if (column != null) {
            textGD.widthHint = column.getWidth() - 35;
        }
    }

    public void setEnable(boolean enable) {
        annotationButton.setEnabled(enable);
        descriptionText.setEnabled(enable);
    }
}
