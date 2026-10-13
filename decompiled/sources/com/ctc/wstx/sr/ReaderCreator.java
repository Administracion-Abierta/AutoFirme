package com.ctc.wstx.sr;

import com.ctc.wstx.dtd.DTDId;
import com.ctc.wstx.dtd.DTDSubset;
import com.ctc.wstx.util.SymbolTable;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sr/ReaderCreator.class */
public interface ReaderCreator {
    DTDSubset findCachedDTD(DTDId dTDId);

    void updateSymbolTable(SymbolTable symbolTable);

    void addCachedDTD(DTDId dTDId, DTDSubset dTDSubset);
}
