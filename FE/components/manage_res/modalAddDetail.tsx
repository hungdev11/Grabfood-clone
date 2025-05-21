'use client';

import React, { useEffect, useState } from 'react';
import { Food } from '../types/Types';

interface AddVoucherDetailRequestRes {
  startDate: string;
  endDate: string;
  voucher_id: number;
  foodIds: number[];
}

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: AddVoucherDetailRequestRes) => void;
  voucherId?: number;
  applyType?: string;
  foodIds?: number[];
  restaurantId: number;
}

const ModalAddDetail: React.FC<ModalProps> = ({ isOpen, onClose, onSubmit, voucherId, applyType, foodIds, restaurantId }) => {
  const [formData, setFormData] = useState<AddVoucherDetailRequestRes>({
    startDate: '',
    endDate: '',
    voucher_id: voucherId || 0,
    foodIds: foodIds || [],
  });

  const [foods, setFoods] = useState<Food[]>([]);

  const getFoods = async () => {
    try {
      const response = await fetch(`http://localhost:6969/grab/foods/all/restaurant/${restaurantId}`);
      const data = await response.json();
      setFoods(data.data || []);
    } catch (error) {
      console.error('Error fetching foods:', error);
    }
  };

  useEffect(() => {
    if (applyType === 'SPECIFIC') {
      getFoods();
    }
  }, [applyType]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'voucher_id' ? parseInt(value) || 0 : value,
    }));
  };

  const formatDateTime = (dateTime: string): string => {
    if (!dateTime) return '';
    const date = new Date(dateTime);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = '00';
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (applyType === 'SPECIFIC' && (!formData.foodIds || formData.foodIds.length === 0)) {
      alert('Vui lòng chọn ít nhất một món ăn áp dụng.');
      return;
    }

    if (!formData.startDate || !formData.endDate) {
      alert('Vui lòng chọn thời gian bắt đầu và kết thúc.');
      return;
    }

    const now = new Date();
    const start = new Date(formData.startDate);
    const end = new Date(formData.endDate);

    if (now >= start) {
      alert('Ngày bắt đầu phải sau thời điểm hiện tại.');
      return;
    }

    if (start >= end) {
      alert('Ngày kết thúc phải sau ngày bắt đầu.');
      return;
    }

    const formattedData = {
      ...formData,
      startDate: formatDateTime(formData.startDate),
      endDate: formatDateTime(formData.endDate),
    };

    onSubmit(formattedData);
    onClose();
  };



  if (!isOpen) return null;

  const filteredFoods = foods;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-2xl">
        <h2 className="text-2xl font-bold mb-4">Chọn thời gian hiệu lực</h2>
        <form onSubmit={handleSubmit}>
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

          {applyType === 'SPECIFIC' && (
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">Chọn món ăn áp dụng</label>
              <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                {filteredFoods.map((food) => {
                  const isChecked = formData.foodIds?.includes(food.id) || false;
                  return (
                    <label
                      key={food.id}
                      className={`flex items-start space-x-3 border rounded-xl p-3 cursor-pointer transition
                      ${isChecked ? 'bg-indigo-50 border-indigo-400' : 'hover:bg-gray-50'}`}
                    >
                      <input
                        type="checkbox"
                        value={food.id}
                        checked={isChecked}
                        onChange={(e) => {
                          const checked = e.target.checked;
                          setFormData((prev) => ({
                            ...prev,
                            foodIds: checked
                              ? [...(prev.foodIds || []), food.id]
                              : prev.foodIds?.filter((id) => id !== food.id) || [],
                          }));
                        }}
                        className="mt-1"
                      />
                      <div>
                        <div className="font-semibold text-gray-800">{food.name}</div>
                        <div className="text-sm text-gray-500">{food.price.toLocaleString()} đ</div>
                      </div>
                    </label>
                  );
                })}
              </div>
            </div>
          )}

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
