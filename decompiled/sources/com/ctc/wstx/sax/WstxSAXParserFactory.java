package com.ctc.wstx.sax;

import com.ctc.wstx.stax.WstxInputFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sax/WstxSAXParserFactory.class */
public class WstxSAXParserFactory extends SAXParserFactory {
    protected final WstxInputFactory mStaxFactory;
    protected boolean mFeatNsPrefixes;

    public WstxSAXParserFactory() {
        this(new WstxInputFactory());
    }

    public WstxSAXParserFactory(WstxInputFactory f) {
        this.mFeatNsPrefixes = false;
        this.mStaxFactory = f;
        setNamespaceAware(true);
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        SAXFeature stdFeat = SAXFeature.findByUri(name);
        if (stdFeat == SAXFeature.EXTERNAL_GENERAL_ENTITIES) {
            return this.mStaxFactory.getConfig().willSupportExternalEntities();
        }
        if (stdFeat == SAXFeature.EXTERNAL_PARAMETER_ENTITIES) {
            return this.mStaxFactory.getConfig().willSupportExternalEntities();
        }
        if (stdFeat == SAXFeature.IS_STANDALONE || stdFeat == SAXFeature.LEXICAL_HANDLER_PARAMETER_ENTITIES) {
            return false;
        }
        if (stdFeat == SAXFeature.NAMESPACES) {
            return this.mStaxFactory.getConfig().willSupportNamespaces();
        }
        if (stdFeat == SAXFeature.NAMESPACE_PREFIXES) {
            return this.mFeatNsPrefixes;
        }
        if (stdFeat == SAXFeature.RESOLVE_DTD_URIS) {
            return false;
        }
        if (stdFeat == SAXFeature.STRING_INTERNING) {
            return this.mStaxFactory.getConfig().willInternNames();
        }
        if (stdFeat == SAXFeature.UNICODE_NORMALIZATION_CHECKING) {
            return false;
        }
        if (stdFeat == SAXFeature.USE_ATTRIBUTES2 || stdFeat == SAXFeature.USE_LOCATOR2 || stdFeat == SAXFeature.USE_ENTITY_RESOLVER2) {
            return true;
        }
        if (stdFeat == SAXFeature.VALIDATION) {
            return this.mStaxFactory.getConfig().willValidateWithDTD();
        }
        if (stdFeat == SAXFeature.XMLNS_URIS || stdFeat == SAXFeature.XML_1_1) {
            return true;
        }
        if (stdFeat == SAXFeature.JDK_SECURE_PROCESSING) {
            return this.mStaxFactory.getConfig().willProcessSecurely();
        }
        throw new SAXNotRecognizedException("Feature '" + name + "' not recognized");
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public SAXParser newSAXParser() {
        return new WstxSAXParser(this.mStaxFactory, this.mFeatNsPrefixes);
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        boolean invalidValue = false;
        boolean readOnly = false;
        SAXFeature stdFeat = SAXFeature.findByUri(name);
        if (stdFeat == SAXFeature.EXTERNAL_GENERAL_ENTITIES) {
            this.mStaxFactory.getConfig().doSupportExternalEntities(value);
        } else if (stdFeat != SAXFeature.EXTERNAL_PARAMETER_ENTITIES) {
            if (stdFeat == SAXFeature.IS_STANDALONE) {
                readOnly = true;
            } else if (stdFeat != SAXFeature.LEXICAL_HANDLER_PARAMETER_ENTITIES) {
                if (stdFeat == SAXFeature.NAMESPACES) {
                    this.mStaxFactory.getConfig().doSupportNamespaces(value);
                } else if (stdFeat == SAXFeature.NAMESPACE_PREFIXES) {
                    this.mFeatNsPrefixes = value;
                } else if (stdFeat != SAXFeature.RESOLVE_DTD_URIS) {
                    if (stdFeat == SAXFeature.STRING_INTERNING) {
                        invalidValue = !value;
                    } else if (stdFeat == SAXFeature.UNICODE_NORMALIZATION_CHECKING) {
                        invalidValue = value;
                    } else if (stdFeat == SAXFeature.USE_ATTRIBUTES2 || stdFeat == SAXFeature.USE_LOCATOR2 || stdFeat == SAXFeature.USE_ENTITY_RESOLVER2) {
                        readOnly = true;
                    } else if (stdFeat == SAXFeature.VALIDATION) {
                        this.mStaxFactory.getConfig().doValidateWithDTD(value);
                    } else if (stdFeat == SAXFeature.XMLNS_URIS) {
                        invalidValue = !value;
                    } else if (stdFeat == SAXFeature.XML_1_1) {
                        readOnly = true;
                    } else if (stdFeat == SAXFeature.JDK_SECURE_PROCESSING) {
                        this.mStaxFactory.getConfig().doProcessSecurely(value);
                    } else {
                        throw new SAXNotRecognizedException("Feature '" + name + "' not recognized");
                    }
                }
            }
        }
        if (readOnly) {
            throw new SAXNotSupportedException("Feature '" + name + "' is read-only, can not be modified");
        }
        if (invalidValue) {
            throw new SAXNotSupportedException("Trying to set invalid value for feature '" + name + "', '" + value + OperatorName.SHOW_TEXT_LINE);
        }
    }
}
