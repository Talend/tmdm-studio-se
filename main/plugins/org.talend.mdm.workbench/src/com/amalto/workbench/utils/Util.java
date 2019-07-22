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
package com.amalto.workbench.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PropertyResourceBundle;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.impl.XSDImportImpl;
import org.eclipse.xsd.impl.XSDIncludeImpl;
import org.eclipse.xsd.impl.XSDModelGroupImpl;
import org.eclipse.xsd.impl.XSDParticleImpl;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDSchemaLocator;
import org.osgi.framework.Bundle;
import org.talend.core.GlobalServiceRegister;
import org.talend.mdm.commmon.util.core.EUUIDCustomType;
import org.talend.mdm.commmon.util.core.ICoreConstants;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.talend.mdm.commmon.util.workbench.Version;
import org.talend.mdm.commmon.util.workbench.ZipToFile;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.editors.xsdeditor.XSDEditor;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeObjectTransfer;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.service.IWebServiceHook;
import com.amalto.workbench.service.MissingJarService;
import com.amalto.workbench.service.MissingJarsException;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.TMDMService_Service;
import com.amalto.workbench.webservices.WSComponent;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModel;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSGetComponentVersion;
import com.amalto.workbench.webservices.WSGetDataModel;
import com.amalto.workbench.webservices.WSGetViewPKs;
import com.amalto.workbench.webservices.WSPing;
import com.amalto.workbench.webservices.WSRegexDataClusterPKs;
import com.amalto.workbench.webservices.WSRegexDataModelPKs;
import com.amalto.workbench.webservices.WSRoutingRuleExpression;
import com.amalto.workbench.webservices.WSRoutingRuleOperator;
import com.amalto.workbench.webservices.WSStringPredicate;
import com.amalto.workbench.webservices.WSVersion;
import com.amalto.workbench.webservices.WSViewPK;
import com.amalto.workbench.webservices.WSWhereCondition;
import com.amalto.workbench.webservices.WSWhereOperator;
import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.xml.internal.ws.wsdl.parser.InaccessibleWSDLException;

/**
 * @author bgrieder
 */
public class Util {

    private static Log log = LogFactory.getLog(Util.class);

    public static LinkedHashMap<String, String> iso2lang = new LinkedHashMap<String, String>();

