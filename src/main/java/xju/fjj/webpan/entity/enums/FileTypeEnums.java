package xju.fjj.webpan.entity.enums;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public enum FileTypeEnums {
    //1:视频 2:音频  3:图片 4:pdf 5:word 6:excel 7:txt 8:code 9:zip 10:其他文件
    VIDEO(FileCategoryEnums.VIDEO, 1, new String[]{".mp4", ".avi", ".rmvb", ".mkv", ".mov"}, "视频"),
    MUSIC(FileCategoryEnums.MUSIC, 2, new String[]{".mp3", ".wav", ".wma", ".mp2", ".flac", ".midi", ".ra", ".ape", ".aac", ".cda"}, "音频"),
    IMAGE(FileCategoryEnums.IMAGE, 3, new String[]{".jpeg", ".jpg", ".png", ".gif", ".bmp", ".dds", ".psd", ".pdt", ".webp", ".xmp", ".svg", ".tiff"}, "图片"),
    PDF(FileCategoryEnums.DOC, 4, new String[]{".pdf"}, "pdf"),
    WORD(FileCategoryEnums.DOC, 5, new String[]{".docx"}, "word"),
    EXCEL(FileCategoryEnums.DOC, 6, new String[]{".xlsx"}, "excel"),
    TXT(FileCategoryEnums.DOC, 7, new String[]{".txt"}, "txt文本"),
    PROGRAM(FileCategoryEnums.OTHER, 8, new String[]{".h", ".c", ".hpp", ".hxx", ".cpp", ".cc", ".c++", ".cxx", ".m", ".o", ".s", ".dll", ".cs",
            ".java", ".class", ".js", ".ts", ".css", ".scss", ".vue", ".jsx", ".sql", ".md", ".json", ".html", ".xml"}, "CODE"),
    ZIP(FileCategoryEnums.OTHER, 9, new String[]{"rar", ".zip", ".7z", ".cab", ".arj", ".lzh", ".tar", ".gz", ".ace", ".uue", ".bz", ".jar", ".iso",
            ".mpq"}, "压缩包"),
    OTHERS(FileCategoryEnums.OTHER, 10, new String[]{}, "其他");

    private final FileCategoryEnums category;
    private final Integer type;
    private final String[] suffixes;
    private final String description;

    FileTypeEnums(FileCategoryEnums category, Integer type, String[] suffixs, String description) {
        this.category = category;
        this.type = type;
        this.suffixes = suffixs;
        this.description = description;
    }

    public static FileTypeEnums getTypeBySuffix(String suffix){
        for(FileTypeEnums item: FileTypeEnums.values())
            if(ArrayUtils.contains(item.getSuffixes(),suffix))
                return item;
        return OTHERS;
    }

    public static List<Integer> getTypesByCategory(FileCategoryEnums category){
        List<Integer> types = new ArrayList<>();
        for(FileTypeEnums item: FileTypeEnums.values())
            if(item.category == category)
                types.add(item.getType());
        return types;
    }

    public FileCategoryEnums getCategory() {
        return category;
    }

    public Integer getType() {
        return type;
    }

    public String[] getSuffixes() {
        return suffixes;
    }

    public String getDescription() {
        return description;
    }
}
