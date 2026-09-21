-- DEVELOPMENT ONLY: resets the sample data in database `se`.
-- Run from MySQL Workbench or another MySQL client while the backend is stopped.
-- Sample login password for every seeded user: password

USE se;

SET FOREIGN_KEY_CHECKS = 0;
START TRANSACTION;

TRUNCATE TABLE branchtransferrequeast_has_inventoryitem;
TRUNCATE TABLE branchtransferrequest;
TRUNCATE TABLE stock;
TRUNCATE TABLE `user`;
TRUNCATE TABLE inventoryitem;
TRUNCATE TABLE category;
TRUNCATE TABLE unittype;
TRUNCATE TABLE department;
TRUNCATE TABLE branch;
TRUNCATE TABLE status;
TRUNCATE TABLE role;

INSERT INTO role (roleid, role_name, access_type) VALUES
    (1, 'System Administrator',  100),
    (2, 'Branch Manager',        80),
    (3, 'Warehouse Officer',     60),
    (4, 'Inventory Clerk',       40);

INSERT INTO status (statusid, name) VALUES
    (1, 'Pending'),
    (2, 'Approved'),
    (3, 'Rejected');

INSERT INTO branch (branchid, branch_name, location) VALUES
    (1, 'Colombo Main Branch', 'Colombo 01'),
    (2, 'Kandy Branch',         'Kandy'),
    (3, 'Galle Branch',         'Galle'),
    (4, 'Jaffna Branch',        'Jaffna');

INSERT INTO department (department_id, department_name, branch_branchid, location) VALUES
    (1, 'Central Warehouse',       1, 'Colombo 01'),
    (2, 'Procurement',             1, 'Colombo 02'),
    (3, 'Kandy Medical Stores',    2, 'Kandy'),
    (4, 'Galle Distribution',      3, 'Galle'),
    (5, 'Jaffna Inventory',        4, 'Jaffna');

-- BCrypt hash for the development password: password
INSERT INTO `user` (userid, full_name, role_roleid, email, password, phone_number, department_department_id) VALUES
    (1, 'Nimal Perera',       2, 'nimal.perera@example.lk',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+94771234567', 1),
    (2, 'Sachini Fernando',   3, 'sachini.fernando@example.lk', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+94772345678', 2),
    (3, 'Kasun Jayawardena',  2, 'kasun.jayawardena@example.lk', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+94773456789', 3),
    (4, 'Tharushi Silva',     4, 'tharushi.silva@example.lk', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+94774567890', 4),
    (5, 'Arulanantham Kumar', 3, 'arul.kumar@example.lk',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+94775678901', 5);

INSERT INTO unittype (unitid, unit) VALUES
    (1, 'Box'),
    (2, 'Bottle'),
    (3, 'Packet'),
    (4, 'Piece');

INSERT INTO category (categoryid, category_name, description) VALUES
    (1, 'Essential Medicines', 'Common medicines used by Sri Lankan hospitals and clinics'),
    (2, 'Personal Protective Equipment', 'Masks, gloves and protective clinical equipment'),
    (3, 'Medical Consumables', 'Single-use clinical and laboratory consumables');

INSERT INTO inventoryitem (item_id, item_name, category_categoryid, total_quantity, unit_type_unitid, unit_price, item_description, reorder_threshold) VALUES
    (1, 'Paracetamol 500mg',       1, 5000, 3,  4.50,  'Paracetamol tablets, 500mg', 1000),
    (2, 'Amoxicillin 500mg',       1, 1800, 3, 18.75,  'Amoxicillin capsules, 500mg', 400),
    (3, 'Surgical Face Masks',     2, 9000, 3, 12.00,  'Three-ply disposable masks', 1500),
    (4, 'Nitrile Examination Gloves', 2, 6000, 2, 35.00, 'Disposable nitrile gloves', 1200),
    (5, 'Normal Saline 500ml',     3, 1200, 2, 95.00,  '0.9 percent sodium chloride infusion', 250),
    (6, 'Syringe 5ml',             3, 7500, 4, 22.50,  'Sterile disposable syringe', 1000),
    (7, 'Gauze Swabs 10cm',        3, 3000, 3,  8.00,  'Sterile gauze swabs', 500),
    (8, 'Hand Sanitizer 500ml',    2,  850, 2, 450.00, 'Alcohol-based hand sanitizer', 150);

-- Stock batches are assigned to branches for stock-summary testing.
INSERT INTO stock (stockid, quantity, manufacture_date, expiry_date, branch_branchid) VALUES
    (1001, 2500, '2026-01-15', '2028-01-14', 1),
    (1002, 1000, '2026-02-10', '2028-02-09', 1),
    (1003, 4500, '2026-01-20', '2028-01-19', 2),
    (1004,  800, '2026-02-05', '2027-08-04', 2),
    (1005, 1200, '2026-01-25', '2028-01-24', 3),
    (1006,  650, '2026-02-15', '2027-08-14', 4);

INSERT INTO branchtransferrequest
    (transfer_requeastid, branch_branchid, to_branch_branchid, user_userid, request_time, status_statusid)
VALUES
    (1, 1, 2, 1, '2026-08-20 09:15:00', 1),
    (2, 2, 3, 3, '2026-08-21 10:30:00', 2),
    (3, 3, 4, 4, '2026-08-22 14:00:00', 3);

-- Both inventory-item columns are required by the current legacy join-table schema.
INSERT INTO branchtransferrequeast_has_inventoryitem
    (InventoryItem_itemId, branch_transfer_requeast_orderid, inventory_item_item_id, quantity)
VALUES
    (1, 1, 1, 500),
    (3, 1, 3, 1000),
    (5, 2, 5, 100),
    (6, 2, 6, 300),
    (4, 3, 4, 250);

COMMIT;
SET FOREIGN_KEY_CHECKS = 1;

-- Quick checks for the UI.
SELECT branchid, branch_name, location FROM branch ORDER BY branchid;
SELECT department_id, department_name, branch_branchid FROM department ORDER BY department_id;
SELECT userid, full_name, email, department_department_id FROM `user` ORDER BY userid;
SELECT transfer_requeastid, branch_branchid, to_branch_branchid, status_statusid FROM branchtransferrequest ORDER BY transfer_requeastid;
