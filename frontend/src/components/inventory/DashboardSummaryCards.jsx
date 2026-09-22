import InventoryIcon from './InventoryIcon.jsx'
import { formatDashboardNumber, formatInventoryCurrency, INVENTORY_CURRENCY } from './dashboardUtils.js'

export default function DashboardSummaryCards({ summary, loading }) {
  const cards = [
    { label: 'Total Inventory Items', field: 'totalItems', caption: 'Active inventory records', icon: 'box', tone: 'teal' },
    { label: 'Total Quantity', field: 'totalQuantity', caption: 'Units currently on hand', icon: 'quantity', tone: 'blue' },
    { label: 'Low Stock Items', field: 'lowStockItems', caption: 'At or below reorder level', icon: 'alert', tone: 'amber' },
    { label: 'Expiring Soon', field: 'expiringSoonBatches', caption: 'Due within 30 days', icon: 'clock', tone: 'amber' },
    { label: 'Expired Stock', field: 'expiredBatches', caption: 'Batches past expiry date', icon: 'calendar', tone: 'red' },
    { label: 'Total Inventory Value', field: 'totalInventoryValue', caption: `Current stock valuation · ${INVENTORY_CURRENCY}`, icon: 'value', tone: 'teal' },
  ]

  return (
    <section className="inv-dashboard-summary" aria-label="Inventory dashboard summary">
      {cards.map((card) => <article className="inv-summary-card inv-dashboard-card" key={card.field}>
        <div><p>{card.label}</p><strong className={card.field === 'totalInventoryValue' ? 'inv-currency-value' : ''}>{loading ? '—' : card.field === 'totalInventoryValue' ? formatInventoryCurrency(summary?.[card.field]) : formatDashboardNumber(summary?.[card.field])}</strong><small>{card.caption}</small></div>
        <span className={`inv-card-icon inv-card-icon--${card.tone}`}><InventoryIcon name={card.icon} /></span>
      </article>)}
    </section>
  )
}
