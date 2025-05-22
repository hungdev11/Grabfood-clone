"use client";

import AdminVoucher from "@/components/admin/AdminVoucher";
import { useState } from "react";
import RestaurantManagement from "@/components/admin/RestaurantManagement";

const Dashboard = () => {
  const [activeButton, setActiveButton] = useState<string | null>(null);

  const handleButtonClick = (buttonName: string) => {
    setActiveButton(buttonName);
  };

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <div className="w-64 bg-white shadow-md">
        <div className="p-6">
          <div className="text-[#00B14F] font-bold text-xl">GrabFood</div>
        </div>
        <nav className="mt-6">
          <div className="mt-4">
            <button
              className={`w-full p-4 text-left text-gray-600 ${
                activeButton === "Đơn hàng"
                  ? "bg-green-500 text-white rounded-lg"
                  : ""
              }`}
              onClick={() => handleButtonClick("Đơn hàng")}
            >
              Đơn hàng
            </button>
            <button
              className={`w-full p-4 text-left text-gray-600 ${
                activeButton === "Voucher"
                  ? "bg-green-500 text-white rounded-lg"
                  : ""
              }`}
              onClick={() => handleButtonClick("Voucher")}
            >
              Voucher
            </button>
            <button
              className={`w-full p-4 text-left text-gray-600 ${
                activeButton === "Restaurant"
                  ? "bg-green-500 text-white rounded-lg"
                  : ""
              }`}
              onClick={() => handleButtonClick("Restaurant")}
            >
              Nhà hàng
            </button>
            <button
              className={`w-full p-4 text-left text-gray-600 ${
                activeButton === "Shadow"
                  ? "bg-green-500 text-white rounded-lg"
                  : ""
              }`}
              onClick={() => handleButtonClick("Shadow")}
            >
              Shadow
            </button>
          </div>
        </nav>
      </div>

      {/* Main Content */}
      <div className="flex-1 p-6">
        {activeButton === "Voucher" && <AdminVoucher />}
        {activeButton === "Restaurant" && <RestaurantManagement />}
        {/* Bạn có thể thêm các component khác cho các nút khác nếu cần */}
      </div>
    </div>
  );
};

export default Dashboard;
