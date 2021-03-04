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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.utils.Util;

/**
 * DOC nrousseau class global comment. Detailled comment <br/>
 * adapted from the TabbedPropertySheetWidgetFactory
 *
 */
public class WidgetFactory extends FormToolkit {

    static WidgetFactory factory;

    public FocusListener focusListener = new FocusListener() {

        public void focusGained(FocusEvent e) {
            Control control = (Control) e.widget;
            if (control instanceof Composite) {
                Composite parent = (Composite) control;
                setChildFocus(parent);
            } else {
                if (control instanceof Text) {
                    ((Text) control).selectAll();
                    // System.out.println(((Text)control).getText() + " focusGained");
                }
                if (control instanceof StyledText) {
                    ((StyledText) control).selectAll();
                }
            }
        }

        public void focusLost(FocusEvent e) {
            Control control = (Control) e.widget;
            if (control instanceof Composite) {
                Composite parent = (Composite) control;
                setChildFocus(parent);
            } else {
                if (control instanceof Text) {
                    Text t = (Text) control;
                    t.clearSelection();
                    // System.out.println(t.getText() + " focusLost");
                }
                if (control instanceof StyledText) {
                    StyledText t = (StyledText) control;
                    t.setSelection(0, 0);
                }
            }
        }
    };

    public static TraverseListener textTraverseListener = new TraverseListener() {

        public void keyTraversed(TraverseEvent e) {
            if (e.detail == SWT.TRAVERSE_TAB_NEXT) {
                e.doit = true;
            }
        }
    };

    public static WidgetFactory getWidgetFactory() {
        if (factory == null) {
            factory = new WidgetFactory();
        }
        return factory;
    }

    /**
     * private constructor.
     */
    private WidgetFactory() {
        super(Display.getCurrent());
    }

    /**
     * Creates the tab folder as a part of the form.
     *
     * @param parent the composite parent.
     * @param style the tab folder style.
     * @return the tab folder
     */
    public CTabFolder createTabFolder(Composite parent, int style) {
        CTabFolder tabFolder = new CTabFolder(parent, style);
        return tabFolder;
    }

    /**
     * Creates the tab item as a part of the tab folder.
     *
     * @param tabFolder the parent.
     * @param style the tab folder style.
     * @return the tab item.
     */
    public CTabItem createTabItem(CTabFolder tabFolder, int style) {
        CTabItem tabItem = new CTabItem(tabFolder, style);
        return tabItem;
    }

    /**
     * Creates the list as a part of the form.
     *
     * @param parent the composite parent.
     * @param style the list style.
     * @return the list.
     */
    public List createList(Composite parent, int style) {
        List list = new org.eclipse.swt.widgets.List(parent, style);
        return list;
    }

    public Composite createComposite(Composite parent, int style) {
        Composite c = super.createComposite(parent, style);
        adapt(c);
        paintBordersFor(c);
        return c;
    }

    @Override
    public void adapt(Composite composite) {
        composite.setBackground(getColors().getBackground());

        // add a focus listener

        addChildListener(composite, focusListener);
        // composite.addFocusListener(focusListener);
        // composite.getParent().addFocusListener(focusListener);
        composite.setMenu(composite.getParent().getMenu());
    }

    private void addChildListener(Composite parent, FocusListener listener) {
        if (parent == null) {
            return;
        }
        for (Control control : parent.getChildren()) {
            if ((control instanceof Combo) || (control instanceof Button)) {
                continue;
            } else if ((control instanceof Text) || (control instanceof StyledText)) {
                control.addFocusListener(listener);
                control.addTraverseListener(textTraverseListener);
            } else {
                if (control instanceof Composite) {
                    addChildListener((Composite) control, listener);
                }
            }
        }

    }

