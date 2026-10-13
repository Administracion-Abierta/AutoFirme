package com.ctc.wstx.sr;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationProblem;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sr/InputProblemReporter.class */
public interface InputProblemReporter {
    void throwParseError(String str) throws XMLStreamException;

    void throwParseError(String str, Object obj, Object obj2) throws XMLStreamException;

    void reportValidationProblem(XMLValidationProblem xMLValidationProblem) throws XMLStreamException;

    void reportValidationProblem(String str) throws XMLStreamException;

    void reportValidationProblem(String str, Object obj, Object obj2) throws XMLStreamException;

    void reportProblem(Location location, String str, String str2, Object obj, Object obj2) throws XMLStreamException;

    Location getLocation();
}
