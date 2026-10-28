package com.mooresedge.getbyid.common;

/**
 * This class contains constant values used across the Get By ID
 * module.
 * These constants ensure consistency in module identification and resource
 * access.
 */
public class Constants {
	/**
	 * The unique identifier for this module.
	 * This ID is used in various parts of the module sdk to recognize and
	 * manage the module.
	 */
	public static final String MODULE_ID = "com.mooresedge.getbyid.GetByID";

	/**
	 * The URL alias for this module's resources.
	 * This alias is used when mounting and accessing module resources in the web
	 * context.
	 */
	public static final String MODULE_URL_ALIAS = "get-by-id";

	/**
	 * The scripting namespace our functions are registered under, e.g.
	 * system.dom.getElementById(...).
	 */
	public static final String SCRIPT_MODULE_PATH = "system.dom";
}