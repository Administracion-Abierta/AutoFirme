package com.sun.java.help.search;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Vector;
import javax.help.search.SearchItem;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/SearchEnvironment.class */
class SearchEnvironment {
    private IntegerArray concepts;
    private IntegerArray offsets;
    private byte[] allLists;
    private IntegerArray concepts3;
    private IntegerArray offsets3;
    private byte[] allChildren;
    private IntegerArray documents;
    private IntegerArray offsets2;
    private IntegerArray titles;
    private RAFFile positionsFile = null;
    private byte[] positions;
    private ByteArrayDecompressor compr;
    private BtreeDict tmap;
    private URL base;
    private String indexDir;
    private static boolean debugFlag = false;

    public SearchEnvironment(String str, URL url) throws Exception {
        this.base = url;
        this.indexDir = str;
        readFromDB();
        this.compr = new ByteArrayDecompressor(null, 0);
    }

    public int fetch(String str) throws Exception {
        return this.tmap.fetch(str);
    }

    public String fetch(int i) throws Exception {
        return this.tmap.fetch(i);
    }

    public SearchItem makeItem(QueryHit queryHit) throws Exception {
        Vector vector = new Vector();
        int[] array = queryHit.getArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] > 0) {
                vector.addElement(fetch(array[i]));
            }
        }
        return new SearchItem(this.base, fetch(this.titles.at(queryHit.getDocument())), Locale.getDefault().toString(), fetch(this.documents.at(queryHit.getDocument())), queryHit.getScore(), queryHit.getBegin(), queryHit.getEnd(), vector);
    }

    public String hitToString(QueryHit queryHit) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(queryHit.getScore());
        stringBuffer.append(" ");
        stringBuffer.append(fetch(this.documents.at(queryHit.getDocument())));
        stringBuffer.append(" [");
        int begin = queryHit.getBegin();
        int end = queryHit.getEnd();
        stringBuffer.append(begin);
        stringBuffer.append(", ");
        stringBuffer.append(end);
        stringBuffer.append("], {");
        int[] array = queryHit.getArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] > 0) {
                stringBuffer.append(fetch(array[i]));
            } else {
                stringBuffer.append("--");
            }
            if (i < array.length - 1) {
                stringBuffer.append(", ");
            }
        }
        stringBuffer.append("}");
        return stringBuffer.toString();
    }

    public byte[] getPositions(int i) throws IOException {
        int iAt;
        int iAt2 = this.offsets2.at(i);
        if (i + 1 == this.offsets2.cardinality()) {
            iAt = (int) this.positionsFile.length();
        } else {
            iAt = this.offsets2.at(i + 1);
        }
        this.positions = new byte[iAt - iAt2];
        this.positionsFile.seek(iAt2);
        this.positionsFile.read(this.positions, 0, iAt - iAt2);
        return this.positions;
    }

    public int getDocumentIndex(int i) {
        return this.offsets2.at(i);
    }

    public void close() throws IOException {
        this.positionsFile.close();
    }

    public boolean occursInText(int i) {
        return this.concepts.indexOf(i) >= 0;
    }

    public NonnegativeIntegerGenerator getDocumentIterator(int i) {
        int iIndexOf = this.concepts.indexOf(i);
        if (iIndexOf >= 0) {
            return new ConceptList(this.allLists, this.offsets.at(iIndexOf));
        }
        return null;
    }

    public NonnegativeIntegerGenerator getChildIterator(int i) {
        int iIndexOf = this.concepts3.indexOf(i);
        if (iIndexOf >= 0) {
            return new ConceptList(this.allChildren, this.offsets3.at(iIndexOf));
        }
        return null;
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public void getChildren(int i, IntegerArray integerArray) throws Exception {
        int iIndexOf = this.concepts3.indexOf(i);
        if (iIndexOf >= 0) {
            int iAt = this.offsets3.at(iIndexOf);
            this.compr.initReading(this.allChildren, iAt + 1);
            this.compr.ascDecode(this.allChildren[iAt], integerArray);
        }
    }

    public int getConceptLength(int i) throws Exception {
        return this.tmap.fetch(i).length();
    }

    private URL getURL(String str) throws MalformedURLException {
        URL url;
        URL url2 = this.base;
        String strConcat = this.indexDir;
        URL url3 = null;
        if (url2 == null) {
            if (new File(strConcat).exists()) {
                if (File.separatorChar != '/') {
                    strConcat = strConcat.replace(File.separatorChar, '/');
                }
                if (strConcat.lastIndexOf(47) != strConcat.length() - 1) {
                    strConcat = strConcat.concat("/");
                }
                debug(new StringBuffer().append("file:").append(strConcat).toString());
                url3 = new URL("file", "", strConcat);
            } else {
                url3 = new URL(strConcat);
            }
        }
        if (url2 != null) {
            url = new URL(url2, new StringBuffer().append(strConcat).append("/").append(str).toString());
        } else {
            url = new URL(url3, str);
        }
        return url;
    }

    private void readChildrenData() throws Exception {
        try {
            InputStream inputStream = getURL("CHILDREN.TAB").openConnection().getInputStream();
            if (inputStream == null) {
                this.concepts3 = new IntegerArray(1);
                return;
            }
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            int i = bufferedInputStream.read();
            this.concepts3 = new IntegerArray(4096);
            new StreamDecompressor(bufferedInputStream).ascDecode(i, this.concepts3);
            int i2 = bufferedInputStream.read();
            this.offsets3 = new IntegerArray(this.concepts3.cardinality() + 1);
            this.offsets3.add(0);
            new StreamDecompressor(bufferedInputStream).ascDecode(i2, this.offsets3);
            bufferedInputStream.close();
            URLConnection uRLConnectionOpenConnection = getURL("CHILDREN").openConnection();
            InputStream inputStream2 = uRLConnectionOpenConnection.getInputStream();
            if (inputStream2 == null) {
                this.concepts3 = new IntegerArray(1);
                return;
            }
            BufferedInputStream bufferedInputStream2 = new BufferedInputStream(inputStream2);
            this.allChildren = new byte[uRLConnectionOpenConnection.getContentLength()];
            bufferedInputStream2.read(this.allChildren);
            bufferedInputStream2.close();
        } catch (FileNotFoundException e) {
            this.concepts3 = new IntegerArray(1);
        } catch (MalformedURLException e2) {
            this.concepts3 = new IntegerArray(1);
        } catch (IOException e3) {
            this.concepts3 = new IntegerArray(1);
        }
    }

    private void readFromDB() throws Exception {
        BtreeDictParameters btreeDictParameters = new BtreeDictParameters(new Schema(this.base, this.indexDir, false), "TMAP");
        btreeDictParameters.readState();
        this.tmap = new BtreeDict(btreeDictParameters);
        readChildrenData();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(getURL("DOCS.TAB").openConnection().getInputStream());
        int i = bufferedInputStream.read();
        this.concepts = new IntegerArray(4096);
        new StreamDecompressor(bufferedInputStream).ascDecode(i, this.concepts);
        int i2 = bufferedInputStream.read();
        this.offsets = new IntegerArray(this.concepts.cardinality() + 1);
        this.offsets.add(0);
        new StreamDecompressor(bufferedInputStream).ascDecode(i2, this.offsets);
        bufferedInputStream.close();
        BufferedInputStream bufferedInputStream2 = new BufferedInputStream(getURL("DOCS").openConnection().getInputStream());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[512];
        while (true) {
            int i3 = bufferedInputStream2.read(bArr);
            if (i3 != -1) {
                byteArrayOutputStream.write(bArr, 0, i3);
            } else {
                this.allLists = byteArrayOutputStream.toByteArray();
                bufferedInputStream2.close();
                BufferedInputStream bufferedInputStream3 = new BufferedInputStream(getURL("OFFSETS").openConnection().getInputStream());
                int i4 = bufferedInputStream3.read();
                this.documents = new IntegerArray(4096);
                new StreamDecompressor(bufferedInputStream3).ascDecode(i4, this.documents);
                int i5 = bufferedInputStream3.read();
                this.offsets2 = new IntegerArray(this.documents.cardinality() + 1);
                new StreamDecompressor(bufferedInputStream3).ascDecode(i5, this.offsets2);
                int i6 = bufferedInputStream3.read();
                this.titles = new IntegerArray(this.documents.cardinality());
                new StreamDecompressor(bufferedInputStream3).decode(i6, this.titles);
                bufferedInputStream3.close();
                this.positionsFile = RAFFileFactory.create().get(getURL("POSITIONS"), false);
                return;
            }
        }
    }

    private static void debug(String str) {
        if (debugFlag) {
            System.out.println(new StringBuffer().append("SearchEnvironment: ").append(str).toString());
        }
    }
}
