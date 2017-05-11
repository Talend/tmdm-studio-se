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
package routines;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/*
 * user specification: the function's comment should contain keys as follows: 1. write about the function's comment.but
 * it must be before the "{talendTypes}" key.
 * 
 * 2. {talendTypes} 's value must be talend Type, it is required . its value should be one of: String, char | Character,
 * long | Long, int | Integer, boolean | Boolean, byte | Byte, Date, double | Double, float | Float, Object, short |
 * Short
 * 
 * 3. {Category} define a category for the Function. it is required. its value is user-defined .
 * 
 * 4. {param} 's format is: {param} <type>[(<default value or closed list values>)] <name>[ : <comment>]
 * 
 * <type> 's value should be one of: string, int, list, double, object, boolean, long, char, date. <name>'s value is the
 * Function's parameter name. the {param} is optional. so if you the Function without the parameters. the {param} don't
 * added. you can have many parameters for the Function.
 * 
 * 5. {example} gives a example for the Function. it is optional.
 */
public class MDM {

	public static final String WS_CLIENT_CONNECTION_TIMEOUT_KEY = "javax.xml.ws.client.connectionTimeout";
	
	public static final String WS_CLIENT_RECEIVE_TIMEOUT_KEY = "javax.xml.ws.client.receiveTimeout";
	
	public static final String WS_CLIENT_CONNECTION_TIME_VALUE = "ws_client_connection_timeout";
	
	public static final String WS_CLIENT_RECEIVE_TIME_VALUE = "ws_client_receive_timeout";
	
    /**
     * getFK: Return one of the FK component by position in a mangled FK (FKs are mangled in MDM to accommodate for
     * compound keys)
     * 
     * 
     * {talendTypes} String
     * 
     * {Category} MDM
     * 
     * {param} string(FKs) mangledFK: original mangled FK.
     * 
     * {param} int(0) pos: key position (starts at 0)
     * 
     * {example} getFK(FKs,0) # 12345
     */
    public static String getFK(String mangledFK, int pos) {
        if (mangledFK == null) {
            return null;
        }
        Pattern p = Pattern.compile("(\\[[^\\[\\]]*\\])"); //$NON-NLS-1$
        Matcher m = p.matcher(mangledFK.trim());
        int i = 0;
        while (m.find()) {
            if (i == pos) {
                String targetValue = m.group(0);
                return targetValue.substring(1, targetValue.length() - 1);
            }
            i++;
        }
        return null;
    }

    /**
     * createFK: Returns the mangled FK string of a given key (FKs are mangled in MDM to accommodate for compound keys).
     * Returns null if key is null.
     * 
     * 
     * {talendTypes} String
     * 
     * {Category} MDM
     * 
     * {param} string singleKey: original key.
     * 
     * 
     * {example} createFK("0") # return "[0]"
     */
    public static String createFK(String singleKey) {
        if (singleKey != null) {
            return "[" + singleKey + "]"; //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            return null;
        }
    }

    /**
     * createFK: Returns the mangled FK string of a given array of keys (FKs are mangled in MDM to accommodate for
     * compound keys). Returns null if one of the keys is null.
     * 
     * 
     * {talendTypes} String
     * 
     * {Category} MDM
     * 
     * {param} string singleKey: original key array.
     * 
     * 
     * {example} createFK({"0","1"}) # return "[0][1]"
     */
    public static String createFK(String[] keys) {
        StringBuffer sb = new StringBuffer();
        for (String key : keys) {
            if (key == null) {
                return null;
            }
            sb.append("[").append(key).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return sb.toString();
    }

    /**
     * get repeating element in xmlString according to the xpath & position
     * 
     * {talendTypes} String
     * 
     * {Category} MDM
     * 
     * {param} string(xml) xml: xml
     * 
     * {param} string(xpath) xpath: xpath.
     * 
     * {param} int(0) position: position.
     */
    public static String getRepeatingElement(String xml, String xpath, int position) throws Exception {

        Node node = parse(xml);
        NodeList list = getNodeList(node, xpath, false);
        for (int i = 0; i < list.getLength(); i++) {
            if (i == position) {
                Node n = list.item(i);
                return n.getNodeValue();
            }
        }
        return null;
    }

    /**
     * check repeating elements in xmlString according to xpath & text
     * 
     * {talendTypes} Boolean
     * 
     * {Category} MDM
     * 
     * {param} string(xml) xml: xml.
     * 
     * {param} string(xpath) xpath: xpath.
     * 
     * {param} String(text) text: text.
     */
    public static boolean hasRepeatingElement(String xml, String xpath, String text) throws Exception {
        Node node = parse(xml);
        NodeList list = getNodeList(node, xpath, false);
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            if (n.getNodeValue().equals(text)) {
                return true;
            }
        }
        return false;
    }

