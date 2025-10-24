"use client"

import {useEffect, useMemo, useState} from "react"
import {useRouter} from "next/navigation"
import {isAuthenticated} from "@/lib/auth"
import {Navigation} from "@/components/navigation"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Slider} from "@/components/ui/slider"
import {PlaceSidebar} from "@/components/place-sidebar"
import {useToast} from "@/hooks/use-toast"
import dynamic from "next/dynamic"
import {searchPlaces, getPlaceDetail, type PlaceDto} from "./placeService"

// --- API 응답 타입 정의 (리뷰 관련) ---
interface ReviewInfo {
  reviewId: number;
  userName: string;
  content: string;
  rating: number;
  imageUrl?: string;
  createdDate: string;
}

interface PlaceReviewsResponse {
  averageRating: number;
  totalReviewCount: number;
  reviews: ReviewInfo[];
}

interface ApiResponse<T> {
  code: string;
  message: string;
  data: T;
}

const API_BASE_URL = "http://localhost:8080/api/v1";
// --- //


// 지도는 브라우저 전용
const MapView = dynamic(() => import("@/components/MapView"), {ssr: false})

const CATEGORY_MAP: Record<string, string> = {
  "동물약국": "VET_PHARMACY",
  "박물관": "MUSEUM",
  "카페": "CAFE",
  "동물병원": "VET_HOSPITAL",
  "반려동물용품": "PET_SUPPLIES",
  "미용": "GROOMING",
  "문예회관": "ART_CENTER",
  "펜션": "PENSION",
  "식당": "RESTAURANT",
  "여행지": "DESTINATION",
  "위탁관리": "DAYCARE",
  "미술관": "ART_MUSEUM",
  "기타": "ETC",
}

