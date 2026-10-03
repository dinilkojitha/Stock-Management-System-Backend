import { useEffect, useRef } from 'react'
import InventoryIcon from './InventoryIcon.jsx'

export default function InventoryModal({ title, subtitle, onClose, busy = false, children, compact = false }) {
  const dialog = useRef(null)
  useEffect(() => {
    const element = dialog.current
    const previousFocus = document.activeElement
    element.showModal()
    const overflow = document.body.style.overflow
    document.body.style.overflow = 'hidden'
    return () => {
      element.close()
      document.body.style.overflow = overflow
      previousFocus?.focus()
    }
  }, [])

  return (
    <dialog ref={dialog} className={`inv-modal ${compact ? 'inv-modal--compact' : ''}`} aria-labelledby="inv-modal-title" aria-describedby={subtitle ? 'inv-modal-subtitle' : undefined} onCancel={(event) => { event.preventDefault(); if (!busy) onClose() }}>
      <div className="inv-modal-header">
        <div><h2 id="inv-modal-title">{title}</h2>{subtitle && <p id="inv-modal-subtitle">{subtitle}</p>}</div>
        <button type="button" className="inv-icon-button" aria-label="Close dialog" disabled={busy} onClick={onClose}><InventoryIcon name="close" /></button>
      </div>
      {children}
    </dialog>
  )
}
