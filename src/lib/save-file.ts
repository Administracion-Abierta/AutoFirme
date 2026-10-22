/**
 * Save bytes the user already holds: native save dialog inside the Tauri
 * shell, blob-anchor download everywhere else (browser build, prerender).
 *
 * The Tauri client is lazy-imported behind the `__TAURI_INTERNALS__` probe
 * so the browser bundle never loads it (static import would drag the client
 * into the web build). Bytes cross IPC as a number array —
 * `Channel<ArrayBuffer>` only carries the reply direction, so the request
 * side goes through the real client, never a hand-rolled `invoke`.
 */

/**
 * Persist `bytes` under `name`. Returns the chosen path on desktop, `null`
 * when the user cancels. Never throws for a cancel; browser fallback is the
 * pre-existing anchor download.
 */
export async function saveBytes(
	name: string,
	bytes: Uint8Array<ArrayBuffer>,
	type: string
): Promise<string | null> {
	if (typeof window !== 'undefined' && '__TAURI_INTERNALS__' in window) {
		const { invoke } = await import('@tauri-apps/api/core');
		// ponytail: Array.from copies the buffer; fine — saves are user-paced,
		// not hot loops. Binary IPC (Channel<ArrayBuffer>) only covers replies.
		return await invoke<string | null>('save_file', {
			suggestedName: name,
			bytes: Array.from(bytes)
		});
	}
	const url = URL.createObjectURL(new Blob([bytes as BlobPart], { type }));
	const link = document.createElement('a');
	link.href = url;
	link.download = name;
	link.click();
	URL.revokeObjectURL(url);
	return null;
}
