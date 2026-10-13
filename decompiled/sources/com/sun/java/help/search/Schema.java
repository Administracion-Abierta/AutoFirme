package com.sun.java.help.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/Schema.class */
public class Schema {
    private String _dirName;
    private URL _hsBase;
    private boolean _update;
    private Vector _lines = new Vector();
    private static boolean debugFlag = false;

    public static void main(String[] strArr) {
        try {
            Schema schema = new Schema(null, "/files/resources/lexicon/LIF_ONT", false);
            debug(schema.parametersAsString("EDGE"));
            debug(schema.parametersAsString("TMAP"));
            debug(schema.parameters("EDGE").get("rt").toString());
            debug(schema.parameters("EDGE").get("vl").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Schema(URL url, String str, boolean z) throws Exception {
        this._hsBase = null;
        this._hsBase = url;
        this._dirName = str;
        this._update = z;
        try {
            read();
        } catch (FileNotFoundException e) {
            debug("error creating SCHEMA");
        }
        for (int i = 0; i < this._lines.size(); i++) {
            debug(this._lines.elementAt(i).toString());
        }
    }

    public void update(String str, String str2) {
        for (int i = 0; i < this._lines.size(); i++) {
            if (((String) this._lines.elementAt(i)).startsWith(str)) {
                this._lines.removeElementAt(i);
            }
        }
        this._lines.addElement(new StringBuffer().append(str).append(" ").append(str2).toString());
    }

    public String parametersAsString(String str) {
        for (int i = 0; i < this._lines.size(); i++) {
            if (((String) this._lines.elementAt(i)).startsWith(str)) {
                return ((String) this._lines.elementAt(i)).substring(str.length() + 1);
            }
        }
        return null;
    }

    public Hashtable parameters(String str) {
        for (int i = 0; i < this._lines.size(); i++) {
            if (((String) this._lines.elementAt(i)).startsWith(str)) {
                Hashtable hashtable = new Hashtable();
                StringTokenizer stringTokenizer = new StringTokenizer((String) this._lines.elementAt(i), " =");
                stringTokenizer.nextToken();
                while (stringTokenizer.hasMoreTokens()) {
                    hashtable.put(stringTokenizer.nextToken(), stringTokenizer.nextToken());
                }
                return hashtable;
            }
        }
        return null;
    }

    public URL getURL(String str) throws Exception {
        URL url = null;
        debug(new StringBuffer().append("getURL ").append(str).toString());
        debug(new StringBuffer().append("dirName=").append(this._dirName).append(" hsBase= ").append(this._hsBase).toString());
        if (this._hsBase == null) {
            if (new File(this._dirName).exists()) {
                if (File.separatorChar != '/') {
                    this._dirName = this._dirName.replace(File.separatorChar, '/');
                }
                if (this._dirName.lastIndexOf(47) != this._dirName.length() - 1) {
                    this._dirName = this._dirName.concat("/");
                }
                debug(new StringBuffer().append("file:").append(this._dirName).toString());
                url = new URL("file", "", this._dirName);
            } else {
                url = new URL(this._dirName);
            }
        }
        if (this._hsBase != null) {
            return new URL(this._hsBase, new StringBuffer().append(this._dirName).append("/").append(str).toString());
        }
        return new URL(url, str);
    }

    public void save() {
        if (this._update) {
            try {
                FileWriter fileWriter = new FileWriter(new StringBuffer().append(this._dirName).append("/SCHEMA").toString());
                fileWriter.write("JavaSearch 1.0\n");
                for (int i = 0; i < this._lines.size(); i++) {
                    fileWriter.write((String) this._lines.elementAt(i));
                    fileWriter.write(10);
                }
                fileWriter.close();
            } catch (IOException e) {
                System.err.println(new StringBuffer().append("SCHEMA save failed ").append(e).toString());
            }
        }
    }

    private void read() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getURL("SCHEMA").openConnection().getInputStream()));
        while (true) {
            String line = bufferedReader.readLine();
            if (line != null) {
                this._lines.addElement(line);
            } else {
                bufferedReader.close();
                return;
            }
        }
    }

    private static void debug(String str) {
        if (debugFlag) {
            System.out.println(new StringBuffer().append("Schema: ").append(str).toString());
        }
    }
}
