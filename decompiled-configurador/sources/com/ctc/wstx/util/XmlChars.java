package com.ctc.wstx.util;

import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/util/XmlChars.class */
public final class XmlChars {
    static final int SIZE = 394;
    static final int[] sXml10StartChars = new int[SIZE];
    static final int[] sXml10Chars;

    static {
        SETBITS(sXml10StartChars, 192, 214);
        SETBITS(sXml10StartChars, 216, 246);
        SETBITS(sXml10StartChars, 248, 255);
        SETBITS(sXml10StartChars, 256, 305);
        SETBITS(sXml10StartChars, 308, 318);
        SETBITS(sXml10StartChars, 321, 328);
        SETBITS(sXml10StartChars, 330, 382);
        SETBITS(sXml10StartChars, 384, 451);
        SETBITS(sXml10StartChars, 461, 496);
        SETBITS(sXml10StartChars, 500, 501);
        SETBITS(sXml10StartChars, 506, 535);
        SETBITS(sXml10StartChars, 592, 680);
        SETBITS(sXml10StartChars, 699, 705);
        SETBITS(sXml10StartChars, 902);
        SETBITS(sXml10StartChars, 904, 906);
        SETBITS(sXml10StartChars, 908);
        SETBITS(sXml10StartChars, 910, 929);
        SETBITS(sXml10StartChars, 931, 974);
        SETBITS(sXml10StartChars, 976, 982);
        SETBITS(sXml10StartChars, 986);
        SETBITS(sXml10StartChars, 988);
        SETBITS(sXml10StartChars, 990);
        SETBITS(sXml10StartChars, 992);
        SETBITS(sXml10StartChars, 994, 1011);
        SETBITS(sXml10StartChars, 1025, 1036);
        SETBITS(sXml10StartChars, 1038, 1103);
        SETBITS(sXml10StartChars, 1105, 1116);
        SETBITS(sXml10StartChars, 1118, 1153);
        SETBITS(sXml10StartChars, 1168, 1220);
        SETBITS(sXml10StartChars, 1223, 1224);
        SETBITS(sXml10StartChars, 1227, 1228);
        SETBITS(sXml10StartChars, 1232, 1259);
        SETBITS(sXml10StartChars, 1262, 1269);
        SETBITS(sXml10StartChars, 1272, 1273);
        SETBITS(sXml10StartChars, 1329, 1366);
        SETBITS(sXml10StartChars, 1369);
        SETBITS(sXml10StartChars, 1377, 1414);
        SETBITS(sXml10StartChars, 1488, 1514);
        SETBITS(sXml10StartChars, 1520, 1522);
        SETBITS(sXml10StartChars, 1569, 1594);
        SETBITS(sXml10StartChars, 1601, 1610);
        SETBITS(sXml10StartChars, 1649, 1719);
        SETBITS(sXml10StartChars, 1722, 1726);
        SETBITS(sXml10StartChars, 1728, 1742);
        SETBITS(sXml10StartChars, 1744, 1747);
        SETBITS(sXml10StartChars, 1749);
        SETBITS(sXml10StartChars, 1765, 1766);
        SETBITS(sXml10StartChars, 2309, 2361);
        SETBITS(sXml10StartChars, 2365);
        SETBITS(sXml10StartChars, 2392, 2401);
        SETBITS(sXml10StartChars, 2437, 2444);
        SETBITS(sXml10StartChars, 2447, 2448);
        SETBITS(sXml10StartChars, 2451, 2472);
        SETBITS(sXml10StartChars, 2474, 2480);
        SETBITS(sXml10StartChars, 2482);
        SETBITS(sXml10StartChars, 2486, 2489);
        SETBITS(sXml10StartChars, 2524);
        SETBITS(sXml10StartChars, 2525);
        SETBITS(sXml10StartChars, 2527, 2529);
        SETBITS(sXml10StartChars, 2544);
        SETBITS(sXml10StartChars, 2545);
        SETBITS(sXml10StartChars, 2565, 2570);
        SETBITS(sXml10StartChars, 2575);
        SETBITS(sXml10StartChars, 2576);
        SETBITS(sXml10StartChars, 2579, 2600);
        SETBITS(sXml10StartChars, 2602, 2608);
        SETBITS(sXml10StartChars, 2610);
        SETBITS(sXml10StartChars, 2611);
        SETBITS(sXml10StartChars, 2613);
        SETBITS(sXml10StartChars, 2614);
        SETBITS(sXml10StartChars, 2616);
        SETBITS(sXml10StartChars, 2617);
        SETBITS(sXml10StartChars, 2649, 2652);
        SETBITS(sXml10StartChars, 2654);
        SETBITS(sXml10StartChars, 2674, 2676);
        SETBITS(sXml10StartChars, 2693, 2699);
        SETBITS(sXml10StartChars, 2701);
        SETBITS(sXml10StartChars, 2703, 2705);
        SETBITS(sXml10StartChars, 2707, 2728);
        SETBITS(sXml10StartChars, 2730, 2736);
        SETBITS(sXml10StartChars, 2738, 2739);
        SETBITS(sXml10StartChars, 2741, 2745);
        SETBITS(sXml10StartChars, 2749);
        SETBITS(sXml10StartChars, 2784);
        SETBITS(sXml10StartChars, 2821, 2828);
        SETBITS(sXml10StartChars, 2831);
        SETBITS(sXml10StartChars, 2832);
        SETBITS(sXml10StartChars, 2835, 2856);
        SETBITS(sXml10StartChars, 2858, 2864);
        SETBITS(sXml10StartChars, 2866);
        SETBITS(sXml10StartChars, 2867);
        SETBITS(sXml10StartChars, 2870, 2873);
        SETBITS(sXml10StartChars, 2877);
        SETBITS(sXml10StartChars, 2908);
        SETBITS(sXml10StartChars, 2909);
        SETBITS(sXml10StartChars, 2911, 2913);
        SETBITS(sXml10StartChars, 2949, 2954);
        SETBITS(sXml10StartChars, 2958, 2960);
        SETBITS(sXml10StartChars, 2962, 2965);
        SETBITS(sXml10StartChars, 2969, 2970);
        SETBITS(sXml10StartChars, 2972);
        SETBITS(sXml10StartChars, 2974);
        SETBITS(sXml10StartChars, 2975);
        SETBITS(sXml10StartChars, 2979);
        SETBITS(sXml10StartChars, 2980);
        SETBITS(sXml10StartChars, 2984, 2986);
        SETBITS(sXml10StartChars, 2990, 2997);
        SETBITS(sXml10StartChars, 2999, 3001);
        SETBITS(sXml10StartChars, 3077, 3084);
        SETBITS(sXml10StartChars, 3086, 3088);
        SETBITS(sXml10StartChars, 3090, 3112);
        SETBITS(sXml10StartChars, 3114, 3123);
        SETBITS(sXml10StartChars, 3125, 3129);
        SETBITS(sXml10StartChars, 3168);
        SETBITS(sXml10StartChars, 3169);
        SETBITS(sXml10StartChars, 3205, 3212);
        SETBITS(sXml10StartChars, 3214, 3216);
        SETBITS(sXml10StartChars, 3218, 3240);
        SETBITS(sXml10StartChars, 3242, 3251);
        SETBITS(sXml10StartChars, 3253, 3257);
        SETBITS(sXml10StartChars, 3294);
        SETBITS(sXml10StartChars, 3296);
        SETBITS(sXml10StartChars, 3297);
        SETBITS(sXml10StartChars, 3333, 3340);
        SETBITS(sXml10StartChars, 3342, 3344);
        SETBITS(sXml10StartChars, 3346, 3368);
        SETBITS(sXml10StartChars, 3370, 3385);
        SETBITS(sXml10StartChars, 3424);
        SETBITS(sXml10StartChars, 3425);
        SETBITS(sXml10StartChars, 3585, 3630);
        SETBITS(sXml10StartChars, 3632);
        SETBITS(sXml10StartChars, 3634);
        SETBITS(sXml10StartChars, 3635);
        SETBITS(sXml10StartChars, 3648, 3653);
        SETBITS(sXml10StartChars, 3713);
        SETBITS(sXml10StartChars, 3714);
        SETBITS(sXml10StartChars, 3716);
        SETBITS(sXml10StartChars, 3719);
        SETBITS(sXml10StartChars, 3720);
        SETBITS(sXml10StartChars, 3722);
        SETBITS(sXml10StartChars, 3725);
        SETBITS(sXml10StartChars, 3732, 3735);
        SETBITS(sXml10StartChars, 3737, 3743);
        SETBITS(sXml10StartChars, 3745, 3747);
        SETBITS(sXml10StartChars, 3749);
        SETBITS(sXml10StartChars, 3751);
        SETBITS(sXml10StartChars, 3754);
        SETBITS(sXml10StartChars, 3755);
        SETBITS(sXml10StartChars, 3757);
        SETBITS(sXml10StartChars, 3758);
        SETBITS(sXml10StartChars, 3760);
        SETBITS(sXml10StartChars, 3762);
        SETBITS(sXml10StartChars, 3763);
        SETBITS(sXml10StartChars, 3773);
        SETBITS(sXml10StartChars, 3776, 3780);
        SETBITS(sXml10StartChars, 3904, 3911);
        SETBITS(sXml10StartChars, 3913, 3945);
        SETBITS(sXml10StartChars, 4256, 4293);
        SETBITS(sXml10StartChars, 4304, 4342);
        SETBITS(sXml10StartChars, 4352);
        SETBITS(sXml10StartChars, 4354, 4355);
        SETBITS(sXml10StartChars, 4357, 4359);
        SETBITS(sXml10StartChars, 4361);
        SETBITS(sXml10StartChars, 4363, 4364);
        SETBITS(sXml10StartChars, 4366, 4370);
        SETBITS(sXml10StartChars, 4412);
        SETBITS(sXml10StartChars, 4414);
        SETBITS(sXml10StartChars, 4416);
        SETBITS(sXml10StartChars, 4428);
        SETBITS(sXml10StartChars, 4430);
        SETBITS(sXml10StartChars, 4432);
        SETBITS(sXml10StartChars, 4436, 4437);
        SETBITS(sXml10StartChars, 4441);
        SETBITS(sXml10StartChars, 4447, 4449);
        SETBITS(sXml10StartChars, 4451);
        SETBITS(sXml10StartChars, 4453);
        SETBITS(sXml10StartChars, 4455);
        SETBITS(sXml10StartChars, 4457);
        SETBITS(sXml10StartChars, 4461, 4462);
        SETBITS(sXml10StartChars, 4466, 4467);
        SETBITS(sXml10StartChars, 4469);
        SETBITS(sXml10StartChars, 4510);
        SETBITS(sXml10StartChars, 4520);
        SETBITS(sXml10StartChars, 4523);
        SETBITS(sXml10StartChars, 4526, 4527);
        SETBITS(sXml10StartChars, 4535, 4536);
        SETBITS(sXml10StartChars, 4538);
        SETBITS(sXml10StartChars, 4540, 4546);
        SETBITS(sXml10StartChars, 4587);
        SETBITS(sXml10StartChars, 4592);
        SETBITS(sXml10StartChars, 4601);
        SETBITS(sXml10StartChars, 7680, 7835);
        SETBITS(sXml10StartChars, 7840, 7929);
        SETBITS(sXml10StartChars, 7936, 7957);
        SETBITS(sXml10StartChars, 7960, 7965);
        SETBITS(sXml10StartChars, 7968, 8005);
        SETBITS(sXml10StartChars, 8008, 8013);
        SETBITS(sXml10StartChars, 8016, 8023);
        SETBITS(sXml10StartChars, 8025);
        SETBITS(sXml10StartChars, 8027);
        SETBITS(sXml10StartChars, 8029);
        SETBITS(sXml10StartChars, 8031, 8061);
        SETBITS(sXml10StartChars, 8064, 8116);
        SETBITS(sXml10StartChars, 8118, 8124);
        SETBITS(sXml10StartChars, 8126);
        SETBITS(sXml10StartChars, 8130, 8132);
        SETBITS(sXml10StartChars, 8134, 8140);
        SETBITS(sXml10StartChars, 8144, 8147);
        SETBITS(sXml10StartChars, 8150, 8155);
        SETBITS(sXml10StartChars, 8160, 8172);
        SETBITS(sXml10StartChars, 8178, 8180);
        SETBITS(sXml10StartChars, 8182, 8188);
        SETBITS(sXml10StartChars, 8486);
        SETBITS(sXml10StartChars, 8490, 8491);
        SETBITS(sXml10StartChars, 8494);
        SETBITS(sXml10StartChars, 8576, 8578);
        SETBITS(sXml10StartChars, 12353, 12436);
        SETBITS(sXml10StartChars, 12449, 12538);
        SETBITS(sXml10StartChars, 12549, 12588);
        SETBITS(sXml10StartChars, 12295);
        SETBITS(sXml10StartChars, 12321, 12329);
        sXml10Chars = new int[SIZE];
        System.arraycopy(sXml10StartChars, 0, sXml10Chars, 0, SIZE);
        SETBITS(sXml10Chars, 768, 837);
        SETBITS(sXml10Chars, 864, 865);
        SETBITS(sXml10Chars, 1155, 1158);
        SETBITS(sXml10Chars, 1425, 1441);
        SETBITS(sXml10Chars, 1443, 1465);
        SETBITS(sXml10Chars, 1467, 1469);
        SETBITS(sXml10Chars, 1471);
        SETBITS(sXml10Chars, 1473, 1474);
        SETBITS(sXml10Chars, 1476);
        SETBITS(sXml10Chars, 1611, 1618);
        SETBITS(sXml10Chars, 1648);
        SETBITS(sXml10Chars, 1750, 1756);
        SETBITS(sXml10Chars, 1757, 1759);
        SETBITS(sXml10Chars, 1760, 1764);
        SETBITS(sXml10Chars, 1767, 1768);
        SETBITS(sXml10Chars, 1770, 1773);
        SETBITS(sXml10Chars, 2305, 2307);
        SETBITS(sXml10Chars, 2364);
        SETBITS(sXml10Chars, 2366, 2380);
        SETBITS(sXml10Chars, 2381);
        SETBITS(sXml10Chars, 2385, 2388);
        SETBITS(sXml10Chars, 2402);
        SETBITS(sXml10Chars, 2403);
        SETBITS(sXml10Chars, 2433, 2435);
        SETBITS(sXml10Chars, 2492);
        SETBITS(sXml10Chars, 2494);
        SETBITS(sXml10Chars, 2495);
        SETBITS(sXml10Chars, 2496, 2500);
        SETBITS(sXml10Chars, 2503);
        SETBITS(sXml10Chars, 2504);
        SETBITS(sXml10Chars, 2507, 2509);
        SETBITS(sXml10Chars, 2519);
        SETBITS(sXml10Chars, 2530);
        SETBITS(sXml10Chars, 2531);
        SETBITS(sXml10Chars, 2562);
        SETBITS(sXml10Chars, 2620);
        SETBITS(sXml10Chars, 2622);
        SETBITS(sXml10Chars, 2623);
        SETBITS(sXml10Chars, 2624, 2626);
        SETBITS(sXml10Chars, 2631);
        SETBITS(sXml10Chars, 2632);
        SETBITS(sXml10Chars, 2635, 2637);
        SETBITS(sXml10Chars, 2672);
        SETBITS(sXml10Chars, 2673);
        SETBITS(sXml10Chars, 2689, 2691);
        SETBITS(sXml10Chars, 2748);
        SETBITS(sXml10Chars, 2750, 2757);
        SETBITS(sXml10Chars, 2759, 2761);
        SETBITS(sXml10Chars, 2763, 2765);
        SETBITS(sXml10Chars, 2817, 2819);
        SETBITS(sXml10Chars, 2876);
        SETBITS(sXml10Chars, 2878, 2883);
        SETBITS(sXml10Chars, 2887);
        SETBITS(sXml10Chars, 2888);
        SETBITS(sXml10Chars, 2891, 2893);
        SETBITS(sXml10Chars, 2902);
        SETBITS(sXml10Chars, 2903);
        SETBITS(sXml10Chars, 2946);
        SETBITS(sXml10Chars, 2947);
        SETBITS(sXml10Chars, 3006, 3010);
        SETBITS(sXml10Chars, 3014, 3016);
        SETBITS(sXml10Chars, 3018, 3021);
        SETBITS(sXml10Chars, 3031);
        SETBITS(sXml10Chars, 3073, 3075);
        SETBITS(sXml10Chars, 3134, 3140);
        SETBITS(sXml10Chars, 3142, 3144);
        SETBITS(sXml10Chars, 3146, 3149);
        SETBITS(sXml10Chars, 3157, 3158);
        SETBITS(sXml10Chars, 3202, 3203);
        SETBITS(sXml10Chars, 3262, 3268);
        SETBITS(sXml10Chars, 3270, 3272);
        SETBITS(sXml10Chars, 3274, 3277);
        SETBITS(sXml10Chars, 3285, 3286);
        SETBITS(sXml10Chars, 3330, 3331);
        SETBITS(sXml10Chars, 3390, 3395);
        SETBITS(sXml10Chars, 3398, 3400);
        SETBITS(sXml10Chars, 3402, 3405);
        SETBITS(sXml10Chars, 3415);
        SETBITS(sXml10Chars, 3633);
        SETBITS(sXml10Chars, 3636, 3642);
        SETBITS(sXml10Chars, 3655, 3662);
        SETBITS(sXml10Chars, 3761);
        SETBITS(sXml10Chars, 3764, 3769);
        SETBITS(sXml10Chars, 3771, 3772);
        SETBITS(sXml10Chars, 3784, 3789);
        SETBITS(sXml10Chars, 3864, 3865);
        SETBITS(sXml10Chars, 3893);
        SETBITS(sXml10Chars, 3895);
        SETBITS(sXml10Chars, 3897);
        SETBITS(sXml10Chars, 3902);
        SETBITS(sXml10Chars, 3903);
        SETBITS(sXml10Chars, 3953, 3972);
        SETBITS(sXml10Chars, 3974, 3979);
        SETBITS(sXml10Chars, 3984, 3989);
        SETBITS(sXml10Chars, 3991);
        SETBITS(sXml10Chars, 3993, 4013);
        SETBITS(sXml10Chars, 4017, 4023);
        SETBITS(sXml10Chars, 4025);
        SETBITS(sXml10Chars, 8400, 8412);
        SETBITS(sXml10Chars, 8417);
        SETBITS(sXml10Chars, 12330, 12335);
        SETBITS(sXml10Chars, 12441);
        SETBITS(sXml10Chars, 12442);
        SETBITS(sXml10Chars, 1632, 1641);
        SETBITS(sXml10Chars, 1776, 1785);
        SETBITS(sXml10Chars, 2406, 2415);
        SETBITS(sXml10Chars, 2534, 2543);
        SETBITS(sXml10Chars, 2662, 2671);
        SETBITS(sXml10Chars, 2790, 2799);
        SETBITS(sXml10Chars, 2918, 2927);
        SETBITS(sXml10Chars, 3047, 3055);
        SETBITS(sXml10Chars, 3174, 3183);
        SETBITS(sXml10Chars, 3302, 3311);
        SETBITS(sXml10Chars, 3430, 3439);
        SETBITS(sXml10Chars, 3664, 3673);
        SETBITS(sXml10Chars, 3792, 3801);
        SETBITS(sXml10Chars, 3872, 3881);
        SETBITS(sXml10Chars, CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA384);
        SETBITS(sXml10Chars, 720);
        SETBITS(sXml10Chars, 721);
        SETBITS(sXml10Chars, 903);
        SETBITS(sXml10Chars, 1600);
        SETBITS(sXml10Chars, 3654);
        SETBITS(sXml10Chars, 3782);
        SETBITS(sXml10Chars, 12293);
        SETBITS(sXml10Chars, 12337, 12341);
        SETBITS(sXml10Chars, 12445, 12446);
        SETBITS(sXml10Chars, 12540, 12542);
    }

