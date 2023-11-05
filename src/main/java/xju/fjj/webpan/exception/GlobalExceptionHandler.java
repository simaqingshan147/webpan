package xju.fjj.webpan.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.entity.vo.ResponseVo;

import java.net.BindException;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 全局异常处理器,将异常信息按json格式返回
 * @date 2023/10/19 21:52
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = ServerException.class)
    @ResponseBody
    public ResponseVo<String> ServerExceptionHandler(ServerException e){
        return runTimeError(e);
    }

    /*参数验证错误*/
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public ResponseVo<String> BindingExceptionHandler(BindException e){
        ServerException serverException = new ServerException(ResultCodeEnum.CODE_600, "参数错误");
        return runTimeError(serverException);
    }

    /*参数验证错误 @Validated @RequestBody 配合使用*/
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseVo<String> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e){
        ServerException serverException = new ServerException(ResultCodeEnum.CODE_600,e.getMessage());
        return runTimeError(serverException);
    }

    /*参数验证错误 @Validated @RequestParam 配合使用*/
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public ResponseVo<String> ConstraintViolationExceptionHandler(ConstraintViolationException e){
        ServerException serverException = new ServerException(ResultCodeEnum.CODE_600,e.getMessage());
        return runTimeError(serverException);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseVo<String> ExceptionHandler(Exception e){
        ServerException serverException = new ServerException(ResultCodeEnum.CODE_500, e.getMessage());
        return runTimeError(serverException);
    }

    protected ResponseVo<String> runTimeError(ServerException e){
        ResponseVo<String> responseVo = new ResponseVo<>();
        responseVo.setStatus("error");
        responseVo.setCode(e.getCode());
        responseVo.setInfo(e.getInfo());
        responseVo.setData(e.getMessage());
        return responseVo;
    }
}
