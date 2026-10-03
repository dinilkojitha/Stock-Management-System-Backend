# StockMaster Inventory Management - Run

Backend (Windows PowerShell):

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
.\gradlew.bat bootRun
```

Backend: http://localhost:2020
Frontend expected: http://localhost:5173 or http://127.0.0.1:5173

The backend CORS configuration allows both Vite development origins.
