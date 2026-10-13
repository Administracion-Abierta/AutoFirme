package es.gob.jmulticard.callback;

import java.io.Serializable;
import javax.security.auth.callback.Callback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/callback/CustomTextInputCallback.class */
public final class CustomTextInputCallback implements Callback, Serializable {
    private static final long serialVersionUID = -8064222478852811804L;
    private final String prompt;
    private String inputText;

    public CustomTextInputCallback(String p) {
        this.prompt = p;
    }

    public CustomTextInputCallback() {
        this.prompt = null;
    }

    public void setText(String text) {
        this.inputText = text;
    }

    public String getText() {
        return this.inputText;
    }

    public String getPrompt() {
        return this.prompt;
    }
}
