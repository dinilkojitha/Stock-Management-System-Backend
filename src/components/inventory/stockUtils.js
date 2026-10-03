export function localToday(now = new Date()) {
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
}

function validDate(value) {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) return false
  const parsed = new Date(`${value}T00:00:00Z`)
  return Number.isFinite(parsed.getTime()) && parsed.toISOString().slice(0, 10) === value
}

export function addCalendarDays(value, days) {
  const parsed = new Date(`${value}T00:00:00Z`)
  parsed.setUTCDate(parsed.getUTCDate() + days)
  return parsed.toISOString().slice(0, 10)
}

// Compare ISO calendar dates, never elapsed hours or UTC-converted local midnights.
export function expiryStatus(expiryDate, today = localToday()) {
  if (!expiryDate) return 'OK'
  if (!validDate(expiryDate)) return 'UNKNOWN'
  if (expiryDate < today) return 'EXPIRED'
  if (expiryDate <= addCalendarDays(today, 30)) return 'EXPIRING SOON'
  return 'OK'
}

export function filterStocks(stocks, { itemId = '', branchId = '', filter = 'all', today = localToday() } = {}) {
  return stocks.filter((stock) => (
    (!itemId || stock.itemIds?.some((id) => String(id) === String(itemId))) &&
    (!branchId || String(stock.branchId) === String(branchId)) &&
    (filter === 'all' || expiryStatus(stock.expiryDate, today) === (filter === 'expired' ? 'EXPIRED' : 'EXPIRING SOON'))
  ))
}

const isIntegerId = (value) => /^-?\d+$/.test(String(value)) && Number(value) >= -2147483648 && Number(value) <= 2147483647

export function validateStock(values) {
  const errors = {}
  if (!isIntegerId(values.stockId)) errors.stockId = 'Enter a whole-number Stock ID between -2147483648 and 2147483647.'
  if (!values.itemIds.length) errors.itemIds = 'Select at least one inventory item.'
  if (!isIntegerId(values.branchId)) errors.branchId = 'Select a branch.'
  if (String(values.quantity).trim() === '' || !Number.isFinite(Number(values.quantity)) || Number(values.quantity) < 0) {
    errors.quantity = 'Quantity must be a number greater than or equal to zero.'
  }
  for (const field of ['manufactureDate', 'expiryDate']) {
    if (values[field] && (!validDate(values[field]) || values[field] < '1000-01-01' || values[field] > '9999-12-31')) {
      errors[field] = 'Enter a valid date between years 1000 and 9999.'
    }
  }
  if (!errors.manufactureDate && !errors.expiryDate && values.manufactureDate && values.expiryDate && values.expiryDate < values.manufactureDate) {
    errors.expiryDate = 'Expiry date cannot be before manufacture date.'
  }
  return errors
}

export function stockPayload(values) {
  return {
    stockId: Number(values.stockId),
    itemIds: [...new Set(values.itemIds.map(Number))],
    branchId: Number(values.branchId),
    quantity: Number(values.quantity),
    manufactureDate: values.manufactureDate || null,
    expiryDate: values.expiryDate || null,
  }
}
