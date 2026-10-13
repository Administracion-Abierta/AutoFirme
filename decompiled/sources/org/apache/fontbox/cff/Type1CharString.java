package org.apache.fontbox.cff;

import com.aowagie.text.pdf.ColumnText;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.encoding.StandardEncoding;
import org.apache.fontbox.type1.Type1CharStringReader;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/Type1CharString.class */
public class Type1CharString {
    private static final Log LOG = LogFactory.getLog(Type1CharString.class);
    private Type1CharStringReader font;
    private final String fontName;
    private final String glyphName;
    private GeneralPath path;
    private int width;
    private Point2D.Float leftSideBearing;
    private Point2D.Float current;
    private boolean isFlex;
    private final List<Point2D.Float> flexPoints;
    protected List<Object> type1Sequence;
    protected int commandCount;

    public Type1CharString(Type1CharStringReader font, String fontName, String glyphName, List<Object> sequence) {
        this(font, fontName, glyphName);
        this.type1Sequence = sequence;
    }

    protected Type1CharString(Type1CharStringReader font, String fontName, String glyphName) {
        this.path = null;
        this.width = 0;
        this.leftSideBearing = null;
        this.current = null;
        this.isFlex = false;
        this.flexPoints = new ArrayList();
        this.font = font;
        this.fontName = fontName;
        this.glyphName = glyphName;
        this.current = new Point2D.Float(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public String getName() {
        return this.glyphName;
    }

    public Rectangle2D getBounds() {
        synchronized (LOG) {
            if (this.path == null) {
                render();
            }
        }
        return this.path.getBounds2D();
    }

    public int getWidth() {
        synchronized (LOG) {
            if (this.path == null) {
                render();
            }
        }
        return this.width;
    }

    public GeneralPath getPath() {
        synchronized (LOG) {
            if (this.path == null) {
                render();
            }
        }
        return this.path;
    }

    public List<Object> getType1Sequence() {
        return this.type1Sequence;
    }

    private void render() {
        this.path = new GeneralPath();
        this.leftSideBearing = new Point2D.Float(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.width = 0;
        CharStringHandler handler = new CharStringHandler() { // from class: org.apache.fontbox.cff.Type1CharString.1
            @Override // org.apache.fontbox.cff.CharStringHandler
            public List<Number> handleCommand(List<Number> numbers, CharStringCommand command) {
                return Type1CharString.this.handleCommand(numbers, command);
            }
        };
        handler.handleSequence(this.type1Sequence);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<Number> handleCommand(List<Number> numbers, CharStringCommand command) {
        this.commandCount++;
        String name = CharStringCommand.TYPE1_VOCABULARY.get(command.getKey());
        if ("rmoveto".equals(name)) {
            if (numbers.size() >= 2) {
                if (this.isFlex) {
                    this.flexPoints.add(new Point2D.Float(numbers.get(0).floatValue(), numbers.get(1).floatValue()));
                    return null;
                }
                rmoveTo(numbers.get(0), numbers.get(1));
                return null;
            }
            return null;
        }
        if ("vmoveto".equals(name)) {
            if (!numbers.isEmpty()) {
                if (this.isFlex) {
                    this.flexPoints.add(new Point2D.Float(ColumnText.GLOBAL_SPACE_CHAR_RATIO, numbers.get(0).floatValue()));
                    return null;
                }
                rmoveTo(0, numbers.get(0));
                return null;
            }
            return null;
        }
        if ("hmoveto".equals(name)) {
            if (!numbers.isEmpty()) {
                if (this.isFlex) {
                    this.flexPoints.add(new Point2D.Float(numbers.get(0).floatValue(), ColumnText.GLOBAL_SPACE_CHAR_RATIO));
                    return null;
                }
                rmoveTo(numbers.get(0), 0);
                return null;
            }
            return null;
        }
        if ("rlineto".equals(name)) {
            if (numbers.size() >= 2) {
                rlineTo(numbers.get(0), numbers.get(1));
                return null;
            }
            return null;
        }
        if ("hlineto".equals(name)) {
            if (!numbers.isEmpty()) {
                rlineTo(numbers.get(0), 0);
                return null;
            }
            return null;
        }
        if ("vlineto".equals(name)) {
            if (!numbers.isEmpty()) {
                rlineTo(0, numbers.get(0));
                return null;
            }
            return null;
        }
        if ("rrcurveto".equals(name)) {
            if (numbers.size() >= 6) {
                rrcurveTo(numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3), numbers.get(4), numbers.get(5));
                return null;
            }
            return null;
        }
        if ("closepath".equals(name)) {
            closeCharString1Path();
            return null;
        }
        if ("sbw".equals(name)) {
            if (numbers.size() >= 3) {
                this.leftSideBearing = new Point2D.Float(numbers.get(0).floatValue(), numbers.get(1).floatValue());
                this.width = numbers.get(2).intValue();
                this.current.setLocation(this.leftSideBearing);
                return null;
            }
            return null;
        }
        if ("hsbw".equals(name)) {
            if (numbers.size() >= 2) {
                this.leftSideBearing = new Point2D.Float(numbers.get(0).floatValue(), ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                this.width = numbers.get(1).intValue();
                this.current.setLocation(this.leftSideBearing);
                return null;
            }
            return null;
        }
        if ("vhcurveto".equals(name)) {
            if (numbers.size() >= 4) {
                rrcurveTo(0, numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3), 0);
                return null;
            }
            return null;
        }
        if ("hvcurveto".equals(name)) {
            if (numbers.size() >= 4) {
                rrcurveTo(numbers.get(0), 0, numbers.get(1), numbers.get(2), 0, numbers.get(3));
                return null;
            }
            return null;
        }
        if ("seac".equals(name)) {
            if (numbers.size() >= 5) {
                seac(numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3), numbers.get(4));
                return null;
            }
            return null;
        }
        if ("setcurrentpoint".equals(name)) {
            if (numbers.size() >= 2) {
                setcurrentpoint(numbers.get(0), numbers.get(1));
                return null;
            }
            return null;
        }
        if ("callothersubr".equals(name)) {
            if (!numbers.isEmpty()) {
                callothersubr(numbers.get(0).intValue());
                return null;
            }
            return null;
        }
        if ("div".equals(name)) {
            float b = numbers.get(numbers.size() - 1).floatValue();
            float a = numbers.get(numbers.size() - 2).floatValue();
            float result = a / b;
            List<Number> list = new ArrayList<>(numbers);
            list.remove(list.size() - 1);
            list.remove(list.size() - 1);
            list.add(Float.valueOf(result));
            return list;
        }
        if (!"hstem".equals(name) && !"vstem".equals(name) && !"hstem3".equals(name) && !"vstem3".equals(name) && !"dotsection".equals(name) && !"endchar".equals(name)) {
            if ("return".equals(name) || "callsubr".equals(name)) {
                LOG.warn("Unexpected charstring command: " + name + " in glyph " + this.glyphName + " of font " + this.fontName);
                return null;
            }
            if (name != null) {
                throw new IllegalArgumentException("Unhandled command: " + name);
            }
            LOG.warn("Unknown charstring command: " + command.getKey() + " in glyph " + this.glyphName + " of font " + this.fontName);
            return null;
        }
        return null;
    }

    private void setcurrentpoint(Number x, Number y) {
        this.current.setLocation(x.floatValue(), y.floatValue());
    }

    private void callothersubr(int num) {
        if (num == 0) {
            this.isFlex = false;
            if (this.flexPoints.size() < 7) {
                LOG.warn("flex without moveTo in font " + this.fontName + ", glyph " + this.glyphName + ", command " + this.commandCount);
                return;
            }
            Point2D.Float reference = this.flexPoints.get(0);
            reference.setLocation(this.current.getX() + reference.getX(), this.current.getY() + reference.getY());
            Point2D.Float first = this.flexPoints.get(1);
            first.setLocation(reference.getX() + first.getX(), reference.getY() + first.getY());
            first.setLocation(first.getX() - this.current.getX(), first.getY() - this.current.getY());
            Point2D.Float p1 = this.flexPoints.get(1);
            Point2D.Float p2 = this.flexPoints.get(2);
            Point2D.Float p3 = this.flexPoints.get(3);
            rrcurveTo(Double.valueOf(p1.getX()), Double.valueOf(p1.getY()), Double.valueOf(p2.getX()), Double.valueOf(p2.getY()), Double.valueOf(p3.getX()), Double.valueOf(p3.getY()));
            Point2D.Float p4 = this.flexPoints.get(4);
            Point2D.Float p5 = this.flexPoints.get(5);
            Point2D.Float p6 = this.flexPoints.get(6);
            rrcurveTo(Double.valueOf(p4.getX()), Double.valueOf(p4.getY()), Double.valueOf(p5.getX()), Double.valueOf(p5.getY()), Double.valueOf(p6.getX()), Double.valueOf(p6.getY()));
            this.flexPoints.clear();
            return;
        }
        if (num == 1) {
            this.isFlex = true;
        } else {
            LOG.warn("Invalid callothersubr parameter: " + num);
        }
    }

    private void rmoveTo(Number dx, Number dy) {
        float x = ((float) this.current.getX()) + dx.floatValue();
        float y = ((float) this.current.getY()) + dy.floatValue();
        this.path.moveTo(x, y);
        this.current.setLocation(x, y);
    }

    private void rlineTo(Number dx, Number dy) {
        float x = ((float) this.current.getX()) + dx.floatValue();
        float y = ((float) this.current.getY()) + dy.floatValue();
        if (this.path.getCurrentPoint() == null) {
            LOG.warn("rlineTo without initial moveTo in font " + this.fontName + ", glyph " + this.glyphName);
            this.path.moveTo(x, y);
        } else {
            this.path.lineTo(x, y);
        }
        this.current.setLocation(x, y);
    }

    private void rrcurveTo(Number dx1, Number dy1, Number dx2, Number dy2, Number dx3, Number dy3) {
        float x1 = ((float) this.current.getX()) + dx1.floatValue();
        float y1 = ((float) this.current.getY()) + dy1.floatValue();
        float x2 = x1 + dx2.floatValue();
        float y2 = y1 + dy2.floatValue();
        float x3 = x2 + dx3.floatValue();
        float y3 = y2 + dy3.floatValue();
        if (this.path.getCurrentPoint() == null) {
            LOG.warn("rrcurveTo without initial moveTo in font " + this.fontName + ", glyph " + this.glyphName);
            this.path.moveTo(x3, y3);
        } else {
            this.path.curveTo(x1, y1, x2, y2, x3, y3);
        }
        this.current.setLocation(x3, y3);
    }

    private void closeCharString1Path() {
        if (this.path.getCurrentPoint() == null) {
            LOG.warn("closepath without initial moveTo in font " + this.fontName + ", glyph " + this.glyphName);
        } else {
            this.path.closePath();
        }
        this.path.moveTo(this.current.getX(), this.current.getY());
    }

    private void seac(Number asb, Number adx, Number ady, Number bchar, Number achar) {
        String baseName = StandardEncoding.INSTANCE.getName(bchar.intValue());
        try {
            Type1CharString base = this.font.getType1CharString(baseName);
            this.path.append(base.getPath().getPathIterator((AffineTransform) null), false);
        } catch (IOException e) {
            LOG.warn("invalid seac character in glyph " + this.glyphName + " of font " + this.fontName);
        }
        String accentName = StandardEncoding.INSTANCE.getName(achar.intValue());
        try {
            Type1CharString accent = this.font.getType1CharString(accentName);
            AffineTransform at = AffineTransform.getTranslateInstance((this.leftSideBearing.getX() + ((double) adx.floatValue())) - ((double) asb.floatValue()), this.leftSideBearing.getY() + ((double) ady.floatValue()));
            this.path.append(accent.getPath().getPathIterator(at), false);
        } catch (IOException e2) {
            LOG.warn("invalid seac character in glyph " + this.glyphName + " of font " + this.fontName);
        }
    }

    public String toString() {
        return this.type1Sequence.toString().replace("|", "\n").replace(",", " ");
    }
}
