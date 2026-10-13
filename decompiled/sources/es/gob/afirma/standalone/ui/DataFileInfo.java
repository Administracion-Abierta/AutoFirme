package es.gob.afirma.standalone.ui;

import java.awt.image.BufferedImage;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/DataFileInfo.class */
public class DataFileInfo {
    private String extension = null;
    private String description = null;
    private int size = 0;
    private byte[] data = null;
    private BufferedImage icon = null;
    private boolean executable = false;

    public String getExtension() {
        return this.extension;
    }

    public void setExtension(String str) {
        this.extension = str;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int i) {
        this.size = i;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] bArr) {
        this.data = bArr;
    }

    public BufferedImage getIcon() {
        return this.icon;
    }

    public void setIcon(BufferedImage bufferedImage) {
        this.icon = bufferedImage;
    }

    public boolean isExecutable() {
        return this.executable;
    }

    public void setExecutable(boolean z) {
        this.executable = z;
    }
}
