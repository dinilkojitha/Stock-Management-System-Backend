import { useEffect, useState } from 'react'
import Categories from './pages/inventory/Categories.jsx'
import InventoryItems from './pages/inventory/InventoryItems.jsx'
import UnitTypes from './pages/inventory/UnitTypes.jsx'
import StockBatches from './pages/inventory/StockBatches.jsx'
import InventoryDashboard from './pages/inventory/InventoryDashboard.jsx'

const pages = {
  '#/dashboard': InventoryDashboard,
  '#/categories': Categories,
  '#/unit-types': UnitTypes,
  '#/inventory-items': InventoryItems,
  '#/stock-batches': StockBatches,
}

export default function App() {
  const [route, setRoute] = useState(window.location.hash)

  useEffect(() => {
    const handleRouteChange = () => setRoute(window.location.hash)
    window.addEventListener('hashchange', handleRouteChange)
    return () => window.removeEventListener('hashchange', handleRouteChange)
  }, [])

  const Page = pages[route] || InventoryDashboard
  return <Page />
}
