import assert from 'node:assert/strict'
import { test } from 'node:test'
import { addCalendarDays, expiryStatus, filterStocks, localToday, stockPayload, validateStock } from '../src/components/inventory/stockUtils.js'
import * as api from '../src/api/inventoryApi.js'

const values = { stockId: '500', itemIds: ['7', '8'], branchId: '1', quantity: '10.5', manufactureDate: '2026-09-01', expiryDate: '2026-10-01' }

test('expiry uses inclusive today/day-30 boundaries, with earlier dates expired', () => {
  const today = '2026-09-21'
  assert.equal(expiryStatus('2026-09-20', today), 'EXPIRED')
  assert.equal(expiryStatus('2026-09-21', today), 'EXPIRING SOON')
  assert.equal(expiryStatus('2026-10-21', today), 'EXPIRING SOON')
  assert.equal(expiryStatus('2026-10-22', today), 'OK')
  assert.equal(expiryStatus(null, today), 'OK')
  assert.equal(expiryStatus('2026-02-30', today), 'UNKNOWN')
})

test('date-only arithmetic crosses month, year, leap-year and DST boundaries', () => {
  assert.equal(addCalendarDays('2026-12-20', 30), '2027-01-19')
  assert.equal(addCalendarDays('2028-02-01', 30), '2028-03-02')
  assert.equal(addCalendarDays('2026-03-01', 30), '2026-03-31')
  assert.equal(localToday(new Date(2026, 8, 21, 0, 1)), '2026-09-21')
})

test('manual IDs and finite nonnegative quantities are required; dates may be equal or absent', () => {
  assert.deepEqual(validateStock(values), {})
  for (const stockId of ['', '1.5', 'Infinity', '2147483648']) {
    assert.ok(validateStock({ ...values, stockId }).stockId)
  }
  for (const quantity of ['', '-1', 'Infinity', 'NaN']) {
    assert.ok(validateStock({ ...values, quantity }).quantity)
  }
  assert.ok(validateStock({ ...values, itemIds: [] }).itemIds)
  assert.ok(validateStock({ ...values, branchId: '' }).branchId)
  assert.deepEqual(validateStock({ ...values, quantity: '0', expiryDate: values.manufactureDate }), {})
  assert.deepEqual(validateStock({ ...values, manufactureDate: '', expiryDate: '' }), {})
  assert.ok(validateStock({ ...values, expiryDate: '2026-08-31' }).expiryDate)
  assert.ok(validateStock({ ...values, manufactureDate: '0999-12-31' }).manufactureDate)
  assert.ok(validateStock({ ...values, manufactureDate: '2026-02-30' }).manufactureDate)
})

test('stock payload preserves the assigned ID and every existing item association', () => {
  assert.deepEqual(stockPayload(values), { stockId: 500, itemIds: [7, 8], branchId: 1, quantity: 10.5, manufactureDate: '2026-09-01', expiryDate: '2026-10-01' })
  const payload = stockPayload({ ...values, itemIds: ['7', '8', '7'], manufactureDate: '', expiryDate: '' })
  assert.deepEqual(payload.itemIds, [7, 8])
  assert.equal(payload.manufactureDate, null)
  assert.equal(payload.expiryDate, null)
})

test('combined item/branch/expiry filters use intersection and retain multi-item batches', () => {
  const rows = [
    { stockId: 1, itemIds: [7, 8], branchId: 1, expiryDate: '2026-09-21' },
    { stockId: 2, itemIds: [7], branchId: 2, expiryDate: '2026-09-20' },
    { stockId: 3, itemIds: [8], branchId: 1, expiryDate: '2026-10-22' },
  ]
  const filters = { itemId: '8', branchId: '1', filter: 'expiring', today: '2026-09-21' }
  assert.deepEqual(filterStocks(rows, filters).map((row) => row.stockId), [1])
  assert.deepEqual(filterStocks(rows, { ...filters, filter: 'all' }).map((row) => row.stockId), [1, 3])
  assert.deepEqual(filterStocks(rows, { filter: 'expired', today: filters.today }).map((row) => row.stockId), [2])
})

// Test-only transport stubs. The running screen always uses real REST endpoints.
test('all Stock REST endpoints and the read-only branch lookup use the correct contract', async (context) => {
  const calls = []
  context.mock.method(globalThis, 'fetch', async (url, options) => {
    calls.push({ path: url.replace(api.API_BASE_URL, ''), ...options })
    return options.method === 'DELETE' ? new Response(null, { status: 204 }) : Response.json([])
  })
  await api.getStocks()
  await api.getStock(500)
  await api.getStocksByItem(7)
  await api.getStocksByBranch(1)
  await api.getExpiringStocks(30)
  await api.getExpiredStocks()
  await api.getBranches()
  await api.createStock(stockPayload(values))
  await api.updateStock(500, stockPayload(values))
  assert.equal(await api.deleteStock(500), null)
  assert.deepEqual(calls.map((call) => call.path), [
    '/api/stocks', '/api/stocks/500', '/api/stocks/item/7', '/api/stocks/branch/1',
    '/api/stocks/expiring?days=30', '/api/stocks/expired', '/api/branches',
    '/api/stocks', '/api/stocks/500', '/api/stocks/500',
  ])
  assert.deepEqual(calls.slice(7).map((call) => call.method), ['POST', 'PUT', 'DELETE'])
  assert.deepEqual(JSON.parse(calls[7].body), stockPayload(values))
  assert.deepEqual(JSON.parse(calls[8].body), stockPayload(values))
})

test('duplicate Stock IDs and deletion conflicts preserve readable backend details', async (context) => {
  const stub = context.mock.method(globalThis, 'fetch', async () => Response.json({ detail: 'Stock ID already exists: 500' }, { status: 409 }))
  await assert.rejects(api.createStock(stockPayload(values)), /Stock ID already exists: 500/)
  stub.mock.mockImplementation(async () => Response.json({ detail: 'Stock cannot be deleted because another record references it' }, { status: 409 }))
  await assert.rejects(api.deleteStock(500), /another record references it/)
})
