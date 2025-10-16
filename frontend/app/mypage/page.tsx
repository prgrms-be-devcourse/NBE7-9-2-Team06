"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { isAuthenticated } from "@/lib/auth"
import { Navigation } from "@/components/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { mockUserReviews, mockPets, mockPlaces, mockBookmarks, type Pet } from "@/lib/mock-data"
import { Star, Plus, Pencil, Trash2, MapPin } from "lucide-react"
import { useToast } from "@/hooks/use-toast"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"

export default function MyPage() {
  const router = useRouter()
  const { toast } = useToast()
  const [showReviews, setShowReviews] = useState(false)
  const [showBookmarks, setShowBookmarks] = useState(false)
  const [showPetDialog, setShowPetDialog] = useState(false)
  const [showDeleteDialog, setShowDeleteDialog] = useState(false)
  const [editingPet, setEditingPet] = useState<Pet | null>(null)
  const [deletingPet, setDeletingPet] = useState<Pet | null>(null)
  const [pets, setPets] = useState<Pet[]>(mockPets)
  const [points, setPoints] = useState(1000)
  const [dailyPointsEarned, setDailyPointsEarned] = useState(350)
  const [animatedPoints, setAnimatedPoints] = useState(1000)

  const [petForm, setPetForm] = useState({
    name: "",
    gender: "",
    birthDate: "",
    breed: "",
  })

  useEffect(() => {
    if (!isAuthenticated()) {
      router.push("/login")
    } else {
      // Animate points progress bar
      const maxDaily = 1000
      const remaining = maxDaily - dailyPointsEarned
      const duration = 3000
      const steps = 60
      const decrement = (points - remaining) / steps

      let currentStep = 0
      const interval = setInterval(() => {
        currentStep++
        const easeOut = 1 - Math.pow(1 - currentStep / steps, 3)
        const newValue = points - decrement * steps * easeOut

        setAnimatedPoints(Math.max(remaining, Math.round(newValue)))

        if (currentStep >= steps) {
          clearInterval(interval)
          setAnimatedPoints(remaining)
        }
      }, duration / steps)

      return () => clearInterval(interval)
    }
  }, [router, points, dailyPointsEarned])

  const getPlaceName = (placeId: string) => {
    const place = mockPlaces.find((p) => p.id === placeId)
    return place ? place.name : "알 수 없는 장소"
  }

  const handleAddPet = () => {
    setEditingPet(null)
    setPetForm({ name: "", gender: "", birthDate: "", breed: "" })
    setShowPetDialog(true)
  }

  const handleEditPet = (pet: Pet) => {
    setEditingPet(pet)
    setPetForm({
      name: pet.name,
      gender: pet.gender || "",
      birthDate: pet.birthDate || "",
      breed: pet.breed || "",
    })
    setShowPetDialog(true)
  }

  const handleDeletePet = (pet: Pet) => {
    setDeletingPet(pet)
    setShowDeleteDialog(true)
  }

  const confirmDelete = () => {
    if (deletingPet) {
      setPets(pets.filter((p) => p.id !== deletingPet.id))
      toast({
        title: "삭제 완료",
        description: "반려동물 정보가 삭제되었습니다.",
      })
    }
    setShowDeleteDialog(false)
    setDeletingPet(null)
  }

  const handleSavePet = () => {
    if (!petForm.name.trim()) {
      toast({
        title: "이름은 필수 항목입니다",
        variant: "destructive",
      })
      return
    }

    if (editingPet) {
      // Check ownership (mock check)
      if (editingPet.userId !== "currentUser") {
        toast({
          title: "권한이 제한된 접근입니다.",
          variant: "destructive",
        })
        router.push("/")
        return
      }

      setPets(pets.map((p) => (p.id === editingPet.id ? { ...p, ...petForm } : p)))
      toast({
        title: "수정 완료",
        description: "반려동물 정보가 수정되었습니다.",
      })
    } else {
      const newPet: Pet = {
        id: `pet-${Date.now()}`,
        userId: "currentUser",
        ...petForm,
      }
      setPets([...pets, newPet])
      toast({
        title: "등록 완료",
        description: "반려동물이 등록되었습니다.",
      })
    }

    setShowPetDialog(false)
    setPetForm({ name: "", gender: "", birthDate: "", breed: "" })
  }

  if (!isAuthenticated()) {
    return null
  }

  const maxDailyPoints = 1000
  const remainingPoints = maxDailyPoints - dailyPointsEarned
  const progressPercentage = (animatedPoints / maxDailyPoints) * 100

  return (
    <div className="min-h-screen bg-background">
      <Navigation />

      <main className="container mx-auto px-4 py-8">
        <h1 className="mb-6 text-3xl font-bold text-balance">마이페이지</h1>

        <div className="grid gap-6 lg:grid-cols-2">
          <Card>
            <CardHeader>
              <CardTitle>사용자 정보</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="flex justify-between">
                <span className="text-muted-foreground">닉네임</span>
                <span className="font-medium">펫러버</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">이메일</span>
                <span className="font-medium">petlover@example.com</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">가입날짜</span>
                <span className="font-medium">2024-01-01</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">주소</span>
                <span className="font-medium">서울시 강남구 테헤란로 123</span>
              </div>
              <div className="flex justify-between">
                <button onClick={() => setShowReviews(true)} className="text-muted-foreground hover:text-primary">
                  내가 남긴 리뷰
                </button>
                <span className="font-medium text-primary">{mockUserReviews.length}개</span>
              </div>
              <div className="flex justify-between">
                <button onClick={() => setShowBookmarks(true)} className="text-muted-foreground hover:text-primary">
                  북마크 목록
                </button>
                <span className="font-medium text-primary">{mockBookmarks.length}개</span>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>포인트 정보</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex justify-between">
                <span className="text-muted-foreground">보유 포인트</span>
                <span className="text-2xl font-bold text-primary">{points}P</span>
              </div>
              <div className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">오늘 획득 가능한 잔여 포인트</span>
                  <span className="font-medium">{remainingPoints}P</span>
                </div>
                <div className="h-4 overflow-hidden rounded-full bg-muted">
                  <div
                    className="h-full bg-primary transition-all duration-300 ease-out"
                    style={{ width: `${progressPercentage}%` }}
                  />
                </div>
                <p className="text-xs text-muted-foreground">일일 최대 {maxDailyPoints}P 획득 가능</p>
              </div>
            </CardContent>
          </Card>

          <Card className="lg:col-span-2">
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>반려동물 정보</CardTitle>
              <Button onClick={handleAddPet} size="sm">
                <Plus className="mr-2 h-4 w-4" />
                반려동물 등록
              </Button>
            </CardHeader>
            <CardContent>
              <div className="grid gap-4 sm:grid-cols-2">
                {pets.map((pet) => (
                  <Card key={pet.id}>
                    <CardContent className="p-4">
                      <div className="mb-3 flex items-start justify-between">
                        <div>
                          <h3 className="font-semibold">{pet.name}</h3>
                          {pet.breed && <p className="text-sm text-muted-foreground">{pet.breed}</p>}
                        </div>
                        <div className="flex gap-1">
                          <Button variant="ghost" size="icon" onClick={() => handleEditPet(pet)}>
                            <Pencil className="h-4 w-4" />
                          </Button>
                          <Button variant="ghost" size="icon" onClick={() => handleDeletePet(pet)}>
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </div>
                      </div>
                      <div className="space-y-1 text-sm">
                        {pet.gender && (
                          <div className="flex justify-between">
                            <span className="text-muted-foreground">성별</span>
                            <span>{pet.gender}</span>
                          </div>
                        )}
                        {pet.birthDate && (
                          <div className="flex justify-between">
                            <span className="text-muted-foreground">생년월일</span>
                            <span>{pet.birthDate}</span>
                          </div>
                        )}
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>
      </main>

      <Dialog open={showReviews} onOpenChange={setShowReviews}>
        <DialogContent className="max-h-[80vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>내가 남긴 리뷰</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            {mockUserReviews.map((review) => (
              <Card key={review.id}>
                <CardContent className="p-4">
                  <div className="mb-2 flex items-center gap-2">
                    <MapPin className="h-4 w-4 text-primary" />
                    <span className="font-semibold text-primary">{getPlaceName(review.placeId)}</span>
                  </div>
                  {review.image && (
                    <img
                      src={review.image || "/placeholder.svg"}
                      alt="리뷰 이미지"
                      className="mb-2 h-32 w-full rounded object-cover"
                    />
                  )}
                  <p className="text-sm text-pretty">{review.content}</p>
                  <div className="mt-2 flex items-center gap-1">
                    {Array.from({ length: 5 }).map((_, i) => (
                      <Star
                        key={i}
                        className={`h-3 w-3 ${i < review.rating ? "fill-secondary text-secondary" : "text-muted"}`}
                      />
                    ))}
                  </div>
                  <p className="mt-1 text-xs text-muted-foreground">{review.date}</p>
                </CardContent>
              </Card>
            ))}
          </div>
        </DialogContent>
      </Dialog>

      <Dialog open={showBookmarks} onOpenChange={setShowBookmarks}>
        <DialogContent className="max-h-[80vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>북마크 목록</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            {mockBookmarks.map((place) => (
              <Card key={place.id}>
                <CardContent className="p-4">
                  <div className="mb-2 flex items-start justify-between">
                    <div className="flex-1">
                      <h3 className="font-semibold text-lg">{place.name}</h3>
                      <p className="mt-1 text-sm text-muted-foreground text-pretty">{place.description}</p>
                    </div>
                  </div>
                  <div className="mt-3 space-y-1 text-sm">
                    <div className="flex items-start gap-2">
                      <MapPin className="mt-0.5 h-4 w-4 flex-shrink-0 text-muted-foreground" />
                      <span className="text-muted-foreground">{place.address}</span>
                    </div>
                    <div className="flex items-center gap-1">
                      <Star className="h-4 w-4 fill-secondary text-secondary" />
                      <span className="font-medium">{place.rating}</span>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </DialogContent>
      </Dialog>

      <Dialog open={showPetDialog} onOpenChange={setShowPetDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editingPet ? "반려동물 수정" : "반려동물 등록"}</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="petName">이름 (필수)</Label>
              <Input
                id="petName"
                value={petForm.name}
                onChange={(e) => setPetForm({ ...petForm, name: e.target.value })}
                placeholder="반려동물 이름"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="petGender">성별</Label>
              <Select value={petForm.gender} onValueChange={(value) => setPetForm({ ...petForm, gender: value })}>
                <SelectTrigger>
                  <SelectValue placeholder="성별 선택" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="남">남</SelectItem>
                  <SelectItem value="여">여</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label htmlFor="petBirthDate">생년월일</Label>
              <Input
                id="petBirthDate"
                type="date"
                value={petForm.birthDate}
                onChange={(e) => setPetForm({ ...petForm, birthDate: e.target.value })}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="petBreed">품종</Label>
              <Input
                id="petBreed"
                value={petForm.breed}
                onChange={(e) => setPetForm({ ...petForm, breed: e.target.value })}
                placeholder="품종"
              />
            </div>

            <div className="flex gap-2">
              <Button variant="outline" onClick={() => setShowPetDialog(false)} className="flex-1">
                취소
              </Button>
              <Button onClick={handleSavePet} className="flex-1">
                {editingPet ? "수정" : "등록"}
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>

      <Dialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>반려동물 삭제</DialogTitle>
          </DialogHeader>
          <p className="text-sm text-muted-foreground">정말 삭제하시겠습니까?</p>
          <div className="flex gap-2">
            <Button variant="outline" onClick={() => setShowDeleteDialog(false)} className="flex-1">
              취소
            </Button>
            <Button variant="destructive" onClick={confirmDelete} className="flex-1">
              삭제
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  )
}
