package nu.xom;

import java.io.Writer;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/ISOThaiWriter.class */
class ISOThaiWriter extends TextWriter {
    ISOThaiWriter(Writer writer, String str) {
        super(writer, str);
    }

    @Override // nu.xom.TextWriter
    boolean needsEscaping(char c) {
        if (c < 128) {
            return false;
        }
        switch (c) {
            case 3585:
                return false;
            case 3586:
                return false;
            case 3587:
                return false;
            case 3588:
                return false;
            case 3589:
                return false;
            case 3590:
                return false;
            case 3591:
                return false;
            case 3592:
                return false;
            case 3593:
                return false;
            case 3594:
                return false;
            case 3595:
                return false;
            case 3596:
                return false;
            case 3597:
                return false;
            case 3598:
                return false;
            case 3599:
                return false;
            case 3600:
                return false;
            case 3601:
                return false;
            case 3602:
                return false;
            case 3603:
                return false;
            case 3604:
                return false;
            case 3605:
                return false;
            case 3606:
                return false;
            case 3607:
                return false;
            case 3608:
                return false;
            case 3609:
                return false;
            case 3610:
                return false;
            case 3611:
                return false;
            case 3612:
                return false;
            case 3613:
                return false;
            case 3614:
                return false;
            case 3615:
                return false;
            case 3616:
                return false;
            case 3617:
                return false;
            case 3618:
                return false;
            case 3619:
                return false;
            case 3620:
                return false;
            case 3621:
                return false;
            case 3622:
                return false;
            case 3623:
                return false;
            case 3624:
                return false;
            case 3625:
                return false;
            case 3626:
                return false;
            case 3627:
                return false;
            case 3628:
                return false;
            case 3629:
                return false;
            case 3630:
                return false;
            case 3631:
                return false;
            case 3632:
                return false;
            case 3633:
                return false;
            case 3634:
                return false;
            case 3635:
                return false;
            case 3636:
                return false;
            case 3637:
                return false;
            case 3638:
                return false;
            case 3639:
                return false;
            case 3640:
                return false;
            case 3641:
                return false;
            case 3642:
                return false;
            default:
                switch (c) {
                    case 3647:
                        return false;
                    case 3648:
                        return false;
                    case 3649:
                        return false;
                    case 3650:
                        return false;
                    case 3651:
                        return false;
                    case 3652:
                        return false;
                    case 3653:
                        return false;
                    case 3654:
                        return false;
                    case 3655:
                        return false;
                    case 3656:
                        return false;
                    case 3657:
                        return false;
                    case 3658:
                        return false;
                    case 3659:
                        return false;
                    case 3660:
                        return false;
                    case 3661:
                        return false;
                    case 3662:
                        return false;
                    case 3663:
                        return false;
                    case 3664:
                        return false;
                    case 3665:
                        return false;
                    case 3666:
                        return false;
                    case 3667:
                        return false;
                    case 3668:
                        return false;
                    case 3669:
                        return false;
                    case 3670:
                        return false;
                    case 3671:
                        return false;
                    case 3672:
                        return false;
                    case 3673:
                        return false;
                    case 3674:
                        return false;
                    case 3675:
                        return false;
                    default:
                        return true;
                }
        }
    }
}
