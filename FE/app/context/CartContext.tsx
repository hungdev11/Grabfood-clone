'use client';

import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import axiosInstance from '@/utils/axiosInstance';
import { AdditionalFood } from '@/components/types/Types'; 

interface CartItem {
  id: number;
  price: number;
  quantity: number;
  image: string;
  foodId: number;
  foodName: string;
  additionalFoods: AdditionalFood[];
  note?: string;
}
interface CartContextType {
  restaurantCartId: number | null;
  cartId: number | null;
  cartItems: CartItem[];
  setCartItems: (items: CartItem[]) => void;
  fetchCartItems: () => Promise<void>;
  updateQuantity: (cartDetailId: number, newQuantity: number) => Promise<void>;
  removeFromCart: (id: number) => Promise<void>;
  totalPrice: number;
  itemCount: number;
  isAuthenticated: boolean;
}

interface CartResponse {
  data: {
    cartId: number;
    restaurantId: number | null;
    listItem: CartItem[];
  };
  message: string;
  code: number;
  restaurantOpen: boolean;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider = ({ children }: { children: ReactNode }) => {
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [userId, setUserId] = useState<number | null>(null);
  const [cartId, setCartId] = useState<number | null>(null);
  const [restaurantCartId, setRestaurantCartId] = useState<number | null>(null);

  ;
  // Check if user is authenticated
  useEffect(() => {
    const checkAuth = () => {
      const token = localStorage.getItem('grabToken');
      setIsAuthenticated(!!token);
    };
    
    // Check auth status on mount and when localStorage changes
    checkAuth();
    window.addEventListener('storage', checkAuth);
    
    return () => {
      window.removeEventListener('storage', checkAuth);
    };
  }, []);
  
  useEffect(() => {
    if (typeof window !== 'undefined') {
      const storedId = localStorage.getItem("grabUserId");
      if (storedId) {
        setUserId(Number(storedId));
      }
    }
  }, []);

  // Fetch cart items only when authenticated
  useEffect(() => {
    if (isAuthenticated) {
      fetchCartItems();
    } else {
      // Clear cart items when not authenticated
      setCartItems([]);
    }
  }, [isAuthenticated]);

  // Lấy dữ liệu giỏ hàng
  const fetchCartItems = async () => {
    if (!isAuthenticated) {
      setCartItems([]);
      return;
    }
    
    try {
      // Use user-specific endpoint instead of hardcoded cart ID
      const response = await axiosInstance.get<CartResponse>("/grab/cart");
      console.log(response.data);
      if (response.data.code !== 200) {
        throw new Error('Không thể lấy dữ liệu giỏ hàng');
      }
      
      console.log(response.data.code);
      console.log(response.data.message);
      console.log(response.data.data);
      console.log(response.data.data.listItem);

      const data: CartItem[] = response.data.data.listItem.map((item: any) => ({
        restaurantId: item.restaurantId,
        id: item.id,
        foodName: item.foodName,
        price: Number(item.price),
        quantity: item.quantity,
        image: item.food_img,
        foodId: item.foodId,
        additionalFoods: item.additionFoods || [],
        note: item.note,
      }));
            
      setCartId(response.data.data.cartId);
      setRestaurantCartId(response.data.data.restaurantId);
      setCartItems(data);
    } catch (error) {
      console.error('Lỗi khi lấy dữ liệu giỏ hàng:', error);
      setCartItems([]);
    }
  };

  // Cập nhật số lượng món
  const updateQuantity = async (cartDetailId: number, newQuantity: number) => {
    if (!isAuthenticated) {
      return;
    }
    
    try {
      const requestData = {
        userId,
        cartDetailId,
        foodId: 0, // Server có thể không dùng, tuỳ logic backend
        newQuantity,
        additionFoodIds: [],
      };
      
      const response = await axiosInstance.put("/grab/cart/update-quantity", requestData);
      
      if (response.status === 200) {
        await fetchCartItems();
      }
    } catch (error) {
      console.error('Lỗi khi cập nhật số lượng:', error);
    }
  };

  // Xóa món khỏi giỏ hàng
  const removeFromCart = async (cartDetailId: number) => {
    const itemToRemove = cartItems.find((item) => item.id === cartDetailId);
    if (!itemToRemove) return;
  
    try {
      const payload = {
        userId,
        foodId: itemToRemove.foodId,
        additionalFoodIds: itemToRemove.additionalFoods.map(add => add.id),
      };
      
  
      console.log('Sending DELETE request with data:', payload);
  
      await axiosInstance.delete('http://localhost:6969/grab/cart/delete', {
        headers: {
          'Content-Type': 'application/json',
        },
        data: payload,
      });
  
      // Sau khi xoá thành công, cập nhật lại giỏ hàng
      await fetchCartItems();
    } catch (error) {
      console.error('❌ Lỗi khi xóa sản phẩm khỏi giỏ hàng:', error);
    }
  };

  // Tổng tiền & tổng số lượng
  const totalPrice = cartItems.reduce((total, item) => {
    const basePrice = item.price;
    const additionalPrice = item.additionalFoods.reduce((sum, add) => sum + Number(add.price), 0);
    return total + (basePrice + additionalPrice) * item.quantity;
  }, 0);
  
  const itemCount = cartItems.reduce((count, item) => count + item.quantity, 0);

  useEffect(() => {
    fetchCartItems();
  }, []);

  return (
    <CartContext.Provider
      value={{ 
        restaurantCartId,
        cartId,
        cartItems, 
        setCartItems, 
        fetchCartItems, 
        updateQuantity, 
        removeFromCart, 
        totalPrice, 
        itemCount,
        isAuthenticated 
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

// Custom hook để dùng context
export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};
