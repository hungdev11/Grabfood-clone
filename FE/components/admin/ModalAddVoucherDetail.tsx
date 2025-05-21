'use client';

import React, { useState } from 'react';

import { AddVoucherDetailRequest } from '../types/voucher';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: AddVoucherDetailRequest) => void;
  voucherId?: number;
}

const ModalAddVoucherDetail: React.FC<ModalProps> = ({voucherId, isOpen, onClose, onSubmit}) => {
  const [formData, setFormData] = useState<AddVoucherDetailRequest>({
    quantity: 0,
    startDate: '',
    endDate: '',
    voucher_id: voucherId || 0,
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'quantity' || name === 'voucher_id' ? parseInt(value) || 0 : value,
    }));
  };

  const formatDateTime = (dateTime: string): string => {
    if (!dateTime) return '';
    // Chuyển đổi thành định dạng YYYY-MM-DDTHH:mm:ss
    const date = new Date(dateTime);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = '00'; // Thêm giây mặc định là 00
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const formattedData = {
      ...formData,
      startDate: formatDateTime(formData.startDate),
      endDate: formatDateTime(formData.endDate),
    };
    onSubmit(formattedData);
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg w-full max-w-md p-6 shadow-lg space-y-6">
        <div className="flex justify-between items-center border-b pb-2">
          <h2 className="text-lg font-semibold">Thiết lập Giảm Giá Theo Thời Gian</h2>
          <button onClick={onClose} className="text-gray-500 hover:text-gray-700 text-xl font-bold">&times;</button>
        </div>

        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Số lượng</label>
            <input
              type="number"
              name="quantity"
              value={formData.quantity}
              onChange={handleChange}
              className="w-full border rounded px-3 py-2 text-black"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Ngày bắt đầu</label>
            <input
              type="datetime-local"
              name="startDate"
              value={formData.startDate}
              onChange={handleChange}
              className="w-full border rounded px-3 py-2 text-black"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Ngày kết thúc</label>
            <input
              type="datetime-local"
              name="endDate"
              value={formData.endDate}
              onChange={handleChange}
              className="w-full border rounded px-3 py-2 text-black"
            />
          </div>
        </div>

        <div className="flex justify-end space-x-2 pt-4 border-t">
          <button
            onClick={onClose}
            className="bg-gray-100 hover:bg-gray-200 text-gray-800 px-4 py-2 rounded"
          >
            Hủy
          </button>
          <button
            onClick={handleSubmit}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
          >
            Lưu
          </button>
        </div>
      </div>
    </div>
  );
};

export default ModalAddVoucherDetail;