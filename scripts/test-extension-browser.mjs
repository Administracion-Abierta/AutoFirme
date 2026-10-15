// Unpacked MV3 testing needs its own Chromium profile and launch flags unavailable
// in the collaborative preview. CHROME_PATH can select a local Chrome binary.
import {spawn, spawnSync} from 'node:child_process';
import {createServer} from 'node:http';
import {mkdtemp, cp, readFile, writeFile, rm, readdir, access} from 'node:fs/promises';
import {tmpdir, homedir} from 'node:os';
import {join, resolve} from 'node:path';
import {createHash} from 'node:crypto';
import assert from 'node:assert/strict';
import {CDP} from './extension-cdp.mjs';

const root = resolve('dist/extension');
await access(join(root, 'index.html')); // A web build alone does not satisfy this test.
const directory = await mkdtemp(join(tmpdir(), 'autofirme-browser-'));
let processHandle, cdp, server, mainSession;
try {
  const extension = join(directory, 'extension');
  await cp(root, extension, {recursive: true});
  const manifest = JSON.parse(await readFile(join(extension, 'manifest.json'), 'utf8'));
  assert.equal(manifest.manifest_version, 3);
  assert.equal(manifest.action.default_popup, undefined, 'app must survive focus changes');
  assert.deepEqual(manifest.host_permissions, ['http://*/*','https://*/*'], 'all websites enabled on installation');
  let executable = process.env.CHROME_PATH;
  if (!executable) {
    const cache = join(homedir(), '.cache/ms-playwright');
    const entries = (await readdir(cache)).filter(name => /^chromium-\d+$/.test(name)).sort((a,b) => Number(b.split('-')[1])-Number(a.split('-')[1]));
    for (const entry of entries) {
      for (const suffix of ['chrome-linux64/chrome', 'chrome-linux/chrome']) {
        const candidate = join(cache, entry, suffix);
        try { await access(candidate); executable = candidate; break; } catch {}
      }
      if (executable) break;
    }
  }
  if (!executable) throw Error('Set CHROME_PATH to Chrome/Chromium or install Chromium with Playwright.');
  const fixture = await readFile('test/extension-site.html');
  server = createServer((request, response) => {response.writeHead(200, {'content-type': 'text/html; charset=utf-8'}); response.end(fixture);});
  await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
  const origin = `http://127.0.0.1:${server.address().port}`;
  processHandle = spawn(executable, ['--headless=new','--no-proxy-server','--no-sandbox','--disable-dev-shm-usage','--no-first-run','--remote-debugging-port=0',`--user-data-dir=${join(directory,'profile')}`,`--disable-extensions-except=${extension}`,`--load-extension=${extension}`,'about:blank'], {stdio:'ignore'});
  let endpoint;
  for (let i=0;i<100;i++) {try {const [port,path]=(await readFile(join(directory,'profile/DevToolsActivePort'),'utf8')).trim().split('\n'); endpoint=`ws://127.0.0.1:${port}${path}`; break;} catch {await new Promise(resolve=>setTimeout(resolve,100));}}
  if (!endpoint) throw Error('Chromium did not start');
  cdp = await CDP.connect(endpoint);
  const id = createHash('sha256').update(extension).digest('hex').slice(0,32).replace(/[0-9a-f]/g,c=>String.fromCharCode(97+parseInt(c,16)));
  const appURL = `chrome-extension://${id}/index.html`;
  const open = async url => {const {targetId}=await cdp.send('Target.createTarget',{url}); return {targetId,session:await cdp.attach(targetId)};};
  const fixtureKey = join(directory, 'fixture.key'), fixtureCert = join(directory, 'fixture.pem'), p12 = join(directory, 'fixture.p12');
  const openssl = args => {const result=spawnSync('openssl',args); assert.equal(result.status,0,result.stderr.toString()); return result;};
  openssl(['req','-x509','-newkey','rsa:2048','-nodes','-sha256','-days','2','-subj','/C=ES/O=Local non-production fixture/CN=AutoFirme Browser Test','-addext','basicConstraints=critical,CA:FALSE','-addext','keyUsage=critical,digitalSignature,nonRepudiation','-keyout',fixtureKey,'-out',fixtureCert]);
  openssl(['pkcs12','-export','-inkey',fixtureKey,'-in',fixtureCert,'-passout','pass:fixture-password','-out',p12]);
  const main = await open(appURL); mainSession=main.session;
  await cdp.wait(main.session, `document.querySelector('h1')?.textContent.includes('AutoFirma')`);
  assert.equal(await cdp.evaluate(main.session, 'location.protocol'), 'chrome-extension:');
  assert.ok(await cdp.evaluate(main.session, "document.body.textContent.includes('CAdES')"));
  const click = (session, selector) => cdp.evaluate(session, `document.querySelector(${JSON.stringify(selector)}).click()`);
  const set = (session, selector, value) => cdp.evaluate(session, `(()=>{const el=document.querySelector(${JSON.stringify(selector)});el.value=${JSON.stringify(value)};el.dispatchEvent(new Event('input',{bubbles:true}));})()`);
  const enter = (session, selector) => cdp.evaluate(session, `document.querySelector(${JSON.stringify(selector)}).dispatchEvent(new KeyboardEvent('keydown',{key:'Enter',bubbles:true}))`);
  await click(main.session, '#pkcs12-picker');
  const doc = await cdp.send('DOM.getDocument', {}, main.session);
  const fileInput = await cdp.send('DOM.querySelector',{nodeId:doc.root.nodeId,selector:'#pkcs12-file'},main.session);
  await cdp.send('DOM.setFileInputFiles',{nodeId:fileInput.nodeId,files:[p12]},main.session);
  await cdp.wait(main.session, 'Boolean(document.querySelector("#add-pin"))');
  await set(main.session, '#add-pin', 'fixture-password');
  await enter(main.session, '#add-pin');
  await cdp.wait(main.session, `document.querySelector('#pkcs12-picker')?.textContent.includes('AutoFirme Browser Test') && !document.querySelector('[role=dialog][data-state=open]')`);
  process.stdout.write('PASS: actual AutoFirme webapp certificate import and encrypted identity storage.\n');
  // Responsive app keeps all content within its viewport, including file/config controls.
  for (const width of [420, 320, 1280]) {
    await cdp.send('Emulation.setDeviceMetricsOverride',{width,height:900,deviceScaleFactor:1,mobile:false},main.session);
    await cdp.wait(main.session, `window.innerWidth===${width}`);
    assert.ok(await cdp.evaluate(main.session, 'document.documentElement.scrollWidth<=window.innerWidth+1'), `full app overflows at ${width}px`);
  }
  await cdp.send('Emulation.clearDeviceMetricsOverride',{},main.session);
  process.stdout.write('PASS: full app responsive at 320px, 420px and 1280px.\n');
  async function upload(session,selector,files) {
    const doc=await cdp.send('DOM.getDocument',{},session);
    const node=await cdp.send('DOM.querySelector',{nodeId:doc.root.nodeId,selector},session);
    assert.ok(node.nodeId, `missing file input ${selector}`);
    await cdp.send('DOM.setFileInputFiles',{nodeId:node.nodeId,files},session);
  }
  await cdp.evaluate(main.session, `[...document.querySelectorAll('button[aria-haspopup=dialog]')].find(b=>b.textContent.includes('CAdES-BES')).click()`);
  await cdp.wait(main.session, 'Boolean(document.querySelector("input[name=format][value=pades]"))');
  await click(main.session,'input[name=format][value=pades]');
  await cdp.wait(main.session,'Boolean(document.querySelector("#visible-signature"))');
  await click(main.session,'#visible-signature');
  assert.equal(await cdp.evaluate(main.session, 'document.querySelector("#visible-signature").getAttribute("aria-checked")'),'true');
  const source=resolve('test/fixtures/pades-source.pdf'), second=join(directory,'second.pdf');
  await cp(source,second);
  await upload(main.session,'#panel-sign input[type=file][multiple]',[source,second]);
  await cdp.wait(main.session, 'document.querySelector("button[aria-describedby=sign-hint]")?.disabled===false');
  await click(main.session,'button[aria-describedby=sign-hint]');
  const outputQuery=`new Promise((resolve,reject)=>{const open=indexedDB.open('autofirme-web',2);open.onerror=()=>reject(Error('test output DB unavailable'));open.onsuccess=()=>{const db=open.result;const tx=db.transaction('outputs');const q=tx.objectStore('outputs').getAll();q.onsuccess=()=>{const rows=q.result;db.close();resolve(rows.map(r=>({name:r.value.name,bytes:Array.from(r.value.bytes)})));};q.onerror=()=>reject(Error('test outputs unavailable'));};})`;
  await cdp.wait(main.session, `(${outputQuery}).then(rows=>rows.length===2)`);
  const outputs=await cdp.evaluate(main.session,outputQuery);
  let producedPDF;
  for(const record of outputs) {
    const pdf=Buffer.from(record.bytes), text=pdf.toString('latin1');
    const ranges=[...text.matchAll(/\/ByteRange\s*\[\s*(\d+)\s+(\d+)\s+(\d+)\s+(\d+)\s*\]/g)].at(-1);
    assert.ok(ranges,'actual app must generate a PDF signature');
    const [a,b,c,d]=ranges.slice(1).map(Number);
    const signature=Buffer.from([...text.matchAll(/\/Contents\s*<([a-fA-F0-9]+)>/g)].at(-1)[1],'hex');
    const contentPath=join(directory,'pdf-content'),sigPath=join(directory,'pdf-signature.der');
    await writeFile(contentPath,Buffer.concat([pdf.subarray(a,a+b),pdf.subarray(c,c+d)]));
    await writeFile(sigPath,signature);
    openssl(['cms','-verify','-cades','-binary','-inform','DER','-in',sigPath,'-content',contentPath,'-CAfile',fixtureCert,'-purpose','any','-out',join(directory,'pdf-verified')]);
    producedPDF=join(directory,'signed.pdf');await writeFile(producedPDF,pdf);
  }
  await cdp.send('Browser.setDownloadBehavior',{behavior:'allow',downloadPath:directory});
  await cdp.evaluate(main.session, `[...document.querySelectorAll('button')].find(b=>b.textContent.includes('Descargar ZIP')).click()`);
  let archive;
  for(let i=0;i<100;i++){archive=(await readdir(directory)).find(name=>name.endsWith('.zip'));if(archive)break;await new Promise(r=>setTimeout(r,100));}
  assert.ok(archive,'batch ZIP download');
  const zipCheck=spawnSync('unzip',['-t',join(directory,archive)]);assert.equal(zipCheck.status,0,zipCheck.stdout.toString()+zipCheck.stderr.toString());
  await click(main.session,'[role=tab][aria-controls=panel-verify]');
  await cdp.wait(main.session,'Boolean(document.querySelector("#panel-verify input[type=file]"))');
  await upload(main.session,'#panel-verify input[type=file]',[producedPDF]);
  await cdp.wait(main.session, 'document.querySelector("[data-check=signature]")?.dataset.status==="pass"');
  assert.equal(await cdp.evaluate(main.session,'document.querySelector("[data-check=structure]").dataset.status'),'pass');
  process.stdout.write('PASS: full app PDF stamps, two-document batch signing, saved outputs, valid ZIP and PDF verification; OpenSSL checks both signatures.\n');
  // No sites:set: the first website request must work with fresh storage.
  const page = await open(origin);
  await cdp.wait(page.session, 'Boolean(window.fixture)');
  async function picker(selector) {
    await click(page.session, selector);
    for (let i=0;i<200;i++) {
      const {targetInfos}=await cdp.send('Target.getTargets');
      const target=targetInfos.find(t=>t.type==='page'&&t.url.startsWith(appURL+'?request='));
      if (target) {const item={targetId:target.targetId,session:await cdp.attach(target.targetId)}; await cdp.wait(item.session,`document.querySelector('#request-origin')?.textContent.trim()===${JSON.stringify(origin)}`); return item;}
      await new Promise(resolve=>setTimeout(resolve,100));
    }
    throw Error('No certificate chooser opened');
  }
  async function unlockChooser(chooser) {
    await cdp.wait(chooser.session, 'Boolean(document.querySelector("#pkcs12-choices") || document.querySelector("#pkcs12-picker"))');
    if (!await cdp.evaluate(chooser.session, 'Boolean(document.querySelector("#pkcs12-choices"))')) await click(chooser.session, '#pkcs12-picker');
    await cdp.wait(chooser.session, `Boolean([...document.querySelectorAll('button[aria-expanded]')].find(b=>b.textContent.includes('AutoFirme Browser Test')))`);
    await cdp.evaluate(chooser.session, `[...document.querySelectorAll('button[aria-expanded]')].find(b=>b.textContent.includes('AutoFirme Browser Test')).click()`);
    await cdp.wait(chooser.session, 'Boolean(document.querySelector("#store-pin"))');
    await set(chooser.session,'#store-pin','fixture-password');
    await enter(chooser.session,'#store-pin');
    await cdp.wait(chooser.session, `document.querySelector('#request-confirm') && !document.querySelector('#request-confirm').disabled && !document.querySelector('[role=dialog][data-state=open]')`);
  }
  for (const mode of ['implicit','explicit']) {
    await set(page.session,'#mode',mode);
    const chooser=await picker('#sign');
    await unlockChooser(chooser);
    if(mode==='implicit') {
      for(const width of [320,420]) {
        await cdp.send('Emulation.setDeviceMetricsOverride',{width,height:900,deviceScaleFactor:1,mobile:false},chooser.session);
        await cdp.wait(chooser.session,`window.innerWidth===${width}`);
        assert.ok(await cdp.evaluate(chooser.session,'document.documentElement.scrollWidth<=window.innerWidth+1'),`chooser overflows at ${width}px`);
      }
      await cdp.send('Emulation.clearDeviceMetricsOverride',{},chooser.session);
    }
    assert.equal(await cdp.evaluate(page.session,'window.fixture.results.length'),0,'selection alone cannot sign');
    await click(chooser.session,'#request-confirm');
    await cdp.wait(page.session,'Array.isArray(window.fixture.results.at(-1))');
    const result=await cdp.evaluate(page.session,'window.fixture.results.at(-1)');
    assert.equal(result.length,3); assert.equal(result[2],null);
    const signature=join(directory,'signature.der'), content=join(directory,'content');
    await writeFile(signature,Buffer.from(result[0],'base64'));
    await writeFile(content,await cdp.evaluate(page.session,"document.querySelector('#document').value"));
    openssl(['cms','-verify','-cades','-binary','-inform','DER','-in',signature,'-CAfile',fixtureCert,'-purpose','any','-out',join(directory,'verified'),...(mode==='explicit'?['-content',content]:[])]);
    assert.equal(await cdp.evaluate(page.session,'window.fixture.fallbackCalls'),0);
    await cdp.evaluate(page.session,'window.fixture.results=[]');
  }
  process.stdout.write('PASS: chooser unlock, explicit consent and attached/detached CAdES verified with OpenSSL -cades.\n');
  let selected=await picker('#select'); await unlockChooser(selected); await click(selected.session,'#request-confirm');
  await cdp.wait(page.session,'Array.isArray(window.fixture.results.at(-1))');
  assert.equal((await cdp.evaluate(page.session,'window.fixture.results.at(-1)')).length,1);
  await cdp.evaluate(page.session,'window.fixture.results=[]');
  let explicit=await picker('#sign');
  // Cancel picker modal to expose request action buttons if it opens automatically.
  await cdp.evaluate(explicit.session, `document.querySelector('[role=dialog][data-state=open] button[data-dialog-close]')?.click()`);
  await click(explicit.session,'#request-fallback');
  await cdp.wait(page.session,'window.fixture.fallbackCalls===1');

  let chooser = await picker('#sign');
  assert.equal(await cdp.evaluate(chooser.session,"document.querySelector('#request-origin').textContent.trim()"), origin);
  assert.equal(await cdp.evaluate(page.session, 'window.fixture.results.length'), 0, 'opening chooser must not sign');
  await cdp.send('Target.closeTarget',{targetId:chooser.targetId});
  await cdp.wait(page.session, 'window.fixture.fallbackCalls===2');
  const cancelled=await picker('#sign'); await click(cancelled.session,'#request-cancel');
  await cdp.wait(page.session, "window.fixture.results.at(-1)?.type==='es.gob.afirma.core.AOCancelledOperationException'");
  assert.equal(await cdp.evaluate(page.session,'window.fixture.fallbackCalls'),2);
  await click(page.session, '#unsupported');
  assert.equal(await cdp.evaluate(page.session, 'window.fixture.fallbackCalls'), 3);
  const chooserTargets=async()=>(await cdp.send('Target.getTargets')).targetInfos.filter(t=>t.type==='page'&&t.url.startsWith(appURL+'?request='));
  async function waitChoosers(count){for(let i=0;i<200;i++){const items=await chooserTargets();if(items.length===count)return items;await new Promise(r=>setTimeout(r,50));}throw Error('Unexpected pending chooser count '+count);}
  await waitChoosers(0);
  await cdp.evaluate(page.session,'window.fixture.results=[]');
  await set(page.session,'#document','Concurrent request A');await click(page.session,'#sign');
  await set(page.session,'#document','Concurrent request B');await click(page.session,'#sign');
  const concurrent=await waitChoosers(2);
  const requests=[];
  for(const target of concurrent){const item={targetId:target.targetId,session:await cdp.attach(target.targetId)};await cdp.wait(item.session,'Boolean(document.querySelector("#request-origin"))');const value=await cdp.evaluate(item.session,`chrome.runtime.sendMessage({type:'request:get',id:new URLSearchParams(location.search).get('request')})`);assert.equal(value.ok,true);requests.push({...item,data:Buffer.from(value.value.data,'base64').toString()});}
  const first=requests.find(r=>r.data==='Concurrent request A'), secondRequest=requests.find(r=>r.data==='Concurrent request B');
  assert.ok(first && secondRequest);
  const leaked=await cdp.evaluate(main.session,`chrome.runtime.sendMessage({type:'request:get',id:${JSON.stringify(new URL(concurrent[0].url).searchParams.get('request'))}})`);
  assert.equal(leaked.ok,false,'other app tab cannot retrieve chooser request');
  await click(first.session,'#request-cancel');await waitChoosers(1);
  await unlockChooser(secondRequest);await click(secondRequest.session,'#request-confirm');
  await cdp.wait(page.session,'window.fixture.results.length===2');
  const isolated=await cdp.evaluate(page.session,'window.fixture.results');
  assert.equal(isolated[0].type,'es.gob.afirma.core.AOCancelledOperationException');assert.ok(Array.isArray(isolated[1]));
  await writeFile(join(directory,'concurrent.der'),Buffer.from(isolated[1][0],'base64'));
  await writeFile(join(directory,'concurrent-content'),'Concurrent request B');
  openssl(['cms','-verify','-cades','-binary','-inform','DER','-in',join(directory,'concurrent.der'),'-content',join(directory,'concurrent-content'),'-CAfile',fixtureCert,'-purpose','any','-out',join(directory,'concurrent-verified')]);
  await waitChoosers(0);
  await picker('#sign');
  await cdp.send('Page.navigate',{url:origin+'?fresh=1'},page.session);
  await cdp.wait(page.session, 'location.search==="?fresh=1" && Boolean(window.fixture)');
  await waitChoosers(0);
  assert.equal(await cdp.evaluate(page.session,'window.fixture.results.length'),0,'navigation cannot deliver old results');
  assert.equal(await cdp.evaluate(page.session,'window.fixture.fallbackCalls'),0,'navigation cannot replay');
  const disabled=await cdp.evaluate(main.session,`chrome.runtime.sendMessage({type:'sites:set',origin:${JSON.stringify(origin)},enabled:false})`);assert.equal(disabled.ok,true);
  await click(page.session,'#sign');
  await cdp.wait(page.session,'window.fixture.fallbackCalls===1');
  assert.equal((await chooserTargets()).length,0,'disabled existing page must bypass chooser');
  process.stdout.write('PASS: concurrent payloads isolated, other app tabs rejected, stale navigation drops requests, revocation restores native fallback.\n');
  process.stdout.write('PASS: real bundled AutoFirme app, origin-bound chooser, dismissal once and unsupported pass-through.\n');
} catch(error) {
  if(cdp && mainSession) {try {process.stderr.write('App state: '+await cdp.evaluate(mainSession,'document.body.innerText.slice(0,5000)')+'\n');process.stderr.write('Browser errors: '+JSON.stringify(cdp.errors)+'\n');}catch{}}
  throw error;
} finally {
  cdp?.close(); processHandle?.kill('SIGKILL');
  if (server) await new Promise(resolve=>server.close(resolve));
  await rm(directory,{recursive:true,force:true});
}
