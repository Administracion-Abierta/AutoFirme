package com.sun.jna.platform.win32;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/SspiUtil.class */
public class SspiUtil {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/SspiUtil$ManagedSecBufferDesc.class */
    public static class ManagedSecBufferDesc extends Sspi.SecBufferDesc {
        private final Sspi.SecBuffer[] secBuffers;

        public ManagedSecBufferDesc(int type, byte[] token) {
            this.secBuffers = new Sspi.SecBuffer[]{new Sspi.SecBuffer(type, token)};
            this.pBuffers = this.secBuffers[0].getPointer();
            this.cBuffers = this.secBuffers.length;
        }

        public ManagedSecBufferDesc(int type, int tokenSize) {
            this.secBuffers = new Sspi.SecBuffer[]{new Sspi.SecBuffer(type, tokenSize)};
            this.pBuffers = this.secBuffers[0].getPointer();
            this.cBuffers = this.secBuffers.length;
        }

        public ManagedSecBufferDesc(int bufferCount) {
            this.cBuffers = bufferCount;
            this.secBuffers = (Sspi.SecBuffer[]) new Sspi.SecBuffer().toArray(bufferCount);
            this.pBuffers = this.secBuffers[0].getPointer();
            this.cBuffers = this.secBuffers.length;
        }

        public Sspi.SecBuffer getBuffer(int idx) {
            return this.secBuffers[idx];
        }

        @Override // com.sun.jna.Structure
        public void write() {
            for (Sspi.SecBuffer sb : this.secBuffers) {
                sb.write();
            }
            writeField("ulVersion");
            writeField("pBuffers");
            writeField("cBuffers");
        }

        @Override // com.sun.jna.Structure
        public void read() {
            for (Sspi.SecBuffer sb : this.secBuffers) {
                sb.read();
            }
        }
    }
}
