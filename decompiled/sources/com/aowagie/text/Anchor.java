package com.aowagie.text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Anchor.class */
public class Anchor extends Phrase {
    private static final long serialVersionUID = -852278536049236911L;
    private String name;
    private String reference;

    public Anchor() {
        super(16.0f);
        this.name = null;
        this.reference = null;
    }

    public Anchor(Phrase phrase) {
        super(phrase);
        this.name = null;
        this.reference = null;
        if (phrase instanceof Anchor) {
            Anchor a = (Anchor) phrase;
            setName(a.name);
            setReference(a.reference);
        }
    }

    @Override // com.aowagie.text.Phrase, com.aowagie.text.Element
    public boolean process(ElementListener listener) {
        try {
            boolean localDestination = this.reference != null && this.reference.startsWith("#");
            boolean notGotoOK = true;
            for (Chunk chunk : getChunks()) {
                if (this.name != null && notGotoOK && !chunk.isEmpty()) {
                    chunk.setLocalDestination(this.name);
                    notGotoOK = false;
                }
                if (localDestination) {
                    chunk.setLocalGoto(this.reference.substring(1));
                }
                listener.add(chunk);
            }
            return true;
        } catch (DocumentException e) {
            return false;
        }
    }

    @Override // com.aowagie.text.Phrase, com.aowagie.text.Element
    public ArrayList getChunks() {
        ArrayList tmp = new ArrayList();
        Iterator i = iterator();
        boolean localDestination = this.reference != null && this.reference.startsWith("#");
        boolean notGotoOK = true;
        while (i.hasNext()) {
            Chunk chunk = (Chunk) i.next();
            if (this.name != null && notGotoOK && !chunk.isEmpty()) {
                chunk.setLocalDestination(this.name);
                notGotoOK = false;
            }
            if (localDestination) {
                chunk.setLocalGoto(this.reference.substring(1));
            } else if (this.reference != null) {
                chunk.setAnchor(this.reference);
            }
            tmp.add(chunk);
        }
        return tmp;
    }

    @Override // com.aowagie.text.Phrase, com.aowagie.text.Element
    public int type() {
        return 17;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getName() {
        return this.name;
    }

    public String getReference() {
        return this.reference;
    }

    public URL getUrl() {
        try {
            return new URL(this.reference);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
