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
  const router = useRouter()
  const { toast } = useToast()

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault()

    if (!username || !password) {
      toast({
        title: "입력 오류",
        description: "아이디와 비밀번호를 입력해주세요.",
        variant: "destructive",
      })
      return
    }

    // Mock login - in real app, validate with backend
    setAuthToken("mock-token-" + username)
    toast({
      title: "로그인 성공",
      description: "환영합니다!",
    })
    router.push("/")
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

            <Button type="submit" className="w-full">
              로그인
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
