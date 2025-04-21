"use client"; // Đảm bảo thêm dòng này vào đầu file

import React, { useEffect, useState } from "react";
import { useRouter } from "next/router";
import { fetchWithAuth } from "@/utils/api";
import { Restaurant } from "./types/Types";

const RestaurantPage = () => {
  const router = useRouter();
  const { id } = router.query; // Lấy id từ URL query
  const [restaurant, setRestaurant] = useState<Restaurant | null>(null);

  useEffect(() => {
    if (id) {
      // Fetch dữ liệu chi tiết của nhà hàng khi id có sẵn
      const fetchRestaurantDetails = async () => {
        try {
          const res = await fetchWithAuth(
            `http://localhost:6969/grab/restaurants/${id}`
          );
          const data = await res.json();
          setRestaurant(data);
        } catch (error) {
          console.error("Error fetching restaurant details:", error);
        }
      };

      fetchRestaurantDetails();
    }
  }, [id]); // Gọi lại khi ID thay đổi

  if (!restaurant) {
    return <div>Loading...</div>;
  }

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold">{restaurant.name}</h1>
      <img
        src={restaurant.image}
        alt={restaurant.name}
        className="w-full h-64 object-cover mt-4"
      />
      <p className="mt-4">{restaurant.description}</p>
      <div className="mt-4">
        <span>⭐ {restaurant.rating}</span>
      </div>
    </div>
  );
};

export default RestaurantPage;
