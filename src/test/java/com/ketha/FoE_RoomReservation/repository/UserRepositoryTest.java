package com.ketha.FoE_RoomReservation.repository;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.model.User.UserType;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {
	
	@Autowired
	private UserRepository userRepository;
	
	@Test
	public void UserRepository_Save_ReturnSavedUser() {
		// Arrange
		User user = User.builder()
						.firstName("David")
						.lastName("John")
						.email("e20199@eng.pdn.ac.lk")
						.phoneNo(0771233456)
						.userName("e20199")
						.password("password")
						.userType(UserType.admin)
						.build();
		
		// Act
		userRepository.save(user);
		
		Optional<User> returnedUser = userRepository.findByUserName(user.getUserName());
		
		// Assert
		Assertions.assertThat(returnedUser).isNotNull();
	    Assertions.assertThat(returnedUser.get().getUserId()).isGreaterThan(0);
	}
	
	@Test
	public void UserRepository_DuplicateUserName_ReturnError() {
		// Arrange
		User user1 = User.builder()
						.firstName("David")
						.lastName("John")
						.email("Davidjohn123@gmail.com")
						.phoneNo(0771233456)
						.userName("David")
						.password("password")
						.userType(UserType.admin)
						.build();
		// Act
		userRepository.save(user1);
		
		// Create User2 with duplicate UserName
		User user2 = User.builder()
						.firstName("David")
						.lastName("Miller")
						.email("Davidmiller123@gmail.com")
						.phoneNo(0771233456)
						.userName("David")
						.password("password")
						.userType(UserType.admin)
						.build();
		
		// Assert 
		assertThrows(DataIntegrityViolationException.class, ()->userRepository.save(user2),"Duplicate Username should throw Exception");
	}
	
	@Test
	public void UserRepository_DuplicateEmail_ReturnError() {
		// Arrange
		User user1 = User.builder()
						.firstName("David")
						.lastName("John")
						.email("David123@gmail.com")
						.phoneNo(0771233456)
						.userName("David")
						.password("password")
						.userType(UserType.admin)
						.build();
		// Act
		userRepository.save(user1);
		
		// Create User2 with duplicate UserName
		User user2 = User.builder()
						.firstName("David")
						.lastName("Miller")
						.email("David123@gmail.com")
						.phoneNo(0771233456)
						.userName("Miller25")
						.password("password")
						.userType(UserType.admin)
						.build();
		
		// Assert 
		assertThrows(DataIntegrityViolationException.class, ()->userRepository.save(user2),"Duplicate Username should throw Exception");
	}
	
	@Test
	public void UserRepository_FindById_ReturnUser() {
		// Arrange
		User user = User.builder()
					.firstName("Oliver")
					.lastName("Wilson")
					.email("e20199@eng.pdn.ac.lk")
					.phoneNo(0771233456)
					.userName("e20199")
					.password("password")
					.userType(UserType.admin)
					.build();
				
		userRepository.save(user);
		
		// Act
		User returnUser = userRepository.findById(user.getUserId()).get(); 
				
		// Assert
		Assertions.assertThat(returnUser).isNotNull();
	}
	
	@Test
	public void UserRepository_ExistsByUserName_ReturnTrue() {
		User user = User.builder()
				.firstName("David")
				.lastName("John")
				.email("e20199@eng.pdn.ac.lk")
				.phoneNo(0771233456)
				.userName("e20199")
				.password("password")
				.userType(UserType.admin)
				.build();
				
		userRepository.save(user);
		
		Boolean exist = userRepository.existsByUserName("e20199"); 
				
		Assertions.assertThat(exist).isNotNull();
		Assertions.assertThat(exist).isTrue();
	}
	
	@Test
	public void UserRepository_FindByUserName_ReturnUserNotNull() {
		User user = User.builder()
				.firstName("David")
				.lastName("John")
				.email("e20199@eng.pdn.ac.lk")
				.phoneNo(0771233456)
				.userName("e20199")
				.password("password")
				.userType(UserType.admin)
				.build();
		
		userRepository.save(user);
		
		User returnUser = userRepository.findByUserName(user.getUserName()).get(); 
				
		Assertions.assertThat(returnUser).isNotNull();
		Assertions.assertThat(returnUser.getUserName()).isEqualTo("e20199");
	}
	
	@Test
	public void UserRepository_FindByUserType_ReturnUserList() {
		User user1 = User.builder()
					.firstName("Tom")
					.lastName("Cruise")
					.email("e20199@eng.pdn.ac.lk")
					.phoneNo(0771233456)
					.userName("e20199")
					.password("password")
					.userType(UserType.admin)
					.build();
		
		User user2 = User.builder()
				.firstName("Oliver")
				.lastName("Wilson")
				.email("regularuser@eng.pdn.ac.lk")
				.phoneNo(0771233456)
				.userName("regularuser")
				.password("password")
				.userType(UserType.regularUser)
				.build();
		
		userRepository.save(user1);
		userRepository.save(user2);
		
		List<User> userList = userRepository.findUserByUserType(UserType.admin); 
				
		Assertions.assertThat(userList).isNotNull();
		Assertions.assertThat(userList.size()).isEqualTo(1);
	}
	
	@Test
	public void UserRepository_DeleteById_ReturnUser() {
		User user1 = User.builder()
				.firstName("David")
				.lastName("John")
				.email("e20199@eng.pdn.ac.lk")
				.phoneNo(0771233456)
				.userName("e20199")
				.password("password")
				.userType(UserType.admin)
				.build();
		
		userRepository.save(user1);
		
		User returnUser = userRepository.findByUserName("e20199").get(); 
				
		Assertions.assertThat(returnUser).isNotNull();
		Assertions.assertThat(returnUser.getUserName()).isEqualTo("e20199");
	}
}
