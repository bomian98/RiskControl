package wangsc.riskcontrol.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wangsc.riskcontrol.entity.ResBody;
import wangsc.riskcontrol.entity.User;
import wangsc.riskcontrol.mapper.RedisDao;
import wangsc.riskcontrol.mapper.UserMapper;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisDao redisDao;


    /**
     * 判断数据库中是否存在用户名和手机号 1. 存在用户名，出错 2. 存在手机号 2.1 是否是黑名单用户 status=-1 2.2 是否当前可用 status=0 2.3 是否之前注销过
     * status=1 2.3 未被注册 更改 username、password、status属性 3. 均不存在，直接插入数据
     */
    public ResBody userRegister(HttpSession session, String username, String password,
            String phone) {
        ResBody resBody = new ResBody();
        if (getUserByUsername(username) != null) {
            resBody.setWrongMessage("用户名已被注册过，请更换");
            return resBody;
        }
        User user = getUserByPhone(phone);
        if (user == null) { //之前没有注册过，直接注册
            int cnt = userMapper.insertUser(username, password, phone);
            if (cnt == 0) {
                resBody.setWrongMessage("数据库出错，请稍后再试");
            }
        } else {
            int status = user.getStatus();
            switch (status) {
                case -1:
                    resBody.setWrongMessage("黑名单用户，不允许注册");
                    break;
                case 0:
                    resBody.setWrongMessage("手机号已被注册过，请直接登录");
                    break;
                case 1:
                    int cnt = updateUserInfoByPhone(username, phone, password, 0);
                    if (cnt == 0) {
                        resBody.setWrongMessage("数据库出错，请稍后再试");
                    }
                    user.setInfo(username, phone, password, status);
                    saveUserBySessionIDInRedis(session, user);
                    break;
                default:
                    // 暂时没有其他状态
                    break;
            }
        }
        return resBody;
    }


    /**
     * 从session中获得用户，并根据用户进行删除 1. 找不到用户，说明当前连接断开 2. 找到用户，更新用户数据，status=1，将 username 和 password 置为空
     */
    public ResBody userDelete(HttpSession session) {
        User user = getUserBySessionIDInRedis(session);
        ResBody resBody = new ResBody();
        if (user == null) {
            resBody.setWrongMessage("会话已结束了，无法操作，请重新登录后再操作");
        } else {
            int cnt = updateUserInfoByPhone("", user.getPhone(), "", 1);
            if (cnt == 0) {
                resBody.setWrongMessage("数据库出错，请稍后再试");
            }
        }
        return resBody;
    }


    /**
     * 判断能否根据用户名和账户名进行登录 1. 有数据，则登录成功，保存用户态 2. 没有数据，则登录失败
     */
    public ResBody userLoginByUsernameAndPassword(HttpSession session, String username,
            String password) {
        User user = userMapper.getUserByUsernameAndPassword(username, password);
        ResBody resBody = new ResBody();
        if (user != null) {
            this.saveUserBySessionIDInRedis(session, user);
        } else {
            resBody.setWrongMessage("登录失败，检查用户名或密码是否正确");
        }
        return resBody;
    }

    /**
     * 判断能否根据手机号进行登录 1. 有数据，则登录成功，保存用户态 2. 没有数据，则登录失败
     */
    public ResBody userLoginByPhone(HttpSession session, String phone) {
        User user = getUserByPhone(phone);
        ResBody resBody = new ResBody();
        if (user != null) {
            this.saveUserBySessionIDInRedis(session, user);
        } else {
            resBody.setWrongMessage("登录失败，检查手机号是否正确");
        }
        return resBody;
    }


    /**
     * 用户登出服务 删除 redis 中的登录态
     */
    public void userLogout(HttpSession session) {
        deleteUserInRedis(session);
    }

    /**
     * 从数据库中获得 username 对应的用户
     */
    public User getUserByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 从数据库中获得 phone 对应的用户
     */
    public User getUserByPhone(String phone) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        return userMapper.selectOne(wrapper);
    }


    /**
     * 根据手机号更新用户信息
     */
    public int updateUserInfoByPhone(String username, String phone, String password, int status) {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("phoneNumber", phone)
                .set("username", username)
                .set("password", password)
                .set("tag", status);
        return userMapper.update(null, wrapper); // 只修改部分字段
    }


    /**
     * 从Redis中获得当前session的用户信息
     */
    public User getUserBySessionIDInRedis(HttpSession session) {
        String key = session.getId() + "_session_user";
        String userJson = redisDao.getValue(key);
        if (StringUtils.isEmpty(userJson)) {
            return null;
        } else {
            return JSON.parseObject(userJson, User.class);
        }
    }


    /**
     * 将当前session对应的用户信息保存下来
     */
    public void saveUserBySessionIDInRedis(HttpSession session, User user) {
        String key = session.getId() + "_session_user";
        String userJson = JSON.toJSONString(user);
        redisDao.setKeyValue(key, userJson, 10, TimeUnit.MINUTES);
    }

    /**
     * 更新当前session对应的用户维持时间
     */
    public void setUserExpireInRedis(HttpSession session) {
        String key = session.getId() + "_session_user";
        redisDao.setExpireTime(key, 10, TimeUnit.MINUTES);
    }


    /**
     * 删除Redis中的用户状态
     */
    public void deleteUserInRedis(HttpSession session) {
        String key = session.getId() + "_session_user";
        redisDao.deleteKeyValue(key);
    }
}
