/** ES/EN copy. The app ships both; Spanish is the default (AutoFirma's audience). */

import type { Pkcs12ErrorCode } from '#lib/pkcs12';
import type { StoreErrorCode } from '#lib/certstore';
import type { SignatureCorner } from '#lib/pades-visible';
import type { CheckId } from '#lib/verify';

export type Lang = 'es' | 'en';

export interface Copy {
	langLabel: string;
	/** Theme switcher: group label plus the three choices. */
	themeLabel: string;
	themeLight: string;
	themeDark: string;
	themeSystem: string;
	brand: string;
	privacyNote: string;
	/** Title of the privacy section of the settings modal. */
	privacyTitle: string;
	/** Link to the project's source repository, in the Ayuda section. */
	sourceCode: string;
	/** Link to the in-app diagnostics page (P0.4 smoke table). */
	diagnosticsLink: string;
	/** Label of the header help menu. */
	helpMenu: string;
	/** Header settings button and modal title. */
	settingsLabel: string;
	settingsTitle: string;
	/** Settings modal tabs. */
	settingsTab: string;
	mcpTab: string;
	/** Header button that opens the browser's install prompt (#47). */
	installApp: string;
	/** Header button that activates a downloaded update of the installed app (#47). */
	updateApp: string;
	/** Settings: optional PIN memory for the signing picker. */
	pinMemoryTitle: string;
	pinMemoryHint: string;
	pinMemoryMinutes: string;
	/** Selected-files table headers. */
	colFile: string;
	colActions: string;
	step1Title: string;
	dropTitle: string;
	dropHint: string;
	browse: string;
	dragging: string;
	dropOverlay: string;
	remove: string;
	step2Title: string;
	/** Trigger of the signature picker modal. */
	selectSign: string;
	/** Cell of that modal that opens the file dialog for a PKCS#12 from disk. */
	addSign: string;
	certLoading: string;
	certExpired: string;
	certNotYetValid: string;
	/** Summary lines under the picker icon: who issued the sign, and how long it holds. */
	signIssuer: string;
	signValidity: string;
	/** The page is not a secure context, so the browser withholds WebCrypto entirely. */
	insecureContext: string;
	certErrors: Record<Pkcs12ErrorCode | 'unknown', string>;
	formatTitle: string;
	formatHint: string;
	/** Label shown next to the chosen format: it applies to the whole batch. */
	formatAll: string;
	/** Honest note under the OOXML option: AutoFirma 1.9 never verifies these. Refs #127. */
	formatOoxmlNoVerify: string;
	/** Optional RFC 3161 timestamp: the TSA address field of the sign panel. */
	timeStampTitle: string;
	timeStampPlaceholder: string;
	timeStampHint: string;
	pendingIssue: string;
	/** Visible signature block (PAdES only): toggle, image, page and corner picker. */
	visibleTitle: string;
	visibleHint: string;
	visibleImage: string;
	visibleImageChoose: string;
	visibleImageInvalid: string;
	visiblePage: string;
	visibleCorner: string;
	visibleCorners: Record<SignatureCorner, string>;
	/** Prefix of the block's date line. */
	signedOn: string;
	signButton: string;
	signNeedsFile: string;
	signNeedsCert: string;
	signPendingFormat: string;
	queueTitle: string;
	queueProgress: string;
	queueRunning: string;
	queueCancel: string;
	queueCancelled: string;
	queueStatus: { pending: string; signing: string; signed: string; error: string };
	queueHash: string;
	queueDownload: string;
	queueArchive: string;
	downloadOne: string;
	modeSign: string;
	entryShared: string;
	entryPortal: string;
	entryError: string;
	entryReturn: string;
	entryReturnWait: string;
	entryReturnReady: string;
	entryReturnButton: string;
	/** Desktop shell (`afirma://` deep-links, #103): scheme owner status lines. */
	afirmaSchemeOurs: string;
	afirmaSchemeOther: string;
	afirmaSchemeNone: string;
	/** Desktop shell: incoming portal request banner + allow-list section. */
	afirmaTitle: string;
	afirmaHint: string;
	afirmaOp: string;
	afirmaOrigin: string;
	afirmaNoOrigin: string;
	afirmaReview: string;
	afirmaDismiss: string;
	afirmaRemember: string;
	afirmaConfirmed: string;
	afirmaListTitle: string;
	afirmaListEmpty: string;
	afirmaRemove: string;
	checkLabels: Record<CheckId, string>;
	storeTitle: string;
	storeHint: string;
	storeEmpty: string;
	/** Shown when the browser gives no database, so saved signatures cannot outlive the visit. */
	storeEphemeral: string;
	/** Button in that notice: deletes the unusable store and reloads. */
	storeReset: string;
	/** Leaves the PIN prompt of a saved signature without selecting it. */
	storeBack: string;
	/** Badge on the modal cell that mirrors the sign currently loaded in memory. */
	storeCurrent: string;
	storePin: string;
	/** Checkbox under the PIN prompt; `{minutes}` is replaced by the configured duration. */
	rememberPin: string;
	storeSave: string;
	storeUnlock: string;
	storeOpen: string;
	storeExpiry: string;
	/** Alias of a saved signature: the user-chosen name shown instead of the file name. */
	storeAlias: string;
	storeRename: string;
	storeErrors: Record<StoreErrorCode, string>;
	stepConfigure: string;
	stepFiles: string;
	stepDownload: string;
	next: string;
	back: string;
	newBatch: string;
	/** MCP server section, inline in the settings modal. */
	mcpMenu: string;
	mcpTitle: string;
	mcpHint: string;
	mcpUrlLabel: string;
	mcpUrlHint: string;
	mcpTokenLabel: string;
	mcpTokenHint: string;
	mcpCopy: string;
	mcpCopied: string;
	mcpCopiedNote: string;
	/** Extension-only: bridge to the local MCP server. */
	mcpBridgeTitle: string;
	mcpBridgeEnable: string;
	mcpBridgeHint: string;
	mcpBridgeConnect: string;
	mcpBridgeOnline: string;
	mcpBridgeOffline: string;
	mcpBridgeNeedsToken: string;
	mcpBridgeBadEndpoint: string;
	mcpBridgeUnreachable: string;
	/** Extension chooser label when the request comes from an MCP agent. */
	mcpOrigin: string;
	/** Copy of the in-app diagnostics page (#167): the WebCrypto probe, the parity table and the
	 *  PAdES budget run. Nested so the page reads `t.diagnostics.title`. */
	diagnostics: DiagnosticsCopy;
	/** Native surfaces of the Tauri shell: PIN and consent dialogs, save dialog, updater, installer, error toasts. */
	native: NativeCopy;
}

