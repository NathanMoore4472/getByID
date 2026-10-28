package com.mooresedge.getbyid.common.scripting;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.script.hints.JythonElement;
import com.inductiveautomation.ignition.common.script.hints.ScriptArg;

/**
 * The system.dom scripting functions.
 *
 * The public methods here define what scripts can call, and their docs (shown
 * in Designer autocomplete) live in AbstractDomScriptModule.properties. Each
 * scope supplies its own implementation: the Gateway does the real work, since
 * that is where Perspective scripts run, while the Designer only needs the
 * signatures for script hints.
 */
public abstract class AbstractDomScriptModule {

	static {
		BundleUtil.get().addBundle(
				AbstractDomScriptModule.class.getSimpleName(),
				AbstractDomScriptModule.class.getClassLoader(),
				AbstractDomScriptModule.class.getName().replace('.', '/'));
	}

	/**
	 * Finds the component in the current view whose meta.domId matches the given
	 * id, like document.getElementById in JavaScript. Only the view the script is
	 * running in is searched; embedded views, docks and popups are not.
	 *
	 * @param id The domId to look for.
	 * @return The matching component (the same kind of object as 'self'), or
	 *         None if no component in the view has that domId.
	 */
	@JythonElement(docBundlePrefix = "AbstractDomScriptModule")
	public Object getElementById(@ScriptArg("id") String id) {
		return getElementByIdImpl(id);
	}

	/**
	 * Like getElementById, but searches the whole page: the current view first,
	 * then every other view on it (embedded views, docks and popups).
	 *
	 * @param id The domId to look for.
	 * @return The matching component, or None if nothing on the page has that
	 *         domId.
	 */
	@JythonElement(docBundlePrefix = "AbstractDomScriptModule")
	public Object getPageElementById(@ScriptArg("id") String id) {
		return getPageElementByIdImpl(id);
	}

	protected abstract Object getElementByIdImpl(String id);

	protected abstract Object getPageElementByIdImpl(String id);
}
