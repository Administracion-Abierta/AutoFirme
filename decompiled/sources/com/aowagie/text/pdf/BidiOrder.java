package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/BidiOrder.class */
final class BidiOrder {
    private final byte[] initialTypes;
    private byte[] embeddings;
    private byte paragraphEmbeddingLevel;
    private int textLength;
    private byte[] resultTypes;
    private byte[] resultLevels;
    public static final byte L = 0;
    private static final byte LRE = 1;
    private static final byte LRO = 2;
    public static final byte R = 3;
    public static final byte AL = 4;
    private static final byte RLE = 5;
    private static final byte RLO = 6;
    private static final byte PDF = 7;
    public static final byte EN = 8;
    private static final byte ES = 9;
    private static final byte ET = 10;
    private static final byte AN = 11;
    private static final byte CS = 12;
    private static final byte NSM = 13;
    private static final byte BN = 14;
    private static final byte B = 15;
    private static final byte S = 16;
    private static final byte WS = 17;
    private static final byte ON = 18;
    private static final byte TYPE_MIN = 0;
    private static final byte TYPE_MAX = 18;
    private static final byte[] rtypes = new byte[65536];
    private static char[] baseTypes = {0, '\b', 14, '\t', '\t', 16, '\n', '\n', 15, 11, 11, 16, '\f', '\f', 17, '\r', '\r', 15, 14, 27, 14, 28, 30, 15, 31, 31, 16, ' ', ' ', 17, '!', '\"', 18, '#', '%', '\n', '&', '*', 18, '+', '+', '\n', ',', ',', '\f', '-', '-', '\n', '.', '.', '\f', '/', '/', '\t', '0', '9', '\b', ':', ':', '\f', ';', '@', 18, 'A', 'Z', 0, '[', '`', 18, 'a', 'z', 0, '{', '~', 18, 127, 132, 14, 133, 133, 15, 134, 159, 14, 160, 160, '\f', 161, 161, 18, 162, 165, '\n', 166, 169, 18, 170, 170, 0, 171, 175, 18, 176, 177, '\n', 178, 179, '\b', 180, 180, 18, 181, 181, 0, 182, 184, 18, 185, 185, '\b', 186, 186, 0, 187, 191, 18, 192, 214, 0, 215, 215, 18, 216, 246, 0, 247, 247, 18, 248, 696, 0, 697, 698, 18, 699, 705, 0, 706, 719, 18, 720, 721, 0, 722, 735, 18, 736, 740, 0, 741, 749, 18, 750, 750, 0, 751, 767, 18, 768, 855, '\r', 856, 860, 0, 861, 879, '\r', 880, 883, 0, 884, 885, 18, 886, 893, 0, 894, 894, 18, 895, 899, 0, 900, 901, 18, 902, 902, 0, 903, 903, 18, 904, 1013, 0, 1014, 1014, 18, 1015, 1154, 0, 1155, 1158, '\r', 1159, 1159, 0, 1160, 1161, '\r', 1162, 1417, 0, 1418, 1418, 18, 1419, 1424, 0, 1425, 1441, '\r', 1442, 1442, 0, 1443, 1465, '\r', 1466, 1466, 0, 1467, 1469, '\r', 1470, 1470, 3, 1471, 1471, '\r', 1472, 1472, 3, 1473, 1474, '\r', 1475, 1475, 3, 1476, 1476, '\r', 1477, 1487, 0, 1488, 1514, 3, 1515, 1519, 0, 1520, 1524, 3, 1525, 1535, 0, 1536, 1539, 4, 1540, 1547, 0, 1548, 1548, '\f', 1549, 1549, 4, 1550, 1551, 18, 1552, 1557, '\r', 1558, 1562, 0, 1563, 1563, 4, 1564, 1566, 0, 1567, 1567, 4, 1568, 1568, 0, 1569, 1594, 4, 1595, 1599, 0, 1600, 1610, 4, 1611, 1624, '\r', 1625, 1631, 0, 1632, 1641, 11, 1642, 1642, '\n', 1643, 1644, 11, 1645, 1647, 4, 1648, 1648, '\r', 1649, 1749, 4, 1750, 1756, '\r', 1757, 1757, 4, 1758, 1764, '\r', 1765, 1766, 4, 1767, 1768, '\r', 1769, 1769, 18, 1770, 1773, '\r', 1774, 1775, 4, 1776, 1785, '\b', 1786, 1805, 4, 1806, 1806, 0, 1807, 1807, 14, 1808, 1808, 4, 1809, 1809, '\r', 1810, 1839, 4, 1840, 1866, '\r', 1867, 1868, 0, 1869, 1871, 4, 1872, 1919, 0, 1920, 1957, 4, 1958, 1968, '\r', 1969, 1969, 4, 1970, 2304, 0, 2305, 2306, '\r', 2307, 2363, 0, 2364, 2364, '\r', 2365, 2368, 0, 2369, 2376, '\r', 2377, 2380, 0, 2381, 2381, '\r', 2382, 2384, 0, 2385, 2388, '\r', 2389, 2401, 0, 2402, 2403, '\r', 2404, 2432, 0, 2433, 2433, '\r', 2434, 2491, 0, 2492, 2492, '\r', 2493, 2496, 0, 2497, 2500, '\r', 2501, 2508, 0, 2509, 2509, '\r', 2510, 2529, 0, 2530, 2531, '\r', 2532, 2545, 0, 2546, 2547, '\n', 2548, 2560, 0, 2561, 2562, '\r', 2563, 2619, 0, 2620, 2620, '\r', 2621, 2624, 0, 2625, 2626, '\r', 2627, 2630, 0, 2631, 2632, '\r', 2633, 2634, 0, 2635, 2637, '\r', 2638, 2671, 0, 2672, 2673, '\r', 2674, 2688, 0, 2689, 2690, '\r', 2691, 2747, 0, 2748, 2748, '\r', 2749, 2752, 0, 2753, 2757, '\r', 2758, 2758, 0, 2759, 2760, '\r', 2761, 2764, 0, 2765, 2765, '\r', 2766, 2785, 0, 2786, 2787, '\r', 2788, 2800, 0, 2801, 2801, '\n', 2802, 2816, 0, 2817, 2817, '\r', 2818, 2875, 0, 2876, 2876, '\r', 2877, 2878, 0, 2879, 2879, '\r', 2880, 2880, 0, 2881, 2883, '\r', 2884, 2892, 0, 2893, 2893, '\r', 2894, 2901, 0, 2902, 2902, '\r', 2903, 2945, 0, 2946, 2946, '\r', 2947, 3007, 0, 3008, 3008, '\r', 3009, 3020, 0, 3021, 3021, '\r', 3022, 3058, 0, 3059, 3064, 18, 3065, 3065, '\n', 3066, 3066, 18, 3067, 3133, 0, 3134, 3136, '\r', 3137, 3141, 0, 3142, 3144, '\r', 3145, 3145, 0, 3146, 3149, '\r', 3150, 3156, 0, 3157, 3158, '\r', 3159, 3259, 0, 3260, 3260, '\r', 3261, 3275, 0, 3276, 3277, '\r', 3278, 3392, 0, 3393, 3395, '\r', 3396, 3404, 0, 3405, 3405, '\r', 3406, 3529, 0, 3530, 3530, '\r', 3531, 3537, 0, 3538, 3540, '\r', 3541, 3541, 0, 3542, 3542, '\r', 3543, 3632, 0, 3633, 3633, '\r', 3634, 3635, 0, 3636, 3642, '\r', 3643, 3646, 0, 3647, 3647, '\n', 3648, 3654, 0, 3655, 3662, '\r', 3663, 3760, 0, 3761, 3761, '\r', 3762, 3763, 0, 3764, 3769, '\r', 3770, 3770, 0, 3771, 3772, '\r', 3773, 3783, 0, 3784, 3789, '\r', 3790, 3863, 0, 3864, 3865, '\r', 3866, 3892, 0, 3893, 3893, '\r', 3894, 3894, 0, 3895, 3895, '\r', 3896, 3896, 0, 3897, 3897, '\r', 3898, 3901, 18, 3902, 3952, 0, 3953, 3966, '\r', 3967, 3967, 0, 3968, 3972, '\r', 3973, 3973, 0, 3974, 3975, '\r', 3976, 3983, 0, 3984, 3991, '\r', 3992, 3992, 0, 3993, 4028, '\r', 4029, 4037, 0, 4038, 4038, '\r', 4039, 4140, 0, 4141, 4144, '\r', 4145, 4145, 0, 4146, 4146, '\r', 4147, 4149, 0, 4150, 4151, '\r', 4152, 4152, 0, 4153, 4153, '\r', 4154, 4183, 0, 4184, 4185, '\r', 4186, 5759, 0, 5760, 5760, 17, 5761, 5786, 0, 5787, 5788, 18, 5789, 5905, 0, 5906, 5908, '\r', 5909, 5937, 0, 5938, 5940, '\r', 5941, 5969, 0, 5970, 5971, '\r', 5972, 6001, 0, 6002, 6003, '\r', 6004, 6070, 0, 6071, 6077, '\r', 6078, 6085, 0, 6086, 6086, '\r', 6087, 6088, 0, 6089, 6099, '\r', 6100, 6106, 0, 6107, 6107, '\n', 6108, 6108, 0, 6109, 6109, '\r', 6110, 6127, 0, 6128, 6137, 18, 6138, 6143, 0, 6144, 6154, 18, 6155, 6157, '\r', 6158, 6158, 17, 6159, 6312, 0, 6313, 6313, '\r', 6314, 6431, 0, 6432, 6434, '\r', 6435, 6438, 0, 6439, 6443, '\r', 6444, 6449, 0, 6450, 6450, '\r', 6451, 6456, 0, 6457, 6459, '\r', 6460, 6463, 0, 6464, 6464, 18, 6465, 6467, 0, 6468, 6469, 18, 6470, 6623, 0, 6624, 6655, 18, 6656, 8124, 0, 8125, 8125, 18, 8126, 8126, 0, 8127, 8129, 18, 8130, 8140, 0, 8141, 8143, 18, 8144, 8156, 0, 8157, 8159, 18, 8160, 8172, 0, 8173, 8175, 18, 8176, 8188, 0, 8189, 8190, 18, 8191, 
    8191, 0, 8192, 8202, 17, 8203, 8205, 14, 8206, 8206, 0, 8207, 8207, 3, 8208, 8231, 18, 8232, 8232, 17, 8233, 8233, 15, 8234, 8234, 1, 8235, 8235, 5, 8236, 8236, 7, 8237, 8237, 2, 8238, 8238, 6, 8239, 8239, 17, 8240, 8244, '\n', 8245, 8276, 18, 8277, 8278, 0, 8279, 8279, 18, 8280, 8286, 0, 8287, 8287, 17, 8288, 8291, 14, 8292, 8297, 0, 8298, 8303, 14, 8304, 8304, '\b', 8305, 8307, 0, 8308, 8313, '\b', 8314, 8315, '\n', 8316, 8318, 18, 8319, 8319, 0, 8320, 8329, '\b', 8330, 8331, '\n', 8332, 8334, 18, 8335, 8351, 0, 8352, 8369, '\n', 8370, 8399, 0, 8400, 8426, '\r', 8427, 8447, 0, 8448, 8449, 18, 8450, 8450, 0, 8451, 8454, 18, 8455, 8455, 0, 8456, 8457, 18, 8458, 8467, 0, 8468, 8468, 18, 8469, 8469, 0, 8470, 8472, 18, 8473, 8477, 0, 8478, 8483, 18, 8484, 8484, 0, 8485, 8485, 18, 8486, 8486, 0, 8487, 8487, 18, 8488, 8488, 0, 8489, 8489, 18, 8490, 8493, 0, 8494, 8494, '\n', 8495, 8497, 0, 8498, 8498, 18, 8499, 8505, 0, 8506, 8507, 18, 8508, 8511, 0, 8512, 8516, 18, 8517, 8521, 0, 8522, 8523, 18, 8524, 8530, 0, 8531, 8543, 18, 8544, 8591, 0, 8592, 8721, 18, 8722, 8723, '\n', 8724, 9013, 18, 9014, 9082, 0, 9083, 9108, 18, 9109, 9109, 0, 9110, 9168, 18, 9169, 9215, 0, 9216, 9254, 18, 9255, 9279, 0, 9280, 9290, 18, 9291, 9311, 0, 9312, 9371, '\b', 9372, 9449, 0, 9450, 9450, '\b', 9451, 9751, 18, 9752, 9752, 0, 9753, 9853, 18, 9854, 9855, 0, 9856, 9873, 18, 9874, 9887, 0, 9888, 9889, 18, 9890, 9984, 0, 9985, 9988, 18, 9989, 9989, 0, 9990, 9993, 18, 9994, 9995, 0, 9996, 10023, 18, 10024, 10024, 0, 10025, 10059, 18, 10060, 10060, 0, 10061, 10061, 18, 10062, 10062, 0, 10063, 10066, 18, 10067, 10069, 0, 10070, 10070, 18, 10071, 10071, 0, 10072, 10078, 18, 10079, 10080, 0, 10081, 10132, 18, 10133, 10135, 0, 10136, 10159, 18, 10160, 10160, 0, 10161, 10174, 18, 10175, 10191, 0, 10192, 10219, 18, 10220, 10223, 0, 10224, 11021, 18, 11022, 11903, 0, 11904, 11929, 18, 11930, 11930, 0, 11931, 12019, 18, 12020, 12031, 0, 12032, 12245, 18, 12246, 12271, 0, 12272, 12283, 18, 12284, 12287, 0, 12288, 12288, 17, 12289, 12292, 18, 12293, 12295, 0, 12296, 12320, 18, 12321, 12329, 0, 12330, 12335, '\r', 12336, 12336, 18, 12337, 12341, 0, 12342, 12343, 18, 12344, 12348, 0, 12349, 12351, 18, 12352, 12440, 0, 12441, 12442, '\r', 12443, 12444, 18, 12445, 12447, 0, 12448, 12448, 18, 12449, 12538, 0, 12539, 12539, 18, 12540, 12828, 0, 12829, 12830, 18, 12831, 12879, 0, 12880, 12895, 18, 12896, 12923, 0, 12924, 12925, 18, 12926, 12976, 0, 12977, 12991, 18, 12992, 13003, 0, 13004, 13007, 18, 13008, 13174, 0, 13175, 13178, 18, 13179, 13277, 0, 13278, 13279, 18, 13280, 13310, 0, 13311, 13311, 18, 13312, 19903, 0, 19904, 19967, 18, 19968, 42127, 0, 42128, 42182, 18, 42183, 64284, 0, 64285, 64285, 3, 64286, 64286, '\r', 64287, 64296, 3, 64297, 64297, '\n', 64298, 64310, 3, 64311, 64311, 0, 64312, 64316, 3, 64317, 64317, 0, 64318, 64318, 3, 64319, 64319, 0, 64320, 64321, 3, 64322, 64322, 0, 64323, 64324, 3, 64325, 64325, 0, 64326, 64335, 3, 64336, 64433, 4, 64434, 64466, 0, 64467, 64829, 4, 64830, 64831, 18, 64832, 64847, 0, 64848, 64911, 4, 64912, 64913, 0, 64914, 64967, 4, 64968, 65007, 0, 65008, 65020, 4, 65021, 65021, 18, 65022, 65023, 0, 65024, 65039, '\r', 65040, 65055, 0, 65056, 65059, '\r', 65060, 65071, 0, 65072, 65103, 18, 65104, 65104, '\f', 65105, 65105, 18, 65106, 65106, '\f', 65107, 65107, 0, 65108, 65108, 18, 65109, 65109, '\f', 65110, 65118, 18, 65119, 65119, '\n', 65120, 65121, 18, 65122, 65123, '\n', 65124, 65126, 18, 65127, 65127, 0, 65128, 65128, 18, 65129, 65130, '\n', 65131, 65131, 18, 65132, 65135, 0, 65136, 65140, 4, 65141, 65141, 0, 65142, 65276, 4, 65277, 65278, 0, 65279, 65279, 14, 65280, 65280, 0, 65281, 65282, 18, 65283, 65285, '\n', 65286, 65290, 18, 65291, 65291, '\n', 65292, 65292, '\f', 65293, 65293, '\n', 65294, 65294, '\f', 65295, 65295, '\t', 65296, 65305, '\b', 65306, 65306, '\f', 65307, 65312, 18, 65313, 65338, 0, 65339, 65344, 18, 65345, 65370, 0, 65371, 65381, 18, 65382, 65503, 0, 65504, 65505, '\n', 65506, 65508, 18, 65509, 65510, '\n', 65511, 65511, 0, 65512, 65518, 18, 65519, 65528, 0, 65529, 65531, 14, 65532, 65533, 18, 65534, 65535, 0};

