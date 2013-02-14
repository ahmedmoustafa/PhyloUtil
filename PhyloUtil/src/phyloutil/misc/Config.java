/*
 * $Id: Config.java,v 1.18 2011/03/14 08:10:44 ahmed Exp $
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;


/**
 * A singleton PhyloSort set of configurations
 * 
 * @author Ahmed Moustafa
 * @version $Revision: 1.18 $
 */
public final class Config {

	/**
	 * Logger
	 */
	private static final Logger logger = SingleLogger.getLogger();

	/**
	 * Instance of {@link Config}
	 */
	private static Config instance = null;

	/**
	 * Returns an instance of {@link Config}
	 * 
	 * @return {@link Config}
	 */
	public static Config getInstance() {

		if (instance == null) {
			instance = new Config();
		}

		return instance;
	}

	/**
	 * Hidden constructor
	 * 
	 */
	private Config() {

	}

	/**
	 * PhyloSort config system property
	 */
	public static final String SYSTEM_PROPERTY_PHYLOSORT_CONFIG = "phylosort.config";

	/**
	 * Default PhyloSort config file name
	 */
	public static final String DEFAULT_PHYLOSORT_FILE_NAME = "phylosort.config";

	/**
	 * Property pattern for taxa extraction
	 */
	public static final String PROPERTY_REGEXP = "phylosort.pattern";
	
	/**
	 * Default pattern for taxa extraction
	 */
	public static final String DEFAULT_REGEXP = "(.+)";
	
	/**
	 * Property pattern for file name and query extraction
	 */
	public static final String PROPERTY_FILENAME_REGEXP = "phylosort.filename.pattern";
	
	/**
	 * Default pattern for file name and query extraction
	 */
	public static final String DEFAULT_FILENAME_REGEXP = "(.+)\\.tre$";
	
	/**
	 * Property query OTU required yes or no? 
	 */
	public static final String PROPERTY_QUERY_REQUIRED = "phylosort.query.required";
	
	/**
	 * Query OTU required NO
	 */
	public static final String PROPERTY_QUERY_REQUIRED_NO = "no";

	/**
	 * Query OTU required YES
	 */
	public static final String PROPERTY_QUERY_REQUIRED_YES = "yes";
	
	/**
	 * Default query OTU required
	 */
	public static final String DEFAULT_QUERY_REQUIRED = PROPERTY_QUERY_REQUIRED_NO;

	/**
	 * System property tree rooting
	 */
	public static final String PROPERTY_ROOT = "phylosort.root.outgroup";

	/**
	 * Tree rooting using an outgroup NO
	 */
	public static final String PROPERTY_ROOT_OUTGROUP_NO = "no";

	/**
	 * Tree rooting using an outgroup YES
	 */
	public static final String PROPERTY_ROOT_OUTGROUP_YES = "yes";

	/**
	 * Default tree rerooting (midpoint)
	 */
	public static final String DEFAULT_ROOT = PROPERTY_ROOT_OUTGROUP_YES;

	/**
	 * System property mode of operation
	 */
	public static final String PROPERTY_MODE = "phylosort.mode";

	/**
	 * Exclusive mode of searching
	 */
	public static final String PROPERTY_MODE_EXCLUSIVE = "exclusive";

	/**
	 * Inclusive mode of searching
	 */
	public static final String PROPERTY_MODE_INCLUSIVE = "inclusive";

	/**
	 * Default mode of operation
	 */
	public static final String DEFAULT_MODE = PROPERTY_MODE_INCLUSIVE;

	/**
	 * Property minimum group size
	 */
	public static final String PROPERTY_MINIMUM_GROUP_SIZE = "phylosort.minimum.group.size";
	
	/**
	 * Default minimum group size
	 */
	public static final int DEFAULT_MINIMUM_GROUP_SIZE = 1;
		
	/**
	 * Property minimum number of taxa
	 */
	public static final String PROPERTY_MINIMUM_NUMBER_OF_TAXA = "phylosort.minimum.number.taxa";

	/**
	 * Default minimum number of taxa
	 */
	public static final int DEFAULT_MINIMUM_NUMBER_OF_TAXA = -1;

