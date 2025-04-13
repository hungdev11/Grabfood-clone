// components/Header.tsx
'use client';

import Image from "next/image";
import { ShoppingBag, ShoppingCart, User, LogOut } from "lucide-react";
import { Button } from "@/components/ui/button";
import Link from "next/link";
import Cart from "@/components/cart";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";

export default function Header() {
    const router = useRouter();
    const [isCartOpen, setIsCartOpen] = useState<boolean>(false);
    const [itemCount, setItemCount] = useState<number>(0);
    const [totalPrice, setTotalPrice] = useState<number>(0);
    const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false);
    const [username, setUsername] = useState<string>("");
  
    // Check authentication status on load and when localStorage changes
    useEffect(() => {
      const checkAuth = () => {
        const token = localStorage.getItem("grabToken");
        if (token) {
          setIsLoggedIn(true);
          
          // Try to extract username from the JWT token
          try {
            const tokenParts = token.split('.');
            if (tokenParts.length === 3) {
              const payload = JSON.parse(atob(tokenParts[1]));
              setUsername(payload.sub || "User"); // Use 'sub' claim or default to "User"
            }
          } catch (e) {
            setUsername("User");
            console.warn("Could not decode token:", e);
          }
        } else {
          setIsLoggedIn(false);
          setUsername("");
        }
      };
      
      checkAuth();
      
      // Listen for storage events (in case user logs in/out in another tab)
      window.addEventListener('storage', checkAuth);
      
      return () => {
        window.removeEventListener('storage', checkAuth);
      };
    }, []);
  
    // Handle logout
    const handleLogout = () => {
      localStorage.removeItem("grabToken");
      setIsLoggedIn(false);
      setUsername("");
      router.push("/");
    };
  
    // Handle cart changes
    const handleCartChange = (count: number, price: number) => {
      setItemCount(count);
      setTotalPrice(price);
    };

  return (
    <>
      <header className="sticky top-0 z-50 bg-white p-4 shadow-sm">
        <div className="mx-auto flex max-w-7xl items-center justify-between">
          <div className="flex items-center">
            <Link href="/">
              <div className="text-[#00B14F] font-bold text-xl">
                GrabFood
              </div>
            </Link>
          </div>
          <div className="flex items-center gap-3">
            {itemCount > 0 ? (
              <Button id="cart-button"
                className="bg-green-500 hover:bg-green-600 text-white rounded-md px-4 py-2 flex items-center gap-2 relative"
                onClick={() => setIsCartOpen(!isCartOpen)}
              >
                <span className="absolute -top-2 -left-2 bg-white text-green-500 rounded-full w-5 h-5 flex items-center justify-center text-xs font-semibold border border-green-500">
                  {itemCount}
                </span>
                <ShoppingBag className="h-5 w-5 text-white" />
                <span>{totalPrice.toLocaleString("vi-VN")} đ</span>
              </Button>
            ) : (
              <Button id="cart-button"
                variant="outline"
                className="p-2 border-gray-300 rounded-md"
                onClick={() => setIsCartOpen(!isCartOpen)}
              >
                <ShoppingCart className="h-5 w-5 text-gray-600" />
              </Button>
            )}
            
            {isLoggedIn ? (
              <div className="flex items-center gap-2">
                <div className="flex items-center gap-1 bg-gray-100 px-3 py-1 rounded-full">
                  <User className="h-4 w-4 text-gray-600" />
                  <span className="text-sm font-medium">{username}</span>
                </div>
                <Button
                  variant="outline"
                  size="sm"
                  className="flex items-center gap-1"
                  onClick={handleLogout}
                >
                  <LogOut className="h-4 w-4" />
                  <span>Logout</span>
                </Button>
              </div>
            ) : (
              <Button
                variant="outline"
                className="px-4 py-2 border-gray-300 rounded-md text-gray-700 hover:bg-gray-100"
                asChild
              >
                <Link href="/login">
                  Login/Sign Up
                </Link>
              </Button>
            )}
          </div>
        </div>
      </header>

      <Cart
        isOpen={isCartOpen}
        onClose={() => setIsCartOpen(false)}
        onCartChange={handleCartChange}
      />
    </>
  );
}