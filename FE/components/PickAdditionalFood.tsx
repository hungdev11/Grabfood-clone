// PickAdditionalFood.tsx
import React from "react";
import { AdditionalFood } from "./types/Types";

interface PickAdditionalFoodProps {
  additionalFoods: AdditionalFood[];
  onCheckboxChange: (foodId: number, price: number) => void;
  onSpecialInstructionsChange: (note: string) => void;
  selectedItems: { [key: number]: number }; // <-- THÊM
  specialInstructions: string; // <-- THÊM
}

const PickAdditionalFood: React.FC<PickAdditionalFoodProps> = ({
  additionalFoods,
  onCheckboxChange,
  onSpecialInstructionsChange, // Nhận callback từ Popup
  selectedItems,
  specialInstructions,
}) => {
  return (
    <div className="p-4">
        {/* List of additional foods */}
        {additionalFoods.map((food) => {
          const isChecked = selectedItems.hasOwnProperty(food.id);
          return (
            <div key={food.id} className="flex justify-between items-center p-2 border-b border-gray-300">
              <div className="flex items-center gap-2">
                <input
                  type="checkbox"
                  id={`food-${food.id}`}
                  checked={isChecked}
                  onChange={() => onCheckboxChange(food.id, food.discountPrice)}
                  className="h-5 w-5"
                />
                <label htmlFor={`food-${food.id}`} className="text-lg">{food.name}</label>
              </div>
              <span className="text-lg font-bold">{food.discountPrice.toLocaleString()}đ</span>
            </div>
          );
        })}


        {/* Special Instructions */}
        <div className="pt-4">
          <label htmlFor="special-instructions" className="block text-lg font-semibold">Special Instructions (Optional)</label>
          <textarea
            id="special-instructions"
            value={specialInstructions} // <-- Liên kết với state
            onChange={(e) => onSpecialInstructionsChange(e.target.value)}
            className="w-full h-32 p-2 mt-2 border border-gray-300 rounded-md"
            placeholder="Enter any special instructions here..."
          />

        </div>
      </div>
  );
};

export default PickAdditionalFood;
