package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.misc.LoggerUtil;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/DropDataFileListener.class */
public class DropDataFileListener implements DropTargetListener {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private final LoadDataFileListener listener;

    public DropDataFileListener(LoadDataFileListener loadDataFileListener) {
        this.listener = loadDataFileListener;
    }

    public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {
        if (!dropTargetDragEvent.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dropTargetDragEvent.rejectDrag();
        }
    }

    public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent) {
    }

    public void dragOver(DropTargetDragEvent dropTargetDragEvent) {
    }

    public void dragExit(DropTargetEvent dropTargetEvent) {
    }

    public void drop(DropTargetDropEvent dropTargetDropEvent) {
        File file;
        Transferable transferable = dropTargetDropEvent.getTransferable();
        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dropTargetDropEvent.acceptDrop(1);
            try {
                Object transferData = transferable.getTransferData(DataFlavor.javaFileListFlavor);
                if (transferData instanceof List) {
                    dropTargetDropEvent.getDropTargetContext().dropComplete(true);
                    List list = (List) transferData;
                    if (list.isEmpty()) {
                        dropTargetDropEvent.dropComplete(false);
                        return;
                    }
                    ArrayList arrayList = new ArrayList();
                    Iterator it = list.iterator();
                    while (it.hasNext()) {
                        String string = it.next().toString();
                        if (string.startsWith("http://") || string.startsWith("https://") || string.startsWith("ftp://")) {
                            LOGGER.warning("Solo se soportan ficheros locales. Se ignorara: " + string);
                        } else {
                            if (string.startsWith("file://")) {
                                try {
                                    file = new File(new URI(string));
                                } catch (Exception e) {
                                    LOGGER.warning(String.format("Error al cargar el fichero '%s', se ignorara: ", LoggerUtil.getCleanUserHomePath(string)) + e);
                                }
                            } else {
                                file = new File(string);
                            }
                            arrayList.add(file);
                        }
                    }
                    this.listener.loadFiles((File[]) arrayList.toArray(new File[arrayList.size()]), null);
                    return;
                }
                return;
            } catch (Exception e2) {
                LOGGER.warning("Ha fallado la operacion de arrastrar y soltar: " + e2);
                dropTargetDropEvent.dropComplete(false);
                return;
            }
        }
        dropTargetDropEvent.rejectDrop();
        dropTargetDropEvent.dropComplete(false);
    }
}
