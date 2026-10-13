package es.gob.afirma.standalone.configurator;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/GraphicConfiguratorConsole.class */
final class GraphicConfiguratorConsole extends JFrame implements Console {
    private static final int DEFAULT_WINDOW_WIDTH = 480;
    private static final int DEFAULT_WINDOW_HEIGHT = 350;
    private static final long serialVersionUID = 398187262022150395L;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private final JTextArea console = new JTextArea();
    private final ConsoleListener listener;

    ConsoleListener getConsoleListener() {
        return this.listener;
    }

    GraphicConfiguratorConsole(ConsoleListener consoleListener) {
        this.listener = consoleListener;
    }

    @Override // es.gob.afirma.standalone.configurator.Console
    public void showConsole() {
        SwingUtilities.invokeLater(() -> {
            createUI(new WindowListener() { // from class: es.gob.afirma.standalone.configurator.GraphicConfiguratorConsole.1
                public void windowClosing(WindowEvent windowEvent) {
                    if (GraphicConfiguratorConsole.this.getConsoleListener() != null) {
                        GraphicConfiguratorConsole.this.getConsoleListener().close();
                    }
                }

                public void windowOpened(WindowEvent windowEvent) {
                }

                public void windowClosed(WindowEvent windowEvent) {
                }

                public void windowActivated(WindowEvent windowEvent) {
                }

                public void windowIconified(WindowEvent windowEvent) {
                }

                public void windowDeiconified(WindowEvent windowEvent) {
                }

                public void windowDeactivated(WindowEvent windowEvent) {
                }
            }, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
        });
    }

    void createUI(WindowListener windowListener, int i, int i2) {
        if (!LookAndFeelManager.HIGH_CONTRAST) {
            setBackground(LookAndFeelManager.WINDOW_COLOR);
        }
        setTitle(Messages.getString("ConfiguratorConsole.0"));
        setSize(i, i2);
        setLayout(new GridBagLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(0);
        if (windowListener != null) {
            addWindowListener(windowListener);
        }
        this.console.setMargin(new Insets(5, 5, 5, 5));
        this.console.setEditable(false);
        Component jScrollPane = new JScrollPane(this.console);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.insets = new Insets(11, 11, 11, 11);
        add(jScrollPane, gridBagConstraints);
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/logo_cliente_256.png")));
        } catch (Exception e) {
            LOGGER.warning("No se ha podido cargar el icono de la aplicacion: " + e);
        }
        setVisible(true);
    }

    @Override // es.gob.afirma.standalone.configurator.Console
    public void print(String str) {
        try {
            this.console.append(str);
            this.console.append("\n");
            LOGGER.info("Mensaje de consola: " + str);
        } catch (Exception e) {
            LOGGER.warning("No se pudo mostrar por consola el mensaje '" + str + "': " + e);
        }
    }

    @Override // es.gob.afirma.standalone.configurator.Console
    public Component getParentComponent() {
        return this;
    }
}
