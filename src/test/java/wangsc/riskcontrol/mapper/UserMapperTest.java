package wangsc.riskcontrol.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wangsc.riskcontrol.entity.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUser() {
        List<User> list = userMapper.selectList(null);
        list.forEach((e) -> System.out.println(e));
    }

    @Test
    public void testInsertUser() {
        int ret = userMapper.insertUser("123", "12233445566", "999");
        assert ret == 1;
    }

    @Test
    public void testDeleteUser() {
        int ret = userMapper.deleteUserByUsername("123");
        assert ret == 1;
    }

    @Test
    public void testGetUserByUsernameAndPassword() {
        User user = userMapper.getUserByUsernameAndPassword("123", "12233445566");
        System.out.println(user);
    }
}