    private XmlChars() {
    }

    public static final boolean is10NameStartChar(char c) {
        if (c <= 12588) {
            return (sXml10StartChars[c >> 5] & (1 << (c & 31))) != 0;
        }
        if (c < 44032) {
            return c >= 19968 && c <= 40869;
        }
        if (c <= 55203) {
            return true;
        }
        return c <= 56319 && c >= 55296;
    }

    public static final boolean is10NameChar(char c) {
        if (c <= 12588) {
            return (sXml10Chars[c >> 5] & (1 << (c & 31))) != 0;
        }
        if (c < 44032) {
            return c >= 19968 && c <= 40869;
        }
        if (c <= 55203) {
            return true;
        }
        return c >= 55296 && c <= 57343;
    }

    public static final boolean is11NameStartChar(char c) {
        if (c <= 12271) {
            if (c < 768) {
                return (c < 192 || c == 215 || c == 247) ? false : true;
            }
            if (c >= 11264) {
                return true;
            }
            if (c < 880 || c > 8591) {
                return false;
            }
            if (c < 8192) {
                return c != 894;
            }
            return c >= 8304 || c == 8204 || c == 8205;
        }
        if (c >= 12289) {
            if (c <= 56319) {
                return true;
            }
            if (c < 63744 || c > 65533) {
                return false;
            }
            return c <= 64975 || c >= 65008;
        }
        return false;
    }

