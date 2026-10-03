import InventoryIcon from './InventoryIcon.jsx'

export default function ReferenceDataTable({ records, idKey, columns, onEdit, onDelete }) {
  return (
    <div className="inv-table-scroll" role="region" aria-label="Management records table. Scroll horizontally for more columns." tabIndex={0}>
      <table className="inv-table inv-reference-table">
        <thead><tr><th scope="col">ID</th>{columns.map((column) => <th key={column.key} scope="col">{column.label}</th>)}<th scope="col">Actions</th></tr></thead>
        <tbody>{records.map((record) => (
          <tr key={record[idKey]}>
            <td className="inv-item-id">#{record[idKey]}</td>
            {columns.map((column, index) => index === 0
              ? <th key={column.key} scope="row" className="inv-item-name">{record[column.key]}</th>
              : <td key={column.key} className="inv-description-cell">{record[column.key] || <span className="inv-muted-value">No description</span>}</td>)}
            <td><div className="inv-row-actions"><button className="inv-text-button" aria-label={`Edit ${record.name}`} onClick={() => onEdit(record)}><InventoryIcon name="edit" />Edit</button><button className="inv-icon-button inv-delete-button" aria-label={`Delete ${record.name}`} title="Delete" onClick={() => onDelete(record)}><InventoryIcon name="trash" /></button></div></td>
          </tr>
        ))}</tbody>
      </table>
    </div>
  )
}