    private boolean setChildFocus(Composite parent) {
        if (parent == null) {
            return false;
        }
        for (Control control : parent.getChildren()) {
            if ((control instanceof Combo) || (control instanceof Button)) {
                continue;
            } else if ((control instanceof Text) || (control instanceof StyledText)) {
                control.setFocus();
                return true;
            } else {
                if (control instanceof Composite) {
                    if (setChildFocus((Composite) control)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Composite createComposite(Composite parent) {
        Composite c = createComposite(parent, SWT.NONE);
        return c;
    }

    /**
     * Creates a plain composite as a part of the form.
     *
     * @param parent the composite parent.
     * @param style the composite style.
     * @return the composite.
     */
    public Composite createPlainComposite(Composite parent, int style) {
        Composite c = super.createComposite(parent, style);
        c.setBackground(parent.getBackground());
        paintBordersFor(c);
        return c;
    }

    /**
     * Creates a scrolled composite as a part of the form.
     *
     * @param parent the composite parent.
     * @param style the composite style.
     * @return the composite.
     */
    public ScrolledComposite createScrolledComposite(Composite parent, int style) {
        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, style);
        return scrolledComposite;
    }

    /**
     * Creates a combo box as a part of the form.
     *
     * @param parent the combo box parent.
     * @param comboStyle the combo box style.
     * @return the combo box.
     */
    public CCombo createCCombo(Composite parent, int comboStyle) {
        CCombo combo = new CCombo(parent, comboStyle);
        adapt(combo, true, false);
        // Bugzilla 145837 - workaround for no borders on Windows XP
        if (getBorderStyle() == SWT.BORDER) {
            combo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
        }
        return combo;
    }

    /**
     * Creates a combo box as a part of the form.
     *
     * @param parent the combo box parent.
     * @return the combo box.
     */
    public CCombo createCCombo(Composite parent) {
        return createCCombo(parent, SWT.FLAT | SWT.READ_ONLY);
    }

    /**
     * Creates a group as a part of the form.
     *
     * @param parent the group parent.
     * @param text the group title.
     * @return the composite.
     */
    public Group createGroup(Composite parent, String text) {
        Group group = new Group(parent, SWT.SHADOW_NONE);
        group.setText(text);
        group.setBackground(getColors().getBackground());
        group.setForeground(getColors().getForeground());
        return group;
    }

    /**
     * Creates a flat form composite as a part of the form.
     *
     * @param parent the composite parent.
     * @return the composite.
     */
    public Composite createFlatFormComposite(Composite parent) {
        Composite composite = createComposite(parent);
        FormLayout layout = new FormLayout();
        layout.marginWidth = 5 + 2;
        layout.marginHeight = 6;
        layout.spacing = 6 + 1;
        composite.setLayout(layout);
        return composite;
    }

    /**
     * Creates a label as a part of the form.
     *
     * @param parent the label parent.
     * @param text the label text.
     * @return the label.
     */
    public CLabel createCLabel(Composite parent, String text) {
        return createCLabel(parent, text, SWT.NONE);
    }

    public Text createText(Composite parent, String value) {

        Text t = super.createText(parent, value);
        return t;
    }

    public Text createText(Composite parent, String value, int style) {

        Text t = super.createText(parent, value, style);
        Util.createCompDropTarget(t);
        return t;
    }

    /**
     * Creates a label as a part of the form.
     *
     * @param parent the label parent.
     * @param text the label text.
     * @param style the label style.
     * @return the label.
     */
    public CLabel createCLabel(Composite parent, String text, int style) {
        final CLabel label = new CLabel(parent, style);
        label.setBackground(parent.getBackground());
        label.setText(text);
        return label;
    }

    public void dispose() {
        if (getColors() != null) {
            super.dispose();
        }
    }

    public void setBackGround(Composite comp, Color color) {
        comp.setBackground(color);
        Control[] children = comp.getChildren();
        for (Control child : children) {
            if (child instanceof Composite) {
                setBackGround((Composite) child, color);
            } else {
                child.setBackground(color);
            }
        }
    }
}
