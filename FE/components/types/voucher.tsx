
export interface Voucher {
  id: number;
  code: string;
  description: string;
  minRequire: number;
  type: 'PERCENTAGE' | 'FIXED';
  value: number;
  applyType: string;
  endTime: string | null;
  status: 'ACTIVE' | 'INACTIVE' | string;
  restaurant_name: string | null;
  active: boolean;
}

export interface VoucherRequest {
  code: string;
  description: string;
  minRequire: number;
  type: 'PERCENTAGE' | 'FIXED';
  value: number;
  applyType: 'ORDER' | 'SHIPPING';
  status: 'ACTIVE' | 'INACTIVE' | 'EXPIRED';
  restaurant_id: number;
}
