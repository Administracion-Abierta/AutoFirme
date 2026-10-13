package org.codehaus.stax2;

import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationProblem;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/XMLReporter2.class */
public interface XMLReporter2 extends XMLReporter {
    void report(XMLValidationProblem xMLValidationProblem) throws XMLStreamException;
}
