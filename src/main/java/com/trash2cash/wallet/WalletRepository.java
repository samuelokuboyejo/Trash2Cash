package com.trash2cash.wallet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);
    boolean existsByUserId(Long userId);


    @Query("SELECT w FROM Wallet w JOIN FETCH w.user u ORDER BY w.points DESC")
    List<Wallet> findAllOrderByPointsDesc();

    @Query("SELECT w FROM Wallet w JOIN FETCH w.user u ORDER BY w.points DESC")
    Page<Wallet> findAllOrderByPointsDesc(Pageable pageable);
}
