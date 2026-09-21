import { useEffect, useState } from 'react'
import {
  createInventoryItem, deleteInventoryItem, getCategories, getInventoryItems,
  getLowStockItems, getUnitTypes, searchInventoryItems, updateInventoryItem,
} from '../../api/inventoryApi.js'
import ConfirmDeleteModal from '../../components/inventory/ConfirmDeleteModal.jsx'
import InventoryIcon from '../../components/inventory/InventoryIcon.jsx'
import InventoryItemEditor from '../../components/inventory/InventoryItemEditor.jsx'
import InventoryItemTable from '../../components/inventory/InventoryItemTable.jsx'
import InventoryLayout from '../../components/inventory/InventoryLayout.jsx'
import InventorySummaryCards from '../../components/inventory/InventorySummaryCards.jsx'
import { isLowStock } from '../../components/inventory/inventoryUtils.js'

export default function InventoryItems() {
  const [items, setItems] = useState([])
  const [categories, setCategories] = useState([])
  const [unitTypes, setUnitTypes] = useState([])
  const [keyword, setKeyword] = useState('')
  const [filter, setFilter] = useState('all')
  const [loading, setLoading] = useState(true)
  const [lookupLoading, setLookupLoading] = useState(true)
  const [error, setError] = useState('')
  const [lookupError, setLookupError] = useState('')
  const [success, setSuccess] = useState('')
  const [revision, setRevision] = useState(0)
  const [lookupRevision, setLookupRevision] = useState(0)
  const [editor, setEditor] = useState(null)
  const [deleteTarget, setDeleteTarget] = useState(null)

  useEffect(() => {
    const controller = new AbortController()
    Promise.all([getCategories({ signal: controller.signal }), getUnitTypes({ signal: controller.signal })])
      .then(([nextCategories, nextUnits]) => { setCategories(nextCategories); setUnitTypes(nextUnits) })
      .catch((failure) => { if (!controller.signal.aborted) setLookupError(failure.message) })
      .finally(() => { if (!controller.signal.aborted) setLookupLoading(false) })
    return () => controller.abort()
  }, [lookupRevision])

  useEffect(() => {
    const controller = new AbortController()
    const timer = setTimeout(async () => {
      try {
        const options = { signal: controller.signal }
        // Search is server-side; apply the same low-stock predicate to its results.
        const result = keyword.trim()
          ? await searchInventoryItems(keyword.trim(), options)
          : filter === 'low' ? await getLowStockItems(options) : await getInventoryItems(options)
        if (!controller.signal.aborted) {
          setItems(filter === 'low' ? result.filter(isLowStock) : result)
          setError('')
        }
      } catch (failure) {
        if (!controller.signal.aborted) { setError(failure.message); setItems([]) }
      } finally {
        if (!controller.signal.aborted) setLoading(false)
      }
    }, keyword.trim() ? 300 : 0)
    return () => { clearTimeout(timer); controller.abort() }
  }, [keyword, filter, revision])

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

  async function saveItem(id, payload) {
    if (id == null) await createInventoryItem(payload)
    else await updateInventoryItem(id, payload)
    setEditor(null)
    setSuccess(`“${payload.name}” ${id == null ? 'created' : 'updated'} successfully.`)
    refresh()
  }

  async function removeItem(item) {
    await deleteInventoryItem(item.id)
    setDeleteTarget(null)
    setSuccess(`“${item.name}” deleted successfully.`)
    refresh()
  }

  function changeFilter(nextFilter) {
    if (nextFilter !== filter) { setLoading(true); setFilter(nextFilter) }
  }

  function clearFilters() {
    setLoading(true)
    setKeyword('')
    setFilter('all')
  }

  const action = <button className="inv-button inv-button--primary" disabled={lookupLoading || Boolean(lookupError)} onClick={() => { setSuccess(''); setEditor({ id: null }) }}><InventoryIcon name="plus" />Add Inventory Item</button>

  return (
    <InventoryLayout active="items" eyebrow="STOCK CONTROL" title="Inventory Management" description="A clear view of your hotel essentials, stock levels, and inventory value." action={action}>

        {success && <div className="inv-notice inv-notice--success" role="status"><InventoryIcon name="check" /><span>{success}</span><button className="inv-icon-button" aria-label="Dismiss success message" onClick={() => setSuccess('')}><InventoryIcon name="close" /></button></div>}
        {lookupError && <div className="inv-notice inv-notice--error" role="alert"><InventoryIcon name="alert" /><span>Category and unit type details could not be loaded. {lookupError}</span><button className="inv-text-button" onClick={retryLookups}>Retry lookups</button></div>}
        {lookupLoading && <p className="inv-lookup-loading" role="status">Loading categories and unit types…</p>}

        <InventorySummaryCards items={items} loading={loading} unavailable={Boolean(error)} />

        <section className="inv-panel" aria-labelledby="inv-items-title">
          <div className="inv-panel-heading"><div><h2 id="inv-items-title">Inventory Items <span className="inv-count">{loading || error ? '—' : items.length}</span></h2><p>Manage your stock catalogue and keep essentials in check.</p></div><button className="inv-button inv-button--secondary inv-refresh" onClick={refresh} disabled={loading}><InventoryIcon name="refresh" />Refresh</button></div>
          <div className="inv-toolbar">
            <div className="inv-search"><InventoryIcon name="search" /><label className="inv-sr-only" htmlFor="inv-search">Search inventory items by name</label><input id="inv-search" type="search" value={keyword} placeholder="Search inventory items by name…" onChange={(event) => { setLoading(true); setKeyword(event.target.value) }} /></div>
            <div className="inv-filters" role="group" aria-label="Stock status filter"><button aria-pressed={filter === 'all'} className={filter === 'all' ? 'active' : ''} onClick={() => changeFilter('all')}>All Items</button><button aria-pressed={filter === 'low'} className={filter === 'low' ? 'active' : ''} onClick={() => changeFilter('low')}><span className="inv-filter-dot" />Low Stock</button></div>
          </div>
          <div aria-busy={loading}>
            {loading ? <div className="inv-empty" role="status"><span className="inv-spinner" /><h3>Loading inventory</h3><p>Fetching the latest stock information.</p></div> : error ? (
              <div className="inv-empty"><span className="inv-empty-icon inv-empty-icon--error"><InventoryIcon name="alert" /></span><h3>Inventory is unavailable</h3><p role="alert">{error}</p><button className="inv-button inv-button--secondary" onClick={refresh}>Try Again</button></div>
            ) : items.length ? (
              <InventoryItemTable items={items} categories={categories} unitTypes={unitTypes} onEdit={(id) => { setSuccess(''); setEditor({ id }) }} onDelete={(item) => { setSuccess(''); setDeleteTarget(item) }} />
            ) : (
              <div className="inv-empty"><span className="inv-empty-icon"><InventoryIcon name="box" /></span><h3>{keyword.trim() ? 'No matching items' : filter === 'low' ? 'Stock levels look good' : 'Your inventory starts here'}</h3><p>{keyword.trim() ? 'Try a different item name or clear the filters.' : filter === 'low' ? 'No items are currently at or below their reorder level.' : 'Add your first inventory item to start tracking hotel essentials.'}</p>{keyword || filter !== 'all' ? <button className="inv-button inv-button--secondary" onClick={clearFilters}>Clear Filters</button> : <button className="inv-button inv-button--secondary" disabled={lookupLoading || Boolean(lookupError)} onClick={() => setEditor({ id: null })}><InventoryIcon name="plus" />Add Inventory Item</button>}</div>
            )}
          </div>
          <div className="inv-table-footer"><span>{loading ? 'Updating inventory…' : error ? 'Unable to load inventory' : `Showing ${items.length} ${items.length === 1 ? 'item' : 'items'}${filter === 'low' ? ' · low stock' : ''}${keyword.trim() ? ' · search results' : ''}`}</span><span><span className="inv-filter-dot" />Low stock: quantity ≤ reorder level</span></div>
        </section>
      {editor && <InventoryItemEditor itemId={editor.id} categories={categories} unitTypes={unitTypes} onSave={saveItem} onClose={() => setEditor(null)} />}
      {deleteTarget && <ConfirmDeleteModal item={deleteTarget} onConfirm={removeItem} onClose={() => setDeleteTarget(null)} />}
    </InventoryLayout>
  )
}
