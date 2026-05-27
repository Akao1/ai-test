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
  secret: 'multi-mall-secret-2026',
  resave: false, saveUninitialized: false,
  cookie: { maxAge: 24 * 60 * 60 * 1000 }
}));

if (!fs.existsSync(DATA_DIR)) fs.mkdirSync(DATA_DIR);

function load(name) {
  const fp = path.join(DATA_DIR, name + '.json');
  if (!fs.existsSync(fp)) return [];
  return JSON.parse(fs.readFileSync(fp, 'utf-8')).data;
}

function save(name, data) {
  fs.writeFileSync(path.join(DATA_DIR, name + '.json'), JSON.stringify({ data }, null, 2));
}

function nextId(arr) { return arr.reduce((m, d) => Math.max(m, d.id || 0), 0) + 1; }

function seed() {
  if (load('users').length > 0) return;
  save('users', [
    { id:1, username:'admin', password:'admin123', nickname:'管理员', role:'admin', status:1, created:'2026-01-01' },
    { id:2, username:'user', password:'user123', nickname:'测试用户', role:'user', status:1, created:'2026-01-01' },
    { id:3, username:'merchant1', password:'merch123', nickname:'李掌柜', role:'merchant', status:1, created:'2026-01-01' },
    { id:4, username:'merchant2', password:'merch123', nickname:'王掌柜', role:'merchant', status:1, created:'2026-01-01' },
    { id:5, username:'merchant3', password:'merch123', nickname:'赵掌柜', role:'merchant', status:1, created:'2026-01-01' },
  ]);
  save('merchants', [
    { id:1, user_id:3, shop_name:'李家果园', shop_logo:'🍎', shop_desc:'专注优质水果种植，产地直发新鲜到家', contact_phone:'138****1234', contact_name:'李掌柜', status:1, created_at:'2026-01-01' },
    { id:2, user_id:4, shop_name:'王家农场', shop_logo:'🥬', shop_desc:'绿色有机农产品，从田间到餐桌', contact_phone:'139****5678', contact_name:'王掌柜', status:1, created_at:'2026-01-02' },
    { id:3, user_id:5, shop_name:'赵家山货', shop_logo:'🌲', shop_desc:'深山珍品，天然好味道', contact_phone:'137****9012', contact_name:'赵掌柜', status:1, created_at:'2026-01-03' },
  ]);
  save('categories', [
    {id:1,name:'新鲜水果',parent_id:0,level:1,sort:1,icon:'🍎'},{id:2,name:'蔬菜专区',parent_id:0,level:1,sort:2,icon:'🥬'},
    {id:3,name:'粮油调味',parent_id:0,level:1,sort:3,icon:'🧂'},{id:4,name:'肉禽蛋奶',parent_id:0,level:1,sort:4,icon:'🥩'},
    {id:5,name:'农资农具',parent_id:0,level:1,sort:5,icon:'🔧'},{id:6,name:'手工艺品',parent_id:0,level:1,sort:6,icon:'🎨'},
    {id:7,name:'苹果',parent_id:1,level:2,sort:1},{id:8,name:'柑橘橙柚',parent_id:1,level:2,sort:2},
    {id:9,name:'桃李杏',parent_id:1,level:2,sort:3},{id:10,name:'草莓浆果',parent_id:1,level:2,sort:4},
    {id:11,name:'叶菜类',parent_id:2,level:2,sort:1},{id:12,name:'根茎类',parent_id:2,level:2,sort:2},
    {id:13,name:'菌菇类',parent_id:2,level:2,sort:3},{id:14,name:'瓜果类',parent_id:2,level:2,sort:4},
    {id:15,name:'米面',parent_id:3,level:2,sort:1},{id:16,name:'食用油',parent_id:3,level:2,sort:2},
    {id:17,name:'调味品',parent_id:3,level:2,sort:3},{id:18,name:'干货',parent_id:3,level:2,sort:4},
    {id:19,name:'猪肉',parent_id:4,level:2,sort:1},{id:20,name:'禽类',parent_id:4,level:2,sort:2},
    {id:21,name:'蛋品',parent_id:4,level:2,sort:3},{id:22,name:'乳制品',parent_id:4,level:2,sort:4},
  ]);
  save('products', [
    {id:1,merchant_id:1,name:'红富士苹果',category_id:7,price:29.9,original_price:39.9,stock:500,status:1,images:['https://picsum.photos/seed/apple1/400/400'],description:'陕西洛川红富士，色泽红润，脆甜多汁，果香浓郁，产地直发。',sales:1258,rating:4.8,created_at:'2026-01-15'},
    {id:2,merchant_id:1,name:'赣南脐橙',category_id:8,price:35.9,original_price:45.0,stock:800,status:1,images:['https://picsum.photos/seed/orange1/400/400'],description:'江西赣南脐橙，果大形正，橙红鲜艳，光洁美观，肉质脆嫩化渣。',sales:980,rating:4.9,created_at:'2026-01-20'},
    {id:3,merchant_id:2,name:'土鸡蛋20枚装',category_id:21,price:25.9,original_price:32.0,stock:1200,status:1,images:['https://picsum.photos/seed/egg1/400/400'],description:'农家散养土鸡蛋，天然谷物喂养，蛋黄饱满，营养丰富。',sales:2100,rating:4.7,created_at:'2026-02-01'},
    {id:4,merchant_id:2,name:'有机五常大米5kg',category_id:15,price:49.9,original_price:59.9,stock:600,status:1,images:['https://picsum.photos/seed/rice1/400/400'],description:'黑龙江五常有机稻花香米，颗粒饱满，晶莹剔透，饭香四溢。',sales:1560,rating:4.9,created_at:'2026-01-10'},
    {id:5,merchant_id:2,name:'纯正菜籽油2.5L',category_id:16,price:42.0,original_price:52.0,stock:400,status:1,images:['https://picsum.photos/seed/oil1/400/400'],description:'物理压榨菜籽油，色泽金黄透亮，烹饪油烟少，香味纯正。',sales:870,rating:4.6,created_at:'2026-02-15'},
    {id:6,merchant_id:3,name:'散养土鸡',category_id:20,price:68.0,original_price:88.0,stock:200,status:1,images:['https://picsum.photos/seed/chicken1/400/400'],description:'林下散养土鸡，生长期300天以上，肉质紧实鲜美，适合炖汤。',sales:645,rating:4.8,created_at:'2026-03-01'},
    {id:7,merchant_id:3,name:'新鲜香菇250g',category_id:13,price:8.9,original_price:12.0,stock:1000,status:1,images:['https://picsum.photos/seed/mushroom1/400/400'],description:'当日采摘鲜香菇，肉质肥厚，菇香浓郁，口感滑嫩。',sales:1890,rating:4.5,created_at:'2026-03-10'},
    {id:8,merchant_id:3,name:'高山红薯5斤装',category_id:12,price:19.9,original_price:25.0,stock:900,status:1,images:['https://picsum.photos/seed/sweetpotato1/400/400'],description:'高山沙地红薯，蜜甜流油，粉糯无丝，蒸烤皆宜。',sales:1340,rating:4.7,created_at:'2026-02-20'},
    {id:9,merchant_id:3,name:'纯天然蜂蜜500g',category_id:18,price:58.0,original_price:78.0,stock:350,status:1,images:['https://picsum.photos/seed/honey1/400/400'],description:'深山百花蜜，自然成熟蜜，波美度42+，口感醇厚甘甜。',sales:560,rating:4.9,created_at:'2026-03-15'},
    {id:10,merchant_id:3,name:'手工竹编制篮',category_id:6,price:45.0,original_price:55.0,stock:150,status:1,images:['https://picsum.photos/seed/basket1/400/400'],description:'老匠人手工编织，精选竹篾，经久耐用，环保美观。',sales:320,rating:4.6,created_at:'2026-04-01'},
  ]);
  save('skus', [
    {id:1,product_id:1,attrs:'5斤家庭装',price:29.9,stock:300,image:''},{id:2,product_id:1,attrs:'10斤实惠装',price:49.9,stock:200,image:''},
    {id:3,product_id:2,attrs:'5斤装',price:35.9,stock:500,image:''},{id:4,product_id:2,attrs:'10斤装',price:59.9,stock:300,image:''},
    {id:5,product_id:3,attrs:'20枚装',price:25.9,stock:800,image:''},{id:6,product_id:3,attrs:'40枚装',price:45.0,stock:400,image:''},
    {id:7,product_id:4,attrs:'5kg装',price:49.9,stock:400,image:''},{id:8,product_id:4,attrs:'10kg装',price:89.0,stock:200,image:''},
    {id:9,product_id:5,attrs:'2.5L装',price:42.0,stock:300,image:''},{id:10,product_id:5,attrs:'5L装',price:75.0,stock:100,image:''},
    {id:11,product_id:6,attrs:'1只（约2斤）',price:68.0,stock:100,image:''},{id:12,product_id:6,attrs:'1只（约3斤）',price:88.0,stock:100,image:''},
    {id:13,product_id:7,attrs:'250g装',price:8.9,stock:600,image:''},{id:14,product_id:7,attrs:'500g装',price:15.9,stock:400,image:''},
    {id:15,product_id:8,attrs:'5斤装',price:19.9,stock:500,image:''},{id:16,product_id:8,attrs:'10斤装',price:35.0,stock:400,image:''},
    {id:17,product_id:9,attrs:'500g装',price:58.0,stock:200,image:''},{id:18,product_id:9,attrs:'1000g礼盒装',price:108.0,stock:150,image:''},
    {id:19,product_id:10,attrs:'小号（直径25cm）',price:45.0,stock:80,image:''},{id:20,product_id:10,attrs:'大号（直径35cm）',price:65.0,stock:70,image:''},
  ]);
  save('reviews', [
    {id:1,user_id:2,product_id:1,order_id:1,sku_attrs:'5斤家庭装',rating:5,content:'苹果很新鲜，脆甜好吃！',images:[],created_at:'2026-03-20'},
    {id:2,user_id:2,product_id:3,order_id:1,sku_attrs:'20枚装',rating:5,content:'土鸡蛋品质很好',images:[],created_at:'2026-03-22'},
  ]);
  save('addresses', []);
  save('cart', []);
  save('orders', []);
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
  const merchants = load('merchants');
  const myShop = merchants.find(m => m.user_id === user.id);
  res.json({ success:true, user:req.session.user, myShop: myShop || null });
});
app.post('/api/logout', (req, res) => { req.session.destroy(); res.json({ success:true }); });
app.get('/api/me', (req, res) => {
  if (!req.session.user) return res.status(401).json({ error:'未登录' });
  const merchants = load('merchants');
  const myShop = merchants.find(m => m.user_id === req.session.user.id);
  res.json({ ...req.session.user, myShop: myShop || null });
});
app.post('/api/register', (req, res) => {
  const { username, password, nickname } = req.body;
  if (!username || !password) return res.status(400).json({ error:'用户名和密码必填' });
  const users = load('users');
  if (users.find(u => u.username === username)) return res.status(400).json({ error:'用户名已存在' });
  const user = { id:nextId(users), username, password, nickname:nickname||username, role:'user', status:1, created:new Date().toISOString().slice(0,10) };
  users.push(user); save('users', users);
  req.session.user = { id:user.id, username:user.username, nickname:user.nickname, role:user.role };
  res.status(201).json({ success:true, user:req.session.user });
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

crud('users', ['admin']);
crud('categories');
crud('skus');
crud('addresses');
crud('reviews');

// ---- Merchants ----
app.get('/api/merchants', (req, res) => {
  const all = load('merchants').filter(m => m.status === 1);
  const products = load('products');
  all.forEach(m => { m.productCount = products.filter(p => p.merchant_id === m.id).length; });
  res.json(all);
});
app.get('/api/merchants/:id', (req, res) => {
  const m = load('merchants').find(d => d.id === Number(req.params.id));
  if (!m) return res.status(404).json({ error:'Not found' });
  const products = load('products').filter(p => p.merchant_id === m.id && p.status === 1);
  const cats = load('categories');
  res.json({ ...m, products, categories:cats });
});
app.post('/api/merchants/register', auth(), (req, res) => {
  if (req.session.user.role !== 'user') return res.status(400).json({ error:'仅普通用户可申请开店' });
  const exist = load('merchants').find(m => m.user_id === req.session.user.id);
  if (exist) return res.status(400).json({ error:'您已拥有店铺' });
  const { shop_name, shop_desc, contact_phone, contact_name } = req.body;
  const arr = load('merchants');
  const item = { id:nextId(arr), user_id:req.session.user.id, shop_name, shop_desc:shop_desc||'', shop_logo:'🏪', contact_phone:contact_phone||'', contact_name:contact_name||'', status:0, created_at:new Date().toISOString().slice(0,10) };
  arr.push(item); save('merchants', arr);
  const users = load('users'); const u = users.find(x => x.id === req.session.user.id);
  if (u) { u.role = 'merchant'; save('users', users); req.session.user.role = 'merchant'; }
  res.status(201).json(item);
});
app.put('/api/merchants/:id/status', auth(['admin']), (req, res) => {
  const arr = load('merchants');
  const idx = arr.findIndex(d => d.id === Number(req.params.id));
  if (idx === -1) return res.status(404).json({ error:'Not found' });
  arr[idx].status = req.body.status;
  save('merchants', arr); res.json(arr[idx]);
});
// Admin: get all merchants (including pending/closed)
app.get('/api/admin/merchants', auth(['admin']), (req, res) => {
  const all = load('merchants');
  const products = load('products');
  all.forEach(m => { m.productCount = products.filter(p => p.merchant_id === m.id).length; });
  res.json(all);
});
// Admin: create merchant (user + shop in one call)
app.post('/api/admin/merchants/create', auth(['admin']), (req, res) => {
  const { username, password, nickname, shop_name, shop_desc, contact_name, contact_phone } = req.body;
  if (!username || !password || !shop_name) return res.status(400).json({ error:'用户名、密码和店铺名必填' });
  const users = load('users');
  if (users.find(u => u.username === username)) return res.status(400).json({ error:'用户名已存在' });
  const user = { id:nextId(users), username, password, nickname:nickname||shop_name, role:'merchant', status:1, created:new Date().toISOString().slice(0,10) };
  users.push(user); save('users', users);
  const arr = load('merchants');
  const item = { id:nextId(arr), user_id:user.id, shop_name, shop_desc:shop_desc||'', shop_logo:'🏪', contact_phone:contact_phone||'', contact_name:contact_name||'', status:1, created_at:new Date().toISOString().slice(0,10) };
  arr.push(item); save('merchants', arr);
  res.status(201).json({ user, merchant: item });
});
// Admin: delete merchant
app.delete('/api/admin/merchants/:id', auth(['admin']), (req, res) => {
  let merchants = load('merchants');
  const m = merchants.find(d => d.id === Number(req.params.id));
  if (!m) return res.status(404).json({ error:'Not found' });
  merchants = merchants.filter(d => d.id !== Number(req.params.id));
  save('merchants', merchants);
  let users = load('users');
  users = users.filter(d => d.id !== m.user_id);
  save('users', users);
  res.json({ success:true });
});

// ---- Products (with merchant attachment) ----
app.get('/api/products', (req, res) => {
  const products = load('products');
  const merchants = load('merchants');
  const list = products.map(p => { const m = merchants.find(x => x.id === p.merchant_id); return { ...p, merchant_name: m?.shop_name||'', merchant_logo: m?.shop_logo||'' }; });
  res.json(list);
});
app.get('/api/products/:id', (req, res) => {
  const p = load('products').find(d => d.id === Number(req.params.id));
  if (!p) return res.status(404).json({ error:'Not found' });
  const m = load('merchants').find(x => x.id === p.merchant_id);
  res.json({ ...p, merchant_name: m?.shop_name||'', merchant_logo: m?.shop_logo||'' });
});
// Merchant's own products
app.get('/api/products/merchant/:merchantId', (req, res) => {
  res.json(load('products').filter(p => p.merchant_id === Number(req.params.merchantId)));
});
// Merchant create/update product (merchant role, only their own)
app.post('/api/products/add', auth(['merchant','admin']), (req, res) => {
  const arr = load('products');
  const merchant = load('merchants').find(m => m.user_id === req.session.user.id);
  if (!merchant && req.session.user.role !== 'admin') return res.status(400).json({ error:'无店铺' });
  const item = { id:nextId(arr), merchant_id: merchant?.id||req.body.merchant_id, ...req.body, created_at:new Date().toISOString().slice(0,10) };
  arr.push(item); save('products', arr); res.status(201).json(item);
});
app.put('/api/products/edit/:id', auth(['merchant','admin']), (req, res) => {
  const arr = load('products');
  const idx = arr.findIndex(d => d.id === Number(req.params.id));
  if (idx === -1) return res.status(404).json({ error:'Not found' });
  const merchant = load('merchants').find(m => m.user_id === req.session.user.id);
  if (arr[idx].merchant_id !== merchant?.id && req.session.user.role !== 'admin') return res.status(403).json({ error:'只能编辑自己的商品' });
  arr[idx] = { ...arr[idx], ...req.body, id:Number(req.params.id) };
  save('products', arr); res.json(arr[idx]);
});
app.delete('/api/products/del/:id', auth(['merchant','admin']), (req, res) => {
  let arr = load('products');
  const idx = arr.findIndex(d => d.id === Number(req.params.id));
  if (idx === -1) return res.status(404).json({ error:'Not found' });
  const merchant = load('merchants').find(m => m.user_id === req.session.user.id);
  if (arr[idx].merchant_id !== merchant?.id && req.session.user.role !== 'admin') return res.status(403).json({ error:'只能删除自己的商品' });
  arr = arr.filter(d => d.id !== Number(req.params.id));
  save('products', arr); res.json({ success:true });
});

// ---- Cart (with merchant info) ----
app.get('/api/cart', auth(), (req, res) => {
  const uid = req.session.user.id;
  const all = load('cart').filter(c => c.user_id === uid);
  const skus = load('skus');
  const products = load('products');
  const merchants = load('merchants');
  all.forEach(c => {
    const s = skus.find(x => x.id === c.sku_id);
    if (s) { c.sku = s; const p = products.find(p => p.id === s.product_id); c.product = p; if (p) { const m = merchants.find(x => x.id === p.merchant_id); c.merchant_name = m?.shop_name||''; } }
  });
  res.json(all);
});
app.post('/api/cart', auth(), (req, res) => {
  const arr = load('cart'); const uid = req.session.user.id;
  const { sku_id, quantity } = req.body;
  const exist = arr.find(c => c.user_id === uid && c.sku_id === sku_id);
  if (exist) { exist.quantity += (quantity||1); save('cart', arr); return res.json(exist); }
  const item = { id:nextId(arr), user_id:uid, sku_id, quantity:quantity||1, selected:true };
  arr.push(item); save('cart', arr); res.status(201).json(item);
});
app.put('/api/cart/:id', auth(), (req, res) => {
  const arr = load('cart');
  const idx = arr.findIndex(d => d.id === Number(req.params.id) && d.user_id === req.session.user.id);
  if (idx === -1) return res.status(404).json({ error:'Not found' });
  arr[idx] = { ...arr[idx], ...req.body, id:Number(req.params.id) };
  save('cart', arr); res.json(arr[idx]);
});
app.delete('/api/cart/:id', auth(), (req, res) => {
  let arr = load('cart'); const len = arr.length;
  arr = arr.filter(d => !(d.id === Number(req.params.id) && d.user_id === req.session.user.id));
  if (arr.length === len) return res.status(404).json({ error:'Not found' });
  save('cart', arr); res.json({ success:true });
});

// ---- Orders (multi-merchant) ----
app.post('/api/orders/create', auth(), (req, res) => {
  const uid = req.session.user.id;
  const { skuIds, address_id, remark } = req.body;
  const allCart = load('cart'); const skus = load('skus'); const products = load('products');
  const merchants = load('merchants');
  const selected = allCart.filter(c => c.user_id === uid && skuIds.includes(c.id));
  if (!selected.length) return res.status(400).json({ error:'未选择商品' });
  const items = []; let total = 0;
  for (const c of selected) {
    const s = skus.find(x => x.id === c.sku_id); if (!s) continue;
    const p = products.find(x => x.id === s.product_id); if (p && p.status !== 1) continue;
    if (s.stock < c.quantity) return res.status(400).json({ error:`商品库存不足: ${p?.name||''} ${s.attrs||''}` });
    const m = merchants.find(x => x.id === p?.merchant_id);
    items.push({ sku_id:s.id, product_id:s.product_id, quantity:c.quantity, price:s.price,
      product_name:p?.name||'', product_image:p?.images?.[0]||'', sku_attrs:s.attrs||'',
      merchant_id:p?.merchant_id||0, merchant_name:m?.shop_name||'' });
    total += s.price * c.quantity;
    s.stock -= c.quantity;
  }
  if (!items.length) return res.status(400).json({ error:'无可下单商品' });
  const addr = load('addresses').find(a => a.id === address_id && a.user_id === uid);
  const orders = load('orders');
  const orderNo = 'ORD' + Date.now() + Math.random().toString(36).slice(2,6).toUpperCase();
  const order = {
    id:nextId(orders), user_id:uid, order_no:orderNo, total_amount:Math.round(total*100)/100,
    status:'待付款', remark:remark||'', address_snapshot:addr?JSON.stringify(addr):'',
    items:items.map((it,i)=>({id:i+1,...it})),
    created_at:new Date().toISOString().slice(0,19).replace('T',' ')
  };
  orders.push(order); save('orders', orders);
  save('skus', skus);
  const remaining = allCart.filter(c => !(c.user_id === uid && skuIds.includes(c.id)));
  save('cart', remaining);
  const pAll = load('products');
  items.forEach(it => { const pi = pAll.find(p => p.id === it.product_id); if (pi) { pi.sales = (pi.sales||0) + it.quantity; } });
  save('products', pAll);
  res.status(201).json(order);
});
app.get('/api/orders/user', auth(), (req, res) => {
  const uid = req.session.user.id;
  res.json(load('orders').filter(o => o.user_id === uid));
});
// Merchant orders (containing merchant's products)
app.get('/api/orders/merchant', auth(['merchant','admin']), (req, res) => {
  const merchant = load('merchants').find(m => m.user_id === req.session.user.id);
  if (!merchant && req.session.user.role !== 'admin') return res.status(400).json({ error:'无店铺' });
  const mid = merchant?.id || (req.session.user.role === 'admin' ? null : null);
  const all = load('orders');
  if (mid) {
    const filtered = all.filter(o => o.items?.some(it => it.merchant_id === mid));
    // flag items belonging to this merchant
    filtered.forEach(o => { o.myItems = o.items.filter(it => it.merchant_id === mid); });
    res.json(filtered);
  } else {
    res.json(all);
  }
});
app.get('/api/orders/detail/:id', auth(), (req, res) => {
  const o = load('orders').find(o => o.id === Number(req.params.id));
  if (!o || (o.user_id !== req.session.user.id && req.session.user.role === 'user')) return res.status(404).json({ error:'Not found' });
  res.json(o);
});
app.put('/api/orders/:id/pay', auth(), (req, res) => {
  const arr = load('orders');
  const o = arr.find(d => d.id === Number(req.params.id) && d.user_id === req.session.user.id);
  if (!o) return res.status(404).json({ error:'Not found' });
  if (o.status !== '待付款') return res.status(400).json({ error:'当前状态不可支付' });
  o.status = '待发货'; save('orders', arr); res.json(o);
});
// Ship: admin or the merchant who owns the products
app.put('/api/orders/:id/ship', auth(), (req, res) => {
  const arr = load('orders');
  const o = arr.find(d => d.id === Number(req.params.id));
  if (!o) return res.status(404).json({ error:'Not found' });
  if (o.status !== '待发货') return res.status(400).json({ error:'当前状态不可发货' });
  const merchant = load('merchants').find(m => m.user_id === req.session.user.id);
  const isAdmin = req.session.user.role === 'admin';
  const isMerchantOwner = merchant && o.items?.some(it => it.merchant_id === merchant.id);
  if (!isAdmin && !isMerchantOwner) return res.status(403).json({ error:'无权操作' });
  o.status = '待收货'; save('orders', arr); res.json(o);
});
app.put('/api/orders/:id/receive', auth(), (req, res) => {
  const arr = load('orders');
  const o = arr.find(d => d.id === Number(req.params.id) && d.user_id === req.session.user.id);
  if (!o) return res.status(404).json({ error:'Not found' });
  if (o.status !== '待收货') return res.status(400).json({ error:'当前状态不可收货' });
  o.status = '已完成'; save('orders', arr); res.json(o);
});
app.put('/api/orders/:id/cancel', auth(), (req, res) => {
  const arr = load('orders');
  const o = arr.find(d => d.id === Number(req.params.id) && d.user_id === req.session.user.id);
  if (!o) return res.status(404).json({ error:'Not found' });
  if (!['待付款','待发货'].includes(o.status)) return res.status(400).json({ error:'当前状态不可取消' });
  o.status = '已取消';
  const skus = load('skus');
  (o.items||[]).forEach(it => { const s = skus.find(x => x.id === it.sku_id); if (s) s.stock += it.quantity; });
  save('skus', skus);
  save('orders', arr); res.json(o);
});
app.get('/api/orders/all', auth(['admin']), (req, res) => {
  res.json(load('orders').reverse());
});

// ---- AI Consultation ----
const aiKnowledge = [
  { keywords:['苹果','红富士','水果'], response:'🍎 **红富士苹果**是当前最受欢迎的品种！山东洛川红富士以"色泽红润、脆甜多汁"闻名。建议选择5斤家庭装（¥29.9），适合日常食用。保存时放在冰箱冷藏可保鲜2-3周。如果您喜欢脆甜口感，红富士绝对是最佳选择！' },
  { keywords:['脐橙','赣南','橙子'], response:'🍊 **赣南脐橙**果大形正、橙红鲜艳，肉质脆嫩化渣，是冬季补充维生素C的佳品！5斤装¥35.9，10斤装¥59.9更划算。建议购买后尽早食用，常温保存不超过2周。赣南脐橙出汁率高，榨汁也是一绝！' },
  { keywords:['土鸡蛋','鸡蛋','蛋'], response:'🥚 我们的**土鸡蛋**来自农家散养土鸡，天然谷物喂养，蛋黄饱满、营养丰富。20枚装¥25.9，40枚装¥45.0更实惠。土鸡蛋蛋白浓稠、蛋黄呈橙黄色，适合白煮或蒸蛋，营养保留最完整。建议冰箱冷藏保存，保质期约30天。' },
  { keywords:['大米','五常大米','稻花香','米'], response:'🍚 **五常有机稻花香米**是"米中贵族"，产自黑龙江五常，颗粒饱满、晶莹剔透。5kg装¥49.9，一家三口可吃约1个月。煮饭时建议米水比1:1.2，浸泡20分钟再煮，口感更软糯。五常大米煮粥也很香浓！' },
  { keywords:['菜籽油','食用油','油'], response:'🫒 **纯正菜籽油**采用物理压榨工艺，色泽金黄透亮，烹饪油烟少、香味纯正。2.5L装¥42.0适合日常炒菜。菜籽油富含不饱和脂肪酸，比普通调和油更健康。存放需避光、远离灶台高温。' },
  { keywords:['土鸡','散养鸡','鸡','鸡肉'], response:'🐔 **散养土鸡**是林下放养300天以上的老鸡，肉质紧实鲜美，最适合炖汤！一只约2斤（¥68）适合3-4人食用。炖汤时建议冷水下锅，大火烧开转小火慢炖2小时，汤汁浓白鲜美。土鸡富含胶原蛋白，适合滋补。' },
  { keywords:['香菇','蘑菇','菌菇'], response:'🍄 **新鲜香菇**当日采摘，肉质肥厚、菇香浓郁。250g装¥8.9，性价比很高！香菇炒青菜、香菇炖鸡都是经典做法。保存时放入冰箱冷藏，建议3天内食用完。香菇富含香菇多糖，增强免疫力。' },
  { keywords:['红薯','地瓜','高山红薯'], response:'🍠 **高山红薯**产自高山沙地，蜜甜流油、粉糯无丝。5斤装¥19.9，蒸烤皆宜。烤红薯时200℃烤40分钟，流油香甜！保存时放在阴凉通风处，不要放冰箱——低温会让红薯变硬变涩。' },
  { keywords:['蜂蜜','纯天然'], response:'🍯 **纯天然深山百花蜜**，自然成熟蜜，波美度42+（国家标准≥40），口感醇厚甘甜！500g装¥58.0，1000g礼盒装¥108.0送人自用皆宜。蜂蜜用60℃以下温水冲泡，避免高温破坏活性酶。早上空腹一勺蜂蜜水，润肠通便。' },
  { keywords:['竹编','手工','工艺品'], response:'🧺 **手工竹编制篮**由老匠人手工编织，精选竹篾、经久耐用。小号¥45.0（直径25cm），大号¥65.0（直径35cm）。竹编不仅实用，更是传统手工艺的传承。日常保养注意防潮，用湿布擦拭后阴干即可。' },
  { keywords:['推荐','买什么','好吃','有什么'], response:'🤔 根据当前季节和您的需求，我推荐几个最受欢迎的商品：\n1️⃣ **红富士苹果**（¥29.9）— 脆甜多汁，老少皆宜\n2️⃣ **赣南脐橙**（¥35.9）— 维C之王，冬季必备\n3️⃣ **五常大米**（¥49.9）— 一日三餐离不开\n4️⃣ **散养土鸡**（¥68）— 炖汤滋补首选\n您对哪类商品感兴趣？我可以详细介绍！' },
  { keywords:['便宜','优惠','划算','性价比'], response:'💰 为您推荐几款高性价比商品：\n🥚 土鸡蛋20枚¥25.9，折合每枚仅1.3元\n🍠 高山红薯5斤¥19.9，折合每斤4元\n🍄 新鲜香菇250g¥8.9，做菜必备\n🍚 五常大米5kg¥49.9，优质主食不贵\n多买还有更大规格装，单价更低哦！' },
  { keywords:['送礼','礼物','送人'], response:'🎁 送礼我推荐这几款有特色的商品：\n🍯 **纯天然蜂蜜礼盒**（¥108）— 深山百花蜜，送礼体面\n🧺 **手工竹编制篮**（¥65大号）— 传统手工艺，有文化内涵\n🍎 **红富士苹果10斤装**（¥49.9）— 实惠又健康\n🎀 如需精美包装可留言备注，我们会特别处理！' },
  { keywords:['季节','当季','应季'], response:'🗓️ **当前季节推荐（5月）**：\n🍎 红富士苹果（春季口感最佳）\n🍊 赣南脐橙（产季末，果味最甜）\n🥚 土鸡蛋（全年供应）\n🍲 散养土鸡（春夏炖汤清爽不腻）\n🌿 建议多吃新鲜蔬果，注意防暑！' },
  { keywords:['保存','存放','保鲜','怎么放'], response:'📦 **商品保存小贴士**：\n🍎 苹果/橙子：冰箱冷藏，2-3周\n🥚 土鸡蛋：冰箱冷藏，30天\n🍚 大米：阴凉干燥处，密封防虫\n🍄 香菇：冰箱冷藏，3天内食用\n🍠 红薯：阴凉通风处，勿放冰箱\n🍯 蜂蜜：常温避光，保质期2年\n🧺 竹编：防潮防霉，湿布擦拭' },
  { keywords:['烹饪','怎么做','做法','怎么吃'], response:'👨‍🍳 **美食做法推荐**：\n🥚 **土鸡蛋**：白煮6分钟溏心蛋，蒸蛋羹嫩滑\n🐔 **散养土鸡**：冷水下锅炖2小时，加姜片枸杞\n🍠 **红薯**：烤箱200℃烤40分钟流油\n🍚 **五常大米**：米水比1:1.2，浸泡20分钟\n🍄 **香菇**：切片炒青菜，或炖鸡提鲜\n您想了解哪个的详细食谱？' },
  { keywords:['客服','人工','电话','联系'], response:'📞 **联系我们**：\n如果您需要人工帮助，可以通过以下方式：\n- 在线咨询（我们已接入AI智能服务，随时为您解答）\n- 订单页面查看商家联系方式\n- 如有售后问题，请在订单详情页发起售后\nAI助手全天在线，您有什么问题我可以帮您解答？' },
  { keywords:['下单','买','购买','怎么买'], response:'🛒 **购物指南**：\n1️⃣ 浏览商品 → 点击进入详情\n2️⃣ 选择规格（重量/数量）→ 加入购物车\n3️⃣ 进入购物车 → 选择商品 → 结算\n4️⃣ 填写/选择收货地址 → 提交订单\n5️⃣ 支付等待商家发货\n📱 全程线上操作，方便快捷！需要我帮您推荐商品吗？' },
  { keywords:['付款','支付','没付'], response:'💳 **支付说明**：\n提交订单后，在"我的订单"中点击"去付款"即可完成支付。目前支持在线支付。如果付款遇到问题，请检查网络后重试，或取消订单重新下单。订单超过24小时未支付会自动取消。' },
  { keywords:['发货','物流','快递','送到'], response:'🚚 **配送说明**：\n- 下单后商家会在 **1-2天** 内发货\n- 同城配送通常 **1-2天** 送达\n- 偏远地区可能需要 **3-5天**\n- 发货后可在订单详情查看物流信息（如有物流号）\n如长时间未收到，请联系AI客服查询~' },
  { keywords:['退货','退款','售后','坏了','烂了'], response:'🔄 **售后政策**：\n收到商品后如有质量问题，请在**48小时内**联系售后：\n1️⃣ 在订单详情页截图保存证据\n2️⃣ 联系商家说明情况\n3️⃣ 生鲜类商品坏果烂果包赔\n4️⃣ 非质量问题退货需自理运费\nAI小助手已记录您的问题，如果急需可以找人工客服哦~' },
  { keywords:['营养','健康','养生','维生素'], response:'🥗 **营养小课堂**：\n🍎 苹果富含果胶和维C，每天一个医生远离我\n🍊 脐橙维C含量是柠檬的2倍，增强免疫力\n🥚 土鸡蛋含优质蛋白和卵磷脂，健脑益智\n🍯 蜂蜜润肠通便，改善睡眠\n🍠 红薯膳食纤维丰富，有助消化\n均衡搭配才健康，祝您吃得健康！' },
  { keywords:['你好','您好','嗨','在吗','hello','hi'], response:'👋 **您好！欢迎来到乡村多商户商城！**\n我是您的AI购物助手，可以为您提供以下帮助：\n🛒 商品推荐和介绍\n🥘 烹饪和保存建议\n📦 订单和配送查询\n🌿 农产品和养生知识\n有什么问题尽管问我吧！' },
  { keywords:['功能','你能','可以','做什么'], response:'🤖 **我能帮您做什么？**\n\n🔍 **商品咨询** — 了解商品详情、价格、规格\n💡 **推荐建议** — 根据需求推荐最适合的商品\n🍳 **烹饪食谱** — 各种食材的烹饪方法和技巧\n📦 **保存方法** — 各类商品的正确保存方式\n🌿 **营养知识** — 农产品营养和健康建议\n🚚 **订单帮助** — 下单、支付、配送等问题\n\n直接在对话框输入您的问题，我会尽力解答！' },
  { keywords:['价格','多少钱','贵'], response:'💰 **价格说明**：\n我们坚持产地直供，去除中间环节，价格实惠品质有保障。商品价格由各商家自主定价，您可以在商品详情页查看具体价格。批量购买通常有更大规格选项，单价更优惠！' },
  { keywords:['商家','店铺','有哪些'], response:'🏪 目前入驻的商家有：\n🍎 **李家果园** — 专注优质水果，产地直发\n🥬 **王家农场** — 绿色有机农产品，从田间到餐桌\n🌲 **赵家山货** — 深山珍品，天然好味道\n每家商户都有独特的特色产品，欢迎点击"店铺"页面浏览！' },
];

function aiReply(msg) {
  const m = msg.toLowerCase();
  // Find best matching knowledge
  let best = null, bestScore = 0;
  for (const k of aiKnowledge) {
    let score = 0;
    for (const kw of k.keywords) {
      if (m.includes(kw)) score += 1;
      // extra weight for exact match
      if (m.includes(kw) && kw.length > 2) score += 0.5;
    }
    if (score > bestScore) { bestScore = score; best = k; }
  }
  if (best && bestScore >= 1) return best.response;
  // Fallback
  const fallbacks = [
    '🤔 这个问题我还不太了解，不过我可以帮您：\n1️⃣ 推荐热销商品\n2️⃣ 查询订单信息\n3️⃣ 提供农产品知识\n请问您想了解什么？',
    '😊 关于这个问题我不太确定，您可以试试问我商品推荐、烹饪做法或订单问题，我会尽力帮您！',
    '🌾 抱歉我暂时无法回答这个问题。您可以试试：\n- "推荐好吃的"\n- "苹果怎么保存"\n- "土鸡怎么炖"\n我会为您详细解答！',
  ];
  return fallbacks[Math.floor(Math.random() * fallbacks.length)];
}

app.post('/api/ai/chat', (req, res) => {
  const { message, context } = req.body;
  if (!message) return res.json({ reply: '请说点什么吧~', products: [] });
  const reply = aiReply(message);
  // If the reply mentions specific products, also return matching products
  const products = load('products').filter(p => p.status === 1);
  const merchants = load('merchants');
  const matched = [];
  for (const k of aiKnowledge) {
    if (k.response === reply) {
      for (const kw of k.keywords) {
        for (const p of products) {
          if ((p.name.includes(kw) || p.description.includes(kw)) && !matched.find(x => x.id === p.id)) {
            const m = merchants.find(x => x.id === p.merchant_id);
            matched.push({ ...p, merchant_name: m?.shop_name||'', merchant_logo: m?.shop_logo||'' });
          }
        }
      }
      break;
    }
  }
  res.json({ reply, products: matched.slice(0, 6) });
});

app.use(express.static(path.join(__dirname)));
app.listen(PORT, () => {
  console.log(`\x1b[36m🚀 多商户商城: http://localhost:${PORT}\x1b[0m`);
  console.log(`\x1b[35m🛒 前台: http://localhost:${PORT}/mall.html\x1b[0m`);
  console.log(`\x1b[33m⚙️  管理后台: http://localhost:${PORT}/admin.html\x1b[0m`);
  console.log(`\x1b[32m🏪  商户端: http://localhost:${PORT}/merchant.html\x1b[0m`);
  console.log(`\x1b[35m👤 admin/admin123 | merchant1/merch123 | user/user123\x1b[0m`);
});
