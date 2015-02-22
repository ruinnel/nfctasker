package net.ruinnel.nfc.tasker.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class FileUtil {
	private static final String TAG = FileUtil.class.getSimpleName();

	public static boolean copyFile(String src, String dest) {
		try {
			File srcFile = new File(src);
			File dstFile = new File(dest);

			if (!dstFile.exists()) {
				dstFile.createNewFile();
			}

//			Log.i(TAG, "copyFile : srcFile.exists() - " + srcFile.exists());
//			Log.i(TAG, "copyFile : dstFile.canWrite - " + dstFile.canWrite());
			if (srcFile.exists() && dstFile.canWrite()) {
				FileChannel srcChan = new FileInputStream(srcFile).getChannel();
				FileChannel dstChan = new FileOutputStream(dstFile).getChannel();
				dstChan.transferFrom(srcChan, 0, srcChan.size());
				srcChan.close();
				dstChan.close();
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
