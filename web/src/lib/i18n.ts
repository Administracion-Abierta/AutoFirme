/** ES/EN copy. The app ships both; Spanish is the default (AutoFirma's audience). */

import type { Pkcs12ErrorCode } from '#lib/pkcs12';

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
	certValidFrom: string;
	certErrors: Record<Pkcs12ErrorCode | 'unknown', string>;
	formatTitle: string;
	pendingIssue: string;
	signButton: string;
	signNeedsFile: string;
	signNeedsCert: string;
	resultTitle: string;
	resultEmpty: string;
}

export const copy: Record<Lang, Copy> = {
	es: {
		langLabel: 'Idioma',
		brand: 'AutoFirma Web',
		tagline: 'Firma electrónica en tu navegador, sin instalar nada.',
		privacyNote: 'Las claves privadas nunca salen de tu equipo: todo se firma en el navegador.',
		step1Title: '1. Elige el documento',
		dropTitle: 'Arrastra aquí el fichero que quieres firmar',
		dropHint: 'o selecciónalo desde tu equipo',
		browse: 'Seleccionar fichero',
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
		certValidFrom: 'Válido desde',
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
		resultTitle: 'Resultado',
		resultEmpty: 'Todavía no se ha firmado ningún documento.'
	},
	en: {
		langLabel: 'Language',
		brand: 'AutoFirma Web',
		tagline: 'Electronic signature in your browser, nothing to install.',
		privacyNote: 'Private keys never leave your machine: signing happens in the browser.',
		step1Title: '1. Choose the document',
		dropTitle: 'Drag the file you want to sign here',
		dropHint: 'or pick it from your machine',
		browse: 'Choose file',
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
		certValidFrom: 'Valid from',
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
		resultTitle: 'Result',
		resultEmpty: 'No document has been signed yet.'
	}
};
