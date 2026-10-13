package nu.xom;

import com.sun.jna.platform.win32.ShellAPI;
import com.sun.jna.platform.win32.WinError;
import java.io.Writer;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/ISOArabicWriter.class */
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
            case WinError.ERROR_INVALID_TASK_NAME /* 1550 */:
                return true;
            case WinError.ERROR_INVALID_TASK_INDEX /* 1551 */:
                return true;
            case WinError.ERROR_THREAD_ALREADY_IN_TASK /* 1552 */:
                return true;
            case 1553:
                return true;
            case 1554:
                return true;
            case 1555:
                return true;
            case ShellAPI.FOF_NO_UI /* 1556 */:
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
            case WinError.ERROR_INSTALL_SERVICE_FAILURE /* 1601 */:
                return false;
            case WinError.ERROR_INSTALL_USEREXIT /* 1602 */:
                return false;
            case WinError.ERROR_INSTALL_FAILURE /* 1603 */:
                return false;
            case WinError.ERROR_INSTALL_SUSPEND /* 1604 */:
                return false;
            case WinError.ERROR_UNKNOWN_PRODUCT /* 1605 */:
                return false;
            case WinError.ERROR_UNKNOWN_FEATURE /* 1606 */:
                return false;
            case WinError.ERROR_UNKNOWN_COMPONENT /* 1607 */:
                return false;
            case WinError.ERROR_UNKNOWN_PROPERTY /* 1608 */:
                return false;
            case WinError.ERROR_INVALID_HANDLE_STATE /* 1609 */:
                return false;
            case WinError.ERROR_BAD_CONFIGURATION /* 1610 */:
                return false;
            case WinError.ERROR_INDEX_ABSENT /* 1611 */:
                return false;
            case WinError.ERROR_INSTALL_SOURCE_ABSENT /* 1612 */:
                return false;
            case WinError.ERROR_INSTALL_PACKAGE_VERSION /* 1613 */:
                return false;
            case WinError.ERROR_PRODUCT_UNINSTALLED /* 1614 */:
                return false;
            case WinError.ERROR_BAD_QUERY_SYNTAX /* 1615 */:
                return false;
            case WinError.ERROR_INVALID_FIELD /* 1616 */:
                return false;
            case WinError.ERROR_DEVICE_REMOVED /* 1617 */:
                return false;
            case WinError.ERROR_INSTALL_ALREADY_RUNNING /* 1618 */:
                return false;
            default:
                switch (c) {
                    case 164:
                        return false;
                    case 173:
                        return false;
                    default:
                        return true;
                }
        }
    }
}
