package com.grupo1.mindbody.activities.repository;

import com.grupo1.mindbody.activities.model.Activity;
import com.grupo1.mindbody.activities.model.ActivityCategory;
import com.grupo1.mindbody.activities.model.ActivityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Page<Activity> findByStatus(ActivityStatus status, Pageable pageable);

    Page<Activity> findByInstitutionId(Long institutionId, Pageable pageable);

    @Query("""
        SELECT a FROM Activity a
        WHERE (:filterCategory = false OR a.category = :category)
          AND (:filterDate = false OR a.date = :date)
          AND (:filterLocation = false OR LOWER(a.location) LIKE LOWER(CONCAT('%', :location, '%')))
        """)
    Page<Activity> findByFilters(
        @Param("filterCategory") boolean filterCategory,
        @Param("category") ActivityCategory category,
        @Param("filterDate") boolean filterDate,
        @Param("date") LocalDate date,
        @Param("filterLocation") boolean filterLocation,
        @Param("location") String location,
        Pageable pageable);

    @Query(value = "SELECT category, COUNT(*) as total_activities, SUM(current_enrollment) as total_enrollment " +
                   "FROM activities GROUP BY category ORDER BY category",
           nativeQuery = true)
    List<Object[]> reportByCategoryRaw();
}
