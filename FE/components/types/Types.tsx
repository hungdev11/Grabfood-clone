import { types } from "util";

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
    address : string;
    openingHour : string;
    closingHour : string;
    rating: number;
    timeDistance: string;
    distance: string;
  }
  
  export interface AdditionalFood {
    id: number;
    name: string;
    price: number;
  }

  export interface Food {
    id: number;
    name: string;
    price: number;
    discountPrice: number,
    image: string;
    description?: string;
    rating?: number;
    quantity?: number;
    cartDetailId?: number;
    additionalFoods?: AdditionalFood[];
    note?: string;
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
    replyAt: string
  }