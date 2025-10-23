"use client"

import { useEffect, useMemo, useState } from "react"
import { useRouter } from "next/navigation"
import { isAuthenticated } from "@/lib/auth"
import { Navigation } from "@/components/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Slider } from "@/components/ui/slider"
import { mockReviews } from "@/lib/mock-data"
import { PlaceSidebar } from "@/components/place-sidebar"
import { useToast } from "@/hooks/use-toast"
import dynamic from "next/dynamic"
import { searchPlaces, getPlaceDetail, type PlaceDto } from "./placeService"

// ★ 반드시 SSR 끈 동적 import 사용
const MapView = dynamic(() => import("@/components/MapView"), { ssr: false })

export default function SearchPage() {
  const router = useRouter()
  const { toast } = useToast()

  const [mounted, setMounted] = useState(false)                // ★ 추가
  useEffect(() => { setMounted(true) }, [])                    // ★ 추가
  useEffect(() => {
    if (!isAuthenticated()) {
      router.push("/login")
    }
  }, [router])

  const [keyword, setKeyword] = useState("")
  const [radius, setRadius] = useState([10])
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null)

  const [userCenter, setUserCenter] = useState<[number, number] | null>(null)
  const [places, setPlaces] = useState<PlaceDto[]>([])
  const [selectedPlace, setSelectedPlace] = useState<PlaceDto | null>(null)
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const [loading, setLoading] = useState(false)

  // 브라우저에서만 위치 권한 시도
  useEffect(() => {
    if (!mounted) return
    if ("geolocation" in navigator) {
      navigator.geolocation.getCurrentPosition(
        (pos) => setUserCenter([pos.coords.latitude, pos.coords.longitude]),
        () => setUserCenter(null),
        { enableHighAccuracy: true, timeout: 5000, maximumAge: 0 }
      )
    } else {
      setUserCenter(null)
    }
  }, [mounted])

  const categories = useMemo(() => ["병원", "약국", "식당"], [])

  const runSearch = async (overrides?: { keyword?: string; category?: string | null }) => {
    if (!userCenter) {
      toast({ title: "위치 확인 필요", description: "내 위치를 확인한 후 검색해 주세요.", variant: "destructive" })
      return
    }
    const q = {
      lat: userCenter[0],
      lon: userCenter[1],
      radiusKm: radius[0],
      keyword: overrides?.keyword ?? (keyword.trim() || undefined),
      category: overrides?.category ?? selectedCategory ?? undefined,
    }
    try {
      setLoading(true)
      const res = await searchPlaces(q)
      setPlaces(res.data)
    } catch (e: any) {
      toast({ title: "검색 실패", description: e?.message || "잠시 후 다시 시도해 주세요.", variant: "destructive" })
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = () => runSearch()
  const handleCategoryClick = (category: string) => {
    const next = category === selectedCategory ? null : category
    setSelectedCategory(next)
    runSearch({ category: next })
  }
  const handleRadiusChange = (value: number[]) => setRadius(value)

  const handlePlaceClick = async (place: PlaceDto) => {
    setSelectedPlace(place)
    setSidebarOpen(true)
    // 상세 API 붙일 곳:
    // const detail = await getPlaceDetail(place.id)
  }

  if (!mounted) return null                                  // ★ 추가: 페이지도 마운트 전 렌더 안 함
  if (!isAuthenticated()) return null

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      <main className="container mx-auto px-4 py-6">
        <div className="grid gap-6 lg:grid-cols-[1fr_400px]">
          <div className="space-y-4">
            <div className="flex gap-2">
              <Input value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="장소를 검색하세요 (선택)"
                     onKeyDown={(e) => e.key === "Enter" && handleSearch()} />
              <Button onClick={handleSearch} disabled={loading}>{loading ? "검색 중..." : "검색"}</Button>
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium">반경: {radius[0]}km</label>
              <Slider value={radius} onValueChange={handleRadiusChange} min={1} max={10} step={1} className="w-full" />
            </div>

            <div className="flex gap-2">
              {categories.map((category) => (
                <Button key={category}
                        variant={selectedCategory === category ? "default" : "outline"}
                        onClick={() => handleCategoryClick(category)}>
                  {category}
                </Button>
              ))}
            </div>

            <div className="space-y-4">
              <div className="rounded-lg border border-border bg-muted/30 p-8">
                <MapView center={userCenter} places={places} onSelectPlace={handlePlaceClick} />
              </div>

              <div className="grid gap-3 sm:grid-cols-2">
                {places.map((place) => (
                  <button key={place.id} onClick={() => handlePlaceClick(place)}
                          className="rounded-lg border border-border bg-card p-3 text-left transition-shadow hover:shadow-md">
                    <img src={"/placeholder.svg"} alt={place.name} className="mb-2 h-32 w-full rounded object-cover" />
                    <h3 className="font-semibold text-card-foreground">{place.name}</h3>
                    <p className="text-xs text-muted-foreground">{place.category2}</p>
                    <p className="text-xs text-muted-foreground">{Math.round(place.distanceMeters / 100) / 10}km</p>
                  </button>
                ))}
              </div>
            </div>
          </div>

          <PlaceSidebar
            place={selectedPlace as any}
            reviews={mockReviews.filter((r) => r.placeId === (selectedPlace as any)?.id)}
            open={sidebarOpen}
            onOpenChange={setSidebarOpen}
          />
        </div>
      </main>
    </div>
  )
}
