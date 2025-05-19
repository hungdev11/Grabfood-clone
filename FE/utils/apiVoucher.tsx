
import axios from 'axios';
import { Voucher } from '@/components/types/voucher';
import { VoucherRequest } from '@/components/types/voucher';
import { toast } from 'react-toastify';

interface ApiResponse {
  data: Voucher[];
  message: string;
  code: number;
}

// ⚠️ Đổi thành URL thật

export const fetchVouchers = async (restaurantId: string): Promise<Voucher[]> => {
  const API_URL = `http://localhost:6969/grab/vouchers/restaurant/${restaurantId}`;
  try {
    const response = await axios.get<ApiResponse>(API_URL);

    if (response.data.code === 200) {
      return response.data.data;
    } else {
      throw new Error(response.data.message || 'Failed to fetch vouchers');
    }
  } catch (error) {
    console.error('Error fetching vouchers:', (error as Error).message);
    return [];
  }
};

export const createVoucher = async (data: VoucherRequest) => {
  try {
    const response = await axios.post<ApiResponse>('http://localhost:6969/grab/vouchers', data, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    if (response.data.code === 200) {
      return response.data.code;
    } else {
      throw new Error(response.data.message || 'Failed to create vouchers');
    }
  } catch (error: any) {
    throw error.response?.data || error.message;
  }
};

export const deleteVoucher = async (voucherId: number) => {
  try {
    const response = await axios.delete(`http://localhost:6969/grab/vouchers/${voucherId}`);
    if (response.data.code === 200) {
      toast.success('Xóa thành công!');
    }
    else if (response.data.status === 404) {
      toast.error('Voucher đã được sử dụng, không thể xóa!');
    } else {
      toast.error('Đã xảy ra lỗi khi xóa, vui lòng thử lại!');
    }
  } catch (error: any) {
    toast.error('Đã xảy ra lỗi khi xóa!');
}
}

