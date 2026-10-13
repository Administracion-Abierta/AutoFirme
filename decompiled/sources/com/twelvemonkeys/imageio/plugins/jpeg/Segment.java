package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import com.twelvemonkeys.lang.Validate;
import java.io.DataInput;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/Segment.class */
abstract class Segment {
    final int marker;

    protected Segment(int i) {
        this.marker = ((Integer) Validate.isTrue((i >> 8) == 255, Integer.valueOf(i), "Unknown JPEG marker: 0x%04x")).intValue();
    }

    static Segment read(int i, String str, int i2, DataInput dataInput) throws IOException {
        switch (i) {
            case JPEG.SOF0 /* 65472 */:
            case JPEG.SOF1 /* 65473 */:
            case JPEG.SOF2 /* 65474 */:
            case JPEG.SOF3 /* 65475 */:
            case JPEG.SOF5 /* 65477 */:
            case JPEG.SOF6 /* 65478 */:
            case JPEG.SOF7 /* 65479 */:
            case JPEG.SOF9 /* 65481 */:
            case JPEG.SOF10 /* 65482 */:
            case JPEG.SOF11 /* 65483 */:
            case JPEG.SOF13 /* 65485 */:
            case JPEG.SOF14 /* 65486 */:
            case JPEG.SOF15 /* 65487 */:
                return Frame.read(i, dataInput, i2);
            case JPEG.DHT /* 65476 */:
                return HuffmanTable.read(dataInput, i2);
            case 65480:
            case JPEG.DAC /* 65484 */:
            case 65488:
            case 65489:
            case 65490:
            case 65491:
            case 65492:
            case 65493:
            case 65494:
            case 65495:
            case JPEG.SOI /* 65496 */:
            case JPEG.EOI /* 65497 */:
            case JPEG.DNL /* 65500 */:
            case JPEG.DHP /* 65502 */:
            case JPEG.EXP /* 65503 */:
            case 65520:
            case 65521:
            case 65522:
            case 65523:
            case 65524:
            case 65525:
            case 65526:
            case JPEG.SOF55 /* 65527 */:
            case JPEG.LSE /* 65528 */:
            case 65529:
            case 65530:
            case 65531:
            case 65532:
            case 65533:
            default:
                return Unknown.read(i, i2, dataInput);
            case JPEG.SOS /* 65498 */:
                return Scan.read(dataInput, i2);
            case JPEG.DQT /* 65499 */:
                return QuantizationTable.read(dataInput, i2);
            case JPEG.DRI /* 65501 */:
                return RestartInterval.read(dataInput, i2);
            case JPEG.APP0 /* 65504 */:
            case 65505:
            case 65506:
            case 65507:
            case 65508:
            case 65509:
            case 65510:
            case 65511:
            case 65512:
            case 65513:
            case 65514:
            case JPEG.APP11 /* 65515 */:
            case JPEG.APP12 /* 65516 */:
            case JPEG.APP13 /* 65517 */:
            case JPEG.APP14 /* 65518 */:
            case JPEG.APP15 /* 65519 */:
                return Application.read(i, str, dataInput, i2);
            case JPEG.COM /* 65534 */:
                return Comment.read(dataInput, i2);
        }
    }
}
