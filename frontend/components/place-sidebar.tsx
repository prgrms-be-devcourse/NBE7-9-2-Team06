"use client"

import {useState, useEffect} from "react";
import {Button} from "@/components/ui/button";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Dialog, DialogContent, DialogHeader, DialogTitle} from "@/components/ui/dialog";
import {Input} from "@/components/ui/input";
import {Textarea} from "@/components/ui/textarea";
import {Label} from "@/components/ui/label";
import {Star, Bookmark, X, Loader2} from "lucide-react";
import {useToast} from "@/hooks/use-toast";

// --- API 응답 타입 정의 (백엔드 DTO와 일치) ---
interface PlaceInfo {
  placeId: number;
  placeName: string;
  fullAddress: string;
}

interface ReviewInfo {
  reviewId: number;
  userName: string;
  content: string;
  rating: number;
  imageUrl?: string;
  createdDate: string; // LocalDate -> string
}

interface PlaceReviewsResponse {
  averageRating: number;
  totalReviewCount: number;
  reviews: ReviewInfo[];
}

interface PresignedUrlResponse {
  presignedUrl: string;
  s3FilePath: string;
}

interface ReviewCreateResponse {
  reviewId: number;
  pointResultMessage: string;
}

interface ApiResponse<T> {
  code: string;
  message: string;
  data: T;
}

// 백엔드 Place 엔티티/DTO 필드 기준 (필요한 것만)
interface Place {
  id: number;                   // Long -> number
  uniqueKey?: string;            // uniqueKey 추가 (optional?)
  name: string;
  category1?: string;            // Category1Type Enum -> string
  category2?: string;            // Category2Type Enum -> string
  openingHours?: string;
  closedDays?: string;
  parking?: boolean;
  petAllowed?: boolean;
  petRestriction?: string;
  tel?: string;
  url?: string;
  postalCode?: string;
  address?: string;
  latitude?: number;             // Double -> number
  longitude?: number;            // Double -> number
  rawDescription?: string;       // rawDescription
  averageRating?: number;        // Double -> number
  totalReviewCount?: number;     // Integer -> number
  image?: string;                // 장소 대표 이미지 (백엔드 필드 확인)
}

// --- 인증 토큰 함수 ---
const getToken = (): string | null => {
  // 테스트용 토큰 (실제 구현 시 localStorage 사용)
  return "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjIsImlhdCI6MTc2MTI2ODM3NiwiZXhwIjoxNzYyMTY4Mzc2fQ.qJx0rSlc383j2xEW4iaeFH47jAQdnWlRF2FmpnkqcEE";
  // if (typeof window !== 'undefined') {
  //   return localStorage.getItem("accessToken");
  // }
  // return null;
}
// --- //

// --- API 엔드포인트 ---
const API_BASE_URL = "http://localhost:8080/api/v1";

// --- //

interface PlaceSidebarProps {
  place: Place | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onReviewAdded: () => void;
}

