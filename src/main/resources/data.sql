-- Seed data for local/dev use only (spring.sql.init.mode=always in application-dev.yml).
-- Passwords below are BCrypt hashes of the plaintext shown in the comment next to each row.

-- Admin user: email admin@finshield.com / password: Admin@123
INSERT INTO user (id, name, email, password_hash, role, created_at)
SELECT 1, 'System Admin', 'admin@finshield.com',
       '$2a$10$7EqJtq98hPqEX7fNZaFWoOe6z6mDvC7c7GgLZ8x6XG5c3.j0y5F3G', 'ADMIN', NOW()
WHERE NOT EXISTS (SELECT 1 FROM user WHERE email = 'admin@finshield.com');

-- Customer user: email customer@finshield.com / password: Customer@123
INSERT INTO user (id, name, email, password_hash, role, created_at)
SELECT 2, 'Test Customer', 'customer@finshield.com',
       '$2a$10$3z1p8QhY0f5G9mQeqjK.T.nQyzB1sVh9jv8Y5B2sVJb0kK1zQpF9O', 'CUSTOMER', NOW()
WHERE NOT EXISTS (SELECT 1 FROM user WHERE email = 'customer@finshield.com');

-- A couple of default fraud rules (matches FraudRuleEvaluator ruleName() values)
INSERT INTO fraud_rule (id, rule_type, config, active)
SELECT 1, 'VELOCITY', '{"maxCount":5,"windowMinutes":10}', true
WHERE NOT EXISTS (SELECT 1 FROM fraud_rule WHERE rule_type = 'VELOCITY');

INSERT INTO fraud_rule (id, rule_type, config, active)
SELECT 2, 'AMOUNT', '{"threshold":50000}', true
WHERE NOT EXISTS (SELECT 1 FROM fraud_rule WHERE rule_type = 'AMOUNT');

-- Sample blacklist entries
INSERT INTO blacklist_entry (id, identifier, reason, added_by)
SELECT 1, 'ACC9999', 'Reported mule account', 'admin@finshield.com'
WHERE NOT EXISTS (SELECT 1 FROM blacklist_entry WHERE identifier = 'ACC9999');

INSERT INTO blacklist_entry (id, identifier, reason, added_by)
SELECT 2, 'UPI-scammer@fraudbank', 'Flagged by NPCI advisory', 'admin@finshield.com'
WHERE NOT EXISTS (SELECT 1 FROM blacklist_entry WHERE identifier = 'UPI-scammer@fraudbank');
