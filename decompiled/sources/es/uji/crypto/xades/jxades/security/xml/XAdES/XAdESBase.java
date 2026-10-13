package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.Date;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/XAdESBase.class */
public interface XAdESBase {
    Element getBaseElement();

    Document getBaseDocument();

    Date getSigningTime();

    void setSigningTime(Date date);

    Signer getSigner();

    void setSigner(Signer signer);

    List<DataObjectFormat> getDataObjectFormats();

    void setDataObjectFormats(List<DataObjectFormat> list);

    List<CommitmentTypeIndication> getCommitmentTypeIndications();

    void setCommitmentTypeIndications(List<CommitmentTypeIndication> list);

    List<AllDataObjectsTimeStamp> getAllDataObjectsTimeStamps();

    void setAllDataObjectsTimeStamps(List<AllDataObjectsTimeStamp> list);

    List<XAdESTimeStamp> getIndividualDataObjectsTimeStamps();

    void setIndividualDataObjectsTimeStamps(List<IndividualDataObjectsTimeStamp> list);

    List<CounterSignature> getCounterSignatures();

    void setCounterSignatures(List<CounterSignature> list);

    String getXadesPrefix();

    String getXadesNamespace();

    String getXmlSignaturePrefix();

    String getDigestMethod();
}