export function PlaceSidebar({place, open, onOpenChange, onReviewAdded}: PlaceSidebarProps) {
  const [showReviewDialog, setShowReviewDialog] = useState(false);
  const [reviewContent, setReviewContent] = useState("");
  const [reviewRating, setReviewRating] = useState(0);
  const [reviewImage, setReviewImage] = useState<File | null>(null);
  const [bookmarked, setBookmarked] = useState(false);
  const [apiReviews, setApiReviews] = useState<ReviewInfo[]>([]);
  const [isLoadingReviews, setIsLoadingReviews] = useState(false);
  const [isSubmittingReview, setIsSubmittingReview] = useState(false);
  const {toast} = useToast();

  useEffect(() => {
    if (place?.id) {
      fetchReviewsForPlace(place.id);
    } else {
      setApiReviews([]);
    }
  }, [place]);

  const fetchReviewsForPlace = async (placeId: string | number) => {
    setIsLoadingReviews(true);
    try {
      const response = await fetch(`${API_BASE_URL}/places/${placeId}/reviews`);
      if (!response.ok) {
        throw new Error('리뷰 목록 로딩 실패');
      }
      const result: ApiResponse<PlaceReviewsResponse> = await response.json();
      setApiReviews(result.data?.reviews || []);
    } catch (error: any) {
      console.error("Error fetching reviews:", error);
      toast({title: "오류", description: error.message, variant: "destructive"});
      setApiReviews([]);
    } finally {
      setIsLoadingReviews(false);
    }
  }

  const handleReviewSubmit = async () => {
    console.log("handleReviewSubmit called");
    if (!place) return;
    if (reviewRating === 0) {
      toast({title: "별점 필수", variant: "destructive"});
      return;
    }
    if (reviewContent.length < 30) {
      toast({title: "30자 이상 필수", variant: "destructive"});
      return;
    }
    if (reviewImage) {
      const validTypes = ["image/jpeg", "image/jpg", "image/png", "image/heic", "image/heif"];
      if (!validTypes.some(type => reviewImage.type.includes(type))) {
        toast({title: "지원하지 않는 파일 형식 (JPG, PNG, HEIC만 가능)", variant: "destructive"});
        return;
      }
    }

    setIsSubmittingReview(true);
    const token = getToken();
    if (!token) {
      toast({title: "로그인 필요", variant: "destructive"});
      setIsSubmittingReview(false);
      return;
    }

    let s3ImagePath: string | null = null;
    try {
      console.log("Attempting image upload...");
      if (reviewImage) {
        console.log("Requesting presigned URL...");
        const presignedResponse = await fetch(`${API_BASE_URL}/reviews/presigned-url?filename=${encodeURIComponent(reviewImage.name)}`, {
          method: 'POST',
          headers: {'Authorization': `Bearer ${token}`}
        });
        if (!presignedResponse.ok) {
          const err = await presignedResponse.json().catch(() => ({}));
          throw new Error(err.message || 'Presigned URL 요청 실패');
        }
        const presignedResult: ApiResponse<PresignedUrlResponse> = await presignedResponse.json();
        const {presignedUrl, s3FilePath} = presignedResult.data;
        s3ImagePath = s3FilePath;
        console.log("Got presigned URL, uploading to S3...");

        const uploadResponse = await fetch(presignedUrl, {
          method: 'PUT',
          headers: {'Content-Type': reviewImage.type},
          body: reviewImage
        });
        if (!uploadResponse.ok) {
          throw new Error('S3 업로드 실패');
        }
        console.log("S3 upload successful");
      } else {
        console.log("No image to upload");
      }

      console.log("Submitting review data...");
      const reviewPayload = {
        placeId: place.id,
        content: reviewContent,
        rating: reviewRating,
        s3ImagePath: s3ImagePath
      };
      const reviewSubmitResponse = await fetch(`${API_BASE_URL}/reviews`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json', 'Authorization': `Bearer ${token}`},
        body: JSON.stringify(reviewPayload)
      });
      const reviewSubmitResult: ApiResponse<ReviewCreateResponse> = await reviewSubmitResponse.json();
      if (!reviewSubmitResponse.ok) {
        throw new Error(reviewSubmitResult.message || '리뷰 등록 실패');
      }
      console.log("Review submission successful");

      toast({title: "리뷰 등록 완료", description: reviewSubmitResult.data.pointResultMessage});
      resetReviewFormAndCloseDialog();
      fetchReviewsForPlace(place.id);
      onReviewAdded();

    } catch (error: any) {
      console.error("리뷰 등록 중 오류 발생:", error);
      toast({title: "오류 발생", description: error.message, variant: "destructive"});
    } finally {
      setIsSubmittingReview(false);
      console.log("handleReviewSubmit finished");
    }
  }

  const resetReviewFormAndCloseDialog = () => {
    setShowReviewDialog(false);
    setReviewContent("");
    setReviewRating(0);
    setReviewImage(null);
  }

  if (!place) return null;

  return (
      <>
        <Card
            className={`${open ? "block" : "hidden lg:block"} sticky top-6 h-[calc(100vh-120px)] overflow-y-auto`}>
          <CardHeader className="flex flex-row items-start justify-between">
            <div className="flex-1">
              <CardTitle className="text-balance">{place.name}</CardTitle>
              {/* place.category 대신 category2 또는 category1 사용 */}
              <p className="text-sm text-muted-foreground">{place.category2 ?? place.category1}</p>
            </div>
            <div className="flex gap-2">
              <Button variant="ghost" size="icon" onClick={() => setBookmarked(!bookmarked)}>
                <Bookmark className={bookmarked ? "fill-primary" : ""}/> </Button>
              <Button variant="ghost" size="icon" className="lg:hidden"
                      onClick={() => onOpenChange(false)}> <X/> </Button>
            </div>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* 장소 대표 이미지 (place.image 필드가 Place 타입에 정의되어 있어야 함) */}
            <img src={place.image || "/placeholder.svg"} alt={place.name}
                 className="h-48 w-full rounded-lg object-cover"/>
            <div>
              {/* place.description 대신 rawDescription 사용 (Place 타입 정의 확인) */}
              <p className="text-sm text-pretty">{place.rawDescription ?? "설명 정보 없음"}</p>
              <p className="mt-1 text-sm text-muted-foreground">{place.address}</p>
              <div className="mt-2 flex items-center gap-1">
                {Array.from({length: 5}).map((_, i) => (<Star key={i}
                                                              className={`h-4 w-4 ${i < Math.floor(place.averageRating ?? 0) ? "fill-secondary text-secondary" : "text-muted"}`}/>))}
                <span className="ml-1 text-sm">{place.averageRating?.toFixed(1) ?? 'N/A'}</span>
              </div>
            </div>
            <Button onClick={() => setShowReviewDialog(true)} className="w-full"
                    disabled={isSubmittingReview}>
              {isSubmittingReview ? <Loader2 className="mr-2 h-4 w-4 animate-spin"/> : null} 리뷰 등록
            </Button>
            <div className="space-y-3">
              <h3 className="font-semibold">리뷰
                ({isLoadingReviews ? '...' : apiReviews.length}개)</h3>
              {isLoadingReviews ? (<div className="flex justify-center py-4"><Loader2
                      className="h-6 w-6 animate-spin text-muted-foreground"/></div>)
                  : apiReviews.length === 0 ? (
                          <p className="text-sm text-muted-foreground">아직 리뷰가 없습니다.</p>)
                      : (apiReviews.map((review) => (
                              <Card key={review.reviewId}>
                                <CardContent className="p-4">
                                  {review.imageUrl && (<img src={review.imageUrl} alt="리뷰 이미지"
                                                            className="mb-2 h-32 w-full rounded object-cover"/>)}
                                  <p className="text-sm text-pretty">{review.content}</p>
                                  <div
                                      className="mt-2 flex items-center gap-1"> {Array.from({length: 5}).map((_, i) => (
                                      <Star key={i}
                                            className={`h-3 w-3 ${i < review.rating ? "fill-secondary text-secondary" : "text-muted"}`}/>))} </div>
                                  <p className="mt-1 text-xs text-muted-foreground"> {review.userName} · {review.createdDate} </p>
                                </CardContent>
                              </Card>
                          ))
                      )}
            </div>
          </CardContent>
        </Card>

        <Dialog open={showReviewDialog} onOpenChange={setShowReviewDialog}>
          <DialogContent>
            <DialogHeader><DialogTitle>리뷰 작성</DialogTitle></DialogHeader>
            <div className="space-y-4">
              <div className="space-y-2">
                <Label>별점 (필수)</Label>
                <div className="flex gap-1"> {Array.from({length: 5}).map((_, i) => (
                    <button key={i} type="button" onClick={() => setReviewRating(i + 1)}><Star
                        className={`h-6 w-6 ${i < reviewRating ? "fill-secondary text-secondary" : "text-muted"}`}/>
                    </button>))} </div>
              </div>
              <div className="space-y-2">
                <Label>내용 (30자 이상 필수)</Label>
                <Textarea value={reviewContent} onChange={(e) => setReviewContent(e.target.value)}
                          placeholder="리뷰 내용을 작성하세요" rows={5}/>
                <p className="text-xs text-muted-foreground">{reviewContent.length}/30자</p>
              </div>
              <div className="space-y-2">
                <Label>이미지 (선택, JPG/PNG/HEIC)</Label>
                <Input type="file" accept="image/jpeg,image/jpg,image/png,image/heic,image/heif"
                       onChange={(e) => setReviewImage(e.target.files?.[0] || null)}
                       disabled={isSubmittingReview}/>
              </div>
              <div className="flex gap-2">
                <Button variant="outline" onClick={resetReviewFormAndCloseDialog} className="flex-1"
                        disabled={isSubmittingReview}>취소</Button>
                <Button onClick={handleReviewSubmit} className="flex-1"
                        disabled={isSubmittingReview}>
                  {isSubmittingReview && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>} 등록
                </Button>
              </div>
            </div>
          </DialogContent>
        </Dialog>
      </>
  )
}