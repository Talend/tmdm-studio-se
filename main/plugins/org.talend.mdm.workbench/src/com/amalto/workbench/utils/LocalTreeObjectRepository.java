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
package com.amalto.workbench.utils;

import java.io.File;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.widgets.TreeItem;
import org.talend.mdm.commmon.util.core.ICoreConstants;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSCategoryData;

public class LocalTreeObjectRepository implements IXObjectModelListener, ITreeViewerListener {

    private static Log log = LogFactory.getLog(LocalTreeObjectRepository.class);

    private boolean internalCheck = false;

    private boolean synchronize = false;

    private ArrayList<String> accommodations = new ArrayList<String>();

    private HashMap<String, Element> outPutSchemas = new HashMap<String, Element>();

    private static String config = System.getProperty("user.dir") + "/.treeObjectConfig.xml";//$NON-NLS-1$//$NON-NLS-2$

    private static String rootPath = "/" + ICoreConstants.DEFAULT_CATEGORY_ROOT;//$NON-NLS-1$

    private static String DEFAULT_CATALOG = "System";//$NON-NLS-1$

    private static String EXPAND_NAME = "Expand";//$NON-NLS-1$

    private static String EXPAND_VALUE = "true";//$NON-NLS-1$

    private static String COLLAPSE_VALUE = "false";//$NON-NLS-1$

    private static String URL = "Url";//$NON-NLS-1$

    private static String REALNAME = "Name";//$NON-NLS-1$

    private static int XTENTIS_LEVEL = 4;

    private static int MODEL_LEVEL = 3;

    private Element catalogTreeObj = null;

    private TreeItem itemFocus = null;

    private Document spareDoc = null;

    private TreeObject[] originalImportXobjects = null;

    private String[] importCategories = null;

    private boolean lazySave = true;

    private static HashMap<String, Credential> credentials = new HashMap<String, Credential>();

    private static LocalTreeObjectRepository repository = null;

    synchronized public static LocalTreeObjectRepository getInstance() {
        if (repository == null) {
            repository = new LocalTreeObjectRepository();
        }

        return repository;
    }

    private class Credential {

        public String user;

        public String pwd;

        public Document doc;

        public TMDMService service;

        public boolean state;

        public Credential(String user, String pwd, Document doc) {
            this.user = user;
            this.pwd = pwd;
            this.doc = doc;
        }
    }

    public void startUp(String ur, String user, String pwd) {
        TMDMService service = null;
        Document doc = null;
        SAXReader saxReader = new SAXReader();

        try {
            service = Util.getMDMService(new URL(ur), user, pwd);
            WSCategoryData category = service.getMDMCategory(null);
            doc = saxReader.read(new StringReader(category.getCategorySchema()));
            saveCredential(ur, user, pwd, doc, service, true);
            doUpgrade(UnifyUrl(ur));
        } catch (Exception e) {

            log.error(e.getMessage(), e);
            String empty = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";//$NON-NLS-1$
            empty += "<" + ICoreConstants.DEFAULT_CATEGORY_ROOT + "/>";//$NON-NLS-1$//$NON-NLS-2$
            WSCategoryData newData = new WSCategoryData();
            newData.setCategorySchema(empty);
            try {
                newData = service.getMDMCategory(newData);
                doc = saxReader.read(new StringReader(newData.getCategorySchema()));
                saveCredential(ur, user, pwd, doc, service, true);
            } catch (Exception e1) {
                saveCredential(ur, user, pwd, doc, service, false);
            }

        }
    }

    private void saveCredential(String ur, String user, String pwd, Document doc, TMDMService service, boolean stat) {
        Credential credal = credentials.get(UnifyUrl(ur));
        if (credal == null) {
            credal = new Credential(user, pwd, doc);
        }

        credal.service = service;
        if (credal.doc == null) {
            credal.doc = doc;
        }
        credal.state = stat;
        credentials.put(UnifyUrl(ur), credal);
    }

    private void doUpgrade(String url) {
        Document doc = credentials.get(url).doc;
        String path = "//child::*[text() = '" + TreeObject.CATEGORY_FOLDER + "' and count(@Universe) = 0 and count(@Url) = 0"//$NON-NLS-1$//$NON-NLS-2$
                + "]";//$NON-NLS-1$
        List<Element> categorys = doc.selectNodes(path);

        for (Element elem : categorys) {
            Attribute attr = elem.attribute(URL);
            if (attr == null) {
                elem.addAttribute(URL, UnifyUrl(url));
            }
        }
        saveDocument(url);
    }

    private boolean forceDelete() {
        File configFile = new File(config);
        boolean result = false;
        int tryCount = 0;
        while (!result && tryCount++ < 10) {
            System.gc();
            result = configFile.delete();
        }

        if (result) {
            MessageDialog.openWarning(null, Messages.LocalTreeObjectRepository_ErrorTitle,
                    Messages.LocalTreeObjectRepository_ErrorMsg);
        }

        return result;
    }

    private String getXPathForElem(Element elem) {
        List<String> xpathList = new ArrayList<String>();
        recurseElementForXPath(elem, xpathList);

        String xpathVar = "";//$NON-NLS-1$
        for (int idx = xpathList.size() - 1; idx >= 0; idx--) {
            xpathVar += "/" + xpathList.get(idx);//$NON-NLS-1$
        }

        return xpathVar;
    }

    private void recurseElementForXPath(Element elem, List<String> xpathList) {
        if (elem != null) {
            xpathList.add(elem.getName());
            recurseElementForXPath(elem.getParent(), xpathList);
        }

    }

