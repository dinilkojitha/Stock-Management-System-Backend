import { createUnitType, deleteUnitType, getUnitTypes, updateUnitType } from '../../api/inventoryApi.js'
import ReferenceDataPage from '../../components/inventory/ReferenceDataPage.jsx'
import UnitTypeForm from '../../components/inventory/UnitTypeForm.jsx'

const columns = [{ key: 'name', label: 'Unit Type Name' }]

export default function UnitTypes() {
  return <ReferenceDataPage active="unit-types" eyebrow="MEASUREMENT SETTINGS" title="Unit Type Management" description="Keep quantity measurements consistent across hotel inventory." entityLabel="Unit Type" pluralLabel="Unit Types" idKey="unitTypeId" columns={columns} FormComponent={UnitTypeForm} listRecords={getUnitTypes} createRecord={createUnitType} updateRecord={updateUnitType} deleteRecord={deleteUnitType} />
}
