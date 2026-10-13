package com.aowagie.text.pdf;

import es.gob.afirma.core.misc.protocol.ProtocolConstants;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.signvalidation.ISignatureFormatDetector;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.apache.fontbox.afm.AFMParser;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkedContentReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDObjectReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDUserAttributeObject;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.apache.pdfbox.pdmodel.encryption.PublicKeySecurityHandler;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionEmbeddedGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionHide;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionImportData;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionLaunch;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionNamed;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionRemoteGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionResetForm;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionSubmitForm;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationPopup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.measurement.PDNumberFormatDictionary;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.apache.xml.security.utils.EncryptionConstants;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfName.class */
public class PdfName extends PdfObject implements Comparable {
    public static final PdfName _3D = new PdfName("3D");
    public static final PdfName A = new PdfName("A");
    public static final PdfName AA = new PdfName("AA");
    public static final PdfName ABSOLUTECOLORIMETRIC = new PdfName("AbsoluteColorimetric");
    public static final PdfName AC = new PdfName("AC");
    public static final PdfName ACROFORM = new PdfName("AcroForm");
    public static final PdfName ACTION = new PdfName(PDAction.TYPE);
    public static final PdfName ACTIVATION = new PdfName("Activation");
    public static final PdfName ADBE = new PdfName("ADBE");
    public static final PdfName ACTUALTEXT = new PdfName("ActualText");
    public static final PdfName ADBE_PKCS7_DETACHED = new PdfName(AOSignConstants.PADES_SUBFILTER_BASIC);
    public static final PdfName ADBE_PKCS7_S4 = new PdfName("adbe.pkcs7.s4");
    public static final PdfName ADBE_PKCS7_S5 = new PdfName("adbe.pkcs7.s5");
    public static final PdfName ADBE_PKCS7_SHA1 = new PdfName("adbe.pkcs7.sha1");
    public static final PdfName ADBE_X509_RSA_SHA1 = new PdfName("adbe.x509.rsa_sha1");
    public static final PdfName ADOBE_PPKLITE = new PdfName("Adobe.PPKLite");
    public static final PdfName ADOBE_PPKMS = new PdfName("Adobe.PPKMS");
    public static final PdfName AESV2 = new PdfName("AESV2");
    public static final PdfName AESV3 = new PdfName("AESV3");
    public static final PdfName AIS = new PdfName("AIS");
    public static final PdfName ALLPAGES = new PdfName("AllPages");
    public static final PdfName ALT = new PdfName("Alt");
    public static final PdfName ALTERNATE = new PdfName("Alternate");
    public static final PdfName ANIMATION = new PdfName("Animation");
    public static final PdfName ANNOT = new PdfName(StandardStructureTypes.ANNOT);
    public static final PdfName ANNOTS = new PdfName("Annots");
    public static final PdfName ANTIALIAS = new PdfName("AntiAlias");
    public static final PdfName AP = new PdfName("AP");
    public static final PdfName APPDEFAULT = new PdfName("AppDefault");
    public static final PdfName ART = new PdfName(StandardStructureTypes.ART);
    public static final PdfName ARTBOX = new PdfName(PDViewerPreferences.BOUNDARY_ART_BOX);
    public static final PdfName ASCENT = new PdfName("Ascent");
    public static final PdfName AS = new PdfName("AS");
    public static final PdfName ASCII85DECODE = new PdfName("ASCII85Decode");
    public static final PdfName ASCIIHEXDECODE = new PdfName("ASCIIHexDecode");
    public static final PdfName ASSET = new PdfName("Asset");
    public static final PdfName ASSETS = new PdfName("Assets");
    public static final PdfName AUTHEVENT = new PdfName("AuthEvent");
    public static final PdfName AUTHOR = new PdfName("Author");
    public static final PdfName B = new PdfName("B");
    public static final PdfName BACKGROUND = new PdfName("Background");
    public static final PdfName BASEENCODING = new PdfName("BaseEncoding");
    public static final PdfName BASEFONT = new PdfName("BaseFont");
    public static final PdfName BASEVERSION = new PdfName("BaseVersion");
    public static final PdfName BBOX = new PdfName("BBox");
    public static final PdfName BC = new PdfName(BouncyCastleProvider.PROVIDER_NAME);
    public static final PdfName BG = new PdfName("BG");
    public static final PdfName BIBENTRY = new PdfName(StandardStructureTypes.BIB_ENTRY);
    public static final PdfName BIGFIVE = new PdfName("BigFive");
    public static final PdfName BINDING = new PdfName("Binding");
    public static final PdfName BINDINGMATERIALNAME = new PdfName("BindingMaterialName");
    public static final PdfName BITSPERCOMPONENT = new PdfName("BitsPerComponent");
    public static final PdfName BITSPERSAMPLE = new PdfName("BitsPerSample");
    public static final PdfName BL = new PdfName("Bl");
    public static final PdfName BLACKIS1 = new PdfName("BlackIs1");
    public static final PdfName BLACKPOINT = new PdfName("BlackPoint");
    public static final PdfName BLOCKQUOTE = new PdfName(StandardStructureTypes.BLOCK_QUOTE);
    public static final PdfName BLEEDBOX = new PdfName(PDViewerPreferences.BOUNDARY_BLEED_BOX);
    public static final PdfName BLINDS = new PdfName("Blinds");
    public static final PdfName BM = new PdfName("BM");
    public static final PdfName BORDER = new PdfName("Border");
    public static final PdfName BOUNDS = new PdfName("Bounds");
    public static final PdfName BOX = new PdfName("Box");
    public static final PdfName BS = new PdfName("BS");
    public static final PdfName BTN = new PdfName("Btn");
    public static final PdfName BYTERANGE = new PdfName("ByteRange");
    public static final PdfName C = new PdfName("C");
    public static final PdfName C0 = new PdfName("C0");
    public static final PdfName C1 = new PdfName("C1");
    public static final PdfName CA = new PdfName("CA");
    public static final PdfName ca = new PdfName("ca");
    public static final PdfName CALGRAY = new PdfName("CalGray");
    public static final PdfName CALRGB = new PdfName("CalRGB");
    public static final PdfName CAPHEIGHT = new PdfName(AFMParser.CAP_HEIGHT);
    public static final PdfName CAPTION = new PdfName(StandardStructureTypes.CAPTION);
    public static final PdfName CATALOG = new PdfName("Catalog");
    public static final PdfName CATEGORY = new PdfName("Category");
    public static final PdfName CCITTFAXDECODE = new PdfName("CCITTFaxDecode");
    public static final PdfName CENTER = new PdfName("Center");
    public static final PdfName CENTERWINDOW = new PdfName("CenterWindow");
    public static final PdfName CERT = new PdfName("Cert");
    public static final PdfName CF = new PdfName("CF");
    public static final PdfName CFM = new PdfName("CFM");
    public static final PdfName CH = new PdfName("Ch");
    public static final PdfName CHARPROCS = new PdfName("CharProcs");
    public static final PdfName CI = new PdfName("CI");
    public static final PdfName CIDFONTTYPE0 = new PdfName("CIDFontType0");
    public static final PdfName CIDFONTTYPE2 = new PdfName("CIDFontType2");
    public static final PdfName CIDSET = new PdfName("CIDSet");
    public static final PdfName CIDSYSTEMINFO = new PdfName("CIDSystemInfo");
    public static final PdfName CIDTOGIDMAP = new PdfName("CIDToGIDMap");
    public static final PdfName CIRCLE = new PdfName("Circle");
    public static final PdfName CMD = new PdfName("CMD");
    public static final PdfName CO = new PdfName("CO");
    public static final PdfName CODE = new PdfName(StandardStructureTypes.CODE);
    public static final PdfName COLORS = new PdfName("Colors");
    public static final PdfName COLORSPACE = new PdfName("ColorSpace");
    public static final PdfName COLLECTION = new PdfName("Collection");
    public static final PdfName COLLECTIONFIELD = new PdfName("CollectionField");
    public static final PdfName COLLECTIONITEM = new PdfName("CollectionItem");
    public static final PdfName COLLECTIONSCHEMA = new PdfName("CollectionSchema");
    public static final PdfName COLLECTIONSORT = new PdfName("CollectionSort");
    public static final PdfName COLLECTIONSUBITEM = new PdfName("CollectionSubitem");
    public static final PdfName COLUMNS = new PdfName("Columns");
    public static final PdfName CONDITION = new PdfName("Condition");
    public static final PdfName CONFIGURATION = new PdfName("Configuration");
    public static final PdfName CONFIGURATIONS = new PdfName("Configurations");
    public static final PdfName CONTACTINFO = new PdfName("ContactInfo");
    public static final PdfName CONTENT = new PdfName("Content");
    public static final PdfName CONTENTS = new PdfName("Contents");
    public static final PdfName COORDS = new PdfName("Coords");
    public static final PdfName COUNT = new PdfName("Count");
    public static final PdfName COURIER = new PdfName("Courier");
    public static final PdfName COURIER_BOLD = new PdfName("Courier-Bold");
    public static final PdfName COURIER_OBLIQUE = new PdfName("Courier-Oblique");
    public static final PdfName COURIER_BOLDOBLIQUE = new PdfName("Courier-BoldOblique");
    public static final PdfName CREATIONDATE = new PdfName("CreationDate");
    public static final PdfName CREATOR = new PdfName("Creator");
    public static final PdfName CREATORINFO = new PdfName("CreatorInfo");
    public static final PdfName CROPBOX = new PdfName(PDViewerPreferences.BOUNDARY_CROP_BOX);
    public static final PdfName CRYPT = new PdfName("Crypt");
    public static final PdfName CS = new PdfName(OperatorName.STROKING_COLORSPACE);
    public static final PdfName CUEPOINT = new PdfName("CuePoint");
    public static final PdfName CUEPOINTS = new PdfName("CuePoints");
    public static final PdfName D = new PdfName("D");
    public static final PdfName DA = new PdfName("DA");
    public static final PdfName DATA = new PdfName(AOSignConstants.CMS_CONTENTTYPE_DATA);
    public static final PdfName DC = new PdfName("DC");
    public static final PdfName DCTDECODE = new PdfName("DCTDecode");
    public static final PdfName DEACTIVATION = new PdfName("Deactivation");
    public static final PdfName DECODE = new PdfName("Decode");
    public static final PdfName DECODEPARMS = new PdfName("DecodeParms");
    public static final PdfName DEFAULT = new PdfName("Default");
    public static final PdfName DEFAULTCRYPTFILTER = new PdfName("DefaultCryptFilter");
    public static final PdfName DEFAULTCMYK = new PdfName("DefaultCMYK");
    public static final PdfName DEFAULTGRAY = new PdfName("DefaultGray");
    public static final PdfName DEFAULTRGB = new PdfName("DefaultRGB");
    public static final PdfName DESC = new PdfName("Desc");
    public static final PdfName DESCENDANTFONTS = new PdfName("DescendantFonts");
    public static final PdfName DESCENT = new PdfName("Descent");
    public static final PdfName DEST = new PdfName("Dest");
    public static final PdfName DESTOUTPUTPROFILE = new PdfName("DestOutputProfile");
    public static final PdfName DESTS = new PdfName("Dests");
    public static final PdfName DEVICEGRAY = new PdfName("DeviceGray");
    public static final PdfName DEVICERGB = new PdfName("DeviceRGB");
    public static final PdfName DEVICECMYK = new PdfName("DeviceCMYK");
    public static final PdfName DI = new PdfName("Di");
    public static final PdfName DIFFERENCES = new PdfName("Differences");
    public static final PdfName DISSOLVE = new PdfName("Dissolve");
    public static final PdfName DIRECTION = new PdfName("Direction");
    public static final PdfName DISPLAYDOCTITLE = new PdfName("DisplayDocTitle");
    public static final PdfName DIV = new PdfName(StandardStructureTypes.DIV);
    public static final PdfName DM = new PdfName("Dm");
    public static final PdfName DOCMDP = new PdfName("DocMDP");
    public static final PdfName DOCOPEN = new PdfName("DocOpen");
    public static final PdfName DOCUMENT = new PdfName(StandardStructureTypes.DOCUMENT);
    public static final PdfName DOMAIN = new PdfName("Domain");
    public static final PdfName DP = new PdfName(OperatorName.MARKED_CONTENT_POINT_WITH_PROPS);
    public static final PdfName DR = new PdfName("DR");
    public static final PdfName DS = new PdfName("DS");
    public static final PdfName DUR = new PdfName("Dur");
    public static final PdfName DUPLEX = new PdfName("Duplex");
    public static final PdfName DUPLEXFLIPSHORTEDGE = new PdfName("DuplexFlipShortEdge");
    public static final PdfName DUPLEXFLIPLONGEDGE = new PdfName("DuplexFlipLongEdge");
    public static final PdfName DV = new PdfName("DV");
    public static final PdfName DW = new PdfName("DW");
    public static final PdfName E = new PdfName("E");
    public static final PdfName EARLYCHANGE = new PdfName("EarlyChange");
    public static final PdfName EF = new PdfName("EF");
    public static final PdfName EFF = new PdfName("EFF");
    public static final PdfName EFOPEN = new PdfName("EFOpen");
    public static final PdfName EMBEDDED = new PdfName("Embedded");
    public static final PdfName EMBEDDEDFILE = new PdfName("EmbeddedFile");
    public static final PdfName EMBEDDEDFILES = new PdfName("EmbeddedFiles");
    public static final PdfName ENCODE = new PdfName("Encode");
    public static final PdfName ENCODEDBYTEALIGN = new PdfName("EncodedByteAlign");
    public static final PdfName ENCODING = new PdfName("Encoding");
    public static final PdfName ENCRYPT = new PdfName("Encrypt");
    public static final PdfName ENCRYPTMETADATA = new PdfName("EncryptMetadata");
    public static final PdfName ENDOFBLOCK = new PdfName("EndOfBlock");
    public static final PdfName ENDOFLINE = new PdfName("EndOfLine");
    public static final PdfName EXTEND = new PdfName("Extend");
    public static final PdfName EXTENSIONS = new PdfName("Extensions");
    public static final PdfName EXTENSIONLEVEL = new PdfName("ExtensionLevel");
    public static final PdfName EXTGSTATE = new PdfName("ExtGState");
    public static final PdfName EXPORT = new PdfName("Export");
    public static final PdfName EXPORTSTATE = new PdfName("ExportState");
    public static final PdfName EVENT = new PdfName("Event");
    public static final PdfName F = new PdfName("F");
    public static final PdfName FAR = new PdfName("Far");
    public static final PdfName FB = new PdfName("FB");
    public static final PdfName FDECODEPARMS = new PdfName("FDecodeParms");
    public static final PdfName FDF = new PdfName("FDF");
    public static final PdfName FF = new PdfName("Ff");
    public static final PdfName FFILTER = new PdfName("FFilter");
    public static final PdfName FIELDS = new PdfName("Fields");
    public static final PdfName FIGURE = new PdfName(StandardStructureTypes.Figure);
    public static final PdfName FILEATTACHMENT = new PdfName("FileAttachment");
    public static final PdfName FILESPEC = new PdfName("Filespec");
    public static final PdfName FILTER = new PdfName("Filter");
    public static final PdfName FIRST = new PdfName("First");
    public static final PdfName FIRSTCHAR = new PdfName("FirstChar");
    public static final PdfName FIRSTPAGE = new PdfName("FirstPage");
    public static final PdfName FIT = new PdfName("Fit");
    public static final PdfName FITH = new PdfName("FitH");
    public static final PdfName FITV = new PdfName("FitV");
    public static final PdfName FITR = new PdfName("FitR");
    public static final PdfName FITB = new PdfName("FitB");
    public static final PdfName FITBH = new PdfName("FitBH");
    public static final PdfName FITBV = new PdfName("FitBV");
    public static final PdfName FITWINDOW = new PdfName("FitWindow");
    public static final PdfName FLAGS = new PdfName("Flags");
    public static final PdfName FLASH = new PdfName("Flash");
    public static final PdfName FLASHVARS = new PdfName("FlashVars");
    public static final PdfName FLATEDECODE = new PdfName("FlateDecode");
    public static final PdfName FO = new PdfName("Fo");
    public static final PdfName FONT = new PdfName("Font");
    public static final PdfName FONTBBOX = new PdfName(AFMParser.FONT_BBOX);
    public static final PdfName FONTDESCRIPTOR = new PdfName("FontDescriptor");
    public static final PdfName FONTFILE = new PdfName("FontFile");
    public static final PdfName FONTFILE2 = new PdfName("FontFile2");
    public static final PdfName FONTFILE3 = new PdfName("FontFile3");
    public static final PdfName FONTMATRIX = new PdfName("FontMatrix");
    public static final PdfName FONTNAME = new PdfName(AFMParser.FONT_NAME);
    public static final PdfName FOREGROUND = new PdfName("Foreground");
    public static final PdfName FORM = new PdfName(StandardStructureTypes.FORM);
    public static final PdfName FORMTYPE = new PdfName("FormType");
    public static final PdfName FORMULA = new PdfName(StandardStructureTypes.FORMULA);
    public static final PdfName FREETEXT = new PdfName("FreeText");
    public static final PdfName FRM = new PdfName("FRM");
    public static final PdfName FS = new PdfName("FS");
    public static final PdfName FT = new PdfName("FT");
    public static final PdfName FULLSCREEN = new PdfName("FullScreen");
    public static final PdfName FUNCTION = new PdfName("Function");
    public static final PdfName FUNCTIONS = new PdfName("Functions");
    public static final PdfName FUNCTIONTYPE = new PdfName("FunctionType");
    public static final PdfName GAMMA = new PdfName("Gamma");
    public static final PdfName GBK = new PdfName("GBK");
    public static final PdfName GLITTER = new PdfName("Glitter");
    public static final PdfName GOTO = new PdfName(PDActionGoTo.SUB_TYPE);
    public static final PdfName GOTOE = new PdfName(PDActionEmbeddedGoTo.SUB_TYPE);
    public static final PdfName GOTOR = new PdfName(PDActionRemoteGoTo.SUB_TYPE);
    public static final PdfName GROUP = new PdfName(PDAnnotationMarkup.RT_GROUP);
    public static final PdfName GTS_PDFA1 = new PdfName("GTS_PDFA1");
    public static final PdfName GTS_PDFX = new PdfName("GTS_PDFX");
    public static final PdfName GTS_PDFXVERSION = new PdfName("GTS_PDFXVersion");
    public static final PdfName H = new PdfName(StandardStructureTypes.H);
    public static final PdfName H1 = new PdfName(StandardStructureTypes.H1);
    public static final PdfName H2 = new PdfName(StandardStructureTypes.H2);
    public static final PdfName H3 = new PdfName(StandardStructureTypes.H3);
    public static final PdfName H4 = new PdfName(StandardStructureTypes.H4);
    public static final PdfName H5 = new PdfName(StandardStructureTypes.H5);
    public static final PdfName H6 = new PdfName(StandardStructureTypes.H6);
    public static final PdfName HALIGN = new PdfName("HAlign");
    public static final PdfName HEIGHT = new PdfName("Height");
    public static final PdfName HELV = new PdfName("Helv");
    public static final PdfName HELVETICA = new PdfName("Helvetica");
    public static final PdfName HELVETICA_BOLD = new PdfName("Helvetica-Bold");
    public static final PdfName HELVETICA_OBLIQUE = new PdfName("Helvetica-Oblique");
    public static final PdfName HELVETICA_BOLDOBLIQUE = new PdfName("Helvetica-BoldOblique");
    public static final PdfName HID = new PdfName("Hid");
    public static final PdfName HIDE = new PdfName(PDActionHide.SUB_TYPE);
    public static final PdfName HIDEMENUBAR = new PdfName("HideMenubar");
    public static final PdfName HIDETOOLBAR = new PdfName("HideToolbar");
    public static final PdfName HIDEWINDOWUI = new PdfName("HideWindowUI");
    public static final PdfName HIGHLIGHT = new PdfName("Highlight");
    public static final PdfName HOFFSET = new PdfName("HOffset");
    public static final PdfName I = new PdfName("I");
    public static final PdfName ICCBASED = new PdfName("ICCBased");
    public static final PdfName ID = new PdfName("ID");
    public static final PdfName IDENTITY = new PdfName("Identity");
    public static final PdfName IF = new PdfName("IF");
    public static final PdfName IMAGE = new PdfName("Image");
    public static final PdfName IMAGEB = new PdfName("ImageB");
    public static final PdfName IMAGEC = new PdfName("ImageC");
    public static final PdfName IMAGEI = new PdfName("ImageI");
    public static final PdfName IMAGEMASK = new PdfName("ImageMask");
    public static final PdfName INDEX = new PdfName(StandardStructureTypes.INDEX);
    public static final PdfName INDEXED = new PdfName("Indexed");
    public static final PdfName INFO = new PdfName(EncryptionConstants._TAG_INFO);
    public static final PdfName INK = new PdfName("Ink");
    public static final PdfName INKLIST = new PdfName("InkList");
    public static final PdfName INSTANCES = new PdfName("Instances");
    public static final PdfName IMPORTDATA = new PdfName(PDActionImportData.SUB_TYPE);
    public static final PdfName INTENT = new PdfName("Intent");
    public static final PdfName INTERPOLATE = new PdfName("Interpolate");
    public static final PdfName ISMAP = new PdfName("IsMap");
    public static final PdfName IRT = new PdfName("IRT");
    public static final PdfName ITALICANGLE = new PdfName(AFMParser.ITALIC_ANGLE);
    public static final PdfName ITXT = new PdfName("ITXT");
    public static final PdfName IX = new PdfName("IX");
    public static final PdfName JAVASCRIPT = new PdfName(PDActionJavaScript.SUB_TYPE);
    public static final PdfName JBIG2DECODE = new PdfName("JBIG2Decode");
    public static final PdfName JBIG2GLOBALS = new PdfName("JBIG2Globals");
    public static final PdfName JPXDECODE = new PdfName("JPXDecode");
    public static final PdfName JS = new PdfName("JS");
    public static final PdfName K = new PdfName(OperatorName.STROKING_COLOR_CMYK);
    public static final PdfName KEYWORDS = new PdfName("Keywords");
    public static final PdfName KIDS = new PdfName("Kids");
    public static final PdfName L = new PdfName("L");
    public static final PdfName L2R = new PdfName(PDViewerPreferences.READING_DIRECTION_L2R);
    public static final PdfName LANG = new PdfName("Lang");
    public static final PdfName LANGUAGE = new PdfName("Language");
    public static final PdfName LAST = new PdfName("Last");
    public static final PdfName LASTCHAR = new PdfName("LastChar");
    public static final PdfName LASTPAGE = new PdfName("LastPage");
    public static final PdfName LAUNCH = new PdfName(PDActionLaunch.SUB_TYPE);
    public static final PdfName LBL = new PdfName(StandardStructureTypes.LBL);
    public static final PdfName LBODY = new PdfName(StandardStructureTypes.L_BODY);
    public static final PdfName LENGTH = new PdfName("Length");
    public static final PdfName LENGTH1 = new PdfName("Length1");
    public static final PdfName LI = new PdfName(StandardStructureTypes.LI);
    public static final PdfName LIMITS = new PdfName("Limits");
    public static final PdfName LINE = new PdfName("Line");
    public static final PdfName LINEAR = new PdfName("Linear");
    public static final PdfName LINK = new PdfName("Link");
    public static final PdfName LISTMODE = new PdfName("ListMode");
    public static final PdfName LOCATION = new PdfName("Location");
    public static final PdfName LOCK = new PdfName("Lock");
    public static final PdfName LOCKED = new PdfName("Locked");
    public static final PdfName LZWDECODE = new PdfName("LZWDecode");
    public static final PdfName M = new PdfName(OperatorName.SET_LINE_MITERLIMIT);
    public static final PdfName MATERIAL = new PdfName("Material");
    public static final PdfName MATRIX = new PdfName("Matrix");
    public static final PdfName MAC_EXPERT_ENCODING = new PdfName("MacExpertEncoding");
    public static final PdfName MAC_ROMAN_ENCODING = new PdfName("MacRomanEncoding");
    public static final PdfName MARKED = new PdfName("Marked");
    public static final PdfName MARKINFO = new PdfName("MarkInfo");
    public static final PdfName MASK = new PdfName("Mask");
    public static final PdfName MAX_LOWER_CASE = new PdfName("max");
    public static final PdfName MAX_CAMEL_CASE = new PdfName("Max");
    public static final PdfName MAXLEN = new PdfName("MaxLen");
    public static final PdfName MEDIABOX = new PdfName(PDViewerPreferences.BOUNDARY_MEDIA_BOX);
    public static final PdfName MCID = new PdfName("MCID");
    public static final PdfName MCR = new PdfName(PDMarkedContentReference.TYPE);
    public static final PdfName METADATA = new PdfName("Metadata");
    public static final PdfName MIN_LOWER_CASE = new PdfName("min");
    public static final PdfName MIN_CAMEL_CASE = new PdfName("Min");
    public static final PdfName MK = new PdfName("MK");
    public static final PdfName MMTYPE1 = new PdfName("MMType1");
    public static final PdfName MODDATE = new PdfName("ModDate");
    public static final PdfName N = new PdfName("N");
    public static final PdfName N0 = new PdfName("n0");
    public static final PdfName N1 = new PdfName("n1");
    public static final PdfName N2 = new PdfName("n2");
    public static final PdfName N3 = new PdfName("n3");
    public static final PdfName N4 = new PdfName("n4");
    public static final PdfName NAME = new PdfName("Name");
    public static final PdfName NAMED = new PdfName(PDActionNamed.SUB_TYPE);
    public static final PdfName NAMES = new PdfName("Names");
    public static final PdfName NAVIGATION = new PdfName("Navigation");
    public static final PdfName NAVIGATIONPANE = new PdfName("NavigationPane");
    public static final PdfName NEAR = new PdfName("Near");
    public static final PdfName NEEDAPPEARANCES = new PdfName("NeedAppearances");
    public static final PdfName NEWWINDOW = new PdfName("NewWindow");
    public static final PdfName NEXT = new PdfName("Next");
    public static final PdfName NEXTPAGE = new PdfName("NextPage");
    public static final PdfName NM = new PdfName("NM");
    public static final PdfName NONE = new PdfName("None");
    public static final PdfName NONFULLSCREENPAGEMODE = new PdfName("NonFullScreenPageMode");
    public static final PdfName NONSTRUCT = new PdfName(StandardStructureTypes.NON_STRUCT);
    public static final PdfName NOTE = new PdfName("Note");
    public static final PdfName NUMCOPIES = new PdfName("NumCopies");
    public static final PdfName NUMS = new PdfName("Nums");
    public static final PdfName O = new PdfName(PDAnnotationLink.HIGHLIGHT_MODE_OUTLINE);
    public static final PdfName OBJ = new PdfName("Obj");
    public static final PdfName OBJR = new PdfName(PDObjectReference.TYPE);
    public static final PdfName OBJSTM = new PdfName("ObjStm");
    public static final PdfName OC = new PdfName("OC");
    public static final PdfName OCG = new PdfName("OCG");
    public static final PdfName OCGS = new PdfName("OCGs");
    public static final PdfName OCMD = new PdfName("OCMD");
    public static final PdfName OCPROPERTIES = new PdfName("OCProperties");
    public static final PdfName Off = new PdfName("Off");
    public static final PdfName OFF = new PdfName("OFF");
    public static final PdfName ON = new PdfName("ON");
    public static final PdfName ONECOLUMN = new PdfName("OneColumn");
    public static final PdfName OPEN = new PdfName("Open");
    public static final PdfName OPENACTION = new PdfName("OpenAction");
    public static final PdfName OP = new PdfName("OP");
    public static final PdfName op = new PdfName(ProtocolConstants.OPERATION_PARAM);
    public static final PdfName OPM = new PdfName("OPM");
    public static final PdfName OPT = new PdfName("Opt");
    public static final PdfName ORDER = new PdfName("Order");
    public static final PdfName ORDERING = new PdfName("Ordering");
    public static final PdfName OSCILLATING = new PdfName("Oscillating");
    public static final PdfName OUTLINES = new PdfName("Outlines");
    public static final PdfName OUTPUTCONDITION = new PdfName("OutputCondition");
    public static final PdfName OUTPUTCONDITIONIDENTIFIER = new PdfName("OutputConditionIdentifier");
    public static final PdfName OUTPUTINTENT = new PdfName("OutputIntent");
    public static final PdfName OUTPUTINTENTS = new PdfName("OutputIntents");
    public static final PdfName P = new PdfName("P");
    public static final PdfName PAGE = new PdfName("Page");
    public static final PdfName PAGELABELS = new PdfName("PageLabels");
    public static final PdfName PAGELAYOUT = new PdfName("PageLayout");
    public static final PdfName PAGEMODE = new PdfName("PageMode");
    public static final PdfName PAGES = new PdfName("Pages");
    public static final PdfName PAINTTYPE = new PdfName("PaintType");
    public static final PdfName PANOSE = new PdfName("Panose");
    public static final PdfName PARAMS = new PdfName("Params");
    public static final PdfName PARENT = new PdfName("Parent");
    public static final PdfName PARENTTREE = new PdfName("ParentTree");
    public static final PdfName PARENTTREENEXTKEY = new PdfName("ParentTreeNextKey");
    public static final PdfName PART = new PdfName(StandardStructureTypes.PART);
    public static final PdfName PASSCONTEXTCLICK = new PdfName("PassContextClick");
    public static final PdfName PATTERN = new PdfName("Pattern");
    public static final PdfName PATTERNTYPE = new PdfName("PatternType");
    public static final PdfName PC = new PdfName("PC");
    public static final PdfName PDF = new PdfName(ISignatureFormatDetector.FORMAT_PDF);
    public static final PdfName PDFDOCENCODING = new PdfName("PDFDocEncoding");
    public static final PdfName PERCEPTUAL = new PdfName("Perceptual");
    public static final PdfName PERMS = new PdfName("Perms");
    public static final PdfName PG = new PdfName("Pg");
    public static final PdfName PI = new PdfName("PI");
    public static final PdfName PICKTRAYBYPDFSIZE = new PdfName("PickTrayByPDFSize");
    public static final PdfName PLAYCOUNT = new PdfName("PlayCount");
    public static final PdfName PO = new PdfName("PO");
    public static final PdfName POPUP = new PdfName(PDAnnotationPopup.SUB_TYPE);
    public static final PdfName POSITION = new PdfName("Position");
    public static final PdfName PREDICTOR = new PdfName("Predictor");
    public static final PdfName PREFERRED = new PdfName("Preferred");
    public static final PdfName PRESENTATION = new PdfName("Presentation");
    public static final PdfName PRESERVERB = new PdfName("PreserveRB");
    public static final PdfName PREV = new PdfName("Prev");
    public static final PdfName PREVPAGE = new PdfName("PrevPage");
    public static final PdfName PRINT = new PdfName("Print");
    public static final PdfName PRINTAREA = new PdfName("PrintArea");
    public static final PdfName PRINTCLIP = new PdfName("PrintClip");
    public static final PdfName PRINTPAGERANGE = new PdfName("PrintPageRange");
    public static final PdfName PRINTSCALING = new PdfName("PrintScaling");
    public static final PdfName PRINTSTATE = new PdfName("PrintState");
    public static final PdfName PRIVATE = new PdfName(StandardStructureTypes.PRIVATE);
    public static final PdfName PROCSET = new PdfName("ProcSet");
    public static final PdfName PRODUCER = new PdfName("Producer");
    public static final PdfName PROPERTIES = new PdfName("Properties");
    public static final PdfName PS = new PdfName("PS");
    public static final PdfName PUBSEC = new PdfName(PublicKeySecurityHandler.FILTER);
    public static final PdfName PV = new PdfName("PV");
    public static final PdfName Q = new PdfName("Q");
    public static final PdfName QUADPOINTS = new PdfName("QuadPoints");
    public static final PdfName QUOTE = new PdfName(StandardStructureTypes.QUOTE);
    public static final PdfName R = new PdfName("R");
    public static final PdfName R2L = new PdfName(PDViewerPreferences.READING_DIRECTION_R2L);
    public static final PdfName RANGE = new PdfName("Range");
    public static final PdfName RC = new PdfName("RC");
    public static final PdfName RBGROUPS = new PdfName("RBGroups");
    public static final PdfName REASON = new PdfName("Reason");
    public static final PdfName RECIPIENTS = new PdfName("Recipients");
    public static final PdfName RECT = new PdfName("Rect");
    public static final PdfName REFERENCE = new PdfName("Reference");
    public static final PdfName REGISTRY = new PdfName("Registry");
    public static final PdfName REGISTRYNAME = new PdfName("RegistryName");
    public static final PdfName RELATIVECOLORIMETRIC = new PdfName("RelativeColorimetric");
    public static final PdfName RENDITION = new PdfName("Rendition");
    public static final PdfName RESETFORM = new PdfName(PDActionResetForm.SUB_TYPE);
    public static final PdfName RESOURCES = new PdfName("Resources");
    public static final PdfName RI = new PdfName("RI");
    public static final PdfName RICHMEDIA = new PdfName("RichMedia");
    public static final PdfName RICHMEDIAACTIVATION = new PdfName("RichMediaActivation");
    public static final PdfName RICHMEDIAANIMATION = new PdfName("RichMediaAnimation");
    public static final PdfName RICHMEDIACOMMAND = new PdfName("RichMediaCommand");
    public static final PdfName RICHMEDIACONFIGURATION = new PdfName("RichMediaConfiguration");
    public static final PdfName RICHMEDIACONTENT = new PdfName("RichMediaContent");
    public static final PdfName RICHMEDIADEACTIVATION = new PdfName("RichMediaDeactivation");
    public static final PdfName RICHMEDIAEXECUTE = new PdfName("RichMediaExecute");
    public static final PdfName RICHMEDIAINSTANCE = new PdfName("RichMediaInstance");
    public static final PdfName RICHMEDIAPARAMS = new PdfName("RichMediaParams");
    public static final PdfName RICHMEDIAPOSITION = new PdfName("RichMediaPosition");
    public static final PdfName RICHMEDIAPRESENTATION = new PdfName("RichMediaPresentation");
    public static final PdfName RICHMEDIASETTINGS = new PdfName("RichMediaSettings");
    public static final PdfName RICHMEDIAWINDOW = new PdfName("RichMediaWindow");
    public static final PdfName ROLEMAP = new PdfName("RoleMap");
    public static final PdfName ROOT = new PdfName("Root");
    public static final PdfName ROTATE = new PdfName("Rotate");
    public static final PdfName ROWS = new PdfName("Rows");
    public static final PdfName RUBY = new PdfName(StandardStructureTypes.RUBY);
    public static final PdfName RUNLENGTHDECODE = new PdfName("RunLengthDecode");
    public static final PdfName RV = new PdfName("RV");
    public static final PdfName S = new PdfName("S");
    public static final PdfName SATURATION = new PdfName("Saturation");
    public static final PdfName SCHEMA = new PdfName("Schema");
    public static final PdfName SCREEN = new PdfName("Screen");
    public static final PdfName SCRIPTS = new PdfName("Scripts");
    public static final PdfName SECT = new PdfName(StandardStructureTypes.SECT);
    public static final PdfName SEPARATION = new PdfName("Separation");
    public static final PdfName SETOCGSTATE = new PdfName("SetOCGState");
    public static final PdfName SETTINGS = new PdfName("Settings");
    public static final PdfName SHADING = new PdfName("Shading");
    public static final PdfName SHADINGTYPE = new PdfName("ShadingType");
    public static final PdfName SHIFT_JIS = new PdfName("Shift-JIS");
    public static final PdfName SIG = new PdfName("Sig");
    public static final PdfName SIGFLAGS = new PdfName("SigFlags");
    public static final PdfName SIGREF = new PdfName("SigRef");
    public static final PdfName SIMPLEX = new PdfName("Simplex");
    public static final PdfName SINGLEPAGE = new PdfName("SinglePage");
    public static final PdfName SIZE = new PdfName("Size");
    public static final PdfName SMASK = new PdfName("SMask");
    public static final PdfName SORT = new PdfName("Sort");
    public static final PdfName SOUND = new PdfName("Sound");
    public static final PdfName SPAN = new PdfName(StandardStructureTypes.SPAN);
    public static final PdfName SPEED = new PdfName("Speed");
    public static final PdfName SPLIT = new PdfName("Split");
    public static final PdfName SQUARE = new PdfName("Square");
    public static final PdfName SQUIGGLY = new PdfName("Squiggly");
    public static final PdfName ST = new PdfName("St");
    public static final PdfName STAMP = new PdfName("Stamp");
    public static final PdfName STANDARD = new PdfName("Standard");
    public static final PdfName STATE = new PdfName("State");
    public static final PdfName STDCF = new PdfName("StdCF");
    public static final PdfName STEMV = new PdfName("StemV");
    public static final PdfName STMF = new PdfName("StmF");
    public static final PdfName STRF = new PdfName("StrF");
    public static final PdfName STRIKEOUT = new PdfName("StrikeOut");
    public static final PdfName STRUCTPARENT = new PdfName("StructParent");
    public static final PdfName STRUCTPARENTS = new PdfName("StructParents");
    public static final PdfName STRUCTTREEROOT = new PdfName("StructTreeRoot");
    public static final PdfName STYLE = new PdfName("Style");
    public static final PdfName SUBFILTER = new PdfName("SubFilter");
    public static final PdfName SUBJECT = new PdfName("Subject");
    public static final PdfName SUBMITFORM = new PdfName(PDActionSubmitForm.SUB_TYPE);
    public static final PdfName SUBTYPE = new PdfName("Subtype");
    public static final PdfName SUPPLEMENT = new PdfName("Supplement");
    public static final PdfName SV = new PdfName("SV");
    public static final PdfName SW = new PdfName("SW");
    public static final PdfName SYMBOL = new PdfName("Symbol");
    public static final PdfName T = new PdfName(PDNumberFormatDictionary.FRACTIONAL_DISPLAY_TRUNCATE);
    public static final PdfName TA = new PdfName("TA");
    public static final PdfName TABLE = new PdfName("Table");
    public static final PdfName TABS = new PdfName("Tabs");
    public static final PdfName TBODY = new PdfName(StandardStructureTypes.T_BODY);
    public static final PdfName TD = new PdfName("TD");
    public static final PdfName TEXT = new PdfName("Text");
    public static final PdfName TFOOT = new PdfName(StandardStructureTypes.T_FOOT);
    public static final PdfName TH = new PdfName(StandardStructureTypes.TH);
    public static final PdfName THEAD = new PdfName(StandardStructureTypes.T_HEAD);
    public static final PdfName THUMB = new PdfName("Thumb");
    public static final PdfName THREADS = new PdfName("Threads");
    public static final PdfName TI = new PdfName("TI");
    public static final PdfName TIME = new PdfName("Time");
    public static final PdfName TILINGTYPE = new PdfName("TilingType");
    public static final PdfName TIMES_ROMAN = new PdfName("Times-Roman");
    public static final PdfName TIMES_BOLD = new PdfName("Times-Bold");
    public static final PdfName TIMES_ITALIC = new PdfName("Times-Italic");
    public static final PdfName TIMES_BOLDITALIC = new PdfName("Times-BoldItalic");
    public static final PdfName TITLE = new PdfName("Title");
    public static final PdfName TK = new PdfName("TK");
    public static final PdfName TM = new PdfName("TM");
    public static final PdfName TOC = new PdfName(StandardStructureTypes.TOC);
    public static final PdfName TOCI = new PdfName(StandardStructureTypes.TOCI);
    public static final PdfName TOGGLE = new PdfName("Toggle");
    public static final PdfName TOOLBAR = new PdfName("Toolbar");
    public static final PdfName TOUNICODE = new PdfName("ToUnicode");
    public static final PdfName TP = new PdfName("TP");
    public static final PdfName TABLEROW = new PdfName(StandardStructureTypes.TR);
    public static final PdfName TRANS = new PdfName("Trans");
    public static final PdfName TRANSFORMPARAMS = new PdfName("TransformParams");
    public static final PdfName TRANSFORMMETHOD = new PdfName("TransformMethod");
    public static final PdfName TRANSPARENCY = new PdfName("Transparency");
    public static final PdfName TRANSPARENT = new PdfName("Transparent");
    public static final PdfName TRAPPED = new PdfName("Trapped");
    public static final PdfName TRIMBOX = new PdfName(PDViewerPreferences.BOUNDARY_TRIM_BOX);
    public static final PdfName TRUETYPE = new PdfName("TrueType");
    public static final PdfName TU = new PdfName("TU");
    public static final PdfName TWOCOLUMNLEFT = new PdfName("TwoColumnLeft");
    public static final PdfName TWOCOLUMNRIGHT = new PdfName("TwoColumnRight");
    public static final PdfName TWOPAGELEFT = new PdfName("TwoPageLeft");
    public static final PdfName TWOPAGERIGHT = new PdfName("TwoPageRight");
    public static final PdfName TX = new PdfName("Tx");
    public static final PdfName TYPE = new PdfName("Type");
    public static final PdfName TYPE0 = new PdfName("Type0");
    public static final PdfName TYPE1 = new PdfName("Type1");
    public static final PdfName TYPE3 = new PdfName("Type3");
    public static final PdfName U = new PdfName(PDBorderStyleDictionary.STYLE_UNDERLINE);
    public static final PdfName UE = new PdfName("UE");
    public static final PdfName OE = new PdfName("OE");
    public static final PdfName UF = new PdfName("UF");
    public static final PdfName UHC = new PdfName("UHC");
    public static final PdfName UNDERLINE = new PdfName("Underline");
    public static final PdfName UR = new PdfName("UR");
    public static final PdfName UR3 = new PdfName("UR3");
    public static final PdfName URI = new PdfName("URI");
    public static final PdfName URL = new PdfName("URL");
    public static final PdfName USAGE = new PdfName("Usage");
    public static final PdfName USEATTACHMENTS = new PdfName("UseAttachments");
    public static final PdfName USENONE = new PdfName(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE_USE_NONE);
    public static final PdfName USEOC = new PdfName(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE_USE_OPTIONAL_CONTENT);
    public static final PdfName USEOUTLINES = new PdfName(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE_USE_OUTLINES);
    public static final PdfName USER = new PdfName("User");
    public static final PdfName USERPROPERTIES = new PdfName(PDUserAttributeObject.OWNER_USER_PROPERTIES);
    public static final PdfName USERUNIT = new PdfName("UserUnit");
    public static final PdfName USETHUMBS = new PdfName(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE_USE_THUMBS);
    public static final PdfName V = new PdfName("V");
    public static final PdfName V2 = new PdfName("V2");
    public static final PdfName VALIGN = new PdfName("VAlign");
    public static final PdfName VERISIGN_PPKVS = new PdfName("VeriSign.PPKVS");
    public static final PdfName VERSION = new PdfName(AFMParser.VERSION);
    public static final PdfName VIDEO = new PdfName("Video");
    public static final PdfName VIEW = new PdfName("View");
    public static final PdfName VIEWS = new PdfName("Views");
    public static final PdfName VIEWAREA = new PdfName("ViewArea");
    public static final PdfName VIEWCLIP = new PdfName("ViewClip");
    public static final PdfName VIEWERPREFERENCES = new PdfName("ViewerPreferences");
    public static final PdfName VIEWSTATE = new PdfName("ViewState");
    public static final PdfName VISIBLEPAGES = new PdfName("VisiblePages");
    public static final PdfName VOFFSET = new PdfName("VOffset");
    public static final PdfName W = new PdfName("W");
    public static final PdfName W2 = new PdfName("W2");
    public static final PdfName WARICHU = new PdfName("Warichu");
    public static final PdfName WC = new PdfName("WC");
    public static final PdfName WIDGET = new PdfName(PDAnnotationWidget.SUB_TYPE);
    public static final PdfName WIDTH = new PdfName("Width");
    public static final PdfName WIDTHS = new PdfName("Widths");
    public static final PdfName WIN = new PdfName("Win");
    public static final PdfName WIN_ANSI_ENCODING = new PdfName("WinAnsiEncoding");
    public static final PdfName WINDOW = new PdfName("Window");
    public static final PdfName WINDOWED = new PdfName("Windowed");
    public static final PdfName WIPE = new PdfName("Wipe");
    public static final PdfName WHITEPOINT = new PdfName("WhitePoint");
    public static final PdfName WP = new PdfName(StandardStructureTypes.WP);
    public static final PdfName WS = new PdfName("WS");
    public static final PdfName X = new PdfName("X");
    public static final PdfName XA = new PdfName("XA");
    public static final PdfName XD = new PdfName("XD");
    public static final PdfName XFA = new PdfName("XFA");
    public static final PdfName XML = new PdfName("XML");
    public static final PdfName XOBJECT = new PdfName("XObject");
    public static final PdfName XSTEP = new PdfName("XStep");
    public static final PdfName XREF = new PdfName("XRef");
    public static final PdfName XREFSTM = new PdfName("XRefStm");
    public static final PdfName XYZ = new PdfName("XYZ");
    public static final PdfName YSTEP = new PdfName("YStep");
    public static final PdfName ZADB = new PdfName("ZaDb");
    public static final PdfName ZAPFDINGBATS = new PdfName("ZapfDingbats");
    public static final PdfName ZOOM = new PdfName("Zoom");
    public static Map staticNames;
    private int hash;

