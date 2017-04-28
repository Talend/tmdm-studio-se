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
package com.amalto.workbench.editors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.webservices.WSDataCluster;

public class DataClusterMainPage extends AMainPageV2 implements ITextListener {

    private static Log log = LogFactory.getLog(DataClusterMainPage.class);

    protected Text descriptionText;

    // protected Combo dataClusterTypeCombo;
    /*
     * protected Composite stemmerGroup; protected Button useStemming; protected Combo languageCombo; protected Button
     * singleRoot; protected Button partOfSpeechSingleWord; protected Button rootConsistency; protected Button
     * accentedWords; protected Button compoundWords; protected Button spellingCorrection; protected Button
     * phoneticCorrection; protected Button partOfSpeechCompoundWords; protected Button synonymInformation; protected
     * Button stopWords; protected Button typingErrors; protected Button optimizeQueryResult;
     */

    protected boolean refreshing = false;

    protected boolean comitting = false;

    public DataClusterMainPage(FormEditor editor) {
        super(editor, DataClusterMainPage.class.getName(), Messages.DataClusterMainPage_DataContainer
                + ((XObjectEditorInput) editor.getEditorInput()).getName());
    }

    @Override
    protected void createCharacteristicsContent(FormToolkit toolkit, Composite mainComposite) {

        try {

            // description
            Label descriptionLabel = toolkit.createLabel(mainComposite, Messages.DataClusterMainPage_Description, SWT.NULL);
            descriptionLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
            descriptionText = toolkit.createText(mainComposite, "", SWT.BORDER);//$NON-NLS-1$
            descriptionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            ((GridData) descriptionText.getLayoutData()).minimumHeight = 30;
            descriptionText.addModifyListener(this);

            /*
             * Label dataClusterTypeLabel = toolkit.createLabel(charComposite, "Data Cluster Type", SWT.NULL);
             * dataClusterTypeLabel.setLayoutData( new GridData(SWT.FILL,SWT.FILL,false,true,1,1) );
             */
            /*
             * dataClusterTypeCombo = new Combo(charComposite,SWT.READ_ONLY |SWT.DROP_DOWN|SWT.SINGLE);
             * dataClusterTypeCombo.setLayoutData( new GridData(SWT.FILL,SWT.FILL,false,true,1,1) );
             * dataClusterTypeCombo.add("Items"); dataClusterTypeCombo.add("Cache");
             * dataClusterTypeCombo.addModifyListener(this);
             */
            /*
             * useStemming = toolkit.createButton(charComposite,"Use Stemming",SWT.TOGGLE | SWT.TRAIL);
             * useStemming.setLayoutData( new GridData(SWT.LEAD,SWT.FILL,false,true,1,1) );
             * useStemming.addSelectionListener(new SelectionListener() { public void
             * widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {}; public void
             * widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
             * DataClusterMainPage.this.stemmerGroup.setEnabled(DataClusterMainPage.this.useStemming.getSelection());
             * ((Section
             * )DataClusterMainPage.this.stemmerGroup.getParent()).setExpanded(DataClusterMainPage.this.useStemming
             * .getSelection()); if (DataClusterMainPage.this.useStemming.getSelection()) {
             * useStemming.setText("Stemming Enabled"); } else { useStemming.setText("Stemming Disabled"); }
             * 
             * if (! DataClusterMainPage.this.refreshing) markDirty(); }; });
             */
            /*
             * //Stemmer Characteristics stemmerGroup = this.getNewSectionComposite("Stemmer Characteristics");
             * stemmerGroup.setLayout(new GridLayout(2,true));
             * 
             * Label languageLabel = toolkit.createLabel(stemmerGroup, "Language", SWT.NULL);
             * languageLabel.setLayoutData( new GridData(SWT.FILL,SWT.FILL,false,true,1,1) ); languageCombo = new
             * Combo(stemmerGroup,SWT.READ_ONLY |SWT.DROP_DOWN|SWT.SINGLE); languageCombo.setLayoutData( new
             * GridData(SWT.FILL,SWT.FILL,false,false,1,1) ); languageCombo.add("en"); languageCombo.add("fr");
             * languageCombo.addModifyListener(this);
             * 
             * singleRoot = toolkit.createButton( stemmerGroup, "Single Root", SWT.CHECK | SWT.LEAD );
             * singleRoot.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true,2,1));
             * singleRoot.addSelectionListener(new SelectionListener() { public void
             * widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {}; public void
             * widgetSelected(org.eclipse.swt.events.SelectionEvent e) { if (! DataClusterMainPage.this.refreshing)
             * markDirty(); }; }); partOfSpeechSingleWord = toolkit.createButton( stemmerGroup,
             * "Part Of Speech Single Word", SWT.CHECK | SWT.LEAD ); partOfSpeechSingleWord.setLayoutData(new
             * GridData(SWT.FILL,SWT.FILL,false,true,2,1)); partOfSpeechSingleWord.addSelectionListener(new
             * SelectionListener() { public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
             * public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) { if (!
             * DataClusterMainPage.this.refreshing) markDirty(); }; }); rootConsistency = toolkit.createButton(
             * stemmerGroup, "Root Consistency", SWT.CHECK | SWT.LEAD ); rootConsistency.setLayoutData(new
             * GridData(SWT.FILL,SWT.FILL,false,true,2,1)); rootConsistency.addSelectionListener(new SelectionListener()
             * { public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {}; public void
             * widgetSelected(org.eclipse.swt.events.SelectionEvent e) { if (! DataClusterMainPage.this.refreshing)
             * markDirty(); }; }); accentedWords = toolkit.createButton( stemmerGroup, "Accented Words", SWT.CHECK |
             * SWT.LEAD ); accentedWords.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true,2,1));
             * accentedWords.addSelectionListener(new SelectionListener() { public void
             * widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {}; public void
             * widgetSelected(org.eclipse.swt.events.SelectionEvent e) { if (! DataClusterMainPage.this.refreshing)
             * markDirty(); }; }); compoundWords = toolkit.createButton( stemmerGroup, "Compound Words", SWT.CHECK |
             * SWT.LEAD ); compoundWords.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true,2,1));
             * compoundWords.addSelectionListener(new SelectionListener() { public void
             * widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {}; public void
             * widgetSelected(org.eclipse.swt.events.SelectionEvent e) { if (! DataClusterMainPage.this.refreshing)
             * markDirty(); }; }); spellingCorrection = toolkit.createButton( stemmerGroup, "Spelling Correction",
             * SWT.CHECK | SWT.LEAD ); spellingCorrection.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true,2,1));
             * spellingCorrection.addSelectionListener(new SelectionListener() { public void
             * widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {}; public void
             * widgetSelected(org.eclipse.swt.events.SelectionEvent e) { if (! DataClusterMainPage.this.refreshing)
             * markDirty(); }; }); phoneticCorrection = toolkit.createButton( stemmerGroup, "Phonetic Correction",
             * SWT.CHECK | SWT.LEAD ); phoneticCorrection.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true,2,1));
             * phoneticCorrection.addSelectionListener(new SelectionListener() { public void
             * widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {}; public void
             * widgetSelected(org.eclipse.swt.events.SelectionEvent e) { if (! DataClusterMainPage.this.refreshing)
             * markDirty(); }; }); partOfSpeechCompoundWords = toolkit.createButton( stemmerGroup,
             * "Part Of Speech Compound Words", SWT.CHECK | SWT.LEAD ); partOfSpeechCompoundWords.setLayoutData(new
             * GridData(SWT.FILL,SWT.FILL,false,true,2,1)); partOfSpeechCompoundWords.addSelectionListener(new
             * SelectionListener() { public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
             * public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) { if (!
             * DataClusterMainPage.this.refreshing) markDirty(); }; }); synonymInformation = toolkit.createButton(
             * stemmerGroup, "Synonym Information", SWT.CHECK | SWT.LEAD ); synonymInformation.setLayoutData(new
             * GridData(SWT.FILL,SWT.FILL,false,true,2,1)); synonymInformation.addSelectionListener(new
             * SelectionListener() { public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
             * public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) { if (!
             * DataClusterMainPage.this.refreshing) markDirty(); }; }); stopWords = toolkit.createButton( stemmerGroup,
             * "Stop Words", SWT.CHECK | SWT.LEAD ); stopWords.setLayoutData(new
             * GridData(SWT.FILL,SWT.FILL,false,true,2,1)); stopWords.addSelectionListener(new SelectionListener() {
             * public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {}; public void
             * widgetSelected(org.eclipse.swt.events.SelectionEvent e) { if (! DataClusterMainPage.this.refreshing)
             * markDirty(); }; }); typingErrors = toolkit.createButton( stemmerGroup, "Typing Errors", SWT.CHECK |
             * SWT.LEAD ); typingErrors.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true,2,1));
             * typingErrors.addSelectionListener(new SelectionListener() { public void
             * widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {}; public void
             * widgetSelected(org.eclipse.swt.events.SelectionEvent e) { if (! DataClusterMainPage.this.refreshing)
             * markDirty(); }; }); optimizeQueryResult = toolkit.createButton( stemmerGroup, "Optimize Query Results",
             * SWT.CHECK | SWT.LEAD ); optimizeQueryResult.setLayoutData(new
             * GridData(SWT.FILL,SWT.FILL,false,true,2,1)); optimizeQueryResult.addSelectionListener(new
             * SelectionListener() { public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
             * public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) { if (!
             * DataClusterMainPage.this.refreshing) markDirty(); }; });
             */

            refreshData();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }// createCharacteristicsContent

    @Override
    protected void refreshData() {
        try {
            if (this.comitting) {
                return;
            }

            this.refreshing = true;

            WSDataCluster wsObject = (WSDataCluster) (getXObject().getWsObject());

            descriptionText.setText(wsObject.getDescription() == null ? "" : wsObject.getDescription());//$NON-NLS-1$

            /*
             * if (wsObject.getWSDataClusterType().equals(WSDataClusterType.CACHE)) dataClusterTypeCombo.select(1); else
             * dataClusterTypeCombo.select(0);
             */

            /*
             * boolean doStemming = ! ("0100".equals(wsObject.getStemmer())); useStemming.setSelection(doStemming); if
             * (doStemming) { useStemming.setText("Stemming Enabled"); } else {
             * useStemming.setText("Stemming Disabled"); } stemmerGroup.setEnabled(doStemming);
             * ((Section)DataClusterMainPage.this.stemmerGroup.getParent()).setExpanded(doStemming);
             * 
             * String language = wsObject.getStemmer().substring(0,2); if ("fr".equals(language))
             * languageCombo.select(1); else languageCombo.select(0);
             * 
             * if (useStemming.getSelection()){ String vals =
             * wsObject.getStemmer().substring(2,wsObject.getStemmer().length()); int val = Integer.parseInt(vals);
             * singleRoot.setSelection((val & (1 << 0)) == (1 << 0)); partOfSpeechSingleWord.setSelection((val & (1 <<
             * 1)) == (1 << 1)); rootConsistency.setSelection((val & (1 << 2)) == (1 << 2));
             * accentedWords.setSelection((val & (1 << 3)) == (1 << 3)); compoundWords.setSelection((val & (1 << 4)) ==
             * (1 << 4)); spellingCorrection.setSelection((val & (1 << 5)) == (1 << 5));
             * phoneticCorrection.setSelection((val & (1 << 6)) == (1 << 6));
             * partOfSpeechCompoundWords.setSelection((val & (1 << 8)) == (1 << 8));
             * synonymInformation.setSelection((val & (1 << 9)) == (1 << 9)); stopWords.setSelection((val & (1 << 10))
             * == (1 << 10)); typingErrors.setSelection((val & (1 << 11)) == (1 << 11));
             * optimizeQueryResult.setSelection((val & (1 << 12)) == (1 << 12));
             * 
             * }
             */

            this.refreshing = false;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getSite().getShell(), Messages.DataClusterMainPage_ErrorRefreshingPage,
                    Messages.DataClusterMainPage_ErrorRefreshingPageXX + e.getLocalizedMessage());
        }
    }

