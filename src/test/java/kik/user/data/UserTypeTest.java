package kik.user.data;

import kik.user.data.accessrole.AccessRole;
import kik.user.data.accessrole.AccessRolesEnum;
import kik.user.data.usertype.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserTypeTest {

	private final String userTypeName = "someotheruserType456";
	private final Set<AccessRole> roleSet = Set.of(new AccessRole(AccessRolesEnum.USER)
		, new AccessRole(AccessRolesEnum.ADMIN));
	private UserType userType;


	@BeforeEach
	public void setup() {
		this.userType = new UserType(userTypeName, this.roleSet);
	}

	@Test
	public void UserTypeDefaultConstructorTest(){
		assertEquals(this.userType.getName(), userTypeName);
	}
}