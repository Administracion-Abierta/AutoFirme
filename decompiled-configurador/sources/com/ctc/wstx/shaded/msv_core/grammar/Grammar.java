package com.ctc.wstx.shaded.msv_core.grammar;

import java.io.Serializable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/Grammar.class */
public interface Grammar extends Serializable {
    Expression getTopLevel();

    ExpressionPool getPool();
}
