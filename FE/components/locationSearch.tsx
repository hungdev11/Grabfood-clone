"use client";
import React, { useEffect, useState } from "react";
import { MapPin, Crosshair } from "lucide-react";

interface LocationSearchProps {
  onSelectLocation?: (lat: string, lon: string, displayName: string) => void;
}

interface Place {
  place_id: string;
  display_name: string;
  lat: string;
  lon: string;
}

const LocationSearch: React.FC<LocationSearchProps> = ({
  onSelectLocation,
}) => {
  const [query, setQuery] = useState("");
  const [suggestions, setSuggestions] = useState<Place[]>([]);

  const [loading, setLoading] = useState(false);

  const handleGetCurrentLocation = () => {
    setLoading(true); // Bắt đầu loading

    navigator.geolocation.getCurrentPosition(
      async (position) => {
        const { latitude, longitude } = position.coords;

        try {
          const res = await fetch(
            `https://nominatim.openstreetmap.org/reverse?format=json&lat=${latitude}&lon=${longitude}`
          );
          const data = await res.json();
          const display_name = data.display_name || "Vị trí hiện tại";

          setQuery(display_name);
          if (onSelectLocation) {
            onSelectLocation(latitude.toString(), longitude.toString(), display_name);
          }
        } catch (error) {
          console.error("Lỗi khi reverse geocoding:", error);
          setLoading(false); // Bắt đầu loading
        } finally {
          setLoading(false); // Kết thúc loading
        }
      },
      (error) => {
        console.error("Lỗi khi lấy vị trí:", error);
        alert("Không thể lấy vị trí hiện tại. Vui lòng bật định vị.");
      }
    );
  };

  const fetchSuggestions = async (keyword: string) => {
    try {
      const res = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(
          keyword
        )}&addressdetails=1`
      );
      const data = await res.json();
      setSuggestions(data);
    } catch (err) {
      console.error("Failed to fetch suggestions:", err);
    }
  };

  useEffect(() => {
    const timer = setTimeout(() => {
      if (query.length > 2) {
        fetchSuggestions(query);
      } else {
        setSuggestions([]);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [query]);

  const handleSelect = (place: Place) => {
    setQuery(place.display_name);
    setSuggestions([]);
    console.log("Selected place:", place.lat, place.lon);

    if (onSelectLocation) {
      onSelectLocation(place.lat, place.lon, place.display_name);
    }
  };
  return (
    <div className="relative mb-1">
      <input
        type="text"
        value={query}
        placeholder="Nhập địa điểm..."
        onChange={(e) => setQuery(e.target.value)}
        className="h-8 w-full rounded-md border border-gray-300 pl-8 pr-8 text-sm focus:outline-none focus:ring-1 focus:ring-green-500"
      />
      <MapPin className="absolute left-2 top-1/2 h-3 w-3 -translate-y-1/2 text-red-500" />
      {/* Nút lấy vị trí */}
      <button
        type="button"
        onClick={handleGetCurrentLocation}
        disabled={loading}
        className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-500 hover:text-green-600"
      >
        <Crosshair
          className={`w-3 h-3 transition-transform ${
            loading ? "animate-spin text-green-500" : ""
          }`}
        />
      </button>{" "}
      {suggestions.length > 0 && (
        <ul className="absolute z-[999] w-full bg-white border border-gray-300 mt-1 max-h-40 overflow-y-auto text-xs rounded-md shadow">
          {suggestions.map((place) => (
            <li
              key={place.place_id}
              onClick={() => handleSelect(place)}
              className="p-1.5 hover:bg-gray-100 cursor-pointer"
            >
              {place.display_name}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default LocationSearch;
