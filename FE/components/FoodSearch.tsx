"use client";

import { useState, useRef } from "react";
import { Search, X } from "lucide-react";
import { Input } from "@/components/ui/input";
import { fetchWithAuth } from "@/utils/api";
import { Food } from "./types/Types";

interface FoodSearchProps {
  restaurantId?: string;
  onResults?: (foods: Food[]) => void;
  placeholder?: string;
  className?: string;
}

export default function FoodSearch({
  restaurantId,
  onResults,
  placeholder = "Search for dishes...",
  className = "",
}: FoodSearchProps) {
  const [searchQuery, setSearchQuery] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const debounceRef = useRef<NodeJS.Timeout | null>(null);

  // Real-time search with debounce
  const handleInputChange = (value: string) => {
    setSearchQuery(value);
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      if (value.trim()) {
        searchFoods(value);
      } else {
        if (onResults) onResults([]);
      }
    }, 400);
  };

  // Sửa searchFoods để nhận query truyền vào
  const searchFoods = async (query: string) => {
    if (!query.trim()) {
      if (onResults) onResults([]);
      return;
    }
    setIsLoading(true);
    try {
      let url = `http://localhost:6969/grab/foods/search?query=${encodeURIComponent(
        query.trim()
      )}&isForCustomer=true`;
      if (restaurantId) {
        url += `&restaurantId=${restaurantId}`;
      }
      const response = await fetchWithAuth(url);
      const result = await response.json();
      if (result.data && Array.isArray(result.data)) {
        if (onResults) onResults(result.data);
      }
    } catch (error) {
      console.error("Error searching for foods:", error);
    } finally {
      setIsLoading(false);
    }
  };
  return (
    <div className={`relative ${className}`}>
      <div className="relative flex items-center">
        <Search className="absolute left-2 top-1/2 h-3 w-3 -translate-y-1/2 text-gray-500" />
        <Input
          type="text"
          placeholder={placeholder}
          value={searchQuery}
          onChange={(e) => handleInputChange(e.target.value)}
          className="pl-7 pr-10 py-1 w-full rounded-md border text-sm h-8"
        />
        {isLoading && (
          <div className="absolute right-3 top-1/2 -translate-y-1/2">
            <div className="animate-spin h-3 w-3 border-2 border-gray-500 border-t-transparent rounded-full" />
          </div>
        )}
        {searchQuery && !isLoading && (
          <button
            onClick={() => {
              setSearchQuery("");
              if (onResults) onResults([]);
            }}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
          >
            <X className="h-3 w-3" />
          </button>
        )}
      </div>
      <p className="text-xs text-gray-500 mt-1">
        {isLoading
          ? "Đang tìm kiếm..."
          : searchQuery
          ? "Kết quả được cập nhật khi bạn gõ"
          : "Nhập từ khóa để tìm món ăn"}
      </p>
    </div>
  );
}
