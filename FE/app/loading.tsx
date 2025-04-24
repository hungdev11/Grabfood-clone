import { Loader2 } from 'lucide-react'; // Icon xoay tròn từ lucide-react
export default function Loading() {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-white-500 bg-opacity-50 z-50">
    <Loader2 className="w-12 h-12 text-green-500 animate-spin" />
  </div>
  )
}

