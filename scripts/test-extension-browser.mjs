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
let processHandle, cdp, server, mainSession, main;
try {
  const extension = join(directory, 'extension');
  await cp(root, extension, {recursive: true});
  const manifest = JSON.parse(await readFile(join(extension, 'manifest.json'), 'utf8'));
  assert.equal(manifest.manifest_version, 3);
  assert.equal(manifest.action.default_popup, undefined, 'app must survive focus changes');
  assert.deepEqual(manifest.host_permissions, ['http://*/*','https://*/*'], 'all-sites interception is the install-time default');
  assert.equal(manifest.optional_host_permissions, undefined, 'all-sites access is no longer an opt-in');
  let executable = process.env.CHROME_PATH;
  if (!executable) {
    const cache = join(homedir(), '.cache/ms-playwright');
    let entries = [];
    try {entries = (await readdir(cache)).filter(name => /^chromium-\d+$/.test(name)).sort((a,b) => Number(b.split('-')[1])-Number(a.split('-')[1]));} catch {} // No Playwright cache anywhere: fall through to the CHROME_PATH hint below instead of ENOENT.
    for (const entry of entries) {
      for (const suffix of ['chrome-linux64/chrome', 'chrome-linux/chrome']) {
        const candidate = join(cache, entry, suffix);
        try { await access(candidate); executable = candidate; break; } catch {}
      }
      if (executable) break;
    }
  }
  if (!executable) throw Error('Set CHROME_PATH to Chrome/Chromium or install Chromium with Playwright.');
  // Branded Google Chrome builds >=137 ignore --load-extension; only Chromium and Chrome for
  // Testing keep it. Say so before launch instead of dying on a chooser timeout.
  const version = spawnSync(executable, ['--version'], {encoding: 'utf8'});
  const flavour = `${version.stdout ?? ''}${version.stderr ?? ''}`.trim();
  if (/^Google Chrome\b/.test(flavour) && !/for Testing/.test(flavour)) throw Error(`${flavour} ignores --load-extension since 137. Set CHROME_PATH to Chromium or Chrome for Testing, e.g. after: npx playwright install --with-deps chromium (~/.cache/ms-playwright/chromium-<build>/chrome-linux64/chrome).`);
  const fixture = await readFile('test/extension-site.html');
  server = createServer((request, response) => {response.writeHead(200, {'content-type': 'text/html; charset=utf-8'}); response.end(fixture);});
  await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
  const address = server.address();
  if (!address || typeof address === 'string') throw Error('fixture server did not bind a TCP port');
  const origin = `http://127.0.0.1:${address.port}`;
  const profileDir = join(directory, 'profile');
  const launch = async () => {
    const handle = spawn(executable, ['--headless=new','--no-proxy-server','--no-sandbox','--disable-dev-shm-usage','--no-first-run','--remote-debugging-port=0',`--user-data-dir=${profileDir}`,`--disable-extensions-except=${extension}`,`--load-extension=${extension}`,'about:blank'], {stdio:'ignore'});
    let endpoint;
    for (let i=0;i<300;i++) {try {const [port,path]=(await readFile(join(profileDir,'DevToolsActivePort'),'utf8')).trim().split('\n'); endpoint=`ws://127.0.0.1:${port}${path}`; break;} catch {await new Promise(resolve=>setTimeout(resolve,100));}}
    if (!endpoint) throw Error('Chromium did not start');
    return {processHandle: handle, cdp: await CDP.connect(endpoint)};
  };
  ({processHandle, cdp} = await launch());
  const id = createHash('sha256').update(extension).digest('hex').slice(0,32).replace(/[0-9a-f]/g,c=>String.fromCharCode(97+parseInt(c,16)));
  const appURL = `chrome-extension://${id}/index.html`;
  const open = async url => {const {targetId}=await cdp.send('Target.createTarget',{url}); return {targetId,session:await cdp.attach(targetId)};};
  const fixtureKey = join(directory, 'fixture.key'), fixtureCert = join(directory, 'fixture.pem'), p12 = join(directory, 'fixture.p12');
  const openssl = args => {const result=spawnSync('openssl',args); assert.equal(result.status,0,result.stderr.toString()); return result;};
  openssl(['req','-x509','-newkey','rsa:2048','-nodes','-sha256','-days','2','-subj','/C=ES/O=Local non-production fixture/CN=AutoFirme Browser Test','-addext','basicConstraints=critical,CA:FALSE','-addext','keyUsage=critical,digitalSignature,nonRepudiation','-keyout',fixtureKey,'-out',fixtureCert]);
  openssl(['pkcs12','-export','-inkey',fixtureKey,'-in',fixtureCert,'-passout','pass:fixture-password','-out',p12]);
  main = await open(appURL); mainSession=main.session;
  await cdp.wait(main.session, `document.querySelector('h1')?.textContent.includes('AutoFirma')`);
  assert.equal(await cdp.evaluate(main.session, 'location.protocol'), 'chrome-extension:');
  assert.ok(await cdp.evaluate(main.session, "document.body.textContent.includes('Configura la firma')"));
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
  // Two steps now: certificate on step 1, then the documents card that holds the format chooser.
  // The Next button carries no aria-describedby once a certificate is picked; match its text.
  await cdp.wait(main.session, `[...document.querySelectorAll('#panel-sign button')].find(b=>b.textContent.trim()==='Siguiente')?.disabled===false`);
  await cdp.evaluate(main.session, `[...document.querySelectorAll('#panel-sign button')].find(b=>b.textContent.trim()==='Siguiente').click()`);
  await cdp.wait(main.session, "Boolean(document.querySelector('#panel-sign fieldset button[aria-haspopup=dialog]'))");
  await cdp.evaluate(main.session, `[...document.querySelectorAll('button[aria-haspopup=dialog]')].find(b=>b.textContent.includes('CAdES-BES')).click()`);
  await cdp.wait(main.session, 'Boolean(document.querySelector("input[name=format][value=pades]"))');
  await click(main.session,'input[name=format][value=pades]');
  await cdp.wait(main.session,'Boolean(document.querySelector("#visible-signature"))');
  await click(main.session,'#visible-signature');
  assert.equal(await cdp.evaluate(main.session, 'document.querySelector("#visible-signature").getAttribute("aria-checked")'),'true');
  const source=resolve('test/fixtures/pades-source.pdf'), second=join(directory,'second.pdf');
  await cp(source,second);
  await upload(main.session,'#panel-sign input[type=file][multiple]',[source,second]);
  await cdp.wait(main.session, `[...document.querySelectorAll('#panel-sign button')].find(b=>b.textContent.trim()==='Firmar')?.disabled===false`);
  await cdp.evaluate(main.session, `[...document.querySelectorAll('#panel-sign button')].find(b=>b.textContent.trim()==='Firmar').click()`);
  await cdp.wait(main.session, `document.querySelector('[aria-live=polite]')?.textContent.includes('2/2')`);
  // The signed bytes live only in the queue now: pull them from the ZIP the app downloads.
  await cdp.send('Browser.setDownloadBehavior',{behavior:'allow',downloadPath:directory});
  await cdp.evaluate(main.session, `[...document.querySelectorAll('button')].find(b=>b.textContent.includes('Descargar ZIP')).click()`);
  let archive;
  for(let i=0;i<100;i++){archive=(await readdir(directory)).find(name=>name.endsWith('.zip'));if(archive)break;await new Promise(r=>setTimeout(r,100));}
  assert.ok(archive,'batch ZIP download');
  const zipCheck=spawnSync('unzip',['-t',join(directory,archive)]);assert.equal(zipCheck.status,0,zipCheck.stdout.toString()+zipCheck.stderr.toString());
  const names=spawnSync('unzip',['-Z1',join(directory,archive)]).stdout.toString().split('\n').filter(Boolean);
  assert.equal(names.length,3,'ZIP carries both signed documents plus the manifest');
  let producedPDF;
  for(const name of names) {
    if(name==='manifest.json') continue;
    const pdf=Buffer.from(spawnSync('unzip',['-p',join(directory,archive),name]).stdout);
    const text=pdf.toString('latin1');
    const ranges=[...text.matchAll(/\/ByteRange\s*\[\s*(\d+)\s+(\d+)\s+(\d+)\s+(\d+)\s*\]/g)].at(-1);
    assert.ok(ranges,'actual app must generate a PDF signature');
    const [a,b,c,d]=ranges.slice(1).map(Number);
    const contentsMatch = [...text.matchAll(/\/Contents\s*<([a-fA-F0-9]+)>/g)].at(-1);
    assert.ok(contentsMatch, 'actual app must embed a /Contents hex blob');
    const signature=Buffer.from(contentsMatch[1],'hex');
    const contentPath=join(directory,'pdf-content'),sigPath=join(directory,'pdf-signature.der');
    await writeFile(contentPath,Buffer.concat([pdf.subarray(a,a+b),pdf.subarray(c,c+d)]));
    await writeFile(sigPath,signature);
    openssl(['cms','-verify','-cades','-binary','-inform','DER','-in',sigPath,'-content',contentPath,'-CAfile',fixtureCert,'-purpose','any','-out',join(directory,'pdf-verified')]);
    producedPDF=join(directory,'signed.pdf');await writeFile(producedPDF,pdf);
  }
  await click(main.session,'[role=tab][aria-controls=panel-verify]');
  await cdp.wait(main.session,'Boolean(document.querySelector("#panel-verify input[type=file]"))');
  await upload(main.session,'#panel-verify input[type=file]',[producedPDF]);
  await cdp.wait(main.session, 'document.querySelector("[data-check=signature]")?.dataset.status==="pass"');
  assert.equal(await cdp.evaluate(main.session,'document.querySelector("[data-check=structure]").dataset.status'),'pass');
  process.stdout.write('PASS: full app PDF stamps, two-document batch signing, valid ZIP and PDF verification; OpenSSL checks both signatures.\n');
  // Clean install intercepts every HTTP(S) site: the fixture page gets the bridge
  // immediately and the first sign call opens the chooser.
  const cold = await open(origin);
  await cdp.wait(cold.session, "Boolean(window.AutoScript && Object.getOwnPropertyDescriptor(window,'AutoScript').set)");
  await click(cold.session, '#sign');
  let coldChooser;
  for (let i=0;i<100;i++) {
    const {targetInfos}=await cdp.send('Target.getTargets');
    coldChooser=targetInfos.find(t=>t.type==='page'&&t.url.startsWith(appURL+'?request='));
    if (coldChooser) break;
    await new Promise(r=>setTimeout(r,100));
  }
  assert.ok(coldChooser,'a clean install must capture the first website call');
  await cdp.send('Target.closeTarget',{targetId:coldChooser.targetId});
  await cdp.wait(cold.session, 'window.fixture.fallbackCalls===0');
  // The SiteAccess panel lives inside the app's settings dialog now: open it by its label.
  await click(main.session, "[aria-label='Ajustes y ayuda']");
  await cdp.wait(main.session, 'Boolean(document.querySelector("#site-access-panel"))');
  await set(main.session, '#site-origin', origin);
  await cdp.wait(main.session, "document.querySelector('#disable-site')?.disabled===false");
  await click(main.session, '#disable-site');
  await cdp.wait(main.session, "chrome.runtime.sendMessage({type:'sites:list'}).then(r=>r.value.disabledOrigins.length===1)");
  await cdp.evaluate(main.session, "document.querySelector('[role=dialog] [data-dialog-close]')?.click()");
  await cdp.send('Target.closeTarget', {targetId: cold.targetId});
  process.stdout.write('PASS: a clean install intercepts websites and the panel disables an origin.\n');
  // Restart: the wildcard grant and the disabled exception survive the browser restart.
  cdp.close();
  await new Promise(resolve => {
    const timer = setTimeout(() => {processHandle.kill('SIGKILL'); resolve(undefined);}, 5000);
    processHandle.once('exit', () => {clearTimeout(timer); resolve(undefined);});
    processHandle.kill('SIGTERM');
  });
  // The dead port file would make the next launch read the previous session's endpoint.
  await rm(join(profileDir, 'DevToolsActivePort'), {force: true});
  ({processHandle, cdp} = await launch());
  main = await open(appURL); mainSession = main.session;
  const granted = await cdp.evaluate(main.session, `chrome.runtime.sendMessage({type:'sites:list'}).then(r=>r.value)`);
  assert.equal(granted.allSites, true, 'the all-sites wildcard survives the restart');
  assert.deepEqual(granted.disabledOrigins, [origin], 'the disabled-site exception survives the restart');
  process.stdout.write('PASS: the wildcard grant and the disabled exception survive the restart.\n');
  const page = await open(origin);
  await cdp.wait(page.session, 'Boolean(window.fixture)');
  // The disabled exception survived the restart, so this load keeps the original AutoFirma.
  await click(page.session, '#sign');
  await cdp.wait(page.session, 'window.fixture.fallbackCalls===1');
  await cdp.evaluate(main.session, `chrome.runtime.sendMessage({type:'sites:set',origin:${JSON.stringify(origin)},enabled:true})`);
  await cdp.send('Page.navigate',{url:origin},page.session);
  await cdp.wait(page.session, "Boolean(window.AutoScript && Object.getOwnPropertyDescriptor(window,'AutoScript').set)");
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
    if(mode==='implicit') {
      await cdp.wait(chooser.session,'Boolean(document.querySelector("#request-details"))');
      assert.equal(await cdp.evaluate(chooser.session,'document.querySelector("#request-details").open'),false);
      assert.equal(await cdp.evaluate(chooser.session,'Boolean(document.querySelector("header h1"))'),true);
      await cdp.wait(chooser.session,'document.querySelector("#request-chooser").getBoundingClientRect().height <= window.innerHeight+2');
      const digestButton=await cdp.evaluate(chooser.session,`(()=>{const b=document.querySelector('#request-copy-digest').getBoundingClientRect();return {x:b.x+b.width/2,y:b.y+b.height/2};})()`);
      await cdp.send('Input.dispatchMouseEvent',{type:'mouseMoved',...digestButton},chooser.session);
      await cdp.wait(chooser.session,`Boolean([...document.querySelectorAll('[data-slot=tooltip-content]')].find(el=>el.textContent.includes('Copiar SHA256')))`);
      await cdp.send('Input.dispatchMouseEvent',{type:'mousePressed',button:'left',clickCount:1,...digestButton},chooser.session);
      await cdp.send('Input.dispatchMouseEvent',{type:'mouseReleased',button:'left',clickCount:1,...digestButton},chooser.session);
      await cdp.wait(chooser.session,`Boolean([...document.querySelectorAll('[role=status]')].find(el=>el.textContent.includes('SHA256 copiado')))`);
      await cdp.send('Input.dispatchMouseEvent',{type:'mouseMoved',x:1,y:1},chooser.session);
      const compactHeight=await cdp.evaluate(chooser.session,'window.outerHeight');
      await click(chooser.session,'#request-details summary');
      await cdp.wait(chooser.session,`window.outerHeight > ${compactHeight}+20`);
      await cdp.wait(chooser.session,'document.querySelector("#request-chooser").getBoundingClientRect().height <= window.innerHeight+2 || window.outerHeight >= screen.availHeight-2');
      await click(chooser.session,'#request-details summary');
      await cdp.wait(chooser.session,`window.outerHeight <= ${compactHeight}+2`);
      process.stdout.write('PASS: chooser fits its content, grows when details expand, and shrinks when they close.\n');
    }
    await unlockChooser(chooser);
    if(mode==='implicit') {
      assert.equal(await cdp.evaluate(chooser.session,'document.querySelector("#pkcs12-choices button[aria-expanded]")'),null,'selected certificate must not appear twice');
      assert.equal(await cdp.evaluate(chooser.session,'document.querySelector("#selected-certificate-details").open'),false);
      for(const width of [320,420]) {
        await cdp.send('Emulation.setDeviceMetricsOverride',{width,height:900,deviceScaleFactor:1,mobile:false},chooser.session);
        await cdp.wait(chooser.session,`window.innerWidth===${width}`);
        const overflow=await cdp.evaluate(chooser.session,`({width:innerWidth,scroll:document.documentElement.scrollWidth,elements:[...document.querySelectorAll('body *')].filter(el=>{const r=el.getBoundingClientRect();return r.width && (r.right>innerWidth+1 || r.left<0);}).map(el=>({tag:el.tagName,id:el.id,slot:el.getAttribute('data-slot'),class:el.className,left:el.getBoundingClientRect().left,right:el.getBoundingClientRect().right})).slice(0,12)})`);
        assert.ok(overflow.scroll<=overflow.width+1,`chooser overflows at ${width}px: ${JSON.stringify(overflow)}`);
        for(const language of ['en','es']) {
          await cdp.evaluate(chooser.session,`[...document.querySelectorAll('header button')].find(b=>b.textContent.trim()===${JSON.stringify(language.toUpperCase())}).click()`);
          const layout=await cdp.evaluate(chooser.session,`(()=>{const box=id=>document.querySelector(id).getBoundingClientRect();return {buttons:['#request-confirm','#request-cancel','#request-fallback'].map(id=>box(id).height),header:box('header').height,site:box('#request-site').height,overflow:document.documentElement.scrollWidth>window.innerWidth+1,addOnRight:box('#pkcs12-choices-header button').left>box('#pkcs12-choices-header h2').right};})()`);
          assert.deepEqual(layout.buttons,[40,40,40]);assert.ok(layout.header<70);assert.ok(layout.site<55);assert.equal(layout.overflow,false);assert.equal(layout.addOnRight,true);
          await click(chooser.session,'#selected-certificate-details summary');
          assert.ok(await cdp.evaluate(chooser.session,'document.documentElement.scrollWidth<=window.innerWidth+1'),'expanded certificate details must wrap');
          await click(chooser.session,'#selected-certificate-details summary');
        }
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
  await cdp.send('Page.navigate',{url:origin+'?disabled=1'},page.session);
  await cdp.wait(page.session, "Boolean(window.fixture) && !Object.getOwnPropertyDescriptor(window,'AutoScript')?.set");
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
  // SIGKILL can leave the profile mid-write: retry the removal so it never masks the real error.
  await rm(directory,{recursive:true,force:true,maxRetries:20,retryDelay:100});
}
