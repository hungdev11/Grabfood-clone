"use client";
import React, { useState, useEffect } from "react";
import { fetchWithAuth } from "@/utils/api";
import { Review } from "@/components/types/Types";

interface ReviewListProps {
  restaurantId: string;
}

interface PaginatedResponse {
  page: number;
  size: number;
  total: number;
  items: Review[];
}

const PAGE_SIZE = 5;

async function getPaginatedReviews(
  restaurantId: string,
  page: number,
  ratingFilter?: number
): Promise<PaginatedResponse> {
  const params = new URLSearchParams({
    page: page.toString(),
    size: PAGE_SIZE.toString(),
  });

  // Nếu có ratingFilter, thêm vào URL query
  if (ratingFilter !== undefined && ratingFilter !== 6) {
    params.append("ratingFilter", ratingFilter.toString());
  }

  const res = await fetchWithAuth(
    `http://localhost:6969/grab/reviews/restaurant/${restaurantId}?${params.toString()}`,
    { cache: "no-store" }
  );

  if (!res.ok) {
    throw new Error("Failed to fetch paginated restaurant reviews");
  }

  const data = await res.json();
  return data.data;
}

export const ReviewList: React.FC<ReviewListProps> = ({ restaurantId }) => {
  const [reviews, setReviews] = useState<Review[]>([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [ratingFilter, setRatingFilter] = useState<number | null>(6);

  useEffect(() => {
    setLoading(true);
    getPaginatedReviews(restaurantId, currentPage, ratingFilter ?? undefined)
      .then((data) => {
        setReviews(data.items);
        setTotal(data.total);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setLoading(false);
      });
  }, [restaurantId, currentPage, ratingFilter]);

  const totalPages = Math.ceil(total / PAGE_SIZE);

  return (
    <div className="ml-4 mb-4 space-y-4">
      <div className="text-2xl font-semibold text-gray-800 mb-4 border-b pb-2 border-gray-200">
        What People Say
      </div>

      {/* FILTER */}
      <div className="flex gap-2 mb-2">
        <button
          onClick={() => setRatingFilter(6)}  // Chọn tất cả
          className={`px-3 py-1 rounded ${
            ratingFilter === 6 ? "bg-blue-500 text-white" : "bg-gray-100"
          }`}
        >
          All
        </button>
        {[5, 4, 3, 2, 1].map((star) => (
          <button
            key={star}
            onClick={() => {
              setRatingFilter(star);  // Lọc theo số sao
              setCurrentPage(0); // reset về trang đầu
            }}
            className={`px-3 py-1 rounded ${
              ratingFilter === star ? "bg-blue-500 text-white" : "bg-gray-100"
            }`}
          >
            {star}★
          </button>
        ))}
      </div>

        {/* REVIEW LIST */}
        {loading ? (
        <div>Loading reviews...</div>
        ) : reviews.length === 0 ? (
        <div className="text-gray-500">No reviews found.</div>
        ) : (
        reviews.map((review) => (
            <div key={review.reviewId} className="p-4 rounded-lg border shadow-sm bg-white">
            <div className="flex justify-between items-center mb-2">
                <div className="font-semibold text-lg">{review.customerName}</div>
                <div className="text-yellow-500 font-medium">⭐ {review.rating}/5</div>
            </div>
            <div className="text-sm text-gray-500 mb-1">
                Ordered: <span className="italic">{review.orderString}</span>
            </div>
            <div className="text-base text-gray-800 mb-2">{review.reviewMessage}</div>
            <div className="text-xs text-gray-400 mb-1">Reviewed on {review.createdAt}</div>
            {review.replyMessage && (
                <div className="mt-3 p-3 rounded-md bg-gray-50 border-l-4 border-green-400">
                <div className="text-sm text-gray-700">
                    <strong>Reply:</strong> {review.replyMessage}
                </div>
                <div className="text-xs text-gray-400 mt-1">Replied on {review.replyAt}</div>
                </div>
            )}
            </div>
        ))
        )}


      {/* PAGINATION */}
      {totalPages > 1 && (
        <div className="flex justify-center items-center gap-2 mt-4">
          <button
            onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 0))}
            className="px-3 py-1 border rounded disabled:opacity-50"
            disabled={currentPage === 0}
          >
            Prev
          </button>
          <span>
            Page {currentPage + 1} / {totalPages}
          </span>
          <button
            onClick={() =>
              setCurrentPage((prev) => Math.min(prev + 1, totalPages - 1))
            }
            className="px-3 py-1 border rounded disabled:opacity-50"
            disabled={currentPage === totalPages - 1}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
};
