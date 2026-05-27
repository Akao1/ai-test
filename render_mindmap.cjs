const { Markmap } = require('markmap-lib');
const { Transformer } = require('markmap-lib');
const puppeteer = require('puppeteer-core');
const fs = require('fs');
const path = require('path');

async function main() {
  const md = fs.readFileSync('D:\\my\\mindmap.md', 'utf-8');
  const transformer = new Transformer();
  const { root } = transformer.transform(md);

  const html = `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
* { margin: 0; padding: 0; }
html, body { width: 100%; height: 100%; background: white; }
#mindmap { width: 100%; height: 100%; }
</style>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/markmap-view@0.18/dist/index.css">
</head>
<body>
<svg id="mindmap"></svg>
<script src="https://cdn.jsdelivr.net/npm/d3@7/dist/d3.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/markmap-view@0.18/dist/index.js"></script>
<script>
const { markmap } = window;
markmap.Markmap.create('#mindmap', null, ${JSON.stringify(root)});
setTimeout(() => document.title = 'rendered', 2000);
</script>
</body>
</html>`;

  const tmpHtml = 'D:\\my\\mindmap_temp.html';
  fs.writeFileSync(tmpHtml, html, 'utf-8');

  const browser = await puppeteer.launch({
    headless: true,
    executablePath: 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe',
    args: ['--no-sandbox', '--disable-gpu']
  });
  const page = await browser.newPage();
  await page.setViewport({ width: 1920, height: 1080 });
  await page.goto('file:///' + tmpHtml.replace(/\\/g, '/'), { waitUntil: 'networkidle0' });
  await page.waitForFunction("document.title === 'rendered'", { timeout: 10000 });
  await page.waitForTimeout(500);
  
  const el = await page.$('#mindmap');
  await el.screenshot({ path: 'D:\\my\\数字乡村系统思维导图.png' });
  
  await browser.close();
  fs.unlinkSync(tmpHtml);
  console.log('OK');
}

main().catch(e => { console.error(e); process.exit(1); });
