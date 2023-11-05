package xju.fjj.webpan.utils;

import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.exception.ServerException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: ffmpeg命令相关工具类
 * @date 2023/10/26 16:24
 */
public class ProcessUtils {
    public static String executeCommand(String cmd) throws ServerException{
        if(cmd.isBlank())
            return null;
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            //执行ffmpeg指令
            process = runtime.exec(cmd);
            //取出输出流和错误流信息
            PrintStream error = new PrintStream(process.getErrorStream());
            PrintStream out = new PrintStream(process.getInputStream());
            error.start();
            out.start();
            //等待ffmpeg执行完毕
            process.waitFor();
            //获取执行结果
            return error.buffer.append(out.buffer).append("\n").toString();
        }catch(Exception e){
            throw new ServerException(ResultCodeEnum.CODE_500,"ffmpeg命令: "+cmd+"执行失败");
        } finally {
            if(process != null){
                ProcessKiller ffmpegKiller = new ProcessKiller(process);
                runtime.addShutdownHook(ffmpegKiller);
            }
        }
    }

    /*用于结束ffmpeg线程的线程*/
    private static class ProcessKiller extends Thread{
        private final Process process;

        public ProcessKiller(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            this.process.destroy();
        }
    }

    /*用于取出ffmpeg线程执行中产生的输出和错误信息的线程*/
    static class PrintStream extends Thread{
        final InputStream inputStream;
        BufferedReader reader;
        final StringBuffer buffer = new StringBuffer();

        public PrintStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            if(inputStream == null)
                return;
            try{
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