    BidiOrder(char[] text, int offset, int length, byte paragraphEmbeddingLevel) {
        this.paragraphEmbeddingLevel = (byte) -1;
        this.initialTypes = new byte[length];
        for (int k = 0; k < length; k++) {
            this.initialTypes[k] = rtypes[text[offset + k]];
        }
        validateParagraphEmbeddingLevel(paragraphEmbeddingLevel);
        this.paragraphEmbeddingLevel = paragraphEmbeddingLevel;
        runAlgorithm();
    }

    static final byte getDirection(char c) {
        return rtypes[c];
    }

    private void runAlgorithm() {
        this.textLength = this.initialTypes.length;
        this.resultTypes = (byte[]) this.initialTypes.clone();
        if (this.paragraphEmbeddingLevel == -1) {
            determineParagraphEmbeddingLevel();
        }
        this.resultLevels = new byte[this.textLength];
        setLevels(0, this.textLength, this.paragraphEmbeddingLevel);
        determineExplicitEmbeddingLevels();
        this.textLength = removeExplicitCodes();
        byte prevLevel = this.paragraphEmbeddingLevel;
        int i = 0;
        while (true) {
            int start = i;
            if (start < this.textLength) {
                byte level = this.resultLevels[start];
                byte prevType = typeForLevel(Math.max((int) prevLevel, (int) level));
                int limit = start + 1;
                while (limit < this.textLength && this.resultLevels[limit] == level) {
                    limit++;
                }
                byte succLevel = limit < this.textLength ? this.resultLevels[limit] : this.paragraphEmbeddingLevel;
                byte succType = typeForLevel(Math.max((int) succLevel, (int) level));
                resolveWeakTypes(start, limit, level, prevType, succType);
                resolveNeutralTypes(start, limit, level, prevType, succType);
                resolveImplicitLevels(start, limit, level, prevType, succType);
                prevLevel = level;
                i = limit;
            } else {
                this.textLength = reinsertExplicitCodes(this.textLength);
                return;
            }
        }
    }

