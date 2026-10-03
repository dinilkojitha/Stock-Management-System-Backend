import { useState } from 'react'
import { formatValue, inventoryValue, itemPayload, validateItem } from './inventoryUtils.js'
import InventoryIcon from './InventoryIcon.jsx'

export default function InventoryItemForm({ item, categories, unitTypes, onSave, onCancel, busy, error }) {
  const [values, setValues] = useState({
    name: item?.name ?? '',
    categoryId: String(item?.categoryId ?? ''),
    unitTypeId: String(item?.unitTypeId ?? ''),
    totalQuantity: String(item?.totalQuantity ?? 0),
    unitPrice: String(item?.unitPrice ?? 0),
    reorderThreshold: String(item?.reorderThreshold ?? 0),
    description: item?.description ?? '',
  })
  const [errors, setErrors] = useState({})

  function change(event) {
    const { name, value } = event.target
    setValues((current) => ({ ...current, [name]: value }))
    setErrors((current) => ({ ...current, [name]: undefined }))
  }

  function submit(event) {
    event.preventDefault()
    const nextErrors = validateItem(values)
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length) {
      event.currentTarget.elements.namedItem(Object.keys(nextErrors)[0])?.focus()
      return
    }
    onSave(itemPayload(values))
  }

  function attributes(name) {
    return { id: `inv-${name}`, name, value: values[name], onChange: change,
      'aria-invalid': Boolean(errors[name]), 'aria-describedby': errors[name] ? `inv-${name}-error` : undefined }
  }

  const fieldError = (name) => errors[name] && <span className="inv-field-error" id={`inv-${name}-error`}>{errors[name]}</span>
  const missingLookups = !categories.length || !unitTypes.length

  return (
    <form className="inv-form" onSubmit={submit} noValidate>
      {error && <div className="inv-notice inv-notice--error" role="alert"><InventoryIcon name="alert" /><span>{error}</span></div>}
      {missingLookups && <div className="inv-notice inv-notice--warning" role="status">A category and a unit type must exist before an inventory item can be saved. Ask the person managing these records to add them, then refresh this page.</div>}
      <fieldset disabled={busy}>
        <div className="inv-field inv-field--full">
          <label htmlFor="inv-name">Item Name <span>*</span></label>
          <input {...attributes('name')} required maxLength={60} placeholder="e.g. Basmati rice" autoComplete="off" />
          {fieldError('name')}
        </div>
        <div className="inv-field">
          <label htmlFor="inv-categoryId">Category <span>*</span></label>
          <select {...attributes('categoryId')} required>
            <option value="">Select a category</option>
            {item?.categoryId && !categories.some((category) => String(category.categoryId) === String(item.categoryId)) && <option value={item.categoryId} disabled>Unavailable category #{item.categoryId}</option>}
            {categories.map((category) => <option key={category.categoryId} value={category.categoryId}>{category.name}</option>)}
          </select>
          {fieldError('categoryId')}
        </div>
        <div className="inv-field">
          <label htmlFor="inv-unitTypeId">Unit Type <span>*</span></label>
          <select {...attributes('unitTypeId')} required>
            <option value="">Select a unit type</option>
            {item?.unitTypeId && !unitTypes.some((unit) => String(unit.unitTypeId) === String(item.unitTypeId)) && <option value={item.unitTypeId} disabled>Unavailable unit type #{item.unitTypeId}</option>}
            {unitTypes.map((unit) => <option key={unit.unitTypeId} value={unit.unitTypeId}>{unit.name}</option>)}
          </select>
          {fieldError('unitTypeId')}
        </div>
        {[['totalQuantity', 'Quantity'], ['unitPrice', 'Unit Price'], ['reorderThreshold', 'Reorder Threshold']].map(([name, label]) => (
          <div className="inv-field" key={name}>
            <label htmlFor={`inv-${name}`}>{label} <span>*</span></label>
            <input {...attributes(name)} type="number" min="0" step="any" inputMode="decimal" required />
            {fieldError(name)}
          </div>
        ))}
        <div className="inv-form-value"><span>Calculated inventory value</span><strong>{Number.isFinite(inventoryValue(values)) ? formatValue(inventoryValue(values)) : '—'}</strong><small>Quantity × unit price</small></div>
        <div className="inv-field inv-field--full">
          <label htmlFor="inv-description">Description <small>Optional</small></label>
          <textarea {...attributes('description')} rows={3} placeholder="Add specifications or storage notes…" />
        </div>
      </fieldset>
      <p className="inv-form-hint">Items at or below the reorder threshold are marked as low stock.</p>
      <div className="inv-modal-actions">
        <button type="button" className="inv-button inv-button--secondary" disabled={busy} onClick={onCancel}>Cancel</button>
        <button type="submit" className="inv-button inv-button--primary" disabled={busy || missingLookups}>{busy ? 'Saving…' : item ? 'Save Changes' : 'Create Inventory Item'}</button>
      </div>
    </form>
  )
}
