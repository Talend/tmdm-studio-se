// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.core.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.core.command.param.DataModelCmdParam;
import org.talend.mdm.repository.core.command.param.ICommandParameter;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.ui.dialogs.impact.ImpactResultDialog;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.utils.HttpClientUtil;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * created by HHB on 2014-3-7 Detailled comment
 * 
 */
public class ModelImpactAnalyseService {

    // SEVERITY
    public static final int LOW = 1, MEDIUM = 2, HIGH = 3;

    public enum ImpactOperation {
        RECREATE_TABLE(Messages.ModelImpactAnalyseService_recreateTable),
        APPLY_LOW_CHANGE(Messages.ModelImpactAnalyseService_applyChange),
        CANCEL(Messages.ModelImpactAnalyseService_cancelDeploying);

        private final String description;

        private ImpactOperation(String description) {
            this.description = description;
        }

        public static ImpactOperation getOperation(int index) {
            EnumSet<ImpactOperation> set = EnumSet.allOf(ImpactOperation.class);
            for (ImpactOperation operation : set) {
                if (operation.ordinal() == index) {
                    return operation;
                }
            }
            return null;
        }

        public String getDescription() {
            return this.description;
        }
    }

    public static class Change {

        private String message;

        private int severity;

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getSeverity() {
            return this.severity;
        }

        public void setSeverity(int severity) {
            this.severity = severity;
        }
    }

    public static class EntitiesToDrop {

        List<String> entities = new ArrayList<String>();

        public List<String> getEntities() {
            return this.entities;
        }

        public void addEntity(String entity) {
            entities.add(entity);
        }

    }

    static class Severity {

        List<Change> changes = new ArrayList<Change>();

        public void addChange(Change change) {
            changes.add(change);
        }

        public List<Change> getChanges() {
            return this.changes;
        }
    }

    static class SeverityLow extends Severity {
    }

    static class SeverityMedium extends Severity {
    }

    static class SeverityHigh extends Severity {
    }

    public static class Result {

        List<Severity> severities = new ArrayList<Severity>();

        EntitiesToDrop entitiesToDrop;

        public EntitiesToDrop getEntitiesToDrop() {
            return this.entitiesToDrop;
        }

        public void setEntitiesToDrop(EntitiesToDrop entitiesToDrop) {
            this.entitiesToDrop = entitiesToDrop;
        }

        public void addSeverity(Severity severity) {
            severities.add(severity);
        }

        public List<Severity> getSeverities() {
            return severities;
        }

        public List<Change> getChanges() {
            List<Change> changes = new ArrayList<ModelImpactAnalyseService.Change>();
            if (getSeverities() != null) {
                for (Severity severity : getSeverities()) {
                    if (severity.getChanges() != null) {
                        for (Change change : severity.getChanges()) {
                            int s = 0;
                            if (severity instanceof SeverityHigh) {
                                s = HIGH;
                            } else if (severity instanceof SeverityMedium) {
                                s = MEDIUM;
                            } else if (severity instanceof SeverityLow) {
                                s = LOW;
                            }
                            change.setSeverity(s);
                            changes.add(change);
                        }
                    }
                }
            }
            return changes;
        }
    }

    private static Logger log = Logger.getLogger(ModelImpactAnalyseService.class);

    private static XStream xstream;

    private static Result analyzeModelChange(MDMServerDef serverDef, IRepositoryViewObject modelViewObj) throws XtentisException {
        String responseMsg = invokeService(serverDef, modelViewObj, false, null);
        if (responseMsg != null) {
            return readResponseMessage(responseMsg);
        }
        return null;
    }

    private static Map<IRepositoryViewObject, Result> analyzeModelChanges(MDMServerDef serverDef,
            List<IRepositoryViewObject> modelViewObjs) throws XtentisException {
        Map<IRepositoryViewObject, Result> result = new HashMap<IRepositoryViewObject, Result>();
        for (IRepositoryViewObject viewObj : modelViewObjs) {
            if (viewObj.getRepositoryObjectType() == IServerObjectRepositoryType.TYPE_DATAMODEL) {
                Result analyzeResult = analyzeModelChange(serverDef, viewObj);
                List<Change> changes = analyzeResult.getChanges();
                if (changes != null && changes.size() > 0) {
                    result.put(viewObj, analyzeResult);
                }
            }
        }
        return result;
    }

    public static Map<IRepositoryViewObject, ImpactOperation> analyzeCommandImpact(MDMServerDef serverDef,
            List<AbstractDeployCommand> commands) throws InterruptedException {
        List<IRepositoryViewObject> viewObjs = new LinkedList<IRepositoryViewObject>();
        for (AbstractDeployCommand cmd : commands) {
            int commandType = cmd.getCommandType();
            if ((commandType == ICommand.CMD_MODIFY || commandType == ICommand.CMD_ADD) && cmd.getViewObject() != null) {
                viewObjs.add(cmd.getViewObject());
            }
        }
        return analyzeModelImpact(serverDef, viewObjs);
    }

