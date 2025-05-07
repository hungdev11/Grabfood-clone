"use client";

import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams } from "next/navigation";
import { Food, AdditionalItem } from "../types/Types";

export default function MenuManagement() {
  const params = useParams();
  const restaurantId = params?.restaurantId as string;

  const [foods, setFoods] = useState<Food[]>([]);
  const [types, setTypes] = useState<string[]>([]);
  const [activeType, setActiveType] = useState<string>("");
  const [selectedFood, setSelectedFood] = useState<Food | null>(null);
  const [loading, setLoading] = useState(true);
  const [editData, setEditData] = useState<Partial<Food>>({});

  // Define the type for additional items
  const [additionalItems, setAdditionalItems] = useState<AdditionalItem[]>([]);
  const [isLoadingAdditionalItems, setIsLoadingAdditionalItems] = useState(false);

  useEffect(() => {
    const fetchAdditionalItems = async () => {
      if (!selectedFood) return;

      setIsLoadingAdditionalItems(true);
      try {
        const response = await axios.get(`http://localhost:6969/grab/foods/additional`, {
          params: {
            page: 0,
            pageSize: 10, 
            restaurantId: restaurantId,
            isForCustomer: false,
          },
        });
        setAdditionalItems(response.data.data.items || []);
      } catch (error) {
        console.error("Lỗi khi lấy món ăn phụ:", error);
      } finally {
        setIsLoadingAdditionalItems(false);
      }
    };

    fetchAdditionalItems();
  }, [restaurantId, selectedFood]); // Run when selectedFood changes

  const [newFoodData, setNewFoodData] = useState({
    name: "",
    image: "",
    description: "",
    kind: "MAIN",
    type: "Bánh mì",
    price: 0,
  });

  const [isAddFoodModalOpen, setIsAddFoodModalOpen] = useState(false);

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
  };

  const closePopup = () => {
    setSelectedFood(null);
  };

  const handleSave = async () => {
    if (!selectedFood) return;

    const payload = {
      name: editData.name || undefined,
      image: editData.image || undefined,
      foodType: editData.type || undefined,
      foodKind: editData.kind || undefined, 
      description: editData.description || undefined,
      status: editData.status || undefined, 
      oldPrice: selectedFood.price, 
      newPrice: typeof editData.price === 'number' ? editData.price : selectedFood.price, 
      additionalIds: Array.isArray(editData.additionalIds) ? editData.additionalIds : [], 
    };

    console.log("Payload to save:", payload); 
    try {
      await axios.put(
        `http://localhost:6969/grab/foods/info/${selectedFood.id}?restaurantId=${restaurantId}`,
        payload
      );

      alert("Cập nhật món ăn thành công!");
      setSelectedFood(null);

      const response = await axios.get(`http://localhost:6969/grab/foods/restaurant/${restaurantId}`);
      setFoods(response.data.data.foods || []);
    } catch (error) {
      console.error("Lỗi khi lưu món ăn:", error);
      alert("Cập nhật thất bại.");
    }
  };

  // Define the function for handling the change of additional items
  const handleAdditionalChange = (additionalId: string) => {
    if (editData.additionalIds?.includes(additionalId)) {
      setEditData({
        ...editData,
        additionalIds: editData.additionalIds.filter((id) => id !== additionalId),
      });
    } else {
      setEditData({
        ...editData,
        additionalIds: [...(editData.additionalIds || []), additionalId],
      });
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

  const handleAddNewFood = async () => {
    try {
      const response = await axios.post("http://localhost:6969/grab/foods", {
        ...newFoodData,
        restaurantId: Number(restaurantId),
      });

      if (response.data.code === 202) {
        alert("Thêm món ăn thành công!");
        setIsAddFoodModalOpen(false);
        const fetchFoods = await axios.get(`http://localhost:6969/grab/foods/restaurant/${restaurantId}`);
        setFoods(fetchFoods.data.data.foods || []);
      } else {
        alert("Có lỗi xảy ra khi thêm món ăn.");
      }
    } catch (error) {
      console.error("Lỗi khi thêm món ăn:", error);
      alert("Thêm món ăn thất bại.");
    }
  };

  const filteredFoods = activeType ? foods.filter((food) => food.type === activeType) : foods;

  if (loading) return <p>Đang tải menu...</p>;

  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold">Quản lý Menu</h2>
        <button 
          onClick={() => setIsAddFoodModalOpen(true)} 
          className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">
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
              <p className={`absolute top-0 right-0 text-sm ${food.status === 'ACTIVE' ? 'text-green-500' : 'text-red-500'}`}>
                {food.status}
              </p>
            </div>
          </div>
        ))}
      </div>

      {/* Detail Modal */}
      {selectedFood && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-lg">
            <h3 className="text-xl font-semibold mb-4">Chi tiết món ăn</h3>
            <div className="space-y-2">
              <input
                type="text"
                value={editData.name || ""}
                onChange={(e) => setEditData({ ...editData, name: e.target.value })}
                className="w-full border px-3 py-2 rounded"
                placeholder="Tên món ăn"
              />
              <input
                type="text"
                value={editData.image || ""}
                onChange={(e) => setEditData({ ...editData, image: e.target.value })}
                className="w-full border px-3 py-2 rounded"
                placeholder="URL ảnh"
              />
              <textarea
                value={editData.description || ""}
                onChange={(e) => setEditData({ ...editData, description: e.target.value })}
                className="w-full border px-3 py-2 rounded"
                placeholder="Mô tả"
              />
              <input
                type="number"
                value={editData.price || 0}
                onChange={(e) => setEditData({ ...editData, price: Number(e.target.value) })}
                className="w-full border px-3 py-2 rounded"
                placeholder="Giá"
              />
            </div>

            {/* Chọn món phụ */}
            <div className="mt-4">
              <label className="block text-sm font-medium">Chọn món ăn phụ</label>
              {isLoadingAdditionalItems ? (
                <p>Đang tải món phụ...</p>
              ) : (
                <div className="space-y-2">
                  {additionalItems.map((item) => (
                    <label key={item.id} className="flex items-center">
                      <input
                        type="checkbox"
                        checked={editData.additionalIds?.includes(item.id)} // Kiểm tra món này đã được chọn chưa
                        onChange={() => handleAdditionalChange(item.id)} // Hàm xử lý khi chọn món phụ
                        className="mr-2"
                      />
                      {item.name}
                    </label>
                  ))}
                </div>
              )}
            </div>

            <div className="flex justify-end space-x-2 mt-4">
              <button onClick={handleSave} className="bg-blue-600 text-white px-4 py-2 rounded">
                Lưu thay đổi
              </button>
              <button onClick={handleDelete} className="bg-red-600 text-white px-4 py-2 rounded">
                Xóa món ăn
              </button>
              <button onClick={closePopup} className="bg-gray-500 text-white px-4 py-2 rounded">
                Đóng
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
