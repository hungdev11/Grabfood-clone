"use client";

import Footer from "@/components/footer";
import Header from "@/components/header";
import Image from "next/image";
import { useState } from "react";
import {
  MapPin,
  Star,
  Clock,
  Heart,
  ShoppingCart,
  ShoppingBag,
  X,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import Link from "next/link";
import Cart from "@/components/cart";
import ResListHome from "@/components/ResListIndex";
import LocationSearch from "@/components/locationSearch";
import FoodSearch from "@/components/FoodSearch";
import PopupFood from "@/components/PopupFood";
import { RestaurantHome, Food } from "@/components/types/Types";
import { CartProvider } from "./context/CartContext";

export default function Home() {
  const [location, setLocation] = useState<{
    lat: string;
    lon: string;
    displayName: string;
  }>({
    lat: "-1",
    lon: "-1",
    displayName: "N/A",
  });
  const [restaurants, setRestaurants] = useState<RestaurantHome[]>([]);
  const [searchResults, setSearchResults] = useState<Food[]>([]);
  const [showSearchResults, setShowSearchResults] = useState(false);
  const [restaurantSearchResults, setRestaurantSearchResults] = useState<
    RestaurantHome[]
  >([]);
  const [isPopupVisible, setIsPopupVisible] = useState(false);
  const [selectedFood, setSelectedFood] = useState<Food | null>(null);

  const handleLocationSelect = (
    lat: string,
    lon: string,
    displayName: string
  ) => {
    setLocation({ lat, lon, displayName });
    console.log("Đã chọn vị trí:", lat, lon, displayName);
  };
  const handleSearchResults = (foods: Food[]) => {
    setSearchResults(foods);
    setShowSearchResults(foods.length > 0);
  };
  const handleRestaurantSearchResults = (restaurants: RestaurantHome[]) => {
    setRestaurantSearchResults(restaurants);
    // Nếu có kết quả nhà hàng hoặc món ăn, hiển thị phần kết quả tìm kiếm
    setShowSearchResults(restaurants.length > 0 || searchResults.length > 0);
  };

  const handleFoodClick = (food: Food) => {
    setSelectedFood(food);
    setIsPopupVisible(true);
  };

  const closePopup = () => {
    setIsPopupVisible(false);
    setSelectedFood(null);
  };

  const fetchNearbyRestaurants = async (lat: string, lon: string) => {
    console.log("Fetching nearby restaurants...");
    try {
      const res = await fetch(
        `http://localhost:6969/grab/restaurants/nearby?userLat=${lat}&userLon=${lon}`
      );
      const data = await res.json();

      console.log("API Response:", data); // Log response của API

      if (data?.data && Array.isArray(data.data) && data.data.length > 0) {
        console.log(data.data);
        setRestaurants(data.data);
      } else {
        console.log("No data received from API.");
      }
    } catch (error) {
      console.error("Lỗi fetch nearby restaurants:", error);
    }
  };

  return (
    <CartProvider>
      <div className="flex min-h-screen flex-col">
        {/* Header */}
        <Header /> {/* Hero Banner - Increased height for better display */}
        <div className="relative h-[400px] w-full">
          <Image
            src="/VN-new-4.jpg"
            alt="Food Banner"
            width={1200}
            height={400}
            className="h-full w-full object-cover"
            priority
          />{" "}
          <div className="absolute inset-0 bg-gradient-to-r from-black/40 to-transparent">
            <div className="mx-auto max-w-7xl px-4">
              <div
                className="mt-16 bg-white p-3 rounded-lg shadow-sm max-w-md"
                style={{ marginLeft: 0 }}
              >
                <h2 className="text-xs text-gray-600">Good Evening</h2>
                <h1 className="mb-2 text-lg font-bold">
                  Where should we deliver your food today?
                </h1>
                <div className="space-y-2">
                  <LocationSearch onSelectLocation={handleLocationSelect} />
                  {/* Food Search Box - Now integrated into the banner */}
                  <FoodSearch
                    onResults={handleSearchResults}
                    onRestaurantResults={handleRestaurantSearchResults}
                    placeholder="Tìm kiếm món ăn..."
                    className="text-sm"
                  />
                </div>
                <Button
                  className="mt-2 w-full bg-[#00B14F] hover:bg-[#00A040] text-white text-sm py-1"
                  onClick={() => {
                    if (location) {
                      fetchNearbyRestaurants(location.lat, location.lon);
                    } else {
                      alert("Vui lòng chọn vị trí trước khi tìm kiếm!");
                    }
                  }}
                >
                  Tìm kiếm
                </Button>
              </div>
            </div>{" "}
          </div>
        </div>{" "}
        {/* Search Results Section */}
        {showSearchResults && (
          <div className="mx-auto max-w-7xl px-4 py-8">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-xl font-bold">Search Results</h2>
              <Button
                variant="outline"
                size="sm"
                onClick={() => {
                  setShowSearchResults(false);
                  setSearchResults([]);
                  setRestaurantSearchResults([]);
                }}
                className="text-sm"
              >
                <X className="h-4 w-4 mr-1" />
                Clear Results
              </Button>
            </div>

            {/* Restaurant Search Results */}
            {restaurantSearchResults.length > 0 && (
              <div className="mb-8">
                <h3 className="text-lg font-semibold mb-4 border-b pb-2">
                  Restaurants
                </h3>
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
                  {restaurantSearchResults.map((restaurant) => (
                    <Link
                      href={`/restaurant/${restaurant.id}`}
                      key={restaurant.id}
                    >
                      <div className="flex flex-col border border-gray-200 rounded-lg shadow-sm hover:shadow-md transition-all hover:border-green-400 group cursor-pointer h-full overflow-hidden">
                        <div className="w-full h-40 overflow-hidden relative">
                          <img
                            src={restaurant.image || "/placeholder.svg"}
                            alt={restaurant.name}
                            className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
                          />
                          <div className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/60 to-transparent py-2">
                            <div className="px-3 text-white">
                              <div className="flex items-center">
                                <Star className="h-3 w-3 mr-1 text-yellow-400" />
                                <span className="text-xs">
                                  {restaurant.rating.toFixed(1)}
                                </span>
                              </div>
                            </div>
                          </div>
                        </div>
                        <div className="flex-1 p-4">
                          <h3 className="text-lg font-bold line-clamp-1 group-hover:text-green-600 transition-colors">
                            {restaurant.name}
                          </h3>
                          <p className="text-gray-500 text-sm line-clamp-2 mt-1">
                            {restaurant.description || ""}
                          </p>
                          <div className="flex items-center mt-2 text-sm text-gray-600">
                            <Clock className="h-3 w-3 mr-1" />
                            <span>
                              {restaurant.timeDistance || "15-30 min"}
                            </span>
                          </div>
                        </div>
                      </div>
                    </Link>
                  ))}
                </div>
              </div>
            )}

            {/* Food Search Results */}
            {searchResults.length > 0 && (
              <div>
                <h3 className="text-lg font-semibold mb-4 border-b pb-2">
                  Foods
                </h3>
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
                  {searchResults.map((food) => (
                    <div
                      key={food.id}
                      onClick={() => handleFoodClick(food)}
                      className="flex items-center p-4 border border-gray-200 rounded-lg shadow-sm hover:shadow-md transition-all hover:border-green-400 group cursor-pointer"
                    >
                      <div className="w-24 h-24 rounded-md overflow-hidden relative">
                        <img
                          src={food.image || "/placeholder.svg"}
                          alt={food.name}
                          className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
                        />
                        {food.type && (
                          <span className="absolute bottom-0 left-0 right-0 bg-black/50 text-white text-xs text-center py-1">
                            {food.type}
                          </span>
                        )}
                      </div>
                      <div className="ml-4 flex-1">
                        <h3 className="text-lg font-bold group-hover:text-green-600 transition-colors">
                          {food.name}
                        </h3>
                        <p className="text-gray-500 text-sm line-clamp-2">
                          {food.description || ""}
                        </p>
                        <p className="text-xl font-bold mt-2">
                          {typeof food.discountPrice === "number" &&
                          food.discountPrice < food.price ? (
                            <>
                              <span className="line-through text-gray-500 mr-2">
                                {food.price.toLocaleString()}đ
                              </span>
                              <span className="text-red-500">
                                {food.discountPrice.toLocaleString()}đ
                              </span>
                            </>
                          ) : (
                            <span>{food.price.toLocaleString()}đ</span>
                          )}
                        </p>
                      </div>
                      <Button
                        variant="success"
                        size="icon"
                        className="ml-4 text-lg rounded-full"
                      >
                        +
                      </Button>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* No Results Message */}
            {searchResults.length === 0 &&
              restaurantSearchResults.length === 0 && (
                <div className="text-center py-12 bg-gray-50 rounded-lg border border-gray-100 shadow-sm">
                  <div className="text-5xl mb-4">🍽️</div>
                  <h3 className="text-lg font-medium mb-2">
                    No matching results found
                  </h3>
                  <p className="text-gray-500">
                    Try a different search term or browse our categories below.
                  </p>
                </div>
              )}
          </div>
        )}{" "}
        {/* Deals Section */}
        <div className="mx-auto max-w-7xl px-4 py-8">
          <h2 className="mb-6 text-xl font-bold w-[70ch] truncate">
            Ưu đãi GrabFood tại{" "}
            <span className="text-[#00B14F]">{location.displayName}</span>
          </h2>
          <ResListHome restaurants={restaurants} location={location} />
          <div className="mt-4 rounded-md border border-gray-200 p-3 text-center text-sm text-gray-600">
            See all promotions
          </div>
        </div>
        {/* Food Categories */}
        <div className="mx-auto max-w-7xl px-4 py-8">
          <h2 className="mb-6 text-xl font-bold">
            There&apos;s something for everyone!
          </h2>
          <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
            {foodCategories.map((category, index) => (
              <div key={index} className="cursor-pointer">
                <div className="mb-2 overflow-hidden rounded-lg">
                  <Image
                    src={category.image || "/placeholder.svg"}
                    alt={category.name}
                    width={200}
                    height={200}
                    className="h-32 w-full object-cover transition-transform hover:scale-105"
                  />
                </div>
                <p className="text-center text-sm font-medium">
                  {category.name}
                </p>
              </div>
            ))}
          </div>
        </div>
        {/* Why Order Section */}
        <div className="mx-auto max-w-7xl px-4 py-8">
          <h2 className="mb-6 text-xl font-bold">
            Vì sao bạn nên Order trên GrabFood?
          </h2>
          <div className="space-y-4">
            {whyOrderReasons.map((reason, index) => (
              <div key={index} className="flex items-start gap-2">
                <div className="mt-1 h-2 w-2 rounded-full bg-black"></div>
                <div>
                  <span className="font-medium">{reason.title}: </span>
                  <span className="text-sm text-gray-600">
                    {reason.description}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
        {/* FAQ Section */}
        <div className="mx-auto max-w-7xl px-4 py-8">
          <h2 className="mb-6 text-xl font-bold">Những câu hỏi thường gặp</h2>
          <div className="rounded-lg border border-gray-200 p-4">
            <h3 className="mb-2 font-medium">GrabFood là gì?</h3>
            <p className="text-sm text-gray-600">
              Lorem, ipsum dolor sit amet consectetur adipisicing elit. Ipsum
              illo placeat quos unde cum a wide selection of merchant partners
              in Vietnam. GrabFood là dịch vụ đặt đồ ăn trực tuyến và giao hàng
              của Grab. Chúng tôi kết nối bạn với các nhà hàng yêu thích của
              bạn. Từ đồ ăn địa phương đến các món ăn quốc tế, từ đồ ăn nhanh
              đến các món ăn đặc sản, từ món chính đến món tráng miệng, chúng
              tôi đều có.
            </p>
            <Button variant="outline" className="mt-4 w-full">
              Read More
            </Button>
          </div>
        </div>
        {/* Features Section */}
        <div className="mx-auto max-w-7xl px-4 py-8">
          <div className="grid grid-cols-1 gap-8 md:grid-cols-2">
            <div className="flex flex-col items-center text-center">
              <Image
                src="/bottom-food-options.svg"
                alt="Curated restaurants"
                width={150}
                height={150}
                className="mb-4"
              />
              <h3 className="mb-2 font-bold">Curated restaurants</h3>
              <p className="text-sm text-gray-600">
                From street bites to big meals, we won&apos;t limit your
                appetite. Go ahead and order all you want.
              </p>
            </div>
            <div className="flex flex-col items-center text-center">
              <Image
                src="/ilus-cool-features-app.svg"
                alt="App features"
                width={150}
                height={150}
                className="mb-4"
              />
              <h3 className="mb-2 font-bold">
                More cool features available on the app
              </h3>
              <p className="text-sm text-gray-600">
                Download Grab app to use other payment methods and enjoy
                seamless communication with your driver.
              </p>
              <div className="mt-4 flex gap-4">
                <Image
                  src="/logo-appstore.svg"
                  alt="App Store"
                  width={120}
                  height={40}
                />
                <Image
                  src="/logo-playstore.svg"
                  alt="Google Play"
                  width={120}
                  height={40}
                />
              </div>
            </div>
          </div>
        </div>{" "}
        {/* Footer */}
        <Footer />
        {/* Popup chi tiết món ăn */}
        {selectedFood && (
          <PopupFood
            selectedFood={selectedFood}
            isVisible={isPopupVisible}
            onClose={closePopup}
            restaurantId={selectedFood.restaurantId?.toString() || "0"}
            userId={Number(localStorage.getItem("grabUserId") || "0")}
          />
        )}
      </div>
    </CartProvider>
  );
}

const foodCategories = [
  { name: "Trà sữa", image: "/categories/tra-sua.webp" },
  { name: "Đồ uống lạnh", image: "/categories/do-uong-lanh.webp" },
  { name: "Cơm tấm", image: "/categories/com-tam.webp" },
  { name: "Rau trộn", image: "/categories/rau-tron.webp" },
  { name: "Bánh Mì", image: "/categories/banh-mi.webp" },
  { name: "Pizza", image: "/categories/pizza.webp" },
  { name: "Thịt gà", image: "/categories/thit-ga.webp" },
  { name: "Cơm", image: "/categories/com.webp" },
  { name: "Thức ăn nhanh", image: "/categories/thuc-an-nhanh.webp" },
  { name: "Hiso Party", image: "/categories/hiso-party.webp" },
  { name: "Weekend Treats", image: "/categories/weekend-treats.webp" },
  { name: "Gà rán", image: "/categories/ga-ran.webp" },
  { name: "Đồ ăn nhẹ", image: "/categories/do-an-nhe.webp" },
  { name: "Cháo", image: "/categories/chao.webp" },
  { name: "Mì Ý", image: "/categories/mi-y.webp" },
];

const whyOrderReasons = [
  {
    title: "Muốn nhanh",
    description: "GrabFood cung cấp dịch vụ giao đồ ăn nhanh nhất thị trường.",
  },
  {
    title: "Đồ ăn ngon mỗi ngày",
    description:
      "Từ món ăn địa phương đến các món ăn quốc tế hoặc chuỗi nhà hàng đồ ăn nổi tiếng, bạn có thể dễ dàng tìm thấy những món ăn ngon trên GrabFood.",
  },
  {
    title: "Không mất thêm phí",
    description:
      "Tự do lựa chọn các món ăn yêu thích. Thanh toán đúng giá hiển thị trên ứng dụng, không phí ẩn.",
  },
  {
    title: "Nhiều ưu đãi hấp dẫn",
    description:
      "Tích điểm GrabRewards mỗi khi đặt món, cơ hội để đổi lấy ưu đãi hấp dẫng, giảm giá đồ ăn, và nhiều ưu đãi hơn.",
  },
];
