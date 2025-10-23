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
  radiusKm: number;     // km
  keyword?: string;     // 선택
  category?: string;    // 선택 (백엔드 파라미터 키와 일치)
}) {
  return api<ApiResponse<PlaceDto[]>>('/api/v1/places/search', { query: params });
}

export async function getPlaceDetail(placeId: number) {
  return api<ApiResponse<any>>(`/api/v1/places/${placeId}`);
}
