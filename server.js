const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');
const session = require('express-session');

const app = express();
const PORT = 3000;
const DATA_DIR = path.join(__dirname, 'data');

app.use(cors({ origin: true, credentials: true }));
app.use(express.json());
app.use(session({
  secret: 'opencode-dashboard-secret-2026',
  resave: false, saveUninitialized: false,
  cookie: { maxAge: 24 * 60 * 60 * 1000 }
}));

if (!fs.existsSync(DATA_DIR)) fs.mkdirSync(DATA_DIR);

function load(name) {
  const fp = path.join(DATA_DIR, name + '.json');
  if (!fs.existsSync(fp)) {
    const defaults = {};
    if (name === 'users') defaults.data = [
      { id:1, username:'admin', password:'admin123', nickname:'管理员', role:'admin', status:1, created:'2026-01-01' },
      { id:2, username:'viewer', password:'view123', nickname:'观察员', role:'viewer', status:1, created:'2026-01-01' }
    ];
    else if (name === 'sysconfig') defaults.data = { title:'乡村振兴数字平台', logo:'', mapKey:'bba9bb58a27102852ffef74286a19f9d', modules:['household','building','crop','policy'] };
    else defaults.data = [];
    fs.writeFileSync(fp, JSON.stringify(defaults, null, 2));
    return defaults.data;
  }
  return JSON.parse(fs.readFileSync(fp, 'utf-8')).data;
}

function save(name, data) {
  fs.writeFileSync(path.join(DATA_DIR, name + '.json'), JSON.stringify({ data }, null, 2));
}

function nextId(arr) { return arr.reduce((m, d) => Math.max(m, d.id || 0), 0) + 1; }

function seed() {
  if (load('villages').length > 0) return;
  save('villages', [
    { id:1, name:'李家村', area:320, population:856, households:212, desc:'东侧自然村，以小麦种植为主', coords:[] },
    { id:2, name:'王家坪', area:280, population:623, households:158, desc:'西侧自然村，蔬菜大棚集中', coords:[] },
    { id:3, name:'赵家沟', area:190, population:412, households:106, desc:'南侧自然村，果树种植区', coords:[] }
  ]);
  save('parcels', [
    { id:1, name:'东洼地', villageId:1, area:85, crop:'小麦', owner:'李建国', status:'种植中', coords:[[35.73898474,106.94353034],[35.73861043,106.94352502],[35.73862023,106.94411332],[35.73897932,106.94410794],[35.73898691,106.94353034]] },
    { id:2, name:'西坡地', villageId:1, area:62, crop:'玉米', owner:'张大山', status:'种植中', coords:[[35.73730676,106.94112364],[35.73770956,106.94225753],[35.73666728,106.94337543],[35.73608261,106.94173349],[35.73730241,106.94112364]] },
    { id:3, name:'南大棚', villageId:2, area:48, crop:'蔬菜', owner:'王翠花', status:'丰收期', coords:[[35.74180210,106.94156188],[35.74160547,106.94200049],[35.74107521,106.94158334],[35.74099676,106.94164753],[35.74093593,106.94158870],[35.74086625,106.94161010],[35.74084002,106.94167428],[35.74078794,106.94159406],[35.74045609,106.94219848],[35.73968232,106.94164763],[35.73963042,106.94146043],[35.74031945,106.94068479],[35.74049387,106.94049222],[35.74180210,106.94156188]] }
  ]);
  save('households', [
    { id:1, villageId:1, name:'李建国', population:4, income:8.5, workCount:2, address:'李家村12号', phone:'138****1234', poverty:'已脱贫', policyIds:[1] },
    { id:2, villageId:1, name:'张大山', population:5, income:6.2, workCount:1, address:'李家村28号', phone:'139****5678', poverty:'一般户', policyIds:[] },
    { id:3, villageId:2, name:'王翠花', population:3, income:12.0, workCount:2, address:'王家坪6号', phone:'137****9012', poverty:'一般户', policyIds:[2] },
    { id:4, villageId:2, name:'赵铁柱', population:6, income:4.8, workCount:1, address:'王家坪15号', phone:'136****3456', poverty:'脱贫监测户', policyIds:[1,2] },
    { id:5, villageId:3, name:'刘秀英', population:2, income:15.0, workCount:1, address:'赵家沟3号', phone:'135****7890', poverty:'一般户', policyIds:[2] }
  ]);
  save('buildings', [
    { id:1, householdId:1, type:'砖混', area:120, floors:2, builtYear:2015, status:'安全' },
    { id:2, householdId:1, type:'简易房', area:30, floors:1, builtYear:2018, status:'附属' },
    { id:3, householdId:2, type:'土木', area:80, floors:1, builtYear:2008, status:'需加固' },
    { id:4, householdId:3, type:'框架', area:160, floors:3, builtYear:2020, status:'安全' },
    { id:5, householdId:4, type:'砖混', area:90, floors:1, builtYear:2010, status:'安全' },
    { id:6, householdId:5, type:'砖混', area:110, floors:2, builtYear:2018, status:'安全' }
  ]);
  save('crops', [
    { id:1, parcelId:1, type:'小麦', variety:'济麦22', area:85, plantDate:'2025-10', harvestDate:'2026-06', yield:42.5, income:9.8, cost:4.2, profit:5.6 },
    { id:2, parcelId:2, type:'玉米', variety:'郑单958', area:62, plantDate:'2026-04', harvestDate:'2026-09', yield:37.2, income:7.4, cost:3.1, profit:4.3 },
    { id:3, parcelId:3, type:'西红柿', variety:'瑞星', area:20, plantDate:'2026-02', harvestDate:'2026-06', yield:60.0, income:15.0, cost:6.0, profit:9.0 },
    { id:4, parcelId:3, type:'黄瓜', variety:'津优', area:28, plantDate:'2026-01', harvestDate:'2026-05', yield:56.0, income:12.8, cost:5.2, profit:7.6 }
  ]);
  save('policies', [
    { id:1, name:'低保补助', type:'生活保障', amount:4800, unit:'元/年', desc:'农村最低生活保障', applyTime:'2026-01' },
    { id:2, name:'耕地补贴', type:'农业补贴', amount:800, unit:'元/亩', desc:'种粮农民直接补贴', applyTime:'2026-03' },
    { id:3, name:'危房改造', type:'住房保障', amount:20000, unit:'元/户', desc:'农村危房改造补助', applyTime:'2026-04' }
  ]);
}
seed();