    static {
        iso2lang.put("en", "English");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("fr", "French");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("es", "Spanish");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ab", "Abkhazian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("aa", "Afar");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("af", "Afrikaans");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("sq", "Albanian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("am", "Amharic");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ar", "Arabic");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("hy", "Armenian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("as", "Assamese");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ay", "Aymara");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("az", "Azerbaijani");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ba", "Bashkir");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("eu", "Basque");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("bn", "Bengali (Bangla)");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("dz", "Bhutani");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("bh", "Bihari");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("bi", "Bislama");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("br", "Breton");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("bg", "Bulgarian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("my", "Burmese");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("be", "Byelorussian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("km", "Cambodian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ca", "Catalan");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("zh", "Chinese");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("co", "Corsican");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("hr", "Croatian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("cs", "Czech");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("da", "Danish");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("nl", "Dutch");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("eo", "Esperanto");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("et", "Estonian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("fo", "Faeroese");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("fa", "Farsi");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("fj", "Fiji");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("fi", "Finnish");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("fy", "Frisian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("gl", "Galician");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ka", "Georgian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("de", "German");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("el", "Greek");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("kl", "Greenlandic");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("gn", "Guarani");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("gu", "Gujarati");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ha", "Hausa");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("iw, he", "Hebrew");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("hi", "Hindi");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("hu", "Hungarian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("is", "Icelandic");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("in, id", "Indonesian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ia", "Interlingua");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ie", "Interlingue");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("iu", "Inuktitut");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ik", "Inupiak");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ga", "Irish");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("it", "Italian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ja", "Japanese");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ji", "Yiddish");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("jw", "Javanese");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("kn", "Kannada");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ks", "Kashmiri");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("kk", "Kazakh");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("rw", "Kinyarwanda");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ky", "Kirghiz");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("rn", "Kirundi");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ko", "Korean");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ku", "Kurdish");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("lo", "Laothian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("la", "Latin");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("lv", "Latvian (Lettish)");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ln", "Lingala");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("lt", "Lithuanian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("mk", "Macedonian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("mg", "Malagasy");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ms", "Malay");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ml", "Malayalam");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("mt", "Maltese");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("gv", "Manx Gaelic");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("mi", "Maori");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("mr", "Marathi");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("mo", "Moldavian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("mn", "Mongolian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("na", "Nauru");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ne", "Nepali");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("no", "Norwegian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("oc", "Occitan");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("or", "Oriya");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("om", "Oromo (Afan)");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ps", "Pashto (Pushto)");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("pl", "Polish");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("pt", "Portuguese");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("pa", "Punjabi");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("qu", "Quechua");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("rm", "Rhaeto-Romance");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ro", "Romanian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ru", "Russian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("sm", "Samoan");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("sg", "Sangro");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("sa", "Sanskrit");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("gd", "Scots Gaelic");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("sr", "Serbian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("sh", "Serbo-Croatian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("st", "Sesotho");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("tn", "Setswana");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("sn", "Shona");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("sd", "Sindhi");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("si", "Singhalese");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ss", "Siswati");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("sk", "Slovak");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("sl", "Slovenian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("so", "Somali");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("su", "Sundanese");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("sw", "Swahili");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("sv", "Swedish");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("tl", "Tagalog");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("tg", "Tajik");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ta", "Tamil");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("tt", "Tatar");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("te", "Telugu");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("th", "Thai");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("bo", "Tibetan");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ti", "Tigrinya");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("to", "Tonga");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ts", "Tsonga");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("tr", "Turkish");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("tk", "Turkmen");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("tw", "Twi");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ug", "Uighur");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("uk", "Ukrainian");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("ur", "Urdu");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("uz", "Uzbek");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("vi", "Vietnamese");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("vo", "Volapük");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("cy", "Welsh");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("wo", "Wolof");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("xh", "Xhosa");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("yi", "Yiddish");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("yo", "Yoruba");//$NON-NLS-1$//$NON-NLS-2$
        iso2lang.put("zu", "Zulu");//$NON-NLS-1$//$NON-NLS-2$
    }

    public static LinkedHashMap<String, String> lang2iso = new LinkedHashMap<String, String>();
    static {
        Set<String> isos = iso2lang.keySet();
        for (String iso : isos) {
            String language = iso2lang.get(iso);
            lang2iso.put(language, iso);
            /*
             * String existingISO = lang2iso.get(language); if (existingISO!=null) { lang2iso.put(language,
             * existingISO+", "+iso); } else { lang2iso.put(language, iso); }
             */
        }
    }

    private static MultiKeyMap cachedMDMService = new MultiKeyMap();// cache the TMDMService instance

    public static String default_endpoint_address = "http://localhost:8180/talendmdm/services/soap";//$NON-NLS-1$

    private static IWebServiceHook webServceHook;

    /*********************************************************************
     * WEB SERVICES
     *********************************************************************/
    public static TMDMService getMDMService(TreeObject xobject) throws XtentisException {
        try {
            if (xobject == null) {
                return null;
            }
            String endpointAddress = xobject.getEndpointAddress();
            String username = xobject.getUsername();
            String password = xobject.getPassword();

            TMDMService mdmService = getMDMService(new URL(endpointAddress), username, password);

            return mdmService;
        } catch (MalformedURLException e) {
            throw new XtentisException(Messages.Util_0 + xobject.getEndpointAddress());
        }
    }

    public static TMDMService getMDMService(String username, String password) throws XtentisException {
        try {
            TMDMService mdmService = getMDMService(new URL(default_endpoint_address), username, password);

            return mdmService;
        } catch (MalformedURLException e) {
            String err = Messages.Util_1 + default_endpoint_address + Messages.Util_2;
            throw new XtentisException(err);
        }
    }

    public static TMDMService getMDMService(URL url, String username, String password) throws XtentisException {
        TMDMService mdmService = getMDMService(url, username, password, true);

        return mdmService;
    }

    public static TMDMService getMDMService(URL url, final String username, final String password, boolean showMissingJarDialog)
            throws XtentisException {
        url = checkAndAddSuffix(url);

        boolean needCheck = true;
        TMDMService service = (TMDMService) cachedMDMService.get(url, username, password);
        if (service == null) {
            needCheck = false;

            boolean checkResult = MissingJarService.getInstance().checkMissingJar(showMissingJarDialog);
            if (!checkResult) {
                throw new MissingJarsException("Missing dependency libraries."); //$NON-NLS-1$
            }

            try {

                TMDMService_Service service_service = new TMDMService_Service(url);

                service = service_service.getTMDMPort();

                BindingProvider stub = (BindingProvider) service;

                // Do not maintain session via cookies
                stub.getRequestContext().put(BindingProvider.SESSION_MAINTAIN_PROPERTY, false);

                Map<String, Object> context = stub.getRequestContext();
                // // dynamic set endpointAddress
                // context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

                // authentication
                context.put(BindingProvider.USERNAME_PROPERTY, username);
                context.put(BindingProvider.PASSWORD_PROPERTY, password);

                IWebServiceHook wsHook = getWebServiceHook();
                if (wsHook != null) {
                    wsHook.preRequestSendingHook(stub, username);
                }

                cachedMDMService.put(url, username, password, service);
            } catch (WebServiceException e) {
                XtentisException ex = convertWebServiceException(e);
                log.error(Messages.bind(Messages.UnableAccessEndpoint, url, e.getMessage()), e);
                if (ex != null) {
                    throw ex;
                }
            }

        }

        if (needCheck) {
            try {
                service.ping(new WSPing());
            } catch (WebServiceException e) {
                cachedMDMService.remove(url, username, password);

                XtentisException ex = convertWebServiceException(e);
                log.error(Messages.bind(Messages.UnableAccessEndpoint, url, e.getMessage()), e);
                if (ex != null) {
                    throw ex;
                }
            }
        }

        return service;
    }

    private static URL checkAndAddSuffix(URL url) {
        String protocol = url.getProtocol();
        if (protocol.equals("http") || protocol.equals("https")) {//$NON-NLS-1$ //$NON-NLS-2$
            String suffix = "wsdl"; //$NON-NLS-1$
            if (!suffix.equalsIgnoreCase(url.getQuery())) {
                try {
                    url = new URL(url.toString() + "?wsdl"); //$NON-NLS-1$
                } catch (MalformedURLException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return url;
    }

    public static XtentisException convertWebServiceException(WebServiceException wsEx) {
        Throwable throwable = analyseWebServiceException(wsEx);
        if (throwable != null) {
            String message = throwable.getMessage();
            if (message == null) {
                message = ""; //$NON-NLS-1$
            }
            return new XtentisException(message, throwable);
        }
        return null;
    }

    public static Throwable analyseWebServiceException(WebServiceException wsEx) {

        if (wsEx instanceof InaccessibleWSDLException) {
            InaccessibleWSDLException ex = (InaccessibleWSDLException) wsEx;
            for (Throwable throwable : ex.getErrors()) {

                return throwable;
            }
        }

        return wsEx;
    }

    public static IWebServiceHook getWebServiceHook() {
        if (webServceHook == null && GlobalServiceRegister.getDefault().isServiceRegistered(IWebServiceHook.class)) {
            webServceHook = (IWebServiceHook) GlobalServiceRegister.getDefault().getService(IWebServiceHook.class);
        }
        return webServceHook;
    }

    public static List<WSDataModelPK> getAllDataModelPKs(URL url, String username, String password) throws XtentisException {
        try {
            TMDMService port = Util.getMDMService(url, username, password);
            return port.getDataModelPKs(new WSRegexDataModelPKs("*")).getWsDataModelPKs();//$NON-NLS-1$
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new XtentisException(Messages.Util_5 + Messages.Util_6 + e.getLocalizedMessage());
        }
    }

    public static List<WSDataClusterPK> getAllDataClusterPKs(URL url, String username, String password) throws XtentisException {
        try {
            TMDMService port = Util.getMDMService(url, username, password);
            return port.getDataClusterPKs(new WSRegexDataClusterPKs("*")).getWsDataClusterPKs();//$NON-NLS-1$
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new XtentisException(Messages.Util_7 + Messages.Util_8 + e.getLocalizedMessage());
        }
    }

    public static List<WSViewPK> getAllViewPKs(URL url, String username, String password, String regex) throws XtentisException {
        try {
            if ((regex == null) || ("".equals(regex))) {
                regex = "*";//$NON-NLS-1$
            }
            TMDMService port = Util.getMDMService(url, username, password);
            return port.getViewPKs(new WSGetViewPKs(regex)).getWsViewPK();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new XtentisException(Messages.Util_9 + Messages.Util_10 + e.getLocalizedMessage());
        }
    }

    /*********************************************************************
     * LOCAL FILE UTILS
     *********************************************************************/

    public static String getXML(String filename) throws Exception {

        InputStream in = null;
        try {
            in = new FileInputStream(filename);
            return IOUtils.toString(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public static String getPackageFilePath(Class<? extends Object> c, String filename) {
        return c.getResource(filename).getPath();
    }

    /*********************************************************************
     * NODE UTILS
     *********************************************************************/

    /**
     * Join an array of strings into a single string using a separator
     * 
     * @param strings
     * @param separator
     * @return a single string or null
     */
    public static String joinStrings(String[] strings, String separator) {
        if (strings == null) {
            return null;
        }
        String res = "";//$NON-NLS-1$
        for (int i = 0; i < strings.length; i++) {
            res += (i > 0) ? separator : "";//$NON-NLS-1$
            res += strings[i];
        }
        return res;
    }

    public static String joinStrings(List<String> strings, String separator) {
        if (strings == null) {
            return null;
        }
        StringBuffer res = new StringBuffer();
        for (String str : strings) {
            res.append((res.length() > 0) ? separator : "");//$NON-NLS-1$
            res.append(str);
        }
        return res.toString();
    }

    /**
     * Returns the first part - eg. the concept - from the path
     * 
     * @param path
     * @return the concept Name
     */
    public static String getConceptFromPath(String path) {
        Pattern p = Pattern.compile("(.*?)[\\[|/].*");//$NON-NLS-1$
        if (!path.endsWith("/")) {
            path += "/";//$NON-NLS-1$
        }
        Matcher m = p.matcher(path);
        if (m.matches()) {
            return m.group(1);
        }
        return null;
    }

    /**
     * get the concept name from the child elment
     * 
     * @param child
     * @return
     */
    public static String getConceptName(XSDConcreteComponent child) {
        if (child.getContainer() instanceof XSDElementDeclaration && child.getContainer().getContainer() instanceof XSDSchema) {
            return child.getContainer().getElement().getAttributes().getNamedItem("name").getNodeValue();//$NON-NLS-1$
        } else {
            if (child != null && child.getContainer() != null) {
                return getConceptName(child.getContainer());
            }
        }
        return null;
    }

    /**
     * Generates an xml string from a node (not pretty formatted)
     * 
     * @param n the node
     * @return the xml string
     * @throws Exception
     */
    public static String nodeToString(Node n) throws Exception {
        StringWriter sw = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty("omit-xml-declaration", "yes");//$NON-NLS-1$//$NON-NLS-2$
        transformer.setOutputProperty("indent", "yes");//$NON-NLS-1$//$NON-NLS-2$
        transformer.transform(new DOMSource(n), new StreamResult(sw));
        return sw.toString();
    }

    /**
     * Get a nodelist from an xPath
     * 
     * @throws Exception
     */
    public static NodeList getNodeList(Document d, String xPath) throws Exception {
        return getNodeList(d.getDocumentElement(), xPath, null, null);
    }

    /**
     * Get a nodelist from an xPath
     * 
     * @throws Exception
     */
    public static NodeList getNodeList(Node contextNode, String xPath) throws Exception {
        return getNodeList(contextNode, xPath, null, null);
    }

    /**
     * Get a nodelist from an xPath
     * 
     * @throws Exception
     */
    public static NodeList getNodeList(Node contextNode, String xPath, String namespace, String prefix) throws Exception {
        XObject xo = XPathAPI.eval(contextNode, xPath,
                (namespace == null) ? contextNode : Util.getRootElement("nsholder", namespace, prefix));//$NON-NLS-1$
        if (xo.getType() != XObject.CLASS_NODESET) {
            return null;
        }
        return xo.nodelist();
    }

    /**
     * Returns a namespaced root element of a document Useful to create a namespace holder element
     * 
     * @param namespace
     * @return the root Element
     */
    public static Element getRootElement(String elementName, String namespace, String prefix) throws Exception {
        Element rootNS = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            Document namespaceHolder = impl.createDocument(namespace, (prefix == null ? "" : prefix + ":") + elementName, null);//$NON-NLS-1$//$NON-NLS-2$
            rootNS = namespaceHolder.getDocumentElement();
            rootNS.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, namespace);//$NON-NLS-1$//$NON-NLS-2$
        } catch (Exception e) {
            String err = Messages.Util_11 + e.getLocalizedMessage();
            throw new Exception(err);
        }
        return rootNS;
    }

    public static Document parse(String xmlString) throws Exception {
        return parse(xmlString, null);
    }

    public static Document parse(String xmlString, String schema) throws Exception {

        // parse
        Document d = null;
        SAXErrorHandler seh = new SAXErrorHandler();

        try {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setValidating((schema != null));
            documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", //$NON-NLS-1$
                    "http://www.w3.org/2001/XMLSchema");//$NON-NLS-1$
            if (schema != null) {
                documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", new InputSource(//$NON-NLS-1$
                        new StringReader(schema)));
            }

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setErrorHandler(seh);
            if (xmlString == null || xmlString.length() == 0 || xmlString.matches("\\s+")) {
                return d;
            }
            d = documentBuilder.parse(new InputSource(new StringReader(xmlString)));

            if (schema != null) {
                String errors = seh.getErrors();
                if (!errors.equals("")) {//$NON-NLS-1$
                    String err = Messages.Util_12 + errors + Messages.Util_13;
                    throw new Exception(err);
                }
            }
            return d;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            String err = Messages.Util_14 + Messages.Util_15 + e.getClass().getName() + Messages.Util_16 + e.getLocalizedMessage()
            + Messages.Util_17 + xmlString;
            throw new Exception(err);
        }
    }

    public static String[] getTextNodes(Node contextNode, String xPath) throws XtentisException {
        return getTextNodes(contextNode, xPath, contextNode);
    }

    public static String[] getTextNodes(Node contextNode, String xPath, Node namespaceNode) throws XtentisException {
        String[] results = null;

        // test for hard-coded values
        if (xPath.startsWith("\"") && xPath.endsWith("\"")) { //$NON-NLS-1$ //$NON-NLS-2$
            return new String[] { xPath.substring(1, xPath.length() - 1) };
        }

        // test for incomplete path (elements missing /text())
        if (!xPath.matches(".*@[^/\\]]+")) { //$NON-NLS-1$
            if (!xPath.endsWith(")")) { //$NON-NLS-1$
                xPath += "/text()";//$NON-NLS-1$
            }
        }

        try {
            XObject xo = XPathAPI.eval(contextNode, xPath, namespaceNode);
            if (xo.getType() == XObject.CLASS_NODESET) {
                NodeList l = xo.nodelist();
                int len = l.getLength();
                results = new String[len];
                for (int i = 0; i < len; i++) {
                    Node n = l.item(i);
                    results[i] = n.getNodeValue();
                }
            } else {
                results = new String[] { xo.toString() };
            }
        } catch (Exception e) {
            String err = Messages.Util_18 + xPath + Messages.Util_19 + e.getClass().getName() + Messages.Util_20
                    + e.getLocalizedMessage();
            throw new XtentisException(err);
        }
        return results;

    }

    public static String getFirstTextNode(Node contextNode, String xPath, Node namespaceNode) throws XtentisException {
        String[] res = getTextNodes(contextNode, xPath, namespaceNode);
        if (res.length == 0) {
            return null;
        }
        return res[0];
    }

    public static String getFirstTextNode(Node contextNode, String xPath) throws XtentisException {
        return getFirstTextNode(contextNode, xPath, contextNode);
    }

    /*********************************************************************
     * XSD Utils
     *********************************************************************/

    public static boolean isTypeDerivedFrom(XSDTypeDefinition typedef, String namespace, String localName) {
        // Walk the baseTypes from this typedef seeing if any
        // of them match the requested one
        XSDTypeDefinition baseType = typedef.getBaseType();

        // As this convenience method if our parameters match
        if (baseType.hasNameAndTargetNamespace(localName, namespace)) {
            return true;
        }

        // If not, check to see if we've run up to the top
        // Performance note: this is horribly inefficient,
        // re-calling this same method every time; but it
        // serves as a good example
        XSDTypeDefinition rootType = typedef.getRootType();
        if (rootType == baseType) {
            // If we've hit the root, we aren't derived from it
            return false;
        } else {
            // Otherwise continue to traverse upwards
            return isTypeDerivedFrom(baseType, namespace, localName);
        }
    }

    /**
     * Find elementDeclarations that use any types derived from a named type.
     * 
     * <p>
     * This shows one way to query the schema for elementDeclarations and then how to find specific kinds of
     * typeDefinitions.
     * </p>
     * 
     * @param objList collection set to search for elemDecls
     * @param localName for the type used
     * @return Boolean indicate any XSDElementDeclarations is found or not
     */
    public static boolean findElementsUsingType(List<Object> objList, XSDTypeDefinition localTypedef) {
        // A handy convenience method quickly gets all
        // elementDeclarations within our schema; note that
        // whether or not this returns types in included,
        // imported, or redefined schemas is subject to change

        for (Object obj : objList) {
            if (obj == localTypedef) {
                continue;
            }

            if (obj instanceof XSDParticle || obj instanceof XSDElementDeclaration || obj instanceof XSDTypeDefinition) {
                XSDTypeDefinition typedef = null;
                if (obj instanceof XSDParticle) {
                    XSDParticle xsdParticle = (XSDParticle) obj;
                    if (xsdParticle.getTerm() instanceof XSDElementDeclaration) {
                        obj = xsdParticle.getTerm();
                    }
                }
                if (obj instanceof XSDElementDeclaration) {
                    XSDElementDeclaration elem = (XSDElementDeclaration) obj;
                    if (elem.getAnonymousTypeDefinition() != null) {
                        typedef = elem.getAnonymousTypeDefinition();
                    } else if (elem.getTypeDefinition() != null) {
                        typedef = elem.getTypeDefinition();
                    } else {
                        // Element is not complete, since it has no type,
                        // thus it's not using our type
                        continue;
                    }
                } else {
                    typedef = (XSDTypeDefinition) obj;
                }

                if (typedef instanceof XSDComplexTypeDefinition) {
                    XSDComplexTypeDefinition type = (XSDComplexTypeDefinition) typedef;
                    if (localTypedef.getName().equals(type.getName()) && (localTypedef instanceof XSDComplexTypeDefinition)) {
                        return true;
                    }
                    if (type.getContent() instanceof XSDParticle) {
                        XSDParticle particle = (XSDParticle) type.getContent();
                        if (particle.getTerm() instanceof XSDModelGroup) {
                            XSDModelGroup group = (XSDModelGroup) particle.getTerm();
                            EList<XSDParticle> elist = group.getContents();
                            for (XSDParticle pt : elist) {
                                if (pt.getContent() instanceof XSDElementDeclaration) {
                                    XSDTypeDefinition typeDef = ((XSDElementDeclaration) pt.getContent()).getTypeDefinition();
                                    boolean sameType = (typeDef instanceof XSDComplexTypeDefinition
                                            && localTypedef instanceof XSDComplexTypeDefinition)
                                            || (typeDef instanceof XSDSimpleTypeDefinition
                                                    && localTypedef instanceof XSDSimpleTypeDefinition);
                                    if (typeDef != null && typeDef.getName() != null && sameType) {
                                        if ((localTypedef.getName().equals(typeDef.getName()))) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (typedef instanceof XSDSimpleTypeDefinition) {
                    XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition) typedef;
                    XSDSimpleTypeDefinition baseType = type.getBaseTypeDefinition();
                    if (baseType != null && baseType.getName().equals(localTypedef.getName())
                            && localTypedef instanceof XSDSimpleTypeDefinition) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static List<String> getAllCustomSimpleDataType(XSDSchema schema) {
        List<String> customTypes = new ArrayList<String>();
        for (Object element : schema.getTypeDefinitions()) {
            XSDTypeDefinition type = (XSDTypeDefinition) element;
            if (type instanceof XSDSimpleTypeDefinition) {
                customTypes.add(type.getName());
            }
        }
        return customTypes;
    }

    public static List<String> getAllSchemaSimpleDataType(XSDSchema schema) {
        List<String> builtInTypes = new ArrayList<String>();
        for (XSDTypeDefinition xsdTypeDefinition : schema.getSchemaForSchema().getTypeDefinitions()) {
            XSDTypeDefinition type = xsdTypeDefinition;
            if (type instanceof XSDSimpleTypeDefinition) {
                builtInTypes.add(type.getName());
            }
        }
        return builtInTypes;
    }

    public static Object findElementUsingName(XSDSchema schema, String name) {
        EList<XSDElementDeclaration> elems = schema.getElementDeclarations();
        for (XSDElementDeclaration elem : elems) {
            if (elem.getName().equals(name)) {
                return elem;
            }
        }
        return null;
    }

    /**
     * return key info if the 'key' is set to primary key under any entity
     */
    public static List<Object> getKeyInfo(Object key) {
        if (!((key instanceof XSDElementDeclaration))) {
            return null;
        }

        return getTopParent(key);

    }

    public static void getForeignKeyofParcle(Set<String> list, XSDAnnotation annotation) {
        if (annotation != null) {

            List<Element> annotList = annotation.getApplicationInformation();
            for (int k = 0; k < annotList.size(); k++) {
                if ("appinfo".equals(annotList.get(k).getLocalName())) {//$NON-NLS-1$
                    Node source = annotList.get(k).getAttributes().getNamedItem("source");//$NON-NLS-1$
                    if (source == null) {
                        continue;
                    }
                    String appinfoSource = source.getNodeValue();
                    if ("X_ForeignKey".equals(appinfoSource)) {//$NON-NLS-1$
                        String path = annotList.get(k).getFirstChild().getNodeValue();
                        list.add(getConceptFromPath(path));
                    }
                }
            }
        }
    }

    /**
     * set the list with foreign concept name of in the element
     * 
     * @author ymli
     * @param list
     * @param element
     */
    public static void getforeignKeyOfElement(Set<String> list, XSDElementDeclaration element) {
        if (element != null) {
            if (element.getAnnotation() != null) {
                getForeignKeyofParcle(list, element.getAnnotation());
            }
            if (element.getTypeDefinition() instanceof XSDComplexTypeDefinition) {
                XSDComplexTypeContent fromcomplexType = ((XSDComplexTypeDefinition) element.getTypeDefinition()).getContent();
                if (fromcomplexType instanceof XSDParticle) {

                    XSDParticle particle = (XSDParticle) fromcomplexType;

                    if (particle.getTerm() instanceof XSDModelGroup) {

                        XSDModelGroup modelGroup = ((XSDModelGroup) particle.getTerm());
                        EList<XSDParticle> fromlist = modelGroup.getContents();

                        for (XSDParticle el : fromlist.toArray(new XSDParticle[fromlist.size()])) {
                            XSDTerm term = el.getTerm();
                            if (term instanceof XSDElementDeclaration) {
                                if (isReferrenced(element, (XSDElementDeclaration) term)) {
                                    continue;
                                }

                                getforeignKeyOfElement(list, (XSDElementDeclaration) term);
                            }
                        }
                    }
                }
            }
        }

    }

    private static boolean isReferrenced(XSDElementDeclaration element, XSDElementDeclaration term) {
        if (element == term) {
            return true;
        }
        if (term.getTypeDefinition() instanceof XSDComplexTypeDefinition) {
            XSDComplexTypeContent fromcomplexType = ((XSDComplexTypeDefinition) term.getTypeDefinition()).getContent();
            if (fromcomplexType instanceof XSDParticle) {
                XSDParticle particle = (XSDParticle) fromcomplexType;
                if (particle.getTerm() instanceof XSDModelGroup) {
                    XSDModelGroup modelGroup = ((XSDModelGroup) particle.getTerm());
                    EList<?> fromlist = modelGroup.getContents();
                    for (Object obj : fromlist) {
                        XSDTerm t = ((XSDParticle) obj).getTerm();
                        if (t == element) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // keep all foreignkeys in the memory to improve performance
    static Set<String> foreignKeys;

    public static Set<String> getForeignKeys() {
        return foreignKeys;
    }

    public static void setForeignKeys(Set<String> foreignKeys) {
        Util.foreignKeys = foreignKeys;
    }

    /**
     * set the list with all the foreign concepty name in the parent
     * 
     * @author ymli
     * @param list
     * @param parent
     * @throws Exception
     */
    public static void getForeingKeyInDataModel(Set<String> list, TreeParent parent, TMDMService service) throws Exception {
        TreeObject[] children = parent.getChildren();
        for (TreeObject object : children) {
            if (object instanceof TreeParent) {
                getForeingKeyInDataModel(list, (TreeParent) object, service);
                continue;
            }
            WSDataModel wsDataModel = service.getDataModel(new WSGetDataModel(new WSDataModelPK(object.getDisplayName())));
            XSDSchema xsd = Util.createXsdSchema(wsDataModel.getXsdSchema(), object);
            getForeingKeyInSchema(list, xsd);
        }
    }

    /**
     * set the list with foreign concept names in the schema
     * 
     * @author ymli
     * @param list
     * @param schema
     * @return
     */
    public static Set<String> getForeingKeyInSchema(Set<String> list, XSDSchema schema) {
        EList<XSDSchemaContent> contents = schema.getContents();
        for (XSDSchemaContent content : contents) {
            if (content instanceof XSDElementDeclaration) {
                getforeignKeyOfElement(list, (XSDElementDeclaration) content);
            }
        }

        return list;
    }

    /**
     * the all the typeDefinition in the schema
     * 
     * @author ymli
     * @param schema
     * @return
     */
    public static Map<String, XSDTypeDefinition> getTypeDefinition(XSDSchema schema) {
        HashMap<String, XSDTypeDefinition> map = new HashMap<String, XSDTypeDefinition>();
        for (XSDSchemaContent content : schema.getContents()) {
            if (content instanceof XSDTypeDefinition) {
                map.put(((XSDTypeDefinition) content).getName(), (XSDTypeDefinition) content);
            }
        }
        return map;
    }

    public static Object getParent(Object son) {
        if (!((son instanceof XSDElementDeclaration) || (son instanceof XSDParticle))) {
            return null;
        }

        XSDElementDeclaration elem = null;
        if (son instanceof XSDParticle) {
            elem = (XSDElementDeclaration) ((XSDParticle) son).getContent();
        } else if (son instanceof XSDElementDeclaration) {
            elem = (XSDElementDeclaration) son;
        }
        if (elem == null || elem.getSchema() == null) {
            return null;
        }
        EList<XSDSchemaContent> parentList = elem.getSchema().getContents();
        for (XSDSchemaContent top : parentList) {
            if (!(top instanceof XSDElementDeclaration) && !(top instanceof XSDComplexTypeDefinition)) {
                continue;
            }
            if (top instanceof XSDElementDeclaration) {
                XSDElementDeclaration decl = (XSDElementDeclaration) top;
                if (decl == son) {
                    return decl;
                }
                if (decl.getTypeDefinition() instanceof XSDComplexTypeDefinition) {
                    XSDComplexTypeDefinition type = (XSDComplexTypeDefinition) decl.getTypeDefinition();
                    if (type.getContent() instanceof XSDParticle) {
                        XSDParticle particle = (XSDParticle) type.getContent();
                        if (particle.getTerm() instanceof XSDModelGroup) {
                            XSDModelGroup group = (XSDModelGroup) particle.getTerm();
                            EList<XSDParticle> elist = group.getContents();
                            for (XSDParticle pt : elist) {
                                if (pt.getContent() instanceof XSDElementDeclaration) {
                                    if (((XSDElementDeclaration) pt.getContent()) == elem) {
                                        return decl;
                                    }
                                }
                                Set<XSDConcreteComponent> complexTypes = new HashSet<XSDConcreteComponent>();
                                XSDElementDeclaration spec = findOutSpecialSonElement((XSDElementDeclaration) pt.getContent(),
                                        elem, complexTypes);
                                if (spec != null) {
                                    return spec;
                                }
                            }
                        }
                    }
                }
            } else {
                XSDComplexTypeDefinition type = (XSDComplexTypeDefinition) top;
                if (type.getContent() instanceof XSDParticle) {
                    XSDParticle particle = (XSDParticle) type.getContent();
                    if (particle.getTerm() instanceof XSDModelGroup) {
                        XSDModelGroup group = (XSDModelGroup) particle.getTerm();
                        EList<XSDParticle> elist = group.getContents();
                        for (XSDParticle pt : elist) {
                            if (pt.getContent() instanceof XSDElementDeclaration) {
                                if (((XSDElementDeclaration) pt.getContent()) == elem) {
                                    return top;
                                }
                            }
                            if (pt.getContent() instanceof XSDElementDeclaration) {
                                Set<XSDConcreteComponent> complexTypes = new HashSet<XSDConcreteComponent>();
                                XSDElementDeclaration spec = findOutSpecialSonElement((XSDElementDeclaration) pt.getContent(),
                                        elem, complexTypes);
                                if (spec != null) {
                                    return spec;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static List<Object> getTopParent(Object son) {
        if (!((son instanceof XSDElementDeclaration))) {
            return null;
        }

        XSDElementDeclaration elem = null;

        elem = (XSDElementDeclaration) son;
        if (elem.getSchema() == null) {
            return null;
        }
        EList<XSDSchemaContent> parentList = elem.getSchema().getContents();
        List<Object> list = new ArrayList<Object>();
        for (XSDSchemaContent top : parentList) {
            list.clear();
            if (!(top instanceof XSDElementDeclaration)) {
                continue;
            }

            XSDElementDeclaration decl = (XSDElementDeclaration) top;
            if (decl.getTypeDefinition() instanceof XSDComplexTypeDefinition) {

                String primaryKey = getTopElement(decl, elem);
                if (!"".equalsIgnoreCase(primaryKey)) {//$NON-NLS-1$
                    EList<XSDIdentityConstraintDefinition> idtylist = decl.getIdentityConstraintDefinitions();
                    for (XSDIdentityConstraintDefinition idty : idtylist) {
                        EList<XSDXPathDefinition> fields = idty.getFields();
                        for (XSDXPathDefinition path : fields) {
                            if ((path.getValue()).equals(primaryKey)) {
                                list.add(idty);
                                list.add(path);
                                return list;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String getTopElement(XSDElementDeclaration parent, XSDElementDeclaration son) {
        XSDTypeDefinition type = parent.getTypeDefinition();
        if (!(type instanceof XSDComplexTypeDefinition)) {
            return null;
        }

        List<XSDComplexTypeDefinition> hierarchyComplexTypes = getAllSuperComplexTypes(
                (XSDComplexTypeDefinition) type);

        for (XSDComplexTypeDefinition complexType : hierarchyComplexTypes) {
            if (complexType.getContent() instanceof XSDParticle) {
                XSDParticle particle = (XSDParticle) complexType.getContent();
                if (particle.getTerm() instanceof XSDModelGroup) {
                    XSDModelGroup group = (XSDModelGroup) particle.getTerm();
                    EList<XSDParticle> elist = group.getContents();
                    for (XSDParticle pt : elist) {
                        if (pt.getContent() instanceof XSDElementDeclaration) {
                            XSDElementDeclaration ele = (XSDElementDeclaration) pt.getContent();
                            if (ele == son) {
                                return ele.getName();
                                /*
                                 * ArrayList<String> complexTypes = new ArrayList<String>(); XSDElementDeclaration spec
                                 * = findOutSpecialSonElement( (XSDElementDeclaration) pt.getContent(), son,
                                 * complexTypes); if (spec != null) return spec.getName();
                                 */
                                // if (ele.getTypeDefinition() instanceof XSDComplexTypeDefinition) {
                                //
                                // return ele.getName() + "/"//$NON-NLS-1$
                                // + getTopElement(ele, son, (XSDComplexTypeDefinition) ele.getTypeDefinition());
                                //
                                //
                                // }
                            }

                        }
                    }
                }
            }
        }

        return "";//$NON-NLS-1$
    }

    /**
     * contains childType itself
     */
    public static List<XSDComplexTypeDefinition> getAllSuperComplexTypes(XSDComplexTypeDefinition childType) {
        if (childType == null) {
            return new ArrayList<XSDComplexTypeDefinition>();
        }

        XSDTypeDefinition rootType = childType.getRootType();
        XSDTypeDefinition typeDefinition = childType;

        List<XSDComplexTypeDefinition> hierarchyComplexTypes = new ArrayList<XSDComplexTypeDefinition>();
        while (typeDefinition != null && typeDefinition instanceof XSDComplexTypeDefinition) {
            hierarchyComplexTypes.add((XSDComplexTypeDefinition) typeDefinition);

            if (typeDefinition.equals(rootType)) {
                break;
            }
            typeDefinition = typeDefinition.getBaseType();
        }

        return hierarchyComplexTypes;
    }

    public static List<Object> getRealKeyInfos(XSDElementDeclaration currentEntity, XSDParticle son) {
        if (currentEntity == null || son == null) {
            return null;
        }

        if (!isDirectChild(currentEntity, son)) {
            return null;
        }

        List<Object> list = new ArrayList<Object>();

        XSDTerm term = son.getTerm();
        if (term instanceof XSDElementDeclaration) {
            String primaryKey = ((XSDElementDeclaration) term).getName();
            EList<XSDIdentityConstraintDefinition> idtylist = currentEntity.getIdentityConstraintDefinitions();
            for (XSDIdentityConstraintDefinition idty : idtylist) {
                EList<XSDXPathDefinition> fields = idty.getFields();
                for (XSDXPathDefinition path : fields) {
                    if ((path.getValue()).equals(primaryKey)) {
                        list.add(idty);
                        list.add(path);
                        break;
                    }
                }
            }
        }

        return list;
    }

    private static boolean isDirectChild(XSDElementDeclaration entity, XSDParticle child) {
        if (entity == null || child == null) {
            return false;
        }

        XSDComplexTypeDefinition ctypeDefinition = (XSDComplexTypeDefinition) entity.getTypeDefinition();
        try {
            Map<String, XSDParticle> childElements = getChildElements(null, ctypeDefinition, true, new HashSet<Object>());
            if (childElements.values().contains(child)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    private static XSDElementDeclaration findOutSpecialSonElement(XSDElementDeclaration parent, XSDElementDeclaration son,
            Set<XSDConcreteComponent> complexTypes) {
        List<XSDElementDeclaration> particleElemList = findOutAllSonElements(parent, complexTypes);
        XSDElementDeclaration specialParent = null;
        for (XSDElementDeclaration e : particleElemList) {
            if (e == son) {
                specialParent = parent;
                break;
            }
        }

        if (specialParent == null) {
            for (XSDElementDeclaration e : particleElemList) {
                specialParent = findOutSpecialSonElement(e, son, complexTypes);
                if (specialParent != null) {
                    break;
                }
            }
        }

        return specialParent;
    }

    private static List<XSDElementDeclaration> findOutAllSonElements(XSDElementDeclaration decl,
            Set<XSDConcreteComponent> complexTypes) {
        List<XSDElementDeclaration> holder = new ArrayList<XSDElementDeclaration>();
        if (decl.getTypeDefinition() instanceof XSDComplexTypeDefinition) {
            XSDComplexTypeDefinition type = (XSDComplexTypeDefinition) decl.getTypeDefinition();
            if (complexTypes.contains(type)) {
                return holder;
            }
            if (type.getContent() instanceof XSDParticle) {
                XSDParticle particle = (XSDParticle) type.getContent();
                if (particle.getTerm() instanceof XSDModelGroup) {
                    XSDModelGroup group = (XSDModelGroup) particle.getTerm();
                    EList<XSDParticle> elist = group.getContents();
                    boolean addComplexType = false;
                    for (XSDParticle pt : elist) {
                        if (pt.getContent() instanceof XSDElementDeclaration) {
                            XSDElementDeclaration elem = (XSDElementDeclaration) pt.getContent();
                            if (!addComplexType) {
                                complexTypes.add(type);
                                addComplexType = true;
                            }
                            if (StringUtils.equals(elem.getName(), decl.getName())) {
                                if (ObjectUtils.equals(elem.getType(), decl.getType())) {
                                    if (StringUtils.equals(elem.getTargetNamespace(), decl.getTargetNamespace())) {
                                        continue;
                                    }
                                }
                            }
                            holder.add(elem);
                        }
                    }
                }
            }
        }

        return holder;
    }

    public static boolean checkConcept(XSDElementDeclaration decl) {
        boolean isConcept = false;
        EList<XSDIdentityConstraintDefinition> list = decl.getIdentityConstraintDefinitions();
        for (XSDIdentityConstraintDefinition icd : list) {
            if (icd.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL)) {
                isConcept = true;
                break;
            }
        }
        return isConcept;
    }

    public static Object[] getAllObject(Object elem, List<Object> objList, IStructuredContentProvider provider) {
        Object[] elems = provider.getElements(elem);
        for (Object obj : elems) {
            if (obj == null) {
                continue;
            }
            if (obj instanceof XSDModelGroup || obj instanceof XSDElementDeclaration || obj instanceof XSDParticle
                    || obj instanceof XSDTypeDefinition) {
                if (!objList.contains(obj)) {
                    objList.add(obj);

                    getAllObject(obj, objList, provider);
                }
            }
        }

        return objList.toArray();

    }

    /**
     * update reference to newType
     * 
     * @param elem
     * @param newType
     * @param provider
     */
    public static void updateReferenceToXSDTypeDefinition(Object elem, XSDTypeDefinition newType,
            IStructuredContentProvider provider) {
        if (newType instanceof XSDComplexTypeDefinition) {
            updateChildrenReferenceToComplexType((XSDComplexTypeDefinition) newType);
        }

        List<Object> objList = new ArrayList<Object>();
        Object[] allNodes = getAllObject(elem, objList, provider);
        for (Object node : allNodes) {
            if (node instanceof XSDElementDeclaration) {
                XSDElementDeclaration xsdElem = (XSDElementDeclaration) node;
                if (xsdElem.getTypeDefinition() == newType) {
                    xsdElem.setTypeDefinition(newType);
                }
            } else if (node instanceof XSDParticle) {
                XSDParticle particle = (XSDParticle) node;
                if (particle.getTerm() instanceof XSDModelGroup) {
                    XSDModelGroup group = (XSDModelGroup) particle.getTerm();
                    EList<XSDParticle> elist = group.getContents();
                    for (XSDParticle pt : elist) {
                        if (pt.getContent() instanceof XSDElementDeclaration) {
                            if (((XSDElementDeclaration) pt.getContent()).getTypeDefinition() == newType) {
                                ((XSDElementDeclaration) pt.getContent()).setTypeDefinition(newType);
                            }
                        }

                    }
                } else if (particle.getTerm() instanceof XSDElementDeclaration) {
                    XSDElementDeclaration xsdElem = (XSDElementDeclaration) particle.getTerm();
                    if (xsdElem.getTypeDefinition() == newType) {
                        xsdElem.setTypeDefinition(newType);
                    }
                }
            }
        }

    }

    private static void updatePrimaryKeyInfo(XSDElementDeclaration decl, String oldValue, String newValue) {
        XSDAnnotationsStructure struc = new XSDAnnotationsStructure(decl);
        Collection<String> primaryKeyInfos = struc.getPrimaryKeyInfos().values();
        if (primaryKeyInfos != null && primaryKeyInfos.size() > 0) {
            List<String> newInfos = new LinkedList<String>();
            for (String info : primaryKeyInfos) {
                if (info.startsWith(oldValue)) {
                    info = info.replaceFirst(oldValue, newValue);
                    newInfos.add(info);
                }
            }
            if (newInfos.size() > 0) {
                struc.setPrimaryKeyInfos(newInfos);
            }
        }
    }

    public static void updateReference(Object decl, Object[] objs, Object[] allForeignKeyAndInfos, String oldValue,
            String newValue) {
        if (!(decl instanceof XSDElementDeclaration)) {
            return;
        }

        updatePrimaryKeyInfo((XSDElementDeclaration) decl, oldValue, newValue);
        updateForeignKeyRelatedInfo(oldValue, newValue, allForeignKeyAndInfos);

        for (Object obj : objs) {
            if (obj instanceof XSDParticle) {
                XSDTerm term = ((XSDParticle) obj).getTerm();

                if (term instanceof XSDElementDeclaration) {
                    XSDElementDeclaration xsdElem = (XSDElementDeclaration) term;
                    if (xsdElem == decl) {
                        ((XSDParticle) obj).setTerm((XSDElementDeclaration) decl);
                        ((XSDParticle) obj).updateElement();
                    }
                }
                if (!(((XSDParticle) obj).getContent() instanceof XSDElementDeclaration)) {
                    continue;
                }
                XSDElementDeclaration elem = (XSDElementDeclaration) ((XSDParticle) obj).getContent();
                if (elem.isElementDeclarationReference()) {
                    if (elem.getResolvedElementDeclaration() == decl) {
                        elem.setResolvedElementDeclaration((XSDElementDeclaration) decl);
                    }
                }
            }
        }

    }

    public static void updateForeignKeyRelatedInfo(String oldValue, String newValue, Object[] allForeignKeyAndInfos) {
        if (allForeignKeyAndInfos == null || allForeignKeyAndInfos.length == 0) {
            return;
        }

        for (Object obj : allForeignKeyAndInfos) {
            Element e = (Element) obj;
            String nodeValue = e.getChildNodes().item(0).getNodeValue();

            String source = e.getAttribute("source");//$NON-NLS-1$
            if (source.equals("X_ForeignKey_Filter")) { //$NON-NLS-1$
                StringBuilder build = new StringBuilder();
                String[] splits = nodeValue.split(FKFilterParser.endSeparator);
                for (String str : splits) {
                    if (!str.trim().isEmpty() && str.startsWith(oldValue)) {
                        str = str.replaceFirst(oldValue, newValue);
                    }
                    build.append(str).append(FKFilterParser.endSeparator);
                }
                e.getChildNodes().item(0).setNodeValue(build.toString());

            } else {
                if (nodeValue.equals(oldValue)) {
                    e.getChildNodes().item(0).setNodeValue(newValue);
                }
            }

        }

    }

    public static void collectElementPaths(IStructuredContentProvider provider, Object input, XSDParticle toSearch,
            Set<String> paths, String inputPath) {
        if (input == null || paths == null || provider == null) {
            return;
        }
        Object[] elems = provider.getElements(input);
        if (elems != null && elems.length > 0) {
            for (Object obj : elems) {
                if (obj == null) {
                    continue;
                }
                String curPath = inputPath;
                if (obj instanceof XSDElementDeclaration) {
                    String name = ((XSDElementDeclaration) obj).getName();
                    if (curPath == null) {
                        curPath = name;
                    } else {
                        curPath += "/" + name; //$NON-NLS-1$
                    }
                }
                if (obj instanceof XSDParticle) {
                    XSDParticleContent content = ((XSDParticle) obj).getContent();
                    if (content instanceof XSDElementDeclaration) {
                        String name = ((XSDElementDeclaration) content).getName();
                        curPath += "/" + name; //$NON-NLS-1$
                    }
                    if (obj == toSearch) {
                        paths.add(curPath);
                        break;
                    }
                }
                collectElementPaths(provider, obj, toSearch, paths, curPath);

            }
        }
    }

    /**
     * return {fk,fk info,fk filter} element
     */
    public static Object[] getAllForeignKeyRelatedInfos(Object elem, List<Object> objList, IStructuredContentProvider provider,
            final Set<Object> visited) {
        if (elem == null || objList == null || visited == null || provider == null) {
            return null;
        }

        visited.add(elem);

        Object[] elems = provider.getElements(elem);
        if (elems != null && elems.length > 0) {
            for (Object obj : elems) {
                if (obj == null) {
                    continue;
                }

                if (obj instanceof Element) {
                    Element e = (Element) obj;
                    if (e.getLocalName().equals("appinfo")) {//$NON-NLS-1$
                        String source = e.getAttribute("source");//$NON-NLS-1$
                        if (source != null && !objList.contains(e)) {
                            if (source.equals("X_ForeignKey") || source.equals("X_ForeignKeyInfo") //$NON-NLS-1$ //$NON-NLS-2$
                                    || source.equals("X_ForeignKey_Filter")) {//$NON-NLS-1$
                                objList.add(e);
                            }
                        }
                    }
                } else {
                    if (!visited.contains(obj)) {
                        getAllForeignKeyRelatedInfos(obj, objList, provider, visited);
                    }
                }
            }
        }

        return objList.toArray();
    }

    public static boolean isReferencedBy(Object decl, Object[] objs) {
        for (Object obj : objs) {
            if (obj instanceof XSDParticle) {
                XSDTerm term = ((XSDParticle) obj).getTerm();

                if (term instanceof XSDElementDeclaration) {
                    XSDElementDeclaration xsdElem = (XSDElementDeclaration) term;
                    if (xsdElem == decl) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static void deleteReference(Object decl, Object[] objs) {
        for (Object obj : objs) {
            if (obj instanceof XSDParticle) {
                XSDTerm term = ((XSDParticle) obj).getTerm();

                if (term instanceof XSDElementDeclaration) {
                    XSDElementDeclaration xsdElem = (XSDElementDeclaration) term;
                    if (xsdElem == decl) {
                        XSDModelGroup group = (XSDModelGroup) ((XSDParticle) obj).getContainer();
                        if (group != null) {
                            group.getContents().remove(obj);
                        }
                    }
                }
            }
        }

    }

    public static XSDElementDeclaration findReference(String refName, XSDSchema schema) {
        if (refName != null && schema != null) {
            EList<XSDElementDeclaration> eDecls = schema.getElementDeclarations();
            if (refName.indexOf(" : ") != -1) {//$NON-NLS-1$
                refName = refName.substring(0, refName.indexOf(" : "));//$NON-NLS-1$
            }
            for (XSDElementDeclaration d : eDecls) {
                if (d.getQName().equals(refName)) {
                    return d;
                }
            }
        }

        return null;
    }

    private static Clipboard clipboard = null;

    /**
     * Clipboard support
     * 
     * @return the Clipboard
     */
    public static Clipboard getClipboard() {
        if (clipboard == null) {
            clipboard = new Clipboard(Display.getCurrent());
        }
        return clipboard;
    }

    public static Version getVersion(TreeObject xobject) throws XtentisException {
        try {
            WSVersion version = getMDMService(xobject)
                    .getComponentVersion(new WSGetComponentVersion(WSComponent.DATA_MANAGER, null));
            return new Version(version.getMajor(), version.getMinor(), version.getRevision(), version.getBuild());
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            throw new XtentisException(Messages.Util_27 + e.getClass().getName() + Messages.Util_28 + e.getMessage());
        }
    }

    /**
     * @deprecated using getChildren(TreeParent xObject,int objectType)
     * @param xObject
     * @param objectType
     * @return
     */
    @Deprecated
    public static List<String> getCachedXObjectsNameSet(TreeObject xObject, int objectType) {
        List<String> xObjectsNameSet = new ArrayList<String>();
        if (xObject != null) {

            TreeParent treeNode = xObject.findServerFolder(objectType);
            TreeObject[] xObjectsSet = treeNode.getChildren();
            for (TreeObject element : xObjectsSet) {
                xObject = element;
                String xObjectName = xObject.getDisplayName();
                xObjectsNameSet.add(xObjectName);
            }

        }
        return xObjectsNameSet;
    }

    public static List<String> getChildren(TreeParent xObject, int objectType) {
        List<String> objs = new ArrayList<String>();
        for (TreeObject obj : xObject.getChildren()) {
            if (obj instanceof TreeParent) {
                TreeParent parent = (TreeParent) obj;
                objs.addAll(getChildren(parent, objectType));
            } else {
                if (obj.getType() == objectType) {
                    objs.add(obj.getDisplayName());
                }
            }
        }
        return objs;
    }

    // Modified by hbhong,to fix bug 21784
    public static TreeParent getServerTreeParent(TreeObject tObj) {
        TreeParent serverRoot = tObj.getServerRoot();
        if (serverRoot == null) {
            return getTreeParent(tObj, TreeObject._SERVER_);
        }
        return serverRoot;
    }

    private static TreeParent getTreeParent(TreeObject tObj, int type) {
        if (tObj == null) {
            throw new IllegalArgumentException(Messages.Util_29);
        }
        if (tObj instanceof TreeParent) {
            if (tObj.getType() == type) {
                return (TreeParent) tObj;
            }
        }
        TreeParent parent = tObj.getParent();
        if (parent != null) {
            return getTreeParent(parent, type);
        }
        return null;
    }

    // The ending| bug:21784
    public static List<String> getDataModel(TreeObject obj, String datamodel, String conceptName) {
        List<String> systemDataModelValues = Util.getChildren(obj.getServerRoot(), TreeObject.DATA_MODEL);

        // filter the datamodel according to conceptName
        List<String> avaiList = new ArrayList<String>();
        avaiList.addAll(systemDataModelValues);
        if (datamodel != null) {
            avaiList.clear();
            avaiList.add(datamodel);
        } else if (conceptName != null && !conceptName.contains("*")) {//$NON-NLS-1$
            for (String data : systemDataModelValues) {
                try {
                    WSDataModel dm = Util.getMDMService(obj).getDataModel(new WSGetDataModel(new WSDataModelPK(data)));
                    if (dm != null) {
                        // XSDSchema xsdSchema = Util.getXSDSchema(dm.getXsdSchema());
                        String schema = dm.getXsdSchema();
                        Pattern p = Pattern.compile("<xsd:element(.*?)name=\"" + conceptName + "\"");//$NON-NLS-1$//$NON-NLS-2$
                        if (!p.matcher(schema).find()) {
                            avaiList.remove(data);
                        }
                    }
                } catch (Exception e1) {
                    log.error(e1.getMessage(), e1);
                }
            }
        }
        if (avaiList.size() == 0) {
            avaiList.addAll(systemDataModelValues);
        }
        return avaiList;
    }

    // modify this to resolve the bug 21723,by jsxie
    public static List<TreeObject> getChildrenObj(TreeParent xObject) {
        List<TreeObject> objs = new ArrayList<TreeObject>();

        if (xObject.getChildren() != null) {
            for (TreeObject obj : xObject.getChildren()) {
                if (obj instanceof TreeParent) {
                    TreeParent parent = (TreeParent) obj;
                    if (parent != null) {
                        // system folder
                        if (obj.getType() == TreeObject.CATEGORY_FOLDER) {
                            // Data container
                            if (obj.getParent().getType() == TreeObject.DATA_CLUSTER) {
                                for (TreeObject chld : ((TreeParent) obj).getChildren()) {
                                    if (chld.getName().equals(XSystemObjects.DC_PROVISIONING.getName())) {
                                        objs.add(chld);
                                    }
                                    if (chld.getName().equals(XSystemObjects.DC_CONF.getName())) {
                                        objs.add(chld);
                                    }
                                }
                                continue;
                            } else {
                                continue;
                            }
                        }

                        if (obj.getType() == TreeObject.RESOURCES) {
                            for (TreeObject child : ((TreeParent) obj).getChildren()) {
                                if (child.getType() == TreeObject.PICTURES_RESOURCE) {
                                    objs.add(child);
                                }
                            }
                            continue;
                        }
                        objs.addAll(getChildrenObj(parent));

                    }

                } else {
                    if (obj.isXObject()) {
                        objs.add(obj);
                    }
                }
            }
        }
        return objs;
    }

    public static List<String> getChildElementNames(String parentxpath, XSDElementDeclaration decl) throws Exception {
        List<String> childNames = new ArrayList<String>();

        Map<String, XSDParticle> childElements = getChildElements(parentxpath, decl, false, new HashSet<Object>());
        Iterator<String> iterator = childElements.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.indexOf("//") == -1) { //$NON-NLS-1$
                childNames.add(key);
            }
        }

        return childNames;
    }

    /**
     * onlyTopLevel indicates whether return decl's direct subelements(true) or all the subelements(false).
     */
    public static Map<String, XSDParticle> getChildElements(String parentxpath, XSDElementDeclaration decl, boolean onlyTopLevel,
            final Set<Object> visited) throws Exception {
        Map<String, XSDParticle> childElements = new HashMap<String, XSDParticle>();

        XSDTypeDefinition baseType = decl.getTypeDefinition();

        if (baseType instanceof XSDComplexTypeDefinition) {
            XSDComplexTypeDefinition cmpType = (XSDComplexTypeDefinition) baseType;
            Map<String, XSDParticle> childs = getChildElements(parentxpath, cmpType, onlyTopLevel, visited);
            for (String xsdDecl : childs.keySet()) {
                if (!childElements.containsKey(xsdDecl)) {
                    childElements.put(xsdDecl, childs.get(xsdDecl));
                }
            }
        }

        return childElements;
    }

    public static Map<String, XSDParticle> getChildElements(String parentxpath, XSDComplexTypeDefinition ctype,
            boolean onlyTopLevel, final Set<Object> visited) throws Exception {
        if (visited == null || ctype == null) {
            throw new IllegalArgumentException();
        }

        if (parentxpath == null) {
            parentxpath = ""; //$NON-NLS-1$
        }

        Map<String, XSDParticle> childElements = new HashMap<String, XSDParticle>();

        XSDTypeDefinition baseType = ctype.getBaseType();
        if (!visited.contains(ctype)) {
            visited.add(ctype);

            if (baseType instanceof XSDComplexTypeDefinition && baseType != ctype) {
                XSDComplexTypeDefinition cmpType = (XSDComplexTypeDefinition) baseType;
                childElements.putAll(getChildElements(parentxpath, cmpType, onlyTopLevel, visited));
            }

            childElements.putAll(getComplexChilds(parentxpath, ctype, onlyTopLevel, visited));
        }

        return childElements;
    }

    /**
     * use the map from path to XSDParticle,path is separated by '/' or '//'
     */
    private static Map<String, XSDParticle> getComplexChilds(String parentxpath, XSDComplexTypeDefinition ctype,
            boolean onlyTopLevel, final Set<Object> visited) throws Exception {
        Map<String, XSDParticle> childDecls = new HashMap<String, XSDParticle>();

        if (ctype.getContent() instanceof XSDParticle) {
            XSDParticleImpl particle = (XSDParticleImpl) ctype.getContent();
            if (particle.getTerm() instanceof XSDModelGroup) {
                XSDModelGroup group = (XSDModelGroup) particle.getTerm();
                EList<XSDParticle> particles = group.getParticles();
                for (XSDParticle part : particles) {
                    if (part.getTerm() instanceof XSDElementDeclaration) {
                        XSDElementDeclaration el = (XSDElementDeclaration) part.getTerm();
                        if (el.getTypeDefinition() instanceof XSDSimpleTypeDefinition) {
                            String child = parentxpath.length() == 0 ? el.getName() : parentxpath + "/" + el.getName();//$NON-NLS-1$
                            childDecls.put(child, part);
                        } else {
                            String complexTypeChildPath = parentxpath.length() == 0 ? "//" + el.getName() : parentxpath //$NON-NLS-1$
                                    + "//" + el.getName();//$NON-NLS-1$
                            childDecls.put(complexTypeChildPath, part);
                            if (!onlyTopLevel && el.getTypeDefinition() instanceof XSDComplexTypeDefinition) {
                                String child = parentxpath.length() == 0 ? el.getName() : parentxpath + "/" + el.getName();//$NON-NLS-1$
                                childDecls.putAll(getChildElements(child, (XSDComplexTypeDefinition) el.getTypeDefinition(),
                                        onlyTopLevel, visited));
                            }
                        }
                    }
                }
            }
        }
        return childDecls;
    }

    public static List<String> getChildElementNames(XSDSchema schema, String concept) throws Exception {
        List<String> childNames = new ArrayList<String>();
        EList<XSDElementDeclaration> xsdElementDeclarations = schema.getElementDeclarations();
        XSDElementDeclaration conceptEl = null;
        for (XSDElementDeclaration el : xsdElementDeclarations
                .toArray(new XSDElementDeclaration[xsdElementDeclarations.size()])) {
            if (el.getName().equals(concept)) {
                conceptEl = el;
                break;
            }
        }
        if (conceptEl != null) {
            childNames.addAll(getChildElementNames(conceptEl.getName(), conceptEl));
        }
        return childNames;
    }

    public static IStatus changeElementTypeToSequence(XSDElementDeclaration decl, int maxOccurs) {
        if (maxOccurs < -1) {
            MessageDialog.openError(null, Messages.Util_30, Messages.Util_31);
            return Status.CANCEL_STATUS;
        }
        if (Util.getParent(decl) instanceof XSDElementDeclaration) {
            XSDElementDeclaration parent = (XSDElementDeclaration) Util.getParent(decl);

            return doChangeElementTypeToSequence((XSDComplexTypeDefinition) parent.getTypeDefinition(), maxOccurs);

        } else {
            if (Util.getParent(decl) instanceof XSDComplexTypeDefinition) {
                return doChangeElementTypeToSequence((XSDComplexTypeDefinition) Util.getParent(decl), maxOccurs);
            }
        }
        return Status.OK_STATUS;
    }

    private static IStatus doChangeElementTypeToSequence(XSDComplexTypeDefinition compx, int maxOccurs) {

        XSDParticleImpl partCnt = (XSDParticleImpl) compx.getContent();
        XSDModelGroupImpl mdlGrp = (XSDModelGroupImpl) partCnt.getTerm();
        if ((maxOccurs > 1 || maxOccurs == -1) && mdlGrp.getCompositor() != XSDCompositor.SEQUENCE_LITERAL) {
            // change the parent element to xsd:sequence
            if (!MessageDialog.openConfirm(null, Messages.Util_32, Messages.Util_33)) {
                return Status.CANCEL_STATUS;
            }

            mdlGrp.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
            compx.updateElement();
        }

        return Status.OK_STATUS;
    }

    public static boolean IsAImporedElement(XSDConcreteComponent component, XSDSchema schema) {
        EObject parent = null;
        EObject obj = component;
        do {
            if (null == obj) {
                return false;
            }
            parent = obj.eContainer();
            obj = parent;
        } while (!(parent instanceof XSDSchema));
        if (parent != null && parent instanceof XSDSchema) {
            if (parent.equals(schema)) {
                return false;
            }
            try {
                EList<XSDSchemaDirective> referencingDirectives = ((XSDSchema) parent).getReferencingDirectives();
                return !referencingDirectives.isEmpty();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return false;
    }

    public static boolean IsAImporedElement(XSDConcreteComponent component, String schema) {
        if (component == null) {
            return true;
        }
        String componentName = getComponentName(component);
        try {
            String xsd = schema;
            if (xsd.indexOf(componentName) != -1) {
                return false;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error(e.getMessage(), e);
        }
        return true;
    }

    public static String getComponentName(Object component) {
        if (component instanceof XSDElementDeclaration) {
            XSDElementDeclaration decl = (XSDElementDeclaration) component;
            return "name=\"" + decl.getName() + "\"";

        } else if (component instanceof XSDParticle) {
            XSDParticle particle = (XSDParticle) component;
            XSDTerm term = particle.getTerm();
            if (term instanceof XSDElementDeclaration) {
                return "name=\"" + ((XSDElementDeclaration) term).getName() + "\"";
            }

        } else if (component instanceof XSDComplexTypeDefinition) {
            XSDComplexTypeDefinition type = (XSDComplexTypeDefinition) component;
            return "name=\"" + type.getName() + "\"";

        } else if (component instanceof XSDSimpleTypeDefinition) {

            return "name=\"" + ((XSDSimpleTypeDefinition) component).getName() + "\"";

        }

        else if (component instanceof XSDIdentityConstraintDefinition) {
            XSDIdentityConstraintDefinition identify = (XSDIdentityConstraintDefinition) component;
            return "name=\"" + identify.getName() + "\"";
        } else if (component instanceof XSDXPathDefinition) {
            XSDXPathDefinition path = (XSDXPathDefinition) component;
            return "xpath=\"" + path.getValue() + "\"";
        }
        return null;
    }

    public static List<String> retrieveXSDComponentPath(Object component, XSDSchema schema, List<String> buffer) {
        String name = null;
        if (component instanceof XSDElementDeclaration) {
            XSDElementDeclaration decl = (XSDElementDeclaration) component;
            name = decl.getName();
            buffer.add("//xsd:element[@name='" + name + "']");//$NON-NLS-1$//$NON-NLS-2$
            if (decl.getContainer() instanceof XSDSchemaImpl) {
                return buffer;
            } else {
                return retrieveXSDComponentPath(decl.getContainer(), schema, buffer);
            }
        } else if (component instanceof XSDParticle) {
            XSDParticle particle = (XSDParticle) component;
            XSDTerm term = particle.getTerm();
            if (term instanceof XSDElementDeclaration
                    && !(((XSDElementDeclaration) term).getContainer() instanceof XSDParticle)) {
                String prefix = null;
                String ns = ((XSDElementDeclaration) term).getTargetNamespace();
                Iterator<Map.Entry<String, String>> iter = schema.getQNamePrefixToNamespaceMap().entrySet().iterator();
                while (iter.hasNext() && ns != null) {
                    Map.Entry<String, String> entry = iter.next();
                    if (entry.getValue().equals(ns)) {
                        prefix = entry.getKey();
                    }
                }
                name = ((XSDElementDeclaration) term).getName();
                buffer.add(
                        "//xsd:element[@name='" + name + "' or @ref='" + (prefix != null ? (prefix + ":" + name) : name) + "']");//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
                return retrieveXSDComponentPath(particle.getContainer(), schema, buffer);
            } else {
                retrieveXSDComponentPath(particle.getContainer(), schema, buffer);
            }
        } else if (component instanceof XSDComplexTypeDefinition) {
            XSDComplexTypeDefinition type = (XSDComplexTypeDefinition) component;
            name = type.getName();
            buffer.add("//xsd:complexType" + (name != null ? "[@name='" + name + "']" : ""));//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
            if (type.getContainer() instanceof XSDSchemaImpl) {
                return buffer;
            }
            return retrieveXSDComponentPath(type.getContainer(), schema, buffer);
        } else if (component instanceof XSDSimpleTypeDefinition) {
            // TreePath tPath=((TreeSelection)selection).getPaths()[0];
            // Object elem = tPath.getSegment(0);
            // return retrieveXSDComponentPath(elem, schema, buffer, selection);
            String typeName = ((XSDSimpleTypeDefinition) component).getName();
            buffer.add("//xsd:simpleType[@name='" + typeName + "']");//$NON-NLS-1$//$NON-NLS-2$
            return buffer;
        } else if (component instanceof XSDModelGroup) {
            XSDModelGroup group = (XSDModelGroup) component;
            String literal = group.getCompositor().getLiteral();
            buffer.add("//xsd:" + literal);//$NON-NLS-1$
            return retrieveXSDComponentPath(group.getContainer(), schema, buffer);
        } else if (component instanceof XSDIdentityConstraintDefinition) {
            XSDIdentityConstraintDefinition identify = (XSDIdentityConstraintDefinition) component;
            XSDConcreteComponent c = identify.getContainer();
            buffer.add("//xsd:unique[@name='" + identify.getName() + "']");//$NON-NLS-1$//$NON-NLS-2$
            return retrieveXSDComponentPath(c, schema, buffer);
        } else if (component instanceof XSDXPathDefinition) {
            XSDXPathDefinition path = (XSDXPathDefinition) component;
            buffer.add("//xsd:field[@xpath='" + path.getValue() + "']");//$NON-NLS-1$//$NON-NLS-2$
            return retrieveXSDComponentPath(path.getContainer(), schema, buffer);
        } else if (component instanceof XSDAnnotation) {
            XSDAnnotation annon = (XSDAnnotation) component;
            buffer.add("//xsd:annotation");//$NON-NLS-1$
            return retrieveXSDComponentPath(annon.getContainer(), schema, buffer);
        } else {
            return buffer;
        }

        return buffer;
    }

    public static String getResponseFromURL(String url, TreeObject treeObj) throws Exception {
        InputStreamReader doc = null;
        try {
            Encoder encoder = Base64.getEncoder();
            StringBuffer buffer = new StringBuffer();
            String credentials = encoder.encodeToString((new String(treeObj.getServerRoot().getUsername() + ":"//$NON-NLS-1$
                    + treeObj.getServerRoot().getPassword()).getBytes()));

            URL urlCn = new URL(url);
            URLConnection conn = urlCn.openConnection();
            conn.setAllowUserInteraction(true);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization", "Basic " + credentials);//$NON-NLS-1$//$NON-NLS-2$
            conn.setRequestProperty("Expect", "100-continue");//$NON-NLS-1$//$NON-NLS-2$

            doc = new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(doc);
            String line = reader.readLine();
            while (line != null) {
                buffer.append(line);
                line = reader.readLine();
            }

            return buffer.toString();
        } finally {
            if (doc != null) {
                doc.close();
            }
        }
    }

    private static void addImport(XSDSchema xsdSchema, List<XSDImport> imports) {
        Map<String, String> nsMap = xsdSchema.getQNamePrefixToNamespaceMap();
        int imp = 0;
        for (XSDImport xsdImport : imports) {
            String ns = xsdImport.getNamespace();
            if (ns.equals("")) { //$NON-NLS-1$
                continue;
            }
            int last = ns.lastIndexOf("/");//$NON-NLS-1$
            if (!nsMap.containsValue(ns)) {
                if (ns.equals("http://www.w3.org/XML/1998/namespace")) {//$NON-NLS-1$
                    nsMap.put("xml", ns);//$NON-NLS-1$
                } else {
                    nsMap.put(ns.substring(last + 1).replaceAll("[\\W]", ""), ns);//$NON-NLS-1$//$NON-NLS-2$
                }
            }

            boolean exist = false;
            for (XSDSchemaContent cnt : xsdSchema.getContents()) {
                if (cnt instanceof XSDImportImpl) {
                    XSDImportImpl cntImpl = (XSDImportImpl) cnt;
                    if (cntImpl.getNamespace().equals(ns)) {
                        exist = true;
                        break;
                    }
                }
            }
            if (!exist) {
                xsdSchema.getContents().add(imp++, xsdImport);
            }
        }
        xsdSchema.updateElement();
    }

    public static XSDSchema createXsdSchema(String xsd, TreeObject treeObj) throws Exception {
        List<XSDImport> imports = new ArrayList<XSDImport>();
        List<Exception> exceptons = new ArrayList<Exception>();
        Map<String, Integer> schemaMonitor = new HashMap<String, Integer>();
        // no import or include using old getXsdschema()
        // if (Util.getNodeList(doc.getDocumentElement(),"//xsd:import").getLength() ==0
        // || Util.getNodeList(doc.getDocumentElement(),
        // "//xsd:include").getLength() ==0)
        // {
        // return Util.getXSDSchema(xsd);
        // }
        // replace the old roles with the new role scheme
        String xsdCpy = new String(xsd);
        for (Map.Entry<String, String> pair : ICoreConstants.rolesConvert.oldRoleToNewRoleMap.entrySet()) {
            xsdCpy = xsdCpy.replaceAll(pair.getKey().toString(), pair.getValue().toString());
        }
        XSDSchema xsdSchema = Util.getXSDSchema(null, xsdCpy, imports, treeObj, false, exceptons, schemaMonitor);
        if (exceptons.size() > 0) {
            throw exceptons.get(0);
        } else if (schemaMonitor.containsValue(new Integer(-1))) {
            throw new Exception(Messages.Util_44);
        }

        addImport(xsdSchema, imports);
        return xsdSchema;
    }

    private static void importSchema(XSDSchema xsdSchema, List<XSDImport> imports, Map<String, Integer> schemaMonitor) {
        EList<XSDSchemaContent> list = xsdSchema.getContents();
        for (XSDSchemaContent schemaCnt : list) {
            if (schemaCnt instanceof XSDImport) {
                XSDImportImpl xsdImpl = (XSDImportImpl) schemaCnt;
                if (xsdImpl.getNamespace() == null || xsdImpl.getNamespace().trim().equals("")) {//$NON-NLS-1$
                    URI fileURI = URI.createFileURI(xsdImpl.getSchemaLocation());
                    xsdImpl.setNamespace(fileURI.toFileString().replaceAll("[\\W]", ""));//$NON-NLS-1$//$NON-NLS-2$
                }
                if (xsdImpl.getSchemaLocation() == null) {
                    continue;
                }
                ((XSDImportImpl) schemaCnt).importSchema();
                imports.add(((XSDImportImpl) schemaCnt));
            } else if (schemaCnt instanceof XSDInclude) {
                XSDIncludeImpl xsdInclude = (XSDIncludeImpl) schemaCnt;
                ((XSDSchemaImpl) xsdSchema).included(xsdInclude);
            }
        }
    }

    private static XSDSchema getXSDSchema(String namespaceURI, String rawData, final List<XSDImport> imports,
            final TreeObject treeObj, boolean uri, final List<Exception> exceptions, final Map<String, Integer> schemaMonitor)
                    throws Exception {
        FileInputStream fin = null;
        try {
            final String xsdFileName = System.getProperty("user.dir") + "/.xsdModel.xml";//$NON-NLS-1$//$NON-NLS-2$
            URI fileURI = URI.createFileURI(xsdFileName);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setValidating(false);
            DocumentBuilder documentBuilder;
            XSDSchema schema = null;
            InputSource source = null;
            Document document = null;
            String schemaLocation = rawData;
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            if (rawData == null) {
                return XSDSchemaImpl.getSchemaForSchema("http://www.w3.org/2001/XMLSchema");//$NON-NLS-1$
            }
            if (namespaceURI == null && rawData.endsWith(".xsd") && rawData.indexOf(File.separator) > 0) {//$NON-NLS-1$
                File rawFile = new File(rawData);
                if (!rawFile.exists()) {
                    throw new IllegalArgumentException(rawData);
                }
            }
            // import namespace="http://xxx" schemaLocation="xxxx"
            if (namespaceURI != null && schemaLocation.endsWith(".xsd")) {//$NON-NLS-1$
                URL url = new java.net.URI(namespaceURI + "/" + rawData).toURL();//$NON-NLS-1$
                uri = false;
                rawData = IOUtils.toString(url.openConnection().getInputStream());
                rawData = rawData.replaceAll("<!DOCTYPE(.*?)>", "");//$NON-NLS-1$//$NON-NLS-2$
            }
            if (rawData.equals("http://www.w3.org/2001/03/xml.xsd")) {//$NON-NLS-1$
                URL url = new java.net.URI("http://www.w3.org/2001/03/xml.xsd").toURL();//$NON-NLS-1$
                uri = false;
                rawData = IOUtils.toString(url.openConnection().getInputStream());
                rawData = rawData.replaceAll("<!DOCTYPE(.*?)>", "");//$NON-NLS-1$//$NON-NLS-2$
            }

            if (uri) {
                File file = new File(rawData);
                if (file.exists()) {
                    fin = new FileInputStream(file);
                    source = new InputSource(fin);
                } else {
                    source = new InputSource(new StringReader(Util.getResponseFromURL(rawData, treeObj)));
                }
            } else {
                source = new InputSource(new StringReader(rawData));
            }

            try {
                document = documentBuilder.parse(source);
            } catch (SAXParseException ex) {
                exceptions.add(ex);
                return null;
            }
            schema = XSDSchemaImpl.createSchema(document.getDocumentElement());

            ResourceSet resourceSet = new ResourceSetImpl();
            Resource resource = resourceSet.createResource(fileURI);
            resourceSet.getAdapterFactories().add(new AdapterFactoryImpl() {

                class SchemaLocator extends AdapterImpl implements XSDSchemaLocator {

                    public XSDSchema locateSchema(XSDSchema xsdSchema, String namespaceURI, String rawSchemaLocationURI,
                            String resolvedSchemaLocation) {
                        XSDSchema schema;
                        Integer rawCnt = schemaMonitor.get(rawSchemaLocationURI);
                        if (rawCnt == null) {
                            rawCnt = 0;
                        } else {
                            rawCnt++;
                        }
                        schemaMonitor.put(rawSchemaLocationURI, rawCnt);
                        if (rawCnt >= 10) {
                            schemaMonitor.put(rawSchemaLocationURI, -1);
                            return null;
                        }

                        try {
                            schema = Util.getXSDSchema(namespaceURI, rawSchemaLocationURI, imports, treeObj, true, exceptions,
                                    schemaMonitor);
                        } catch (Exception e) {
                            return XSDSchemaImpl.getSchemaForSchema(namespaceURI);
                        }
                        schema.setTargetNamespace(namespaceURI);
                        schema.setElement(schema.getDocument().getDocumentElement());
                        return schema;
                    }

                    public boolean isAdatperForType(Object type) {
                        return type == XSDSchemaLocator.class;
                    }
                }

                protected SchemaLocator schemaLocator = new SchemaLocator();

                @Override
                public boolean isFactoryForType(Object type) {
                    return type == XSDSchemaLocator.class;
                }

                @Override
                public Adapter adaptNew(Notifier target, Object type) {
                    return schemaLocator;
                }
            });
            // import namespace="http://xxx" schemaLocation="xxxx"
            if (namespaceURI != null && schemaLocation.endsWith(".xsd")) {//$NON-NLS-1$
                schema.setSchemaLocation(schemaLocation);
            } else {
                schema.setSchemaLocation(fileURI.toString());
                // set the schema for schema QName prefix to "xsd"
                schema.setSchemaForSchemaQNamePrefix("xsd");//$NON-NLS-1$
            }
            // catch up the NPE to make sure data model can still run in case of unknown conflict
            try {
                resource.getContents().add(schema);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
            // Add the root schema to the resource that was created above
            Iterator<Integer> iter = schemaMonitor.values().iterator();
            while (iter.hasNext()) {
                Integer it = iter.next();
                if (it.intValue() == -1) {
                    return schema;
                }
            }
            importSchema(schema, imports, schemaMonitor);
            schema.setElement(document.getDocumentElement());
            return schema;
        } finally {
            if (fin != null) {
                fin.close();
            }
        }
    }

    public static List<XSDComplexTypeDefinition> getComplexTypes(XSDSchema xsd) {
        EList<XSDTypeDefinition> contents = xsd.getTypeDefinitions();
        List<XSDComplexTypeDefinition> complexs = new ArrayList<XSDComplexTypeDefinition>();
        for (XSDTypeDefinition type : contents) {
            if (type instanceof XSDComplexTypeDefinition) {
                boolean exist = false;
                for (XSDComplexTypeDefinition xsdEl : complexs) {
                    if (xsdEl.getName().equals(type.getName()) && xsdEl.getTargetNamespace() != null
                            && type.getTargetNamespace() != null
                            && xsdEl.getTargetNamespace().equals(type.getTargetNamespace())) {
                        exist = true;
                        break;
                    } else if (xsdEl.getTargetNamespace() == null && type.getTargetNamespace() == null
                            && xsdEl.getName().equals(type.getName())) {
                        exist = true;
                        break;
                    }
                }
                if (!exist
                        && ((type.getTargetNamespace() != null
                        && !type.getTargetNamespace().equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001))
                                || type.getTargetNamespace() == null)) {
                    complexs.add((XSDComplexTypeDefinition) type);
                }
            }
        }

        return complexs;
    }

    public static String getTmpFolder() {
        String tmp = System.getProperty("user.dir") + "/exist"; //$NON-NLS-1$ //$NON-NLS-2$
        tmp = tmp.replace('\\', '/');
        return tmp;
    }

    /**
     * Deletes the temporary files.
     */
    public static void deleteTempFiles() {
        String tmpFold = getTmpFolder();
        File file = new File(tmpFold);
        if (!file.exists() && !file.isDirectory()) {
            return;
        }
        ZipToFile.deleteDirectory(file);
    }

    public static String getRealPath(String bundleID, String entry) throws Exception {
        URL urlentry;
        String strEntry;

        Bundle bundle = Platform.getBundle(bundleID);

        // get path URL
        urlentry = bundle.getEntry(entry);

        strEntry = FileLocator.toFileURL(urlentry).getPath();

        return strEntry;
    }

    public static void copyFile(String source, String dest) {
        if (source.equals(dest)) {
            return;
        }
        byte[] buf = new byte[1024];
        OutputStream os = null;
        InputStream is = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(dest));
            is = new BufferedInputStream(new FileInputStream(source));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error(e.getMessage(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            ;
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                }
            }
            ;
        }
    }

    public static boolean hasTags(TreeObject xobject) {
        if (xobject.isXObject()) {
            return true;
        }
        switch (xobject.getType()) {
        case TreeObject.DATA_CLUSTER: // is necessary
        case TreeObject.DATA_MODEL:
        case TreeObject.VIEW:
        case TreeObject.MENU:
        case TreeObject.ROLE:
        case TreeObject.ROUTING_RULE:
        case TreeObject.STORED_PROCEDURE:
        case TreeObject.TRANSFORMER:
            return true;
        default:
            return false;
        }
    }

    public static String[] convertWhereCondition(WSWhereCondition wc) {
        List<String> list = new ArrayList<String>();
        list.add(wc.getLeftPath());
        String operator = "";//$NON-NLS-1$
        if (wc.getOperator().equals(WSWhereOperator.CONTAINS)) {
            operator = "Contains";//$NON-NLS-1$
        } else if (wc.getOperator().equals(WSWhereOperator.CONTAINS_SENTENCE)) {
            operator = "contains the sentence";//$NON-NLS-1$
        } else if (wc.getOperator().equals(WSWhereOperator.EQUALS)) {
            operator = "=";//$NON-NLS-1$
        } else if (wc.getOperator().equals(WSWhereOperator.GREATER_THAN)) {
            operator = ">";//$NON-NLS-1$
        } else if (wc.getOperator().equals(WSWhereOperator.GREATER_THAN_OR_EQUAL)) {
            operator = ">=";//$NON-NLS-1$
        } else if (wc.getOperator().equals(WSWhereOperator.JOIN)) {
            operator = "Join With";//$NON-NLS-1$
        } else if (wc.getOperator().equals(WSWhereOperator.LOWER_THAN)) {
            operator = "<";//$NON-NLS-1$
        } else if (wc.getOperator().equals(WSWhereOperator.LOWER_THAN_OR_EQUAL)) {
            operator = "<=";//$NON-NLS-1$
        } else if (wc.getOperator().equals(WSWhereOperator.NOT_EQUALS)) {
            operator = "!=";//$NON-NLS-1$
        } else if (wc.getOperator().equals(WSWhereOperator.STARTSWITH)) {
            operator = "Starts With";//$NON-NLS-1$
        } else if (wc.getOperator().equals(WSWhereOperator.EMPTY_NULL)) {
            operator = "Is Empty Or Null";//$NON-NLS-1$
        } else if (wc.getOperator().equals(WSWhereOperator.FULLTEXTSEARCH)) {
            operator = "whole content contains";//$NON-NLS-1$
        }
        list.add(operator);
        list.add(wc.getRightValueOrPath());

        String predicate = "";//$NON-NLS-1$
        if (wc.getStringPredicate().equals(WSStringPredicate.AND)) {
            predicate = "And";//$NON-NLS-1$
        } else if (wc.getStringPredicate().equals(WSStringPredicate.NONE)) {
            predicate = "";//$NON-NLS-1$
        } else if (wc.getStringPredicate().equals(WSStringPredicate.OR)) {
            predicate = "Or";//$NON-NLS-1$
        } else if (wc.getStringPredicate().equals(WSStringPredicate.NOT)) {
            predicate = "Not";//$NON-NLS-1$
        }

        list.add(predicate);
        return list.toArray(new String[list.size()]);
    }

    public static String[] convertRouteCondition(WSRoutingRuleExpression wc) {
        List<String> list = new ArrayList<String>();
        list.add(wc.getXpath());
        String operator = "";//$NON-NLS-1$
        if (wc.getWsOperator().equals(WSRoutingRuleOperator.CONTAINS)) {
            operator = "Contains";//$NON-NLS-1$
        } else if (wc.getWsOperator().equals(WSRoutingRuleOperator.EQUALS)) {
            operator = "=";//$NON-NLS-1$
        } else if (wc.getWsOperator().equals(WSRoutingRuleOperator.GREATER_THAN)) {
            operator = ">";//$NON-NLS-1$
        } else if (wc.getWsOperator().equals(WSRoutingRuleOperator.GREATER_THAN_OR_EQUAL)) {
            operator = ">=";//$NON-NLS-1$
        } else if (wc.getWsOperator().equals(WSRoutingRuleOperator.MATCHES)) {
            operator = "Matches";//$NON-NLS-1$
        } else if (wc.getWsOperator().equals(WSRoutingRuleOperator.LOWER_THAN)) {
            operator = "<";//$NON-NLS-1$
        } else if (wc.getWsOperator().equals(WSRoutingRuleOperator.LOWER_THAN_OR_EQUAL)) {
            operator = "<=";//$NON-NLS-1$
        } else if (wc.getWsOperator().equals(WSRoutingRuleOperator.NOT_EQUALS)) {
            operator = "!=";//$NON-NLS-1$
        } else if (wc.getWsOperator().equals(WSRoutingRuleOperator.STARTSWITH)) {
            operator = "Starts With";//$NON-NLS-1$
        } else if (wc.getWsOperator().equals(WSRoutingRuleOperator.IS_NULL)) {
            operator = "Is Null";//$NON-NLS-1$
        } else if (wc.getWsOperator().equals(WSRoutingRuleOperator.IS_NOT_NULL)) {
            operator = "Is Not Null";//$NON-NLS-1$
        }
        list.add(operator);
        list.add(wc.getValue());
        list.add(wc.getName() == null ? "" : wc.getName());//$NON-NLS-1$
        return list.toArray(new String[list.size()]);
    }

    public static WSRoutingRuleExpression convertLineRoute(String[] values) {
        WSRoutingRuleExpression wc = new WSRoutingRuleExpression();

        wc.setXpath(values[0]);
        WSRoutingRuleOperator operator = null;
        if (values[1].equals("Contains")) { //$NON-NLS-1$
            operator = WSRoutingRuleOperator.CONTAINS;
        } else if (values[1].equals("Matches")) { //$NON-NLS-1$
            operator = WSRoutingRuleOperator.MATCHES;
        } else if (values[1].equals("=")) { //$NON-NLS-1$
            operator = WSRoutingRuleOperator.EQUALS;
        } else if (values[1].equals(">")) { //$NON-NLS-1$
            operator = WSRoutingRuleOperator.GREATER_THAN;
        } else if (values[1].equals(">=")) { //$NON-NLS-1$
            operator = WSRoutingRuleOperator.GREATER_THAN_OR_EQUAL;
        } else if (values[1].equals("<")) { //$NON-NLS-1$
            operator = WSRoutingRuleOperator.LOWER_THAN;
        } else if (values[1].equals("<=")) { //$NON-NLS-1$
            operator = WSRoutingRuleOperator.LOWER_THAN_OR_EQUAL;
        } else if (values[1].equals("!=")) { //$NON-NLS-1$
            operator = WSRoutingRuleOperator.NOT_EQUALS;
        } else if (values[1].equals("Starts With")) { //$NON-NLS-1$
            operator = WSRoutingRuleOperator.STARTSWITH;
        } else if (values[1].equals("Is Null")) { //$NON-NLS-1$
            operator = WSRoutingRuleOperator.IS_NULL;
        } else if (values[1].equals("Is Not Null")) { //$NON-NLS-1$
            operator = WSRoutingRuleOperator.IS_NOT_NULL;
        }
        wc.setWsOperator(operator);
        wc.setValue((values[2]));
        wc.setName((values[3]));
        return wc;
    }

    public static WSWhereCondition convertLine(String[] values) {
        WSWhereCondition wc = new WSWhereCondition();
        wc.setLeftPath(values[0]);
        WSWhereOperator operator = null;
        if (values[1].equals("Contains")) { //$NON-NLS-1$
            operator = WSWhereOperator.CONTAINS;
        } else if (values[1].equals("contains the sentence")) {//$NON-NLS-1$
            operator = WSWhereOperator.CONTAINS_SENTENCE;
        } else if (values[1].equals("Join With")) {//$NON-NLS-1$
            operator = WSWhereOperator.JOIN;
        } else if (values[1].equals("=")) { //$NON-NLS-1$
            operator = WSWhereOperator.EQUALS;
        } else if (values[1].equals(">")) { //$NON-NLS-1$
            operator = WSWhereOperator.GREATER_THAN;
        } else if (values[1].equals(">=")) { //$NON-NLS-1$
            operator = WSWhereOperator.GREATER_THAN_OR_EQUAL;
        } else if (values[1].equals("<")) { //$NON-NLS-1$
            operator = WSWhereOperator.LOWER_THAN;
        } else if (values[1].equals("<=")) { //$NON-NLS-1$
            operator = WSWhereOperator.LOWER_THAN_OR_EQUAL;
        } else if (values[1].equals("!=")) { //$NON-NLS-1$
            operator = WSWhereOperator.NOT_EQUALS;
        } else if (values[1].equals("Starts With")) { //$NON-NLS-1$
            operator = WSWhereOperator.STARTSWITH;
        } else if (values[1].equals("Is Empty Or Null")) { //$NON-NLS-1$
            operator = WSWhereOperator.EMPTY_NULL;
        } else if (values[1].equals("whole content contains")) { //$NON-NLS-1$
            operator = WSWhereOperator.FULLTEXTSEARCH;
        }
        wc.setOperator(operator);
        wc.setRightValueOrPath(values[2]);
        WSStringPredicate predicate = null;
        if (values[3].equals("")) { //$NON-NLS-1$
            predicate = WSStringPredicate.NONE;
        } else if (values[3].equals("Or")) { //$NON-NLS-1$
            predicate = WSStringPredicate.OR;
        }
        if (values[3].equals("And")) { //$NON-NLS-1$
            predicate = WSStringPredicate.AND;
        }

        if (values[3].equals("Not")) { //$NON-NLS-1$
            predicate = WSStringPredicate.NOT;
        }
        wc.setStringPredicate(predicate);

        return wc;
    }

    public static String getRevision(TreeObject xobject) {
        String revision = "";//$NON-NLS-1$
        if (xobject.getType() != TreeObject.DATA_CLUSTER) {
            TreeParent parent = xobject.findServerFolder(xobject.getType());

            if (parent != null) {
                Pattern p = Pattern.compile("\\[.*\\]");//$NON-NLS-1$
                Matcher m = p.matcher(parent.getDisplayName());
                while (m.find()) {
                    revision = m.group();
                    break;
                }
            }
        }
        return revision;
    }

    private static Pattern pLoad = Pattern.compile(".*?(<c>.*?</t>).*?(<p>(.*)</p>|<p/>).*", Pattern.DOTALL);//$NON-NLS-1$

    public static String getItemContent(String xmlstring) {
        Matcher m = null;
        m = pLoad.matcher(xmlstring);
        if (m.matches()) {
            if (m.group(2) == null || m.group(2).equals("<p/>")) {//$NON-NLS-1$
                return "";//$NON-NLS-1$
            } else {
                return m.group(3);
            }
        }
        return null;
    }

    /**
     * get all complex types's complextype children
     * 
     * @param complexTypeDefinition
     * @return
     */
    public static ArrayList<Object> getAllComplexTypeChildren(XSDElementDeclaration declaration) {
        XSDComplexTypeDefinition complexTypeDefinition = (XSDComplexTypeDefinition) declaration.getTypeDefinition();
        XSDComplexTypeContent xsdComplexTypeContent = complexTypeDefinition.getContent();
        ArrayList<Object> list = new ArrayList<Object>();

        // Now determine whether ref. If ref look at the base Type definition
        if (xsdComplexTypeContent == null) {
            XSDTypeDefinition typeDefinition = complexTypeDefinition.getBaseTypeDefinition();
            if (typeDefinition instanceof XSDComplexTypeDefinition) {
                list.add(((XSDComplexTypeDefinition) typeDefinition).getContent());
            }
        }

        // check if we are extending a complex Definition
        if ("extension".equals(complexTypeDefinition.getDerivationMethod().getName())) {//$NON-NLS-1$
            if (complexTypeDefinition.getBaseTypeDefinition() instanceof XSDComplexTypeDefinition) {
                XSDComplexTypeDefinition complex = (XSDComplexTypeDefinition) complexTypeDefinition.getBaseTypeDefinition();
                XSDParticle particle = (XSDParticle) complex.getContent();
                if (particle.getTerm() instanceof XSDModelGroup) {
                    XSDModelGroup group = (XSDModelGroup) particle.getTerm();
                    EList<XSDParticle> elist = group.getContents();
                    for (XSDParticle pt : elist) {
                        if (pt.getContent() instanceof XSDElementDeclaration) {
                            XSDTypeDefinition typeDef = ((XSDElementDeclaration) pt.getContent()).getTypeDefinition();
                            if (typeDef instanceof XSDComplexTypeDefinition) {
                                list.addAll(getAllComplexTypeChildren((XSDElementDeclaration) pt.getContent()));
                            }
                        }
                    }
                }
            }
        }

        // now check what we have in the content

        // if (xsdComplexTypeContent instanceof XSDComplexTypeDefinition) {
        list.add(declaration);
        // }

        // xsd Particle: we have a model group

        if (xsdComplexTypeContent instanceof XSDParticle) {
            XSDParticle particle = (XSDParticle) xsdComplexTypeContent;
            if (particle.getTerm() instanceof XSDModelGroup) {
                XSDModelGroup group = (XSDModelGroup) particle.getTerm();
                EList<XSDParticle> elist = group.getContents();
                for (XSDParticle pt : elist) {
                    if (pt.getContent() instanceof XSDElementDeclaration) {
                        XSDTypeDefinition typeDef = ((XSDElementDeclaration) pt.getContent()).getTypeDefinition();
                        if (typeDef instanceof XSDComplexTypeDefinition) {
                            list.addAll(getAllComplexTypeChildren((XSDElementDeclaration) pt.getContent()));
                        }
                    }
                }
            }
        }

        return list;
    }

    public static ArrayList<Object> getComplexTypeDefinitionChildren(XSDComplexTypeDefinition complexTypeDefinition) {
        return getComplexTypeDefinitionChildren(complexTypeDefinition, false);
    }

    public static ArrayList<Object> getComplexTypeDefinitionChildren(XSDComplexTypeDefinition complexTypeDefinition,
            boolean ignoreRestriction) {

        XSDComplexTypeContent xsdComplexTypeContent = complexTypeDefinition.getContent();
        ArrayList<Object> list = new ArrayList<Object>();

        // Now determine whether ref. If ref look at the base Type definition
        XSDTypeDefinition baseTypeDefinition = complexTypeDefinition.getBaseTypeDefinition();
        if (xsdComplexTypeContent == null) {
            XSDTypeDefinition typeDefinition = baseTypeDefinition;
            // if a simple type return the simple type
            if (typeDefinition instanceof XSDSimpleTypeDefinition) {
                list.add(typeDefinition);
                return list;
            } else {
            }
            // it is a complex Type
            xsdComplexTypeContent = ((XSDComplexTypeDefinition) typeDefinition).getContent();
        }

        // check if we are extending a complex Definition
        if ("extension".equals(complexTypeDefinition.getDerivationMethod().getName())) {//$NON-NLS-1$

            if (baseTypeDefinition instanceof XSDComplexTypeDefinition && baseTypeDefinition != complexTypeDefinition) {
                String name = ((XSDComplexTypeDefinition) baseTypeDefinition).getDerivationMethod().getName();
                if (name.equals("restriction") || ignoreRestriction) { //$NON-NLS-1$
                    list.addAll(
                            getComplexTypeDefinitionChildren((XSDComplexTypeDefinition) baseTypeDefinition, ignoreRestriction));
                    //
                }

            }
        }

        // Attributes
        if (complexTypeDefinition.getAttributeContents() != null) {
            list.addAll(complexTypeDefinition.getAttributeContents());
        }

        // Annotations
        if (complexTypeDefinition.getAnnotations() != null) {
            list.addAll(complexTypeDefinition.getAnnotations());
        }

        // now check what we have in the content

        // simple type return the simple type
        if (xsdComplexTypeContent instanceof XSDSimpleTypeDefinition) {
            list.add(xsdComplexTypeContent);
            return list;
        }

        // xsd Particle: we have a model group
        if (xsdComplexTypeContent instanceof XSDParticle) {
            // log.info("Model Group?: "+((XSDParticle)xsdComplexTypeContent).getTerm());
            if (((XSDParticle) xsdComplexTypeContent).getTerm() instanceof XSDModelGroup) {
                // return the model group
                list.add(((XSDParticle) xsdComplexTypeContent).getTerm());
                return list;
            } else { // wild card or element declaration '?)
                list.add(((XSDParticle) xsdComplexTypeContent).getTerm());
                return list;
            }
        }

        // what else could it be ?
        list.add(xsdComplexTypeContent);
        return list;
    }

    public static List<Object> getSimpleTypeDefinitionChildren(XSDSimpleTypeDefinition simpleTypeDefinition) {
        List<Object> result = new ArrayList<Object>();

        // Annotations
        if (!isBuildInType(simpleTypeDefinition)) {
            if (simpleTypeDefinition.getAnnotations() != null) {
                result.addAll(simpleTypeDefinition.getAnnotations());
            }
        }

        result.add(simpleTypeDefinition);

        return result;
    }

    public static List<String> getConcepts(XSDSchema schema) {
        EList<XSDElementDeclaration> xsdElementDeclarations = schema.getElementDeclarations();
        List<String> list = new ArrayList<String>();
        for (XSDElementDeclaration el : xsdElementDeclarations
                .toArray(new XSDElementDeclaration[xsdElementDeclarations.size()])) {
            if (!el.getIdentityConstraintDefinitions().isEmpty()) {
                list.add(el.getName());
            }
        }
        return list;
    }

    /**
     * Returns and XSDSchema Object from an xsd
     * 
     * @param schema
     * @return
     * @throws Exception
     */
    public static XSDSchema getXSDSchema(String schema) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(false);
        StringReader reader = new StringReader(schema);
        InputSource source = new InputSource(new StringReader(schema));
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(source);
        XSDSchema xsdSchema = null;

        xsdSchema = XSDSchemaImpl.createSchema(document.getDocumentElement());
        reader.close();
        return xsdSchema;
    }

    public final static String ENTERPRISE_ID = "org.talend.mdm.workbench.enterprise";//$NON-NLS-1$

    public static boolean IsEnterPrise() {
        return Platform.getBundle(ENTERPRISE_ID) != null;
    }

    public static String getNewLabelString(String oldString) {
        String newLabel = null;
        newLabel = EXtentisObjects.getXtentisObjectDisplayName(oldString);
        if ("Routing Engine V2".equals(oldString)) {//$NON-NLS-1$
            newLabel = EXtentisObjects.SubscriptionEngine.getDisplayName();
        }
        if (newLabel == null) {
            return oldString;
        } else {
            return newLabel;
        }
    }

    public static Image getMenuImage(String menuName) {
        String menu = EImage.MENU.getPath();
        if (menuName.startsWith("Browse")) {
            menu = EImage.BROWSE_MENU.getPath();
        } else if (menuName.equalsIgnoreCase("Cross Referencing")) {
            menu = EImage.CROSSREF.getPath();
        } else if (menuName.equalsIgnoreCase("Hierarchical View")) {
            menu = EImage.HIER_VIEW.getPath();
        } else if (menuName.equalsIgnoreCase("ItemsTrash")) {
            menu = EImage.TRASH.getPath();
        } else if (menuName.equalsIgnoreCase("logging")) {
            menu = EImage.MENU.getPath();
        } else if (menuName.equalsIgnoreCase("Manage users")) {
            menu = EImage.MANAGE_USERS.getPath();
        } else if (menuName.equalsIgnoreCase("UpdateReport")) {
            menu = EImage.UPDATEREPORT.getPath();
        }
        return ImageCache.getImage(menu).createImage();
    }

    public static String formatXsdSource(String xsdSource) {
        return formatXsdSource(xsdSource, false);
    }

    public static String formatXsdSource(String xsdSource, boolean suppressDeclaration) {
        try {
            SAXReader reader = new SAXReader();
            org.dom4j.Document document = reader.read(new StringReader(xsdSource));
            StringWriter writer = new StringWriter();
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");//$NON-NLS-1$
            format.setSuppressDeclaration(suppressDeclaration);
            XMLWriter xmlwriter = new XMLWriter(writer, format);
            xmlwriter.write(document);
            String str = writer.toString();
            writer.close();
            xmlwriter.close();
            return str;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return xsdSource;

    }

    // add this method for the text drop action of all the texts available.
    public static void createCompDropTarget(final Text text) {
        DropTarget dropTarget = new DropTarget(text, DND.DROP_MOVE | DND.DROP_LINK);
        dropTarget.setTransfer(new TreeObjectTransfer[] { TreeObjectTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetAdapter() {

            @Override
            public void dragEnter(DropTargetEvent event) {
            }

            @Override
            public void dragLeave(DropTargetEvent event) {
            }

            @Override
            public void dragOver(DropTargetEvent event) {
                event.feedback |= DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
            }

            @Override
            public void drop(DropTargetEvent event) {
                if (event.data instanceof TreeObject[]) {
                    text.setText(text.getText() + ((TreeObject[]) event.data)[0].getDisplayName());
                }
            }
        });

    }

    public static boolean checkInCOpyTypeParticle(Object[] selectedObjs) {
        for (Object obj : selectedObjs) {
            if (obj instanceof XSDParticle) {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }

    public static boolean checkInCopyTypeElement(Object[] selectedObjs) {
        /*
         * if(selectedObjs.length>1) displayName = "Copy Entities";
         */
        for (Object obj : selectedObjs) {
            if (obj instanceof XSDElementDeclaration) {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }

    public static boolean checkInCopy(Object[] selectedObjs) {
        return checkInCopyTypeElement(selectedObjs) || checkInCOpyTypeParticle(selectedObjs);
    }

    public static String checkOnVersionCompatibility(String url, String username, String password) {
        IProduct product = Platform.getProduct();
        String versionComp = "";//$NON-NLS-1$
        try {
            URL resourceURL = product.getDefiningBundle().getResource("/about.mappings");//$NON-NLS-1$
            PropertyResourceBundle bundle = new PropertyResourceBundle(resourceURL.openStream());
            String studioVersion = bundle.getString("1").trim();//$NON-NLS-1$
            Pattern vsnPtn = Pattern.compile("^(\\d+)\\.(\\d+)(\\.)*(\\d*)$");//$NON-NLS-1$
            Matcher match = vsnPtn.matcher(studioVersion);
            if (!match.find()) {
                return null;
            }
            versionComp = Messages.Util_45 + studioVersion + Messages.Util_46;

            int major = Integer.parseInt(match.group(1));
            int minor = Integer.parseInt(match.group(2));
            int rev = match.group(4) != null && !match.group(4).equals("") ? Integer.parseInt(match.group(4)) : 0;//$NON-NLS-1$
            TMDMService service = Util.getMDMService(new URL(url), username, password);
            WSVersion wsVersion = service.getComponentVersion(new WSGetComponentVersion(WSComponent.DATA_MANAGER, null));
            versionComp += Messages.Util_47 + wsVersion.getMajor() + Messages.Util_48 + wsVersion.getMinor() + Messages.Util_49
                    + wsVersion.getRevision();
            if (major != wsVersion.getMajor() || minor != wsVersion.getMinor()) {
                return versionComp;
            }
            if (rev == 0) {
                // major version compare
                if (wsVersion.getRevision() != 0) {
                    return versionComp;
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1) {
                out.write(b, 0, n);
            }
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }

    public static Object[] filterOutDuplicatedElems(XSDNamedComponent[] checkedElements) {

        List<XSDNamedComponent> list = new ArrayList<XSDNamedComponent>();
        for (XSDNamedComponent el : checkedElements) {
            boolean exist = false;
            for (XSDNamedComponent xsdEl : list) {
                if (xsdEl.getName().equals(el.getName()) && xsdEl.getTargetNamespace() != null && el.getTargetNamespace() != null
                        && xsdEl.getTargetNamespace().equals(el.getTargetNamespace())) {
                    exist = true;
                    break;
                } else if (xsdEl.getName().equals(el.getName()) && xsdEl.getTargetNamespace() == null
                        && el.getTargetNamespace() == null) {
                    exist = true;
                    break;
                }
            }
            if (!exist
                    && ((el.getTargetNamespace() != null
                    && !el.getTargetNamespace().equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001))
                            || el.getTargetNamespace() == null)) {
                list.add(el);
            }
        }

        return list.toArray(new Object[] {});

    }

    public static XSDSchema getXSDSchema(TreeObject treeObj, String dataModelName) {

        TreeObject dataModelFolder = treeObj.findServerFolder(TreeObject.DATA_MODEL);

        TMDMService port = null;
        try {
            port = Util.getMDMService(dataModelFolder);
        } catch (XtentisException e3) {
            log.error(e3.getMessage(), e3);
        } catch (Exception e3) {
            log.error(e3.getMessage(), e3);
        }
        WSDataModel wsDataModel = null;
        try {
            wsDataModel = port.getDataModel(new WSGetDataModel(new WSDataModelPK(dataModelName)));
            String schema = wsDataModel.getXsdSchema();
            return Util.createXsdSchema(schema, dataModelFolder);
        } catch (Exception e1) {
            log.error(e1.getMessage(), e1);
        }

        return null;
    }

    public static XSDSchema getXSDSchemaOfDirtyEditor(String dataModelName) {
        XSDSchema xsd = null;
        IEditorPart[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getDirtyEditors();
        if (null != editors) {
            for (IEditorPart editorpart : editors) {
                if (editorpart instanceof XSDEditor) {
                    XSDEditor xeditor = (XSDEditor) editorpart;
                    DataModelMainPage mainPage = xeditor.getdMainPage();
                    if (mainPage.getDataModel().getName().equals(dataModelName)) {
                        xsd = mainPage.getXSDSchema();
                        break;
                    }
                }
            }
        }

        return xsd;
    }

    /**
     * Replace the source string by the parameters
     * 
     * @param sourceString the source string with parameters,like : "This is {0} examples for {1}"
     * @param parameters the parameters used to do the replacement, the key is the index of the parameter, the value is
     * the content;
     * @return the string after replacement
     */
    public static String replaceWithParameters(final String sourceString, Map<Integer, String> parameters) {
        String temp = sourceString;

        for (Entry<Integer, String> eachId2Content : parameters.entrySet()) {
            Pattern pattern = Pattern.compile("\\{" + eachId2Content.getKey() + "\\}");//$NON-NLS-1$//$NON-NLS-2$
            Matcher matcher = pattern.matcher(temp);
            temp = matcher.replaceAll(eachId2Content.getValue());
        }

        return temp;
    }

    public static boolean isSimpleTypedParticle(XSDParticle curXSDParticle) {

        if (!isValidParticle(curXSDParticle)) {
            return false;
        }

        return (((XSDElementDeclaration) curXSDParticle.getContent()).getTypeDefinition() instanceof XSDSimpleTypeDefinition);
    }

    public static String getParticleName(XSDParticle curXSDParticle) {

        if (!isValidParticle(curXSDParticle)) {
            return "";//$NON-NLS-1$
        }

        String name = ((XSDElementDeclaration) curXSDParticle.getContent()).getName();

        return (name == null ? "" : name);//$NON-NLS-1$
    }

    public static String getParticleReferenceName(XSDParticle curXSDParticle) {

        if (!isValidParticle(curXSDParticle)) {
            return "";//$NON-NLS-1$
        }

        XSDElementDeclaration ref = null;
        if (((XSDElementDeclaration) curXSDParticle.getContent()).isElementDeclarationReference()) {
            ref = ((XSDElementDeclaration) curXSDParticle.getContent()).getResolvedElementDeclaration();
        }

        return ((ref == null ? "" : ref.getQName()));//$NON-NLS-1$
    }

    private static boolean isValidParticle(XSDParticle curXSDParticle) {

        return (curXSDParticle != null && curXSDParticle.getContent() instanceof XSDElementDeclaration);

    }

    // connection refuse or ssl error
    private static String CONNECT_FAIL = "http.client.failed";//$NON-NLS-1$

    // 401
    private static String UNAUTHORIZE = "http.client.unauthorized";//$NON-NLS-1$

    // 404
    private static String NOT_FOUND = "http.not.found";//$NON-NLS-1$

    public static boolean handleConnectionException(IWorkbenchPart part, Throwable t, String title) {
        return handleConnectionException(part.getSite().getShell(), t, title);
    }

    public static boolean handleConnectionException(final Shell shell, Throwable t, String title) {
        if (null == t) {
            return false;
        }
        String message = null;
        if (t instanceof com.sun.xml.internal.ws.client.ClientTransportException) {
            String key = ((com.sun.xml.internal.ws.client.ClientTransportException) t).getKey();
            if (null == title) {
                title = Messages.ConnectFailedTitle;
            }

            if (CONNECT_FAIL.equals(key)) {
                message = Messages.ConnectFailed;
            } else if (UNAUTHORIZE.equals(key)) {
                message = Messages.ConnectUnauthorized;
            } else if (NOT_FOUND.equals(key)) {
                message = Messages.ConnectNotFound;
            } else {
                return false;
            }

            return true;
        } else if (t instanceof ConnectException) {
            message = t.getLocalizedMessage();
        }
        if (t instanceof XtentisException) {
            return handleConnectionException(shell, t.getCause(), title);
        }
        if (message != null) {
            if (null == Display.getCurrent()) {
                final String fTitle = title;
                final String fMessage = message;
                Display.getDefault().syncExec(new Runnable() {

                    public void run() {
                        MessageDialog.openWarning(shell, fTitle, fMessage);
                    }
                });
            } else {
                MessageDialog.openWarning(shell, title, message);
            }
            return true;
        }
        return false;
    }

    public static List<String> getAllCustomTypeNames(XSDSchema schema) {

        List<String> customTypeNames = new ArrayList<String>();

        for (EUUIDCustomType eUUIDType : EUUIDCustomType.values()) {
            customTypeNames.add(eUUIDType.getName());
        }

        for (XSDTypeDefinition eachType : schema.getTypeDefinitions()) {

            if (!(eachType instanceof XSDSimpleTypeDefinition)) {
                continue;
            }

            if (eachType.getTargetNamespace() != null
                    && eachType.getTargetNamespace().equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001)) {
                continue;
            }

            if (customTypeNames.contains(eachType.getName())) {
                continue;
            }

            customTypeNames.add(eachType.getName());
        }

        return customTypeNames;
    }

    public static boolean isUUID(XSDElementDeclaration decl) {
        XSDTypeDefinition typeDefinition = decl.getTypeDefinition();
        if (null == typeDefinition) {
            return false;
        }
        String type = typeDefinition.getName();
        if (type == null) {
            type = typeDefinition.getBaseType().getName();
        }
        // set enum
        if (typeDefinition instanceof XSDSimpleTypeDefinition && Util.isCustomrType(decl.getSchema(), type)) {
            XSDSimpleTypeDefinition typedef = (XSDSimpleTypeDefinition) typeDefinition;
            if (typedef.getBaseTypeDefinition() != null && typedef.getEnumerationFacets().size() > 0) {
                type = "enum"; //$NON-NLS-1$
            }
        }
        if ("UUID".equals(type) || "AUTO_INCREMENT".equals(type)) { //$NON-NLS-1$ //$NON-NLS-2$
            return true;
        }
        return false;
    }

    public static boolean isCustomrType(XSDSchema schema, String typeName) {
        return getAllCustomTypeNames(schema).contains(typeName);
    }

    public static List<XSDSimpleTypeDefinition> getAllBuildInTypes(XSDSchema schema) {

        if (schema == null) {
            return new ArrayList<XSDSimpleTypeDefinition>();
        }

        List<XSDSimpleTypeDefinition> builtInTypes = new ArrayList<XSDSimpleTypeDefinition>();
        for (XSDTypeDefinition eachBuildInTypeDef : schema.getSchemaForSchema().getTypeDefinitions()) {

            if (!(eachBuildInTypeDef instanceof XSDSimpleTypeDefinition)) {
                continue;
            }

            builtInTypes.add((XSDSimpleTypeDefinition) eachBuildInTypeDef);
        }

        return builtInTypes;
    }

    public static boolean isBuildInType(XSDSimpleTypeDefinition type) {
        return getAllBuildInTypes(type.getSchema()).contains(type);
    }

    public static boolean isSequenceComplexType(XSDComplexTypeDefinition type) {
        return XSDCompositor.SEQUENCE_LITERAL.equals(getComplexTypeGroupType(type));
    }

    public static boolean isAllComplexType(XSDComplexTypeDefinition type) {
        return XSDCompositor.ALL_LITERAL.equals(getComplexTypeGroupType(type));
    }

    public static boolean isChoiceComplexType(XSDComplexTypeDefinition type) {
        return XSDCompositor.CHOICE_LITERAL.equals(getComplexTypeGroupType(type));
    }

    public static XSDCompositor getComplexTypeGroupType(XSDComplexTypeDefinition type) {

        if (type == null || !(type.getContent() instanceof XSDParticle)
                || !(((XSDParticle) type.getContent()).getContent() instanceof XSDModelGroup)) {
            return null;
        }

        XSDParticle groupParticle = (XSDParticle) type.getContent();
        XSDModelGroup group = (XSDModelGroup) groupParticle.getContent();

        return group.getCompositor();
    }

    /**
     * update reference from child to this XSDComplexTypeDefinition
     */
    public static void updateChildrenReferenceToComplexType(XSDComplexTypeDefinition type) {
        List<XSDComplexTypeDefinition> complexTypes = getComplexTypes(type.getSchema());
        for (XSDComplexTypeDefinition complexType : complexTypes) {
            if (complexType.equals(type)) {
                continue;
            }

            if (Util.getParentTypes(complexType).contains(type)) {
                complexType.setBaseTypeDefinition(type);
                continue;
            }
        }
    }

    public static List<XSDTypeDefinition> getParentTypes(XSDTypeDefinition type) {
        return getParentTypes(type, new ArrayList<XSDTypeDefinition>());
    }

    private static List<XSDTypeDefinition> getParentTypes(XSDTypeDefinition type, List<XSDTypeDefinition> results) {

        XSDTypeDefinition baseType = type.getBaseType();

        if (baseType == type) {
            return results;
        }
        if (!results.contains(baseType)) {
            results.add(baseType);
        }

        if (baseType == null || baseType
                .equals(type.getSchema().resolveTypeDefinition(type.getSchema().getSchemaForSchemaNamespace(), "anyType"))) {
            return results;
        }

        return getParentTypes(baseType, results);

    }

    public static boolean isDouble(XSDSimpleTypeDefinition simpleType) {
        return isSpecifiedBuildInType(simpleType, "double");//$NON-NLS-1$
    }

    public static boolean isFloat(XSDSimpleTypeDefinition simpleType) {
        return isSpecifiedBuildInType(simpleType, "float");//$NON-NLS-1$
    }

    public static boolean isDecimal(XSDSimpleTypeDefinition simpleType) {

        if (isSpecifiedBuildInType(simpleType, "integer")) {
            return false;
        }

        return isSpecifiedBuildInType(simpleType, "decimal");//$NON-NLS-1$
    }

    public static boolean isDate(XSDSimpleTypeDefinition simpleType) {
        return isSpecifiedBuildInType(simpleType, "date");//$NON-NLS-1$
    }

    public static boolean isDateTime(XSDSimpleTypeDefinition simpleType) {
        return isSpecifiedBuildInType(simpleType, "dateTime");//$NON-NLS-1$
    }

    public static boolean isTime(XSDSimpleTypeDefinition simpleType) {
        return isSpecifiedBuildInType(simpleType, "time");//$NON-NLS-1$
    }

    public static boolean isSpecifiedBuildInType(XSDSimpleTypeDefinition simpleType, String buildInTypeName) {

        if (simpleType == null || simpleType.getSchema() == null) {
            return false;
        }

        XSDSimpleTypeDefinition systemDoubleType = simpleType.getSchema()
                .resolveSimpleTypeDefinition(simpleType.getSchema().getSchemaForSchemaNamespace(), buildInTypeName);

        if (systemDoubleType == null) {
            return false;
        }

        return simpleType.equals(systemDoubleType) || systemDoubleType.equals(simpleType.getBaseType());
    }

    public static ByteArrayOutputStream retrieveJarEntryAsBytes(File barFile, String entryName) {
        JarInputStream jarIn = null;
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

        try {
            jarIn = new JarInputStream(new FileInputStream(barFile));
            JarEntry entry;
            byte[] buf = new byte[4096];
            while ((entry = jarIn.getNextJarEntry()) != null) {
                if (entry.toString().equals(entryName)) {
                    int read;
                    while ((read = jarIn.read(buf, 0, 4096)) != -1) {
                        outBytes.write(buf, 0, read);
                    }
                    if (outBytes.toByteArray().length > 0) {
                        return outBytes;
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * DOC hbhong Comment method "unZipFile". same with unZipFile(String zipfile, String unzipdir) method except having
     * a progressMonitor
     * 
     * @param zipfile
     * @param unzipdir
     * @param totalProgress
     * @param monitor
     * @throws IOException
     * @throws Exception
     */
    public static void unZipFile(String zipfile, String unzipdir, int totalProgress, IProgressMonitor monitor)
            throws IOException {
        monitor.setTaskName(Messages.Util_50);
        File unzipF = new File(unzipdir);
        if (!unzipF.exists()) {
            unzipF.mkdirs();
        }
        ZipFile zfile = null;

        try {
            zfile = new ZipFile(zipfile);
            int total = zfile.size();
            // System.out.println("zip's entry size:"+total);
            int interval, step;
            if (totalProgress / total > 0) {
                interval = 1;
                step = Math.round(totalProgress / total);
            } else {
                step = 1;
                interval = Math.round(total / totalProgress + 0.5f);
            }
            Enumeration zList = zfile.entries();
            ZipEntry ze = null;
            byte[] buf = new byte[1024];
            int tmp = 1;
            while (zList.hasMoreElements()) {
                ze = (ZipEntry) zList.nextElement();
                monitor.subTask(ze.getName());
                if (ze.isDirectory()) {
                    File f = new File(unzipdir + ze.getName());
                    f.mkdirs();
                    continue;
                }
                unzipdir = unzipdir.replace('\\', '/');
                if (!unzipdir.endsWith("/")) { //$NON-NLS-1$
                    unzipdir = unzipdir + "/"; //$NON-NLS-1$
                }
                String filename = unzipdir + ze.getName();
                File zeF = new File(filename);
                if (!zeF.getParentFile().exists()) {
                    zeF.getParentFile().mkdirs();
                }

                OutputStream os = null;
                InputStream is = null;
                try {
                    os = new BufferedOutputStream(new FileOutputStream(zeF));
                    is = new BufferedInputStream(zfile.getInputStream(ze));
                    int readLen = 0;
                    while ((readLen = is.read(buf, 0, 1024)) != -1) {
                        os.write(buf, 0, readLen);
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (Exception e) {
                    }
                    try {
                        if (os != null) {
                            os.close();
                        }
                    } catch (Exception e) {
                    }

                }
                // update monitor
                if (interval == 1) {
                    monitor.worked(step);
                } else {
                    if (tmp >= interval) {
                        monitor.worked(step);
                        tmp = 1;
                    } else {
                        tmp++;
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (zfile != null) {
                try {
                    zfile.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static String formatErrorMessage(String sourceMessage) {
        String saxExceptionPattern = "\\[\\w*\\]\\s:\\d+:\\d+:\\s.+:\\s"; //$NON-NLS-1$
        String nestedExceptionPattern = ";?\\s?nested exception is:[\\w\\W]*"; //$NON-NLS-1$

        Pattern pattern = Pattern.compile(saxExceptionPattern);
        Matcher matcher = pattern.matcher(sourceMessage);
        String result = matcher.replaceFirst(""); //$NON-NLS-1$

        pattern = Pattern.compile(nestedExceptionPattern);
        matcher = pattern.matcher(result);
        result = matcher.replaceFirst(""); //$NON-NLS-1$

        return result;
    }

    public static String getContextPath(String urlPath) {
        String contextPath = ""; //$NON-NLS-1$

        if (urlPath != null) {
            String path = urlPath;
            int index = path.indexOf("/services/soap"); //$NON-NLS-1$
            if (index != -1 && index != 0) {
                contextPath = path.substring(0, index);
            }
        }
        return contextPath;
    }

}
