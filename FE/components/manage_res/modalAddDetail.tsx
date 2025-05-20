'use client';

import React, { useState } from 'react';

interface AddVoucherDetailRequest {
  quantity: number;
  startDate: string;
  endDate: string;
  voucher_id: number;
}

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: AddVoucherDetailRequest) => void;
  voucherId?: number;
}

const ModalAddDetail: React.FC<ModalProps> = ({voucherId, isOpen, onClose, onSubmit }) => {
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
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-md">
        <h2 className="text-2xl font-bold mb-4">Thêm chi tiết Voucher</h2>
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="quantity" className="block text-sm font-medium text-gray-700">
              Số lượng
            </label>
            <input
              type="number"
              id="quantity"
              name="quantity"
              value={formData.quantity}
              onChange={handleChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              required
            />
          </div>
          <div className="mb-4">
            <label htmlFor="startDate" className="block text-sm font-medium text-gray-700">
              Ngày bắt đầu
            </label>
            <input
              type="datetime-local"
              id="startDate"
              name="startDate"
              value={formData.startDate}
              onChange={handleChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              required
            />
          </div>
          <div className="mb-4">
            <label htmlFor="endDate" className="block text-sm font-medium text-gray-700">
              Ngày kết thúc
            </label>
            <input
              type="datetime-local"
              id="endDate"
              name="endDate"
              value={formData.endDate}
              onChange={handleChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              required
            />
          </div>
          <div className="flex justify-end space-x-2">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300"
            >
              Hủy
            </button>
            <button
              type="submit"
              className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700"
            >
              Gửi
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ModalAddDetail;