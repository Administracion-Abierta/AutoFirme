package org.bouncycastle.pqc.crypto.falcon;

import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/falcon/FalconKeyGen.class */
class FalconKeyGen {
    private short[] REV10 = {0, 512, 256, 768, 128, 640, 384, 896, 64, 576, 320, 832, 192, 704, 448, 960, 32, 544, 288, 800, 160, 672, 416, 928, 96, 608, 352, 864, 224, 736, 480, 992, 16, 528, 272, 784, 144, 656, 400, 912, 80, 592, 336, 848, 208, 720, 464, 976, 48, 560, 304, 816, 176, 688, 432, 944, 112, 624, 368, 880, 240, 752, 496, 1008, 8, 520, 264, 776, 136, 648, 392, 904, 72, 584, 328, 840, 200, 712, 456, 968, 40, 552, 296, 808, 168, 680, 424, 936, 104, 616, 360, 872, 232, 744, 488, 1000, 24, 536, 280, 792, 152, 664, 408, 920, 88, 600, 344, 856, 216, 728, 472, 984, 56, 568, 312, 824, 184, 696, 440, 952, 120, 632, 376, 888, 248, 760, 504, 1016, 4, 516, 260, 772, 132, 644, 388, 900, 68, 580, 324, 836, 196, 708, 452, 964, 36, 548, 292, 804, 164, 676, 420, 932, 100, 612, 356, 868, 228, 740, 484, 996, 20, 532, 276, 788, 148, 660, 404, 916, 84, 596, 340, 852, 212, 724, 468, 980, 52, 564, 308, 820, 180, 692, 436, 948, 116, 628, 372, 884, 244, 756, 500, 1012, 12, 524, 268, 780, 140, 652, 396, 908, 76, 588, 332, 844, 204, 716, 460, 972, 44, 556, 300, 812, 172, 684, 428, 940, 108, 620, 364, 876, 236, 748, 492, 1004, 28, 540, 284, 796, 156, 668, 412, 924, 92, 604, 348, 860, 220, 732, 476, 988, 60, 572, 316, 828, 188, 700, 444, 956, 124, 636, 380, 892, 252, 764, 508, 1020, 2, 514, 258, 770, 130, 642, 386, 898, 66, 578, 322, 834, 194, 706, 450, 962, 34, 546, 290, 802, 162, 674, 418, 930, 98, 610, 354, 866, 226, 738, 482, 994, 18, 530, 274, 786, 146, 658, 402, 914, 82, 594, 338, 850, 210, 722, 466, 978, 50, 562, 306, 818, 178, 690, 434, 946, 114, 626, 370, 882, 242, 754, 498, 1010, 10, 522, 266, 778, 138, 650, 394, 906, 74, 586, 330, 842, 202, 714, 458, 970, 42, 554, 298, 810, 170, 682, 426, 938, 106, 618, 362, 874, 234, 746, 490, 1002, 26, 538, 282, 794, 154, 666, 410, 922, 90, 602, 346, 858, 218, 730, 474, 986, 58, 570, 314, 826, 186, 698, 442, 954, 122, 634, 378, 890, 250, 762, 506, 1018, 6, 518, 262, 774, 134, 646, 390, 902, 70, 582, 326, 838, 198, 710, 454, 966, 38, 550, 294, 806, 166, 678, 422, 934, 102, 614, 358, 870, 230, 742, 486, 998, 22, 534, 278, 790, 150, 662, 406, 918, 86, 598, 342, 854, 214, 726, 470, 982, 54, 566, 310, 822, 182, 694, 438, 950, 118, 630, 374, 886, 246, 758, 502, 1014, 14, 526, 270, 782, 142, 654, 398, 910, 78, 590, 334, 846, 206, 718, 462, 974, 46, 558, 302, 814, 174, 686, 430, 942, 110, 622, 366, 878, 238, 750, 494, 1006, 30, 542, 286, 798, 158, 670, 414, 926, 94, 606, 350, 862, 222, 734, 478, 990, 62, 574, 318, 830, 190, 702, 446, 958, 126, 638, 382, 894, 254, 766, 510, 1022, 1, 513, 257, 769, 129, 641, 385, 897, 65, 577, 321, 833, 193, 705, 449, 961, 33, 545, 289, 801, 161, 673, 417, 929, 97, 609, 353, 865, 225, 737, 481, 993, 17, 529, 273, 785, 145, 657, 401, 913, 81, 593, 337, 849, 209, 721, 465, 977, 49, 561, 305, 817, 177, 689, 433, 945, 113, 625, 369, 881, 241, 753, 497, 1009, 9, 521, 265, 777, 137, 649, 393, 905, 73, 585, 329, 841, 201, 713, 457, 969, 41, 553, 297, 809, 169, 681, 425, 937, 105, 617, 361, 873, 233, 745, 489, 1001, 25, 537, 281, 793, 153, 665, 409, 921, 89, 601, 345, 857, 217, 729, 473, 985, 57, 569, 313, 825, 185, 697, 441, 953, 121, 633, 377, 889, 249, 761, 505, 1017, 5, 517, 261, 773, 133, 645, 389, 901, 69, 581, 325, 837, 197, 709, 453, 965, 37, 549, 293, 805, 165, 677, 421, 933, 101, 613, 357, 869, 229, 741, 485, 997, 21, 533, 277, 789, 149, 661, 405, 917, 85, 597, 341, 853, 213, 725, 469, 981, 53, 565, 309, 821, 181, 693, 437, 949, 117, 629, 373, 885, 245, 757, 501, 1013, 13, 525, 269, 781, 141, 653, 397, 909, 77, 589, 333, 845, 205, 717, 461, 973, 45, 557, 301, 813, 173, 685, 429, 941, 109, 621, 365, 877, 237, 749, 493, 1005, 29, 541, 285, 797, 157, 669, 413, 925, 93, 605, 349, 861, 221, 733, 477, 989, 61, 573, 317, 829, 189, 701, 445, 957, 125, 637, 381, 893, 253, 765, 509, 1021, 3, 515, 259, 771, 131, 643, 387, 899, 67, 579, 323, 835, 195, 707, 451, 963, 35, 547, 291, 803, 163, 675, 419, 931, 99, 611, 355, 867, 227, 739, 483, 995, 19, 531, 275, 787, 147, 659, 403, 915, 83, 595, 339, 851, 211, 723, 467, 979, 51, 563, 307, 819, 179, 691, 435, 947, 115, 627, 371, 883, 243, 755, 499, 1011, 11, 523, 267, 779, 139, 651, 395, 907, 75, 587, 331, 843, 203, 715, 459, 971, 43, 555, 299, 811, 171, 683, 427, 939, 107, 619, 363, 875, 235, 747, 491, 1003, 27, 539, 283, 795, 155, 667, 411, 923, 91, 603, 347, 859, 219, 731, 475, 987, 59, 571, 315, 827, 187, 699, 443, 955, 123, 635, 379, 891, 251, 763, 507, 1019, 7, 519, 263, 775, 135, 647, 391, 903, 71, 583, 327, 839, 199, 711, 455, 967, 39, 551, 295, 807, 167, 679, 423, 935, 103, 615, 359, 871, 231, 743, 487, 999, 23, 535, 279, 791, 151, 663, 407, 919, 87, 599, 343, 855, 215, 727, 471, 983, 55, 567, 311, 823, 183, 695, 439, 951, 119, 631, 375, 887, 247, 759, 503, 1015, 15, 527, 271, 783, 143, 655, 399, 911, 79, 591, 335, 847, 207, 719, 463, 975, 47, 559, 303, 815, 175, 687, 431, 943, 111, 623, 367, 879, 239, 751, 495, 1007, 31, 543, 287, 799, 159, 671, 415, 927, 
    95, 607, 351, 863, 223, 735, 479, 991, 63, 575, 319, 831, 191, 703, 447, 959, 127, 639, 383, 895, 255, 767, 511, 1023};
    final long[] gauss_1024_12289 = {1283868770400643928L, 6416574995475331444L, 4078260278032692663L, 2353523259288686585L, 1227179971273316331L, 575931623374121527L, 242543240509105209L, 91437049221049666L, 30799446349977173L, 9255276791179340L, 2478152334826140L, 590642893610164L, 125206034929641L, 23590435911403L, 3948334035941L, 586753615614L, 77391054539L, 9056793210L, 940121950, 86539696, 7062824, 510971, 32764, 1862, 94, 4, 0};
    final int[] MAX_BL_SMALL = {1, 1, 2, 2, 4, 7, 14, 27, 53, 106, WinError.ERROR_INVALID_SIGNAL_NUMBER};
    final int[] MAX_BL_LARGE = {2, 2, 5, 7, 12, 21, 40, 78, 157, 308};
    final int[] bitlength_avg = {4, 11, 24, 50, 102, 202, WinError.ERROR_THREAD_MODE_NOT_BACKGROUND, 794, 1577, 3138, 6308};
    final int[] bitlength_std = {0, 1, 1, 1, 1, 2, 4, 5, 8, 13, 25};
    final int DEPTH_INT_FG = 4;
    FPREngine fpr = new FPREngine();
    FalconSmallPrimeList primes = new FalconSmallPrimeList();
    FalconFFT fft = new FalconFFT();
    FalconCodec codec = new FalconCodec();
    FalconVrfy vrfy = new FalconVrfy();

    FalconKeyGen() {
    }

    private static int mkn(int i) {
        return 1 << i;
    }

    int modp_set(int i, int i2) {
        return i + (i2 & (-(i >>> 31)));
    }

    int modp_norm(int i, int i2) {
        return i - (i2 & (((i - ((i2 + 1) >>> 1)) >>> 31) - 1));
    }

    int modp_ninv31(int i) {
        int i2 = 2 - i;
        int i3 = i2 * (2 - (i * i2));
        int i4 = i3 * (2 - (i * i3));
        int i5 = i4 * (2 - (i * i4));
        return Integer.MAX_VALUE & (-(i5 * (2 - (i * i5))));
    }

    int modp_R(int i) {
        return Integer.MIN_VALUE - i;
    }

    int modp_add(int i, int i2, int i3) {
        int i4 = (i + i2) - i3;
        return i4 + (i3 & (-(i4 >>> 31)));
    }

    int modp_sub(int i, int i2, int i3) {
        int i4 = i - i2;
        return i4 + (i3 & (-(i4 >>> 31)));
    }

    int modp_montymul(int i, int i2, int i3, int i4) {
        long unsignedLong = toUnsignedLong(i) * toUnsignedLong(i2);
        int unsignedLong2 = ((int) ((unsignedLong + (((unsignedLong * ((long) i4)) & toUnsignedLong(WinNT.MAXLONG)) * ((long) i3))) >>> 31)) - i3;
        return unsignedLong2 + (i3 & (-(unsignedLong2 >>> 31)));
    }

    int modp_R2(int i, int i2) {
        int iModp_R = modp_R(i);
        int iModp_add = modp_add(iModp_R, iModp_R, i);
        int iModp_montymul = modp_montymul(iModp_add, iModp_add, i, i2);
        int iModp_montymul2 = modp_montymul(iModp_montymul, iModp_montymul, i, i2);
        int iModp_montymul3 = modp_montymul(iModp_montymul2, iModp_montymul2, i, i2);
        int iModp_montymul4 = modp_montymul(iModp_montymul3, iModp_montymul3, i, i2);
        int iModp_montymul5 = modp_montymul(iModp_montymul4, iModp_montymul4, i, i2);
        return (iModp_montymul5 + (i & (-(iModp_montymul5 & 1)))) >>> 1;
    }

