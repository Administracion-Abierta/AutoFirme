/**
 * Optional PIN memory for the signing picker.
 *
 * The PIN is never written to disk. In the extension it lives in `chrome.storage.session`, which
 * Chrome keeps in memory and clears when the browser closes; elsewhere it is a module-level Map.
 * The duration is a setting; the checkbox next to each PIN prompt decides whether that PIN goes in.
 */

export interface PinMemorySettings {
	/** Minutes a remembered PIN stays usable. */
	minutes: number;
	/** Whether the picker's PIN prompts start checked. */
	remember: boolean;
}

const SETTINGS_KEY = 'autofirme-pin-memory';
const PINS_KEY = 'autofirme-pin-memory-pins';
export const DEFAULT_MINUTES = 10;

const EMPTY: PinMemorySettings = { minutes: DEFAULT_MINUTES, remember: false };
const memory = new Map<string, { pin: string; expires: number }>();

interface SessionArea {
	get(key: string): Promise<Record<string, unknown>>;
	set(items: Record<string, unknown>): Promise<void>;
}

/** `chrome.storage.session` when running as an extension; the API is absent in the web build. */
function sessionArea(): SessionArea | null {
	const chrome = (globalThis as typeof globalThis & {
		chrome?: { storage?: { session?: SessionArea } };
	}).chrome;
	return chrome?.storage?.session ?? null;
}

function validMinutes(value: unknown): number | null {
	return typeof value === 'number' && Number.isInteger(value) && value >= 1 && value <= 120
		? value
		: null;
}

export function readPinMemorySettings(): PinMemorySettings {
	try {
		const raw = localStorage.getItem(SETTINGS_KEY);
		if (!raw) return EMPTY;
		const parsed: unknown = JSON.parse(raw);
		if (typeof parsed !== 'object' || parsed === null) return EMPTY;
		const { minutes, remember } = parsed as Partial<PinMemorySettings>;
		return {
			minutes: validMinutes(minutes) ?? DEFAULT_MINUTES,
			remember: remember === true
		};
	} catch {
		return EMPTY;
	}
}

export function savePinMemorySettings(settings: PinMemorySettings): void {
	try {
		const minutes = validMinutes(settings.minutes) ?? DEFAULT_MINUTES;
		localStorage.setItem(SETTINGS_KEY, JSON.stringify({ minutes, remember: settings.remember === true }));
	} catch {
		// Prerender or a storage-denied browser: the checkbox still works for this page.
	}
}

function live(entry: { pin: string; expires: number } | undefined): string | null {
	return entry && entry.expires > Date.now() ? entry.pin : null;
}

async function readPins(): Promise<Record<string, { pin: string; expires: number }>> {
	const area = sessionArea();
	if (!area) return Object.fromEntries(memory);
	try {
		const stored = (await area.get(PINS_KEY))[PINS_KEY];
		return typeof stored === 'object' && stored !== null
			? (stored as Record<string, { pin: string; expires: number }>)
			: {};
	} catch {
		return Object.fromEntries(memory);
	}
}

async function writePins(pins: Record<string, { pin: string; expires: number }>): Promise<void> {
	const now = Date.now();
	for (const [id, entry] of Object.entries(pins)) if (!live(entry)) delete pins[id];
	const area = sessionArea();
	if (!area) {
		memory.clear();
		for (const [id, entry] of Object.entries(pins)) memory.set(id, entry);
		return;
	}
	try {
		await area.set({ [PINS_KEY]: pins });
	} catch {
		// A suspended worker or a denied storage area: the PIN simply is not remembered.
	}
}

export async function rememberedPin(id: string): Promise<string | null> {
	const pin = live((await readPins())[id]);
	if (pin === null) await forgetPin(id);
	return pin;
}

export async function rememberPin(id: string, pin: string): Promise<void> {
	if (pin === '') return;
	const minutes = readPinMemorySettings().minutes;
	const pins = await readPins();
	pins[id] = { pin, expires: Date.now() + minutes * 60_000 };
	await writePins(pins);
}

export async function forgetPin(id: string): Promise<void> {
	const pins = await readPins();
	if (!(id in pins)) return;
	delete pins[id];
	await writePins(pins);
}
