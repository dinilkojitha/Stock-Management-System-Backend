import { useEffect, useState } from 'react'
import { getInventoryItem } from '../../api/inventoryApi.js'
import InventoryModal from './InventoryModal.jsx'
import { formatQuantity } from './inventoryUtils.js'
import { validateQuantityAdjustment } from './quantityUtils.js'

export default function QuantityAdjustmentModal({ itemId, onSave, onClose }) {
  const [item, setItem] = useState(null)
  const [delta, setDelta] = useState('')
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    const controller = new AbortController()
    getInventoryItem(itemId, { signal: controller.signal })
      .then((data) => {
        if (!data || data.id == null) throw new Error('The inventory service returned an invalid item.')
        setItem(data)
      })
      .catch((failure) => { if (!controller.signal.aborted) setError(failure.message) })
      .finally(() => { if (!controller.signal.aborted) setLoading(false) })
    return () => controller.abort()
  }, [itemId])

  async function submit(event) {
    event.preventDefault()
    const validation = validateQuantityAdjustment(delta, item?.totalQuantity)
    setError(validation)
    if (validation) return
    setBusy(true)
    try { await onSave(item, Number(delta)) }
    catch (failure) { setError(failure.message); setBusy(false) }
  }

  const result = Number(item?.totalQuantity ?? 0) + Number(delta)
  return (
    <InventoryModal title="Adjust Quantity" subtitle={item ? `${item.name} · Item #${itemId}` : `Item #${itemId}`} onClose={onClose} busy={busy} compact>
      {loading ? <div className="inv-modal-state" role="status">Loading current quantity…</div> : (
        <form className="inv-form inv-reference-form" onSubmit={submit} noValidate>
          {error && <div className="inv-notice inv-notice--error" role="alert">{error}</div>}
          <fieldset disabled={busy || !item}>
            <div className="inv-form-value"><span>Current quantity</span><strong>{item ? formatQuantity(item.totalQuantity) : 'Unavailable'}</strong></div>
            <div className="inv-field">
              <label htmlFor="inv-quantity-delta">Quantity Adjustment <span>*</span></label>
              <input id="inv-quantity-delta" type="number" step="any" required value={delta} onChange={(event) => { setDelta(event.target.value); setError('') }} aria-describedby="inv-quantity-help" />
              <span id="inv-quantity-help" className="inv-field-help">Enter a positive number to add stock or a negative number to reduce it.</span>
            </div>
            <div className="inv-form-value"><span>Estimated resulting quantity</span><strong>{delta.trim() && Number.isFinite(result) ? formatQuantity(result) : '—'}</strong><small>The backend validates the latest quantity before saving.</small></div>
          </fieldset>
          <div className="inv-modal-actions">
            <button type="button" className="inv-button inv-button--secondary" disabled={busy} onClick={onClose}>Cancel</button>
            <button type="submit" className="inv-button inv-button--primary" disabled={busy || !item}>{busy ? 'Saving…' : 'Save Adjustment'}</button>
          </div>
        </form>
      )}
    </InventoryModal>
  )
}
