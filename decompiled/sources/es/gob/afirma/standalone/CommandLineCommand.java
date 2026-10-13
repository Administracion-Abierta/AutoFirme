package es.gob.afirma.standalone;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/CommandLineCommand.class */
enum CommandLineCommand {
    LIST("listaliases", false),
    SIGN(CommandLineParameters.MASSIVE_OP_SIGN, false),
    COSIGN("cosign", false),
    COUNTERSIGN(CommandLineParameters.MASSIVE_OP_COUNTERSIGN, false),
    VERIFY("verify", false),
    BATCHSIGN("batchsign", false);

    private final String op;
    private final boolean mainFileNeeded;

    CommandLineCommand(String str, boolean z) {
        this.op = str;
        this.mainFileNeeded = z;
    }

    public String getOp() {
        return this.op;
    }

    public boolean isMainFileNeeded() {
        return this.mainFileNeeded;
    }

    public static CommandLineCommand parse(String str) {
        for (CommandLineCommand commandLineCommand : values()) {
            if (commandLineCommand.getOp().equals(str)) {
                return commandLineCommand;
            }
        }
        return null;
    }
}
