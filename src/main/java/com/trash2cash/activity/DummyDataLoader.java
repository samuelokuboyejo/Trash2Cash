//package com.trash2cash.activity;
//
//import com.trash2cash.transactions.TransactionRepository;
//import com.trash2cash.users.enums.WasteStatus;
//import com.trash2cash.users.model.User;
//import com.trash2cash.users.repo.UserRepository;
//import com.trash2cash.waste.WasteListing;
//import com.trash2cash.transactions.Transaction;
//import com.trash2cash.waste.WasteListingRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Component
//@RequiredArgsConstructor
//public class DummyDataLoader implements CommandLineRunner {
//    private final UserRepository userRepository;
//    private final WasteListingRepository wasteListingRepository;
//    private final TransactionRepository transactionRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        User user = userRepository.findById(21L)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // SCHEDULED listing
//        WasteListing scheduledListing = WasteListing.builder()
//                .title("Dummy Scheduled Listing")
//                .description("This is a dummy scheduled pickup")
//                .status(WasteStatus.SCHEDULED)
//                .createdAt(LocalDateTime.now())
//                .createdBy(user.getId())
//                .generator(user)
//                .scheduledDateTime(LocalDateTime.now().plusDays(2))
//                .unit(2)
//                .weight(5)
//                .imageUrl("https://res.cloudinary.com/dmq8xmbll/image/upload/v1758108254/f83y1kveqpfkx3rb7tcq.jpg")
//                .pickupLocation("123 Dummy St")
//                .contactPhone("08000000000")
//                .build();
//
//        wasteListingRepository.save(scheduledListing);
//
//        // COMPLETED listing
//        WasteListing completedListing = WasteListing.builder()
//                .title("Dummy Completed Listing")
//                .description("This is a completed pickup")
//                .status(WasteStatus.COMPLETED)
//                .createdAt(LocalDateTime.now().minusDays(3))
//                .createdBy(user.getId())
//                .generator(user)
//                .unit(3)
//                .weight(10)
//                .imageUrl("https://res.cloudinary.com/dmq8xmbll/image/upload/v1758108254/f83y1kveqpfkx3rb7tcq.jpg")
//                .pickupLocation("123 Dummy St")
//                .contactPhone("08000000000")
//                .build();
//
//        wasteListingRepository.save(completedListing);
//
//        // PAID listing with transaction
//        WasteListing paidListing = WasteListing.builder()
//                .title("Dummy Paid Listing")
//                .description("This listing has been paid for")
//                .status(WasteStatus.PAID)
//                .createdAt(LocalDateTime.now().minusDays(1))
//                .createdBy(user.getId())
//                .generator(user)
//                .unit(1)
//                .weight(2)
//                .imageUrl("https://res.cloudinary.com/dmq8xmbll/image/upload/v1758108254/f83y1kveqpfkx3rb7tcq.jpg")
//                .pickupLocation("123 Dummy St")
//                .contactPhone("08000000000")
//                .build();
//
//        wasteListingRepository.save(paidListing);
//
//        Transaction transaction = new Transaction();
//        transaction.setAmount(BigDecimal.valueOf(5500));
//        transaction.setUser(user);
//        transaction.setWasteListing(paidListing);
//        transaction.setCreatedAt(LocalDateTime.now());
//
//        transactionRepository.save(transaction);
//
//        System.out.println("Dummy listings and transaction created successfully!");
//    }
//}