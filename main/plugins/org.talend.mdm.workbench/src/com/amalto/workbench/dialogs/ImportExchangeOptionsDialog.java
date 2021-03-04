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
package com.amalto.workbench.dialogs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.talend.core.runtime.util.SharedStudioUtils;
import org.talend.mdm.commmon.util.workbench.ZipToFile;

import com.amalto.workbench.MDMWorbenchPlugin;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.HttpClientUtil;

public class ImportExchangeOptionsDialog extends Dialog implements SelectionListener {

    private static Log log = LogFactory.getLog(ImportExchangeOptionsDialog.class);

    private Button exportsBtn, dataModelBtn, executeBtn;

    private Table exchangeDwnTable;

    private FormToolkit toolkit;

    private StringBuffer zipFileRepository;

    private boolean export = true;

    private boolean radioEnable;

    private JSONObject[] dataContent = null;

    private String revision;

    private CCombo revisionCombo;

    private static String EXCHANGE_DOWNLOAD_URL = "http://talendforge.org/exchange/mdm/api/get_revision_list.php?";//$NON-NLS-1$

    private static String COLUMN_EXTENSION_NAME = "extension_name";//$NON-NLS-1$

    private static String COLUMN_REVISION_NAME = "revision_name";//$NON-NLS-1$

    private static String COLUMN_URL_NAME = "download_url";//$NON-NLS-1$

    private static String REVISION_LIST_URL = "http://talendforge.org/exchange/mdm/api/get_version_list.php"; //$NON-NLS-1$

    public ImportExchangeOptionsDialog(Shell parentShell, FormToolkit toolkit, boolean importOption,
            StringBuffer zipFileRepository) {
        super(parentShell);
        export = importOption;
        this.toolkit = toolkit;
        this.zipFileRepository = zipFileRepository;
    }

	public boolean chooseExport() {
		return exportsBtn.getSelection();
	}

    public void setRadioEnable(boolean radioEnable) {
		this.radioEnable = radioEnable;
	}

