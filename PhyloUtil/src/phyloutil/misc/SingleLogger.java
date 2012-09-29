/*
 * $Id: SingleLogger.java,v 1.1.1.1 2007/05/29 16:11:41 ahmed Exp $
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

import java.util.logging.Logger;

/**
 * One central logger ({@link Logger})
 * 
 * @author Ahmed Moustafa
 * @version $Revision: 1.1.1.1 $
 */

public class SingleLogger {
	
	/**
	 * Logger
	 */
	private static Logger logger = null;

	/**
	 * Returns {@link Logger}
	 * 
	 * @return {@link Logger}
	 */
	public static Logger getLogger() {
		
		if (logger == null) {
			
			logger = Logger.getLogger("phylosort");
			
		}
		
		return logger;
		
	}
}
