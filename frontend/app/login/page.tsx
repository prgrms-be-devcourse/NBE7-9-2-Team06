"use client"

import type React from "react"
import { useState } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { setAuthToken } from "@/lib/auth"
import { useToast } from "@/hooks/use-toast"

export default function LoginPage() {
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [loading, setLoading] = useState(false)
  const router = useRouter()
  const { toast } = useToast()

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!username || !password) {
      toast({
        title: "입력 오류",
        description: "아이디와 비밀번호를 입력해주세요.",
        variant: "destructive",
      })
      return
    }

    setLoading(true)

    try {
      const response = await fetch("http://localhost:8080/api/v1/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nickName: username, password }), // ✅ 백엔드 요청에 맞게 변경
      })

      const data = await response.json()

      if (response.ok && data.code === "200") {
        const token = data.data.token

        setAuthToken(token)

        toast({
          title: "로그인 성공",
          description: "환영합니다!",
        })
        router.push("/")
      } else {
        toast({
          title: "로그인 실패",
          description: data.message || "아이디 또는 비밀번호가 틀렸습니다.",
          variant: "destructive",
        })
      }
    } catch (error: any) {
      toast({
        title: "네트워크 오류",
        description: error.message || "로그인 중 오류가 발생했습니다.",
        variant: "destructive",
      })
    } finally {
      setLoading(false)
    }
  }

  return (
      <div className="flex min-h-screen items-center justify-center bg-background px-4">
        <Card className="w-full max-w-md">
          <CardHeader className="text-center">
            <CardTitle className="text-3xl font-bold text-primary">Petplace</CardTitle>
            <CardDescription>반려동물 종합 플랫폼에 오신 것을 환영합니다</CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleLogin} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="username">아이디</Label>
                <Input
                    id="username"
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="아이디를 입력하세요"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="password">비밀번호</Label>
                <Input
                    id="password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="비밀번호를 입력하세요"
                />
              </div>

              <Button type="submit" className="w-full" disabled={loading}>
                {loading ? "로그인 중..." : "로그인"}
              </Button>

              <div className="text-center text-sm">
                <span className="text-muted-foreground">계정이 없으신가요? </span>
                <Link href="/signup" className="text-primary hover:underline">
                  회원가입
                </Link>
              </div>
            </form>
          </CardContent>
        </Card>
      </div>
  )
}