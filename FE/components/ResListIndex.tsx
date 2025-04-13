"use client"; // Thêm dòng này vào đầu file

import React, { useEffect, useState } from 'react';
import RestaurantList from './RestaurantList'; // Import component RestaurantList
import { ApiResponse, PageResponse, Restaurant } from './types/Types';
import { fetchWithAuth } from '@/utils/api';

const IndexPage = () => {
  const [restaurants, setRestaurants] = useState<Restaurant[]>([]);

  useEffect(() => {
    const fetchRestaurants = async () => {
      try {
        const res = await fetch(
          "http://localhost:6969/grab/restaurants?sortBy=name&page=0&pageSize=20"
        );

        if (!res.ok) {
          throw new Error("Failed to fetch data");
        }

        const data: ApiResponse<PageResponse<Restaurant>> = await res.json();

        if (data && data.data && data.data.items) {
          setRestaurants(data.data.items); // Gán dữ liệu vào state
        } else {
          setRestaurants([]); // Nếu không có dữ liệu, gán mảng rỗng
        }
      } catch (error) {
        console.error("Error fetching data:", error);
        setRestaurants([]); // Trường hợp có lỗi, gán mảng rỗng
      }
    };

    fetchRestaurants();
  }, []); // Gọi API 1 lần khi trang được mount

  return (
    <div>
      <RestaurantList restaurants={restaurants} /> {/* Truyền props restaurants */}
    </div>
  );
};

export default IndexPage;
