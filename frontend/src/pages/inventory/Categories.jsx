import { createCategory, deleteCategory, getCategories, updateCategory } from '../../api/inventoryApi.js'
import CategoryForm from '../../components/inventory/CategoryForm.jsx'
import ReferenceDataPage from '../../components/inventory/ReferenceDataPage.jsx'

const columns = [{ key: 'name', label: 'Category Name' }, { key: 'description', label: 'Description' }]

export default function Categories() {
  return <ReferenceDataPage active="categories" eyebrow="ITEM CLASSIFICATION" title="Category Management" description="Organize hotel inventory into clear, consistent categories." entityLabel="Category" pluralLabel="Categories" idKey="categoryId" columns={columns} FormComponent={CategoryForm} listRecords={getCategories} createRecord={createCategory} updateRecord={updateCategory} deleteRecord={deleteCategory} />
}
