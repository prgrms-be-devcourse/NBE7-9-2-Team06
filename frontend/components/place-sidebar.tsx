"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Label } from "@/components/ui/label"
import {
  Star,
  Bookmark,
  X,
  Loader2,
  Clock4,
  CalendarX2,
  ParkingCircle,
  PawPrint,
  Phone,
  Globe,
  MapPin,
  Hash,
} from "lucide-react"
import { useToast } from "@/hooks/use-toast"
import type { PlaceDetailResponse, PlaceDto, ReviewDetail, ReviewCreateRequest } from "@/app/search/placeService"
import { getCategory1Label, getCategory2Label, getPresignedUrl, createReview } from "@/app/search/placeService"
import { getAuthToken } from "@/lib/auth"

interface PlaceSidebarProps {
  place: PlaceDto | null
  reviews: ReviewDetail[]
  open: boolean
  onOpenChange: (open: boolean) => void
  detail?: PlaceDetailResponse | null
  detailLoading?: boolean
  reviewsLoading?: boolean
  onReviewCreated: () => void
}

// ... (InfoRow, BoolBadge 함수 동일) ...
function InfoRow({
  icon,
  label,
  value,
}: {
  icon: React.ReactNode
  label: string
  value: React.ReactNode
}) {
  return (
    <div className="flex items-start gap-3 rounded-lg bg-muted/40 px-3 py-2">
      <div className="mt-0.5 shrink-0 text-muted-foreground">{icon}</div>
      <div className="min-w-0 flex-1">
        <div className="text-xs text-muted-foreground">{label}</div>
        <div className="truncate text-sm font-medium">{value}</div>
      </div>
    </div>
  )
}

function BoolBadge({ on }: { on: boolean | null | undefined }) {
  if (on === true)
    return <span className="rounded-full bg-emerald-100 px-2 py-0.5 text-xs font-medium text-emerald-700">가능</span>
  if (on === false)
    return <span className="rounded-full bg-rose-100 px-2 py-0.5 text-xs font-medium text-rose-700">불가</span>
  return <span className="rounded-full bg-muted px-2 py-0.5 text-xs text-muted-foreground">-</span>
}