    public void handleEvent(int type, TreeObject parent, TreeObject child) {
        if (internalCheck || child.getServerRoot() == null) {
            return;
        }
        String url = UnifyUrl(child.getServerRoot().getWsKey().toString());
        Credential cre = credentials.get(url);
        if (cre != null) {
            if (cre.state == false) {
                return;
            }
        }

        try {
            switch (type) {
            case IXObjectModelListener.ADD:
                addChild(parent, child);
                break;
            case IXObjectModelListener.DELETE:
                removeChild(parent, child);
                break;
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private Element getTopElementWithUser(String user, String ip) {
        Document doc = credentials.get(ip).doc;
        List<Element> userRoots = doc.selectNodes(rootPath + "/" + user);//$NON-NLS-1$
        Element elementUser;
        if (userRoots.isEmpty()) {
            elementUser = doc.getRootElement().addElement(user);
            elementUser.setText("0");//$NON-NLS-1$
        } else {
            elementUser = userRoots.get(0);
        }

        return elementUser;
    }

    private void saveDocument(TreeObject parent) {
        if (lazySave || parent == null || parent.getServerRoot() == null) {
            return;
        }

        String url = UnifyUrl(parent.getServerRoot().getWsKey().toString());
        saveDocument(url);
    }

    private void saveDocument(String url) {
        if (credentials.get(url) != null) {
            TMDMService service = credentials.get(url).service;
            Document doc = credentials.get(url).doc;
            if (doc != null) {
                service.getMDMCategory(new WSCategoryData(doc.asXML()));
            }
        }
    }

    private String convertSpecCharToDigital(String res) {
        String cpy = "", trail = ".";//$NON-NLS-1$//$NON-NLS-2$
        Pattern mask = Pattern.compile("[\\W]+");//$NON-NLS-1$
        for (int id = 0; id < res.length(); id++) {
            String slip = "" + res.charAt(id);//$NON-NLS-1$
            Matcher matcher = mask.matcher(slip);
            if (matcher.find()) {
                cpy += "" + (int) res.charAt(id);//$NON-NLS-1$
                trail += (int) res.charAt(id);
            } else {
                cpy += slip;
            }
        }

        mask = Pattern.compile("[\\d]+");//$NON-NLS-1$
        String chead = "" + cpy.charAt(0);//$NON-NLS-1$
        Matcher match = mask.matcher(chead);
        if (match.find()) {
            int var = 'a' + Integer.parseInt(chead);
            chead = (char) var + "";//$NON-NLS-1$
            if (cpy.length() > 1) {
                cpy = chead + cpy.substring(1, cpy.length());
            } else {
                cpy = chead;
            }
            cpy += chead;
        }
        if (!res.equals(cpy)) {
            cpy += "qwer1ty2ui4o";//$NON-NLS-1$
        }
        return cpy + (trail.length() > 1 ? trail : "");//$NON-NLS-1$
    }

    private String filterOutBlank(String input) {
        Pattern mask = Pattern.compile("([\\s]+)");//$NON-NLS-1$
        String res = mask.matcher(input).replaceAll("");//$NON-NLS-1$
        res = res.replaceAll("\\[\\w+\\]", "");//$NON-NLS-1$//$NON-NLS-2$

        return convertSpecCharToDigital(res);
    }

    public Element getParentElement(TreeObject treeObj) {
        if (!(treeObj instanceof TreeParent)) {
            return null;
        }
        if (treeObj.getServerRoot() == null) {
            return null;
        }
        Element elemFolder = null;
        String xpath = getXPathForTreeObject(treeObj);
        if (credentials.get(UnifyUrl(treeObj.getServerRoot().getWsKey().toString())) == null) {
            return null;
        }
        Document doc = credentials.get(UnifyUrl(treeObj.getServerRoot().getWsKey().toString())).doc;
        if (doc.selectNodes(xpath).isEmpty()) {
            xpath = xpath.replaceAll("\\[.*\\]", "");//$NON-NLS-1$//$NON-NLS-2$
            if (doc.selectNodes(xpath).isEmpty()) {
                xpath = getXPathForTreeObject(treeObj.getParent() != null ? treeObj.getParent() : treeObj.getServerRoot());
            }
            if (xpath != null) {
                Element elemTop = null;
                if (doc.selectNodes(xpath).isEmpty()) {
                    elemTop = getTopElementWithUser(treeObj.getServerRoot().getUser().getUsername(), UnifyUrl(treeObj
                            .getServerRoot().getWsKey().toString()));
                } else {
                    // process and trigger is in special, as they are located in EventManagement which is in the same
                    // level as data container and data model
                    if (treeObj.getType() == TreeObject.TRANSFORMER && treeObj.getParent() == null) {
                        xpath += "/EventManagement[text() = '33']";//$NON-NLS-1$
                    }
                    elemTop = (Element) doc.selectNodes(xpath).get(0);
                }
                String selPath = "./" + filterOutBlank(treeObj.getDisplayName()) + "[text() = '" + treeObj.getType() + "']";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                if (elemTop.selectNodes(selPath).isEmpty()) {
                    elemFolder = elemTop.addElement(filterOutBlank(treeObj.getDisplayName()));
                    elemFolder.setText(treeObj.getType() + "");//$NON-NLS-1$
                } else {
                    elemFolder = (Element) elemTop.selectNodes(selPath).get(0);
                }
            }
        } else {
            elemFolder = (Element) doc.selectNodes(xpath).get(0);
        }

        return elemFolder;
    }

    private void addChild(TreeObject parent, TreeObject child) {
        if (parent.getParent() == null && parent.getDisplayName().equals("INVISIBLE ROOT")) {
            return;
        }
        if (parent.getServerRoot() == null) {
            return;
        }
        String xpath = getXPathForTreeObject(child);
        Document doc = credentials.get(UnifyUrl(parent.getServerRoot().getWsKey().toString())).doc;
        List<Element> models = doc.selectNodes(xpath);
        if (!models.isEmpty() && child instanceof TreeParent) {
            Element model = models.get(0);
            if (isAEXtentisObjects(model, child) == MODEL_LEVEL) {
                checkUpAllCategoryForModel((TreeParent) child);
            }
        }

        String catalog = synchronizeWithElem(child, (TreeParent) parent, true);
        Element elemFolder = getParentElement(parent);
        if (elemFolder != null) {
            String xpathTail = "";//$NON-NLS-1$
            String xpathTailOther = "']";//$NON-NLS-1$
            xpath = "child::*[name()='" + filterOutBlank(child.getDisplayName()) + "' and text()='" + child.getType();//$NON-NLS-1$//$NON-NLS-2$
            if (child.getType() == TreeObject.CATEGORY_FOLDER) {
                xpathTail = "' and @Url='" + getURLFromTreeObject(child);//$NON-NLS-1$
            }
            List<Element> list = elemFolder.selectNodes(xpath + xpathTail + xpathTailOther);
            if (list.isEmpty() && (catalog.equals("") || catalog == null)) {//$NON-NLS-1$
                Element childElem = elemFolder.addElement(filterOutBlank(child.getDisplayName()));
                childElem.setText(child.getType() + "");//$NON-NLS-1$
                if (child.getType() == TreeObject.CATEGORY_FOLDER) {
                    childElem.addAttribute(URL, getURLFromTreeObject(child));
                    childElem.addAttribute(REALNAME, child.getDisplayName());
                }
            }
        }
        saveDocument(parent);
    }

    private void removeChild(TreeObject parent, TreeObject child) {
        if (parent.getParent() == null && parent.getDisplayName().equals("INVISIBLE ROOT")) {
            return;
        }

        if (synchronize) {
            synchronizeWithElem(child, (TreeParent) parent, true);
        }

        Element elemFolder = getParentElement(parent);
        String xpath = "child::*[name()='" + filterOutBlank(child.getDisplayName()) + "' and text()='" + child.getType() + "']";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        if (elemFolder == null) {
            return;
        }
        List<Element> list = elemFolder.selectNodes(xpath);
        if (!list.isEmpty()) {
            elemFolder.remove(list.get(0));
        }

        saveDocument(parent);
    }

    public void correctDisplayNameForCategory(TreeObject category) {
        if (category.getType() != TreeObject.CATEGORY_FOLDER) {
            return;
        }
        if (category.getServerRoot() == null) {
            return;
        }
        String xpath = getXPathForTreeObject(category);
        Document doc = credentials.get(UnifyUrl(category.getServerRoot().getWsKey().toString())).doc;
        List<Element> elems = doc.selectNodes(xpath);
        if (elems.size() > 0) {
            Element elem = elems.get(0);
            String value = elem.attributeValue(REALNAME);
            if (value != null) {
                category.setDisplayName(value);
            }
        }
    }

    public void synchronizeWithDoc(TreeParent root) {
        internalCheck = false;
        TreeObject[] targetChildren = root.getChildren();
        for (TreeObject treeObj : targetChildren) {
            if (treeObj instanceof TreeParent && treeObj.getServerRoot() == treeObj) {
                synchronizeWithDoc((TreeParent) treeObj);
            }

            synchronizeWithElem(treeObj, root, true);
        }
    }

    private String getXPathForTreeObject(TreeObject theObj) {
        List<String> xpathList = new ArrayList<String>();
        recurseTreeObjectForXPath(theObj, xpathList);

        String xpathVar = "";//$NON-NLS-1$
        for (int idx = xpathList.size() - 1; idx >= 0; idx--) {
            xpathVar += "/" + xpathList.get(idx);//$NON-NLS-1$
        }

        String xpathTail = "[text() = '" + theObj.getType() + "']";//$NON-NLS-1$//$NON-NLS-2$

        if (theObj.getType() == TreeObject.CATEGORY_FOLDER) {
            xpathTail = "[text() = '" + theObj.getType() + "' and @Url='" + getURLFromTreeObject(theObj) + "']";//$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        }

        return xpathVar + xpathTail;
    }

    private void recurseTreeObjectForXPath(TreeObject theObj, List<String> list) {
        if (theObj.getParent() == null && theObj.getDisplayName().equals("INVISIBLE ROOT")) {//$NON-NLS-1$
            list.add(rootPath);
            return;
        } else if (theObj instanceof TreeParent && theObj.getServerRoot() == theObj) {
            list.add(theObj.getUser().getUsername());
            list.add(ICoreConstants.DEFAULT_CATEGORY_ROOT);
            return;
        } else {
            list.add(filterOutBlank(theObj.getDisplayName()));
        }

        recurseTreeObjectForXPath(theObj.getParent() != null ? theObj.getParent() : theObj.getServerRoot(), list);
    }

    private String getURLFromTreeObject(TreeObject theObj) {
        String url = theObj.getServerRoot().getUser().getServerUrl();
        // http://localhost:8080/talend/TalendPort
        return UnifyUrl(url);
    }

    private String UnifyUrl(String url) {
        Pattern mask = Pattern.compile("http://(.*?):(.*)");//$NON-NLS-1$
        Matcher matcher = mask.matcher(url);
        if (matcher.find()) {
            String ip = matcher.group(1);
            String ipAlias = "";//$NON-NLS-1$
            String hostName = "";//$NON-NLS-1$
            try {
                InetAddress addr = InetAddress.getLocalHost();
                ipAlias = addr.getHostAddress().toString();
                hostName = addr.getHostName().toString();
                if (ip.equals("127.0.0.1") || ip.equals(ipAlias) || ip.equals(hostName) || ip.equals("localhost")) {//$NON-NLS-1$//$NON-NLS-2$
                    url = "http://" + ipAlias + ":" + matcher.group(2);//$NON-NLS-1$//$NON-NLS-2$
                }
            } catch (UnknownHostException e) {
                log.error(e.getMessage(), e);
            }
        }
        return url;
    }

    private String synchronizeWithElem(TreeObject theObj, TreeParent folder, boolean fireEvent) {
        internalCheck = fireEvent;
        String catalogPath = "";//$NON-NLS-1$
        ArrayList<String> catalogs = checkUpCatalogRepositoryForTreeObject(theObj, folder);
        if (catalogs != null && folder.getType() != TreeObject.CATEGORY_FOLDER) {
            // create a catalog folder and insert the theObj into it
            TreeParent subFolder = folder;
            TreeParent category = null;
            for (String catalogName : catalogs) {
                // if (catalogName.equals(filterOutBlank(folder.getDisplayName())))
                // continue;
                category = null;
                for (TreeObject child : subFolder.getChildren()) {
                    if (child.getDisplayName().equals(catalogName) && child.getType() == TreeObject.CATEGORY_FOLDER
                            && child instanceof TreeParent) {
                        category = (TreeParent) child;
                        subFolder = category;
                        break;
                    }
                }

                if (category == null) {
                    category = new TreeParent(catalogName, folder.getServerRoot(), TreeObject.CATEGORY_FOLDER, null, null);
                    subFolder.addChild(category);
                    category.setServerRoot(folder.getServerRoot());
                    addAttributeToCategoryElem(category, URL, getURLFromTreeObject(folder));
                    saveDocument(folder);
                    subFolder = category;
                }
            }

            boolean exist = false;
            for (TreeObject obj : category.getChildren()) {
                if (obj.getDisplayName().equals(theObj.getDisplayName()) && obj.getType() == theObj.getType()) {
                    exist = true;
                    break;
                }
            }

            if (!exist) {
                folder.removeChild(theObj);
                category.addChild(theObj);
            }

            catalogPath = catalogs.isEmpty() ? "" : catalogs.get(0);//$NON-NLS-1$
        } else {
            // check up the system catalog, create it and all system tree objects
            // into it if these objects are not categorized
            if (XSystemObjects.isExist(theObj.getType(), theObj.getDisplayName())) {
                TreeParent systemCatalog = null;
                for (TreeObject xobj : folder.getChildren()) {
                    if (DEFAULT_CATALOG.equals(filterOutBlank(xobj.getDisplayName()))
                            && xobj.getType() == TreeObject.CATEGORY_FOLDER) {
                        systemCatalog = (TreeParent) xobj;
                        break;
                    }
                }

                if (folder.getDisplayName().equals("System") && folder.getType() == TreeObject.CATEGORY_FOLDER) {
                    return DEFAULT_CATALOG;
                }

                if (theObj.getParent() == null) {
                    internalCheck = !fireEvent;
                    return DEFAULT_CATALOG;
                }
                if (theObj.getServerRoot() != null) {
                    String xpath = "//" + theObj.getServerRoot().getUser().getUsername() + "/"//$NON-NLS-1$//$NON-NLS-2$
                            + filterOutBlank(folder.getDisplayName()) + "//child::*[name() = 'System'"//$NON-NLS-1$
                            + " and @Url='" + getURLFromTreeObject(theObj) + "']";//$NON-NLS-1$//$NON-NLS-2$

                    Document doc = credentials.get(UnifyUrl(folder.getServerRoot().getWsKey().toString())).doc;
                    if (systemCatalog == null) {
                        List<Element> elems = doc.selectNodes(xpath);
                        if (elems.isEmpty()) {
                            systemCatalog = new TreeParent(DEFAULT_CATALOG, folder.getServerRoot(), TreeObject.CATEGORY_FOLDER,
                                    null, null);
                            folder.addChild(systemCatalog);
                            folder.removeChild(theObj);
                            systemCatalog.addChild(theObj);
                            systemCatalog.setServerRoot(folder.getServerRoot());

                            Element elemFolder = getParentElement(folder);
                            Element elemSystem = elemFolder.addElement(systemCatalog.getDisplayName());
                            elemSystem.setText(TreeObject.CATEGORY_FOLDER + "");//$NON-NLS-1$
                            elemSystem.addAttribute(URL, getURLFromTreeObject(folder));
                            Element childElem = elemSystem.addElement(filterOutBlank(theObj.getDisplayName()));
                            childElem.setText(theObj.getType() + "");//$NON-NLS-1$

                            saveDocument(folder);
                        }
                    } else {
                        boolean exist = false;

                        for (TreeObject xbj : systemCatalog.getChildren()) {
                            if (xbj.getDisplayName().equals(theObj.getDisplayName()) && xbj.getType() == theObj.getType()) {
                                exist = true;
                                break;
                            }

                        }
                        if (!exist) {
                            folder.removeChild(theObj);
                            systemCatalog.addChild(theObj);
                            List<Element> elems = doc.selectNodes(xpath);
                            if (!elems.isEmpty()) {
                                elems.get(0).addElement(filterOutBlank(theObj.getDisplayName())).setText(theObj.getType() + "");//$NON-NLS-1$
                                saveDocument(folder);
                            }

                        }
                    }
                }
                catalogPath = DEFAULT_CATALOG;
            }
        }

        // check up the catalog with none children
        checkUpCatalogHavingNoChildren(theObj, folder);

        if (theObj instanceof TreeParent) {
            TreeParent subParent = (TreeParent) theObj;
            for (TreeObject obj : subParent.getChildren()) {
                synchronizeWithElem(obj, subParent, fireEvent);
            }
        }

        internalCheck = !fireEvent;

        return catalogPath;
    }

    private void checkUpAllCategoryForModel(TreeParent model) {
        if (model.getServerRoot() == null) {
            return;
        }
        String xpath = "//" + model.getServerRoot().getUser().getUsername() + "/" + filterOutBlank(model.getDisplayName())//$NON-NLS-1$//$NON-NLS-2$
                + "//child::*[text() = '" + TreeObject.CATEGORY_FOLDER + "' and @Url='" + getURLFromTreeObject(model) + "']";//$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        Document doc = credentials.get(UnifyUrl(model.getServerRoot().getWsKey().toString())).doc;
        String xpathForModel = getXPathForTreeObject(model);
        List<Element> elems = doc.selectNodes(xpathForModel);
        Element modelElem = elems.get(0);
        elems = doc.selectNodes(xpath);
        for (Element elem : elems) {
            Element spec = elem;
            ArrayList<Element> hierarchicalList = new ArrayList<Element>();
            while (spec != modelElem) {
                hierarchicalList.add(spec);
                spec = spec.getParent();
            }
            Collections.reverse(hierarchicalList);
            TreeParent modelCpy = model;
            while (!hierarchicalList.isEmpty()) {
                spec = hierarchicalList.remove(0);
                String elemName = spec.getName();
                if (spec.attributeValue(REALNAME) != null) {
                    elemName = spec.attributeValue(REALNAME);
                }
                TreeObject to = findObject(modelCpy, Integer.parseInt(spec.getText().trim()), elemName);
                if (to == null) {
                    TreeParent catalog = new TreeParent(elemName, modelCpy.getServerRoot(), TreeObject.CATEGORY_FOLDER, null,
                            null);
                    boolean cpyInternalCheck = internalCheck;
                    internalCheck = true;
                    modelCpy.addChild(catalog);
                    internalCheck = cpyInternalCheck;
                    modelCpy = catalog;
                } else {
                    modelCpy = (TreeParent) to;
                }
            }
        }
    }

    private TreeObject findObject(TreeObject xobj, int type, String name) {
        if (!(xobj instanceof TreeParent)) {
            return null;
        }

        TreeParent parent = (TreeParent) xobj;
        for (TreeObject child : parent.getChildren()) {
            if (child.getType() == type && filterOutBlank(child.getDisplayName()).equals(filterOutBlank(name))) {
                return child;
            }
        }
        return null;
    }

    private void checkUpCatalogHavingNoChildren(TreeObject theObj, TreeParent folder) {
        if (folder.getServerRoot() == folder || theObj.getServerRoot() == null) {
            return;
        }
        if (folder.getServerRoot() == null) {
            return;
        }
        Document doc = credentials.get(UnifyUrl(folder.getServerRoot().getWsKey().toString())).doc;
        String path = this.getXPathForTreeObject(folder);
        List<Element> topElems = doc.selectNodes(path);
        if (topElems.size() > 0 && folder.getParent() != null) {
            if (isAEXtentisObjects(topElems.get(0), folder) == XTENTIS_LEVEL && folder.getParent().getType() == 23) {
                if (topElems.get(0).getParent() != null) {
                    if (isAEXtentisObjects(topElems.get(0).getParent(), folder) == MODEL_LEVEL && folder.getType() == 24) {
                        accommodations.add("Resources");//$NON-NLS-1$
                    }
                }

            }
        }

        String accmds = "";//$NON-NLS-1$
        for (String accmd : accommodations) {
            if (accmd == accommodations.get(0)) {
                accmds = "/";//$NON-NLS-1$
            }
            accmds += accmd;
            if (accmd != accommodations.get(accommodations.size() - 1)) {
                accmds += "//";//$NON-NLS-1$
            }
        }
        String xpath = "//" + theObj.getServerRoot().getUser().getUsername() + accmds + "/"//$NON-NLS-1$//$NON-NLS-2$
                + filterOutBlank(folder.getDisplayName()) + "//child::*[text() = '" + TreeObject.CATEGORY_FOLDER//$NON-NLS-1$
                + "' and @Url='" + getURLFromTreeObject(theObj)//$NON-NLS-1$
                + "' and count(child::*) = 0]";//$NON-NLS-1$

        TreeParent subFolder = folder;
        List<Element> elems = doc.selectNodes(xpath);
        for (Element elem : elems) {
            String xpaths = getXPathForElem(elem);
            int modelPos = xpaths.indexOf(filterOutBlank(folder.getDisplayName()));
            if (modelPos == -1) {
                System.err.println("elem.getName() is in wrong position !!!"); //$NON-NLS-1$
                return;
            }
            xpaths = xpaths.substring(modelPos + filterOutBlank(folder.getDisplayName()).length() + 1);
            modelPos = xpaths.indexOf("/");//$NON-NLS-1$
            String xpathTrack = getXPathForElem(elem).substring(0, getXPathForElem(elem).indexOf(xpaths) - 1);
            while (modelPos != -1 || (!xpaths.equals("") && modelPos == -1)) {//$NON-NLS-1$
                String nodeName = modelPos != -1 ? xpaths.substring(0, modelPos) : xpaths;
                xpathTrack += "/" + nodeName;//$NON-NLS-1$
                List<Element> parnts = doc.selectNodes(xpathTrack);
                if (parnts.size() > 0 && parnts.get(0).attributeValue(REALNAME) != null) {
                    nodeName = parnts.get(0).attributeValue(REALNAME);
                }
                boolean catalogExist = false;
                for (TreeObject child : subFolder.getChildren()) {
                    if (nodeName.equals(child.getDisplayName()) && child.getType() == TreeObject.CATEGORY_FOLDER) {
                        catalogExist = true;
                        subFolder = (TreeParent) child;
                        break;
                    }
                }

                if (!catalogExist) {
                    if (parnts.size() > 0 && parnts.get(0).getText().equals(TreeObject.CATEGORY_FOLDER)) {
                        TreeParent catalog = new TreeParent(nodeName, folder.getServerRoot(), TreeObject.CATEGORY_FOLDER, null,
                                null);
                        subFolder.addChild(catalog);
                        subFolder = catalog;
                    }
                }

                if (xpaths.indexOf("/") != -1) {//$NON-NLS-1$
                    xpaths = xpaths.substring(modelPos + 1);
                    modelPos = xpaths.indexOf("/");//$NON-NLS-1$
                } else {
                    xpaths = "";//$NON-NLS-1$
                }

            }
            subFolder = folder;
        }

        accommodations.clear();
    }

    private ArrayList<String> checkUpCatalogRepositoryForTreeObject(TreeObject theObj, TreeObject folder) {
        if (theObj.getType() == 0 || theObj.getType() == TreeObject.CATEGORY_FOLDER) {
            return null;
        }
        try {
            String modelName = getXPathForTreeObject(folder);
            String url = getURLFromTreeObject(theObj);

            String xpath = modelName
                    + "//child::*[text() = '" + TreeObject.CATEGORY_FOLDER + "' and @Url='" + url + "']//child::*";//$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

            Document doc = credentials.get(getURLFromTreeObject(folder)).doc;
            List<Element> elems = doc.selectNodes(xpath);
            for (Element elem : elems) {
                String xpathElem = getXPathForElem(elem);
                String xpathObj = getXPathForTreeObject(theObj);
                int squarebk = xpathObj.indexOf("[");//$NON-NLS-1$
                if (squarebk != -1) {
                    xpathObj = xpathObj.substring(0, squarebk);
                }
                if (elem.getName().equals(filterOutBlank(theObj.getDisplayName()))
                        && elem.getData().toString().equals(theObj.getType() + "")) {//$NON-NLS-1$
                    ArrayList<String> path = new ArrayList<String>();
                    HashMap<Integer, String> slice = new HashMap<Integer, String>();
                    while (isAEXtentisObjects(elem, theObj) > XTENTIS_LEVEL) {
                        String elemName = elem.getParent().getName();
                        if (elem.getParent().attributeValue(REALNAME) != null) {
                            elemName = elem.getParent().attributeValue(REALNAME);
                        }
                        if (elem.getText() != null
                                && StringUtils.trim(elem.getParent().getText()).equals(TreeObject.CATEGORY_FOLDER + "")) {//$NON-NLS-1$
                            path.add(elem.getParent().getName());
                            if (elem.getParent().attributeValue(REALNAME) != null) {
                                slice.put(path.size() - 1, elem.getParent().attributeValue(REALNAME));
                            }
                        }

                        elem = elem.getParent();
                    }

                    ArrayList<String> pathCpy = new ArrayList<String>(path);
                    Collections.reverse(path);
                    if (!isEqualString(xpathElem, xpathObj, path)) {
                        path = null;
                    }
                    if (path != null) {
                        for (int i = 0; i < pathCpy.size(); i++) {
                            if (slice.get(i) != null) {
                                pathCpy.set(i, slice.get(i));
                            }
                        }
                        Collections.reverse(pathCpy);
                        path = pathCpy;
                    }
                    return path;
                }
            }
        } catch (Exception ex) {
            return null;
        }

        return null;
    }

    private boolean isEqualString(String xpathElem, String xpathObj, ArrayList<String> catalogs) {
        ArrayList<String> elems = new ArrayList<String>(Arrays.asList(xpathElem.split("/")));//$NON-NLS-1$
        ArrayList<String> objs = new ArrayList<String>(Arrays.asList(xpathObj.split("/")));//$NON-NLS-1$
        int orgSize = objs.size();
        for (int i = 0; i < objs.size(); i++) {
            if (!objs.get(i).equals(elems.get(i))) {
                objs.addAll(i, catalogs);
                break;
            }
        }

        if (orgSize == objs.size() && orgSize != elems.size()) {
            objs.addAll(objs.size(), catalogs);
        }

        return CollectionUtils.isEqualCollection(elems, objs);
    }

    private int isAEXtentisObjects(Element elemXobj, TreeObject treeObj) {
        if (elemXobj == null || treeObj.getServerRoot() == null) {
            return 0;
        }
        Document doc = credentials.get(UnifyUrl(treeObj.getServerRoot().getWsKey().toString())).doc;
        if (elemXobj == doc.getRootElement()) {
            return 1;
        }

        return 1 + isAEXtentisObjects(elemXobj.getParent(), treeObj);
    }

    public int receiveUnCertainTreeObjectType(TreeObject xobj) {
        String path = this.getXPathForTreeObject(xobj);
        int level = XTENTIS_LEVEL;
        if (path.startsWith("/category/admin/EventManagement")) {//$NON-NLS-1$
            level = XTENTIS_LEVEL + 1;
        }
        Document doc = credentials.get(UnifyUrl(xobj.getServerRoot().getWsKey().toString())).doc;
        List<Element> elems = doc.selectNodes(path);
        if (!elems.isEmpty()) {
            Element elem = elems.get(0);
            while (isAEXtentisObjects(elem, xobj) >= level) {
                elem = elem.getParent();
            }
            return Integer.parseInt(elem.getText().trim());
        } else {
            return -1;
        }
    }

    public void switchOnListening() {
        internalCheck = false;
    }

    public void switchOffListening() {
        internalCheck = true;
    }

    public void swithOnSynchronize() {
        synchronize = true;
    }

    public void switchOffSynchronize() {
        synchronize = false;
    }

    public TreeObject registerNewTreeObject(TreeObject treeObject) {
        if (treeObject.getType() == TreeObject.CATEGORY_FOLDER) {
            String xpath = getXPathForTreeObject(treeObject);
            Document doc = credentials.get(UnifyUrl(treeObject.getServerRoot().getWsKey().toString())).doc;
            List<Element> elems = doc.selectNodes(xpath);
            if (!elems.isEmpty()) {
                if (catalogTreeObj == null) {
                    catalogTreeObj = elems.get(0);
                }
            }

            treeObject = treeObject.getParent();
            treeObject = registerNewTreeObject(treeObject);
        } else if (!(treeObject instanceof TreeParent) && treeObject.getParent().getType() == TreeObject.CATEGORY_FOLDER) {
            registerNewTreeObject(treeObject.getParent());
            treeObject = treeObject.getParent().getParent();
        }

        return treeObject;
    }

    public void mergeNewTreeObject(TreeObject newTreeObject) {
        if (catalogTreeObj != null) {
            catalogTreeObj.addElement(filterOutBlank(newTreeObject.getDisplayName())).setText(newTreeObject.getType() + "");//$NON-NLS-1$
            saveDocument(newTreeObject);
        }

        catalogTreeObj = null;
    }

    public boolean isInSystemCatalog(TreeObject xobject) {
        if (xobject instanceof TreeParent) {
            if (xobject.getType() == TreeObject.CATEGORY_FOLDER && xobject.getDisplayName().equals("System")) {//$NON-NLS-1$
                Document doc = credentials.get(UnifyUrl(xobject.getServerRoot().getWsKey().toString())).doc;
                String path = this.getXPathForTreeObject(xobject);
                List<Element> elems = doc.selectNodes(path);
                if (!elems.isEmpty()) {
                    Element elem = elems.get(0);
                    if (isAEXtentisObjects(elem, xobject) == XTENTIS_LEVEL) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            TreeParent parent = xobject.getParent();
            return isInSystemCatalog(parent);
        }
    }

    public void treeCollapsed(TreeExpansionEvent event) {
        setTreeNodeImage(event, false);
    }

    public void treeExpanded(TreeExpansionEvent event) {
        setTreeNodeImage(event, true);
    }

    private void addAttributeToCategoryElem(TreeParent category, String attrName, String defaultAttrValue) {
        Element elem = locateCategoryElement(category);
        Attribute attr = elem.attribute(attrName);
        if (attr != null) {
            elem.remove(attr);
        }
        elem.addAttribute(attrName, defaultAttrValue);
    }

    private Element locateCategoryElement(TreeParent category) {
        if (category.getParent() == category.getServerRoot()) {
            return null;
        }

        String xpath = "//" + category.getServerRoot().getUser().getUsername() + "//"//$NON-NLS-1$//$NON-NLS-2$
                + filterOutBlank(category.getParent().getDisplayName()) + "//child::*/.[text() = '" + TreeObject.CATEGORY_FOLDER//$NON-NLS-1$
                + "' and name()='" + filterOutBlank(category.getDisplayName()) + "' and @Url = '" + getURLFromTreeObject(category) + "']";//$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

        Document doc = credentials.get(UnifyUrl(category.getServerRoot().getWsKey().toString())).doc;
        List<Element> elms = doc.selectNodes(xpath);

        return !elms.isEmpty() ? elms.get(0) : null;
    }

    private void setTreeNodeImage(TreeExpansionEvent event, boolean expand) {
        // Object elem = event.getElement();
        // Widget item = view.getViewer().testFindItem(event.getElement());
        // if (view == null) {
        // return;
        // }
        // if (item == null || (item != itemFocus && itemFocus != null)) {
        // item = itemFocus;
        // }
        // if (elem instanceof TreeParent && item != null) {
        // TreeParent parent = (TreeParent) elem;
        // if (parent.getType() == TreeObject.CATEGORY_FOLDER) {
        //                ((TreeItem) item).setImage(expand ? ImageCache.getCreatedImage("icons/folder_open.gif") : ImageCache//$NON-NLS-1$
        //                        .getCreatedImage("icons/folder.gif"));//$NON-NLS-1$
        //
        // Element elemCategory = locateCategoryElement(parent);
        // if (elemCategory != null) {
        // elemCategory.attribute(EXPAND_NAME).setValue(expand ? EXPAND_VALUE : COLLAPSE_VALUE);
        // saveDocument(parent);
        // }
        //
        // }
        // }
    }

    public boolean getCategoryExpandState(TreeParent category) {
        Element elem = locateCategoryElement(category);
        if (elem != null) {
            return Boolean.valueOf(elem.attribute(EXPAND_NAME).getValue());
        }

        return false;
    }

    private void getTreeItemFromTreeObject(TreeItem[] items, TreeObject xobj, ArrayList<TreeItem> ret) {
        for (TreeItem o : items) {
            TreeObject treeObj = (TreeObject) o.getData();
            if (equalTermsWithTreeObject(treeObj, xobj)) {
                ret.add(o);
                return;
            }
            getTreeItemFromTreeObject(o.getItems(), xobj, ret);
        }
    }

    private boolean equalTermsWithTreeObject(TreeObject theObj, TreeObject otherObj) {
        if (theObj == null || otherObj == null) {
            return false;
        }
        if (theObj == otherObj) {
            return true;
        }

        if (theObj.getParent() == theObj.getServerRoot() && otherObj.getParent() == otherObj.getServerRoot()) {
            if (theObj.getDisplayName().equals(otherObj.getDisplayName())) {
                return true;
            } else {
                return false;
            }
        }

        if (theObj.getType() != otherObj.getType()
                || !filterOutBlank(theObj.getDisplayName()).equals(filterOutBlank(otherObj.getDisplayName()))) {
            return false;
        }

        return equalTermsWithTreeObject(theObj.getParent(), otherObj.getParent());
    }

    //
    // public void refreshCategoryStateWithinModel(TreeParent model) {
    // Element elemCategory = null;
    // for (TreeObject xobj : model.getChildren()) {
    // if (xobj instanceof TreeParent) {
    // refreshCategoryStateWithinModel((TreeParent) xobj);
    // } else {
    // continue;
    // }
    //
    // elemCategory = locateCategoryElement((TreeParent) xobj);
    // if (elemCategory == null) {
    // continue;
    // }
    // Attribute attr = elemCategory.attribute(EXPAND_NAME);
    // if (attr != null) {
    // String value = attr.getValue();
    // Tree tree = (Tree) view.getViewer().getControl();
    // ArrayList<TreeItem> list = new ArrayList<TreeItem>();
    // getTreeItemFromTreeObject(tree.getItems(), xobj, list);
    // itemFocus = list.isEmpty() ? null : list.get(0);
    // if (itemFocus == null) {
    // return;
    // }
    // TreeExpansionEvent event = new TreeExpansionEvent(view.getViewer(), xobj);
    // if (value.equals(EXPAND_VALUE)) {
    // // set the category to be closed if it is empty
    // if (elemCategory.elements().isEmpty()) {
    // itemFocus.setExpanded(false);
    // treeCollapsed(event);
    // } else {
    // itemFocus.setExpanded(true);
    // treeExpanded(event);
    // }
    // } else {
    // itemFocus.setExpanded(false);
    // treeCollapsed(event);
    // }
    // itemFocus = null;
    // }
    // }
    // }

    public void resetAllCategoryState(TreeParent root) {
        for (TreeObject xobj : root.getChildren()) {
            if (xobj.getType() == TreeObject.CATEGORY_FOLDER) {
                locateCategoryElement((TreeParent) xobj).attribute(EXPAND_NAME).setValue(COLLAPSE_VALUE);
            }

            if (xobj instanceof TreeParent && xobj.getType() != TreeObject.CATEGORY_FOLDER) {
                resetAllCategoryState((TreeParent) xobj);
            }
        }

        saveDocument(root);
    }

    public void receiveAllOffsprings(TreeParent parent, ArrayList<TreeObject> list) {
        list.addAll(Arrays.asList(parent.getChildren()));
        for (TreeObject obj : parent.getChildren()) {
            if (obj instanceof TreeParent) {
                receiveAllOffsprings((TreeParent) obj, list);
            }
        }
    }

    public void setLazySaveStrategy(boolean lazy, TreeParent parent) {
        lazySave = lazy;
        saveDocument(parent);
    }

    public void addAccommodation(String name) {
        accommodations.add(name);
    }

    public void clearAccommodation() {
        accommodations.clear();
    }

    private Element parseElements(String schema) {
        SAXReader saxReader = new SAXReader();
        try {
            Document doc = saxReader.read(new StringReader(schema));
            return doc.getRootElement();
        } catch (DocumentException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    public void makeUpDocWithImportCategory(String[] schemas, TreeParent serverRoot) {
        if (serverRoot.getServerRoot() == null) {
            return;
        }
        Document orgDoc = credentials.get(UnifyUrl(serverRoot.getServerRoot().getWsKey().toString())).doc;
        // spareDoc is meant to show the category when import digloag is launched
        spareDoc = (Document) orgDoc.clone();
        if (schemas != null) {
            for (String schema : schemas) {
                Element subRoot = parseElements(schema);
                String subRootXquery = "descendant::" + subRoot.getName() + "[text()='" + subRoot.getTextTrim() + "']";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                Element division = pingElement(subRootXquery, spareDoc.getRootElement());
                if (division == null || division.getParent() == null) {
                    return;
                }
                Element divisionParent = division.getParent();
                divisionParent.remove(division);
                divisionParent.add((Element) subRoot.clone());
            }

            String url = getURLFromTreeObject(serverRoot);
            String urlXquery = "descendant::*[@Url != '" + url + "']";//$NON-NLS-1$//$NON-NLS-2$
            List<Element> elems = spareDoc.selectNodes(urlXquery);
            for (Element elem : elems) {
                elem.addAttribute("Url", url);//$NON-NLS-1$
            }
        }

        credentials.get(UnifyUrl(serverRoot.getServerRoot().getWsKey().toString())).doc = spareDoc;
        spareDoc = orgDoc;
        importCategories = schemas;
    }

    public void setOriginalXobjectsToImport(TreeObject[] originalXobjects) {
        originalImportXobjects = originalXobjects;
    }

    public void mergeImportCategory(TreeObject[] xobjectsToImport, TreeParent serverRoot) {
        if (xobjectsToImport.length == 0 || originalImportXobjects == null || originalImportXobjects.length == 0) {
            return;
        }

        Collection dels = CollectionUtils.subtract(Arrays.asList(originalImportXobjects), Arrays.asList(xobjectsToImport));
        ArrayList<TreeObject> delList = new ArrayList<TreeObject>(dels);
        ArrayList<String> xpaths = new ArrayList<String>();

        for (TreeObject del : delList) {
            String xpath = this.getXPathForTreeObject(del);
            xpath.replaceFirst("/" + del.getServerRoot().getUser().getUsername(), "/" + serverRoot.getUser().getUsername());//$NON-NLS-1$//$NON-NLS-2$
            xpaths.add(xpath);
        }

        mergeImportCategory(importCategories, serverRoot, xpaths);
    }

    public void cancelMergeImportCategory(TreeParent serverRoot) {
        if (spareDoc != null) {
            if (serverRoot != null && serverRoot.getServerRoot() != null) {
                String key = UnifyUrl(serverRoot.getServerRoot().getWsKey().toString());
                if (credentials.get(key) != null) {
                    credentials.get(key).doc = spareDoc;
                }
            }
            originalImportXobjects = null;
            importCategories = null;
            spareDoc = null;
        }
    }

    private void mergeImportCategory(String[] schemas, TreeParent serverRoot, ArrayList<String> xpathsToDel) {
        if (serverRoot.getServerRoot() == null) {
            return;
        }
        Document orgDoc = spareDoc;
        String user = serverRoot.getServerRoot().getUser().getUsername();
        String xpathPrefix = "/category/" + user;//$NON-NLS-1$
        String xmlPrefix = "<category><" + user + ">*</" + user + "></category>";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        if (schemas == null) // old version, skip the import
        {
            credentials.get(UnifyUrl(serverRoot.getServerRoot().getWsKey().toString())).doc = orgDoc;
            return;
        }
        for (String schema : schemas) {
            Element root = parseElements(xmlPrefix.replace("*", schema));//$NON-NLS-1$
            Element subRoot = parseElements(schema);

            // delete all unselected xobjects from orgDoc
            for (String xpathToDel : xpathsToDel) {
                Element elemToDel = pingElement(xpathToDel, root);
                if (elemToDel != null) {
                    Element parent = elemToDel.getParent();
                    if (parent != null) {
                        parent.remove(elemToDel);
                    }
                }
            }
            // check out all categories having none child, and delete them if available
            String xpathForCategoriesWithNoneChild = "//child::*[count(child::*) = 0 and text()='" + TreeObject.CATEGORY_FOLDER//$NON-NLS-1$
                    + "']";//$NON-NLS-1$
            Element categoryToDel = pingElement(xpathForCategoriesWithNoneChild, root);
            while (categoryToDel != null) {
                Element categoryParent = categoryToDel.getParent();
                if (categoryParent != null) {
                    categoryParent.remove(categoryToDel);
                }
                categoryToDel = pingElement(xpathForCategoriesWithNoneChild, root);
            }

            subRoot = this.pingElement(xpathPrefix + "/" + subRoot.getName(), root);//$NON-NLS-1$
            String xpathForCategory = "//descendant::*[text()='" + TreeObject.CATEGORY_FOLDER + "']";//$NON-NLS-1$//$NON-NLS-2$
            List<Element> elementList = subRoot.selectNodes(xpathForCategory);
            List<String> categoryXpathForCurDoc = new ArrayList<String>();
            List<String> categoryXpathForOrgDoc = new ArrayList<String>();

            for (Element element : elementList) {
                List<String> categoryHierarchicals = parseXpathForElement(element, subRoot);
                String xpathForOrgCategory = xpathPrefix + "/" + subRoot.getName();//$NON-NLS-1$
                for (String categoryHierarchical : categoryHierarchicals) {
                    xpathForOrgCategory += "/" + categoryHierarchical;//$NON-NLS-1$
                }
                if (!categoryXpathForOrgDoc.contains(xpathForOrgCategory)) {
                    categoryXpathForOrgDoc.add(xpathForOrgCategory);
                }
            }
            xpathForCategory = xpathPrefix + "/" + subRoot.getName() + "//descendant::*[text()='" + TreeObject.CATEGORY_FOLDER//$NON-NLS-1$//$NON-NLS-2$
                    + "']";//$NON-NLS-1$
            // String xpathForOrgCategory = xpathPrefix + "/" + subRoot.getName() + "//descendant::" + element.getName()
            // + "[text()='" + element.getTextTrim() + "']";
            String topElemXpath = xpathPrefix + "/" + subRoot.getName();//$NON-NLS-1$
            Element topElem = pingElement(topElemXpath, orgDoc.getRootElement());
            elementList = orgDoc.getRootElement().selectNodes(xpathForCategory);
            for (Element elem : elementList) {
                List<String> categoryHierarchicals = parseXpathForElement(elem, topElem);
                String xpathForOrgCategory = xpathPrefix + "/" + subRoot.getName();//$NON-NLS-1$
                for (String categoryHierarchical : categoryHierarchicals) {
                    xpathForOrgCategory += "/" + categoryHierarchical;//$NON-NLS-1$
                }
                if (!categoryXpathForCurDoc.contains(xpathForOrgCategory)) {
                    categoryXpathForCurDoc.add(xpathForOrgCategory);
                }
            }

            for (String categoryHierarchical : categoryXpathForOrgDoc) {
                createOrReplaceCategory(categoryHierarchical, categoryXpathForCurDoc, root, orgDoc.getRootElement(), serverRoot);
            }
        }
        // success
        credentials.get(UnifyUrl(serverRoot.getServerRoot().getWsKey().toString())).doc = orgDoc;
    }

    private void createOrReplaceCategory(String categoryHierarchical, List<String> categoryXpathForCurDoc, Element srcElem,
            Element tgtElem, TreeParent serverRoot) {
        if (categoryXpathForCurDoc.contains(categoryHierarchical)) {
            // transfer all elements under the imported category to corresponding pos of org doc
            transferElementsWithCategoryPath(categoryHierarchical, srcElem, tgtElem);
        } else {
            String match = "";//$NON-NLS-1$
            for (String categoryXpath : categoryXpathForCurDoc) {
                if (categoryXpath.startsWith(categoryHierarchical)) {
                    // transfer all elements under the imported category to corresponding pos of org doc
                    transferElementsWithCategoryPath(categoryHierarchical, srcElem, tgtElem);
                } else if (categoryHierarchical.startsWith(categoryXpath)) {
                    match = categoryXpath;
                }
            }

            if (match.length() >= 0) {
                // create category according to the given xpath
                createCategoryForOrgDoc(categoryHierarchical, srcElem, tgtElem, serverRoot);
            }
        }
    }

    private void createCategoryForOrgDoc(String categoryToCreate, Element srcElem, Element tgtElem, TreeParent serverRoot) {
        String[] xpathSnippetsToCreate = categoryToCreate.split("/");//$NON-NLS-1$
        int categoryBeginPos = 3;
        if (xpathSnippetsToCreate[3].equals("EventManagement")) {//$NON-NLS-1$
            categoryBeginPos = 4;
        }

        int pos = categoryToCreate.indexOf("/" + xpathSnippetsToCreate[categoryBeginPos]);//$NON-NLS-1$
        String categoryXpath = categoryToCreate.substring(0, pos + xpathSnippetsToCreate[categoryBeginPos].length() + 1);
        Element subParentElem = pingElement(categoryXpath, tgtElem);
        for (int i = categoryBeginPos + 1; i < xpathSnippetsToCreate.length; i++) {
            String categoryXpathSnippet = "child::" + xpathSnippetsToCreate[i] + "[text()='" + TreeObject.CATEGORY_FOLDER + "']";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            Element newCategory = pingElement(categoryXpathSnippet, subParentElem);
            if (newCategory == null) {
                Element existedCategory = pingElement(categoryXpath + "/" + categoryXpathSnippet, srcElem);//$NON-NLS-1$
                newCategory = subParentElem.addElement(xpathSnippetsToCreate[i]);
                newCategory.setText(TreeObject.CATEGORY_FOLDER + "");//$NON-NLS-1$
                newCategory.addAttribute(URL, getURLFromTreeObject(serverRoot));
                newCategory.addAttribute(REALNAME, existedCategory.attributeValue(REALNAME));
            }
            categoryXpath += "/" + xpathSnippetsToCreate[i];//$NON-NLS-1$
            subParentElem = newCategory;
        }

        transferElementsWithCategoryPath(categoryToCreate, srcElem, tgtElem);
    }

    private Element drillUpForDevisionElement(Element elem) {
        Element parent = elem.getParent();
        while (parent != null && !parent.getTextTrim().equals(elem.getTextTrim()) && !(parent.getTextTrim().equals("0"))) {//$NON-NLS-1$
            parent = parent.getParent();
        }

        return parent;
    }

    private void transferElementsWithCategoryPath(String categoryXpath, Element srcElemRoot, Element targtElemRoot) {
        categoryXpath += "[text()='" + TreeObject.CATEGORY_FOLDER + "']";//$NON-NLS-1$//$NON-NLS-2$
        // clear up the context of targtElemRoot firstly
        Element elemCategoryInTagt = pingElement(categoryXpath, targtElemRoot);
        List elems = elemCategoryInTagt.content();
        List xobjects = new ArrayList();
        for (Object obj : elems) {
            if (obj instanceof Element) {
                Element elem = (Element) obj;
                if (!elem.getTextTrim().equals(TreeObject.CATEGORY_FOLDER + "")) {//$NON-NLS-1$
                    Element division = drillUpForDevisionElement(elem);
                    division.addElement(elem.getName()).setText(elem.getTextTrim());
                    xobjects.add(elem);
                }
            }
        }

        elemCategoryInTagt.content().removeAll(xobjects);

        Element elemCategoryInSrc = pingElement(categoryXpath, srcElemRoot);
        elems = elemCategoryInSrc.content();
        for (Object obj : elems) {
            if (obj instanceof Element) {
                Element elem = (Element) obj;
                if (!elem.getTextTrim().equals(TreeObject.CATEGORY_FOLDER + "")) {//$NON-NLS-1$
                    String xpath = ".//descendant::" + elem.getName() + "[text()='" + elem.getTextTrim() + "']";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                    List<Element> es = targtElemRoot.selectNodes(xpath);
                    Element newElem = null;
                    for (Element elemExist : es) {
                        if (elemExist.getParent() != null
                                && elemExist.getParent().getTextTrim().equals(TreeObject.EVENT_MANAGEMENT + "")) {
                            continue;
                        }
                        if (elemExist != null) {
                            Element parentExist = elemExist.getParent();
                            parentExist.remove(elemExist);
                        }
                        elemExist = pingElement(categoryXpath, targtElemRoot);
                        newElem = elemExist.addElement(elem.getName());
                        newElem.setText(elem.getTextTrim());
                    }
                    if (es.size() == 0 || (es.size() > 0 && newElem == null)) {
                        Element elemExist = pingElement(categoryXpath, targtElemRoot);
                        newElem = elemExist.addElement(elem.getName());
                        newElem.setText(elem.getTextTrim());
                    }
                }
            }

        }
    }

    private Element pingElement(String xpath, Element root) {
        List<Element> elems = root.selectNodes(xpath);
        if (elems.size() > 0) {
            return elems.get(0);
        }
        return null;
    }

    private List<String> parseXpathForElement(Element elem, Element subRoot) {
        List<String> list = new ArrayList<String>();
        while (elem != subRoot) {
            if (elem.attributeValue("name") != null) {
                list.add(elem.attributeValue("name"));//$NON-NLS-1$
            } else {
                list.add(elem.getName());
            }

            elem = elem.getParent();
        }

        Collections.reverse(list);
        return list;
    }

    public void parseElementForOutput(TreeObject[] xobjs) {
        for (TreeObject xobj : xobjs) {
            TreeObject subParent = xobj;
            while (subParent.getParent().getType() != 0) {
                subParent = subParent.getParent();
            }
            Element modelElem = getParentElement(subParent);

            if (!outPutSchemas.containsKey(modelElem.getName())) {
                Element copyElem = (Element) modelElem.clone();
                copyElem.clearContent();
                copyElem.setText(modelElem.getTextTrim());
                outPutSchemas.put(modelElem.getName(), copyElem);
            }

            subParent = xobj;
            TreeObject categorySubRoot = null;
            while (subParent.getParent().getType() == TreeObject.CATEGORY_FOLDER) {
                categorySubRoot = subParent.getParent();
                subParent = subParent.getParent();
            }

            Element divisionElem = null;
            Element copyModelElem = outPutSchemas.get(modelElem.getName());
            Document doc = credentials.get(UnifyUrl(xobj.getServerRoot().getWsKey().toString())).doc;
            String division = xobj.getType() == TreeObject.TRANSFORMER ? "Process" : "Trigger";//$NON-NLS-1$//$NON-NLS-2$
            String xpathForDivision = ".//child::" + division + "[text()='" + xobj.getType() + "']";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

            if (categorySubRoot != null) {
                Element categoryElem = getParentElement(categorySubRoot);
                if (categoryElem.getParent() != modelElem) {
                    divisionElem = pingElement(xpathForDivision, copyModelElem);
                    if (divisionElem == null) {
                        divisionElem = copyModelElem.addElement(categoryElem.getParent().getName());
                        divisionElem.setText(categoryElem.getParent().getTextTrim());
                    }
                } else {
                    divisionElem = copyModelElem;
                }

                Element categoryElementClone = (Element) categoryElem.clone();
                String xpath = "./child::" + categoryElem.getName() + "[text()='" + TreeObject.CATEGORY_FOLDER + "']";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                if (divisionElem.selectNodes(xpath).size() == 0) {
                    divisionElem.add(categoryElementClone);
                }
            } else {
                // individual xobject
                Element xobjElem = pingElement(getXPathForTreeObject(xobj), doc.getRootElement());
                Element parentElem = xobjElem.getParent();
                if (parentElem == modelElem) {
                    parentElem = copyModelElem;
                } else {
                    divisionElem = pingElement(xpathForDivision, copyModelElem);
                    if (divisionElem == null) {
                        divisionElem = copyModelElem.addElement(parentElem.getName());
                        divisionElem.setText(parentElem.getTextTrim());
                    }
                }

                String xpath = ".//child::" + xobjElem.getName() + "[text()='" + xobjElem.getTextTrim() + "']";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                if (divisionElem != null && pingElement(xpath, divisionElem) == null) {
                    divisionElem.add((Element) xobjElem.clone());
                }
            }
        }

        // filter those excluded from xobjects out of categorys
        String xpath = ".//descendant::*[text() ='" + TreeObject.CATEGORY_FOLDER + "']";//$NON-NLS-1$//$NON-NLS-2$
        Iterator<Element> iter = outPutSchemas.values().iterator();
        while (iter.hasNext()) {
            Element divisionElement = iter.next();
            List<Element> categorys = divisionElement.selectNodes(xpath);

            if (categorys != null) {
                for (Element categoryElems : categorys) {
                    List objs = categoryElems.content();
                    List<Element> elemToDel = new ArrayList<Element>();
                    for (Object obj : objs) {
                        if (obj instanceof Element) {
                            Element categoryElement = (Element) obj;
                            if (categoryElement.getTextTrim().equals(TreeObject.CATEGORY_FOLDER + "")) {
                                continue;
                            }
                            boolean match = false;
                            for (TreeObject xobj : xobjs) {
                                if (filterOutBlank(xobj.getDisplayName()).equals(categoryElement.getName())
                                        && categoryElement.getTextTrim().equals(xobj.getType() + "")) {//$NON-NLS-1$
                                    match = true;
                                    break;
                                }
                            }
                            if (!match) {
                                elemToDel.add(categoryElement);
                            }
                        }
                    }

                    for (Element del : elemToDel) {
                        categoryElems.remove(del);
                    }
                }
            }
        }

        ArrayList<String> schemas = new ArrayList<String>();
        Iterator<Element> iterd = outPutSchemas.values().iterator();
        while (iterd.hasNext()) {
            schemas.add(iterd.next().asXML());
        }
    }

    public String[] outPutSchemas() {
        ArrayList<String> schemas = new ArrayList<String>();
        Iterator<Element> iter = outPutSchemas.values().iterator();
        while (iter.hasNext()) {
            schemas.add(iter.next().asXML());
        }
        outPutSchemas.clear();
        return schemas.toArray(new String[schemas.size()]);
    }
}
