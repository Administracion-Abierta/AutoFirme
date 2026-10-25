/** A request can delegate only while it is still waiting for consent. */
export class RequestState {
  state = 'waiting';
  start() {
    if (this.state !== 'waiting') return false;
    this.state = 'signing';
    return true;
  }
  finish(kind) {
    if (!['waiting', 'signing'].includes(this.state)) return false;
    if (!['result', 'fallback', 'cancel', 'error'].includes(kind)) return false;
    if (kind === 'result' && this.state !== 'signing') return false;
    // Cancel is terminal and never replays the original, so it stays valid while signing: a
    // chooser reloaded mid-signing must have an exit. Fallback would re-run the original call and
    // risks a double signature, so it remains waiting-only.
    if (this.state === 'signing' && kind === 'fallback') return false;
    this.state = kind;
    return true;
  }
}
