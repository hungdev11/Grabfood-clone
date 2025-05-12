import React, { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import axios from "axios";
import { Order, CartDetail } from "../types/Types"


export default function OrdersManagement() {
    const [orders, setOrders] = useState<Order[]>([]);
    const [statusList, setStatusList] = useState<string[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [selectedStatus, setSelectedStatus] = useState<string | null>(null);
    const [expandedOrderId, setExpandedOrderId] = useState<number | null>(null); // Track expanded order ID
    const [page, setPage] = useState(0);
    const [size] = useState(5); // mỗi trang 5 đơn
    const [total, setTotal] = useState(0);

    const params = useParams();
    const restaurantId = params?.restaurantId as string;
  
      // Kiểm tra xem restaurantId có hợp lệ không, nếu không có sẽ không hiển thị gì
    if (!restaurantId) {
      return <div>Restaurant ID not found</div>;
    }
    const fetchOrders = async () => {
      try {
        const response = await axios.get(
          `http://localhost:6969/grab/order/restaurant/${restaurantId}?page=${page}&size=${size}${
            selectedStatus ? `&status=${selectedStatus}` : ""
          }`
        );

        const resData = response.data?.data;
        const items = resData?.items;
        const total = resData?.total || 0;

        setOrders(items?.orders || []);
        setStatusList(items?.statusList || []);
        setTotal(total);
      } catch (err) {
        setError("Không thể tải thông tin đơn hàng.");
        console.error("Lỗi khi lấy đơn hàng:", err);
      } finally {
        setLoading(false);
      }
    };


    useEffect(() => {
      setPage(0); // Reset lại page khi status thay đổi
    }, [selectedStatus]);

    useEffect(() => {
      if (!restaurantId) return;
      fetchOrders();
    }, [restaurantId, page, selectedStatus]);
  
    const filteredOrders = selectedStatus
      ? orders.filter((o) => o.status === selectedStatus)
      : orders;
  
    if (loading) return <p>Đang tải đơn hàng...</p>;
    if (error) return <p>{error}</p>;
  
    const handleToggleDetails = (orderId: number) => {
        // Toggle expanded state for each order
        setExpandedOrderId(expandedOrderId === orderId ? null : orderId);
      };
    const changeStatus = async (order: Order, status: "PROCESSING" | "REJECTED") => {
      try {
        const restaurantId = params?.restaurantId as string;
    
        await axios.put(
          `http://localhost:6969/grab/restaurants/${restaurantId}/handle-order/${order.id}`,
          null, // PUT body is empty
          {
            params: { status }, // status passed as query param
          }
        );
    
        alert(`Đã cập nhật trạng thái đơn hàng #${order.id} thành ${status}`);
        fetchOrders(); // Refresh orders after status change
      } catch (error) {
        console.error("Lỗi khi cập nhật trạng thái đơn hàng:", error);
        alert("Cập nhật trạng thái thất bại.");
      }
    };
    return (
      <div>
        <h2 className="text-xl font-semibold mb-4">Quản lý Đơn Hàng</h2>
  
        {/* Nút lọc theo status */}
        <div className="flex flex-wrap gap-2 mb-4">
          <button
            onClick={() => setSelectedStatus(null)}
            className={`px-3 py-1 rounded ${selectedStatus === null ? "bg-blue-600 text-white" : "bg-gray-200"}`}
          >
            Tất cả
          </button>
          {statusList.map((status) => (
            <button
              key={status}
              onClick={() => setSelectedStatus(status)}
              className={`px-3 py-1 rounded ${
                selectedStatus === status ? "bg-blue-600 text-white" : "bg-gray-200"
              }`}
            >
              {status}
            </button>
          ))}
        </div>
  
        <table className="min-w-full table-auto">
          <thead>
            <tr className="bg-gray-100">
              <th className="px-4 py-2">Mã Đơn</th>
              <th className="px-4 py-2">Khách Hàng</th>
              <th className="px-4 py-2">Thời Gian</th>
              <th className="px-4 py-2">Giá trị đơn hàng</th>
              <th className="px-4 py-2">Địa chỉ</th>
              <th className="px-4 py-2">Ghi chú</th>
              <th className="px-4 py-2">Chi tiết</th>
            </tr>
            </thead>
        <tbody>
          {filteredOrders.length === 0 ? (
            <tr>
              <td colSpan={7} className="text-center py-4">
                Không có đơn hàng nào.
              </td>
            </tr>
          ) : (
            filteredOrders.map((order) => (
              <React.Fragment key={order.id}>
                <tr className="border-b text-center">
                  <td className="px-4 py-2">#{order.id}</td>
                  <td className="px-4 py-2">{order.userName || "N/A"}</td>
                  <td className="px-4 py-2">
                    {order.createdAt
                      ? new Intl.DateTimeFormat("vi-VN", {
                          day: "2-digit",
                          month: "2-digit",
                          year: "numeric",
                          hour: "2-digit",
                          minute: "2-digit",
                          hour12: false,
                        }).format(new Date(order.createdAt))
                      : "N/A"}
                  </td>
                  <td className="px-4 py-2">
                    {new Intl.NumberFormat("vi-VN").format(
                      order.totalPrice - (order.discountOrderPrice || 0)
                    )}
                    ₫
                  </td>
                  <td className="px-4 py-2">{order.address || "N/A"}</td>
                  <td className="px-4 py-2">{order.note || "Nothing"}</td>
                  <td className="px-4 py-2">
                    <button
                      onClick={() => handleToggleDetails(order.id)}
                      className="text-blue-500 hover:underline"
                    >
                      Chi tiết
                    </button>
                  </td>
                </tr>

                {/* Render expanded order details below the clicked row */}
                {expandedOrderId === order.id && (
                  <tr>
                    <td colSpan={7} className="border-b bg-gray-100">
                      <div className="p-4">
                        <h3 className="text-xl font-semibold mb-4">Chi tiết Món Ăn</h3>
                        <div className="mb-4 flex gap-2">
                        {order.status === "PENDING" && (
                          <>
                            <button
                              onClick={() => changeStatus(order, "PROCESSING")}
                              className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
                            >
                              Nhận
                            </button>
                            <button
                              onClick={() => changeStatus(order, "REJECTED")}
                              className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
                            >
                              Từ chối
                            </button>
                          </>
                        )}
                      </div>
                        <table className="min-w-full table-auto">
                          <thead>
                            <tr className="bg-gray-100">
                              <th className="px-4 py-2">STT</th>
                              <th className="px-4 py-2">Tên Món</th>
                              <th className="px-4 py-2">Hình Ảnh</th>
                              <th className="px-4 py-2">Giá</th>
                              <th className="px-4 py-2">Số Lượng</th>
                              <th className="px-4 py-2">Món Thêm</th>
                            </tr>
                          </thead>
                          <tbody>
                            {order.cartDetails.map((cartDetail, index) => (
                              <tr className = {"text-center"} key={cartDetail.id}>
                                <td className="px-4 py-2">{index + 1}</td>
                                <td className="px-4 py-2">{cartDetail.foodName}</td>
                                <td className="px-4 py-2">
                                  <img
                                    src={cartDetail.food_img}
                                    alt={cartDetail.foodName}
                                    className="w-16 h-16 object-cover"
                                  />
                                </td>
                                <td className="px-4 py-2">
                                  {new Intl.NumberFormat("vi-VN").format(cartDetail.price)}₫
                                </td>
                                <td className="px-4 py-2">{cartDetail.quantity}</td>
                                <td className="px-4 py-2">
                                  {cartDetail.additionFoods.length > 0
                                    ? cartDetail.additionFoods.map((additionalFood, idx) => (
                                        <p key={idx}>+ {additionalFood.name}</p>
                                      ))
                                    : "Không có"}
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    </td>
                  </tr>
                )}
              </React.Fragment>
            ))
          )}
        </tbody>
      </table>
      <div className="mt-4 flex justify-center gap-2">
        <button
          onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
          disabled={page === 0}
          className="px-3 py-1 rounded bg-gray-300 disabled:opacity-50"
        >
          Trang trước
        </button>
        <span className="px-2 py-1">Trang {page + 1}</span>
        <button
          onClick={() => setPage((prev) => (prev + 1) * size < total ? prev + 1 : prev)}
          disabled={(page + 1) * size >= total}
          className="px-3 py-1 rounded bg-gray-300 disabled:opacity-50"
        >
          Trang sau
        </button>
      </div>
    </div>
    
  );
}

