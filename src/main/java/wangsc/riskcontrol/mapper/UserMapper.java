package wangsc.riskcontrol.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import wangsc.riskcontrol.entity.User;

public interface UserMapper extends BaseMapper<User> {

    public User getUserByUsernameAndPassword(String username, String password);

    public int deleteUserByUsername(String username);

    public int insertUser(String username, String password, String phone);

}
