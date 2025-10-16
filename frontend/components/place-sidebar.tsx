"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Label } from "@/components/ui/label"
import { Star, Bookmark, X } from "lucide-react"
import { useToast } from "@/hooks/use-toast"
import type { Place, Review } from "@/lib/mock-data"

interface PlaceSidebarProps {
  place: Place | null
  reviews: Review[]
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function PlaceSidebar({ place, reviews, open, onOpenChange }: PlaceSidebarProps) {
  const [showReviewDialog, setShowReviewDialog] = useState(false)
  const [reviewContent, setReviewContent] = useState("")
  const [reviewRating, setReviewRating] = useState(0)
  const [reviewImage, setReviewImage] = useState<File | null>(null)
  const [bookmarked, setBookmarked] = useState(false)
  const [localReviews, setLocalReviews] = useState<Review[]>(reviews)
  const { toast } = useToast()

  // Track daily review points
  const [dailyPoints, setDailyPoints] = useState(0)
  const [reviewedPlacesToday, setReviewedPlacesToday] = useState<Set<string>>(new Set())

  const handleReviewSubmit = () => {
    if (reviewRating === 0) {
      toast({
        title: "별점 등록은 필수입니다",
        variant: "destructive",
      })
      return
    }

    if (reviewContent.length < 30) {
      toast({
        title: "본문은 30자 이상 작성 필수입니다",
        variant: "destructive",
      })
      return
    }

    if (reviewImage) {
      const validTypes = ["image/jpeg", "image/jpg", "image/png"]
      if (!validTypes.includes(reviewImage.type)) {
        toast({
          title: "지원하지 않는 파일 형식입니다",
          variant: "destructive",
        })
        return
      }
    }

    // Check if already reviewed this place today
    if (place && reviewedPlacesToday.has(place.id)) {
      toast({
        title: "일일 한 장소의 리뷰 포인트 적립은 한번입니다.",
        variant: "destructive",
      })

      // Still add the review, just no points
      addReview()
      return
    }

    // Check daily limit
    const pointsToAdd = reviewImage ? 100 : 50

    if (dailyPoints + pointsToAdd > 1000) {
      toast({
        title: "일일 한도 1000p 적립으로 포인트는 제공 되지 않습니다.",
        variant: "destructive",
      })
      addReview()
      return
    }

    // Award points
    setDailyPoints((prev) => prev + pointsToAdd)
    if (place) {
      setReviewedPlacesToday((prev) => new Set(prev).add(place.id))
    }

    toast({
      title: "리뷰 등록 완료",
      description: `${pointsToAdd}포인트가 적립되었습니다!`,
    })

    addReview()
  }

  const addReview = () => {
    if (!place) return

    const newReview: Review = {
      id: `new-${Date.now()}`,
      placeId: place.id,
      userId: "currentUser",
      userName: "나",
      content: reviewContent,
      rating: reviewRating,
      image: reviewImage ? URL.createObjectURL(reviewImage) : undefined,
      date: new Date().toISOString().split("T")[0],
    }

    setLocalReviews([newReview, ...localReviews])
    setShowReviewDialog(false)
    setReviewContent("")
    setReviewRating(0)
    setReviewImage(null)
  }

  if (!place) return null

  return (
    <>
      <Card className={`${open ? "block" : "hidden lg:block"} sticky top-6 h-[calc(100vh-120px)] overflow-y-auto`}>
        <CardHeader className="flex flex-row items-start justify-between">
          <div className="flex-1">
            <CardTitle className="text-balance">{place.name}</CardTitle>
            <p className="text-sm text-muted-foreground">{place.category}</p>
          </div>
          <div className="flex gap-2">
            <Button variant="ghost" size="icon" onClick={() => setBookmarked(!bookmarked)}>
              <Bookmark className={bookmarked ? "fill-primary" : ""} />
            </Button>
            <Button variant="ghost" size="icon" className="lg:hidden" onClick={() => onOpenChange(false)}>
              <X />
            </Button>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          <img
            src={place.image || "/placeholder.svg"}
            alt={place.name}
            className="h-48 w-full rounded-lg object-cover"
          />

          <div>
            <p className="text-sm text-pretty">{place.description}</p>
            <p className="mt-1 text-sm text-muted-foreground">{place.address}</p>
            <div className="mt-2 flex items-center gap-1">
              {Array.from({ length: 5 }).map((_, i) => (
                <Star
                  key={i}
                  className={`h-4 w-4 ${i < Math.floor(place.rating) ? "fill-secondary text-secondary" : "text-muted"}`}
                />
              ))}
              <span className="ml-1 text-sm">{place.rating}</span>
            </div>
          </div>

          <Button onClick={() => setShowReviewDialog(true)} className="w-full">
            리뷰 등록
          </Button>

          <div className="space-y-3">
            <h3 className="font-semibold">리뷰</h3>
            {localReviews.length === 0 ? (
              <p className="text-sm text-muted-foreground">아직 리뷰가 없습니다.</p>
            ) : (
              localReviews.map((review) => (
                <Card key={review.id}>
                  <CardContent className="p-4">
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
                    <p className="mt-1 text-xs text-muted-foreground">
                      {review.userName} · {review.date}
                    </p>
                  </CardContent>
                </Card>
              ))
            )}
          </div>
        </CardContent>
      </Card>

      <Dialog open={showReviewDialog} onOpenChange={setShowReviewDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>리뷰 작성</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
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
              <Input
                type="file"
                accept="image/jpeg,image/jpg,image/png"
                onChange={(e) => setReviewImage(e.target.files?.[0] || null)}
              />
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
              <Button onClick={handleReviewSubmit} className="flex-1">
                등록
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </>
  )
}
