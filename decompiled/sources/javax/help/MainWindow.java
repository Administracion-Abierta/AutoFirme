package javax.help;

/* JADX INFO: loaded from: autofirma.jar:javax/help/MainWindow.class */
public class MainWindow extends WindowPresentation {
    private static final boolean debug = false;

    private MainWindow(HelpSet helpSet) {
        super(helpSet);
    }

    public static Presentation getPresentation(HelpSet helpSet, String str) {
        MainWindow mainWindow = new MainWindow(helpSet);
        if (helpSet != null) {
            HelpSet.Presentation defaultPresentation = null;
            if (str != null) {
                defaultPresentation = helpSet.getPresentation(str);
            }
            if (defaultPresentation == null) {
                defaultPresentation = helpSet.getDefaultPresentation();
            }
            mainWindow.setHelpSetPresentation(defaultPresentation);
        }
        return mainWindow;
    }

    private static void debug(Object obj) {
    }
}
