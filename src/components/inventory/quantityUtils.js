export function validateQuantityAdjustment(delta, currentQuantity) {
  if (!String(delta).trim() || !Number.isFinite(Number(delta))) return 'Enter a finite quantity adjustment.'
  const result = Number(currentQuantity ?? 0) + Number(delta)
  if (!Number.isFinite(result) || result < 0) return 'The resulting quantity must be finite and zero or greater.'
  return ''
}
