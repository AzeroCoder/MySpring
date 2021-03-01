package util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;

import java.io.File;

/**
 * @Author: zerocoder
 * @Description:
 * @Date: 2021/2/28 11:18
 */

public class Constant {
    public static final String CONFIG_PATH = "spring.xml";
    public static final File CONFIG_XML = FileUtil.file(CONFIG_PATH);
    public static final String[] types = {"java.lang.Integer",
            "java.lang.Double",
            "java.lang.Float",
            "java.lang.Long",
            "java.lang.Short",
            "java.lang.Byte",
            "java.lang.Boolean",
            "java.lang.Character",
            "java.lang.String",
            "int","double","long","short","byte","boolean","char","float"};
}
