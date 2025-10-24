export function getAuthToken(): string | null {
  if (typeof window === "undefined") return null
  return sessionStorage.getItem("authToken")
}

export function setAuthToken(token: string): void {
  if (typeof window === "undefined") return
  sessionStorage.setItem("authToken", token)
}

export function removeAuthToken(): void {
  if (typeof window === "undefined") return
  sessionStorage.removeItem("authToken")
}

export function isAuthenticated(): boolean {
  return !!getAuthToken()
}
