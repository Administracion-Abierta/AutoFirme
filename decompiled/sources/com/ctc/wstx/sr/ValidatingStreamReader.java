package com.ctc.wstx.sr;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.cfg.ParsingErrorMsgs;
import com.ctc.wstx.dtd.DTDId;
import com.ctc.wstx.dtd.DTDSubset;
import com.ctc.wstx.dtd.DTDValidatorBase;
import com.ctc.wstx.dtd.FullDTDReader;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.io.BranchingReaderSource;
import com.ctc.wstx.io.DefaultInputResolver;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.util.URLUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.NotationDeclaration;
import org.codehaus.stax2.validation.DTDValidationSchema;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationProblem;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sr/ValidatingStreamReader.class */
public class ValidatingStreamReader extends TypedStreamReader {
    static final String STAX_PROP_ENTITIES = "javax.xml.stream.entities";
    static final String STAX_PROP_NOTATIONS = "javax.xml.stream.notations";
    DTDValidationSchema mDTD;
    XMLValidator mAutoDtdValidator;
    boolean mDtdValidatorSet;
    protected ValidationProblemHandler mVldProbHandler;

    private ValidatingStreamReader(InputBootstrapper bs, BranchingReaderSource input, ReaderCreator owner, ReaderConfig cfg, InputElementStack elemStack, boolean forER) throws XMLStreamException {
        super(bs, input, owner, cfg, elemStack, forER);
        this.mDTD = null;
        this.mAutoDtdValidator = null;
        this.mDtdValidatorSet = false;
        this.mVldProbHandler = null;
    }

    public static ValidatingStreamReader createValidatingStreamReader(BranchingReaderSource input, ReaderCreator owner, ReaderConfig cfg, InputBootstrapper bs, boolean forER) throws XMLStreamException {
        ValidatingStreamReader sr = new ValidatingStreamReader(bs, input, owner, cfg, createElementStack(cfg), forER);
        return sr;
    }

    @Override // com.ctc.wstx.sr.BasicStreamReader
    public Object getProperty(String name) {
        if (name.equals(STAX_PROP_ENTITIES)) {
            safeEnsureFinishToken();
            if (this.mDTD == null || !(this.mDTD instanceof DTDSubset)) {
                return null;
            }
            List<EntityDecl> l = ((DTDSubset) this.mDTD).getGeneralEntityList();
            return new ArrayList(l);
        }
        if (name.equals(STAX_PROP_NOTATIONS)) {
            safeEnsureFinishToken();
            if (this.mDTD == null || !(this.mDTD instanceof DTDSubset)) {
                return null;
            }
            List<NotationDeclaration> l2 = ((DTDSubset) this.mDTD).getNotationList();
            return new ArrayList(l2);
        }
        return super.getProperty(name);
    }

    @Override // com.ctc.wstx.sr.BasicStreamReader, org.codehaus.stax2.DTDInfo
    public Object getProcessedDTD() {
        return getProcessedDTDSchema();
    }

    @Override // com.ctc.wstx.sr.BasicStreamReader, org.codehaus.stax2.DTDInfo
    public DTDValidationSchema getProcessedDTDSchema() {
        DTDValidationSchema dtd = this.mConfig.getDTDOverride();
        if (dtd == null) {
            DTDValidationSchema dTDValidationSchema = this.mDTD;
        }
        return this.mDTD;
    }

