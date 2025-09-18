DELETE FROM cases;

INSERT INTO cases (case_number, title, description, status, created_date) 
VALUES 
('CASE-001', 'Sample Case 1', 'This is a sample case for testing', 'OPEN', CURRENT_TIMESTAMP),
('CASE-002', 'Sample Case 2', 'Another sample case', 'IN_PROGRESS', CURRENT_TIMESTAMP),
('CASE-003', 'Completed Case', 'A completed case example', 'CLOSED', CURRENT_TIMESTAMP);