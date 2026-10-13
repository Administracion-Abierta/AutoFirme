package org.bouncycastle.tsp.ers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/ers/ERSDirectoryDataGroup.class */
public class ERSDirectoryDataGroup extends ERSDataGroup {
    public ERSDirectoryDataGroup(File file) throws FileNotFoundException {
        super(buildGroup(file));
    }

    private static List<ERSData> buildGroup(File file) throws FileNotFoundException {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("file reference does not refer to directory");
        }
        File[] fileArrListFiles = file.listFiles();
        ArrayList arrayList = new ArrayList(fileArrListFiles.length);
        for (int i = 0; i != fileArrListFiles.length; i++) {
            if (!fileArrListFiles[i].isDirectory()) {
                arrayList.add(new ERSFileData(fileArrListFiles[i]));
            } else if (fileArrListFiles[i].listFiles().length != 0) {
                arrayList.add(new ERSDirectoryDataGroup(fileArrListFiles[i]));
            }
        }
        return arrayList;
    }

    public List<ERSFileData> getFiles() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i != this.dataObjects.size(); i++) {
            if (this.dataObjects.get(i) instanceof ERSFileData) {
                arrayList.add((ERSFileData) this.dataObjects.get(i));
            }
        }
        return arrayList;
    }

    public List<ERSDirectoryDataGroup> getSubdirectories() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i != this.dataObjects.size(); i++) {
            if (this.dataObjects.get(i) instanceof ERSDirectoryDataGroup) {
                arrayList.add((ERSDirectoryDataGroup) this.dataObjects.get(i));
            }
        }
        return arrayList;
    }
}
