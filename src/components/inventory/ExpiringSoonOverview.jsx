import InventoryIcon from './InventoryIcon.jsx'
import { formatQuantity } from './inventoryUtils.js'
import { expiryCountdown, formatCalendarDate } from './dashboardUtils.js'

export default function ExpiringSoonOverview({ stocks, inventoryItems, today }) {
  const names = new Map(inventoryItems.map((item) => [String(item.id), item.name]))

  return (
    <section className="inv-panel inv-dashboard-panel" aria-labelledby="dashboard-expiring">
      <div className="inv-panel-heading"><div><h2 id="dashboard-expiring">Expiring Soon <span className="inv-count">{stocks.length}</span></h2><p>Batches expiring today or within 30 days.</p></div><a className="inv-panel-link" href="#/stock-batches">Manage Batches <InventoryIcon name="arrow" /></a></div>
      {stocks.length ? <div className="inv-dashboard-list">
        {stocks.map((stock) => {
          const itemNames = stock.itemIds?.map((id) => names.get(String(id)) || `Item #${id}`) || []
          return <article className="inv-alert-row" key={stock.stockId}>
            <span className="inv-alert-symbol inv-alert-symbol--amber"><InventoryIcon name="clock" /></span>
            <div className="inv-alert-main"><strong>{itemNames.length ? itemNames.join(', ') : 'Unlinked stock batch'}</strong><small>Stock #{stock.stockId} · Branch #{stock.branchId}</small></div>
            <div className="inv-alert-metric"><small>Quantity</small><strong>{formatQuantity(stock.quantity)}</strong></div>
            <div className="inv-alert-metric inv-expiry-metric"><small>{expiryCountdown(stock.expiryDate, today)}</small><strong>{formatCalendarDate(stock.expiryDate)}</strong></div>
            <span className="inv-status inv-status--low"><span />EXPIRING SOON</span>
          </article>
        })}
      </div> : <div className="inv-dashboard-empty"><span><InventoryIcon name="check" /></span><div><strong>No upcoming expiries</strong><p>No stock batches expire during the next 30 days.</p></div></div>}
    </section>
  )
}
