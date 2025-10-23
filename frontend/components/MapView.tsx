'use client';

import { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import type { PlaceDto } from '@/app/search/placeService';
import type { LatLngExpression } from 'leaflet';
// import 'leaflet/dist/leaflet.css';

type Props = {
  center: [number, number] | null; // 내 위치
  places: PlaceDto[];
  onSelectPlace?: (p: PlaceDto) => void;
};

/** center가 바뀔 때 지도만 이동 (MapContainer 재마운트 금지) */
function RecenterOnChange({ center }: { center: LatLngExpression }) {
  const map = useMap();
  useEffect(() => {
    map.setView(center);
  }, [center, map]);
  return null;
}

export default function MapView({ center, places, onSelectPlace }: Props) {
  const [mounted, setMounted] = useState(false);
  useEffect(() => setMounted(true), []);
  if (!mounted) return null; // StrictMode/SSR 초기 중복 마운트 방지

  const fallbackCenter: LatLngExpression = [37.5665, 126.9780]; // 서울시청
  const effectiveCenter: LatLngExpression = center ?? fallbackCenter;

  return (
    <MapContainer
      center={effectiveCenter}
      zoom={13}
      style={{ height: '400px', width: '100%' }}
    >
      <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />

      <RecenterOnChange center={effectiveCenter} />

      {/* 내 위치 마커 */}
      {center && <Marker position={effectiveCenter} />}

      {/* 검색 결과 마커 */}
      {places.map((p) => {
        const pos: LatLngExpression = [p.latitude, p.longitude];
        return (
          <Marker
            key={p.id}
            position={pos}
            eventHandlers={{ click: () => onSelectPlace?.(p) }}
          >
            <Popup>
              <div className="space-y-1">
                <div className="font-semibold">{p.name}</div>
                <div className="text-xs">{p.address}</div>
                <div className="text-xs">거리 {p.distanceMeters}m | 평점 {p.averageRating}</div>
              </div>
            </Popup>
          </Marker>
        );
      })}
    </MapContainer>
  );
}
