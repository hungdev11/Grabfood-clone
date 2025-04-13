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
  }
  
  export interface Food {
    id: number;
    name: string;
    price: number;
    image?: string;
    description?: string;
    rating?: number;
    additionalFoods?: {
      id: number;
      name: string;
      price: number;
    }[];
  }
  