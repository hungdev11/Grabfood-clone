"use client";

import { useState } from "react";
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

  // Search function - now only called on button click or Enter
  const searchFoods = async () => {
    // Don't search if query is empty
    if (!searchQuery.trim()) {
      if (onResults) onResults([]);
      return;
    }

    setIsLoading(true);
    try {
      let url = `http://localhost:6969/grab/foods/search?query=${encodeURIComponent(
        searchQuery.trim()
      )}&isForCustomer=true`;

      // Add restaurant ID to query if provided
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
          onChange={(e) => setSearchQuery(e.target.value)}
          className="pl-7 pr-3 py-1 w-full rounded-md border text-sm h-8"
          onKeyDown={(e) => {
            if (e.key === "Enter" && searchQuery.trim()) {
              e.preventDefault();
              searchFoods();
            }
          }}
        />
        {isLoading && (
          <div className="absolute right-3 top-1/2 -translate-y-1/2">
            <div className="animate-spin h-3 w-3 border-2 border-gray-500 border-t-transparent rounded-full" />
          </div>
        )}
        <button
          onClick={searchFoods}
          disabled={isLoading || !searchQuery.trim()}
          className="ml-1 px-2 py-1 bg-[#00B14F] text-white rounded-md hover:bg-[#00A040] disabled:bg-gray-300 disabled:cursor-not-allowed flex-shrink-0 text-xs h-8"
        >
          {isLoading ? "Searching..." : "Search"}
        </button>
      </div>
      <p className="text-xs text-gray-500 mt-1">
        {isLoading
          ? "Searching..."
          : restaurantId
          ? "Nhập từ khóa và nhấn vào Tìm kiếm"
          : "Nhập từ khóa và nhấn vào Tìm kiếm"}
      </p>
    </div>
  );
}
