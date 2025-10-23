'use client';

import { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import type { PlaceDto } from '@/app/search/placeService';
import type { LatLngExpression } from 'leaflet';

type Props = {
  center: [number, number] | null;
  places: PlaceDto[];
  onSelectPlace?: (p: PlaceDto) => void;
};

function RecenterOnChange({ center }: { center: LatLngExpression }) {
  const map = useMap();
  useEffect(() => { map.setView(center); }, [center, map]);
  return null;
}

export default function MapView({ center, places, onSelectPlace }: Props) {
  const [mounted, setMounted] = useState(false);
  useEffect(() => setMounted(true), []);
  if (!mounted) return null;

  const fallbackCenter: LatLngExpression = [37.5665, 126.9780]; // 서울시청
  const effectiveCenter: LatLngExpression = center ?? fallbackCenter;

  return (
    <MapContainer center={effectiveCenter} zoom={13} style={{ height: '400px', width: '100%' }}>
      <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
      <RecenterOnChange center={effectiveCenter} />

      {center && <Marker position={effectiveCenter} />}

      {places.map((p) => {
        const pos: LatLngExpression = [p.latitude, p.longitude];
        return (
          <Marker key={p.id} position={pos} eventHandlers={{ click: () => onSelectPlace?.(p) }}>
            <Popup>
              <div className="space-y-1.5">
                <div className="font-semibold">{p.name}</div>
                <div className="text-xs text-muted-foreground">{p.address}</div>
                <div className="text-xs">거리 {p.distanceMeters}m · 평점 {p.averageRating ?? "-"}</div>
              </div>
            </Popup>
          </Marker>
        );
      })}
    </MapContainer>
  );
}
