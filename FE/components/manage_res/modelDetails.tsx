import React from 'react';
import { Voucher } from '../types/voucher';
import { Food } from '../types/Types';

interface ModalDetailsProps {
  isOpen: boolean;
  onClose: () => void;
  voucher: Voucher;
  foods: Food[];
}

function ModalDetails({ isOpen, onClose, voucher, foods }: ModalDetailsProps) {
  if (!isOpen) return null;

  // Lọc món ăn được chọn trong voucher
  const appliedFoods = foods.filter((food) => voucher.foodIds.includes(food.id));
    console.log("appliedFoods", appliedFoods);
  console.log("foods", foods);
  console.log("foodIds", voucher.foodIds);
  console.log("voucher", voucher);
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
      <div className="bg-white p-6 rounded-lg max-w-lg w-full max-h-[80vh] overflow-auto shadow-lg">
        <h2 className="text-xl font-bold mb-4">Chi tiết Voucher: {voucher.code}</h2>
        <p>Loại áp dụng: {voucher.applyType === "ALL" ? "Tất cả món" : "Chọn món"}</p>

        {voucher.applyType === "SPECIFIC" && (
          <>
            <h3 className="mt-4 mb-2 font-semibold">Danh sách món áp dụng:</h3>
            {appliedFoods.length > 0 ? (
              <div className="grid grid-cols-2 gap-4">
                {appliedFoods.map(food => (
                  <div
                    key={food.id}
                    className="border p-3 rounded shadow-sm bg-gray-50"
                  >
                    <div className="font-semibold">{food.name}</div>
                    <div className="text-sm text-gray-600">{food.price.toLocaleString()} đ</div>
                  </div>
                ))}
              </div>
            ) : (
              <p>Không có món ăn nào được chọn.</p>
            )}
          </>
        )}

        <button
          className="mt-6 bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
          onClick={onClose}
        >
          Đóng
        </button>
      </div>
    </div>
  );
}

export default ModalDetails;
