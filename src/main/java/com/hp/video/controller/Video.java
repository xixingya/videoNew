package com.hp.video.controller;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.bytedeco.javacv.Frame;
import sun.nio.ch.DirectBuffer;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.UUID;


@Controller
public class Video {

    @RequestMapping(value = "/userUpload", method = RequestMethod.POST)
    @ResponseBody
    public void userUpdate(@RequestParam(value = "files") MultipartFile file,
                           HttpServletRequest request
    ) throws Exception {
        // 获取文件名
        String fileName = file.getOriginalFilename();
        //获取最后一个点的位置
        int index = fileName.lastIndexOf(".");
        //截取扩展名
        String extension = fileName.substring(index);
        //获取UUID
        String uuid = UUID.randomUUID().toString().replace("-", "");
        //加个UUID，尽量避免文件名称重复，新的视频文件名称
        String newName = uuid + extension;
        System.out.println("新的文件名称-----" + newName);
        //图片存放文件名称
        String picName = uuid + ".jpg";
        System.out.println("图片文件名称-----" + picName);
        //图片在本地存放路径
        String picpath = "/Users/liuzhifei/Desktop/file" + picName;
        File picFile = new File(picpath);
        System.out.println(picpath);
        //视频在本地存放路径
        String path = "/Users/liuzhifei/Desktop/file" + newName;
        File dest = new File(path);
        System.out.println(path);
        //保存文件
        file.transferTo(dest);
        long start = System.currentTimeMillis();
        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(dest);
        ff.start();
        int lenght = ff.getLengthInFrames();
        int i = 0;
        Frame f = null;
        while (i < lenght) {
            // 过滤前5帧，避免出现全黑的图片，依自己情况而定
            f = ff.grabFrame();
            if ((i > 5) && (f.image != null)) {
                break;
            }
            i++;
        }
        Java2DFrameConverter converter  = new Java2DFrameConverter();
        final BufferedImage bi = converter.getBufferedImage(f);
        ImageIO.write(bi,"jpg",picFile);
//        final int oheight = f.imageHeight;
//        final int owidth = f.imageWidth;
//        // 对截取的帧进行等比例缩放
//        int width = 800;
//        int height = (int) (((double) width / owidth) * oheight);
//        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

//                bi.getGraphics().drawImage(,
//                0, 0, null);
//        IplImage img = f.image;
//        int owidth = img.width();
//        int oheight = img.height();
//        // 对截取的帧进行等比例缩放
//        int width = 800;
//        int height = (int) (((double) width / owidth) * oheight);
//        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
//        bi.getGraphics().drawImage(f.image.getBufferedImage().getScaledInstance(width, height, Image.SCALE_SMOOTH),
//                0, 0, null);
//        ImageIO.write(bi, "jpg", file1);
//        ff.stop();
//        System.out.println(System.currentTimeMillis() - start);
    }

    public BufferedImage readByteBufferToBufferedImage(ByteBuffer byteBuffer) throws IOException {

        if (null == byteBuffer)
            return null;

        byte[] b = new byte[byteBuffer.position()];

        byteBuffer.get(b);

        //byte[] b = byteBuffer.array();
        ByteArrayInputStream in = new ByteArrayInputStream(b);

        BufferedImage image = ImageIO.read(in);

        return image;
    }

    public void doExecuteFrame(Frame frame, String targetFilePath, String targetFileName, int index, List<File> files){
        if(frame==null||frame.image==null){
            return;
        }
        Java2DFrameConverter converter  = new Java2DFrameConverter();
        final BufferedImage bi = converter.getBufferedImage(frame);

    }
}
