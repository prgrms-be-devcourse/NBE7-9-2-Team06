"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Navigation } from "@/components/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useToast } from "@/hooks/use-toast"
import { getAuthToken } from "@/lib/auth"
import { Star, Plus, Pencil, Trash2, MapPin, Loader2, List } from "lucide-react" // Loader2, List 아이콘 추가

// --- 포인트 관련 타입 ---
interface PlaceInfo { placeId: number; placeName: string; fullAddress: string; }
interface PointTransaction { pointId: number; place: PlaceInfo; hasImage: boolean; createdDate: string; points: number; description: string; }
interface PointHistoryResponse { totalPoints: number; history: PointTransaction[]; }

interface Pet { id: number | string; name: string; gender?: string; birthDate?: string; type?: string }
interface UserInfo { id: number; nickname: string; userEmail: string; createdDate: string; address: string; point: number; earnablePoints: number; totalReviews: number }

interface MyReviewPlaceInfo {
  placeId: number;
  placeName: string;
  fullAddress: string;
}
interface MyReviewResponse {
  reviewId: number;
  place: MyReviewPlaceInfo;
  rating: number;
  content: string;
  imageUrl: string | null; 
  createdDate: string;
  pointsAwarded: number;
}

interface MyPageData { userInfo: UserInfo; pets: Pet[] }

const API_BASE_URL = "http://localhost:8080/api/v1"
const JWT = `Bearer ${getAuthToken()}` || "";

