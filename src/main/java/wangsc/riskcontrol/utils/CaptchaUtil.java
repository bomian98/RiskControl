package wangsc.riskcontrol.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CaptchaUtil {

    private static int width = 165; //验证码的宽
    private static int height = 45; //验证码的高
    private static int lineSize = 30; //验证码中夹杂的干扰线数量
    private static int randomStrNum = 4; //验证码字符个数
    private static String randomString = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWSYZ";


    private static Font getFont() {
        return new Font("Times New Roman", Font.ROMAN_BASELINE, 40);
    }


    private static Color getRandomColor(int fc, int bc) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        fc = Math.min(fc, 255);
        bc = Math.min(bc, 255);
        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 12);

        return new Color(r, g, b);
    }


    private static void drawLine(Graphics g) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(20);
        int yl = random.nextInt(10);
        g.drawLine(x, y, x + xl, y + yl);
    }


    private static String getRandomString(int num) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        num = num > 0 ? num : randomString.length();
        return String.valueOf(randomString.charAt(random.nextInt(num)));
    }


    private static String drawString(Graphics g, String randomStr, int i) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        g.setFont(getFont());
        g.setColor(getRandomColor(108, 190));
        String rand = getRandomString(random.nextInt(randomString.length()));
        randomStr += rand;
        g.translate(random.nextInt(3), random.nextInt(6));
        g.drawString(rand, 40 * i + 10, 25);
        return randomStr;
    }

    public static Map<String, Object> getRandomCodeImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();
        g.fillRect(0, 0, width, height);
        g.setColor(getRandomColor(105, 189));
        g.setFont(getFont());
        for (int i = 0; i < lineSize; i++) {
            drawLine(g);
        }
        String randomStr = "";
        for (int i = 0; i < randomStrNum; i++) {
            randomStr = drawString(g, randomStr, i);
        }
        System.out.println("随机字符：" + randomStr);
        g.dispose();
        Map<String, Object> map = new HashMap<>();
        map.put("code", randomStr);
        map.put("image", image);
        return map;
    }
}