    private void determineParagraphEmbeddingLevel() {
        byte strongType = -1;
        for (int i = 0; i < this.textLength; i++) {
            byte t = this.resultTypes[i];
            if (t == 0 || t == 4 || t == 3) {
                strongType = t;
                break;
            }
        }
        if (strongType == -1) {
            this.paragraphEmbeddingLevel = (byte) 0;
        } else if (strongType == 0) {
            this.paragraphEmbeddingLevel = (byte) 0;
        } else {
            this.paragraphEmbeddingLevel = (byte) 1;
        }
    }

    private void determineExplicitEmbeddingLevels() {
        this.embeddings = processEmbeddings(this.resultTypes, this.paragraphEmbeddingLevel);
        for (int i = 0; i < this.textLength; i++) {
            byte level = this.embeddings[i];
            if ((level & 128) != 0) {
                level = (byte) (level & 127);
                this.resultTypes[i] = typeForLevel(level);
            }
            this.resultLevels[i] = level;
        }
    }

    private int removeExplicitCodes() {
        int w = 0;
        for (int i = 0; i < this.textLength; i++) {
            byte t = this.initialTypes[i];
            if (t != 1 && t != 5 && t != 2 && t != 6 && t != 7 && t != 14) {
                this.embeddings[w] = this.embeddings[i];
                this.resultTypes[w] = this.resultTypes[i];
                this.resultLevels[w] = this.resultLevels[i];
                w++;
            }
        }
        return w;
    }

