import axios from "axios"
import { getAuthToken, setAuthToken, removeAuthToken } from "@/lib/auth"

const api = axios.create({
  baseURL: "https://localhost:8443/api/v1",
  withCredentials: true, // refresh token 쿠키 포함
})

// ✅ 요청 인터셉터 - 액세스 토큰 자동 첨부
api.interceptors.request.use((config) => {
  const token = getAuthToken()
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// ✅ 응답 인터셉터 - 401이면 자동 재발급 시도
api.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config

      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true
        try {
          const refreshResponse = await axios.post(
              "https://localhost:8443/api/v1/auth/refresh",
              {},
              { withCredentials: true }
          )

          const newAccessToken = refreshResponse.data.accessToken
          setAuthToken(newAccessToken)

          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`
          return api(originalRequest) // 원래 요청 재시도
        } catch (refreshError) {
          console.error("토큰 재발급 실패", refreshError)
          removeAuthToken()
          window.location.href = "/login"
        }
      }

      return Promise.reject(error)
    }
)

export default api


// axios 인스턴스를 만들고,
// 인증 헤더를 자동으로 붙이고,
// 만료 시 리프레시 요청까지 처리하고,
// 실패 시 로그인 페이지로 리다이렉트
