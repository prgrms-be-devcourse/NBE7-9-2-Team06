"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { isAuthenticated } from "@/lib/auth"
import { Navigation } from "@/components/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Slider } from "@/components/ui/slider"
import { mockPlaces, mockReviews, type Place } from "@/lib/mock-data"
import { PlaceSidebar } from "@/components/place-sidebar"
import { useToast } from "@/hooks/use-toast"

export default function SearchPage() {
  const router = useRouter()
  const { toast } = useToast()
  const [keyword, setKeyword] = useState("")
  const [radius, setRadius] = useState([5])
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null)
  const [filteredPlaces, setFilteredPlaces] = useState<Place[]>(mockPlaces)
  const [selectedPlace, setSelectedPlace] = useState<Place | null>(null)
  const [sidebarOpen, setSidebarOpen] = useState(false)

  useEffect(() => {
    if (!isAuthenticated()) {
      router.push("/login")
    } else {
      // Initial load: show all places within 5km
      filterPlaces()
    }
  }, [router])

  const categories = ["병원", "약국", "식당"]

  const filterPlaces = (searchKeyword?: string, category?: string | null) => {
    let filtered = mockPlaces.filter((place) => place.distance <= radius[0])

    if (searchKeyword) {
      filtered = filtered.filter(
        (place) => place.name.includes(searchKeyword) || place.description.includes(searchKeyword),
      )
    }

    if (category) {
      filtered = filtered.filter((place) => place.category === category)
    }

    setFilteredPlaces(filtered)
  }

  const handleSearch = () => {
    if (!keyword.trim()) {
      toast({
        title: "검색 오류",
        description: "키워드를 넣어주세요",
        variant: "destructive",
      })
      return
    }
    setSelectedCategory(null)
    filterPlaces(keyword, null)
  }

  const handleCategoryClick = (category: string) => {
    setKeyword("")
    setSelectedCategory(category)
    filterPlaces("", category)
  }

  const handleRadiusChange = (value: number[]) => {
    setRadius(value)
    filterPlaces(keyword || undefined, selectedCategory)
  }

  const handlePlaceClick = (place: Place) => {
    setSelectedPlace(place)
    setSidebarOpen(true)
  }

  if (!isAuthenticated()) {
    return null
  }

  return (
    <div className="min-h-screen bg-background">
      <Navigation />

      <main className="container mx-auto px-4 py-6">
        <div className="grid gap-6 lg:grid-cols-[1fr_400px]">
          <div className="space-y-4">
            <div className="flex gap-2">
              <Input
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                placeholder="장소를 검색하세요"
                onKeyDown={(e) => e.key === "Enter" && handleSearch()}
              />
              <Button onClick={handleSearch}>검색</Button>
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium">반경: {radius[0]}km</label>
              <Slider value={radius} onValueChange={handleRadiusChange} min={1} max={10} step={1} className="w-full" />
            </div>

            <div className="flex gap-2">
              {categories.map((category) => (
                <Button
                  key={category}
                  variant={selectedCategory === category ? "default" : "outline"}
                  onClick={() => handleCategoryClick(category)}
                >
                  {category}
                </Button>
              ))}
            </div>

            <div className="space-y-4">
              <div className="rounded-lg border border-border bg-muted/30 p-8">
                <div className="flex h-[400px] items-center justify-center text-sm text-muted-foreground">
                  지도 영역 (JavaScript Map API 통합 예정)
                </div>
              </div>

              <div className="grid gap-3 sm:grid-cols-2">
                {filteredPlaces.map((place) => (
                  <button
                    key={place.id}
                    onClick={() => handlePlaceClick(place)}
                    className="rounded-lg border border-border bg-card p-3 text-left transition-shadow hover:shadow-md"
                  >
                    <img
                      src={place.image || "/placeholder.svg"}
                      alt={place.name}
                      className="mb-2 h-32 w-full rounded object-cover"
                    />
                    <h3 className="font-semibold text-card-foreground">{place.name}</h3>
                    <p className="text-xs text-muted-foreground">{place.category}</p>
                    <p className="text-xs text-muted-foreground">{place.distance}km</p>
                  </button>
                ))}
              </div>
            </div>
          </div>

          <PlaceSidebar
            place={selectedPlace}
            reviews={mockReviews.filter((r) => r.placeId === selectedPlace?.id)}
            open={sidebarOpen}
            onOpenChange={setSidebarOpen}
          />
        </div>
      </main>
    </div>
  )
}
