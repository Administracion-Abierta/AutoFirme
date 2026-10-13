package es.uji.crypto.xades.jxades.util;

import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/util/FileExtension.class */
public interface FileExtension {
    String getDescription();

    String getExtension();

    String getFileFilterName();

    String getFileFilterPattern();

    List<FileExtension> getExtensions();

    boolean contains(FileExtension fileExtension);
}
