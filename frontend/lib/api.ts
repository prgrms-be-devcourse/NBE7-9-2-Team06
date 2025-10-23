// 로컬 전용: 환경변수 없이 바로 백엔드 호출
const BASE_URL = "http://localhost:8080";

type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

export async function api<T>(
  path: string,
  options: {
    method?: HttpMethod;
    query?: Record<string, string | number | boolean | undefined>;
    token?: string;
    body?: unknown; // 필요 시 확장
  } = {}
): Promise<T> {
  const { method = "GET", query, token, body } = options;

  const qs = query
    ? "?" +
      Object.entries(query)
        .filter(([, v]) => v !== undefined)
        .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(String(v))}`)
        .join("&")
    : "";

  const res = await fetch(`${BASE_URL}${path}${qs}`, {
    method,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    credentials: "include", // 쿠키 인증 사용 시 유지
    ...(body !== undefined ? { body: JSON.stringify(body) } : {}),
  });

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(`API ${res.status}: ${text || res.statusText}`);
  }
  return res.json() as Promise<T>;
}
