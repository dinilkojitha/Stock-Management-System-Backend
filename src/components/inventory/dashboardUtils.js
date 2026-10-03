import { addCalendarDays, localToday } from './stockUtils.js'

const configuredCurrency = String(import.meta.env?.VITE_CURRENCY || 'LKR').toUpperCase()
export const INVENTORY_CURRENCY = /^[A-Z]{3}$/.test(configuredCurrency) ? configuredCurrency : 'LKR'

const numberFormatter = new Intl.NumberFormat('en-LK', { maximumFractionDigits: 2 })
const currencyFormatter = new Intl.NumberFormat('en-LK', {
  style: 'currency',
  currency: INVENTORY_CURRENCY,
  currencyDisplay: 'code',
  minimumFractionDigits: 2,
  maximumFractionDigits: 2,
})
const dateFormatter = new Intl.DateTimeFormat('en-LK', {
  day: 'numeric',
  month: 'short',
  year: 'numeric',
  timeZone: 'UTC',
})

export const formatDashboardNumber = (value) => numberFormatter.format(Number(value ?? 0))
export const formatInventoryCurrency = (value) => currencyFormatter.format(Number(value ?? 0))

export function formatCalendarDate(value) {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(value || '')) return 'Not set'
  const parsed = new Date(`${value}T00:00:00Z`)
  return Number.isFinite(parsed.getTime()) ? dateFormatter.format(parsed) : 'Not set'
}

export function expiryCountdown(expiryDate, today = localToday()) {
  if (!expiryDate) return 'No expiry date'
  if (expiryDate === today) return 'Expires today'
  const todayTime = Date.parse(`${today}T00:00:00Z`)
  const expiryTime = Date.parse(`${expiryDate}T00:00:00Z`)
  const days = Math.round((expiryTime - todayTime) / 86400000)
  if (!Number.isFinite(days)) return 'Date unavailable'
  if (days < 0) return 'Expired'
  return `${days} ${days === 1 ? 'day' : 'days'} remaining`
}

export function dashboardWindow(today = localToday()) {
  return `${formatCalendarDate(today)} – ${formatCalendarDate(addCalendarDays(today, 30))}`
}

export function validateDashboardSummary(summary) {
  const fields = ['totalItems', 'totalQuantity', 'lowStockItems', 'expiringSoonBatches', 'expiredBatches', 'totalInventoryValue']
  return Boolean(summary && fields.every((field) =>
    typeof summary[field] === 'number' && Number.isFinite(summary[field]) && summary[field] >= 0,
  ))
}