	/**
	 * Property maximum number of taxa
	 */
	public static final String PROPERTY_MAXIMUM_NUMBER_OF_TAXA = "phylosort.maximum.number.taxa";

	/**
	 * Default maximum number of taxa
	 */
	public static final int DEFAULT_MAXIMUM_NUMBER_OF_TAXA = -1;

	/**
	 * Property minimum bootstrap support
	 */
	public static final String PROPERTY_MINIMUM_BOOTSTRAP_SUPPORT = "phylosort.minimum.bootstrap.support";

	/**
	 * Default minimum bootstrap support
	 */
	public static final float DEFAULT_MINIMUM_BOOTSTRAP_SUPPORT = -1;

	/**
	 * Property maximum average number of copies
	 */
	public static final String PROPERTY_MAXIMUM_AVERAGE_NUMBER_OF_COPIES = "phylosort.maximum.average.number.copies";

	/**
	 * Default maximum average number of copies
	 */
	public static final float DEFAULT_MAXIMUM_AVERAGE_NUMBER_OF_COPIES = -1;

	/**
	 * Property on match action
	 */
	public static final String PROPERTY_ON_MATCH_ACTION = "phylosort.on.match.action";

	/**
	 * On match action copy
	 */
	public static final String PROPERTY_ON_MATCH_ACTION_COPY = "copy";

	/**
	 * On match action copy
	 */
	public static final String PROPERTY_ON_MATCH_ACTION_MOVE = "move";

	/**
	 * On match action count
	 */
	public static final String PROPERTY_ON_MATCH_ACTION_COUNT = "count";

	/**
	 * Default on match action
	 */
	public static final String DEFAULT_ON_MATCH_ACTION = PROPERTY_ON_MATCH_ACTION_COUNT;

	/**
	 * Mode flag
	 */
	private boolean exclusive = DEFAULT_MODE.equalsIgnoreCase(PROPERTY_MODE_EXCLUSIVE);

	/**
	 * Tree rooting
	 */
	private boolean outgroup = DEFAULT_ROOT.equalsIgnoreCase(PROPERTY_ROOT_OUTGROUP_YES);

	/**
	 * On match action flag
	 */
	private String match = DEFAULT_ON_MATCH_ACTION;
	
	/**
	 * Minimum group size
	 */
	private int minimumGroupSize = DEFAULT_MINIMUM_GROUP_SIZE;

	/**
	 * Minimum number of taxa
	 */
	private int minimumNumberOfTaxa = DEFAULT_MINIMUM_NUMBER_OF_TAXA;

	/**
	 * Maximum number of taxa
	 */
	private int maximumNumberOfTaxa = DEFAULT_MAXIMUM_NUMBER_OF_TAXA;

	/**
	 * Minimum bootstrap support
	 */
	private float minimumBootstrapSupport = DEFAULT_MINIMUM_BOOTSTRAP_SUPPORT;

	/**
	 * Maximum average number of copies
	 */
	private float maximumAverageNumberOfCopies = DEFAULT_MAXIMUM_AVERAGE_NUMBER_OF_COPIES;

	/**
	 * Default taxa extraction regular expression
	 */
	private Pattern regexp = Pattern.compile(DEFAULT_REGEXP);
	
	/**
	 * Default filename extraction regular expression
	 */
	private Pattern filenameRegexp = Pattern.compile(DEFAULT_FILENAME_REGEXP);
	
	/**
	 * Query required
	 */
	private boolean queryRequired = DEFAULT_QUERY_REQUIRED.equalsIgnoreCase(PROPERTY_QUERY_REQUIRED_YES);

	/**
	 * Returns rooting method
	 * 
	 * @return Rooting method
	 */
	public String getRoot() {
		if (isOutgroup()) {
			return PROPERTY_ROOT_OUTGROUP_YES;
		} else {
			return PROPERTY_ROOT_OUTGROUP_NO;
		}
	}
	
	/**
	 * Returns query required flag
	 * 
	 * @return query required flag
	 */
	public String getQueryRequired() {
		if (isQueryRequired()) {
			return PROPERTY_QUERY_REQUIRED_YES;
		} else {
			return PROPERTY_QUERY_REQUIRED_NO;
		}
	}

