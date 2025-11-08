// lib/api/refreshToken.ts
import axios from "axios";
import { setAuthToken, removeAuthToken } from "@/lib/auth"

// refresh token은 쿠키에 들어 있으므로 withCredentials 필요
export async function refreshToken() {
  try {
    const response = await axios.post(
        "https://localhost:8443/api/v1/auth/refresh",
        {},
        { withCredentials: true } // 쿠키 전송
    );

    // 새 access token 저장
    const newAccessToken = response.data.accessToken;
    if (newAccessToken) {
      setAuthToken(newAccessToken);
    }
    return newAccessToken;
  } catch (error) {
    console.error("토큰 재발급 실패:", error);
    removeAuthToken();
    window.location.href = "/login"; // 로그인 페이지로 이동
    throw error;
  }
}
