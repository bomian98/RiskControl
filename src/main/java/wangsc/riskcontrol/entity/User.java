package wangsc.riskcontrol.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "accounts")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer uid;
    private String username;
    @TableField(value = "phoneNumber")
    private String phone;
    private String password;
    @TableField(value = "tag")
    private Integer status;


    public void setInfo(String username, String phone, String password, int status){
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.status = status;
    }
}
