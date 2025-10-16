"use client"

import type React from "react"

import { useState } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useToast } from "@/hooks/use-toast"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"

export default function SignupPage() {
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [email, setEmail] = useState("")
  const [emailDomain, setEmailDomain] = useState("gmail.com")
  const [customDomain, setCustomDomain] = useState("")
  const [address, setAddress] = useState("")
  const [showAddressPopup, setShowAddressPopup] = useState(false)
  const router = useRouter()
  const { toast } = useToast()

  const checkUsername = () => {
    // Mock duplicate check
    if (username === "admin") {
      toast({
        title: "중복된 아이디",
        description: "이미 사용 중인 아이디입니다.",
        variant: "destructive",
      })
    } else {
      toast({
        title: "사용 가능",
        description: "사용 가능한 아이디입니다.",
      })
    }
  }

  const validatePassword = (pwd: string) => {
    const hasLength = pwd.length >= 8 && pwd.length <= 12
    const hasNumber = /\d/.test(pwd)
    const hasLetter = /[a-zA-Z]/.test(pwd)
    const hasSpecial = /[!@#$%^&*]/.test(pwd)

    return hasLength && hasNumber && hasLetter && hasSpecial
  }

  const handleSignup = (e: React.FormEvent) => {
    e.preventDefault()

    if (!username) {
      toast({
        title: "입력 오류",
        description: "아이디를 입력해주세요.",
        variant: "destructive",
      })
      return
    }

    if (!validatePassword(password)) {
      toast({
        title: "비밀번호 오류",
        description: "비밀번호는 8-12자, 숫자, 영문, 특수문자를 포함해야 합니다.",
        variant: "destructive",
      })
      return
    }

    if (!email) {
      toast({
        title: "입력 오류",
        description: "이메일을 입력해주세요.",
        variant: "destructive",
      })
      return
    }

    if (!address) {
      toast({
        title: "입력 오류",
        description: "주소를 입력해주세요.",
        variant: "destructive",
      })
      return
    }

    toast({
      title: "회원가입 성공",
      description: "로그인 페이지로 이동합니다.",
    })
    router.push("/login")
  }

  const handleAddressSearch = () => {
    setShowAddressPopup(true)
  }

  const selectAddress = (addr: string) => {
    setAddress(addr)
    setShowAddressPopup(false)
  }

  const fullEmail = emailDomain === "custom" ? `${email}@${customDomain}` : `${email}@${emailDomain}`

  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4 py-8">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <CardTitle className="text-3xl font-bold text-primary">회원가입</CardTitle>
          <CardDescription>Petplace에 가입하여 다양한 서비스를 이용하세요</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSignup} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="username">아이디</Label>
              <div className="flex gap-2">
                <Input
                  id="username"
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="아이디"
                  className="flex-1"
                />
                <Button type="button" variant="outline" onClick={checkUsername}>
                  중복 확인
                </Button>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="password">비밀번호</Label>
              <Input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="8-12자, 숫자, 영문, 특수문자 포함"
              />
              <p className="text-xs text-muted-foreground">8-12자, 숫자, 영문, 특수문자를 포함해야 합니다</p>
            </div>

            <div className="space-y-2">
              <Label htmlFor="email">이메일</Label>
              <div className="flex gap-2">
                <Input
                  id="email"
                  type="text"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="이메일"
                  className="flex-1"
                />
                <span className="flex items-center">@</span>
                <Select value={emailDomain} onValueChange={setEmailDomain}>
                  <SelectTrigger className="w-[140px]">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="gmail.com">gmail.com</SelectItem>
                    <SelectItem value="naver.com">naver.com</SelectItem>
                    <SelectItem value="daum.net">daum.net</SelectItem>
                    <SelectItem value="custom">직접 입력</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              {emailDomain === "custom" && (
                <Input
                  type="text"
                  value={customDomain}
                  onChange={(e) => setCustomDomain(e.target.value)}
                  placeholder="도메인 입력"
                  className="mt-2"
                />
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="address">주소</Label>
              <div className="flex gap-2">
                <Input
                  id="address"
                  type="text"
                  value={address}
                  readOnly
                  placeholder="주소 검색 버튼을 클릭하세요"
                  className="flex-1"
                />
                <Button type="button" variant="outline" onClick={handleAddressSearch}>
                  주소 검색
                </Button>
              </div>
            </div>

            <Button type="submit" className="w-full">
              회원가입
            </Button>

            <div className="text-center text-sm">
              <span className="text-muted-foreground">이미 계정이 있으신가요? </span>
              <Link href="/login" className="text-primary hover:underline">
                로그인
              </Link>
            </div>
          </form>
        </CardContent>
      </Card>

      <Dialog open={showAddressPopup} onOpenChange={setShowAddressPopup}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>주소 검색</DialogTitle>
          </DialogHeader>
          <div className="space-y-2">
            <p className="text-sm text-muted-foreground">주소를 선택하세요</p>
            <div className="space-y-2">
              <Button
                variant="outline"
                className="w-full justify-start bg-transparent"
                onClick={() => selectAddress("서울시 강남구 테헤란로 123")}
              >
                서울시 강남구 테헤란로 123
              </Button>
              <Button
                variant="outline"
                className="w-full justify-start bg-transparent"
                onClick={() => selectAddress("서울시 서초구 서초대로 456")}
              >
                서울시 서초구 서초대로 456
              </Button>
              <Button
                variant="outline"
                className="w-full justify-start bg-transparent"
                onClick={() => selectAddress("서울시 송파구 올림픽로 789")}
              >
                서울시 송파구 올림픽로 789
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  )
}
