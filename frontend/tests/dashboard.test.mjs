import assert from 'node:assert/strict'
import { test } from 'node:test'
import * as api from '../src/api/inventoryApi.js'
import {
  dashboardWindow, expiryCountdown, formatInventoryCurrency,
  INVENTORY_CURRENCY, validateDashboardSummary,
} from '../src/components/inventory/dashboardUtils.js'

test('dashboard accepts the exact backend summary contract and rejects missing or invalid metrics', () => {
  const summary = {
    totalItems: 3,
    totalQuantity: 15,
    lowStockItems: 2,
    expiredBatches: 2,
    expiringSoonBatches: 3,
    totalInventoryValue: 3075,
  }
  assert.equal(validateDashboardSummary(summary), true)
  assert.equal(validateDashboardSummary({ ...summary, totalItems: undefined }), false)
  assert.equal(validateDashboardSummary({ ...summary, totalQuantity: -1 }), false)
  assert.equal(validateDashboardSummary({ ...summary, totalInventoryValue: 'not-a-number' }), false)
  for (const invalid of [null, '', '0', false, Infinity, NaN]) {
    assert.equal(validateDashboardSummary({ ...summary, totalItems: invalid }), false)
  }
})

test('inventory valuation is formatted with an explicit configured currency code', () => {
  assert.match(formatInventoryCurrency(3075), new RegExp(INVENTORY_CURRENCY))
  assert.match(formatInventoryCurrency(3075), /3[,.]075/)
})

test('expiry watch labels use inclusive calendar-day boundaries', () => {
  const window = dashboardWindow('2026-09-22')
  assert.match(window, /Sep.*22.*2026/i)
  assert.match(window, /Oct.*22.*2026/i)
  assert.equal(expiryCountdown('2026-09-22', '2026-09-22'), 'Expires today')
  assert.equal(expiryCountdown('2026-09-23', '2026-09-22'), '1 day remaining')
  assert.equal(expiryCountdown('2026-10-22', '2026-09-22'), '30 days remaining')
})

test('dashboard and its two required detail sections call the expected endpoints', async (context) => {
  const calls = []
  context.mock.method(globalThis, 'fetch', async (url) => {
    calls.push(url.replace(api.API_BASE_URL, ''))
    return url.endsWith('/api/inventory/dashboard')
      ? Response.json({ totalItems: 0, totalQuantity: 0, lowStockItems: 0, expiredBatches: 0, expiringSoonBatches: 0, totalInventoryValue: 0 })
      : Response.json([])
  })
  await api.getInventoryDashboard()
  await api.getLowStockItems()
  await api.getExpiringStocks(30)
  assert.deepEqual(calls, [
    '/api/inventory/dashboard',
    '/api/inventory-items/low-stock',
    '/api/stocks/expiring?days=30',
  ])
})