export function PlaceSidebar({
  place,
  reviews,
  open,
  onOpenChange,
  detail,
  detailLoading,
  reviewsLoading,
  onReviewCreated,
}: PlaceSidebarProps) {
  const [showReviewDialog, setShowReviewDialog] = useState(false)
  const [reviewContent, setReviewContent] = useState("")
  const [reviewRating, setReviewRating] = useState(0)
  const [reviewImage, setReviewImage] = useState<File | null>(null)
  const [bookmarked, setBookmarked] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const { toast } = useToast()

  const handleReviewSubmit = async () => {
    // ... (유효성 검사 동일) ...
    if (reviewRating === 0) {
      toast({ title: "별점 등록은 필수입니다", variant: "destructive" })
      return
    }
    if (reviewContent.length < 30) {
      toast({ title: "본문은 30자 이상 작성 필수입니다", variant: "destructive" })
      return
    }
    const currentPlaceId = detail?.id ?? place?.id
    if (!currentPlaceId) {
      toast({ title: "장소 정보가 없습니다", variant: "destructive" })
      return
    }

    const token = getAuthToken()
    if (!token) {
      toast({ title: "로그인이 필요합니다", variant: "destructive" })
      return
    }

    if (reviewImage) {
      const validTypes = ["image/jpeg", "image/jpg", "image/png"]
      if (!validTypes.includes(reviewImage.type)) {
        toast({ title: "지원하지 않는 파일 형식입니다", variant: "destructive" })
        return
      }
    }

    setIsSubmitting(true)
    try {
      let finalS3Path: string | null = null // (수정) 변수명 변경

      if (reviewImage) {
        const presignedRes = await getPresignedUrl(reviewImage.name)
        // (수정) 백엔드 DTO에 맞게 s3FilePath로 변경
        const { presignedUrl, s3FilePath } = presignedRes.data

        const uploadRes = await fetch(presignedUrl, {
          method: "PUT",
          body: reviewImage,
          headers: {
            "Content-Type": reviewImage.type,
          },
        })

        if (!uploadRes.ok) {
          throw new Error("S3 이미지 업로드에 실패했습니다. (CORS 확인 필요)")
        }
        finalS3Path = s3FilePath // (수정)
      }

      // (수정) 백엔드 DTO에 맞게 s3ImagePath로 변경
      const reviewRequest: ReviewCreateRequest = {
        placeId: currentPlaceId,
        content: reviewContent,
        rating: reviewRating,
        s3ImagePath: finalS3Path, // (수정)
      }

      const reviewRes = await createReview(reviewRequest, token)
      
      const pointMessage = reviewRes.data?.pointResultMessage 

      onReviewCreated() // 부모 컴포넌트에 새로고침 알림

      setShowReviewDialog(false)
      setReviewContent("")
      setReviewRating(0)
      setReviewImage(null)
      
      toast({
        title: "리뷰 등록 완료",
        description: pointMessage || "리뷰가 성공적으로 등록되었습니다.",
        variant: "default",
      })

    } catch (error: any) {
      toast({
        title: "리뷰 등록 실패",
        description: error?.message || "잠시 후 다시 시도해 주세요.",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
    }
  }
  if (!place && !detail) return null

  // ... (렌더링 변수 선언 동일) ...
  const title = detail?.name ?? place?.name ?? ""
  const categoryLabel = detail
    ? `${getCategory1Label(detail.category1)} / ${getCategory2Label(detail.category2)}`
    : (place?.category2 ? getCategory2Label(place.category2) : "")
  const address = detail?.address ?? place?.address ?? ""
  const rating = (detail?.averageRating ?? place?.averageRating ?? 0) as number
  const reviewCount = detail?.totalReviewCount
  const dash = (s?: string | null) => (s && s.trim() ? s : "-")


  return (
    <>
      <Card
        className={`${open ? "block" : "hidden lg:block"} sticky top-6 h-[calc(100vh-120px)] overflow-y-auto rounded-2xl`}
      >
        {/* 헤더 */}
        <CardHeader className="flex flex-row items-start justify-between gap-3">
          <div className="min-w-0">
            <CardTitle className="text-balance text-xl">{title}</CardTitle>
            <p className="mt-1 line-clamp-1 text-sm text-muted-foreground">{categoryLabel}</p>
            <p className="mt-1 line-clamp-2 text-xs text-muted-foreground flex items-center gap-1">
              <MapPin className="h-3.5 w-3.5" />
              {address}
            </p>
            <div className="mt-2 flex items-center gap-1">
              {Array.from({ length: 5 }).map((_, i) => (
                <Star
                  key={i}
                  className={`h-4 w-4 ${i < Math.floor(rating) ? "fill-secondary text-secondary" : "text-muted"}`}
                />
              ))}
              <span className="ml-1 text-sm font-medium">{rating ? rating.toFixed(1) : "-"}</span>
              {typeof reviewCount === "number" && (
                <span className="text-xs text-muted-foreground">· 리뷰 {reviewCount}개</span>
              )}
            </div>
          </div>
          <div className="flex shrink-0 gap-2">
            <Button variant="ghost" size="icon" onClick={() => setBookmarked(!bookmarked)} className="rounded-full">
              <Bookmark className={bookmarked ? "fill-primary" : ""} />
            </Button>
            <Button variant="ghost" size="icon" className="rounded-full lg:hidden" onClick={() => onOpenChange(false)}>
              <X />
            </Button>
          </div>
        </CardHeader>

        {/* 구분선 */}
        <div className="mx-6 h-px bg-border" />

        {/* 콘텐츠 */}
        <CardContent className="space-y-6 py-6">
          {/* 상세 정보 */}
          <section className="space-y-3">
            <h3 className="text-sm font-semibold">상세 정보</h3>

            {detailLoading ? (
              <div className="flex items-center gap-2 text-sm text-muted-foreground">
                <Loader2 className="h-4 w-4 animate-spin" />
                불러오는 중…
              </div>
            ) : detail ? (
              <div className="grid gap-2">
                <InfoRow icon={<Clock4 className="h-4 w-4" />} label="영업시간" value={dash(detail.openingHours)} />
                <InfoRow icon={<CalendarX2 className="h-4 w-4" />} label="휴무일" value={dash(detail.closedDays)} />
                <InfoRow
                  icon={<ParkingCircle className="h-4 w-4" />}
                  label="주차"
                  value={<BoolBadge on={detail.parking} />}
                />
                <InfoRow
                  icon={<PawPrint className="h-4 w-4" />}
                  label="반려동물 동반"
                  value={
                    <div className="flex items-center gap-2">
                      <BoolBadge on={detail.petAllowed} />
                      <span className="text-xs text-muted-foreground">{dash(detail.petRestriction)}</span>
                    </div>
                  }
                />
                <InfoRow
                  icon={<Phone className="h-4 w-4" />}
                  label="전화번호"
                  value={
                    detail.tel ? (
                      <a className="underline underline-offset-2" href={`tel:${detail.tel}`}>
                        {detail.tel}
                      </a>
                    ) : (
                      "-"
                    )
                  }
                />
                <InfoRow
                  icon={<Globe className="h-4 w-4" />}
                  label="웹사이트"
                  value={
                    detail.url ? (
                      <a className="truncate underline underline-offset-2" href={detail.url} target="_blank" rel="noreferrer">
                        {detail.url}
                      </a>
                    ) : (
                      "-"
                    )
                  }
                />
                <InfoRow icon={<Hash className="h-4 w-4" />} label="우편번호" value={dash(detail.postalCode)} />
                {/* 원본 설명 */}
                {detail.rawDescription && (
                  <div className="rounded-lg bg-muted/40 px-3 py-2">
                    <div className="mb-1 text-xs text-muted-foreground">원본 설명</div>
                    <div className="whitespace-pre-wrap text-xs text-muted-foreground">{detail.rawDescription}</div>
                  </div>
                )}
              </div>
            ) : (
              <p className="text-sm text-muted-foreground">상세 정보를 불러오지 못했습니다.</p>
            )}
          </section>

          {/* 리뷰 작성 버튼 */}
          <section className="space-y-2">
            <Button onClick={() => setShowReviewDialog(true)} className="w-full rounded-xl">
              리뷰 등록
            </Button>
          </section>

          {/* 리뷰 리스트 */}
          <section className="space-y-3">
            <h3 className="text-sm font-semibold">리뷰</h3>
            {reviewsLoading ? (
              <div className="flex items-center gap-2 text-sm text-muted-foreground">
                <Loader2 className="h-4 w-4 animate-spin" />
                리뷰를 불러오는 중…
              </div>
            ) : reviews.length === 0 ? (
              <p className="text-sm text-muted-foreground">아직 리뷰가 없습니다.</p>
            ) : (
              <div className="space-y-3">
                {reviews.map((review) => (
                  // (수정) key prop을 DTO 필드명인 'reviewId'로 변경
                  <Card key={review.reviewId} className="rounded-xl">
                    <CardContent className="space-y-2 p-4">
                      {review.imageUrl && (
                        <img
                          src={review.imageUrl} // (중요) 이제 이곳에 S3 전체 URL이 들어옴
                          alt="리뷰 이미지"
                          className="h-32 w-full rounded-lg object-cover"
                        />
                      )}
                      <p className="text-sm leading-relaxed text-pretty">{review.content}</p>
                      <div className="flex items-center gap-1">
                        {Array.from({ length: 5 }).map((_, i) => (
                          <Star
                            key={i}
                            className={`h-3.5 w-3.5 ${i < review.rating ? "fill-secondary text-secondary" : "text-muted"}`}
                          />
                        ))}
                      </div>
                      <p className="text-xs text-muted-foreground">
                        {/* (수정) DTO 필드명인 'createdDate'로 변경 */}
                        {review.userName} · {review.createdDate}
                      </p>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </section>
        </CardContent>
      </Card>
      {/* 리뷰 작성 다이얼로그 */}
      <Dialog open={showReviewDialog} onOpenChange={setShowReviewDialog}>
        <DialogContent className="sm:max-w-lg">
          <DialogHeader>
            <DialogTitle>리뷰 작성</DialogTitle>
            <DialogDescription className="sr-only">
              이 장소에 대한 리뷰를 작성합니다. 별점, 내용, 이미지를 등록할 수 있습니다.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-5">
            <div className="space-y-2">
              <Label>별점 (필수)</Label>
              <div className="flex gap-1">
                {Array.from({ length: 5 }).map((_, i) => (
                  <button key={i} type="button" onClick={() => setReviewRating(i + 1)}>
                    <Star className={`h-6 w-6 ${i < reviewRating ? "fill-secondary text-secondary" : "text-muted"}`} />
                  </button>
                ))}
              </div>
            </div>

            <div className="space-y-2">
              <Label>내용 (30자 이상 필수)</Label>
              <Textarea
                value={reviewContent}
                onChange={(e) => setReviewContent(e.target.value)}
                placeholder="리뷰 내용을 작성하세요"
                rows={5}
              />
              <p className="text-xs text-muted-foreground">{reviewContent.length}/30자</p>
            </div>

            <div className="space-y-2">
              <Label>이미지 (선택)</Label>
              <Input type="file" accept="image/jpeg,image/jpg,image/png" onChange={(e) => setReviewImage(e.target.files?.[0] || null)} />
            </div>

            <div className="flex gap-2">
              <Button
                variant="outline"
                onClick={() => {
                  setShowReviewDialog(false)
                  setReviewContent("")
                  setReviewRating(0)
                  setReviewImage(null)
                }}
                className="flex-1"
              >
                취소
              </Button>
              <Button onClick={handleReviewSubmit} className="flex-1" disabled={isSubmitting}>
                {isSubmitting ? <Loader2 className="h-4 w-4 animate-spin" /> : "등록"}
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </>
  )
}