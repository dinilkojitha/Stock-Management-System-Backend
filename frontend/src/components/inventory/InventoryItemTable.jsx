import InventoryIcon from './InventoryIcon.jsx'
import { formatQuantity, formatValue, inventoryValue, isLowStock } from './inventoryUtils.js'

export default function InventoryItemTable({ items, categories, unitTypes, onEdit, onDelete, onAdjust }) {
  const categoryNames = new Map(categories.map((category) => [String(category.categoryId), category.name]))
  const unitNames = new Map(unitTypes.map((unit) => [String(unit.unitTypeId), unit.name]))

  return (
    <div className="inv-table-scroll" role="region" aria-label="Inventory items table. Scroll horizontally for more columns." tabIndex={0}>
      <table className="inv-table">
        <caption className="inv-sr-only">Inventory items, stock levels, valuations, and actions</caption>
        <thead><tr>{['Item ID', 'Item Name', 'Category', 'Quantity', 'Unit Type', 'Unit Price', 'Inventory Value', 'Reorder Level', 'Status', 'Actions'].map((label) => <th key={label} scope="col" className={['Quantity', 'Unit Price', 'Inventory Value', 'Reorder Level'].includes(label) ? 'inv-numeric' : ''}>{label}</th>)}</tr></thead>
        <tbody>{items.map((item) => {
          const low = isLowStock(item)
          return (
            <tr key={item.id}>
              <td className="inv-item-id">#{item.id}</td>
              <th scope="row" className="inv-item-name">{item.name}</th>
              <td><span className="inv-category">{categoryNames.get(String(item.categoryId)) || `Category #${item.categoryId}`}</span></td>
              <td className={`inv-numeric ${low ? 'inv-low-quantity' : ''}`}>{formatQuantity(item.totalQuantity)}</td>
              <td>{unitNames.get(String(item.unitTypeId)) || `Unit #${item.unitTypeId}`}</td>
              <td className="inv-numeric">{formatValue(item.unitPrice)}</td>
              <td className="inv-numeric inv-value-cell">{formatValue(inventoryValue(item))}</td>
              <td className="inv-numeric">{formatQuantity(item.reorderThreshold)}</td>
              <td><span className={`inv-status ${low ? 'inv-status--low' : 'inv-status--ok'}`}><span />{low ? 'LOW STOCK' : 'IN STOCK'}</span></td>
              <td><div className="inv-row-actions"><button className="inv-text-button" aria-label={`View or edit ${item.name}`} onClick={() => onEdit(item.id)}><InventoryIcon name="edit" />View/Edit</button><button className="inv-text-button" aria-label={`Adjust quantity for ${item.name}`} onClick={() => onAdjust(item.id)}>Adjust Qty</button><button className="inv-icon-button inv-delete-button" aria-label={`Delete ${item.name}`} title="Delete item" onClick={() => onDelete(item)}><InventoryIcon name="trash" /></button></div></td>
            </tr>
          )
        })}</tbody>
      </table>
    </div>
  )
}
