export interface ApiResponse<T> {
  data: T;
  message: string;
  code: number;
}

export interface PageResponse<T> {
  page: number;
  size: number;
  total: number;
  items: T[];
}

export interface RestaurantHome {
  id: number;
  name: string;
  image: string;
  rating: number;
  description?: string;
  timeDistance?: string;
}

export interface Restaurant {
  id: number;
  name: string;
  image: string;
  description: string;
  address: string;
  openingHour: string;
  closingHour: string;
  rating: number;
  phone: string;
  timeDistance: string;
  distance: string;
  latitude?: number;
  longitude?: number;
  email?: string;
}
export interface UpdateRestaurant {
  name: string;
  description: string;
  address: AddressRequest;
  openingHour: string;
  closingHour: string;
  phone: string;
}
export interface AddressRequest {
  detail: string;
  ward: string;
  province: string;
  district: string;
  latitude: string;
  longitude: string;
}
export interface AdditionalFood {
  id: number;
  name: string;
  price: number;
  discountPrice: number;
}

type FoodKind = "MAIN" | "ADDITIONAL" | "BOTH";
export interface Food {
  id: number;
  name: string;
  price: number;
  discountPrice: number;
  image: string;
  description?: string;
  rating?: number;
  quantity?: number;
  cartDetailId?: number;
  restaurantId?: number;
  status?: string;
  additionalFoods?: AdditionalFood[]; // danh sách object
  additionalIds?: number[]; // danh sách ID, phục vụ update
  note?: string;
  kind: FoodKind;
  type: string;
}

export interface GroupedFood {
  types: string[];
  foods: Food[];
}

export interface Location {
  lat: string;
  lon: string;
}

export interface Review {
  reviewId: number;
  orderId: number;
  customerName: string;
  orderString: string;
  reviewMessage: string;
  rating: number;
  createdAt: string;
  replyMessage: string;
  replyAt: string;
}

export interface CartDetail {
  id: number;
  foodName: string;
  quantity: number;
  price: number;
  additionFoods: AdditionalFood[];
  food_img: string;
  note: string;
  restaurantId: number;
  foodId: number | null;
}

export interface Order {
  id: number;
  userId: number | null;
  userName: string;
  createdAt: string;
  restaurantId: number | null;
  restaurantName: string;
  totalPrice: number;
  address: string;
  status: string;
  shippingFee: number;
  note: string;
  review: boolean;
  reviewResponse: Review;
  payment_method: string | null;
  cartDetails: CartDetail[];
  discountShippingFee: number | null;
  discountOrderPrice: number | null;
}

export interface Notification {
  id: string;
  subject: string;
  body: string;
  timeArrived: string;
  read: boolean;
}

export interface Reminder {
  id: string;
  title: string;
  description: string;
  reminderTime: string;
  isProcessed: boolean;
}
