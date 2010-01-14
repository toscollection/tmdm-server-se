// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation ��1.1.2_01������� R40��
// Generated source version: 1.1.2

package com.amalto.webapp.util.webservices;

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

public class WSProcessTaskInstanceArray_LiteralSerializer extends LiteralObjectSerializerBase implements Initializable  {
    private static final QName ns1_wstaskinstance_QNAME = new QName("", "wstaskinstance");
    private static final QName ns2_WSProcessTaskInstance_TYPE_QNAME = new QName("urn-com-amalto-xtentis-webservice", "WSProcessTaskInstance");
    private CombinedSerializer ns2_myWSProcessTaskInstance_LiteralSerializer;
    
    public WSProcessTaskInstanceArray_LiteralSerializer(QName type, String encodingStyle) {
        this(type, encodingStyle, false);
    }
    
    public WSProcessTaskInstanceArray_LiteralSerializer(QName type, String encodingStyle, boolean encodeType) {
        super(type, true, encodingStyle, encodeType);
    }
    
    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        ns2_myWSProcessTaskInstance_LiteralSerializer = (CombinedSerializer)registry.getSerializer("", com.amalto.webapp.util.webservices.WSProcessTaskInstance.class, ns2_WSProcessTaskInstance_TYPE_QNAME);
    }
    
    public Object doDeserialize(XMLReader reader,
        SOAPDeserializationContext context) throws Exception {
        com.amalto.webapp.util.webservices.WSProcessTaskInstanceArray instance = new com.amalto.webapp.util.webservices.WSProcessTaskInstanceArray();
        Object member=null;
        QName elementName;
        List values;
        Object value;
        
        reader.nextElementContent();
        elementName = reader.getName();
        if ((reader.getState() == XMLReader.START) && (elementName.equals(ns1_wstaskinstance_QNAME))) {
            values = new ArrayList();
            for(;;) {
                elementName = reader.getName();
                if ((reader.getState() == XMLReader.START) && (elementName.equals(ns1_wstaskinstance_QNAME))) {
                    value = ns2_myWSProcessTaskInstance_LiteralSerializer.deserialize(ns1_wstaskinstance_QNAME, reader, context);
                    if (value == null) {
                        throw new DeserializationException("literal.unexpectedNull");
                    }
                    values.add(value);
                    reader.nextElementContent();
                } else {
                    break;
                }
            }
            member = new com.amalto.webapp.util.webservices.WSProcessTaskInstance[values.size()];
            member = values.toArray((Object[]) member);
            instance.setWstaskinstance((com.amalto.webapp.util.webservices.WSProcessTaskInstance[])member);
        }
        else if(!(reader.getState() == XMLReader.END)) {
            throw new DeserializationException("literal.expectedElementName", reader.getName().toString());
        }
        
        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        return (Object)instance;
    }
    
    public void doSerializeAttributes(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        com.amalto.webapp.util.webservices.WSProcessTaskInstanceArray instance = (com.amalto.webapp.util.webservices.WSProcessTaskInstanceArray)obj;
        
    }
    public void doSerialize(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        com.amalto.webapp.util.webservices.WSProcessTaskInstanceArray instance = (com.amalto.webapp.util.webservices.WSProcessTaskInstanceArray)obj;
        
        if (instance.getWstaskinstance() != null) {
            for (int i = 0; i < instance.getWstaskinstance().length; ++i) {
                ns2_myWSProcessTaskInstance_LiteralSerializer.serialize(instance.getWstaskinstance()[i], ns1_wstaskinstance_QNAME, null, writer, context);
            }
        }
    }
}
