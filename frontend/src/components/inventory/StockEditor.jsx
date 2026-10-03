import { useEffect, useState } from 'react'
import { getStock } from '../../api/inventoryApi.js'
import InventoryModal from './InventoryModal.jsx'
import StockForm from './StockForm.jsx'

export default function StockEditor({ stockId, items, branches, lookupLoading, lookupError, onRetryLookups, onSave, onClose }) {
  const [stock, setStock] = useState(null)
  const [loading, setLoading] = useState(stockId != null)
  const [loadError, setLoadError] = useState('')
  const [saveError, setSaveError] = useState('')
  const [busy, setBusy] = useState(false)
  const [revision, setRevision] = useState(0)

  useEffect(() => {
    if (stockId == null) return
    const controller = new AbortController()
    getStock(stockId, { signal: controller.signal })
      .then((data) => {
        if (!data || data.stockId !== stockId || !Array.isArray(data.itemIds)) throw new Error('The inventory service returned invalid stock details.')
        if (!controller.signal.aborted) setStock(data)
      })
      .catch((failure) => { if (!controller.signal.aborted) setLoadError(failure.message) })
      .finally(() => { if (!controller.signal.aborted) setLoading(false) })
    return () => controller.abort()
  }, [stockId, revision])

  async function save(payload) {
    setBusy(true)
    setSaveError('')
    try { await onSave(stockId, payload) }
    catch (failure) { setSaveError(failure.message); setBusy(false) }
  }

  return (
    <InventoryModal title={stockId == null ? 'Add Stock Batch' : 'View / Edit Stock Batch'} subtitle={stockId == null ? 'Record stock quantities, location, and expiry dates.' : `Stock #${stockId} · Review and update batch details.`} onClose={onClose} busy={busy}>
      {loading ? <div className="inv-modal-state" role="status">Loading stock batch…</div> : loadError ? (
        <div className="inv-modal-state"><p role="alert">{loadError}</p><button className="inv-button inv-button--secondary" onClick={() => { setLoadError(''); setLoading(true); setRevision((value) => value + 1) }}>Retry</button></div>
      ) : <>
        {lookupLoading && <div className="inv-stock-lookup-notice" role="status">Loading inventory items and branches…</div>}
        {lookupError && <div className="inv-notice inv-notice--error inv-stock-lookup-notice" role="alert"><span>{lookupError}</span><button className="inv-text-button" onClick={onRetryLookups}>Retry lookups</button></div>}
        <StockForm stock={stock} items={items} branches={branches} onSave={save} onCancel={onClose} busy={busy} error={saveError} lookupsReady={!lookupLoading && !lookupError} />
      </>}
    </InventoryModal>
  )
}
