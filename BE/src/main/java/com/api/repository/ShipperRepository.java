package com.api.repository;

import com.api.entity.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipperRepository extends JpaRepository<Shipper, Long> {
    
    // Tìm shipper theo phone
    Optional<Shipper> findByPhone(String phone);
    
    // Tìm shipper theo email
    Optional<Shipper> findByEmail(String email);
    
    // Tìm shipper theo account ID
    Optional<Shipper> findByAccountId(Long accountId);
    
    // Tìm shipper theo phone hoặc email
    @Query("SELECT s FROM Shipper s WHERE s.phone = :identifier OR s.email = :identifier")
    Optional<Shipper> findByPhoneOrEmail(@Param("identifier") String identifier);
    
    // Lấy tất cả shipper online và active trong bán kính
    @Query("SELECT s FROM Shipper s WHERE " +
           "s.isOnline = true AND s.status = 'ACTIVE' AND " +
           "s.currentLatitude IS NOT NULL AND s.currentLongitude IS NOT NULL AND " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(s.currentLatitude)) * " +
           "cos(radians(s.currentLongitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(s.currentLatitude)))) <= :radiusKm " +
           "ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(s.currentLatitude)) * " +
           "cos(radians(s.currentLongitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(s.currentLatitude))))")
    List<Shipper> findAvailableShippersInRadius(@Param("latitude") Double latitude, 
                                               @Param("longitude") Double longitude, 
                                               @Param("radiusKm") Double radiusKm);
    
    // Lấy shipper online và active
    @Query("SELECT s FROM Shipper s WHERE s.isOnline = true AND s.status = 'ACTIVE'")
    List<Shipper> findOnlineActiveShippers();
    
    // Lấy shipper theo status
    List<Shipper> findByStatus(Shipper.ShipperStatus status);
    
    // Lấy shipper theo online status
    List<Shipper> findByIsOnline(Boolean isOnline);
    
    // Lấy shipper theo status và online
    List<Shipper> findByStatusAndIsOnline(Shipper.ShipperStatus status, Boolean isOnline);
    
    // Đếm số shipper online
    @Query("SELECT COUNT(s) FROM Shipper s WHERE s.isOnline = true AND s.status = 'ACTIVE'")
    Long countOnlineShippers();
    
    // Tìm top shipper theo rating
    @Query("SELECT s FROM Shipper s WHERE s.status = 'ACTIVE' ORDER BY s.rating DESC, s.completedOrders DESC")
    List<Shipper> findTopShippersByRating();
    
    // Kiểm tra tồn tại phone
    boolean existsByPhone(String phone);
    
    // Kiểm tra tồn tại email
    boolean existsByEmail(String email);
    
    // Kiểm tra tồn tại license plate
    boolean existsByLicensePlate(String licensePlate);
    
    // Lấy shipper có nhiều đơn hàng hoàn thành nhất
    @Query("SELECT s FROM Shipper s WHERE s.status = 'ACTIVE' ORDER BY s.completedOrders DESC")
    List<Shipper> findTopShippersByCompletedOrders();
    
    // Lấy shipper theo acceptance rate cao nhất
    @Query("SELECT s FROM Shipper s WHERE s.status = 'ACTIVE' AND s.totalOrders >= :minOrders ORDER BY s.acceptanceRate DESC")
    List<Shipper> findShippersByAcceptanceRate(@Param("minOrders") Integer minOrders);
    
    // Tìm shipper gần nhất
    @Query("SELECT s FROM Shipper s WHERE " +
           "s.isOnline = true AND s.status = 'ACTIVE' AND " +
           "s.currentLatitude IS NOT NULL AND s.currentLongitude IS NOT NULL " +
           "ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(s.currentLatitude)) * " +
           "cos(radians(s.currentLongitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(s.currentLatitude)))) ASC")
    List<Shipper> findNearestShippers(@Param("latitude") Double latitude, 
                                     @Param("longitude") Double longitude);
    
    // Lấy shipper theo vehicle type
    List<Shipper> findByVehicleTypeAndStatusAndIsOnline(String vehicleType, 
                                                        Shipper.ShipperStatus status, 
                                                        Boolean isOnline);
    
    // Thống kê shipper theo status
    @Query("SELECT s.status, COUNT(s) FROM Shipper s GROUP BY s.status")
    List<Object[]> getShipperStatsByStatus();
    
    // Lấy shipper có gems nhiều nhất
    @Query("SELECT s FROM Shipper s WHERE s.status = 'ACTIVE' ORDER BY s.gems DESC")
    List<Shipper> findTopShippersByGems();
} 