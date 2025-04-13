import FoodList from "@/components/FoodList";
import { Breadcrumb, BreadcrumbList, BreadcrumbItem, BreadcrumbLink, BreadcrumbSeparator } from "@/components/ui/breadcrumb";
import { Food, Restaurant } from "@/components/types/Types";
import Footer from "@/components/footer";
import Header from "@/components/header";
import { fetchWithAuth } from "@/utils/api";

interface Params {
  restaurantId: string;
}

async function getRestaurantData(id: string): Promise<Food[]> {
  const res = await fetchWithAuth(
    `http://localhost:6969/grab/foods/restaurant/${id}?isForCustomer=true&page=0&pageSize=20`,
    { cache: "no-store" }
  );

  if (!res.ok) {
    throw new Error("Failed to fetch restaurant data");
  }

  const data = await res.json();
  return data.data.items;
}
async function getRestaurantInfo(id: string): Promise<Restaurant> {
  const resInfo = await fetchWithAuth(
    `http://localhost:6969/grab/restaurants/${id}`,
    { cache: "no-store" }
  );

  if (!resInfo.ok) {
    throw new Error("Failed to fetch restaurant info");
  }

  const data = await resInfo.json();
  return data.data;
}

export default async function RestaurantPage({ params }: { params: Params }) {
  const { restaurantId } = params;

  if (!restaurantId) {
    throw new Error("Restaurant ID is missing");
  }
  const restaurantInfo = await getRestaurantInfo(restaurantId);
  const foods = await getRestaurantData(restaurantId);

  return (
    
    <div className="p-4">
      <Header />
      {/* Breadcrumb Navigation */}
      <Breadcrumb aria-label="breadcrumb">
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink href="/">Home</BreadcrumbLink>
          </BreadcrumbItem>

          <BreadcrumbSeparator />

          <BreadcrumbItem>
            <BreadcrumbLink href={`/restaurant/${restaurantId}`}>Restaurants {restaurantId}</BreadcrumbLink>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>

      {/* Restaurant Details
      <div className="mb-6">
        <h2 className="text-xl font-semibold" style={{ fontSize: '36px' }}>{restaurantInfo.name}</h2>
        <p>{restaurantInfo.description}</p>
        <p><strong>Address:</strong> {restaurantInfo.address}</p>
        <p><strong>Opening Hours:</strong> {restaurantInfo.openingHour} - {restaurantInfo.closingHour}</p>
      </div> */}

<div className="mb-6">
          {/* Tên nhà hàng */}
          <h1 className="text-3xl font-extrabold text-gray-900 mb-2">
            {restaurantInfo.name}
          </h1>

          {/* Mô tả + đánh giá */}
          <div className="flex flex-wrap items-center text-gray-700 text-sm mb-3 gap-x-4 gap-y-1">
            {/* Mô tả */}
            <p className="text-gray-600">{restaurantInfo.description}</p>

            {/* Đánh giá sao */}
            <div className="flex items-center">
              <span className="text-yellow-500 mr-1">⭐</span>
              <span>{restaurantInfo.rating}</span>
            </div>

            {/* Giao hàng
            <div className="flex items-center">
              <span className="mr-1">⏱️</span>
              <span>{restaurantInfo.deliveryTime} phút</span>
            </div>

            {/* Khoảng cách */}
            {/* <div className="flex items-center">
              <span className="text-lg font-bold mx-1">•</span>
              <span>{restaurantInfo.distance} km</span>
            </div> */}
          </div>

          {/* Địa chỉ */}
          <p className="text-gray-700 mb-1">
            <span className="font-medium">Địa chỉ:</span> {restaurantInfo.address}
          </p>

          {/* Giờ mở cửa */}
          <div className="text-gray-700 mt-1">
            <span className="font-medium">Giờ mở cửa:</span>
            <span className="ml-2">Hôm nay {restaurantInfo.openingHour} - {restaurantInfo.closingHour}</span>
          </div>
        </div>

      {/* Food List */}
      <h1 className="text-2xl font-bold mb-4">Danh sách món ăn</h1>
      <FoodList foods={foods} restaurantId={restaurantId} />

      <Footer />
      </div>

  );
}
