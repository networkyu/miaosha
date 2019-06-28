package com.miaoshaproject;

        import org.mybatis.spring.annotation.MapperScan;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.SpringApplication;
        import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
        import org.springframework.boot.autoconfigure.SpringBootApplication;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = {"com.miaoshaproject"})
//@EnableAutoConfiguration
@RestController
@MapperScan("com.miaoshaproject.dao")
public class App
{
    @Autowired
    private com.miaoshaproject.dao.UserInfoMapper userInfoMapper ;
    @RequestMapping("/")
    public String home(){
        com.miaoshaproject.dataobject.UserInfo userInfo = userInfoMapper.selectByPrimaryKey(1);
        if (userInfo == null){
            return "用户对象不存在";
        } else {
            return userInfo.getName();
        }
//        return "Hello world";
    }
    public static void main( String[] args )
    {

        SpringApplication.run(App.class,args);
    }
}
