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
package com.amalto.workbench.editors.xsdeditor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.WorkbenchPartReference;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageSelectionProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsd.ui.internal.adapters.CategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.RedefineCategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDDirectivesManager;
import org.eclipse.wst.xsd.ui.internal.editor.XSDTabbedPropertySheetPage;
import org.eclipse.wst.xsd.ui.internal.text.XSDModelAdapter;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDDiagnostic;
import org.eclipse.xsd.XSDDiagnosticSeverity;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.talend.mdm.commmon.util.core.CommonUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;

import com.amalto.workbench.dialogs.ErrorExceptionDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.editors.IServerObjectEditorState;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.datamodel.SchemaTreeContentProvider;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDUtil;
import com.amalto.workbench.views.MDMPerspective;
import com.amalto.workbench.webservices.WSDataModel;

@SuppressWarnings("nls")
public class XSDEditor extends MultiPageEditorPart
        implements IServerObjectEditorState, ITabbedPropertySheetPageContributor, IPropertyListener {

    public static final String MSG_OMIT[] = {
            "XSD: The value '1' of attribute 'maxOccurs' must be one of  as constrained by 'http://www.w3.org/2001/XMLSchema#maxOccurs_._type'",
            "XSD: The attribute may not have duplicate name and target namespace",
            "XSD: The type may not have duplicate name and target namespace",
            "XSD: The attribute group may not have duplicate name and target namespace",
            "The complex type may not have duplicate name",
            "XSD: An element reference may only have an id, minOccurs, or maxOccurs" };

    private static final Log LOG = LogFactory.getLog(XSDEditor.class);

    public static final String CONTRUIBUTIONID_DATAMODELPAGE = "org.talend.mdm.workbench.propertyContributor.datamodel";

    protected static int MODEL_PAGE_INDEX = -1;

    public static int SOURCE_PAGE_INDEX = 0;

    private TreeExpandHelper expandHelper = new TreeExpandHelper();

    protected int preActivePageIndex = -1;

    protected TreeObject xobject;

    private XSDSelectionManagerSelectionListener fXSDSelectionListener;

    private byte[] fileContents = null;

    boolean hasXSDErrors = false;

    public void setXObject(TreeObject xobject) {
        this.xobject = xobject;
        InputStreamReader reader = null;
        try {// temporarily store the file data for restore
            IFile file = getXSDFile(xobject);
            reader = new InputStreamReader(file.getContents());
            fileContents = IOUtils.toByteArray(reader, "utf-8");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (reader != null) {
                IOUtils.closeQuietly(reader);
            }
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        XSDDirectivesManager.removeUnusedXSDImports(getXSDSchema());
        structuredTextEditor.doSave(monitor);
        InputStream stream = null;
        try {
            EditorPart part = (EditorPart) getSelectedPage();
            String xsd = null;
            if (null != part) {
                if (part instanceof StructuredTextEditor) {
                    xsd = ((StructuredTextEditor) part).getTextViewer().getDocument().get();
                } else {
                    // main page or er editor
                    DataModelMainPage dmp = getdMainPage();
                    xsd = dmp.getXSDSchemaString();
                }
            }
            if (null != xsd) {
                DataModelMainPage mainPage = getdMainPage();
                WSDataModel wsDataModel = (WSDataModel) xobject.getWsObject();
                wsDataModel.setXsdSchema(xsd);
                if (mainPage != null) {
                    mainPage.save(xsd);
                }
                fileContents = xsd.getBytes("utf-8");
            }
            getDataModelEditorPage().setDirty(false);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(stream);
            if (null != monitor) {
                monitor.done();
            }
        }
    }

    protected IFile getXSDFile(TreeObject object) throws Exception {
        return XSDEditorUtil.createFile(object);
    }

    boolean doUpdateSourceLocation = false;

    private int lastPageIndex = -1;

    private StructuredTextEditor structuredTextEditor;

    private XSDSchema xsdSchema;

    private ISelectionProvider editorSelectionProvider;

    private XSDEditorContentOutline contentOutline;

    @Override
    protected void pageChange(int newPageIndex) {
        resetTreeSelection(newPageIndex);

        super.pageChange(newPageIndex);
        doUpdateSourceLocation = newPageIndex == SOURCE_PAGE_INDEX;
        if (doUpdateSourceLocation && fXSDSelectionListener != null) {
            fXSDSelectionListener.doSetSelection();
        }
        doPageChanged(newPageIndex, lastPageIndex);
        refreshPropertyView();
        updateContentOutlinePage();
        setFocus();
        lastPageIndex = newPageIndex;
    }

    protected void doPageChanged(int newPageIndex, int lastPageIndex) {
        if (xobject == null) {
            return;
        }
        try {
            if (newPageIndex == MODEL_PAGE_INDEX) {
                DataModelMainPage mainPage = getDataModelEditorPage();
                if (mainPage != null) {
                    if (lastPageIndex == SOURCE_PAGE_INDEX) {
                        validateXsdSourceEditor();
                        if (!hasXSDErrors) {
                            String xsd = getSourcePageDocument();
                            XSDSchema schema = Util.createXsdSchema(xsd, xobject);
                            mainPage.setXsdSchema(schema);
                        }
                    }
                    mainPage.refresh();
                    //
                    expandHelper.recoverExpandState(mainPage);
                }
            } else if (newPageIndex == SOURCE_PAGE_INDEX) {
                if (hasXSDErrors) {
                    return;
                }
                // save DataModelMainPage's contents to file
                DataModelMainPage mainPage = getDataModelEditorPage();
                expandHelper.recordExpandState(mainPage);
                if (mainPage != null && this.isDirty()) {
                    String xsd = mainPage.getXSDSchemaString();
                    xsd = Util.formatXsdSource(xsd);
                    WSDataModel wsDataModel = (WSDataModel) xobject.getWsObject();
                    wsDataModel.setXsdSchema(xsd);
                    IFile file = getXSDFile(xobject);
                    file.setCharset("utf-8", null);
                    file.setContents(new ByteArrayInputStream(xsd.getBytes("utf-8")), IFile.FORCE, null);
                }
            }
        } catch (SAXParseException ex) {
            // do nothing
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public boolean hasXSDErrors() {
        return this.hasXSDErrors;
    }

    private void updateContentOutlinePage() {
        IContentOutlinePage outlinePage = getActiveEditor().getAdapter(IContentOutlinePage.class);
        contentOutline.setActiveOutlinePage(outlinePage);
    }

    private DataModelMainPage getDataModelEditorPage() {

        return (DataModelMainPage) getEditor(MODEL_PAGE_INDEX);
    }

    private class XSDSelectionManagerSelectionListener implements ISelectionChangedListener {

        private Map<XSDElementDeclaration, XSDElementDeclaration> declarMap = new HashMap<XSDElementDeclaration, XSDElementDeclaration>();

        /**
         * Determines DOM node based on object (xsd node)
         *
         * @param object
         * @return
         */
        private Object getObjectForOtherModel(Object object) {
            Node node = null;
            if (object instanceof Node) {
                node = (Node) object;
            } else if (object instanceof XSDComponent) {
                if (object instanceof XSDElementDeclaration) {
                    XSDElementDeclaration selected = (XSDElementDeclaration) object;
                    String name = selected.getName();

                    EList<XSDElementDeclaration> elementDeclarations = getXSDSchema().getElementDeclarations();

                    for (XSDElementDeclaration elem : elementDeclarations) {
                        if (elem.getName().equals(name)) {
                            node = elem.getElement();
                            break;
                        }
                    }
                } else if (object instanceof XSDParticle) {
                    XSDTerm term = ((XSDParticle) object).getTerm();
                    if (term instanceof XSDElementDeclaration) {
                        XSDElementDeclaration declar = (XSDElementDeclaration) term;
                        XSDElementDeclaration[] xsdDeclarations = getAllMarkableDeclarationOfParticle();
                        if (xsdDeclarations != null) {
                            XSDElementDeclaration content = (XSDElementDeclaration) ((XSDParticle) object).getContent();
                            boolean isReference = content.isElementDeclarationReference();
                            String name = declar.getName();
                            for (XSDElementDeclaration elem : xsdDeclarations) {
                                if (name.equals(elem.getName())) {
                                    if (isUnderSameComplexType(declar, isReference, elem)) {
                                        if (isReference) {
                                            node = declarMap.get(elem).getElement();
                                        } else {
                                            node = elem.getElement();
                                        }

                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    node = ((XSDComponent) object).getElement();
                }
            } else if (object instanceof RedefineCategoryAdapter) {
                RedefineCategoryAdapter category = (RedefineCategoryAdapter) object;
                node = category.getXSDRedefine().getElement();
            } else if (object instanceof CategoryAdapter) {
                node = ((CategoryAdapter) object).getXSDSchema().getElement();
            } else if (object instanceof XSDBaseAdapter) {
                if (((XSDBaseAdapter) object).getTarget() instanceof XSDConcreteComponent) {
                    node = ((XSDConcreteComponent) ((XSDBaseAdapter) object).getTarget()).getElement();
                }
            } else if (object instanceof String) {
                // This case was added to make the F3/hyperlink navigation work
                // when an
                // inline schema from a WSDL document is opened in the schema
                // editor.
                // The string is expected to be a URI fragment used to identify
                // an XSD
                // component in the context of the enclosing WSDL resource.

                String uriFragment = (String) object;
                Resource resource = getXSDSchema().eResource();
                EObject modelObject = resource.getEObject(uriFragment);

                if (modelObject != null && modelObject instanceof XSDComponent) {
                    XSDComponent component = (XSDComponent) modelObject;
                    node = component.getElement();
                }
            }

            // the text editor can only accept sed nodes!
            //
            if (!(node instanceof IDOMNode)) {
                node = null;
            }
            return node;
        }

        private XSDElementDeclaration[] getAllMarkableDeclarationOfParticle() {
            declarMap.clear();
            // find all entities and record their name
            List<String> concepts = new ArrayList<String>();
            SchemaTreeContentProvider schemaContentProvider = (SchemaTreeContentProvider) getDataModelEditorPage()
                    .getSchemaContentProvider();
            Object[] elementDeclarations = schemaContentProvider.getElements(getDataModelEditorPage().getXSDSchema());
            for (Object dec : elementDeclarations) {
                if (dec instanceof XSDElementDeclaration) {
                    concepts.add(((XSDElementDeclaration) dec).getName());
                }
            }

            Set<XSDTypeDefinition> visited = new HashSet<XSDTypeDefinition>();

            // add from entity
            List<XSDElementDeclaration> particles = new ArrayList<XSDElementDeclaration>();
            EList<XSDElementDeclaration> declarations = getXSDSchema().getElementDeclarations();
            for (XSDElementDeclaration dec : declarations) {
                if (concepts.contains(dec.getName())) {
                    XSDTypeDefinition typeDefinition = dec.getTypeDefinition();
                    addChildParticles(particles, typeDefinition, visited);
                }
            }

            // add from complex type
            EList<XSDTypeDefinition> typeDefinitions = getXSDSchema().getTypeDefinitions();
            for (XSDTypeDefinition definition : typeDefinitions) {
                if ((definition instanceof XSDComplexTypeDefinition) && !visited.contains(definition)) {
                    addChildParticles(particles, definition, visited);
                }
            }

            return particles.toArray(new XSDElementDeclaration[0]);
        }

        private void addChildParticles(List<XSDElementDeclaration> particles, XSDTypeDefinition typeDefinition,
                Set<XSDTypeDefinition> visited) {
            if (typeDefinition instanceof XSDComplexTypeDefinition && !visited.contains(typeDefinition)) {
                visited.add(typeDefinition);

                XSDComplexTypeDefinition complexDefinition = (XSDComplexTypeDefinition) typeDefinition;

                XSDParticle particle = (XSDParticle) complexDefinition.getContent();
                if (particle != null) {
                    XSDModelGroup content = (XSDModelGroup) particle.getTerm();
                    EList<XSDParticle> contents = content.getContents();

                    for (XSDParticle xsdParticle : contents) {
                        if (xsdParticle.getTerm() instanceof XSDElementDeclaration) {
                            XSDElementDeclaration contt = (XSDElementDeclaration) xsdParticle.getContent();
                            boolean isDeclarRef = contt.isElementDeclarationReference();
                            if (isDeclarRef) {
                                declarMap.put((XSDElementDeclaration) xsdParticle.getTerm(), contt);
                            }

                            particles.add((XSDElementDeclaration) xsdParticle.getTerm());
                            addChildParticles(particles, ((XSDElementDeclaration) xsdParticle.getTerm()).getTypeDefinition(),
                                    visited);
                        }
                    }
                }
            }
        }

        private boolean isUnderSameComplexType(XSDElementDeclaration elemOnTree, boolean isElemOnTreeBeReferenceType,
                XSDElementDeclaration elemInSource) {
            try {
                if (isElemOnTreeBeReferenceType) {
                    boolean isDeclarationReference = declarMap.get(elemInSource).isElementDeclarationReference();
                    if (isDeclarationReference && (elemOnTree.getContainer() instanceof XSDSchema)
                            && (elemInSource.getContainer() instanceof XSDSchema)) {
                        return true;
                    }

                    return false;
                } else {
                    XSDComplexTypeDefinition selContainer = (XSDComplexTypeDefinition) elemOnTree.getContainer().getContainer()
                            .getContainer().getContainer();
                    XSDComplexTypeDefinition tarContainer = (XSDComplexTypeDefinition) elemInSource.getContainer().getContainer()
                            .getContainer().getContainer();
                    if (selContainer.getContainer().getClass().equals(tarContainer.getContainer().getClass())) {
                        if (selContainer.getContainer() instanceof XSDSchema) {
                            if (selContainer.getName().equals(tarContainer.getName())) {
                                return true;
                            }
                        } else if (selContainer.getContainer() instanceof XSDElementDeclaration) {
                            XSDElementDeclaration selDec = (XSDElementDeclaration) selContainer.getContainer();
                            XSDElementDeclaration tarDec = (XSDElementDeclaration) tarContainer.getContainer();
                            if (selDec.getName().equals(tarDec.getName())) {
                                return true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }

            return false;
        }

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            // do not fire selection in source editor if the current active page
            // is the InternalXSDMultiPageEditor
            // (source)
            // We only want to make source selections if the active page is
            // either the outline or properties (a modify
            // has been done via the outline or properties and not the source
            // view). We don't want to be selecting
            // and unselecting things in the source when editing in the source!!
            boolean isSourcePage = false;
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window != null && window.getActivePage() != null) {
                IWorkbenchPage page = window.getActivePage();
                if (page.getActivePart() instanceof XSDEditor) {
                    if (getActiveEditor() instanceof StructuredTextEditor) {
                        isSourcePage = true;
                    }
                } else {
                    return;
                }

            }

            // do not fire selection in source editor if selection event came

            if (isSourcePage) {
                ISelection selection = getDataModelEditorPage().getSelectionProvider().getSelection();
                if (selection instanceof IStructuredSelection) {
                    List otherModelObjectList = new ArrayList();
                    for (Iterator i = ((IStructuredSelection) selection).iterator(); i.hasNext();) {
                        Object modelObject = i.next();
                        Object otherModelObject = getObjectForOtherModel(modelObject);
                        if (otherModelObject != null) {
                            otherModelObjectList.add(otherModelObject);
                        }
                    }
                    if (!otherModelObjectList.isEmpty()) {
                        StructuredSelection nodeSelection = new StructuredSelection(otherModelObjectList);
                        getTextEditor().getSelectionProvider().setSelection(nodeSelection);
                    }
                }
            }
        }

        public void doSetSelection() {
            ISelection iSelection = getSelectionManager().getSelection();
            if (iSelection != null) {
                Object firstElement = ((IStructuredSelection) iSelection).getFirstElement();
                Object otherModelObject = getObjectForOtherModel(firstElement);
                if (otherModelObject != null) {
                    StructuredSelection nodeSelection = new StructuredSelection(otherModelObject);
                    getTextEditor().getSelectionProvider().setSelection(nodeSelection);
                }
            }
        }
    }

    @Override
    protected void createPages() {
        createSourcePage();
        buildSchema();

        fXSDSelectionListener = new XSDSelectionManagerSelectionListener();
        getSelectionManager().addSelectionChangedListener(fXSDSelectionListener);

        // init content outline
        this.contentOutline = new XSDEditorContentOutline(this);

    }

    public ISelectionProvider getSelectionManager() {
        if (editorSelectionProvider == null) {
            editorSelectionProvider = new MultiPageSelectionProvider(this);
            getSite().setSelectionProvider(editorSelectionProvider);
        }
        return editorSelectionProvider;
    }

    protected void createSourcePage() {
        this.structuredTextEditor = new StructuredTextEditor() {

            @Override
            public boolean isEditable() {
                if (isReadOnly()) {
                    return false;
                }
                return super.isEditable();
            }

        };
        try {
            int index = addPage(this.structuredTextEditor, getEditorInput());
            setPageText(index, org.eclipse.wst.xsd.ui.internal.adt.editor.Messages._UI_LABEL_SOURCE);
            this.structuredTextEditor.update();
            this.structuredTextEditor.setEditorPart(this);
            this.structuredTextEditor.addPropertyListener(this);

            /*
             * **please don't remove this annotation util the problem is really resolve.
             *
             * Here add a property listener to source editor to achieve a effect that when source editor is modified by
             * user, notify the model editor that it should be in dirty state.
             *
             * this is not a perfect resolution of issue TMDM-6403, because we have not find the real reason that lead
             * to that result. the more detailed thing is the variable 'fSynchronizationStamp' in class
             * 'ResourceTextFileBuffer' is updated in some place and this variable finally affect the variable
             * 'fCanBeSaved' (see documentChanged method of the inner class DocumentListener)which indicated the source
             * editor's dirty state, so affect combined editor's dirty state,then affect the combined editor's property
             * listener's execution, then lead to the editor's save and model validation.
             */

            firePropertyChange(1);
        } catch (PartInitException e) {
            ErrorDialog.openError(getSite().getShell(), Messages.XSDEditor_ErrorMessage, null, e.getStatus());
        }
    }

    public void validateXsdSourceEditor() {
        String xsd = getSourcePageDocument();
        Exception ex = null;
        try {
            XSDSchema xsdSchema = Util.createXsdSchema(xsd, xobject);
            String error = validateDiagnoses(xsdSchema);
            if (!"".equals(error)) {
                ex = new IllegalAccessException(error);
            }

        } catch (SAXParseException e) {
            ex = e;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            ex = e;
        }

        if (ex != null) {
            hasXSDErrors = true;

            ErrorExceptionDialog.openError(getSite().getShell(), Messages.XSDEditor_ChangedPageErrorTitle,
                    CommonUtil.getErrMsgFromException(ex));
            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    setActivePage(SOURCE_PAGE_INDEX);
                }
            });

            return;
        }
        hasXSDErrors = false;
        return;
    }

    public String getSourcePageDocument() {
        return getTextEditor().getTextViewer().getDocument().get();
    }

    protected StructuredTextEditor getTextEditor() {
        return structuredTextEditor;
    }

    /**
     * DOC talend-mdm Comment method "validateDiagnoses".
     *
     * @param xsdSchema
     * @return
     */
    private String validateDiagnoses(XSDSchema xsdSchema) {
        xsdSchema.clearDiagnostics();
        xsdSchema.getAllDiagnostics().clear();
        xsdSchema.validate();
        EList<XSDDiagnostic> diagnoses = xsdSchema.getAllDiagnostics();
        String error = "";
        Set<String> errors = new HashSet<String>();
        for (int i = 0; i < diagnoses.size(); i++) {
            XSDDiagnostic dia = diagnoses.get(i);
            XSDDiagnosticSeverity servity = dia.getSeverity();
            if (servity == XSDDiagnosticSeverity.ERROR_LITERAL || servity == XSDDiagnosticSeverity.FATAL_LITERAL) {
                boolean omit = false;
                for (String msg : MSG_OMIT) {
                    if (dia.getMessage().indexOf(msg) != -1) {
                        omit = true;
                        break;
                    }
                }
                if (!omit && !errors.contains(dia.getMessage())) {
                    error += dia.getMessage() + "\n";
                    errors.add(dia.getMessage());
                }
            }
        }
        if (StringUtils.isEmpty(error)) {
            Map<String, String> categoryError = XSDUtil.validateCategory(xsdSchema);
            if (categoryError != null) {
                return categoryError.values().iterator().next();
            }
        }
        return error;
    }

    @Override
    public String getContributorId() {
        int activePageIndex = getActivePage();
        if (activePageIndex == SOURCE_PAGE_INDEX) {
            return "org.eclipse.wst.xsd.ui.internal.editor";
        }
        return CONTRUIBUTIONID_DATAMODELPAGE;

    }

    @Override
    public Object getAdapter(Class type) {
        if (type == IPropertySheetPage.class) {
            int activePageIndex = getActivePage();
            if (activePageIndex == SOURCE_PAGE_INDEX) {
                return new XSDTabbedPropertySheetPage(this);
            }
            if (activePageIndex == MODEL_PAGE_INDEX) {
                return new TabbedPropertySheetPage(this) {

                    @Override
                    public String getTitleText(ISelection selection) {
                        String text = super.getTitleText(selection);
                        text = isReadOnly() ? NLS.bind(Messages.XSDEditor_SheetPageTitle, text) : text;
                        return text;
                    }
                };
            }
        }

        if (type == DataModelMainPage.class) {
            for (int i = 0; i < getPageCount(); i++) {
                if (getEditor(i) instanceof DataModelMainPage) {
                    return getEditor(i);
                }
            }
        }
        if (Boolean.class == type) {
            return new Boolean(isReadOnly());
        }
        // Add this for enable false the properties view while this editor is
        // readOnly.
        if (XSDSchema.class == type && isReadOnly()) {
            return null;
        }
        if (IContentOutlinePage.class == type) {
            return contentOutline;
        }
        return super.getAdapter(type);

    }

    private void refreshPropertyView() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IViewPart propView = page.findView(MDMPerspective.VIEWID_PROPERTYVIEW);
        if (propView != null) {
            EPartService partService = page.getWorkbenchWindow().getService(EPartService.class);

            for (IViewReference viewRef : page.getViewReferences()) {
                if (viewRef.getPart(false) == propView) {
                    MPart mPart = ((WorkbenchPartReference) viewRef).getModel();
                    if (mPart != null) {
                        partService.hidePart(mPart);
                        partService.showPart(mPart, PartState.VISIBLE);
                        break;
                    }
                }
            }
        } else {
            try {
                page.showView(MDMPerspective.VIEWID_PROPERTYVIEW);
            } catch (PartInitException e) {
                LOG.error("Error occurred while showing properties view.", e);
            }
        }
    }

    private void resetTreeSelection(int newPageIndex) {
        DataModelMainPage dataModelEditorPage = getDataModelEditorPage();
        if (dataModelEditorPage != null) {
            TreeViewer treeViewer = dataModelEditorPage.getTreeViewer();
            if (newPageIndex == MODEL_PAGE_INDEX) {
                treeViewer.setSelection(null);
            } else if (newPageIndex == SOURCE_PAGE_INDEX) {
                IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
                if (!selection.isEmpty()) {
                    Object firstElement = selection.getFirstElement();
                    if ((firstElement instanceof XSDIdentityConstraintDefinition)) {
                        XSDIdentityConstraintDefinition cdf = (XSDIdentityConstraintDefinition) firstElement;
                        XSDConcreteComponent container = cdf.getContainer();
                        treeViewer.setSelection(new StructuredSelection(container));
                    } else if ((firstElement instanceof XSDXPathDefinition)) {
                        XSDXPathDefinition pathdef = (XSDXPathDefinition) firstElement;
                        XSDConcreteComponent container = pathdef.getContainer().getContainer();
                        treeViewer.setSelection(new StructuredSelection(container));
                    } else if (firstElement instanceof XSDAnnotation) {
                        XSDAnnotation annotation = (XSDAnnotation) firstElement;
                        XSDConcreteComponent container = annotation.getContainer();
                        treeViewer.setSelection(new StructuredSelection(container));
                    }
                }
            }
        }
    }

    @Override
    public void setFocus() {
        Object selectedPage = getSelectedPage();
        if (selectedPage instanceof IEditorPart) {
            ((IEditorPart) selectedPage).setFocus();
        } else {
            super.setFocus();
        }
    }

    @Override
    public boolean isLocalInput() {
        return false;
    }

    public void setName(String name) {
        setPartName(name);
    }

    @Override
    public void dispose() {
        try {
            if (isDirty()) {
                IFile file = getXSDFile(xobject);
                file.setCharset("utf-8", null);
                file.setContents(new ByteArrayInputStream(fileContents), IFile.FORCE, null);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {

                IEditorInput input = getEditorInput();
                if (input != null && input instanceof XSDEditorInput) {
                    XSDEditorInput editorInput = (XSDEditorInput) input;
                    editorInput.dispose();
                }
                System.gc();
            } catch (Throwable e) {
                LOG.error(e.getMessage(), e);
            }
        }

        super.dispose();
    }

    public DataModelMainPage getdMainPage() {
        return null;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void doSaveAs() {
        // do nothing
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    protected void buildSchema() {
        try {
            IEditorInput editorInput = getEditorInput();

            Document document = null;
            IDocument doc = structuredTextEditor.getDocumentProvider().getDocument(getEditorInput());
            if (doc instanceof IStructuredDocument) {
                IStructuredModel model = null;
                try {
                    // TODO: for StorageEditorInputs, should be forRead
                    model = StructuredModelManager.getModelManager().getExistingModelForEdit(doc);
                    if (model == null) {
                        model = StructuredModelManager.getModelManager().getModelForEdit((IStructuredDocument) doc);
                    }
                    document = ((IDOMModel) model).getDocument();
                } finally {
                    if (model != null) {
                        model.releaseFromEdit();
                    }
                }
            }
            Assert.isNotNull(document);

            xsdSchema = XSDModelAdapter.lookupOrCreateSchema(document);
        } catch (Exception e) {

        }

    }

    public XSDSchema getXSDSchema() {
        return this.xsdSchema;
    }

    @Override
    public void propertyChanged(Object source, int propId) {
        if (source == structuredTextEditor && propId == PROP_DIRTY) {
            boolean dirty = structuredTextEditor.isDirty();
            if (dirty) {
                DataModelMainPage dMainPage = getdMainPage();
                if (dMainPage != null) {
                    dMainPage.setDirty(dirty);
                }
            }
        }

    }

    public void gotoMarker(IMarker marker) {
        setActivePage(SOURCE_PAGE_INDEX);
        IDE.gotoMarker(structuredTextEditor, marker);
    }

}
