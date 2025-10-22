"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
// 기존 isAuthenticated 사용 유지
import { isAuthenticated } from "@/lib/auth"
import { Navigation } from "@/components/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
// 기존 mock 데이터 import 유지
import { mockUserReviews, mockPets, mockPlaces, mockBookmarks, type Pet } from "@/lib/mock-data"
import { Star, Plus, Pencil, Trash2, MapPin, Loader2, List } from "lucide-react" // Loader2, List 아이콘 추가
import { useToast } from "@/hooks/use-toast"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"

// --- API 응답 타입 정의 (포인트 관련) ---
interface PlaceInfo { placeId: number; placeName: string; fullAddress: string; }
interface PointTransaction { pointId: number; place: PlaceInfo; hasImage: boolean; createdDate: string; points: number; description: string; }
interface PointHistoryResponse { totalPoints: number; history: PointTransaction[]; }
interface ApiResponse<T> { code: string; message: string; data: T; }
const API_BASE_URL = "http://localhost:8080/api/v1";
// --- //

export default function MyPage() {
  const router = useRouter()
  const { toast } = useToast()
  // --- 기존 상태 변수들 (mock 데이터 기반) ---
  const [showReviews, setShowReviews] = useState(false)
  const [showBookmarks, setShowBookmarks] = useState(false)
  const [showPetDialog, setShowPetDialog] = useState(false)
  const [showDeleteDialog, setShowDeleteDialog] = useState(false)
  const [editingPet, setEditingPet] = useState<Pet | null>(null)
  const [deletingPet, setDeletingPet] = useState<Pet | null>(null)
  const [pets, setPets] = useState<Pet[]>(mockPets) // 펫 목록 상태 (mock)
  const [petForm, setPetForm] = useState({ name: "", gender: "", birthDate: "", breed: "" })
  // --- //

  // --- 포인트 관련 상태 변수 (API 연동용) ---
  const [pointHistory, setPointHistory] = useState<PointHistoryResponse | null>(null)
  const [isLoadingPoints, setIsLoadingPoints] = useState(true)
  const [showPointHistoryDialog, setShowPointHistoryDialog] = useState(false); // 포인트 내역 Dialog 상태
  // --- //

  // --- 인증 체크 및 초기 데이터 로드 ---
  useEffect(() => {
    if (!isAuthenticated()) {
      router.push("/login")
    } else {
      fetchPointHistory(); // 포인트 내역 API 호출
    }
  }, [router])

  // --- 포인트 내역 API 호출 함수 ---
  const fetchPointHistory = async () => {
    setIsLoadingPoints(true);
    // 함수 내에서 직접 테스트 토큰 사용
    const testToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjIsImlhdCI6MTc2MTEwMDUwNiwiZXhwIjoxNzYyMDAwNTA2fQ.WBaFYh6XopxZ2ewbylyxemSLlZx72UglKxpg6dWKo_E";
    try {
      const response = await fetch(`${API_BASE_URL}/my/points`, {
        headers: { 'Authorization': `Bearer ${testToken}` }
      });
      if (!response.ok) {
        let errorMessage = `포인트 내역 로딩 실패 (${response.status})`;
        try {
          const errorData: ApiResponse<null> | { message?: string } = await response.json();
          if (typeof errorData === 'object' && errorData && 'message' in errorData && errorData.message) { errorMessage = errorData.message; }
        } catch (e) {}
        throw new Error(errorMessage);
      }
      const result: ApiResponse<PointHistoryResponse> = await response.json();
      setPointHistory(result.data);
    } catch (error: any) {
      console.error("Error fetching point history:", error);
      toast({ title: "오류 발생", description: error.message, variant: "destructive"});
      setPointHistory(null);
    } finally { setIsLoadingPoints(false); }
  }
  // --- //

  // --- 기존 함수들 (mock 데이터 기반 유지) ---
  const getPlaceName = (placeId: string): string => { return mockPlaces.find(p=>p.id === placeId)?.name ?? "알 수 없는 장소"; }
  const handleAddPet = () => { setEditingPet(null); setPetForm({ name: "", gender: "", birthDate: "", breed: "" }); setShowPetDialog(true); }
  const handleEditPet = (pet: Pet) => { setEditingPet(pet); setPetForm({ name: pet.name, gender: pet.gender || "", birthDate: pet.birthDate || "", breed: pet.breed || "" }); setShowPetDialog(true); }
  const handleDeletePet = (pet: Pet) => { setDeletingPet(pet); setShowDeleteDialog(true); }
  const confirmDelete = () => { if (deletingPet) { setPets(pets.filter((p) => p.id !== deletingPet.id)); toast({ title: "삭제 완료" }); } setShowDeleteDialog(false); setDeletingPet(null); }
  const handleSavePet = () => { if (!petForm.name.trim()) { toast({ title: "이름 필수", variant: "destructive" }); return; } if (editingPet) { if (editingPet.userId !== "currentUser") { /* 권한 체크 */ } setPets(pets.map((p) => (p.id === editingPet.id ? { ...p, ...petForm } : p))); toast({ title: "수정 완료" }); } else { const newPet: Pet = { id: `pet-${Date.now()}`, userId: "currentUser", ...petForm }; setPets([...pets, newPet]); toast({ title: "등록 완료" }); } setShowPetDialog(false); setPetForm({ name: "", gender: "", birthDate: "", breed: "" }); }
  // --- //

  if (!isAuthenticated()) { return null }

  // --- 포인트 UI 계산 로직 (API 데이터 기반) ---
  const maxDailyPoints = 1000;
  const todayStr = new Date().toISOString().split('T')[0];
  const dailyPointsEarned = pointHistory?.history?.filter(tx => tx.createdDate === todayStr && tx.points > 0)?.reduce((sum, tx) => sum + tx.points, 0) ?? 0;
  const remainingPoints = Math.max(0, maxDailyPoints - dailyPointsEarned);
  const progressPercentage = pointHistory ? (dailyPointsEarned / maxDailyPoints) * 100 : 0;
  // --- //

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      <main className="container mx-auto px-4 py-8">
        <h1 className="mb-6 text-3xl font-bold text-balance">마이페이지</h1>
        <div className="grid gap-6 lg:grid-cols-2">

          {/* === 사용자 정보 Card (포인트 내역 버튼 추가) === */}
          <Card>
            <CardHeader><CardTitle>사용자 정보</CardTitle></CardHeader>
            <CardContent className="space-y-2">
              <div className="flex justify-between"><span className="text-muted-foreground">닉네임</span><span className="font-medium">펫러버</span></div>
              <div className="flex justify-between"><span className="text-muted-foreground">이메일</span><span className="font-medium">petlover@example.com</span></div>
              <div className="flex justify-between"><span className="text-muted-foreground">가입날짜</span><span className="font-medium">2024-01-01</span></div>
              <div className="flex justify-between"><span className="text-muted-foreground">주소</span><span className="font-medium">서울시 강남구 테헤란로 123</span></div>
              <div className="flex justify-between border-t pt-2 mt-2">
                <button onClick={() => setShowReviews(true)} className="text-muted-foreground hover:text-primary">내가 남긴 리뷰</button>
                <span className="font-medium text-primary">{mockUserReviews.length}개</span>
              </div>
              <div className="flex justify-between">
                <button onClick={() => setShowBookmarks(true)} className="text-muted-foreground hover:text-primary">북마크 목록</button>
                <span className="font-medium text-primary">{mockBookmarks.length}개</span>
              </div>
            </CardContent>
          </Card>

          {/* === 포인트 정보 Card (요약 정보만 표시) === */}
          <Card>
            <CardHeader><CardTitle>포인트 정보</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              {isLoadingPoints ? (
                 <div className="flex justify-center py-4"><Loader2 className="h-6 w-6 animate-spin text-muted-foreground" /></div>
              ) : pointHistory ? (
                <>
                  <div className="space-y-2">
                    <div className="flex justify-between text-sm">
                      <span className="text-muted-foreground">오늘 획득 가능한 잔여 포인트</span>
                      <span className="font-medium">{remainingPoints}P</span>
                    </div>
                    <div className="h-4 overflow-hidden rounded-full bg-muted">
                      <div className="h-full bg-primary" style={{ width: `${progressPercentage}%` }} />
                    </div>
                    <p className="text-xs text-muted-foreground">일일 최대 {maxDailyPoints}P 획득 가능</p>
                  </div>
                </>
              ) : (
                <p className="text-center text-muted-foreground">포인트 정보를 불러오지 못했습니다.</p>
              )}
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

          {/* === 반려동물 정보 Card (기존 mock 데이터 + 렌더링 코드 복구) === */}
          <Card className="lg:col-span-2">
             <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>반려동물 정보</CardTitle>
              <Button onClick={handleAddPet} size="sm"><Plus className="mr-2 h-4 w-4" />반려동물 등록</Button>
            </CardHeader>
            <CardContent>
              <div className="grid gap-4 sm:grid-cols-2">
                 {pets.length === 0 ? (<p className="col-span-full text-center text-sm text-muted-foreground">등록된 반려동물이 없습니다.</p>) : (
                    pets.map((pet) => (
                      <Card key={pet.id}>
                        <CardContent className="p-4">
                          <div className="mb-3 flex items-start justify-between">
                            <div>
                              <h3 className="font-semibold">{pet.name}</h3>
                              {pet.breed && <p className="text-sm text-muted-foreground">{pet.breed}</p>}
                            </div>
                            <div className="flex gap-1">
                              <Button variant="ghost" size="icon" onClick={() => handleEditPet(pet)}><Pencil className="h-4 w-4" /></Button>
                              <Button variant="ghost" size="icon" onClick={() => handleDeletePet(pet)}><Trash2 className="h-4 w-4" /></Button>
                            </div>
                          </div>
                          <div className="space-y-1 text-sm">
                            {pet.gender && (<div className="flex justify-between"><span>성별</span><span>{pet.gender}</span></div>)}
                            {pet.birthDate && (<div className="flex justify-between"><span>생년월일</span><span>{pet.birthDate}</span></div>)}
                          </div>
                        </CardContent>
                      </Card>
                    ))
                 )}
               </div>
            </CardContent>
          </Card>
        </div>
      </main>

      {/* === Dialogs (기존 mock 데이터 기반 유지 + 포인트 Dialog 추가) === */}
      {/* 내가 남긴 리뷰 Dialog (Mock) */}
      <Dialog open={showReviews} onOpenChange={setShowReviews}>
         <DialogContent className="max-h-[80vh] overflow-y-auto">
          <DialogHeader><DialogTitle>내가 남긴 리뷰</DialogTitle></DialogHeader>
          <div className="space-y-4">
            {mockUserReviews.map((review) => (
              <Card key={review.id}><CardContent className="p-4">
                  <div className="mb-2 flex items-center gap-2"><MapPin className="h-4 w-4 text-primary" /><span className="font-semibold text-primary">{getPlaceName(review.placeId)}</span></div>
                  {review.image && (<img src={review.image || "/placeholder.svg"} alt="리뷰 이미지" className="mb-2 h-32 w-full rounded object-cover"/>)}
                  <p className="text-sm text-pretty">{review.content}</p>
                  <div className="mt-2 flex items-center gap-1">{Array.from({ length: 5 }).map((_, i) => (<Star key={i} className={`h-3 w-3 ${i < review.rating ? "fill-secondary text-secondary" : "text-muted"}`}/>))}</div>
                  <p className="mt-1 text-xs text-muted-foreground">{review.date}</p>
              </CardContent></Card>
            ))}
          </div>
        </DialogContent>
      </Dialog>
      {/* 북마크 목록 Dialog (Mock) */}
      <Dialog open={showBookmarks} onOpenChange={setShowBookmarks}>
         <DialogContent className="max-h-[80vh] overflow-y-auto">
          <DialogHeader><DialogTitle>북마크 목록</DialogTitle></DialogHeader>
          <div className="space-y-4">
            {mockBookmarks.map((place) => (
              <Card key={place.id}><CardContent className="p-4">
                 <div className="mb-2 flex items-start justify-between"><div><h3 className="font-semibold text-lg">{place.name}</h3><p className="mt-1 text-sm text-muted-foreground text-pretty">{place.description}</p></div></div>
                 <div className="mt-3 space-y-1 text-sm"><div className="flex items-start gap-2"><MapPin className="mt-0.5 h-4 w-4 flex-shrink-0 text-muted-foreground" /><span className="text-muted-foreground">{place.address}</span></div><div className="flex items-center gap-1"><Star className="h-4 w-4 fill-secondary text-secondary" /><span className="font-medium">{place.rating}</span></div></div>
              </CardContent></Card>
            ))}
          </div>
        </DialogContent>
      </Dialog>

      {/* 포인트 내역 Dialog (API data) */}
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

      {/* Pet Dialogs (기존 코드 유지) */}
      <Dialog open={showPetDialog} onOpenChange={setShowPetDialog}>
        <DialogContent>
          <DialogHeader><DialogTitle>{editingPet ? "반려동물 수정" : "반려동물 등록"}</DialogTitle></DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2"><Label htmlFor="petName">이름 (필수)</Label><Input id="petName" value={petForm.name} onChange={(e) => setPetForm({ ...petForm, name: e.target.value })} placeholder="반려동물 이름"/></div>
            <div className="space-y-2"><Label htmlFor="petGender">성별</Label><Select value={petForm.gender} onValueChange={(value) => setPetForm({ ...petForm, gender: value })}><SelectTrigger><SelectValue placeholder="성별 선택" /></SelectTrigger><SelectContent><SelectItem value="남">남</SelectItem><SelectItem value="여">여</SelectItem></SelectContent></Select></div>
            <div className="space-y-2"><Label htmlFor="petBirthDate">생년월일</Label><Input id="petBirthDate" type="date" value={petForm.birthDate} onChange={(e) => setPetForm({ ...petForm, birthDate: e.target.value })}/></div>
            <div className="space-y-2"><Label htmlFor="petBreed">품종</Label><Input id="petBreed" value={petForm.breed} onChange={(e) => setPetForm({ ...petForm, breed: e.target.value })} placeholder="품종"/></div>
            <div className="flex gap-2"><Button variant="outline" onClick={() => setShowPetDialog(false)} className="flex-1">취소</Button><Button onClick={handleSavePet} className="flex-1">{editingPet ? "수정" : "등록"}</Button></div>
          </div>
        </DialogContent>
      </Dialog>
      <Dialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
         <DialogContent>
          <DialogHeader><DialogTitle>반려동물 삭제</DialogTitle></DialogHeader>
          <p className="text-sm text-muted-foreground">정말 삭제하시겠습니까?</p>
          <div className="flex gap-2"><Button variant="outline" onClick={() => setShowDeleteDialog(false)} className="flex-1">취소</Button><Button variant="destructive" onClick={confirmDelete} className="flex-1">삭제</Button></div>
        </DialogContent>
      </Dialog>
    </div>
  )
}