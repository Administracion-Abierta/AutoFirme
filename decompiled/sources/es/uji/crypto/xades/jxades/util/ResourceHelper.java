package es.uji.crypto.xades.jxades.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/util/ResourceHelper.class */
public class ResourceHelper {
    private String classFileName;
    private String resourceFolderName;
    private URL resourceURL;
    private Enumeration<ResourceEntry> resourceEntries;

    /* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/util/ResourceHelper$ResourceEntry.class */
    public interface ResourceEntry {
        String getPathName();

        boolean isDirectory();

        InputStream getInputStream() throws IOException;
    }

    public ResourceHelper(Class clazz) throws IOException {
        this(clazz, null);
    }

    public ResourceHelper(Class clazz, String resourceFolderName) throws IOException {
        this.resourceFolderName = resourceFolderName;
        this.classFileName = clazz.getName().replace('.', '/') + ".class";
        ClassLoader classLoader = clazz.getClassLoader();
        this.resourceURL = ClassLoader.getSystemResource(this.classFileName);
        if (this.resourceURL == null) {
            this.resourceURL = classLoader.getResource(this.classFileName);
        }
        if (this.resourceURL == null) {
            throw new IOException("Impossible to find Resource URL.");
        }
    }

    public URL getResourceURL() {
        return this.resourceURL;
    }

    public Enumeration<ResourceEntry> resourceEntries() throws IOException {
        if (this.resourceEntries == null) {
            String protocol = this.resourceURL.getProtocol().toLowerCase();
            if ("file".equals(protocol)) {
                String path = this.resourceURL.getPath();
                int pos = path.length() - this.classFileName.length();
                String basePath = path.substring(0, pos);
                File baseFolder = new File(basePath);
                if (this.resourceFolderName != null) {
                    baseFolder = new File(baseFolder, this.resourceFolderName);
                }
                List<File> fileStore = new ArrayList<>();
                loadFiles(baseFolder, fileStore);
                return new FileResourceEntries(fileStore);
            }
            if ("jar".equals(protocol)) {
                JarURLConnection jarConn = (JarURLConnection) this.resourceURL.openConnection();
                JarFile jarFile = jarConn.getJarFile();
                this.resourceEntries = new JarResourceEntries(jarFile, this.resourceFolderName);
            } else {
                this.resourceEntries = new EmptyResourceEntries();
            }
        }
        return this.resourceEntries;
    }

    private void loadFiles(File folder, List<File> fileStore) {
        for (File file : folder.listFiles()) {
            fileStore.add(file);
            if (file.isDirectory()) {
                loadFiles(file, fileStore);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/util/ResourceHelper$EmptyResourceEntries.class */
    private static class EmptyResourceEntries implements Enumeration<ResourceEntry> {
        private EmptyResourceEntries() {
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return false;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Enumeration
        public ResourceEntry nextElement() {
            throw new NoSuchElementException();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/util/ResourceHelper$JarResourceEntries.class */
    private static class JarResourceEntries implements Enumeration<ResourceEntry> {
        private JarFile jarFile;
        private Iterator<JarEntry> entries;

        public JarResourceEntries(JarFile jarFile, String resourceFolderName) {
            this.jarFile = jarFile;
            List<JarEntry> jarEntryList = new ArrayList<>();
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry entry = jarEntries.nextElement();
                if (resourceFolderName == null || entry.getName().startsWith(resourceFolderName)) {
                    jarEntryList.add(entry);
                }
            }
            this.entries = jarEntryList.iterator();
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.entries.hasNext();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Enumeration
        public ResourceEntry nextElement() {
            return new JarResourceEntry(this.jarFile, this.entries.next());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/util/ResourceHelper$FileResourceEntries.class */
    private static class FileResourceEntries implements Enumeration<ResourceEntry> {
        private Iterator<File> entries;

        public FileResourceEntries(List<File> fileStore) {
            this.entries = fileStore.iterator();
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.entries.hasNext();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Enumeration
        public ResourceEntry nextElement() {
            return new FileResourceEntry(this.entries.next());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/util/ResourceHelper$FileResourceEntry.class */
    private static class FileResourceEntry implements ResourceEntry {
        private File file;

        public FileResourceEntry(File file) {
            this.file = file;
        }

        @Override // es.uji.crypto.xades.jxades.util.ResourceHelper.ResourceEntry
        public String getPathName() {
            return this.file.getAbsolutePath();
        }

        @Override // es.uji.crypto.xades.jxades.util.ResourceHelper.ResourceEntry
        public boolean isDirectory() {
            return this.file.isDirectory();
        }

        @Override // es.uji.crypto.xades.jxades.util.ResourceHelper.ResourceEntry
        public InputStream getInputStream() throws IOException {
            return new FileInputStream(this.file);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/util/ResourceHelper$JarResourceEntry.class */
    private static class JarResourceEntry implements ResourceEntry {
        private JarFile jarFile;
        private JarEntry entry;

        public JarResourceEntry(JarFile jarFile, JarEntry entry) {
            this.entry = entry;
            this.jarFile = jarFile;
        }

        @Override // es.uji.crypto.xades.jxades.util.ResourceHelper.ResourceEntry
        public String getPathName() {
            return this.entry.getName();
        }

        @Override // es.uji.crypto.xades.jxades.util.ResourceHelper.ResourceEntry
        public boolean isDirectory() {
            return this.entry.isDirectory();
        }

        @Override // es.uji.crypto.xades.jxades.util.ResourceHelper.ResourceEntry
        public InputStream getInputStream() throws IOException {
            return this.jarFile.getInputStream(this.entry);
        }
    }
}
