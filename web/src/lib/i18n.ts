/** ES/EN copy. The app ships both; Spanish is the default (AutoFirma's audience). */

import type { Pkcs12ErrorCode } from '#lib/pkcs12';
import type { CheckId } from '#lib/verify';

export type Lang = 'es' | 'en';

export interface Copy {
	langLabel: string;
	brand: string;
	tagline: string;
	privacyNote: string;
	step1Title: string;
	dropTitle: string;
	dropHint: string;
	browse: string;
	dragging: string;
	fileLabel: string;
	remove: string;
	step2Title: string;
	certTitle: string;
	certLabel: string;
	certPlaceholder: string;
	certFileLabel: string;
	certPinLabel: string;
	certLoad: string;
	certLoading: string;
	certHint: string;
	certNoKey: string;
	certExpired: string;
	certNotYetValid: string;
	certIssuer: string;
	certValidity: string;
	certErrors: Record<Pkcs12ErrorCode | 'unknown', string>;
	formatTitle: string;
	pendingIssue: string;
	signButton: string;
	signNeedsFile: string;
	signNeedsCert: string;
	signPendingFormat: string;
	queueTitle: string;
	queueProgress: string;
	queueRunning: string;
	queueStatus: { pending: string; signing: string; signed: string; error: string };
	queueHash: string;
	queueDownload: string;
	queueArchive: string;
	modesLabel: string;
	modeSign: string;
	modeVerify: string;
	verifyStepTitle: string;
	verifyDropTitle: string;
	verifyDropHint: string;
	verifyRunning: string;
	verifyReportTitle: string;
	verifyOk: string;
	verifyFail: string;
	verifySigner: string;
	verifySigningTime: string;
	verifyNoSigner: string;
	verifyAddDocument: string;
	verifyAddDocumentHint: string;
	verifyDocumentLabel: string;
	checkLabels: Record<CheckId, string>;
}

