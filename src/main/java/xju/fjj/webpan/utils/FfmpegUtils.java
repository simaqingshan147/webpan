package xju.fjj.webpan.utils;

import org.apache.commons.io.FileUtils;
import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.exception.ServerException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: ffmpeg操作工具类
 * @date 2023/10/26 17:38
 */
public class FfmpegUtils {
    /*合并temp中的分片,保存到targetFile*/
    public static void union(File temp,String targetFilePath, String fileName){
        List<File> chunks = Arrays.asList(Objects.requireNonNull(temp.listFiles()));
        //按文件名称排序
        chunks.sort(Comparator.comparing(File::getName));
        //此时targetFile肯定不存在
        File targetFile = new File(targetFilePath);
        try(RandomAccessFile writeFile = new RandomAccessFile(targetFile,"rw")){
            //10kB缓冲区
            byte[] buffer = new byte[1024*10];
            for(File chunk : chunks){
                int len;
                try(RandomAccessFile readFile = new RandomAccessFile(chunk,"r")) {
                    while ((len = readFile.read(buffer)) != -1)
                        writeFile.write(buffer,0,len);
                }catch (IOException e){
                    throw new ServerException(ResultCodeEnum.CODE_500,"读取分片失败");
                }
            }
            //删除临时文件夹
            FileUtils.deleteDirectory(temp);
        }catch (IOException e) {
            throw new ServerException(ResultCodeEnum.CODE_500,"合并文件"+fileName+"失败了: "+e.getMessage());
        }
    }

    /*为targetFile的视频文件分片.ts文件*/
    public static void cutFile4Video(Integer fileId,String videoFilePath)
            throws ServerException{
        //视频文件路径: file/yyyy-MM/userId+fileMd5.xxx

        //1.生成ts切片目录: file/yyyy-MM/userId+fileMd5/
        File tsFolder = new File(videoFilePath.substring(0,videoFilePath.lastIndexOf(".")));
        if(!tsFolder.exists())
            tsFolder.mkdirs();

        final String CMD_TRANSFER_TO_TS = "ffmpeg -y -i %s  -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s";
        final String CMD_CUT_TS = "ffmpeg -i %s -c copy -map 0 -f segment -segment_list %s -segment_time 30 %s/%d_%%4d.ts";
        //2.将视频转为index.ts
        //index.ts路径: file/yyyy-MM/userId+fileMd5/index.ts
        String tsPath = tsFolder.getPath()+"/"+ Constants.TS_NAME;
        String cmd = String.format(CMD_TRANSFER_TO_TS,videoFilePath,tsPath);
        ProcessUtils.executeCommand(cmd);

        //3.切片index.ts,生成切片.ts文件和索引index.m3u8
        //index.m3u8路径: file/yyyy-MM/userId+fileMd5/index.m3u8
        //fileId_0001.ts ~ fileId_9999.ts路径: file/yyyy-MM/userId+fileMd5/fileId_xxxx.ts
        String m3u8Path = tsFolder.getPath()+"/"+Constants.M3U8_NAME;
        cmd = String.format(CMD_CUT_TS,tsPath,m3u8Path,tsFolder.getPath(),fileId);
        ProcessUtils.executeCommand(cmd);
        //4.删除index.ts
        new File(tsPath).delete();
    }

    /*为source视频文件创建缩略图,保存在cover*/
    public static void createCover4Video(File source,Integer width, File cover)
            throws ServerException{
        final String CREATE_COVER = "ffmpeg -i %s -y -vframes 1 -vf scale=%d:%d/a %s";
        String cmd = String.format(CREATE_COVER,source.getAbsolutePath(),width,width,cover.getAbsolutePath());
        ProcessUtils.executeCommand(cmd);
    }
    /*为source图片文件创建缩略图(压缩),保存在cover*/
    public static Boolean createThumbForImage(File source, Integer width, File cover)
            throws IOException,ServerException{
        BufferedImage src = ImageIO.read(source);
        int sourceWidth = src.getWidth();
        int sourceHeight = src.getHeight();
        //压缩宽度不能比源文件宽度大
        if(width>=sourceWidth)
            return false;
        String COMPRESS_IMAGE = "ffmpeg -i %s -vf scale=%d:-1 %s -y";
        String cmd = String.format(COMPRESS_IMAGE,source.getAbsolutePath(),width,cover.getAbsolutePath());
        ProcessUtils.executeCommand(cmd);
        return true;
    }
}
