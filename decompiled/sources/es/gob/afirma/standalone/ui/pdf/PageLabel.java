package es.gob.afirma.standalone.ui.pdf;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.util.EventListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/PageLabel.class */
final class PageLabel extends JLabel {
    private static final long serialVersionUID = 4917110251831788580L;
    private static final int TRANSPARENCY_LEVEL = 3;
    private final PageLabelListener listener;
    private final float scale;
    private Rectangle selectionBounds;
    private Point clickPoint;

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/PageLabel$PageLabelListener.class */
    interface PageLabelListener extends EventListener {
        void setX(String str);

        void setY(String str);

        void setWidth(String str);

        void setHeight(String str);
    }

    PageLabelListener getPageLabelListener() {
        return this.listener;
    }

    void setSelectionBounds(Rectangle rectangle) {
        this.selectionBounds = rectangle;
    }

    Rectangle getSelectionBounds() {
        this.selectionBounds = null;
        return null;
    }

    Point getClickPoint() {
        return this.clickPoint;
    }

    void setClickPoint(Point point) {
        this.clickPoint = point;
    }

    PageLabel(Image image, int i, int i2, PageLabelListener pageLabelListener, float f) {
        super(new ImageIcon(image));
        this.selectionBounds = null;
        if (pageLabelListener == null) {
            throw new IllegalArgumentException();
        }
        this.listener = pageLabelListener;
        this.scale = f;
        setSize(i, i2);
        setCursor(new Cursor(1));
        MouseAdapter mouseAdapter = new MouseAdapter() { // from class: es.gob.afirma.standalone.ui.pdf.PageLabel.1
            public void mouseClicked(MouseEvent mouseEvent) {
            }

            public void mousePressed(MouseEvent mouseEvent) {
                PageLabel.this.clearAreaValues();
                PageLabel.this.setClickPoint(mouseEvent.getPoint());
                PageLabel.this.setSelectionBounds(null);
                PageLabel.this.repaint();
            }

            public void mouseReleased(MouseEvent mouseEvent) {
                int i3;
                int i4;
                int i5;
                int i6;
                Point clickPoint = PageLabel.this.getClickPoint();
                Point point = mouseEvent.getPoint();
                if (clickPoint.getX() == point.getX() || clickPoint.getY() == point.getY()) {
                    PageLabel.this.clearAreaValues();
                } else {
                    int width = PageLabel.this.getWidth();
                    int height = PageLabel.this.getHeight();
                    int x = (int) clickPoint.getX();
                    if (x < 0) {
                        i3 = 0;
                    } else {
                        i3 = x > width ? width : x;
                    }
                    int i7 = i3;
                    int y = (int) clickPoint.getY();
                    if (y < 0) {
                        i4 = 0;
                    } else {
                        i4 = y > height ? height : y;
                    }
                    int i8 = i4;
                    int x2 = (int) point.getX();
                    if (x2 < 0) {
                        i5 = 0;
                    } else {
                        i5 = x2 > width ? width : x2;
                    }
                    int i9 = i5;
                    int y2 = (int) point.getY();
                    if (y2 < 0) {
                        i6 = 0;
                    } else {
                        i6 = y2 > height ? height : y2;
                    }
                    int i10 = i6;
                    PageLabel.this.setAreaValues(Math.min(i7, i9), Math.min(i8, i10), Math.abs(i7 - i9), Math.abs(i8 - i10));
                }
                PageLabel.this.setClickPoint(null);
            }

            public void mouseDragged(MouseEvent mouseEvent) {
                if (PageLabel.this.getClickPoint() != null) {
                    Point point = mouseEvent.getPoint();
                    PageLabel.this.setSelectionBounds(new Rectangle(Math.min(PageLabel.this.getClickPoint().x, point.x), Math.min(PageLabel.this.getClickPoint().y, point.y), Math.max(PageLabel.this.getClickPoint().x - point.x, point.x - PageLabel.this.getClickPoint().x), Math.max(PageLabel.this.getClickPoint().y - point.y, point.y - PageLabel.this.getClickPoint().y)));
                    PageLabel.this.repaint();
                }
            }

            public void mouseMoved(MouseEvent mouseEvent) {
            }

            public void mouseExited(MouseEvent mouseEvent) {
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    void clearAreaValues() {
        getPageLabelListener().setX("");
        getPageLabelListener().setY("");
        getPageLabelListener().setWidth("");
        getPageLabelListener().setHeight("");
    }

    void setAreaValues(int i, int i2, int i3, int i4) {
        getPageLabelListener().setX(Integer.toString(Math.round(Math.min(i, getWidth()) / this.scale)));
        getPageLabelListener().setY(Integer.toString(Math.round(Math.min(i2, getHeight()) / this.scale)));
        getPageLabelListener().setWidth(Integer.toString(Math.round(i3 / this.scale)));
        getPageLabelListener().setHeight(Integer.toString(Math.round(i4 / this.scale)));
    }

    public float getScale() {
        return this.scale;
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2DCreate = graphics.create();
        graphics2DCreate.setColor(new Color(255, 255, 255, 128));
        Area area = new Area(new Rectangle(new Point(0, 0), getSize()));
        if (this.selectionBounds != null) {
            area.subtract(new Area(this.selectionBounds));
        }
        graphics2DCreate.fill(area);
        if (this.selectionBounds != null) {
            graphics2DCreate.setColor(Color.BLUE);
            graphics2DCreate.setComposite(AlphaComposite.getInstance(3, 0.3f));
            graphics2DCreate.fill(this.selectionBounds);
        }
        graphics2DCreate.dispose();
    }
}
