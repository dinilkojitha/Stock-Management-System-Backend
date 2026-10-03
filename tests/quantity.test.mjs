import assert from 'node:assert/strict'
import { test } from 'node:test'
import { validateQuantityAdjustment } from '../src/components/inventory/quantityUtils.js'

test('quantity adjustments allow additions, reductions, and zero remaining', () => {
  for (const delta of ['2.5', '-2.5', '-5', '0']) assert.equal(validateQuantityAdjustment(delta, 5), '')
})

test('quantity adjustments reject blanks, nonfinite values, overflow and negative results', () => {
  for (const delta of ['', ' ', 'NaN', 'Infinity', '-6']) assert.notEqual(validateQuantityAdjustment(delta, 5), '')
  assert.notEqual(validateQuantityAdjustment('1e308', 1e308), '')
})
