package javax.help;

import java.io.Serializable;
import java.util.Vector;
import javax.help.event.HelpHistoryModelListener;
import javax.help.event.HelpModelListener;

/* JADX INFO: loaded from: autofirma.jar:javax/help/HelpHistoryModel.class */
public interface HelpHistoryModel extends HelpModelListener, Serializable {
    void addHelpHistoryModelListener(HelpHistoryModelListener helpHistoryModelListener);

    void removeHelpHistoryModelListener(HelpHistoryModelListener helpHistoryModelListener);

    void discard();

    void goForward();

    void goBack();

    Vector getBackwardHistory();

    Vector getForwardHistory();

    void setHistoryEntry(int i);

    void removeHelpSet(HelpSet helpSet);

    Vector getHistory();

    int getIndex();

    void setHelpModel(HelpModel helpModel);
}
