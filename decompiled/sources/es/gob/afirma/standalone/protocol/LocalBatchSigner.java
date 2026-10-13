package es.gob.afirma.standalone.protocol;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOFormatFileException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.AOSignerFactory;
import es.gob.afirma.core.signers.AOTriphaseException;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.signers.pades.InvalidPdfException;
import es.gob.afirma.signers.pades.common.BadPdfPasswordException;
import es.gob.afirma.signers.pades.common.PdfHasUnregisteredSignaturesException;
import es.gob.afirma.signers.pades.common.PdfIsCertifiedException;
import es.gob.afirma.signers.pades.common.PdfIsPasswordProtectedException;
import es.gob.afirma.signers.pkcs7.ContainsNoDataException;
import es.gob.afirma.signers.xades.EFacturaAlreadySignedException;
import es.gob.afirma.signers.xades.InvalidEFacturaDataException;
import es.gob.afirma.signers.xml.InvalidXMLException;
import es.gob.afirma.signvalidation.InvalidSignatureException;
import es.gob.afirma.standalone.plugins.SignOperation;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/LocalBatchSigner.class */
public class LocalBatchSigner {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String DONE_SIGN = "DONE_AND_SAVED";
    private static final String SKIPPED_SIGN = "SKIPPED";
    private static final String ERROR_SIGN = "ERROR_PRE";
    private static final String EXTRAPARAM_HEADLESS = "headless";

    public static String signLocalBatch(BatchSignOperation batchSignOperation, KeyStore.PrivateKeyEntry privateKeyEntry) {
        ArrayList<LocalSingleBatchResult> arrayList = new ArrayList();
        boolean z = false;
        for (SingleSignOperation singleSignOperation : batchSignOperation.getSigns()) {
            if (z && batchSignOperation.isStopOnError()) {
                arrayList.add(new LocalSingleBatchResult(singleSignOperation.getDocId()));
            } else {
                try {
                    arrayList.add(new LocalSingleBatchResult(singleSignOperation.getDocId(), processSign(singleSignOperation, privateKeyEntry)));
                } catch (SocketOperationException e) {
                    z = true;
                    LOGGER.severe("Error al procesar la firma de lotes monofasica con ID: " + LoggerUtil.getTrimStr(singleSignOperation.getDocId()) + "Excepcion :" + e);
                    if (batchSignOperation.isStopOnError()) {
                        for (LocalSingleBatchResult localSingleBatchResult : arrayList) {
                            localSingleBatchResult.setResult(SKIPPED_SIGN);
                            localSingleBatchResult.setSignature(null);
                        }
                    }
                    arrayList.add(new LocalSingleBatchResult(singleSignOperation.getDocId(), e.getMessage()));
                }
            }
        }
        return JSONBatchManager.buildBatchResultJson(arrayList);
    }