export const copy: Record<Lang, Copy> = {
	es: {
		langLabel: 'Idioma',
		brand: 'AutoFirma Web',
		tagline: 'Firma electrónica en tu navegador, sin instalar nada.',
		privacyNote: 'Las claves privadas nunca salen de tu equipo: todo se firma en el navegador.',
		step1Title: '1. Elige los documentos',
		dropTitle: 'Arrastra aquí los ficheros que quieres firmar',
		dropHint: 'o selecciónalos desde tu equipo',
		browse: 'Seleccionar ficheros',
		dragging: 'Suelta el fichero para cargarlo',
		fileLabel: 'Fichero',
		remove: 'Quitar',
		step2Title: '2. Configura la firma',
		certTitle: 'Certificado de firma',
		certLabel: 'Certificado',
		certPlaceholder: 'Sin certificado',
		certFileLabel: 'Fichero PKCS#12 (.p12 o .pfx)',
		certPinLabel: 'PIN del fichero',
		certLoad: 'Cargar certificados',
		certLoading: 'Descifrando el fichero…',
		certHint: 'Elige tu fichero PKCS#12 y escribe su PIN. Se descifra en tu navegador.',
		certNoKey: 'sin clave privada',
		certExpired: 'Este certificado está caducado; puedes firmar, pero puede ser rechazado.',
		certNotYetValid: 'Este certificado aún no es válido.',
		certIssuer: 'Emitido por',
		certValidity: 'Válido hasta',
		certErrors: {
			'wrong-pin': 'PIN incorrecto.',
			'not-pkcs12': 'El fichero no es un PKCS#12 válido.',
			'unsupported-pbe': 'El fichero usa un cifrado antiguo (3DES/RC2). Vuelve a exportarlo como PKCS#12 moderno (AES).',
			'unsupported-cipher': 'El fichero usa un cifrado que el navegador no admite.',
			'unsupported-key': 'La clave privada de este fichero no se puede usar en el navegador.',
			'no-cert': 'El fichero no contiene ningún certificado.',
			'no-key': 'El fichero no contiene ninguna clave privada.',
			'key-mismatch': 'La clave privada no corresponde a los certificados del fichero.',
			unknown: 'No se ha podido leer el fichero.'
		},
		formatTitle: 'Formato de firma',
		pendingIssue: 'pendiente',
		signButton: 'Firmar',
		signNeedsFile: 'Selecciona primero un documento.',
		signNeedsCert: 'Selecciona un certificado para habilitar la firma.',
		signPendingFormat: 'Este formato llegará en otra entrega.',
		queueTitle: 'Cola de firma',
		queueProgress: 'Firmados',
		queueRunning: 'Firmando lote…',
		queueStatus: {
			pending: 'Pendiente',
			signing: 'Firmando',
			signed: 'Firmada',
			error: 'Error'
		},
		queueHash: 'SHA-256',
		queueDownload: 'Descargar ZIP',
		queueArchive: 'firmas-lote.zip',
		modesLabel: 'Modo',
		modeSign: 'Firmar',
		modeVerify: 'Verificar',
		verifyStepTitle: 'Comprueba una firma',
		verifyDropTitle: 'Arrastra aquí el fichero firmado (.csig, .xsig o PDF)',
		verifyDropHint: 'se analiza en tu equipo, sin subir nada',
		verifyRunning: 'Comprobando la firma…',
		verifyReportTitle: 'Informe de validación',
		verifyOk: 'Firma verificada: todas las comprobaciones han pasado.',
		verifyFail: 'La firma no supera todas las comprobaciones.',
		verifySigner: 'Firmante',
		verifySigningTime: 'Fecha de firma',
		verifyNoSigner: 'No se ha encontrado el certificado del firmante.',
		verifyAddDocument: 'Añade el documento original',
		verifyAddDocumentHint: 'la firma es separada (detached): sin el documento no se puede comprobar el contenido',
		verifyDocumentLabel: 'Documento original',
		checkLabels: {
			structure: 'Estructura de la firma',
			contentDigest: 'Integridad del contenido',
			signature: 'Firma criptográfica',
			reference: 'Referencia firmada',
			signerCert: 'Certificado del firmante',
			certValidity: 'Vigencia del certificado',
			certChain: 'Cadena de confianza',
			signingTime: 'Fecha de firma',
			unsupported: 'Formato'
		}
	},
	en: {
		langLabel: 'Language',
		brand: 'AutoFirma Web',
		tagline: 'Electronic signature in your browser, nothing to install.',
		privacyNote: 'Private keys never leave your machine: signing happens in the browser.',
		step1Title: '1. Choose the documents',
		dropTitle: 'Drag the files you want to sign here',
		dropHint: 'or pick them from your machine',
		browse: 'Choose files',
		dragging: 'Drop the file to load it',
		fileLabel: 'File',
		remove: 'Remove',
		step2Title: '2. Configure the signature',
		certTitle: 'Signing certificate',
		certLabel: 'Certificate',
		certPlaceholder: 'No certificate',
		certFileLabel: 'PKCS#12 file (.p12 or .pfx)',
		certPinLabel: 'File PIN',
		certLoad: 'Load certificates',
		certLoading: 'Decrypting the file…',
		certHint: 'Pick your PKCS#12 file and type its PIN. It is decrypted in your browser.',
		certNoKey: 'no private key',
		certExpired: 'This certificate has expired; you can sign, but it may be rejected.',
		certNotYetValid: 'This certificate is not valid yet.',
		certIssuer: 'Issued by',
		certValidity: 'Valid until',
		certErrors: {
			'wrong-pin': 'Wrong PIN.',
			'not-pkcs12': 'The file is not a valid PKCS#12 container.',
			'unsupported-pbe': 'This file uses legacy encryption (3DES/RC2). Re-export it as modern (AES) PKCS#12.',
			'unsupported-cipher': 'This file uses a cipher the browser does not support.',
			'unsupported-key': 'The private key in this file cannot be used in a browser.',
			'no-cert': 'The file holds no certificate.',
			'no-key': 'The file holds no private key.',
			'key-mismatch': 'The private key does not match the certificates in the file.',
			unknown: 'The file could not be read.'
		},
		formatTitle: 'Signature format',
		pendingIssue: 'pending',
		signButton: 'Sign',
		signNeedsFile: 'Choose a document first.',
		signNeedsCert: 'Choose a certificate to enable signing.',
		signPendingFormat: 'This format ships in a later delivery.',
		queueTitle: 'Signing queue',
		queueProgress: 'Signed',
		queueRunning: 'Signing batch…',
		queueStatus: {
			pending: 'Pending',
			signing: 'Signing',
			signed: 'Signed',
			error: 'Error'
		},
		queueHash: 'SHA-256',
		queueDownload: 'Download ZIP',
		queueArchive: 'signed-batch.zip',
		modesLabel: 'Mode',
		modeSign: 'Sign',
		modeVerify: 'Verify',
		verifyStepTitle: 'Check a signature',
		verifyDropTitle: 'Drop the signed file here (.csig, .xsig or PDF)',
		verifyDropHint: 'it is analysed on your machine, nothing is uploaded',
		verifyRunning: 'Checking the signature…',
		verifyReportTitle: 'Validity report',
		verifyOk: 'Signature verified: every check passed.',
		verifyFail: 'The signature does not pass every check.',
		verifySigner: 'Signer',
		verifySigningTime: 'Signing time',
		verifyNoSigner: 'No signer certificate was found.',
		verifyAddDocument: 'Add the original document',
		verifyAddDocumentHint: 'this is a detached signature: without the document the content cannot be checked',
		verifyDocumentLabel: 'Original document',
		checkLabels: {
			structure: 'Signature structure',
			contentDigest: 'Content integrity',
			signature: 'Cryptographic signature',
			reference: 'Signed reference',
			signerCert: 'Signer certificate',
			certValidity: 'Certificate validity',
			certChain: 'Trust chain',
			signingTime: 'Signing time',
			unsupported: 'Format'
		}
	}
};
