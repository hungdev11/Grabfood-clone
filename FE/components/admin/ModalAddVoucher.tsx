'use client';

import React, { useState } from 'react';
import { X } from 'lucide-react';
import { AddAdminVoucherRequest } from '../types/voucher';

type Props = {
  isOpen: boolean;
  onClose: () => void;
 onSubmit: (data: AddAdminVoucherRequest) => void;
};

const ModalAddVoucher: React.FC<Props> = ({ isOpen, onClose, onSubmit }) => {
  const [formData, setFormData] = useState<AddAdminVoucherRequest>({
    code: '',
    description: '',
    minRequire: 0,
    type: 'FIXED',
    value: 0,
    applyType: 'ORDER',
    status: 'ACTIVE',
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'minRequire' || name === 'value' ? Number(value) : value,
    }));
  };

  const handleSubmit = () => {
    onSubmit(formData);
    onClose();
  };

if (!isOpen) return null;

return (
  <div className="fixed inset-0 z-40 flex items-center justify-center bg-black bg-opacity-40">
    <div className="bg-white text-black rounded-xl w-full max-w-md p-6 shadow-lg relative z-50 animate-fade-in">
      <button
        onClick={onClose}
        className="absolute top-4 right-4 text-gray-500 hover:text-black"
      >
        <X />
      </button>

      <h2 className="text-xl font-bold mb-4">Thêm Mã Giảm Giá</h2>

      <div className="space-y-4">
        <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Mã CODE</label>
            <input
            name="code"
            placeholder="Mã giảm giá"
            value={formData.code}
            onChange={handleChange}
            className="w-full border rounded px-3 py-2 text-black"
            />
        </div>

        <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Mô tả</label>
            <input
            name="description"
            placeholder="Mô tả"
            value={formData.description}
            onChange={handleChange}
            className="w-full border rounded px-3 py-2 text-black"
            />
        </div>

        <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Giá trị tối thiểu (VND)</label>
            <input
            name="minRequire"
            placeholder="Số tiền tối thiểu"
            type="number"
            value={formData.minRequire}
            onChange={handleChange}
            className="w-full border rounded px-3 py-2 text-black"
            />
        </div>

        <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Loại giảm giá</label>
            <select
            name="type"
            value={formData.type}
            onChange={handleChange}
            className="w-full border rounded px-3 py-2 text-black"
            >
            <option value="FIXED">Cố định (VND)</option>
            <option value="PERCENTAGE">Phần trăm (%)</option>
            </select>
        </div>

        <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Giá trị giảm</label>
            <input
            name="value"
            placeholder="Giá trị giảm"
            type="number"
            value={formData.value}
            onChange={handleChange}
            className="w-full border rounded px-3 py-2 text-black"
            />
        </div>

        <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Áp dụng cho</label>
            <select
            name="applyType"
            value={formData.applyType}
            onChange={handleChange}
            className="w-full border rounded px-3 py-2 text-black"
            >
            <option value="ORDER">Đơn hàng</option>
            <option value="SHIPPING">Vận chuyển</option>
            </select>
        </div>
        </div>


      <div className="flex justify-end mt-6 space-x-2">
        <button onClick={onClose} className="px-4 py-2 rounded bg-gray-200 hover:bg-gray-300 text-black">Huỷ</button>
        <button onClick={handleSubmit} className="px-4 py-2 rounded bg-green-500 text-white hover:bg-green-400">Lưu</button>
      </div>
    </div>
  </div>
);
};

export default ModalAddVoucher;
