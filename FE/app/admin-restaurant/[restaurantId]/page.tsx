"use client";

import React, { useState, useEffect } from "react";
import axios from "axios";
import { useParams } from "next/navigation";
import Sidebar from "@/components/manage_res/SideBar";
import ProfileManagement from "@/components/manage_res/ProfileManagement";
import OrdersManagement from "@/components/manage_res/OrderManagement";
import MenuManagement from "@/components/manage_res/MenuManagement";
import RevenueReport from "@/components/manage_res/RevenueReport";
import { Notification } from "@/components/types/Types";
import { NotificationBell } from "@/components/NotificationBell";
import VoucherManagement from "@/components/manage_res/VoucherManagement";

export default function RestaurantPage() {
  const [selectedMenu, setSelectedMenu] = useState("profile");
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const params = useParams();
  const restaurantId = params?.restaurantId as string;

  const fetchNotifications = async () => {
    try {
      const res = await axios.get(`http://localhost:6969/grab/notifications/restaurant/${restaurantId}`);
      setNotifications(res.data.data || []);
    } catch (error) {
      console.error("Lỗi khi tải thông báo:", error);
    }
  };

  useEffect(() => {
    if (restaurantId) fetchNotifications();
  }, [restaurantId]);

  const handleMarkAsRead = async (id: string) => {
    await axios.patch(`http://localhost:6969/grab/notifications/${id}/read`);
    fetchNotifications();
  };


  const handleMarkAllAsRead = async () => {
    await axios.patch(`http://localhost:6969/grab/notifications/restaurant/${restaurantId}/read-all`);
    fetchNotifications();
  };

  const handleDeleteNotification = async (id: string) => {
    await axios.delete(`http://localhost:6969/grab/notifications/${id}`);
    fetchNotifications();
  };

  const handleDeleteAll = async () => {
    await axios.delete(`http://localhost:6969/grab/notifications/restaurant/${restaurantId}`);
    fetchNotifications();
  };

  if (!restaurantId) return <div>Restaurant ID not found</div>;

  return (
    <div className="flex h-screen">
      <Sidebar setSelectedMenu={setSelectedMenu} />
      <div className="flex-1 p-6 bg-gray-50">
        <div className="flex justify-between items-center py-4 border-b">
          <h1 className="text-3xl font-bold">Quản lý Nhà Hàng</h1>
          <NotificationBell
            channelId={`restaurant/${restaurantId}`}
            notifications={notifications}
            onTrigger={fetchNotifications}
            onMarkAsRead={handleMarkAsRead}
            onMarkAllAsRead={handleMarkAllAsRead}
            onDeleteNotification={handleDeleteNotification}
            onDeleteAll={handleDeleteAll}
          />
        </div>

        <div className="mt-4">
          {selectedMenu === "profile" && <ProfileManagement />}
          {selectedMenu === "orders" && <OrdersManagement />}
          {selectedMenu === "main" && <MenuManagement />}
          {selectedMenu === "side" && <div>Hiển thị danh sách Món phụ</div>}
          {selectedMenu === "report" && <RevenueReport />}
          {selectedMenu === "vouchers" && <VoucherManagement />}
        </div>
      </div>
    </div>
  );
}
