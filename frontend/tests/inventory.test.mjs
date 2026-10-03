import assert from 'node:assert/strict'
import { test } from 'node:test'
import { inventoryValue, isLowStock, itemPayload, validateItem } from '../src/components/inventory/inventoryUtils.js'
import * as api from '../src/api/inventoryApi.js'

const values = { name: '  Rice  ', categoryId: '1', unitTypeId: '2', totalQuantity: '10.5', unitPrice: '250', reorderThreshold: '10.5', description: '  Dry storage  ' }

test('stock status includes the exact threshold and valuation supports fractional quantities', () => {
  assert.equal(isLowStock(values), true)
  assert.equal(isLowStock({ ...values, totalQuantity: 10.6 }), false)
  assert.equal(isLowStock({ totalQuantity: 0, reorderThreshold: 0 }), true)
  assert.equal(inventoryValue(values), 2625)
})

test('validation rejects missing references, whitespace names, negative and non-finite quantities', () => {
  assert.deepEqual(validateItem(values), {})
  for (const field of ['name', 'categoryId', 'unitTypeId']) {
    assert.ok(validateItem({ ...values, [field]: '' })[field])
  }
  assert.ok(validateItem({ ...values, name: '   ' }).name)
  assert.ok(validateItem({ ...values, name: 'a'.repeat(61) }).name)
  for (const field of ['totalQuantity', 'unitPrice', 'reorderThreshold']) {
    for (const invalid of ['', '-1', 'Infinity', 'NaN']) {
      assert.ok(validateItem({ ...values, [field]: invalid })[field])
    }
    assert.equal(validateItem({ ...values, [field]: '0' })[field], undefined)
  }
})

test('create and edit payload matches the flat Spring inventory JSON contract', () => {
  assert.deepEqual(itemPayload(values), { name: 'Rice', categoryId: 1, unitTypeId: 2, totalQuantity: 10.5, unitPrice: 250, reorderThreshold: 10.5, description: 'Dry storage' })
})

// Isolated transport stubs only: the running application has no mock data or fallback.
test('API methods use the correct endpoints, verbs, JSON bodies and empty DELETE response', async (context) => {
  const calls = []
  context.mock.method(globalThis, 'fetch', async (url, options) => {
    calls.push({ url, ...options })
    return options.method === 'DELETE' ? new Response(null, { status: 204 }) : Response.json([])
  })
  const payload = itemPayload(values)
  await api.getInventoryItems()
  await api.getInventoryItem(7)
  await api.searchInventoryItems('rice & tea')
  await api.getLowStockItems()
  await api.getCategories()
  await api.getUnitTypes()
  await api.createInventoryItem(payload)
  await api.updateInventoryItem(7, payload)
  await api.updateInventoryItemQuantity(7, -2.5)
  assert.equal(await api.deleteInventoryItem(7), null)
  await api.createCategory({ name: 'Food', description: 'Kitchen supplies' })
  await api.updateCategory(3, { name: 'Food', description: 'Food supplies' })
  assert.equal(await api.deleteCategory(3), null)
  await api.createUnitType({ name: 'Kilogram' })
  await api.updateUnitType(4, { name: 'kg' })
  assert.equal(await api.deleteUnitType(4), null)
  assert.deepEqual(calls.map(({ url }) => url.replace(api.API_BASE_URL, '')), [
    '/api/inventory-items', '/api/inventory-items/7', '/api/inventory-items/search?keyword=rice+%26+tea',
    '/api/inventory-items/low-stock', '/api/categories', '/api/unit-types',
    '/api/inventory-items', '/api/inventory-items/7', '/api/inventory-items/7/quantity', '/api/inventory-items/7',
    '/api/categories', '/api/categories/3', '/api/categories/3',
    '/api/unit-types', '/api/unit-types/4', '/api/unit-types/4',
  ])
  assert.deepEqual(calls.slice(6).map(({ method }) => method), ['POST', 'PUT', 'PUT', 'DELETE', 'POST', 'PUT', 'DELETE', 'POST', 'PUT', 'DELETE'])
  assert.deepEqual(JSON.parse(calls[6].body), payload)
  assert.deepEqual(JSON.parse(calls[7].body), payload)
  assert.deepEqual(JSON.parse(calls[8].body), { quantityDelta: -2.5 })
  assert.deepEqual(JSON.parse(calls[10].body), { name: 'Food', description: 'Kitchen supplies' })
  assert.deepEqual(JSON.parse(calls[11].body), { name: 'Food', description: 'Food supplies' })
  assert.deepEqual(JSON.parse(calls[13].body), { name: 'Kilogram' })
  assert.deepEqual(JSON.parse(calls[14].body), { name: 'kg' })
  assert.equal(calls[6].headers['Content-Type'], 'application/json')
})

test('API preserves useful backend errors and translates connection failures', async (context) => {
  const stub = context.mock.method(globalThis, 'fetch', async () => Response.json({ detail: 'Inventory item cannot be deleted because it is in use' }, { status: 409 }))
  await assert.rejects(api.deleteInventoryItem(7), /cannot be deleted because it is in use/)
  stub.mock.mockImplementation(async () => { throw new TypeError('Failed to fetch') })
  await assert.rejects(api.getInventoryItems(), /Cannot connect to the inventory service/)
  stub.mock.mockImplementation(async () => new Response('<html>Proxy failure</html>', { status: 502 }))
  await assert.rejects(api.getInventoryItems(), /request failed \(502\)/)
  stub.mock.mockImplementation(async () => Response.json({ unexpected: true }))
  await assert.rejects(api.getInventoryItems(), /unexpected list format/)
})

test('cancelled reads remain AbortError so superseded search responses can be ignored', async (context) => {
  context.mock.method(globalThis, 'fetch', async (_url, options) => {
    options.signal.throwIfAborted()
    return Response.json([])
  })
  const controller = new AbortController()
  controller.abort()
  await assert.rejects(api.searchInventoryItems('rice', { signal: controller.signal }), { name: 'AbortError' })
})
