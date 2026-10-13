package es.gob.afirma.standalone.plugins;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/plugins/Permission.class */
public enum Permission {
    INSTALL(1),
    RESET(3),
    PRESIGN(4),
    POSTSIGN(5),
    BUTTONS(6),
    MENU(7),
    COMMANDS(8),
    INLINE_PROCESS(9);

    private int order;

    Permission(int i) {
        this.order = i;
    }

    public int getOrder() {
        return this.order;
    }

    public static Permission forName(String str) {
        for (Permission permission : values()) {
            if (permission.toString().equalsIgnoreCase(str)) {
                return permission;
            }
        }
        throw new IllegalArgumentException("Permiso no reconocido: " + str);
    }
}
