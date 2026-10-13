package es.uji.crypto.xades.jxades.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/util/DefaultFileExtension.class */
public enum DefaultFileExtension implements FileExtension {
    CERTIFICATE_KEY_STORE_P12(".p12", "P12 Certificate Files"),
    CERTIFICATE_KEY_STORE_PFX(".pfx", "PFX Certificate Files"),
    CERTIFICATE_KEY_STORES("PKCS #12 Certificate Files", CERTIFICATE_KEY_STORE_P12, CERTIFICATE_KEY_STORE_PFX),
    CERTIFICATE_CER(".cer", "CER Certificate Files"),
    CERTIFICATE_CRT(".crt", "CRT Certificate Files"),
    CERTIFICATE_DER(".der", "DER Certificate Files"),
    ALL_CERTIFICATES("All Certificate Files", CERTIFICATE_CER, CERTIFICATE_CRT, CERTIFICATE_DER),
    CERTIFICATE_REVOCATION_LIST(".crl", "Certificate Revocation Lists", "CRL Files"),
    WINDOWS_SYSTEM_LIBRARY(".dll", "Windows System Library", "DLL Files"),
    LINUX_SYSTEM_LIBRARY(".so", "Linux System Library", "SO Files"),
    TXT_FILES(".txt", "Text Documents (*.txt) with UTF-8 encoding"),
    ALL_FILES(".*", "All files", "All Files");

    private String extension;
    private String description;
    private String fileFilterName;
    private String fileFilterPattern;
    private List<FileExtension> extensions;
    private static final TreeMap<String, TreeMap<String, FileExtension>> fileExtensionsMap = new TreeMap<>();

    DefaultFileExtension(String extension, String description) {
        this(extension, description, description, "*");
    }

    DefaultFileExtension(String extension, String description, String fileFilterName) {
        this(extension, description, fileFilterName, "*");
    }

    DefaultFileExtension(String extension, String description, String fileFilterName, String prefixFileFilterPattern) {
        this.extension = extension;
        this.description = description;
        this.fileFilterName = fileFilterName;
        this.fileFilterPattern = prefixFileFilterPattern + extension;
    }

    DefaultFileExtension(String description, FileExtension... exts) {
        this(description, description, Arrays.asList(exts));
    }

    DefaultFileExtension(String description, String fileFilterName, FileExtension... exts) {
        this(description, fileFilterName, Arrays.asList(exts));
    }

    DefaultFileExtension(String description, String fileFilterName, List list) {
        this.description = description;
        this.fileFilterName = fileFilterName;
        this.extensions = list;
    }

    @Override // es.uji.crypto.xades.jxades.util.FileExtension
    public String getDescription() {
        return this.description;
    }

    @Override // es.uji.crypto.xades.jxades.util.FileExtension
    public String getExtension() {
        return this.extension;
    }

    @Override // es.uji.crypto.xades.jxades.util.FileExtension
    public String getFileFilterName() {
        return this.fileFilterName;
    }

    @Override // es.uji.crypto.xades.jxades.util.FileExtension
    public String getFileFilterPattern() {
        return this.fileFilterPattern;
    }

    @Override // es.uji.crypto.xades.jxades.util.FileExtension
    public List<FileExtension> getExtensions() {
        return this.extensions;
    }

    @Override // es.uji.crypto.xades.jxades.util.FileExtension
    public boolean contains(FileExtension object) {
        if (this.extension != null) {
            return equals(object);
        }
        if (this.extensions != null) {
            Iterator<FileExtension> iter = this.extensions.iterator();
            while (iter.hasNext()) {
                if (iter.next().equals(object)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static DefaultFileExtension getEnumById(String extension) {
        return (DefaultFileExtension) getFileExtensionById(DefaultFileExtension.class, extension);
    }

    public static FileExtension getFileExtensionById(Class fileExtnsionEnumClass, String extension) {
        if (fileExtnsionEnumClass == null || extension == null) {
            throw new NullPointerException("Invalid parameter(s): 'fileExtnsionEnumClass' and 'extension' can not be NULL.");
        }
        if (!fileExtnsionEnumClass.isEnum()) {
            throw new IllegalArgumentException("Invalid parameter 'fileExtnsionEnumClass'. The parameter must exends Enum class.");
        }
        if (!FileExtension.class.isAssignableFrom(fileExtnsionEnumClass)) {
            throw new IllegalArgumentException("Invalid parameter 'fileExtnsionEnumClass'. The class must implements 'FileExtension' interface.");
        }
        String className = fileExtnsionEnumClass.getName();
        TreeMap<String, FileExtension> enumMap = fileExtensionsMap.get(className);
        if (enumMap == null) {
            enumMap = new TreeMap<>();
            for (Object enumObject : fileExtnsionEnumClass.getEnumConstants()) {
                FileExtension fileExt = (FileExtension) enumObject;
                String ext = fileExt.getExtension();
                if (ext != null) {
                    enumMap.put(ext, fileExt);
                }
            }
            fileExtensionsMap.put(className, enumMap);
        }
        if (extension.charAt(0) != '.') {
            return enumMap.get("." + extension.toLowerCase());
        }
        return enumMap.get(extension.toLowerCase());
    }

    public static void main(String[] args) {
        DefaultFileExtension fileExt = getEnumById("CRL");
        System.out.println("fileExt: " + fileExt);
    }
}
