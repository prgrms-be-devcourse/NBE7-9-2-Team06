import { api } from '@/lib/api';

export type PlaceDto = {
  id: number;
  name: string;
  category2: string;      // 백엔드 필드명 그대로
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

/** 백엔드가 기대하는 쿼리 키는 category2 입니다! */
export async function searchPlaces(params: {
  lat: number;
  lon: number;
  radiusKm: number;        // km (필수)
  keyword?: string;        // 선택
  category2?: string;      // ✅ 선택 (예: 'MUSEUM', 'HOSPITAL', ...)
}) {
  return api<ApiResponse<PlaceDto[]>>('/api/v1/places/search', { query: params });
}

export async function getPlaceDetail(placeId: number) {
  return api<ApiResponse<any>>(`/api/v1/places/${placeId}`);
}
