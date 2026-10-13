package nu.xom;

import java.io.Writer;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/ISOGreekWriter.class */
class ISOGreekWriter extends TextWriter {
    ISOGreekWriter(Writer writer, String str) {
        super(writer, str);
    }

    @Override // nu.xom.TextWriter
    boolean needsEscaping(char c) {
        if (c < 127) {
            return false;
        }
        switch (c) {
            case 900:
                return false;
            case 901:
                return false;
            case 902:
                return false;
            case 903:
                return true;
            case 904:
                return false;
            case 905:
                return false;
            case 906:
                return false;
            case 907:
                return true;
            case 908:
                return false;
            case 909:
                return true;
            case 910:
                return false;
            case 911:
                return false;
            case 912:
                return false;
            case 913:
                return false;
            case 914:
                return false;
            case 915:
                return false;
            case 916:
                return false;
            case 917:
                return false;
            case 918:
                return false;
            case 919:
                return false;
            case 920:
                return false;
            case 921:
                return false;
            case 922:
                return false;
            case 923:
                return false;
            case 924:
                return false;
            case 925:
                return false;
            case 926:
                return false;
            case 927:
                return false;
            case 928:
                return false;
            case 929:
                return false;
            case 930:
                return true;
            case 931:
                return false;
            case 932:
                return false;
            case 933:
                return false;
            case 934:
                return false;
            case 935:
                return false;
            case 936:
                return false;
            case 937:
                return false;
            case 938:
                return false;
            case 939:
                return false;
            case 940:
                return false;
            case 941:
                return false;
            case 942:
                return false;
            case 943:
                return false;
            case 944:
                return false;
            case 945:
                return false;
            case 946:
                return false;
            case 947:
                return false;
            case 948:
                return false;
            case 949:
                return false;
            case 950:
                return false;
            case 951:
                return false;
            case 952:
                return false;
            case 953:
                return false;
            case 954:
                return false;
            case 955:
                return false;
            case 956:
                return false;
            case 957:
                return false;
            case 958:
                return false;
            case 959:
                return false;
            case 960:
                return false;
            case 961:
                return false;
            case 962:
                return false;
            case 963:
                return false;
            case 964:
                return false;
            case 965:
                return false;
            case 966:
                return false;
            case 967:
                return false;
            case 968:
                return false;
            case 969:
                return false;
            case 970:
                return false;
            case 971:
                return false;
            case 972:
                return false;
            case 973:
                return false;
            case 974:
                return false;
            default:
                switch (c) {
                    case CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256 /* 160 */:
                        return false;
                    case CipherSuite.TLS_DH_RSA_WITH_AES_256_GCM_SHA384 /* 161 */:
                        return true;
                    case CipherSuite.TLS_DHE_DSS_WITH_AES_128_GCM_SHA256 /* 162 */:
                        return true;
                    case CipherSuite.TLS_DHE_DSS_WITH_AES_256_GCM_SHA384 /* 163 */:
                        return false;
                    case CipherSuite.TLS_DH_DSS_WITH_AES_128_GCM_SHA256 /* 164 */:
                        return true;
                    case CipherSuite.TLS_DH_DSS_WITH_AES_256_GCM_SHA384 /* 165 */:
                        return true;
                    case CipherSuite.TLS_DH_anon_WITH_AES_128_GCM_SHA256 /* 166 */:
                        return false;
                    case CipherSuite.TLS_DH_anon_WITH_AES_256_GCM_SHA384 /* 167 */:
                        return false;
                    case CipherSuite.TLS_PSK_WITH_AES_128_GCM_SHA256 /* 168 */:
                        return false;
                    case CipherSuite.TLS_PSK_WITH_AES_256_GCM_SHA384 /* 169 */:
                        return false;
                    case CipherSuite.TLS_DHE_PSK_WITH_AES_128_GCM_SHA256 /* 170 */:
                        return true;
                    case CipherSuite.TLS_DHE_PSK_WITH_AES_256_GCM_SHA384 /* 171 */:
                        return false;
                    case CipherSuite.TLS_RSA_PSK_WITH_AES_128_GCM_SHA256 /* 172 */:
                        return false;
                    case CipherSuite.TLS_RSA_PSK_WITH_AES_256_GCM_SHA384 /* 173 */:
                        return false;
                    case CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA256 /* 174 */:
                        return true;
                    case CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA384 /* 175 */:
                        return true;
                    case CipherSuite.TLS_PSK_WITH_NULL_SHA256 /* 176 */:
                        return false;
                    case CipherSuite.TLS_PSK_WITH_NULL_SHA384 /* 177 */:
                        return false;
                    case CipherSuite.TLS_DHE_PSK_WITH_AES_128_CBC_SHA256 /* 178 */:
                        return false;
                    case CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA384 /* 179 */:
                        return false;
                    case CipherSuite.TLS_DHE_PSK_WITH_NULL_SHA256 /* 180 */:
                        return true;
                    case CipherSuite.TLS_DHE_PSK_WITH_NULL_SHA384 /* 181 */:
                        return true;
                    case CipherSuite.TLS_RSA_PSK_WITH_AES_128_CBC_SHA256 /* 182 */:
                        return true;
                    case CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA384 /* 183 */:
                        return false;
                    case CipherSuite.TLS_RSA_PSK_WITH_NULL_SHA256 /* 184 */:
                        return true;
                    case CipherSuite.TLS_RSA_PSK_WITH_NULL_SHA384 /* 185 */:
                        return true;
                    case CipherSuite.TLS_RSA_WITH_CAMELLIA_128_CBC_SHA256 /* 186 */:
                        return true;
                    case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA256 /* 187 */:
                        return false;
                    case 188:
                        return true;
                    case CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA256 /* 189 */:
                        return false;
                    default:
                        switch (c) {
                            case 8213:
                                return false;
                            default:
                                return true;
                        }
                }
        }
    }
}
