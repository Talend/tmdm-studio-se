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
package com.amalto.workbench.detailtabs.sections.composites;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.amalto.workbench.detailtabs.sections.BasePropertySection;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.langinfo.LanguageInfo;
import com.amalto.workbench.detailtabs.sections.providers.LanguageInfoLabelProvider;
import com.amalto.workbench.detailtabs.sections.providers.LanguageInfoModifier;
import com.amalto.workbench.detailtabs.sections.providers.LanguageInfoSorter;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.providers.ListContentProvider;
import com.amalto.workbench.utils.Util;

public class LanguageInfoComposite extends Composite {

	private final List<String> allLanguages;

	private TreeViewer tvInfos;

	private Text txtLabel;

	private Button btnAdd;

	private Button btnRemove;

	private Combo comboLanguage;

	private List<LanguageInfo> infos = new ArrayList<LanguageInfo>();
	protected BasePropertySection section;

	public LanguageInfoComposite(Composite parent, int style,BasePropertySection section) {
		super(parent, style);
		this.section=section;
		allLanguages = Arrays.asList(Util.lang2iso.keySet().toArray(
				new String[0]));

		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		setLayout(gridLayout);

		comboLanguage = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN
				| SWT.SINGLE);
		final GridData gd_comboLanguage = new GridData();
		gd_comboLanguage.widthHint=120;
		comboLanguage.setLayoutData(gd_comboLanguage);

		txtLabel = new Text(this, SWT.BORDER);
		final GridData gd_txtLabel = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		txtLabel.setLayoutData(gd_txtLabel);

		btnAdd = new Button(this, SWT.NONE);
		btnAdd.setImage(ImageCache.getCreatedImage(EImage.ADD_OBJ.getPath()));
		btnAdd.setToolTipText(Messages.Add);

