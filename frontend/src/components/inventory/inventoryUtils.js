const numberFormat = new Intl.NumberFormat(undefined, { maximumFractionDigits: 4 })
const valueFormat = new Intl.NumberFormat(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })

export const formatQuantity = (value) => numberFormat.format(Number(value ?? 0))
export const formatValue = (value) => valueFormat.format(Number(value ?? 0))
export const inventoryValue = (item) => Number(item.totalQuantity ?? 0) * Number(item.unitPrice ?? 0)
export const isLowStock = (item) => Number(item.totalQuantity ?? 0) <= Number(item.reorderThreshold ?? 0)

export function validateItem(values) {
  const errors = {}
  if (!values.name.trim()) errors.name = 'Enter an item name.'
  else if (values.name.trim().length > 60) errors.name = 'Use 60 characters or fewer.'
  if (!values.categoryId) errors.categoryId = 'Choose a category.'
  if (!values.unitTypeId) errors.unitTypeId = 'Choose a unit type.'
  for (const [field, label] of [
    ['totalQuantity', 'Quantity'], ['unitPrice', 'Unit price'], ['reorderThreshold', 'Reorder threshold'],
  ]) {
    if (String(values[field]).trim() === '' || !Number.isFinite(Number(values[field])) || Number(values[field]) < 0) {
      errors[field] = `${label} must be a number greater than or equal to zero.`
    }
  }
  return errors
}

export function itemPayload(values) {
  return {
    name: values.name.trim(),
    categoryId: Number(values.categoryId),
    unitTypeId: Number(values.unitTypeId),
    totalQuantity: Number(values.totalQuantity),
    unitPrice: Number(values.unitPrice),
    reorderThreshold: Number(values.reorderThreshold),
    description: values.description.trim(),
  }
}
