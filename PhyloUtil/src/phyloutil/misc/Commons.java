/*
 * $Id: Commons.java,v 1.5 2008/05/27 06:09:47 ahmed Exp $
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package phyloutil.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global constants/variables/settings and utility methods
 * 
 * @author Ahmed Moustafa
 * @version $Revision: 1.5 $
 */

public abstract class Commons {
	/**
	 * Tab
	 */
	public static final String TAB = "\t";

	/**
	 * Delimiters
	 */
	public static final String DELIMITERS = " \t,;";

	/**
	 * Comment starter
	 */
	public static final String COMMENT_STARTER = "#";

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger("phylosort");

	/**
	 * Build timestamp attribute (as defined in the jar file manifest)
	 */
	private static final String BUILD_TIMESTAMP_ATTRIBUTE = "Build-Timestamp";

	/**
	 * Build timestamp value
	 */
	private static final String BUILD_TIMESTAMP = _getBuildTimestamp();

	/**
	 * Current version attribute (as defined in the jar file manifest)
	 */
	private static final String CURRENT_VERSION_ATTRIBUTE = "Current-Version";

	/**
	 * Current version value
	 */
	private static final String CURRENT_VERSION = _getCurrentVersion();

	/**
	 * Default home directory
	 */
	private static final String DEFAULT_USER_DIRECTORY = "/";

	/**
	 * Default file separator
	 */
	private static final String DEFAULT_FILE_SEPARATOR = "/";

	/**
	 * Default line separator
	 */
	private static final String DEFAULT_LINE_SEPARATOR = "\r\n";

	/**
	 * Default buffer size
	 */
	private static final int BUFFER_SIZE = 1024;

