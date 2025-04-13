"use client";
import React, { useState, useEffect } from "react";
import { Food } from "@/components/types/Types";
import { Button } from "@/components/ui/button";
import PickAdditionalFood from "@/components/PickAdditionalFood"; // Import your component here
import { useCart } from "@/app/context/CartContext";
import { fetchWithAuth } from "@/utils/api";
import { useRouter } from "next/navigation"; // Import router
import { toast } from "@/components/ui/use-toast"; // Import toast if available, or use another notification system

interface PopupProps {
  selectedFood: Food;
  isVisible: boolean;
  onClose: () => void;
  restaurantId: string;
  userId: number; // Added userId for the request
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

  let data: any;
  try {
    data = await res.json();
  } catch (err) {
    console.error("Failed to parse JSON:", err);
    throw new Error("Invalid JSON response");
  }

  if (!data?.data?.items || !Array.isArray(data.data.items)) {
    console.warn("Response doesn't contain 'data.items':", data);
    return [];
  }

  return data.data.items;
}


async function addToCart(userId: number, requestData: any) {
  const res = await fetchWithAuth(`http://localhost:6969/grab/cart/add?userId=${userId}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(requestData),
  });

  if (!res.ok) {
    throw new Error("Failed to add to cart");
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
  const [selectedItems, setSelectedItems] = useState<{ [key: number]: number }>(
    {}
  );
  const [specialInstructions, setSpecialInstructions] = useState("");
  const { fetchCartItems, isAuthenticated } = useCart(); // Get authentication status from cart context
  const router = useRouter();

  useEffect(() => {
    const fetchAdditionalFoods = async () => {
      try {
        const foods = await getAdditionalFood(selectedFood.id, restaurantId);
        setAdditionalFoods(foods);
      } catch (error) {
        console.error("Failed to load additional food:", error);
      }
    };

    if (selectedFood && restaurantId) {
      fetchAdditionalFoods();
    }
  }, [selectedFood, restaurantId]);
  

  if (!isVisible || !selectedFood) return null;

  const increase = () => setQuantity((prev) => prev + 1);
  const decrease = () => setQuantity((prev) => prev - 1);

  const handleCheckboxChange = (foodId: number, price: number) => {
    setSelectedItems((prevSelectedItems) => {
      const updatedItems = { ...prevSelectedItems };
      if (updatedItems[foodId]) {
        // If already selected, remove it from the list
        delete updatedItems[foodId];
      } else {
        // If not selected, add it to the list
        updatedItems[foodId] = price;
      }
      return updatedItems;
    });
  };

  const totalAdditionalPrice = Object.values(selectedItems).reduce(
    (sum, price) => sum + price,
    0
  );
  const totalPrice = (selectedFood.price + totalAdditionalPrice) * quantity; // Include the additional items' price in total

  const handleAddToBasket = async () => {
    // Check if user is authenticated
    if (!isAuthenticated) {
      // Show login notification
      if (typeof toast !== "undefined") {
        toast({
          title: "Login Required",
          description: "Please login to add items to your basket",
          variant: "destructive",
        });
      } else {
        alert("Please login to add items to your basket");
      }

      // Close the popup
      onClose();

      // Redirect to login page
      router.push("/login");
      return;
    }
    console.log("Special Instructions:", specialInstructions); // Kiểm tra giá trị của specialInstructions

    const requestData = {
      foodId: selectedFood.id,
      quantity: quantity,
      additionalItems: Object.keys(selectedItems).map(Number),
      note: specialInstructions, // Gửi specialInstructions dưới dạng note
    };

    try {
      await addToCart(userId, requestData);
      await fetchCartItems();
      console.log("Item added to cart");

      // Show success notification
      if (typeof toast !== "undefined") {
        toast({
          title: "Added to Basket",
          description: `${quantity} × ${selectedFood.name} added to your basket`,
          variant: "default",
        });
      }

      onClose();
    } catch (error) {
      console.error("Failed to add to cart:", error);

      // Show error notification
      if (typeof toast !== "undefined") {
        toast({
          title: "Failed to Add Item",
          description: "Could not add item to basket. Please try again.",
          variant: "destructive",
        });
      } else {
        alert("Failed to add item to basket");
      }
    }
  };

  return (
    isVisible && (
      <div
        className="fixed top-0 right-0 h-full w-2/5 bg-white shadow-lg z-50 transition-transform transform duration-300 flex flex-col"
        style={{ transform: isVisible ? "translateX(0)" : "translateX(100%)" }}
      >

        <div className="flex justify-left mt-4 mb-4">
          <button
            onClick={onClose}
            className="text-4xl font-bold text-gray-700 hover:text-red-500 transition duration-200"
          >
            ×
          </button>
        </div>

        {/* Image and Food Details */}
        <div className="flex items-center justify-between mb-4 p-4">
          <img
            src={selectedFood.image || "/placeholder.svg"}
            alt={selectedFood.name}
            className="w-24 h-24 object-cover rounded-md mr-4"
          />
          <div className="flex-1 mx-4">
            <h2 className="text-2xl font-bold text-left">
              {selectedFood.name}
            </h2>
            <p className="text-xl font-bold mt-2 text-left">
              {selectedFood.price.toLocaleString()}đ
            </p>
          </div>
        </div>

        {/* PickAdditionalFood Component */}
        <div className="flex-1 overflow-y-auto p-4 border-t border-gray-300 my-4">
          <PickAdditionalFood
            additionalFoods={additionalFoods}
            onCheckboxChange={handleCheckboxChange} // Pass the handler for checkbox change
            onSpecialInstructionsChange={setSpecialInstructions} // Truyền hàm callback vào đây
          />
        </div>

        {/* Quantity and Add to Basket */}
        <div className="p-4 border-t border-gray-200 bg-white">
          <div className="flex items-center justify-between gap-4">
            <div className="flex items-center gap-2">
              <button
                onClick={decrease}
                className="w-10 h-10 rounded-full border text-xl font-bold hover:bg-gray-100"
              >
                −
              </button>
              <span className="text-xl font-semibold w-8 text-center">
                {quantity}
              </span>
              <button
                onClick={increase}
                className="w-10 h-10 rounded-full border text-xl font-bold hover:bg-gray-100"
              >
                +
              </button>
            </div>

            <Button
              variant={quantity <= 0 ? "destructive" : "success"} // Switches button variant based on quantity
              size="lg"
              onClick={() => {
                if (quantity <= 0) {
                  console.log("Cancel operation");
                  onClose(); // Close the popup if no items are selected
                } else {
                  console.log(
                    "Add to basket:",
                    selectedFood,
                    "Quantity:",
                    quantity
                  );
                  handleAddToBasket(); // Gọi hàm handleAddToBasket để thêm vào giỏ hàng
                }
              }}
              className={`flex-1 whitespace-nowrap ${
                quantity <= 0 ? "bg-red-600 text-white" : ""
              }`}
            >
              {quantity <= 0
                ? "Cancel"
                : isAuthenticated
                ? `Add to basket – ${totalPrice.toLocaleString()}đ`
                : `Login to add – ${totalPrice.toLocaleString()}đ`}
            </Button>
          </div>
        </div>
      </div>
    )
  );
};

export default Popup;
