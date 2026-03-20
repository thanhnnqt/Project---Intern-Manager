-- Expanded Seed Data for Pagination Testing - V2__Seed_Data.sql
-- All passwords are '123456' hashed with BCrypt: $2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO

-- 1. Seed Accounts (ID 1-40)
INSERT INTO accounts (id, username, password, role) VALUES 
(1, 'admin@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'ADMIN'),
-- Mentors (ID 2-13)
(2, 'mentor1@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'MENTOR'),
(3, 'mentor2@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'MENTOR'),
(4, 'mentor3@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'MENTOR'),
(5, 'mentor4@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'MENTOR'),
(6, 'mentor5@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'MENTOR'),
(7, 'mentor6@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'MENTOR'),
(8, 'mentor7@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'MENTOR'),
(9, 'mentor8@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'MENTOR'),
(10, 'mentor9@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'MENTOR'),
(11, 'mentor10@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'MENTOR'),
(12, 'mentor11@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'MENTOR'),
(13, 'mentor12@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'MENTOR'),
-- Interns (ID 14-40 - adding 27 interns)
(14, 'intern1@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(15, 'intern2@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(16, 'intern3@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(17, 'intern4@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(18, 'intern5@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(19, 'intern6@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(20, 'intern7@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(21, 'intern8@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(22, 'intern9@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(23, 'intern10@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(24, 'intern11@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(25, 'intern12@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(26, 'intern13@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(27, 'intern14@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(28, 'intern15@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(29, 'intern16@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(30, 'intern17@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(31, 'intern18@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(32, 'intern19@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER'),
(33, 'intern20@example.com', '$2a$10$mV/NkqKaoTQsLa60LsGliOK8wpL1ixtspq1AN0rvlyJjwQ8I07JzO', 'USER');

-- 2. Seed Mentors (ID 1-12)
INSERT INTO mentors (id, full_name, email, phone, department, position, avatar, account_id) VALUES 
(1, 'Nguyễn Văn A', 'mentor1@example.com', '0910000001', 'Kỹ thuật', 'Senior Backend Engineer', 'https://ui-avatars.com/api/?name=Nguyen+Van+A&background=random', 2),
(2, 'Lê Thị B', 'mentor2@example.com', '0910000002', 'Kỹ thuật', 'Backend Developer', 'https://ui-avatars.com/api/?name=Le+Thi+B&background=random', 3),
(3, 'Trần Văn C', 'mentor3@example.com', '0910000003', 'Sản phẩm', 'Product Lead', 'https://ui-avatars.com/api/?name=Tran+Van+C&background=random', 4),
(4, 'Phạm Thị D', 'mentor4@example.com', '0910000004', 'Sản phẩm', 'Business Analyst', 'https://ui-avatars.com/api/?name=Pham+Thi+D&background=random', 5),
(5, 'Hoàng Văn E', 'mentor5@example.com', '0910000005', 'Thiết kế', 'UI/UX Design Lead', 'https://ui-avatars.com/api/?name=Hoang+Van+E&background=random', 6),
(6, 'Ngô Thị F', 'mentor6@example.com', '0910000006', 'Thiết kế', 'Graphic Designer', 'https://ui-avatars.com/api/?name=Ngo+Thi+F&background=random', 7),
(7, 'Đỗ Văn G', 'mentor7@example.com', '0910000007', 'Kỹ thuật', 'Frontend Team Lead', 'https://ui-avatars.com/api/?name=Do+Van+G&background=random', 8),
(8, 'Vũ Thị H', 'mentor8@example.com', '0910000008', 'Nhân sự', 'HR Manager', 'https://ui-avatars.com/api/?name=Vu+Thi+H&background=random', 9),
(9, 'Phan Văn I', 'mentor9@example.com', '0910000009', 'Nhân sự', 'Recruiter', 'https://ui-avatars.com/api/?name=Phan+Van+I&background=random', 10),
(10, 'Lý Thị J', 'mentor10@example.com', '0910000010', 'Marketing', 'Content Creator', 'https://ui-avatars.com/api/?name=Ly+Thi+J&background=random', 11),
(11, 'Bùi Văn K', 'mentor11@example.com', '0910000011', 'Marketing', 'Digital Marketing', 'https://ui-avatars.com/api/?name=Bui+Van+K&background=random', 12),
(12, 'Hồ Thị L', 'mentor12@example.com', '0910000012', 'Kỹ thuật', 'DevOps Engineer', 'https://ui-avatars.com/api/?name=Ho+Thi+L&background=random', 13);

-- 3. Seed Interns (ID 1-20)
INSERT INTO interns (id, full_name, email, phone, university, major, status, avatar, account_id, mentor_id) VALUES 
(1, 'Thực tập sinh 1', 'intern1@example.com', '0810000001', 'ĐH Bách Khoa Hà Nội', 'Kỹ thuật phần mềm', 'ACTIVE', 'https://ui-avatars.com/api/?name=TTS+1&background=random', 14, 1),
(2, 'Thực tập sinh 2', 'intern2@example.com', '0810000002', 'ĐH Bách Khoa Hà Nội', 'Kỹ thuật phần mềm', 'ACTIVE', 'https://ui-avatars.com/api/?name=TTS+2&background=random', 15, 1),
(3, 'Thực tập sinh 3', 'intern3@example.com', '0810000003', 'ĐH FPT', 'Khoa học máy tính', 'PENDING', 'https://ui-avatars.com/api/?name=TTS+3&background=random', 16, 2),
(4, 'Thực tập sinh 4', 'intern4@example.com', '0810000004', 'ĐH FPT', 'Khoa học máy tính', 'ACTIVE', 'https://ui-avatars.com/api/?name=TTS+4&background=random', 17, 2),
(5, 'Thực tập sinh 5', 'intern5@example.com', '0810000005', 'ĐH Quốc Gia', 'Hệ thống thông tin', 'COMPLETED', 'https://ui-avatars.com/api/?name=TTS+5&background=random', 18, 1),
(6, 'Thực tập sinh 6', 'intern6@example.com', '0810000006', 'ĐH Quốc Gia', 'Hệ thống thông tin', 'ACTIVE', 'https://ui-avatars.com/api/?name=TTS+6&background=random', 19, 3),
(7, 'Thực tập sinh 7', 'intern7@example.com', '0810000007', 'ĐH Bách Khoa TP.HCM', 'An toàn thông tin', 'ACTIVE', 'https://ui-avatars.com/api/?name=TTS+7&background=random', 20, 3),
(8, 'Thực tập sinh 8', 'intern8@example.com', '0810000008', 'ĐH Bách Khoa TP.HCM', 'An toàn thông tin', 'ACTIVE', 'https://ui-avatars.com/api/?name=TTS+8&background=random', 21, 4),
(9, 'Thực tập sinh 9', 'intern9@example.com', '0810000009', 'ĐH Kinh tế Quốc dân', 'Quản trị kinh doanh', 'PENDING', 'https://ui-avatars.com/api/?name=TTS+9&background=random', 22, 4),
(10, 'Thực tập sinh 10', 'intern10@example.com', '0810000010', 'ĐH Kinh tế Quốc dân', 'Quản trị kinh doanh', 'ACTIVE', 'https://ui-avatars.com/api/?name=TTS+10&background=random', 23, 5),
(11, 'Thực tập sinh 11', 'intern11@example.com', '0810000011', 'ĐH Bách Khoa Hà Nội', 'Kỹ thuật phần mềm', 'ACTIVE', 'https://ui-avatars.com/api/?name=TTS+11&background=random', 24, 7),
(12, 'Thực tập sinh 12', 'intern12@example.com', '0810000012', 'ĐH FPT', 'Truyền thông đa phương tiện', 'ACTIVE', 'https://ui-avatars.com/api/?name=TTS+12&background=random', 25, 10),
(13, 'Thực tập sinh 13', 'intern13@example.com', '0810000013', 'ĐH Bách Khoa Hà Nội', 'Kỹ thuật phần mềm', 'COMPLETED', 'https://ui-avatars.com/api/?name=TTS+13&background=random', 26, 1),
(14, 'Thực tập sinh 14', 'intern14@example.com', '0810000014', 'ĐH Bách Khoa Hà Nội', 'Kỹ thuật phần mềm', 'ACTIVE', 'https://ui-avatars.com/api/?name=TTS+14&background=random', 27, 2),
(15, 'Thực tập sinh 15', 'intern15@example.com', '0810000015', 'ĐH FPT', 'Khoa học máy tính', 'ACTIVE', 'https://ui-avatars.com/api/?name=TTS+15&background=random', 28, 3),
(16, 'Thực tập sinh 16', 'intern16@example.com', '0810000016', 'ĐH FPT', 'Khoa học máy tính', 'ACTIVE', 'https://ui-avatars.com/api/?name=TTS+16&background=random', 29, 3),
(17, 'Thực tập sinh 17', 'intern17@example.com', '0810000017', 'ĐH Quốc Gia', 'Hệ thống thông tin', 'ACTIVE', 'https://ui-avatars.com/api/?name=TTS+17&background=random', 30, 4),
(18, 'Thực tập sinh 18', 'intern18@example.com', '0810000018', 'ĐH Quốc Gia', 'Hệ thống thông tin', 'PENDING', 'https://ui-avatars.com/api/?name=TTS+18&background=random', 31, 4),
(19, 'Thực tập sinh 19', 'intern19@example.com', '0810000019', 'ĐH Kinh tế Quốc dân', 'Quản trị kinh doanh', 'ACTIVE', 'https://ui-avatars.com/api/?name=TTS+19&background=random', 32, 5),
(20, 'Thực tập sinh 20', 'intern20@example.com', '0810000020', 'ĐH Kinh tế Quốc dân', 'Quản trị kinh doanh', 'ACTIVE', 'https://ui-avatars.com/api/?name=TTS+20&background=random', 33, 5);

-- 4. Seed Tasks
INSERT INTO tasks (id, title, description, status, priority, deadline, intern_id, mentor_id) VALUES 
(1, 'Nghiên cứu Spring Boot', 'Cơ bản về Dependency Injection', 'COMPLETED', 'HIGH', DATE_ADD(NOW(), INTERVAL -2 DAY), 1, 1),
(2, 'Thiết kế Database', 'Chuẩn bị Flyway và ERD', 'IN_PROGRESS', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 5 DAY), 1, 1),
(3, 'Xây dựng giao diện List Intern', 'Dùng Vue.js và CSS Vanilla', 'IN_PROGRESS', 'HIGH', DATE_ADD(NOW(), INTERVAL 3 DAY), 2, 1),
(4, 'Tạo API Mentor', 'Hỗ trợ phân trang và tìm kiếm', 'COMPLETED', 'HIGH', DATE_ADD(NOW(), INTERVAL -1 DAY), 3, 2),
(5, 'Viết unit test cho AuthService', 'Sử dụng JUnit 5 và Mockito', 'PENDING', 'LOW', DATE_ADD(NOW(), INTERVAL 7 DAY), 1, 1);

-- 5. Seed Notifications
INSERT INTO notifications (id, title, content, created_at, is_read, account_id) VALUES 
(1, 'Chào mừng mới', 'Chào mừng bạn gia nhập hệ thống Intern Manager!', NOW(), 0, 14),
(2, 'Task mới được giao', 'Bạn có 1 task mới cần hoàn thành.', NOW(), 1, 14);

-- 6. Seed Comments
INSERT INTO comments (id, content, created_at, task_id, account_id) VALUES 
(1, 'Em đã bắt đầu nghiên cứu ạ.', DATE_ADD(NOW(), INTERVAL -3 DAY), 1, 14),
(2, 'Tốt lắm, cần hỗ trợ gì cứ nhắn mentor.', DATE_ADD(NOW(), INTERVAL -2 DAY), 1, 2);