	@Override
    protected Control createDialogArea(Composite parent) {
        parent.getShell().setText(Messages.ImportExchangeOptionsDialog_DialogTitle);

        Composite composite = (Composite) super.createDialogArea(parent);

        GridLayout layout = (GridLayout) composite.getLayout();
        layout.numColumns = 5;

        exportsBtn = new Button(composite, SWT.RADIO);
        exportsBtn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        exportsBtn.setText(Messages.ImportExchangeOptionsDialog_Exports);
        exportsBtn.setEnabled((export | radioEnable)? true : false);
        exportsBtn.setSelection(false);

        dataModelBtn = new Button(composite, SWT.RADIO);
        dataModelBtn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        dataModelBtn.setText(Messages.ImportExchangeOptionsDialog_Datamodels);
        dataModelBtn.setEnabled((!export)| radioEnable ? true : false);
        dataModelBtn.setSelection(false);

        Label label = new Label(composite, SWT.BORDER);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        label.setText(Messages.ImportExchangeOptionsDialog_RevisionXX);

        revisionCombo = new CCombo(composite, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.FLAT| SWT.BORDER);
        GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        revisionCombo.setLayoutData(gd);
        gd.widthHint = 100;
        Set<String> revisions = new HashSet<String>();
        Map<String, String> rMap = getRevisionMap();
        revisions.addAll(rMap.values());
        revisionCombo.setItems(revisions.toArray(new String[0]));

        // get current plugin revision
        String bundleVersion = MDMWorbenchPlugin.getDefault().getVersion();
        String version = bundleVersion.split("_")[0];//$NON-NLS-1$
        revision = rMap.get(version);
        if (revision == null) {
            revision = "1"; //$NON-NLS-1$
        }
        revisionCombo.setText(revision);

        revisionCombo.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                revision = revisionCombo.getText();
                fillInTable();
            }
        });
        executeBtn = new Button(composite, SWT.PUSH);
        executeBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1));
        executeBtn.addSelectionListener(this);
        executeBtn.setImage(ImageCache.getCreatedImage(EImage.REFRESH.getPath()));

        if (exportsBtn.getSelection()) {
            exchangeDwnTable = new Table(composite, SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        } else {
            exchangeDwnTable = new Table(composite, SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
                    | SWT.MULTI);
        }

        exchangeDwnTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
        ((GridData) exchangeDwnTable.getLayoutData()).heightHint = 300;
        exchangeDwnTable.setHeaderVisible(true);
        exchangeDwnTable.setLinesVisible(true);

        final TableColumn column1 = new TableColumn(exchangeDwnTable, SWT.NONE);
        column1.setText(Messages.ImportExchangeOptionsDialog_Name);
        final TableColumn column2 = new TableColumn(exchangeDwnTable, SWT.NONE);
        column2.setText(Messages.ImportExchangeOptionsDialog_Revision);
        final TableColumn column3 = new TableColumn(exchangeDwnTable, SWT.NONE);
        column3.setText(Messages.ImportExchangeOptionsDialog_Url);
        column1.setWidth(100);
        column2.setWidth(100);
        column3.setWidth(400);

        exchangeDwnTable.addListener(SWT.SetData, new Listener() {

            public void handleEvent(Event e) {
                TableItem item = (TableItem) e.item;
                int index = exchangeDwnTable.indexOf(item);
                try {
                    JSONObject datum = dataContent[index];
                    item.setText(new String[] { datum.get(COLUMN_EXTENSION_NAME).toString(),
                            datum.get(COLUMN_REVISION_NAME).toString(), datum.get(COLUMN_URL_NAME).toString() });
                } catch (JSONException je) {
                    log.error(je.getMessage(), je);
                }
            }
        });

        exchangeDwnTable.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                getButton(IDialogConstants.OK_ID).setEnabled(exchangeDwnTable.getSelection().length >= 1 ? true : false);
            }

        });

        // Add sort indicator and sort data when column selected
        Listener sortListener = new Listener() {

            @SuppressWarnings("unchecked")
            public void handleEvent(Event e) {
                // determine new sort column and direction
                TableColumn sortColumn = exchangeDwnTable.getSortColumn();
                final TableColumn currentColumn = (TableColumn) e.widget;
                int dir = exchangeDwnTable.getSortDirection();
                if (sortColumn == currentColumn) {
                    dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
                } else {
                    exchangeDwnTable.setSortColumn(currentColumn);
                    dir = SWT.UP;
                }
                // sort the data based on column and direction
                final int direction = dir;
                Arrays.sort(dataContent, new Comparator() {

                    public int compare(Object arg0, Object arg1) {
                        JSONObject jsonA = (JSONObject) arg0;
                        JSONObject jsonB = (JSONObject) arg1;
                        String valueA = null, valueB = null;

                        try {
                            if (currentColumn == column1) {
                                valueA = jsonA.get(COLUMN_EXTENSION_NAME).toString();
                                valueB = jsonB.get(COLUMN_EXTENSION_NAME).toString();
                            } else if (currentColumn == column2) {
                                valueA = jsonA.get(COLUMN_REVISION_NAME).toString();
                                valueB = jsonB.get(COLUMN_REVISION_NAME).toString();
                            } else {
                                valueA = jsonA.get(COLUMN_URL_NAME).toString();
                                valueB = jsonB.get(COLUMN_URL_NAME).toString();
                            }

                            if (valueA.equals(valueB)) {
                                return 0;
                            }
                            if (direction == SWT.UP) {
                                return valueA.compareTo(valueB) < 0 ? -1 : 1;
                            }
                            return valueA.compareTo(valueB) < 0 ? 1 : -1;
                        } catch (JSONException je) {
                            log.error(je.getMessage(), je);
                            return -1;
                        }
                    }
                });
                // update data displayed in table
                exchangeDwnTable.setSortDirection(dir);
                exchangeDwnTable.clearAll();
            }
        };

        column1.addListener(SWT.Selection, sortListener);
        column2.addListener(SWT.Selection, sortListener);
        column3.addListener(SWT.Selection, sortListener);

        exchangeDwnTable.setSortColumn(column1);
        exchangeDwnTable.setSortDirection(SWT.UP);

        return composite;
    }

    private HashMap<String, String> getRevisionMap() {
        HashMap<String, String> idRevisonMap = new HashMap<String, String>();
        try {
            String content = HttpClientUtil.getStringContentByHttpget(REVISION_LIST_URL);
            if (StringUtils.isEmpty(content)) {
                throw new Exception("no response content"); //$NON-NLS-1$
            }
            JSONArray jsonArray = new JSONArray(content);
            JSONObject[] dc = new JSONObject[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.get("name").toString(); //$NON-NLS-1$
                String revision = jsonObject.getString("nb_extensions");//$NON-NLS-1$
                idRevisonMap.put(name, revision);
                dc[i] = jsonObject;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return idRevisonMap;
    }

    public void fillInTable() {
        clearTable();
        String url = EXCHANGE_DOWNLOAD_URL + "version=" + revision + "&categories=" + (exportsBtn.getSelection() ? "2" : "1");//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
        try {
            String out = HttpClientUtil.getStringContentByHttpget(url);
            if (StringUtils.isEmpty(out)) {
                throw new Exception("no response content"); //$NON-NLS-1$
            }
            JSONArray jsonArray = new JSONArray(out);
            dataContent = new JSONObject[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.get(COLUMN_EXTENSION_NAME).toString();
                String revision = jsonObject.getString(COLUMN_REVISION_NAME);
                String download_url = jsonObject.getString(COLUMN_URL_NAME).toString();
                TableItem item = new TableItem(exchangeDwnTable, SWT.NONE);
                item.setText(new String[] { name, revision, download_url });
                dataContent[i] = jsonObject;
            }
            exchangeDwnTable.setItemCount(dataContent.length);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        getButton(IDialogConstants.OK_ID).addSelectionListener(this);
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        Button btn = (Button) e.getSource();
        if (btn == executeBtn) {
            fillInTable();
        }
    }

    @Override
    protected void okPressed() {
        // no close let Action Handler handle it
        unzipDownloadRes(exportsBtn.getSelection());
        super.okPressed();
    }

    private void unzipDownloadRes(boolean export) {
        JSONObject datum = dataContent[exchangeDwnTable.getSelectionIndex()];
        InputStream stream = null;
        OutputStream out = null;
        try {
            String url = datum.getString(COLUMN_URL_NAME);
            stream = HttpClientUtil.getInstreamContentByHttpget(url);
            if (null == stream) {
                throw new RuntimeException("cannot get the content stream"); //$NON-NLS-1$
            }
            IPath parentPath = SharedStudioUtils.getTempFolderPath().removeLastSegments(1);
            String downloadFolder = parentPath.toPortableString() + File.separator + (export ? "temp" : "xsdTemp");//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            String subFolderForTmp = downloadFolder + File.separator + "tmp" + System.currentTimeMillis();//$NON-NLS-1$
            File tempFile = new File(subFolderForTmp + File.separator + "tmp" + System.currentTimeMillis());//$NON-NLS-1$

            File dir = new File(downloadFolder);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File subDir = new File(subFolderForTmp);
            if (!subDir.exists()) {
                subDir.mkdir();
            }
            if (zipFileRepository.length() > 0) {
                zipFileRepository.delete(0, zipFileRepository.length());
            }
            out = new FileOutputStream(tempFile);
            IOUtils.copy(stream, out);
            out.flush();
            if (!export) {
            	ZipToFile.unZipFile(tempFile.getAbsolutePath(), subFolderForTmp);
                boolean result = false;
                int tryCount = 0;
                while (!result && tryCount++ < 10) {
                    System.gc();
                    result = tempFile.delete();
                }
                zipFileRepository.append(subFolderForTmp);
            } else {
                zipFileRepository.append(tempFile.getAbsolutePath());
            }
        } catch (Exception e1) {
            final MessageDialog dialog = new MessageDialog(this.getParentShell().getShell(),
                    Messages.ImportExchangeOptionsDialog_ParsingError, null, e1.getMessage(), MessageDialog.ERROR,
                    new String[] { IDialogConstants.OK_LABEL }, 0);
            dialog.open();
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(stream);
        }
    }

    protected void clearTable() {
        exchangeDwnTable.removeAll();
    }
}