    public static Map<IRepositoryViewObject, ImpactOperation> analyzeModelImpact(MDMServerDef serverDef,
            List<IRepositoryViewObject> modelViewObjs) throws InterruptedException {
        try {
            Map<IRepositoryViewObject, Result> changes = analyzeModelChanges(serverDef, modelViewObjs);
            if (!changes.isEmpty()) {
                Shell shell = Display.getDefault().getActiveShell();
                ImpactResultDialog dialog = new ImpactResultDialog(shell, changes);
                if (dialog.open() == IDialogConstants.OK_ID) {
                    Map<IRepositoryViewObject, ImpactOperation> configuration = dialog.getImpactConfiguration();
                    return configuration;
                } else {
                    Map<IRepositoryViewObject, ImpactOperation> configuration = new HashMap<IRepositoryViewObject, ModelImpactAnalyseService.ImpactOperation>(
                            changes.size());
                    for (IRepositoryViewObject viewObj : changes.keySet()) {
                        configuration.put(viewObj, ImpactOperation.CANCEL);
                    }

                    return configuration;
                }
            }
        } catch (XtentisException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static void updateModel(MDMServerDef serverDef, IRepositoryViewObject modelViewObj, Boolean force)
            throws XtentisException {
        invokeService(serverDef, modelViewObj, true, force);
    }

    private static String invokeService(MDMServerDef serverDef, IRepositoryViewObject modelViewObj, boolean isUpdateModel,
            Boolean force) throws XtentisException {
        String xsd = getModelContent(modelViewObj);
        if (xsd != null) {
            String contextPath = Util.getContextPath(serverDef.getPath());
            String result = HttpClientUtil.invokeModelService(serverDef.getProtocol(), serverDef.getHost(), serverDef.getPort(),
                    contextPath, serverDef.getUser(), serverDef.getPasswd(), modelViewObj.getLabel(), xsd, isUpdateModel, force);
            return result;
        }
        return null;
    }

    private static String getModelContent(IRepositoryViewObject viewObj) {
        Item item = viewObj.getProperty().getItem();
        IFile file = RepositoryResourceUtil.findReferenceFile(IServerObjectRepositoryType.TYPE_DATAMODEL, item, "xsd"); //$NON-NLS-1$

        try {
            byte[] bytes = IOUtils.toByteArray(file.getContents());

            String xsd = new String(bytes);
            return xsd;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (CoreException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private static XStream getParser() {
        if (xstream == null) {
            xstream = new XStream(new DomDriver());
            xstream.alias("result", Result.class); //$NON-NLS-1$
            xstream.alias("change", Change.class); //$NON-NLS-1$
            xstream.alias("medium", SeverityMedium.class); //$NON-NLS-1$
            xstream.alias("low", SeverityLow.class); //$NON-NLS-1$
            xstream.alias("high", SeverityHigh.class); //$NON-NLS-1$
            xstream.alias("entitiesToDrop", EntitiesToDrop.class); //$NON-NLS-1$
            xstream.alias("entity", String.class); //$NON-NLS-1$

            xstream.addImplicitCollection(Result.class, "severities"); //$NON-NLS-1$
            xstream.addImplicitCollection(Severity.class, "changes"); //$NON-NLS-1$
            xstream.addImplicitCollection(EntitiesToDrop.class, "entities"); //$NON-NLS-1$
        }
        return xstream;
    }

    public static Result readResponseMessage(String message) {
        if (message != null) {
            if (message.trim().isEmpty()) {
                // first time deploy
                return null;
            }
            Thread cur = Thread.currentThread();
            ClassLoader save = cur.getContextClassLoader();
            cur.setContextClassLoader(RepositoryPlugin.getDefault().getClass().getClassLoader());
            try {
                Result result = (Result) getParser().fromXML(message);
                return result;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            } finally {
                cur.setContextClassLoader(save);

            }
        }
        return null;
    }

    public static void shrinkDeployCommands(Map<IRepositoryViewObject, ImpactOperation> impactResult,
            List<AbstractDeployCommand> commands) {
        Set<IRepositoryViewObject> toRemoveKeys = new HashSet<IRepositoryViewObject>();
        Iterator<AbstractDeployCommand> il = commands.iterator();
        while (il.hasNext()) {
            AbstractDeployCommand cmd = il.next();
            IRepositoryViewObject viewObject = cmd.getViewObject();
            if (viewObject != null) {
                ERepositoryObjectType type = viewObject.getRepositoryObjectType();
                if (type == IServerObjectRepositoryType.TYPE_DATAMODEL) {
                    ImpactOperation operation = impactResult.get(viewObject);
                    if (operation == ImpactOperation.CANCEL) {
                        il.remove();
                        toRemoveKeys.add(viewObject);
                    }
                } else if (type == IServerObjectRepositoryType.TYPE_MATCH_RULE_MAPINFO) {
                    String label = viewObject.getLabel();
                    for (IRepositoryViewObject modelViewObj : impactResult.keySet()) {
                        if (modelViewObj.getLabel().equals(label)) {
                            ImpactOperation operation = impactResult.get(modelViewObj);
                            if (operation == ImpactOperation.CANCEL) {
                                il.remove();
                            }
                        }
                    }
                }
            }
        }
        for (IRepositoryViewObject key : toRemoveKeys) {
            impactResult.remove(key);
        }
    }

    public static Map<IRepositoryViewObject, ICommandParameter> convertToParameters(
            Map<IRepositoryViewObject, ImpactOperation> impactResult) {
        Map<IRepositoryViewObject, ICommandParameter> paramMap = new HashMap<IRepositoryViewObject, ICommandParameter>();
        for (IRepositoryViewObject viewObj : impactResult.keySet()) {
            ImpactOperation impactOperation = impactResult.get(viewObj);
            ICommandParameter param = new DataModelCmdParam(impactOperation);
            paramMap.put(viewObj, param);
        }
        return paramMap;
    }
}
