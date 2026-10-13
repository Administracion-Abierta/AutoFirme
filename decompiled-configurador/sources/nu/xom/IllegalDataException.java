package nu.xom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/IllegalDataException.class */
public class IllegalDataException extends WellformednessException {
    private static final long serialVersionUID = 5116683358318890040L;
    private String data;

    public IllegalDataException(String str) {
        super(str);
    }

    public IllegalDataException(String str, Throwable th) {
        super(str, th);
    }

    public void setData(String str) {
        this.data = str;
    }

    public String getData() {
        return this.data;
    }
}
