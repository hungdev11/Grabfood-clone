import { useParams } from "next/navigation";

export default function VoucherManagement() {
    const { restaurantId } = useParams();
    return (
        <div>VOUCHER</div>
    )
}