	/**
	 * Returns mode
	 * 
	 * @return Mode
	 */
	public String getMode() {
		if (isExclusive()) {
			return PROPERTY_MODE_EXCLUSIVE;
		} else {
			return PROPERTY_MODE_INCLUSIVE;
		}
	}

	/**
	 * Returns true if the mode is set to "exclusive"
	 * 
	 * @return True if the mode is set to "exclusive"
	 */
	public boolean isExclusive() {
		return exclusive;
	}

	/**
	 * Returns the maximum number of taxa
	 * 
	 * @return The maximum number of taxa
	 */
	public int getMaximumNumberOfTaxa() {
		return maximumNumberOfTaxa;
	}

	/**
	 * Sets the maximum number of taxa
	 * 
	 * @param maximumNumberOfTaxa
	 *            The maximum number of taxa to set
	 */
	public void setMaximumNumberOfTaxa(int maximumNumberOfTaxa) {
		this.maximumNumberOfTaxa = maximumNumberOfTaxa;
	}

	/**
	 * Returns true if the maximum bootstrap support is negative, otherwise
	 * false
	 * 
	 * @return True if the maximum bootstrap support is negative, otherwise
	 *         false
	 */
	public boolean isMaximumNumberOfTaxaOn() {
		return maximumNumberOfTaxa >= 0;
	}

	/**
	 * Returns the minimum bootstrap support
	 * 
	 * @return The minimum bootstrap support
	 */
	public float getMinimumBootstrapSupport() {
		return minimumBootstrapSupport;
	}

	/**
	 * Sets the minimum bootstrap support
	 * 
	 * @param minimumBootstrapSupport
	 *            The minimum bootstrap support to set
	 */
	public void setMinimumBootstrapSupport(float minimumBootstrapSupport) {
		this.minimumBootstrapSupport = minimumBootstrapSupport;
	}

	/**
	 * Returns true if the minimum bootstrap support is negative, otherwise
	 * false
	 * 
	 * @return True if the minimum bootstrap support is negative, otherwise
	 *         false
	 */
	public boolean isMinimumBootstrapSupportOn() {
		return minimumBootstrapSupport >= 0;
	}

	/**
	 * Returns the minimum number of taxa
	 * 
	 * @return The minimum number of taxa
	 */
	public int getMinimumNumberOfTaxa() {
		return minimumNumberOfTaxa;
	}

	/**
	 * Sets the minimum number of taxa
	 * 
	 * @param minimumNumberOfTaxa
	 *            The minimum number of taxa to set
	 */
	public void setMinimumNumberOfTaxa(int minimumNumberOfTaxa) {
		this.minimumNumberOfTaxa = minimumNumberOfTaxa;
	}

	/**
	 * Returns true if the minimum number of taxa is negative, otherwise false
	 * 
	 * @return True if the minimum number of taxa is negative, otherwise false
	 */
	public boolean isMinimumNumberOfTaxaOn() {
		return minimumNumberOfTaxa >= 0;
	}

	/**
	 * Sets the mode
	 * 
	 * @param mode
	 *            The mode to set
	 */
	public void setMode(String mode) throws Exception {
		if (mode.equalsIgnoreCase(PROPERTY_MODE_EXCLUSIVE)) {
			exclusive = true;
		} else if (mode.equalsIgnoreCase(PROPERTY_MODE_INCLUSIVE)) {
			exclusive = false;
		} else {
			throw new Exception("Invalid " + PROPERTY_MODE + ": " + mode);
		}
	}

	/**
	 * Sets the on match action
	 * 
	 * @param onMatchAction
	 *            The on match action to set
	 */
	public void setOnMatchAction(String onMatchAction) throws Exception {
		if (onMatchAction.equalsIgnoreCase(PROPERTY_ON_MATCH_ACTION_COPY)) {
			match = PROPERTY_ON_MATCH_ACTION_COPY;
		} else if (onMatchAction.equalsIgnoreCase(PROPERTY_ON_MATCH_ACTION_MOVE)) {
			match = PROPERTY_ON_MATCH_ACTION_MOVE;
		} else if (onMatchAction.equalsIgnoreCase(PROPERTY_ON_MATCH_ACTION_COUNT)) {
			match = PROPERTY_ON_MATCH_ACTION_COUNT;
		} else {
			throw new Exception("Invalid " + PROPERTY_ON_MATCH_ACTION + ": " + onMatchAction);
		}
	}

