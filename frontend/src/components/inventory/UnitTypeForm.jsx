import { useState } from 'react'

export default function UnitTypeForm({ record, onSave, onCancel, busy, error }) {
  const [name, setName] = useState(record?.name ?? '')
  const [validationError, setValidationError] = useState('')

  function submit(event) {
    event.preventDefault()
    const trimmedName = name.trim()
    if (!trimmedName) {
      setValidationError('Enter a unit type name.')
      event.currentTarget.elements.name.focus()
      return
    }
    if (trimmedName.length > 45) {
      setValidationError('Use 45 characters or fewer.')
      event.currentTarget.elements.name.focus()
      return
    }
    onSave({ name: trimmedName })
  }

  return (
    <form className="inv-form inv-reference-form" onSubmit={submit} noValidate>
      {error && <div className="inv-notice inv-notice--error" role="alert">{error}</div>}
      <fieldset disabled={busy}>
        <div className="inv-field inv-field--full">
          <label htmlFor="unit-type-name">Unit Type Name <span>*</span></label>
          <input id="unit-type-name" name="name" value={name} onChange={(event) => { setName(event.target.value); setValidationError('') }} maxLength={45} required autoFocus placeholder="e.g. Kilogram" aria-invalid={Boolean(validationError)} aria-describedby={validationError ? 'unit-type-name-error' : undefined} />
          {validationError && <span className="inv-field-error" id="unit-type-name-error">{validationError}</span>}
          <small className="inv-field-help">Use a clear unit that appears beside item quantities.</small>
        </div>
      </fieldset>
      <div className="inv-modal-actions">
        <button type="button" className="inv-button inv-button--secondary" disabled={busy} onClick={onCancel}>Cancel</button>
        <button type="submit" className="inv-button inv-button--primary" disabled={busy}>{busy ? 'Saving…' : record ? 'Save Changes' : 'Create Unit Type'}</button>
      </div>
    </form>
  )
}
