import { useEffect } from 'react'
import InventoryIcon from './InventoryIcon.jsx'
import '../../styles/inventory.css'

const navigation = [
  { id: 'items', label: 'Inventory Items', href: '#/inventory-items' },
  { id: 'categories', label: 'Categories', href: '#/categories' },
  { id: 'unit-types', label: 'Unit Types', href: '#/unit-types' },
  { id: 'stock-batches', label: 'Stock Batches', href: '#/stock-batches' },
]

export default function InventoryLayout({ active, eyebrow, title, description, action, children }) {
  useEffect(() => {
    document.title = `${title} | StockMaster`
  }, [title])

  const currentLabel = navigation.find((item) => item.id === active)?.label || 'Inventory'

  return (
    <div className="inventory-app">
      <header className="inv-topbar">
        <a className="inv-brand" href="#/inventory-items" aria-label="StockMaster inventory">
          <span className="inv-brand-mark"><InventoryIcon name="box" /></span>
          <span>Stock<span>Master</span><small>HOTEL INVENTORY</small></span>
        </a>
        <nav className="inv-navigation" aria-label="Inventory management">
          {navigation.map((item) => <a key={item.id} href={item.href} aria-current={active === item.id ? 'page' : undefined}>{item.label}</a>)}
        </nav>
        <div className="inv-topbar-label"><span />Inventory workspace</div>
      </header>
      <main id="inventory-main" className="inv-main">
        <div className="inv-breadcrumb">StockMaster <InventoryIcon name="arrow" /> Inventory <InventoryIcon name="arrow" /><span>{currentLabel}</span></div>
        <div className="inv-page-heading">
          <div><span className="inv-eyebrow">{eyebrow}</span><h1>{title}</h1><p>{description}</p></div>
          {action}
        </div>
        {children}
        <footer className="inv-page-footer"><span>StockMaster <span>·</span> Hotel Inventory Management</span><span>Dinsitha W. A. M. <span>·</span> IT25101443</span></footer>
      </main>
    </div>
  )
}
