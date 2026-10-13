package javax.help.search;

import com.ctc.wstx.cfg.XmlConsts;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.BreakIterator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.commons.codec.language.bm.Languages;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDPrintFieldAttributeObject;

/* JADX INFO: loaded from: autofirma.jar:javax/help/search/ConfigFile.class */
public class ConfigFile {
    private String remove;
    private String prepend;
    private Hashtable stopWords;
    private String[] defStopWords = {PDPageLabelRange.STYLE_LETTERS_LOWER, PdfExtraParams.PAGES_TO_CHECK_PSA_VALUE_ALL, "am", "an", "and", Languages.ANY, "are", "as", "at", "be", "but", "by", "can", "could", "did", "do", "does", "etc", "for", "from", "goes", "got", "had", "has", "have", "he", "her", "him", "his", "how", "if", "in", "is", "it", "let", "me", "more", "much", "must", "my", "nor", "not", "now", "of", PDPrintFieldAttributeObject.CHECKED_STATE_OFF, PDPrintFieldAttributeObject.CHECKED_STATE_ON, "or", "our", "own", "see", "set", "shall", "she", "should", "so", "some", "than", "that", "the", "them", "then", "there", "these", "this", "those", "though", "to", "too", "us", "was", "way", "we", "what", "when", "where", "which", "who", "why", "will", "would", XmlConsts.XML_SA_YES, "yet", "you"};
    private Vector files;

    public ConfigFile(String str, Vector vector, boolean z) {
        this.files = vector;
        String str2 = new String("IndexRemove ");
        String str3 = new String("IndexPrepend ");
        String str4 = new String("File ");
        String str5 = new String("StopWords ");
        String str6 = new String("StopWordsFile ");
        this.stopWords = new Hashtable();
        if (str == null) {
            if (!z) {
                useDefaultStopWords();
                return;
            }
            return;
        }
        try {
            LineNumberReader lineNumberReader = new LineNumberReader(new BufferedReader(new FileReader(str)));
            while (true) {
                String line = lineNumberReader.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith(str2)) {
                    this.remove = line.substring(str2.length(), line.length());
                } else if (line.startsWith(str3)) {
                    this.prepend = line.substring(str3.length(), line.length());
                } else if (line.startsWith(str4)) {
                    vector.addElement(line.substring(str4.length(), line.length()));
                } else if (line.startsWith(str6)) {
                    addStopWordsFile(line.substring(str6.length(), line.length()));
                } else if (line.startsWith(str5)) {
                    if (!z) {
                        String strSubstring = line.substring(str5.length(), line.length());
                        BreakIterator wordInstance = BreakIterator.getWordInstance();
                        wordInstance.setText(strSubstring);
                        int iFirst = wordInstance.first();
                        for (int next = wordInstance.next(); next != -1; next = wordInstance.next()) {
                            String lowerCase = strSubstring.substring(iFirst, next).trim().toLowerCase();
                            if (!lowerCase.equals(",") && !lowerCase.equals("")) {
                                this.stopWords.put(lowerCase, lowerCase);
                            }
                            iFirst = next;
                        }
                    }
                } else {
                    System.out.println(new StringBuffer().append("Unknown Config Keyword at line ").append(lineNumberReader.getLineNumber()).toString());
                }
            }
            if (this.stopWords.isEmpty() && !z) {
                useDefaultStopWords();
            }
        } catch (IOException e) {
        }
    }

    public String getURLString(String str) {
        String strReplace;
        if (this.remove != null && str.startsWith(this.remove)) {
            strReplace = str.substring(this.remove.length(), str.length());
        } else {
            strReplace = str;
        }
        if (this.prepend != null) {
            strReplace = new StringBuffer().append(this.prepend).append(strReplace).toString();
        }
        if (File.separatorChar != '/') {
            strReplace = strReplace.replace(File.separatorChar, '/');
        }
        return strReplace;
    }

    public Enumeration getStopWords() {
        return this.stopWords.elements();
    }

    public Vector getFiles() {
        return this.files;
    }

    private void useDefaultStopWords() {
        for (int i = 0; i < this.defStopWords.length; i++) {
            this.stopWords.put(this.defStopWords[i], this.defStopWords[i]);
        }
    }

    private void addStopWordsFile(String str) {
        if (str == null) {
            return;
        }
        try {
            LineNumberReader lineNumberReader = new LineNumberReader(new BufferedReader(new FileReader(str)));
            while (true) {
                String line = lineNumberReader.readLine();
                if (line != null) {
                    String strTrim = line.trim();
                    this.stopWords.put(strTrim, strTrim);
                } else {
                    return;
                }
            }
        } catch (IOException e) {
        }
    }
}
