import { useEffect, useState } from 'react'
import {
  getExpiringStocks, getInventoryDashboard, getInventoryItems, getLowStockItems,
} from '../../api/inventoryApi.js'
import DashboardSummaryCards from '../../components/inventory/DashboardSummaryCards.jsx'
import ExpiringSoonOverview from '../../components/inventory/ExpiringSoonOverview.jsx'
import InventoryIcon from '../../components/inventory/InventoryIcon.jsx'
import InventoryLayout from '../../components/inventory/InventoryLayout.jsx'
import LowStockOverview from '../../components/inventory/LowStockOverview.jsx'
import { dashboardWindow, validateDashboardSummary } from '../../components/inventory/dashboardUtils.js'
import { localToday } from '../../components/inventory/stockUtils.js'

export default function InventoryDashboard() {
  const [summary, setSummary] = useState(null)
  const [lowStock, setLowStock] = useState([])
  const [expiring, setExpiring] = useState([])
  const [inventoryItems, setInventoryItems] = useState([])
  const [today, setToday] = useState(localToday)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [revision, setRevision] = useState(0)

  useEffect(() => {
    const controller = new AbortController()
    const options = { signal: controller.signal }
    Promise.all([
      getInventoryDashboard(options),
      getLowStockItems(options),
      getExpiringStocks(30, options),
      getInventoryItems(options),
    ]).then(([nextSummary, nextLowStock, nextExpiring, nextItems]) => {
      if (!validateDashboardSummary(nextSummary)) throw new Error('The inventory service returned an invalid dashboard summary.')
      if (!controller.signal.aborted) {
        setSummary(nextSummary)
        setLowStock(nextLowStock)
        setExpiring(nextExpiring)
        setInventoryItems(nextItems)
        setToday(localToday())
        setError('')
      }
    }).catch((failure) => {
      if (!controller.signal.aborted) setError(failure.message)
    }).finally(() => {
      if (!controller.signal.aborted) setLoading(false)
    })
    return () => controller.abort()
  }, [revision])

  function refresh() {
    setLoading(true)
    setError('')
    setRevision((value) => value + 1)
  }

  const action = <button className="inv-button inv-button--secondary" onClick={refresh} disabled={loading}><InventoryIcon name="refresh" />Refresh Dashboard</button>

  return (
    <InventoryLayout active="dashboard" eyebrow="INVENTORY OVERVIEW" title="Inventory Dashboard" description="Monitor hotel stock levels, valuation, and upcoming expiry risks." action={action}>
      <div className="inv-dashboard-window"><InventoryIcon name="calendar" /><span>Expiry watch window</span><strong>{dashboardWindow(today)}</strong></div>
      <DashboardSummaryCards summary={summary} loading={loading || Boolean(error)} />
      {loading ? <div className="inv-panel inv-empty" role="status"><span className="inv-spinner" /><h3>Loading inventory dashboard</h3><p>Collecting the latest item and stock health information.</p></div> : error ? (
        <div className="inv-panel inv-empty"><span className="inv-empty-icon inv-empty-icon--error"><InventoryIcon name="alert" /></span><h3>Dashboard is unavailable</h3><p role="alert">{error}</p><button className="inv-button inv-button--secondary" onClick={refresh}>Try Again</button></div>
      ) : <div className="inv-dashboard-sections"><LowStockOverview items={lowStock} /><ExpiringSoonOverview stocks={expiring} inventoryItems={inventoryItems} today={today} /></div>}
    </InventoryLayout>
  )
}
