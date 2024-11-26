package com.ketha.FoE_RoomReservation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.model.User.UserType;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUserName(String userName);
	Boolean existsByUserName(String userName);
	Boolean existsByEmail(String email);
	List<User> findUserByUserType(UserType userType);
	
	@Query(value = "SELECT * FROM users u WHERE CONCAT(u.first_name, ' ', u.last_name) REGEXP :namePart", nativeQuery = true)
	List<User> findByName(@Param("namePart") String namePart);
}
