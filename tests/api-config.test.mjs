import assert from 'node:assert/strict'
import { test } from 'node:test'
import * as api from '../src/api/inventoryApi.js'

test('API base accepts documented /api and legacy origin without duplicating the prefix', () => {
  for (const value of ['http://localhost:2020', 'http://localhost:2020/', 'http://localhost:2020/api', ' http://localhost:2020/api/ ']) {
    assert.equal(api.normalizeApiBaseUrl(value), 'http://localhost:2020/api')
  }
  assert.equal(api.API_BASE_URL, 'http://localhost:2020/api')
})

test('HTTP failures retain their status and are not reported as connection failures', async (context) => {
  for (const status of [400, 404, 500]) {
    const stub = context.mock.method(globalThis, 'fetch', async () => Response.json({}, { status }))
    await assert.rejects(api.getCategories(), (error) => {
      assert.equal(error.status, status)
      assert.doesNotMatch(error.message, /Cannot connect/)
      return true
    })
    stub.mock.restore()
  }
})
