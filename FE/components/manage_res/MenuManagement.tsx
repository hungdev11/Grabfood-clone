"use client";

import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams } from "next/navigation";
import { Food } from "../types/Types";

export default function MenuManagement() {
  const params = useParams();
  const restaurantId = params?.restaurantId as string;

  const [foods, setFoods] = useState<Food[]>([]);
  const [types, setTypes] = useState<string[]>([]);
  const [activeType, setActiveType] = useState<string>("");
  const [selectedFood, setSelectedFood] = useState<Food | null>(null);
  const [loading, setLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const [editData, setEditData] = useState<Partial<Food>>({});

  if (!restaurantId) {
    return <div>Restaurant ID not found</div>;
  }

  useEffect(() => {
    const fetchFoods = async () => {
      try {
        const response = await axios.get(`http://localhost:6969/grab/foods/restaurant/${restaurantId}`);
        const fetchedFoods = response.data.data.foods || [];
        setFoods(fetchedFoods);
        const fetchedTypes = response.data.data.types || [];
        setTypes(fetchedTypes);
      } catch (error) {
        console.error("Lỗi khi lấy món ăn:", error);
      } finally {
        setLoading(false);
      }
    };

    if (restaurantId) {
      fetchFoods();
    }
  }, [restaurantId]);

  const handleFoodClick = (food: Food) => {
    setSelectedFood(food);
    setEditData(food);
    setIsEditing(false);
  };

  const closePopup = () => {
    setSelectedFood(null);
    setIsEditing(false);
  };

  const handleSave = async () => {
    try {
      await axios.put(`http://localhost:6969/grab/foods/${selectedFood?.id}`, editData);
      alert("Cập nhật món ăn thành công!");
      setSelectedFood(null);
      // Reload
      const response = await axios.get(`http://localhost:6969/grab/foods/restaurant/${restaurantId}`);
      setFoods(response.data.data.foods || []);
    } catch (error) {
      console.error("Lỗi khi lưu món ăn:", error);
      alert("Cập nhật thất bại.");
    }
  };

  const handleDelete = async () => {
    if (!confirm("Bạn có chắc muốn xóa món này?")) return;
    try {
      await axios.delete(`http://localhost:6969/grab/foods/${selectedFood?.id}`);
      alert("Đã xóa món ăn!");
      setSelectedFood(null);
      const response = await axios.get(`http://localhost:6969/grab/foods/restaurant/${restaurantId}`);
      setFoods(response.data.data.foods || []);
    } catch (error) {
      console.error("Lỗi khi xóa món ăn:", error);
      alert("Xóa thất bại.");
    }
  };

  const filteredFoods = activeType ? foods.filter((food) => food.type === activeType) : foods;

  if (loading) return <p>Đang tải menu...</p>;

  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold">Quản lý Menu</h2>
        <button className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">
          + Thêm món ăn
        </button>
      </div>

      <div className="border-b mb-4 flex space-x-4 overflow-x-auto">
        {types.map((type) => (
          <button
            key={type}
            onClick={() => setActiveType(type)}
            className={`px-4 py-2 whitespace-nowrap ${
              activeType === type ? "border-b-2 border-green-600 text-green-600 font-semibold" : "text-gray-500"
            }`}
          >
            {type}
          </button>
        ))}
      </div>
      
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
        {filteredFoods.map((food) => (
          <div key={food.id} className="flex items-center p-4 border border-gray-200 rounded-lg shadow-sm hover:shadow-md transition">
            <img src={food.image || "/placeholder.svg"} alt={food.name} className="w-24 h-24 object-cover rounded-md" />
            <div className="ml-4 flex-1">
              <h3 className="text-lg font-bold">{food.name}</h3>
              <p className="text-gray-500 text-sm">{food.description || ""}</p>
              <p className="text-xl font-bold mt-2">
                {typeof food.discountPrice === "number" && food.discountPrice < food.price ? (
                  <>
                    <span className="line-through text-gray-500 mr-2">{food.price.toLocaleString()}đ</span>
                    <span className="text-red-500">{food.discountPrice.toLocaleString()}đ</span>
                  </>
                ) : (
                  <span>{food.price.toLocaleString()}đ</span>
                )}
              </p>
              <button onClick={() => handleFoodClick(food)} className="text-blue-500 hover:underline mt-2">
                Xem chi tiết
              </button>
            </div>
            <div className="ml-4 flex-1 relative">
              <p
                className={`absolute top-0 right-0 text-sm ${
                  food.status === 'ACTIVE' ? 'text-green-500' : 'text-red-500'
                }`}
              >
                {food.status}
              </p>
            </div>
          </div>
        ))}
      </div>

      {/* Popup chi tiết món ăn */}
      {selectedFood && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-lg">
            <h3 className="text-xl font-semibold mb-4">Chi tiết món ăn</h3>
            <div className="space-y-2">
              <input
                type="text"
                value={editData.name || ""}
                disabled={!isEditing}
                onChange={(e) => setEditData({ ...editData, name: e.target.value })}
                className="w-full border px-3 py-2 rounded"
                placeholder="Tên món"
              />
              <input
                type="number"
                value={editData.price || 0}
                disabled={!isEditing}
                onChange={(e) => setEditData({ ...editData, price: Number(e.target.value) })}
                className="w-full border px-3 py-2 rounded"
                placeholder="Giá"
              />
              <textarea
                value={editData.description || ""}
                disabled={!isEditing}
                onChange={(e) => setEditData({ ...editData, description: e.target.value })}
                className="w-full border px-3 py-2 rounded"
                placeholder="Mô tả"
              />
            </div>
            <div className="flex justify-end space-x-2 mt-4">
              {isEditing ? (
                <>
                  <button onClick={handleSave} className="bg-green-600 text-white px-4 py-2 rounded">
                    Lưu
                  </button>
                  <button onClick={() => setIsEditing(false)} className="bg-gray-300 px-4 py-2 rounded">
                    Hủy
                  </button>
                </>
              ) : (
                <>
                  <button onClick={() => setIsEditing(true)} className="bg-blue-600 text-white px-4 py-2 rounded">
                    Sửa
                  </button>
                  <button onClick={handleDelete} className="bg-red-600 text-white px-4 py-2 rounded">
                    Xóa
                  </button>
                  <button onClick={closePopup} className="bg-gray-300 px-4 py-2 rounded">
                    Đóng
                  </button>
                </>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
