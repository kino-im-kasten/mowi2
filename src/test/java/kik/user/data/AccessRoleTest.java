package kik.user.data;

import kik.user.data.accessrole.AccessRole;
import kik.user.data.accessrole.AccessRolesEnum;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AccessRoleTest {

	private AccessRole accessRole = new AccessRole(AccessRolesEnum.USER);

	@Test
	public void AccessRoleDefaultConstructorTest(){
		assertEquals(accessRole.getSalesPointRole(), Role.of(AccessRolesEnum.USER.toString()));
	}
}
