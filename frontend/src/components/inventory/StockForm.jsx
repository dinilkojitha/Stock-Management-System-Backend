import { useState } from 'react'
import { stockPayload, validateStock } from './stockUtils.js'

export default function StockForm({ stock, items, branches, onSave, onCancel, busy, error, lookupsReady }) {
  const [values, setValues] = useState({
    stockId: String(stock?.stockId ?? ''),
    itemIds: (stock?.itemIds ?? []).map(String),
    branchId: String(stock?.branchId ?? ''),
    quantity: String(stock?.quantity ?? 0),
    manufactureDate: stock?.manufactureDate ?? '',
    expiryDate: stock?.expiryDate ?? '',
  })
  const [errors, setErrors] = useState({})
  const unavailableIds = values.itemIds.filter((id) => !items.some((item) => String(item.id) === id))
  const missingOptions = lookupsReady && (!items.length || !branches.length)

  function change(event) {
    const { name, value } = event.target
    setValues((current) => ({ ...current, [name]: value }))
    setErrors((current) => ({ ...current, [name]: undefined }))
  }

  function submit(event) {
    event.preventDefault()
    if (busy || !lookupsReady || missingOptions) return
    const nextErrors = validateStock(values)
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length) {
      event.currentTarget.querySelector(`[name="${Object.keys(nextErrors)[0]}"]`)?.focus()
      return
    }
    onSave(stockPayload(values))
  }

  function attributes(name) {
    return { id: `stock-${name}`, name, value: values[name], onChange: change,
      'aria-invalid': Boolean(errors[name]), 'aria-describedby': errors[name] ? `stock-${name}-error` : undefined }
  }
  const fieldError = (name) => errors[name] && <span className="inv-field-error" id={`stock-${name}-error`}>{errors[name]}</span>

  return (
    <form className="inv-form" onSubmit={submit} noValidate>
      {error && <div className="inv-notice inv-notice--error" role="alert">{error}</div>}
      {missingOptions && <div className="inv-notice inv-notice--warning" role="status">An inventory item and an existing branch are needed to save a stock batch. Add inventory items in Inventory Management, or contact the branch administrator if no branches are available.</div>}
      <fieldset disabled={busy}>
        <div className="inv-field">
          <label htmlFor="stock-stockId">Stock ID <span>*</span></label>
          <input {...attributes('stockId')} type="number" step="1" min="-2147483648" max="2147483647" required readOnly={Boolean(stock)} placeholder="Enter a unique Stock ID" />
          {fieldError('stockId')}
          <small className="inv-field-help">{stock ? 'Stock ID cannot be changed.' : 'Enter a unique ID. Stock IDs are assigned manually.'}</small>
        </div>
        <div className="inv-field">
          <label htmlFor="stock-branchId">Branch <span>*</span></label>
          <select {...attributes('branchId')} required disabled={!lookupsReady}>
            <option value="">Select a branch</option>
            {values.branchId && !branches.some((branch) => String(branch.id) === values.branchId) && <option value={values.branchId}>Unavailable branch #{values.branchId}</option>}
            {branches.map((branch) => <option key={branch.id} value={branch.id}>{branch.branchName} (#{branch.id})</option>)}
          </select>
          {fieldError('branchId')}
        </div>
        <div className="inv-field inv-field--full">
          <label htmlFor="stock-itemIds">Inventory Item <span>*</span></label>
          <select id="stock-itemIds" name="itemIds" multiple size={4} value={values.itemIds} disabled={!lookupsReady} required aria-invalid={Boolean(errors.itemIds)} aria-describedby={`stock-items-help${errors.itemIds ? ' stock-itemIds-error' : ''}`}
            onChange={(event) => { const itemIds = Array.from(event.target.selectedOptions, (option) => option.value); setValues((current) => ({ ...current, itemIds })); setErrors((current) => ({ ...current, itemIds: undefined })) }}>
            {unavailableIds.map((id) => <option key={id} value={id}>Unavailable item #{id}</option>)}
            {items.map((item) => <option key={item.id} value={item.id}>{item.name} (#{item.id})</option>)}
          </select>
          {fieldError('itemIds')}
          <small className="inv-field-help" id="stock-items-help">Select one or more items. Hold Ctrl (Windows) or Command (Mac) to select several. All linked items are retained when editing.</small>
        </div>
        <div className="inv-field inv-field--full">
          <label htmlFor="stock-quantity">Quantity <span>*</span></label>
          <input {...attributes('quantity')} type="number" min="0" step="any" required inputMode="decimal" />
          {fieldError('quantity')}
          <small className="inv-field-help">The total quantity for this batch.</small>
        </div>
        <div className="inv-field">
          <label htmlFor="stock-manufactureDate">Manufacture Date <small>Optional</small></label>
          <input {...attributes('manufactureDate')} type="date" min="1000-01-01" max="9999-12-31" />
          {fieldError('manufactureDate')}
        </div>
        <div className="inv-field">
          <label htmlFor="stock-expiryDate">Expiry Date <small>Optional</small></label>
          <input {...attributes('expiryDate')} type="date" min={values.manufactureDate || '1000-01-01'} max="9999-12-31" />
          {fieldError('expiryDate')}
        </div>
      </fieldset>
      <p className="inv-form-hint">Batches expiring today or within the next 30 days are marked Expiring Soon.</p>
      <div className="inv-modal-actions">
        <button type="button" className="inv-button inv-button--secondary" disabled={busy} onClick={onCancel}>Cancel</button>
        <button type="submit" className="inv-button inv-button--primary" disabled={busy || !lookupsReady || missingOptions}>{busy ? 'Saving…' : stock ? 'Save Changes' : 'Create Stock Batch'}</button>
      </div>
    </form>
  )
}