    private static byte[] processSign(SingleSignOperation singleSignOperation, KeyStore.PrivateKeyEntry privateKeyEntry) throws SocketOperationException {
        byte[] bArrCountersign;
        SingleSignOperation.Operation cryptoOperation = singleSignOperation.getCryptoOperation();
        byte[] data = singleSignOperation.getData();
        try {
            String strComposeSignatureAlgorithmName = AOSignConstants.composeSignatureAlgorithmName(singleSignOperation.getAlgorithm(), privateKeyEntry.getPrivateKey().getAlgorithm());
            String format = singleSignOperation.getFormat();
            Properties extraParams = singleSignOperation.getExtraParams();
            AOSigner signer = null;
            if (!"auto".equalsIgnoreCase(format)) {
                signer = AOSignerFactory.getSigner(format);
                if (signer == null) {
                    LOGGER.severe("No hay un firmador configurado para el formato: " + LoggerUtil.getTrimStr(format));
                    throw new SocketOperationException("SAF_06");
                }
            }
            if (signer == null) {
                String strIdentifyFormatFromData = ProtocolInvocationLauncherUtil.identifyFormatFromData(data, SignOperation.Operation.valueOf(cryptoOperation.name()));
                if (strIdentifyFormatFromData == null) {
                    LOGGER.severe("Los datos no se corresponden con una firma electronica o no se pudieron analizar");
                    throw new SocketOperationException("SAF_17");
                }
                signer = AOSignerFactory.getSigner(strIdentifyFormatFromData);
            }
            Properties properties = new Properties();
            if (extraParams != null) {
                properties.putAll(extraParams);
            }
            properties.setProperty("headless", Boolean.TRUE.toString());
            try {
                try {
                    switch (cryptoOperation) {
                        case SIGN:
                            bArrCountersign = signer.sign(data, strComposeSignatureAlgorithmName, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), properties);
                            break;
                        case COSIGN:
                            bArrCountersign = signer.cosign(data, strComposeSignatureAlgorithmName, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), properties);
                            break;
                        case COUNTERSIGN:
                            bArrCountersign = signer.countersign(data, strComposeSignatureAlgorithmName, CounterSignTarget.getTarget(properties.getProperty("target")) == CounterSignTarget.TREE ? CounterSignTarget.TREE : CounterSignTarget.LEAFS, null, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), properties);
                            break;
                        default:
                            LOGGER.severe("Error al realizar la operacion firma");
                            throw new SocketOperationException("SAF_04");
                    }
                    return bArrCountersign;
                } catch (AOTriphaseException e) {
                    throw ProtocolInvocationLauncherUtil.getInternalException(e);
                }
            } catch (AOFormatFileException e2) {
                LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e2);
                throw new SocketOperationException("SAF_30", e2);
            } catch (AOTriphaseException e3) {
                LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e3);
                throw new SocketOperationException("SAF_40", e3);
            } catch (InvalidPdfException e4) {
                LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e4);
                throw new SocketOperationException("SAF_28", e4);
            } catch (BadPdfPasswordException | PdfIsPasswordProtectedException e5) {
                LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", e5);
                throw new SocketOperationException("SAF_33", e5);
            } catch (PdfHasUnregisteredSignaturesException e6) {
                LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e6);
                throw new SocketOperationException("SAF_34", e6);
            } catch (PdfIsCertifiedException e7) {
                LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e7);
                throw new SocketOperationException("SAF_35", e7);
            } catch (ContainsNoDataException e8) {
                LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e8);
                throw new SocketOperationException("SAF_44", e8);
            } catch (EFacturaAlreadySignedException e9) {
                LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e9);
                throw new SocketOperationException("SAF_32", e9);
            } catch (InvalidEFacturaDataException e10) {
                LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e10);
                throw new SocketOperationException("SAF_38", e10);
            } catch (AOInvalidFormatException e11) {
                LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e11);
                throw new SocketOperationException("SAF_31", e11);
            } catch (InvalidXMLException e12) {
                LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e12);
                throw new SocketOperationException("SAF_29", e12);
            } catch (InvalidSignatureException e13) {
                LOGGER.log(Level.SEVERE, "La firma de entrada no es valida", (Throwable) e13);
                throw new SocketOperationException("SAF_39", e13);
            } catch (AOException e14) {
                LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e14);
                throw new SocketOperationException("SAF_09", e14);
            } catch (SocketOperationException e15) {
                throw e15;
            } catch (IllegalArgumentException e16) {
                LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e16);
                throw new SocketOperationException("SAF_03", e16);
            } catch (UnsupportedOperationException e17) {
                LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e17);
                throw new SocketOperationException("SAF_04", e17);
            } catch (Exception e18) {
                LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e18);
                throw new SocketOperationException("SAF_09", e18);
            }
        } catch (Exception e19) {
            throw new SocketOperationException("SAF_51", e19);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/LocalBatchSigner$LocalSingleBatchResult.class */
    static class LocalSingleBatchResult {
        private String docId;
        private String result;
        private String description;
        private byte[] signature;

        public LocalSingleBatchResult(String str) {
            this.docId = str;
            this.result = LocalBatchSigner.SKIPPED_SIGN;
        }

        public LocalSingleBatchResult(String str, byte[] bArr) {
            this.docId = str;
            this.result = LocalBatchSigner.DONE_SIGN;
            this.signature = bArr;
        }

        public LocalSingleBatchResult(String str, String str2) {
            this.docId = str;
            this.result = LocalBatchSigner.ERROR_SIGN;
            this.description = str2;
        }

        public void setDocId(String str) {
            this.docId = str;
        }

        public String getDocId() {
            return this.docId;
        }

        public void setResult(String str) {
            this.result = str;
        }

        public String getResult() {
            return this.result;
        }

        public void setDescription(String str) {
            this.description = str;
        }

        public String getDescription() {
            return this.description;
        }

        public void setSignature(byte[] bArr) {
            this.signature = bArr;
        }

        public byte[] getSignature() {
            return this.signature;
        }
    }
}
