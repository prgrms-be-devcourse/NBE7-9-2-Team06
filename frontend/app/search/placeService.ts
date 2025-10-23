import { api } from '@/lib/api';

/** ====== 공용 타입 ====== */

export type PlaceDto = {
  id: number;
  name: string;
  category2: string;        // 서버에서 enum name으로 옴 (e.g., VET_PHARMACY)
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

/** ====== 상세 응답 타입 (백엔드 DTO와 동일) ====== */
export type PlaceDetailResponse = {
  id: number;
  name: string;
  category1: string;        // enum name (e.g., PET_MEDICAL)
  category2: string;        // enum name (e.g., VET_PHARMACY)
  openingHours: string | null;
  closedDays: string | null;
  parking: boolean | null;
  petAllowed: boolean | null;
  petRestriction: string | null;
  tel: string | null;
  url: string | null;
  postalCode: string | null;
  address: string;
  latitude: number;
  longitude: number;
  averageRating: number | null;
  totalReviewCount: number | null;
  rawDescription: string | null;
};

/** ====== 카테고리 한글 라벨 매핑 ====== */

/** 대분류 */
export const CATEGORY1_LABELS: Record<string, string> = {
  PET_MEDICAL: '반려의료',
  PET_TRAVEL: '반려동반여행',
  PET_CAFE_RESTAURANT: '반려동물식당카페',
  PET_SERVICE: '반려동물 서비스',
  ETC: '기타',
};

/** 중분류 */
export const CATEGORY2_LABELS: Record<string, string> = {
  VET_PHARMACY: '동물약국',
  MUSEUM: '박물관',
  CAFE: '카페',
  VET_HOSPITAL: '동물병원',
  PET_SUPPLIES: '반려동물용품',
  GROOMING: '미용',
  ART_CENTER: '문예회관',
  PENSION: '펜션',
  RESTAURANT: '식당',
  DESTINATION: '여행지',
  DAYCARE: '위탁관리',
  ART_MUSEUM: '미술관',
  ETC: '기타',
};

/** 안전한 라벨 변환 함수 */
export function getCategory1Label(v?: string | null) {
  if (!v) return '-';
  return CATEGORY1_LABELS[v] ?? v; // 매핑 없으면 원본 표시(방어)
}

export function getCategory2Label(v?: string | null) {
  if (!v) return '-';
  return CATEGORY2_LABELS[v] ?? v;
}

/** ====== API ====== */

export async function searchPlaces(params: {
  lat: number;
  lon: number;
  radiusKm: number;     // km
  keyword?: string;     // 선택
  category?: string;    // 선택 (백엔드 파라미터 키와 일치: category2)
}) {
  return api<ApiResponse<PlaceDto[]>>('/api/v1/places/search', { query: params });
}

export async function getPlaceDetail(placeId: number) {
  return api<ApiResponse<PlaceDetailResponse>>(`/api/v1/places/${placeId}`);
}
