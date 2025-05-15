"use client";

import { Bell, Trash2, Eye } from "lucide-react";
import { motion } from "framer-motion";
import { useState, useEffect } from "react";
import { connectWebSocket, disconnectWebSocket } from "@/lib/websocket";
import { Notification } from "@/components/types/Types";

type NotificationBellProps = {
  channelId: string;
  notifications: Notification[];
  onTrigger: () => void;
  onMarkAsRead: (id: string) => void;
  onMarkAllAsRead: () => void;
  onDeleteNotification: (id: string) => void;
  onDeleteAll: () => void;
};

export const NotificationBell = ({
  channelId,
  notifications,
  onTrigger,
  onMarkAsRead,
  onMarkAllAsRead,
  onDeleteNotification,
  onDeleteAll
}: NotificationBellProps) => {
  const [isOpen, setIsOpen] = useState(false);

  useEffect(() => {
    const handleOrderReceived = () => {
      onTrigger(); // Trigger khi có thông báo mới
    };

    connectWebSocket(channelId, handleOrderReceived);
    return () => disconnectWebSocket();
  }, [channelId, onTrigger]);

  const unreadCount = notifications.filter((n) => !n.read).length;

  return (
    <div className="relative inline-block text-left">
      <button
        onClick={() => setIsOpen((prev) => !prev)}
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
          className="absolute right-0 mt-2 w-80 bg-white border border-gray-200 rounded shadow-lg z-50"
        >
          <div className="flex justify-between items-center p-2 font-semibold border-b">
            <span>Thông báo</span>
            <button
              onClick={onMarkAllAsRead}
              className="text-xs text-blue-500 hover:underline"
            >
              Đánh dấu đã xem tất cả
            </button>
          </div>

          <ul className="max-h-64 overflow-y-auto divide-y divide-gray-100">
            {notifications.length === 0 ? (
              <li className="p-2 text-sm text-gray-500">Không có thông báo</li>
            ) : (
              notifications.map((n) => (
                <li
                  key={n.id}
                  className={`flex justify-between items-start p-2 text-sm hover:bg-gray-50 ${
                    n.read ? "bg-white" : "bg-blue-50"
                  }`}
                >
                  <div className="flex-1">
                    <p className={`${n.read ? "text-gray-500" : "text-black font-semibold"}`}>
                      {n.subject}: {n.body}
                    </p>
                    <span className="text-xs text-gray-400">{n.timeArrived}</span>
                  </div>
                  <div className="flex flex-col items-end ml-2">
                    {!n.read && (
                      <button
                        onClick={() => onMarkAsRead(n.id)}
                        title="Đánh dấu đã đọc"
                        className="text-blue-500 hover:text-blue-700"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                    )}
                    <button
                      onClick={() => onDeleteNotification(n.id)}
                      className="text-red-500 hover:text-red-700 mt-1"
                      title="Xóa"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </li>

              ))
            )}
          </ul>

          {notifications.length > 0 && (
            <div className="border-t p-2 text-right">
              <button
                onClick={onDeleteAll}
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
