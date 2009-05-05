/*
 * Created on 27 oct. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.amalto.workbench.editors;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.amalto.workbench.actions.SaveXObjectAction;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.IConstants;

public class XObjectEditor extends FormEditor implements IXObjectModelListener{	
	
	private ArrayList<IFormPage> formPages = new ArrayList<IFormPage>();
	private TreeObject initialXObject = null;  //backup
	
	protected boolean saveInProgress = false;
	
	/*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    protected void addPages() {
        try {
        	updateTitle();
        	
        	TreeObject xobject = (TreeObject)((XObjectEditorInput)this.getEditorInput()).getModel();
        	
        	//backup initial object
        	this.initialXObject = new TreeObject(
        			xobject.getDisplayName(),
        			xobject.getServerRoot(),
        			xobject.getType(),
        			xobject.getWsKey(),
        			xobject.getWsObject(),
        			xobject.getAdditionalInfo()
        	);
        	
        	if (!xobject.isXObject()) return;
        	
        	//register model listener
        	xobject.addListener(this);
        	
            switch(xobject.getType()) {
	           	case TreeObject.DATA_MODEL:
	           		addPage(new DataModelMainPage(this));
                    addPage(new DataModelEditorPage(this));
                    //addPage(new DataModelEditorPage2());
	           		break;
	           	case TreeObject.INBOUND_PLUGIN:
	           		break;
	           	case TreeObject.OUTBOUND_PLUGIN:
	           		break;
	           	case TreeObject.VIEW:
	           		addPage(new  ViewMainPage(this));
	           		break;
	           	case TreeObject.DATA_CLUSTER:
                    addPage(new DataClusterMainPage(this));
	           		break;
	           	case TreeObject.STORED_PROCEDURE:
                    addPage(new StoredProcedureMainPage(this));
	           		break;	
	           	case TreeObject.ROLE:
                    addPage(new RoleMainPage(this));
	           		break;		           		
	           	case TreeObject.ROUTING_RULE:
                    addPage(new RoutingRuleMainPage(this));
	           		break;
	           	case TreeObject.TRANSFORMER:
                    addPage(new TransformerMainPage(this));
	           		break;	
	           	case TreeObject.MENU:
                    addPage(new MenuMainPage(this));
	           		break;
	           	case TreeObject.UNIVERSE:
	           		addPage(new UniverseMainPage(this));	           		
	           		break;
	           	case TreeObject.SYNCHRONIZATIONPLAN:	           		
	           		addPage(new SynchronizationMainPage(this));
	           		break;
	           	default:
	           		MessageDialog.openError(this.getSite().getShell(), "Error", "Unknown "+IConstants.TALEND+" Object Type: "+xobject.getType());
	           		return;
            }//switch
        } catch (PartInitException e) {
            MessageDialog.openError(this.getSite().getShell(), "Error", "Unable to open the editor :"+e.getLocalizedMessage());
        }
    }
    
    
    //Overloaded - Fixes issues with getEditor()
    public int addPage(IFormPage page) throws PartInitException {
		formPages.add(page);
		return super.addPage(page);
	}

	/*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave(IProgressMonitor monitor) {

    	this.saveInProgress = true;
    	
    	int numPages = formPages.size();
    	monitor.beginTask("Saving "+this.getEditorInput().getName(), numPages+1);
    	
    	for (int i = 0; i < numPages; i++) {
    		(formPages.get(i)).doSave(monitor);
    		monitor.worked(1);
    		if (monitor.isCanceled()) {
    			this.saveInProgress = false;
    			return;
    		}
		}
    	
    	//perform the actual save
    	SaveXObjectAction action = new SaveXObjectAction(this);
    	action.run();
    	monitor.done();
    	
    	this.saveInProgress = false;
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed() {
        return false;
    }
	public void doSaveAs() {
	}

    
	
	
	private void updateTitle() {
    	IEditorInput input = this.getEditorInput();
    	setPartName(input.getName());
    	setContentDescription("");
    }


	public void dispose() {
		//save space
		TreeObject xobject = (TreeObject)((XObjectEditorInput)this.getEditorInput()).getModel(); 
		if (xobject!=null) { 
				xobject.setWsObject(null);
				xobject.removeListener(this);
		}
		super.dispose();
	}

	/**
	 * Model Listener
	 */
	public void handleEvent(int type, TreeObject parent, TreeObject child) {
		TreeObject xobject = (TreeObject)((XObjectEditorInput)this.getEditorInput()).getModel();
		switch(type) {
			case IXObjectModelListener.DELETE:
				if (xobject.equals(child)) this.close(false);
				break;
			case IXObjectModelListener.SAVE:
				if (saveInProgress)
					this.editorDirtyStateChanged();
				else
					/*
					MessageDialog.openWarning(
						this.getSite().getShell(),
						"Warning underlying data changed", 
						"The current displayed data may not be in sync with the data persisted on the server."
					);
					*/
				break;
			case IXObjectModelListener.UPDATE:
				if (xobject.equals(child)) {
					((AFormPage)getActivePageInstance()).refreshPage();
					/*
			    	for (int i = formPages.size()-1; i >=0 ; i--) {
			    		((AFormPage) formPages.get(i)).refreshPage();
					}
					*/
				}
			default:
		}
	}


	public TreeObject getInitialXObject() {
		return initialXObject;
	}

    
    protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);
        AFormPage page = (AFormPage)formPages.get(newPageIndex);
        page.refreshPage();
    }

    @Override
    public Image getTitleImage() {
    	TreeObject object = (TreeObject)((XObjectEditorInput)this.getEditorInput()).getModel();
    	
		if (object.getType() == TreeObject._SERVER_)
			return AbstractUIPlugin.imageDescriptorFromPlugin("com.amalto.workbench", "icons/talend-picto-small.gif").createImage();
		else if (object.getType() == TreeObject.DATA_CLUSTER)
			return AbstractUIPlugin.imageDescriptorFromPlugin("com.amalto.workbench", "icons/data_cluster.gif").createImage();
		else if (object.getType() == TreeObject.DATA_MODEL)
			return AbstractUIPlugin.imageDescriptorFromPlugin("com.amalto.workbench", "icons/data_model.gif").createImage();
		else if (object.getType() == TreeObject.MENU)
			return AbstractUIPlugin.imageDescriptorFromPlugin("com.amalto.workbench", "icons/menu.gif").createImage();
		else if (object.getType() == TreeObject.TRANSFORMER)
			return AbstractUIPlugin.imageDescriptorFromPlugin("com.amalto.workbench", "icons/transformer.gif").createImage();
		else if (object.getType() == TreeObject.ROLE)
			return AbstractUIPlugin.imageDescriptorFromPlugin("com.amalto.workbench", "icons/role.gif").createImage();
		else if (object.getType() == TreeObject.STORED_PROCEDURE)
			return AbstractUIPlugin.imageDescriptorFromPlugin("com.amalto.workbench", "icons/stored_procedure.gif").createImage();
		else if (object.getType() == TreeObject.ROUTING_RULE)
			return AbstractUIPlugin.imageDescriptorFromPlugin("com.amalto.workbench", "icons/routing_rule.gif").createImage();
		else if (object.getType() == TreeObject.VIEW)
			return AbstractUIPlugin.imageDescriptorFromPlugin("com.amalto.workbench", "icons/view.gif").createImage();
		else if (object.getType() == TreeObject.DOCUMENT)
			return AbstractUIPlugin.imageDescriptorFromPlugin("com.amalto.workbench", "icons/documents.gif").createImage();			
		else if (object.getType() == TreeObject.SUBSCRIPTION_ENGINE)
			return AbstractUIPlugin.imageDescriptorFromPlugin("com.amalto.workbench", "icons/routing_rule.gif").createImage();
		else if (object.getType() == TreeObject.SYNCHRONIZATIONPLAN)
			return AbstractUIPlugin.imageDescriptorFromPlugin("com.amalto.workbench", "icons/catchuprelease_rls.gif").createImage();
		else if (object.getType() == TreeObject.UNIVERSE)
			return AbstractUIPlugin.imageDescriptorFromPlugin("com.amalto.workbench", "icons/unique.gif").createImage();
		
		return AbstractUIPlugin.imageDescriptorFromPlugin("com.amalto.workbench", "icons/error.gif").createImage();
    }
    
}