    @Override // com.ctc.wstx.sr.BasicStreamReader, org.codehaus.stax2.validation.Validatable
    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return this.mElementStack.validateAgainst(schema);
    }

    @Override // com.ctc.wstx.sr.BasicStreamReader, org.codehaus.stax2.validation.Validatable
    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return this.mElementStack.stopValidatingAgainst(schema);
    }

    @Override // com.ctc.wstx.sr.BasicStreamReader, org.codehaus.stax2.validation.Validatable
    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        return this.mElementStack.stopValidatingAgainst(validator);
    }

    @Override // com.ctc.wstx.sr.BasicStreamReader, org.codehaus.stax2.validation.Validatable
    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
        ValidationProblemHandler oldH = this.mVldProbHandler;
        this.mVldProbHandler = h;
        return oldH;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sr.BasicStreamReader
    protected void finishDTD(boolean copyContents) throws XMLStreamException {
        if (!hasConfigFlags(16)) {
            super.finishDTD(copyContents);
            return;
        }
        char c = getNextChar(ParsingErrorMsgs.SUFFIX_IN_DTD);
        DTDSubset intSubset = null;
        if (c == '[') {
            if (copyContents) {
                ((BranchingReaderSource) this.mInput).startBranch(this.mTextBuffer, this.mInputPtr, this.mNormalizeLFs);
            }
            try {
                intSubset = FullDTDReader.readInternalSubset(this, this.mInput, this.mConfig, hasConfigFlags(32), this.mDocXmlVersion);
                if (copyContents) {
                    ((BranchingReaderSource) this.mInput).endBranch(this.mInputPtr - 1);
                }
                c = getNextCharAfterWS(ParsingErrorMsgs.SUFFIX_IN_DTD_INTERNAL);
            } catch (Throwable th) {
                if (copyContents) {
                    ((BranchingReaderSource) this.mInput).endBranch(this.mInputPtr - 1);
                }
                throw th;
            }
        }
        if (c != '>') {
            throwUnexpectedChar(c, "; expected '>' to finish DOCTYPE declaration.");
        }
        this.mDTD = this.mConfig.getDTDOverride();
        if (this.mDTD == null) {
            DTDSubset extSubset = null;
            if (this.mDtdPublicId != null || this.mDtdSystemId != null) {
                extSubset = findDtdExtSubset(this.mDtdPublicId, this.mDtdSystemId, intSubset);
            }
            if (intSubset == null) {
                this.mDTD = extSubset;
            } else if (extSubset == null) {
                this.mDTD = intSubset;
            } else {
                this.mDTD = intSubset.combineWithExternalSubset(this, extSubset);
            }
        }
        if (this.mDTD == null) {
            this.mGeneralEntities = null;
            return;
        }
        if (this.mDTD instanceof DTDSubset) {
            this.mGeneralEntities = ((DTDSubset) this.mDTD).getGeneralEntityMap();
        } else {
            _reportProblem(this.mConfig.getXMLReporter(), ErrorConsts.WT_DT_DECL, "Value to set for property 'org.codehaus.stax2.propDtdOverride' not a native Woodstox DTD implementation (but " + this.mDTD.getClass() + "): can not access full entity or notation information", null);
        }
        this.mAutoDtdValidator = this.mDTD.createValidator(this.mElementStack);
        this.mDtdValidatorSet = true;
        NsDefaultProvider nsDefs = null;
        if (this.mAutoDtdValidator instanceof DTDValidatorBase) {
            DTDValidatorBase dtdv = (DTDValidatorBase) this.mAutoDtdValidator;
            dtdv.setAttrValueNormalization(true);
            if (dtdv.hasNsDefaults()) {
                nsDefs = dtdv;
            }
        }
        this.mElementStack.setAutomaticDTDValidator(this.mAutoDtdValidator, nsDefs);
    }

    @Override // com.ctc.wstx.sr.StreamScanner, com.ctc.wstx.sr.InputProblemReporter
    public void reportValidationProblem(XMLValidationProblem prob) throws XMLStreamException {
        if (this.mVldProbHandler != null) {
            this.mVldProbHandler.reportProblem(prob);
        } else {
            super.reportValidationProblem(prob);
        }
    }

    @Override // com.ctc.wstx.sr.BasicStreamReader
    protected void initValidation() throws XMLStreamException {
        if (hasConfigFlags(32) && !this.mDtdValidatorSet) {
            reportProblem(null, ErrorConsts.WT_DT_DECL, ErrorConsts.W_MISSING_DTD, null, null);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private DTDSubset findDtdExtSubset(String pubId, String sysId, DTDSubset intSubset) throws XMLStreamException {
        DTDSubset extSubset;
        boolean cache = hasConfigFlags(65536);
        try {
            DTDId dtdId = constructDtdId(pubId, sysId);
            if (cache && (extSubset = findCachedSubset(dtdId, intSubset)) != null) {
                return extSubset;
            }
            if (sysId == null) {
                throwParseError("Can not resolve DTD with public id \"{0}\"; missing system identifier", this.mDtdPublicId, null);
            }
            WstxInputSource src = null;
            try {
                int xmlVersion = this.mDocXmlVersion;
                if (xmlVersion == 0) {
                    xmlVersion = 256;
                }
                src = DefaultInputResolver.resolveEntity(this.mInput, null, null, pubId, sysId, this.mConfig.getDtdResolver(), this.mConfig, xmlVersion);
            } catch (FileNotFoundException fex) {
                throwParseError("(was {0}) {1}", fex.getClass().getName(), fex.getMessage());
            } catch (IOException ioe) {
                throwFromIOE(ioe);
            }
            DTDSubset extSubset2 = FullDTDReader.readExternalSubset(src, this.mConfig, intSubset, hasConfigFlags(32), this.mDocXmlVersion);
            if (cache && extSubset2.isCachable()) {
                this.mOwner.addCachedDTD(dtdId, extSubset2);
            }
            return extSubset2;
        } catch (IOException ioe2) {
            throw constructFromIOE(ioe2);
        }
    }

    private DTDSubset findCachedSubset(DTDId id, DTDSubset intSubset) throws XMLStreamException {
        DTDSubset extSubset = this.mOwner.findCachedDTD(id);
        if (extSubset != null) {
            if (intSubset == null || extSubset.isReusableWith(intSubset)) {
                return extSubset;
            }
            return null;
        }
        return null;
    }

    private URI resolveExtSubsetPath(String systemId) throws IOException {
        URL ctxt = this.mInput == null ? null : this.mInput.getSource();
        if (ctxt == null) {
            return URLUtil.uriFromSystemId(systemId);
        }
        URL url = URLUtil.urlFromSystemId(systemId, ctxt);
        try {
            return new URI(url.toExternalForm());
        } catch (URISyntaxException e) {
            throw new IOException("Failed to construct URI for external subset, URL = " + url.toExternalForm() + ": " + e.getMessage());
        }
    }

    protected DTDId constructDtdId(String pubId, String sysId) throws IOException {
        int significantFlags = this.mConfigFlags & 2621473;
        URI sysRef = (sysId == null || sysId.length() == 0) ? null : resolveExtSubsetPath(sysId);
        boolean usePublicId = (this.mConfigFlags & 131072) != 0;
        if (usePublicId && pubId != null && pubId.length() > 0) {
            return DTDId.construct(pubId, sysRef, significantFlags, this.mXml11);
        }
        if (sysRef == null) {
            return null;
        }
        return DTDId.constructFromSystemId(sysRef, significantFlags, this.mXml11);
    }

    protected DTDId constructDtdId(URI sysId) throws IOException {
        int significantFlags = this.mConfigFlags & 524321;
        return DTDId.constructFromSystemId(sysId, significantFlags, this.mXml11);
    }

    @Override // com.ctc.wstx.sr.BasicStreamReader
    protected void reportInvalidContent(int evtType) throws XMLStreamException {
        switch (this.mVldContent) {
            case 0:
                reportValidationProblem(ErrorConsts.ERR_VLD_EMPTY, this.mElementStack.getTopElementDesc(), ErrorConsts.tokenTypeDesc(evtType));
                break;
            case 1:
            case 2:
                reportValidationProblem(ErrorConsts.ERR_VLD_NON_MIXED, this.mElementStack.getTopElementDesc(), null);
                break;
            case 3:
            case 4:
                reportValidationProblem(ErrorConsts.ERR_VLD_ANY, this.mElementStack.getTopElementDesc(), ErrorConsts.tokenTypeDesc(evtType));
                break;
            default:
                throwParseError("Internal error: trying to report invalid content for " + evtType);
                break;
        }
    }
}
