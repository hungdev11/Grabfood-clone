
export interface Voucher {
  id: number;
  code: string;
  description: string;
  minRequire: number;
  type: 'PERCENTAGE' | 'FIXED';
  value: number;
  applyType: string;
  startTime: string | null;
  endTime: string | null;
  foodIds: number[];
  status: 'ACTIVE' | 'INACTIVE' | string;
  restaurant_name: string | null;
  active: boolean;
}

export interface VoucherRequest {
  code: string;
  description: string;
  type: 'PERCENTAGE' | 'FIXED';
  value: number;
  applyType: 'ALL' | 'SPECIFIC';
  status: 'ACTIVE' | 'INACTIVE' | 'EXPIRED';
  restaurant_id: number;
  foodIds: null | number[];
  startDate: string;
  endDate: string;
}

export interface AddAdminVoucherRequest {
  code: string;
  description: string;
  minRequire: number;
  type: 'PERCENTAGE' | 'FIXED';
  value: number;
  applyType: 'ORDER' | 'SHIPPING';
  status: 'ACTIVE' | 'INACTIVE';
}


export interface AddVoucherDetailRequest {
  quantity: number;
  startDate: string;
  endDate: string;
  voucher_id: number;
}

export interface AddVoucherDetailRequestRes {
  startDate: string;
  endDate: string;
  voucher_id: number;
  foodIds: number[];
}
