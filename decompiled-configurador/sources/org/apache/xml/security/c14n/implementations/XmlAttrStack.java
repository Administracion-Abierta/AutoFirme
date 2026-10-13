package org.apache.xml.security.c14n.implementations;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/c14n/implementations/XmlAttrStack.class */
class XmlAttrStack {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) XmlAttrStack.class);
    private XmlsStackElement cur;
    private final boolean c14n11;
    private int currentLevel = 0;
    private int lastlevel = 0;
    private final List<XmlsStackElement> levels = new ArrayList();

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/c14n/implementations/XmlAttrStack$XmlsStackElement.class */
    private static class XmlsStackElement {
        int level;
        boolean rendered;
        final List<Attr> nodes;

        private XmlsStackElement() {
            this.rendered = false;
            this.nodes = new ArrayList();
        }
    }

    public XmlAttrStack(boolean c14n11) {
        this.c14n11 = c14n11;
    }

    void push(int level) {
        this.currentLevel = level;
        if (this.currentLevel == -1) {
            return;
        }
        this.cur = null;
        while (this.lastlevel >= this.currentLevel) {
            this.levels.remove(this.levels.size() - 1);
            int newSize = this.levels.size();
            if (newSize == 0) {
                this.lastlevel = 0;
                return;
            }
            this.lastlevel = this.levels.get(newSize - 1).level;
        }
    }

    void addXmlnsAttr(Attr n) {
        if (this.cur == null) {
            this.cur = new XmlsStackElement();
            this.cur.level = this.currentLevel;
            this.levels.add(this.cur);
            this.lastlevel = this.currentLevel;
        }
        this.cur.nodes.add(n);
    }

    void getXmlnsAttr(Collection<Attr> col) {
        int size = this.levels.size() - 1;
        if (this.cur == null) {
            this.cur = new XmlsStackElement();
            this.cur.level = this.currentLevel;
            this.lastlevel = this.currentLevel;
            this.levels.add(this.cur);
        }
        boolean parentRendered = false;
        if (size == -1) {
            parentRendered = true;
        } else {
            XmlsStackElement e = this.levels.get(size);
            if (e.rendered && e.level + 1 == this.currentLevel) {
                parentRendered = true;
            }
        }
        if (parentRendered) {
            col.addAll(this.cur.nodes);
            this.cur.rendered = true;
            return;
        }
        Map<String, Attr> loa = new HashMap<>();
        if (this.c14n11) {
            List<Attr> baseAttrs = new ArrayList<>();
            boolean successiveOmitted = true;
            while (size >= 0) {
                XmlsStackElement e2 = this.levels.get(size);
                if (e2.rendered) {
                    successiveOmitted = false;
                }
                Iterator<Attr> it = e2.nodes.iterator();
                while (it.hasNext() && successiveOmitted) {
                    Attr n = it.next();
                    if ("base".equals(n.getLocalName()) && !e2.rendered) {
                        baseAttrs.add(n);
                    } else if (!loa.containsKey(n.getName())) {
                        loa.put(n.getName(), n);
                    }
                }
                size--;
            }
            if (!baseAttrs.isEmpty()) {
                String base = null;
                Attr baseAttr = null;
                for (Attr n2 : col) {
                    if ("base".equals(n2.getLocalName())) {
                        base = n2.getValue();
                        baseAttr = n2;
                        break;
                    }
                }
                for (Attr n3 : baseAttrs) {
                    if (base == null) {
                        base = n3.getValue();
                        baseAttr = n3;
                    } else {
                        try {
                            base = joinURI(n3.getValue(), base);
                        } catch (URISyntaxException ue) {
                            LOG.debug(ue.getMessage(), (Throwable) ue);
                        }
                    }
                }
                if (base != null && base.length() != 0) {
                    baseAttr.setValue(base);
                    col.add(baseAttr);
                }
            }
        } else {
            while (size >= 0) {
                for (Attr n4 : this.levels.get(size).nodes) {
                    if (!loa.containsKey(n4.getName())) {
                        loa.put(n4.getName(), n4);
                    }
                }
                size--;
            }
        }
        this.cur.rendered = true;
        col.addAll(loa.values());
    }

    private static String joinURI(String baseURI, String relativeURI) throws URISyntaxException {
        String tpath;
        String tpath2;
        String tquery;
        String tauthority;
        String tscheme;
        String bscheme = null;
        String bauthority = null;
        String bpath = "";
        String bquery = null;
        if (baseURI != null) {
            if (baseURI.endsWith("..")) {
                baseURI = baseURI + "/";
            }
            URI base = new URI(baseURI);
            bscheme = base.getScheme();
            bauthority = base.getAuthority();
            bpath = base.getPath();
            bquery = base.getQuery();
        }
        URI r = new URI(relativeURI);
        String rscheme = r.getScheme();
        String rauthority = r.getAuthority();
        String rpath = r.getPath();
        String rquery = r.getQuery();
        if (rscheme != null && rscheme.equals(bscheme)) {
            rscheme = null;
        }
        if (rscheme != null) {
            tscheme = rscheme;
            tauthority = rauthority;
            tpath2 = removeDotSegments(rpath);
            tquery = rquery;
        } else {
            if (rauthority != null) {
                tauthority = rauthority;
                tpath2 = removeDotSegments(rpath);
                tquery = rquery;
            } else {
                if (rpath.length() == 0) {
                    tpath2 = bpath;
                    if (rquery != null) {
                        tquery = rquery;
                    } else {
                        tquery = bquery;
                    }
                } else {
                    if (rpath.charAt(0) == '/') {
                        tpath2 = removeDotSegments(rpath);
                    } else {
                        if (bauthority != null && bpath.length() == 0) {
                            tpath = "/" + rpath;
                        } else {
                            int last = bpath.lastIndexOf(47);
                            if (last == -1) {
                                tpath = rpath;
                            } else {
                                tpath = bpath.substring(0, last + 1) + rpath;
                            }
                        }
                        tpath2 = removeDotSegments(tpath);
                    }
                    tquery = rquery;
                }
                tauthority = bauthority;
            }
            tscheme = bscheme;
        }
        return new URI(tscheme, tauthority, tpath2, tquery, null).toString();
    }

    private static String removeDotSegments(String path) {
        String input;
        int end;
        String segment;
        String strSubstring;
        LOG.debug("STEP OUTPUT BUFFER\t\tINPUT BUFFER");
        String strReplaceAll = path;
        while (true) {
            input = strReplaceAll;
            if (input.indexOf("//") <= -1) {
                break;
            }
            strReplaceAll = input.replaceAll("//", "/");
        }
        StringBuilder output = new StringBuilder();
        if (input.charAt(0) == '/') {
            output.append('/');
            input = input.substring(1);
        }
        printStep("1 ", output.toString(), input);
        while (input.length() != 0) {
            if (input.startsWith("./")) {
                input = input.substring(2);
                printStep("2A", output.toString(), input);
            } else if (input.startsWith("../")) {
                input = input.substring(3);
                if (!"/".equals(output.toString())) {
                    output.append("../");
                }
                printStep("2A", output.toString(), input);
            } else if (input.startsWith("/./")) {
                input = input.substring(2);
                printStep("2B", output.toString(), input);
            } else if ("/.".equals(input)) {
                input = input.replaceFirst("/.", "/");
                printStep("2B", output.toString(), input);
            } else if (input.startsWith("/../")) {
                input = input.substring(3);
                if (output.length() == 0) {
                    output.append('/');
                } else if (output.toString().endsWith("../")) {
                    output.append("..");
                } else if (output.toString().endsWith("..")) {
                    output.append("/..");
                } else {
                    int index = output.lastIndexOf("/");
                    if (index == -1) {
                        output = new StringBuilder();
                        if (input.charAt(0) == '/') {
                            input = input.substring(1);
                        }
                    } else {
                        output = output.delete(index, output.length());
                    }
                }
                printStep("2C", output.toString(), input);
            } else if ("/..".equals(input)) {
                input = input.replaceFirst("/..", "/");
                if (output.length() == 0) {
                    output.append('/');
                } else if (output.toString().endsWith("../")) {
                    output.append("..");
                } else if (output.toString().endsWith("..")) {
                    output.append("/..");
                } else {
                    int index2 = output.lastIndexOf("/");
                    if (index2 == -1) {
                        output = new StringBuilder();
                        if (input.charAt(0) == '/') {
                            input = input.substring(1);
                        }
                    } else {
                        output = output.delete(index2, output.length());
                    }
                }
                printStep("2C", output.toString(), input);
            } else if (".".equals(input)) {
                input = "";
                printStep("2D", output.toString(), input);
            } else if ("..".equals(input)) {
                if (!"/".equals(output.toString())) {
                    output.append("..");
                }
                input = "";
                printStep("2D", output.toString(), input);
            } else {
                int begin = input.indexOf(47);
                if (begin == 0) {
                    end = input.indexOf(47, 1);
                } else {
                    end = begin;
                    begin = 0;
                }
                if (end == -1) {
                    segment = input.substring(begin);
                    strSubstring = "";
                } else {
                    segment = input.substring(begin, end);
                    strSubstring = input.substring(end);
                }
                input = strSubstring;
                output.append(segment);
                printStep("2E", output.toString(), input);
            }
        }
        if (output.toString().endsWith("..")) {
            output.append('/');
            printStep("3 ", output.toString(), input);
        }
        return output.toString();
    }

    private static void printStep(String step, String output, String input) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(" " + step + ":   " + output);
            if (output.length() == 0) {
                LOG.debug("\t\t\t\t" + input);
            } else {
                LOG.debug("\t\t\t" + input);
            }
        }
    }
}