    int modp_Rx(int i, int i2, int i3, int i4) {
        int i5 = i - 1;
        int iModp_montymul = i4;
        int iModp_R = modp_R(i2);
        for (int i6 = 0; (1 << i6) <= i5; i6++) {
            if ((i5 & (1 << i6)) != 0) {
                iModp_R = modp_montymul(iModp_R, iModp_montymul, i2, i3);
            }
            iModp_montymul = modp_montymul(iModp_montymul, iModp_montymul, i2, i3);
        }
        return iModp_R;
    }

    int modp_div(int i, int i2, int i3, int i4, int i5) {
        int i6 = i3 - 2;
        int iModp_montymul = i5;
        for (int i7 = 30; i7 >= 0; i7--) {
            int iModp_montymul2 = modp_montymul(iModp_montymul, iModp_montymul, i3, i4);
            iModp_montymul = iModp_montymul2 ^ ((iModp_montymul2 ^ modp_montymul(iModp_montymul2, i2, i3, i4)) & (-((i6 >>> i7) & 1)));
        }
        return modp_montymul(i, modp_montymul(iModp_montymul, 1, i3, i4), i3, i4);
    }

    void modp_mkgm2(int[] iArr, int i, int[] iArr2, int i2, int i3, int i4, int i5, int i6) {
        int iMkn = mkn(i3);
        int iModp_R2 = modp_R2(i5, i6);
        int iModp_montymul = modp_montymul(i4, iModp_R2, i5, i6);
        for (int i7 = i3; i7 < 10; i7++) {
            iModp_montymul = modp_montymul(iModp_montymul, iModp_montymul, i5, i6);
        }
        int iModp_div = modp_div(iModp_R2, iModp_montymul, i5, i6, modp_R(i5));
        int i8 = 10 - i3;
        int iModp_R = modp_R(i5);
        int iModp_montymul2 = iModp_R;
        int iModp_montymul3 = iModp_R;
        for (int i9 = 0; i9 < iMkn; i9++) {
            short s = this.REV10[i9 << i8];
            iArr[i + s] = iModp_montymul3;
            iArr2[i2 + s] = iModp_montymul2;
            iModp_montymul3 = modp_montymul(iModp_montymul3, iModp_montymul, i5, i6);
            iModp_montymul2 = modp_montymul(iModp_montymul2, iModp_div, i5, i6);
        }
    }

    void modp_NTT2_ext(int[] iArr, int i, int i2, int[] iArr2, int i3, int i4, int i5, int i6) {
        if (i4 == 0) {
            return;
        }
        int iMkn = mkn(i4);
        int i7 = iMkn;
        int i8 = 1;
        while (true) {
            int i9 = i8;
            if (i9 >= iMkn) {
                return;
            }
            int i10 = i7 >> 1;
            int i11 = 0;
            int i12 = 0;
            while (true) {
                int i13 = i12;
                if (i11 < i9) {
                    int i14 = iArr2[i3 + i9 + i11];
                    int i15 = i + (i13 * i2);
                    int i16 = i15 + (i10 * i2);
                    int i17 = 0;
                    while (i17 < i10) {
                        int i18 = iArr[i15];
                        int iModp_montymul = modp_montymul(iArr[i16], i14, i5, i6);
                        iArr[i15] = modp_add(i18, iModp_montymul, i5);
                        iArr[i16] = modp_sub(i18, iModp_montymul, i5);
                        i17++;
                        i15 += i2;
                        i16 += i2;
                    }
                    i11++;
                    i12 = i13 + i7;
                }
            }
            i7 = i10;
            i8 = i9 << 1;
        }
    }

    void modp_iNTT2_ext(int[] iArr, int i, int i2, int[] iArr2, int i3, int i4, int i5, int i6) {
        if (i4 == 0) {
            return;
        }
        int iMkn = mkn(i4);
        int i7 = 1;
        int i8 = iMkn;
        while (true) {
            int i9 = i8;
            if (i9 <= 1) {
                break;
            }
            int i10 = i9 >> 1;
            int i11 = i7 << 1;
            int i12 = 0;
            int i13 = 0;
            while (true) {
                int i14 = i13;
                if (i12 < i10) {
                    int i15 = iArr2[i3 + i10 + i12];
                    int i16 = i + (i14 * i2);
                    int i17 = i16 + (i7 * i2);
                    int i18 = 0;
                    while (i18 < i7) {
                        int i19 = iArr[i16];
                        int i20 = iArr[i17];
                        iArr[i16] = modp_add(i19, i20, i5);
                        iArr[i17] = modp_montymul(modp_sub(i19, i20, i5), i15, i5, i6);
                        i18++;
                        i16 += i2;
                        i17 += i2;
                    }
                    i12++;
                    i13 = i14 + i11;
                }
            }
            i7 = i11;
            i8 = i9 >> 1;
        }
        int i21 = 1 << (31 - i4);
        int i22 = 0;
        int i23 = i;
        while (true) {
            int i24 = i23;
            if (i22 >= iMkn) {
                return;
            }
            iArr[i24] = modp_montymul(iArr[i24], i21, i5, i6);
            i22++;
            i23 = i24 + i2;
        }
    }

    void modp_NTT2(int[] iArr, int i, int[] iArr2, int i2, int i3, int i4, int i5) {
        modp_NTT2_ext(iArr, i, 1, iArr2, i2, i3, i4, i5);
    }

    void modp_iNTT2(int[] iArr, int i, int[] iArr2, int i2, int i3, int i4, int i5) {
        modp_iNTT2_ext(iArr, i, 1, iArr2, i2, i3, i4, i5);
    }

    void modp_poly_rec_res(int[] iArr, int i, int i2, int i3, int i4, int i5) {
        int i6 = 1 << (i2 - 1);
        for (int i7 = 0; i7 < i6; i7++) {
            iArr[i + i7] = modp_montymul(modp_montymul(iArr[i + (i7 << 1) + 0], iArr[i + (i7 << 1) + 1], i3, i4), i5, i3, i4);
        }
    }

    int zint_sub(int[] iArr, int i, int[] iArr2, int i2, int i3, int i4) {
        int i5 = 0;
        int i6 = -i4;
        for (int i7 = 0; i7 < i3; i7++) {
            int i8 = iArr[i + i7];
            int i9 = (i8 - iArr2[i2 + i7]) - i5;
            i5 = i9 >>> 31;
            iArr[i + i7] = i8 ^ (((i9 & WinNT.MAXLONG) ^ i8) & i6);
        }
        return i5;
    }

    int zint_mul_small(int[] iArr, int i, int i2, int i3) {
        int i4 = 0;
        for (int i5 = 0; i5 < i2; i5++) {
            long unsignedLong = (toUnsignedLong(iArr[i + i5]) * toUnsignedLong(i3)) + ((long) i4);
            iArr[i + i5] = ((int) unsignedLong) & WinNT.MAXLONG;
            i4 = (int) (unsignedLong >> 31);
        }
        return i4;
    }

    int zint_mod_small_unsigned(int[] iArr, int i, int i2, int i3, int i4, int i5) {
        int iModp_add = 0;
        int i6 = i2;
        while (true) {
            int i7 = i6;
            i6--;
            if (i7 <= 0) {
                return iModp_add;
            }
            int iModp_montymul = modp_montymul(iModp_add, i5, i3, i4);
            int i8 = iArr[i + i6] - i3;
            iModp_add = modp_add(iModp_montymul, i8 + (i3 & (-(i8 >>> 31))), i3);
        }
    }

    int zint_mod_small_signed(int[] iArr, int i, int i2, int i3, int i4, int i5, int i6) {
        if (i2 == 0) {
            return 0;
        }
        return modp_sub(zint_mod_small_unsigned(iArr, i, i2, i3, i4, i5), i6 & (-(iArr[(i + i2) - 1] >>> 30)), i3);
    }

    void zint_add_mul_small(int[] iArr, int i, int[] iArr2, int i2, int i3, int i4) {
        int i5 = 0;
        for (int i6 = 0; i6 < i3; i6++) {
            long unsignedLong = (toUnsignedLong(iArr2[i2 + i6]) * toUnsignedLong(i4)) + toUnsignedLong(iArr[i + i6]) + toUnsignedLong(i5);
            iArr[i + i6] = ((int) unsignedLong) & WinNT.MAXLONG;
            i5 = (int) (unsignedLong >>> 31);
        }
        iArr[i + i3] = i5;
    }

    void zint_norm_zero(int[] iArr, int i, int[] iArr2, int i2, int i3) {
        int i4 = 0;
        int i5 = 0;
        int i6 = i3;
        while (true) {
            int i7 = i6;
            i6--;
            if (i7 <= 0) {
                zint_sub(iArr, i, iArr2, i2, i3, i4 >>> 31);
                return;
            }
            int i8 = iArr[i + i6];
            int i9 = (iArr2[i2 + i6] >>> 1) | (i5 << 30);
            i5 = iArr2[i2 + i6] & 1;
            int i10 = i9 - i8;
            i4 |= (((-i10) >>> 31) | (-(i10 >>> 31))) & ((i4 & 1) - 1);
        }
    }

    void zint_rebuild_CRT(int[] iArr, int i, int i2, int i3, int i4, FalconSmallPrime[] falconSmallPrimeArr, int i5, int[] iArr2, int i6) {
        iArr2[i6 + 0] = falconSmallPrimeArr[0].p;
        for (int i7 = 1; i7 < i2; i7++) {
            int i8 = falconSmallPrimeArr[i7].p;
            int i9 = falconSmallPrimeArr[i7].s;
            int iModp_ninv31 = modp_ninv31(i8);
            int iModp_R2 = modp_R2(i8, iModp_ninv31);
            int i10 = 0;
            int i11 = i;
            while (true) {
                int i12 = i11;
                if (i10 < i4) {
                    zint_add_mul_small(iArr, i12, iArr2, i6, i7, modp_montymul(i9, modp_sub(iArr[i12 + i7], zint_mod_small_unsigned(iArr, i12, i7, i8, iModp_ninv31, iModp_R2), i8), i8, iModp_ninv31));
                    i10++;
                    i11 = i12 + i3;
                }
            }
            iArr2[i6 + i7] = zint_mul_small(iArr2, i6, i7, i8);
        }
        if (i5 == 0) {
            return;
        }
        int i13 = 0;
        int i14 = i;
        while (true) {
            int i15 = i14;
            if (i13 >= i4) {
                return;
            }
            zint_norm_zero(iArr, i15, iArr2, i6, i2);
            i13++;
            i14 = i15 + i3;
        }
    }

    void zint_negate(int[] iArr, int i, int i2, int i3) {
        int i4 = i3;
        int i5 = (-i3) >>> 1;
        for (int i6 = 0; i6 < i2; i6++) {
            int i7 = (iArr[i + i6] ^ i5) + i4;
            iArr[i + i6] = i7 & WinNT.MAXLONG;
            i4 = i7 >>> 31;
        }
    }

