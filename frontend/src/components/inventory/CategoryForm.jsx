import { useState } from 'react'

export default function CategoryForm({ record, onSave, onCancel, busy, error }) {
  const [values, setValues] = useState({ name: record?.name ?? '', description: record?.description ?? '' })
  const [errors, setErrors] = useState({})

  function change(event) {
    const { name, value } = event.target
    setValues((current) => ({ ...current, [name]: value }))
    setErrors((current) => ({ ...current, [name]: undefined }))
  }

  function submit(event) {
    event.preventDefault()
    const name = values.name.trim()
    if (!name) {
      setErrors({ name: 'Enter a category name.' })
      event.currentTarget.elements.name.focus()
      return
    }
    if (name.length > 45) {
      setErrors({ name: 'Use 45 characters or fewer.' })
      event.currentTarget.elements.name.focus()
      return
    }
    onSave({ name, description: values.description.trim() })
  }

  return (
    <form className="inv-form inv-reference-form" onSubmit={submit} noValidate>
      {error && <div className="inv-notice inv-notice--error" role="alert">{error}</div>}
      <fieldset disabled={busy}>
        <div className="inv-field inv-field--full">
          <label htmlFor="category-name">Category Name <span>*</span></label>
          <input id="category-name" name="name" value={values.name} onChange={change} maxLength={45} required autoFocus placeholder="e.g. Food & Beverage" aria-invalid={Boolean(errors.name)} aria-describedby={errors.name ? 'category-name-error' : undefined} />
          {errors.name && <span className="inv-field-error" id="category-name-error">{errors.name}</span>}
        </div>
        <div className="inv-field inv-field--full">
          <label htmlFor="category-description">Description <small>Optional</small></label>
          <textarea id="category-description" name="description" value={values.description} onChange={change} rows={4} placeholder="Describe the items grouped in this category…" />
        </div>
      </fieldset>
      <div className="inv-modal-actions">
        <button type="button" className="inv-button inv-button--secondary" disabled={busy} onClick={onCancel}>Cancel</button>
        <button type="submit" className="inv-button inv-button--primary" disabled={busy}>{busy ? 'Saving…' : record ? 'Save Changes' : 'Create Category'}</button>
      </div>
    </form>
  )
}
