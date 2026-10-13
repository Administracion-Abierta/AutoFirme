package com.ctc.wstx.shaded.msv_core.grammar.util;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/util/NameClassCollisionChecker.class */
public class NameClassCollisionChecker extends NameClassComparator {
    @Override // com.ctc.wstx.shaded.msv_core.grammar.util.NameClassComparator
    protected void probe(String uri, String local) {
        if (this.nc1.accepts(uri, local) && this.nc2.accepts(uri, local)) {
            throw this.eureka;
        }
    }
}
