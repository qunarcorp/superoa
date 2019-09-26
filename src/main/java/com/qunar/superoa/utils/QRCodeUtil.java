package com.qunar.superoa.utils;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 下午4:54 2018/11/9
 * @Modify by:
 */

import com.google.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

/**
 * url生成二维码图片
 */
@Slf4j
public class QRCodeUtil {

  /**
   * 默认二维码宽度
   */
  private static final int width = 300;
  /**
   * 默认二维码高度
   */
  private static final int height = 300;
  /**
   * 默认二维码文件格式
   */
  private static final String format = "png";
  /**
   * 二维码其他参数
   */
  private static final Map<EncodeHintType, Object> hints = Maps.newHashMap();

  /**
   * 初始化参数
   */
  static {
    //字符编码
    hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
    //容错等级
    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
    //二维码与图片边距
    hints.put(EncodeHintType.MARGIN, 2);
  }

  /**
   * 返回一个 BufferedImage 对象
   * @param content 二维码内容，如URL
   * @param width 二维码宽度
   * @param height 二维码高度
   */
  public static BufferedImage toBufferedImage(String content, int width, int height) {
    BitMatrix bitMatrix = null;
    try {
      bitMatrix = new MultiFormatWriter()
          .encode(content, BarcodeFormat.QR_CODE, width, height, hints);
    } catch (WriterException e) {
      log.error("生成二维码图片失败",e);
    }
    assert bitMatrix != null;
    //重设二维码前景色和背景色,否则会导致插入的logo颜色一直为黑白色
    MatrixToImageConfig config = new MatrixToImageConfig(0xFF000001,0xFFFFFFFF);
    return MatrixToImageWriter.toBufferedImage(bitMatrix,config);

  }

  /**
   * 返回一个 ByteArrayOutputStream 流
   * @param content 二维码内容，如URL
   * @param width 二维码宽度
   * @param height 二维码高度
   */
  public static ByteArrayOutputStream toOutputStream(String content, int width, int height) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    BitMatrix bitMatrix = null;
    try {
      bitMatrix = new MultiFormatWriter()
          .encode(content, BarcodeFormat.QR_CODE, width, height, hints);
    } catch (WriterException e) {
      log.error("生成二维码图片失败",e);
    }
    try {
      assert bitMatrix != null;
      //重设二维码前景色和背景色,否则会导致插入的logo颜色一直为黑白色
      MatrixToImageConfig config = new MatrixToImageConfig(0xFF000001,0xFFFFFFFF);
      MatrixToImageWriter.writeToStream(bitMatrix, format, outputStream, config);
    } catch (IOException e) {
      log.error("将二维码输出到流失败", e);
    }

    return outputStream;
  }

  /**
   * 返回一个 带logo的 BufferedImage 对象
   * @param content 二维码内容，如URL
   * @param logoPath logo路径
   * @param width 二维码宽度
   * @param height 二维码高度
   */
  public static  BufferedImage toBufferedImageWithLogo(String content, String logoPath, int width, int height) {
    //获取二维码
    BufferedImage matrixImage = toBufferedImage(content, width, height);
    //读取二维码，构建绘图对象
    Graphics2D g2 = matrixImage.createGraphics();
    int matrixWidth = matrixImage.getWidth();
    int matrixHeight = matrixImage.getHeight();

    //读取logo图片
    BufferedImage logo = null;
    try {
      logo = ImageIO.read(new URL(logoPath));
    } catch (IOException e) {
      log.error("logo图片读取失败", e);
    }

    //开始绘制图片
    g2.drawImage(logo, matrixWidth/5*2, matrixHeight/5*2, matrixWidth/5, matrixHeight/5, null);
    BasicStroke stroke = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    g2.setStroke(stroke);

    //制定弧度的圆角矩阵
    RoundRectangle2D.Float round = new RoundRectangle2D.Float(matrixWidth/5*2, matrixHeight/5*2, matrixWidth/5+2, matrixHeight/5+2, 20, 20);
    g2.setColor(Color.white);
    g2.draw(round);

    //设置log有一道灰色边框
//    BasicStroke stroke2 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
//    g2.setStroke(stroke2);
//    RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(matrixWidth/5*2+2, matrixHeight/5*2+2, matrixWidth/5-4, matrixHeight/5-4,20,20);
//    g2.setColor(Color.gray);
//    g2.draw(round2);

    g2.dispose();
    matrixImage.flush();

    return matrixImage;
  }

  /**
   * 返回一个 带logo的 ByteArrayOutputStream 流
   * @param content 二维码内容，如URL
   * @param logoPath logo路径
   * @param width 二维码宽度
   * @param height 二维码高度
   */
  public static ByteArrayOutputStream toOutputStreamWithLogo(String content, String logoPath, int width, int height) {
    BufferedImage bufferedImage = toBufferedImageWithLogo(content, logoPath, width, height);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      ImageIO.write(bufferedImage, format, outputStream);
    } catch (IOException e) {
      log.error("输出带logo标志的二维码图片流失败", e);
    }

    return outputStream;
  }



}
