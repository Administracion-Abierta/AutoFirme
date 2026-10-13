package com.aowagie.text;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/ElementTags.class */
public class ElementTags {
    public static final String ITEXT = "itext";
    public static final String TITLE = "title";
    static final String SUBJECT = "subject";
    static final String KEYWORDS = "keywords";
    static final String AUTHOR = "author";
    static final String CREATIONDATE = "creationdate";
    static final String PRODUCER = "producer";
    public static final String CHAPTER = "chapter";
    public static final String SECTION = "section";
    public static final String NUMBERDEPTH = "numberdepth";
    public static final String INDENT = "indent";
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String PHRASE = "phrase";
    public static final String ANCHOR = "anchor";
    public static final String LIST = "list";
    public static final String LISTITEM = "listitem";
    public static final String PARAGRAPH = "paragraph";
    public static final String LEADING = "leading";
    public static final String ALIGN = "align";
    public static final String NAME = "name";
    public static final String REFERENCE = "reference";
    public static final String LISTSYMBOL = "listsymbol";
    public static final String NUMBERED = "numbered";
    public static final String LETTERED = "lettered";
    public static final String FIRST = "first";
    public static final String SYMBOLINDENT = "symbolindent";
    public static final String INDENTATIONLEFT = "indentationleft";
    public static final String INDENTATIONRIGHT = "indentationright";
    public static final String IGNORE = "ignore";
    public static final String ENTITY = "entity";
    public static final String ID = "id";
    public static final String CHUNK = "chunk";
    static final String ENCODING = "encoding";
    static final String EMBEDDED = "embedded";
    public static final String COLOR = "color";
    public static final String RED = "red";
    public static final String GREEN = "green";
    public static final String BLUE = "blue";
    public static final String SUBSUPSCRIPT = Chunk.SUBSUPSCRIPT.toLowerCase();
    public static final String LOCALGOTO = Chunk.LOCALGOTO.toLowerCase();
    public static final String REMOTEGOTO = Chunk.REMOTEGOTO.toLowerCase();
    public static final String LOCALDESTINATION = Chunk.LOCALDESTINATION.toLowerCase();
    public static final String GENERICTAG = Chunk.GENERICTAG.toLowerCase();
    public static final String TABLE = "table";
    public static final String ROW = "row";
    public static final String CELL = "cell";
    public static final String COLUMNS = "columns";
    public static final String LASTHEADERROW = "lastHeaderRow";
    public static final String CELLPADDING = "cellpadding";
    public static final String CELLSPACING = "cellspacing";
    public static final String OFFSET = "offset";
    public static final String WIDTHS = "widths";
    public static final String TABLEFITSPAGE = "tablefitspage";
    public static final String CELLSFITPAGE = "cellsfitpage";
    public static final String CONVERT2PDFP = "convert2pdfp";
    public static final String HORIZONTALALIGN = "horizontalalign";
    public static final String VERTICALALIGN = "verticalalign";
    public static final String COLSPAN = "colspan";
    public static final String ROWSPAN = "rowspan";
    public static final String HEADER = "header";
    public static final String NOWRAP = "nowrap";
    public static final String BORDERWIDTH = "borderwidth";
    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";
    public static final String WIDTH = "width";
    public static final String BORDERCOLOR = "bordercolor";
    public static final String BACKGROUNDCOLOR = "backgroundcolor";
    public static final String BGRED = "bgred";
    public static final String BGGREEN = "bggreen";
    public static final String BGBLUE = "bgblue";
    public static final String GRAYFILL = "grayfill";
    public static final String IMAGE = "image";
    public static final String URL = "url";
    public static final String UNDERLYING = "underlying";
    public static final String TEXTWRAP = "textwrap";
    public static final String ALT = "alt";
    public static final String ABSOLUTEX = "absolutex";
    public static final String ABSOLUTEY = "absolutey";
    public static final String PLAINWIDTH = "plainwidth";
    public static final String PLAINHEIGHT = "plainheight";
    public static final String ROTATION = "rotation";
    public static final String NEWPAGE = "newpage";
    public static final String NEWLINE = "newline";
    public static final String ANNOTATION = "annotation";
    public static final String FILE = "file";
    public static final String DESTINATION = "destination";
    public static final String PAGE = "page";
    public static final String NAMED = "named";
    public static final String LLX = "llx";
    public static final String LLY = "lly";
    public static final String URX = "urx";
    public static final String URY = "ury";
    public static final String CONTENT = "content";
    public static final String ALIGN_LEFT = "Left";
    static final String ALIGN_CENTER = "Center";
    public static final String ALIGN_RIGHT = "Right";
    static final String ALIGN_JUSTIFIED = "Justify";
    static final String ALIGN_JUSTIFIED_ALL = "JustifyAll";
    private static final String ALIGN_TOP = "Top";
    public static final String ALIGN_MIDDLE = "Middle";
    private static final String ALIGN_BOTTOM = "Bottom";
    private static final String ALIGN_BASELINE = "Baseline";
    private static final String DEFAULT = "Default";
    static final String UNKNOWN = "unknown";
    public static final String FONT = "font";
    public static final String SIZE = "size";
    public static final String STYLE = "fontstyle";
    public static final String HORIZONTALRULE = "horizontalrule";
    public static final String PAGE_SIZE = "pagesize";
    public static final String ORIENTATION = "orientation";
    public static final String ALIGN_INDENTATION_ITEMS = "alignindent";
    public static final String AUTO_INDENT_ITEMS = "autoindent";
    public static final String LOWERCASE = "lowercase";
    public static final String SRC = "src";

    static int alignmentValue(String alignment) {
        if (alignment == null) {
            return -1;
        }
        if ("Center".equalsIgnoreCase(alignment)) {
            return 1;
        }
        if (ALIGN_LEFT.equalsIgnoreCase(alignment)) {
            return 0;
        }
        if (ALIGN_RIGHT.equalsIgnoreCase(alignment)) {
            return 2;
        }
        if ("Justify".equalsIgnoreCase(alignment)) {
            return 3;
        }
        if (ALIGN_JUSTIFIED_ALL.equalsIgnoreCase(alignment)) {
            return 8;
        }
        if (ALIGN_TOP.equalsIgnoreCase(alignment)) {
            return 4;
        }
        if ("Middle".equalsIgnoreCase(alignment)) {
            return 5;
        }
        if (ALIGN_BOTTOM.equalsIgnoreCase(alignment)) {
            return 6;
        }
        if (ALIGN_BASELINE.equalsIgnoreCase(alignment)) {
            return 7;
        }
        return -1;
    }
}
