import InventoryIcon from './InventoryIcon.jsx'
import { formatQuantity } from './inventoryUtils.js'
import { expiryStatus } from './stockUtils.js'

const statusClasses = { EXPIRED: 'inv-status--expired', 'EXPIRING SOON': 'inv-status--low', OK: 'inv-status--ok', UNKNOWN: 'inv-status--unknown' }

export default function StockTable({ stocks, items, branches, today, onEdit, onDelete }) {
  const itemNames = new Map(items.map((item) => [String(item.id), item.name]))
  const branchNames = new Map(branches.map((branch) => [String(branch.id), branch.branchName]))

  return (
    <div className="inv-table-scroll" role="region" aria-label="Stock batches table. Scroll horizontally for more columns." tabIndex={0}>
      <table className="inv-table inv-stock-table">
        <caption className="inv-sr-only">Stock batches, inventory items, branches, quantities, and expiry dates</caption>
        <thead><tr>{['Stock ID', 'Inventory Item', 'Branch', 'Quantity', 'Manufacture Date', 'Expiry Date', 'Expiry Status', 'Actions'].map((label) => <th key={label} scope="col" className={label === 'Quantity' ? 'inv-numeric' : ''}>{label}</th>)}</tr></thead>
        <tbody>{stocks.map((stock) => {
          const status = expiryStatus(stock.expiryDate, today)
          return <tr key={stock.stockId}>
            <th scope="row" className="inv-item-id">#{stock.stockId}</th>
            <td className="inv-stock-item-list">{stock.itemIds?.length ? stock.itemIds.map((id) => <span key={id}>{itemNames.get(String(id)) || `Item #${id}`}</span>) : <span className="inv-muted-value">No linked item</span>}</td>
            <td className="inv-stock-branch">{branchNames.get(String(stock.branchId)) || `Branch #${stock.branchId}`}</td>
            <td className="inv-numeric">{formatQuantity(stock.quantity)}</td>
            <td>{stock.manufactureDate ? <time dateTime={stock.manufactureDate}>{stock.manufactureDate}</time> : <span className="inv-muted-value">Not set</span>}</td>
            <td>{stock.expiryDate ? <time dateTime={stock.expiryDate}>{stock.expiryDate}</time> : <span className="inv-muted-value">Not set</span>}</td>
            <td><span className={`inv-status ${statusClasses[status]}`} title={!stock.expiryDate ? 'No expiry date set' : undefined}><span />{status}</span></td>
            <td><div className="inv-row-actions"><button className="inv-text-button" aria-label={`View or edit stock ${stock.stockId}`} onClick={() => onEdit(stock.stockId)}><InventoryIcon name="edit" />View/Edit</button><button className="inv-icon-button inv-delete-button" aria-label={`Delete stock ${stock.stockId}`} title="Delete stock batch" onClick={() => onDelete(stock)}><InventoryIcon name="trash" /></button></div></td>
          </tr>
        })}</tbody>
      </table>
    </div>
  )
}
