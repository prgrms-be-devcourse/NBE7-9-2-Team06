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

export type PlaceDetailResponse = {
  id: number;
  name: string;
  category1: string;
  category2: string;
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

// (수정) 백엔드 ReviewInfo DTO와 필드명 일치
export type ReviewDetail = {
  reviewId: number; // (수정) id -> reviewId
  userName: string;
  content: string;
  rating: number;
  imageUrl: string | null;
  createdDate: string; // (수정) date -> createdDate
};

// (수정) 백엔드 PresignedUrlResponse DTO와 필드명 일치
export type PresignedUrlResponse = {
  presignedUrl: string;
  s3FilePath: string; // (수정) imageUrl -> s3FilePath
};

// (수정) 백엔드 ReviewCreateRequest DTO와 필드명 일치
export type ReviewCreateRequest = {
  placeId: number;
  content: string;
  rating: number;
  s3ImagePath: string | null; // (수정) imageUrl -> s3ImagePath
};

// (수정) 백엔드 ReviewCreateResponse DTO와 필드명 일치
export type ReviewCreateResponse = {
  reviewId: number; // (수정) id -> reviewId
  pointResultMessage: string;
};

// (추가) 백엔드 PlaceReviewsResponse DTO 타입 정의
export type PlaceReviewsResponse = {
  averageRating: number;
  totalReviewCount: number;
  reviews: ReviewDetail[]; // ReviewDetail[]이 ReviewInfo[]와 같음
};

// ... (CATEGORY1_LABELS, CATEGORY2_LABELS, CATEGORY2_OPTIONS) ...
export const CATEGORY1_LABELS: Record<string, string> = {
  PET_MEDICAL: '반려의료',
  PET_TRAVEL: '반려동반여행',
  PET_CAFE_RESTAURANT: '반려동물식당카페',
  PET_SERVICE: '반려동물 서비스',
  ETC: '기타',
};
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
export const CATEGORY2_OPTIONS: { value: keyof typeof CATEGORY2_LABELS; label: string }[] = [
  { value: 'VET_PHARMACY', label: CATEGORY2_LABELS.VET_PHARMACY },
  { value: 'MUSEUM', label: CATEGORY2_LABELS.MUSEUM },
  { value: 'CAFE', label: CATEGORY2_LABELS.CAFE },
  { value: 'VET_HOSPITAL', label: CATEGORY2_LABELS.VET_HOSPITAL },
  { value: 'PET_SUPPLIES', label: CATEGORY2_LABELS.PET_SUPPLIES },
  { value: 'GROOMING', label: CATEGORY2_LABELS.GROOMING },
  { value: 'ART_CENTER', label: CATEGORY2_LABELS.ART_CENTER },
  { value: 'PENSION', label: CATEGORY2_LABELS.PENSION },
  { value: 'RESTAURANT', label: CATEGORY2_LABELS.RESTAURANT },
  { value: 'DESTINATION', label: CATEGORY2_LABELS.DESTINATION },
  { value: 'DAYCARE', label: CATEGORY2_LABELS.DAYCARE },
  { value: 'ART_MUSEUM', label: CATEGORY2_LABELS.ART_MUSEUM },
];


export function getCategory1Label(v?: string | null) {
  if (!v) return '-';
  return CATEGORY1_LABELS[v] ?? v;
}

export function getCategory2Label(v?: string | null) {
  if (!v) return '-';
  return CATEGORY2_LABELS[v] ?? v;
}


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
  return api<ApiResponse<PlaceDetailResponse>>(`/api/v1/places/${placeId}`);
}

export async function getReviewsByPlace(placeId: number) {
  // (수정) 반환 타입을 백엔드 DTO(PlaceReviewsResponse)로 수정
  return api<ApiResponse<PlaceReviewsResponse>>(`/api/v1/places/${placeId}/reviews`);
}

export async function getPresignedUrl(filename: string) {
  return api<ApiResponse<PresignedUrlResponse>>('/api/v1/presigned-url', {
    method: 'POST',
    query: { filename },
  });
}

export async function createReview(request: ReviewCreateRequest, token: string) {
  return api<ApiResponse<ReviewCreateResponse>>('/api/v1/reviews', {
    method: 'POST',
    body: request,
    token: token,
  });
}