    @Override
    protected void commit() {
        try {

            if (this.refreshing) {
                return;
            }

            this.comitting = true;

            WSDataCluster wsObject = (WSDataCluster) (getXObject().getWsObject());
            wsObject.setDescription(descriptionText.getText());
            /*
             * if ("Cache".equals(dataClusterTypeCombo.getText()))
             * wsObject.setWSDataClusterType(WSDataClusterType.CACHE); else
             * wsObject.setWSDataClusterType(WSDataClusterType.ITEMS);
             */
            /*
             * wsObject.setWSDataClusterType(WSDataClusterType.ITEMS);
             * 
             * wsObject.setWSSpellerRefreshPeriodInSeconds(0); if (!useStemming.getSelection())
             * wsObject.setStemmer("0100"); else { String s=languageCombo.getText(); int i=0; if
             * (singleRoot.getSelection()) i+=1; if (partOfSpeechSingleWord.getSelection()) i+=2; if
             * (rootConsistency.getSelection()) i+=4; if (accentedWords.getSelection()) i+=8; if
             * (compoundWords.getSelection()) i+=16; if (spellingCorrection.getSelection()) i+=32; if
             * (phoneticCorrection.getSelection()) i+=64; if (partOfSpeechCompoundWords.getSelection()) i+=256; if
             * (synonymInformation.getSelection()) i+=512; if (stopWords.getSelection()) i+=1024; if
             * (typingErrors.getSelection()) i+=2048; if (optimizeQueryResult.getSelection()) i+=4096; s+=""+i;
             * wsObject.setStemmer(s); }
             */

            this.comitting = false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getSite().getShell(), Messages.DataClusterMainPage_ErrorCommitingPage,
                    Messages.DataClusterMainPage_ErrorCommitingPageXX + e.getLocalizedMessage());
        }
    }

    @Override
    protected void createActions() {
    }

    public void textChanged(TextEvent event) {
        if (this.refreshing) {
            return;
        }
        markDirty();
    }

    @Override
    public void modifyText(ModifyEvent e) {
        if (this.refreshing) {
            return;
        }
        super.modifyText(e);
    }

    /*
     * private void hookContextMenu(TreeViewer viewer) { }
     * 
     * private void fillContextMenu(IMenuManager manager) { }
     */

    @Override
    public void dispose() {
        super.dispose();
    }

}