    public static final boolean is11NameChar(char c) {
        if (c <= 12271) {
            if (c < 8192) {
                return (c >= 192 && c != 894) || c == 183;
            }
            if (c >= 11264) {
                return true;
            }
            if (c < 8204 || c > 8591) {
                return false;
            }
            return c >= 8304 || c == 8204 || c == 8205 || c == 8255 || c == 8256;
        }
        if (c >= 12289) {
            if (c <= 57343) {
                return true;
            }
            if (c < 63744 || c > 65533) {
                return false;
            }
            return c <= 64975 || c >= 65008;
        }
        return false;
    }

    private static void SETBITS(int[] array, int start, int end) {
        int bit1 = start & 31;
        int bit2 = end & 31;
        int start2 = start >> 5;
        int end2 = end >> 5;
        if (start2 == end2) {
            while (bit1 <= bit2) {
                array[start2] = array[start2] | (1 << bit1);
                bit1++;
            }
            return;
        }
        for (int bit = bit1; bit <= 31; bit++) {
            array[start2] = array[start2] | (1 << bit);
        }
        while (true) {
            start2++;
            if (start2 >= end2) {
                break;
            } else {
                array[start2] = -1;
            }
        }
        for (int bit3 = 0; bit3 <= bit2; bit3++) {
            array[end2] = array[end2] | (1 << bit3);
        }
    }

    private static void SETBITS(int[] array, int point) {
        int ix = point >> 5;
        int bit = point & 31;
        array[ix] = array[ix] | (1 << bit);
    }
}
