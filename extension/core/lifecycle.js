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
    if (this.state === 'signing' && ['fallback', 'cancel'].includes(kind)) return false;
    this.state = kind;
    return true;
  }
}