	/**
	 * Returns the taxa extraction regular expression
	 * 
	 * @return The taxa extraction regular expression
	 */
	public Pattern getRegexp() {
		return regexp;
	}

	/**
	 * Sets the taxa extraction regular expression
	 * 
	 * @param regexp
	 *            Regular expression
	 */
	public void setRegexp(String regexp) {
		this.regexp = Pattern.compile(regexp);
	}

	/**
	 * Returns the filename extraction regular expression
	 * 
	 * @return The filename extraction regular expression
	 */
	public Pattern getFilenameRegexp() {
		return filenameRegexp;
	}

	/**
	 * Sets the filename extraction regular expression
	 * 
	 * @param regexp
	 *            Regular expression
	 */
	public void setFilenameRegexp(String regexp) {
		this.filenameRegexp = Pattern.compile(regexp);
	}
	
	
	/**
	 * Loads configuration settings from a file
	 * 
	 * @param filename
	 *            File name
	 * @return Instance of {@link Config} loaded from a file
	 * @throws Exception
	 */
	public static Config load(String filename) throws Exception {
		try {
			Properties properties = new Properties();
			FileInputStream fileInputStream = new FileInputStream(filename);
			properties.load(fileInputStream);

			load(properties);

			return instance;
		} catch (Exception e) {
			String msg = "Failed loading configuration file: " + e.getMessage();
			logger.log(Level.SEVERE, msg, e);
			throw new Exception(msg, e);
		}
	}

	/**
	 * Loads configuration settings from a file
	 * 
	 * @param filename
	 *            File name
	 * @return Instance of {@link Config} loaded from a file
	 * @throws Exception
	 */
	public static Config load(File file) throws Exception {
		try {
			Properties properties = new Properties();
			FileInputStream fileInputStream = new FileInputStream(file);
			properties.load(fileInputStream);

			load(properties);

			return instance;
		} catch (Exception e) {
			String msg = "Failed loading configuration file: " + e.getMessage();
			logger.log(Level.SEVERE, msg, e);
			throw new Exception(msg, e);
		}
	}

