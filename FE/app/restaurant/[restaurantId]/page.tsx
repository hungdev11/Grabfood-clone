import FoodList from "@/components/FoodList";
import { Breadcrumb, BreadcrumbList, BreadcrumbItem, BreadcrumbLink, BreadcrumbSeparator } from "@/components/ui/breadcrumb";
import { Food, GroupedFood, Restaurant, Review } from "@/components/types/Types";
import Footer from "@/components/footer";
import Header from "@/components/header";
import { fetchWithAuth } from "@/utils/api";
import {ReviewList} from "@/components/ReviewList";
import { CartProvider } from "@/app/context/CartContext";
import { parse, isAfter, isBefore } from "date-fns";

interface Params {
  restaurantId: string;
}

async function getRestaurantData(id: string): Promise<{ types: string[]; foods: Food[] }> {
  const res = await fetchWithAuth(
    `http://localhost:6969/grab/foods/restaurant/${id}?isForCustomer=true`,
    { cache: "no-store" }
  );

  if (!res.ok) {
    throw new Error("Failed to fetch restaurant data");
  }

  const data = await res.json();
  return data.data; // <-- Trả về { types, foods }
}

async function getRestaurantInfo(id: string, lat: string, lon: string): Promise<Restaurant> {
  const resInfo = await fetchWithAuth(
    `http://localhost:6969/grab/restaurants/${id}?userLat=${lat}&userLon=${lon}`,
    { cache: "no-store" }
  );

  if (!resInfo.ok) {
    throw new Error("Failed to fetch restaurant info");
  }

  const data = await resInfo.json();
  return data.data;
}

function isRestaurantOpen(opening: string, closing: string): boolean {
  const now = new Date();
  const todayOpen = parse(opening, "HH:mm:ss", new Date());
  const todayClose = parse(closing, "HH:mm:ss", new Date());

  return isAfter(now, todayOpen) && isBefore(now, todayClose);
}

export default async function RestaurantPage({ params, searchParams}: { params: Params ,searchParams: { lat?: string; lon?: string }}) {
  const { restaurantId } = params;

  if (!restaurantId) {
    throw new Error("Restaurant ID is missing");
  }
  const userLat = searchParams.lat ?? "-1";
  const userLon = searchParams.lon ?? "-1";

  const restaurantInfo = await getRestaurantInfo(restaurantId, userLat, userLon);
  const { types, foods } = await getRestaurantData(restaurantId);
  const isOpen = isRestaurantOpen(restaurantInfo.openingHour, restaurantInfo.closingHour);

  return (
    <CartProvider>
    <div className="p-4">
      <Header />
      {/* Breadcrumb Navigation */}
      <Breadcrumb className="ml-4" aria-label="breadcrumb" style={{ marginTop: "16px", fontSize: "3rem" }}>
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink href="/">Home</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbItem>
            <BreadcrumbLink href={`/restaurant/${restaurantId}`}>{restaurantInfo.name}</BreadcrumbLink>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>



      {/* Info section giống hình 1 */}
      <div className="mb-6 mt-4 ml-4">
        <h1 className="text-3xl font-extrabold text-gray-900 mb-2">
          {restaurantInfo.name}
        </h1>

        <div className="flex flex-wrap items-center gap-4 text-sm text-gray-600">
          {/* Rating */}
          <div className="flex items-center">
            <span className="text-yellow-500 mr-1">⭐</span>
            <span>{restaurantInfo.rating || "Chưa có đánh giá"}</span>
          </div>

          {/* Delivery time */}
          <div className="flex items-center">
            <span className="mr-1">⏱️</span>
            <span>{restaurantInfo.timeDistance || "Chưa xác định"}</span>
          </div>

          {/* Distance */}
          <div className="flex items-center">
            <span className="text-lg font-bold mx-1">•</span>
            <span>{restaurantInfo.distance || "Chưa xác định "}</span>
          </div>
        </div>

        {/* Opening hour */}
        <div className="mt-2 text-gray-700">
          <span className="font-medium">Giờ mở cửa:</span>{" "}
          Hôm nay {restaurantInfo.openingHour} - {restaurantInfo.closingHour}
        </div>

        {/* Promotions */}
        {/* <div className="mt-4 space-y-2">
          <div className="bg-green-50 p-2 rounded flex items-center text-sm text-green-800 border border-green-200">
            🏷️ Giảm 5.000₫ phí giao hàng khi đặt đơn tối thiểu 150.000₫
          </div>
          <div className="bg-green-50 p-2 rounded flex items-center text-sm text-green-800 border border-green-200">
            🎁 Tận hưởng ưu đãi hôm nay!
          </div>
        </div> */}

        {/* Delivery date/time - giả lập dropdown */}
        {/* <div className="flex flex-wrap gap-4 mt-4">
          <div className="flex items-center gap-2">
            <span className="font-medium">📅 Ngày giao hàng:</span>
            <span>Hôm nay</span>
          </div>
          <div className="flex items-center gap-2">
            <span className="font-medium">⏰ Thời gian giao:</span>
            <span>Ngay bây giờ</span>
          </div>
        </div> */}
      </div> 

      {/* Food list */}
      <FoodList types={types} foods={foods} restaurantId={restaurantId} isOpen={isOpen} />
      <ReviewList restaurantId = {restaurantId}/>
      <Footer />
    </div>
    </CartProvider>
  );
}
