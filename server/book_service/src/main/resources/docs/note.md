- URL mở H2 là localhost:9001/h2-console
- Định nghĩa Projection trong CQRS, DDD layer:

* Là cách để xây dựng các view, model, các cấu trúc dữ liệu READ để tối ưu hoá các sự kiện event
* Đổi với Command: Khi 1 event xảy ra, ví dụ như 1 đơn hàng được tạo thì projection sẽ lắng nghe event đó rồi cập nhật trạng thái
* Đối với Query: Khi 1 event xảy ra, projection sẽ lắng nghe Query rồi truy vấn vào Database để return data

- Event Sourcing là 1 cơ chế lưu trữ dữ liệu trong kiến trúc phần mềm, trong đó dữ liệu được lưu trữ dưới dạng chuỗi các sự kiện Events.
  Thay vì lưu trữ trạng thái của đối tượng, Event Sourcing lưu trữ các sự kiện đã xảy ra và sử dụng chúng để xây dựng trạng thái hiện tại

- Axon Framework là 1 thư viện mã nguồn mỡ được sử dụng để phát triển các ứng dụng sử dụng Event Sourcing và CQRS (Command Query Responsibility Segregation).
  Nó cung cấp các công cụ và cấu trúc để xây dựng hệ thống dựa trên sự kiện và phân tách trách nhiệm giữa các lệnh (commands) và truy vấn (queries)

- Trong Axon Framework, Event là 1 sự kiện quan trọng xảy ra trong hệ thống. Nó biểu diễn 1 hành động đã xảy ra trong quá khứ và được lưu trữ trong Event Store.
  Event Store là nơi lưu trữ tất cả sự kiện đã xảy ra trong hệ thống để có thể tái tạo trạng thái hiện tại của đối phương.

- Command là 1 yêu cầu từ người dùng hoặc hệ thống để thực hiện 1 hành động.
  Nó biểu thị ý định của người dùng hoặc hệ thống và được gửi đến các xử lý viên (command handler) để thực thi các logic tương ứng.

- CQRS (command query responsibility segregation) là 1 kiến trúc phân tách trách nhiệm giữa việc xử lý các lệnh (commands) và truy vấn (queries) trong hệ thống.
  Thay vì sử dụng 1 mô hình truy vấn - chỉnh sửa (CRUD) thì CQRS chia thành 2 mô hình riêng biệt để xử lý các yêu cầu chỉnh sửa và truy vấn, tối ưu hoá và khả năng scale up hệ thống.