	/**
	 * Loads configuration settings from a file
	 * 
	 * @param filename
	 *            File name
	 * @return Instance of {@link Config} loaded from a file
	 * @throws Exception
	 */
	public static Config load(Properties properties) throws Exception {
		try {

			logger.info("Loading PhyloSort configuration...");

			Config config = Config.getInstance();

			String mode = properties.getProperty(PROPERTY_MODE);
			boolean exclusive = config.isExclusive();
			if (mode != null) {
				if (mode.equalsIgnoreCase(PROPERTY_MODE_EXCLUSIVE)) {
					exclusive = true;
				} else if (mode.equalsIgnoreCase(PROPERTY_MODE_INCLUSIVE)) {
					exclusive = false;
				} else {
					throw new Exception("Invalid " + PROPERTY_MODE + ": " + mode);
				}
			}

			String onMatchAction = properties.getProperty(PROPERTY_ON_MATCH_ACTION);
			String match = config.getOnMatchAction();
			if (onMatchAction != null) {
				if (onMatchAction.equalsIgnoreCase(PROPERTY_ON_MATCH_ACTION_COPY)) {
					match = PROPERTY_ON_MATCH_ACTION_COPY;
				} else if (onMatchAction.equalsIgnoreCase(PROPERTY_ON_MATCH_ACTION_MOVE)) {
					match = PROPERTY_ON_MATCH_ACTION_MOVE;
				} else if (onMatchAction.equalsIgnoreCase(PROPERTY_ON_MATCH_ACTION_COUNT)) {
					match = PROPERTY_ON_MATCH_ACTION_COUNT;
				} else {
					throw new Exception("Invalid " + PROPERTY_ON_MATCH_ACTION + ": " + onMatchAction);
				}
			}

			String root = properties.getProperty(PROPERTY_ROOT);
			boolean outgroup = config.isOutgroup();
			if (root != null) {
				if (root.equalsIgnoreCase(PROPERTY_ROOT_OUTGROUP_YES)) {
					outgroup = true;
				} else if (root.equalsIgnoreCase(PROPERTY_ROOT_OUTGROUP_NO)) {
					outgroup = false;
				} else {
					throw new Exception("Invalid " + PROPERTY_ROOT + ": " + root);
				}
			}
			
			String _queryRequired = properties.getProperty(PROPERTY_QUERY_REQUIRED);
			boolean queryRequired = config.isQueryRequired();
			if (_queryRequired != null) {
				if (_queryRequired.equalsIgnoreCase(PROPERTY_QUERY_REQUIRED_YES)) {
					queryRequired = true;
				} else if (_queryRequired.equalsIgnoreCase(PROPERTY_QUERY_REQUIRED_NO)) {
					queryRequired = false;
				} else {
					throw new Exception("Invalid " + PROPERTY_QUERY_REQUIRED + ": " + _queryRequired);
				}
			}

			String _regexp = properties.getProperty(PROPERTY_REGEXP, config.getRegexp().toString());
			Pattern regexp = Pattern.compile(_regexp);
			
			String _filenameRegexp = properties.getProperty(PROPERTY_FILENAME_REGEXP, config.getFilenameRegexp().toString());
			Pattern filenameRegexp = Pattern.compile(_filenameRegexp);

			String _minimumNumberOfTaxa = properties.getProperty(PROPERTY_MINIMUM_NUMBER_OF_TAXA);
			int minimumNumberOfTaxa = config.getMinimumNumberOfTaxa();
			if (_minimumNumberOfTaxa != null) {
				minimumNumberOfTaxa = Integer.parseInt(_minimumNumberOfTaxa);
			}

			String _maximumNumberOfTaxa = properties.getProperty(PROPERTY_MAXIMUM_NUMBER_OF_TAXA);
			int maximumNumberOfTaxa = config.getMaximumNumberOfTaxa();
			if (_maximumNumberOfTaxa != null) {
				maximumNumberOfTaxa = Integer.parseInt(_maximumNumberOfTaxa);
			}

			String _minimumBootstrapSupport = properties.getProperty(PROPERTY_MINIMUM_BOOTSTRAP_SUPPORT);
			float minimumBootstrapSupport = config.getMinimumBootstrapSupport();
			if (_minimumBootstrapSupport != null) {
				minimumBootstrapSupport = Float.parseFloat(_minimumBootstrapSupport);
			}

			String _maximumAverageNumberOfCopies = properties.getProperty(PROPERTY_MAXIMUM_AVERAGE_NUMBER_OF_COPIES);
			float maximumAverageNumberOfCopies = config.getMinimumBootstrapSupport();
			if (_maximumAverageNumberOfCopies != null) {
				maximumAverageNumberOfCopies = Float.parseFloat(_maximumAverageNumberOfCopies);
			}
			
			String _minimumGroupSize = properties.getProperty(PROPERTY_MINIMUM_GROUP_SIZE);
			int minimumGroupSize = config.getMinimumGroupSize();
			if (_minimumGroupSize != null) {
				minimumGroupSize = Integer.parseInt(_minimumGroupSize);
			}


			// All new settings are OK. Now set them
			instance.outgroup = outgroup;
			instance.match = match;
			instance.exclusive = exclusive;
			instance.regexp = regexp;
			instance.filenameRegexp = filenameRegexp;
			instance.queryRequired = queryRequired;
			instance.minimumNumberOfTaxa = minimumNumberOfTaxa;
			instance.maximumNumberOfTaxa = maximumNumberOfTaxa;
			instance.minimumBootstrapSupport = minimumBootstrapSupport;
			instance.maximumAverageNumberOfCopies = maximumAverageNumberOfCopies;
			instance.minimumGroupSize = minimumGroupSize;

			logger.info("Finished loading PhyloSort configuration successfully");

			return instance;
		} catch (Exception e) {
			String msg = "Failed loading configuration: " + e.getMessage();
			logger.log(Level.SEVERE, msg, e);
			throw new Exception(msg, e);
		}
	}