	/**
	 * User home directory
	 */
	private static String userDirectory = DEFAULT_USER_DIRECTORY;
	static {
		try {
			String _userDirectory = System.getProperty("user.home");
			if (_userDirectory != null && _userDirectory.length() > 0) {
				userDirectory = _userDirectory;
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Failed getting home current directory: " + e.getMessage(), e);
		}
	}

	/**
	 * Line separator
	 */
	private static String fileSeparator = DEFAULT_FILE_SEPARATOR;
	static {
		try {
			fileSeparator = System.getProperty("file.separator");
		} catch (Exception e) {
			logger.log(Level.WARNING, "Failed getting system file separator: " + e.getMessage(), e);
		}
	}

	/**
	 * Line separator
	 */
	private static String lineSeparator = DEFAULT_LINE_SEPARATOR;

	static {
		try {
			lineSeparator = System.getProperty("line.separator");
		} catch (Exception e) {
			logger.log(Level.WARNING, "Failed getting system line separator: " + e.getMessage(), e);
		}
	}

	/**
	 * Returns system file separator.
	 * 
	 * @return file separator
	 */
	public static String getFileSeparator() {
		return fileSeparator;
	}

	/**
	 * Returns system line separator.
	 * 
	 * @return line separator
	 */
	public static String getLineSeparator() {
		return lineSeparator;
	}

	/**
	 * Returns user's current directory.
	 * 
	 * @return user's current directory
	 */
	public static String getUserDirectory() {
		return userDirectory;
	}

	/**
	 * Sets user's current directory.
	 * 
	 * @param _userDirectory
	 *            user's current directory to set
	 * 
	 */
	public static String setUserDirectory(String _userDirectory) {
		return userDirectory = _userDirectory;
	}

	/**
	 * Moves a file from a source to a destination
	 * 
	 * @param in
	 *            Source file
	 * @param out
	 *            Target file
	 */
	public static void move(File in, File out) throws Exception {
		if (out.exists()) {
			logger.warning("The target file \"" + out.getPath() + "\" already exists. Will try to overwrite.");
			boolean deleted = out.delete();
			if (deleted) {
				logger.warning("Deleted \"" + out.getPath() + "\" successfully");
			} else {
				logger.warning("Failed deleting \"" + out.getPath() + "\"");
			}
		}
		boolean moved = in.renameTo(out);
		if (!moved) {
			String msg = "Failed moving tree from \"" + in.getPath() + "\" to \"" + out.getPath() + "\"";
			throw new Exception(msg);
		}
	}

	/**
	 * Copies a file from a source to a destination
	 * 
	 * @param in
	 *            Source file
	 * @param out
	 *            Target file
	 * @throws Exception
	 */
	public static void copy(File in, File out) throws Exception {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(in);
			fos = new FileOutputStream(out);

			copy(fis, fos);
		} catch (Exception e) {
			String msg = "Failed copy: " + e.getMessage();
			logger.log(Level.SEVERE, msg, e);
			throw new Exception(msg, e);
		} finally {

			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					String msg = "Failed closing file input stream: " + e.getMessage();
					logger.log(Level.WARNING, msg, e);
				}
			}

			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					String msg = "Failed closing file out stream: " + e.getMessage();
					logger.log(Level.SEVERE, msg, e);
					throw new Exception(msg, e);
				}
			}
		}
	}

	/**
	 * Copies a file from a source to a destination
	 * 
	 * @param in
	 *            Input stream
	 * @param out
	 *            Ouput stream
	 * @throws Exception
	 */
	public static void copy(InputStream in, OutputStream out) throws Exception {
		try {
			byte[] buf = new byte[BUFFER_SIZE];
			int i = 0;
			while ((i = in.read(buf)) != -1) {
				out.write(buf, 0, i);
			}
		} catch (Exception e) {
			String msg = "Failed copy: " + e.getMessage();
			logger.log(Level.SEVERE, msg, e);
			throw new Exception(msg, e);
		} finally {

			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					String msg = "Failed closing file input stream: " + e.getMessage();
					logger.log(Level.WARNING, msg, e);
				}
			}

			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					String msg = "Failed closing file out stream: " + e.getMessage();
					logger.log(Level.SEVERE, msg, e);
					throw new Exception(msg, e);
				}
			}
		}
	}

	/**
	 * Returns build teimstamp from the jar manifest
	 * 
	 * @return Build timestamp
	 */
	private static String _getBuildTimestamp() {
		JarURLConnection connection = null;
		JarFile jarFile = null;
		URL url = Commons.class.getClassLoader().getResource("phylosort");
		try {
			// Get jar connection
			connection = (JarURLConnection) url.openConnection();

			// Get the jar file
			jarFile = connection.getJarFile();

			// Get the manifest
			Manifest manifest = jarFile.getManifest();

			// Get the manifest entries
			Attributes attributes = manifest.getMainAttributes();

			Attributes.Name name = new Attributes.Name(BUILD_TIMESTAMP_ATTRIBUTE);
			return attributes.getValue(name);
		} catch (Exception e) {
			String message = "Failed getting the current build info: " + e.getMessage();
			logger.warning(message);
		}
		return null;
	}

	/**
	 * Returns package current version
	 * 
	 * @return Current version
	 */
	private static String _getCurrentVersion() {
		JarURLConnection connection = null;
		JarFile jarFile = null;
		URL url = Commons.class.getClassLoader().getResource("phylosort");
		try {
			// Get jar connection
			connection = (JarURLConnection) url.openConnection();

			// Get the jar file
			jarFile = connection.getJarFile();

			// Get the manifest
			Manifest manifest = jarFile.getManifest();

			// Get the manifest entries
			Attributes attributes = manifest.getMainAttributes();

			Attributes.Name name = new Attributes.Name(CURRENT_VERSION_ATTRIBUTE);
			return attributes.getValue(name);
		} catch (Exception e) {
			String message = "Failed getting the current version info: " + e.getMessage();
			logger.warning(message);
		}
		return null;
	}

	/**
	 * Returns current version from the jar manifest
	 * 
	 * @return Current version
	 */
	public static String getBuildTimestamp() {
		return Commons.BUILD_TIMESTAMP;
	}

	/**
	 * Returns build teimstamp from the jar manifest
	 * 
	 * @return Build timestamp
	 */
	public static String getCurrentVersion() {
		return Commons.CURRENT_VERSION;
	}
}