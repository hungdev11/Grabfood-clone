'use client';

import { useEffect } from 'react';
import { Voucher } from '../types/voucher';


export default function DiscountDetailModal({
  onOpen,
  data,
  onClose,
}: {
  onOpen: boolean;
  data: Voucher;
  onClose: () => void;
}) {
  // Format ngày giờ hiển thị thân thiện
  const formatDate = (iso: string | null) => {
    if (!iso) return 'N/A';
    const d = new Date(iso);
    return d.toLocaleString('vi-VN');
  };

  if (!onOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg w-full max-w-md p-6 shadow-lg space-y-6">
        <div className="flex justify-between items-center border-b pb-2">
          <h2 className="text-lg font-semibold">Chi Tiết Mã Giảm Giá</h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700 text-xl font-bold"
          >
            &times;
          </button>
        </div>

        <div className="space-y-3">
          <DetailRow label="Mã giảm giá" value={data.code} />
          <DetailRow label="Mô tả" value={data.description} />
          <DetailRow
            label="Giá trị tối thiểu"
            value={`${data.minRequire.toLocaleString()}₫`}
          />
          <DetailRow
            label="Giá trị giảm"
            value={
              data.type === 'PERCENTAGE'
                ? `${data.value}%`
                : `${data.value.toLocaleString()}₫`
            }
          />
          <DetailRow
            label="Áp dụng cho"
            value={data.applyType === 'ORDER' ? 'Đơn hàng' : 'Vận chuyển'}
          />
          <DetailRow label="Thời gian bắt đầu" value={formatDate(data.startTime)} />
          <DetailRow label="Thời gian hết hạn" value={formatDate(data.endTime)} />
        </div>

        <div className="flex justify-end pt-4 border-t">
          <button
            onClick={onClose}
            className="bg-gray-100 hover:bg-gray-200 text-gray-800 px-4 py-2 rounded"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
}

function DetailRow({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <p className="text-sm font-medium text-gray-700">{label}</p>
      <p className="text-base text-gray-900">{value}</p>
    </div>
  );
}
