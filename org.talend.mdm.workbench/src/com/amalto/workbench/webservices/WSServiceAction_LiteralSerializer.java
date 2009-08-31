// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation ��1.1.2_01������� R40��
// Generated source version: 1.1.2

package com.amalto.workbench.webservices;

import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.encoding.literal.*;
import com.sun.xml.rpc.encoding.literal.DetailFragmentDeserializer;
import com.sun.xml.rpc.encoding.simpletype.*;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.encoding.soap.SOAP12Constants;
import com.sun.xml.rpc.streaming.*;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.ArrayList;

public class WSServiceAction_LiteralSerializer extends LiteralObjectSerializerBase implements Initializable  {
    private static final QName ns1_jndiName_QNAME = new QName("", "jndiName");
    private static final QName ns3_string_TYPE_QNAME = SchemaConstants.QNAME_TYPE_STRING;
    private CombinedSerializer ns3_myns3_string__java_lang_String_String_Serializer;
    private static final QName ns1_wsAction_QNAME = new QName("", "wsAction");
    private static final QName ns2_WSServiceActionCode_TYPE_QNAME = new QName("urn-com-amalto-xtentis-webservice", "WSServiceActionCode");
    private CombinedSerializer ns2myns2_WSServiceActionCode__WSServiceActionCode_LiteralSerializer;
    private static final QName ns1_methodName_QNAME = new QName("", "methodName");
    private static final QName ns1_methodParameters_QNAME = new QName("", "methodParameters");
    
    public WSServiceAction_LiteralSerializer(QName type, String encodingStyle) {
        this(type, encodingStyle, false);
    }
    
    public WSServiceAction_LiteralSerializer(QName type, String encodingStyle, boolean encodeType) {
        super(type, true, encodingStyle, encodeType);
    }
    
    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        ns3_myns3_string__java_lang_String_String_Serializer = (CombinedSerializer)registry.getSerializer("", java.lang.String.class, ns3_string_TYPE_QNAME);
        ns2myns2_WSServiceActionCode__WSServiceActionCode_LiteralSerializer = (CombinedSerializer)registry.getSerializer("", com.amalto.workbench.webservices.WSServiceActionCode.class, ns2_WSServiceActionCode_TYPE_QNAME);
    }
    
    public Object doDeserialize(XMLReader reader,
        SOAPDeserializationContext context) throws Exception {
        com.amalto.workbench.webservices.WSServiceAction instance = new com.amalto.workbench.webservices.WSServiceAction();
        Object member=null;
        QName elementName;
        List values;
        Object value;
        
        reader.nextElementContent();
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(ns1_jndiName_QNAME)) {
                member = ns3_myns3_string__java_lang_String_String_Serializer.deserialize(ns1_jndiName_QNAME, reader, context);
                if (member == null) {
                    throw new DeserializationException("literal.unexpectedNull");
                }
                instance.setJndiName((java.lang.String)member);
                reader.nextElementContent();
            } else {
                throw new DeserializationException("literal.unexpectedElementName", new Object[] { ns1_jndiName_QNAME, reader.getName() });
            }
        }
        else {
            throw new DeserializationException("literal.expectedElementName", reader.getName().toString());
        }
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(ns1_wsAction_QNAME)) {
                member = ns2myns2_WSServiceActionCode__WSServiceActionCode_LiteralSerializer.deserialize(ns1_wsAction_QNAME, reader, context);
                if (member == null) {
                    throw new DeserializationException("literal.unexpectedNull");
                }
                instance.setWsAction((com.amalto.workbench.webservices.WSServiceActionCode)member);
                reader.nextElementContent();
            } else {
                throw new DeserializationException("literal.unexpectedElementName", new Object[] { ns1_wsAction_QNAME, reader.getName() });
            }
        }
        else {
            throw new DeserializationException("literal.expectedElementName", reader.getName().toString());
        }
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(ns1_methodName_QNAME)) {
                member = ns3_myns3_string__java_lang_String_String_Serializer.deserialize(ns1_methodName_QNAME, reader, context);
                instance.setMethodName((java.lang.String)member);
                reader.nextElementContent();
            }
        }
        elementName = reader.getName();
        if ((reader.getState() == XMLReader.START) && (elementName.equals(ns1_methodParameters_QNAME))) {
            values = new ArrayList();
            for(;;) {
                elementName = reader.getName();
                if ((reader.getState() == XMLReader.START) && (elementName.equals(ns1_methodParameters_QNAME))) {
                    value = ns3_myns3_string__java_lang_String_String_Serializer.deserialize(ns1_methodParameters_QNAME, reader, context);
                    values.add(value);
                    reader.nextElementContent();
                } else {
                    break;
                }
            }
            member = new java.lang.String[values.size()];
            member = values.toArray((Object[]) member);
            instance.setMethodParameters((java.lang.String[])member);
        }
        else {
            instance.setMethodParameters(new java.lang.String[0]);
        }
        
        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        return (Object)instance;
    }
    
    public void doSerializeAttributes(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        com.amalto.workbench.webservices.WSServiceAction instance = (com.amalto.workbench.webservices.WSServiceAction)obj;
        
    }
    public void doSerialize(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        com.amalto.workbench.webservices.WSServiceAction instance = (com.amalto.workbench.webservices.WSServiceAction)obj;
        
        if (instance.getJndiName() == null) {
            throw new SerializationException("literal.unexpectedNull");
        }
        ns3_myns3_string__java_lang_String_String_Serializer.serialize(instance.getJndiName(), ns1_jndiName_QNAME, null, writer, context);
        if (instance.getWsAction() == null) {
            throw new SerializationException("literal.unexpectedNull");
        }
        ns2myns2_WSServiceActionCode__WSServiceActionCode_LiteralSerializer.serialize(instance.getWsAction(), ns1_wsAction_QNAME, null, writer, context);
        ns3_myns3_string__java_lang_String_String_Serializer.serialize(instance.getMethodName(), ns1_methodName_QNAME, null, writer, context);
        if (instance.getMethodParameters() != null) {
            for (int i = 0; i < instance.getMethodParameters().length; ++i) {
                ns3_myns3_string__java_lang_String_String_Serializer.serialize(instance.getMethodParameters()[i], ns1_methodParameters_QNAME, null, writer, context);
            }
        }
    }
}
