package com.mooresedge.getbyid.gateway.scripting;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import com.inductiveautomation.ignition.common.gson.JsonPrimitive;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.inductiveautomation.perspective.common.api.PropertyType;
import com.inductiveautomation.perspective.gateway.api.Component;
import com.inductiveautomation.perspective.gateway.model.ComponentModel;
import com.inductiveautomation.perspective.gateway.model.PageModel;
import com.inductiveautomation.perspective.gateway.model.ViewModel;
import com.inductiveautomation.perspective.gateway.property.PropertyTree;
import com.inductiveautomation.perspective.gateway.script.ComponentModelScriptWrapper;

import com.mooresedge.getbyid.common.scripting.AbstractDomScriptModule;

/**
 * Gateway implementation of the system.dom functions.
 *
 * Perspective attaches the current page and view to the thread before running
 * any session script (the same mechanism the built-in system.perspective
 * functions use), so we can find the page without the caller passing 'self'.
 */
public class GatewayDomScriptModule extends AbstractDomScriptModule {

	private static final String DOM_ID_KEY = "domId";

	@Override
	protected Object getElementByIdImpl(String id) {
		if (id == null || id.isEmpty()) {
			return null;
		}

		ViewModel currentView = ViewModel.VIEW.get();
		if (currentView == null) {
			throw new IllegalStateException(
					"system.dom.getElementById can only be called from a Perspective script running in a view "
							+ "(component events, property change scripts, message handlers, etc).");
		}

		return findInViews(List.of(currentView), id);
	}

	@Override
	protected Object getPageElementByIdImpl(String id) {
		if (id == null || id.isEmpty()) {
			return null;
		}

		ViewModel currentView = ViewModel.VIEW.get();
		PageModel page = PageModel.PAGE.get();
		if (page == null && currentView != null && currentView.getPage() instanceof PageModel) {
			page = (PageModel) currentView.getPage();
		}
		if (page == null && currentView == null) {
			throw new IllegalStateException(
					"system.dom.getPageElementById can only be called from a Perspective script running on a page "
							+ "(component events, property change scripts, message handlers, etc).");
		}

		// Search the view the script is running in first, so duplicate ids resolve to
		// the closest match, then the rest of the page (docks, popups, embedded views).
		List<ViewModel> views = new ArrayList<>();
		if (currentView != null) {
			views.add(currentView);
		}
		if (page != null) {
			for (ViewModel view : page.getViews()) {
				if (view != currentView) {
					views.add(view);
				}
			}
		}

		return findInViews(views, id);
	}

	/**
	 * Searches the given views in order and returns the first matching component,
	 * wrapped for scripting, or null if none match.
	 */
	private static Object findInViews(List<ViewModel> views, String id) {
		for (ViewModel view : views) {
			if (!view.isRunning()) {
				continue;
			}
			Optional<ComponentModel> match = findInView(view, id);
			if (match.isPresent()) {
				return new ComponentModelScriptWrapper(match.get());
			}
		}
		return null;
	}

	/**
	 * Depth-first search of a view's component tree, in document order.
	 */
	private static Optional<ComponentModel> findInView(ViewModel view, String id) {
		ComponentModel root = view.getRootContainer();
		if (root == null) {
			return Optional.empty();
		}

		Deque<ComponentModel> stack = new ArrayDeque<>();
		stack.push(root);
		while (!stack.isEmpty()) {
			ComponentModel component = stack.pop();
			if (id.equals(readDomId(component))) {
				return Optional.of(component);
			}

			// Push children in reverse so they are visited in their natural order
			List<ComponentModel> children = new ArrayList<>();
			for (Component child : component.getChildren()) {
				if (child instanceof ComponentModel) {
					children.add((ComponentModel) child);
				}
			}
			for (int i = children.size() - 1; i >= 0; i--) {
				stack.push(children.get(i));
			}
		}
		return Optional.empty();
	}

	private static String readDomId(ComponentModel component) {
		PropertyTree meta = component.getPropertyTreeOf(PropertyType.meta);
		if (meta == null) {
			return null;
		}

		// Property trees may only be read on their own execution queue. runOrSubmit
		// runs inline if we're already on it, and otherwise waits for it, which is
		// the same approach Perspective's own script wrappers take.
		Object value;
		try {
			value = meta.getQueue()
					.runOrSubmit(() -> meta.read(DOM_ID_KEY).map(QualifiedValue::getValue).orElse(null))
					.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Interrupted while reading meta.domId", e);
		} catch (ExecutionException e) {
			throw new IllegalStateException("Unable to read meta.domId of " + component.getQualifiedPath(), e.getCause());
		}

		if (value instanceof JsonPrimitive) {
			return ((JsonPrimitive) value).getAsString();
		}
		return value == null ? null : value.toString();
	}
}
