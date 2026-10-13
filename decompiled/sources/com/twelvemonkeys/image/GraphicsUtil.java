package com.twelvemonkeys.image;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/GraphicsUtil.class */
public final class GraphicsUtil {
    public static void enableAA(Graphics graphics) {
        ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public static void setAlpha(Graphics graphics, float f) {
        ((Graphics2D) graphics).setComposite(AlphaComposite.getInstance(3, f));
    }
}
