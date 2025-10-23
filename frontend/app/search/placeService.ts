import { api } from '@/lib/api';

export type PlaceDto = {
  id: number;
  name: string;
  category2: string;      
  latitude: number;
  longitude: number;
  distanceMeters: number;
  averageRating: number;
  address: string;
};

export type ApiResponse<T> = {
  code: string;
  message: string;
  data: T;
};

export async function searchPlaces(params: {
  lat: number;
  lon: number;
  radiusKm: number;        
  keyword?: string;        
  category2?: string;      
}) {
  return api<ApiResponse<PlaceDto[]>>('/api/v1/places/search', { query: params });
}

export async function getPlaceDetail(placeId: number) {
  return api<ApiResponse<any>>(`/api/v1/places/${placeId}`);
}
