"use client";

import { useState, useEffect } from "react";
import { getRevenueByTimeRange, MonthlyRevenue } from "@/utils/apiRevenue";
import { Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

// Đăng ký các components cần thiết cho ChartJS
ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

const RevenueTracking = () => {
  // State cho khoảng thời gian
  const [startMonth, setStartMonth] = useState(1);
  const [startYear, setStartYear] = useState(new Date().getFullYear());
  const [endMonth, setEndMonth] = useState(new Date().getMonth() + 1);
  const [endYear, setEndYear] = useState(new Date().getFullYear());

  // State cho dữ liệu doanh thu
  const [revenueData, setRevenueData] = useState<MonthlyRevenue[]>([]);
  const [totalRevenue, setTotalRevenue] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Tạo mảng năm từ 2020 đến năm hiện tại để hiển thị trong dropdown
  const currentYear = new Date().getFullYear();
  const years = Array.from(
    { length: currentYear - 2020 + 1 },
    (_, i) => 2020 + i
  );

  // Định dạng số tiền thành chuỗi có dấu phân cách hàng nghìn
  const formatCurrency = (amount: number) => {
    return amount.toLocaleString("vi-VN") + " VNĐ";
  };
  // Hàm tạo chuỗi tháng/năm theo định dạng "MM/yyyy"
  const formatMonthYear = (month: number, year: number) => {
    return `${month.toString().padStart(2, "0")}/${year}`;
  };

  // Hàm fetch dữ liệu doanh thu
  const fetchRevenueData = async () => {
    setLoading(true);
    setError(null);

    try {
      // Kiểm tra tính hợp lệ của khoảng thời gian
      if (
        startYear > endYear ||
        (startYear === endYear && startMonth > endMonth)
      ) {
        throw new Error("Thời gian bắt đầu phải trước thời gian kết thúc");
      }

      // Tạo chuỗi ngày theo định dạng "MM/yyyy"
      const startMonthYear = formatMonthYear(startMonth, startYear);
      const endMonthYear = formatMonthYear(endMonth, endYear);

      const response = await getRevenueByTimeRange(
        startMonthYear,
        endMonthYear
      );
      console.log("Revenue data:", response);

      const result = response.data;
      setRevenueData(result.monthlyRevenues);
      setTotalRevenue(result.totalRevenue);
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Đã xảy ra lỗi khi tải dữ liệu"
      );
    } finally {
      setLoading(false);
    }
  };

  // Chuẩn bị dữ liệu cho biểu đồ
  const chartData = {
    labels: revenueData?.map((item) => item.monthYear) || [],
    datasets: [
      {
        label: "Doanh thu (VNĐ)",
        data: revenueData?.map((item) => item.amount) || [],
        backgroundColor: "rgba(0, 177, 79, 0.7)", // Màu xanh GrabFood
        borderColor: "rgba(0, 177, 79, 1)",
        borderWidth: 1,
      },
    ],
  };
  // Cấu hình tùy chọn cho biểu đồ
  const chartOptions = {
    responsive: true,
    plugins: {
      legend: {
        position: "top" as const,
      },
      title: {
        display: true,
        text: "Doanh thu theo tháng",
        font: {
          size: 16,
        },
      },
      tooltip: {
        callbacks: {
          label: function (context: any) {
            return formatCurrency(context.parsed.y);
          },
        },
      },
    },
    scales: {
      y: {
        beginAtZero: true,
      },
    },
  };
  return (
    <div className="bg-white p-6 rounded-lg shadow-md max-w-full overflow-hidden">
      <h2 className="text-2xl font-bold mb-4">Thống kê doanh thu</h2>

      {/* Form chọn khoảng thời gian */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
        <div>
          <h3 className="font-medium mb-2">Từ:</h3>
          <div className="flex gap-2">
            <div className="w-1/2">
              <label className="block text-sm mb-1">Tháng</label>
              <select
                className="w-full p-2 border rounded-md"
                value={startMonth}
                onChange={(e) => setStartMonth(parseInt(e.target.value))}
              >
                {Array.from({ length: 12 }, (_, i) => i + 1).map((month) => (
                  <option key={`start-month-${month}`} value={month}>
                    {month}
                  </option>
                ))}
              </select>
            </div>
            <div className="w-1/2">
              <label className="block text-sm mb-1">Năm</label>
              <select
                className="w-full p-2 border rounded-md"
                value={startYear}
                onChange={(e) => setStartYear(parseInt(e.target.value))}
              >
                {years.map((year) => (
                  <option key={`start-year-${year}`} value={year}>
                    {year}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        <div>
          <h3 className="font-medium mb-2">Đến:</h3>
          <div className="flex gap-2">
            <div className="w-1/2">
              <label className="block text-sm mb-1">Tháng</label>
              <select
                className="w-full p-2 border rounded-md"
                value={endMonth}
                onChange={(e) => setEndMonth(parseInt(e.target.value))}
              >
                {Array.from({ length: 12 }, (_, i) => i + 1).map((month) => (
                  <option key={`end-month-${month}`} value={month}>
                    {month}
                  </option>
                ))}
              </select>
            </div>
            <div className="w-1/2">
              <label className="block text-sm mb-1">Năm</label>
              <select
                className="w-full p-2 border rounded-md"
                value={endYear}
                onChange={(e) => setEndYear(parseInt(e.target.value))}
              >
                {years.map((year) => (
                  <option key={`end-year-${year}`} value={year}>
                    {year}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>
      </div>

      <button
        className="bg-green-500 text-white px-4 py-2 rounded-md hover:bg-green-600 transition-colors mb-6"
        onClick={fetchRevenueData}
        disabled={loading}
      >
        {loading ? "Đang tải..." : "Xem thống kê"}
      </button>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      {/* Hiển thị biểu đồ khi có dữ liệu */}
      {revenueData.length > 0 && (
        <div className="mt-6">
          <div
            className="w-full"
            style={{ height: "400px", position: "relative" }}
          >
            <Bar
              data={chartData}
              options={{ ...chartOptions, maintainAspectRatio: false }}
            />
          </div>

          <div className="bg-gray-100 p-4 rounded-md mt-6">
            <div className="flex justify-between items-center">
              <span className="font-bold">Tổng doanh thu:</span>
              <span className="text-xl font-bold text-green-600">
                {formatCurrency(totalRevenue)}
              </span>
            </div>
          </div>
        </div>
      )}

      {/* Thông báo khi không có dữ liệu */}
      {!loading && !error && revenueData.length === 0 && (
        <div className="text-center py-10 text-gray-500">
          Hãy chọn khoảng thời gian và bấm "Xem thống kê" để hiển thị dữ liệu
        </div>
      )}
    </div>
  );
};

export default RevenueTracking;
