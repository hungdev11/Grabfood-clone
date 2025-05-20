"use client";

import React, { useState, useEffect } from "react";
import { CheckCircle } from "lucide-react";
import Image from "next/image";
import Link from "next/link";
import Header from "@/components/header";
import { Button } from "@/components/ui/button";
import axiosInstance from "@/utils/axiosInstance";
import { Loader2 } from "lucide-react"; // Icon xoay tròn từ lucide-react

// Interface cho dữ liệu API
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
  restaurantId: number | null;
  restaurantName: string;
  totalPrice: number;
  address: string;
  status: string;
  shippingFee: number;
  note: string;
  review: boolean;
  payment_method: string | null;
  cartDetails: CartDetail[];
  discountShippingFee: number | null;
  discountOrderPrice: number | null;
}

// Interface cho mục đơn hàng trong UI
interface CartItem {
  id: string;
  image: string;
  foodName: string;
  additionalFoods: AdditionalFood[];
  price: number;
  quantity: number;
}

export default function HomePage() {
  const [reviewInputs, setReviewInputs] = useState<{
    [key: number]: { show: boolean; content: string; rating: number };
  }>({});
  const [activeButton, setActiveButton] = useState<string>("Tất cả");
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const [userId, setUserId] = useState<number | null>(null);
  useEffect(() => {
      const storedId = localStorage.getItem("grabUserId");
      if (storedId) {
        setUserId(Number(storedId));
      }
    }, []);
    
  // Hàm đặt lại đơn hàng
  const reorder = async (userId: number, orderId: number) => {
    try {
      const response = await axiosInstance.post(
        `http://localhost:6969/grab/order/user/${userId}/reorder/${orderId}`
      );
      console.log("Đặt lại đơn hàng thành công:", response.data);
      if (response.data?.data) {
        alert("Đặt lại đơn hàng thành công! Vui lòng kiểm tra giỏ hàng.");
        window.location.href = "http://localhost:3000/order-history";
      } else {
        alert("Không thể đặt lại đơn hàng.");
      }
    } catch (err) {
      alert("Lỗi khi đặt lại đơn hàng.");
    }
  };

  // Ánh xạ button sang trạng thái API
  const statusMap: { [key: string]: string | undefined } = {
    "Tất cả": undefined, // Không có status để lấy tất cả
    "Chờ thanh toán": "PENDING",
    "Vận chuyển": "PENDING", // Giả định, cần xác nhận với backend
    "Chờ giao hàng": "PENDING", // Giả định, cần xác nhận với backend
    "Hoàn thành": "COMPLETED",
    "Đã hủy": "REJECTED",
  };

  // Hàm lấy dữ liệu từ API
  const fetchOrders = async (status?: string) => {
    try {
      const response = await axiosInstance.get(
        "http://localhost:6969/grab/order",
        {
          params: {
            status: status, // Truyền status qua params, undefined nếu không có
          },
        }
      );
      console.log("Dữ liệu đơn hàng:", response.data); // In dữ liệu nhận được từ API

      if (response) {
        setOrders(response.data);
      } else {
        setError("Không thể tải danh sách đơn hàng.");
      }
      setLoading(false);
    } catch (err) {
      setError("Lỗi khi tải danh sách đơn hàng. Vui lòng thử lại.");
      console.error("Lỗi khi gọi API đơn hàng:", err);
      setLoading(false);
    }
  };

  // Gọi API khi activeButton thay đổi
  useEffect(() => {
    setLoading(true);
    setError(null);
    const status = statusMap[activeButton];
    fetchOrders(status);
  }, [activeButton]);

  // Danh sách button điều hướng
  const navButtons = [
    "Tất cả",
    "Chờ thanh toán",
    "Vận chuyển",
    "Chờ giao hàng",
    "Hoàn thành",
    "Đã hủy",
  ];

  // Hiển thị trạng thái đơn hàng
  const getStatusDisplay = (status: string) => {
    switch (status) {
      case "PENDING":
        return { text: "Chờ xử lý", color: "text-yellow-500" };
      case "COMPLETED":
        return { text: "Hoàn thành", color: "text-green-500" };
      case "REJECTED":
        return { text: "Đã hủy", color: "text-red-500" };
      case "CANCELLED":
        return { text: "Đã hủy", color: "text-red-500" };
      default:
        return { text: status, color: "text-gray-500" };
    }
  };

  const toggleReviewInput = (orderId: number) => {
    setReviewInputs((prev) => ({
      ...prev,
      [orderId]: {
        show: !prev[orderId]?.show,
        content: "",
        rating: 5,
      },
    }));
  };

  const handleReviewChange = (
    orderId: number,
    field: "content" | "rating",
    value: string | number
  ) => {
    setReviewInputs((prev) => ({
      ...prev,
      [orderId]: {
        ...prev[orderId],
        [field]: value,
      },
    }));
  };

  const submitReview = async (orderId: number) => {
    const { content, rating } = reviewInputs[orderId];
    try {
      await axiosInstance.post("http://localhost:6969/grab/reviews", {
        orderId,
        reviewMessage: content, // Dùng reviewMessage thay vì content
        rating,
      });
      alert("Đánh giá thành công!");
      fetchOrders(statusMap[activeButton]); // refresh lại đơn hàng
    } catch (err) {
      alert("Lỗi khi gửi đánh giá");
    }
  };

  if (loading) {
    return (
      <div className="fixed inset-0 flex items-center justify-center bg-white-500 bg-opacity-50 z-50">
        <Loader2 className="w-12 h-12 text-green-500 animate-spin" />
      </div>
    );
  }

  if (error) {
    return <div className="container mx-auto p-4">Lỗi: {error}</div>;
  }

  return (
    <div className="flex flex-col min-h-screen">
      {/* Header */}
      <Header />

      <div className="container mx-auto p-4 max-w-4xl">
        {/* Thanh điều hướng */}
        <nav className="bg-gray-100 p-3 rounded-lg mb-4 overflow-x-auto">
          <div className="flex justify-between space-x-2 min-w-max">
            {navButtons.map((button) => (
              <button
                key={button}
                onClick={() => setActiveButton(button)}
                className={`px-3 py-1 rounded whitespace-nowrap ${
                  activeButton === button
                    ? "bg-green-500 text-white"
                    : "text-gray-700 hover:text-green-500"
                }`}
              >
                {button}
              </button>
            ))}
          </div>
        </nav>

        {/* Phần chính */}
        <main className="bg-gray-50">
          {/* Thanh tìm kiếm */}
          <div className="mb-6">
            <input
              type="text"
              placeholder="Bạn có thể tìm kiếm theo Tên Shop, ID đơn hàng hoặc Tên Sản phẩm"
              className="w-full p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>

          {/* Phần đơn hàng */}
          {orders.length === 0 ? (
            <div className="bg-white p-4 rounded-lg shadow text-center text-gray-500">
              Không có đơn hàng nào
            </div>
          ) : (
            orders.map((order) => (
              <div
                key={order.id}
                className="bg-white p-4 rounded-lg shadow mb-4"
              >
                <div className="flex flex-col sm:flex-row sm:items-center justify-between mb-4">
                  <div className="flex items-center space-x-2 mb-2 sm:mb-0">
                    <span className="text-gray-700 font-medium">
                      {order.restaurantName || "Khong xac dinh"}
                    </span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() =>
                        (window.location.href = `http://localhost:3000/restaurant/${order.restaurantId}`)
                      }
                      className="border border-gray-300 text-gray-700 px-3 py-1 rounded hover:bg-gray-100 text-sm"
                    >
                      Xem Shop
                    </button>
                    <div className="flex items-center space-x-1">
                      {order.status === "COMPLETED" && (
                        <CheckCircle className="text-green-500 w-4 h-4" />
                      )}
                      <span
                        className={`font-semibold text-sm ${
                          getStatusDisplay(order.status).color
                        }`}
                      >
                        {getStatusDisplay(order.status).text.toUpperCase()}
                      </span>
                    </div>
                  </div>
                </div>

                {/* Thông tin món ăn */}
                {order.cartDetails.map((item) => (
                  <div
                    key={item.id}
                    className="flex items-center justify-between mb-3"
                  >
                    <div className="flex items-center gap-2">
                      <Image
                        src={item.food_img}
                        alt={item.foodName}
                        width={50}
                        height={50}
                        className="rounded-md object-cover"
                      />
                      <div>
                        <h3 className="text-base font-medium">
                          {item.foodName}
                        </h3>
                        <div className="text-xs text-gray-600">
                          {item.additionFoods.map((food) => (
                            <p key={food.id}>+ {food.name}</p>
                          ))}
                        </div>
                        <div className="flex items-center gap-1 mt-1">
                          <button
                            className="w-6 h-6 flex items-center justify-center bg-gray-200 rounded-full text-gray-600"
                            disabled
                          >
                            -
                          </button>
                          <span className="text-sm">{item.quantity}</span>
                          <button
                            className="w-5 h-5 flex items-center justify-center bg-gray-200 rounded-full text-gray-600 text-sm"
                            disabled
                          >
                            +
                          </button>
                        </div>
                      </div>
                    </div>
                    <p className="text-base font-semibold">
                      {(item.price * item.quantity).toLocaleString()} đ
                    </p>
                  </div>
                ))}
                <div className="border-t pt-3 mt-3">
                  <div className="flex justify-between text-sm text-gray-600 mb-1">
                    <span>Tổng tiền</span>
                    <span>{order.totalPrice.toLocaleString()} đ</span>
                  </div>
                  <div className="flex justify-between text-sm text-gray-600 mb-1">
                    <span>
                      Phí vận chuyển{" "}
                      <span className="text-blue-500 cursor-pointer">ⓘ</span>
                    </span>
                    <span>{order.shippingFee.toLocaleString()} đ</span>
                  </div>
                  {(order.discountOrderPrice ?? 0) !== 0 && (
                    <div className="flex justify-between text-sm text-gray-600 mb-1">
                      <span>Giảm giá </span>
                      <span>
                        - {(order.discountOrderPrice ?? 0).toLocaleString()} đ
                      </span>
                    </div>
                  )}
                  {(order.discountShippingFee ?? 0) !== 0 && (
                    <div className="flex justify-between text-sm text-gray-600 mb-1">
                      <span>Giảm giá vận chuyển</span>
                      <span>
                        - {(order.discountShippingFee ?? 0).toLocaleString()} đ
                      </span>
                    </div>
                  )}
                </div>

                {/* Nút hành động */}
                <div className="flex flex-col sm:flex-row justify-end space-y-3 sm:space-y-0 sm:space-x-4 mt-4">
                  {order.status === "COMPLETED" &&
                    (!order.review && (
                      <>
                        <button
                          onClick={() => toggleReviewInput(order.id)}
                          className="border border-gray-300 bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 w-full sm:w-auto"
                        >
                          {reviewInputs[order.id]?.show ? "Đóng" : "Đánh giá"}
                        </button>

                        {reviewInputs[order.id]?.show && (
                          <div className="w-full mt-3 space-y-2">
                            <textarea
                              className="w-full border rounded px-3 py-2"
                              rows={3}
                              placeholder="Nhập đánh giá..."
                              value={reviewInputs[order.id].content}
                              onChange={(e) =>
                                handleReviewChange(
                                  order.id,
                                  "content",
                                  e.target.value
                                )
                              }
                            />
                            <div className="flex items-center space-x-2">
                              <label className="text-sm">Số sao:</label>
                              <select
                                className="border px-2 py-1 rounded"
                                value={reviewInputs[order.id].rating}
                                onChange={(e) =>
                                  handleReviewChange(
                                    order.id,
                                    "rating",
                                    Number(e.target.value)
                                  )
                                }
                              >
                                {[5, 4, 3, 2, 1].map((star) => (
                                  <option key={star} value={star}>
                                    {star}
                                  </option>
                                ))}
                              </select>
                              <button
                                onClick={() => submitReview(order.id)}
                                disabled={!reviewInputs[order.id].content}
                                className="bg-blue-500 text-white px-4 py-1 rounded hover:bg-blue-600 disabled:opacity-50"
                              >
                                Gửi đánh giá
                              </button>
                            </div>
                          </div>
                        )}
                      </>
                    ))}
                    {order.status === "COMPLETED" && (
                      <button 
                        className="border border-gray-300 bg-yellow-500 text-white px-4 py-2 rounded hover:bg-yellow-600 w-full sm:w-auto"
                        onClick={() => reorder(userId, order.id)}>Đặt lại
                      </button>
                    )}

                </div>
              </div>
            ))
          )}
        </main>
      </div>
    </div>
  );
}
