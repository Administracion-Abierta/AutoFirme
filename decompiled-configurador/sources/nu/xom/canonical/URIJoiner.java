package nu.xom.canonical;

import nu.xom.MalformedURIException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/canonical/URIJoiner.class */
class URIJoiner {

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/canonical/URIJoiner$ParsedURI.class */
    static class ParsedURI {
        String scheme;
        String schemeSpecificPart;
        String query;
        String fragment;
        String authority;
        String path;

        ParsedURI(String str) {
            this.path = "";
            int iIndexOf = str.indexOf(58);
            int iLastIndexOf = str.lastIndexOf(35);
            int iIndexOf2 = iLastIndexOf == -1 ? str.indexOf(63) : str.substring(0, iLastIndexOf).indexOf(63);
            if (iIndexOf != -1) {
                this.scheme = str.substring(0, iIndexOf);
            }
            if (iIndexOf2 == -1 && iLastIndexOf == -1) {
                this.schemeSpecificPart = str.substring(iIndexOf + 1);
            } else if (iIndexOf2 != -1) {
                if (iIndexOf2 < iIndexOf) {
                    MalformedURIException malformedURIException = new MalformedURIException("Unparseable URI");
                    malformedURIException.setData(str);
                    throw malformedURIException;
                }
                this.schemeSpecificPart = str.substring(iIndexOf + 1, iIndexOf2);
            } else {
                if (iLastIndexOf < iIndexOf) {
                    MalformedURIException malformedURIException2 = new MalformedURIException("Unparseable URI");
                    malformedURIException2.setData(str);
                    throw malformedURIException2;
                }
                this.schemeSpecificPart = str.substring(iIndexOf + 1, iLastIndexOf);
            }
            if (iLastIndexOf != -1) {
                this.fragment = str.substring(iLastIndexOf + 1);
            }
            if (iIndexOf2 != -1) {
                if (iLastIndexOf == -1) {
                    this.query = str.substring(iIndexOf2 + 1);
                } else {
                    this.query = str.substring(iIndexOf2 + 1, iLastIndexOf);
                }
            }
            if (!this.schemeSpecificPart.startsWith("//")) {
                this.path = this.schemeSpecificPart;
                return;
            }
            int iIndexOf3 = this.schemeSpecificPart.indexOf(47, 2);
            if (iIndexOf3 == -1) {
                this.authority = this.schemeSpecificPart.substring(2);
                this.path = "";
            } else {
                this.authority = this.schemeSpecificPart.substring(2, iIndexOf3);
                this.path = this.schemeSpecificPart.substring(iIndexOf3);
            }
        }

        ParsedURI() {
            this.path = "";
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer(30);
            if (this.scheme != null) {
                stringBuffer.append(this.scheme);
                stringBuffer.append(':');
            }
            if (this.schemeSpecificPart != null) {
                stringBuffer.append(this.schemeSpecificPart);
            } else {
                if (this.scheme != null) {
                    stringBuffer.append("//");
                }
                if (this.authority != null) {
                    stringBuffer.append(this.authority);
                }
                stringBuffer.append(this.path);
            }
            if (this.query != null) {
                stringBuffer.append('?');
                stringBuffer.append(this.query);
            }
            if (this.fragment != null) {
                stringBuffer.append('#');
                stringBuffer.append(this.fragment);
            }
            return stringBuffer.toString();
        }
    }

    URIJoiner() {
    }

    static String absolutize(String str, String str2) {
        if ("".equals(str) || str == null) {
            return str2;
        }
        ParsedURI parsedURI = new ParsedURI(str);
        if (parsedURI.path.endsWith("/..")) {
            parsedURI.path = new StringBuffer().append(parsedURI.path).append('/').toString();
        }
        ParsedURI parsedURI2 = new ParsedURI(str2);
        ParsedURI parsedURI3 = new ParsedURI();
        if (parsedURI2.scheme != null) {
            parsedURI3.scheme = parsedURI2.scheme;
            parsedURI3.authority = parsedURI2.authority;
            parsedURI3.query = parsedURI2.query;
            parsedURI3.path = removeDotSegments(parsedURI2.path);
        } else {
            if (parsedURI2.authority != null) {
                parsedURI3.authority = parsedURI2.authority;
                parsedURI3.query = parsedURI2.query;
                parsedURI3.path = removeDotSegments(parsedURI2.path);
            } else {
                if ("".equals(parsedURI2.path)) {
                    parsedURI3.path = parsedURI.path;
                    if (parsedURI2.query != null) {
                        parsedURI3.query = parsedURI2.query;
                    } else {
                        parsedURI3.query = parsedURI.query;
                    }
                } else {
                    if (parsedURI2.path.startsWith("/")) {
                        parsedURI3.path = removeDotSegments(parsedURI2.path);
                    } else {
                        parsedURI3.path = merge(parsedURI, parsedURI2.path);
                        parsedURI3.path = removeDotSegments(parsedURI3.path);
                    }
                    parsedURI3.query = parsedURI2.query;
                }
                parsedURI3.authority = parsedURI.authority;
            }
            parsedURI3.scheme = parsedURI.scheme;
        }
        parsedURI3.fragment = parsedURI2.fragment;
        return parsedURI3.toString();
    }

    static String merge(ParsedURI parsedURI, String str) {
        if (parsedURI.authority != null && "".equals(parsedURI.path) && !"".equals(parsedURI.authority)) {
            return new StringBuffer().append("/").append(str).toString();
        }
        int iLastIndexOf = parsedURI.path.lastIndexOf(47);
        if (iLastIndexOf == -1) {
            return str;
        }
        return new StringBuffer().append(parsedURI.path.substring(0, iLastIndexOf + 1)).append(str).toString();
    }

    static String removeDotSegments(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        while (str.length() > 0) {
            if (str.startsWith("/./")) {
                str = new StringBuffer().append('/').append(str.substring(3)).toString();
            } else if (str.equals("/.")) {
                str = "/";
            } else if (str.equals("/..")) {
                str = "/";
                int iLastIndexOf = stringBuffer.toString().lastIndexOf(47);
                if (iLastIndexOf != -1) {
                    stringBuffer.setLength(iLastIndexOf);
                }
            } else if (str.equals(".")) {
                str = "";
            } else if (str.startsWith("./")) {
                str = str.substring(2);
            } else {
                int iIndexOf = str.indexOf(47);
                if (iIndexOf == 0) {
                    iIndexOf = str.indexOf(47, 1);
                }
                if (iIndexOf == -1) {
                    stringBuffer.append(str);
                    str = "";
                } else {
                    stringBuffer.append(str.substring(0, iIndexOf));
                    str = str.substring(iIndexOf);
                }
            }
        }
        return stringBuffer.toString();
    }
}
