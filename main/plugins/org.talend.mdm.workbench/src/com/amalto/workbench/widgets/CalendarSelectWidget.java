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
package com.amalto.workbench.widgets;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;

import com.amalto.workbench.dialogs.CalendarDialog;

/**
 *
 * @author achen
 *
 */
public class CalendarSelectWidget {

    final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");//$NON-NLS-1$

    Button btn;

    CalendarDialog calendar;

    private Text text;

    private CalendarDialog cal;

    public CalendarSelectWidget(FormToolkit toolkit, final Composite composite, boolean hasDefaultTime) {
        // from

        text = toolkit.createText(composite, "", SWT.BORDER);//$NON-NLS-1$
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
        ((GridData) text.getLayoutData()).widthHint = 100;
        if (hasDefaultTime) {
            Calendar c = Calendar.getInstance();
            long yesterday = c.getTimeInMillis() - (1000 * 60 * 60 * 24);
            c.setTimeInMillis(yesterday);
            // text.setText(sdf.format(c.getTime()));
            text.pack();
        }

        btn = toolkit.createButton(composite, "", SWT.CENTER | SWT.ARROW | SWT.DOWN);//$NON-NLS-1$
        btn.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event event) {
                if (cal == null || (cal.getShell() != null && cal.getShell().isDisposed()))
                    cal = new CalendarDialog(composite.getShell());
                if (text.getText() != null && text.getText().length() > 0) {
                    try {
                        Date d = sdf.parse(text.getText());
                        cal.setDate(d);
                    } catch (ParseException pe) {

                    }
                }

                cal.addDateChangedListener(new SWTCalendarListener() {

                    public void dateChanged(SWTCalendarEvent calendarEvent) {
                        text.setText(sdf.format(calendarEvent.getCalendar().getTime()));
                    }
                });

                Point sbPoint = btn.getDisplay().map(btn.getParent(), null, btn.getLocation());
                cal.create();
                cal.getShell().setLocation(new Point(sbPoint.x, sbPoint.y + btn.getSize().y));
                cal.open();
            };
        });

    }

    public Button getBtn() {
        return btn;
    }

    public CalendarDialog getCalendar() {
        return calendar;
    }

    public Text getText() {
        return text;
    }

}
