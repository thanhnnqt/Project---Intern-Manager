# Dự án Quản Lý Thực Tập Sinh (Project - Intern Manager)

Chào mừng bạn đến với dự án **Quản lý Thực Tập Sinh**. Đây là hệ thống quản lý thông tin, theo dõi tiến độ và đánh giá thực tập sinh, được đóng gói hoàn toàn bằng Docker để dễ dàng triển khai.

## 🚀 Công nghệ sử dụng

- **Frontend:** Vue 3 (Vite), Pinia, Vue Router, Axios.
- **Backend:** Java Spring Boot, Spring Security (JWT), Hibernate, Flyway.
- **Database:** MySQL 8.0.
- **Triển khai:** Docker & Docker Compose.

## 🛠 Yêu cầu hệ thống

Trước khi bắt đầu, hãy đảm bảo máy bạn đã cài đặt:
- **Docker Desktop** (Đã bao gồm Docker Compose).

## 📥 Hướng dẫn cài đặt và chạy

1. **Tải mã nguồn:** Giải nén tệp tin dự án vào thư mục trên máy bạn.
2. **Khởi chạy hệ thống:** Mở Terminal (PowerShell hoặc Command Prompt) tại thư mục gốc của dự án và chạy lệnh sau:

   ```bash
   docker-compose up -d --build
   ```

3. **Chờ đợi:** Hệ thống sẽ tự động tải các thư viện, build mã nguồn và khởi chạy các container. Quá trình này có thể mất 1-3 phút tùy vào tốc độ mạng.

## 🌍 Truy cập ứng dụng

Sau khi khởi chạy thành công, bạn có thể truy cập dự án qua các địa chỉ sau:

| Thành phần | Địa chỉ (URL) | Cổng (Port) |
| :--- | :--- | :--- |
| **Giao diện người dùng** | [http://localhost](http://localhost) | `80` |
| **API Backend** | [http://localhost:8080](http://localhost:8080) | `8080` |
| **Cơ sở dữ liệu** | `localhost` | `3307` |

> **Lưu ý:** Cổng Database đã được đổi từ `3306` sang `3307` để tránh xung đột với MySQL cài sẵn trên máy Windows.

## 🔑 Thông tin đăng nhập mặc định

- **Email:** `admin@example.com`
- **Mật khẩu:** `123456`

## 📁 Cấu trúc dự án

- `BackEnd/`: Chứa mã nguồn Spring Boot.
- `FrontEnd/`: Chứa mã nguồn Vue.js.
- `docker-compose.yml`: File cấu hình chạy toàn bộ hệ thống.

## 📝 Lưu ý khi đóng gói gửi đi

Nếu bạn đóng gói dự án để gửi cho người khác, hãy đảm bảo **XÓA** các thư mục sau để giảm dung lượng:
- `BackEnd/build/`
- `BackEnd/.gradle/`
- `FrontEnd/node_modules/`
- `.git/` (nếu có)
