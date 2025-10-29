package com.trash2cash.users.repo;

import com.trash2cash.users.model.Device;
import com.trash2cash.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByDeviceId(String deviceId);
    boolean existsByDeviceId(String deviceId);
    Optional<Device> findByDeviceIdAndUser(String deviceId, User user);

    @Query("SELECT d.fcmToken FROM Device d WHERE d.user.id = :userId AND d.fcmToken IS NOT NULL")
    String findFcmTokenByUserId(@Param("userId") Long userId);

    @Query("SELECT d.fcmToken FROM Device d WHERE d.user.id = :userId AND d.fcmToken IS NOT NULL")
    List<String> findAllFcmTokensByUserId(@Param("userId") Long userId);

    @Query("SELECT d.fcmToken FROM Device d WHERE d.user.id IN :userIds AND d.fcmToken IS NOT NULL")
    List<String> findFcmTokensByUserIds(@Param("userIds") List<Long> userIds);

}
