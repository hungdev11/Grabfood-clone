"use client";

import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams } from "next/navigation";
import { Food, AdditionalFood } from "../types/Types";
import axiosInstance from "@/utils/axiosInstance";

export default function MenuManagement() {
  const kinds = ["MAIN", "ADDITIONAL", "BOTH"];
  const kindsMap = {
    "MAIN": "Món chính",
    "ADDITIONAL": "Món phụ",
    "BOTH": "Linh hoạt",
  };
  const params = useParams();
  const restaurantId = params?.restaurantId as string;
  const [typeList, setTypeList] = useState<string[]>([]);
  const [foods, setFoods] = useState<Food[]>([]);
  const [types, setTypes] = useState<string[]>([]);
  const [activeType, setActiveType] = useState<string>("");
  const [selectedFood, setSelectedFood] = useState<Food | null>(null);
  const [loading, setLoading] = useState(true);
  const [editData, setEditData] = useState<Partial<Food>>({});

  // Define the type for additional items
  const [additionalItems, setAdditionalItems] = useState<AdditionalFood[]>([]);
  const [isLoadingAdditionalItems, setIsLoadingAdditionalItems] = useState(false);

  const [searchTerm, setSearchTerm] = useState("");

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
    kind: "",
    type: "",
    price: 0,
  });

  const [isAddFoodModalOpen, setIsAddFoodModalOpen] = useState(false);

  if (!restaurantId) {
    return <div>Restaurant ID not found</div>;
  }

  useEffect(() => {
    loadTypeList();
  }, []);

  const fetchFoods = async () => {
    try {
      const response = await axios.get(`http://localhost:6969/grab/foods/restaurant/${restaurantId}`);
      const response1 = await axios.get(`http://localhost:6969/grab/foods/additional?restaurantId=${restaurantId}`);

      const fetchedFoods = response.data.data.foods || [];
      const additionalFoods = (response1.data.data.items || []).filter(
        (item: Food) => item.kind === "ADDITIONAL"
      );

      const combinedFoods = [...fetchedFoods, ...additionalFoods];
      setFoods(combinedFoods);

      const fetchedTypes = response.data.data.types || [];
      setTypes(fetchedTypes);
    } catch (error) {
      console.error("Lỗi khi lấy món ăn:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (restaurantId) {
      fetchFoods();
    }
  }, [restaurantId]);

  const handleFoodClick = async (food: Food) => {
    try {
      const selectedAdditionalResponse = await axios.get(
        `http://localhost:6969/grab/foods/additional/${food.id}?restaurantId=${restaurantId}&isForCustomer=false`
      );

      let selectedAdditionalItems: Food[] = [];
      let selectedAdditionalIds: number[] = [];

      if (food.kind !== "ADDITIONAL") {
        selectedAdditionalItems = selectedAdditionalResponse.data.data.items || [];
        selectedAdditionalIds = selectedAdditionalItems.map(item => item.id);
      }

      setEditData({
        ...food,
        additionalFoods: selectedAdditionalItems,
        additionalIds: selectedAdditionalIds,
      });

      setSelectedFood(food);
    } catch (error) {
      console.error("Lỗi khi lấy đồ thêm đã gắn:", error);
      setEditData({
        ...food,
        additionalFoods: [],
        additionalIds: [],
      });
      setSelectedFood(food);
    }
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
      additionalIds: editData.additionalIds || [],
    };

    console.log("Payload to save:", payload); 
    try {
      await axiosInstance.put(
        `http://localhost:6969/grab/foods/info/${selectedFood.id}?restaurantId=${restaurantId}`,
        payload
      );

      alert("Cập nhật món ăn thành công!");
      setSelectedFood(null);

      await fetchFoods();
    } catch (error) {
      console.error("Lỗi khi lưu món ăn:", error);
      alert("Cập nhật thất bại.");
    }
  };
  
  const loadTypeList = async () => {
    try {
      const response = await axios.get(`http://localhost:6969/grab/food-types`);
      setTypeList((response.data.data || []).map((item : Food) => item.name));
    } catch (error) {
      console.error("Lỗi khi load food types:", error);
    }
  };

  // Define the function for handling the change of additional items
  const handleAdditionalChange = (additionalId: number) => {
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
      await axiosInstance.delete(`http://localhost:6969/grab/foods/${selectedFood?.id}`);
      alert("Đã xóa món ăn!");
      setSelectedFood(null);
      await fetchFoods();
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

  const toggleFoodStatus = async (foodId : number, restaurantId : string, currentStatus : string, refreshCallback : any) => {
    const newStatus = currentStatus === "ACTIVE" ? "INACTIVE" : "ACTIVE";
    try {
      // Cập nhật trạng thái món ăn thông qua PUT request
      const response = await axiosInstance.put(
        `http://localhost:6969/grab/foods/${foodId}`,  // Đảm bảo URL đúng với API bạn đang sử dụng
        null,
        { params: { restaurantId, foodStatus: newStatus } }
      );
      console.log(response.data);
      refreshCallback();  // Gọi lại dữ liệu sau khi cập nhật trạng thái
    } catch (error) {
      console.error("Failed to update food status:", error);
    }
  };


  const getFilteredFoods = () => {
    return foods.filter((food : Food) => {
      const matchType = !activeType || food.type === activeType;
      const matchSearch =
        food.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        kindsMap[food.kind]?.toLowerCase().includes(searchTerm.toLowerCase());
      return matchType && matchSearch;
    });
  };


  if (loading) return <p>Đang tải menu...</p>;

  return (
    <div className="p-4">
    {isAddFoodModalOpen && (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
        <div className="bg-white rounded-lg p-6 w-full max-w-md">
          <h3 className="text-xl font-semibold mb-4">Thêm món ăn mới</h3>
          <div className="space-y-2">
            <input
              type="text"
              value={newFoodData.name}
              onChange={(e) => setNewFoodData({ ...newFoodData, name: e.target.value })}
              className="w-full border px-3 py-2 rounded"
              placeholder="Tên món ăn"
            />
           <input
              type="text"
              value={newFoodData.image}
              onChange={(e) => setNewFoodData({ ...newFoodData, image: e.target.value })}
              className="w-full border px-3 py-2 rounded"
              placeholder="URL ảnh"
            />
            {newFoodData.image && (
              <img
                src={newFoodData.image}
                alt="Image preview"
                className="w-16 h-16 object-cover rounded-md mt-2"
              />
            )}

            <textarea
              value={newFoodData.description}
              onChange={(e) => setNewFoodData({ ...newFoodData, description: e.target.value })}
              className="w-full border px-3 py-2 rounded"
              placeholder="Mô tả"
            />
            <input
              type="number"
              value={newFoodData.price}
              onChange={(e) => setNewFoodData({ ...newFoodData, price: Number(e.target.value) })}
              className="w-full border px-3 py-2 rounded"
              placeholder="Giá"
            />
           <div className="flex justify-between mb-4 space-x-4">
            <div className="w-1/2">
              <label className="block text-sm font-medium mb-1">Loại món (Kind)</label>
              <select 
                value={newFoodData.kind} 
                className="w-full border px-3 py-2 rounded"
                onChange={(e) => setNewFoodData({ ...newFoodData, kind: e.target.value })}>
                {kinds.map((kind) => (
                  <option key={kind} value={kind}>{kind}</option>
                ))}
              </select>
            </div>

            <div className="w-1/2">
              <label className="block text-sm font-medium mb-1">Phân loại (Type)</label>
              <select 
                value={newFoodData.type} 
                className="w-full border px-3 py-2 rounded"
                onChange={(e) => setNewFoodData({ ...newFoodData, type: e.target.value })}>
                {typeList.map((type) => (
                  <option key={type} value={type}>{type}</option>
                ))}
              </select>
            </div>
          </div>

          </div>

          <div className="flex justify-end space-x-2 mt-4">
            <button onClick={handleAddNewFood} className="bg-green-600 text-white px-4 py-2 rounded">
              Lưu món mới
            </button>
            <button onClick={() => setIsAddFoodModalOpen(false)} className="bg-gray-500 text-white px-4 py-2 rounded">
              Đóng
            </button>
          </div>
        </div>
      </div>
    )}

      <div className="flex justify-between items-center mb-4">
        <input
          type="text"
          placeholder="Tìm món ăn..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="p-2 border rounded mr-4 w-2/3"
        />
        <button 
          onClick={() => setIsAddFoodModalOpen(true)} 
          className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">
          + Thêm món ăn
        </button>
      </div>

      <div className="border-b mb-4 flex space-x-4 overflow-x-auto">
        {/* Nút "Tất cả" */}
        <button
          onClick={() => setActiveType("")}
          className={`px-4 py-2 whitespace-nowrap ${
            activeType === "" ? "border-b-2 border-green-600 text-green-600 font-semibold" : "text-gray-500"
          }`}
        >
          Tất cả
        </button>
        {/* Các nút theo loại món ăn */}
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
        {getFilteredFoods().map((food) => (
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
            <div className="ml-4 flex-1 space-y-2">
              <p className={`text-sm ${food.status === 'ACTIVE' ? 'text-green-500' : 'text-red-500'}`}>
                {food.status} - {food.kind === 'MAIN' ? 'Món chính' : food.kind === 'BOTH' ? 'Linh hoạt' : "Món phụ"}
              </p>
              
              <div className="flex justify-between mt-2">
                <button
                className={`px-4 py-2 mt-2 ${food.status === 'ACTIVE' ? 'bg-red-500' : 'bg-green-500'} text-white rounded`}
                onClick={() => toggleFoodStatus(food.id, restaurantId, food.status, () => {
                  // Làm mới danh sách món ăn sau khi cập nhật trạng thái
                  setFoods(foods.map(f => f.id === food.id ? { ...f, status: food.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE' } : f));
                })}
              >
                {food.status === "ACTIVE" ? "Ẩn món" : "Hiện món"}
              </button>
              </div>
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
                value={editData.image}
                onChange={(e) => setEditData({ ...editData, image: e.target.value })}
                className="w-full border px-3 py-2 rounded"
                placeholder="URL ảnh"
              />
              {editData.image && (
                <img
                  src={editData.image}
                  alt="Image preview"
                  className="w-16 h-16 object-cover rounded-md mt-2"
                />
              )}
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
              {selectedFood.kind !== "ADDITIONAL" && (
                <>
                  <label className="block text-sm font-medium">Chọn món ăn phụ</label>
                  {isLoadingAdditionalItems ? (
                    <p>Đang tải món phụ...</p>
                  ) : (
                    <div className="space-y-2">
                      {additionalItems.map((item) => (
                        <label key={item.id} className="flex items-center">
                          <input
                            type="checkbox"
                            checked={editData.additionalIds?.includes(item.id)}
                            onChange={() => handleAdditionalChange(item.id)}
                            className="mr-2"
                          />
                          {item.name}
                        </label>
                      ))}
                    </div>
                  )}
                </>
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