/** Copy of the in-app diagnostics page (#167). */
export interface DiagnosticsCopy {
	/** Browser tab title of the diagnostics page. */
	title: string;
	/** Page heading. */
	heading: string;
	/** Sentence introducing the WebCrypto secure-context probe. */
	secureContext: string;
	/** Heading of the WebCrypto probe block. */
	webCryptoTitle: string;
	/** Probe state while the probes run. */
	probesRunning: string;
	/** Probe state when every probe passed. */
	probeOk: string;
	/** Probe state when a probe failed. */
	probeFail: string;
	/** Heading of the 12-item parity gate block. */
	parityTitle: string;
	/** Sentence explaining that each item is proven by the suite named next to it. */
	parityHint: string;
	/** The command that runs the suites, e.g. `pnpm test`. */
	parityCommand: string;
	/** Heading of the PAdES budget block. */
	budgetTitle: string;
	/** Sentence describing what the budget run does. */
	budgetHint: string;
	/** Placeholder of the PIN field. */
	pinPlaceholder: string;
	/** Label of the button that starts the budget run. */
	runBudget: string;
	/** Label of the button while the budget run is in flight. */
	running: string;
	/** Prefix of the progress line. */
	phase: string;
	/** Row label of the signing time. */
	signRow: string;
	/** Row label of the verification time. */
	verifyRow: string;
	/** Row label of the signed size. */
	signedRow: string;
	/** Row label of the verification verdict. */
	verifiedRow: string;
	/** Row label of the pass/fail verdict. */
	verdictRow: string;
	/** Value of the verification verdict when it held. */
	verifiedYes: string;
	/** Value of the verification verdict when it did not hold. */
	verifiedNo: string;
	/** Value of the pass/fail verdict when the run passed. */
	verdictPass: string;
	/** Value of the pass/fail verdict when the run failed. */
	verdictFail: string;
	/** The run's time against its budget, e.g. `671 ms (budget 30000 ms)`. */
	msBudget: string;
	/** The signed size in bytes, e.g. `52432788 bytes`. */
	bytes: string;
}