    int zint_co_reduce(int[] iArr, int i, int[] iArr2, int i2, int i3, long j, long j2, long j3, long j4) {
        long j5 = 0;
        long j6 = 0;
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = iArr[i + i4];
            int i6 = iArr2[i2 + i4];
            long j7 = (((long) i5) * j) + (((long) i6) * j2) + j5;
            long j8 = (((long) i5) * j3) + (((long) i6) * j4) + j6;
            if (i4 > 0) {
                iArr[(i + i4) - 1] = ((int) j7) & WinNT.MAXLONG;
                iArr2[(i2 + i4) - 1] = ((int) j8) & WinNT.MAXLONG;
            }
            j5 = j7 >> 31;
            j6 = j8 >> 31;
        }
        iArr[(i + i3) - 1] = (int) j5;
        iArr2[(i2 + i3) - 1] = (int) j6;
        int i7 = (int) (j5 >>> 63);
        int i8 = (int) (j6 >>> 63);
        zint_negate(iArr, i, i3, i7);
        zint_negate(iArr2, i2, i3, i8);
        return i7 | (i8 << 1);
    }

    void zint_finish_mod(int[] iArr, int i, int i2, int[] iArr2, int i3, int i4) {
        int i5 = 0;
        for (int i6 = 0; i6 < i2; i6++) {
            i5 = ((iArr[i + i6] - iArr2[i3 + i6]) - i5) >>> 31;
        }
        int i7 = (-i4) >>> 1;
        int i8 = -(i4 | (1 - i5));
        int i9 = i4;
        for (int i10 = 0; i10 < i2; i10++) {
            int i11 = (iArr[i + i10] - ((iArr2[i3 + i10] ^ i7) & i8)) - i9;
            iArr[i + i10] = i11 & WinNT.MAXLONG;
            i9 = i11 >>> 31;
        }
    }

    void zint_co_reduce_mod(int[] iArr, int i, int[] iArr2, int i2, int[] iArr3, int i3, int i4, int i5, long j, long j2, long j3, long j4) {
        long j5 = 0;
        long j6 = 0;
        int i6 = (((iArr[i + 0] * ((int) j)) + (iArr2[i2 + 0] * ((int) j2))) * i5) & WinNT.MAXLONG;
        int i7 = (((iArr[i + 0] * ((int) j3)) + (iArr2[i2 + 0] * ((int) j4))) * i5) & WinNT.MAXLONG;
        for (int i8 = 0; i8 < i4; i8++) {
            int i9 = iArr[i + i8];
            int i10 = iArr2[i2 + i8];
            long unsignedLong = (((long) i9) * j) + (((long) i10) * j2) + (((long) iArr3[i3 + i8]) * toUnsignedLong(i6)) + j5;
            long unsignedLong2 = (((long) i9) * j3) + (((long) i10) * j4) + (((long) iArr3[i3 + i8]) * toUnsignedLong(i7)) + j6;
            if (i8 > 0) {
                iArr[(i + i8) - 1] = ((int) unsignedLong) & WinNT.MAXLONG;
                iArr2[(i2 + i8) - 1] = ((int) unsignedLong2) & WinNT.MAXLONG;
            }
            j5 = unsignedLong >> 31;
            j6 = unsignedLong2 >> 31;
        }
        iArr[(i + i4) - 1] = (int) j5;
        iArr2[(i2 + i4) - 1] = (int) j6;
        zint_finish_mod(iArr, i, i4, iArr3, i3, (int) (j5 >>> 63));
        zint_finish_mod(iArr2, i2, i4, iArr3, i3, (int) (j6 >>> 63));
    }

    int zint_bezout(int[] iArr, int i, int[] iArr2, int i2, int[] iArr3, int i3, int[] iArr4, int i4, int i5, int[] iArr5, int i6) {
        if (i5 == 0) {
            return 0;
        }
        int i7 = i6 + i5;
        int i8 = i7 + i5;
        int i9 = i8 + i5;
        int iModp_ninv31 = modp_ninv31(iArr3[i3 + 0]);
        int iModp_ninv32 = modp_ninv31(iArr4[i4 + 0]);
        System.arraycopy(iArr3, i3, iArr5, i8, i5);
        System.arraycopy(iArr4, i4, iArr5, i9, i5);
        iArr[i + 0] = 1;
        iArr2[i2 + 0] = 0;
        for (int i10 = 1; i10 < i5; i10++) {
            iArr[i + i10] = 0;
            iArr2[i2 + i10] = 0;
        }
        System.arraycopy(iArr4, i4, iArr5, i6, i5);
        System.arraycopy(iArr3, i3, iArr5, i7, i5);
        int i11 = i7 + 0;
        iArr5[i11] = iArr5[i11] - 1;
        for (int i12 = (62 * i5) + 30; i12 >= 30; i12 -= 30) {
            int i13 = -1;
            int i14 = -1;
            int i15 = 0;
            int i16 = 0;
            int i17 = 0;
            int i18 = 0;
            int i19 = i5;
            while (true) {
                int i20 = i19;
                i19--;
                if (i20 <= 0) {
                    break;
                }
                int i21 = iArr5[i8 + i19];
                int i22 = iArr5[i9 + i19];
                i15 ^= (i15 ^ i21) & i13;
                i16 ^= (i16 ^ i21) & i14;
                i17 ^= (i17 ^ i22) & i13;
                i18 ^= (i18 ^ i22) & i14;
                i14 = i13;
                i13 &= (((i21 | i22) + WinNT.MAXLONG) >>> 31) - 1;
            }
            long unsignedLong = (toUnsignedLong(i15 & (i14 ^ (-1))) << 31) + toUnsignedLong(i16 | (i15 & i14));
            long unsignedLong2 = (toUnsignedLong(i17 & (i14 ^ (-1))) << 31) + toUnsignedLong(i18 | (i17 & i14));
            int i23 = iArr5[i8 + 0];
            int i24 = iArr5[i9 + 0];
            long j = 1;
            long j2 = 0;
            long j3 = 0;
            long j4 = 1;
            for (int i25 = 0; i25 < 31; i25++) {
                long j5 = unsignedLong2 - unsignedLong;
                int i26 = (int) ((j5 ^ ((unsignedLong ^ unsignedLong2) & (unsignedLong ^ j5))) >>> 63);
                int i27 = (i23 >> i25) & 1;
                int i28 = (i24 >> i25) & 1;
                int i29 = i27 & i28 & i26;
                int i30 = i27 & i28 & (i26 ^ (-1));
                int i31 = i29 | (i27 ^ 1);
                int i32 = i23 - (i24 & (-i29));
                long j6 = unsignedLong - (unsignedLong2 & (-toUnsignedLong(i29)));
                long j7 = j - (j3 & (-i29));
                long j8 = j2 - (j4 & (-i29));
                int i33 = i24 - (i32 & (-i30));
                long j9 = unsignedLong2 - (j6 & (-toUnsignedLong(i30)));
                long j10 = j3 - (j7 & (-i30));
                long j11 = j4 - (j8 & (-i30));
                i23 = i32 + (i32 & (i31 - 1));
                j = j7 + (j7 & (((long) i31) - 1));
                j2 = j8 + (j8 & (((long) i31) - 1));
                unsignedLong = j6 ^ ((j6 ^ (j6 >> 1)) & (-toUnsignedLong(i31)));
                i24 = i33 + (i33 & (-i31));
                j3 = j10 + (j10 & (-i31));
                j4 = j11 + (j11 & (-i31));
                unsignedLong2 = j9 ^ ((j9 ^ (j9 >> 1)) & (toUnsignedLong(i31) - 1));
            }
            int iZint_co_reduce = zint_co_reduce(iArr5, i8, iArr5, i9, i5, j, j2, j3, j4);
            long j12 = j - ((j + j) & (-(iZint_co_reduce & 1)));
            long j13 = j2 - ((j2 + j2) & (-(iZint_co_reduce & 1)));
            long j14 = j3 - ((j3 + j3) & (-(iZint_co_reduce >>> 1)));
            long j15 = j4 - ((j4 + j4) & (-(iZint_co_reduce >>> 1)));
            zint_co_reduce_mod(iArr, i, iArr5, i6, iArr4, i4, i5, iModp_ninv32, j12, j13, j14, j15);
            zint_co_reduce_mod(iArr2, i2, iArr5, i7, iArr3, i3, i5, iModp_ninv31, j12, j13, j14, j15);
        }
        int i34 = iArr5[i8 + 0] ^ 1;
        for (int i35 = 1; i35 < i5; i35++) {
            i34 |= iArr5[i8 + i35];
        }
        return (1 - ((i34 | (-i34)) >>> 31)) & iArr3[i3 + 0] & iArr4[i4 + 0];
    }

    void zint_add_scaled_mul_small(int[] iArr, int i, int i2, int[] iArr2, int i3, int i4, int i5, int i6, int i7) {
        if (i4 == 0) {
            return;
        }
        int i8 = (-(iArr2[(i3 + i4) - 1] >>> 30)) >>> 1;
        int i9 = 0;
        int i10 = 0;
        for (int i11 = i6; i11 < i2; i11++) {
            int i12 = i11 - i6;
            int i13 = i12 < i4 ? iArr2[i3 + i12] : i8;
            int i14 = ((i13 << i7) & WinNT.MAXLONG) | i9;
            i9 = i13 >>> (31 - i7);
            long unsignedLong = (toUnsignedLong(i14) * ((long) i5)) + toUnsignedLong(iArr[i + i11]) + ((long) i10);
            iArr[i + i11] = ((int) unsignedLong) & WinNT.MAXLONG;
            i10 = (int) (unsignedLong >>> 31);
        }
    }

    void zint_sub_scaled(int[] iArr, int i, int i2, int[] iArr2, int i3, int i4, int i5, int i6) {
        if (i4 == 0) {
            return;
        }
        int i7 = (-(iArr2[(i3 + i4) - 1] >>> 30)) >>> 1;
        int i8 = 0;
        int i9 = 0;
        for (int i10 = i5; i10 < i2; i10++) {
            int i11 = i10 - i5;
            int i12 = i11 < i4 ? iArr2[i3 + i11] : i7;
            int i13 = ((i12 << i6) & WinNT.MAXLONG) | i8;
            i8 = i12 >>> (31 - i6);
            int i14 = (iArr[i + i10] - i13) - i9;
            iArr[i + i10] = i14 & WinNT.MAXLONG;
            i9 = i14 >>> 31;
        }
    }

    int zint_one_to_plain(int[] iArr, int i) {
        int i2 = iArr[i + 0];
        return i2 | ((i2 & 1073741824) << 1);
    }

    void poly_big_to_fp(FalconFPR[] falconFPRArr, int i, int[] iArr, int i2, int i3, int i4, int i5) {
        int iMkn = mkn(i5);
        if (i3 == 0) {
            for (int i6 = 0; i6 < iMkn; i6++) {
                falconFPRArr[i + i6] = this.fpr.fpr_zero;
            }
            return;
        }
        int i7 = 0;
        while (i7 < iMkn) {
            int i8 = -(iArr[(i2 + i3) - 1] >>> 30);
            int i9 = i8 >>> 1;
            int i10 = i8 & 1;
            FalconFPR falconFPRFpr_add = this.fpr.fpr_zero;
            FalconFPR falconFPRFpr_mul = this.fpr.fpr_one;
            int i11 = 0;
            while (i11 < i3) {
                int i12 = (iArr[i2 + i11] ^ i9) + i10;
                i10 = i12 >>> 31;
                int i13 = i12 & WinNT.MAXLONG;
                falconFPRFpr_add = this.fpr.fpr_add(falconFPRFpr_add, this.fpr.fpr_mul(this.fpr.fpr_of(i13 - ((i13 << 1) & i8)), falconFPRFpr_mul));
                i11++;
                falconFPRFpr_mul = this.fpr.fpr_mul(falconFPRFpr_mul, this.fpr.fpr_ptwo31);
            }
            falconFPRArr[i + i7] = falconFPRFpr_add;
            i7++;
            i2 += i4;
        }
    }

    int poly_big_to_small(byte[] bArr, int i, int[] iArr, int i2, int i3, int i4) {
        int iMkn = mkn(i4);
        for (int i5 = 0; i5 < iMkn; i5++) {
            int iZint_one_to_plain = zint_one_to_plain(iArr, i2 + i5);
            if (iZint_one_to_plain < (-i3) || iZint_one_to_plain > i3) {
                return 0;
            }
            bArr[i + i5] = (byte) iZint_one_to_plain;
        }
        return 1;
    }

    void poly_sub_scaled(int[] iArr, int i, int i2, int i3, int[] iArr2, int i4, int i5, int i6, int[] iArr3, int i7, int i8, int i9, int i10) {
        int iMkn = mkn(i10);
        for (int i11 = 0; i11 < iMkn; i11++) {
            int i12 = -iArr3[i7 + i11];
            int i13 = i + (i11 * i3);
            int i14 = i4;
            for (int i15 = 0; i15 < iMkn; i15++) {
                zint_add_scaled_mul_small(iArr, i13, i2, iArr2, i14, i5, i12, i8, i9);
                if (i11 + i15 == iMkn - 1) {
                    i13 = i;
                    i12 = -i12;
                } else {
                    i13 += i3;
                }
                i14 += i6;
            }
        }
    }

    void poly_sub_scaled_ntt(int[] iArr, int i, int i2, int i3, int[] iArr2, int i4, int i5, int i6, int[] iArr3, int i7, int i8, int i9, int i10, int[] iArr4, int i11) {
        int iMkn = mkn(i10);
        int i12 = i5 + 1;
        int iMkn2 = i11 + mkn(i10);
        int iMkn3 = iMkn2 + mkn(i10);
        int i13 = iMkn3 + (iMkn * i12);
        FalconSmallPrimeList falconSmallPrimeList = this.primes;
        FalconSmallPrime[] falconSmallPrimeArr = FalconSmallPrimeList.PRIMES;
        for (int i14 = 0; i14 < i12; i14++) {
            int i15 = falconSmallPrimeArr[i14].p;
            int iModp_ninv31 = modp_ninv31(i15);
            int iModp_R2 = modp_R2(i15, iModp_ninv31);
            int iModp_Rx = modp_Rx(i5, i15, iModp_ninv31, iModp_R2);
            modp_mkgm2(iArr4, i11, iArr4, iMkn2, i10, falconSmallPrimeArr[i14].g, i15, iModp_ninv31);
            for (int i16 = 0; i16 < iMkn; i16++) {
                iArr4[i13 + i16] = modp_set(iArr3[i7 + i16], i15);
            }
            modp_NTT2(iArr4, i13, iArr4, i11, i10, i15, iModp_ninv31);
            int i17 = 0;
            int i18 = i4;
            int i19 = iMkn3;
            int i20 = i14;
            while (true) {
                int i21 = i19 + i20;
                if (i17 >= iMkn) {
                    break;
                }
                iArr4[i21] = zint_mod_small_signed(iArr2, i18, i5, i15, iModp_ninv31, iModp_R2, iModp_Rx);
                i17++;
                i18 += i6;
                i19 = i21;
                i20 = i12;
            }
            modp_NTT2_ext(iArr4, iMkn3 + i14, i12, iArr4, i11, i10, i15, iModp_ninv31);
            int i22 = 0;
            int i23 = iMkn3;
            int i24 = i14;
            while (true) {
                int i25 = i23 + i24;
                if (i22 < iMkn) {
                    iArr4[i25] = modp_montymul(modp_montymul(iArr4[i13 + i22], iArr4[i25], i15, iModp_ninv31), iModp_R2, i15, iModp_ninv31);
                    i22++;
                    i23 = i25;
                    i24 = i12;
                }
            }
            modp_iNTT2_ext(iArr4, iMkn3 + i14, i12, iArr4, iMkn2, i10, i15, iModp_ninv31);
        }
        zint_rebuild_CRT(iArr4, iMkn3, i12, i12, iMkn, falconSmallPrimeArr, 1, iArr4, i13);
        int i26 = 0;
        int i27 = i;
        int i28 = iMkn3;
        while (true) {
            int i29 = i28;
            if (i26 >= iMkn) {
                return;
            }
            zint_sub_scaled(iArr, i27, i2, iArr4, i29, i12, i8, i9);
            i26++;
            i27 += i3;
            i28 = i29 + i12;
        }
    }

    long get_rng_u64(SHAKE256 shake256) {
        byte[] bArr = new byte[8];
        shake256.inner_shake256_extract(bArr, 0, bArr.length);
        return (((long) bArr[0]) & 255) | ((((long) bArr[1]) & 255) << 8) | ((((long) bArr[2]) & 255) << 16) | ((((long) bArr[3]) & 255) << 24) | ((((long) bArr[4]) & 255) << 32) | ((((long) bArr[5]) & 255) << 40) | ((((long) bArr[6]) & 255) << 48) | ((((long) bArr[7]) & 255) << 56);
    }

    int mkgauss(SHAKE256 shake256, int i) {
        int i2 = 1 << (10 - i);
        int i3 = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            long j = get_rng_u64(shake256);
            int i5 = (int) (j >>> 63);
            int i6 = (int) (((j & Long.MAX_VALUE) - this.gauss_1024_12289[0]) >>> 63);
            int i7 = 0;
            long j2 = get_rng_u64(shake256) & Long.MAX_VALUE;
            for (int i8 = 1; i8 < this.gauss_1024_12289.length; i8++) {
                int i9 = ((int) ((j2 - this.gauss_1024_12289[i8]) >>> 63)) ^ 1;
                i7 |= i8 & (-(i9 & (i6 ^ 1)));
                i6 |= i9;
            }
            i3 += (i7 ^ (-i5)) + i5;
        }
        return i3;
    }

    int poly_small_sqnorm(byte[] bArr, int i, int i2) {
        int iMkn = mkn(i2);
        int i3 = 0;
        int i4 = 0;
        for (int i5 = 0; i5 < iMkn; i5++) {
            byte b = bArr[i + i5];
            i3 += b * b;
            i4 |= i3;
        }
        return i3 | (-(i4 >>> 31));
    }

    void poly_small_to_fp(FalconFPR[] falconFPRArr, int i, byte[] bArr, int i2, int i3) {
        int iMkn = mkn(i3);
        for (int i4 = 0; i4 < iMkn; i4++) {
            falconFPRArr[i + i4] = this.fpr.fpr_of(bArr[i2 + i4]);
        }
    }

    void make_fg_step(int[] iArr, int i, int i2, int i3, int i4, int i5) {
        int i6 = 1 << i2;
        int i7 = i6 >> 1;
        int i8 = this.MAX_BL_SMALL[i3];
        int i9 = this.MAX_BL_SMALL[i3 + 1];
        FalconSmallPrimeList falconSmallPrimeList = this.primes;
        FalconSmallPrime[] falconSmallPrimeArr = FalconSmallPrimeList.PRIMES;
        int i10 = i + (i7 * i9);
        int i11 = i10 + (i7 * i9);
        int i12 = i11 + (i6 * i8);
        int i13 = i12 + (i6 * i8);
        int i14 = i13 + i6;
        int i15 = i14 + i6;
        System.arraycopy(iArr, i, iArr, i11, 2 * i6 * i8);
        for (int i16 = 0; i16 < i8; i16++) {
            int i17 = falconSmallPrimeArr[i16].p;
            int iModp_ninv31 = modp_ninv31(i17);
            int iModp_R2 = modp_R2(i17, iModp_ninv31);
            modp_mkgm2(iArr, i13, iArr, i14, i2, falconSmallPrimeArr[i16].g, i17, iModp_ninv31);
            int i18 = 0;
            int i19 = i11;
            int i20 = i16;
            while (true) {
                int i21 = i19 + i20;
                if (i18 >= i6) {
                    break;
                }
                iArr[i15 + i18] = iArr[i21];
                i18++;
                i19 = i21;
                i20 = i8;
            }
            if (i4 == 0) {
                modp_NTT2(iArr, i15, iArr, i13, i2, i17, iModp_ninv31);
            }
            int i22 = 0;
            int i23 = i;
            int i24 = i16;
            while (true) {
                int i25 = i23 + i24;
                if (i22 >= i7) {
                    break;
                }
                iArr[i25] = modp_montymul(modp_montymul(iArr[i15 + (i22 << 1) + 0], iArr[i15 + (i22 << 1) + 1], i17, iModp_ninv31), iModp_R2, i17, iModp_ninv31);
                i22++;
                i23 = i25;
                i24 = i9;
            }
            if (i4 != 0) {
                modp_iNTT2_ext(iArr, i11 + i16, i8, iArr, i14, i2, i17, iModp_ninv31);
            }
            int i26 = 0;
            int i27 = i12;
            int i28 = i16;
            while (true) {
                int i29 = i27 + i28;
                if (i26 >= i6) {
                    break;
                }
                iArr[i15 + i26] = iArr[i29];
                i26++;
                i27 = i29;
                i28 = i8;
            }
            if (i4 == 0) {
                modp_NTT2(iArr, i15, iArr, i13, i2, i17, iModp_ninv31);
            }
            int i30 = 0;
            int i31 = i10;
            int i32 = i16;
            while (true) {
                int i33 = i31 + i32;
                if (i30 >= i7) {
                    break;
                }
                iArr[i33] = modp_montymul(modp_montymul(iArr[i15 + (i30 << 1) + 0], iArr[i15 + (i30 << 1) + 1], i17, iModp_ninv31), iModp_R2, i17, iModp_ninv31);
                i30++;
                i31 = i33;
                i32 = i9;
            }
            if (i4 != 0) {
                modp_iNTT2_ext(iArr, i12 + i16, i8, iArr, i14, i2, i17, iModp_ninv31);
            }
            if (i5 == 0) {
                modp_iNTT2_ext(iArr, i + i16, i9, iArr, i14, i2 - 1, i17, iModp_ninv31);
                modp_iNTT2_ext(iArr, i10 + i16, i9, iArr, i14, i2 - 1, i17, iModp_ninv31);
            }
        }
        zint_rebuild_CRT(iArr, i11, i8, i8, i6, falconSmallPrimeArr, 1, iArr, i13);
        zint_rebuild_CRT(iArr, i12, i8, i8, i6, falconSmallPrimeArr, 1, iArr, i13);
        for (int i34 = i8; i34 < i9; i34++) {
            int i35 = falconSmallPrimeArr[i34].p;
            int iModp_ninv32 = modp_ninv31(i35);
            int iModp_R3 = modp_R2(i35, iModp_ninv32);
            int iModp_Rx = modp_Rx(i8, i35, iModp_ninv32, iModp_R3);
            modp_mkgm2(iArr, i13, iArr, i14, i2, falconSmallPrimeArr[i34].g, i35, iModp_ninv32);
            int i36 = 0;
            int i37 = i11;
            while (true) {
                int i38 = i37;
                if (i36 >= i6) {
                    break;
                }
                iArr[i15 + i36] = zint_mod_small_signed(iArr, i38, i8, i35, iModp_ninv32, iModp_R3, iModp_Rx);
                i36++;
                i37 = i38 + i8;
            }
            modp_NTT2(iArr, i15, iArr, i13, i2, i35, iModp_ninv32);
            int i39 = 0;
            int i40 = i;
            int i41 = i34;
            while (true) {
                int i42 = i40 + i41;
                if (i39 >= i7) {
                    break;
                }
                iArr[i42] = modp_montymul(modp_montymul(iArr[i15 + (i39 << 1) + 0], iArr[i15 + (i39 << 1) + 1], i35, iModp_ninv32), iModp_R3, i35, iModp_ninv32);
                i39++;
                i40 = i42;
                i41 = i9;
            }
            int i43 = 0;
            int i44 = i12;
            while (true) {
                int i45 = i44;
                if (i43 >= i6) {
                    break;
                }
                iArr[i15 + i43] = zint_mod_small_signed(iArr, i45, i8, i35, iModp_ninv32, iModp_R3, iModp_Rx);
                i43++;
                i44 = i45 + i8;
            }
            modp_NTT2(iArr, i15, iArr, i13, i2, i35, iModp_ninv32);
            int i46 = 0;
            int i47 = i10;
            int i48 = i34;
            while (true) {
                int i49 = i47 + i48;
                if (i46 >= i7) {
                    break;
                }
                iArr[i49] = modp_montymul(modp_montymul(iArr[i15 + (i46 << 1) + 0], iArr[i15 + (i46 << 1) + 1], i35, iModp_ninv32), iModp_R3, i35, iModp_ninv32);
                i46++;
                i47 = i49;
                i48 = i9;
            }
            if (i5 == 0) {
                modp_iNTT2_ext(iArr, i + i34, i9, iArr, i14, i2 - 1, i35, iModp_ninv32);
                modp_iNTT2_ext(iArr, i10 + i34, i9, iArr, i14, i2 - 1, i35, iModp_ninv32);
            }
        }
    }

    void make_fg(int[] iArr, int i, byte[] bArr, int i2, byte[] bArr2, int i3, int i4, int i5, int i6) {
        int iMkn = mkn(i4);
        int i7 = i + iMkn;
        FalconSmallPrimeList falconSmallPrimeList = this.primes;
        FalconSmallPrime[] falconSmallPrimeArr = FalconSmallPrimeList.PRIMES;
        int i8 = falconSmallPrimeArr[0].p;
        for (int i9 = 0; i9 < iMkn; i9++) {
            iArr[i + i9] = modp_set(bArr[i2 + i9], i8);
            iArr[i7 + i9] = modp_set(bArr2[i3 + i9], i8);
        }
        if (i5 != 0 || i6 == 0) {
            int i10 = 0;
            while (i10 < i5) {
                make_fg_step(iArr, i, i4 - i10, i10, i10 != 0 ? 1 : 0, (i10 + 1 < i5 || i6 != 0) ? 1 : 0);
                i10++;
            }
            return;
        }
        int i11 = falconSmallPrimeArr[0].p;
        int iModp_ninv31 = modp_ninv31(i11);
        int i12 = i7 + iMkn;
        modp_mkgm2(iArr, i12, iArr, i12 + iMkn, i4, falconSmallPrimeArr[0].g, i11, iModp_ninv31);
        modp_NTT2(iArr, i, iArr, i12, i4, i11, iModp_ninv31);
        modp_NTT2(iArr, i7, iArr, i12, i4, i11, iModp_ninv31);
    }

    int solve_NTRU_deepest(int i, byte[] bArr, int i2, byte[] bArr2, int i3, int[] iArr, int i4) {
        int i5 = this.MAX_BL_SMALL[i];
        FalconSmallPrimeList falconSmallPrimeList = this.primes;
        FalconSmallPrime[] falconSmallPrimeArr = FalconSmallPrimeList.PRIMES;
        int i6 = i4 + i5;
        int i7 = i6 + i5;
        int i8 = i7 + i5;
        int i9 = i8 + i5;
        make_fg(iArr, i7, bArr, i2, bArr2, i3, i, i, 0);
        zint_rebuild_CRT(iArr, i7, i5, i5, 2, falconSmallPrimeArr, 0, iArr, i9);
        return (zint_bezout(iArr, i6, iArr, i4, iArr, i7, iArr, i8, i5, iArr, i9) != 0 && zint_mul_small(iArr, i4, i5, 12289) == 0 && zint_mul_small(iArr, i6, i5, 12289) == 0) ? 1 : 0;
    }

    int solve_NTRU_intermediate(int i, byte[] bArr, int i2, byte[] bArr2, int i3, int i4, int[] iArr, int i5) {
        FalconFPR falconFPRFpr_sqr;
        int i6 = i - i4;
        int i7 = 1 << i6;
        int i8 = i7 >> 1;
        int i9 = this.MAX_BL_SMALL[i4];
        int i10 = this.MAX_BL_SMALL[i4 + 1];
        int i11 = this.MAX_BL_LARGE[i4];
        FalconSmallPrimeList falconSmallPrimeList = this.primes;
        FalconSmallPrime[] falconSmallPrimeArr = FalconSmallPrimeList.PRIMES;
        int i12 = i5 + (i10 * i8) + (i10 * i8);
        make_fg(iArr, i12, bArr, i2, bArr2, i3, i, i4, 1);
        int i13 = i5;
        int i14 = i13 + (i7 * i11);
        int i15 = i14 + (i7 * i11);
        System.arraycopy(iArr, i12, iArr, i15, 2 * i7 * i9);
        int i16 = i15 + (i9 * i7);
        int i17 = i16 + (i9 * i7);
        System.arraycopy(iArr, i5, iArr, i17, 2 * i8 * i10);
        int i18 = i17 + (i8 * i10);
        for (int i19 = 0; i19 < i11; i19++) {
            int i20 = falconSmallPrimeArr[i19].p;
            int iModp_ninv31 = modp_ninv31(i20);
            int iModp_R2 = modp_R2(i20, iModp_ninv31);
            int iModp_Rx = modp_Rx(i10, i20, iModp_ninv31, iModp_R2);
            int i21 = 0;
            int i22 = i17;
            int i23 = i18;
            int i24 = i13 + i19;
            int i25 = i14;
            int i26 = i19;
            while (true) {
                int i27 = i25 + i26;
                if (i21 < i8) {
                    iArr[i24] = zint_mod_small_signed(iArr, i22, i10, i20, iModp_ninv31, iModp_R2, iModp_Rx);
                    iArr[i27] = zint_mod_small_signed(iArr, i23, i10, i20, iModp_ninv31, iModp_R2, iModp_Rx);
                    i21++;
                    i22 += i10;
                    i23 += i10;
                    i24 += i11;
                    i25 = i27;
                    i26 = i11;
                }
            }
        }
        for (int i28 = 0; i28 < i11; i28++) {
            int i29 = falconSmallPrimeArr[i28].p;
            int iModp_ninv32 = modp_ninv31(i29);
            int iModp_R3 = modp_R2(i29, iModp_ninv32);
            if (i28 == i9) {
                zint_rebuild_CRT(iArr, i15, i9, i9, i7, falconSmallPrimeArr, 1, iArr, i17);
                zint_rebuild_CRT(iArr, i16, i9, i9, i7, falconSmallPrimeArr, 1, iArr, i17);
            }
            int i30 = i17 + i7;
            int i31 = i30 + i7;
            int i32 = i31 + i7;
            modp_mkgm2(iArr, i17, iArr, i30, i6, falconSmallPrimeArr[i28].g, i29, iModp_ninv32);
            if (i28 < i9) {
                int i33 = 0;
                int i34 = i15 + i28;
                int i35 = i16;
                int i36 = i28;
                while (true) {
                    int i37 = i35 + i36;
                    if (i33 >= i7) {
                        break;
                    }
                    iArr[i31 + i33] = iArr[i34];
                    iArr[i32 + i33] = iArr[i37];
                    i33++;
                    i34 += i9;
                    i35 = i37;
                    i36 = i9;
                }
                modp_iNTT2_ext(iArr, i15 + i28, i9, iArr, i30, i6, i29, iModp_ninv32);
                modp_iNTT2_ext(iArr, i16 + i28, i9, iArr, i30, i6, i29, iModp_ninv32);
            } else {
                int iModp_Rx2 = modp_Rx(i9, i29, iModp_ninv32, iModp_R3);
                int i38 = 0;
                int i39 = i15;
                int i40 = i16;
                while (true) {
                    int i41 = i40;
                    if (i38 >= i7) {
                        break;
                    }
                    iArr[i31 + i38] = zint_mod_small_signed(iArr, i39, i9, i29, iModp_ninv32, iModp_R3, iModp_Rx2);
                    iArr[i32 + i38] = zint_mod_small_signed(iArr, i41, i9, i29, iModp_ninv32, iModp_R3, iModp_Rx2);
                    i38++;
                    i39 += i9;
                    i40 = i41 + i9;
                }
                modp_NTT2(iArr, i31, iArr, i17, i6, i29, iModp_ninv32);
                modp_NTT2(iArr, i32, iArr, i17, i6, i29, iModp_ninv32);
            }
            int i42 = i32 + i7;
            int i43 = i42 + i8;
            int i44 = 0;
            int i45 = i13 + i28;
            int i46 = i14;
            int i47 = i28;
            while (true) {
                int i48 = i46 + i47;
                if (i44 >= i8) {
                    break;
                }
                iArr[i42 + i44] = iArr[i45];
                iArr[i43 + i44] = iArr[i48];
                i44++;
                i45 += i11;
                i46 = i48;
                i47 = i11;
            }
            modp_NTT2(iArr, i42, iArr, i17, i6 - 1, i29, iModp_ninv32);
            modp_NTT2(iArr, i43, iArr, i17, i6 - 1, i29, iModp_ninv32);
            int i49 = 0;
            int i50 = i13 + i28;
            int i51 = i14;
            int i52 = i28;
            while (true) {
                int i53 = i51 + i52;
                if (i49 < i8) {
                    int i54 = iArr[i31 + (i49 << 1) + 0];
                    int i55 = iArr[i31 + (i49 << 1) + 1];
                    int i56 = iArr[i32 + (i49 << 1) + 0];
                    int i57 = iArr[i32 + (i49 << 1) + 1];
                    int iModp_montymul = modp_montymul(iArr[i42 + i49], iModp_R3, i29, iModp_ninv32);
                    int iModp_montymul2 = modp_montymul(iArr[i43 + i49], iModp_R3, i29, iModp_ninv32);
                    iArr[i50 + 0] = modp_montymul(i57, iModp_montymul, i29, iModp_ninv32);
                    iArr[i50 + i11] = modp_montymul(i56, iModp_montymul, i29, iModp_ninv32);
                    iArr[i53 + 0] = modp_montymul(i55, iModp_montymul2, i29, iModp_ninv32);
                    iArr[i53 + i11] = modp_montymul(i54, iModp_montymul2, i29, iModp_ninv32);
                    i49++;
                    i50 += i11 << 1;
                    i51 = i53;
                    i52 = i11 << 1;
                }
            }
            modp_iNTT2_ext(iArr, i13 + i28, i11, iArr, i30, i6, i29, iModp_ninv32);
            modp_iNTT2_ext(iArr, i14 + i28, i11, iArr, i30, i6, i29, iModp_ninv32);
        }
        zint_rebuild_CRT(iArr, i13, i11, i11, i7, falconSmallPrimeArr, 1, iArr, i17);
        zint_rebuild_CRT(iArr, i14, i11, i11, i7, falconSmallPrimeArr, 1, iArr, i17);
        FalconFPR[] falconFPRArr = new FalconFPR[i7];
        FalconFPR[] falconFPRArr2 = new FalconFPR[i7];
        FalconFPR[] falconFPRArr3 = new FalconFPR[i7];
        FalconFPR[] falconFPRArr4 = new FalconFPR[i7];
        FalconFPR[] falconFPRArr5 = new FalconFPR[i7 >> 1];
        int[] iArr2 = new int[i7];
        int i58 = i9 > 10 ? 10 : i9;
        poly_big_to_fp(falconFPRArr3, 0, iArr, (i15 + i9) - i58, i58, i9, i6);
        poly_big_to_fp(falconFPRArr4, 0, iArr, (i16 + i9) - i58, i58, i9, i6);
        int i59 = 31 * (i9 - i58);
        int i60 = this.bitlength_avg[i4] - (6 * this.bitlength_std[i4]);
        int i61 = this.bitlength_avg[i4] + (6 * this.bitlength_std[i4]);
        this.fft.FFT(falconFPRArr3, 0, i6);
        this.fft.FFT(falconFPRArr4, 0, i6);
        this.fft.poly_invnorm2_fft(falconFPRArr5, 0, falconFPRArr3, 0, falconFPRArr4, 0, i6);
        this.fft.poly_adj_fft(falconFPRArr3, 0, i6);
        this.fft.poly_adj_fft(falconFPRArr4, 0, i6);
        int i62 = i11;
        int i63 = 31 * i11;
        int i64 = i63 - i60;
        while (true) {
            int i65 = i62 > 10 ? 10 : i62;
            poly_big_to_fp(falconFPRArr, 0, iArr, (i13 + i62) - i65, i65, i11, i6);
            poly_big_to_fp(falconFPRArr2, 0, iArr, (i14 + i62) - i65, i65, i11, i6);
            this.fft.FFT(falconFPRArr, 0, i6);
            this.fft.FFT(falconFPRArr2, 0, i6);
            this.fft.poly_mul_fft(falconFPRArr, 0, falconFPRArr3, 0, i6);
            this.fft.poly_mul_fft(falconFPRArr2, 0, falconFPRArr4, 0, i6);
            this.fft.poly_add(falconFPRArr2, 0, falconFPRArr, 0, i6);
            this.fft.poly_mul_autoadj_fft(falconFPRArr2, 0, falconFPRArr5, 0, i6);
            this.fft.iFFT(falconFPRArr2, 0, i6);
            int i66 = (i64 - (31 * (i62 - i65))) + i59;
            if (i66 < 0) {
                i66 = -i66;
                falconFPRFpr_sqr = this.fpr.fpr_two;
            } else {
                falconFPRFpr_sqr = this.fpr.fpr_onehalf;
            }
            FalconFPR falconFPRFpr_mul = this.fpr.fpr_one;
            while (i66 != 0) {
                if ((i66 & 1) != 0) {
                    falconFPRFpr_mul = this.fpr.fpr_mul(falconFPRFpr_mul, falconFPRFpr_sqr);
                }
                i66 >>= 1;
                falconFPRFpr_sqr = this.fpr.fpr_sqr(falconFPRFpr_sqr);
            }
            for (int i67 = 0; i67 < i7; i67++) {
                FalconFPR falconFPRFpr_mul2 = this.fpr.fpr_mul(falconFPRArr2[i67], falconFPRFpr_mul);
                if (!this.fpr.fpr_lt(this.fpr.fpr_mtwo31m1, falconFPRFpr_mul2) || !this.fpr.fpr_lt(falconFPRFpr_mul2, this.fpr.fpr_ptwo31m1)) {
                    return 0;
                }
                iArr2[i67] = (int) this.fpr.fpr_rint(falconFPRFpr_mul2);
            }
            int i68 = i64 / 31;
            int i69 = i64 % 31;
            if (i4 <= 4) {
                poly_sub_scaled_ntt(iArr, i13, i62, i11, iArr, i15, i9, i9, iArr2, 0, i68, i69, i6, iArr, i17);
                poly_sub_scaled_ntt(iArr, i14, i62, i11, iArr, i16, i9, i9, iArr2, 0, i68, i69, i6, iArr, i17);
            } else {
                poly_sub_scaled(iArr, i13, i62, i11, iArr, i15, i9, i9, iArr2, 0, i68, i69, i6);
                poly_sub_scaled(iArr, i14, i62, i11, iArr, i16, i9, i9, iArr2, 0, i68, i69, i6);
            }
            int i70 = i64 + i61 + 10;
            if (i70 < i63) {
                i63 = i70;
                if (i62 * 31 >= i63 + 31) {
                    i62--;
                }
            }
            if (i64 <= 0) {
                if (i62 < i9) {
                    int i71 = 0;
                    while (i71 < i7) {
                        int i72 = (-(iArr[(i13 + i62) - 1] >>> 30)) >>> 1;
                        for (int i73 = i62; i73 < i9; i73++) {
                            iArr[i13 + i73] = i72;
                        }
                        int i74 = (-(iArr[(i14 + i62) - 1] >>> 30)) >>> 1;
                        for (int i75 = i62; i75 < i9; i75++) {
                            iArr[i14 + i75] = i74;
                        }
                        i71++;
                        i13 += i11;
                        i14 += i11;
                    }
                }
                int i76 = 0;
                int i77 = i5;
                int i78 = i5;
                while (true) {
                    int i79 = i78;
                    if (i76 >= (i7 << 1)) {
                        return 1;
                    }
                    System.arraycopy(iArr, i79, iArr, i77, i9);
                    i76++;
                    i77 += i9;
                    i78 = i79 + i11;
                }
            } else {
                i64 -= 25;
                if (i64 < 0) {
                    i64 = 0;
                }
            }
        }
    }

    int solve_NTRU_binary_depth1(int i, byte[] bArr, int i2, byte[] bArr2, int i3, int[] iArr, int i4) {
        int i5 = 1 << i;
        int i6 = i - 1;
        int i7 = 1 << i6;
        int i8 = i7 >> 1;
        int i9 = this.MAX_BL_SMALL[1];
        int i10 = this.MAX_BL_SMALL[1 + 1];
        int i11 = this.MAX_BL_LARGE[1];
        int i12 = i4 + (i10 * i8);
        int i13 = i12 + (i10 * i8);
        int i14 = i13 + (i11 * i7);
        for (int i15 = 0; i15 < i11; i15++) {
            FalconSmallPrimeList falconSmallPrimeList = this.primes;
            int i16 = FalconSmallPrimeList.PRIMES[i15].p;
            int iModp_ninv31 = modp_ninv31(i16);
            int iModp_R2 = modp_R2(i16, iModp_ninv31);
            int iModp_Rx = modp_Rx(i10, i16, iModp_ninv31, iModp_R2);
            int i17 = 0;
            int i18 = i4;
            int i19 = i12;
            int i20 = i13 + i15;
            int i21 = i14;
            int i22 = i15;
            while (true) {
                int i23 = i21 + i22;
                if (i17 < i8) {
                    iArr[i20] = zint_mod_small_signed(iArr, i18, i10, i16, iModp_ninv31, iModp_R2, iModp_Rx);
                    iArr[i23] = zint_mod_small_signed(iArr, i19, i10, i16, iModp_ninv31, iModp_R2, iModp_Rx);
                    i17++;
                    i18 += i10;
                    i19 += i10;
                    i20 += i11;
                    i21 = i23;
                    i22 = i11;
                }
            }
        }
        System.arraycopy(iArr, i13, iArr, i4, i11 * i7);
        System.arraycopy(iArr, i14, iArr, i4 + (i11 * i7), i11 * i7);
        int i24 = i4 + (i11 * i7);
        int i25 = i24 + (i11 * i7);
        int i26 = i25 + (i9 * i7);
        int i27 = i26 + (i9 * i7);
        for (int i28 = 0; i28 < i11; i28++) {
            FalconSmallPrimeList falconSmallPrimeList2 = this.primes;
            int i29 = FalconSmallPrimeList.PRIMES[i28].p;
            int iModp_ninv32 = modp_ninv31(i29);
            int iModp_R3 = modp_R2(i29, iModp_ninv32);
            int i30 = i27 + i5;
            int i31 = i30 + i7;
            int i32 = i31 + i5;
            FalconSmallPrimeList falconSmallPrimeList3 = this.primes;
            modp_mkgm2(iArr, i27, iArr, i30, i, FalconSmallPrimeList.PRIMES[i28].g, i29, iModp_ninv32);
            for (int i33 = 0; i33 < i5; i33++) {
                iArr[i31 + i33] = modp_set(bArr[i2 + i33], i29);
                iArr[i32 + i33] = modp_set(bArr2[i3 + i33], i29);
            }
            modp_NTT2(iArr, i31, iArr, i27, i, i29, iModp_ninv32);
            modp_NTT2(iArr, i32, iArr, i27, i, i29, iModp_ninv32);
            for (int i34 = i; i34 > i6; i34--) {
                modp_poly_rec_res(iArr, i31, i34, i29, iModp_ninv32, iModp_R3);
                modp_poly_rec_res(iArr, i32, i34, i29, iModp_ninv32, iModp_R3);
            }
            if (1 > 0) {
                System.arraycopy(iArr, i30, iArr, i27 + i7, i7);
                i30 = i27 + i7;
                System.arraycopy(iArr, i31, iArr, i30 + i7, i7);
                i31 = i30 + i7;
                System.arraycopy(iArr, i32, iArr, i31 + i7, i7);
                i32 = i31 + i7;
            }
            int i35 = i32 + i7;
            int i36 = i35 + i8;
            int i37 = 0;
            int i38 = i4 + i28;
            int i39 = i24;
            int i40 = i28;
            while (true) {
                int i41 = i39 + i40;
                if (i37 >= i8) {
                    break;
                }
                iArr[i35 + i37] = iArr[i38];
                iArr[i36 + i37] = iArr[i41];
                i37++;
                i38 += i11;
                i39 = i41;
                i40 = i11;
            }
            modp_NTT2(iArr, i35, iArr, i27, i6 - 1, i29, iModp_ninv32);
            modp_NTT2(iArr, i36, iArr, i27, i6 - 1, i29, iModp_ninv32);
            int i42 = 0;
            int i43 = i4 + i28;
            int i44 = i24;
            int i45 = i28;
            while (true) {
                int i46 = i44 + i45;
                if (i42 >= i8) {
                    break;
                }
                int i47 = iArr[i31 + (i42 << 1) + 0];
                int i48 = iArr[i31 + (i42 << 1) + 1];
                int i49 = iArr[i32 + (i42 << 1) + 0];
                int i50 = iArr[i32 + (i42 << 1) + 1];
                int iModp_montymul = modp_montymul(iArr[i35 + i42], iModp_R3, i29, iModp_ninv32);
                int iModp_montymul2 = modp_montymul(iArr[i36 + i42], iModp_R3, i29, iModp_ninv32);
                iArr[i43 + 0] = modp_montymul(i50, iModp_montymul, i29, iModp_ninv32);
                iArr[i43 + i11] = modp_montymul(i49, iModp_montymul, i29, iModp_ninv32);
                iArr[i46 + 0] = modp_montymul(i48, iModp_montymul2, i29, iModp_ninv32);
                iArr[i46 + i11] = modp_montymul(i47, iModp_montymul2, i29, iModp_ninv32);
                i42++;
                i43 += i11 << 1;
                i44 = i46;
                i45 = i11 << 1;
            }
            modp_iNTT2_ext(iArr, i4 + i28, i11, iArr, i30, i6, i29, iModp_ninv32);
            modp_iNTT2_ext(iArr, i24 + i28, i11, iArr, i30, i6, i29, iModp_ninv32);
            if (i28 < i9) {
                modp_iNTT2(iArr, i31, iArr, i30, i6, i29, iModp_ninv32);
                modp_iNTT2(iArr, i32, iArr, i30, i6, i29, iModp_ninv32);
                int i51 = 0;
                int i52 = i25 + i28;
                int i53 = i26;
                int i54 = i28;
                while (true) {
                    int i55 = i53 + i54;
                    if (i51 < i7) {
                        iArr[i52] = iArr[i31 + i51];
                        iArr[i55] = iArr[i32 + i51];
                        i51++;
                        i52 += i9;
                        i53 = i55;
                        i54 = i9;
                    }
                }
            }
        }
        FalconSmallPrimeList falconSmallPrimeList4 = this.primes;
        zint_rebuild_CRT(iArr, i4, i11, i11, i7 << 1, FalconSmallPrimeList.PRIMES, 1, iArr, i27);
        FalconSmallPrimeList falconSmallPrimeList5 = this.primes;
        zint_rebuild_CRT(iArr, i25, i9, i9, i7 << 1, FalconSmallPrimeList.PRIMES, 1, iArr, i27);
        FalconFPR[] falconFPRArr = new FalconFPR[i7];
        FalconFPR[] falconFPRArr2 = new FalconFPR[i7];
        poly_big_to_fp(falconFPRArr, 0, iArr, i4, i11, i11, i6);
        poly_big_to_fp(falconFPRArr2, 0, iArr, i24, i11, i11, i6);
        System.arraycopy(iArr, i25, iArr, i4, 2 * i9 * i7);
        int i56 = i4 + (i9 * i7);
        FalconFPR[] falconFPRArr3 = new FalconFPR[i7];
        FalconFPR[] falconFPRArr4 = new FalconFPR[i7];
        poly_big_to_fp(falconFPRArr3, 0, iArr, i4, i9, i9, i6);
        poly_big_to_fp(falconFPRArr4, 0, iArr, i56, i9, i9, i6);
        this.fft.FFT(falconFPRArr, 0, i6);
        this.fft.FFT(falconFPRArr2, 0, i6);
        this.fft.FFT(falconFPRArr3, 0, i6);
        this.fft.FFT(falconFPRArr4, 0, i6);
        FalconFPR[] falconFPRArr5 = new FalconFPR[i7];
        FalconFPR[] falconFPRArr6 = new FalconFPR[i7 >> 1];
        this.fft.poly_add_muladj_fft(falconFPRArr5, 0, falconFPRArr, 0, falconFPRArr2, 0, falconFPRArr3, 0, falconFPRArr4, 0, i6);
        this.fft.poly_invnorm2_fft(falconFPRArr6, 0, falconFPRArr3, 0, falconFPRArr4, 0, i6);
        this.fft.poly_mul_autoadj_fft(falconFPRArr5, 0, falconFPRArr6, 0, i6);
        this.fft.iFFT(falconFPRArr5, 0, i6);
        for (int i57 = 0; i57 < i7; i57++) {
            FalconFPR falconFPR = falconFPRArr5[i57];
            if (!this.fpr.fpr_lt(falconFPR, this.fpr.fpr_ptwo63m1) || !this.fpr.fpr_lt(this.fpr.fpr_mtwo63m1, falconFPR)) {
                return 0;
            }
            falconFPRArr5[i57] = this.fpr.fpr_of(this.fpr.fpr_rint(falconFPR));
        }
        this.fft.FFT(falconFPRArr5, 0, i6);
        this.fft.poly_mul_fft(falconFPRArr3, 0, falconFPRArr5, 0, i6);
        this.fft.poly_mul_fft(falconFPRArr4, 0, falconFPRArr5, 0, i6);
        this.fft.poly_sub(falconFPRArr, 0, falconFPRArr3, 0, i6);
        this.fft.poly_sub(falconFPRArr2, 0, falconFPRArr4, 0, i6);
        this.fft.iFFT(falconFPRArr, 0, i6);
        this.fft.iFFT(falconFPRArr2, 0, i6);
        int i58 = i4 + i7;
        for (int i59 = 0; i59 < i7; i59++) {
            iArr[i4 + i59] = (int) this.fpr.fpr_rint(falconFPRArr[i59]);
            iArr[i58 + i59] = (int) this.fpr.fpr_rint(falconFPRArr2[i59]);
        }
        return 1;
    }

    int solve_NTRU_binary_depth0(int i, byte[] bArr, int i2, byte[] bArr2, int i3, int[] iArr, int i4) {
        int i5 = 1 << i;
        int i6 = i5 >> 1;
        FalconSmallPrimeList falconSmallPrimeList = this.primes;
        int i7 = FalconSmallPrimeList.PRIMES[0].p;
        int iModp_ninv31 = modp_ninv31(i7);
        int iModp_R2 = modp_R2(i7, iModp_ninv31);
        int i8 = i4 + i6;
        int i9 = i8 + i6;
        int i10 = i9 + i5;
        int i11 = i10 + i5;
        int i12 = i11 + i5;
        FalconSmallPrimeList falconSmallPrimeList2 = this.primes;
        modp_mkgm2(iArr, i11, iArr, i12, i, FalconSmallPrimeList.PRIMES[0].g, i7, iModp_ninv31);
        for (int i13 = 0; i13 < i6; i13++) {
            iArr[i4 + i13] = modp_set(zint_one_to_plain(iArr, i4 + i13), i7);
            iArr[i8 + i13] = modp_set(zint_one_to_plain(iArr, i8 + i13), i7);
        }
        modp_NTT2(iArr, i4, iArr, i11, i - 1, i7, iModp_ninv31);
        modp_NTT2(iArr, i8, iArr, i11, i - 1, i7, iModp_ninv31);
        for (int i14 = 0; i14 < i5; i14++) {
            iArr[i9 + i14] = modp_set(bArr[i2 + i14], i7);
            iArr[i10 + i14] = modp_set(bArr2[i3 + i14], i7);
        }
        modp_NTT2(iArr, i9, iArr, i11, i, i7, iModp_ninv31);
        modp_NTT2(iArr, i10, iArr, i11, i, i7, iModp_ninv31);
        for (int i15 = 0; i15 < i5; i15 += 2) {
            int i16 = iArr[i9 + i15 + 0];
            int i17 = iArr[i9 + i15 + 1];
            int i18 = iArr[i10 + i15 + 0];
            int i19 = iArr[i10 + i15 + 1];
            int iModp_montymul = modp_montymul(iArr[i4 + (i15 >> 1)], iModp_R2, i7, iModp_ninv31);
            int iModp_montymul2 = modp_montymul(iArr[i8 + (i15 >> 1)], iModp_R2, i7, iModp_ninv31);
            iArr[i9 + i15 + 0] = modp_montymul(i19, iModp_montymul, i7, iModp_ninv31);
            iArr[i9 + i15 + 1] = modp_montymul(i18, iModp_montymul, i7, iModp_ninv31);
            iArr[i10 + i15 + 0] = modp_montymul(i17, iModp_montymul2, i7, iModp_ninv31);
            iArr[i10 + i15 + 1] = modp_montymul(i16, iModp_montymul2, i7, iModp_ninv31);
        }
        modp_iNTT2(iArr, i9, iArr, i12, i, i7, iModp_ninv31);
        modp_iNTT2(iArr, i10, iArr, i12, i, i7, iModp_ninv31);
        int i20 = i4 + i5;
        int i21 = i20 + i5;
        System.arraycopy(iArr, i9, iArr, i4, 2 * i5);
        int i22 = i21 + i5;
        int i23 = i22 + i5;
        int i24 = i23 + i5;
        int i25 = i24 + i5;
        FalconSmallPrimeList falconSmallPrimeList3 = this.primes;
        modp_mkgm2(iArr, i21, iArr, i22, i, FalconSmallPrimeList.PRIMES[0].g, i7, iModp_ninv31);
        modp_NTT2(iArr, i4, iArr, i21, i, i7, iModp_ninv31);
        modp_NTT2(iArr, i20, iArr, i21, i, i7, iModp_ninv31);
        int iModp_set = modp_set(bArr[i2 + 0], i7);
        iArr[i25 + 0] = iModp_set;
        iArr[i24 + 0] = iModp_set;
        for (int i26 = 1; i26 < i5; i26++) {
            iArr[i24 + i26] = modp_set(bArr[i2 + i26], i7);
            iArr[(i25 + i5) - i26] = modp_set(-bArr[i2 + i26], i7);
        }
        modp_NTT2(iArr, i24, iArr, i21, i, i7, iModp_ninv31);
        modp_NTT2(iArr, i25, iArr, i21, i, i7, iModp_ninv31);
        for (int i27 = 0; i27 < i5; i27++) {
            int iModp_montymul3 = modp_montymul(iArr[i25 + i27], iModp_R2, i7, iModp_ninv31);
            iArr[i22 + i27] = modp_montymul(iModp_montymul3, iArr[i4 + i27], i7, iModp_ninv31);
            iArr[i23 + i27] = modp_montymul(iModp_montymul3, iArr[i24 + i27], i7, iModp_ninv31);
        }
        int iModp_set2 = modp_set(bArr2[i3 + 0], i7);
        iArr[i25 + 0] = iModp_set2;
        iArr[i24 + 0] = iModp_set2;
        for (int i28 = 1; i28 < i5; i28++) {
            iArr[i24 + i28] = modp_set(bArr2[i3 + i28], i7);
            iArr[(i25 + i5) - i28] = modp_set(-bArr2[i3 + i28], i7);
        }
        modp_NTT2(iArr, i24, iArr, i21, i, i7, iModp_ninv31);
        modp_NTT2(iArr, i25, iArr, i21, i, i7, iModp_ninv31);
        for (int i29 = 0; i29 < i5; i29++) {
            int iModp_montymul4 = modp_montymul(iArr[i25 + i29], iModp_R2, i7, iModp_ninv31);
            iArr[i22 + i29] = modp_add(iArr[i22 + i29], modp_montymul(iModp_montymul4, iArr[i20 + i29], i7, iModp_ninv31), i7);
            iArr[i23 + i29] = modp_add(iArr[i23 + i29], modp_montymul(iModp_montymul4, iArr[i24 + i29], i7, iModp_ninv31), i7);
        }
        FalconSmallPrimeList falconSmallPrimeList4 = this.primes;
        modp_mkgm2(iArr, i21, iArr, i24, i, FalconSmallPrimeList.PRIMES[0].g, i7, iModp_ninv31);
        modp_iNTT2(iArr, i22, iArr, i24, i, i7, iModp_ninv31);
        modp_iNTT2(iArr, i23, iArr, i24, i, i7, iModp_ninv31);
        for (int i30 = 0; i30 < i5; i30++) {
            iArr[i21 + i30] = modp_norm(iArr[i22 + i30], i7);
            iArr[i22 + i30] = modp_norm(iArr[i23 + i30], i7);
        }
        FalconFPR[] falconFPRArr = new FalconFPR[3 * i5];
        int i31 = 0 + i5;
        int i32 = i31 + i5;
        for (int i33 = 0; i33 < i5; i33++) {
            falconFPRArr[i32 + i33] = this.fpr.fpr_of(iArr[i22 + i33]);
        }
        this.fft.FFT(falconFPRArr, i32, i);
        System.arraycopy(falconFPRArr, i32, falconFPRArr, i31, i6);
        int i34 = i31 + i6;
        for (int i35 = 0; i35 < i5; i35++) {
            falconFPRArr[i34 + i35] = this.fpr.fpr_of(iArr[i21 + i35]);
        }
        this.fft.FFT(falconFPRArr, i34, i);
        this.fft.poly_div_autoadj_fft(falconFPRArr, i34, falconFPRArr, i31, i);
        this.fft.iFFT(falconFPRArr, i34, i);
        for (int i36 = 0; i36 < i5; i36++) {
            iArr[i21 + i36] = modp_set((int) this.fpr.fpr_rint(falconFPRArr[i34 + i36]), i7);
        }
        int i37 = i21 + i5;
        int i38 = i37 + i5;
        int i39 = i38 + i5;
        int i40 = i39 + i5;
        FalconSmallPrimeList falconSmallPrimeList5 = this.primes;
        modp_mkgm2(iArr, i37, iArr, i38, i, FalconSmallPrimeList.PRIMES[0].g, i7, iModp_ninv31);
        for (int i41 = 0; i41 < i5; i41++) {
            iArr[i39 + i41] = modp_set(bArr[i2 + i41], i7);
            iArr[i40 + i41] = modp_set(bArr2[i3 + i41], i7);
        }
        modp_NTT2(iArr, i21, iArr, i37, i, i7, iModp_ninv31);
        modp_NTT2(iArr, i39, iArr, i37, i, i7, iModp_ninv31);
        modp_NTT2(iArr, i40, iArr, i37, i, i7, iModp_ninv31);
        for (int i42 = 0; i42 < i5; i42++) {
            int iModp_montymul5 = modp_montymul(iArr[i21 + i42], iModp_R2, i7, iModp_ninv31);
            iArr[i4 + i42] = modp_sub(iArr[i4 + i42], modp_montymul(iModp_montymul5, iArr[i39 + i42], i7, iModp_ninv31), i7);
            iArr[i20 + i42] = modp_sub(iArr[i20 + i42], modp_montymul(iModp_montymul5, iArr[i40 + i42], i7, iModp_ninv31), i7);
        }
        modp_iNTT2(iArr, i4, iArr, i38, i, i7, iModp_ninv31);
        modp_iNTT2(iArr, i20, iArr, i38, i, i7, iModp_ninv31);
        for (int i43 = 0; i43 < i5; i43++) {
            iArr[i4 + i43] = modp_norm(iArr[i4 + i43], i7);
            iArr[i20 + i43] = modp_norm(iArr[i20 + i43], i7);
        }
        return 1;
    }

    int solve_NTRU(int i, byte[] bArr, int i2, byte[] bArr2, int i3, byte[] bArr3, int i4, byte[] bArr4, int i5, int i6, int[] iArr, int i7) {
        int iMkn = mkn(i);
        if (solve_NTRU_deepest(i, bArr3, i4, bArr4, i5, iArr, i7) == 0) {
            return 0;
        }
        if (i <= 2) {
            int i8 = i;
            do {
                int i9 = i8;
                i8--;
                if (i9 > 0) {
                }
            } while (solve_NTRU_intermediate(i, bArr3, i4, bArr4, i5, i8, iArr, i7) != 0);
            return 0;
        }
        int i10 = i;
        do {
            int i11 = i10;
            i10--;
            if (i11 <= 2) {
                if (solve_NTRU_binary_depth1(i, bArr3, i4, bArr4, i5, iArr, i7) == 0 || solve_NTRU_binary_depth0(i, bArr3, i4, bArr4, i5, iArr, i7) == 0) {
                    return 0;
                }
            }
        } while (solve_NTRU_intermediate(i, bArr3, i4, bArr4, i5, i10, iArr, i7) != 0);
        return 0;
        if (bArr2 == null) {
            i3 = 0;
            bArr2 = new byte[iMkn];
        }
        if (poly_big_to_small(bArr, i2, iArr, i7, i6, i) == 0 || poly_big_to_small(bArr2, i3, iArr, i7 + iMkn, i6, i) == 0) {
            return 0;
        }
        int i12 = i7 + iMkn;
        int i13 = i12 + iMkn;
        int i14 = i13 + iMkn;
        int i15 = i14 + iMkn;
        FalconSmallPrimeList falconSmallPrimeList = this.primes;
        FalconSmallPrime[] falconSmallPrimeArr = FalconSmallPrimeList.PRIMES;
        int i16 = falconSmallPrimeArr[0].p;
        int iModp_ninv31 = modp_ninv31(i16);
        modp_mkgm2(iArr, i15, iArr, i7, i, falconSmallPrimeArr[0].g, i16, iModp_ninv31);
        for (int i17 = 0; i17 < iMkn; i17++) {
            iArr[i7 + i17] = modp_set(bArr2[i3 + i17], i16);
        }
        for (int i18 = 0; i18 < iMkn; i18++) {
            iArr[i12 + i18] = modp_set(bArr3[i4 + i18], i16);
            iArr[i13 + i18] = modp_set(bArr4[i5 + i18], i16);
            iArr[i14 + i18] = modp_set(bArr[i2 + i18], i16);
        }
        modp_NTT2(iArr, i12, iArr, i15, i, i16, iModp_ninv31);
        modp_NTT2(iArr, i13, iArr, i15, i, i16, iModp_ninv31);
        modp_NTT2(iArr, i14, iArr, i15, i, i16, iModp_ninv31);
        modp_NTT2(iArr, i7, iArr, i15, i, i16, iModp_ninv31);
        int iModp_montymul = modp_montymul(12289, 1, i16, iModp_ninv31);
        for (int i19 = 0; i19 < iMkn; i19++) {
            if (modp_sub(modp_montymul(iArr[i12 + i19], iArr[i7 + i19], i16, iModp_ninv31), modp_montymul(iArr[i13 + i19], iArr[i14 + i19], i16, iModp_ninv31), i16) != iModp_montymul) {
                return 0;
            }
        }
        return 1;
    }

    void poly_small_mkgauss(SHAKE256 shake256, byte[] bArr, int i, int i2) {
        int iMkgauss;
        int iMkn = mkn(i2);
        int i3 = 0;
        for (int i4 = 0; i4 < iMkn; i4++) {
            while (true) {
                iMkgauss = mkgauss(shake256, i2);
                if (iMkgauss >= -127 && iMkgauss <= 127) {
                    if (i4 == iMkn - 1) {
                        if ((i3 ^ (iMkgauss & 1)) != 0) {
                            break;
                        }
                    } else {
                        i3 ^= iMkgauss & 1;
                        break;
                    }
                }
            }
            bArr[i + i4] = (byte) iMkgauss;
        }
    }

    void keygen(SHAKE256 shake256, byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, int i3, byte[] bArr4, int i4, short[] sArr, int i5, int i6) {
        int i7;
        int i8;
        int iMkn = mkn(i6);
        while (true) {
            FalconFPR[] falconFPRArr = new FalconFPR[3 * iMkn];
            poly_small_mkgauss(shake256, bArr, i, i6);
            poly_small_mkgauss(shake256, bArr2, i2, i6);
            int i9 = 1 << (this.codec.max_fg_bits[i6] - 1);
            for (int i10 = 0; i10 < iMkn; i10++) {
                if (bArr[i + i10] >= i9 || bArr[i + i10] <= (-i9) || bArr2[i2 + i10] >= i9 || bArr2[i2 + i10] <= (-i9)) {
                    i9 = -1;
                    break;
                }
            }
            if (i9 >= 0) {
                int iPoly_small_sqnorm = poly_small_sqnorm(bArr, i, i6);
                int iPoly_small_sqnorm2 = poly_small_sqnorm(bArr2, i2, i6);
                if ((((long) ((iPoly_small_sqnorm + iPoly_small_sqnorm2) | (-((iPoly_small_sqnorm | iPoly_small_sqnorm2) >>> 31)))) & 4294967295L) >= 16823) {
                    continue;
                } else {
                    int i11 = 0 + iMkn;
                    int i12 = i11 + iMkn;
                    poly_small_to_fp(falconFPRArr, 0, bArr, i, i6);
                    poly_small_to_fp(falconFPRArr, i11, bArr2, i2, i6);
                    this.fft.FFT(falconFPRArr, 0, i6);
                    this.fft.FFT(falconFPRArr, i11, i6);
                    this.fft.poly_invnorm2_fft(falconFPRArr, i12, falconFPRArr, 0, falconFPRArr, i11, i6);
                    this.fft.poly_adj_fft(falconFPRArr, 0, i6);
                    this.fft.poly_adj_fft(falconFPRArr, i11, i6);
                    this.fft.poly_mulconst(falconFPRArr, 0, this.fpr.fpr_q, i6);
                    this.fft.poly_mulconst(falconFPRArr, i11, this.fpr.fpr_q, i6);
                    this.fft.poly_mul_autoadj_fft(falconFPRArr, 0, falconFPRArr, i12, i6);
                    this.fft.poly_mul_autoadj_fft(falconFPRArr, i11, falconFPRArr, i12, i6);
                    this.fft.iFFT(falconFPRArr, 0, i6);
                    this.fft.iFFT(falconFPRArr, i11, i6);
                    FalconFPR falconFPRFpr_add = this.fpr.fpr_zero;
                    for (int i13 = 0; i13 < iMkn; i13++) {
                        falconFPRFpr_add = this.fpr.fpr_add(this.fpr.fpr_add(falconFPRFpr_add, this.fpr.fpr_sqr(falconFPRArr[0 + i13])), this.fpr.fpr_sqr(falconFPRArr[i11 + i13]));
                    }
                    if (this.fpr.fpr_lt(falconFPRFpr_add, this.fpr.fpr_bnorm_max)) {
                        short[] sArr2 = new short[2 * iMkn];
                        if (sArr == null) {
                            i7 = 0;
                            sArr = sArr2;
                            i8 = 0 + iMkn;
                        } else {
                            i7 = i5;
                            i8 = 0;
                        }
                        if (this.vrfy.compute_public(sArr, i7, bArr, i, bArr2, i2, i6, sArr2, i8) != 0) {
                            if (solve_NTRU(i6, bArr3, i3, bArr4, i4, bArr, i, bArr2, i2, (1 << (this.codec.max_FG_bits[i6] - 1)) - 1, i6 > 2 ? new int[28 * iMkn] : new int[28 * iMkn * 3], 0) != 0) {
                                return;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
    }

    private long toUnsignedLong(int i) {
        return ((long) i) & 4294967295L;
    }
}
