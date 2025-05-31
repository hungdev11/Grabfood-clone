import axiosInstance from './axiosInstance';

// Interface định nghĩa cấu trúc dữ liệu doanh thu theo tháng
export interface MonthlyRevenue {
  monthYear: string; // Format: "MM/YYYY"
  amount: number;
}

export interface RevenueData {
    monthlyRevenues: MonthlyRevenue[];
    totalRevenue: number;
  }
  
  // Interface cho kết quả trả về từ API
  export interface RevenueResponse {
    data: RevenueData;
    message: string;
    code: number;
  }

// Hàm lấy doanh thu theo khoảng thời gian
export const getRevenueByTimeRange = async (
  startMonthYear: string, // Format: "MM/yyyy"
  endMonthYear: string    // Format: "MM/yyyy"
): Promise<RevenueResponse> => {
  try {
    const response = await axiosInstance.get('/grab/admin/revenues', {
      params: {
        startMonthYear,
        endMonthYear,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching revenue data:', error);
    throw error;
  }
};