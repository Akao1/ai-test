const CACHE = 'village-mall-v1';
const PRECACHE = [
  '/mall.html',
  '/manifest.json',
  '/icons/icon-192.svg',
  '/icons/icon-512.svg',
  'https://unpkg.com/element-ui/lib/theme-chalk/index.css',
  'https://unpkg.com/vue@2/dist/vue.js',
  'https://unpkg.com/element-ui/lib/index.js'
];

self.addEventListener('install', e => {
  e.waitUntil(
    caches.open(CACHE).then(c => c.addAll(PRECACHE)).then(() => self.skipWaiting())
  );
});

self.addEventListener('activate', e => {
  e.waitUntil(
    caches.keys().then(keys =>
      Promise.all(keys.filter(k => k !== CACHE).map(k => caches.delete(k)))
    ).then(() => self.clients.claim())
  );
});

self.addEventListener('fetch', e => {
  const { request } = e;
  if (request.url.startsWith(self.location.origin) && request.url.includes('/api/')) {
    e.respondWith(networkFirst(request));
    return;
  }
  e.respondWith(
    caches.match(request).then(r => r || fetch(request).catch(() => new Response('Offline', { status: 503 })))
  );
});

async function networkFirst(request) {
  try {
    const res = await fetch(request);
    const cache = await caches.open(CACHE);
    cache.put(request, res.clone());
    return res;
  } catch {
    return caches.match(request) || new Response(JSON.stringify({ error: '网络不可用' }), {
      status: 503,
      headers: { 'Content-Type': 'application/json' }
    });
  }
}
