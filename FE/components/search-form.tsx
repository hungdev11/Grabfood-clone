import { Search } from "lucide-react"
import { Input } from "@/components/ui/input"

export function SearchForm() {
  return (
    <div className="relative">
      <Input className="h-10 w-full rounded-full border border-gray-300 pl-10 pr-4" placeholder="Bạn muốn tìm gì?" />
      <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
    </div>
  )
}

