// Recorrido de la tienda con un navegador real (Playwright) y capturas de pantalla.
// Uso: BASE=http://localhost:8080/tienda ADMIN_PASSWORD=1234 node recorrido.mjs
import { chromium } from 'playwright';
import fs from 'node:fs';

const BASE = process.env.BASE || 'http://localhost:8080/tienda';
const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD || '1234';
const OUT = process.env.OUT || 'capturas';
fs.mkdirSync(OUT, { recursive: true });

const resultados = [];
const browser = await chromium.launch();
const page = await browser.newPage({ viewport: { width: 1366, height: 768 }, locale: 'es-PE' });
page.setDefaultTimeout(20000);
page.on('pageerror', (e) => resultados.push(`ERROR JS: ${e.message}`));

const foto = async (nombre) => { await page.waitForTimeout(800); await page.screenshot({ path: `${OUT}/${nombre}.png`, fullPage: true }); };
const paso = async (nombre, fn) => {
  try { await fn(); resultados.push(`OK    ${nombre}`); }
  catch (e) { resultados.push(`FALLA ${nombre}: ${e.message.split('\n')[0]} (url: ${page.url()})`); await foto('error-' + nombre.replace(/\W+/g, '-')); }
};
const login = async (u, p) => {
  await page.goto(`${BASE}/login`);
  await page.fill('#username', u);
  await page.fill('#password', p);
  await page.click('button[type=submit]');
};
const salir = async () => { await page.click('button:has-text("Salir")'); await page.waitForURL(/\/tienda\/?$/); };

await paso('Catálogo carga los productos', async () => {
  await page.goto(`${BASE}/`);
  await page.waitForSelector('.card .titulo');
  const n = await page.locator('.card .titulo').count();
  if (n < 1) throw new Error('sin productos');
  await foto('01-catalogo');
});

await paso('Login con contraseña incorrecta muestra el error', async () => {
  await login('admin', 'clave-mala');
  await page.waitForSelector('.alert-danger');
  await foto('02-login-error');
});

await paso('Registro de cliente', async () => {
  await page.goto(`${BASE}/registro`);
  await page.fill('#nombre', 'María Pérez');
  await page.fill('#correo', 'maria@mail.com');
  await page.fill('#usuario', 'maria');
  await page.fill('#clave', 'flores123');
  await page.fill('#dir', 'Av. Arequipa 1234, Lince');
  await foto('03-registro');
  await page.click('button[type=submit]');
  await page.waitForSelector('text=Cuenta creada');
});

await paso('Login del cliente', async () => {
  await page.fill('#username', 'maria');
  await page.fill('#password', 'flores123');
  await page.click('button[type=submit]');
  await page.waitForSelector('button:has-text("Salir")');
});

await paso('Compra con Yape', async () => {
  await page.goto(`${BASE}/`);
  await page.locator('a:has-text("Comprar")').nth(1).click();
  await page.waitForSelector('#direccion');
  await page.fill('#cantidad', '2');
  await page.fill('#direccion', 'Av. Arequipa 1234, Lince');
  await page.selectOption('#metodo', 'Yape');
  await foto('04-comprar');
  await page.click('button:has-text("Pagar pedido")');
  await page.waitForSelector('text=¡Gracias por tu compra!');
  await foto('05-compra-confirmada');
});

await paso('Mis pedidos muestra la compra', async () => {
  await page.goto(`${BASE}/mis-pedidos`);
  await page.waitForSelector('text=ORD-');
  await foto('06-mis-pedidos');
});

await paso('Cliente no puede entrar a Gestionar flores', async () => {
  await page.goto(`${BASE}/admin/flores`);
  await page.waitForURL(/\/tienda\/?$/);
  await salir();
});

await paso('Admin: gestionar flores', async () => {
  await login('admin', ADMIN_PASSWORD);
  await page.waitForURL(/admin\/flores/);
  await page.waitForSelector('table tbody tr');
  await foto('07-admin-flores');
});

await paso('Admin: registrar flor nueva (POST)', async () => {
  await page.goto(`${BASE}/admin/flores/nueva`);
  await page.fill('#nombre', 'Ramo primaveral');
  await page.fill('#desc', 'Mix de flores de estación');
  await page.fill('#precio', '45');
  await page.fill('#stock', '6');
  await foto('08-flor-nueva');
  await page.click('button[type=submit]');
  await page.waitForSelector('text=Ramo primaveral');
});

await paso('Admin: editar flor (PUT)', async () => {
  const fila = page.locator('tr', { hasText: 'Ramo primaveral' });
  const enlace = fila.locator('a[title="Editar"]');
  resultados.push(`      enlace editar: href=${await enlace.getAttribute('href')}`);
  await enlace.click();
  await page.waitForURL(/editar/);
  await page.waitForSelector('h2:has-text("Editar flor")');
  await foto('08b-flor-editar');
  await page.fill('#precio', '49.9');
  await page.click('button[type=submit]');
  await page.waitForSelector('text=49.90');
});

await paso('Admin: eliminar flor (DELETE)', async () => {
  const fila = page.locator('tr', { hasText: 'Ramo primaveral' });
  await fila.locator('button[title=Eliminar]').click();
  await page.click('button:has-text("Sí, eliminar")');
  await page.waitForSelector('text=Flor eliminada correctamente');
  await foto('09-flor-eliminada');
});

await paso('Admin: pedidos y total vendido', async () => {
  await page.goto(`${BASE}/admin/pedidos`);
  await page.waitForSelector('text=ORD-');
  await foto('10-admin-pedidos');
  await salir();
});

await paso('Repartidor: marca En camino y Entregado', async () => {
  await login('Deli', 'very');
  await page.waitForURL(/admin\/pedidos/);
  await page.waitForSelector('text=ORD-');
  await page.click('button:has-text("En camino")');
  await page.waitForSelector('text=ahora está EN CAMINO');
  await foto('11-delivery-en-camino');
  await page.click('button:has-text("Entregado")');
  await page.waitForSelector('text=ahora está ENTREGADO');
  await foto('12-delivery-entregado');
  await salir();
});

await paso('El cliente ve su pedido ENTREGADO', async () => {
  await login('maria', 'flores123');
  await page.waitForSelector('button:has-text("Salir")');
  await page.goto(`${BASE}/mis-pedidos`);
  await page.waitForSelector('text=ENTREGADO');
  await foto('13-cliente-entregado');
});

await page.setViewportSize({ width: 390, height: 844 });
await paso('Vista en celular', async () => {
  await page.goto(`${BASE}/`);
  await page.waitForSelector('.card .titulo');
  await page.screenshot({ path: `${OUT}/14-celular.png` });
});

await browser.close();
fs.writeFileSync(`${OUT}/resultado-recorrido.txt`, resultados.join('\n') + '\n');
console.log(resultados.join('\n'));
if (resultados.some((r) => r.startsWith('FALLA'))) process.exit(1);
