/**
 * Web Worker side of the signing engine (Refs #94). No DOM here — only the
 * typed protocol from `./engine` plus the sign/verify cores.
 */

import { runEngineSync, type EngineRequest } from './engine';

type Incoming = EngineRequest & { id: number };

/** Minimal worker scope — avoids pulling the `WebWorker` lib into the app tsconfig. */
interface WorkerScope {
	postMessage(message: unknown, transfer?: Transferable[]): void;
	onmessage: ((event: MessageEvent<Incoming>) => void) | null;
}

const scope = self as unknown as WorkerScope;

scope.onmessage = async (event: MessageEvent<Incoming>) => {
	const { id, ...request } = event.data;
	const progress = (phase: string, fraction: number | null): void =>
		scope.postMessage({ id, phase, fraction });
	try {
		progress(request.kind === 'sign-pades' ? 'load' : 'verify', null);
		const result = await runEngineSync(request);
		progress('done', 1);
		if (result.kind === 'sign-pades') {
			scope.postMessage({ id, ok: true as const, result }, [result.signed]);
		} else {
			scope.postMessage({ id, ok: true as const, result });
		}
	} catch (error) {
		scope.postMessage({ id, ok: false as const, error: (error as Error).message });
	}
};
