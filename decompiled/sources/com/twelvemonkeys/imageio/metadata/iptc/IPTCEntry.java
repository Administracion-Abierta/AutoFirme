package com.twelvemonkeys.imageio.metadata.iptc;

import com.twelvemonkeys.imageio.metadata.AbstractEntry;
import es.gob.afirma.signers.xades.XAdESConstants;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/iptc/IPTCEntry.class */
class IPTCEntry extends AbstractEntry {
    public IPTCEntry(int i, Object obj) {
        super(Integer.valueOf(i), obj);
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractEntry, com.twelvemonkeys.imageio.metadata.Entry
    public String getFieldName() {
        switch (((Integer) getIdentifier()).intValue()) {
            case 512:
                return "RecordVersion";
            case 537:
                return "Keywords";
            case 552:
                return "Instructions";
            case 567:
                return "DateCreated";
            case 572:
                return "TimeCreated";
            case 574:
                return "DigitalCreationDate";
            case 575:
                return "DigitalCreationTime";
            case 592:
                return "ByLine";
            case 597:
                return "ByLineTitle";
            case 602:
                return XAdESConstants.TAG_CITY;
            case 604:
                return "SubLocation";
            case 607:
                return "StateProvince";
            case 612:
                return "CountryCode";
            case 613:
                return "Country";
            case 627:
                return "Source";
            case 628:
                return "CopyrightNotice";
            case 632:
                return StandardStructureTypes.CAPTION;
            default:
                return null;
        }
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractEntry
    protected String getNativeIdentifier() {
        int iIntValue = ((Integer) getIdentifier()).intValue();
        return String.format("%d:%02d", Integer.valueOf(iIntValue >> 8), Integer.valueOf(iIntValue & 255));
    }
}
