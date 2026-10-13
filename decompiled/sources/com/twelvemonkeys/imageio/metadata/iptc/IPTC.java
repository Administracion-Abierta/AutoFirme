package com.twelvemonkeys.imageio.metadata.iptc;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/iptc/IPTC.class */
public interface IPTC {
    public static final int ENVELOPE_RECORD = 256;
    public static final int APPLICATION_RECORD = 512;
    public static final int TAG_DESTINATION = 261;
    public static final int TAG_PRODUCT_ID = 306;
    public static final int TAG_CODED_CHARACTER_SET = 346;
    public static final int TAG_RECORD_VERSION = 512;
    public static final int TAG_OBJECT_TYPE_REFERENCE = 515;
    public static final int TAG_OBJECT_ATTRIBUTE_REFERENCE = 516;
    public static final int TAG_OBJECT_NAME = 517;
    public static final int TAG_EDIT_STATUS = 519;
    public static final int TAG_EDITORIAL_UPDATE = 520;
    public static final int TAG_URGENCY = 522;
    public static final int TAG_SUBJECT_REFERENCE = 524;
    public static final int TAG_CATEGORY = 527;
    public static final int TAG_SUPPLEMENTAL_CATEGORIES = 532;
    public static final int TAG_FIXTURE_IDENTIFIER = 534;
    public static final int TAG_KEYWORDS = 537;
    public static final int TAG_CONTENT_LOCATION_CODE = 538;
    public static final int TAG_CONTENT_LOCATION_NAME = 539;
    public static final int TAG_RELEASE_DATE = 542;
    public static final int TAG_RELEASE_TIME = 547;
    public static final int TAG_EXPIRATION_DATE = 549;
    public static final int TAG_EXPIRATION_TIME = 550;
    public static final int TAG_SPECIAL_INSTRUCTIONS = 552;
    public static final int TAG_ACTION_ADVICED = 554;
    public static final int TAG_REFERENCE_SERVICE = 557;
    public static final int TAG_REFERENCE_DATE = 559;
    public static final int TAG_REFERENCE_NUMBER = 562;
    public static final int TAG_DATE_CREATED = 567;
    public static final int TAG_TIME_CREATED = 572;
    public static final int TAG_DIGITAL_CREATION_DATE = 574;
    public static final int TAG_DIGITAL_CREATION_TIME = 575;
    public static final int TAG_ORIGINATING_PROGRAM = 577;
    public static final int TAG_PROGRAM_VERSION = 582;
    public static final int TAG_OBJECT_CYCLE = 587;
    public static final int TAG_BY_LINE = 592;
    public static final int TAG_BY_LINE_TITLE = 597;
    public static final int TAG_CITY = 602;
    public static final int TAG_SUB_LOCATION = 604;
    public static final int TAG_PROVINCE_OR_STATE = 607;
    public static final int TAG_COUNTRY_OR_PRIMARY_LOCATION_CODE = 612;
    public static final int TAG_COUNTRY_OR_PRIMARY_LOCATION = 613;
    public static final int TAG_ORIGINAL_TRANSMISSION_REFERENCE = 615;
    public static final int TAG_HEADLINE = 617;
    public static final int TAG_CREDIT = 622;
    public static final int TAG_SOURCE = 627;
    public static final int TAG_COPYRIGHT_NOTICE = 628;
    public static final int TAG_CONTACT = 630;
    public static final int TAG_CAPTION = 632;
    public static final int TAG_WRITER = 634;
    public static final int TAG_RASTERIZED_CATPTION = 637;
    public static final int TAG_IMAGE_TYPE = 642;
    public static final int TAG_IMAGE_ORIENTATION = 643;
    public static final int TAG_LANGUAGE_IDENTIFIER = 647;
    public static final int CUSTOM_TAG_JOBMINDER_ASSIGNMENT_DATA = 711;

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/iptc/IPTC$Tags.class */
    public static final class Tags {
        static boolean isArray(short s) {
            switch (s) {
                case 261:
                case 306:
                case IPTC.TAG_SUBJECT_REFERENCE /* 524 */:
                case 532:
                case 537:
                case 538:
                case 539:
                case 592:
                    return true;
                default:
                    return false;
            }
        }
    }
}
