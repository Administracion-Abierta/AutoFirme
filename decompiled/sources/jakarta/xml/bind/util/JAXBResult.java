package jakarta.xml.bind.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.UnmarshallerHandler;
import javax.xml.transform.sax.SAXResult;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/util/JAXBResult.class */
public class JAXBResult extends SAXResult {
    private final UnmarshallerHandler unmarshallerHandler;

    public JAXBResult(JAXBContext context) throws JAXBException {
        this(context == null ? assertionFailed() : context.createUnmarshaller());
    }

    public JAXBResult(Unmarshaller _unmarshaller) throws JAXBException {
        if (_unmarshaller == null) {
            throw new JAXBException(Messages.format("JAXBResult.NullUnmarshaller"));
        }
        this.unmarshallerHandler = _unmarshaller.getUnmarshallerHandler();
        super.setHandler(this.unmarshallerHandler);
    }

    public Object getResult() throws JAXBException {
        return this.unmarshallerHandler.getResult();
    }

    private static Unmarshaller assertionFailed() throws JAXBException {
        throw new JAXBException(Messages.format("JAXBResult.NullContext"));
    }
}
