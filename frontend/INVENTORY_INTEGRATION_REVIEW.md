# StockMaster Inventory Management — final integration review

Student: Dinsitha W. A. M. — IT25101443

Verified on 22 September 2026. Scope: Inventory Dashboard, Inventory Items,
Categories, Unit Types, Stock Batches, search, quantity adjustments and expiry.

## Results

| Area | Result | Evidence |
| --- | --- | --- |
| Backend | PASS | `mvn clean test` and then `mvn clean package`: both successful, 59 tests each |
| Frontend | PASS | `npm run build`, `npm run lint`, 19 frontend unit tests |
| CRUD | PASS | 72 checks against the real Spring Boot/MySQL service; browser create/read/update flows and confirmation dialogs |
| Database connection | PASS | MySQL 8.0.46, existing `se_V2` database, real REST persistence and foreign-key checks |

Maven used Java 17.0.20.1. `pom.xml` still specifies Java/release 17 and was not
changed. No Gradle files were created. Maven unit/repository tests use H2;
the separate live checks exercised MySQL, not H2 or mocked API responses.

## Problems fixed in this review

- Corrected the frontend default API origin from port 8080 to the actual Spring
  Boot port 2020; configuration remains available through `VITE_API_BASE_URL`.
- Allowed Vite origins on the inventory controllers and the existing branch-list
  GET method. Existing team origins remain valid, unlisted origins are rejected,
  and branch mutation preflight from Vite remains rejected. No security-chain or
  authentication change was made.
- Connected the existing quantity-delta endpoint to an **Adjust Qty** dialog with
  loading, current quantity, result preview, validation, server errors and success
  refresh. A negative result is rejected both in the UI and by the backend.
- Tightened dashboard response validation so null, empty or string metrics cannot
  silently appear as valid zero counts.

Frontend files changed by this review: `.env.example`, `README.md`,
`src/api/inventoryApi.js`, `src/pages/inventory/InventoryItems.jsx`,
`src/components/inventory/InventoryItemTable.jsx`,
`src/components/inventory/dashboardUtils.js`, and `tests/dashboard.test.mjs`.
New files: `QuantityAdjustmentModal.jsx`, `quantityUtils.js` under the inventory
components directory; `tests/quantity.test.mjs`, `tests/live-inventory.mjs`, and
this report. Existing uncommitted dashboard/layout work was preserved.

Backend changes are CORS annotations only in `CategoryController`,
`UnitTypeController`, `InventoryItemController`, `InventoryDashboardController`,
`StockController`, and the read-only `BranchController.getAll` method.

## Coverage

- Real create/read/update/delete for category, unit type, inventory item and stock.
- JSON fields and IDs round-trip correctly; stock IDs remain manually assigned.
- `stock_items` relationships persist; item/branch filters return the created batch.
- Search, low-stock equality, signed quantity adjustment, dashboard counters and
  quantity-times-price valuation match persisted records.
- Invalid names/references, negative quantities/prices/thresholds, missing stock
  ID, duplicate stock ID, invalid date order and deletion of referenced records
  return the expected 400/409 responses.
- Expiry tested at yesterday, today, today + 30 and today + 31. Expiring includes
  today and day 30; expired means strictly before today.
- Browser verified form errors, successful saves and refresh, all four delete
  confirmation/cancel dialogs, search empty state, loading state, offline error
  and retry recovery. Successful DELETE requests were exercised by the live API
  test; irreversible delete buttons were not clicked in the browser.
- Dashboard inspected at desktop, tablet and mobile widths. Mobile inventory
  table scrolls within its container; mobile modal content remains accessible.
  No React console errors or warnings were observed during the successful flows.

Only temporary records created for these checks were deleted; existing business
records were not edited. Test rows were permanently removed (no application undo).
Final dashboard totals match the starting totals: 2 items, quantity 1,095,
inventory value LKR 547,500.00. Auto-increment IDs naturally advance during tests.

## Repeat before demonstration

1. Start MySQL and the Java 17 Maven backend on port 2020, then `npm run dev` on
   port 5173. Verify the configured API origin and currency.
2. Open Dashboard; confirm six populated cards, readable currency and both detail
   sections. Keep browser and backend in the hotel's timezone for expiry checks.
3. Create/edit a demonstration category and unit type. Use them in a new inventory
   item; verify search, valuation and Low Stock at the exact reorder threshold.
4. Use **Adjust Qty** with positive and negative amounts; try an invalid reduction.
5. Create a batch with an unused manual ID and an existing branch. Test expired,
   today, day-30 and later dates; reject expiry before manufacture.
6. Cancel a delete first. Then remove disposable batches, items, categories and
   unit types in that order; confirm success and refreshed lists.
7. Check a narrow screen and the browser Console/Network panels for unexpected
   errors. Confirm recovery after temporarily stopping and restarting the backend.

To repeat automated live checks: `node tests/live-inventory.mjs --run-live`.
This writes and removes temporary records; use a development/test database with
an existing branch. The script is opt-in and performs no branch CRUD.

Stock-batch quantity and aggregate inventory quantity remain separate, matching
the existing backend contract. This review does not add automatic reconciliation,
transfers, procurement, authentication or other team modules.

No commit or push was performed.
