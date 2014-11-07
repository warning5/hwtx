/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 YU YUE, SOSO STUDIO, wuyuetiger@gmail.com
 *
 * License: GNU Lesser General Public License (LGPL)
 * 
 * Source code availability:
 *  https://github.com/wuyuetiger/dbking
 *  https://code.csdn.net/tigeryu/dbking
 *  https://git.oschina.net/db-unifier/dbking
 */

package com.hwtx.tool.autocode;

import com.hwtx.tool.DbKing;
import com.hwtx.tool.Table;
import com.hwtx.tool.util.IoUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Generator {

    private static String getTemplate(String templateFilename)
            throws IOException {
        InputStream is = Generator.class.getResourceAsStream(templateFilename);
        try {
            byte[] bytes = IoUtil.convertStream(is);
            return new String(bytes, "UTF-8");
        } finally {
            IoUtil.closeInputStream(is);
        }
    }

    protected static void write(Template template, VelocityContext context, String targetPath, String fileName) throws Exception {
        FileWriter writer = null;
        try {
            createDirIfNotExist(targetPath);
            targetPath += File.separator + fileName;
            writer = new FileWriter(targetPath);
            template.merge(context, writer);
            writer.flush();
        } finally {
            IoUtil.closeWriter(writer);
        }
    }

    public static void generatEntity(String targetPath, String data_source, String _package, String... tables) throws Exception {

        Properties prop = new Properties();
        prop.setProperty("file.resource.loader.path", Generator.class.getClassLoader().getResource("vm").getPath());
        Velocity.init(prop);
        VelocityContext context = new VelocityContext();
        Template template = Velocity.getTemplate("entity.vm");
        DbKing dbKing = new DbKing(data_source);
        for (String t : tables) {
            Table table = dbKing.getTable(t);
            if (table == null) {
                table = dbKing.getView(t);
            }
            context.put("table", table);
            context.put("package", _package);
            write(template, context, targetPath, table.getDefinationName() + ".java");
        }
    }

    private static void createDirIfNotExist(String fullPath) throws Exception {
        File tmp = new File(fullPath);
        if (!tmp.exists()) {
            boolean success = (new File(fullPath)).mkdirs();
            if (!success) {
                throw new Exception("Create File failed");
            }
        }
    }
}
