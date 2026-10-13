package jakarta.xml.bind.annotation.adapters;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/annotation/adapters/XmlAdapter.class */
public abstract class XmlAdapter<ValueType, BoundType> {
    public abstract BoundType unmarshal(ValueType valuetype) throws Exception;

    public abstract ValueType marshal(BoundType boundtype) throws Exception;

    protected XmlAdapter() {
    }
}
