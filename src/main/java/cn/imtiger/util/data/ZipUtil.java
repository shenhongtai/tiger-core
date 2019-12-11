package cn.imtiger.util.data;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ZIP压缩
 * @author ShenHongtai
 * @date 2019-12-10
 */
public class ZipUtil {
	private static final Logger log = LoggerFactory.getLogger(ZipUtil.class);
	
	public static void createZip(String sourcePath, String zipPath) {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;

		try {
			fos = new FileOutputStream(zipPath);
			zos = new ZipOutputStream(fos);
			writeZip(new File(sourcePath), "", zos);
		} catch (FileNotFoundException var13) {
			log.error("创建压缩文件失败", var13);
		} finally {
			try {
				if (zos != null) {
					zos.close();
				}
			} catch (IOException var12) {
				log.error("创建压缩文件失败", var12);
			}

		}

	}

	private static void writeZip(File file, String parentPath, ZipOutputStream zos) {
		if (file.exists()) {
			File[] files;
			if (file.isDirectory()) {
				parentPath = parentPath + file.getName() + File.separator;
				files = file.listFiles();
				if (files != null && files.length > 0) {
					File[] var4 = files;
					int var5 = files.length;

					for (int var6 = 0; var6 < var5; ++var6) {
						File f = var4[var6];
						writeZip(f, parentPath, zos);
					}
				}
			} else {
				files = null;
				DataInputStream dis = null;

				try {
					FileInputStream fis = new FileInputStream(file);
					dis = new DataInputStream(new BufferedInputStream(fis));
					ZipEntry ze = new ZipEntry(parentPath + file.getName());
					zos.putNextEntry(ze);
					byte[] content = new byte[1024];

					int len;
					while ((len = fis.read(content)) != -1) {
						zos.write(content, 0, len);
						zos.flush();
					}
				} catch (FileNotFoundException var18) {
					log.error("创建压缩文件失败", var18);
				} catch (IOException var19) {
					log.error("创建压缩文件失败", var19);
				} finally {
					try {
						if (dis != null) {
							dis.close();
						}
					} catch (IOException var17) {
						log.error("创建压缩文件失败", var17);
					}

				}
			}
		}

	}
}