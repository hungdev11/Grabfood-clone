"use client";

import React, { useEffect, useState } from 'react';
import RestaurantList from './RestaurantList';
import { ApiResponse, PageResponse, RestaurantHome } from './types/Types';
import {Location} from './types/Types';

type Props = {
  restaurants?: RestaurantHome[];
  location: Location;
};

const IndexPage: React.FC<Props> = ({ restaurants: propsRestaurants, location: propsLocation}) => {
  const [restaurants, setRestaurants] = useState<RestaurantHome[]>([]);

  useEffect(() => {
    // Nếu propsRestaurants có giá trị thì dùng luôn, không cần gọi API nữa
    if (propsRestaurants && propsRestaurants.length > 0) {
      setRestaurants(propsRestaurants);
      console.log("Using propsRestaurants:", propsRestaurants);
      return;
    }

    // Nếu không có propsRestaurants, fetch từ API
    const fetchRestaurants = async () => {
      try {
        const res = await fetch(
          "http://localhost:6969/grab/restaurants?sortBy=name&page=0&pageSize=20"
        );

        if (!res.ok) {
          throw new Error("Failed to fetch data");
        }

        const data: ApiResponse<PageResponse<RestaurantHome>> = await res.json();

        if (data && data.data && data.data.items) {
          setRestaurants(data.data.items);
        } else {
          setRestaurants([]); // Không có dữ liệu trả về
        }
      } catch (error) {
        console.error("Error fetching data:", error);
        setRestaurants([]); // Trong trường hợp có lỗi
      }
    };

    fetchRestaurants(); // Gọi API nếu không có dữ liệu từ props
  }, [propsRestaurants]); // useEffect chỉ chạy lại khi propsRestaurants thay đổi

  return (
    <div>
      <RestaurantList restaurants={restaurants} location={propsLocation} />
    </div>
  );
};

export default IndexPage;
