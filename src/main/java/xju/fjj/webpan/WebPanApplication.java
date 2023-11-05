package xju.fjj.webpan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(basePackages = {"xju.fjj.webpan.mapper"})
@EnableTransactionManagement
public class WebPanApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebPanApplication.class, args);
    }

}
