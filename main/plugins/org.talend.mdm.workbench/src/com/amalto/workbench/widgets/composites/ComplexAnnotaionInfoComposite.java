// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import com.amalto.workbench.detailtabs.sections.BasePropertySection;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.providers.ListContentProvider;

public abstract class ComplexAnnotaionInfoComposite<T> extends Composite {

    protected TreeViewer tvInfos;

    protected Button btnAdd;

    protected Button btnRemove;

    protected Button btnRemoveAll;

    protected Button btnMoveUp;

    protected Button btnMoveDown;

    protected Button btnCopy;

    protected Button btnPaste;

    protected T copyedObj = null;

    protected Image nillableColImage = ImageCache.getCreatedImage(EImage.WILDCARD.getPath());

    protected List<T> infos;

    protected BasePropertySection section;

    public ComplexAnnotaionInfoComposite(Composite parent, int style, Object[] initParameters, BasePropertySection section) {
        super(parent, style);
        this.section = section;
        initParameters(initParameters);

        setLayout(new GridLayout());

        createTreeArea();

        createBtnArea();

        createExtentArea();

        initUIListeners();
    }

    protected void createTreeArea() {

        tvInfos = new TreeViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
        Tree tree = tvInfos.getTree();
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);
        final GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_tree.heightHint = 120;
        gd_tree.minimumHeight = 80;
        tree.setLayoutData(gd_tree);
        tvInfos.setContentProvider(new ListContentProvider());
        tvInfos.setLabelProvider(getLabelProviderForViewer());
        tvInfos.setColumnProperties(getColumnProperties());
        tvInfos.setCellEditors(getCellEditors());
        tvInfos.setCellModifier(getCellModifier());

        tvInfos.setInput(getInfos());

