package com.sun.java.help.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Vector;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.text.AttributeSet;
import javax.swing.text.ComponentView;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/CustomKit.class */
public class CustomKit extends HTMLEditorKit {
    private MouseMotionListener mouseHandler = new MouseHandler();
    private static final boolean debug = false;

    public Object clone() {
        return new CustomKit();
    }

    public void install(JEditorPane jEditorPane) {
        jEditorPane.addMouseMotionListener(this.mouseHandler);
        super.install(jEditorPane);
    }

    public void deinstall(JEditorPane jEditorPane) {
        jEditorPane.removeMouseMotionListener(this.mouseHandler);
        super.deinstall(jEditorPane);
    }

    public Document createDefaultDocument() {
        CustomDocument customDocument = new CustomDocument(super.createDefaultDocument().getStyleSheet());
        customDocument.putProperty("__PARSER__", getParser());
        customDocument.setAsynchronousLoadPriority(4);
        customDocument.setTokenThreshold(100);
        debug("fetch custom document");
        return customDocument;
    }

    public ViewFactory getViewFactory() {
        debug("fetched custom factory");
        return new CustomFactory();
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/CustomKit$MouseHandler.class */
    public static class MouseHandler implements MouseMotionListener {
        private Cursor origCursor;
        private Element curElem = null;
        private Cursor handCursor = null;

        public void mouseDragged(MouseEvent mouseEvent) {
        }

        public void mouseMoved(MouseEvent mouseEvent) {
            int iViewToModel;
            JEditorPane jEditorPane = (JEditorPane) mouseEvent.getSource();
            if (!jEditorPane.isEditable() && (iViewToModel = jEditorPane.viewToModel(new Point(mouseEvent.getX(), mouseEvent.getY()))) >= 0) {
                HTMLDocument document = jEditorPane.getDocument();
                if (document instanceof HTMLDocument) {
                    Element characterElement = document.getCharacterElement(iViewToModel);
                    AttributeSet attributeSet = (AttributeSet) characterElement.getAttributes().getAttribute(HTML.Tag.A);
                    if ((attributeSet != null ? (String) attributeSet.getAttribute(HTML.Attribute.HREF) : null) != null) {
                        if (this.curElem != characterElement) {
                            this.curElem = characterElement;
                            if (this.origCursor == null) {
                                this.origCursor = jEditorPane.getCursor();
                            }
                            if (this.handCursor == null) {
                                this.handCursor = Cursor.getPredefinedCursor(12);
                            }
                            jEditorPane.setCursor(this.handCursor);
                            return;
                        }
                        return;
                    }
                    if (this.curElem != null) {
                        this.curElem = null;
                        jEditorPane.setCursor(this.origCursor);
                    }
                }
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/CustomKit$CustomFactory.class */
    static class CustomFactory extends HTMLEditorKit.HTMLFactory {
        CustomFactory() {
        }

        public View create(Element element) {
            Object attribute = element.getAttributes().getAttribute(StyleConstants.NameAttribute);
            if ((attribute instanceof HTML.Tag) && ((HTML.Tag) attribute) == HTML.Tag.OBJECT) {
                CustomKit.debug(new StringBuffer().append("creating ObjectView1 for: ").append(element).toString());
                return new ObjectView1(element);
            }
            return super.create(element);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/CustomKit$CustomDocument.class */
    static class CustomDocument extends HTMLDocument {
        CustomDocument(StyleSheet styleSheet) {
            super(styleSheet);
        }

        public HTMLEditorKit.ParserCallback getReader(int i) {
            Object property = getProperty("stream");
            if (property instanceof URL) {
                setBase((URL) property);
            }
            return new CustomReader(this, i);
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/CustomKit$CustomDocument$CustomReader.class */
        class CustomReader extends HTMLDocument.HTMLReader {
            private final CustomDocument this$0;

            public CustomReader(CustomDocument customDocument, int i) {
                super(customDocument, i);
                this.this$0 = customDocument;
                registerTag(HTML.Tag.PARAM, new ObjectAction1(this));
            }

            Vector getParseBuffer() {
                return this.parseBuffer;
            }

            /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/CustomKit$CustomDocument$CustomReader$ObjectAction1.class */
            class ObjectAction1 extends HTMLDocument.HTMLReader.SpecialAction {
                private final CustomReader this$1;

                ObjectAction1(CustomReader customReader) {
                    super(customReader);
                    this.this$1 = customReader;
                }

                public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                    if (tag == HTML.Tag.PARAM) {
                        addParameter(mutableAttributeSet);
                    } else {
                        super.start(tag, mutableAttributeSet);
                    }
                }

                public void end(HTML.Tag tag) {
                    if (tag != HTML.Tag.PARAM) {
                        super.end(tag);
                    }
                }

                void addParameter(AttributeSet attributeSet) {
                    CustomKit.debug(new StringBuffer().append("addParameter AttributeSet=").append(attributeSet).toString());
                    String str = (String) attributeSet.getAttribute(HTML.Attribute.NAME);
                    String str2 = (String) attributeSet.getAttribute(HTML.Attribute.VALUE);
                    if (str != null && str2 != null) {
                        ((DefaultStyledDocument.ElementSpec) this.this$1.getParseBuffer().lastElement()).getAttributes().addAttribute(str, str2);
                    }
                }
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/CustomKit$ObjectView1.class */
    static class ObjectView1 extends ComponentView {
        public ObjectView1(Element element) {
            super(element);
        }

        protected Component createComponent() {
            AttributeSet attributes = getElement().getAttributes();
            CustomKit.debug(new StringBuffer().append("attr: ").append(attributes.copyAttributes()).toString());
            String str = (String) attributes.getAttribute(HTML.Attribute.CLASSID);
            try {
                int iIndexOf = str.indexOf(58);
                if (iIndexOf != -1 && str.substring(0, iIndexOf).toLowerCase().compareTo("java") == 0) {
                    Object objNewInstance = getClass(str.substring(iIndexOf + 1)).newInstance();
                    if (objNewInstance instanceof Component) {
                        ViewAwareComponent viewAwareComponent = (Component) objNewInstance;
                        if (objNewInstance instanceof ViewAwareComponent) {
                            viewAwareComponent.setViewData(this);
                        }
                        setParameters(viewAwareComponent, attributes);
                        return viewAwareComponent;
                    }
                }
            } catch (Throwable th) {
            }
            return getUnloadableRepresentation();
        }

        Component getUnloadableRepresentation() {
            JLabel jLabel = new JLabel("??");
            jLabel.setForeground(Color.red);
            return jLabel;
        }

        private Class getClass(String str) throws ClassNotFoundException {
            Class<?> cls = null;
            ClassLoader classLoader = getDocument().getClass().getClassLoader();
            if (classLoader != null) {
                cls = classLoader.loadClass(str);
            }
            if (cls == null) {
                cls = Class.forName(str);
            }
            return cls;
        }

        private void setParameters(Component component, AttributeSet attributeSet) {
            try {
                PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(component.getClass()).getPropertyDescriptors();
                for (int i = 0; i < propertyDescriptors.length; i++) {
                    CustomKit.debug(new StringBuffer().append("checking on props[i]: ").append(propertyDescriptors[i].getName()).toString());
                    Object attribute = attributeSet.getAttribute(propertyDescriptors[i].getName());
                    if (attribute instanceof String) {
                        String str = (String) attribute;
                        Method writeMethod = propertyDescriptors[i].getWriteMethod();
                        if (writeMethod == null || writeMethod.getParameterTypes().length != 1) {
                            return;
                        }
                        try {
                            writeMethod.invoke(component, str);
                            CustomKit.debug("Invocation succeeded");
                        } catch (Exception e) {
                            CustomKit.debug("Invocation failed");
                        }
                    }
                }
            } catch (IntrospectionException e2) {
                CustomKit.debug(new StringBuffer().append("introspector failed, ex: ").append(e2).toString());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void debug(String str) {
    }
}
