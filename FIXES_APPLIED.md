# Fixes applied

- Restored the completed Inventory Management implementation from the inventory feature branch.
- Backend uses Gradle Kotlin DSL and Java 17.
- Backend runs on port 2020.
- MySQL default schema is `se_v2`; username/password can be overridden with `DB_USERNAME` and `DB_PASSWORD`.
- Hibernate schema mutation is disabled with `ddl-auto: none`.
- CORS allows both `http://localhost:5173` and `http://127.0.0.1:5173` for Vite development.
- Inventory routes restored for categories, unit types, inventory items, stock batches, branches lookup, and inventory dashboard.
