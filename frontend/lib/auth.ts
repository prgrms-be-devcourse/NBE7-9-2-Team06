export function getAuthToken(): string | null {
  if (typeof window === "undefined") return null
  return localStorage.getItem("authToken")
}

export function setAuthToken(token: string): void {
  if (typeof window === "undefined") return
  localStorage.setItem("authToken", token)
}

export function removeAuthToken(): void {
  if (typeof window === "undefined") return
  localStorage.removeItem("authToken")
}

export function isAuthenticated(): boolean {
  return !!getAuthToken()
}
