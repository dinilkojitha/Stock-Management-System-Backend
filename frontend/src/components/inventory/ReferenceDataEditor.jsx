import { useState } from 'react'
import InventoryModal from './InventoryModal.jsx'

export default function ReferenceDataEditor({ entityLabel, record, FormComponent, onSave, onClose }) {
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  async function save(payload) {
    setBusy(true)
    setError('')
    try {
      await onSave(record, payload)
    } catch (failure) {
      setError(failure.message)
      setBusy(false)
    }
  }

  return (
    <InventoryModal
      title={`${record ? 'Edit' : 'Add'} ${entityLabel}`}
      subtitle={record ? `Update “${record.name}” and save your changes.` : `Create a new ${entityLabel.toLowerCase()} for inventory items.`}
      onClose={onClose}
      busy={busy}
      compact
    >
      <FormComponent record={record} onSave={save} onCancel={onClose} busy={busy} error={error} />
    </InventoryModal>
  )
}
