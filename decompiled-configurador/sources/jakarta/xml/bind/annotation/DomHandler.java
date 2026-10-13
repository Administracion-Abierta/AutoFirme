package jakarta.xml.bind.annotation;

import jakarta.xml.bind.ValidationEventHandler;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/annotation/DomHandler.class */
public interface DomHandler<ElementT, ResultT extends Result> {
    ResultT createUnmarshaller(ValidationEventHandler validationEventHandler);

    ElementT getElement(ResultT resultt);

    Source marshal(ElementT elementt, ValidationEventHandler validationEventHandler);
}
