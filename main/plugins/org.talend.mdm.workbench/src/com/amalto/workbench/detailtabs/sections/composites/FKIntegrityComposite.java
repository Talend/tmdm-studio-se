// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDParticle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.amalto.workbench.detailtabs.sections.ElementFKSection;
import com.amalto.workbench.i18n.Messages;

public class FKIntegrityComposite extends Composite {

	private static final String X_FK_INTEGRITY_OVERRIDE = "X_FKIntegrity_Override"; //$NON-NLS-1$

	private static final String X_FK_INTEGRITY = "X_FKIntegrity"; //$NON-NLS-1$

	private final Button btnAllowFkIntegrity;

	private final Button btnEnforceFkIntegrity;

	private final ViewRefreshAdapter refreshAdapter = new ViewRefreshAdapter();

	private XSDElementDeclaration xsdComponent;

	private ElementFKSection elementFKSection;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 * @param elementFKSection
	 * @param curXSDComponent
	 * @param xsdElementDeclaration
	 */
	public FKIntegrityComposite(Composite parent, int style,
			XSDComponent xsdComponent, ElementFKSection elementFKSection) {
		super(parent, style);

		GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 10;
		setLayout(gridLayout);

		btnEnforceFkIntegrity = new Button(this, SWT.FLAT | SWT.CHECK);
		btnEnforceFkIntegrity.setText(Messages.FKIntegrityComposite_EnforceFKIntegrity);
		btnEnforceFkIntegrity.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean enforceFK = btnEnforceFkIntegrity.getSelection();
				btnAllowFkIntegrity.setEnabled(enforceFK);
				if (!enforceFK) {
					// Disable allow fk override once fk integrity is disabled.
					btnAllowFkIntegrity.setSelection(false);
				}
                updateModel();
			}
		});
		btnEnforceFkIntegrity.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));
		btnEnforceFkIntegrity.setSelection(true);

		btnAllowFkIntegrity = new Button(this, SWT.CHECK);
		btnAllowFkIntegrity.setText(Messages.FKIntegrityComposite_AllowFKIntergrityOverride);
		btnAllowFkIntegrity.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateModel();
			}
		});
		btnAllowFkIntegrity.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));
		btnAllowFkIntegrity.setSelection(false);

		this.elementFKSection = elementFKSection;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

    @Override
    public void dispose() {
        // Remove listener on disposal (no more need to keep view in sync with model)
        xsdComponent.eAdapters().remove(refreshAdapter);
        super.dispose();
    }

	private void refresh() {
		XSDAnnotation annotation = xsdComponent.getAnnotation();
		if (annotation == null) {
			annotation = XSDFactory.eINSTANCE.createXSDAnnotation();
		}

		Element fkIntegrityElement = null;
		Element fkIntegrityOverrideElement = null;

		EList<Element> appInfo = annotation.getApplicationInformation();
		for (Element currentElement : appInfo) {
			String source = currentElement.getAttribute("source"); //$NON-NLS-1$
			if (X_FK_INTEGRITY.endsWith(source)) {
				fkIntegrityElement = currentElement;
			} else if (X_FK_INTEGRITY_OVERRIDE.endsWith(source)) {
				fkIntegrityOverrideElement = currentElement;
			}
		}

		if (fkIntegrityElement != null) {
			String textContent = fkIntegrityElement.getTextContent();
			boolean enabled = textContent == null ? true : Boolean
					.valueOf(textContent);
			btnEnforceFkIntegrity.setSelection(enabled);
		} else {
			btnEnforceFkIntegrity.setSelection(true); // default is true
		}

		if (fkIntegrityOverrideElement != null) {
			String textContent = fkIntegrityOverrideElement.getTextContent();
			boolean enabled = textContent == null ? true : Boolean
					.valueOf(textContent);
			btnAllowFkIntegrity.setSelection(enabled);
		} else {
			btnAllowFkIntegrity.setSelection(false); // default is false for override
		}

        // refresh buttion enabled
        if (xsdComponent.getAnnotation() == null) {
            btnEnforceFkIntegrity.setEnabled(false);
            btnAllowFkIntegrity.setEnabled(false);
        } else {
            btnEnforceFkIntegrity.setEnabled(true);
            btnAllowFkIntegrity.setEnabled(btnEnforceFkIntegrity.getSelection());
        }
	}

	private void updateModel() {
		XSDAnnotation annotation = xsdComponent.getAnnotation();
		if (annotation == null) {
			annotation = XSDFactory.eINSTANCE.createXSDAnnotation();
			xsdComponent.setAnnotation(annotation);
		}

		Element fkIntegrityElement = null;
		Element fkIntegrityOverrideElement = null;
		EList<Element> appInfo = annotation.getApplicationInformation();
		for (Element currentElement : appInfo) {
			String source = currentElement.getAttribute("source"); //$NON-NLS-1$
			if (X_FK_INTEGRITY.endsWith(source)) {
				fkIntegrityElement = currentElement;
			} else if (X_FK_INTEGRITY_OVERRIDE.endsWith(source)) {
				fkIntegrityOverrideElement = currentElement;
			}
		}

		updateModelElement(xsdComponent, fkIntegrityElement, X_FK_INTEGRITY,
				btnEnforceFkIntegrity.getSelection());
		updateModelElement(xsdComponent, fkIntegrityOverrideElement,
				X_FK_INTEGRITY_OVERRIDE, btnAllowFkIntegrity.getSelection());
		this.elementFKSection.autoCommit();
	}

	private static void updateModelElement(XSDElementDeclaration xsdComponent,
			Element property, String type, boolean value) {
		Element xsdDeclarationDomElement = xsdComponent.getElement();

		if (property == null) {
			Document ownerDocument = xsdDeclarationDomElement
					.getOwnerDocument();
			property = ownerDocument.createElementNS(
					xsdDeclarationDomElement.getNamespaceURI(), "appinfo"); //$NON-NLS-1$
			property.setPrefix("xsd"); //$NON-NLS-1$
			property.setAttribute("source", type); //$NON-NLS-1$
			property.setTextContent(Boolean.toString(value));

			xsdComponent.getAnnotation().getApplicationInformation()
					.add(property);
			xsdComponent.getAnnotation().getElement().appendChild(property);
		} else {
			xsdComponent.getAnnotation().getApplicationInformation()
					.remove(property);
			property.setTextContent(Boolean.toString(value));
			xsdComponent.getAnnotation().getApplicationInformation()
					.add(property);
		}
	}

    private class ViewRefreshAdapter implements Adapter {

        private Notifier notifier;

        @Override
        public void setTarget(Notifier notifier) {
            this.notifier = notifier;
        }

        @Override
        public void notifyChanged(Notification event) {
            if (!event.isTouch()) {
                refresh();
            }
        }

        @Override
        public boolean isAdapterForType(Object obj) {
            return obj instanceof XSDElementDeclaration;
        }

        @Override
        public Notifier getTarget() {
            return notifier;
        }
    }

	public void setXSDComponent(XSDComponent xsdComponent) {
		if (xsdComponent == null) {
			throw new IllegalArgumentException(
					Messages.FKIntegrityComposite_ArgCannotbeNull);
		}
		if (!(xsdComponent instanceof XSDParticle)) {
            throw new IllegalArgumentException(Messages.bind(Messages.FKIntegrityComposite_ExceptionInfo,
                    XSDElementDeclaration.class.getName(), xsdComponent.getClass().getName()));
		}

		// Take into account only component change
        if (xsdComponent != this.xsdComponent) {
            removeRefreshAdapter();
            this.xsdComponent = (XSDElementDeclaration) ((XSDParticle) xsdComponent).getContent();
            this.xsdComponent.eAdapters().add(refreshAdapter);
        }

		// Update UI with the information in the XSDComponent
		refresh();
	}

    public void removeRefreshAdapter() {
        if (this.xsdComponent != null && refreshAdapter != null) {
            this.xsdComponent.eAdapters().remove(refreshAdapter);
        }
    }
}
