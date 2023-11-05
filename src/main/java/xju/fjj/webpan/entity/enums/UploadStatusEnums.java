package xju.fjj.webpan.entity.enums;

public enum UploadStatusEnums {
    UPLOAD_SECONDS("upload_seconds", "秒传"),
    UPLOADING("uploading", "上传中"),
    UPLOAD_FINISH("upload_finish", "上传完成");
    private final String code;
    private final String description;

    UploadStatusEnums(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
