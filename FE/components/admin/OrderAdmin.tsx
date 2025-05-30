import React, { useEffect, useState } from "react";
import axios from "axios";
import { toast } from "sonner";
import { logout } from "@/utils/authService";
import { LogOut } from "lucide-react";

interface AdditionFood {
  id: number;
  name: string;
  price: number | null;
}

interface CartDetail {
  id: number;
  foodId: number | null;
  foodName: string;
  quantity: number;
  additionFoods: AdditionFood[];
  price: number;
  note: string;
  food_img: string;
}

interface OrderItem {
  id: number;
  userName: string;
  restaurantName: string;
  totalPrice: number;
  address: string;
  status: string;
  shippingFee: number;
  discountShippingFee: number;
  discountOrderPrice: number;
  note: string;
  cartDetails: CartDetail[];
  createdAt: string;
  review: boolean;
}

interface PageResponse {
  page: number;
  size: number;
  total: number;
  items: OrderItem[];
}

export default function OrderList() {
  const [orders, setOrders] = useState<OrderItem[]>([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [total, setTotal] = useState(0);

  const fetchOrders = async () => {
    try {
      const response = await axios.get<{ data: PageResponse }>(
        `http://localhost:6969/grab/order/admin?page=${page}&size=${size}`
      );
      setOrders(response.data.data.items);
      setTotal(response.data.data.total);
    } catch (error) {
      console.error("Error fetching orders:", error);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [page]);

  const handleCancelOrder = async (orderId: number) => {
    try {
      await axios.put(
        `http://localhost:6969/grab/order/admin/cancel/${orderId}`
      );
      alert("Đơn hàng đã được hủy thành công");
      fetchOrders(); // Refresh the order list after cancellation
    } catch (error) {
      console.error("Error cancelling order:", error);
    }
  };
  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-xl font-bold">Danh sách đơn hàng</h1>
        <button
          onClick={logout}
          className="flex items-center gap-2 bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition shadow-sm"
        >
          <LogOut size={18} />
          Đăng xuất
        </button>
      </div>
      <div className="space-y-6">
        {orders.map((order) => (
          <div
            key={order.id}
            className="border rounded-lg p-4 shadow-sm bg-white"
          >
            <div className="flex justify-between items-center mb-2">
              <div>
                <p className="font-semibold">#{order.id}</p>
                <p className="text-gray-500 text-sm">
                  {new Date(order.createdAt).toLocaleString()}
                </p>
              </div>
              <div className="flex items-center gap-2">
                <span className="text-sm px-2 py-1 bg-yellow-100 text-yellow-800 rounded">
                  {order.status}
                </span>
                {(order.status === "PENDING" ||
                  order.status === "PROCESSING" ||
                  order.status === "SHIPPING") && (
                  <button
                    className="bg-red-500 text-white px-3 py-1.5 rounded-lg hover:bg-red-600 transition shadow-sm"
                    onClick={() => handleCancelOrder(order.id)}
                  >
                    Hủy
                  </button>
                )}
              </div>
            </div>
            <p className="text-sm">Khách hàng: {order.userName}</p>
            <p className="text-sm">Nhà hàng: {order.restaurantName}</p>
            <p className="text-sm">Địa chỉ: {order.address}</p>
            <p className="text-sm">
              Tổng giá:{" "}
              {(order.totalPrice + order.shippingFee).toLocaleString()}đ
            </p>
            <div className="mt-3">
              {order.cartDetails.map((item) => (
                <div key={item.id} className="flex items-center gap-4 mb-2">
                  <img
                    src={item.food_img}
                    alt={item.foodName}
                    className="w-16 h-16 object-cover rounded"
                  />
                  <div>
                    <p className="font-medium">{item.foodName}</p>
                    <p className="text-sm text-gray-600">
                      SL: {item.quantity} - Giá: {item.price.toLocaleString()}đ
                    </p>
                    <p className="text-xs text-gray-500">
                      Thêm:{" "}
                      {item.additionFoods.map((add) => add.name).join(", ")}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>

      <div className="mt-6 flex justify-center gap-2">
        {Array.from({ length: Math.ceil(total / size) }).map((_, i) => (
          <button
            key={i}
            className={`px-3 py-1 rounded border ${
              i === page ? "bg-blue-500 text-white" : "bg-white text-blue-500"
            }`}
            onClick={() => setPage(i)}
          >
            {i + 1}
          </button>
        ))}
      </div>
    </div>
  );
}