        fillColumnsInTree(tree);
    }

    protected void createBtnArea() {

        final Composite compBtns = new Composite(this, SWT.NONE);
        compBtns.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 7;
        compBtns.setLayout(gridLayout);

        btnAdd = new Button(compBtns, SWT.NONE);
        btnAdd.setImage(ImageCache.getCreatedImage(EImage.ADD_OBJ.getPath()));
        btnAdd.setToolTipText(Messages._Add);

        btnRemove = new Button(compBtns, SWT.NONE);
        btnRemove.setImage(ImageCache.getCreatedImage(EImage.DELETE_OBJ.getPath()));
        btnRemove.setToolTipText(Messages._Remove);

        btnRemoveAll = new Button(compBtns, SWT.NONE);
        btnRemoveAll.setImage(ImageCache.getCreatedImage(EImage.PROGRESS_REMALL.getPath()));
        btnRemoveAll.setToolTipText(Messages.ComplexAnnotaionXX_RemoveAll);

        btnMoveUp = new Button(compBtns, SWT.NONE);
        btnMoveUp.setImage(ImageCache.getCreatedImage(EImage.PREV_NAV.getPath()));
        btnMoveUp.setToolTipText(Messages.ComplexAnnotaionXX_MoveUp);

        btnMoveDown = new Button(compBtns, SWT.NONE);
        btnMoveDown.setImage(ImageCache.getCreatedImage(EImage.NEXT_NAV.getPath()));
        btnMoveDown.setToolTipText(Messages.ComplexAnnotaionXX_MoveDown);

        btnCopy = new Button(compBtns, SWT.NONE);
        btnCopy.setImage(ImageCache.getCreatedImage(EImage.COPY.getPath()));
        btnCopy.setToolTipText(Messages._Copy);

        btnPaste = new Button(compBtns, SWT.NONE);
        btnPaste.setImage(ImageCache.getCreatedImage(EImage.PASTE.getPath()));
        btnPaste.setToolTipText(Messages._Paste);

    }

    protected void initUIListeners() {

        initUIListnerForAddBtn();

        initUIListenersForRemoveBtn();

        initUIListenerForRemoveAllBtn();

        initUIListenerForMoveUpBtn();

        initUIListenerForMoveDownBtn();

        initUIListenerForCopyBtn();

        initUIListenerForPasteBtn();
    }

    private void initUIListnerForAddBtn() {

        btnAdd.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                T createdDefaultObj = createDefaultInfoObj();

                if (createdDefaultObj == null) {
                    return;
                }

                getInfos().add(createdDefaultObj);

                tvInfos.refresh();
                doCommit();
            }
        });
    }

    private void initUIListenersForRemoveBtn() {
        btnRemove.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                if (!hasSelection()) {
                    return;
                }

                getInfos().remove(getSelection());

                tvInfos.refresh();
                doCommit();
            }
        });
    }

    private void initUIListenerForRemoveAllBtn() {

        btnRemoveAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                getInfos().clear();

                tvInfos.refresh();
                doCommit();
            }
        });

    }

    private void initUIListenerForMoveUpBtn() {
        btnMoveUp.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                if (!hasSelection()) {
                    return;
                }

                moveInfoUp(getSelection());
                doCommit();
            }
        });
    }

    private void initUIListenerForMoveDownBtn() {

        btnMoveDown.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                if (!hasSelection()) {
                    return;
                }

                moveInfoDown(getSelection());
                doCommit();
            }
        });

    }

    private void initUIListenerForCopyBtn() {
        btnCopy.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                if (!hasSelection()) {
                    return;
                }

                copyedObj = getSelection();
            }
        });
    }

    private void initUIListenerForPasteBtn() {

        btnPaste.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                if (copyedObj == null) {
                    return;
                }

                if (!validateBeforePaste(copyedObj)) {
                    return;
                }

                getInfos().add(cloneCopyedObj(copyedObj));

                tvInfos.refresh();
                doCommit();

            }
        });

    }

    private boolean hasSelection() {
        return getSelection() != null;
    }

    @SuppressWarnings("unchecked")
    private T getSelection() {

        if (!(tvInfos.getSelection() instanceof IStructuredSelection)
                || ((IStructuredSelection) tvInfos.getSelection()).isEmpty()) {
            return null;
        }

        return (T) ((IStructuredSelection) tvInfos.getSelection()).getFirstElement();
    }

    private void moveInfoUp(T movedObj) {
        doMove(movedObj, (getInfos().indexOf(movedObj) - 1) < 0 ? 0 : (getInfos().indexOf(movedObj) - 1));
    }

    private void moveInfoDown(T movedObj) {
        doMove(movedObj,
                (getInfos().indexOf(movedObj) == getInfos().size() - 1) ? (getInfos().size() - 1)
                        : (getInfos().indexOf(movedObj) + 1));
    }

    private void doMove(T movedObj, int newIndex) {
        getInfos().remove(movedObj);
        getInfos().add(newIndex, movedObj);
        tvInfos.refresh();
    }

    public Viewer getViewer() {
        return tvInfos;
    }

    public void setInfos(List<T> objs) {

        if (objs == null) {
            return;
        }

        infos = objs;
        tvInfos.setInput(infos);
    }

    public void setInfos(T[] objs) {

        infos.clear();

        for (T eachInputedObj : objs) {

            if (eachInputedObj == null) {
                continue;
            }

            infos.add(eachInputedObj);
        }

        tvInfos.refresh();
    }

    @Override
    public void dispose() {
        super.dispose();

        nillableColImage.dispose();
    }

    protected List<T> getInfos() {
        return infos;
    }

    protected void doCommit() {
        if (section != null) {
            section.autoCommit();
        }
    }

    protected abstract ITableLabelProvider getLabelProviderForViewer();

    protected abstract void createTopExtentArea();

    protected abstract void createExtentArea();

    protected abstract void fillColumnsInTree(Tree tree);

    protected abstract T createDefaultInfoObj();

    protected abstract boolean validateBeforePaste(T copyedObj);

    protected abstract T cloneCopyedObj(T copyedObj);

    protected abstract String[] getColumnProperties();

    protected abstract CellEditor[] getCellEditors();

    protected abstract ICellModifier getCellModifier();

    protected abstract void initParameters(Object[] parameters);

}
