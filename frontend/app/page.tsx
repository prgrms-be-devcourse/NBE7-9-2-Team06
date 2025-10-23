"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { isAuthenticated } from "@/lib/auth"
import { Navigation } from "@/components/navigation"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"

export default function HomePage() {
  const router = useRouter()
  const [mounted, setMounted] = useState(false)       

  useEffect(() => {
    setMounted(true)                                   
    if (!isAuthenticated()) {
      router.push("/login")
    }
  }, [router])

  if (!mounted) return null                            

  const articles = [
    {
      id: 1,
      title: "반려동물 건강 관리의 중요성",
      description: "정기적인 건강 검진과 예방접종으로 반려동물의 건강을 지켜주세요.",
      image: "/healthy-pet-checkup.jpg",
      content: "반려동물의 건강은 정기적인 관리가 필수입니다. 매년 건강검진을 받고, 예방접종 일정을 꼭 지켜주세요.",
    },
    {
      id: 2,
      title: "반려동물과 함께하는 산책의 즐거움",
      description: "매일 규칙적인 산책은 반려동물의 신체적, 정신적 건강에 도움이 됩니다.",
      image: "/dog-walking-park.png",
      content: "산책은 반려동물에게 운동과 스트레스 해소의 기회를 제공합니다. 하루 30분 이상 산책을 권장합니다.",
    },
    {
      id: 3,
      title: "올바른 반려동물 영양 관리",
      description: "나이와 건강 상태에 맞는 적절한 사료 선택이 중요합니다.",
      image: "/pet-food-nutrition.jpg",
      content:
        "반려동물의 나이, 체중, 건강 상태에 따라 적절한 영양 공급이 필요합니다. 수의사와 상담하여 최적의 식단을 구성하세요.",
    },
  ]

  if (!isAuthenticated()) {
    return null
  }

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      <main className="container mx-auto px-4 py-8">
        <div className="mb-8 text-center">
          <h1 className="mb-4 text-4xl font-bold text-balance text-foreground">반려동물과 함께하는 행복한 일상</h1>
          <p className="text-lg text-muted-foreground text-pretty">
            Petplace에서 반려동물을 위한 다양한 정보와 서비스를 만나보세요
          </p>
        </div>

        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {articles.map((article) => (
            <Card key={article.id} className="overflow-hidden transition-shadow hover:shadow-lg">
              <img src={article.image || "/placeholder.svg"} alt={article.title} className="h-48 w-full object-cover" />
              <CardHeader>
                <CardTitle className="text-balance">{article.title}</CardTitle>
                <CardDescription className="text-pretty">{article.description}</CardDescription>
              </CardHeader>
              <CardContent>
                <p className="text-sm text-muted-foreground text-pretty">{article.content}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      </main>
    </div>
  )
}
