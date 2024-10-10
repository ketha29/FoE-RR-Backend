package utils;

import com.ketha.FoE_RoomReservation.dto.UserDto;
import com.ketha.FoE_RoomReservation.model.User;

public class Utils {

	public static UserDto mapUserEntityToUserDto(User user) {
		UserDto userDto = UserDto.builder()
				.userId(user.getUserId())
				.email(user.getEmail())
				.phoneNo(user.getPhoneNo())
				.userName(user.getUserName())
				.userType(user.getUserType())
				.build();
		return userDto;
	}
}
