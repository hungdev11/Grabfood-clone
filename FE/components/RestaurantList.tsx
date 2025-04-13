"use client";

import React from 'react';
import { RestaurantHome } from './types/Types';

interface Props {
  restaurants: RestaurantHome[];
}

const RestaurantList: React.FC<Props> = ({ restaurants }) => {

  const handleClick = (restaurantId: number) => {
    // Điều hướng bằng link cố định (reload trang)
    window.location.href = `/restaurant/${restaurantId}`;
  };

  return (
    <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
      {restaurants.map((restaurant) => (
        <div
          key={restaurant.id}
          className="rounded-lg border border-gray-200 overflow-hidden cursor-pointer"
          onClick={() => handleClick(restaurant.id)}
        >
          <div className="relative h-40">
            <img
              src={restaurant.image || "/placeholder.svg"}
              alt={restaurant.name}
              className="h-full w-full object-cover"
            />
          </div>
          <div className="p-3">
            <h3 className="font-bold">{restaurant.name}</h3>
            <p className="text-xs text-gray-500">
              {restaurant.description || ""}
            </p>
            <div className="mt-2 flex items-center gap-1">
              ⭐ <span className="text-xs">{restaurant.rating}</span>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default RestaurantList;
