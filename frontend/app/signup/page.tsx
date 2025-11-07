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
import { CheckCircle2, XCircle, Loader2 } from "lucide-react"
import Script from "next/script"

declare global {
  interface Window {
    daum: any
  }
}

export default function SignupPage() {
  const router = useRouter()
  const { toast } = useToast()

  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [emailLocal, setEmailLocal] = useState("")
  const [emailDomain, setEmailDomain] = useState("gmail.com")
  const [customDomain, setCustomDomain] = useState("")
  const [address, setAddress] = useState("")
  const [zipcode, setZipcode] = useState("")
  const [detailAddress, setDetailAddress] = useState("")
  const [verificationCode, setVerificationCode] = useState("")

  const [usernameChecked, setUsernameChecked] = useState(false)
  const [usernameAvailable, setUsernameAvailable] = useState(false)
  const [checkingUsername, setCheckingUsername] = useState(false)

  const [emailChecked, setEmailChecked] = useState(false)
  const [emailAvailable, setEmailAvailable] = useState(false)
  const [checkingEmail, setCheckingEmail] = useState(false)

  const [emailVerified, setEmailVerified] = useState(false)
  const [codeSent, setCodeSent] = useState(false)
  const [sendingCode, setSendingCode] = useState(false)
  const [verifyingCode, setVerifyingCode] = useState(false)

  const [postcodeLoaded, setPostcodeLoaded] = useState(false)
  const [loading, setLoading] = useState(false)

  const [passwordValidation, setPasswordValidation] = useState({
    length: false,
    hasNumber: false,
    hasLetter: false,
    hasSpecial: false,
  })

// 이메일 중복 확인 함수
  const checkEmailDuplicate = async () => {
    const fullEmail = `${emailLocal}@${emailDomain === "custom" ? customDomain : emailDomain}`

    if (!emailLocal || (emailDomain === "custom" && !customDomain)) {
      toast({
        title: "입력 오류",
        description: "이메일을 입력해주세요.",
        variant: "destructive",
      })
      return
    }

    setCheckingEmail(true)
    try {
      const response = await fetch(
          `https://localhost:8443/api/v1/signup-email?email=${encodeURIComponent(fullEmail)}`,
          { method: "GET", headers: { "Content-Type": "application/json" } }
      )
      const data = await response.json()

      if (response.ok && data.code === "200" && data.data?.result === true) {
        setEmailChecked(true)
        setEmailAvailable(true)
        toast({ title: "사용 가능", description: "사용 가능한 이메일입니다." })
      } else {
        setEmailChecked(true)
        setEmailAvailable(false)
        toast({
          title: "중복된 이메일",
          description: data.message || "이미 사용 중인 이메일입니다.",
          variant: "destructive",
        })
      }
    } catch (error: any) {
      toast({
        title: "네트워크 오류",
        description: error.message || "이메일 확인 중 오류가 발생했습니다.",
        variant: "destructive",
      })
    } finally {
      setCheckingEmail(false)
    }
  }
  const validatePassword = (pwd: string) => {
    setPasswordValidation({ //🚨 영어, 숫자, 특수문자, 길이 체크함
      length: pwd.length >= 8 && pwd.length <= 12,
      hasNumber: /\d/.test(pwd),
      hasLetter: /[a-zA-Z]/.test(pwd),
      hasSpecial: /[!@#$%^&*(),.?":{}|<>]/.test(pwd),
    })
  }

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newPassword = e.target.value
    setPassword(newPassword)
    validatePassword(newPassword)
  }

  const handleUsernameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUsername(e.target.value)
    setUsernameChecked(false)
    setUsernameAvailable(false)
  }

  const handleEmailChange = () => {
    setEmailVerified(false)
    setCodeSent(false)
    setVerificationCode("")
  }

  const checkUsername = async () => {
    if (!username) {
      toast({
        title: "입력 오류",
        description: "아이디를 입력해주세요.",
        variant: "destructive",
      })
      return
    }

    const usernameRegex = /^[가-힣a-zA-Z0-9]+$/
    if (!usernameRegex.test(username)) {
      toast({
        title: "입력 오류",
        description: "아이디는 한글, 영어, 숫자만 사용할 수 있습니다.",
        variant: "destructive",
      })
      return
    }

    try {
      const response = await fetch(
          `https://localhost:8443/api/v1/signup-username?nickName=${encodeURIComponent(username)}`,
          { method: "GET", headers: { "Content-Type": "application/json" } }
      )

      const data = await response.json()

      if (response.ok && data.code === "200" && data.data?.result === true) {
        setUsernameChecked(true)
        setUsernameAvailable(true)
        toast({
          title: "사용 가능",
          description: "사용 가능한 아이디입니다.",
        })
      } else {
        setUsernameChecked(true)
        setUsernameAvailable(false)
        toast({
          title: "중복된 아이디",
          description: data.message || "이미 사용 중인 아이디입니다.",
          variant: "destructive",
        })
      }
    } catch (error: any) {
      toast({
        title: "네트워크 오류",
        description: error.message || "아이디 확인 중 오류가 발생했습니다.",
        variant: "destructive",
      })
    }
  }


  const sendVerificationCode = async () => {
    const fullEmail = `${emailLocal}@${emailDomain === "custom" ? customDomain : emailDomain}`

    if (!emailLocal || (emailDomain === "custom" && !customDomain)) {
      toast({
        title: "입력 오류",
        description: "이메일을 입력해주세요.",
        variant: "destructive",
      })
      return
    }

    setSendingCode(true)

    try {
      const response = await fetch(
          `https://localhost:8443/api/v1/email/auth?email=${encodeURIComponent(fullEmail)}`,
          {
            method: "GET",
          }
      )

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || "인증 코드 발송 실패")
      }

      setCodeSent(true)
      toast({
        title: "인증 코드 발송",
        description: "이메일로 인증 코드가 발송되었습니다.",
      })

      if (data.code) {
        console.log("[v0] Development mode - Verification code:", data.code)
      }
    } catch (error: any) {
      toast({
        title: "발송 실패",
        description: error.message || "인증 코드 발송 중 오류가 발생했습니다.",
        variant: "destructive",
      })
    } finally {
      setSendingCode(false)
    }
  }

  const verifyCode = async () => {
    const fullEmail = `${emailLocal}@${emailDomain === "custom" ? customDomain : emailDomain}`;

    if (!verificationCode) return toast({ title: "입력 오류", description: "인증 코드를 입력해주세요.", variant: "destructive" });

    setVerifyingCode(true);

    try {
      const response = await fetch("https://localhost:8443/api/v1/email/auth", {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: fullEmail.trim(),
          authCode: verificationCode.trim() // 공백 제거
        }),
      });

      const data = await response.json();

      console.log("verify response:", data); // ✅ 응답 확인용

      if (response.ok && data.code === "200" && data.data?.result === true) {
        setEmailVerified(true);
        toast({ title: "인증 완료", description: "이메일 인증이 완료되었습니다." });
      } else {
        toast({ title: "인증 실패", description: data.message || "인증 코드 확인 중 오류가 발생했습니다.", variant: "destructive" });
      }
    } catch (error: any) {
      toast({ title: "네트워크 오류", description: error.message || "인증 코드 확인 중 오류가 발생했습니다.", variant: "destructive" });
    } finally {
      setVerifyingCode(false);
    }
  };

  const openAddressSearch = () => {
    if (!postcodeLoaded || !window.daum) {
      toast({
        title: "로딩 중",
        description: "주소 검색 서비스를 불러오는 중입니다. 잠시 후 다시 시도해주세요.",
        variant: "destructive",
      })
      return
    }

    new window.daum.Postcode({
      oncomplete: (data: any) => {
        const fullAddress = data.roadAddress || data.jibunAddress
        setAddress(fullAddress)
        setZipcode(data.zonecode)
      },
    }).open()
  }

  const handleSignup = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!usernameChecked || !usernameAvailable) {
      toast({
        title: "확인 필요",
        description: "아이디 중복 확인을 해주세요.",
        variant: "destructive",
      })
      return
    }

    const isPasswordValid = Object.values(passwordValidation).every((v) => v)
    if (!isPasswordValid) {
      toast({
        title: "비밀번호 오류",
        description: "비밀번호 조건을 모두 충족해주세요.",
        variant: "destructive",
      })
      return
    }

    if (!emailVerified) {
      toast({
        title: "인증 필요",
        description: "이메일 인증을 완료해주세요.",
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

    setLoading(true)

    try {
      const fullEmail = `${emailLocal}@${emailDomain === "custom" ? customDomain : emailDomain}`
      const fullAddress = detailAddress ? `${address}, ${detailAddress}` : address

      const signupData = {
        nickName: username,
        password: password,
        email: fullEmail,
        address: address,
        zipcode: zipcode,
        addressDetail: detailAddress,
        authCode: verificationCode,
      }

      const response = await fetch("https://localhost:8443/api/v1/signup", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(signupData),
      })

      const data = await response.json()

      if (response.ok && data.code === "200") {
        toast({
          title: "회원가입 성공",
          description: "로그인 페이지로 이동합니다.",
        })
        router.push("/login")
      } else {
        toast({
          title: "회원가입 실패",
          description: data.message || "회원가입 중 오류가 발생했습니다.",
          variant: "destructive",
        })
      }
    } catch (error: any) {
      toast({
        title: "네트워크 오류",
        description: error.message || "회원가입 중 오류가 발생했습니다.",
        variant: "destructive",
      })
    } finally {
      setLoading(false)
    }
  }

  const isPasswordValid = Object.values(passwordValidation).every((v) => v)

  return (
      <>
        <Script
            src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"
            onLoad={() => setPostcodeLoaded(true)}
            strategy="lazyOnload"
        />
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
                        placeholder="아이디 (2~12자)"
                        className="flex-1"
                        maxLength={12} // ✅ 입력 제한
                    />
                    <Button type="button" variant="outline" onClick={checkUsername}>
                      중복 확인
                    </Button>
                  </div>
                  {username.length > 0 && ( // ✅ 길이 검증 메시지
                      <p
                          className={`text-xs ${
                              username.length < 2 || username.length > 12
                                  ? "text-red-500"
                                  : "text-green-500"
                          }`}
                      >
                        {username.length < 2
                            ? "아이디는 최소 2자 이상이어야 합니다."
                            : username.length > 12
                                ? "아이디는 최대 12자까지 가능합니다."
                                : "사용 가능한 길이입니다."}
                      </p>
                  )}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="password">비밀번호</Label>
                  <Input
                      id="password"
                      type="password"
                      value={password}
                      onChange={handlePasswordChange}
                      placeholder="8-12자, 숫자, 영문, 특수문자 포함"
                      required
                  />
                  <div className="space-y-1 text-sm">
                    <div className="flex items-center gap-2">
                      {passwordValidation.length ? (
                          <CheckCircle2 className="h-3 w-3 text-green-600" />
                      ) : (
                          <XCircle className="h-3 w-3 text-muted-foreground" />
                      )}
                      <span className={passwordValidation.length ? "text-green-600" : "text-muted-foreground"}>
                      8자 이상 12자 이하
                    </span>
                    </div>
                    <div className="flex items-center gap-2">
                      {passwordValidation.hasNumber ? (
                          <CheckCircle2 className="h-3 w-3 text-green-600" />
                      ) : (
                          <XCircle className="h-3 w-3 text-muted-foreground" />
                      )}
                      <span className={passwordValidation.hasNumber ? "text-green-600" : "text-muted-foreground"}>
                      숫자 포함
                    </span>
                    </div>
                    <div className="flex items-center gap-2">
                      {passwordValidation.hasLetter ? (
                          <CheckCircle2 className="h-3 w-3 text-green-600" />
                      ) : (
                          <XCircle className="h-3 w-3 text-muted-foreground" />
                      )}
                      <span className={passwordValidation.hasLetter ? "text-green-600" : "text-muted-foreground"}>
                      영문 포함
                    </span>
                    </div>
                    <div className="flex items-center gap-2">
                      {passwordValidation.hasSpecial ? (
                          <CheckCircle2 className="h-3 w-3 text-green-600" />
                      ) : (
                          <XCircle className="h-3 w-3 text-muted-foreground" />
                      )}
                      <span className={passwordValidation.hasSpecial ? "text-green-600" : "text-muted-foreground"}>
                      특수문자 포함
                    </span>
                    </div>
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="email">이메일</Label>
                  <div className="flex gap-2 items-center">
                    <Input
                        id="email"
                        type="text"
                        value={emailLocal}
                        onChange={(e) => {
                          setEmailLocal(e.target.value)
                          handleEmailChange()
                        }}
                        placeholder="이메일"
                        className="flex-1"
                        disabled={emailVerified}
                        required
                    />
                    <span className="text-muted-foreground">@</span>
                    <Select
                        value={emailDomain}
                        onValueChange={(value) => {
                          setEmailDomain(value)
                          handleEmailChange()
                        }}
                        disabled={emailVerified}
                    >
                      <SelectTrigger className="w-[140px]">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="gmail.com">gmail.com</SelectItem>
                        <SelectItem value="naver.com">naver.com</SelectItem>
                        <SelectItem value="daum.net">daum.net</SelectItem>
                        <SelectItem value="kakao.com">kakao.com</SelectItem>
                        <SelectItem value="custom">직접 입력</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  {emailDomain === "custom" && !emailVerified && (
                      <Input
                          type="text"
                          value={customDomain}
                          onChange={(e) => {
                            setCustomDomain(e.target.value)
                            handleEmailChange()
                          }}
                          placeholder="도메인 입력"
                          required
                      />
                  )}
                  {/* ✅ 이메일 중복 확인 버튼 추가 */}
                  {!emailVerified && (
                      <Button
                          type="button"
                          variant="outline"
                          onClick={checkEmailDuplicate}
                          disabled={checkingEmail || !emailLocal || (emailDomain === "custom" && !customDomain)}
                          className="w-full"
                      >
                        {checkingEmail ? "확인 중..." : "이메일 중복 확인"}
                      </Button>
                  )}
                  {!emailVerified && (
                      <div className="space-y-2">
                        <Button
                            type="button"
                            variant="outline"
                            onClick={sendVerificationCode}
                            disabled={sendingCode || !emailLocal || (emailDomain === "custom" && !customDomain)}
                            className="w-full bg-transparent"
                        >
                          {sendingCode ? (
                              <>
                                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                발송 중...
                              </>
                          ) : (
                              "인증 코드 발송"
                          )}
                        </Button>
                        {codeSent && (
                            <div className="flex gap-2">
                              <Input
                                  type="text"
                                  value={verificationCode}
                                  onChange={(e) => setVerificationCode(e.target.value)}
                                  placeholder="인증 코드 7자리"
                                  maxLength={7}
                                  required
                              />
                              <Button
                                  type="button"
                                  variant="outline"
                                  onClick={verifyCode}
                                  disabled={verifyingCode || !verificationCode}
                              >
                                {verifyingCode ? <Loader2 className="h-4 w-4 animate-spin" /> : "확인"}
                              </Button>
                            </div>
                        )}
                      </div>
                  )}
                  {emailVerified && (
                      <div className="flex items-center gap-2 text-sm">
                        <CheckCircle2 className="h-4 w-4 text-green-600" />
                        <span className="text-green-600">이메일 인증이 완료되었습니다</span>
                      </div>
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
                        required
                    />
                    <Label htmlFor="zipcode"></Label>
                    <div className="flex gap-2">
                      <Input
                          id="zipcode"
                          type="text"
                          value={zipcode}
                          readOnly
                          placeholder="우편번호"
                          className="flex-1"
                          style={{ width: '80px' }}
                      />
                    </div>
                    <Button type="button" variant="outline" onClick={openAddressSearch}>
                      주소 검색
                    </Button>
                  </div>
                  {address && (
                      <Input
                          type="text"
                          value={detailAddress}
                          onChange={(e) => setDetailAddress(e.target.value)}
                          placeholder="상세 주소를 입력하세요 (예: 101동 202호)"
                      />
                  )}
                </div>

                <Button
                    type="submit"
                    className="w-full"
                    disabled={loading || !usernameAvailable || !isPasswordValid || !emailVerified || !address}
                >
                  {loading ? (
                      <>
                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        회원가입 중...
                      </>
                  ) : (
                      "회원가입"
                  )}
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
        </div>
      </>
  )
}