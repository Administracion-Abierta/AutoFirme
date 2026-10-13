package org.apache.oro.text.regex;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/text/regex/Perl5MatchResult.class */
final class Perl5MatchResult implements MatchResult {
    int _matchBeginOffset;
    int[] _beginGroupOffset;
    int[] _endGroupOffset;
    String _match;

    Perl5MatchResult(int groups) {
        this._beginGroupOffset = new int[groups];
        this._endGroupOffset = new int[groups];
    }

    @Override // org.apache.oro.text.regex.MatchResult
    public int length() {
        int length = this._endGroupOffset[0] - this._beginGroupOffset[0];
        if (length > 0) {
            return length;
        }
        return 0;
    }

    @Override // org.apache.oro.text.regex.MatchResult
    public int groups() {
        return this._beginGroupOffset.length;
    }

    @Override // org.apache.oro.text.regex.MatchResult
    public String group(int group) {
        if (group < this._beginGroupOffset.length) {
            int begin = this._beginGroupOffset[group];
            int end = this._endGroupOffset[group];
            int length = this._match.length();
            if (begin >= 0 && end >= 0) {
                if (begin < length && end <= length && end > begin) {
                    return this._match.substring(begin, end);
                }
                if (begin <= end) {
                    return "";
                }
                return null;
            }
            return null;
        }
        return null;
    }

    @Override // org.apache.oro.text.regex.MatchResult
    public int begin(int group) {
        if (group < this._beginGroupOffset.length) {
            int begin = this._beginGroupOffset[group];
            int end = this._endGroupOffset[group];
            if (begin >= 0 && end >= 0) {
                return begin;
            }
            return -1;
        }
        return -1;
    }

    @Override // org.apache.oro.text.regex.MatchResult
    public int end(int group) {
        if (group < this._beginGroupOffset.length) {
            int begin = this._beginGroupOffset[group];
            int end = this._endGroupOffset[group];
            if (begin >= 0 && end >= 0) {
                return end;
            }
            return -1;
        }
        return -1;
    }

    @Override // org.apache.oro.text.regex.MatchResult
    public int beginOffset(int group) {
        if (group < this._beginGroupOffset.length) {
            int begin = this._beginGroupOffset[group];
            int end = this._endGroupOffset[group];
            if (begin >= 0 && end >= 0) {
                return this._matchBeginOffset + begin;
            }
            return -1;
        }
        return -1;
    }

    @Override // org.apache.oro.text.regex.MatchResult
    public int endOffset(int group) {
        if (group < this._endGroupOffset.length) {
            int begin = this._beginGroupOffset[group];
            int end = this._endGroupOffset[group];
            if (begin >= 0 && end >= 0) {
                return this._matchBeginOffset + end;
            }
            return -1;
        }
        return -1;
    }

    @Override // org.apache.oro.text.regex.MatchResult
    public String toString() {
        return group(0);
    }
}
