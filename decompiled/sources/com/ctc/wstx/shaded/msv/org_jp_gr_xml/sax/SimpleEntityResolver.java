package com.ctc.wstx.shaded.msv.org_jp_gr_xml.sax;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_jp_gr_xml/sax/SimpleEntityResolver.class */
public class SimpleEntityResolver implements EntityResolver {
    private Map publicIds_ = new HashMap();
    private Map systemIds_ = new HashMap();
    private List relativeSystemIds_ = new ArrayList();

    public SimpleEntityResolver() {
    }

    public SimpleEntityResolver(String s, String s1) {
        _init(new String[][]{new String[]{s, s1}}, (String[][]) null);
    }

    public SimpleEntityResolver(String[][] as) {
        _init(as, (String[][]) null);
    }

    public SimpleEntityResolver(String[][] as, String[][] as1) {
        _init(as, as1);
    }

    private void _init(String[][] as, String[][] as1) {
        if (as != null) {
            new ArrayList();
            for (int j = 0; j < as.length; j++) {
                String s = as[j][0];
                addSystemId(s, as[j][1]);
            }
        }
        if (as1 != null) {
            for (int i = 0; i < as1.length; i++) {
                addPublicId(as1[i][0], as1[i][1]);
            }
        }
    }

    public void addSystemId(String s, String s1) {
        this.systemIds_.put(s, s1);
        this.relativeSystemIds_.add(s);
    }

    public void addPublicId(String s, String s1) {
        this.publicIds_.put(s, s1);
    }

    @Override // org.xml.sax.EntityResolver
    public InputSource resolveEntity(String s, String s1) {
        if (s1 != null && _isExist(s1)) {
            return new InputSource(s1);
        }
        if (s != null) {
            String s2 = (String) this.publicIds_.get(s);
            if (s2 != null) {
                return new InputSource(s2);
            }
            return null;
        }
        if (s1 != null) {
            String s3 = _getURIBySystemId(s1);
            if (s3 != null) {
                return new InputSource(s3);
            }
            return new InputSource(s1);
        }
        return null;
    }

    private boolean _isExist(String s) {
        try {
            URL url = new URL(s);
            if ("file".equals(url.getProtocol())) {
                InputStream inputstream = url.openStream();
                inputstream.close();
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    private String _getURIBySystemId(String s) {
        String s1 = (String) this.systemIds_.get(s);
        if (s1 != null) {
            return s1;
        }
        int i = this.relativeSystemIds_.size();
        for (int j = 0; j < i; j++) {
            String s2 = (String) this.relativeSystemIds_.get(j);
            if (s.endsWith(s2)) {
                return (String) this.systemIds_.get(s2);
            }
        }
        return null;
    }
}
