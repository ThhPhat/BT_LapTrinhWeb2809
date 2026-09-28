# Spring Boot 4 + Spring Security 7 — Báo Cáo Bài Tập Lập Trình Web

Hệ thống gồm 2 project hoàn chỉnh theo đúng yêu cầu từ 3 tài liệu hướng dẫn (PDF):

| Thư mục | Đề tài / Chức năng | Phương thức đăng nhập | Cổng | Cơ sở dữ liệu |
|---|---|---|---|---|
| **ex2-custom-login-username-email** | **Bài 1:** Custom Login (Gộp Ví dụ 1 & Ví dụ 2)<br>• Đăng nhập bằng `username` hoặc `email`<br>• Hiển thị Avatar tròn, Họ tên, Role badge trên Header<br>• Phân quyền Admin (`/dashboard`) và User (`/`)<br>• Giao diện hiện đại với Thymeleaf Layout Dialect | Username hoặc Email | **8081** | `webst9` |
| **ex3-shop-otp-cloudinary** | **Bài 2:** Shop Quản lý Bán hàng (Đề tổng hợp Ví dụ 3)<br>• Đăng ký xác thực OTP qua Email<br>• Quên mật khẩu & Đổi mật khẩu bằng OTP<br>• CRUD & Tìm kiếm, Phân trang bảng User và Product<br>• Upload & Xóa ảnh sản phẩm trên Cloudinary<br>• Thống kê số lượng User và Product | Username hoặc Email | **8080** | `webst3` |

---

## 🚀 Hướng Dẫn Khởi Chạy

### Yêu cầu môi trường
- **Java**: JDK 22+ (hoặc JDK 21/26)
- **Maven**: 3.9+
- **SQL Server**: Chạy trên cổng mặc định `1433`, tài khoản `sa`. Đã tạo sẵn 2 database: `webst9` và `webst3`.

### 1. Khởi chạy Bài 1: Custom Login (`ex2-custom-login-username-email`)
```bash
cd ex2-custom-login-username-email
mvn spring-boot:run
```
- Truy cập trình duyệt: [http://localhost:8081](http://localhost:8081)
- **Tài khoản mẫu đã tạo sẵn tự động:**
  - **User:** `user01` (hoặc `user01@gmail.com`) / Mật khẩu: `123456`
  - **Admin:** `admin` (hoặc `admin@iotstar.vn`) / Mật khẩu: `123456`

---

### 2. Khởi chạy Bài 2: Shop OTP & Cloudinary (`ex3-shop-otp-cloudinary`)
```bash
cd ex3-shop-otp-cloudinary
mvn spring-boot:run
```
- Truy cập trình duyệt: [http://localhost:8080](http://localhost:8080)
- **Tài khoản Admin mặc định:** `admin` (hoặc `admin@iotstar.vn`) / Mật khẩu: `admin123`
- **Lưu ý kiểm thử OTP:** Mặc định có hỗ trợ chế độ dev `app.otp.log-to-console=true` trong `application.properties` (in trực tiếp mã OTP 6 số ra màn hình Console khi test đăng ký / quên mật khẩu mà không cần tài khoản SMTP).
- Muốn dùng Cloudinary & Gmail thật: Copy file `.env.example` thành `.env` cùng cấp `pom.xml` và điền key của bạn.

---

## 🛠️ Các cải tiến & sửa lỗi so với tài liệu PDF gốc
1. **Lỗi `join fetch` trong `Page<Product>`**: Tài liệu dùng `join fetch` kèm phân trang làm count query bị crash; project đã tách riêng `countQuery`.
2. **Xung đột Bean UTF-8**: Thêm `spring.main.allow-bean-definition-overriding=true` để không bị đụng độ với `characterEncodingFilter` mặc định của Spring Boot.
3. **Quản lý ảnh Cloudinary**: Tách riêng `imageUrl` và `imagePublicId` vào 2 cột database, không gộp chuỗi `|` giúp xóa ảnh cũ chuẩn xác và hiển thị mượt mà.
4. **Bảo mật**: Phân quyền chi tiết, chỉ chủ sở hữu hoặc ADMIN mới có quyền sửa/xóa sản phẩm; chặn Admin tự xóa tài khoản của mình.
5. **Giao diện**: Hoàn thiện toàn bộ CSS, responsive, bảng màu hiện đại, avatar bo góc, badges quyền hạn rõ ràng.