    static {
        Field[] fields = PdfName.class.getDeclaredFields();
        staticNames = new HashMap(fields.length);
        try {
            for (Field curFld : fields) {
                if ((curFld.getModifiers() & 25) == 25 && curFld.getType().equals(PdfName.class)) {
                    PdfName name = (PdfName) curFld.get(null);
                    staticNames.put(decodeName(name.toString()), name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PdfName(String name) {
        this(name, true);
    }

    public PdfName(String name, boolean lengthCheck) {
        super(4);
        this.hash = 0;
        int length = name.length();
        if (lengthCheck && length > 127) {
            throw new IllegalArgumentException("The name '" + name + "' is too long (" + length + " characters).");
        }
        this.bytes = encodeName(name);
    }

    public PdfName(byte[] bytes) {
        super(4, bytes);
        this.hash = 0;
    }

    @Override // java.lang.Comparable
    public int compareTo(Object object) {
        PdfName name = (PdfName) object;
        byte[] myBytes = this.bytes;
        byte[] objBytes = name.bytes;
        int len = Math.min(myBytes.length, objBytes.length);
        for (int i = 0; i < len; i++) {
            if (myBytes[i] > objBytes[i]) {
                return 1;
            }
            if (myBytes[i] < objBytes[i]) {
                return -1;
            }
        }
        if (myBytes.length < objBytes.length) {
            return -1;
        }
        if (myBytes.length > objBytes.length) {
            return 1;
        }
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof PdfName) && compareTo(obj) == 0;
    }

    public int hashCode() {
        int h = this.hash;
        if (h == 0) {
            int ptr = 0;
            int len = this.bytes.length;
            for (int i = 0; i < len; i++) {
                int i2 = ptr;
                ptr++;
                h = (31 * h) + (this.bytes[i2] & 255);
            }
            this.hash = h;
        }
        return h;
    }

    public static byte[] encodeName(String name) {
        int length = name.length();
        ByteBuffer buf = new ByteBuffer(length + 20);
        buf.append('/');
        char[] chars = name.toCharArray();
        for (int k = 0; k < length; k++) {
            char c = (char) (chars[k] & 255);
            switch (c) {
                case ' ':
                case '#':
                case '%':
                case '(':
                case ')':
                case '/':
                case '<':
                case '>':
                case '[':
                case ']':
                case '{':
                case '}':
                    buf.append('#');
                    buf.append(Integer.toString(c, 16));
                    break;
                default:
                    if (c >= ' ' && c <= '~') {
                        buf.append(c);
                    } else {
                        buf.append('#');
                        if (c < 16) {
                            buf.append('0');
                        }
                        buf.append(Integer.toString(c, 16));
                    }
                    break;
            }
        }
        return buf.toByteArray();
    }

    public static String decodeName(String name) {
        StringBuffer buf = new StringBuffer();
        try {
            int len = name.length();
            int k = 1;
            while (k < len) {
                char c = name.charAt(k);
                if (c == '#') {
                    char c1 = name.charAt(k + 1);
                    char c2 = name.charAt(k + 2);
                    c = (char) ((PRTokeniser.getHex(c1) << 4) + PRTokeniser.getHex(c2));
                    k += 2;
                }
                buf.append(c);
                k++;
            }
        } catch (IndexOutOfBoundsException e) {
        }
        return buf.toString();
    }
}
