export const API_BASE_URL = (
  import.meta.env?.VITE_API_BASE_URL || 'http://localhost:2020'
).replace(/\/+$/, '')

const statusMessages = {
  400: 'Please check the submitted details and try again.',
  401: 'Your session is not authorized to access inventory.',
  403: 'You do not have permission to perform this action.',
  404: 'The requested inventory record could not be found.',
  409: 'This item is in use or conflicts with another record.',
  500: 'The inventory service encountered an error. Please try again.',
}

async function request(path, { body, signal, ...options } = {}) {
  const timeout = new AbortController()
  const timer = setTimeout(() => timeout.abort(), 15000)
  const abort = () => timeout.abort()
  signal?.addEventListener('abort', abort, { once: true })
  if (signal?.aborted) timeout.abort()

  try {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      ...options,
      signal: timeout.signal,
      headers: { Accept: 'application/json', ...(body ? { 'Content-Type': 'application/json' } : {}) },
      ...(body ? { body: JSON.stringify(body) } : {}),
    })
    const text = await response.text()
    let data
    try { data = text ? JSON.parse(text) : null } catch { data = null }

    if (!response.ok) {
      const detail = data?.detail || data?.message
      throw new Error(
        (typeof detail === 'string' && detail) ||
        statusMessages[response.status] || `The request failed (${response.status}). Please try again.`,
      )
    }
    if (response.status !== 204 && text && data === null) {
      throw new Error('The inventory service returned an unreadable response.')
    }
    return data
  } catch (error) {
    if (signal?.aborted) throw new DOMException('Request cancelled', 'AbortError')
    if (timeout.signal.aborted) throw new Error('The inventory service took too long to respond. Please retry.', { cause: error })
    if (error instanceof TypeError) {
      throw new Error('Cannot connect to the inventory service. Check the backend URL, connection, and CORS settings.', { cause: error })
    }
    throw error
  } finally {
    clearTimeout(timer)
    signal?.removeEventListener('abort', abort)
  }
}

async function list(path, options) {
  const data = await request(path, options)
  if (!Array.isArray(data)) throw new Error('The inventory service returned an unexpected list format.')
  return data
}

const itemPath = (id) => `/api/inventory-items/${encodeURIComponent(id)}`
const categoryPath = (id) => `/api/categories/${encodeURIComponent(id)}`
const unitTypePath = (id) => `/api/unit-types/${encodeURIComponent(id)}`

export const getInventoryItems = (options) => list('/api/inventory-items', options)
export const getInventoryItem = (id, options) => request(itemPath(id), options)
export const searchInventoryItems = (keyword, options) =>
  list(`/api/inventory-items/search?${new URLSearchParams({ keyword })}`, options)
export const getLowStockItems = (options) => list('/api/inventory-items/low-stock', options)
export const createInventoryItem = (item) => request('/api/inventory-items', { method: 'POST', body: item })
export const updateInventoryItem = (id, item) => request(itemPath(id), { method: 'PUT', body: item })
export const deleteInventoryItem = (id) => request(itemPath(id), { method: 'DELETE' })
// This endpoint accepts a delta, not an absolute quantity. The edit form uses the full PUT above.
export const updateInventoryItemQuantity = (id, quantityDelta) =>
  request(`${itemPath(id)}/quantity`, { method: 'PUT', body: { quantityDelta } })
export const getCategories = (options) => list('/api/categories', options)
export const createCategory = (category) => request('/api/categories', { method: 'POST', body: category })
export const updateCategory = (id, category) => request(categoryPath(id), { method: 'PUT', body: category })
export const deleteCategory = (id) => request(categoryPath(id), { method: 'DELETE' })
export const getUnitTypes = (options) => list('/api/unit-types', options)
export const createUnitType = (unitType) => request('/api/unit-types', { method: 'POST', body: unitType })
export const updateUnitType = (id, unitType) => request(unitTypePath(id), { method: 'PUT', body: unitType })
export const deleteUnitType = (id) => request(unitTypePath(id), { method: 'DELETE' })

const stockPath = (id) => `/api/stocks/${encodeURIComponent(id)}`
export const getStocks = (options) => list('/api/stocks', options)
export const getStock = (id, options) => request(stockPath(id), options)
export const createStock = (stock) => request('/api/stocks', { method: 'POST', body: stock })
export const updateStock = (id, stock) => request(stockPath(id), { method: 'PUT', body: stock })
export const deleteStock = (id) => request(stockPath(id), { method: 'DELETE' })
export const getStocksByItem = (itemId, options) => list(`/api/stocks/item/${encodeURIComponent(itemId)}`, options)
export const getStocksByBranch = (branchId, options) => list(`/api/stocks/branch/${encodeURIComponent(branchId)}`, options)
export const getExpiringStocks = (days = 30, options) => list(`/api/stocks/expiring?${new URLSearchParams({ days })}`, options)
export const getExpiredStocks = (options) => list('/api/stocks/expired', options)
// Existing team endpoint, read-only lookup. BranchResponse has id/branchName.
export const getBranches = (options) => list('/api/branches', options)
export const getInventoryDashboard = (options) => request('/api/inventory/dashboard', options)
