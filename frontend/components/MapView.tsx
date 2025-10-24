'use client';

import { useEffect, useMemo } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import type { PlaceDto } from '@/app/search/placeService';
import type { LatLngExpression } from 'leaflet';
import L from 'leaflet';


type Props = {
  center: [number, number] | null; // 내 위치
  places: PlaceDto[];
  onSelectPlace?: (p: PlaceDto) => void;
};


function RecenterOnChange({ center }: { center: LatLngExpression }) {
  const map = useMap();
  useEffect(() => {
    map.setView(center);
  }, [center, map]);
  return null;
}


function svgToDataUrl(svg: string) {
  return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
}


const USER_SVG = `
<svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <filter id="shadow" x="-50%" y="-50%" width="200%" height="200%">
      <feDropShadow dx="0" dy="2" stdDeviation="2" flood-opacity="0.25"/>
    </filter>
  </defs>
  <g filter="url(#shadow)">
    <path d="M24 44c0 0 14-12.5 14-22A14 14 0 1 0 10 22c0 9.5 14 22 14 22z" fill="#3B82F6"/>
    <circle cx="24" cy="22" r="6.5" fill="white"/>
  </g>
</svg>
`;


const PLACE_SVG = `
<svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <filter id="shadow2" x="-50%" y="-50%" width="200%" height="200%">
      <feDropShadow dx="0" dy="2" stdDeviation="2" flood-opacity="0.25"/>
    </filter>
  </defs>
  <g filter="url(#shadow2)">
    <path d="M24 44c0 0 14-12.5 14-22A14 14 0 1 0 10 22c0 9.5 14 22 14 22z" fill="#EF4444"/>
    <circle cx="24" cy="22" r="6.5" fill="white"/>
  </g>
</svg>
`;

const userIcon = L.icon({
  iconUrl: svgToDataUrl(USER_SVG),
  iconSize: [32, 32],
  iconAnchor: [16, 32],   // 핀 끝이 좌표를 가리키도록
  popupAnchor: [0, -28],
});

const placeIcon = L.icon({
  iconUrl: svgToDataUrl(PLACE_SVG),
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -28],
});

export default function MapView({ center, places, onSelectPlace }: Props) {
  const fallbackCenter: LatLngExpression = [37.5665, 126.9780]; // 서울시청
  const effectiveCenter: LatLngExpression = center ?? fallbackCenter;

  // 마커 배열 메모이제이션 (렌더 최적화)
  const placeMarkers = useMemo(
    () =>
      places.map((p) => {
        const pos: LatLngExpression = [p.latitude, p.longitude];
        return (
          <Marker
            key={p.id}
            position={pos}
            icon={placeIcon}
            eventHandlers={{ click: () => onSelectPlace?.(p) }}
          >
            <Popup>
              <div className="space-y-1">
                <div className="font-semibold">{p.name}</div>
                <div className="text-xs">{p.address}</div>
                <div className="text-xs">
                  거리 {p.distanceMeters}m | 평점 {p.averageRating ?? '-'}
                </div>
              </div>
            </Popup>
          </Marker>
        );
      }),
    [places, onSelectPlace]
  );

  return (
    <MapContainer
      center={effectiveCenter}
      zoom={13}
      style={{ height: '400px', width: '100%' }}
    >
      <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />

      {/* center가 바뀌면 부드럽게 이동 */}
      <RecenterOnChange center={effectiveCenter} />

      {/* 내 위치 마커 (위로 보이게 zIndexOffset) */}
      {center && (
        <Marker position={effectiveCenter} icon={userIcon} zIndexOffset={1000}>
          <Popup>
            <div className="text-sm">내 위치</div>
          </Popup>
        </Marker>
      )}

      {/* 검색 결과 마커들 */}
      {placeMarkers}
    </MapContainer>
  );
}
