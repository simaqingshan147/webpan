package xju.fjj.webpan.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.dto.SessionShareDto;
import xju.fjj.webpan.entity.dto.SessionUserDto;
import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.entity.vo.ResponseVo;
import xju.fjj.webpan.exception.ServerException;

import java.io.*;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 简化控制器创建ResponseVo的重复代码
 * @date 2023/10/15 0:24
 */
public class BaseController {
    protected static final String SUCCESS = "success";
    protected static final String ERROR = "error";
    protected <T> ResponseVo<T> success(T data){
        ResponseVo<T> responseVo = new ResponseVo<>();
        responseVo.setStatus(SUCCESS);
        responseVo.setCode(ResultCodeEnum.CODE_200.getCode());
        responseVo.setInfo(ResultCodeEnum.CODE_200.getMsg());
        responseVo.setData(data);
        return responseVo;
    }

    protected ResponseVo<String> runTimeError(ServerException e){
        ResponseVo<String> responseVo = new ResponseVo<>();
        responseVo.setStatus(ERROR);
        responseVo.setCode(e.getCode());
        responseVo.setInfo(e.getInfo());
        responseVo.setData(e.getMessage());
        return responseVo;
    }

    /*通过response传递文件*/
    protected void readFile(HttpServletResponse response, File file){
        FileInputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            out = response.getOutputStream();
            int len;
            while ((len = in.read(bytes)) != -1)
                out.write(bytes,0,len);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected SessionUserDto getUserInfoFromSession(HttpSession session){
        return (SessionUserDto) session.getAttribute(Constants.SESSION_KEY);
    }

    protected SessionShareDto getShareInfoFromSession(HttpSession session,Integer shareId){
        return (SessionShareDto) session.getAttribute(Constants.SESSION_SHARE_KEY_PREFIX+shareId);
    }

    protected boolean checkCheckCode(HttpSession session,String checkCode){
        //获取session中的验证码
        String realCheckCode = (String) session.getAttribute(Constants.CHECK_CODE_KEY);
        //输入验证码错误
        if(!checkCode.equalsIgnoreCase(realCheckCode))
            return false;
        //人机验证完成,移出session中的验证码
        session.removeAttribute(Constants.CHECK_CODE_KEY);
        return true;
    }
}
