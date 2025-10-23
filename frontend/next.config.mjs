/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: false,   // ✅ 추가: 개발모드 중복 마운트 방지

  eslint: {
    ignoreDuringBuilds: true,
  },
  typescript: {
    ignoreBuildErrors: true,
  },
  images: {
    unoptimized: true,
  },
}

export default nextConfig