	public String toString() {
		Config config = getInstance();

		StringBuffer buffer = new StringBuffer();

		buffer.append(Config.PROPERTY_ROOT + " = " + config.getRoot());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_QUERY_REQUIRED + " = " + config.getQueryRequired());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_MODE + " = " + config.getMode());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_MINIMUM_BOOTSTRAP_SUPPORT + " = " + config.getMinimumBootstrapSupport());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_MAXIMUM_NUMBER_OF_TAXA + " = " + config.getMaximumNumberOfTaxa());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_MINIMUM_NUMBER_OF_TAXA + " = " + config.getMinimumNumberOfTaxa());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_MAXIMUM_AVERAGE_NUMBER_OF_COPIES + " = " + config.getMaximumAverageNumberOfCopies());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_ON_MATCH_ACTION + " = " + config.getOnMatchAction());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_REGEXP + " = " + config.getRegexp());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_FILENAME_REGEXP + " = " + config.getFilenameRegexp());
		buffer.append(Commons.getLineSeparator());
		buffer.append(Config.PROPERTY_MINIMUM_GROUP_SIZE + " = " + config.getMinimumGroupSize());
		buffer.append(Commons.getLineSeparator());
		

		return buffer.toString();
	}

	public static void save(File file) throws Exception {

		logger.info("Saving PhyloSort configuration file \"" + file.getPath() + "\"...");

		Config config = getInstance();

		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(config.toString());
			writer.newLine();
		} catch (Exception exception) {
			String msg = "Failed saving PhyloSort configuration file \"" + file.getPath() + "\": " + exception.getMessage();
			logger.log(Level.SEVERE, msg, exception);
			throw new Exception(msg, exception);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception exception) {
					String msg = "Failed closing config file " + file.getName() + ": " + exception.getMessage();
					logger.log(Level.WARNING, msg, exception);
				}
			}
		}

		logger.info("Finished saving PhyloSort configuration file \"" + file.getPath() + "\"");
	}

	/**
	 * Returns the root boolean flag
	 * 
	 * @return True if the current rooting option is yes and false if it is no
	 */
	public boolean isOutgroup() {
		return outgroup;
	}

	/**
	 * Sets the rooting boolean flag
	 * 
	 * @param outgroup
	 *            True (=> root by an outgroup) or false (=> do not root)
	 */
	public void setOutgroup(boolean outgroup) {
		this.outgroup = outgroup;
	}
	
	/**
	 * Returns the query required boolean flag
	 * 
	 * @return True if the query is required, otherwise false
	 */
	public boolean isQueryRequired() {
		return queryRequired;
	}

	/**
	 * Sets the query required boolean flag
	 * 
	 * @param queryRequired
	 */
	public void setQueryRequired(boolean queryRequired) {
		this.queryRequired = queryRequired;
	}
	

	/**
	 * Returns the maximum average number of copies
	 * 
	 * @return The maximum average number of copies
	 */
	public float getMaximumAverageNumberOfCopies() {
		return maximumAverageNumberOfCopies;
	}

	/**
	 * Sets the maximum average number of copies
	 * 
	 * @param maximumAverageNumberOfCopies
	 *            the maximum average number of copies to set
	 */
	public void setMaximumAverageNumberOfCopies(float maximumAverageNumberOfCopies) {
		this.maximumAverageNumberOfCopies = maximumAverageNumberOfCopies;
	}

	/**
	 * Returns true if the maximum number of copies is negative, otherwise false
	 * 
	 * @return True if the maximum number of copies is negative, otherwise false
	 */
	public boolean isMaximumAverageNumberOfCopiesOn() {
		return maximumAverageNumberOfCopies >= 0;
	}

	/**
	 * Returns on match action
	 * 
	 * @return On match action
	 */
	public String getOnMatchAction() {
		return match;
	}

	public int getMinimumGroupSize() {
		return minimumGroupSize;
	}

	public void setMinimumGroupSize(int minimumGroupSize) {
		this.minimumGroupSize = minimumGroupSize;
	}
}