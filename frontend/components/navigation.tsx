"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"
import { Button } from "@/components/ui/button"
import { removeAuthToken } from "@/lib/auth"
import { useRouter } from "next/navigation"

export function Navigation() {
  const pathname = usePathname()
  const router = useRouter()

  const handleLogout = () => {
    removeAuthToken()
    router.push("/login")
  }

  const navItems = [
    { href: "/", label: "메인" },
    { href: "/search", label: "검색" },
    { href: "/mypage", label: "마이페이지" },
    { href: "/products", label: "상품 구매" },
  ]

  return (
    <nav className="border-b border-border bg-card">
      <div className="container mx-auto px-4">
        <div className="flex h-16 items-center justify-between">
          <Link href="/" className="text-2xl font-bold text-primary">
            Petplace
          </Link>

          <div className="flex items-center gap-2">
            {navItems.map((item) => (
              <Button key={item.href} variant={pathname === item.href ? "default" : "ghost"} asChild>
                <Link href={item.href}>{item.label}</Link>
              </Button>
            ))}
            <Button variant="outline" onClick={handleLogout}>
              로그아웃
            </Button>
          </div>
        </div>
      </div>
    </nav>
  )
}
