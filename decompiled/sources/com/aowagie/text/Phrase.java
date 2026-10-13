package com.aowagie.text;

import com.aowagie.text.pdf.HyphenationEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Phrase.class */
public class Phrase extends ArrayList implements TextElementArray {
    private static final long serialVersionUID = 2643594602455068231L;
    protected float leading;
    protected Font font;
    private HyphenationEvent hyphenation;

    public Phrase() {
        this(16.0f);
    }

    Phrase(Phrase phrase) {
        this.leading = Float.NaN;
        this.hyphenation = null;
        addAll(phrase);
        this.leading = phrase.getLeading();
        this.font = phrase.getFont();
        setHyphenation(phrase.getHyphenation());
    }

    Phrase(float leading) {
        this.leading = Float.NaN;
        this.hyphenation = null;
        this.leading = leading;
        this.font = new Font();
    }

    public Phrase(Chunk chunk) {
        this.leading = Float.NaN;
        this.hyphenation = null;
        super.add(chunk);
        this.font = chunk.getFont();
        setHyphenation(chunk.getHyphenation());
    }

    public Phrase(String string) {
        this(Float.NaN, string, new Font());
    }

    public Phrase(String string, Font font) {
        this(Float.NaN, string, font);
    }

    public Phrase(float leading, String string, Font font) {
        this.leading = Float.NaN;
        this.hyphenation = null;
        this.leading = leading;
        this.font = font;
        if (string != null && string.length() != 0) {
            super.add(new Chunk(string, font));
        }
    }

    public boolean process(ElementListener listener) {
        try {
            Iterator i = iterator();
            while (i.hasNext()) {
                listener.add((Element) i.next());
            }
            return true;
        } catch (DocumentException e) {
            return false;
        }
    }

    public int type() {
        return 11;
    }

    public ArrayList getChunks() {
        ArrayList tmp = new ArrayList();
        Iterator i = iterator();
        while (i.hasNext()) {
            tmp.addAll(((Element) i.next()).getChunks());
        }
        return tmp;
    }

    @Override // com.aowagie.text.Element
    public boolean isContent() {
        return true;
    }

    @Override // com.aowagie.text.Element
    public boolean isNestable() {
        return true;
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
    public void add(int index, Object o) {
        if (o == null) {
            return;
        }
        try {
            Element element = (Element) o;
            if (element.type() == 10) {
                Chunk chunk = (Chunk) element;
                if (!this.font.isStandardFont()) {
                    chunk.setFont(this.font.difference(chunk.getFont()));
                }
                if (this.hyphenation != null && chunk.getHyphenation() == null && !chunk.isEmpty()) {
                    chunk.setHyphenation(this.hyphenation);
                }
                super.add(index, chunk);
            } else if (element.type() == 11 || element.type() == 17 || element.type() == 29 || element.type() == 22 || element.type() == 55 || element.type() == 50) {
                super.add(index, element);
            } else {
                throw new ClassCastException(String.valueOf(element.type()));
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Insertion of illegal Element: " + cce.getMessage());
        }
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List, com.aowagie.text.TextElementArray
    public boolean add(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof String) {
            return super.add(new Chunk((String) o, this.font));
        }
        if (o instanceof RtfElementInterface) {
            return super.add(o);
        }
        try {
            Element element = (Element) o;
            switch (element.type()) {
                case 10:
                    return addChunk((Chunk) o);
                case 11:
                case 12:
                    Phrase<Element> phrase = (Phrase) o;
                    boolean success = true;
                    for (Element e : phrase) {
                        if (e instanceof Chunk) {
                            success &= addChunk((Chunk) e);
                        } else {
                            success &= add(e);
                        }
                    }
                    return success;
                case 14:
                case 17:
                case 22:
                case 23:
                case 29:
                case 50:
                case 55:
                    return super.add(o);
                default:
                    throw new ClassCastException(String.valueOf(element.type()));
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Insertion of illegal Element: " + cce.getMessage());
        }
    }

    @Override // java.util.ArrayList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean addAll(Collection collection) {
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            add(iterator.next());
        }
        return true;
    }

    private boolean addChunk(Chunk chunk) {
        Font f = chunk.getFont();
        String c = chunk.getContent();
        if (this.font != null && !this.font.isStandardFont()) {
            f = this.font.difference(chunk.getFont());
        }
        if (size() > 0 && !chunk.hasAttributes()) {
            try {
                Chunk previous = (Chunk) get(size() - 1);
                if (!previous.hasAttributes() && ((f == null || f.compareTo(previous.getFont()) == 0) && !"".equals(previous.getContent().trim()) && !"".equals(c.trim()))) {
                    previous.append(c);
                    return true;
                }
            } catch (ClassCastException e) {
            }
        }
        Chunk newChunk = new Chunk(c, f);
        newChunk.setAttributes(chunk.getAttributes());
        if (this.hyphenation != null && newChunk.getHyphenation() == null && !newChunk.isEmpty()) {
            newChunk.setHyphenation(this.hyphenation);
        }
        return super.add(newChunk);
    }

    protected void addSpecial(Object object) {
        super.add(object);
    }

    public void setLeading(float leading) {
        this.leading = leading;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public float getLeading() {
        if (Float.isNaN(this.leading) && this.font != null) {
            return this.font.getCalculatedLeading(1.5f);
        }
        return this.leading;
    }

    boolean hasLeading() {
        if (Float.isNaN(this.leading)) {
            return false;
        }
        return true;
    }

    public Font getFont() {
        return this.font;
    }

    public String getContent() {
        StringBuffer buf = new StringBuffer();
        Iterator i = getChunks().iterator();
        while (i.hasNext()) {
            buf.append(i.next().toString());
        }
        return buf.toString();
    }

    @Override // java.util.ArrayList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean isEmpty() {
        switch (size()) {
            case 0:
                return true;
            case 1:
                Element element = (Element) get(0);
                if (element.type() == 10 && ((Chunk) element).isEmpty()) {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    public HyphenationEvent getHyphenation() {
        return this.hyphenation;
    }

    public void setHyphenation(HyphenationEvent hyphenation) {
        this.hyphenation = hyphenation;
    }

    private Phrase(boolean dummy) {
        this.leading = Float.NaN;
        this.hyphenation = null;
    }

    private static final Phrase getInstance(int leading, String string, Font font) {
        Phrase p = new Phrase(true);
        p.setLeading(leading);
        p.font = font;
        if (font.getFamily() != 3 && font.getFamily() != 4 && font.getBaseFont() == null) {
            while (true) {
                int index = SpecialSymbol.index(string);
                if (index <= -1) {
                    break;
                }
                if (index > 0) {
                    String firstPart = string.substring(0, index);
                    p.add(new Chunk(firstPart, font));
                    string = string.substring(index);
                }
                Font symbol = new Font(3, font.getSize(), font.getStyle(), font.getColor());
                StringBuffer buf = new StringBuffer();
                buf.append(SpecialSymbol.getCorrespondingSymbol(string.charAt(0)));
                String strSubstring = string.substring(1);
                while (true) {
                    string = strSubstring;
                    if (SpecialSymbol.index(string) == 0) {
                        buf.append(SpecialSymbol.getCorrespondingSymbol(string.charAt(0)));
                        strSubstring = string.substring(1);
                    }
                }
                p.add(new Chunk(buf.toString(), symbol));
            }
        }
        if (string != null && string.length() != 0) {
            p.add(new Chunk(string, font));
        }
        return p;
    }
}
