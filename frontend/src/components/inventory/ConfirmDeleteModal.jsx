import { useState } from 'react'
import InventoryModal from './InventoryModal.jsx'
import InventoryIcon from './InventoryIcon.jsx'

export default function ConfirmDeleteModal({ item, onConfirm, onClose }) {
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  async function confirm() {
    setBusy(true)
    setError('')
    try { await onConfirm(item) }
    catch (failure) { setError(failure.message); setBusy(false) }
  }

  return (
    <InventoryModal title="Delete inventory item?" onClose={onClose} busy={busy} compact>
      <div className="inv-delete-content">
        <div className="inv-delete-icon"><InventoryIcon name="trash" /></div>
        <p>You are about to delete <strong>{item.name}</strong> (#{item.id}). This action cannot be undone.</p>
        {error && <div className="inv-notice inv-notice--error" role="alert">{error}</div>}
      </div>
      <div className="inv-modal-actions">
        <button className="inv-button inv-button--secondary" disabled={busy} onClick={onClose}>Keep Item</button>
        <button className="inv-button inv-button--danger" disabled={busy} onClick={confirm}>{busy ? 'Deleting…' : 'Delete Item'}</button>
      </div>
    </InventoryModal>
  )
}
