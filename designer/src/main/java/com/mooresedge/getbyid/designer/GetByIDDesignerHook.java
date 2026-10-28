package com.mooresedge.getbyid.designer;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.perspective.designer.DesignerComponentRegistry;
import com.inductiveautomation.perspective.designer.api.PerspectiveDesignerInterface;

import com.mooresedge.getbyid.common.Constants;
import com.mooresedge.getbyid.common.components.input.Button;
import com.mooresedge.getbyid.designer.scripting.DesignerDomScriptModule;

/**
 * Designer module hook for the Get By ID.
 * This class is responsible for initializing and managing the module's
 * lifecycle in the Ignition Designer.
 */
public class GetByIDDesignerHook extends AbstractDesignerModuleHook {
	private static final LoggerEx log = LoggerEx.newBuilder().build(GetByIDDesignerHook.class);

	private DesignerContext context;
	private DesignerComponentRegistry registry;

	/**
	 * Registers the system.dom scripting functions so the Designer's script
	 * editors show them in autocomplete.
	 *
	 * @param manager The Designer's script manager.
	 */
	@Override
	public void initializeScriptManager(ScriptManager manager) {
		super.initializeScriptManager(manager);
		manager.addScriptModule(Constants.SCRIPT_MODULE_PATH, new DesignerDomScriptModule(),
				new PropertiesFileDocProvider());
	}

	/**
	 * Initializes the module in the Designer scope.
	 * 
	 * @param context         The DesignerContext for this module.
	 * @param activationState The current license state.
	 */
	@Override
	public void startup(DesignerContext context, LicenseState activationState) {
		log.trace("Starting up Get By ID Designer Hook");
		this.context = context;
		init();
	}

	/**
	 * Initializes the component registry and registers components.
	 */
	private void init() {
		PerspectiveDesignerInterface pdi = PerspectiveDesignerInterface.get(context);

		registry = pdi.getDesignerComponentRegistry();

		// Each component must be registered, with an optional icon, to the component registry.
		ComponentUtilities.registerComponentWithIcon(registry, Button.DESCRIPTOR, "/images/button-click.svg");
	}

	/**
	 * Shuts down the module and unregisters components.
	 */
	@Override
	public void shutdown() {
		log.trace("Shutting down Get By ID Designer Hook");
		removeComponents();
	}

	/**
	 * Removes registered components from the registry.
	 */
	private void removeComponents() {
		registry.removeComponent(Button.COMPONENT_ID);
	}
}