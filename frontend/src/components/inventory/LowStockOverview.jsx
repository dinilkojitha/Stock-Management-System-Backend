import InventoryIcon from './InventoryIcon.jsx'
import { formatQuantity } from './inventoryUtils.js'

export default function LowStockOverview({ items }) {
  return (
    <section className="inv-panel inv-dashboard-panel" aria-labelledby="dashboard-low-stock">
      <div className="inv-panel-heading"><div><h2 id="dashboard-low-stock">Low Stock <span className="inv-count">{items.length}</span></h2><p>Items that need replenishment attention.</p></div><a className="inv-panel-link" href="#/inventory-items">Manage Items <InventoryIcon name="arrow" /></a></div>
      {items.length ? <div className="inv-dashboard-list">
        {items.map((item) => {
          const quantity = Number(item.totalQuantity ?? 0)
          const threshold = Number(item.reorderThreshold ?? 0)
          return <article className="inv-alert-row" key={item.id}>
            <span className="inv-alert-symbol inv-alert-symbol--amber"><InventoryIcon name="alert" /></span>
            <div className="inv-alert-main"><strong>{item.name}</strong><small>Item #{item.id}</small></div>
            <div className="inv-alert-metric"><small>On hand</small><strong>{formatQuantity(quantity)}</strong></div>
            <div className="inv-alert-metric"><small>Reorder at</small><strong>{formatQuantity(threshold)}</strong></div>
            <span className="inv-status inv-status--low"><span />LOW STOCK</span>
          </article>
        })}
      </div> : <div className="inv-dashboard-empty"><span><InventoryIcon name="check" /></span><div><strong>Stock levels look good</strong><p>No items are currently at or below their reorder level.</p></div></div>}
    </section>
  )
}
