"use client";

import React from "react";
import { useState } from "react";
import { useParams } from "next/navigation";
import { NotificationBell } from "@/components/NotificationBell";
import Sidebar from "@/components/manage_res/SideBar";
import ProfileManagement from "@/components/manage_res/ProfileManagement";
import OrdersManagement from "@/components/manage_res/OrderManagement";
import MenuManagement from "@/components/manage_res/MenuManagement";
import RevenueReport from "@/components/manage_res/RevenueReport";
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
          <h1 className="text-3xl font-bold">Quản lý Nhà Hàng</h1>  {/* Hiển thị restaurantId */}
          <NotificationBell
            channelId={`restaurant/${restaurantId}`}  // Truyền dynamic channelId từ restaurantId
            parseMessage={(msg) => `📦 Đơn hàng mới: mã #${msg.id}`}
          />
        </div>

        {/* Content area */}
        <div className="mt-4">
          {selectedMenu === "profile" && <ProfileManagement />} 
          {selectedMenu === "orders" && <OrdersManagement />}
          {selectedMenu === "main" && <MenuManagement />}
          {/* {selectedMenu === "vouchers" && <VoucherManagement />}  */}
          {selectedMenu === "side" && <div>Hiển thị danh sách Món phụ</div>}
          {selectedMenu === "report" && <RevenueReport />}
        </div>
      </div>
    </div>
  );
}