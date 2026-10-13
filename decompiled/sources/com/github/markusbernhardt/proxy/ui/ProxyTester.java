package com.github.markusbernhardt.proxy.ui;

import com.github.markusbernhardt.proxy.ProxySearch;
import com.github.markusbernhardt.proxy.util.Logger;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbBase;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URL;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.slf4j.helpers.MessageFormatter;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/ui/ProxyTester.class */
public class ProxyTester extends JFrame {
    private static final long serialVersionUID = 1;
    private JComboBox<ProxySearch.Strategy> modes;
    private JButton testButton;
    private JTextField urlField;
    private JTextArea logArea;

    public ProxyTester() {
        init();
    }

    private void init() {
        setTitle("Proxy Vole Tester");
        setDefaultCloseOperation(3);
        JPanel p = new JPanel();
        p.add(new JLabel("Mode:"));
        this.modes = new JComboBox<>(ProxySearch.Strategy.values());
        p.add(this.modes);
        p.add(new JLabel("URL:"));
        this.urlField = new JTextField(30);
        this.urlField.setText("http://www.google.com/");
        p.add(this.urlField);
        this.testButton = new JButton("Test");
        this.testButton.addActionListener(new ActionListener() { // from class: com.github.markusbernhardt.proxy.ui.ProxyTester.1
            public void actionPerformed(ActionEvent arg0) {
                ProxyTester.this.testUrl();
            }
        });
        p.add(this.testButton);
        this.logArea = new JTextArea(5, 50);
        JPanel contenPane = new JPanel(new BorderLayout());
        contenPane.add(p, "North");
        contenPane.add(new JScrollPane(this.logArea), "Center");
        setContentPane(contenPane);
        pack();
        setLocationRelativeTo(null);
        installLogger();
    }

    private void installLogger() {
        Logger.setBackend(new Logger.LogBackEnd() { // from class: com.github.markusbernhardt.proxy.ui.ProxyTester.2
            @Override // com.github.markusbernhardt.proxy.util.Logger.LogBackEnd
            public void log(Class<?> clazz, Logger.LogLevel loglevel, String msg, Object... params) {
                ProxyTester.this.logArea.append(loglevel + TlbBase.TAB + MessageFormatter.format(msg, params).getMessage() + "\n");
            }
        });
    }

    protected void testUrl() {
        try {
            if (this.urlField.getText().trim().length() == 0) {
                this.logArea.append("ERROR\tPlease enter an URL first.\n");
                return;
            }
            this.logArea.setText("");
            ProxySearch.Strategy pss = (ProxySearch.Strategy) this.modes.getSelectedItem();
            ProxySearch ps = new ProxySearch();
            ps.addStrategy(pss);
            ProxySelector psel = ps.getProxySelector();
            if (psel == null) {
                this.logArea.append("ERROR\tNo proxy settings available for this mode.\n");
                return;
            }
            ProxySelector.setDefault(psel);
            URL url = new URL(this.urlField.getText().trim());
            List<Proxy> result = psel.select(url.toURI());
            if (result == null || result.size() == 0) {
                this.logArea.append("INFO\tNo proxy found for this url.\n");
            } else {
                this.logArea.append("INFO\tProxy Settings found using " + pss + " strategy.\n");
                this.logArea.append("INFO\tProxy used for URL is: " + result.get(0) + "\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error:" + e.getMessage(), "Error checking URL.", 0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() { // from class: com.github.markusbernhardt.proxy.ui.ProxyTester.3
            @Override // java.lang.Runnable
            public void run() {
                ProxyTester.setLookAndFeel();
                ProxyTester mainFrame = new ProxyTester();
                mainFrame.setVisible(true);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
    }
}