    private int reinsertExplicitCodes(int textLength) {
        int i = this.initialTypes.length;
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            byte t = this.initialTypes[i];
            if (t == 1 || t == 5 || t == 2 || t == 6 || t == 7 || t == 14) {
                this.embeddings[i] = 0;
                this.resultTypes[i] = t;
                this.resultLevels[i] = -1;
            } else {
                textLength--;
                this.embeddings[i] = this.embeddings[textLength];
                this.resultTypes[i] = this.resultTypes[textLength];
                this.resultLevels[i] = this.resultLevels[textLength];
            }
        }
        if (this.resultLevels[0] == -1) {
            this.resultLevels[0] = this.paragraphEmbeddingLevel;
        }
        for (int i2 = 1; i2 < this.initialTypes.length; i2++) {
            if (this.resultLevels[i2] == -1) {
                this.resultLevels[i2] = this.resultLevels[i2 - 1];
            }
        }
        return this.initialTypes.length;
    }

    /* JADX WARN: Code duplicated, block: B:27:0x00f5  */
    private static byte[] processEmbeddings(byte[] resultTypes, byte paragraphEmbeddingLevel) {
        byte newLevel;
        int textLength = resultTypes.length;
        byte[] embeddings = new byte[textLength];
        byte[] embeddingValueStack = new byte[62];
        int stackCounter = 0;
        int overflowAlmostCounter = 0;
        int overflowCounter = 0;
        byte currentEmbeddingLevel = paragraphEmbeddingLevel;
        byte currentEmbeddingValue = paragraphEmbeddingLevel;
        for (int i = 0; i < textLength; i++) {
            embeddings[i] = currentEmbeddingValue;
            byte t = resultTypes[i];
            switch (t) {
                case 1:
                case 2:
                case 5:
                case 6:
                    if (overflowCounter == 0) {
                        if (t == 5 || t == 6) {
                            newLevel = (byte) ((currentEmbeddingLevel + 1) | 1);
                        } else {
                            newLevel = (byte) ((currentEmbeddingLevel + 2) & (-2));
                        }
                        if (newLevel < 62) {
                            embeddingValueStack[stackCounter] = currentEmbeddingValue;
                            stackCounter++;
                            currentEmbeddingLevel = newLevel;
                            if (t == 2 || t == 6) {
                                currentEmbeddingValue = (byte) (newLevel | 128);
                            } else {
                                currentEmbeddingValue = newLevel;
                            }
                            embeddings[i] = currentEmbeddingValue;
                        } else if (currentEmbeddingLevel == 60) {
                            overflowAlmostCounter++;
                        } else {
                            overflowCounter++;
                        }
                    } else {
                        overflowCounter++;
                    }
                    break;
                case 7:
                    if (overflowCounter > 0) {
                        overflowCounter--;
                    } else if (overflowAlmostCounter > 0 && currentEmbeddingLevel != 61) {
                        overflowAlmostCounter--;
                    } else if (stackCounter > 0) {
                        stackCounter--;
                        currentEmbeddingValue = embeddingValueStack[stackCounter];
                        currentEmbeddingLevel = (byte) (currentEmbeddingValue & 127);
                    }
                    break;
                case 15:
                    stackCounter = 0;
                    overflowCounter = 0;
                    overflowAlmostCounter = 0;
                    currentEmbeddingLevel = paragraphEmbeddingLevel;
                    currentEmbeddingValue = paragraphEmbeddingLevel;
                    embeddings[i] = paragraphEmbeddingLevel;
                    break;
            }
        }
        return embeddings;
    }

    private void resolveWeakTypes(int start, int limit, byte level, byte sor, byte eor) {
        byte preceedingCharacterType = sor;
        for (int i = start; i < limit; i++) {
            byte t = this.resultTypes[i];
            if (t == 13) {
                this.resultTypes[i] = preceedingCharacterType;
            } else {
                preceedingCharacterType = t;
            }
        }
        for (int i2 = start; i2 < limit; i2++) {
            if (this.resultTypes[i2] == 8) {
                for (int j = i2 - 1; j >= start; j--) {
                    byte t2 = this.resultTypes[j];
                    if (t2 == 0 || t2 == 3 || t2 == 4) {
                        if (t2 != 4) {
                            break;
                        }
                        this.resultTypes[i2] = 11;
                        break;
                    }
                }
            }
        }
        for (int i3 = start; i3 < limit; i3++) {
            if (this.resultTypes[i3] == 4) {
                this.resultTypes[i3] = 3;
            }
        }
        for (int i4 = start + 1; i4 < limit - 1; i4++) {
            if (this.resultTypes[i4] == 9 || this.resultTypes[i4] == 12) {
                byte prevSepType = this.resultTypes[i4 - 1];
                byte succSepType = this.resultTypes[i4 + 1];
                if (prevSepType == 8 && succSepType == 8) {
                    this.resultTypes[i4] = 8;
                } else if (this.resultTypes[i4] == 12 && prevSepType == 11 && succSepType == 11) {
                    this.resultTypes[i4] = 11;
                }
            }
        }
        int i5 = start;
        while (i5 < limit) {
            if (this.resultTypes[i5] == 10) {
                int runstart = i5;
                int runlimit = findRunLimit(runstart, limit, new byte[]{10});
                byte t3 = runstart == start ? sor : this.resultTypes[runstart - 1];
                if (t3 != 8) {
                    t3 = runlimit == limit ? eor : this.resultTypes[runlimit];
                }
                if (t3 == 8) {
                    setTypes(runstart, runlimit, (byte) 8);
                }
                i5 = runlimit;
            }
            i5++;
        }
        for (int i6 = start; i6 < limit; i6++) {
            byte t4 = this.resultTypes[i6];
            if (t4 == 9 || t4 == 10 || t4 == 12) {
                this.resultTypes[i6] = 18;
            }
        }
        for (int i7 = start; i7 < limit; i7++) {
            if (this.resultTypes[i7] == 8) {
                byte prevStrongType = sor;
                for (int j2 = i7 - 1; j2 >= start; j2--) {
                    byte t5 = this.resultTypes[j2];
                    if (t5 == 0 || t5 == 3) {
                        prevStrongType = t5;
                        break;
                    }
                }
                if (prevStrongType == 0) {
                    this.resultTypes[i7] = 0;
                }
            }
        }
    }

    private void resolveNeutralTypes(int start, int limit, byte level, byte sor, byte eor) {
        byte leadingType;
        byte trailingType;
        byte resolvedType;
        int i = start;
        while (i < limit) {
            byte t = this.resultTypes[i];
            if (t == 17 || t == 18 || t == 15 || t == 16) {
                int runstart = i;
                int runlimit = findRunLimit(runstart, limit, new byte[]{15, 16, 17, 18});
                if (runstart == start) {
                    leadingType = sor;
                } else {
                    leadingType = this.resultTypes[runstart - 1];
                    if (leadingType != 0 && leadingType != 3 && (leadingType == 11 || leadingType == 8)) {
                        leadingType = 3;
                    }
                }
                if (runlimit == limit) {
                    trailingType = eor;
                } else {
                    trailingType = this.resultTypes[runlimit];
                    if (trailingType != 0 && trailingType != 3 && (trailingType == 11 || trailingType == 8)) {
                        trailingType = 3;
                    }
                }
                if (leadingType == trailingType) {
                    resolvedType = leadingType;
                } else {
                    resolvedType = typeForLevel(level);
                }
                setTypes(runstart, runlimit, resolvedType);
                i = runlimit;
            }
            i++;
        }
    }

    private void resolveImplicitLevels(int start, int limit, byte level, byte sor, byte eor) {
        if ((level & 1) == 0) {
            for (int i = start; i < limit; i++) {
                byte t = this.resultTypes[i];
                if (t != 0) {
                    if (t == 3) {
                        byte[] bArr = this.resultLevels;
                        int i2 = i;
                        bArr[i2] = (byte) (bArr[i2] + 1);
                    } else {
                        byte[] bArr2 = this.resultLevels;
                        int i3 = i;
                        bArr2[i3] = (byte) (bArr2[i3] + 2);
                    }
                }
            }
            return;
        }
        for (int i4 = start; i4 < limit; i4++) {
            if (this.resultTypes[i4] != 3) {
                byte[] bArr3 = this.resultLevels;
                int i5 = i4;
                bArr3[i5] = (byte) (bArr3[i5] + 1);
            }
        }
    }

    public byte[] getLevels() {
        return getLevels(new int[]{this.textLength});
    }

    private byte[] getLevels(int[] linebreaks) {
        validateLineBreaks(linebreaks, this.textLength);
        byte[] result = (byte[]) this.resultLevels.clone();
        for (int i = 0; i < result.length; i++) {
            byte t = this.initialTypes[i];
            if (t == 15 || t == 16) {
                result[i] = this.paragraphEmbeddingLevel;
                for (int j = i - 1; j >= 0 && isWhitespace(this.initialTypes[j]); j--) {
                    result[j] = this.paragraphEmbeddingLevel;
                }
            }
        }
        int start = 0;
        for (int limit : linebreaks) {
            for (int j2 = limit - 1; j2 >= start && isWhitespace(this.initialTypes[j2]); j2--) {
                result[j2] = this.paragraphEmbeddingLevel;
            }
            start = limit;
        }
        return result;
    }

    private static int[] computeMultilineReordering(byte[] levels, int[] linebreaks) {
        int[] result = new int[levels.length];
        int start = 0;
        for (int limit : linebreaks) {
            byte[] templevels = new byte[limit - start];
            System.arraycopy(levels, start, templevels, 0, templevels.length);
            int[] temporder = computeReordering(templevels);
            for (int j = 0; j < temporder.length; j++) {
                result[start + j] = temporder[j] + start;
            }
            start = limit;
        }
        return result;
    }

    private static int[] computeReordering(byte[] levels) {
        int lineLength = levels.length;
        int[] result = new int[lineLength];
        for (int i = 0; i < lineLength; i++) {
            result[i] = i;
        }
        byte highestLevel = 0;
        byte lowestOddLevel = 63;
        for (byte level : levels) {
            if (level > highestLevel) {
                highestLevel = level;
            }
            if ((level & 1) != 0 && level < lowestOddLevel) {
                lowestOddLevel = level;
            }
        }
        for (int level2 = highestLevel; level2 >= lowestOddLevel; level2--) {
            int i2 = 0;
            while (i2 < lineLength) {
                if (levels[i2] >= level2) {
                    int start = i2;
                    int limit = i2 + 1;
                    while (limit < lineLength && levels[limit] >= level2) {
                        limit++;
                    }
                    int j = start;
                    for (int k = limit - 1; j < k; k--) {
                        int temp = result[j];
                        result[j] = result[k];
                        result[k] = temp;
                        j++;
                    }
                    i2 = limit;
                }
                i2++;
            }
        }
        return result;
    }

    public byte getBaseLevel() {
        return this.paragraphEmbeddingLevel;
    }

    private static boolean isWhitespace(byte biditype) {
        switch (biditype) {
            case 1:
            case 2:
            case 5:
            case 6:
            case 7:
            case 14:
            case 17:
                return true;
            case 3:
            case 4:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 15:
            case 16:
            default:
                return false;
        }
    }

    private static byte typeForLevel(int level) {
        return (level & 1) == 0 ? (byte) 0 : (byte) 3;
    }

    private int findRunLimit(int index, int limit, byte[] validSet) {
        int index2 = index - 1;
        while (true) {
            index2++;
            if (index2 < limit) {
                byte t = this.resultTypes[index2];
                for (byte b : validSet) {
                    if (t != b) {
                    }
                }
                return index2;
            }
            return limit;
        }
    }

    private int findRunStart(int index, byte[] validSet) {
        while (true) {
            index--;
            if (index >= 0) {
                byte t = this.resultTypes[index];
                for (byte b : validSet) {
                    if (t != b) {
                    }
                }
                return index + 1;
            }
            return 0;
        }
    }

    private void setTypes(int start, int limit, byte newType) {
        for (int i = start; i < limit; i++) {
            this.resultTypes[i] = newType;
        }
    }

    private void setLevels(int start, int limit, byte newLevel) {
        for (int i = start; i < limit; i++) {
            this.resultLevels[i] = newLevel;
        }
    }

    private static void validateTypes(byte[] types) {
        if (types == null) {
            throw new IllegalArgumentException("types is null");
        }
        for (int i = 0; i < types.length; i++) {
            if (types[i] < 0 || types[i] > 18) {
                throw new IllegalArgumentException("illegal type value at " + i + ": " + ((int) types[i]));
            }
        }
        for (int i2 = 0; i2 < types.length - 1; i2++) {
            if (types[i2] == 15) {
                throw new IllegalArgumentException("B type before end of paragraph at index: " + i2);
            }
        }
    }

    private static void validateParagraphEmbeddingLevel(byte paragraphEmbeddingLevel) {
        if (paragraphEmbeddingLevel != -1 && paragraphEmbeddingLevel != 0 && paragraphEmbeddingLevel != 1) {
            throw new IllegalArgumentException("illegal paragraph embedding level: " + ((int) paragraphEmbeddingLevel));
        }
    }

    private static void validateLineBreaks(int[] linebreaks, int textLength) {
        int prev = 0;
        for (int i = 0; i < linebreaks.length; i++) {
            int next = linebreaks[i];
            if (next <= prev) {
                throw new IllegalArgumentException("bad linebreak: " + next + " at index: " + i);
            }
            prev = next;
        }
        if (prev != textLength) {
            throw new IllegalArgumentException("last linebreak must be at " + textLength);
        }
    }

    static {
        int k = 0;
        while (k < baseTypes.length) {
            int start = baseTypes[k];
            int k2 = k + 1;
            char c = baseTypes[k2];
            int k3 = k2 + 1;
            byte b = (byte) baseTypes[k3];
            while (start <= c) {
                int i = start;
                start++;
                rtypes[i] = b;
            }
            k = k3 + 1;
        }
    }
}
