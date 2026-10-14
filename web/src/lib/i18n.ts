/** ES/EN copy. The app ships both; Spanish is the default (AutoFirma's audience). */

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
	certPending: string;
	formatTitle: string;
	pendingIssue: string;
	signButton: string;
	signNeedsFile: string;
	signNeedsCert: string;
	resultTitle: string;
	resultEmpty: string;
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
		certPending: 'La carga de certificados PKCS#12 llega en la incidencia #5.',
		formatTitle: 'Formato de firma',
		pendingIssue: 'pendiente',
		signButton: 'Firmar',
		signNeedsFile: 'Selecciona primero un documento.',
		signNeedsCert: 'Selecciona un certificado para habilitar la firma.',
		resultTitle: 'Resultado',
		resultEmpty: 'Todavía no se ha firmado ningún documento.',
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
		certPending: 'PKCS#12 certificate loading arrives with issue #5.',
		formatTitle: 'Signature format',
		pendingIssue: 'pending',
		signButton: 'Sign',
		signNeedsFile: 'Choose a document first.',
		signNeedsCert: 'Choose a certificate to enable signing.',
		resultTitle: 'Result',
		resultEmpty: 'No document has been signed yet.',
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
