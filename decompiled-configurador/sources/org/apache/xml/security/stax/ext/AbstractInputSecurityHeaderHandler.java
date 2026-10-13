package org.apache.xml.security.stax.ext;

import jakarta.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.XMLSecurityEventReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/ext/AbstractInputSecurityHeaderHandler.class */
public abstract class AbstractInputSecurityHeaderHandler implements XMLSecurityHeaderHandler {
    protected <T> T parseStructure(Deque<XMLSecEvent> deque, int i, XMLSecurityProperties xMLSecurityProperties) throws XMLSecurityException {
        try {
            return (T) XMLSecurityConstants.getJaxbUnmarshaller(xMLSecurityProperties.isDisableSchemaValidation()).unmarshal(new XMLSecurityEventReader(deque, i));
        } catch (JAXBException e) {
            if (e.getCause() != null && (e.getCause() instanceof Exception)) {
                throw new XMLSecurityException((Exception) e.getCause());
            }
            throw new XMLSecurityException(e);
        }
    }

    protected List<QName> getElementPath(Deque<XMLSecEvent> eventDeque) throws XMLSecurityException {
        XMLSecEvent xmlSecEvent = eventDeque.peek();
        return xmlSecEvent.getElementPath();
    }

    protected XMLSecEvent getResponsibleStartXMLEvent(Deque<XMLSecEvent> eventDeque, int index) {
        Iterator<XMLSecEvent> xmlSecEventIterator = eventDeque.descendingIterator();
        int curIdx = 0;
        while (true) {
            int i = curIdx;
            curIdx++;
            if (i < index) {
                xmlSecEventIterator.next();
            } else {
                return xmlSecEventIterator.next();
            }
        }
    }

    protected List<XMLSecEvent> getResponsibleXMLSecEvents(Deque<XMLSecEvent> xmlSecEvents, int index) {
        List<XMLSecEvent> xmlSecEventList = new ArrayList<>();
        Iterator<XMLSecEvent> xmlSecEventIterator = xmlSecEvents.descendingIterator();
        int curIdx = 0;
        while (true) {
            int i = curIdx;
            curIdx++;
            if (i >= index || !xmlSecEventIterator.hasNext()) {
                break;
            }
            xmlSecEventIterator.next();
        }
        while (xmlSecEventIterator.hasNext()) {
            xmlSecEventList.add(xmlSecEventIterator.next());
        }
        return xmlSecEventList;
    }
}
