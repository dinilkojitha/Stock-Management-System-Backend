// Explicit opt-in: writes uniquely named temporary records to the REAL backend.
// No branch creation, schema changes, or changes to existing records.
import assert from 'node:assert/strict'
import * as api from '../src/api/inventoryApi.js'
import { addCalendarDays, localToday, expiryStatus } from '../src/components/inventory/stockUtils.js'

if (!process.argv.includes('--run-live')) {
  console.log('Start Spring Boot with your test MySQL database, then run: node tests/live-inventory.mjs --run-live')
  process.exit(0)
}

const marker = `Review-${Date.now()}`
const cleanup = []
let checks = 0
function check(condition, message) { assert.ok(condition, message); checks++; console.log(`PASS ${message}`) }
async function raw(path, method = 'GET', body, headers = {}) {
  return fetch(`${api.API_BASE_URL}${path}`, {
    method, headers: { ...(body ? { 'Content-Type': 'application/json' } : {}), ...headers },
    ...(body ? { body: JSON.stringify(body) } : {}), signal: AbortSignal.timeout(15000),
  })
}
async function rejects(path, method, body, status) {
  const response = await raw(path, method, body)
  check(response.status === status, `${method} ${path} rejects invalid/conflicting data (${status})`)
}

try {
  for (const origin of ['http://localhost:5173', 'http://127.0.0.1:5173']) {
    for (const path of ['/api/inventory/dashboard', '/api/inventory-items', '/api/categories', '/api/unit-types', '/api/stocks', '/api/branches']) {
      const response = await raw(path, 'GET', undefined, { Origin: origin })
      check(response.ok && [origin, '*'].includes(response.headers.get('access-control-allow-origin')), `CORS GET ${path} from ${origin}`)
      if (!path.includes('/dashboard') && path !== '/api/branches') {
        const preflight = await raw(`${path}/1`, 'OPTIONS', undefined, { Origin: origin, 'Access-Control-Request-Method': 'PUT', 'Access-Control-Request-Headers': 'content-type' })
        check(preflight.ok && preflight.headers.get('access-control-allow-methods')?.includes('PUT'), `CORS PUT preflight ${path}`)
      }
    }
  }
  const preservedOrigin = await raw('/api/inventory/dashboard', 'GET', undefined, { Origin: 'http://localhost:3000' })
  check(preservedOrigin.ok, 'Existing team frontend origin remains supported')
  const blockedOrigin = await raw('/api/inventory/dashboard', 'GET', undefined, { Origin: 'https://untrusted.example' })
  check(blockedOrigin.status === 403, 'Unlisted origins remain blocked')
  const branchMutation = await raw('/api/branches', 'OPTIONS', undefined, { Origin: 'http://localhost:5173', 'Access-Control-Request-Method': 'POST', 'Access-Control-Request-Headers': 'content-type' })
  check(branchMutation.status === 403, 'Vite branch access remains read-only; branch CRUD not enabled')
  const baseline = await api.getInventoryDashboard()
  const branches = await api.getBranches()
  assert.ok(branches.length, 'An existing branch is required; this test never creates branches.')
  let category = await api.createCategory({ name: marker, description: 'Temporary integration verification' })
  cleanup.unshift(() => api.deleteCategory(category.categoryId))
  category = await api.updateCategory(category.categoryId, { name: `${marker}-edited`, description: 'Edited' })
  check((await api.getCategories()).some((row) => row.categoryId === category.categoryId && row.description === 'Edited'), 'Category create/read/update')
  let unit = await api.createUnitType({ name: marker })
  cleanup.unshift(() => api.deleteUnitType(unit.unitTypeId))
  unit = await api.updateUnitType(unit.unitTypeId, { name: `${marker}-edited` })
  check((await api.getUnitTypes()).some((row) => row.unitTypeId === unit.unitTypeId && row.name.endsWith('-edited')), 'Unit type create/read/update')

  const payload = { name: marker, categoryId: category.categoryId, unitTypeId: unit.unitTypeId, totalQuantity: 5, unitPrice: 12.5, reorderThreshold: 5, description: 'Temporary integration verification' }
  let item = await api.createInventoryItem(payload)
  cleanup.unshift(() => api.deleteInventoryItem(item.id))
  check((await api.getInventoryItem(item.id)).categoryId === category.categoryId, 'Inventory create/read and JSON foreign-key properties')
  item = await api.updateInventoryItem(item.id, { ...payload, name: `${marker}-edited`, totalQuantity: 6 })
  check(item.totalQuantity === 6 && item.name.endsWith('-edited'), 'Inventory update')
  item = await api.updateInventoryItemQuantity(item.id, -1)
  check(item.totalQuantity === 5, 'Quantity adjustment accepts a delta, not an absolute total')
  check((await api.searchInventoryItems(marker)).some((row) => row.id === item.id), 'Server-side search')
  check((await api.getLowStockItems()).some((row) => row.id === item.id), 'Low stock includes quantity equal to reorder threshold')
  await rejects(`/api/inventory-items/${item.id}/quantity`, 'PUT', { quantityDelta: -6 }, 400)
  for (const invalid of [{ name: ' ' }, { categoryId: null }, { unitTypeId: null }, { totalQuantity: -1 }, { unitPrice: -1 }, { reorderThreshold: -1 }]) {
    await rejects('/api/inventory-items', 'POST', { ...payload, ...invalid }, 400)
  }
  await rejects('/api/categories', 'POST', { name: ' ' }, 400)
  await rejects('/api/unit-types', 'POST', { name: ' ' }, 400)
  await rejects(`/api/categories/${category.categoryId}`, 'DELETE', undefined, 409)
  await rejects(`/api/unit-types/${unit.unitTypeId}`, 'DELETE', undefined, 409)

  const stockIds = new Set((await api.getStocks()).map((row) => row.stockId))
  let stockId = 1000000000 + Math.floor(Math.random() * 1000000000)
  while (stockIds.has(stockId)) stockId++
  const today = localToday()
  const stockPayload = { stockId, itemIds: [item.id], branchId: branches[0].id, quantity: 2.5, manufactureDate: addCalendarDays(today, -2), expiryDate: today }
  await api.createStock(stockPayload)
  cleanup.unshift(() => api.deleteStock(stockId))
  const stock = await api.getStock(stockId)
  check(stock.stockId === stockId && stock.itemIds.includes(item.id), 'Stock create/read uses manual ID and JPA stock_items relationship')
  check((await api.getStocksByItem(item.id)).some((row) => row.stockId === stockId), 'Stock item filter')
  check((await api.getStocksByBranch(branches[0].id)).some((row) => row.stockId === stockId), 'Stock branch lookup/filter (read only)')
  await rejects('/api/stocks', 'POST', stockPayload, 409)
  await rejects('/api/stocks', 'POST', { ...stockPayload, stockId: null }, 400)
  await rejects(`/api/stocks/${stockId}`, 'PUT', { ...stockPayload, quantity: -1 }, 400)
  await rejects(`/api/stocks/${stockId}`, 'PUT', { ...stockPayload, expiryDate: addCalendarDays(today, -3) }, 400)
  await rejects(`/api/inventory-items/${item.id}`, 'DELETE', undefined, 409)
  for (const offset of [-1, 0, 30, 31]) {
    const expiryDate = addCalendarDays(today, offset)
    const updated = await api.updateStock(stockId, { ...stockPayload, expiryDate })
    check(updated.expiryDate === expiryDate, `Stock update round-trips date-only offset ${offset}`)
    check((await api.getExpiringStocks(30)).some((row) => row.stockId === stockId) === (offset >= 0 && offset <= 30), `Expiring API inclusive boundary ${offset}`)
    check((await api.getExpiredStocks()).some((row) => row.stockId === stockId) === (offset < 0), `Expired API boundary ${offset}`)
    const status = expiryStatus(expiryDate, today)
    check(status === (offset < 0 ? 'EXPIRED' : offset <= 30 ? 'EXPIRING SOON' : 'OK'), `Frontend expiry status boundary ${offset}`)
  }
  await api.updateStock(stockId, stockPayload)
  const summary = await api.getInventoryDashboard()
  check(summary.totalItems === baseline.totalItems + 1 && summary.totalQuantity === baseline.totalQuantity + 5, 'Dashboard item count and quantity match MySQL writes')
  check(summary.totalInventoryValue === baseline.totalInventoryValue + 62.5, 'Dashboard valuation equals quantity × unit price')
  check(summary.lowStockItems === baseline.lowStockItems + 1 && summary.expiringSoonBatches === baseline.expiringSoonBatches + 1, 'Dashboard low-stock and expiry counters')
} finally {
  // Only IDs returned by successful creates in this run are eligible for cleanup.
  for (const remove of cleanup) {
    try { await remove(); checks++; console.log('PASS temporary record DELETE') }
    catch (error) { process.exitCode = 1; console.error(`Temporary-record cleanup failed: ${error.message}`) }
  }
}
console.log(`${checks} live integration checks passed. Temporary test records removed.`)
