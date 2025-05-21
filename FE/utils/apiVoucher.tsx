
import axios from 'axios';
import { AddAdminVoucherRequest, AddVoucherDetailRequestRes, Voucher } from '@/components/types/voucher';
import { VoucherRequest, AddVoucherDetailRequest } from '@/components/types/voucher';
import { toast } from 'react-toastify';
import { tr } from 'date-fns/locale';

interface ApiResponse {
  data: any;
  message: string;
  code: number;
}


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

export const fetchVouchersRestaurant = async (restaurantId: string): Promise<Voucher[]> => {
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

export const createVoucher = async (data: AddAdminVoucherRequest) => {
  try {
    const response = await axios.post<ApiResponse>('http://localhost:6969/grab/vouchers', data, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    if (response.data.code === 200) {
      toast.success('Tạo voucher thành công!');
      return response.data.code;
    } else {
      toast.error(`Lỗi: ${response.data.message}`);
    }
  } catch (error: any) {
    throw error.response?.data || error.message;
  }
};

export const createVoucherRestaurant = async (data: VoucherRequest) => {
  try {
    const response = await axios.post<ApiResponse>(`http://localhost:6969/grab/vouchers/restaurant`, data, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    if (response.data.code === 200 && response.data.data !== -1) {
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

export const updateVoucher = async (voucherId: number) => {
  try {
    const response = await axios.put(`http://localhost:6969/grab/vouchers/${voucherId}`);
    if (response.data.code === 200) {
      toast.success('Cập nhật thành công!');
    } else {
      toast.error('Đã xảy ra lỗi khi cập nhật, vui lòng thử lại!');
    }
  } catch (error: any) {
    toast.error('Đã xảy ra lỗi khi cập nhật!');
  }
}

export const addVoucherDetailRes = async (data: AddVoucherDetailRequestRes ) => {
  try {
    const response = await axios.post<ApiResponse>('http://localhost:6969/grab/vouchers/extend-voucher', data, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    console.log(response.data);
    if (response.data.code === 200 && response.data.data === true) {
      toast.success('Thêm chi tiết voucher thành công!');
    } else {
      throw new Error(response.data.message || 'Failed to add voucher detail');
    }
  } catch (error: any) {
    toast.error('Đã xảy ra lỗi khi thêm chi tiết voucher!');
  }
}

export const deleteVoucherRes = async (restaurantId: string, voucherId: number) => {
  try {
    const response = await axios.delete(`http://localhost:6969/grab/vouchers/${voucherId}/restaurant/${restaurantId}`);
    if (response.data.code === 200 && response.data.data === true) {
      toast.success('Xóa thành công!');
    } else {
      toast.error('Không thể xóa voucher!');
    }
    } catch (error: any) {
      toast.error('Đã xảy ra lỗi khi xóa!');
  }
}

export const fetchAdminVouchers = async (): Promise<Voucher[]> => {
  try {
    const response = await axios.get<ApiResponse>('http://localhost:6969/grab/vouchers/admin');
    if (response.data.code === 200) {
      return response.data.data;
    } else {
      toast.error('Lấy danh sách voucher thất bại!');
      throw new Error(response.data.message || 'Failed to fetch vouchers');
    }
  } catch (error) {
    toast.error('Đã xảy ra lỗi khi lấy danh sách voucher!');
    console.error('Error fetching vouchers:', (error as Error).message);
      return [];

  }
}

export const addAdminVoucherDetail = async (data: AddVoucherDetailRequest) => {
  try {
    const response = await axios.post<ApiResponse>('http://localhost:6969/grab/voucherDetails', data, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    if (response.data.code === 200) {
      toast.success('Thêm chi tiết voucher thành công!');
    } else {
       toast.error(`Lỗi: ${response.data.message}`);
    }
  } catch (error: any) {
    toast.error('Đã xảy ra lỗi khi thêm chi tiết voucher!');
  }
}

