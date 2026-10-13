package com.sun.java.help.search;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/StreamDecompressor.class */
class StreamDecompressor extends Decompressor {
    private InputStream _input;

    public StreamDecompressor(InputStream inputStream) {
        initReading(inputStream);
    }

    public void initReading(InputStream inputStream) {
        this._input = inputStream;
        initReading();
    }

    @Override // com.sun.java.help.search.Decompressor
    public int getNextByte() throws IOException {
        return this._input.read();
    }

    public static void main(String[] strArr) {
        try {
            FileInputStream fileInputStream = new FileInputStream(strArr[0]);
            try {
                int i = fileInputStream.read();
                long jCurrentTimeMillis = System.currentTimeMillis();
                System.out.println(new StringBuffer().append("k1 = ").append(i).toString());
                IntegerArray integerArray = new IntegerArray();
                try {
                    new StreamDecompressor(fileInputStream).ascDecode(i, integerArray);
                } catch (Exception e) {
                    System.err.println(e);
                }
                System.out.println(new StringBuffer().append("index1 = ").append(integerArray.cardinality()).toString());
                int i2 = fileInputStream.read();
                System.out.println(new StringBuffer().append("k2 = ").append(i2).toString());
                IntegerArray integerArray2 = new IntegerArray(integerArray.cardinality());
                try {
                    new StreamDecompressor(fileInputStream).decode(i2, integerArray2);
                } catch (Exception e2) {
                    System.err.println(e2);
                }
                System.out.println(new StringBuffer().append("index2 = ").append(integerArray2.cardinality()).toString());
                System.out.println(new StringBuffer().append(System.currentTimeMillis() - jCurrentTimeMillis).append(" msec").toString());
                fileInputStream.close();
            } catch (IOException e3) {
                System.err.println(e3);
            }
        } catch (FileNotFoundException e4) {
            System.err.println(e4);
        }
    }
}