    /**
     * list repeating elements in xmlString according to xpath & delimiter
     * 
     * {talendTypes} String
     * 
     * {Category} MDM
     * 
     * {param} string(xml) xml: xml.
     * 
     * {param} string(xpath) xpath: xpath.
     * 
     * {param} char(delimiter) delimiter: delimiter.
     */
    public static String listRepeatingElement(String xml, String xpath, char delimiter) throws Exception {
        Node node = parse(xml);

        NodeList list = getNodeList(node, xpath, false);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            sb.append(n.getNodeValue());
            if (i >= 0 && i < list.getLength() - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    /**
     * add repeating elements in xmlString according to xpath & text
     * 
     * {talendTypes} String
     * 
     * {Category} MDM
     * 
     * {param} string(xml) xml: xml
     * 
     * {param} string(xpath) xpath: xpath
     * 
     * {param} String(text) text: text
     */
    public static String addRepeatingElement(String xml, String xpath, String text) throws Exception {
        Node node = parse(xml);

        int pos = xpath.lastIndexOf('/');
        String name = xpath.substring(pos + 1);
        String parentPath = xpath.substring(0, pos);
        NodeList plist = getNodeList(node, parentPath, true);
        if (plist.getLength() > 0) {
            Element el = node.getOwnerDocument().createElement(name);
            el.setTextContent(text);
            plist.item(0).appendChild(el);
        }

        return nodeToString(node, true);
    }

    /**
     * @deprecated Generate an <error code="X">msg</error> fragment
     * 
     * 
     * {talendTypes} String
     * 
     * {Category} MDM
     * 
     * {param} string msg: error message.
     * 
     * {param} int(0) code: error code, (1:ERROR, 0:NORMAL)
     * 
     * {example} genErrMsg("test message",0) # return <error code="0">test message</error>
     */
    @Deprecated
    public static String createReturnMessage(String msg, int code) {
        return "<error code=\"" + code + "\">" + msg + "</error>"; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
    }

    /**
     * Generate an <report><message type="X">msg</message><report> fragment
     * 
     * 
     * {talendTypes} String
     * 
     * {Category} MDM
     * 
     * {param} string msg: error message.
     * 
     * {param} String(0) type: error code, (info|error)
     * 
     * {example} genErrMsg("test message",0) # return <error code="0">test message</error>
     */
    public static String createReturnMessage(String msg, String type) {
        return "<report><message type=\"" + type + "\">" + msg + "</message></report>"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    }

    /**
     * Add or update an ISO variant to the multi-lingual text value
     * 
     * 
     * {talendTypes} String
     * 
     * {Category} MDM
     * 
     * {param} string(iso) iso: iso
     * 
     * {param} string(value) value: value
     * 
     * {param} string(rawValue) rawValue: rawValue
     * 
     * {example} setLanguageVariant("EN","abc","[EN:ab][FR:ab_fr]") # return [EN:abc][FR:ab_fr]
     */
    public static String setLanguageVariant(String iso, String value, String rawValue) {

        return setLanguageVariant(iso, value, rawValue, "EN", true); //$NON-NLS-1$

    }

    /**
     * Add or update an ISO variant to the multi-lingual text value with defaultIso and sort option (For the legacy
     * value which do not follow the multi-lingual field syntax, it will be adapted to defaultIso)
     * 
     * {talendTypes} String
     * 
     * {Category} MDM
     * 
     * {param} string(iso) iso: iso
     * 
     * {param} string(value) value: value
     * 
     * {param} string(rawValue) rawValue: rawValue
     * 
     * {param} string(defaultIso) defaultIso: defaultIso
     * 
     * {param} string(sort) sort: sort
     * 
     * {example} setLanguageVariant("FR","ab_fr","ab","EN", true) # return [EN:ab][FR:ab_fr]
     */
    public static String setLanguageVariant(String iso, String value, String rawValue, String defaultIso, boolean sort) {

        if (iso == null || value == null) {
            throw new IllegalArgumentException();
        }

        iso = iso.toUpperCase();

        Map<String, String> isoValues = new LinkedHashMap<String, String>();

        if (rawValue == null || rawValue.trim().length() == 0) {
            isoValues.put(iso, value);
        } else {

            Pattern p = Pattern.compile("\\[(\\w+)\\:([^\\[\\]]*?)\\]{1,}"); //$NON-NLS-1$
            Matcher m = p.matcher(rawValue);
            while (m.find()) {
                isoValues.put(m.group(1).toUpperCase(), m.group(2));
            }

            // illegal/legacy raw value
            if (isoValues.size() == 0) {
                // throw new IllegalArgumentException();
                if (defaultIso != null && defaultIso.trim().length() > 0) {
                    isoValues.put(defaultIso.toUpperCase(), rawValue);
                } else {
                    isoValues.put("EN", rawValue); //$NON-NLS-1$
                }
            }

            isoValues.put(iso, value);
        }

        StringBuilder result = new StringBuilder();
        if (isoValues.size() > 0) {

            List<String> isoList = new ArrayList<String>(isoValues.keySet());
            // sort
            if (sort) {
                Collections.sort(isoList);
            }

            for (String string : isoList) {
                String isoKey = string;
                String isoValue = isoValues.get(isoKey);
                result.append("[").append(isoKey).append(":").append(isoValue).append("]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }

        return result.toString();

    }

    /**
     * Give an ISO value from a multi-lingual text value, with default iso fallback
     * 
     * {talendTypes} String
     * 
     * {Category} MDM
     * 
     * {param} string(iso) iso: iso
     * 
     * {param} string(defaultIso) defaultIso: Default iso used in case of fallback
     * 
     * {param} string(rawValue) rawValue: rawValue
     * 
     * {example} getLanguageVariant("DE","EN","[EN:ab][FR:ab_fr]") # return [EN:ab]
     */
    public static String getLanguageVariant(String iso, String defaultIso, String rawValue) {
        String requestedLanguageVariant = getLanguageVariant(iso, rawValue);
        if (requestedLanguageVariant == null || "".equals(requestedLanguageVariant)) { //$NON-NLS-1$
            // fallback to the default variant
            return getLanguageVariant(defaultIso, rawValue);
        } else {
            return requestedLanguageVariant;
        }
    }

    /**
     * Give an ISO value from a multi-lingual text value
     * 
     * 
     * {talendTypes} String
     * 
     * {Category} MDM
     * 
     * {param} string(iso) iso: iso
     * 
     * {param} string(rawValue) rawValue: rawValue
     * 
     * {example} getLanguageVariant("FR","[EN:ab][FR:ab_fr]") # return ab_fr
     */
    public static String getLanguageVariant(String iso, String rawValue) {

        if (iso == null || rawValue == null) {
            throw new IllegalArgumentException();
        }

        iso = iso.toUpperCase();

        Map<String, String> isoValues = new HashMap<String, String>();
        Pattern p = Pattern.compile("\\[(\\w+)\\:([^\\[\\]]*?)\\]{1,}"); //$NON-NLS-1$
        Matcher m = p.matcher(rawValue);
        while (m.find()) {
            isoValues.put(m.group(1).toUpperCase(), m.group(2));
        }

        return isoValues.get(iso);
    }

    /**
     * Get webservice client timeout information from jvm param setting
     *  
     * {talendTypes} int
     * 
     * {Category} MDM
     * 
     * {param} String property: timeout property
     *  
     * {example} getWSClientTimeout(String property) # return 60000
     */
    public static int getWSClientTimeout(String property) throws Exception {
    	int defaultTimeout = 60000;
        String inputTimeout = System.getProperty(property);
        if (inputTimeout != null) {
            try {
            	int timeout = Integer.parseInt(inputTimeout);
            	if (timeout > 0) {
            		return timeout;
            	}
            } catch (Exception exception) {
                throw new RuntimeException("Webservice " + property + " property value '" + inputTimeout + "' is invalid", exception);
            }
        }
        return defaultTimeout;
    }
    
    // Utility methods
    /**
     * Get a nodelist from an xPath
     * 
     * @throws Exception
     */
    private static NodeList getNodeList(Node contextNode, String xPath, boolean isParent) throws Exception {
        if (!xPath.matches(".*@[^/\\]]+")) { //$NON-NLS-1$
            if (!xPath.endsWith(")") && !isParent) { //$NON-NLS-1$
                xPath += "/text()"; //$NON-NLS-1$
            }
        }
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath.compile(xPath);

        Object result = expr.evaluate(contextNode, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        return nodes;
    }

    /**
     * parse the xml
     * 
     * @param xml
     * @return
     * @throws Exception
     */
    private static Node parse(String xml) throws Exception {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true); // never forget this!
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));
        return doc.getDocumentElement();
    }

    /**
     * Generates an xml string from a node with or without the xml declaration (not pretty formatted)
     * 
     * @param n the node
     * @return the xml string
     * @throws TransformerException
     */
    private static String nodeToString(Node n, boolean omitXMLDeclaration) throws TransformerException {
        StringWriter sw = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        if (omitXMLDeclaration) {
            transformer.setOutputProperty("omit-xml-declaration", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            transformer.setOutputProperty("omit-xml-declaration", "no"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        transformer.setOutputProperty("indent", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
        transformer.transform(new DOMSource(n), new StreamResult(sw));
        return sw.toString().replaceAll("\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