		tvInfos = new TreeViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		Tree tree = tvInfos.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		final GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true,
				2, 1);
		gd_tree.heightHint = 80;
		tree.setLayoutData(gd_tree);

		final TreeColumn colLanguage = new TreeColumn(tree, SWT.NONE);
		colLanguage.setWidth(120);
		colLanguage.setText(Messages.LanguageInfoComposite_);

		final TreeColumn colLabel = new TreeColumn(tree, SWT.NONE);
		colLabel.setWidth(242);
		colLabel.setText(Messages.LanguageInfoComposite_Label);

		btnRemove = new Button(this, SWT.NONE);
		final GridData gd_btnRemove = new GridData(SWT.LEFT, SWT.TOP, false,
				false);
		btnRemove.setLayoutData(gd_btnRemove);
		btnRemove.setImage(ImageCache.getCreatedImage(EImage.DELETE_OBJ
				.getPath()));
		btnRemove.setToolTipText(Messages.LanguageInfoComposite_Del);
		//

		tvInfos.setContentProvider(new ListContentProvider());
		tvInfos.setLabelProvider(new LanguageInfoLabelProvider());
		tvInfos.setSorter(new LanguageInfoSorter());

		initUIListeners();
	}

	public void setLanguageInfos(LanguageInfo[] initLanguageInfos) {
		initUIContent(initLanguageInfos);

		tvInfos.refresh();
	}

	public LanguageInfo[] getLanguageInfos() {
		return infos.toArray(new LanguageInfo[0]);
	}

	private void initUIContent(LanguageInfo[] initLanguageInfos) {

		initLanguageCombo();

		initLanguageInfos(initLanguageInfos);
	}

	private void initLanguageCombo() {

		comboLanguage.removeAll();

		for (String eachLanguage : allLanguages)
			comboLanguage.add(eachLanguage);

		comboLanguage.select(0);
	}

	private void initLanguageInfos(LanguageInfo[] initLanguageInfos) {

		infos.clear();

		for (LanguageInfo eachLanguageInfo : initLanguageInfos) {
			infos.add(eachLanguageInfo);
		}

		tvInfos.setInput(infos);
	}

	private void initUIListeners() {

		initUIListenerOfLabelText();

		initUIListenerOfAddButton();

		initUIListenerOfAddRemove();

		initUIListenerOfLangInfoTree();
	}

	private void initUIListenerOfLabelText() {
		txtLabel.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				onMidifyLabelText();
			}
		});
	}

	private void initUIListenerOfLangInfoTree() {

		tvInfos.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				onSelectLanguageInfoFrTree();
			}
		});

		tvInfos.setColumnProperties(new String[] {
				LanguageInfoModifier.COL_PROP_LANG,
				LanguageInfoModifier.COL_PROP_LABLE });
		tvInfos.setCellEditors(new CellEditor[] {
				new ComboBoxCellEditor(tvInfos.getTree(), allLanguages
						.toArray(new String[0])),
				new TextCellEditor(tvInfos.getTree()) });
		LanguageInfoModifier modifier=new LanguageInfoModifier(tvInfos, infos,
				allLanguages,section);

		tvInfos.setCellModifier(modifier);
	}

	private void initUIListenerOfAddRemove() {
		btnRemove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				onRemoveLanguageInfo();
			}
		});
	}

	private void initUIListenerOfAddButton() {
		btnAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				onAddLanguageInfo();
			}
		});
	}

	private void onSelectLanguageInfoFrTree() {

		if (!isLanguageInfoSelected())
			return;

		LanguageInfo selectedLangInfo = getSelectedLanguageInfo();

		comboLanguage.select(allLanguages.indexOf(selectedLangInfo
				.getLanguage()));
		txtLabel.setText(selectedLangInfo.getLabel());
	}

	private void onMidifyLabelText() {

		LanguageInfo correspondLangInfo = getLangInfoBySelectionOfLangCombo();

		if (correspondLangInfo == null)
			return;

		if (correspondLangInfo.getLabel().equals(txtLabel.getText().trim()))
			return;

		if ("".equals(txtLabel.getText().trim())) { //$NON-NLS-1$
			MessageDialog.openWarning(getShell(), Messages.Warning,
					Messages.LanguageInfoComposite_InfoCannotbeEmpty);
			txtLabel.setText(correspondLangInfo.getLabel());
		}

		correspondLangInfo.setLabel(txtLabel.getText().trim());
		tvInfos.refresh();
		if(section!=null)section.autoCommit();
	}

	private void onAddLanguageInfo() {

		if ("".equals(txtLabel.getText().trim())) { //$NON-NLS-1$
			MessageDialog.openWarning(getShell(), Messages.Warning,
					Messages.LanguageInfoComposite_InfoCannotbeEmpty);
			return;
		}

		LanguageInfo correspondLangInfo = getLangInfoBySelectionOfLangCombo();

		if (correspondLangInfo == null) {
			LanguageInfo newLangInfo = new LanguageInfo(
					comboLanguage.getText(), Util.lang2iso.get(comboLanguage
							.getText()), txtLabel.getText().trim());
			infos.add(newLangInfo);
		} else {
			correspondLangInfo.setLabel(txtLabel.getText().trim());
		}

		tvInfos.refresh();
		if(section!=null)section.autoCommit();
	}

	private void onRemoveLanguageInfo() {

		infos.remove(getSelectedLanguageInfo());
		tvInfos.refresh();
		if(section!=null)section.autoCommit();
	}

	private LanguageInfo getLangInfoBySelectionOfLangCombo() {

		for (LanguageInfo eachCurLanguageInfo : infos) {
			if (eachCurLanguageInfo.getLanguage().equals(
					comboLanguage.getText())) {
				return eachCurLanguageInfo;
			}
		}

		return null;
	}

	private LanguageInfo getSelectedLanguageInfo() {

		if (tvInfos.getSelection() == null || tvInfos.getSelection().isEmpty())
			return null;

		Object selectedObj = ((IStructuredSelection) tvInfos.getSelection())
				.getFirstElement();

		if (selectedObj instanceof LanguageInfo)
			return (LanguageInfo) selectedObj;

		return null;
	}

	private boolean isLanguageInfoSelected() {
		return (getSelectedLanguageInfo() != null);
	}
}
