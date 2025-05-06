'use client';

import Image from "next/image";
import { Button } from "@/components/ui/button";
import { useState, useEffect } from "react";
import Link from "next/link";
import { useCart } from "@/app/context/CartContext";
import PopupFood from "@/components/PopupFood"; 

import { Food } from "./types/Types";
import axiosInstance from "@/utils/axiosInstance";
import { set } from "date-fns";
interface CartProps {
  isOpen: boolean;
  onClose: () => void;
  onCartChange?: (itemCount: number, totalPrice: number) => void;
}

export default function Cart({ isOpen, onClose, onCartChange }: CartProps) {
  const {cartId, restaurantCartId, cartItems, updateQuantity, removeFromCart, totalPrice, itemCount } = useCart();
  const [isRestaurantOpen, setIsRestaurantOpen] = useState<boolean | null>(true);
  const [isPopupVisible, setPopupVisible] = useState(false);
  const [selectedFood, setSelectedFood] = useState<any>(null);

  const cartIdRequest = cartId;

  // const openPopup = (cartItem: any) => {
  //   const food = {
  //     id: cartItem.foodId,
  //     name: cartItem.foodName,
  //     price: cartItem.price,
  //     image: cartItem.food_img,
  //     note: cartItem.note,
  //   };
  //   setSelectedFood(food);
  //   setPopupVisible(true);
  // };
  const openPopup = (cartItem: any) => {
    const food = {
      id: cartItem.foodId,
      name: cartItem.foodName,
      price: cartItem.price,
      image: cartItem.image,
      note: cartItem.note ?? "",
      quantity: cartItem.quantity,
      cartDetailId: cartItem.id,
      additionalFoods: cartItem.additionalFoods,
    };
  
    console.log("Food selected for popup:", food);  // Debugging log
    setSelectedFood(food);
    setPopupVisible(true);
  };
  
  const closePopup = () => {
    setSelectedFood(null);
    setPopupVisible(false);
  };

  const handleCartClickItem = (itemId: number) => {
    const clickedItem = cartItems.find(item => item.id === itemId);
    if (clickedItem) openPopup(clickedItem);
  };

  useEffect(() => {
    if (!isOpen || cartItems.length === 0) return;
    const checkRestaurantOpen = async () => {
      try {
        const response = await axiosInstance.get('http://localhost:6969/grab/cart/checkOpen', {
          params: {
            cartId: cartIdRequest
          }
        })
        setIsRestaurantOpen(response.data);
        console.log("Restaurant open status:", response.data);
      } catch (error) {
        console.error("Lỗi kiểm tra nhà hàng:", error);
        setIsRestaurantOpen(false); // fallback nếu lỗi
      }
    }
    checkRestaurantOpen();
  }), [isOpen, cartItems];
  

  useEffect(() => {
    if (onCartChange) {
      onCartChange(itemCount, totalPrice);
    }
  }, [cartItems, onCartChange]);

  const handleIncrement = (item: { id: number }) => {
    const newQuantity = cartItems.find((i) => i.id === item.id)!.quantity + 1;
    updateQuantity(item.id, newQuantity);
  };

  const handleDecrement = (item: { id: number }) => {
    const currentItem = cartItems.find((i) => i.id === item.id);
    if (currentItem && currentItem.quantity > 1) {
      const newQuantity = currentItem.quantity - 1;
      updateQuantity(item.id, newQuantity);
    }
  };

  if (!isOpen) return null;

  return (
    <>
      {/* Overlay */}
      <div
        className={`fixed inset-0 bg-black/20 z-40 transition-opacity duration-300 ${
          isOpen ? "opacity-100" : "opacity-0 pointer-events-none"
        }`}
        onClick={onClose}
      />

      {/* Side Cart */}
      <div
        className={`fixed top-0 right-0 z-50 h-full w-96 bg-gray-100 transition-transform duration-300 ${
          isOpen ? "translate-x-0" : "translate-x-full"
        }`}
      >
        <button
          onClick={onClose}
          className="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
        >
          <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>

        {cartItems.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-full p-6 text-center">
            <div className="mb-4">
              <Image src="/cart-illustration.png" alt="Cart Illustration" width={120} height={120} />
            </div>
            <h3 className="text-lg font-bold text-gray-800">Start Grabbing Food!</h3>
            <p className="text-sm text-gray-600 mt-2">Add items to your basket and place order here.</p>
            <Button variant="link" className="mt-4 text-blue-500 hover:text-blue-700" onClick={onClose}>
              Continue browsing
            </Button>
          </div>
        ) : (
          <div className="flex flex-col h-full p-6">
            <h3 className="text-lg font-bold text-gray-800 mb-2">Giỏ đồ ăn</h3>
            <p className="text-sm text-gray-500 mb-4">Thời gian giao: 15 phút (Cách bạn 0.5 km)</p>
            <div className="flex-1 overflow-y-auto">
              {cartItems.map((item) => (
                <div
                  key={item.id}
                  className="flex items-center gap-3 mb-4 border-b border-gray-200 pb-4 cursor-pointer hover:bg-gray-50 transition"
                  onClick={() => handleCartClickItem(item.id)}
                >
                <Image src={item.image} alt={item.foodName} width={60} height={60} className="rounded-md object-cover" />
                  <div className="flex-1">
                    <h4 className="text-sm font-medium text-gray-800">{item.foodName}</h4>
                    <div className="text-xs text-gray-600">
                      {item.additionalFoods.map((food) => (
                        <p key={food.id}>+ {food.name}</p>
                      ))}
                    </div>

                    <div className="flex items-center gap-2 mt-1" onClick={(e) => e.stopPropagation()}>
                      <button
                        className="w-6 h-6 flex items-center justify-center bg-gray-200 rounded-full text-gray-600"
                        onClick={() => handleDecrement(item)}
                      >
                        -
                      </button>
                      <span className="text-sm">{item.quantity}</span>
                      <button
                        className="w-6 h-6 flex items-center justify-center bg-gray-200 rounded-full text-gray-600"
                        onClick={() => handleIncrement(item)}
                      >
                        +
                      </button>
                      <button
                        className="ml-2 text-red-500 hover:text-red-700 text-sm"
                        onClick={() => removeFromCart(item.id)}
                      >
                        Xóa
                      </button>
                    </div>
                  </div>
                  <p className="text-sm font-semibold text-gray-800">
                    {(
                      (item.price + item.additionalFoods.reduce((sum, add) => sum + add.price, 0)) *
                      item.quantity
                    ).toLocaleString()} đ
                  </p>
                </div>
              ))}
            </div>
            <div className="border-t border-gray-200 pt-4">
              <div className="flex justify-between text-sm text-gray-800 mb-2">
                <span>Tổng</span>
                <span>{totalPrice.toLocaleString()} đ</span>
              </div>
              <div className="flex justify-between text-sm text-gray-500 mb-4">
                <span>Delivery Fee</span>
                <span>Sẽ hiển thị sau khi bạn hoàn tất đơn hàng</span>
              </div>
              <div className="flex justify-between text-sm font-bold text-gray-800 mb-4">
                <span>Tổng cộng</span>
                <span>{totalPrice.toLocaleString()} đ</span>
              </div>
              {isRestaurantOpen === null ? (
                <Button disabled className="w-full bg-gray-300 text-white">Đang kiểm tra...</Button>
              ) : isRestaurantOpen ? (
                <Button id="checkout-button"
                  className="w-full bg-green-500 hover:bg-green-600 text-white"
                  asChild
                >
                  <Link href="/checkout">Thanh toán</Link>
                </Button>
              ) : (
                <Button disabled className="w-full bg-gray-400 text-black cursor-not-allowed">
                  Ngoài giờ bán hàng
                </Button>
              )}
            </div>
          </div>
        )}
      </div>

      {/* ✅ PopupFood nằm bên ngoài để hiển thị full size */}
      <PopupFood
        selectedFood={selectedFood}
        isVisible={isPopupVisible}
        onClose={closePopup}
        restaurantId={String(restaurantCartId!)}
        userId = {Number(localStorage.getItem("grabUserId"))}
        />
    </>
  );
}