export default function MyPage() {
  const router = useRouter()
  const { toast } = useToast()

  const [userInfo, setUserInfo] = useState<UserInfo | null>(null)
  const [pets, setPets] = useState<Pet[]>([])
  const [reviews, setReviews] = useState<MyReviewResponse[]>([])
  const [reviewsLoading, setReviewsLoading] = useState(false);
  const [pointHistory, setPointHistory] = useState<PointHistoryResponse | null>(null)
  const [dailyPointsEarned, setDailyPointsEarned] = useState(0)
  const [animatedPoints, setAnimatedPoints] = useState(0)
  const [isLoadingPoints, setIsLoadingPoints] = useState(false)

  const [showPetDialog, setShowPetDialog] = useState(false)
  const [editingPet, setEditingPet] = useState<Pet | null>(null)
  const [petForm, setPetForm] = useState({ name: "", gender: "", birthDate: "", type: "" })
  const [showDeleteDialog, setShowDeleteDialog] = useState(false)
  const [deletingPet, setDeletingPet] = useState<Pet | null>(null)
  const [showReviewsDialog, setShowReviewsDialog] = useState(false)
  const [showPointHistoryDialog, setShowPointHistoryDialog] = useState(false);

  /** 마이페이지 정보 API 호출 */
  const fetchMyPageData = async () => {
    try {
      const res = await fetch(`${API_BASE_URL}/my-page`, {
        headers: { Authorization: JWT },
      })
      const data: { code: string; message: string; data: MyPageData } = await res.json()
      if (res.ok) {
        setUserInfo(data.data.userInfo)
        setPets(data.data.pets)
        setDailyPointsEarned(data.data.userInfo.earnablePoints)
        setAnimatedPoints(data.data.userInfo.point)
      } else {
        toast({ title: data.message, variant: "destructive" })
      }
    } catch (err) {
      toast({ title: "마이페이지 정보를 불러오는 중 오류가 발생했습니다.", variant: "destructive" })
    }
  }

  /** '내가 남긴 리뷰' API 호출 함수 (fetch 사용) */
  const fetchMyReviews = async () => {
    setReviewsLoading(true);
    try {
      const res = await fetch(`${API_BASE_URL}/my/reviews`, { // (수정) 직접 API 호출
        headers: { Authorization: JWT },
      });
      // (수정) 직접 API 응답 처리 (ApiResponse 형태 가정)
      const result: { code: string; message: string; data: MyReviewResponse[] } = await res.json();
      if (res.ok) {
        setReviews(result.data || []);
      } else {
        throw new Error(result.message || `리뷰 내역 로딩 실패 (${res.status})`);
      }
    } catch (e: any) {
      toast({ title: "리뷰 내역 로딩 실패", description: e.message, variant: "destructive" });
      setReviews([]);
    } finally {
      setReviewsLoading(false);
    }
  }

  /** 포인트 내역 API 호출 (첫 번째 코드 방식) */
  const fetchPointHistory = async () => {
    setIsLoadingPoints(true)
    try {
      const response = await fetch(`${API_BASE_URL}/my/points`, {
        headers: { Authorization: JWT }
      })
      if (!response.ok) throw new Error(`포인트 내역 로딩 실패 (${response.status})`)
      const result: { code: string; message: string; data: PointHistoryResponse } = await response.json()
      setPointHistory(result.data)
    } catch (error: any) {
      console.error("Error fetching point history:", error)
      toast({ title: "오류 발생", description: error.message, variant: "destructive"})
      setPointHistory(null)
    } finally {
      setIsLoadingPoints(false)
    }
  }

  const calculatePetAge = (birthDate: string) => {
    if (!birthDate) return null
    const birth = new Date(birthDate)
    const today = new Date()
    let years = today.getFullYear() - birth.getFullYear()
    let months = today.getMonth() - birth.getMonth()
    const days = today.getDate() - birth.getDate()
    if (days < 0) months--
    if (months < 0) { years--; months += 12 }
    if (months <= 0) return `1개월 미만`
    else if (years <= 0) return `${months}개월`
    return `${years}년 ${months}개월`
  }

  useEffect(() => {
    if (!JWT) router.push("/login")
    fetchMyPageData()
    fetchMyReviews()
    fetchPointHistory() // 포인트 내역 호출
  }, [])

  // 포인트 바 애니메이션
  useEffect(() => {
    if (!userInfo) return
    const maxDaily = 1000
    const remaining = dailyPointsEarned
    const duration = 3000
    const steps = 60
    const decrement = (userInfo.point - remaining) / steps

    let currentStep = 0
    const interval = setInterval(() => {
      currentStep++
      const easeOut = 1 - Math.pow(1 - currentStep / steps, 3)
      const newValue = userInfo.point - decrement * steps * easeOut
      setAnimatedPoints(Math.max(remaining, Math.round(newValue)))
      if (currentStep >= steps) clearInterval(interval)
    }, duration / steps)

    return () => clearInterval(interval)
  }, [userInfo, dailyPointsEarned])

  /** 반려동물 등록/수정 API */
  const savePet = async () => {
    if (!petForm.name.trim()) {
      toast({ title: "이름은 필수 항목입니다", variant: "destructive" })
      return
    }

    try {
      const res = await fetch(editingPet ? `${API_BASE_URL}/update-pet/${editingPet.id}` : `${API_BASE_URL}/create-pet`, {
        method: editingPet ? "PATCH" : "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: JWT,
        },
        body: JSON.stringify(petForm),
      })
      const data = await res.json()
      if (!res.ok) {
        toast({ title: data.data, variant: "destructive" })
        return
      }

      if (editingPet) {
        setPets(pets.map((p) => (p.id === editingPet.id ? { ...p, ...petForm } : p)))
        toast({ title: "수정 완료", description: data.message })
      } else {
        setPets([...pets, { id: data.data.id, ...petForm }])
        toast({ title: "등록 완료", description: data.message })
      }

      setShowPetDialog(false)
      setPetForm({ name: "", gender: "", birthDate: "", type: "" })
      setEditingPet(null)
    } catch (err) {
      toast({ title: "반려동물 저장 중 오류가 발생했습니다.", variant: "destructive" })
    }
  }

  /** 반려동물 삭제 API */
  const deletePet = async () => {
    if (!deletingPet) return
    try {
      const res = await fetch(`${API_BASE_URL}/delete-pet/${deletingPet.id}`, {
        method: "DELETE",
        headers: { Authorization: JWT },
      })
      const data = await res.json()
      if (!res.ok) {
        toast({ title: data.message, variant: "destructive" })
        return
      }

      setPets(pets.filter((p) => p.id !== deletingPet.id))
      toast({ title: "삭제 완료", description: data.message })
      setShowDeleteDialog(false)
      setDeletingPet(null)
    } catch (err) {
      toast({ title: "삭제 중 오류가 발생했습니다.", variant: "destructive" })
    }
  }

  if (!userInfo) return null

  const maxDailyPoints = 1000
  const remainingPoints = dailyPointsEarned
  const progressPercentage = (animatedPoints / maxDailyPoints) * 100

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      <main className="container mx-auto px-4 py-8">
        <h1 className="mb-6 text-3xl font-bold text-balance">마이페이지</h1>
        <div className="grid gap-6 lg:grid-cols-2">
          {/* 사용자 정보 카드 */}
          <Card>
            <CardHeader>
              <CardTitle>사용자 정보</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="flex justify-between">
                <span className="text-muted-foreground">닉네임</span>
                <span className="font-medium">{userInfo.nickname}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">이메일</span>
                <span className="font-medium">{userInfo.userEmail}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">가입날짜</span>
                <span className="font-medium">{new Date(userInfo.createdDate).toLocaleDateString()}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">주소</span>
                <span className="font-medium">{userInfo.address}</span>
              </div>
              <div className="flex justify-between border-t pt-2 mt-2"></div>
              <div className="flex justify-between">
                <button
                  onClick={() => setShowReviewsDialog(true)}
                  className="text-muted-foreground hover:text-primary"
                >
                  내가 남긴 리뷰
                </button>
                <span className="font-medium text-primary">{userInfo.totalReviews}개</span>
                 {/* [추가기능] 북마크 추가 구현되면 북마크 기능 구현 예정 */}
              </div>
            </CardContent>
          </Card>

          {/* 포인트 정보 카드 */}
          <Card>
            <CardHeader><CardTitle>포인트 정보</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <div className="flex justify-between">
                <span className="text-muted-foreground">보유 포인트</span>
                <span className="text-2xl font-bold text-primary">{userInfo.point}P</span>
              </div>
              <div className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">오늘 획득 가능한 잔여 포인트</span>
                  <span className="font-medium">{remainingPoints}P</span>
                </div>
                <div className="h-4 overflow-hidden rounded-full bg-muted">
                  <div className="h-full bg-primary transition-all duration-300 ease-out" style={{ width: `${progressPercentage}%` }} />
                </div>
                <p className="text-xs text-muted-foreground">일일 최대 {maxDailyPoints}P 획득 가능</p>
              </div>
              <div className="flex justify-between">
                <button onClick={() => setShowPointHistoryDialog(true)} className="text-muted-foreground hover:text-primary">
                  포인트 적립 내역
                </button>
                <span className="font-medium text-primary">
                    {isLoadingPoints ? <Loader2 className="h-4 w-4 animate-spin"/> : `${pointHistory?.totalPoints ?? 0}P`}
                </span>
              </div>
            </CardContent>
          </Card>

          {/* 반려동물 정보 카드 */}
          <Card className="lg:col-span-2">
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>반려동물 정보</CardTitle>
              <Button onClick={() => { setEditingPet(null); setPetForm({ name: "", gender: "", birthDate: "", type: "" }); setShowPetDialog(true) }} size="sm">
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
                          {pet.type && <p className="text-sm text-muted-foreground">{pet.type}</p>}
                        </div>
                        <div className="flex gap-1">
                          <Button variant="ghost" size="icon" onClick={() => { setEditingPet(pet); setPetForm({ name: pet.name, gender: pet.gender || "", birthDate: pet.birthDate || "", type: pet.type || "" }); setShowPetDialog(true) }}>
                            <Pencil className="h-4 w-4" />
                          </Button>
                          <Button variant="ghost" size="icon" onClick={() => { setDeletingPet(pet); setShowDeleteDialog(true) }}>
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </div>
                      </div>
                      <div className="space-y-1 text-sm">
                        {pet.gender && (
                          <div className="flex justify-between">
                            <span className="text-muted-foreground">성별</span>
                            <span>{pet.gender === "Male" ? "남자" : "여자"}</span>
                          </div>
                        )}
                        {pet.birthDate && (
                          <div className="flex justify-between">
                            <span className="text-muted-foreground">나이</span>
                            <span>{calculatePetAge(pet.birthDate)}</span>
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

      {/* 리뷰 다이얼로그 */}
      <Dialog open={showReviewsDialog} onOpenChange={setShowReviewsDialog}>
        <DialogContent className="max-h-[80vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>내가 남긴 리뷰</DialogTitle>
            <DialogDescription className="sr-only">내가 남긴 리뷰 목록입니다.</DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            {reviewsLoading ? (
              <div className="flex justify-center py-4">
                <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
              </div>
            ) : reviews.length === 0 ? (
              <p className="text-center text-muted-foreground py-4">작성한 리뷰가 없습니다.</p>
            ) : (
              reviews.map((review) => (
                <Card key={review.reviewId}>
                  <CardContent className="p-4">
                    <div className="mb-2 flex items-center gap-2">
                      <MapPin className="h-4 w-4 text-primary" />
                      <span className="font-semibold text-primary">{review.place.placeName}</span>
                    </div>
                    {review.imageUrl && <img src={review.imageUrl} alt="리뷰 이미지" className="mb-2 h-32 w-full rounded object-cover" />}
                    <p className="text-sm text-pretty">{review.content}</p>
                    <div className="mt-2 flex items-center gap-1">
                      {Array.from({ length: 5 }).map((_, i) => (
                        <Star key={i} className={`h-3 w-3 ${i < review.rating ? "fill-secondary text-secondary" : "text-muted"}`} />
                      ))}
                    </div>
                    <p className="mt-1 text-xs text-muted-foreground">{review.createdDate}</p>
                  </CardContent>
                </Card>
              ))
            )}
          </div>
        </DialogContent>
      </Dialog>

      {/* 포인트 내역 다이얼로그 */}
      <Dialog open={showPointHistoryDialog} onOpenChange={setShowPointHistoryDialog}>
        <DialogContent className="max-h-[80vh] overflow-y-auto">
          <DialogHeader><DialogTitle>포인트 적립 내역</DialogTitle></DialogHeader>
          {isLoadingPoints ? (
            <div className="flex justify-center py-4"><Loader2 className="h-6 w-6 animate-spin text-muted-foreground" /></div>
          ) : pointHistory && pointHistory.history.length > 0 ? (
             <ul className="space-y-2 text-sm pr-2">
                {pointHistory.history.map(tx => (
                    <li key={tx.pointId} className="flex justify-between items-center border-b pb-2 last:border-b-0">
                        <div>
                            <span className="font-medium">{tx.description}</span>
                            {tx.place && tx.place.placeName && (<p className="text-xs text-muted-foreground">({tx.place.placeName})</p>)}
                        </div>
                        <div className="text-right flex-shrink-0 ml-4">
                             <span className={`font-semibold ${tx.points > 0 ? 'text-green-600' : 'text-red-600'}`}>{tx.points > 0 ? '+' : ''}{tx.points}P</span>
                             <p className="text-xs text-muted-foreground">{tx.createdDate}</p>
                        </div>
                    </li>
                ))}
            </ul>
          ) : (
            <p className="text-center text-muted-foreground py-4">포인트 내역이 없습니다.</p>
          )}
        </DialogContent>
      </Dialog>

      {/* 반려동물 등록/수정 & 삭제 다이얼로그 */}
      <Dialog open={showPetDialog} onOpenChange={setShowPetDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editingPet ? "반려동물 수정" : "반려동물 등록"}</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="petName">이름 (필수)</Label>
              <Input id="petName" value={petForm.name} onChange={(e) => setPetForm({ ...petForm, name: e.target.value })} />
            </div>
            <div className="space-y-2">
              <Label htmlFor="petGender">성별 (필수)</Label>
              <Select value={petForm.gender} onValueChange={(value) => setPetForm({ ...petForm, gender: value })}>
                <SelectTrigger><SelectValue placeholder="성별 선택" /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="Male">남</SelectItem>
                  <SelectItem value="Female">여</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="petBirthDate">생년월일</Label>
              <Input id="petBirthDate" type="date" value={petForm.birthDate} onChange={(e) => setPetForm({ ...petForm, birthDate: e.target.value })} />
            </div>
            <div className="space-y-2">
              <Label htmlFor="petType">품종</Label>
              <Input id="petType" value={petForm.type} onChange={(e) => setPetForm({ ...petForm, type: e.target.value })} />
            </div>
            <div className="flex gap-2">
              <Button variant="outline" onClick={() => setShowPetDialog(false)} className="flex-1">취소</Button>
              <Button onClick={savePet} className="flex-1">{editingPet ? "수정" : "등록"}</Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>

      {/* 삭제 확인 다이얼로그 */}
      <Dialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>반려동물 삭제</DialogTitle>
          </DialogHeader>
          <p className="text-sm text-muted-foreground">정말 삭제하시겠습니까?</p>
          <div className="flex gap-2">
            <Button variant="outline" onClick={() => setShowDeleteDialog(false)} className="flex-1">취소</Button>
            <Button variant="destructive" onClick={deletePet} className="flex-1">삭제</Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  )
}