export default function SearchPage() {
  const router = useRouter()
  const {toast} = useToast()

  const [mounted, setMounted] = useState(false)
  useEffect(() => {
    setMounted(true)
  }, [])
  useEffect(() => {
    if (!isAuthenticated()) router.push("/login")
  }, [router])

  const [keyword, setKeyword] = useState("")
  const [radius, setRadius] = useState([10]) // km
  const [selectedCategoryLabel, setSelectedCategoryLabel] = useState<string | null>(null)

  const [userCenter, setUserCenter] = useState<[number, number] | null>(null)
  const [places, setPlaces] = useState<PlaceDto[]>([])
  const [selectedPlace, setSelectedPlace] = useState<PlaceDto | null>(null)
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const [loading, setLoading] = useState(false) // 장소 검색 로딩

  // --- 리뷰 관련 상태 추가 ---
  const [selectedPlaceReviews, setSelectedPlaceReviews] = useState<ReviewInfo[]>([])
  const [isLoadingReviews, setIsLoadingReviews] = useState(false);
  // --- //

  // 위치 권한
  useEffect(() => {
    if (!mounted) return
    if ("geolocation" in navigator) {
      navigator.geolocation.getCurrentPosition(
          (pos) => setUserCenter([pos.coords.latitude, pos.coords.longitude]),
          () => setUserCenter(null),
          {enableHighAccuracy: true, timeout: 5000, maximumAge: 0}
      )
    } else {
      setUserCenter(null)
    }
  }, [mounted])

  // 버튼용 한글 라벨
  const categories = useMemo(() => Object.keys(CATEGORY_MAP), [])

  // 장소 검색 함수 
  const runSearch = async (overrides?: { keyword?: string; categoryLabel?: string | null }) => {
    if (!userCenter) {
      toast({title: "위치 확인 필요", description: "내 위치를 확인한 후 검색해 주세요.", variant: "destructive",})
      return
    }
    const label = overrides?.categoryLabel ?? selectedCategoryLabel
    const category2 = label ? CATEGORY_MAP[label] : undefined
    const q = {
      lat: userCenter[0],
      lon: userCenter[1],
      radiusKm: radius[0],
      keyword: overrides?.keyword ?? (keyword.trim() || undefined),
      category2,
    }
    try {
      setLoading(true)
      const res = await searchPlaces(q)
      setPlaces(res.data)
    } catch (e: any) {
      toast({
        title: "검색 실패",
        description: e?.message || "잠시 후 다시 시도해 주세요.",
        variant: "destructive",
      })
    } finally {
      setLoading(false)
    }
  }

  // --- 리뷰 목록 조회 함수 추가 ---
  const fetchReviewsForPlace = async (placeId: string | number) => {
    setIsLoadingReviews(true);
    setSelectedPlaceReviews([]); // 기존 목록 초기화
    try {
      const response = await fetch(`${API_BASE_URL}/places/${placeId}/reviews`);
      if (!response.ok) {
        throw new Error('리뷰 목록 로딩 실패');
      }
      const result: ApiResponse<PlaceReviewsResponse> = await response.json();
      setSelectedPlaceReviews(result.data?.reviews || []);
      // 장소 정보 업데이트 (평점, 리뷰 수)
      if (selectedPlace && result.data && selectedPlace.id == placeId) {
        setSelectedPlace(prev => prev ? ({
          ...prev,
          averageRating: result.data.averageRating,
          totalReviewCount: result.data.totalReviewCount // PlaceDto 타입에 추가 필요 시 반영
        }) : null);
      }
    } catch (error: any) {
      console.error("Error fetching reviews:", error);
      toast({title: "오류", description: error.message, variant: "destructive"});
      setSelectedPlaceReviews([]);
    } finally {
      setIsLoadingReviews(false);
    }
  }
  // --- //

  // 검색 버튼 핸들러
  const handleSearch = () => runSearch()
  // 카테고리 클릭 핸들러
  const handleCategoryClick = (label: string) => {
    const next = label === selectedCategoryLabel ? null : label;
    setSelectedCategoryLabel(next);
    runSearch({categoryLabel: next})
  }
  // 반경 변경 핸들러
  const handleRadiusChange = (value: number[]) => setRadius(value)

  // 장소 클릭 핸들러 (리뷰 조회 호출 추가)
  const handlePlaceClick = async (place: PlaceDto) => {
    setSelectedPlace(place) // 선택된 장소 상태 업데이트
    setSidebarOpen(true) // 사이드바 열기
    await fetchReviewsForPlace(place.id); // 리뷰 목록 조회 호출
    // const detail = await getPlaceDetail(place.id)
  }

  // 리뷰 추가 후 콜백 함수 (리뷰 목록 새로고침)
  const handleReviewAdded = () => {
    if (selectedPlace) {
      fetchReviewsForPlace(selectedPlace.id); // 리뷰 목록 다시 조회
    }
  }

  if (!mounted) return null
  if (!isAuthenticated()) return null

  return (
      <div className="min-h-screen bg-background">
        <Navigation/>
        <main className="container mx-auto px-4 py-6">
          <div className="grid gap-6 lg:grid-cols-[1fr_400px]">
            <div className="space-y-4">
              {/* 검색 UI (기존 코드 유지) */}
              <div className="flex gap-2"><Input value={keyword}
                                                 onChange={(e) => setKeyword(e.target.value)}
                                                 placeholder="장소명을 입력하세요 (선택)"
                                                 onKeyDown={(e) => e.key === "Enter" && handleSearch()}/>
                <Button onClick={handleSearch}
                        disabled={loading}>{loading ? "검색 중..." : "검색"}</Button></div>
              <div className="space-y-2"><label
                  className="text-sm font-medium">반경: {radius[0]}km</label> <Slider value={radius}
                                                                                    onValueChange={handleRadiusChange}
                                                                                    min={1} max={30}
                                                                                    step={1}/></div>
              <div className="flex flex-wrap gap-2"> {categories.map((label) => (<Button key={label}
                                                                                         variant={selectedCategoryLabel === label ? "default" : "outline"}
                                                                                         onClick={() => handleCategoryClick(label)}> {label} </Button>))} </div>

              {/* 지도 + 결과 목록 (기존 코드 유지) */}
              <div className="space-y-4">
                <div className="rounded-lg border border-border bg-muted/30 p-8"><MapView
                    center={userCenter} places={places} onSelectPlace={handlePlaceClick}/></div>
                <div className="grid gap-3 sm:grid-cols-2"> {places.map((place) => (
                    <button key={place.id} onClick={() => handlePlaceClick(place)}
                            className="rounded-lg border border-border bg-card p-3 text-left transition-shadow hover:shadow-md">
                      <img src={"/placeholder.svg"} alt={place.name}
                           className="mb-2 h-32 w-full rounded object-cover"/> <h3
                        className="font-semibold text-card-foreground">{place.name}</h3> <p
                        className="text-xs text-muted-foreground">{place.category2}</p> <p
                        className="text-xs text-muted-foreground"> {Math.round(place.distanceMeters / 100) / 10}km </p>
                    </button>))} </div>
              </div>
            </div>

            {/* PlaceSidebar 호출 */}
            <PlaceSidebar
                place={selectedPlace}
                open={sidebarOpen}
                onOpenChange={setSidebarOpen}
                onReviewAdded={handleReviewAdded}
            />
          </div>
        </main>
      </div>
  )
}
