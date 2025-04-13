// PickAdditionalFood.tsx
import React from "react";
import { Food } from "./types/Types";

interface PickAdditionalFoodProps {
  additionalFoods: Food[];
  onCheckboxChange: (foodId: number, price: number) => void;
  onSpecialInstructionsChange: (note: string) => void; // Callback để thay đổi note
}

const PickAdditionalFood: React.FC<PickAdditionalFoodProps> = ({
  additionalFoods,
  onCheckboxChange,
  onSpecialInstructionsChange, // Nhận callback từ Popup
}) => {
  return (
    <div className="p-4">
        {/* List of additional foods */}
        {additionalFoods.map((food) => (
          <div key={food.id} className="flex justify-between items-center p-2 border-b border-gray-300">
            <div className="flex items-center gap-2">
              <input
                type="checkbox"
                id={`food-${food.id}`}
                onChange={() => onCheckboxChange(food.id, food.price)}
                className="h-5 w-5"
              />
              <label htmlFor={`food-${food.id}`} className="text-lg">{food.name}</label>
            </div>

            {/* Food Price */}
            <span className="text-lg font-bold">{food.price.toLocaleString()}đ</span>
          </div>
        ))}

        {/* Special Instructions */}
        <div className="pt-4">
          <label htmlFor="special-instructions" className="block text-lg font-semibold">Special Instructions (Optional)</label>
          <textarea
            id="special-instructions"
            onChange={(e) => onSpecialInstructionsChange(e.target.value)} // Gọi callback để cập nhật note
            className="w-full h-32 p-2 mt-2 border border-gray-300 rounded-md"
            placeholder="Enter any special instructions here..."
          />
        </div>
      </div>
  );
};

export default PickAdditionalFood;
