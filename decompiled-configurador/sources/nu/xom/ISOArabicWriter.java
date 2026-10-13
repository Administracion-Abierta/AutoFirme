package nu.xom;

import java.io.Writer;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/ISOArabicWriter.class */
class ISOArabicWriter extends TextWriter {
    ISOArabicWriter(Writer writer, String str) {
        super(writer, str);
    }

    @Override // nu.xom.TextWriter
    boolean needsEscaping(char c) {
        if (c <= 160) {
            return false;
        }
        switch (c) {
            case 1548:
                return false;
            case 1549:
                return true;
            case 1550:
                return true;
            case 1551:
                return true;
            case 1552:
                return true;
            case 1553:
                return true;
            case 1554:
                return true;
            case 1555:
                return true;
            case 1556:
                return true;
            case 1557:
                return true;
            case 1558:
                return true;
            case 1559:
                return true;
            case 1560:
                return true;
            case 1561:
                return true;
            case 1562:
                return true;
            case 1563:
                return false;
            case 1564:
                return true;
            case 1565:
                return true;
            case 1566:
                return true;
            case 1567:
                return false;
            case 1568:
                return true;
            case 1569:
                return false;
            case 1570:
                return false;
            case 1571:
                return false;
            case 1572:
                return false;
            case 1573:
                return false;
            case 1574:
                return false;
            case 1575:
                return false;
            case 1576:
                return false;
            case 1577:
                return false;
            case 1578:
                return false;
            case 1579:
                return false;
            case 1580:
                return false;
            case 1581:
                return false;
            case 1582:
                return false;
            case 1583:
                return false;
            case 1584:
                return false;
            case 1585:
                return false;
            case 1586:
                return false;
            case 1587:
                return false;
            case 1588:
                return false;
            case 1589:
                return false;
            case 1590:
                return false;
            case 1591:
                return false;
            case 1592:
                return false;
            case 1593:
                return false;
            case 1594:
                return false;
            case 1595:
                return true;
            case 1596:
                return true;
            case 1597:
                return true;
            case 1598:
                return true;
            case 1599:
                return true;
            case 1600:
                return false;
            case 1601:
                return false;
            case 1602:
                return false;
            case 1603:
                return false;
            case 1604:
                return false;
            case 1605:
                return false;
            case 1606:
                return false;
            case 1607:
                return false;
            case 1608:
                return false;
            case 1609:
                return false;
            case 1610:
                return false;
            case 1611:
                return false;
            case 1612:
                return false;
            case 1613:
                return false;
            case 1614:
                return false;
            case 1615:
                return false;
            case 1616:
                return false;
            case 1617:
                return false;
            case 1618:
                return false;
            default:
                switch (c) {
                    case CipherSuite.TLS_DH_DSS_WITH_AES_128_GCM_SHA256 /* 164 */:
                        return false;
                    case CipherSuite.TLS_RSA_PSK_WITH_AES_256_GCM_SHA384 /* 173 */:
                        return false;
                    default:
                        return true;
                }
        }
    }
}
