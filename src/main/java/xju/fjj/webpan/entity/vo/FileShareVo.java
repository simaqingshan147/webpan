package xju.fjj.webpan.entity.vo;

import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.enums.ValidTypeEnums;
import xju.fjj.webpan.entity.pojo.DirInfo;
import xju.fjj.webpan.entity.pojo.FileInfo;
import xju.fjj.webpan.entity.pojo.ShareInfo;

import java.util.Calendar;
import java.util.Date;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 返回给前端的分享文件/文件夹信息的vo
 * @date 2023/10/31 22:54
 */
public class FileShareVo{
    /**
     * 分享id
     */
    private Integer shareId;
    /**
     * 文件或目录id
     */
    private Integer id;
    /**
     * 文件或目录所属用户id
     */
    private String userId;
    /**
     * 0:文件 1:目录
     */
    private Integer isFolder;
    /**
     * 0:1天 1:3天 2:7天 3:30天 4:永久有效
     */
    private Integer validType;
    /**
     * 分享开始时间
     */
    private Date shareTime;
    /**
     * 验证码
     */
    private String code;
    /**
     * 分享文件/目录浏览次数
     */
    private Integer showCount;
    private String name;  //文件或目录名
    private String cover;  //文件或目录封面路径
    private Integer type;  //文件类型(null when isFolder = 1)
    private Date expireTime;  //过期时间

    public FileShareVo() {

    }

    public FileShareVo(ShareInfo shareInfo){
        this.shareId = shareInfo.getShareId();
        this.userId = shareInfo.getUserId();
        this.id = shareInfo.getDocumentId();
        this.isFolder = shareInfo.getIsFolder();
        this.validType = shareInfo.getValidType();
        this.shareTime = shareInfo.getShareTime();
        this.code = shareInfo.getCode();
        this.showCount = shareInfo.getShowCount();
    }

    public static FileShareVo convertFromFileInfo(ShareInfo shareInfo,FileInfo fileInfo){
        FileShareVo result = new FileShareVo(shareInfo);
        result.setName(fileInfo.getFileName());
        result.setCover(fileInfo.getFileCover());
        result.setType(fileInfo.getFileType());
        //过期时间
        result.setExpireTime(calculateExpireTime(shareInfo));
        return result;
    }

    public static FileShareVo convertFromDirInfo(ShareInfo shareInfo, DirInfo dirInfo){
        FileShareVo result = new FileShareVo(shareInfo);
        result.setName(dirInfo.getDirName());
        result.setCover(Constants.DEFAULT_COVER_FOLDER+Constants.FOLDER_COVER_NAME);
        //type = null
        result.setExpireTime(calculateExpireTime(shareInfo));
        return result;
    }


    /*计算过期时间并返回*/
    public static Date calculateExpireTime(ShareInfo shareInfo){
        Integer days = ValidTypeEnums.getDaysByType(shareInfo.getValidType());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(shareInfo.getShareTime());
        calendar.add(Calendar.DATE,days);
        return calendar.getTime();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getShareId() {
        return shareId;
    }

    public void setShareId(Integer shareId) {
        this.shareId = shareId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIsFolder() {
        return isFolder;
    }

    public void setIsFolder(Integer isFolder) {
        this.isFolder = isFolder;
    }

    public Integer getValidType() {
        return validType;
    }

    public Date getShareTime() {
        return shareTime;
    }

    public void setShareTime(Date shareTime) {
        this.shareTime = shareTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getShowCount() {
        return showCount;
    }

    public void setShowCount(Integer showCount) {
        this.showCount = showCount;
    }
}
