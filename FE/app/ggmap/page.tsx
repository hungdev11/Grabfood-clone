import MapWrapper from '../../components/MapWrapper';
import LocationSearch from '@/components/locationSearch';

export default function GgmapPage() {
  const center: [number, number] = [10.8471238, 106.786527]; // Ví dụ: Hà Nội
  const zoom = 15;

  return (
    <div>
      <h1>Bản đồ</h1>
      <MapWrapper center={center} zoom={zoom} />
      <h2>Where should we deliver your food today?</h2>
      <LocationSearch />
    </div>
  );
}