"use client";

import { useEffect, useState } from "react";
import { Bell, Trash2 } from "lucide-react";
import { motion } from "framer-motion";
import { connectWebSocket, disconnectWebSocket } from "@/lib/websocket";

type Notification = {
  id: number;
  message: string;
  read: boolean;
};

export const NotificationBell = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [nextId, setNextId] = useState(1); // để tạo id thông báo

  const unreadCount = notifications.filter((n) => !n.read).length;

  const toggleDropdown = () => {
    setIsOpen((prev) => !prev);
  };

  const markAllAsRead = () => {
    setNotifications((prev) =>
      prev.map((n) => ({ ...n, read: true }))
    );
  };

  const deleteNotification = (id: number) => {
    setNotifications((prev) => prev.filter((n) => n.id !== id));
  };

  const deleteAllNotifications = () => {
    setNotifications([]);
  };

  // 📡 Nhận đơn hàng qua websocket rồi thêm vào danh sách noti
  useEffect(() => {
    const restaurantId = "1"; // ID thật ở đây

    const handleOrderReceived = (order: string) => {
      setNotifications((prev) => [
        { id: nextId, message: order, read: false },
        ...prev,
      ]);
      setNextId((id) => id + 1);
    };

    connectWebSocket(restaurantId, handleOrderReceived);

    return () => {
      disconnectWebSocket();
    };
  }, [nextId]); // Cập nhật mỗi khi thêm noti mới

  return (
    <div className="relative inline-block text-left">
      <button
        onClick={() => {
          toggleDropdown();
          markAllAsRead();
        }}
        className="relative"
      >
        <Bell className="w-6 h-6 text-gray-700" />
        {unreadCount > 0 && (
          <span className="absolute top-0 right-0 block w-2 h-2 rounded-full bg-red-500"></span>
        )}
      </button>

      {isOpen && (
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          className="absolute right-0 mt-2 w-72 bg-white border border-gray-200 rounded shadow-lg z-50"
        >
          <div className="p-2 font-semibold border-b">Thông báo</div>
          <ul className="max-h-64 overflow-y-auto divide-y divide-gray-100">
            {notifications.length === 0 ? (
              <li className="p-2 text-sm text-gray-500">Không có thông báo</li>
            ) : (
              notifications.map((n) => (
                <li
                  key={n.id}
                  className="flex items-center justify-between p-2 text-sm group hover:bg-gray-50"
                >
                  <span className={`${n.read ? "text-gray-500" : "text-black font-semibold"}`}>
                    {n.message}
                  </span>
                  <button
                    onClick={() => deleteNotification(n.id)}
                    className="text-gray-400 hover:text-red-500 ml-2"
                    title="Xóa thông báo"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </li>
              ))
            )}
          </ul>

          {notifications.length > 0 && (
            <div className="border-t p-2 text-right">
              <button
                onClick={deleteAllNotifications}
                className="text-sm text-red-600 hover:underline flex items-center gap-1 float-right"
              >
                <Trash2 className="w-4 h-4" />
                Xóa tất cả
              </button>
            </div>
          )}
        </motion.div>
      )}
    </div>
  );
};
