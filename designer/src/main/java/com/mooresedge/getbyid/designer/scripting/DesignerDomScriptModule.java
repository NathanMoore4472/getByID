package com.mooresedge.getbyid.designer.scripting;

import com.mooresedge.getbyid.common.scripting.AbstractDomScriptModule;

/**
 * Designer registration of the system.dom functions.
 *
 * Perspective scripts always run on the Gateway, even when previewing in the
 * Designer, so this exists only to give the script editors autocomplete and
 * docs. Calling it from Designer-scoped scripts (e.g. the Script Console)
 * raises a clear error instead of failing silently.
 */
public class DesignerDomScriptModule extends AbstractDomScriptModule {

	@Override
	protected Object getElementByIdImpl(String id) {
		throw notAvailable("getElementById");
	}

	@Override
	protected Object getPageElementByIdImpl(String id) {
		throw notAvailable("getPageElementById");
	}

	private static IllegalStateException notAvailable(String function) {
		return new IllegalStateException(
				"system.dom." + function + " only works in Perspective scripts, which run on the Gateway. "
						+ "It cannot be used from the Designer Script Console.");
	}
}
