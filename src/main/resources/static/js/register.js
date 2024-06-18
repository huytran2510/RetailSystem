// $(document).ready(function() {
//     $('.btn-register').click(function(e) {
//         e.preventDefault(); // Ngăn chặn hành động mặc định của sự kiện click
//
//         var form = $(this).closest('.form-signin'); // Tìm form bên trong nút được click
//         var formData = form.serialize(); // Chuyển đổi form thành dữ liệu chuỗi
//
//         // Perform AJAX request to add product to cart
//         $.ajax({
//             type: "POST",
//             url: form.attr('action'),
//             data: formData,
//             success: function(response) {
//                 console.log('Đăng ký thành công.', response);
//                 window.location.href = "/products"
//             },
//             error: function(xhr, status, error) {
//                 console.error('Lỗi khi đăng ký.', error);
//                 // Handle error case if needed
//             }
//         });
//     });
// });