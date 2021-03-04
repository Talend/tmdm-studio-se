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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarListener;

import com.amalto.workbench.i18n.Messages;

public class CalendarDialog extends Dialog {

    private SWTCalendar swtcal;
    private Date date = null;
    private List<SWTCalendarListener> listeners = new ArrayList<SWTCalendarListener>();

    public CalendarDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.CalendarDialog_DatePicker);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        comp.setLayout(gridLayout);
        swtcal = new SWTCalendar(comp);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.widthHint = 270;
        swtcal.setLayoutData(layoutData);
        for (SWTCalendarListener listener : listeners) {
            swtcal.addSWTCalendarListener(listener);
        }

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            swtcal.setCalendar(calendar);
        }

        return comp;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        return null;
    }

    public Calendar getCalendar() {
        return swtcal.getCalendar();
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void addDateChangedListener(SWTCalendarListener listener) {
        listeners.add(listener);
    }

    @Override
    public Shell getShell() {
        if (swtcal.isDisposed()) {
            return null;
        }
        return swtcal.getShell();
    }

}
