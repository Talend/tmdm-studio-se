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
package com.amalto.workbench.widgets.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.amalto.workbench.detailtabs.sections.BasePropertySection;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.providers.ListContentProvider;
import com.amalto.workbench.providers.ListStringLabelProvider;

public abstract class ListStringContentsComposite extends Composite {

    protected TreeViewer tvInfos;

    public TreeViewer getInfosTreeViewer() {
        return this.tvInfos;
    }

    protected Button btnAdd;

    protected Button btnUp;

    protected Button btnDown;

    protected Button btnRemove;

    protected List<String> infos = new ArrayList<String>();

    protected BasePropertySection section;

    private boolean treeContentChanged = false;

    public ListStringContentsComposite(Composite parent, int style, Object[] initParas, BasePropertySection section) {
        super(parent, style);
        this.section = section;
        initParas(initParas);

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        setLayout(gridLayout);

        createCandidateInfoUIArea(parent);

        btnAdd = new Button(this, SWT.NONE);
        btnAdd.setImage(ImageCache.getCreatedImage(EImage.ADD_OBJ.getPath()));
        btnAdd.setToolTipText(Messages._Add);

        tvInfos = new TreeViewer(this, SWT.FULL_SELECTION | SWT.BORDER);
        Tree tree = tvInfos.getTree();
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));

        tvInfos.setContentProvider(new ListContentProvider());
        tvInfos.setLabelProvider(new ListStringLabelProvider());
        tvInfos.setInput(infos);

        final TreeColumn colInfo = new TreeColumn(tree, SWT.NONE);
        colInfo.setWidth(300);
        colInfo.setText(getInfoColTitle());

        btnUp = new Button(this, SWT.NONE);
        btnUp.setImage(ImageCache.getCreatedImage(EImage.PREV_NAV.getPath()));
        btnUp.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false));
        btnUp.setToolTipText(Messages._Up);

        btnDown = new Button(this, SWT.NONE);
        btnDown.setImage(ImageCache.getCreatedImage(EImage.NEXT_NAV.getPath()));
        final GridData gd_btnDown = new GridData(SWT.CENTER, SWT.TOP, false, false);
        btnDown.setLayoutData(gd_btnDown);
        btnDown.setToolTipText(Messages._Down);

        btnRemove = new Button(this, SWT.NONE);
        btnRemove.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false));
        btnRemove.setImage(ImageCache.getCreatedImage(EImage.DELETE_OBJ.getPath()));
        btnRemove.setToolTipText(Messages._Del);
        //

        createExtentUIArea(parent);

        initUIListeners();
    }

    protected void initUIListeners() {

        initListenerForAddBtn();

        initListenerForRemoveBtn();

        initListenerForUpBtn();

        initListenerForDownBtn();
    }

    protected void initListenerForAddBtn() {

        btnAdd.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                onAdd();
            }
        });

    }

    protected void initListenerForRemoveBtn() {

        btnRemove.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                onRemove();
            }
        });

    }

    protected void initListenerForUpBtn() {

        btnUp.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                onMoveUp();
            }

        });

    }

    protected void initListenerForDownBtn() {

        btnDown.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                onMoveDown();
            }
        });

    }

    protected void onAdd() {

        if (!hasCandidateInfo() || infos.contains(getCandidateInfo())) {
            return;
        }

        addInfoToInfoTree(getCandidateInfo());
    }

    protected void onRemove() {

        if (!hasSelectionInInfoTree()) {
            return;
        }

        removeInfoFromInfoTree(getSelectionFromInfoTree());
    }

    protected void onMoveUp() {

        if (!hasSelectionInInfoTree()) {
            return;
        }

        moveInfoUp(getSelectionFromInfoTree());
    }

    protected void onMoveDown() {

        if (!hasSelectionInInfoTree()) {
            return;
        }

        moveInfoDown(getSelectionFromInfoTree());
    }

    public String[] getInfos() {
        return infos.toArray(new String[0]);
    }

    protected void addInfoToInfoTree(String info) {
        infos.add(info);
        setContentChanged(true);

        tvInfos.refresh();
        if (section != null) {
            section.autoCommit();
        }
    }

    private void removeInfoFromInfoTree(String info) {
        infos.remove(info);
        setContentChanged(true);

        tvInfos.refresh();
        if (section != null) {
            section.autoCommit();
        }
    }

    private boolean hasSelectionInInfoTree() {
        return getSelectionFromInfoTree() != null;
    }

    private String getSelectionFromInfoTree() {
        return getSelectionFromInfoViewer(tvInfos);
    }

    protected String getSelectionFromInfoViewer(Viewer targetViewer) {

        Object selectedObj = ((IStructuredSelection) targetViewer.getSelection()).getFirstElement();

        if (selectedObj == null) {
            return null;
        }

        return selectedObj.toString();
    }

    private void moveInfoUp(String info) {
        doMoveInfo(info, (infos.indexOf(info) - 1) < 0 ? 0 : (infos.indexOf(info) - 1));
    }

    private void moveInfoDown(String info) {
        doMoveInfo(info, (infos.indexOf(info) == infos.size() - 1) ? (infos.size() - 1) : (infos.indexOf(info) + 1));
    }

    private void doMoveInfo(String info, int newIndex) {
        infos.remove(info);
        infos.add(newIndex, info);
        tvInfos.refresh();
        setContentChanged(true);
        if (section != null) {
            section.autoCommit();
        }
    }

    public void setInfos(String[] infos) {

        initCandidateInfoUIArea();

        initInfoTrees(infos);
    }

    private void initInfoTrees(String[] annotationInfos) {

        infos.clear();

        for (String eachInfo : annotationInfos) {
            infos.add(eachInfo);
        }

        tvInfos.refresh();
    }

    public boolean isContentChanged() {
        return treeContentChanged;
    }

    public void setContentChanged(boolean changed) {
        this.treeContentChanged = changed;
    }

    protected abstract String getInfoColTitle();

    protected abstract void createExtentUIArea(Composite parent);

    protected abstract void createCandidateInfoUIArea(Composite parent);

    protected abstract boolean hasCandidateInfo();

    protected abstract String getCandidateInfo();

    protected abstract void initCandidateInfoUIArea();

    protected abstract void initParas(Object[] paras);
}
