package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignedDataObjectProperties.class */
public class SignedDataObjectProperties extends XAdESStructure {
    private final Document document;

    public SignedDataObjectProperties(Document document, SignedProperties signedProperties, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(document, signedProperties, XAdESConstants.TAG_SIGNED_DATA_OBJECT_PROPERTIES, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        this.document = document;
    }

    public void setDataObjectFormat(ArrayList<DataObjectFormat> dataObjectFormat) {
        for (DataObjectFormat dof : dataObjectFormat) {
            new DataObjectFormatDetails(this.document, this, dof, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
    }

    public void setCommitmentTypeIndications(List<CommitmentTypeIndication> commitmentTypeIndications) {
        for (CommitmentTypeIndication commitmentTypeIndication : commitmentTypeIndications) {
            new CommitmentTypeIndicationDetails(this.document, this, commitmentTypeIndication, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
    }

    public void setAllDataObjectsTimeStamp(ArrayList<AllDataObjectsTimeStamp> allDataObjectsTimeStamp, String tsaURL) {
        for (AllDataObjectsTimeStamp adots : allDataObjectsTimeStamp) {
            new AllDataObjectsTimeStampDetails(this.document, this, adots, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix, tsaURL);
        }
    }

    public void setIndividualDataObjectsTimeStamp(ArrayList<IndividualDataObjectsTimeStamp> individualDataObjectsTimeStamp, String tsaURL) {
        for (IndividualDataObjectsTimeStamp idots : individualDataObjectsTimeStamp) {
            new IndividualDataObjectsTimeStampDetails(this.document, this, idots, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix, tsaURL);
        }
    }
}
