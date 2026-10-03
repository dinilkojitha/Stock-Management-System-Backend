import { useEffect, useState } from 'react'
import { getInventoryItem } from '../../api/inventoryApi.js'
import InventoryModal from './InventoryModal.jsx'
import InventoryItemForm from './InventoryItemForm.jsx'

export default function InventoryItemEditor({ itemId, categories, unitTypes, onSave, onClose }) {
  const [item, setItem] = useState(null)
  const [loading, setLoading] = useState(itemId != null)
  const [loadError, setLoadError] = useState('')
  const [saveError, setSaveError] = useState('')
  const [busy, setBusy] = useState(false)
  const [retry, setRetry] = useState(0)

  useEffect(() => {
    if (itemId == null) return
    const controller = new AbortController()
    getInventoryItem(itemId, { signal: controller.signal })
      .then((data) => {
        if (!data || data.id == null) throw new Error('The inventory service returned an invalid item.')
        setItem(data)
      })
      .catch((error) => { if (!controller.signal.aborted) setLoadError(error.message) })
      .finally(() => { if (!controller.signal.aborted) setLoading(false) })
    return () => controller.abort()
  }, [itemId, retry])

  async function save(payload) {
    setBusy(true)
    setSaveError('')
    try { await onSave(itemId, payload) }
    catch (error) { setSaveError(error.message); setBusy(false) }
  }

  return (
    <InventoryModal title={itemId == null ? 'Add Inventory Item' : 'View / Edit Inventory Item'} subtitle={itemId == null ? 'Add a new item to your hotel inventory.' : `Item #${itemId} · Review details and update stock information.`} onClose={onClose} busy={busy}>
      {loading ? <div className="inv-modal-state" role="status">Loading item details…</div> : loadError ? (
        <div className="inv-modal-state"><p role="alert">{loadError}</p><button className="inv-button inv-button--secondary" onClick={() => { setLoading(true); setLoadError(''); setRetry((value) => value + 1) }}>Retry</button></div>
      ) : <InventoryItemForm item={item} categories={categories} unitTypes={unitTypes} onSave={save} onCancel={onClose} busy={busy} error={saveError} />}
    </InventoryModal>
  )
}
