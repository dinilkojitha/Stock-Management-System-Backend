# Fixes applied

- Frontend API base is configured for `http://localhost:2020/api` through `.env.local`.
- Inventory API paths match the completed backend routes.
- Vite remains on `127.0.0.1:5173`; the corrected backend CORS configuration accepts this origin.
- No `node_modules`, `dist`, or Git metadata is included in this ZIP; run `npm install` before `npm run dev`.
