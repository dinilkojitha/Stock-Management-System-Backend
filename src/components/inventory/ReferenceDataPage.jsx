import { useEffect, useState } from 'react'
import ConfirmDeleteModal from './ConfirmDeleteModal.jsx'
import InventoryIcon from './InventoryIcon.jsx'
import InventoryLayout from './InventoryLayout.jsx'
import ReferenceDataEditor from './ReferenceDataEditor.jsx'
import ReferenceDataTable from './ReferenceDataTable.jsx'

export default function ReferenceDataPage({
  active, eyebrow, title, description, entityLabel, pluralLabel, idKey,
  columns, FormComponent, listRecords, createRecord, updateRecord, deleteRecord,
}) {
  const [records, setRecords] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [revision, setRevision] = useState(0)
  const [editor, setEditor] = useState(null)
  const [deleteTarget, setDeleteTarget] = useState(null)

  useEffect(() => {
    const controller = new AbortController()
    listRecords({ signal: controller.signal })
      .then((data) => { setRecords(data); setError('') })
      .catch((failure) => { if (!controller.signal.aborted) { setRecords([]); setError(failure.message) } })
      .finally(() => { if (!controller.signal.aborted) setLoading(false) })
    return () => controller.abort()
  }, [listRecords, revision])

  function refresh() {
    setLoading(true)
    setError('')
    setRevision((value) => value + 1)
  }

  async function save(record, payload) {
    if (record) await updateRecord(record[idKey], payload)
    else await createRecord(payload)
    setEditor(null)
    setSuccess(`“${payload.name}” ${record ? 'updated' : 'created'} successfully.`)
    refresh()
  }

  async function remove(record) {
    await deleteRecord(record[idKey])
    setDeleteTarget(null)
    setSuccess(`“${record.name}” deleted successfully.`)
    refresh()
  }

  const action = <button className="inv-button inv-button--primary" onClick={() => { setSuccess(''); setEditor({ record: null }) }}><InventoryIcon name="plus" />Add {entityLabel}</button>

  return (
    <InventoryLayout active={active} eyebrow={eyebrow} title={title} description={description} action={action}>
      {success && <div className="inv-notice inv-notice--success" role="status"><InventoryIcon name="check" /><span>{success}</span><button className="inv-icon-button" aria-label="Dismiss success message" onClick={() => setSuccess('')}><InventoryIcon name="close" /></button></div>}
      <section className="inv-panel" aria-labelledby={`${active}-title`}>
        <div className="inv-panel-heading">
          <div><h2 id={`${active}-title`}>{pluralLabel} <span className="inv-count">{loading || error ? '—' : records.length}</span></h2><p>Maintain the reference values used by inventory items.</p></div>
          <button className="inv-button inv-button--secondary inv-refresh" onClick={refresh} disabled={loading}><InventoryIcon name="refresh" />Refresh</button>
        </div>
        <div aria-busy={loading}>
          {loading ? <div className="inv-empty" role="status"><span className="inv-spinner" /><h3>Loading {pluralLabel.toLowerCase()}</h3><p>Fetching the latest records from StockMaster.</p></div> : error ? (
            <div className="inv-empty"><span className="inv-empty-icon inv-empty-icon--error"><InventoryIcon name="alert" /></span><h3>{pluralLabel} are unavailable</h3><p role="alert">{error}</p><button className="inv-button inv-button--secondary" onClick={refresh}>Try Again</button></div>
          ) : records.length ? <ReferenceDataTable records={records} idKey={idKey} columns={columns} onEdit={(record) => { setSuccess(''); setEditor({ record }) }} onDelete={(record) => { setSuccess(''); setDeleteTarget(record) }} /> : (
            <div className="inv-empty"><span className="inv-empty-icon"><InventoryIcon name="box" /></span><h3>No {pluralLabel.toLowerCase()} yet</h3><p>Add the first {entityLabel.toLowerCase()} so it can be assigned to inventory items.</p><button className="inv-button inv-button--secondary" onClick={() => setEditor({ record: null })}><InventoryIcon name="plus" />Add {entityLabel}</button></div>
          )}
        </div>
        <div className="inv-table-footer"><span>{loading ? `Updating ${pluralLabel.toLowerCase()}…` : error ? `Unable to load ${pluralLabel.toLowerCase()}` : `Showing ${records.length} ${records.length === 1 ? entityLabel.toLowerCase() : pluralLabel.toLowerCase()}`}</span><span>Changes are available to inventory item forms after refresh.</span></div>
      </section>
      {editor && <ReferenceDataEditor entityLabel={entityLabel} record={editor.record} FormComponent={FormComponent} onSave={save} onClose={() => setEditor(null)} />}
      {deleteTarget && <ConfirmDeleteModal item={deleteTarget} recordId={deleteTarget[idKey]} entityName={entityLabel.toLowerCase()} keepLabel={`Keep ${entityLabel}`} deleteLabel={`Delete ${entityLabel}`} onConfirm={remove} onClose={() => setDeleteTarget(null)} />}
    </InventoryLayout>
  )
}
