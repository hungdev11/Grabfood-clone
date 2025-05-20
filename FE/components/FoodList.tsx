"use client";
import React, { useState, useEffect } from "react";
import { Food } from "@/components/types/Types";
import PopupFood from "@/components/PopupFood";
import { Button } from "@/components/ui/button";
import FoodSearch from "@/components/FoodSearch";

interface Props {
  types: string[];
  foods: Food[];
  restaurantId: string;
  isOpen: boolean;
}

const FoodListComponent: React.FC<Props> = ({
  types,
  foods,
  restaurantId,
  isOpen,
}) => {
  const [isPopupVisible, setIsPopupVisible] = useState(false);
  const [selectedFood, setSelectedFood] = useState<Food | null>(null);
  const [isClient, setIsClient] = useState(false);
  const [activeType, setActiveType] = useState(types[0] || "");
  const [searchResults, setSearchResults] = useState<Food[]>([]);
  const [isSearching, setIsSearching] = useState(false);

  useEffect(() => {
    setIsClient(true);
  }, []);

  if (!isClient) return null;

  const handleFoodClick = (food: Food) => {
    setSelectedFood(food);
    setIsPopupVisible(true);
  };

  const closePopup = () => {
    setIsPopupVisible(false);
    setSelectedFood(null);
  };

  const handleSearchResults = (results: Food[]) => {
    setSearchResults(results);
    setIsSearching(results.length > 0);
  };

  const filteredFoods = isSearching
    ? searchResults
    : foods.filter((food) => food.type === activeType);
  return (
    <div className="p-4">
      {/* Search bar */}
      <div className="mb-6">
        <FoodSearch
          restaurantId={restaurantId}
          onResults={handleSearchResults}
          placeholder="Tìm kiếm món ăn thuộc nhà hàng..."
          className="w-full md:w-[350px]"
        />
      </div>
      {/* Tabs loại món */}
      <div
        className={`border-b mb-4 flex space-x-4 overflow-x-auto ${
          isSearching ? "opacity-50" : ""
        }`}
      >
        {types.map((type) => (
          <button
            key={type}
            onClick={() => {
              setActiveType(type);
              setIsSearching(false);
            }}
            className={`px-4 py-2 whitespace-nowrap ${
              activeType === type && !isSearching
                ? "border-b-2 border-green-600 text-green-600 font-semibold"
                : "text-gray-500"
            }`}
          >
            {type}
          </button>
        ))}
      </div>{" "}
      {!isOpen && (
        <div className="text-red-500 font-semibold mb-2 text-center">
          Nhà hàng hiện đang đóng cửa. Vui lòng quay lại sau.
        </div>
      )}
      {/* Danh sách món ăn theo loại */}
      {isSearching && searchResults.length === 0 ? (
        <div className="text-center py-8 text-gray-500">
          No dishes found matching your search. Try different keywords.
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
          {filteredFoods.map((food) => (
            <div
              key={food.id}
              className="flex items-center p-4 border border-gray-200 rounded-lg shadow-sm hover:shadow-md transition cursor-pointer"
              onClick={() => {
                if (isOpen) handleFoodClick(food);
              }}
            >
              <img
                src={food.image || "/placeholder.svg"}
                alt={food.name}
                className="w-24 h-24 object-cover rounded-md"
              />
              <div className="ml-4 flex-1">
                <h3 className="text-lg font-bold">{food.name}</h3>
                <p className="text-gray-500 text-sm">
                  {food.description || ""}
                </p>
                <p className="text-xl font-bold mt-2">
                  {typeof food.discountPrice === "number" &&
                  food.discountPrice < food.price ? (
                    <>
                      <span className="line-through text-gray-500 mr-2">
                        {food.price.toLocaleString()}đ
                      </span>
                      <span className="text-red-500">
                        {food.discountPrice.toLocaleString()}đ
                      </span>
                    </>
                  ) : (
                    <span>{food.price.toLocaleString()}đ</span>
                  )}
                </p>
              </div>
              <Button
                variant="success"
                size="icon"
                className="ml-4 text-lg rounded-full"
                disabled={!isOpen}
              >
                +
              </Button>{" "}
            </div>
          ))}
        </div>
      )}
      {/* Popup chi tiết món ăn */}
      {selectedFood && (
        <PopupFood
          selectedFood={selectedFood}
          isVisible={isPopupVisible}
          onClose={closePopup}
          restaurantId={restaurantId}
          userId={Number(localStorage.getItem("grabUserId"))}
        />
      )}
    </div>
  );
};

export default FoodListComponent;
