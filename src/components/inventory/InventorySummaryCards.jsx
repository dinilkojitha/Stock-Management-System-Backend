import InventoryIcon from './InventoryIcon.jsx'
import { formatQuantity, formatValue, inventoryValue, isLowStock } from './inventoryUtils.js'

export default function InventorySummaryCards({ items, loading, unavailable }) {
  const cards = [
    { label: 'Inventory items', value: formatQuantity(items.length), caption: 'Items in the current view', icon: 'box', tone: 'teal' },
    { label: 'Low stock items', value: formatQuantity(items.filter(isLowStock).length), caption: 'At or below reorder level', icon: 'alert', tone: 'amber' },
    { label: 'Inventory value', value: formatValue(items.reduce((sum, item) => sum + inventoryValue(item), 0)), caption: 'Quantity × unit price · current view', icon: 'value', tone: 'blue' },
  ]
  return <section className="inv-summary" aria-label="Current inventory view summary">{cards.map((card) => <article className="inv-summary-card" key={card.label}><div><p>{card.label}</p><strong>{loading || unavailable ? '—' : card.value}</strong><small>{card.caption}</small></div><span className={`inv-card-icon inv-card-icon--${card.tone}`}><InventoryIcon name={card.icon} /></span></article>)}</section>
}
