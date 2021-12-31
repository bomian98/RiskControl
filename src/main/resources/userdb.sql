# 创建用户信息数据库
CREATE TABLE `accounts`
(
    `uid`      int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `username` varchar(30) UNIQUE,
    `phoneNumber`    char(11) UNIQUE,
    `password` varchar(25),
    `status`   int NOT NULL DEFAULT 0
) ENGINE = InnoDB;

# insert into accounts
# values (11111, "name1", "pwd1", 13555555555, 1);