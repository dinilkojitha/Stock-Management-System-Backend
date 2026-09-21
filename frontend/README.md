# React + Vite

## StockMaster Inventory Items

Student: Dinsitha W. A. M. — IT25101443

The app entry point renders the Inventory Management workspace. It supports item CRUD,
server-side name search, All/Low Stock filters, inventory valuation, and CRUD screens
for Categories, Unit Types, and Stock Batches. No additional module, router, or dependency was added.

### Run locally

1. Copy `.env.example` to `.env.local`. Set `VITE_API_BASE_URL` to the backend origin
   (default `http://localhost:8080`, without `/api`). Restart Vite after changes.
2. Run the Spring Boot backend with MySQL configured. Categories and unit types
   must already exist for item creation. The backend must allow the frontend origin
   through its existing CORS/security configuration.
3. Run `npm install` if dependencies are missing, then `npm run dev`.
4. Run `npm run build`, `npm run lint`, and `node --test tests/inventory.test.mjs tests/stock.test.mjs`
   for verification.

The application always calls the backend. Connection failures display an error and
retry controls; no sample inventory replaces unavailable data.

### Integration details

- Entry: `src/App.jsx` selects Inventory Items, Categories, Unit Types, or Stock Batches from hash-based
  workspace navigation. Each page uses the shared `InventoryLayout`.
- Reusable UI: `src/components/inventory/`; scoped CSS: `src/styles/inventory.css`.
- Fetch API adapter: `src/api/inventoryApi.js`.
- Request fields: `name`, `categoryId`, `unitTypeId`, `totalQuantity`, `unitPrice`,
  `reorderThreshold`, `description`. Item names are limited to the backend's 60 characters.
- Edit uses `GET /api/inventory-items/{id}` followed by full `PUT` with an absolute
  `totalQuantity`. The separate `updateInventoryItemQuantity(id, quantityDelta)`
  helper supports quantity adjustments; the create/edit form does not call it.
- Low stock means `totalQuantity <= reorderThreshold`. When searching with the Low
  Stock filter, the search endpoint runs first and its results use the same predicate.
- Summary cards reflect the current filtered view. Monetary values show two decimal
  places without assuming a currency not specified by the backend.
- Unit tests stub transport only within the test process; they do not access MySQL.

### Stock batch and expiry management

- Open `#/stock-batches` using Stock Batches in the existing workspace navigation.
- `StockBatches.jsx` uses `StockTable`, `StockEditor`, `StockForm`, the shared layout,
  and the existing delete confirmation. Save/delete success refreshes the current view.
- The verified backend requires a manually assigned integer `stockId`, which is
  required on creation and read-only on edit. Duplicate IDs display the backend's
  conflict message. IDs are never generated or guessed by the frontend.
- Stock JSON: `{ stockId, itemIds: [7], branchId: 1, quantity,
  manufactureDate: "2026-09-01", expiryDate: "2026-10-01" }`.
  The backend supports multiple item links; the form preserves/selects all of them.
- Dropdowns use `GET /api/inventory-items` (`id`, `name`) and the team's existing
  `GET /api/branches` (`id`, `branchName`, `location`). Only branch lookup is used.
- Stock API helpers: `getStocks`, `getStock`, `createStock`, `updateStock`,
  `deleteStock`, `getStocksByItem`, `getStocksByBranch`, `getExpiringStocks`, and
  `getExpiredStocks`, all using the existing configurable `VITE_API_BASE_URL`.
- All, Expiring Soon, and Expired call their corresponding REST endpoints.
  Item/branch selectors use the item/branch endpoints in All mode. Combined
  selections apply the remaining constraints to the returned records.
- `expiryStatus` in `stockUtils.js` compares ISO date-only strings: earlier than
  today = EXPIRED (red), today through today + 30 calendar days inclusive =
  EXPIRING SOON (amber), otherwise = OK (green). Dates remain date-only and are
  never converted into local timestamps. The browser's local date updates at
  midnight and on window focus; the Spring backend uses its system timezone,
  so both should use the hotel's timezone for consistent filter boundaries.
- Dates are optional, matching the backend; unset dates display `Not set` and
  follow the otherwise/OK rule. Malformed returned dates display UNKNOWN.
  Nonnegative finite quantities and manufacture/expiry ordering are validated,
  including the backend's supported date years (1000–9999).
- Batch CRUD does not adjust aggregate inventory quantities, matching the current
  backend service. No transfer workflow or branch mutation is included.

---

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Oxc](https://oxc.rs)
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/)

## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.
