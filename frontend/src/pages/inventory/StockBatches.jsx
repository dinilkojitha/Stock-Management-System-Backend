import { useEffect, useState } from 'react'
import {
  createStock, deleteStock, getBranches, getExpiredStocks, getExpiringStocks,
  getInventoryItems, getStocks, getStocksByBranch, getStocksByItem, updateStock,
} from '../../api/inventoryApi.js'
import ConfirmDeleteModal from '../../components/inventory/ConfirmDeleteModal.jsx'
import InventoryIcon from '../../components/inventory/InventoryIcon.jsx'
import InventoryLayout from '../../components/inventory/InventoryLayout.jsx'
import StockEditor from '../../components/inventory/StockEditor.jsx'
import StockTable from '../../components/inventory/StockTable.jsx'
import { addCalendarDays, filterStocks, localToday } from '../../components/inventory/stockUtils.js'

export default function StockBatches() {
  const [stocks, setStocks] = useState([])
  const [items, setItems] = useState([])
  const [branches, setBranches] = useState([])
  const [filter, setFilter] = useState('all')
  const [itemId, setItemId] = useState('')
  const [branchId, setBranchId] = useState('')
  const [loading, setLoading] = useState(true)
  const [lookupLoading, setLookupLoading] = useState(true)
  const [error, setError] = useState('')
  const [lookupError, setLookupError] = useState('')
  const [success, setSuccess] = useState('')
  const [revision, setRevision] = useState(0)
  const [lookupRevision, setLookupRevision] = useState(0)
  const [today, setToday] = useState(localToday)
  const [editor, setEditor] = useState(null)
  const [deleteTarget, setDeleteTarget] = useState(null)

  // Reclassify date-only values at local midnight and when returning to this tab.
  useEffect(() => {
    let timer
    function updateDay() {
      setToday(localToday())
      const next = new Date()
      next.setHours(24, 0, 0, 100)
      clearTimeout(timer)
      timer = setTimeout(updateDay, next.getTime() - Date.now())
    }
    const initial = setTimeout(updateDay, 0)
    window.addEventListener('focus', updateDay)
    return () => { clearTimeout(initial); clearTimeout(timer); window.removeEventListener('focus', updateDay) }
  }, [])

  useEffect(() => {
    const controller = new AbortController()
    Promise.all([getInventoryItems({ signal: controller.signal }), getBranches({ signal: controller.signal })])
      .then(([nextItems, nextBranches]) => {
        if (!controller.signal.aborted) { setItems(nextItems); setBranches(nextBranches); setLookupError('') }
      })
      .catch((failure) => { if (!controller.signal.aborted) setLookupError(`Item and branch selections could not be loaded. ${failure.message}`) })
      .finally(() => { if (!controller.signal.aborted) setLookupLoading(false) })
    return () => controller.abort()
  }, [lookupRevision])

  useEffect(() => {
    const controller = new AbortController()
    const options = { signal: controller.signal }
    // The most specific REST endpoint supplies records; remaining selectors combine locally.
    const fetchStocks = filter === 'expiring' ? getExpiringStocks(30, options)
      : filter === 'expired' ? getExpiredStocks(options)
        : itemId ? getStocksByItem(itemId, options)
          : branchId ? getStocksByBranch(branchId, options) : getStocks(options)
    fetchStocks
      .then((data) => { if (!controller.signal.aborted) { setStocks(data); setError('') } })
      .catch((failure) => { if (!controller.signal.aborted) { setStocks([]); setError(failure.message) } })
      .finally(() => { if (!controller.signal.aborted) setLoading(false) })
    return () => controller.abort()
  }, [filter, itemId, branchId, revision, today])

  function refresh() {
    setLoading(true)
    setError('')
    setRevision((value) => value + 1)
  }

  function retryLookups() {
    setLookupLoading(true)
    setLookupError('')
    setLookupRevision((value) => value + 1)
  }

  function changeFilter(value) {
    if (value !== filter) { setLoading(true); setFilter(value) }
  }

  function clearFilters() {
    setLoading(true)
    setFilter('all')
    setItemId('')
    setBranchId('')
  }

  async function saveStock(id, payload) {
    if (id == null) await createStock(payload)
    else await updateStock(id, payload)
    setEditor(null)
    setSuccess(`Stock #${payload.stockId} ${id == null ? 'created' : 'updated'} successfully.`)
    refresh()
  }

  async function removeStock(stock) {
    await deleteStock(stock.stockId)
    setDeleteTarget(null)
    setSuccess(`Stock #${stock.stockId} deleted successfully.`)
    refresh()
  }

  const visibleStocks = filterStocks(stocks, { itemId, branchId, filter, today })
  const filtered = filter !== 'all' || itemId || branchId
  const add = () => { setSuccess(''); setEditor({ id: null }) }
  const action = <button className="inv-button inv-button--primary" onClick={add}><InventoryIcon name="plus" />Add Stock Batch</button>

  return (
    <InventoryLayout active="stock-batches" eyebrow="BATCH & EXPIRY CONTROL" title="Stock Batch & Expiry Management" description="Track hotel stock by item, branch, and expiry date." action={action}>
      {success && <div className="inv-notice inv-notice--success" role="status"><InventoryIcon name="check" /><span>{success}</span><button className="inv-icon-button" aria-label="Dismiss success message" onClick={() => setSuccess('')}><InventoryIcon name="close" /></button></div>}
      {lookupLoading && <p className="inv-lookup-loading" role="status">Loading inventory items and branches…</p>}
      {lookupError && <div className="inv-notice inv-notice--error" role="alert"><InventoryIcon name="alert" /><span>{lookupError}</span><button className="inv-text-button" onClick={retryLookups}>Retry lookups</button></div>}
      <section className="inv-panel" aria-labelledby="inv-stocks-title">
        <div className="inv-panel-heading">
          <div><h2 id="inv-stocks-title">Stock Batches <span className="inv-count">{loading || error ? '—' : visibleStocks.length}</span></h2><p>Expiring soon: {today} through {addCalendarDays(today, 30)}, inclusive.</p></div>
          <button className="inv-button inv-button--secondary inv-refresh" disabled={loading} onClick={refresh}><InventoryIcon name="refresh" />Refresh</button>
        </div>
        <div className="inv-toolbar inv-stock-toolbar">
          <div className="inv-stock-selectors">
            <div className="inv-field"><label htmlFor="stock-filter-item">Inventory Item</label><select id="stock-filter-item" value={itemId} disabled={lookupLoading || Boolean(lookupError)} onChange={(event) => { setLoading(true); setItemId(event.target.value) }}><option value="">All items</option>{items.map((item) => <option key={item.id} value={item.id}>{item.name} (#{item.id})</option>)}</select></div>
            <div className="inv-field"><label htmlFor="stock-filter-branch">Branch</label><select id="stock-filter-branch" value={branchId} disabled={lookupLoading || Boolean(lookupError)} onChange={(event) => { setLoading(true); setBranchId(event.target.value) }}><option value="">All branches</option>{branches.map((branch) => <option key={branch.id} value={branch.id}>{branch.branchName} (#{branch.id})</option>)}</select></div>
          </div>
          <div className="inv-filters" role="group" aria-label="Expiry filter">{[['all', 'All'], ['expiring', 'Expiring Soon'], ['expired', 'Expired']].map(([value, label]) => <button key={value} aria-pressed={filter === value} className={filter === value ? 'active' : ''} onClick={() => changeFilter(value)}>{label}</button>)}</div>
          {filtered && <button className="inv-text-button" onClick={clearFilters}>Clear Filters</button>}
        </div>
        <div aria-busy={loading}>
          {loading ? <div className="inv-empty" role="status"><span className="inv-spinner" /><h3>Loading stock batches</h3><p>Fetching the latest quantities and expiry dates.</p></div> : error ? (
            <div className="inv-empty"><span className="inv-empty-icon inv-empty-icon--error"><InventoryIcon name="alert" /></span><h3>Stock batches are unavailable</h3><p role="alert">{error}</p><button className="inv-button inv-button--secondary" onClick={refresh}>Try Again</button></div>
          ) : visibleStocks.length ? <StockTable stocks={visibleStocks} items={items} branches={branches} today={today} onEdit={(id) => { setSuccess(''); setEditor({ id }) }} onDelete={(stock) => { setSuccess(''); setDeleteTarget(stock) }} /> : (
            <div className="inv-empty"><span className="inv-empty-icon"><InventoryIcon name="box" /></span><h3>{filtered ? 'No matching stock batches' : 'No stock batches yet'}</h3><p>{filtered ? 'No batches match the selected item, branch, and expiry filters.' : 'Add a stock batch to track its quantity and expiry date.'}</p><button className="inv-button inv-button--secondary" onClick={filtered ? clearFilters : add}>{filtered ? 'Clear Filters' : 'Add Stock Batch'}</button></div>
          )}
        </div>
        <div className="inv-table-footer"><span>{loading ? 'Updating stock batches…' : error ? 'Unable to load stock batches' : `Showing ${visibleStocks.length} stock ${visibleStocks.length === 1 ? 'batch' : 'batches'}`}</span><span>Expiry status uses calendar dates · Today: {today}</span></div>
      </section>
      {editor && <StockEditor stockId={editor.id} items={items} branches={branches} lookupLoading={lookupLoading} lookupError={lookupError} onRetryLookups={retryLookups} onSave={saveStock} onClose={() => setEditor(null)} />}
      {deleteTarget && <ConfirmDeleteModal item={{ ...deleteTarget, name: 'stock batch' }} recordId={deleteTarget.stockId} entityName="stock batch" keepLabel="Keep Batch" deleteLabel="Delete Batch" onConfirm={removeStock} onClose={() => setDeleteTarget(null)} />}
    </InventoryLayout>
  )
}
