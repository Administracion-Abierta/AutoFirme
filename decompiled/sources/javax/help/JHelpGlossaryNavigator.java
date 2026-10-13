package javax.help;

/* JADX INFO: loaded from: autofirma.jar:javax/help/JHelpGlossaryNavigator.class */
public class JHelpGlossaryNavigator extends JHelpNavigator {
    private static final boolean debug = false;

    public JHelpGlossaryNavigator(NavigatorView navigatorView) {
        super(navigatorView);
    }

    public JHelpGlossaryNavigator(NavigatorView navigatorView, HelpModel helpModel) {
        super(navigatorView, helpModel);
    }

    public JHelpGlossaryNavigator(HelpSet helpSet, String str, String str2) throws InvalidNavigatorViewException {
        super(new GlossaryView(helpSet, str, str2, null));
    }

    @Override // javax.help.JHelpNavigator
    public String getUIClassID() {
        return "HelpGlossaryNavigatorUI";
    }

    private static void debug(Object obj, Object obj2, Object obj3) {
    }

    private static void debug(Object obj) {
        debug(obj, null, null);
    }

    private static void debug(Object obj, Object obj2) {
        debug(obj, obj2, null);
    }
}