function auth(roles) {
  return (req, res, next) => {
    if (!req.session.user) return res.status(401).json({ error:'未登录' });
    if (roles && !roles.includes(req.session.user.role)) return res.status(403).json({ error:'无权限' });
    next();
  };
}

app.post('/api/login', (req, res) => {
  const { username, password } = req.body;
  const users = load('users');
  const user = users.find(u => u.username === username && u.password === password && u.status === 1);
  if (!user) return res.status(401).json({ error:'用户名或密码错误' });
  req.session.user = { id:user.id, username:user.username, nickname:user.nickname, role:user.role };
  res.json({ success:true, user:req.session.user });
});

app.post('/api/logout', (req, res) => {
  req.session.destroy();
  res.json({ success:true });
});

app.get('/api/me', (req, res) => {
  if (!req.session.user) return res.status(401).json({ error:'未登录' });
  res.json(req.session.user);
});

function crud(table, authRoles = null) {
  const mid = authRoles ? auth(authRoles) : (req, res, next) => next();
  app.get(`/api/${table}`, mid, (req, res) => res.json(load(table)));
  app.get(`/api/${table}/:id`, mid, (req, res) => {
    const item = load(table).find(d => d.id === Number(req.params.id));
    item ? res.json(item) : res.status(404).json({ error:'Not found' });
  });
  app.post(`/api/${table}`, mid, (req, res) => {
    const arr = load(table);
    const item = { id:nextId(arr), ...req.body };
    arr.push(item); save(table, arr);
    res.status(201).json(item);
  });
  app.put(`/api/${table}/:id`, mid, (req, res) => {
    const arr = load(table);
    const idx = arr.findIndex(d => d.id === Number(req.params.id));
    if (idx === -1) return res.status(404).json({ error:'Not found' });
    arr[idx] = { ...arr[idx], ...req.body, id:Number(req.params.id) };
    save(table, arr);
    res.json(arr[idx]);
  });
  app.delete(`/api/${table}/:id`, mid, (req, res) => {
    let arr = load(table);
    const len = arr.length;
    arr = arr.filter(d => d.id !== Number(req.params.id));
    if (arr.length === len) return res.status(404).json({ error:'Not found' });
    save(table, arr);
    res.json({ success:true });
  });
}

crud('villages');
crud('parcels');
crud('households');
crud('buildings');
crud('crops');
crud('policies', ['admin']);
crud('users', ['admin']);
crud('sysconfig', ['admin']);

app.get('/api/stats/overview', (req, res) => {
  const villages = load('villages');
  const households = load('households');
  const parcels = load('parcels');
  const crops = load('crops');
  const policies = load('policies');
  const buildings = load('buildings');
  res.json({
    villageCount: villages.length,
    householdCount: households.length,
    parcelCount: parcels.length,
    populationTotal: households.reduce((s, h) => s + (h.population||0), 0),
    avgIncome: households.length ? (households.reduce((s, h) => s + (h.income||0), 0) / households.length) : 0,
    totalArea: parcels.reduce((s, p) => s + (p.area||0), 0),
    policyCount: policies.length,
    buildingCount: buildings.length,
    poorCount: households.filter(h => h.poverty && h.poverty.includes('脱贫')).length
  });
});

app.get('/api/stats/crops', (req, res) => {
  const crops = load('crops');
  const byType = {};
  crops.forEach(c => {
    byType[c.type] = byType[c.type] || { type:c.type, area:0, yield:0, income:0, profit:0, count:0 };
    byType[c.type].area += c.area||0;
    byType[c.type].yield += c.yield||0;
    byType[c.type].income += c.income||0;
    byType[c.type].profit += c.profit||0;
    byType[c.type].count++;
  });
  res.json(Object.values(byType));
});

app.get('/api/stats/income', (req, res) => {
  const h = load('households');
  const levels = [
    { level:'< 3万', min:0, max:3, count:0 },
    { level:'3-5万', min:3, max:5, count:0 },
    { level:'5-8万', min:5, max:8, count:0 },
    { level:'8-12万', min:8, max:12, count:0 },
    { level:'12-20万', min:12, max:20, count:0 },
    { level:'>= 20万', min:20, max:Infinity, count:0 }
  ];
  h.forEach(house => {
    const inc = house.income||0;
    const l = levels.find(l => inc >= l.min && inc < l.max);
    if (l) l.count++;
  });
  res.json(levels);
});

app.use(express.static(__dirname));

app.listen(PORT, () => {
  console.log(`\x1b[36m🚀 Server: http://localhost:${PORT}\x1b[0m`);
  console.log(`\x1b[32m📊 大屏: http://localhost:${PORT}/dashboard.html\x1b[0m`);
  console.log(`\x1b[33m⚙️  后台: http://localhost:${PORT}/admin.html\x1b[0m`);
  console.log(`\x1b[35m👤 账号: admin / admin123\x1b[0m`);
});
