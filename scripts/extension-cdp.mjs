// Small CDP client for the disposable unpacked-extension test browser.
export class CDP {
  next=0; pending=new Map(); errors=[];
  constructor(socket){
    this.socket=socket;
    socket.addEventListener('message',event=>{
      const response=JSON.parse(event.data);if(!response.id){if(response.method==='Runtime.exceptionThrown')this.errors.push(response.params.exceptionDetails.exception?.description || response.params.exceptionDetails.text);return;}
      const item=this.pending.get(response.id);if(!item)return;
      this.pending.delete(response.id);clearTimeout(item.timer);
      if(response.error)item.reject(Error(item.method+': '+response.error.message));else item.resolve(response.result);
    });
  }
  static async connect(url){
    const socket=new WebSocket(url);
    await new Promise((resolve,reject)=>{socket.addEventListener('open',resolve,{once:true});socket.addEventListener('error',()=>reject(Error('Cannot connect to test Chromium')),{once:true});});
    return new CDP(socket);
  }
  send(method,params={},sessionId){return new Promise((resolve,reject)=>{
    const id=++this.next;const timer=setTimeout(()=>{this.pending.delete(id);reject(Error('CDP timeout: '+method));},15_000);
    this.pending.set(id,{method,resolve,reject,timer});this.socket.send(JSON.stringify({id,method,params,...(sessionId?{sessionId}:{})}));
  });}
  async attach(targetId){const {sessionId}=await this.send('Target.attachToTarget',{targetId,flatten:true});await this.send('Runtime.enable',{},sessionId);await this.send('Page.enable',{},sessionId);return sessionId;}
  async evaluate(session,expression){
    const response=await this.send('Runtime.evaluate',{expression,returnByValue:true,awaitPromise:true},session);
    if(response.exceptionDetails)throw Error('Test page evaluation failed: '+(response.exceptionDetails.exception?.description || response.exceptionDetails.text));
    return response.result.value;
  }
  async wait(session,expression){
    for(let i=0;i<600;i++){
      if(await this.evaluate(session,expression))return;
      await new Promise(resolve=>setTimeout(resolve,100));
    }
    throw Error('Test page condition timed out: '+expression);
  }
  close(){for(const item of this.pending.values()){clearTimeout(item.timer);item.reject(Error('Test browser closed'));}this.pending.clear();this.socket.close();}
}
