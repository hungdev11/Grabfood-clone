"use client";

import React from "react";
import { useState, useEffect } from "react";
import { useParams } from "next/navigation"; 
import { NotificationBell } from "@/components/NotificationBell";
import Sidebar from "@/components/SideBar";
import axios from "axios";

type RestaurantResponse = {
    id: number;
    name: string;
    image: string;
    phone: string;
    openingHour: string;
    closingHour: string;
    description: string;
    address: string;
    rating: number;
    restaurantVouchersInfo: string[];
};

interface AdditionalFood {
    id: number;
    name: string;
    price: number | null;
}
  
interface CartDetail {
    id: number;
    foodName: string;
    quantity: number;
    price: number;
    additionFoods: AdditionalFood[];
    food_img: string;
    note: string;
    restaurantId: number;
    foodId: number | null;
}

interface Order {
    id: number;
    userId: number | null;
    userName: string;
    createdAt: string;
    restaurantId: number | null;
    restaurantName: string;
    totalPrice: number;
    address: string;
    status: string;
    shippingFee: number;
    note: string;
    review: boolean
    payment_method: string | null;
    cartDetails: CartDetail[];
    discountShippingFee: number | null;
    discountOrderPrice: number | null;
}
  
export default function RestaurantPage() {
  const [selectedMenu, setSelectedMenu] = useState("profile");
  const params = useParams();
  const restaurantId = params?.restaurantId as string;  // Lấy restaurantId từ params

  // Kiểm tra xem restaurantId có hợp lệ không, nếu không có sẽ không hiển thị gì
  if (!restaurantId) {
    return <div>Restaurant ID not found</div>;
  }

  return (
    <div className="flex h-screen">
      {/* Sidebar */}
      <Sidebar setSelectedMenu={setSelectedMenu} />

      <div className="flex-1 p-6 bg-gray-50">
        {/* Header with Notification */}
        <div className="flex justify-between items-center py-4 border-b">
          <h1 className="text-3xl font-bold">Quản lý Nhà Hàng {restaurantId}</h1>  {/* Hiển thị restaurantId */}
          <NotificationBell
            channelId={`restaurant/${restaurantId}`}  // Truyền dynamic channelId từ restaurantId
            parseMessage={(msg) => `📦 Đơn hàng mới: ${msg}`}
          />
        </div>

        {/* Content area */}
        <div className="mt-4">
          {selectedMenu === "profile" && <ProfileManagement />} 
          {selectedMenu === "orders" && <OrdersManagement />}
          {selectedMenu === "menu" && <MenuManagement />}
          {/* {selectedMenu === "vouchers" && <VoucherManagement />}  */}
          {selectedMenu === "employees" && <EmployeesManagement />}
        </div>
      </div>
    </div>
  );
}

// Các Component cho danh mục quản lý
function OrdersManagement() {
    const [orders, setOrders] = useState<Order[]>([]);
    const [statusList, setStatusList] = useState<string[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [selectedStatus, setSelectedStatus] = useState<string | null>(null);
    const [expandedOrderId, setExpandedOrderId] = useState<number | null>(null); // Track expanded order ID

    const params = useParams();
    const restaurantId = params?.restaurantId as string;
  
    useEffect(() => {
      if (!restaurantId) return;
  
      const fetchOrders = async () => {
        try {
          const response = await axios.get(
            `http://localhost:6969/grab/order/restaurant/${restaurantId}`
          );
  
          const { orders, statusList } = response.data.data;
          setOrders(orders || []);
          setStatusList(statusList || []);
        } catch (err) {
          setError("Không thể tải thông tin đơn hàng.");
          console.error("Lỗi khi lấy đơn hàng:", err);
        } finally {
          setLoading(false);
        }
      };
  
      fetchOrders();
    }, [restaurantId]);
  
    const filteredOrders = selectedStatus
      ? orders.filter((o) => o.status === selectedStatus)
      : orders;
  
    if (loading) return <p>Đang tải đơn hàng...</p>;
    if (error) return <p>{error}</p>;
  
    const handleToggleDetails = (orderId: number) => {
        // Toggle expanded state for each order
        setExpandedOrderId(expandedOrderId === orderId ? null : orderId);
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
                              <tr key={cartDetail.id}>
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
    </div>
  );
}
  
  

function MenuManagement() {
  return (
    <div>
      <h2 className="text-xl font-semibold mb-4">Quản lý Menu</h2>
      <p>Đây là phần quản lý menu của nhà hàng.</p>
    </div>
  );
}

function EmployeesManagement() {
  return (
    <div>
      <h2 className="text-xl font-semibold mb-4">Quản lý Nhân Viên</h2>
      <p>Đây là phần quản lý nhân viên của nhà hàng.</p>
    </div>
  );
}

function ProfileManagement() {
    const { restaurantId } = useParams();
    const [profile, setProfile] = useState<RestaurantResponse | null>(null);
    const [loading, setLoading] = useState(true);
  
    useEffect(() => {
      if (!restaurantId) return;
  
      const fetchRestaurant = async () => {
        try {
          const res = await axios.get(
            `http://localhost:6969/grab/restaurants/${restaurantId}?userLat=-1&userLon=-1`
          );
          setProfile(res.data.data); // data nằm trong `ApiResponse.data`
        } catch (error) {
          console.error("Lỗi khi lấy thông tin nhà hàng:", error);
        } finally {
          setLoading(false);
        }
      };
  
      fetchRestaurant();
    }, [restaurantId]);
  
    if (loading) return <p>Đang tải thông tin nhà hàng...</p>;
    if (!profile) return <p>Không tìm thấy thông tin nhà hàng.</p>;
  
    return (
      <div>
        <h2 className="text-xl font-semibold mb-4">Thông tin Nhà Hàng</h2>
  
        <div className="flex items-start gap-6">
          <img
            src={profile.image}
            alt={profile.name}
            className="w-40 h-40 object-cover rounded-lg border"
          />
  
          <div className="flex-1 space-y-2">
            <p><strong>Tên:</strong> {profile.name}</p>
            <p><strong>Điện thoại:</strong> {profile.phone}</p>
            <p><strong>Giờ hoạt động:</strong> {profile.openingHour} - {profile.closingHour}</p>
            <p><strong>Địa chỉ:</strong> {profile.address}</p>
            <p><strong>Đánh giá:</strong> {profile.rating} ⭐</p>
            <p><strong>Mô tả:</strong> {profile.description}</p>
            <button className="px-4 py-2 bg-blue-500 text-white rounded">Cập nhật thông tin</button>
          </div>
        </div>
      </div>
    );
  }