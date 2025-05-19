"use client";
import React, { useState, useEffect } from "react";
import { Food } from "@/components/types/Types";
import { Button } from "@/components/ui/button";
import PickAdditionalFood from "@/components/PickAdditionalFood";
import { useCart } from "@/app/context/CartContext";
import { fetchWithAuth } from "@/utils/api";
import { useRouter } from "next/navigation";
import { toast } from "@/components/ui/use-toast";

interface PopupProps {
  selectedFood: Food;
  isVisible: boolean;
  onClose: () => void;
  restaurantId: string;
  userId: number;
}

async function getAdditionalFood(
  foodId: number,
  restaurantId: string
): Promise<Food[]> {
  const res = await fetch(
    `http://localhost:6969/grab/foods/additional/${foodId}?restaurantId=${restaurantId}&isForCustomer=true&page=0&pageSize=20`,
    { cache: "no-store" }
  );

  if (!res.ok) {
    throw new Error(`Failed to fetch: ${res.status} ${res.statusText}`);
  }

  const data = await res.json();
  return data?.data?.items || [];
}

async function addToCart(userId: number, requestData: any) {
  const res = await fetchWithAuth(`http://localhost:6969/grab/cart/add?userId=${userId}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(requestData),
  });

  if (!res.ok) throw new Error("Failed to add to cart");
}

async function updateWholeCartItem(
  userId: number,
  cartDetailId: number,
  foodId: number,
  quantity: number,
  additionFoodIds: number[],
  note: string
) {
  const requestData = {
    userId,
    cartDetailId,
    foodId,
    newQuantity: quantity,
    additionFoodIds: additionFoodIds,
    note,
  };

  console.log("🛠 Updating whole cart item with data:", requestData); // 👈 Log kiểm tra

  const res = await fetchWithAuth("http://localhost:6969/grab/cart/update", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(requestData),
  });

  if (!res.ok) {
    const errText = await res.text();
    console.error("❌ Update failed:", errText); // 👈 Thêm log nếu lỗi
    throw new Error("Failed to update cart");
  } else {
    console.log("✅ Update success");
  }
}


const Popup: React.FC<PopupProps> = ({
  selectedFood,
  isVisible,
  onClose,
  restaurantId,
  userId,
}) => {
  const [quantity, setQuantity] = useState(1);
  const [additionalFoods, setAdditionalFoods] = useState<Food[]>([]);
  const [selectedItems, setSelectedItems] = useState<{ [key: number]: number }>({});
  const [specialInstructions, setSpecialInstructions] = useState("");
  const { fetchCartItems, isAuthenticated, restaurantCartId } = useCart();
  const router = useRouter();

  useEffect(() => {
    const fetchAdditionalFoods = async () => {
      try {
        const foods = await getAdditionalFood(selectedFood.id, restaurantId);
        setAdditionalFoods(foods);

        if (selectedFood.additionalFoods?.length) {
          const selectedMap: { [key: number]: number } = {};
          selectedFood.additionalFoods.forEach((item) => {
            const matched = foods.find((f) => f.id === item.id);
            if (matched) selectedMap[matched.id] = matched.discountPrice;
          });
          setSelectedItems(selectedMap);
        } else {
          setSelectedItems({});
        }
      } catch (error) {
        console.error("❌ Failed to load additional food:", error);
      }
    };

    if (selectedFood && restaurantId) fetchAdditionalFoods();
  }, [selectedFood, restaurantId]);

  useEffect(() => {
    if (selectedFood) {
      setQuantity(typeof selectedFood.quantity === "number" ? selectedFood.quantity : 1);
      setSpecialInstructions(selectedFood.note || "");
    }
  }, [selectedFood]);

  if (!isVisible || !selectedFood) return null;

  const increase = () => setQuantity((prev) => prev + 1);
  const decrease = () => setQuantity((prev) => Math.max(prev - 1, 0));

  const handleCheckboxChange = (foodId: number, price: number) => {
    setSelectedItems((prev) => {
      const updated = { ...prev };
      if (updated[foodId]) {
        delete updated[foodId];
      } else {
        updated[foodId] = price;
      }
      return updated;
    });
  };

  const totalAdditionalPrice = Object.values(selectedItems).reduce((sum, price) => sum + price, 0);
  const basePrice = typeof selectedFood.discountPrice === "number" && selectedFood.discountPrice < selectedFood.price
  ? selectedFood.discountPrice
  : selectedFood.price;

  const totalPrice = (basePrice + totalAdditionalPrice) * quantity;

  const handleSubmitToCart = async () => {
    if (!isAuthenticated) {
      toast?.({
        title: "Login Required",
        description: "Please login to add items to your basket",
        variant: "destructive",
      });
      onClose();
      router.push("/login");
      return;
    }

    const additionalIds = Object.keys(selectedItems).map(Number);

    try {
      if (selectedFood.cartDetailId) {
        await updateWholeCartItem(userId, selectedFood.cartDetailId, selectedFood.id, quantity, additionalIds, specialInstructions);
        toast?.({
          title: "Cập nhật thành công",
          description: `${selectedFood.name} đã được cập nhật`,
        });
      } else {
        if (restaurantCartId && Number(restaurantCartId) !== Number(restaurantId)) {
          const confirmAdd = window.confirm(
            "Bạn không thể thêm món từ nhiều nhà hàng cùng lúc. Nếu muốn tiếp tục, chọn OK và dữ liệu trong giỏ sẽ được bị xóa và không thể khôi phục."
          );
        
          if (!confirmAdd) {
            return; // Người dùng không đồng ý => Không làm gì cả
          }
        }
        
        // Đồng ý hoặc cùng nhà hàng thì addToCart bình thường
        const requestData = {
          foodId: selectedFood.id,
          quantity,
          additionalItems: additionalIds,
          note: specialInstructions,
        };
        
        await addToCart(userId, requestData);
        
        toast?.({
          title: "Thêm thành công",
          description: `${quantity} × ${selectedFood.name} đã được thêm vào giỏ`,
        });        
      }

      await fetchCartItems();
      onClose();
    } catch (error) {
      toast?.({
        title: "Lỗi",
        description: "Không thể xử lý. Vui lòng thử lại.",
        variant: "destructive",
      });
    }
  };

  return (
    <div
      className="fixed top-0 right-0 h-full w-96 bg-white shadow-lg z-50 flex flex-col transition-transform duration-300"
      style={{ transform: isVisible ? "translateX(0)" : "translateX(100%)" }}
    >
      <div className="flex justify-left mt-4 mb-4">
        <button onClick={onClose} className="text-4xl font-bold text-gray-700 hover:text-red-500">
          ×
        </button>
      </div>

      <div className="flex items-center justify-between mb-4 p-4">
        <img src={selectedFood.image || "/placeholder.svg"} alt={selectedFood.name} className="w-24 h-24 object-cover rounded-md mr-4" />
        <div className="flex-1 mx-4">
          <h2 className="text-2xl font-bold text-left">{selectedFood.name}</h2>
          <p className="text-xl font-bold mt-2 text-left">
          {typeof selectedFood.discountPrice === "number" && selectedFood.discountPrice < selectedFood.price ? (
            <>
              <span className="line-through text-gray-500 mr-2">
                {selectedFood.price.toLocaleString()}đ
              </span>
              <span className="text-red-500">
                {selectedFood.discountPrice.toLocaleString()}đ
              </span>
            </>
          ) : (
            <span>{selectedFood.price.toLocaleString()}đ</span>
          )}
        </p>
        </div>
      </div>

      <div className="flex-1 overflow-y-auto p-4 border-t border-gray-300 my-4">
        <PickAdditionalFood
          additionalFoods={additionalFoods}
          onCheckboxChange={handleCheckboxChange}
          onSpecialInstructionsChange={setSpecialInstructions}
          selectedItems={selectedItems}
          specialInstructions={specialInstructions}
        />
      </div>

      <div className="p-4 border-t border-gray-200 bg-white">
        <div className="flex items-center justify-between gap-4">
          <div className="flex items-center gap-2">
            <button onClick={decrease} className="w-10 h-10 rounded-full border text-xl font-bold hover:bg-gray-100">−</button>
            <span className="text-xl font-semibold w-8 text-center">{quantity}</span>
            <button onClick={increase} className="w-10 h-10 rounded-full border text-xl font-bold hover:bg-gray-100">+</button>
          </div>

          <Button
            variant={quantity <= 0 ? "destructive" : "success"}
            size="lg"
            onClick={() => {
              if (quantity <= 0) {
                onClose();
              } else {
                handleSubmitToCart();
              }
            }}
            className={`flex-1 whitespace-nowrap ${quantity <= 0 ? "bg-red-600 text-white" : ""}`}
          >
            {quantity <= 0
              ? "Cancel"
              : !isAuthenticated
              ? `Login to add – ${totalPrice.toLocaleString()}đ`
              : selectedFood.cartDetailId
              ? `Update – ${totalPrice.toLocaleString()}đ`
              : `Add to basket – ${totalPrice.toLocaleString()}đ`}
          </Button>
        </div>
      </div>
    </div>
  );
};

export default Popup;
