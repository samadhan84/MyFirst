// Multiplayer server for Bruno's Big Adventure.
// One shared world: every client sends its own state, the server relays everyone's state ~15x/second.
// It also serves ../web/bear over HTTP so players can open the game from the same address.
const http = require('http');
const fs = require('fs');
const path = require('path');
const { WebSocketServer } = require('ws');

const PORT = process.env.PORT || 8080;
const MAX_PLAYERS = 16;
const WEB_ROOT = path.join(__dirname, '..', 'web', 'bear');
const TYPES = { '.html': 'text/html', '.js': 'text/javascript', '.txt': 'text/plain' };

const server = http.createServer((req, res) => {
  const url = decodeURIComponent((req.url || '/').split('?')[0]);
  const file = path.normalize(path.join(WEB_ROOT, url === '/' ? 'index.html' : url));
  if (!file.startsWith(WEB_ROOT)) { res.writeHead(403).end(); return; }   // no path traversal
  fs.readFile(file, (err, data) => {
    if (err) { res.writeHead(404).end('Not found'); return; }
    res.writeHead(200, { 'Content-Type': TYPES[path.extname(file)] || 'application/octet-stream' });
    res.end(data);
  });
});

const wss = new WebSocketServer({ server, maxPayload: 1024 });
const players = new Map();   // id -> state
let nextId = 1;

const num = (v, lim) => (typeof v === 'number' && isFinite(v) ? Math.max(-lim, Math.min(lim, v)) : 0);
const cleanName = n => String(n || 'Bear').replace(/[^\w \-]/g, '').slice(0, 12) || 'Bear';

wss.on('connection', ws => {
  if (players.size >= MAX_PLAYERS) { ws.close(1013, 'Server full'); return; }
  const id = nextId++;
  const me = { id, name: 'Bear' + id, hue: Math.floor(Math.random() * 360), x: 0, y: 0, z: 0, f: 0, g: 1, l: 0, p: 0, v: 0 };
  players.set(id, me);
  ws.send(JSON.stringify({ t: 'hello', id, hue: me.hue }));
  let lastMsg = 0;

  ws.on('message', raw => {
    const now = Date.now();
    if (now - lastMsg < 30) return;          // rate limit: ~30 messages/second max
    lastMsg = now;
    let m; try { m = JSON.parse(raw); } catch { return; }
    if (!m || m.t !== 'state') return;
    Object.assign(me, {
      name: cleanName(m.name), x: num(m.x, 500), y: num(m.y, 100), z: num(m.z, 500), f: num(m.f, 10),
      g: m.g ? 1 : 0, l: m.l ? 1 : 0, p: m.p ? 1 : 0, v: num(m.v, 30),
    });
  });
  ws.on('close', () => players.delete(id));
  ws.on('error', () => players.delete(id));
});

setInterval(() => {
  if (!players.size) return;
  const list = [...players.values()];
  const msg = JSON.stringify({ t: 'players', list });
  for (const c of wss.clients) if (c.readyState === 1) c.send(msg);
}, 66);

server.listen(PORT, () => console.log(`Bruno server on http://localhost:${PORT} (open it in a browser, or point the TV app at ws://<this-computer>:${PORT})`));