/** Strings only the desktop shell shows (`src-tauri/src/i18n.rs` embeds this same block). */
export interface NativeCopy {
	pinTitle: string;
	pinPrompt: string;
	pinRemember: string;
	/** `{count}` is replaced by the attempts left before the card locks. */
	pinAttemptsLeft: string;
	pinLocked: string;
	pinWrong: string;
	pinCancel: string;
	consentTitle: string;
	/** `{origin}` is replaced by the requesting website. */
	consentBody: string;
	consentOrigin: string;
	consentDigest: string;
	consentRemember: string;
	consentAllow: string;
	consentDeny: string;
	saveTitle: string;
	saveFilterPdf: string;
	saveFilterAll: string;
	saveOverwrite: string;
	updaterAvailable: string;
	updaterInstall: string;
	updaterDownloading: string;
	updaterRestart: string;
	updaterFailed: string;
	updaterCurrent: string;
	installerTitle: string;
	installerProgress: string;
	installerDone: string;
	installerFailed: string;
	installerRestart: string;
	toastSaveFailed: string;
	toastSaveCancelled: string;
	toastError: string;
}

export const copy: Record<Lang, Copy> = {
	es: {
		langLabel: 'Idioma',
		themeLabel: 'Tema',
		themeLight: 'Claro',
		themeDark: 'Oscuro',
		themeSystem: 'Sistema',
		brand: 'AutoFirma Web',
		privacyNote: 'Las claves privadas nunca salen de tu equipo: todo se firma en el navegador.',
		helpMenu: 'Ayuda',
		privacyTitle: 'Privacidad',
		sourceCode: 'Código fuente',
		diagnosticsLink: 'Diagnóstico del navegador',
		installApp: 'Instalar app',
		updateApp: 'Actualizar versión',
		pinMemoryTitle: 'PIN en memoria',
		pinMemoryHint:
			'La casilla junto al PIN lo recuerda solo en memoria (nunca en disco) durante el tiempo indicado. Cerrar el navegador lo borra. Si no la marcas, se pide cada vez.',
		pinMemoryMinutes: 'Minutos que se recuerda',
		settingsLabel: 'Ajustes y ayuda',
		settingsTitle: 'Ajustes y ayuda',
		settingsTab: 'Ajustes',
		mcpTab: 'MCP',
		colFile: 'Fichero',
		colActions: 'Acciones',
		step1Title: 'Configura la firma',
		dropTitle: 'Arrastra aquí los ficheros que quieres firmar',
		dropHint: 'o selecciónalos desde tu equipo',
		browse: 'Seleccionar ficheros',
		dragging: 'Suelta el fichero para cargarlo',
		dropOverlay: 'Suelta los ficheros aquí para añadirlos',
		remove: 'Quitar',
		step2Title: 'Elige los documentos',
		selectSign: 'Seleccionar firma',
		addSign: 'Agregar firma',
		certLoading: 'Descifrando el fichero…',
		certExpired: 'Este certificado está caducado; puedes firmar, pero puede ser rechazado.',
		certNotYetValid: 'Este certificado aún no es válido.',
		signIssuer: 'Emisor',
		signValidity: 'Vigencia',
		insecureContext:
			'Este navegador no permite la criptografía (WebCrypto) porque la página no es segura: se sirve por http. Ábrela por https o desde localhost.',
		certErrors: {
			'wrong-pin': 'PIN incorrecto.',
			'not-pkcs12': 'El fichero no es un PKCS#12 válido.',
			'unsupported-pbe': 'El fichero usa un cifrado que no se puede procesar. Vuelve a exportarlo o usa el almacén del sistema.',
			'unsupported-cipher': 'El fichero usa un cifrado que el navegador no admite.',
			'unsupported-key': 'La clave privada de este fichero no se puede usar en el navegador.',
			'no-cert': 'El fichero no contiene ningún certificado.',
			'no-key': 'El fichero no contiene ninguna clave privada.',
			'key-mismatch': 'La clave privada no corresponde a los certificados del fichero.',
			unknown: 'No se ha podido leer el fichero.'
		},
		formatTitle: 'Formato de firma',
		formatHint: 'Elige el tipo de firma digital que quieres generar',
		formatAll: 'para todo el lote — se puede cambiar por archivo',
		formatOoxmlNoVerify: 'OOXML se firma, no se verifica: la comprobación no está soportada (AutoFirma 1.9).',
		timeStampTitle: 'Sello de tiempo (TSA)',
		timeStampPlaceholder: 'https://tu-servidor-tsa/ruta',
		timeStampHint:
			'Opcional: con una TSA la firma lleva sello de tiempo (CAdES-T). Solo sale de tu equipo el hash de la firma, nunca el documento ni la clave.',
		pendingIssue: 'pendiente',
		visibleTitle: 'Bloque de firma visible',
		visibleHint: 'Dibuja tu firma en el PDF: imagen, nombre y fecha.',
		visibleImage: 'Imagen de la firma (PNG o JPEG)',
		visibleImageChoose: 'Elegir imagen',
		visibleImageInvalid: 'Solo se admiten imágenes PNG o JPEG.',
		visiblePage: 'Página',
		visibleCorner: 'Posición',
		visibleCorners: {
			'bottom-left': 'Abajo izq.',
			'bottom-right': 'Abajo dcha.',
			'top-left': 'Arriba izq.',
			'top-right': 'Arriba dcha.'
		},
		signedOn: 'Firmado el',
		signButton: 'Firmar',
		signNeedsFile: 'Selecciona primero un documento.',
		signNeedsCert: 'Selecciona un certificado para habilitar la firma.',
		signPendingFormat: 'Este formato llegará en otra entrega.',
		queueTitle: 'Cola de firma',
		queueProgress: 'Firmados',
		queueRunning: 'Firmando lote…',
		queueCancel: 'Cancelar',
		queueCancelled: 'Lote cancelado',
		queueStatus: {
			pending: 'Pendiente',
			signing: 'Firmando',
			signed: 'Firmada',
			error: 'Error'
		},
		queueHash: 'SHA-256',
		queueDownload: 'Descargar ZIP',
		queueArchive: 'firmas-lote.zip',
		downloadOne: 'Descargar',
		entryShared: 'Ficheros recibidos para firmar: revisa, elige certificado y confirma.',
		entryPortal: 'Solicitud de firma recibida',
		entryError: 'Solicitud de firma no válida',
		entryReturn: 'Devolver a',
		entryReturnWait: 'Tras firmar, podrás devolver el resultado al portal.',
		entryReturnReady: 'Firma lista. Revísala y devuélvela al portal con el botón.',
		entryReturnButton: 'Devolver al portal',
		modeSign: 'Firmar',
		checkLabels: {
			structure: 'Estructura de la firma',
			contentDigest: 'Integridad del contenido',
			signature: 'Firma criptográfica',
			reference: 'Referencia firmada',
			signerCert: 'Certificado del firmante',
			certValidity: 'Vigencia del certificado',
			certChain: 'Cadena de confianza',
			signingTime: 'Fecha de firma',
			timestamp: 'Sello de tiempo',
			unsupported: 'Formato no comprobable en este entorno'
		},
		storeTitle: 'Firmas guardadas',
		storeHint:
			'Guarda tu fichero PKCS#12 cifrado en este navegador para no volver a subirlo. El PIN no se escribe en disco; puedes recordarlo en memoria durante unos minutos si lo marcas al abrirlo.',
		storeEmpty: 'No hay ninguna firma guardada.',
		storeEphemeral:
			'Este navegador no puede guardar datos ahora mismo: las firmas se quedan solo en esta sesión. Cierra las otras pestañas de la app y recarga.',
		storeCurrent: 'En uso',
		storePin: 'PIN de la firma',
		rememberPin: 'Recordar el PIN {minutes} min (solo en memoria)',
		storeReset: 'Reiniciar el almacén',
		storeBack: 'Volver',
		storeSave: 'Guardar',
		storeUnlock: 'Usar',
		storeOpen: 'Abrir',
		storeExpiry: 'Caduca',
		storeAlias: 'Alias',
		storeRename: 'Cambiar nombre',
		storeErrors: {
			'wrong-pin': 'PIN incorrecto.',
			tampered: 'El PIN no es el correcto, o la firma guardada está alterada.',
			quota: 'No queda espacio en el navegador para guardar la firma.',
			unavailable: 'El almacén del navegador no está disponible.',
			missing: 'Esa firma ya no está guardada.'
		},
		stepConfigure: 'Configurar',
		stepFiles: 'Documentos',
		stepDownload: 'Descargar firmados',
		next: 'Siguiente',
		back: 'Atrás',
		newBatch: 'Nuevo lote',
		mcpMenu: 'Servidor MCP',
		mcpTitle: 'Servidor MCP remoto',
		mcpHint:
			'Si has desplegado autofirma-mcp (Streamable HTTP), guarda aquí su dirección para tenerla a mano al configurar un agente.',
		mcpUrlLabel: 'Dirección del endpoint',
		mcpUrlHint: 'Se copia tal cual en la configuración del agente, normalmente `https://tu-dominio/mcp`.',
		mcpTokenLabel: 'Token de acceso',
		mcpTokenHint:
			'Es el valor de AUTOFIRME_MCP_TOKEN del servidor: el agente lo envía como cabecera `Authorization: Bearer …`. Se guarda en este navegador y solo lo usa la extensión para conectar el puente MCP.',
		mcpCopy: 'Copiar dirección',
		mcpCopied: 'Copiado',
		mcpCopiedNote: 'Dirección copiada al portapapeles.',
		mcpBridgeTitle: 'Puente MCP (extensión)',
		mcpBridgeEnable: 'Permitir que los agentes MCP pidan firmas',
		mcpBridgeHint:
			'La extensión usa la dirección y el token de arriba (los del servidor) y abre aquí una ventana de confirmación por cada firma.',
		mcpBridgeConnect: 'Conectar',
		mcpBridgeOnline: 'Conectado al servidor MCP.',
		mcpBridgeOffline: 'Sin conexión con el servidor MCP.',
		mcpBridgeNeedsToken: 'Falta el token de acceso.',
		mcpBridgeBadEndpoint: 'La dirección debe ser http(s)://…/mcp, sin usuario ni contraseña.',
		mcpBridgeUnreachable: 'No se pudo abrir el puente: ¿está el servidor MCP en marcha y publicado por TLS?',
		mcpOrigin: 'Agente MCP',
		diagnostics: {
			title: 'Diagnóstico · AutoFirma',
			heading: 'Diagnóstico',
			secureContext: 'WebCrypto en este contexto:',
			webCryptoTitle: 'WebCrypto',
			probesRunning: 'Ejecutando…',
			probeOk: 'ok',
			probeFail: 'falla',
			parityTitle: 'Puerta de paridad de 12 puntos',
			parityHint:
				'Cada punto lo demuestra la suite que se nombra a su lado; ejecútalas con',
			parityCommand: 'pnpm test',
			budgetTitle: 'Diagnóstico · presupuesto PAdES',
			budgetHint:
				'Firma un PDF sintético en un Web Worker y lo verifica. El progreso de abajo demuestra que la interfaz sigue viva.',
			pinPlaceholder: 'PIN',
			runBudget: 'Ejecutar presupuesto',
			running: 'Ejecutando…',
			phase: 'Fase:',
			signRow: 'Firma',
			verifyRow: 'Verificación',
			signedRow: 'Firmado',
			verifiedRow: 'Verificado',
			verdictRow: 'Resultado',
			verifiedYes: 'sí',
			verifiedNo: 'NO',
			verdictPass: 'CORRECTO',
			verdictFail: 'FALLO',
			msBudget: '{ms} ms (presupuesto {budget})',
			bytes: '{bytes} bytes'
		},
		native: {
			pinTitle: 'Firma con tarjeta',
			pinPrompt: 'Introduce el PIN de tu tarjeta para firmar.',
			pinRemember: 'Recordar el PIN durante esta sesión',
			pinAttemptsLeft: 'Intentos restantes: {count}',
			pinLocked: 'La tarjeta está bloqueada. Desbloquéala con el PUK.',
			pinWrong: 'PIN incorrecto.',
			pinCancel: 'Cancelar',
			consentTitle: 'Solicitud de firma',
			consentBody: 'El sitio {origin} pide firmar. Confirma solo si reconoces la solicitud.',
			consentOrigin: 'Sitio solicitante',
			consentDigest: 'Resumen SHA-256',
			consentRemember: 'Permitir durante esta sesión',
			consentAllow: 'Firmar',
			consentDeny: 'Denegar',
			saveTitle: 'Guardar documento firmado',
			saveFilterPdf: 'Documento PDF',
			saveFilterAll: 'Todos los ficheros',
			saveOverwrite: 'Ya existe un fichero con ese nombre. ¿Quieres reemplazarlo?',
			updaterAvailable: 'Hay una versión nueva de AutoFirma.',
			updaterInstall: 'Descargar e instalar',
			updaterDownloading: 'Descargando la actualización…',
			updaterRestart: 'Reinicia para aplicar la actualización',
			updaterFailed: 'No se pudo actualizar. Inténtalo de nuevo más tarde.',
			updaterCurrent: 'Ya tienes la última versión.',
			installerTitle: 'Instalación de AutoFirma',
			installerProgress: 'Instalando…',
			installerDone: 'Instalación completada.',
			installerFailed: 'La instalación ha fallado.',
			installerRestart: 'Cierra la aplicación y vuelve a abrirla.',
			toastSaveFailed: 'No se ha podido guardar el fichero.',
			toastSaveCancelled: 'Guardado cancelado.',
			toastError: 'Se ha producido un error.',
		},
		afirmaSchemeOurs: 'Esta app gestiona los enlaces afirma://.',
		afirmaSchemeOther: 'Otra app gestiona los enlaces afirma://: solo funcionará la extensión.',
		afirmaSchemeNone: 'Ninguna app gestiona los enlaces afirma://.',
		afirmaTitle: 'Solicitudes de portales (afirma://)',
		afirmaHint: 'Cada solicitud se confirma una a una. Solo el dominio de esta solicitud puede recordarse, nunca todos.',
		afirmaOp: 'Operación',
		afirmaOrigin: 'Portal',
		afirmaNoOrigin: 'Sin origen comprobable: se rechaza.',
		afirmaReview: 'Revisar y firmar',
		afirmaDismiss: 'Descartar',
		afirmaRemember: 'Recordar este dominio',
		afirmaConfirmed: 'Solicitud confirmada: elige certificado y firma.',
		afirmaListTitle: 'Portales permitidos',
		afirmaListEmpty: 'Ningún portal permitido todavía.',
		afirmaRemove: 'Quitar'
	},
	en: {
		langLabel: 'Language',
		themeLabel: 'Theme',
		themeLight: 'Light',
		themeDark: 'Dark',
		themeSystem: 'System',
		brand: 'AutoFirma Web',
		privacyNote: 'Private keys never leave your machine: signing happens in the browser.',
		helpMenu: 'Help',
		privacyTitle: 'Privacy',
		sourceCode: 'Source code',
		diagnosticsLink: 'Browser diagnostics',
		installApp: 'Install app',
		updateApp: 'Update version',
		pinMemoryTitle: 'PIN in memory',
		pinMemoryHint:
			'The box next to the PIN keeps it in memory only (never on disk) for the time shown. Closing the browser clears it. Leave it unticked to ask every time.',
		pinMemoryMinutes: 'Minutes to remember it',
		settingsLabel: 'Settings and help',
		settingsTitle: 'Settings and help',
		settingsTab: 'Settings',
		mcpTab: 'MCP',
		colFile: 'File',
		remove: 'Remove',
		colActions: 'Actions',
		step1Title: 'Configure the signature',
		dropTitle: 'Drag the files you want to sign here',
		dropHint: 'or pick them from your machine',
		browse: 'Choose files',
		dragging: 'Drop the file to load it',
		dropOverlay: 'Drop the files here to add them',
		step2Title: 'Choose the documents',
		selectSign: 'Select signature',
		addSign: 'Add signature',
		certLoading: 'Decrypting the file…',
		certExpired: 'This certificate has expired; you can sign, but it may be rejected.',
		certNotYetValid: 'This certificate is not valid yet.',
		signIssuer: 'Issuer',
		signValidity: 'Validity',
		insecureContext:
			'This browser withholds WebCrypto because the page is not secure: it is served over http. Open it over https or from localhost.',
		certErrors: {
			'wrong-pin': 'Wrong PIN.',
			'not-pkcs12': 'The file is not a valid PKCS#12 container.',
			'unsupported-pbe': 'This file uses an encryption that cannot be processed. Re-export it, or use the system keystore.',
			'unsupported-cipher': 'This file uses a cipher the browser does not support.',
			'unsupported-key': 'The private key in this file cannot be used in a browser.',
			'no-cert': 'The file holds no certificate.',
			'no-key': 'The file holds no private key.',
			'key-mismatch': 'The private key does not match the certificates in the file.',
			unknown: 'The file could not be read.'
		},
		formatTitle: 'Signature format',
		formatHint: 'Pick the type of digital signature to generate',
		formatAll: 'for the whole batch — changeable per file',
		formatOoxmlNoVerify: 'OOXML signs, it does not verify: checking is unsupported (AutoFirma 1.9).',
		timeStampTitle: 'Trusted timestamp (TSA)',
		timeStampPlaceholder: 'https://your-tsa-server/path',
		timeStampHint:
			'Optional: with a TSA the signature carries a trusted timestamp (CAdES-T). Only the signature hash leaves your machine — never the document, never the key.',
		pendingIssue: 'pending',
		visibleTitle: 'Visible signature block',
		visibleHint: 'Draws your signature on the PDF: image, name and date.',
		visibleImage: 'Signature image (PNG or JPEG)',
		visibleImageChoose: 'Choose image',
		visibleImageInvalid: 'Only PNG or JPEG images are supported.',
		visiblePage: 'Page',
		visibleCorner: 'Position',
		visibleCorners: {
			'bottom-left': 'Bottom left',
			'bottom-right': 'Bottom right',
			'top-left': 'Top left',
			'top-right': 'Top right'
		},
		signedOn: 'Signed on',
		signButton: 'Sign',
		signNeedsFile: 'Choose a document first.',
		signNeedsCert: 'Choose a certificate to enable signing.',
		signPendingFormat: 'This format ships in a later delivery.',
		queueTitle: 'Signing queue',
		queueProgress: 'Signed',
		queueRunning: 'Signing batch…',
		queueCancel: 'Cancel',
		queueCancelled: 'Batch cancelled',
		queueStatus: {
			pending: 'Pending',
			signing: 'Signing',
			signed: 'Signed',
			error: 'Error'
		},
		queueHash: 'SHA-256',
		queueDownload: 'Download ZIP',
		queueArchive: 'signed-batch.zip',
		downloadOne: 'Download',
		entryShared: 'Files received for signing: review, pick a certificate and confirm.',
		entryPortal: 'Signature request received',
		entryError: 'Invalid signature request',
		entryReturn: 'Return to',
		entryReturnWait: 'After signing, you can hand the result back to the portal.',
		entryReturnReady: 'Signature ready. Review it and hand it back to the portal.',
		entryReturnButton: 'Return to portal',
		modeSign: 'Sign',
		checkLabels: {
			structure: 'Signature structure',
			contentDigest: 'Content integrity',
			signature: 'Cryptographic signature',
			reference: 'Signed reference',
			signerCert: 'Signer certificate',
			certValidity: 'Certificate validity',
			certChain: 'Trust chain',
			signingTime: 'Signing time',
			timestamp: 'Timestamp',
			unsupported: 'Format not checkable in this environment'
		},
		storeTitle: 'Saved signatures',
		storeHint:
			'Keep your PKCS#12 encrypted in this browser so you do not upload it again. The PIN is never written to disk; you can keep it in memory for a few minutes by ticking the box when opening it.',
		storeEmpty: 'No saved signature yet.',
		storeEphemeral:
			'This browser cannot store data right now: signatures stay in this session only. Close the other tabs of the app and reload.',
		storeCurrent: 'In use',
		storePin: 'PIN of the signature',
		rememberPin: 'Remember the PIN for {minutes} min (memory only)',
		storeReset: 'Reset the store',
		storeBack: 'Back',
		storeSave: 'Save',
		storeUnlock: 'Use',
		storeOpen: 'Open',
		storeExpiry: 'Expires',
		storeAlias: 'Alias',
		storeRename: 'Rename',
		storeErrors: {
			'wrong-pin': 'Wrong PIN.',
			tampered: 'The PIN is wrong, or the saved signature was altered.',
			quota: 'The browser has no room left to save the signature.',
			unavailable: 'The browser store is not available.',
			missing: 'That signature is no longer saved.'
		},
		stepConfigure: 'Configure',
		stepFiles: 'Documents',
		stepDownload: 'Download signed',
		next: 'Next',
		back: 'Back',
		newBatch: 'New batch',
		mcpMenu: 'MCP server',
		mcpTitle: 'Remote MCP server',
		mcpHint:
			'If you deployed autofirma-mcp (Streamable HTTP), keep its address here so it is at hand when you configure an agent.',
		mcpUrlLabel: 'Endpoint address',
		mcpUrlHint: 'Paste it as-is into the agent configuration, normally `https://your-domain/mcp`.',
		mcpTokenLabel: 'Access token',
		mcpTokenHint:
			'The server\'s AUTOFIRME_MCP_TOKEN: the agent sends it as an `Authorization: Bearer …` header. It is kept in this browser and only the extension uses it to connect the MCP bridge.',
		mcpCopy: 'Copy address',
		mcpCopied: 'Copied',
		mcpCopiedNote: 'Address copied to the clipboard.',
		mcpBridgeTitle: 'MCP bridge (extension)',
		mcpBridgeEnable: 'Let MCP agents request signatures',
		mcpBridgeHint:
			'The extension uses the address and token above (the server\'s) and opens a confirmation window here for every signature.',
		mcpBridgeConnect: 'Connect',
		mcpBridgeOnline: 'Connected to the MCP server.',
		mcpBridgeOffline: 'Not connected to the MCP server.',
		mcpBridgeNeedsToken: 'The access token is missing.',
		mcpBridgeBadEndpoint: 'The address must be http(s)://…/mcp, without a user or password.',
		mcpBridgeUnreachable: 'Could not open the bridge: is the MCP server running and published over TLS?',
		mcpOrigin: 'MCP agent',
		diagnostics: {
			title: 'Diagnostics · AutoFirma',
			heading: 'Diagnostics',
			secureContext: 'WebCrypto under this context:',
			webCryptoTitle: 'WebCrypto',
			probesRunning: 'Running…',
			probeOk: 'ok',
			probeFail: 'fails',
			parityTitle: '12-item parity gate',
			parityHint: 'Each item is proven by the suite named next to it; run them with',
			parityCommand: 'pnpm test',
			budgetTitle: 'Diagnostics · PAdES budget',
			budgetHint:
				'Signs a synthetic PDF in a Web Worker, then verifies it. Progress below proves the UI stays alive.',
			pinPlaceholder: 'PIN',
			runBudget: 'Run budget',
			running: 'Running…',
			phase: 'Phase:',
			signRow: 'Sign',
			verifyRow: 'Verify',
			signedRow: 'Signed',
			verifiedRow: 'Verified',
			verdictRow: 'Verdict',
			verifiedYes: 'yes',
			verifiedNo: 'NO',
			verdictPass: 'PASS',
			verdictFail: 'FAIL',
			msBudget: '{ms} ms (budget {budget})',
			bytes: '{bytes} bytes'
		},
		native: {
			pinTitle: 'Sign with card',
			pinPrompt: 'Enter your card PIN to sign.',
			pinRemember: 'Remember the PIN for this session',
			pinAttemptsLeft: 'Attempts left: {count}',
			pinLocked: 'The card is locked. Unlock it with the PUK.',
			pinWrong: 'Wrong PIN.',
			pinCancel: 'Cancel',
			consentTitle: 'Signature request',
			consentBody: '{origin} asks to sign. Confirm only if you recognise the request.',
			consentOrigin: 'Requesting website',
			consentDigest: 'SHA-256 digest',
			consentRemember: 'Allow for this session',
			consentAllow: 'Sign',
			consentDeny: 'Deny',
			saveTitle: 'Save signed document',
			saveFilterPdf: 'PDF document',
			saveFilterAll: 'All files',
			saveOverwrite: 'A file with that name already exists. Replace it?',
			updaterAvailable: 'A new version of AutoFirma is available.',
			updaterInstall: 'Download and install',
			updaterDownloading: 'Downloading the update…',
			updaterRestart: 'Restart to apply the update',
			updaterFailed: 'The update failed. Try again later.',
			updaterCurrent: 'You are on the latest version.',
			installerTitle: 'AutoFirma installation',
			installerProgress: 'Installing…',
			installerDone: 'Installation complete.',
			installerFailed: 'The installation failed.',
			installerRestart: 'Close the application and open it again.',
			toastSaveFailed: 'The file could not be saved.',
			toastSaveCancelled: 'Save cancelled.',
			toastError: 'Something went wrong.',
		},
		afirmaSchemeOurs: 'This app handles afirma:// links.',
		afirmaSchemeOther: 'Another app handles afirma:// links: only the extension will work.',
		afirmaSchemeNone: 'No app handles afirma:// links.',
		afirmaTitle: 'Portal requests (afirma://)',
		afirmaHint: 'Each request is confirmed one by one. Only this request\u2019s domain can be remembered, never all.',
		afirmaOp: 'Operation',
		afirmaOrigin: 'Portal',
		afirmaNoOrigin: 'No verifiable origin: refused.',
		afirmaReview: 'Review and sign',
		afirmaDismiss: 'Dismiss',
		afirmaRemember: 'Remember this domain',
		afirmaConfirmed: 'Request confirmed: pick a certificate and sign.',
		afirmaListTitle: 'Allowed portals',
		afirmaListEmpty: 'No allowed portals yet.',
		afirmaRemove: 'Remove'
	}
};
