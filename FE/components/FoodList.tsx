"use client";
import React, { useState, useEffect } from "react";
import { Food } from "@/components/types/Types";
import PopupFood from "@/components/PopupFood";
import { Button } from "@/components/ui/button";

interface FoodListProps {
  foods: Food[];
  restaurantId: string;
}

const FoodListComponent: React.FC<FoodListProps> = ({ foods, restaurantId }) => {
  const [isPopupVisible, setIsPopupVisible] = useState(false);
  const [selectedFood, setSelectedFood] = useState<Food | null>(null);
  const [isClient, setIsClient] = useState(false);

  // Ensure code runs only on the client-side
  useEffect(() => {
    setIsClient(true);
  }, []);

  const handleFoodClick = (food: Food) => {
    setSelectedFood(food);
    setIsPopupVisible(true);
  };

  const closePopup = () => {
    setIsPopupVisible(false);
    setSelectedFood(null);
  };

  // Return a loading state during hydration
  if (!isClient) {
    return null;
  }

  return (
    <div className="p-4">
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        {foods.map((food, index) => (
          <div
            key={`${food.id}-${index}`}
            className="flex items-center p-4 border border-gray-200 rounded-lg shadow-sm hover:shadow-md transition cursor-pointer"
            onClick={() => handleFoodClick(food)}
          >
            <img
              src={food.image || "/placeholder.svg"}
              alt={food.name}
              className="w-24 h-24 object-cover rounded-md"
            />
            <div className="ml-4 flex-1">
              <h3 className="text-lg font-bold">{food.name}</h3>
              <p className="text-gray-500 text-sm">{food.description || ""}</p>
              <p className="text-xl font-bold mt-2">{food.price.toLocaleString()}đ</p>
            </div>
            <Button variant="success" size="icon" className="ml-4 text-lg rounded-full">
              +
            </Button>
          </div>
        ))}
      </div>

      {selectedFood && (
        <PopupFood
          selectedFood={selectedFood}
          isVisible={isPopupVisible}
          onClose={closePopup}
          restaurantId={restaurantId} // Truyền restaurantId vào PopupFood
          userId = {Number(localStorage.getItem("grabUserId"))}
          />
      )}
    </div>
  );
};

export default FoodListComponent;
