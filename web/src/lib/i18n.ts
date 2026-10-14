/** ES/EN copy. The app ships both; Spanish is the default (AutoFirma's audience). */

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
	queueTitle: string;
	queueProgress: string;
	queueRunning: string;
	queueStatus: { pending: string; signing: string; signed: string; error: string };
	queueHash: string;
	queueDownload: string;
	queueArchive: string;
	queueNeedsEngine: string;
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
		certPending: 'La carga de certificados PKCS#12 llega en la incidencia #5.',
		formatTitle: 'Formato de firma',
		pendingIssue: 'pendiente',
		signButton: 'Firmar',
		signNeedsFile: 'Selecciona primero un documento.',
		signNeedsCert: 'Selecciona un certificado para habilitar la firma.',
		resultTitle: 'Resultado',
		resultEmpty: 'Todavía no se ha firmado ningún documento.',
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
		queueNeedsEngine: 'El motor de firma de cada formato llega con las incidencias #6, #7 y #11.'
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
		certPending: 'PKCS#12 certificate loading arrives with issue #5.',
		formatTitle: 'Signature format',
		pendingIssue: 'pending',
		signButton: 'Sign',
		signNeedsFile: 'Choose a document first.',
		signNeedsCert: 'Choose a certificate to enable signing.',
		resultTitle: 'Result',
		resultEmpty: 'No document has been signed yet.',
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
		queueNeedsEngine: "Each format's signing engine arrives with issues #6, #7 and #11."
	}
};
